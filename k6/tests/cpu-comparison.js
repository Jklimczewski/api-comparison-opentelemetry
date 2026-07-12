import http from "k6/http";
import grpc from "k6/net/grpc";
import { check } from "k6";
import { REST_URL, GRAPHQL_URL, GRPC_ADDR, OTEL_MODE } from "../config.js";
import {
  checkHttp,
  checkHttpOrNotFound,
  randomInt,
  errorRate,
} from "../utils/helpers.js";

const grpcClient = new grpc.Client();
grpcClient.load(["../../grpc-service/src/main/proto"], "bookstore.proto");

const CPU_STAGES = [
  { duration: "30s", target: 10 }, // warm-up
  { duration: "30s", target: 50 }, // ramp-up
  { duration: "4m", target: 50 }, // main window
  { duration: "30s", target: 0 }, // cool-down
];

export const options = {
  scenarios: {
    rest: {
      executor: "ramping-vus",
      stages: CPU_STAGES,
      exec: "restScenario",
      tags: { protocol: "rest", otel_mode: OTEL_MODE },
    },
    graphql: {
      executor: "ramping-vus",
      stages: CPU_STAGES,
      exec: "graphqlScenario",
      tags: { protocol: "graphql", otel_mode: OTEL_MODE },
    },
    grpc: {
      executor: "ramping-vus",
      stages: CPU_STAGES,
      exec: "grpcScenario",
      tags: { protocol: "grpc", otel_mode: OTEL_MODE },
    },
  },
  thresholds: {
    errors: ["rate<0.05"],
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<10000"],
    grpc_req_duration: ["p(95)<10000"],
  },
};

const JSON_HEADERS = { "Content-Type": "application/json" };

const QUERY_USER = `
  query GetUser($id: ID!) {
    user(id: $id) { id name email }
  }
`;

const QUERY_BOOKS = `
  query GetBooks {
    books { id title author price }
  }
`;

const QUERY_ORDER = `
  query GetOrder($id: ID!) {
    order(id: $id) {
      id createdAt
      user  { id name email }
      books { id title author price }
    }
  }
`;

const MUTATION_CREATE_ORDER = `
  mutation CreateOrder($input: CreateOrderInput!) {
    createOrder(input: $input) { id createdAt }
  }
`;

export function setup() {
  const orderIds = [];
  for (let i = 0; i < 20; i++) {
    const res = http.post(
      `${REST_URL}/api/orders`,
      JSON.stringify({
        userId: randomInt(1, 1000),
        bookIds: [randomInt(1, 100), randomInt(1, 100)],
      }),
      { headers: JSON_HEADERS },
    );
    if (res.status === 200) {
      try {
        const body = JSON.parse(res.body);
        if (body && body.id) orderIds.push(body.id);
      } catch (_) {
        /* ignore */
      }
    }
  }
  return { orderIds: orderIds.length > 0 ? orderIds : [1] };
}

export function restScenario(data) {
  const userId = randomInt(1, 1000);
  const orderId = data.orderIds[randomInt(0, data.orderIds.length - 1)];
  const bookIds = [randomInt(1, 100), randomInt(1, 100)];

  checkHttp(
    http.get(`${REST_URL}/api/users/${userId}`, {
      tags: { endpoint: "getUser" },
    }),
    "rest:getUser",
  );

  checkHttp(
    http.get(`${REST_URL}/api/books`, { tags: { endpoint: "getBooks" } }),
    "rest:getBooks",
  );

  checkHttpOrNotFound(
    http.get(`${REST_URL}/api/orders/${orderId}`, {
      tags: { endpoint: "getOrder" },
    }),
    "rest:getOrder",
  );

  checkHttp(
    http.post(`${REST_URL}/api/orders`, JSON.stringify({ userId, bookIds }), {
      headers: JSON_HEADERS,
      tags: { endpoint: "createOrder" },
    }),
    "rest:createOrder",
  );
}

const GQL_ENDPOINT = `${GRAPHQL_URL}/graphql`;

function gqlBody(query, variables) {
  return JSON.stringify({ query, variables: variables || {} });
}

function checkGql(res, name) {
  const httpOk = checkHttp(res, name);
  if (!httpOk) return false;
  let hasErrors = false;
  try {
    const body = JSON.parse(res.body);
    hasErrors = Array.isArray(body.errors) && body.errors.length > 0;
  } catch (_) {
    hasErrors = true;
  }
  const gqlOk = check(res, {
    [`${name}: no GraphQL errors`]: () => !hasErrors,
  });
  errorRate.add(!gqlOk);
  return gqlOk;
}

export function graphqlScenario(data) {
  const userId = String(randomInt(1, 1000));
  const orderId = String(data.orderIds[randomInt(0, data.orderIds.length - 1)]);
  const bookIds = [String(randomInt(1, 100)), String(randomInt(1, 100))];

  checkGql(
    http.post(GQL_ENDPOINT, gqlBody(QUERY_USER, { id: userId }), {
      headers: JSON_HEADERS,
      tags: { operation: "user" },
    }),
    "graphql:user",
  );

  checkGql(
    http.post(GQL_ENDPOINT, gqlBody(QUERY_BOOKS), {
      headers: JSON_HEADERS,
      tags: { operation: "books" },
    }),
    "graphql:books",
  );

  checkHttp(
    http.post(GQL_ENDPOINT, gqlBody(QUERY_ORDER, { id: orderId }), {
      headers: JSON_HEADERS,
      tags: { operation: "order" },
    }),
    "graphql:order",
  );

  checkGql(
    http.post(
      GQL_ENDPOINT,
      gqlBody(MUTATION_CREATE_ORDER, { input: { userId, bookIds } }),
      { headers: JSON_HEADERS, tags: { operation: "createOrder" } },
    ),
    "graphql:createOrder",
  );
}

let grpcConnected = false;

export function grpcScenario(data) {
  if (!grpcConnected) {
    grpcClient.connect(GRPC_ADDR, { plaintext: true });
    grpcConnected = true;
  }

  const userId = randomInt(1, 1000);
  const orderId = data.orderIds[randomInt(0, data.orderIds.length - 1)];
  const bookIds = [randomInt(1, 100), randomInt(1, 100)];

  const userResp = grpcClient.invoke(
    "bookstore.BookstoreService/GetUser",
    { id: userId },
    { tags: { method: "GetUser" } },
  );
  const userOk = check(userResp, {
    "grpc:GetUser OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!userOk);

  const booksResp = grpcClient.invoke(
    "bookstore.BookstoreService/GetBooks",
    {},
    { tags: { method: "GetBooks" } },
  );
  const booksOk = check(booksResp, {
    "grpc:GetBooks OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!booksOk);

  const orderResp = grpcClient.invoke(
    "bookstore.BookstoreService/GetOrder",
    { id: orderId },
    { tags: { method: "GetOrder" } },
  );
  check(orderResp, {
    "grpc:GetOrder OK or NOT_FOUND": (r) =>
      r && (r.status === grpc.StatusOK || r.status === grpc.StatusNotFound),
  });

  const createResp = grpcClient.invoke(
    "bookstore.BookstoreService/CreateOrder",
    { userId, bookIds },
    { tags: { method: "CreateOrder" } },
  );
  const createOk = check(createResp, {
    "grpc:CreateOrder OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!createOk);
}

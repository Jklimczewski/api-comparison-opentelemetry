import http from "k6/http";
import { check, sleep } from "k6";
import {
  GRAPHQL_URL,
  BENCHMARK_STAGES,
  BASE_THRESHOLDS,
  OTEL_MODE,
} from "../config.js";
import { checkHttp, randomInt, errorRate } from "../utils/helpers.js";

export const options = {
  stages: BENCHMARK_STAGES,
  thresholds: {
    ...BASE_THRESHOLDS,
    "http_req_duration{operation:user}": ["p(95)<500", "p(99)<1500"],
    "http_req_duration{operation:books}": ["p(95)<1000", "p(99)<3000"],
    "http_req_duration{operation:order}": ["p(95)<800", "p(99)<2000"],
    "http_req_duration{operation:createOrder}": ["p(95)<1500", "p(99)<4000"],
  },
  tags: { protocol: "graphql", otel_mode: OTEL_MODE },
};

const GQL_ENDPOINT = `${GRAPHQL_URL}/graphql`;
const JSON_HEADERS = { "Content-Type": "application/json" };

const QUERY_USER = `
  query GetUser($id: ID!) {
    user(id: $id) {
      id
      name
      email
    }
  }
`;

const QUERY_BOOKS = `
  query GetBooks {
    books {
      id
      title
      author
      price
    }
  }
`;

const QUERY_ORDER = `
  query GetOrder($id: ID!) {
    order(id: $id) {
      id
      createdAt
      user  { id name email }
      books { id title author price }
    }
  }
`;

const MUTATION_CREATE_ORDER = `
  mutation CreateOrder($input: CreateOrderInput!) {
    createOrder(input: $input) {
      id
      createdAt
      user  { id name email }
      books { id title author price }
    }
  }
`;

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

export function setup() {
  const seedOrderIds = [];
  for (let i = 0; i < 20; i++) {
    const input = {
      userId: String(randomInt(1, 1000)),
      bookIds: [String(randomInt(1, 100)), String(randomInt(1, 100))],
    };
    const res = http.post(
      GQL_ENDPOINT,
      gqlBody(MUTATION_CREATE_ORDER, { input }),
      { headers: JSON_HEADERS },
    );
    if (res.status === 200) {
      try {
        const body = JSON.parse(res.body);
        const id =
          body.data && body.data.createOrder && body.data.createOrder.id;
        if (id) seedOrderIds.push(id);
      } catch (_) {}
    }
  }
  return { orderIds: seedOrderIds.length > 0 ? seedOrderIds : ["1"] };
}

export default function (data) {
  const userId = String(randomInt(1, 1000));
  const orderId = data.orderIds[randomInt(0, data.orderIds.length - 1)];

  const userRes = http.post(GQL_ENDPOINT, gqlBody(QUERY_USER, { id: userId }), {
    headers: JSON_HEADERS,
    tags: { operation: "user" },
  });
  checkGql(userRes, "user");

  const booksRes = http.post(GQL_ENDPOINT, gqlBody(QUERY_BOOKS), {
    headers: JSON_HEADERS,
    tags: { operation: "books" },
  });
  checkGql(booksRes, "books");

  const orderRes = http.post(
    GQL_ENDPOINT,
    gqlBody(QUERY_ORDER, { id: orderId }),
    { headers: JSON_HEADERS, tags: { operation: "order" } },
  );
  checkHttp(orderRes, "order");

  const createInput = {
    userId: String(randomInt(1, 1000)),
    bookIds: [String(randomInt(1, 100)), String(randomInt(1, 100))],
  };
  const createRes = http.post(
    GQL_ENDPOINT,
    gqlBody(MUTATION_CREATE_ORDER, { input: createInput }),
    { headers: JSON_HEADERS, tags: { operation: "createOrder" } },
  );
  checkGql(createRes, "createOrder");

  sleep(0.5);
}

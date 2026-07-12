import http from "k6/http";
import grpc from "k6/net/grpc";
import { check, sleep } from "k6";
import { Trend } from "k6/metrics";
import {
  REST_URL,
  GRAPHQL_URL,
  GRPC_ADDR,
  PAYLOAD_STAGES,
  OTEL_MODE,
} from "../config.js";
import { randomInt, errorRate } from "../utils/helpers.js";

const responseBodyBytes = new Trend("response_body_bytes", true);

const grpcClient = new grpc.Client();
grpcClient.load(["../../grpc-service/src/main/proto"], "bookstore.proto");

const grpcSetupClient = new grpc.Client();
grpcSetupClient.load(["../../grpc-service/src/main/proto"], "bookstore.proto");

export const options = {
  stages: PAYLOAD_STAGES,
  thresholds: {
    errors: ["rate<0.01"],
    http_req_failed: ["rate<0.01"],
  },
  tags: { otel_mode: OTEL_MODE, scenario: "payload-comparison" },
};

const GQL_ENDPOINT = `${GRAPHQL_URL}/graphql`;
const JSON_HEADERS = { "Content-Type": "application/json" };

const GQL_USER_MINIMAL = (id) =>
  JSON.stringify({
    query: `{ user(id: "${id}") { id } }`,
  });

const GQL_USER_FULL = (id) =>
  JSON.stringify({
    query: `{ user(id: "${id}") { id name email } }`,
  });

const GQL_ORDER_DEEP = (id) =>
  JSON.stringify({
    query: `{
    order(id: "${id}") {
      id
      createdAt
      user  { id name email }
      books { id title author price }
    }
  }`,
  });

const GQL_BOOKS_FULL = JSON.stringify({
  query: `{ books { id title author price } }`,
});

const GQL_BOOKS_IDS = JSON.stringify({
  query: `{ books { id } }`,
});

export function setup() {
  const restOrderIds = [];
  for (let i = 0; i < 10; i++) {
    const userId = randomInt(1, 1000);
    const bookIds = [randomInt(1, 100), randomInt(1, 100)];
    const res = http.post(
      `${REST_URL}/api/orders`,
      JSON.stringify({ userId, bookIds }),
      { headers: JSON_HEADERS },
    );
    if (res.status === 200) {
      try {
        const body = JSON.parse(res.body);
        if (body && body.id) restOrderIds.push(body.id);
      } catch (_) {}
    }
  }

  grpcSetupClient.connect(GRPC_ADDR, { plaintext: true });
  const grpcOrderIds = [];
  for (let i = 0; i < 10; i++) {
    const userId = randomInt(1, 1000);
    const bookIds = [randomInt(1, 100), randomInt(1, 100)];
    const res = grpcSetupClient.invoke(
      "bookstore.BookstoreService/CreateOrder",
      { userId, bookIds },
    );
    if (res && res.status === grpc.StatusOK && res.message && res.message.id) {
      grpcOrderIds.push(res.message.id);
    }
  }
  grpcSetupClient.close();

  return {
    orderIds: restOrderIds.length > 0 ? restOrderIds : [1],
    grpcOrderIds: grpcOrderIds.length > 0 ? grpcOrderIds : [1],
  };
}

let grpcConnected = false;

export default function (data) {
  if (!grpcConnected) {
    grpcClient.connect(GRPC_ADDR, { plaintext: true });
    grpcConnected = true;
  }

  const userId = randomInt(1, 1000);
  const orderId = data.orderIds[randomInt(0, data.orderIds.length - 1)];

  const restSmallRes = http.get(`${REST_URL}/api/users/${userId}`, {
    tags: { protocol: "rest", payload: "small" },
  });
  if (
    check(restSmallRes, {
      "REST small: 2xx": (r) => r.status >= 200 && r.status < 300,
    })
  ) {
    responseBodyBytes.add(restSmallRes.body.length, {
      protocol: "rest",
      payload: "small",
    });
  } else {
    errorRate.add(1);
  }

  const restMedRes = http.get(`${REST_URL}/api/orders/${orderId}`, {
    tags: { protocol: "rest", payload: "medium" },
  });
  if (restMedRes.status === 200) {
    responseBodyBytes.add(restMedRes.body.length, {
      protocol: "rest",
      payload: "medium",
    });
  }

  const restLargeRes = http.get(`${REST_URL}/api/books`, {
    tags: { protocol: "rest", payload: "large" },
  });
  if (
    check(restLargeRes, {
      "REST large: 2xx": (r) => r.status >= 200 && r.status < 300,
    })
  ) {
    responseBodyBytes.add(restLargeRes.body.length, {
      protocol: "rest",
      payload: "large",
    });
  } else {
    errorRate.add(1);
  }

  const gqlMinRes = http.post(GQL_ENDPOINT, GQL_USER_MINIMAL(userId), {
    headers: JSON_HEADERS,
    tags: { protocol: "graphql", payload: "minimal" },
  });
  if (check(gqlMinRes, { "GQL minimal: 200": (r) => r.status === 200 })) {
    responseBodyBytes.add(gqlMinRes.body.length, {
      protocol: "graphql",
      payload: "minimal",
    });
  } else {
    errorRate.add(1);
  }

  const gqlSmallRes = http.post(GQL_ENDPOINT, GQL_USER_FULL(userId), {
    headers: JSON_HEADERS,
    tags: { protocol: "graphql", payload: "small" },
  });
  if (check(gqlSmallRes, { "GQL small: 200": (r) => r.status === 200 })) {
    responseBodyBytes.add(gqlSmallRes.body.length, {
      protocol: "graphql",
      payload: "small",
    });
  } else {
    errorRate.add(1);
  }

  const gqlMedRes = http.post(GQL_ENDPOINT, GQL_ORDER_DEEP(orderId), {
    headers: JSON_HEADERS,
    tags: { protocol: "graphql", payload: "medium" },
  });
  check(gqlMedRes, { "GQL medium: 200": (r) => r.status === 200 });
  if (gqlMedRes.status === 200) {
    responseBodyBytes.add(gqlMedRes.body.length, {
      protocol: "graphql",
      payload: "medium",
    });
  }

  const gqlLargeIdsRes = http.post(GQL_ENDPOINT, GQL_BOOKS_IDS, {
    headers: JSON_HEADERS,
    tags: { protocol: "graphql", payload: "large-ids" },
  });
  if (
    check(gqlLargeIdsRes, { "GQL large-ids: 200": (r) => r.status === 200 })
  ) {
    responseBodyBytes.add(gqlLargeIdsRes.body.length, {
      protocol: "graphql",
      payload: "large-ids",
    });
  } else {
    errorRate.add(1);
  }

  const gqlLargeRes = http.post(GQL_ENDPOINT, GQL_BOOKS_FULL, {
    headers: JSON_HEADERS,
    tags: { protocol: "graphql", payload: "large" },
  });
  if (check(gqlLargeRes, { "GQL large: 200": (r) => r.status === 200 })) {
    responseBodyBytes.add(gqlLargeRes.body.length, {
      protocol: "graphql",
      payload: "large",
    });
  } else {
    errorRate.add(1);
  }

  const grpcSmallResp = grpcClient.invoke(
    "bookstore.BookstoreService/GetUser",
    { id: userId },
    { tags: { protocol: "grpc", payload: "small" } },
  );
  const grpcSmallOk = check(grpcSmallResp, {
    "gRPC small: OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!grpcSmallOk);

  const grpcOrderId =
    data.grpcOrderIds[randomInt(0, data.grpcOrderIds.length - 1)];
  const grpcMedResp = grpcClient.invoke(
    "bookstore.BookstoreService/GetOrder",
    { id: grpcOrderId },
    { tags: { protocol: "grpc", payload: "medium" } },
  );
  const grpcMedOk = check(grpcMedResp, {
    "gRPC medium: OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!grpcMedOk);

  const grpcLargeResp = grpcClient.invoke(
    "bookstore.BookstoreService/GetBooks",
    {},
    { tags: { protocol: "grpc", payload: "large" } },
  );
  const grpcLargeOk = check(grpcLargeResp, {
    "gRPC large: OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!grpcLargeOk);

  sleep(1);
}

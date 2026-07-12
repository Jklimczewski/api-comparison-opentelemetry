import http from "k6/http";
import { sleep } from "k6";
import {
  REST_URL,
  BENCHMARK_STAGES,
  BASE_THRESHOLDS,
  OTEL_MODE,
} from "../config.js";
import {
  checkHttp,
  checkHttpOrNotFound,
  randomInt,
  errorRate,
} from "../utils/helpers.js";

export const options = {
  stages: BENCHMARK_STAGES,
  thresholds: {
    ...BASE_THRESHOLDS,
    "http_req_duration{endpoint:getUser}": ["p(95)<500", "p(99)<1500"],
    "http_req_duration{endpoint:getBooks}": ["p(95)<1000", "p(99)<3000"],
    "http_req_duration{endpoint:getOrder}": ["p(95)<800", "p(99)<2000"],
    "http_req_duration{endpoint:createOrder}": ["p(95)<1500", "p(99)<4000"],
  },
  tags: { protocol: "rest", otel_mode: OTEL_MODE },
};

const JSON_HEADERS = { "Content-Type": "application/json" };

export function setup() {
  const seedOrderIds = [];
  for (let i = 0; i < 20; i++) {
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
        if (body && body.id) seedOrderIds.push(body.id);
      } catch (_) {}
    }
  }
  return { orderIds: seedOrderIds.length > 0 ? seedOrderIds : [1] };
}

export default function (data) {
  const userId = randomInt(1, 1000);
  const orderId = data.orderIds[randomInt(0, data.orderIds.length - 1)];
  const bookIds = [randomInt(1, 100), randomInt(1, 100)];

  const userRes = http.get(`${REST_URL}/api/users/${userId}`, {
    tags: { endpoint: "getUser" },
  });
  checkHttp(userRes, "getUser");

  const booksRes = http.get(`${REST_URL}/api/books`, {
    tags: { endpoint: "getBooks" },
  });
  checkHttp(booksRes, "getBooks");

  const orderRes = http.get(`${REST_URL}/api/orders/${orderId}`, {
    tags: { endpoint: "getOrder" },
  });
  checkHttpOrNotFound(orderRes, "getOrder");

  const createRes = http.post(
    `${REST_URL}/api/orders`,
    JSON.stringify({ userId, bookIds }),
    { headers: JSON_HEADERS, tags: { endpoint: "createOrder" } },
  );
  checkHttp(createRes, "createOrder");

  sleep(0.5);
}

import grpc from "k6/net/grpc";
import { check, sleep } from "k6";
import { GRPC_ADDR, BENCHMARK_STAGES, OTEL_MODE } from "../config.js";
import { randomInt, errorRate } from "../utils/helpers.js";

const client = new grpc.Client();
client.load(["../../grpc-service/src/main/proto"], "bookstore.proto");

export const options = {
  stages: BENCHMARK_STAGES,
  thresholds: {
    errors: ["rate<0.01"],
    grpc_req_duration: ["p(95)<5000", "p(99)<10000"],
    "grpc_req_duration{method:GetUser}": ["p(95)<500", "p(99)<1500"],
    "grpc_req_duration{method:GetBooks}": ["p(95)<1000", "p(99)<3000"],
    "grpc_req_duration{method:GetOrder}": ["p(95)<800", "p(99)<2000"],
    "grpc_req_duration{method:CreateOrder}": ["p(95)<1500", "p(99)<4000"],
  },
  tags: { protocol: "grpc", otel_mode: OTEL_MODE },
};

export function setup() {
  client.connect(GRPC_ADDR, { plaintext: true });

  const seedOrderIds = [];
  for (let i = 0; i < 20; i++) {
    const userId = randomInt(1, 1000);
    const bookIds = [randomInt(1, 100), randomInt(1, 100)];
    const res = client.invoke("bookstore.BookstoreService/CreateOrder", {
      userId,
      bookIds,
    });
    if (res && res.status === grpc.StatusOK && res.message && res.message.id) {
      seedOrderIds.push(res.message.id);
    }
  }

  client.close();
  return { orderIds: seedOrderIds.length > 0 ? seedOrderIds : [1] };
}

let connected = false;

export default function (data) {
  if (!connected) {
    client.connect(GRPC_ADDR, { plaintext: true });
    connected = true;
  }

  const userId = randomInt(1, 1000);
  const orderId = data.orderIds[randomInt(0, data.orderIds.length - 1)];
  const bookIds = [randomInt(1, 100), randomInt(1, 100)];

  const userResp = client.invoke(
    "bookstore.BookstoreService/GetUser",
    { id: userId },
    { tags: { method: "GetUser" } },
  );
  const userOk = check(userResp, {
    "GetUser: status OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!userOk);

  const booksResp = client.invoke(
    "bookstore.BookstoreService/GetBooks",
    {},
    { tags: { method: "GetBooks" } },
  );
  const booksOk = check(booksResp, {
    "GetBooks: status OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!booksOk);

  const orderResp = client.invoke(
    "bookstore.BookstoreService/GetOrder",
    { id: orderId },
    { tags: { method: "GetOrder" } },
  );
  check(orderResp, {
    "GetOrder: OK or NOT_FOUND": (r) =>
      r && (r.status === grpc.StatusOK || r.status === grpc.StatusNotFound),
  });

  const createResp = client.invoke(
    "bookstore.BookstoreService/CreateOrder",
    { userId, bookIds },
    { tags: { method: "CreateOrder" } },
  );
  const createOk = check(createResp, {
    "CreateOrder: status OK": (r) => r && r.status === grpc.StatusOK,
  });
  errorRate.add(!createOk);

  sleep(0.5);
}

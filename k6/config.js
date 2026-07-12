export const REST_URL = __ENV.REST_URL || "http://localhost:8081";
export const GRAPHQL_URL = __ENV.GRAPHQL_URL || "http://localhost:8082";
export const GRPC_ADDR = __ENV.GRPC_ADDR || "localhost:9091";

export const OTEL_MODE = __ENV.OTEL_MODE || "unknown";

export const BENCHMARK_STAGES = [
  { duration: "30s", target: 5 }, // warm-up
  { duration: "30s", target: 50 }, // ramp-up
  { duration: "2m", target: 50 }, // main window
  { duration: "30s", target: 100 }, // stress peak
  { duration: "30s", target: 0 }, // cool-down
];

export const PAYLOAD_STAGES = [
  { duration: "20s", target: 20 },
  { duration: "1m", target: 20 },
  { duration: "20s", target: 0 },
];

export const BASE_THRESHOLDS = {
  errors: ["rate<0.01"],
  http_req_failed: ["rate<0.01"],
  http_req_duration: ["p(95)<5000", "p(99)<10000"],
};

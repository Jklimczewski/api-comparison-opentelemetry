#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -W 2>/dev/null || pwd)"
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -W 2>/dev/null || pwd)"
RESULTS_DIR="$SCRIPT_DIR/results"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
K6_TREND_STATS="med,p(90),p(95),p(99),max"

mkdir -p "$RESULTS_DIR"

wait_for_service() {
  local url="$1" name="$2" retries=40
  echo "Waiting for $name"
  for i in $(seq 1 $retries); do
    if curl -sf --max-time 3 "$url" > /dev/null 2>&1; then
      echo "$name ready"
      return 0
    fi
    sleep 3
  done
  echo "$name did not become healthy in time" >&2
  exit 1
}

seed_books() {
  echo "Seeding books"
  for DB in restdb graphqldb grpcdb; do
    PGPASSWORD=benchmark psql \
      -h localhost -p 5432 -U benchmark -d "$DB" \
      -f "$SCRIPT_DIR/seed.sql" -q 2>/dev/null \
      && echo "$DB seeded" \
      || echo "Could not seed $DB (psql not available or already seeded)"
  done
}

run_k6() {
  local mode="$1" protocol="$2" script="$3"
  echo "$protocol [otel=$mode]"
  k6 run \
    --env OTEL_MODE="$mode" \
    --summary-trend-stats="$K6_TREND_STATS" \
    --out "json=$RESULTS_DIR/${mode}_${protocol}_${TIMESTAMP}.json" \
    "$SCRIPT_DIR/$script"
}

run_mode() {
  local MODE="$1"

  case "$MODE" in
    off)
      export OTEL_SDK_DISABLED=true
      export OTEL_TRACES_SAMPLER=parentbased_always_on
      export OTEL_TRACES_SAMPLER_ARG=1.0
      ;;
    on-100)
      export OTEL_SDK_DISABLED=false
      export OTEL_TRACES_SAMPLER=parentbased_always_on
      export OTEL_TRACES_SAMPLER_ARG=1.0
      ;;
    *)
      echo "Unknown mode: $MODE" >&2; exit 1 ;;
  esac

  echo "Restarting application services with OTel mode=$MODE"
  cd "$PROJECT_DIR"
  docker compose up -d --force-recreate rest-service graphql-service grpc-service
  cd "$SCRIPT_DIR"

  wait_for_service "http://localhost:8081/actuator/health" "rest-service"
  wait_for_service "http://localhost:8082/actuator/health" "graphql-service"
  wait_for_service "http://localhost:8083/actuator/health" "grpc-service"

  echo "Waiting 10 s for JVM JIT warm-up"
  sleep 10

  seed_books

  run_k6 "$MODE" "rest"    "tests/rest.js"
  run_k6 "$MODE" "graphql" "tests/graphql.js"
  run_k6 "$MODE" "grpc"    "tests/grpc.js"

  if [[ "$MODE" == "off" ]]; then
    run_k6 "$MODE" "payload" "tests/payload-comparison.js"
  fi
}

MODE="${1:-all}"

echo "Starting infrastructure services"
cd "$PROJECT_DIR"
docker compose up -d postgres otel-collector jaeger prometheus grafana
cd "$SCRIPT_DIR"

echo "Waiting 15 s for infrastructure to initialise"
sleep 15

echo "Building application service images"
cd "$PROJECT_DIR"
docker compose build rest-service graphql-service grpc-service
cd "$SCRIPT_DIR"

if [[ "$MODE" == "all" ]]; then
   for M in off on-100; do
    run_mode "$M"
  done
else
  run_mode "$MODE"
fi
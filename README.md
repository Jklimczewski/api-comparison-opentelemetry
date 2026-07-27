# API Benchmark

This repository compares three ways of exposing the same bookstore workload:

- REST in [rest-service](rest-service)
- GraphQL in [graphql-service](graphql-service)
- gRPC in [grpc-service](grpc-service)

All services share the common domain and data access code from [common](common) and use PostgreSQL for persistence.

## What Is Included

- Spring Boot 3.3 services built with Maven and Java 21
- PostgreSQL seeded with sample book data
- OpenTelemetry, Jaeger, Prometheus, and Grafana for observability
- k6 scripts for load and payload comparisons

## Architecture

The default Docker Compose stack starts the supporting infrastructure and the three application services:

- PostgreSQL on `5432`
- OpenTelemetry Collector on `4317` and `4318`
- Jaeger on `16686`
- Prometheus on `9090`
- Grafana on `3000`
- REST service on `8081`
- GraphQL service on `8082`
- gRPC service on `8083` and `9091`

The services expose health and metrics endpoints and export traces through the OpenTelemetry Collector.

## Prerequisites

To run the full stack locally you need:

- Java 21
- Maven 3.9+
- Docker and Docker Compose
- k6 for benchmarks
- `psql` if you want to seed the databases manually from the benchmark scripts

## Build

Build all Maven modules from the repository root:

```bash
mvn clean package
```

## Run the stack

Start the observability and database services together with the application services:

```bash
docker compose up
```

Health checks:

- REST: `http://localhost:8081/actuator/health`
- GraphQL: `http://localhost:8082/actuator/health`
- gRPC: `http://localhost:8083/actuator/health`

## Benchmarking

The benchmark runner is [k6/run.sh](k6/run.sh). It:

1. Starts the infrastructure services
2. Builds the service images
3. Recreates the application services for the selected OpenTelemetry mode
4. Waits for health checks
5. Seeds the databases
6. Runs the k6 scripts in [k6/tests](k6/tests)

Run the full benchmark suite:

```bash
cd k6
./run.sh
```

Run a single OpenTelemetry mode:

```bash
cd k6
./run.sh off
./run.sh on-100
```

Benchmark results are written to `k6/results/` as JSON files.

## Service Endpoints

REST:

- Base URL: `http://localhost:8081`
- Books API: `GET /api/books`

GraphQL:

- Base URL: `http://localhost:8082`
- GraphQL endpoint: `POST /graphql`
- GraphiQL: enabled in the service configuration

gRPC:

- Base address: `localhost:9091`
- gRPC service port inside the container: `9090`

## Observability

Useful UIs after the stack is up:

- Jaeger: `http://localhost:16686`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

Grafana uses the provisioning files in [grafana/provisioning](grafana/provisioning).

## Notes

- Each service has its own `Dockerfile` and Spring configuration in its module directory.
- Database initialization scripts live in [postgres/init.sql](postgres/init.sql) and [k6/seed.sql](k6/seed.sql).
- The root [pom.xml](pom.xml) defines the shared Maven modules and dependency management.

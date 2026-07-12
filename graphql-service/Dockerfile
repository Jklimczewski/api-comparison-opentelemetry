FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY rest-service/pom.xml rest-service/pom.xml
COPY graphql-service/pom.xml graphql-service/pom.xml
COPY grpc-service/pom.xml grpc-service/pom.xml
RUN mvn -q -pl graphql-service -am dependency:go-offline

COPY common/src common/src
COPY graphql-service/src graphql-service/src
RUN mvn -q -pl graphql-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.8.0/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar
COPY --from=build /workspace/graphql-service/target/graphql-service.jar /app/app.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/otel/opentelemetry-javaagent.jar"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

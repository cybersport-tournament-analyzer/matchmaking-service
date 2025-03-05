FROM gradle:8.2.1-jdk17 AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN gradle build -x test

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/matchmaking-service-0.0.1-SNAPSHOT.jar app.jar

COPY live_server.cfg /app/live_server.cfg

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

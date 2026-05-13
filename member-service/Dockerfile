FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /workspace
COPY --chown=gradle:gradle . .
RUN gradle :member-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/member-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

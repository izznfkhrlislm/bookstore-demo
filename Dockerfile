FROM gradle:jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
EXPOSE 8080
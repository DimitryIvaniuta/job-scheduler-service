# --- Build stage -------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace/app

# Install bash (for Gradle wrapper if needed)
RUN apk add --no-cache bash

# Copy Gradle wrapper + build scripts + sources
COPY gradlew .
COPY gradle ./gradle
COPY settings.gradle .
COPY build.gradle .
COPY src ./src

# Build fat jar
RUN chmod +x ./gradlew \
    && ./gradlew clean bootJar --no-daemon

# --- Runtime stage -----------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

ENV TZ=UTC \
    JAVA_OPTS=""

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring
WORKDIR /app

# Copy jar from build stage
COPY --from=builder /workspace/app/build/libs/job-scheduler-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# ==============================================================================
# MULTI-STAGE DOCKERFILE FOR SPRING BOOT REAL-TIME BUS TRACKING SYSTEM
# ==============================================================================

# Build Stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build project
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/realtime-bus-tracking-system-1.0.0.jar app.jar

EXPOSE 8080
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=h2

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-h2}"]

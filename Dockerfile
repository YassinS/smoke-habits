# --------- STAGE 1: Build ---------
FROM maven:3.9.3-eclipse-temurin-20 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (caching layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# --------- STAGE 2: Run ---------
FROM eclipse-temurin:20-jre-jammy

# Set working directory
WORKDIR /app

# Copy Maven/Gradle build artifact
COPY --from=build /app/target/smoke-habits-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Command: wait for Postgres and then start Spring Boot
CMD [ "java", "-jar", "app.jar"]

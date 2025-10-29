FROM openjdk:25-bookworm

WORKDIR /app

COPY target/smoke-habits-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

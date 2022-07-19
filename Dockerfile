FROM openjdk:11-jdk-slim

COPY ./target/edge-*.jar /app/service.jar

CMD ["java", "-jar", "/app/service.jar"]
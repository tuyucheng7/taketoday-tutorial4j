FROM openjdk:8-jdk-alpine
COPY target/shipping-service-async-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","/app.jar"]
FROM openjdk:8-jre-alpine
ADD target/mesos-marathon-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app.jar"]
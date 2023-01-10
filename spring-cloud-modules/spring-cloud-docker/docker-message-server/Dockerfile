FROM openjdk:8-jdk-alpine
MAINTAINER tuyucheng.com
COPY target/docker-message-server-1.0.0.jar message-server.jar
ENTRYPOINT ["java","-jar","/message-server.jar"]
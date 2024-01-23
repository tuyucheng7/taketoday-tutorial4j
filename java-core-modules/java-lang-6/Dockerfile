FROM maven:3.9-amazoncorretto-17
WORKDIR /app
COPY /src/test/java/cn/tuyucheng/taketoday/setenvironment/SettingDockerEnvironmentVariableUnitTest.java \
 ./src/test/java/cn/tuyucheng/taketoday/setenvironment/
COPY /docker-pom.xml ./
ENV CUSTOM_DOCKER_ENV_VARIABLE=TRUE
ENTRYPOINT mvn -f docker-pom.xml test
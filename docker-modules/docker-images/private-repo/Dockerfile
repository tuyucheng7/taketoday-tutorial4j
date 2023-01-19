FROM openjdk:17
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} docker-images-1.0.0.jar
ENTRYPOINT ["java","-jar","/docker-images-1.0.0.jar"]
FROM openjdk:8u92-jdk-alpine
COPY /src /src/
RUN mkdir /app && ls /src && javac /src/main/java/cn/tuyucheng/taketoday/docker/heapsizing/PrintXmxXms.java -d /app
CMD java -version && java $JAVA_OPTS -cp /app cn.tuyucheng.taketoday.docker.heapsizing.PrintXmxXms
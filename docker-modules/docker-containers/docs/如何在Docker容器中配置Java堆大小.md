## 1. 概述

当我们在容器中运行Java时，我们可能希望对其进行优化，以最大限度地利用可用资源。

在本教程中，我们介绍如何在运行Java进程的容器中设置[JVM参数]()。尽管以下内容适用于任何JVM设置，但我们重点关注常见的[-Xmx和-Xms配置](https://docs.oracle.com/en/java/javase/11/tools/java.html)。

我们还将研究使用特定版本的Java运行的容器化程序的常见问题，以及如何在一些流行的容器化Java应用程序中设置标志。

## 2. Java容器中的默认堆设置

JVM非常擅长确定适当的默认[内存设置]()。

在过去，[JVM并不知道分配给容器的内存和CPU](https://developers.redhat.com/blog/2017/03/14/java-inside-docker/)。因此，Java 10引入了一个新设置：+UseContainerSupport(默认启用)来修复[根本原因](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8146115)，开发人员在[8u191](https://www.oracle.com/technetwork/java/javase/8u191-relnotes-5032181.html#JDK-8146115)中将该修复程序反向移植到Java 8 。JVM现在根据分配给容器的内存计算其内存。

但是，我们可能仍希望更改某些应用程序中的默认设置。

### 2.1 自动内存计算

**当我们不设置-Xmx和-Xmx参数时，JVM会根据系统规范调整堆大小**。

我们可以使用下面命令查看堆大小：

```shell
$ java-XX:+PrintFlagsFinal -version | grep -Ei "maxheapsize|maxram"
```

这会输出：

```shell
openjdk version "15" 2022-11-16
OpenJDK Runtime Environment AdoptOpenJDK (build 15+36)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 15+36, mixed mode, sharing)
   size_t MaxHeapSize      = 4253024256      {product} {ergonomic}
 uint64_t MaxRAM           = 137438953472 {pd product} {default}
    uintx MaxRAMFraction   = 4               {product} {default}
   double MaxRAMPercentage = 25.000000       {product} {default}
   size_t SoftMaxHeapSize  = 4253024256   {manageable} {ergonomic}
```

在这里，我们看到JVM将其堆大小设置为可用RAM的大约25%。在本例中，它在具有16GB的系统上分配了4GB。

出于测试目的，我们编写一个以兆字节为单位打印堆大小的程序：

```java
public static void main(String[] args) {
    int mb = 1024  1024;
    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    long xmx = memoryBean.getHeapMemoryUsage().getMax() / mb;
    long xms = memoryBean.getHeapMemoryUsage().getInit() / mb;
    LOGGER.log(Level.INFO, "Initial Memory (xms) : {0}mb", xms);
    LOGGER.log(Level.INFO, "Max Memory (xmx) : {0}mb", xmx);
}
```

我们将该程序放在一个空目录中，在一个名为PrintXmxXms.java的文件中。

我们可以在我们的主机上测试它，假设我们已经安装了JDK。在Linux系统中，我们可以编译我们的程序并从该目录中打开的终端运行它：

```shell
$ javac ./PrintXmxXms.java
$Java-cp . PrintXmxXms
```

在具有16Gb RAM的系统上，输出为：

```shell
INFO: Initial Memory (xms) : 254mb
INFO: Max Memory (xmx) : 4,056mb
```

接下来，我们在一些容器中演示这些。

### 2.2 在JDK 8u191之前

我们在包含我们的Java程序的文件夹中添加以下Dockerfile ：

```dockerfile
FROM openjdk:8u92-jdk-alpine
COPY .java /src/
RUN mkdir /app \
    && ls /src \
    && javac /src/PrintXmxXms.java -d /app
CMD ["sh", "-c", \
     "java -version \
      &&Java-cp /app PrintXmxXms"]
```

在这里，我们使用的是一个使用旧版本Java 8的容器，它早于最新版本中可用的容器支持。接下来我们构建镜像：

```shell
$ docker build -t oldjava .
```

Dockerfile中的CMD行是我们运行容器时默认执行的进程。**由于我们没有提供-Xmx或-Xms JVM标志，因此将默认内存设置**。

接下来运行容器：

```shell
$ docker run --rm -ti oldjava
openjdk version "1.8.0_92-internal"
OpenJDK Runtime Environment (build 1.8.0_92-...)
OpenJDK 64-Bit Server VM (build 25.92-b14, mixed mode)
Initial Memory (xms) : 198mb
Max Memory (xmx) : 2814mb
```

现在让我们将容器内存限制为1GB。

```shell
$ docker run --rm -ti --memory=1g oldjava
openjdk version "1.8.0_92-internal"
OpenJDK Runtime Environment (build 1.8.0_92-...)
OpenJDK 64-Bit Server VM (build 25.92-b14, mixed mode)
Initial Memory (xms) : 198mb
Max Memory (xmx) : 2814mb
```

如我们所见，输出完全相同。**这证明了较旧版本的JVM不遵循容器内存分配**。

### 2.3 JDK 8u130之后

使用相同的测试程序，我们通过更改Dockerfile的第一行来使用更新的JVM 8：

```dockerfile
FROM openjdk:8-jdk-alpine
```

然后我们可以再次测试它：

```shell
$ docker build -t newjava .
$ docker run --rm -ti newjava
openjdk version "1.8.0_212"
OpenJDK Runtime Environment (IcedTea 3.12.0) (Alpine 8.212.04-r0)
OpenJDK 64-Bit Server VM (build 25.212-b04, mixed mode)
Initial Memory (xms) : 198mb
Max Memory (xmx) : 2814mb
```

同样，它使用整个docker主机内存来计算JVM堆大小。但是，如果我们只为容器分配1GB的RAM：

```shell
$ docker run --rm -ti --memory=1g newjava
openjdk version "1.8.0_212"
OpenJDK Runtime Environment (IcedTea 3.12.0) (Alpine 8.212.04-r0)
OpenJDK 64-Bit Server VM (build 25.212-b04, mixed mode)
Initial Memory (xms) : 16mb
Max Memory (xmx) : 247mb
```

这次，JVM根据容器可用的1GB RAM计算堆大小。

现在我们了解了JVM如何计算其默认值，以及为什么我们需要最新的JVM来获得正确的默认值，让我们看看自定义设置。

## 3. 常用基础镜像中的内存设置

### 3.1 OpenJDK和AdoptOpenJDK

与其直接在我们的容器命令上硬编码JVM参数，不如使用环境变量(例如JAVA_OPTS是一种很好的做法)。我们在Dockerfile中使用该变量，但可以在启动容器时对其进行修改：

```dockerfile
FROM openjdk:8u92-jdk-alpine
COPY src/ /src/
RUN mkdir /app \
 && ls /src \
 && javac /src/cn/tuyucheng/taketoday/docker/printxmxxms/PrintXmxXms.java \
    -d /app
ENV JAVA_OPTS=""
CMDJava$JAVA_OPTS -cp /app \ 
    cn.tuyucheng.taketoday.docker.printxmxxms.PrintXmxXms
```

现在我们构建镜像：

```shell
$ docker build -t openjdk-java .
```

我们可以通过指定JAVA_OPTS环境变量在运行时选择我们的内存设置：

```shell
$ docker run --rm -ti -e JAVA_OPTS="-Xms50M -Xmx50M" openjdk-java
INFO: Initial Memory (xms) : 50mb
INFO: Max Memory (xmx) : 48mb
```

我们应该注意到，-Xmx参数与JVM报告的最大内存之间存在细微差别。这是因为Xmx设置了内存分配池的最大大小，其中包括堆、垃圾收集器的幸存者空间和其他池。

### 3.2 Tomcat 9

Tomcat 9容器有自己的启动脚本，因此要设置JVM参数，我们需要使用这些脚本。

bin/catalina.sh脚本要求我们**在环境变量CATALINA_OPTS中设置内存参数**。

首先我们创建一个war文件并将其部署到Tomcat。然后，我们将使用一个简单的Dockerfile将其容器化，我们在其中声明CATALINA_OPTS环境变量：

```dockerfile
FROM tomcat:9.0
COPY ./target/.war /usr/local/tomcat/webapps/ROOT.war
ENV CATALINA_OPTS="-Xms1G -Xmx1G"
```

然后我们构建容器镜像并运行它：

```shell
$ docker build -t tomcat .
$ docker run --name tomcat -d -p 8080:8080 \
  -e CATALINA_OPTS="-Xms512M -Xmx512M" tomcat
```

注意，当我们运行它时，我们正在向CATALINA_OPTS传递一个新值。但是，如果我们不提供此值，则会使用我们在Dockerfile的第3行中提供一些默认值。

我们可以检查应用的运行时参数并验证我们的参数-Xmx和-Xms是否存在：

```shell
$ docker exec -ti tomcat jps -lv
1 org.apache.catalina.startup.Bootstrap <other options...> -Xms512M -Xmx512M
```

## 4. 使用构建插件

Maven和Gradle提供的插件允许我们在没有Dockerfile的情况下创建容器镜像，生成的镜像通常可以在运行时通过环境变量进行参数化。让我们看几个例子。

### 4.1 使用Spring Boot

从Spring Boot 2.3开始，Spring Boot [Maven](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#build-image)和[Gradle](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image)插件可以在没有[Dockerfile](https://spring.io/guides/topicals/spring-boot-docker/)[的情况下构建高效的容器](https://spring.io/guides/topicals/spring-boot-docker/)。

使用Maven，我们将它们添加到spring-boot-maven-plugin中的<configuration\>标签：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <artifactId>heapsizing-demo</artifactId>
    <version>1.0.0</version>
    <!-- dependencies... -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <image>
                        <name>heapsizing-demo</name>
                    </image>
                    <!-- 
                     for more options, check:
                     https://docs.spring.io/spring-boot/docs/2.4.2/maven-plugin/reference/htmlsingle/#build-image 
                    -->
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

要构建项目，运行以下命令：

```shell
$ ./mvnw clean spring-boot:build-image
```

这将生成名为<artifact-id\>:<version\>的镜像，在本例中为heapsizing-demo:1.0.0。在底层，Spring Boot使用[Cloud Native Buildpacks](https://buildpacks.io/)作为底层容器化技术。

**该插件对JVM的内存设置进行硬编码。但是，我们仍然可以通过设置环境变量JAVA_OPTS或JAVA_TOOL_OPTIONS 来覆盖它们**：

```shell
$ docker run --rm -ti -p 8080:8080 \
  -e JAVA_TOOL_OPTIONS="-Xms20M -Xmx20M" \
  --memory=1024M heapsizing-demo:1.0.0
```

输出类似于：

```shell
Setting Active Processor Count to 8
CalculatedJVMMemory Configuration: [...]
[...]
Picked up JAVA_TOOL_OPTIONS: -Xms20M -Xmx20M 
[...]
```

### 4.2 使用谷歌JIB

就像Spring Boot maven插件一样，[Google JIB]()无需Dockerfile即可创建高效的Docker镜像，Maven和Gradle插件的配置方式类似。Google JIB还使用环境变量JAVA_TOOL_OPTIONS作为JVM参数的覆盖机制。

我们可以在任何能够生成可执行jar文件的Java框架中使用Google JIB Maven插件。例如，可以在Spring Boot应用程序中使用它代替spring-boot-maven插件来生成容器镜像：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    
    <!-- dependencies, ... -->

    <build>
        <plugins>
            <!-- [ other plugins ] -->
            <plugin>
                <groupId>com.google.cloud.tools</groupId>
                <artifactId>jib-maven-plugin</artifactId>
                <version>2.7.1</version>
                <configuration>
                    <to>
                        <image>heapsizing-demo-jib</image>
                    </to>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```

该镜像是使用maven jib:dockerBuild目标(goal)构建的：

```shell
$ mvn clean install && mvn jib:dockerBuild
```

现在我们可以像往常一样运行它：

```shell
$ docker run --rm -ti -p 8080:8080 \
-e JAVA_TOOL_OPTIONS="-Xms50M -Xmx50M" heapsizing-demo-jib
Picked up JAVA_TOOL_OPTIONS: -Xms50M -Xmx50M
[...]
2022-11-16 14:30:44.070  INFO 1 --- [           main] c.t.taketoday.docker.XmxXmsDemoApplication  : Started XmxXmsDemoApplication in 1.666 seconds (JVM running for 2.104)
2022-11-16 14:30:44.075  INFO 1 --- [           main] c.t.taketoday.docker.XmxXmsDemoApplication  : Initial Memory (xms) : 50mb
2022-11-16 14:30:44.075  INFO 1 --- [           main] c.t.taketoday.docker.XmxXmsDemoApplication  : Max Memory (xmx) : 50mb
```

## 5. 总结

在本文中，我们介绍了使用最新的JVM来获得在容器中正常工作的默认内存设置的必要性。

然后，我们介绍了在自定义容器镜像中设置-Xms和-Xmx的最佳实践，以及如何使用现有Java应用程序容器在其中设置JVM参数。最后，我们了解了如何利用构建工具来管理Java应用程序的容器化。

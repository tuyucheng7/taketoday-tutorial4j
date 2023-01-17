## 1. 概述

在本教程中，我们将了解 Jib 以及它如何简化Java应用程序的容器化。

我们将使用一个简单的Spring Boot应用程序并使用 Jib 构建它的 Docker 镜像。然后我们还将图像发布到远程注册表。

并确保还参考了我们关于 使用[dockerfile](https://www.baeldung.com/dockerizing-spring-boot-application)[和 d ](https://www.baeldung.com/dockerizing-spring-boot-application)[ocker](https://www.baeldung.com/dockerizing-spring-boot-application)[工具对](https://www.baeldung.com/dockerizing-spring-boot-application)[Spring Boot 应用程序进行](https://www.baeldung.com/dockerizing-spring-boot-application)dockerizing 的教程。

## 二、Jib简介

Jib是由 Google 维护的开源Java工具，用于构建Java应用程序的 Docker 映像。它简化了容器化，因为有了它， 我们不需要编写dockerfile。

实际上，我们甚至不必安装docker就可以自己创建和发布 docker 镜像。

Google 将 Jib 作为 Maven 和 Gradle 插件发布。这很好，因为这意味着每次构建时 Jib 都会捕获我们对应用程序所做的任何更改。这为我们节省了单独的 docker build/push 命令，并简化了将其添加到 CI 管道的过程。

还有一些其他工具，例如 Spotify 的docker- [maven-plugin](https://github.com/spotify/docker-maven-plugin) 和 [dockerfile-maven](https://github.com/spotify/dockerfile-maven) 插件，尽管前者现在已被弃用，后者需要 dockerfile。

## 3. 一个简单的问候应用

让我们以一个简单的 spring-boot 应用程序为例，并使用 Jib 将其 dockerize 化。它将公开一个简单的 GET 端点：

```bash
http://localhost:8080/greeting
```

我们可以使用 Spring MVC 控制器非常简单地完成：

```java
@RestController
public class GreetingController {

    private static final String template = "Hello Docker, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", 
        defaultValue="World") String name) {
        
        return new Greeting(counter.incrementAndGet(),
          String.format(template, name));
    }
}

```

## 4.准备部署

我们还需要在本地进行设置，以使用我们要部署到的 Docker 存储库进行身份验证。

对于此示例，我们将向.m2/settings.xml提供我们的 DockerHub 凭据 ：

```xml
<servers>
    <server>
        <id>registry.hub.docker.com</id>
        <username><DockerHub Username></username>
        <password><DockerHub Password></password>
    </server>
</servers>
```

还有其他方法可以提供凭据。Google 推荐的方法是使用辅助工具，它可以将凭证以加密格式存储在文件系统中。在此示例中，我们可以使用 [docker-credential-helpers](https://github.com/docker/docker-credential-helpers#available-programs) 而不是将纯文本凭证存储在settings.xml 中，这样更安全，但不在本教程的范围内。

## 5. 使用 Jib 部署到 Docker Hub

现在，我们可以使用 jib-maven-plugin或 [Gradle 等效](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)插件， 通过一个简单的命令将我们的应用程序容器化 ：

```bash
mvn compile com.google.cloud.tools:jib-maven-plugin:2.5.0:build -Dimage=$IMAGE_PATH
```

其中 IMAGE_PATH 是容器注册表中的目标路径。

例如，要将图像baeldungjib/spring-jib-app上传到DockerHub，我们会这样做：

```bash
export IMAGE_PATH=registry.hub.docker.com/baeldungjib/spring-jib-app
```

就是这样！这将构建我们应用程序的 docker 镜像并将其推送到DockerHub。

[![ibDocker 1](https://www.baeldung.com/wp-content/uploads/2018/10/JibDocker-1-1024x640-1.jpg)](https://www.baeldung.com/wp-content/uploads/2018/10/JibDocker-1-1024x640-1.jpg)

我们当然可以 通过类似的方式将镜像上传到[Google Container Registry](https://cloud.google.com/container-registry/) 或者 [Amazon Elastic Container Registry](https://aws.amazon.com/ecr/)。

## 6. 简化 Maven 命令

此外，我们可以通过在我们的pom中配置插件来缩短我们的初始命令，就像任何其他 Maven 插件一样。

```xml
<project>
    ...
    <build>
        <plugins>
            ...
            <plugin>
                <groupId>com.google.cloud.tools</groupId>
                <artifactId>jib-maven-plugin</artifactId>
                <version>2.5.0</version>
                <configuration>
                    <to>
                        <image>${image.path}</image>
                    </to>
                </configuration>
            </plugin>
            ...
        </plugins>
    </build>
    ...
</project>
```

通过此更改，我们可以简化我们的 maven 命令：

```bash
mvn compile jib:build
```

## 7. 自定义 Docker 方面

默认情况下，Jib 会对我们想要的内容进行一些合理的猜测，例如 FROM 和 ENTRYPOINT。

让我们对我们的应用程序进行一些更符合我们需求的更改。

首先，Spring Boot 默认暴露了 8080 端口。

但是，比方说，我们想让我们的应用程序在端口 8082 上运行并使其可通过容器公开。

当然，我们会在 Boot. 之后，我们可以使用 Jib 使其在图像中可见：

```xml
<configuration>
    ...
    <container>
        <ports>
            <port>8082</port>
        </ports>
    </container>
</configuration>
```

或者，假设我们需要一个不同的 FROM。默认情况下，Jib 使用 [distro-less java 镜像](https://github.com/GoogleContainerTools/distroless/tree/master/java)。

如果我们想在不同的基础镜像上运行我们的应用程序，比如 [alpine-java](https://hub.docker.com/r/anapsix/alpine-java/)，我们可以用类似的方式配置它：

```xml
<configuration>
    ...
    <from>                           
        <image>openjdk:alpine</image>
    </from>
    ...
</configuration>
```

我们以相同的方式配置标签、卷和[其他几个 Docker 指令](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#extended-usage)。

## 8. 自定义Java方面

而且，通过关联，Jib 也支持许多Java运行时配置：

-   jvmFlags 用于指示要传递给 JVM 的启动标志。
-   mainClass 用于指示主类， 默认情况下 Jib 将尝试自动推断。 
-   args是我们指定传递给 main 方法的程序参数的地方。

当然，请务必查看 Jib 的文档以查看所有[可用的配置属性](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)。

## 9.总结

在本教程中，我们了解了如何使用 Google 的 Jib 构建和发布 docker 镜像，包括如何通过 Maven 访问 docker 指令和Java运行时配置。
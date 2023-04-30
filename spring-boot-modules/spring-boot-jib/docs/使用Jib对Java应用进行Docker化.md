## 1. 概述

在本教程中，我们将了解Jib以及它如何简化Java应用程序的容器化。

我们将使用一个简单的Spring Boot应用程序并使用Jib构建它的Docker镜像。然后，我们还会将镜像发布到远程注册中心。

并确保还参考了我们关于使用[Dockerfile和Docker工具对Spring Boot应用程序进行容器化](https://www.baeldung.com/dockerizing-spring-boot-application)的教程。

## 2. Jib简介

Jib是由Google维护的开源Java工具，用于构建Java应用程序的Docker镜像。它简化了容器化，因为有了它，**我们不需要编写Dockerfile**。

实际上，**我们甚至不必安装Docker就可以自己创建和发布Docker镜像**。

Google将Jib作为Maven和Gradle插件发布。这很好，因为这意味着每次构建时Jib都会捕获我们对应用程序所做的任何更改。**这为我们节省了单独的docker build/push命令，并简化了将其添加到CI管道的过程**。

还有一些其他工具，例如Spotify的[docker-maven-plugin](https://github.com/spotify/docker-maven-plugin)和[dockerfile-maven](https://github.com/spotify/dockerfile-maven)插件，但是前者现在已被弃用，而后者需要Dockerfile。

## 3. 一个简单的问候应用程序

让我们以一个简单的Spring Boot应用程序为例，并使用Jib将其容器化。它将公开一个简单的GET端点：

```text
http://localhost:8080/greeting
```

我们可以简单地使用Spring MVC控制器来完成：

```java
@RestController
public class GreetingController {
    private static final String template = "Hello Docker, %s!";
    
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}
```

## 4. 准备部署

我们还需要在本地进行设置，以使用我们要部署到的Docker仓库进行身份验证。

对于此示例，**我们将向.m2/settings.xml提供我们的DockerHub凭据**：

```xml
<servers>
    <server>
        <id>registry.hub.docker.com</id>
        <username>{DockerHub Username}</username>
        <password>{DockerHub Password}</password>
    </server>
</servers>
```

还有其他方法可以提供凭据。**Google推荐的方法是使用辅助工具，它可以将凭证以加密格式存储在文件系统中**。在此示例中，我们可以使用[docker-credential-helpers](https://github.com/docker/docker-credential-helpers#available-programs)而不是将纯文本凭证存储在settings.xml中，这样更安全，但不在本教程的范围内。

## 5. 使用Jib部署到DockerHub

现在，我们可以使用jib-maven-plugin或[Gradle等效](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)插件，**通过一个简单的命令将我们的应用程序容器化**：

```shell
mvn compile com.google.cloud.tools:jib-maven-plugin:2.5.0:build -Dimage=$IMAGE_PATH
```

其中IMAGE_PATH是容器注册中心中的目标路径。

例如，要将镜像tuyuchengjib/spring-jib-app上传到DockerHub，我们将执行以下操作：

```shell
export IMAGE_PATH=registry.hub.docker.com/tuyuchengjib/spring-jib-app
```

就是这样！这将构建我们应用程序的Docker镜像并将其推送到DockerHub。

<img src="../assets/img.png">

**当然，我们可以通过类似的方式将镜像上传到[Google Container Registry](https://cloud.google.com/container-registry/)或者[Amazon Elastic Container Registry](https://aws.amazon.com/ecr/)**。

## 6. 简化Maven命令

此外，我们可以通过在pom中配置插件来缩短我们的初始命令，就像任何其他Maven插件一样。

```xml
<project>
    <!--...-->
    <build>
        <plugins>
            <!--...-->
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
            <!--...-->
        </plugins>
    </build>
    <!--...-->
</project>
```

通过此更改，我们可以简化Maven命令：

```shell
mvn compile jib:build
```

## 7. 自定义Docker方面

默认情况下，**Jib会对我们想要的内容进行一些合理的猜测**，例如FROM和ENTRYPOINT。

让我们对我们的应用程序进行一些更符合我们需求的更改。

首先，Spring Boot默认暴露了8080端口。

但是，比方说，我们想让我们的应用程序在端口8082上运行并使其可通过容器公开。

当然，我们会在Boot中进行适当的更改。之后，我们可以使用Jib使其在镜像中可公开：

```xml
<configuration>
    <!--...-->
    <container>
        <ports>
            <port>8082</port>
        </ports>
    </container>
</configuration>
```

或者，假设我们需要一个不同的FROM。**默认情况下，Jib使用[无发行版的Java镜像](https://github.com/GoogleContainerTools/distroless/tree/master/java)**。

如果我们想在不同的基础镜像(比如[alpine-java](https://hub.docker.com/r/anapsix/alpine-java/))上运行我们的应用程序，我们可以用类似的方式配置它：

```xml
<configuration>
    <!--...-->
    <from>
        <image>openjdk:alpine</image>
    </from>
    <!--...-->
</configuration>
```

我们以相同的方式配置tags、volumes和[其他几个Docker指令](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#extended-usage)。

## 8. 自定义Java方面

而且，通过关联，Jib也支持许多Java运行时配置：

-   jvmFlags用于指示要传递给JVM的启动标志。
-   mainClass用于指示主类，**默认情况下Jib将尝试自动推断**。
-   args是我们指定传递给main方法的程序参数的地方。

当然，请务必查看Jib的文档以查看所有[可用的配置属性](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)。

## 9. 总结

在本教程中，我们了解了如何使用Google的Jib构建和发布Docker镜像，包括如何通过Maven访问Docker指令和Java运行时配置。
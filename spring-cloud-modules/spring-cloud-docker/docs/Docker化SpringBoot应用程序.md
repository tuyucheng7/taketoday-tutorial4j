## 1. 概述

在本教程中，我们将重点介绍如何将Spring Boot应用程序docker化以在隔离环境(即容器)中运行它。

我们将学习如何创建容器组合，这些容器相互依赖并在虚拟专用网络中相互链接。我们还将看到如何使用单个命令一起管理它们。

让我们从创建一个简单的Spring Boot应用程序开始，然后我们将在运行[Alpine Linux](https://hub.docker.com/_/alpine/)的轻量级基础镜像中运行该应用程序。

## 2. 容器化一个独立的Spring Boot应用程序

作为我们可以容器化的应用程序示例，我们将创建一个简单的Spring Boot应用程序docker-message-server，它公开一个端点并返回一条静态消息：

```java
@RestController
public class DockerMessageController {

	@GetMapping("/messages")
	public String getMessage() {
		return "Hello from Docker!";
	}
}
```

使用正确配置的Maven文件，我们可以创建一个可执行的jar文件：

```shell
$> mvn clean package
```

接下来，我们将启动Spring Boot应用程序：

```shell
$> java -jar target/docker-message-server-1.0.0.jar
```

现在我们有一个工作的Spring Boot应用程序，我们可以在localhost:8888/messages访问它。

为了对应用程序进行容器化，我们首先创建一个名为Dockerfile的文件，其中包含以下内容：

```dockerfile
FROM openjdk:8-jdk-alpine
MAINTAINER tuyucheng.com
COPY target/docker-message-server-1.0.0.jar message-server-1.0.0.jar
ENTRYPOINT ["java","-jar","/message-server-1.0.0.jar"]
```

该文件包含以下信息：

-   **FROM**：作为我们镜像的基础，我们将采用在上一节中创建的支持Java的Alpine Linux
-   **MAINTAINER**：镜像的维护者
-   **COPY**：我们让Docker将我们的jar文件复制到镜像中
-   **ENTRYPOINT**：这将是容器启动时要启动的可执行文件。我们必须将它们定义为JSON数组，因为我们将结合使用ENTRYPOINT和CMD来处理某些应用程序参数

要从我们的Dockerfile创建镜像，我们必须像以前一样运行“docker build”：

```shell
$> docker build --tag=message-server:latest .
```

最后，我们可以从我们的镜像运行容器：

```shell
$> docker run -p8887:8888 message-server:latest
```

这将在Docker中启动我们的应用程序，我们可以从位于localhost:8887/messages的主机访问它。这里定义端口映射很重要，它将主机上的端口(8887)映射到Docker内部的端口(8888)。这是我们在Spring Boot应用程序的属性中定义的端口。

> 注意：端口8887在我们启动容器的机器上可能不可用。在这种情况下，映射可能不起作用，我们需要选择一个仍然可用的端口。

如果我们以分离模式运行容器，我们可以使用以下命令检查其详细信息、停止它并删除它：

```shell
$> docker inspect message-server
$> docker stop message-server
$> docker rm message-server
```

### 2.1 更改基础镜像

我们可以很容易地更改基础镜像以使用不同的Java版本。例如，如果我们想使用亚马逊的Corretto发行版，我们可以简单地更改Dockerfile：

```dockerfile
FROM amazoncorretto:11-alpine-jdk
MAINTAINER tuyucheng.com
COPY target/docker-message-server-1.0.0.jar message-server-1.0.0.jar
ENTRYPOINT ["java","-jar","/message-server-1.0.0.jar"]
```

此外，我们可以使用自定义基础镜像。我们将在本教程的后面部分介绍如何执行此操作。

## 3. 容器化组合中的应用程序

Docker命令和Dockerfiles特别适合创建单独的容器。但是，如果我们想在一个隔离的应用程序网络上运行，容器管理很快就会变得混乱。

**为了解决这个问题，Docker提供了一个名为Docker Compose的工具**。该工具自带YAML格式的构建文件，更适合管理多个容器。例如，它能够在一个命令中启动或停止一组服务，或者将多个服务的日志输出合并到一个伪终端中。

### 3.1 第二个Spring Boot应用

让我们构建一个在不同Docker容器中运行的两个应用程序的示例。它们将相互通信，并作为“单个单元”呈现给主机系统。作为一个简单的例子，我们将创建第二个Spring Boot应用程序docker-product-server：

```java
@RestController
public class DockerProductController {

	@GetMapping("/products")
	public String getMessage() {
		return "A brand new product";
	}
}
```

我们可以像我们的message-server一样构建和启动应用程序。

### 3.2 Docker Compose文件

我们可以将两种服务的配置合并到一个名为docker-compose.yml的文件中：

```yaml
version: '2'
services:
    message-server:
        container_name: message-server
        build:
            context: docker-message-server
            dockerfile: Dockerfile
        image: message-server:latest
        ports:
            - 18888:8888
        networks:
            - spring-cloud-network
    product-server:
        container_name: product-server
        build:
            context: docker-product-server
            dockerfile: Dockerfile
        image: product-server:latest
        ports:
            - 19999:9999
        networks:
            - spring-cloud-network
networks:
    spring-cloud-network:
        driver: bridge
```

-   **version**：指定应使用的格式版本。这是必填字段。这里我们使用较新的版本，而旧版格式是“1”。
-   **services**：这个key中的每个对象都定义了一个service，也就是容器。此部分是强制性的。
    -   **build**：如果给定，docker-compose能够从Dockerfile构建镜像
        -   **context**：如果给定，则指定查找Dockerfile的构建目录。
        -   **dockerfile**：如果给定，它会为Dockerfile设置一个备用名称。
    -   **image**：告诉Docker在使用构建功能时应该给镜像起什么名字。否则，它会在库或远程注册表中搜索此镜像。
    -   **networks**：这是要使用的命名网络的标识符。给定的名称值必须在networks部分中列出。
-   **networks**：在本节中，我们将指定我们的服务可用的网络。在这个例子中，我们让docker-compose为我们创建一个类型为“bridge”的命名网络。如果选项external设置为true，它将使用具有给定名称的现有选项。

在我们继续之前，我们将检查我们的构建文件是否存在语法错误：

```shell
$> docker-compose config
```

然后我们可以构建我们的镜像，创建定义的容器，并在一个命令中启动它：

```shell
$> docker-compose up --build
```

这将一次性启动message-server和product-server。

要停止容器，请将它们从Docker中删除并从中删除连接的网络。为此，我们可以使用相反的命令：

```shell
$> docker-compose down
```

关于docker-compose更详细的介绍，可以阅读我们的文章[Docker Compose简介](https://www.baeldung.com/docker-compose)。

### 3.3 扩展服务

docker-compose的一个很好的特性是**能够扩展服务**。例如，我们可以告诉Docker为message-server运行三个容器，为product-server运行两个容器。

但是，为了使其正常工作，我们必须从docker-compose.yml中删除container_name，这样Docker才能选择名称，并更改暴露的端口配置以避免冲突。

对于端口，我们可以告诉Docker将主机上的一系列端口映射到Docker中的一个特定端口：

```yaml
ports:
    - 18800-18888:8888
```

之后，我们就可以像这样扩展我们的服务(请注意，我们使用的是修改后的yml-file)：

```shell
$> docker-compose --file docker-compose-scale.yml up -d --build --scale message-server=1 product-server=1
```

此命令将启动单个message-server和单个product-server。

要扩展我们的服务，我们可以运行以下命令：

```shell
$> docker-compose --file docker-compose-scale.yml up -d --build --scale message-server=3 product-server=2
```

此命令将启动两个额外的message-server和一个额外的product-server。正在运行的容器不会停止。

## 4. 自定义基础镜像

到目前为止，我们使用的基础镜像(openjdk:8-jdk-alpine)包含已安装JDK 8的Alpine操作系统发行版。或者，我们可以构建自己的基础镜像(基于Alpine或任何其他操作系统)。

为此，我们可以使用带有Alpine的Dockerfile作为基础镜像，并安装我们选择的JDK：

```dockerfile
FROM alpine:edge
MAINTAINER taketoday.com
RUN apk add --no-cache openjdk8
```

-   **FROM**：关键字FROM告诉Docker使用给定的镜像及其标签作为构建基础。如果此镜像不在本地库中，则会在[DockerHub](https://hub.docker.com/explore/)或任何其他已配置的远程注册表上执行在线搜索。
-   **MAINTAINER：MAINTAINER**通常是一个电子邮件地址，用于标识镜像的作者。
-   **RUN**：使用RUN命令，我们在目标系统中执行shell命令行。在这里，我们使用Alpine Linux的包管理器apk来安装Java 8 OpenJDK。

要最终构建镜像并将其存储在本地库中，我们必须运行：

```shell
docker build --tag=alpine-java:base --rm=true .
```

> **注意**：-tag选项将为镜像命名，-rm=true将在成功构建后删除中间镜像。此shell命令中的最后一个字符是一个点，用作构建目录参数。

现在我们可以使用创建的镜像而不是openjdk:8-jdk-alpine。

## 5. Spring Boot 2.3中的Buildpacks支持

**Spring Boot 2.3添加了对[buildpacks](https://buildpacks.io/)的支持**。简而言之，不是创建我们自己的Dockerfile并使用类似docker build的东西构建它，我们所要做的就是发出以下命令：

```shell
$ mvn spring-boot:build-image
```

同样，在Gradle中：

```shell
$ ./gradlew bootBuildImage
```

**为此，我们需要安装并运行Docker**。

buildpacks背后的主要动机是创造与一些著名的云服务(例如Heroku或Cloud Foundry)已经提供了一段时间相同的部署体验。我们只是运行build-image目标(goal)，然后平台本身负责构建和部署工件。

此外，它还可以帮助我们[更有效地改变构建Docker镜像](https://www.baeldung.com/spring-boot-docker-images)的方式。无需对不同项目中的大量Dockerfile应用相同的更改，我们所要做的就是更改或调整buildpacks镜像构建器。

**除了易用性和更好的整体开发人员体验外，它还可以提高效率**。 例如，buildpacks方法将创建一个分层的Docker镜像并使用Jar文件的分解版本。

让我们看看运行上述命令后会发生什么。

当我们列出可用的Docker镜像时：

```shell
docker image ls -a
```

我们看到我们刚刚创建的镜像有一行：

```shell
docker-message-server 1.0.0 b535b0cc0079
```

在这里，镜像名称和版本与我们在Maven或Gradle配置文件中定义的名称和版本相匹配。哈希码是镜像哈希的简短版本。

然后要启动我们的容器，我们可以简单地运行：

```shell
docker run -it -p9099:8888 docker-message-server:1.0.0
```

与我们构建的镜像一样，我们需要映射端口以使我们的Spring Boot应用程序可以从Docker外部访问。

## 6. 总结

在本文中，我们学习了如何构建自定义Docker镜像、将Spring Boot应用程序作为Docker容器运行，以及使用docker-compose创建容器。

如需进一步了解构建文件，请参阅官方[Dockerfile参考](https://docs.docker.com/engine/reference/builder/)和[docker-compose.yml参考](https://docs.docker.com/compose/compose-file/)。
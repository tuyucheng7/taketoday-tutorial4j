---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

当我们对我们的应用程序进行 docker 化时，我们通常需要暴露一个端口。应用程序使用该端口与其他容器或外部世界进行交互。有时，一个端口是不够的。可能需要一个或多个附加端口来服务于其他目的。例如，在Spring Boot应用程序中，我们需要一个单独的端口来发布管理端点，以使用执行器监控应用程序。

在本文中，我们将了解如何声明多个端口以公开以及如何将公开的端口与主机端口绑定以实现上述目的。

## 2.声明端口

首先，我们需要声明要暴露的端口。我们可以在构建 docker 镜像时做到这一点。也可以在运行基于镜像的容器时声明端口。让我们看看我们是如何做到的。

我们将从示例Spring Boot应用程序的示例开始 - my-app。在整篇文章中，我们将使用相同的示例来理解这些概念。我们的应用程序只有一个GET端点，它返回“ Hello buddy ”。它还启用了Spring执行器。应用程序在端口8080上运行，管理端点在端口8081上运行。因此，当应用程序在本地计算机上运行时，这些命令起作用：

```shell
$ curl http://localhost:8080
Hello buddy

$ curl http://localhost:8081/actuator/health
{"status":"UP"}
```

### 2.1.Dockerfile中的声明

由于我们的应用程序my-app在两个端口8080和8081中发布其端点，因此我们需要在Dockerfile中公开这两个端口。Dockerfile中的[EXPOSE](https://docs.docker.com/engine/reference/builder/#expose)动词公开端口：

```plaintext
FROM openjdk:8-jdk-alpine
EXPOSE 8080
EXPOSE 8081
ARG JAR_FILE=target/my-app-0.1.jar
ADD ${JAR_FILE} my-app.jar
ENTRYPOINT ["java","-jar","/my-app.jar"]
```

然而，当我们使用这个Dockerfile构建镜像时：

```shell
$ docker build -t my-app:latest .
```

这实际上并没有打开端口，因为Dockerfile的作者无法控制容器将运行的网络。相反，EXPOSE命令充当文档。有了这个，运行容器的人就知道容器的哪些端口需要在主机上发布才能与应用程序通信。

我们还可以指定用于在此端口上通信的协议——TCP或UDP ：

```plaintext
EXPOSE 8080/tcp
EXPOSE 8081/udp
```

如果我们不指定任何内容，它将默认使用TCP 。

该命令还支持范围内的端口声明：

```plaintext
EXPOSE 8000-8009
```

上面的命令告诉应用程序需要打开从8000到8009的 10 个端口进行通信。

### 2.2. 在docker run命令中声明

假设我们已经有一个my-app的 docker 镜像，它使用Dockerfile中的EXPOSE命令仅公开一个端口8080。现在，如果我们想公开另一个端口8081，我们应该使用–expose参数和[运行](https://docs.docker.com/engine/reference/run/#expose-incoming-ports)命令：

```shell
$ docker run --name myapp -d --expose=8081 my-app:latest
```

上面的命令从镜像my-app运行一个名为myapp的容器，并公开8081和端口8080。我们可以使用以下方法检查：

```shell
$ docker ps
CONTAINER ID   IMAGE           COMMAND                  CREATED             STATUS              PORTS              NAMES
2debb3c5345b   my-app:latest   "java -jar /my-app.j…"   5 seconds ago       Up 3 seconds        8080-8081/tcp      myapp
```

重要的是要了解此参数仅公开 - 但不发布 - 主机中的端口。为了更清楚地理解它，让我们执行：

```shell
$ docker port myapp
```

这不会打印任何内容，因为主机中没有打开和映射任何端口。因此，即使应用程序在容器内运行，我们也无法访问该应用程序：

```shell
$ curl http://localhost:8080
curl: (7) Failed to connect to localhost port 8080: Connection refused
```

我们也可以选择以相同的方式公开一系列端口：

```shell
$ docker run --name myapp -d --expose=8000-8009 my-app:latest
```

## 3.发布端口

我们已经学会了为 dockerized 应用程序公开端口。现在是时候发布它们了。

### 3.1. 在运行命令中发布

让我们重用上一节中的my-app镜像示例。它在Dockerfile中公开了两个端口—— 8080和8081。在基于此镜像运行容器时，我们可以使用-P参数一次发布所有公开的端口：

```shell
$ docker run --name myapp -d -P myApp:latest
```

上面的命令在宿主机中随机打开两个端口，并映射到Docker容器的8080和8081端口。如果我们公开一系列端口，它的行为方式相同。

要检查映射的端口，我们使用：

```shell
$ docker port myapp
8080/tcp -> 0.0.0.0:32773
8081/tcp -> 0.0.0.0:32772
```

现在，可以使用端口32773 访问该应用程序，并且可以通过32772访问管理端点：

```shell
$ curl http://localhost:32773
Hello buddy
$ curl http://localhost:32772/actuator/health
{"status":"UP"}
```

我们可以使用-p参数在主机中选择特定端口，而不是分配随机端口：

```shell
$ docker run --name myapp -d -p 80:8080 my-app:latest
```

上面的命令只发布8080端口，映射到宿主服务器的80端口。它不会使执行器端点可从容器外部访问：

```shell
$ curl http://localhost:80
Hello buddy
$ curl http://localhost:8081/actuator/health
curl: (7) Failed to connect to localhost port 8081: Connection refused
```

要发布多个端口映射，我们多次使用-p参数：

```shell
$ docker run --name myapp -d -p 80:8080 -p 81:8081 my-app:latest
```

这样，我们还可以控制容器的哪些端口对外开放。

### 3.2. 在docker-compose中发布

如果我们在docker-compose中使用我们的应用程序，我们可以提供需要在docker-compose.yml文件中发布的端口列表：

```plaintext
version: "3.7"
services:
  myapp:
    image: my-app:latest
    ports:
      - 8080
      - 8081
```

如果我们启动此设置，它会使用给定端口分配主机服务器的随机端口：

```shell
$ docker-compose up -d
Starting my-app_myapp_1 ... done
$ docker port  my-app_myapp_1
8080/tcp -> 0.0.0.0:32785
8081/tcp -> 0.0.0.0:32784
```

但是，可以提供特定的端口选择：

```plaintext
version: "3.7"
services:
  myapp:
    image: my-app:latest
    ports:
      - 80:8080
      - 81:8081
```

这里的80和81是宿主机的端口，而8080和8081是容器端口。

## 4. 总结

在本文中，我们讨论了在Docker容器中公开和发布多个端口的不同方式。我们已经了解了暴露的实际含义以及如何完全控制容器和主机之间端口的发布。

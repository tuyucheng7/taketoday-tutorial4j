---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[在本教程中，我们将学习如何使用Docker Compose](https://www.baeldung.com/ops/docker-compose)重启单个Docker容器。

## 2.DockerCompose重启命令

Docker Compose是一种将多个容器作为单一服务进行管理的工具。但是，Docker ComposeCLI包含可应用于单个容器的命令。例如，重启命令让我们提供我们想要重启的服务的名称，而不影响正在运行的其他服务：

```plaintext
docker-compose restart service-name
```

在深入执行重启命令之前，让我们设置一个工作环境。

## 3.设置

我们必须有一个Docker容器来运行Docker Compose命令。我们将使用以前的 Baeldung 项目[spring-cloud-docker](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-docker)，它是一个 dockerized 的Spring Boot应用程序。这个项目有两个Docker容器，它们将帮助我们证明我们可以在不影响另一个服务的情况下重启一个服务。

首先，我们必须通过从项目的根目录运行以下命令来确认我们可以运行两个容器：

```plaintext
docker-compose up --detach --build
```

现在，我们应该能够通过执行docker-compose ps看到这两个服务正在运行：

```plaintext
$ docker ps
     Name                   Command              State            Ports         
--------------------------------------------------------------------------------
message-server   java -jar /message-server.jar   Up      0.0.0.0:18888->8888/tcp
product-server   java -jar /product-server.jar   Up      0.0.0.0:19999->9999/tcp

```

此外，我们可以在浏览器中转到localhost:18888或localhost:19999并验证我们是否看到应用程序服务显示的消息。

## 4.重启单个容器

到目前为止，我们有两个容器作为单个服务运行并由Docker Compose管理。现在，让我们看看如何使用重启命令来停止和启动两个容器之一。

首先，我们看看如何在不重建容器的情况下实现这一点。但是，此解决方案不会使用最新代码更新服务。然后，我们将看到另一种方法，即在运行之前使用最新代码构建容器。

### 4.1. 重启而不重建

在两个容器都运行的情况下，我们选择其中一个服务来重启。在这种情况下，我们将使用消息服务器容器：

```plaintext
docker-compose restart message-server
```

在终端中运行命令后，我们应该能够看到以下消息：

```plaintext
Restarting message-server ... done
```

一旦终端提示输入另一个命令，我们可以通过运行Docker命令ps检查所有正在运行的进程的状态来确认message-server已成功重启：

```plaintext
$ docker ps
CONTAINER ID   IMAGE                   COMMAND                  CREATED          STATUS          PORTS                     NAMES
b6541d1c4ddf   product-server:latest   "java -jar /product-…"   10 minutes ago   Up 42 seconds   0.0.0.0:19999->9999/tcp   product-server
1d07d2a7ed7d   message-server:latest   "java -jar /message-…"   10 minutes ago   Up 15 seconds   0.0.0.0:18888->8888/tcp   message-server

```

最后，我们可以通过查看STATUS列来确定该命令已成功重启消息服务器容器。我们可以看到message-server服务的启动和运行时间比product-server服务要短，后者自从我们在上一节中运行docker-compose up命令以来一直在运行。

### 4.2. 重建和重启

如果需要使用最新代码更新容器，运行重启命令是不够的，因为服务需要先构建以获取代码更改。

在两个容器都运行的情况下，让我们先更改代码以确认我们将在重新启动之前使用最新代码更新服务。在DockerProductController类中，让我们将返回语句更改为如下所示：

```java
public String getMessage() {
    return "This is a brand new product";
}
```

现在，让我们构建 Maven 包：

```plaintext
mvn clean package
```

现在，我们准备重启产品服务器容器。我们可以像启动服务一样实现这一点，但这次是通过提供我们要重启的容器的名称。

让我们运行命令来重建并重新启动容器：

```plaintext
docker-compose up --detach --build product-server
```

现在，我们可以通过运行docker ps并查看输出来验证产品服务器容器是否使用最新代码重新启动：

```plaintext
$ docker ps
CONTAINER ID   IMAGE                   COMMAND                  CREATED          STATUS          PORTS                     NAMES
78a4364e75e6   product-server:latest   "java -jar /product-…"   6 seconds ago    Up 5 seconds    0.0.0.0:19999->9999/tcp   product-server
b559f742973b   message-server:latest   "java -jar /message-…"   22 minutes ago   Up 22 minutes   0.0.0.0:18888->8888/tcp   message-server

```

可以看到，product-server 的CREATED值和 STATUS值都发生了变化，说明服务是先重建后重启的，对message-server没有任何影响。

此外，我们可以通过在浏览器中转到localhost:19999并检查输出是否为最新来进一步确认代码已更新。

## 5.总结

在本文中，我们学习了如何使用Docker Compose重启单个容器。我们介绍了实现这一目标的两种方法。

首先，我们使用带有服务名称的重启命令来重启。然后，我们进行了代码更改以证明我们可以重建最新代码并重新启动单个容器而不影响其他容器。

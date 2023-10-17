---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在本教程中，我们将学习如何使用交互式 shell运行多个[Docker容器。](https://www.baeldung.com/ops/docker-guide)[首先，我们将使用简单的docker run](https://www.baeldung.com/ops/running-docker-containers-indefinitely)命令运行Docker容器。[稍后，我们将使用docker-compose](https://www.baeldung.com/ops/docker-compose)命令运行同一个Docker容器。

## 2. Docker和DockerCompose

Docker容器允许开发人员打包可在不同环境中无缝工作的应用程序。事实上，在生产环境中部署 Web 应用程序可能需要多种服务：

-   数据库服务器
-   负载均衡
-   网络服务器

在这种情况下，Docker Compose是一个非常方便的工具。

Docker Compose主要用于将多个容器作为单个服务运行，同时保持容器之间的平滑连接。

## 3. 了解Docker Compose

要使用docker-compose命令运行Docker容器 ，我们需要将所有配置添加到单个docker-compose.yml配置文件中。重要的是，与普通的docker run命令相比，使用docker-compose的主要好处之一是将配置整合到一个文件中，机器和人类都可以读取该文件。

让我们创建一个简单的docker-compose.yml来展示如何使用[docker-compose up](https://docs.docker.com/engine/reference/commandline/compose_up/)命令运行Docker容器：

```shell
version: "3"
services:
 server:
   image: tomcat:jre11-openjdk
   ports:
     - 8080:8080
```

这里，我们使用[tomcat](https://www.baeldung.com/tomcat)作为基础镜像，并在宿主机上暴露了8080端口。要查看实际效果，让我们使用docker-compose up命令构建并运行此镜像：

```shell
$ docker-compose up
Pulling server (tomcat:jre11-openjdk)...
jre11-openjdk: Pulling from library/tomcat
001c52e26ad5: Pull complete
...
704b1ae41f0e: Pull complete
Digest: sha256:85bfe38b723bc864ed594973a63c04b112e20d6d33eee57cd5303610d8e3dc77
Status: Downloaded newer image for tomcat:jre11-openjdk
Creating dockercontainers_server_1 ... done
Attaching to dockercontainers_server_1
server_1  | NOTE: Picked up JDK_JAVA_OPTIONS:  --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED
server_1  | 03-Aug-2022 06:22:17.259 INFO [main] org.apache.catalina.startup.VersionLoggerListener.log Server version name:   Apache Tomcat/10.0.23
```

关键是，我们应该从包含docker-compose.yml文件的目录运行上述命令。

在上面的输出中，我们可以看到 dockercontainers_server_1已启动并正在运行。但是，这种方法的一个问题是，一旦我们退出上述 shell，容器也会停止。

要长期运行Docker容器，我们需要使用交互式 shell 来运行它。

## 4.Docker中的交互式Shell

Docker 中的交互模式允许我们在容器处于运行状态时执行命令。要以交互模式运行Docker容器，我们使用-it选项。此外，我们使用-it标志将[STDIN 和 STDOUT](https://www.baeldung.com/linux/stream-redirections)通道附加到我们的终端。

Docker Compose使用具有多种优势的单主机部署：

-   快速且易于配置
-   实现快速部署
-   减少完成多项任务所需的时间
-   所有容器独立运行，降低了违规风险

现在让我们使用带有交互式 shell 的docker-compose来运行之前的tomcat容器：

```shell
version: "3"
services:
 server:
   image: tomcat:jre11-openjdk
   ports:
     - 8080:8080
   stdin_open: true 
   tty: true
```

在这种情况下，我们在docker-compose.yml文件中添加了stdin_open和tty选项，这样我们就可以拥有一个带有docker-compose setup 的交互式 shell 。

当然，要访问Docker容器，我们首先需要使用以下命令运行容器：

```shell
$ docker-compose up --d
```

现在，我们可以获得正在运行的docker-compose服务的交互式 shell：

```shell
$ docker-compose exec server bash
```

请注意我们如何使用服务名称而不是容器名称。

最后，我们通过上面的命令成功登录到容器中。

## 5.总结

在本文中，我们演示了如何使用docker-compose命令获取交互式 shell。首先，我们学习了如何使用docker-compose运行Docker容器。之后，我们使用docker exec命令和docker-compose YAML 配置对交互式 shell 进行了探索。

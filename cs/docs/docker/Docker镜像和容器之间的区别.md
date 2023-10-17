---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

Docker 是一种用于轻松创建、部署和运行应用程序的工具。它允许我们将我们的应用程序与所有依赖项打包在一起，并将它们作为单独的包分发。Docker 保证我们的应用程序将在每个Docker实例上以相同的方式运行。

当我们开始使用Docker时，有两个主要概念我们需要弄清楚——镜像和容器。

在本教程中，我们将了解它们是什么以及它们有何不同。

## 2. 码头镜像

镜像是一个文件，代表一个打包的应用程序，其中包含正确运行所需的所有依赖项。换句话说，我们可以说Docker镜像就像一个Java类。

镜像构建为一系列层。一层一层地组装起来。那么，什么是图层？简单地说，图层就是镜像。

假设我们要创建Hello World Java应用程序的Docker镜像。我们需要考虑的第一件事是我们的应用程序需要什么。

首先，它是一个Java应用程序，因此我们需要一个JVM。好吧，这看起来很简单，但是JVM需要运行什么？它需要一个操作系统。因此，我们的Docker镜像将有一个操作系统层、一个JVM和我们的Hello World应用程序。

Docker 的一个主要优势是其庞大的社区。如果我们想构建一个镜像，我们可以去[Docker Hub](https://hub.docker.com/)搜索我们需要的镜像是否可用。

假设我们要使用[PostgreSQL](https://hub.docker.com/_/postgres)数据库创建一个数据库。我们不需要从头开始创建一个新的PostgreSQL镜像。我们只需进入DockerHub，搜索postgres，这是 PostgresSQL 的Docker官方镜像名称，选择我们需要的版本，然后运行它。

我们创建或从Docker Hub提取的每个镜像都存储在我们的文件系统中，并由其名称和标签标识。它也可以通过它的镜像id来识别。

使用docker images命令，我们可以查看文件系统中可用的镜像列表：

```shell
$ docker images
REPOSITORY           TAG                 IMAGE ID            CREATED             SIZE
postgres             11.6                d3d96b1e5d48        4 weeks ago         332MB
mongo                latest              9979235fc504        6 weeks ago         364MB
rabbitmq             3-management        44c4867e4a8b        8 weeks ago         180MB
mysql                8.0.18              d435eee2caa5        2 months ago        456MB
jboss/wildfly        18.0.1.Final        bfc71fe5d7d1        2 months ago        757MB
flyway/flyway        6.0.8               0c11020ffd69        3 months ago        247MB
java                 8-jre               e44d62cf8862        3 years ago         311MB
```

## 3. 运行Docker镜像

使用带有镜像名称和标签的docker run命令运行镜像。假设我们要运行postgres 11.6镜像：

```shell
docker run -d postgres:11.6
```

请注意，我们提供了-d选项。这告诉Docker在后台运行镜像——也称为分离模式。

使用docker ps命令我们可以检查我们的镜像是否正在运行我们应该使用这个命令：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS               NAMES
3376143f0991        postgres:11.6       "docker-entrypoint.s…"   3 minutes ago       Up 3 minutes        5432/tcp            tender_heyrovsky
```

请注意上面输出中的CONTAINER ID 。让我们看一下容器是什么以及它与镜像的关系。

## 4. 码头集装箱

容器是镜像的实例。每个容器都可以通过其 ID 来识别。回到我们的Java开发类比，我们可以说容器就像一个类的实例。

Docker 为容器定义了七种状态：created、restarting、running、removed、paused、exited和dead。了解这一点很重要。由于容器只是镜像的一个实例，因此它不需要运行。

现在让我们再考虑一下上面看到的运行命令。我们说过它是用来运行镜像的，但这并不完全准确。事实上，运行命令用于创建 和启动 一个新的镜像容器。

一大优势是容器就像轻量级虚拟机。他们的行为完全相互隔离。这意味着我们可以运行同一镜像的多个容器，每个容器都处于不同的状态，具有不同的数据和不同的 ID。

能够同时运行同一镜像的多个容器是一个很大的优势，因为它让我们可以轻松地扩展应用程序。例如，让我们考虑一下微服务。如果每个服务都打包为Docker镜像，那么这意味着新服务可以按需部署为容器。

## 5.容器生命周期

前面我们提到了容器的七种状态，下面我们来看看如何使用docker命令行工具来处理不同的生命周期状态。

启动一个新的容器需要我们创建它然后启动它。这意味着它必须经过创建状态才能运行。我们可以通过显式创建和启动容器来做到这一点：

```shell
docker container create <image_name>:<tag>
docker container start <container_id>
```

或者我们可以使用运行命令轻松地做到这一点：

```shell
docker run <image_name>:<tag>
```

我们可以暂停正在运行的容器，然后再次将其置于运行状态：

```shell
docker pause <container_id>
docker unpause <container_id>
```

当我们检查进程时，暂停的容器将显示“暂停”作为状态：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                  PORTS               NAMES
9bef2edcad7b        postgres:11.6       "docker-entrypoint.s…"   5 minutes ago       Up 4 minutes (Paused)   5432/tcp            tender_heyrovsky
```

我们还可以停止正在运行的容器，然后重新运行它：

```shell
docker stop <container_id>
docker start <container_id>
```

最后，我们可以删除一个容器：

```shell
docker container rm <container_id>
```

只能删除处于停止或创建状态的容器。

关于Docker命令的更多信息，我们可以参考[Docker Command Line Reference](https://docs.docker.com/engine/reference/commandline/cli/)。

## 六，总结

在本文中，我们讨论了Docker镜像和容器以及它们之间的区别。镜像描述了应用程序及其运行方式。容器是镜像实例，其中可以运行同一镜像的多个容器，每个容器处于不同的状态。

我们还讨论了容器的生命周期并学习了管理它们的基本命令。

现在我们了解了基础知识，是时候了解更多有关Docker令人兴奋的世界并开始增长我们的知识了！

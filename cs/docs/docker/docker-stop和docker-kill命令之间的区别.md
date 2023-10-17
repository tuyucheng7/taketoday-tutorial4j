---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.baeldung.com/ops/docker-guide)是一个操作系统级的软件框架，用于在服务器和云中创建、管理和运行容器。Docker 支持使用两种不同的方式停止容器。

[在本教程中，我们将学习使用docker stop](https://docs.docker.com/engine/reference/commandline/stop/)和[docker kill](https://docs.docker.com/engine/reference/commandline/kill/)命令停止和终止容器。

我们将使用不同的 docker 命令来停止和删除容器。

## 2.了解docker stop和docker kill命令

启动和停止容器与启动和停止正常进程不同。为了终止容器，Docker 提供了docker stop 和docker kill命令。docker kill 和docker stop命令看起来很相似，但它们的内部执行是不同的。

docker stop命令发出 SIGTERM 信号，而docker kill命令发送 SIGKILL 信号。[SIGTERM 和 SIGKILL](https://www.baeldung.com/linux/sigint-and-other-termination-signals)的执行。是不同的。与 SIGKILL 不同，SIGTERM 优雅地终止进程而不是立即终止它。可以处理、忽略或阻止 SIGTERM 信号，但不能阻止或处理 SIGKILL 信号。SIGTERM 允许子进程或父进程有机会向其他进程发送信息。

使用 SIGKILL，我们可能会创建僵尸进程，因为被杀死的子进程不会通知其父进程它收到了终止信号。容器需要一些时间才能完全关闭。

## 3.执行docker stop和docker kill命令

在继续了解docker stop和docker kill命令之前，让我们先运行一个示例 PostgresDocker容器：

```shell
$ docker run -itd -e POSTGRES_USER=baeldung -e POSTGRES_PASSWORD=baeldung
  -p 5432:5432 -v /data:/var/lib/postgresql/data --name postgresql-baedlung postgres
Unable to find image 'postgres:latest' locally
latest: Pulling from library/postgres
214ca5fb9032: Pull complete 
...
95df4ec75c64: Pull complete 
Digest: sha256:2c954f8c5d03da58f8b82645b783b56c1135df17e650b186b296fa1bb71f9cfd
Status: Downloaded newer image for postgres:latest
0aece936b317984b5c741128ac88a891ffc298d48603cf23514b7baf9eeb981a
```

让我们检查一下容器的详细信息：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
be2848539d76        postgres            "docker-entrypoint.s…"   4 seconds ago       Up 2 seconds        0.0.0.0:5432->5432/tcp   postgresql-baedlung
```

docker ps命令列出主机上所有正在运行的进程。

### 3.1. Docker停止命令

docker stop命令优雅地停止容器并提供安全的退出方式。如果docker stop命令未能在指定的超时时间内终止进程，Docker 会立即隐式发出 kill 命令。在Docker中，有两种方法可以使用docker stop命令来停止进程。我们可以使用containerId或容器名称来停止容器。

让我们演示如何使用容器名称停止容器：

```shell
$ docker stop postgresql-baeldung
```

让我们举例说明如何使用containerId停止容器：

```shell
$ docker stop be2848539d76
```

有趣的是，我们还可以使用 containerId 前缀来启动或停止容器。在这里，我们只需要确保没有其他容器以“be”作为起始containerId运行：

```shell
$ docker stop be
```

默认情况下，docker stop命令等待 10 秒以终止进程。但是我们可以使用-t选项配置等待时间：

```shell
$ docker stop -t 60 be2848539d76
```

在这里，容器会等待 60 秒，然后强行移除容器。我们还可以使用docker container stop命令停止容器：

```shell
$ docker container stop -t 60 be2848539d76
```

这两个命令的工作方式完全相同。Docker容器停止命令在较新版本的Docker中已弃用。

### 3.2. Docker杀死命令

docker kill命令突然终止入口点进程。docker kill命令导致不安全退出。在某些情况下，docker 容器与主机挂载的卷一起运行。如果主进程停止时内存中仍有挂起的更改，这可能会导致文件系统损坏。

让我们看看杀死容器的命令：

```shell
$ docker kill be2848539d76
```

同样的，要杀死一个容器，我们也可以使用docker container kill命令：

```shell
$ docker container kill be2848539d76
```

docker container kill命令的工作方式与docker kill命令类似。

## 4. 停止容器的附加命令

docker kill和docker stop命令都会停止容器。停止容器的另一种方法是将其删除。我们可以使用docker rm命令删除容器。这将立即从本地存储中删除容器：

```shell
$ docker rm be2848539d76
```

当我们运行docker rm命令时，容器将从docker ps -a列表中删除。在使用docker stop命令时，我们可以保留容器以供重复使用。理想情况下，我们可以将容器置于两种状态。容器可以进入停止或暂停状态。如果一个容器被停止，它分配的所有资源都会被释放，而一个容器被暂停不会释放内存，但会释放CPU。在这种情况下，该过程将暂停。

让我们看看暂停容器的命令：

```shell
$ docker pause be2848539d76
```

值得注意的是，即使在容器停止后，我们也可以检查容器的详细信息。要了解有关容器的更多信息，我们可以使用docker inspect命令。docker inspect命令显示容器状态下容器的退出代码。使用docker stop命令停止容器时，退出代码为 0 。同样，docker kill命令将容器状态显示为非零退出代码。

## 5.总结

在本教程中，我们讨论了执行docker stop和docker kill命令之间的区别。首先，我们讨论了使用不同的命令停止容器。接下来，我们讨论了 docker stop 和 docker kill 命令中 SIGTERM 和 SIGKILL 的实现。

我们还探讨了这两个命令的不同选项。后来，我们探索了docker container stop和docker container kill命令。

简而言之，我们学习了各种停止和杀死Docker容器的方法。

---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

我们知道Docker是一个强大的工具，可以轻松创建、部署和运行应用程序。

在[镜像与容器教程](https://www.baeldung.com/docker-images-vs-containers)中，我们讨论了如何使用层构建Docker镜像。我们还讨论了第一层通常是操作系统。

那么，是否可以连接到容器的操作系统呢？是的。现在我们要学习如何去做。

## 2.连接到现有容器

如果我们想连接到现有容器，我们应该让任何容器处于运行状态。为此，我们必须使用docker ps命令检查系统中的容器状态：

```shell
$ docker ps -a
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS                  PORTS               NAMES
4b9d83040f4a        hello-world         "/hello"            8 days ago          Exited (0) 8 days ago                       dazzling_perlman
```

由于我们没有正在运行的容器，让我们启动一个 RabbitMQ 容器作为示例：

```shell
$ docker run -d rabbitmq:3
```

容器启动后，我们将从对docker ps 的另一个调用中看到它：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                                NAMES
b7a9f5eb6b85        rabbitmq:3          "docker-entrypoint.s…"   25 minutes ago      Up 25 minutes       4369/tcp, 5671-5672/tcp, 25672/tcp   trusting_bose
```

现在连接到这个容器就像执行一样简单：

```shell
$ docker exec -it b7a9f5eb6b85 sh
```

此时，我们在容器内有一个交互式 shell：

-   [docker exec](https://docs.docker.com/engine/reference/commandline/exec/)告诉Docker我们要在正在运行的容器中执行命令
-   -it参数意味着它将以交互模式执行——它使 STIN 保持打开状态
-   b7a9f5eb6b85是容器 ID
-   sh是我们要执行的命令

让我们探索一下我们新创建的容器的操作系统：

```shell
$ cat /etc/*-release
DISTRIB_ID=Ubuntu
DISTRIB_RELEASE=18.04
DISTRIB_CODENAME=bionic
DISTRIB_DESCRIPTION="Ubuntu 18.04.4 LTS"
NAME="Ubuntu"
VERSION="18.04.4 LTS (Bionic Beaver)"
ID=ubuntu
ID_LIKE=debian
PRETTY_NAME="Ubuntu 18.04.4 LTS"
VERSION_ID="18.04"
HOME_URL="https://www.ubuntu.com/"
SUPPORT_URL="https://help.ubuntu.com/"
BUG_REPORT_URL="https://bugs.launchpad.net/ubuntu/"
PRIVACY_POLICY_URL="https://www.ubuntu.com/legal/terms-and-policies/privacy-policy"
VERSION_CODENAME=bionic
UBUNTU_CODENAME=bionic
```

它似乎是一个 Bionic Beaver Ubuntu。如果我们检查[RabbitMQDockerfile](https://github.com/docker-library/rabbitmq/blob/1bc288f77525425dfb5b58a0d5dbb3834c7dc53c/3.8/ubuntu/Dockerfile)中的FROM指令，我们会发现这个镜像是使用 Ubuntu 18.04 构建的。

我们可以使用exit命令或CTRL+d断开与容器的连接。

这个例子很简单，因为当我们启动 RabbitMQ 容器时，它会一直运行直到我们停止它。另一方面，有时我们不得不处理不存活的容器，例如操作系统容器。让我们看看如何进入它们。

## 3. 以交互方式运行容器

如果我们尝试启动一个新的操作系统容器，例如 18.04 Ubuntu，我们会发现它没有存活：

```shell
$ docker run ubuntu:18.04
$ docker ps -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                     PORTS                                NAMES
08c26636709f        ubuntu:18.04        "/bin/bash"              10 seconds ago      Exited (0) 7 seconds ago                                        heuristic_dubinsky
b7a9f5eb6b85        rabbitmq:3          "docker-entrypoint.s…"   About an hour ago   Up About an hour           4369/tcp, 5671-5672/tcp, 25672/tcp   trusting_bose
```

当 RabbitMQ 容器仍在运行时，Ubuntu 容器已停止。因此，我们无法使用docker exec命令连接到此容器。

避免这种情况的一种方法是以交互模式运行此容器：

```shell
$ docker run -it ubuntu:18.04
```

所以现在我们在容器内，我们可以检查 shell 类型：

```shell
$ echo $0
/bin/bash
```

实际上，当我们以交互模式启动容器时，使用–rm参数很方便。它会确保在我们退出时删除容器：

```shell
$ docker run -it --rm ubuntu:18.04
```

## 4. 保持容器运行

有时我们会遇到一些奇怪的情况，我们需要启动并连接到一个容器，但是交互模式不起作用。

如果我们遇到这些情况之一，可能是因为出现了问题，应该予以纠正。

但是，如果我们需要一个快速的解决方法，我们可以在容器中运行tail命令：

```shell
$ docker run -d ubuntu:18.04 tail -f /dev/null
```

使用此命令，我们将以分离/后台模式 ( -d ) 启动一个新容器，并在容器内执行tail -f /dev/null命令。结果，这将迫使我们的容器永远运行。

现在我们只需要以我们之前看到的相同方式使用docker exec命令进行连接：

```shell
$ docker exec -it CONTAINER_ID sh
```

请记住，这是一种变通方法，只能在开发环境中使用。

## 5.总结

在本教程中，我们了解了如何连接到正在运行的容器的外壳，以及如何以交互方式启动容器。

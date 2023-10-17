---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在[Docker](https://www.baeldung.com/ops/docker-guide)中，开发人员可以构建、部署和测试应用程序，方法是将应用程序及其所有依赖项打包到一个容器中。[Docker Compose](https://www.baeldung.com/ops/docker-compose)是使用服务管理多个容器的必备工具。

在本教程中，我们将了解如何在使用Docker Compose管理的Docker容器中执行多个命令。此外，我们将探讨在Docker容器中运行多个命令的不同方式。

## 2.运行单个命令

Docker Compose允许我们在Docker容器内执行命令。在容器启动过程中，我们可以通过[命令](https://docs.docker.com/compose/compose-file/#command)指令设置任意命令。

让我们看一下docker-compose.yml，它在容器内运行一个简单的命令：

```shell
version: "3"
services:
 server:
   image: alpine
   command: sh -c "echo "baeldung""
```

在上面的docker-compose.yml文件中，我们在[alpine](https://hub.docker.com/_/alpine)Docker镜像中执行了一个[echo](https://www.baeldung.com/linux/echo-command)命令。

## 3.运行多个命令

我们可以通过在docker-compose.yml文件中创建服务来使用Docker Compose来管理多个应用程序。具体来说，我们将使用&&和| 运营商。让我们来看看两者。

### 3.1. 使用&&运算符

我们将从创建一个简单的docker-compose.yml文件开始，以演示Docker Compose如何运行多个命令：

```shell
version: "3"
services:
 server:
   image: alpine
   command: sh -c "echo "baeldung" && echo "docker" "
```

在这里，我们使用alpine作为Docker容器的基础镜像。请注意我们执行两个命令的最后一行：echo baeldung和echo docker。它们由&&运算符拆分 。

为了演示结果，让我们使用[docker-compose up](https://docs.docker.com/engine/reference/commandline/compose_up/)命令运行这个镜像：

```shell
$ docker-compose up
Creating dockercompose_server_1 ... done
Attaching to dockercompose_server_1
server_1  | baeldung
server_1  | docker
dockercompose_server_1 exited with code 0
```

在这里，两个echo 语句的输出都打印在stdout上。

### 3.2. 使用 | 操作员

我们也可以使用 | 运算符在Docker Compose中运行多个命令。|的语法 运算符与&&运算符有点不同。

为了说明| 操作员工作，让我们更新我们的docker-compose.yml：

```shell
version: "3"
services:
 server:
   image: alpine
   command:
      - /bin/sh
      - -c
      - |
        echo "baeldung"
        echo "docker"
```

在这里，我们在单独的行中添加了命令。除了命令指令外，一切都是一样的。

推荐使用这种方法，因为它使我们的 YAML 文件保持干净，因为所有命令都在单独的行中。

让我们再次使用docker-compose up命令运行Docker容器：

```shell
$ docker-compose up
Creating dockercompose_server_1 ... done
Attaching to dockercompose_server_1
server_1  | baeldung
server_1  | docker
dockercompose_server_1 exited with code 0
```

从上面的输出我们可以看出，两条命令都是一条一条执行的。

## 4. 总结

在本文中，我们演示了如何在Docker Compose中执行多个命令。首先，我们了解了如何使用&&运算符执行多个命令。后来，我们使用了| 运营商达到类似的结果。

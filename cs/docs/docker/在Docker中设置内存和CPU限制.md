---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在很多情况下，我们需要限制 docker 主机上资源的使用。

在本教程中，我们将学习如何为 docker 容器设置内存和CPU限制。

## 2.使用docker run设置资源限制

[我们可以直接使用docker run](https://www.baeldung.com/linux/shell-alpine-docker)命令设置资源限制。这是一个简单的解决方案。但是，该限制仅适用于镜像的一次特定执行。

### 2.1. 记忆

例如，我们将容器可以使用的内存限制为 512 兆字节。

为了限制内存，我们需要使用m 参数：

```shell
$ docker run -m 512m nginx
```

我们还可以设置一个称为预留的软限制。

当 docker 检测到主机内存不足时，它会被激活：

```shell
$ docker run -m 512m --memory-reservation=256m nginx
```

### 2.2. 中央处理器

默认情况下，访问主机的计算能力是无限的。我们可以使用cpus参数设置CPU限制 。 

让我们限制我们的容器最多使用两个CPU：

```shell
$ docker run --cpus=2 nginx
```

我们还可以指定CPU分配的优先级。

默认值为 1024，数字越大优先级越高：

```shell
$ docker run --cpus=2 --cpu-shares=2000 nginx
```

与内存预留类似，当计算能力稀缺且需要在竞争进程之间进行分配时，CPU份额将发挥主要作用。

## 3. 使用 docker-compose 文件设置内存限制

[我们可以使用docker-compose](https://www.baeldung.com/docker-compose)文件实现类似的结果。请记住，格式和可能性会因docker-compose的版本而异。

### 3.1. 带有docker swarm 的版本 3 和更新版本

我们给Nginx服务限制一半的CPU和512兆内存，预留四分之一的CPU和128兆内存。

我们需要 在我们的服务配置中创建部署和资源段：

```yaml
services:
  service:
    image: nginx
    deploy:
        resources:
            limits:
              cpus: 0.50
              memory: 512M
            reservations:
              cpus: 0.25
              memory: 128M
```

要利用docker-compose 文件中的部署 段，我们需要使用[docker stack](https://docs.docker.com/engine/reference/commandline/stack_deploy/)命令。

要将堆栈部署到 swarm，我们运行deploy命令：

```shell
$ docker stack deploy --compose-file docker-compose.yml bael_stack
```

### 3.2. 带有docker-compose 的版本 2

在旧版本的docker-compose中，我们可以将资源限制与服务的主要属性放在同一级别。

它们的命名也略有不同：

```shell
service:
  image: nginx
  mem_limit: 512m
  mem_reservation: 128M
  cpus: 0.5
  ports:
    - "80:80"
```

要创建已配置的容器，我们需要运行docker-compose命令：

```shell
$ docker-compose up
```

## 4.验证资源使用

设置限制后，我们可以使用docker stats命令验证它们：

```shell
$ docker stats
CONTAINER ID        NAME                                             CPU %               MEM USAGE / LIMIT   MEM %               NET I/O             BLOCK I/O           PIDS
8ad2f2c17078        bael_stack_service.1.jz2ks49finy61kiq1r12da73k   0.00%               2.578MiB / 512MiB   0.50%               936B / 0B           0B / 0B             2
```

## 5.总结

在本文中，我们探索了限制 docker 访问主机资源的方法。

我们查看了docker run和docker-compose命令的用法。最后，我们使用docker stats控制资源消耗。

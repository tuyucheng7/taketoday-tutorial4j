---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.baeldung.com/ops/docker-guide)有助于将应用程序及其所有依赖项打包到一个称为容器的轻量级实体中。我们可以在任何物理机、虚拟机甚至云端部署Docker容器。

我们在不同环境下使用Docker服务可能会遇到各种各样的问题。

在本教程中，我们将了解Docker守护程序连接问题。这是初学者遇到的一个非常常见的错误。我们还将研究导致此问题的原因以及解决问题的方法。

## 2. 理解问题

考虑这样一种情况，我们尝试在 [Linux](https://www.baeldung.com/linux/)上运行机器上不存在的命令。我们得到一条命令未找到的 错误消息作为回报。

出现此问题的原因可能是该命令实际上并未安装在计算机上，或者已安装但未正确配置。

让我们首先了解Docker守护进程 ( dockerd )。它是一个管理所有Docker对象的程序，包括镜像、容器、卷等等。

另一个实体，Docker 客户端，帮助通过Docker守护进程将命令从用户传递到Docker服务。

在某些情况下，Docker 客户端无法连接到Docker守护进程。 在这种情况下，Docker 会抛出错误Cannot connect toDockerdaemon 。

Docker 客户端无法连接到Docker守护进程的原因可能有多种。现在让我们深入探讨根本原因和解决此问题的不同解决方案。

## 3. 由于不活跃的Docker服务

此错误最常见的原因是当我们尝试访问Docker服务时，但它没有启动：

```shell
$ docker ps
Cannot connect to the Docker daemon at unix:///var/run/docker.sock.
  Is the docker daemon running?
```

首先，我们将检查Docker服务的状态以及它是否正在运行：

```shell
$ systemctl status docker
 docker.service - Docker Application Container Engine
   Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
   Active: inactive (dead)
   Docs: https://docs.docker.com
```

这里的输出清楚地表明Docker服务处于非活动状态。

我们现在将启动Docker服务。在大多数情况下，这将解决问题。

### 3.1. 使用服务启动 Docker

[通常，当我们使用包管理器](https://www.baeldung.com/linux/yum-and-apt)安装Docker时，它会创建一个Docker服务。这使得管理Docker变得容易。

现在让我们使用systemctl服务命令启动Docker：

```shell
$ systemctl start docker
```

我们可以使用以下命令检查Docker的状态：

```shell
$ systemctl status docker
 docker.service - Docker Application Container Engine
   Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
   Active: active (running) since Thu 2022-02-17 19:14:51 UTC; 1min 38s ago
     Docs: https://docs.docker.com
 Main PID: 1831 (dockerd)
    Tasks: 8
   Memory: 126.5M
   CGroup: /system.slice/docker.service
           └─1831 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
```

此命令将Docker服务的当前状态显示为活动(正在运行)。

### 3.2. 手动启动Docker守护进程

或者，我们也可以在没有服务的情况下启动 Docker。

我们需要做的就是在后台运行dockerd命令：

```shell
$ sudo dockerd
INFO[2022-02-18T05:19:50.048886666Z] Starting up                                  
INFO[2022-02-18T05:19:50.050883459Z] libcontainerd: started new containerd process  pid=2331
INFO[2022-02-18T05:19:50.050943756Z] parsed scheme: "unix"                         module=grpc
```

我们需要确保我们以 sudo 权限运行dockerd。

## 4. 由于权限不足

当我们使用包管理器安装Docker时，它会默认创建一个docker用户和组。为了访问 Docker，我们需要将当前用户添加到docker组。

如果我们尝试从docker组之外的用户访问Docker服务，我们会收到此错误：

```shell
$ docker ps
Got permission denied while trying to connect to the Docker daemon socket
  at unix:///var/run/docker.sock: Get http://%2Fvar%2Frun%2Fdocker.sock/v1.40/containers/json:
  dial unix /var/run/docker.sock: connect: permission denied
```

为了解决这个问题，我们可以做两件事。我们可以将用户添加到docker组，也可以更新Docker套接字文件的权限。

现在让我们深入探讨这两种解决方案以及示例。

### 4.1. 更新用户权限

用户权限是Linux中的一个重要概念。他们决定不同用户对资源的可访问性。

在Linux上安装Docker时，会创建一个新的docker组，所有与Docker服务相关的包都链接到这个docker组。

如果我们在我们的机器上没有找到默认的docker组，我们可以手动创建它：

```shell
$ sudo groupadd docker
```

上面的命令将创建一个 docker组。

现在我们将在docker组中添加当前用户：

```shell
$ sudo usermod -aG docker docker-test
```

这里的-a选项会将用户docker-test附加到docker 组。-G选项用于提及组名。

最后，我们将重新启动Docker服务以使更改生效：

```shell
$ sudo service docker restart
```

### 4.2. 更新Docker套接字文件权限

 我们还可以通过更改/var/run/docker.sock 文件的所有者来解决问题：

```shell
$ sudo chown docker-test /var/run/docker.sock
```

请注意，我们使用 sudo 权限运行此命令。否则，文件的权限不会更新。

## 5.总结

在本文中，我们了解了经常遇到的Docker守护程序连接问题。

当Docker服务未正确启动或我们没有适当的用户权限访问Docker服务时，就会出现此问题。

我们研究了通过将用户添加到docker组并更改sock文件的权限来解决此问题的各种方法。

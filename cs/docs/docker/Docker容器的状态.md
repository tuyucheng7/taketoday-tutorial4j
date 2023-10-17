---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.baeldung.com/ops/docker-guide)容器是在其中运行某个进程的Docker镜像的实例。随着这个过程状态的改变，容器的行为也会受到影响。因此，容器在其整个生命周期中可以处于不同的状态。

在本教程中，我们将了解Docker容器的所有可能状态。

让我们首先看看如何找到Docker容器的状态，然后我们将经历容器的不同阶段。

## 2. 查找Docker容器的当前状态

在我们深入研究Docker容器的不同状态之前，让我们先看看如何找到任何Docker容器的状态。

默认情况下， docker ps命令显示所有Docker容器的当前状态：

```shell
$ docker ps -a
CONTAINER ID   IMAGE      COMMAND       CREATED              STATUS                          PORTS     NAMES
8f0b524f2d32   centos:7   "/bin/bash"   46 seconds ago       Created                                   strange_beaver
e6d798254d45   centos:7   "/bin/bash"   About a minute ago   Exited (0) About a minute ago             wizardly_cohen
```

输出显示机器上存在的所有容器及其状态(第五列)和一系列其他详细信息。

我们还可以使用 docker inspect命令获取单个容器的状态：

```shell
$ docker inspect -f '{{.State.Status}}' mycontainer
running
```

在这里，mycontainer是我们希望找到其当前状态的容器名称。我们也可以将其替换为Docker容器 ID。

## 3.Docker容器的可能状态

在任何特定实例中，可以在 6 种可能状态中找到Docker容器。现在让我们深入了解这些状态中的每一个：

### 3.1. 已创建

Docker 将创建状态分配给自创建以来从未启动过的容器。因此，处于此状态的容器不使用CPU或内存。

使用docker create命令创建的Docker容器显示状态为已创建：

```shell
$ docker create --name mycontainer httpd
dd109e4be16219f1a6b9fc1cbfb050c1ae035d6a2c301ea0e93eb7d5252b8d2e
$ docker inspect -f '{{.State.Status}}' mycontainer
created
```

在这里，我们使用[httpd](https://hub.docker.com/_/httpd)[的官方Docker镜像](https://hub.docker.com/_/httpd)创建了一个Docker容器mycontainer。由于我们使用了docker create命令来启动容器，所以状态显示为已创建。 

当我们需要为一些大任务做好准备时，这样的容器很有用。在这种情况下，我们会创建容器，以便在我们启动它时准备就绪。

### 3.2. 跑步

当我们使用docker start命令启动一个已经创建 状态的容器时 ，它会进入运行状态。

此状态表示进程正在容器内的隔离环境中运行。

```shell
$ docker create --name mycontainer httpd
8d60cb560afc1397d6732672b2b4af16a08bf6289a5a0b6b5125c5635e8ee749
$ docker inspect -f '{{.State.Status}}' mycontainer
created
$ docker start mycontainer
mycontainer
$ docker inspect -f '{{.State.Status}}' mycontainer
running
```

在上面的示例中，我们首先使用官方的httpdDocker镜像创建了一个Docker容器。然后，创建了容器的状态。当我们启动mycontainer时， httpd服务器进程和所有其他相关进程开始运行。因此，同一容器的状态现在已更改为正在运行。

使用docker run命令运行的容器也达到了同样的状态：

```shell
$ docker run -itd --name mycontainer httpd
685efd4c1c4a658fd8a0d6ca66ee3cf88ab75a127b9b439026e91211d09712c7
$ docker inspect -f '{{.State.Status}}' mycontainer
running你
```

在这种状态下，容器的CPU和内存消耗没有妥协。

### 3.3. 重启

简单的说，这个状态表示容器正在重启中。 

Docker支持四种[重启策略](https://docs.docker.com/config/containers/start-containers-automatically/)，分别是-no 、 on -failure、always、unless-stopped。重启策略决定容器退出时的行为。

默认情况下，重启策略设置为no，即容器退出后不会自动启动。

让我们将重启策略更新为始终 并使用以下示例验证Docker容器状态：

```shell
$ docker run -itd --restart=always --name mycontainer centos:7 sleep 5
f7d0e8becdac1ebf7aae25be2d02409f0f211fcc191aea000041d158f89be6f6
```

上面的命令将运行mycontainer并执行sleep 5命令并退出。但是由于我们已经更新了这个容器的重启策略，所以它会在容器退出后自动重启。

5 秒后，容器的状态将重新启动：

```shell
$ docker inspect -f '{{.State.Status}}' mycontainer
restarting
```

### 3.4. 退出

当容器内的进程终止时，就会达到这种状态。 在此状态下，容器不会消耗任何CPU和内存。

正在运行的容器退出的原因可能有多种。让我们来看看其中的几个：

-   容器内进程完成，退出。
-   容器内的进程在运行时遇到了异常。
-   使用docker stop命令有意停止容器。
-   没有交互式终端被设置为运行 bash 的容器。

```shell
$ docker run -itd --name mycontainer centos:7 sleep 10
596a10ddb635b83ad6bb9daffb12c1e2f230280fe26be18559c53c1dca6c755f

```

在这里，我们启动了一个 centos 容器mycontainer， 并传递了命令sleep 10。这将在休眠 10 秒后退出容器。我们可以在 10 秒后运行以下命令来验证这一点：

```shell
$ docker inspect -f '{{.State.Status}}' mycontainer
exited
```

无法使用docker exec命令访问处于退出状态的容器 。但是，我们可以使用docker start 或docker restart 启动容器，然后访问它。

```shell
$ docker start mycontainer
```

### 3.5. 暂停

暂停是Docker容器的状态，它无限期地暂停所有进程。 

可以使用docker pause命令暂停Docker容器。

```shell
$ docker run -itd --name mycontainer centos:7 sleep 1000
1a44702cea17eec42195b057588cf72825174db311a35374e250d3d1da9d70c5

```

在上面的示例中，我们使用 centosDocker镜像启动了一个Docker容器并运行了命令sleep 1000。这将在 1000 秒睡眠后退出容器。

现在让我们在几秒钟后暂停这个容器，比如 100 秒：

```shell
$ docker pause mycontainer
mycontainer
$ docker inspect -f '{{.State.Status}}' mycontainer
paused
```

暂停的容器消耗与运行容器相同的内存，但CPU被完全释放。让我们使用docker stat命令验证这一点：

```shell
$ docker stats --no-stream
CONTAINER ID   NAME          CPU %     MEM USAGE / LIMIT    MEM %     NET I/O       BLOCK I/O   PIDS
1a44702cea17   mycontainer   0.00%     1.09MiB / 7.281GiB   0.01%     1.37kB / 0B   0B / 0B     1
```

请注意，CPU为 0%，但内存使用率不为零。

我们可以使用docker unpause命令恢复容器：

```shell
$ docker unpause mycontainer
mycontainer
```

在取消暂停容器时，它将从我们暂停它的同一点恢复。在上面的示例中，我们在睡眠 100 秒后暂停了容器。所以当我们取消暂停容器时，它会从 100 开始恢复睡眠。因此，容器将从此时开始睡眠 900 秒后停止。(总睡眠时间设置为 1000)。

现在的问题是什么时候暂停Docker容器？考虑Docker容器正在执行一些CPU密集型任务的情况。同时，我们希望运行另一个高优先级的CPU密集型容器。当然，我们可以同时运行两个容器，但是会因为资源不足而减慢执行速度。

在这种情况下，我们可以暂停一个低优先级的容器一段时间，让另一个容器使用完整的CPU。完成后，我们可以取消暂停第一个容器的执行。

### 3.6. 死的

Docker容器的死状态意味着该容器无法正常运行。当我们尝试删除容器时会达到此状态，但无法删除，因为某些资源仍在被外部进程使用。因此，容器被移动到死状态。

处于死状态的容器无法重新启动。它们只能被移除。

由于处于死状态的容器被部分删除，因此它不会消耗任何内存或CPU。 

## 4. 总结

在本教程中，我们经历了Docker容器的不同阶段。

首先，我们研究了几种查找Docker容器状态的方法。后来，我们了解了每个状态的重要性以及如何使用不同的Docker命令实现这些状态。

---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在上一篇文章中，我们学习了[如何删除Docker镜像](https://www.baeldung.com/ops/docker-removing-images)。但是， 只有在没有Docker容器使用该镜像时，才能删除Docker镜像。因此，要删除Docker镜像，有必要删除所有运行该镜像的Docker容器。

在本教程中，我们将学习使用不同的方法删除Docker容器。

## 2. 为什么要删除Docker容器？

当Docker容器完成执行时，它会进入退出状态。这样的容器不消耗任何CPU或内存，但它们仍然使用机器的磁盘空间。此外，停止的容器不会自动删除，除非我们在运行Docker容器时使用–rm标志。

因此，随着越来越多的容器进入退出状态，它们消耗的整体磁盘空间增加。结果，我们可能无法启动新容器，或者Docker守护进程将停止响应。

为避免此类情况，建议使用 –rm标志运行Docker容器或定期手动删除Docker容器。

现在让我们学习如何删除Docker容器。

## 3.删除单个Docker容器

首先，我们将以非交互模式启动一个 CentOSDocker容器。通过这样做，容器将在我们运行容器后立即停止：

```shell
$ docker run --name mycontainer centos:7
$ docker ps -a
CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS                     PORTS              NAMES
418c28b4b04e   centos:7      "/bin/bash"              6 seconds ago   Exited (0) 5 seconds ago                       mycontainer
```

现在让我们使用docker rm命令删除Docker容器mycontainer：

```shell
$ docker rm mycontainer
mycontainer
$ docker ps -a
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES

```

我们还可以使用Docker容器 ID 而不是Docker容器名称来使用docker rm命令删除Docker容器：

```shell
$ docker rm 418c28b4b04e
```

## 4.删除多个Docker容器

我们还可以使用docker rm命令删除多个Docker容器。docker rm命令接受以空格分隔的Docker容器名称或 ID 列表，并将它们全部删除：

```shell
$ docker ps -a
CONTAINER ID   IMAGE      COMMAND       CREATED          STATUS                      PORTS     NAMES
23c70ec6e724   centos:7   "/bin/bash"   6 seconds ago    Exited (0) 5 seconds ago              mycontainer3
fd0886458666   centos:7   "/bin/bash"   10 seconds ago   Exited (0) 9 seconds ago              mycontainer2
c223ec695e2d   centos:7   "/bin/bash"   14 seconds ago   Exited (0) 12 seconds ago             mycontainer1
$ docker rm c223ec695e2d mycontainer2 23c70ec6e724
c223ec695e2d
mycontainer2
23c70ec6e724
```

在上面的示例中，我们使用docker rm命令删除了三个处于退出状态的Docker容器。

我们可以将Docker容器名称和 ID 与任何Docker命令互换使用。请注意，我们为mycontainer1和mycontainer3使用了Docker容器 ID ，为 mycontainer2 使用了容器名称。 

## 5.删除所有的Docker容器

考虑这样一种情况，机器上存在太多已停止的Docker容器，现在我们希望将它们全部删除。当然，我们可以使用上面的方法，将所有的容器id传递给docker rm命令。但是让我们研究一个更优化和更简单的命令来删除所有Docker容器：

```shell
$ docker ps -a
CONTAINER ID   IMAGE      COMMAND       CREATED          STATUS                      PORTS     NAMES
b5c45fa5764f   centos:7   "/bin/bash"   4 seconds ago    Exited (0) 3 seconds ago              mycontainer1
ed806b1743cd   centos:7   "/bin/bash"   9 seconds ago    Exited (0) 7 seconds ago              mycontainer2
2e00a052eb12   centos:7   "/bin/bash"   13 seconds ago   Exited (0) 12 seconds ago             mycontainer3
$ docker rm $(docker ps -qa)
b5c45fa5764f
ed806b1743cd
2e00a052eb12
```

命令docker ps -qa 返回机器上所有容器的数字 ID。然后将所有这些id传递给docker rm命令，该命令将迭代地删除Docker容器。

我们还可以使用[docker container prune](https://docs.docker.com/engine/reference/commandline/container_prune/)命令删除所有停止的容器：

```shell
$ docker container prune -f
```

在这里，我们使用 -f标志来避免确认提示。

## 6. 强制删除正在运行的Docker容器

我们在上面的示例中讨论的所有命令仅在Docker容器停止时才有效。如果我们尝试在不先停止它的情况下删除正在运行的容器，我们将收到类似于以下的错误消息：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
f84692b27b0a        centos:7            "/bin/bash"         59 seconds ago      Up 58 seconds                           mycontainer
$ docker rm mycontainer
Error response from daemon:
  You cannot remove a running container f84692b27b0a18266f34b35c90dad655faa10bb0d9c85d73b22079dde506b8b5.
  Stop the container before attempting removal or force remove
```

删除正在运行的Docker容器的一种方法是首先使用docker stop命令停止该容器，然后使用docker rm命令将其删除。

另一种方法是使用-f选项强制删除此类容器 ：

```shell
$ docker rm -f mycontainer
mycontainer
```

我们可以使用-f选项删除单个Docker容器、多个Docker容器或所有Docker容器。

## 七、总结

在本教程中，我们了解了为什么有必要删除Docker容器。首先，我们学习了从Linux机器上删除容器。此外，我们使用docker rm和docker prune命令批量删除了Docker容器。

最后，我们研究了如何强制删除处于运行状态的Docker容器。

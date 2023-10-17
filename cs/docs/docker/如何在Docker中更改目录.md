---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

Docker镜像包含一组顺序指令，用作构建容器的模板。在本教程中，我们将学习如何在构建Docker镜像或使用镜像运行容器时更改目录。

## 2.使用WORKDIR指令

首先，让我们开始使用现成的ubuntu:latest [镜像](https://www.baeldung.com/ops/docker-images-vs-containers#docker-images)生成一个Docker容器：

```shell
$ docker run -it ubuntu:latest
root@89848b34daa6:/# pwd
/
```

我们可以看到，容器一启动，当前目录就设置为/。

接下来，假设我们想在容器启动时将此目录更改为/tmp。我们可以通过在使用ubuntu:latest作为基本镜像的自定义镜像中使用WORKDIR指令来做到这一点：

```shell
$ cat custom-ubuntu-v1.dockerfile
FROM ubuntu:latest
WORKDIR /tmp
```

在我们可以使用这个镜像运行容器之前，我们需要构建镜像。那么，让我们继续构建custom-ubuntu:v1镜像：

```shell
$ docker build -t custom-ubuntu:v1 - < ./custom-ubuntu-v1.dockerfile
```

最后，让我们使用custom-ubuntu:v1镜像[运行一个容器](https://www.baeldung.com/ops/docker-images-vs-containers#running-images)并验证当前目录：

```shell
$ docker run -it custom-ubuntu:v1
root@4c26093b26e6:/tmp# pwd
/tmp
```

看来我们做对了！

## 3.使用-workdir选项

对于大多数我们想要在构建Docker镜像时更改目录的情况，使用[WORKDIR指令是推荐的做法。](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#workdir)然而，如果我们的用例仅限于在运行容器时更改目录，那么我们可以通过使用–workdir选项来实现：

```shell
$ docker run --workdir /tmp -it ubuntu:latest
root@32c5533c248c:/tmp# pwd
/tmp
```

看看这个，我们可以欣赏到命令的简洁性以及在这种情况下我们不必创建自定义镜像这一事实。

## 4. 使用cd命令

在Linux中，[cd命令](https://www.baeldung.com/linux/cd-command-bash-script)是大多数用例更改目录的标准方法。同样，在使用一些 docker 指令(例如RUN、CMD和ENTRYPOINT)时，我们可以使用cd命令在上下文中更改当前命令的目录。

让我们开始编写custom-ubuntu-v2.docker文件以使用 带有cd命令的RUN指令：

```shell
FROM ubuntu:latest
RUN cd /tmp && echo "sample text" > data.txt
```

我们可以看到，其目的是将“示例文本”写入/tmp/data.txt文件。

接下来，让我们添加ENTRYPOINT指令以在容器启动时将bash作为默认命令运行。此外，我们使用cd命令将当前目录更改为/tmp目录：

```shell
ENTRYPOINT ["sh", "-c", "cd /tmp && bash"]
```

继续，让我们构建自定义镜像：

```shell
$ docker build -t custom-ubuntu:v2 - < ./custom-ubuntu-v2.dockerfile
```

最后，让我们使用custom-ubuntu:v2镜像运行容器并验证命令的执行：

```shell
$ docker run -it custom-ubuntu:v2
root@2731e50ea20a:/tmp# pwd
/tmp
root@2731e50ea20a:/tmp# cat /tmp/data.txt
random text
```

我们可以看到两个更改目录命令的结果都符合预期。此外，我们必须记住WORKDIR仍然是推荐的方式。不过，对于 简单的用例，我们可以将cd命令与RUN、[ENTRYPOINT或CMD指令](https://www.baeldung.com/ops/dockerfile-run-cmd-entrypoint)结合使用。

## 5.总结

在本文中，我们了解了在使用Docker镜像或启动容器时更改目录的不同方法。

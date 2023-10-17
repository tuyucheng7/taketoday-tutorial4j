---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在本教程中，我们将探讨使Docker容器无限期运行的不同方法。

默认情况下，容器仅在其默认命令执行时运行，但出于调试和故障排除目的无限期运行它们是很常见的。

## 2.DockerRun 基础

让我们看一下docker run 命令的一些基础知识，以及在容器启动时将命令传递给容器的方法。

### 2.1. 指定要运行的命令

创建Dockerfile时，有两种方法可以指定要运行的命令：

-   ENTRYPOINT[指令指定容器](https://www.baeldung.com/ops/dockerfile-run-cmd-entrypoint#the-entrypoint-command)[的](https://www.baeldung.com/ops/dockerfile-run-cmd-entrypoint#the-entrypoint-command)命令。这对于我们总是希望运行的命令很有帮助，除非用户明确地覆盖它们。
-   [我们还可以使用CMD 指令](https://www.baeldung.com/ops/dockerfile-run-cmd-entrypoint#the-cmd-command)指定命令 。这用于定义要传递给容器的默认命令或参数。用户在镜像名称后包含的任何参数都会替换整个CMD 指令。

### 2.2. 覆盖默认命令

要覆盖CMD指令，我们可以简单地在docker run [image_name] 之后添加另一个命令：

```shell
docker run ubuntu echo "Hello World"
```

要覆盖ENTRYPOINT指令，我们需要在镜像名称之前添加–entrypoint标志和所需的命令，并在镜像名称之后添加任何参数：

```shell
docker run --entrypoint echo ubuntu "Hello World"
```

当容器启动时，这两个示例都将运行命令 echo “Hello World” 。

### 2.3.Docker使用命令运行

docker run命令的默认行为 可以总结如下：

-   容器在前台运行，除非使用-d标志明确分离。
-   只要指定的命令一直运行，容器就会运行，然后停止。

让我们看一个例子：

```shell
docker run ubuntu bash

```

上面的命令将 在ubuntu镜像 中 运行bash命令 。bash命令 将在容器中启动一个 shell。

命令运行，但容器在命令完成后停止，这几乎是立即完成的。我们可以使用docker ps命令对此进行测试：

```shell
docker ps -a
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
c8f8f8f8f8f8        ubuntu              bash                2 minutes ago      Exited (0)          22/tcp               mystifying_snyder

```

## 3.无限期地运行Docker容器

默认情况下，容器可能会或可能不会设计为无限期运行。例如，只要 Web 服务器在运行，包含 Web 服务器的容器就会运行。但是，包含一次性作业(例如 cron 作业)的容器只能运行很短的时间。

让我们看看无限期运行容器的一些方法。

### 3.1. 永无止境的命令

保持容器运行的最简单方法是传递一个永不结束的命令。 

我们可以使用 tail -f 命令来读取/dev/null文件。该命令一直在文件中寻找新的更改以显示，因此只要文件存在，它就永远不会结束：

```shell
docker run ubuntu tail -f /dev/null

```

我们可以使用下面的命令来运行一个什么都不做的无限循环：

```shell
docker run ubuntu while true; do sleep 1; done
```

下面的命令使容器保持空闲并且不执行任何操作：

```shell
docker run ubuntu sleep infinity

```

我们可以通过以下任何一种方式使用永不结束的命令：

- Dockerfile中的ENTRYPOINT或CMD指令
-   在 docker run 命令中覆盖ENTRYPOINT或CMD

在前台运行永无止境的命令并卡住终端是没有意义的。在这种情况下，我们可以使用 -d 标志在后台运行容器。

### 3.2. 启动伪 TTY

很久以前，当没有其他选项存在时，永无止境的命令是一种黑客攻击。然而，在最新版本的Docker中，可以通过在前台和后台启动终端会话来保持容器运行。

伪 TTY 用于在容器内运行命令。要与容器启动伪 TTY 会话，我们可以使用-t标志。在会话结束之前容器不会退出。

如果我们想与容器交互，我们可以将它与 -i标志结合起来。这将允许我们使用我们的终端在容器中运行命令：

```shell
docker run -it ubuntu bash

```

或者，如果我们的目的只是无限期地运行容器，我们可以使用 -d标志：

```shell
docker run -d -t ubuntu

```

## 4. 总结

在本文中，我们学习了一些无限期运行Docker容器的方法。我们讨论了将命令传递给容器的方式，然后修改这些命令来解决我们的问题。我们还谈到了使用伪 TTY 会话来保持容器运行的现代解决方案。

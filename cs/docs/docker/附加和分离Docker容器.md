---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在使用 docker 容器时，我们经常需要以交互模式运行它。这是我们将终端的标准输入、输出或错误流附加到容器的地方。

通常我们更喜欢在后台运行我们的容器。但是，我们可能希望稍后连接到它以检查其输出或错误或断开会话。

在这篇简短的文章中，我们将学习一些有用的命令来实现这些。我们还将看到在不停止容器的情况下从会话中分离的不同方法。

## 2.以附加/分离模式运行容器

让我们看看如何在附加或分离模式下[运行](https://docs.docker.com/engine/reference/commandline/run/)容器。

### 2.1. 默认模式

默认情况下，Docker 在前台运行一个容器：

```applescript
$ docker run --name test_redis -p 6379:6379 redis
```

这意味着在进程完成之前我们无法返回到 shell 提示符。

上面的命令将标准输出 ( stdout ) 和标准错误 ( stderr ) 流与我们的终端链接起来。因此，我们可以在终端中看到容器的控制台输出。

–name选项为容器命名。我们稍后可以在其他命令中使用相同的名称来引用此容器。[或者，我们可以通过执行docker ps](https://docs.docker.com/engine/reference/commandline/ps/)命令得到的容器 ID 来引用它。

我们还可以使用-a选项从 stdin、stdout 和 stderr 中选择特定的流来连接：

```shell
$ docker run --name test_redis -a STDERR -p 6379:6379 redis
```

上面的命令意味着我们只能看到来自容器的错误信息。

### 2.2. 互动模式

我们以交互模式启动容器，同时使用-i和-t选项：

```shell
$ docker run -it ubuntu /bin/bash
```

在这里，-i选项附加容器中 bash shell 的标准输入流 ( stdin )， -t选项为进程分配一个伪终端。这让我们可以从我们的终端与容器进行交互。

### 2.3. 分离模式

我们使用-d选项以分离模式运行容器：

```shell
$ docker run -d --name test_redis -p 6379:6379 redis
```

此命令启动容器，打印其 ID，然后返回到 shell 提示符。因此，我们可以在容器继续在后台运行的同时继续执行其他任务。

我们可以稍后使用它的名称或容器 ID 连接到这个容器。

## 3.与正在运行的容器交互

### 3.1. 执行命令

[execute](https://docs.docker.com/engine/reference/commandline/exec/)命令让我们可以在已经运行的容器中执行命令：

```shell
$ docker exec -it test_redis redis-cli
```

此命令在名为test_redis的Redis容器中打开一个redis-cli会话，该容器已在运行。我们也可以使用容器id而不是名称。如 2.2 节所述，选项-it启用交互模式。

但是，我们可能只想根据键获取值：

```shell
$ docker exec test_redis redis-cli get mykey
```

这将在redis-cli中执行get命令，返回键mykey的值，并关闭会话。

也可以在后台执行命令：

```shell
$ docker exec -d test_redis redis-cli set anotherkey 100
```

在这里，我们使用 -d 来达到这个目的。它针对键anotherKey设置值 100 ，但不显示命令的输出。

### 3.2. 附加会话

[attach](https://docs.docker.com/engine/reference/commandline/attach/)命令将我们的终端连接到正在运行的容器：

```shell
$ docker attach test_redis
```

默认情况下，该命令将标准输入、输出或错误流与主机 shell 绑定。

要仅查看输出和错误消息，我们可以使用–no-stdin选项省略stdin：

```shell
$ docker attach --no-stdin test_redis
```

## 4. 从容器中分离

从 docker 容器分离的方式取决于它的运行模式。

### 4.1. 默认模式

按 CTRL-c 是结束会话的常用方式。但是，如果我们在没有使用-d或-it选项的情况下启动容器，则 CTRL -c命令会停止容器而不是断开连接。会话将CTRL-c即SIGINT信号传播到容器并终止其主进程。

让我们覆盖传递–sig-proxy=false的行为：

```shell
$ docker run --name test_redis --sig-proxy=false -p 6379:6379 redis
```

现在，我们可以按CTRL-c仅分离当前会话，同时容器继续在后台运行。

### 4.2. 互动模式

在这种模式下，CTRL-c充当交互式会话的命令，因此它不能用作分离键。在这里，我们应该使用CTRL-p CTRL-q来结束会话。

### 4.3. 后台模式

在这种情况下，我们需要在附加会话时覆盖–sig-proxy值：

```shell
$ docker attach --sig-proxy=false test_redis
```

我们还可以通过–detach-keys选项定义一个单独的键：

```shell
$ docker attach --detach-keys="ctrl-x" test_redis
```

这将分离容器并在我们按下CTRL-x时返回提示。

## 5.总结

在本文中，我们看到了如何在附加和分离模式下启动 docker 容器。

然后，我们查看了一些命令来启动或结束与活动容器的会话。

---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

众所周知，我们可以使用Docker将我们的应用程序容器化，从而使软件交付更加容易。除了仅嵌入我们的应用程序之外，我们还可以使用各种选项扩展目标容器，例如，安装外部文件。

在本教程中，我们将重点介绍如何使用Docker挂载单个文件，并展示不同的实现方式。此外，我们将讨论过程中的常见错误以及如何解决这些错误。

## 2. 在Docker中持久化数据

在我们继续之前，让我们快速回顾一下[Docker 如何管理应用程序数据](https://www.baeldung.com/ops/docker-volumes#what-is-a-volume)。

我们知道Docker 为每一个运行的容器创建了一个隔离的环境。默认情况下，文件存储在内部容器的可写层上。这意味着当容器不再存在时，在容器内所做的任何更改都将丢失。

为了防止数据丢失，Docker 提供了两种主要机制来将我们的文件持久化到主机：[volumes](https://docs.docker.com/storage/volumes/) 和[bind mounts](https://docs.docker.com/storage/bind-mounts/)。

卷由Docker本身创建和管理，外部进程不应修改它们。另一方面，绑定挂载可以由主机系统存储和管理，而不会阻止非Docker进程编辑数据。

为了实现我们的目标，我们将专注于绑定机制。

## 3. 使用DockerCLI 绑定文件

与Docker交互的最简单方法是使用专用的DockerCLI，它使用配置标志执行各种命令。正如我们所知，要创建单个容器，我们可以使用带有所需镜像的docker run命令：

```shell
docker run alpine:latest
```

[根据官方参考](https://docs.docker.com/engine/reference/commandline/run/)，运行命令支持许多允许预配置容器的附加选项。让我们浏览一下可用选项列表：

```shell
--mount		        Attach a filesystem mount to the container
--volume , -v		Bind mount a volume
```

两者的[工作方式相似，允许我们在本地持久化数据](https://docs.docker.com/storage/bind-mounts/#choose-the--v-or---mount-flag)。现在让我们看看它们中的每一个。

### 3.1. –mount选项

–mount选项的语法由多个键值对组成，使用<key>=<value>元组。键的顺序不重要，用逗号分隔多对。

首先，让我们在工作目录中创建一个虚拟文件：

```shell
$ echo 'Hi Baeldung! >> file.txt
```

要将单个本地文件挂载到容器，我们可以扩展之前的运行命令：

```shell
$ docker run -d -it \
   --mount type=bind,source="$(pwd)"/file.txt,target=/file.txt,readonly \
   alpine:latest
```

我们刚刚创建并启动了一个新容器来装载我们的本地文件。现在让我们看一下配置键。

该 类型 指定具有可用值的安装机制：bind、volume或tmpfs。在我们的例子中，我们应该始终为bind设置一个值。

源 (或者 - src)是主机上应该挂载的文件或目录的绝对路径。我们也可以使用本地 shell 命令来计算结果。

目标(或者 - 目的地，dst)采用文件或目录在容器内安装的绝对路径。 

最后，还有一个readonly选项，它使绑定挂载成为只读的。这个标志是可选的。

最后我们来验证一下挂载结果：

```shell
$ docker exec ... cat /file.txt
Hi Baeldung!
```

我们还可以检查容器详细信息，使用docker inspect命令检查所有挂载：

```shell
"Mounts": [
    {
        "Type": "bind",
        "Source": ".../file.txt",
        "Destination": "/file.txt",
        "Mode": "",
        "RW": false,
        "Propagation": "rprivate"
    }
],
```

现在，我们可以看看与路径相关的常见错误。如果我们提供非绝对路径，Docker CLI 将返回终止命令执行的错误：

```shell
docker: Error response from daemon: invalid mount config for type "bind": invalid mount path: 'file.txt+' mount path must be absolute.
```

有时我们会提供主机上丢失的文件的绝对源路径。在这种情况下，容器将开始在目标路径中挂载一个空目录。此外，如果我们在Windows上工作，我们[应该注意路径转换](https://docs.docker.com/desktop/windows/troubleshoot/#path-conversion-on-windows)。

### 3.2. –volume选项

正如我们之前提到的，我们可以用相同的功能替换–mount和–volume ( –v) 标志。我们还必须记住语法是完全不同的。

–volume语法 由三个字段组成，由冒号字符分隔。此外，值的顺序很重要。

让我们使用–v 选项转换前面的示例：

```shell
$ docker run -d -it
    -v "$(pwd)"/file.txt:/file.txt:ro \
    alpine:latest
```

结果是一样的。我们刚刚将本地文件挂载到容器中。

正如我们所见，-v选项给出的三个值类似于与 -mount标志一起使用的键。

第一个值，如source键，指定主机上文件或目录的路径。

第二个字段提供容器内的路径和目标键。

最后，我们有一个可选的ro选项，用于指定只读属性。

在我们检查了语法之后，让我们看看一些常见的错误。和以前一样，我们应该记住Windows路径分隔符。选择一个不存在的文件也会导致创建一个空目录。

但与非绝对路径略有不同。如果我们提供 第二个值的无效路径，与之前相同，Docker CLI 将返回一个错误。但是，如果我们提供这样的路径作为源值，Docker 将创建一个命名卷，这是另一种持久文件机制。

总之，–mount和 –volume标志之间最显着的区别是它们的语法。我们可以互换使用它们。

## 4. 使用Docker Compose绑定文件

在我们学习了如何使用DockerCLI 绑定文件之后，现在让我们检查一下我们是否仍然可以使用 docker-compose 文件获得相同的结果。

众所周知，[docker-compose 是一种通过提供配置文件来创建容器的便捷方式](https://www.baeldung.com/ops/docker-compose)。对于每个服务，我们可以声明[一个卷部分来配置绑定选项](https://docs.docker.com/compose/compose-file/#volumes)。volumes 部分可以使用长语法或短语法指定，它们分别与–mount和 –volumes标志有很多共同之处。

### 4.1. 长语法

长语法允许我们单独配置每个键以指定卷安装。同样的例子，让我们准备一个 docker-compose 条目：

```yaml
services:
  alpine:
    image: alpine:latest
    tty: true
    volumes:
      - type: bind
        source: ./file.txt
        target: file.txt
        read_only: true
```

与DockerCLI 一样，我们的容器现在已预先配置为在其中装载本地文件。我们使用type、 source、target和可选的read_only键来确定配置，就像我们使用–mount标志一样。此外，我们可以使用从 docker-compose 文件计算的相对路径作为源值。

### 4.2. 短语法

短语法使用 由冒号分隔的单个字符串值来指定卷安装：

```yaml
services:
  alpine:
    image: alpine:latest
    tty: true
    volumes:
      - ./file.txt:/file.txt:ro
```

该字符串几乎与–volume标志相同。前两个值分别代表源路径和目标路径。最后一部分指定附加标志，我们可以在其中指定只读属性。与长语法一样，我们也可以使用相对源路径。

我们必须记住，长格式允许我们配置不能用短语法表达的额外字段。此外，我们可以在单个卷部分中混合使用这两种语法。

## 5.总结

在本文中，我们刚刚介绍了Docker中数据持久化的一部分。我们尝试使用DockerCLI 和 docker-compose 文件将单个本地文件挂载到容器中。

Docker CLI 提供了–mount和–volume选项以及 用于绑定单个文件或目录的运行 命令。这两个标志的工作方式相似，但语法不同。因此，我们可以互换使用它们。

我们也可以使用 docker-compose 文件达到相同的结果。在每个服务的卷部分中，我们可以使用长语法或短语法配置卷挂载。与以前一样，这些语法可互换地产生相同的结果。

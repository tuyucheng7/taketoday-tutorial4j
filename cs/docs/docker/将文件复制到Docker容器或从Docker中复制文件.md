---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 简介

随着我们越来越多的应用程序被部署到云环境中，使用[Docker](https://www.baeldung.com/docker-java-api)正在成为开发人员的必备技能。通常在调试应用程序时，将文件复制到我们的Docker容器中或从中复制出来非常有用。

在本教程中，我们将学习将文件复制到Docker容器或从中复制文件的不同方法。

## 2. docker cp命令

将文件复制到Docker容器或从中复制文件的最快方法是使用[docker cp命令](https://docs.docker.com/engine/reference/commandline/cp/)。此命令非常模仿 Unix cp 命令，并具有以下语法：

docker cp <SRC> <目标>

在我们查看此命令的一些示例之前，让我们假设我们正在运行以下Docker容器：

```plaintext
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
1477326feb62        grafana/grafana     "/run.sh"                2 months ago        Up 3 days           0.0.0.0:3000->3000/tcp   grafana
8c45029d15e8        prom/prometheus     "/bin/prometheus --c…"   2 months ago        Up 3 days           0.0.0.0:9090->9090/tcp   prometheus
```

第一个示例将主机上/tmp目录中的文件复制到grafana容器中的 Grafana 安装目录中：

```shell
docker cp /tmp/config.ini grafana:/usr/share/grafana/conf/
```

我们还可以使用容器 ID 代替它们的名称：

```shell
docker cp /tmp/config.ini 1477326feb62:/usr/share/grafana/conf/
```

要将文件从grafana容器复制到主机上的/tmp目录，我们只需切换参数的顺序：

```shell
docker cp grafana:/usr/share/grafana/conf/defaults.ini /tmp
```

我们还可以复制整个目录而不是单个文件。此示例将整个conf目录从grafana容器复制到主机上的/tmp目录：

```shell
docker cp grafana:/usr/share/grafana/conf /tmp
```

docker cp命令确实有一些限制。首先，我们不能用它在两个容器之间进行复制。它只能用于在主机系统和单个容器之间复制文件。

[其次，虽然它确实具有与Unix cp 命令](https://www.baeldung.com/linux/copy-file-to-multiple-directories)相同的语法，但它不支持相同的标志。实际上，它只支持两种：

-a : 存档模式，它保留被复制文件的所有 uid/gid 信息
-L : 始终遵循 SRC 中的符号链接

## 3.卷挂载

将文件复制到Docker容器和从Docker容器复制文件的另一种方法是使用卷挂载。这意味着我们在容器内创建一个来自主机系统的目录。

要使用卷挂载，我们必须使用-v标志运行我们的容器：

```shell
docker run -d --name=grafana -p 3000:3000 grafana/grafana -v /tmp:/transfer
```

上面的命令运行一个grafana容器，并将主机中的/tmp目录挂载为名为/transfer的容器内的一个新目录。如果我们愿意，我们可以提供多个-v标志以在容器内创建多个卷安装。

这种方法有几个优点。首先，我们可以使用 Unix cp 命令，它比docker cp命令有更多的标志和选项。

第二个优点是我们可以为所有Docker容器创建一个共享目录。这意味着我们可以直接在容器之间复制，只要它们都具有相同的卷挂载。

请记住，这种方法的缺点是所有文件都必须通过卷安装。这意味着我们不能在单个命令中复制文件。相反，我们首先将文件复制到挂载的目录中，然后再复制到它们最终需要的位置。

这种方法的另一个缺点是我们可能会遇到文件所有权问题。Docker容器通常只有一个root用户，这意味着在容器内创建的文件默认拥有root所有权。如果主机需要，我们可以使用[Unix chown命令恢复文件所有权。](http://www.linfo.org/chown.html)

## 4.文件

[Dockerfile](https://docs.docker.com/engine/reference/builder/)用于构建Docker镜像，然后将其实例化到Docker容器中。Dockerfile可以包含多个不同的指令，其中之一是COPY。

COPY指令让我们将一个(或多个)文件从主机系统复制到镜像中。这意味着文件成为从该镜像创建的每个容器的一部分。

COPY指令的语法类似于我们在上面看到的其他复制命令：

```plaintext
COPY <SRC> <DEST>
```

就像其他复制命令一样，SRC可以是单个文件，也可以是主机上的目录。它还可以包含通配符以匹配多个文件。

让我们看一些例子。

这将从当前Docker构建上下文中复制一个到镜像中：

```plaintext
COPY properties.ini /config/
```

这会将所有 XML 文件复制到Docker镜像中：

```plaintext
COPY *.xml /config/
```

这种方法的主要缺点是我们不能用它来运行Docker容器。[Docker镜像不是Docker容器](https://www.baeldung.com/docker-images-vs-containers)，所以这种方法只有在提前知道镜像中所需的文件集时才有意义。

## 5.总结

在本文中，我们讨论了如何将文件复制到Docker容器或从中复制文件。每个选项都有一些优点和缺点，因此我们必须选择最适合我们需要的方法。

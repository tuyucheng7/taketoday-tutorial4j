---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在Docker中，了解容器化应用程序正在侦听哪些端口非常重要。我们还需要一种从容器外部访问应用程序的方法。

为了解决这些问题，Docker 使我们能够公开和发布端口。

在本文中，我们将了解公开和发布端口。我们将使用一个简单的 Nginx Web 服务器容器作为示例。

## 2.暴露端口

暴露的端口是关于容器化应用程序的一段元数据。在大多数情况下，这会显示应用程序正在侦听的端口。Docker 本身不会对暴露的端口做任何事情。但是，当我们启动容器时，我们可以在发布端口时使用此元数据。

### 2.1. 使用 Nginx 公开

让我们使用 Nginx Web 服务器来尝试一下。

如果我们看一下[Nginx 官方Dockerfile](https://github.com/nginxinc/docker-nginx/blob/dcaaf66e4464037b1a887541f39acf8182233ab8/mainline/debian/Dockerfile)，我们会看到端口 80 是通过以下命令公开的：

```shell
EXPOSE 80
```

端口 80 在这里暴露，因为它是http协议的默认端口。让我们在本地机器上运行 Nginx 容器，看看我们是否可以通过端口 80 访问它：

```shell
$ docker run -d nginx
```

上面的命令将使用 Nginx 的最新镜像并运行容器。我们可以使用以下命令仔细检查 Nginx 容器是否正在运行：

```shell
$ docker container ls
```

此命令将输出有关所有正在运行的容器的一些信息，包括 Nginx：

```plaintext
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS               NAMES
cbc2f10f787f        nginx               "/docker-entrypoint..."   15 seconds ago      Up 15 seconds       80/tcp              dazzling_mclean

```

在这里，我们在端口部分下看到 80。由于暴露了 80 端口，我们可能认为访问localhost:80(或只是localhost)将显示 Nginx 默认页面，但事实并非如此：

```shell
$ curl http://localhost:8080
... no web page appears
```

虽然端口是暴露的，但Docker并没有向主机开放它。

### 2.2. 暴露端口的方法

在Docker中公开端口有两种主要方式。我们可以使用EXPOSE命令在Dockerfile中执行此操作：

```shell
EXPOSE 8765
```

或者，我们也可以在运行容器时使用–expose选项公开端口：

```shell
$ docker run --expose 8765 nginx
```

## 3.发布端口

要通过 docker 主机访问容器端口，我们需要发布它。

### 3.1. 使用 Nginx 发布

让我们使用映射端口运行 Nginx：

```shell
$ docker run -d -p 8080:80 nginx
```

上面的命令会将宿主机的8080端口映射到容器的80端口。该选项的一般语法是：

```shell
-p <hostport>:<container port>
```

如果我们去localhost:8080，我们应该得到 Nginx 的默认欢迎页面：

```shell
$ curl http://localhost:8080
StatusCode : 200
StatusDescription : OK
Content : <!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
... more HTML
```

让我们列出所有正在运行的容器：

```shell
$ docker container ls
```

现在我们应该看到容器有一个端口映射：

```shell
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                  NAMES
38cfed3c61ea        nginx               "/docker-entrypoint..."   31 seconds ago      Up 30 seconds       0.0.0.0:8080->80/tcp   dazzling_kowalevski
```

在端口部分下，我们有0.0.0.0:8080->80/tcp映射。

默认情况下，Docker 为主机添加了0.0.0.0不可路由的元地址。这意味着映射对主机的所有地址/接口都有效。

### 3.2. 限制容器访问

我们可以根据主机 IP 地址限制对容器的访问。我们可以在映射中指定主机 IP 地址，而不是允许从所有接口( 0.0.0.0允许)访问容器。

让我们将对容器的访问限制为仅来自127.0.0.1环回地址的流量：

```shell
$ docker run -d -p 127.0.0.1:8081:80 nginx
```

在这种情况下，容器只能从主机本身访问。这使用扩展语法进行发布，其中包括地址绑定：

```shell
-p <binding address>:<hostport>:<container port>
```

## 4. 发布所有暴露的端口

暴露的端口元数据对于启动容器很有用，因为Docker使我们能够发布所有暴露的端口：

```shell
$ docker run -d --publish-all nginx
```

在这里，Docker 将容器中所有暴露的端口绑定到主机上的空闲随机端口。

让我们看看这个命令启动的容器：

```shell
$ docker container ls

CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                   NAMES
0a23e78732ce        nginx               "/docker-entrypoint..."   6 minutes ago       Up 6 minutes        0.0.0.0:32768->80/tcp   pedantic_curran
```

正如我们所料，Docker 从主机中选择了一个随机端口(在本例中为 32768)并将其映射到暴露的端口。

## 5.总结

在本文中，我们了解了如何在Docker中公开和发布端口。

我们还讨论了公开的端口是关于容器化应用程序的元数据，而发布端口是从主机访问应用程序的一种方式。

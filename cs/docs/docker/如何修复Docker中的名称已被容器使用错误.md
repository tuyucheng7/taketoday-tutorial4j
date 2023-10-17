---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在启动Docker容器时，我们有时可能会遇到“名称已被容器使用”的错误。

在这个简短的教程中，我们将研究这个很容易修复的常见问题。 

首先，我们将展示如何导致此错误。 然后，我们将解释其原因。最后，我们将展示如何修复它。

## 2. 错误是如何产生的

### 2.1. 获取Docker镜像

让我们首先为示例选择一个Docker镜像。

我们将使用免费且公开可用的[Nginx 演示镜像](https://hub.docker.com/r/nginxdemos/hello/)。[NGINX](https://www.nginx.com/resources/glossary/nginx/) 是一种免费的开源 Web 服务器，许多公司都在使用它，例如 Netflix、 CloudFare和Airbnb。我们将要使用的Docker演示镜像提供一个具有一些基本属性(例如主机名、IP 地址和端口)的网页。

### 2.2. 运行多个容器并导致错误

为了导致错误，我们需要运行两个使用相同名称baeldung_nginx的实例。

值得考虑的是，为什么我们甚至需要为容器命名。名称可以是为正在运行的容器列表添加意义的便捷方式。更重要的是，该名称可以作为Docker网络中的参考。

让我们启动第一个容器：

```shell
docker run --name baeldung_nginx -p 80:80 -d nginxdemos/hello:plain-text
```

我们以分离模式运行容器，这意味着它将在后台运行。我们将容器的端口 80 发布到主机上的同一端口。最后，我们为容器指定了自定义名称 – baeldung_nginx。

现在，如果我们在浏览器中打开http://localhost，我们应该会看到类似这样的内容：

```shell
Server address: 123.45.6.7:80
Server name: e378ad49d49d
Date: 08/Apr/2022:22:08:44 +0000
URI: /
Request ID: 7bda7e3234cb6d1e51900fccc89320d5
```

现在让我们尝试运行第二个容器。我们将为第二个实例分配端口 81，因为主机端口 80 已被第一个容器占用：

```
docker run --name baeldung_nginx -p 81:80 -d nginxdemos/hello:plain-text
```

不幸的是，这不起作用。我们得到一个错误：

```shell
docker: Error response from daemon: Conflict. The container name "/baeldung_nginx" is already in use by container "76da8f6d3accc9b6d41c8a98fd492d4b8622804220ee628a438264b8cf4ae3d4". 
You have to remove (or rename) that container to be able to reuse that name.
See 'docker run --help'.
```

## 3. 解释错误的根本原因

每个Docker容器都有一个分配给它的唯一名称。如果我们不在 docker run命令中使用可选的名称参数，Docker 会分配一个随机名称。

在我们的例子中，我们想将相同的名称baeldung_nginx 分配给两个不同的容器。我们应该注意，即使我们使用相同的Docker镜像，每个docker run命令都会创建一个新容器。

由于第二个容器无法使用已在使用的名称，我们得到了错误。

## 4. 如何解决

### 4.1. 重启容器

该方案适用于系统中已经存在名为baeldung_nginx 的Docker容器的情况 ，是正确的状态。在这种情况下，我们不希望有两个同名的不同实例。相反，我们想重启已经存在的容器。

为了重新启动现有容器， 我们必须使用 docker start 而不是 docker run 命令。 

docker run创建一个新的镜像容器。我们可以创建尽可能多的相同镜像的克隆。另一方面，docker start 启动一个先前停止的容器。

因此，我们可能不会尝试启动一个新容器，而是重启现有容器，在这种情况下，这就是解决方案。然而，有时我们想用新镜像启动现有容器的替代品。

### 4.2. 删除现有容器

当我们确定我们希望我们的新容器接管该名称，并且我们已经停止了任何其他具有该名称的容器时，我们可以简单地删除具有该名称的先前容器：

```shell
docker rm baeldung_nginx
```

不幸的是，这个命令并不总是有效。例如，其他容器可能需要我们的容器才能正常工作。如果是这种情况，我们仍然想删除我们的容器，我们可以在 remove 命令中使用force标志：

```shell
docker rm -f baeldung_nginx
```

一旦删除了之前的容器，我们就可以使用我们选择的名称自由启动一个新容器。

### 4.3. 为容器使用不同的名称

如果我们想运行同一镜像的两个实例怎么办？这种情况的解决方案很简单。我们所要做的就是使用两个不同的名称和端口：

```shell
docker run --name baeldung_nginx_1 -p 80:80 -d nginxdemos/hello:plain-text
docker run --name baeldung_nginx_2 -p 81:80 -d nginxdemos/hello:plain-text
```

现在，让我们使用以下Docker命令列出正在运行的容器：

```shell
docker ps
```

我们应该看到类似这样的东西：

```shell
CONTAINER ID   IMAGE                         COMMAND                  CREATED          STATUS          PORTS                NAMES
f341bb9fe165   nginxdemos/hello:plain-text   "/docker-entrypoint.…"   2 seconds ago    Up 2 seconds    0.0.0.0:81->80/tcp   baeldung_nginx_2
33883c2b31a7   nginxdemos/hello:plain-text   "/docker-entrypoint.…"   12 seconds ago   Up 11 seconds   0.0.0.0:80->80/tcp   baeldung_nginx_1
```

## 5.总结

在本文中，我们学习了如何修复Docker 中的“名称已被容器使用”错误。

首先，我们看到了如何重现错误。然后，我们查看了错误的根本原因。

最后，我们看到了解决该问题的三种不同解决方案。

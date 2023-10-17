---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

本教程将演示如何将Docker镜像推送到自托管Docker注册表。首先，我们将探讨什么是自托管注册表以及它与公共注册表的区别。然后我们将学习如何标记我们想要推送到自托管注册表的镜像。最后，我们将看到如何实际推送这些镜像。

## 2. 自托管Docker注册表

Docker 注册表是一种管理容器镜像仓库的服务。它允许我们执行创建仓库、推送和拉取镜像以及管理仓库访问等操作。虽然许多注册中心是作为云服务提供的，但注册中心也可以是自托管的。

当通过云提供注册表时，我们不必关心它的托管和维护。我们只需注册该服务，然后使用提供的功能。云托管的Docker注册表也称为公共注册表。Docker Hub 是公共注册表的一个著名示例。

但是，如果一家公司有特殊要求，公共登记处可能不是一种选择。不能使用公共注册中心的公司通常有自托管的注册中心，他们可以根据自己的要求进行定制。例如，一家公司可能有一项隐私政策，要求只能通过公司的内部网络访问仓库。或者公司可能需要对其镜像运行特定的漏洞扫描。

## 3. 为自托管注册表标记镜像

我们必须以特定方式标记镜像，然后才能将它们推送到自托管注册表。让我们学习如何适当地标记镜像。

### 3.1. 镜像名称模式

推送图片时，图片名称定义了图片将被推送到哪里。这就是为什么为了将镜像推送到自托管注册表，我们使用正确的命名模式至关重要。镜像名称必须包含注册表主机、端口和仓库名称，并且可以选择后跟版本标记：

```plaintext
[registry host]:[registry port]/[repository name]:[tag name]
```

假设我们有一个在localhost上运行并侦听端口5000 的注册表。这是我们要推送到该注册表中的my-fancy-app仓库的镜像版本1.0.0的示例名称：

```plaintext
localhost:5000/my-fancy-app:1.0.0
```

我们将在本文的其余部分使用此示例。

现在我们知道要应用什么命名模式，我们可以为我们的自托管注册表准备一个镜像。首先，我们将了解如何使用正确的名称构建新镜像。然后我们将学习如何通过重命名来准备现有镜像。

### 3.2. 标记新镜像

如果我们正在构建一个新镜像，并且已经准备好应用程序和Dockerfile，我们可以使用docker build命令的-t标志，以便使用适当的名称创建镜像。使用上一节中的示例，命令将是：

```plaintext
$ docker build -t localhost:5000/my-fancy-app:1.0.0 .
```

这会使用正确的名称标记新镜像，后跟可选的版本标记。点“.” 在该行的末尾表示我们正在与Dockerfile相同的目录中执行命令。

### 3.3. 重新标记现有镜像

如果我们想将现有镜像推送到我们的自托管注册表，我们首先需要使用与我们上面描述的命名模式相匹配的新别名来标记镜像。我们可以使用docker tag命令重新标记镜像：

```plaintext
docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
```

例如，假设我们要将名为my-fancy-app:1.0.0的现有镜像推送到我们的自托管注册表。我们将简单地使用docker tag命令用一个与该注册表匹配的新别名来标记它：

```plaintext
$ docker tag my-fancy-app:1.0.0 localhost:5000/my-fancy-app:1.0.0
```

## 4.推送镜像

一旦我们正确地标记了镜像，我们就可以将它推送到我们的自托管注册表。

公司通常需要身份验证来保护他们的自托管注册表。在这种情况下，我们首先需要使用docker login命令登录：

```plaintext
docker login [OPTIONS] [SERVER]
```

对于托管在localhost:5000上的自托管注册表，命令是：

```plaintext
$ docker login localhost:5000
```

登录后，我们使用docker push命令将镜像推送到我们的自托管注册表：

```plaintext
docker push [OPTIONS] NAME[:TAG]
```

让我们看看推送我们在上面部分中准备的镜像的命令：

```plaintext
$ docker push localhost:5000/my-fancy-app:1.0.0
```

## 5.验证推送

最后一步是验证镜像现在是否在我们的自托管注册表中可用。为此，我们将从该注册表中提取镜像，但我们必须首先通过运行docker image rm命令删除缓存的本地镜像：

```plaintext
$ docker image rm localhost:5000/my-fancy-app:1.0.0
```

让我们通过列出我们所有的本地镜像并检查我们的镜像不存在来验证我们删除了镜像：

```plaintext
$ docker images
```

接下来，让我们使用docker pull命令从我们的自托管注册表中拉取镜像：

```plaintext
$ docker pull localhost:5000/my-fancy-app:1.0.0
```

通过再次列出所有本地镜像，我们可以验证我们的镜像是否已成功从我们的自托管注册表中提取：

```plaintext
$ docker images
REPOSITORY                  TAG   IMAGE ID     CREATED        SIZE 
localhost:5000/my-fancy-app 1.0.0 d33a5b65c0f5 20 minutes ago 326MB
```

## 六，总结

在本文中，我们了解了自托管Docker注册表是什么以及我们为什么要使用它们。我们还探讨了如何准备镜像——无论是预先存在的镜像还是我们正在构建的新镜像——以便我们可以将其推送到自托管注册表。最后，我们学习了如何将正确标记的镜像推送到我们的示例注册表。

---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[在本教程中，我们将学习Docker](https://www.baeldung.com/ops/docker-guide)中标签的概念。

Docker 支持将镜像存储在[Docker Hub 仓库](https://hub.docker.com/)中。Docker 标签为Docker镜像提供了唯一标识。在Docker仓库中，有一组具有不同版本的相似镜像，由标签标识。

在这里，我们将学习使用docker build和docker tag命令标记镜像。

## 2. 了解Docker标签

Docker 标签有助于维护构建版本以将镜像推送到Docker Hub。Docker Hub 允许我们根据名称和标签将镜像分组在一起。多个Docker标签可以指向一个特定的镜像。基本上，与在 Git 中一样，Docker 标签类似于特定的提交。Docker 标签只是镜像 ID 的别名。

标签的名称必须是 ASCII 字符串，可以包含大小写字母、数字、下划线、句点和破折号。此外，标签名称不能以句点或破折号开头，并且只能包含 128 个字符。

## 3. 使用Docker标签构建镜像

在我们继续之前，让我们首先创建一个示例Dockerfile来演示标记：

```shell
FROM centos:7
RUN yum -y install wget 
RUN yum -y install unzip 
RUN yum -y install java-1.8.0-openjdk 
RUN yum clean all
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME
```

在上面的Dockerfile中，我们运行所有必要的命令来使用“centos:7”作为基础镜像安装 java。

### 3.1. 使用单个Docker标签构建镜像

在Docker中，我们可以在构建期间标记镜像。为了说明，让我们检查一下标记镜像的命令：

```shell
$ docker build -t baeldung-java:5 .
Sending build context to Docker daemon  2.048kB
Step 1/2 : FROM centos:7
 ---> eeb6ee3f44bd
Step 2/2 : RUN yum -y install wget
 ---> Using cache
 ---> 46ee47a7422d
Successfully built 46ee47a7422d
Successfully tagged baeldung-java:5
```

在这里，在上面的命令中，我们提供了“ baeldung-java:5 ”作为Docker镜像的标签。Docker 中的标签对于维护构建版本以将镜像推送到 DockerHub 很有用。版本控制通常用于部署任何Docker镜像或返回到旧版本。

我们还可以使用以下语法为标签提供用户名和镜像名称：

```shell
$ docker build -t baeldung/baeldung-java:5 .
Sending build context to Docker daemon  2.048kB
Step 1/2 : FROM centos:7
 ---> eeb6ee3f44bd
....
Successfully built 46ee47a7422d
Successfully tagged baeldung/baeldung-java:5
```

在这里，在上面的命令中，我们提供了用户名“baeldung” ，镜像名称为“ baeldung-java ”，标签为“ 5 ”。

### 3.2. 使用多个Docker标签构建镜像

在Docker中，我们还可以为一个镜像分配多个标签。 在这里，我们将使用docker build命令在单个命令中将多个标签分配给镜像。

为了演示，让我们查看上述Dockerfile的命令：

```shell
$ docker build -t baeldung-java:5 -t baeldung-java:6 .
Sending build context to Docker daemon  2.048kB
Step 1/2 : FROM centos:7
 ---> eeb6ee3f44bd
....
Successfully built 46ee47a7422d
Successfully tagged baeldung-java:5
Successfully tagged baeldung-java:6
```

在这里，我们可以看到为imageId “ 46ee47a7422d ”创建了2 个标签“baeldung-java:5”和“baeldung-java:6”。

### 3.3. 构建没有任何标签的镜像

我们还可以在不使用任何标签的情况下构建Docker镜像。但是为了跟踪镜像，我们应该始终提供带有镜像名称的标签。让我们看看构建没有标签的镜像的命令：

```shell
$ docker build -t baeldung-java .
Sending build context to Docker daemon  2.048kB
Step 1/2 : FROM centos:7
 ---> eeb6ee3f44bd
...
Successfully built 46ee47a7422d
Successfully tagged baeldung-java:latest
```

在这里，在上面的命令中，我们构建了没有任何标签的镜像，所以默认情况下，Docker 为镜像提供了一个标签作为最新的“ baeldung-java:latest”。

Docker 始终使用 latest 标签指向最新的稳定版本。旧版本甚至可以称为最新版本。但是我们无法预测它是大版本还是小版本。

## 4. 使用docker tag命令标记镜像

到目前为止，我们已经讨论了使用docker build命令标记镜像。[但我们也可以使用docker tag](https://docs.docker.com/engine/reference/commandline/tag/)命令显式标记镜像。标记镜像只是为镜像名称或imageId创建别名。在这里，我们将探索两种标记镜像的方法。

Docker镜像名称的一般格式如下：

```shell
<user-name>/<image-name>:<tag-name>
```

在上面的代码片段中，冒号后面的组件表示附加到镜像的标签。

让我们看看使用镜像名称标记镜像的命令：

```shell
$ docker tag baeldung-java:6 baeldung-java:8 
```

使用imageId标记镜像的命令如下：

```shell
$ docker tag 46ee47a7422d baeldung-java:9 
```

让我们检查一下到目前为止创建的所有镜像：

```shell
$ docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
baeldung-java       5                   46ee47a7422d        13 minutes ago      370MB
baeldung-java       6                   46ee47a7422d        13 minutes ago      370MB
baeldung-java       8                   46ee47a7422d        13 minutes ago      370MB
baeldung-java       9                   46ee47a7422d        13 minutes ago      370MB
baeldung-java       latest              46ee47a7422d        13 minutes ago      370MB
centos              7                   eeb6ee3f44bd        7 months ago        204MB
```

在这里，我们找到到目前为止创建的所有镜像。

## 五、 docker pull命令中Tag的使用

Docker 标签在创建镜像或从Docker Hub仓库中拉取镜像时非常有用。在我们的Dockerfile中，我们使用了FROM centos:7
命令。这将拉取centos公共镜像的版本“ 7”。

我们还可以拉取带标签或不带标签的镜像。

让我们看一下带有特定标签的命令：

```shell
$ docker pull centos:7
```

没有任何标签的docker pull命令：

```shell
$ docker pull centos
```

上面的命令将从 公共Docker Hub仓库中提取“centos:latest”镜像。我们还可以将多个标签应用于镜像，通常用于指定主要版本和次要版本。

## 六，总结

在本文中，我们学习了如何在Docker中创建和管理标签。我们探索了各种标记镜像的方法。

使用docker build命令，我们首先标记了一个镜像。后来，我们查看了docker tag命令。此外，我们还探索了使用标签的docker pull命令。

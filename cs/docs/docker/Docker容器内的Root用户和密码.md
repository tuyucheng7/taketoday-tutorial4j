---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker 的](https://www.baeldung.com/ops/docker-guide)工作原理是将应用程序及其所有必需的依赖项打包到轻量级[容器](https://www.docker.com/resources/what-container/)中。除了在测试时部署在本地集群上，我们还可以在生产环境中部署这些轻量级容器。

在本教程中，我们将研究使用不同用户在Docker容器中执行命令。

首先，我们将学习使用root用户访问Docker容器以获得一些额外权限。我们还将讨论为root和非root用户设置密码以保护容器免受易受攻击的来源。

## 2. 设置Docker容器

在我们继续之前，我们将创建一个Dockerfile来添加用户john：

```shell
FROM ubuntu:16.04
RUN apt-get update 
RUN useradd -m john
USER john
CMD /bin/bash
```

这里我们使用“ ubuntu:16.04 ”作为基础镜像。[让我们使用docker build](https://docs.docker.com/engine/reference/commandline/build/)命令构建镜像：

```shell
$ docker build -t baeldung .
Sending build context to Docker daemon  2.048kB
Step 1/5 : FROM ubuntu:16.04
16.04: Pulling from library/ubuntu
58690f9b18fc: Pull complete 
...
Step 5/5 : CMD /bin/bash
 ---> Running in d04af94585e2
Removing intermediate container d04af94585e2
 ---> 312faa93c781
Successfully built 312faa93c781
Successfully tagged baeldung:latest
```

现在我们将使用baeldung镜像运行一个Docker容器：

```shell
$ docker run -id --name baeldung baeldung
34dbc77279a2a6244b0e4ee87890d79e814128391c6a4387d2e2fd10fa6e8f20
```

[我们将使用docker ps](https://docs.docker.com/engine/reference/commandline/ps/)命令验证容器是否按预期运行：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED              STATUS              PORTS               NAMES
34dbc77279a2        baeldung            "/bin/sh -c /bin/bash"   About a minute ago   Up About a minute                       baeldung
```

在这里我们可以看到Docker容器正在运行，没有任何问题。

## 3.访问Docker容器

Docker容器旨在以root用户身份访问，以执行非root用户无法执行的命令。我们可以使用docker exec在正在运行的容器中运行命令。我们将使用docker exec命令的-i和-t选项来获取具有 TTY 终端访问权限的交互式 shell。

### 3.1. 使用非根用户

Docker容器“ baeldung ”已启动并正在运行。我们将使用docker exec命令来访问它：

```shell
$ docker exec -it baeldung bash
```

注意我们之前创建的Dockerfile。我们添加了一个新用户john，它被设置为使用该Docker镜像运行的所有容器的默认用户。[我们将使用whoami](https://www.baeldung.com/linux/tag/whoami)命令验证这一点：

```shell
$ whoami
john

```

现在，如果我们尝试将任何包安装到容器中，我们将收到以下错误消息：

```shell
$ apt-get update
Reading package lists... Done
W: chmod 0700 of directory /var/lib/apt/lists/partial failed - SetupAPTPartialDirectory (1: Operation not permitted)
E: Could not open lock file /var/lib/apt/lists/lock - open (13: Permission denied)
E: Unable to lock directory /var/lib/apt/lists/
```

在这种情况下，非root用户无法访问锁定文件。通常，此用户对容器的访问权限将受到限制。

现在我们将退出容器并使用root用户重新登录。

### 3.2. 使用根用户

为了在Docker容器内使用root用户执行，我们将使用-u选项：

```shell
$ docker exec -it -u 0 baeldung bash
```

使用docker exec命令的“ -u”选项，我们将定义根用户的ID 。我们也可以在此命令中使用用户名：

```shell
$ docker exec -it -u root baeldung bash
```

为了检查当前用户的详细信息，我们将运行whoami命令：

```shell
$ whoami
root
```

这次，我们以root用户身份进入容器。现在我们可以对容器执行任何操作：

```shell
$ apt-get update
Hit:1 http://security.ubuntu.com/ubuntu xenial-security InRelease
Hit:2 http://archive.ubuntu.com/ubuntu xenial InRelease
Hit:3 http://archive.ubuntu.com/ubuntu xenial-updates InRelease
Hit:4 http://archive.ubuntu.com/ubuntu xenial-backports InRelease
Reading package lists... Done  
```

从上面的输出我们可以看出更新命令成功了，root用户可以访问锁文件了。凭借root用户的全部权限，我们可以毫无问题地更改任何文件。

作为替代方案，我们也可以以root身份访问Docker容器。在这种情况下，我们将使用[nsenter](https://manpages.ubuntu.com/manpages/xenial/man1/nsenter.1.html)命令访问Docker容器。要使用nsenter命令，我们必须知道正在运行的容器的PID 。

我们来看看获取容器PID的命令：

```shell
$ docker inspect --format {{.State.Pid}} baeldung
6491
```

获得PID后，我们将通过以下方式将其与nsenter命令一起使用：

```shell
$ nsenter --target 6491 --mount --uts --ipc --net --pid
```

这允许我们以root用户身份访问Docker容器，并运行任何命令来访问任何文件。

## 4.在容器内使用sudo命令

Docker容器通常以root作为默认用户运行。为了共享具有不同权限的资源，我们可能需要在Docker容器中创建额外的用户。

在这里，我们将创建一个Dockerfile，并添加一个新用户。重要的是，我们还将在构建镜像时在Docker容器中安装[sudo包。](https://www.baeldung.com/linux/sudo-command)当这个用户需要额外的权限时，它可以使用 s udo命令访问它们。

让我们检查一下Dockerfile：

```shell
FROM ubuntu:16.04
RUN apt-get update && apt-get -y install sudo
RUN useradd -m john && echo "john:john" | chpasswd && adduser john sudo
USER john
CMD /bin/bash

```

这个Dockerfile使用镜像“ ubuntu:16.04 ”作为基础镜像，安装sudo包，并创建一个新用户“ john ”。我们还使用[chpasswd](https://linux.die.net/man/8/chpasswd)命令为john用户添加密码。之后，我们会将其用作默认用户。

让我们运行命令来构建镜像：

```shell
$ docker build -t baeldung .

```

上面的命令将创建baeldung镜像。现在让我们使用baeldung镜像运行容器：

```shell
$ docker run -id --name baeldung baeldung
b0f83a7e8b49ddf043c80792f21d5c483c0c5ab56c700815a83b0a40e5292754

```

容器的默认用户是john，因此我们将使用它来访问容器：

```shell
$ docker exec -it baeldung bash
To run a command as administrator (user "root"), use "sudo <command>".
See "man sudo_root" for details.

```

让我们运行whoami命令来找出登录用户的用户名：

```shell
$ whoami
john
```

这确认我们以非root用户身份登录。如果我们运行[apt-get update](https://www.baeldung.com/linux/yum-and-apt)命令，我们将遇到我们在 3.2 节中遇到的与权限相关的相同问题。

这一次，我们将使用sudo命令为非根用户john获取权限：

```shell
$ sudo apt-get update
[sudo] password for john: 
Get:1 http://security.ubuntu.com/ubuntu xenial-security InRelease [99.8 kB]
Hit:2 http://archive.ubuntu.com/ubuntu xenial InRelease 
Get:3 http://archive.ubuntu.com/ubuntu xenial-updates InRelease [99.8 kB]
Get:4 http://archive.ubuntu.com/ubuntu xenial-backports InRelease [97.4 kB]
Fetched 297 kB in 1s (178 kB/s)
Reading package lists... Done
```

通过使用这种方法，我们可以使用sudo命令在非root帐户中运行任何命令。

## 5.总结

在本文中，我们演示了如何在不同用户的Docker容器内运行命令。首先，我们讨论了root用户和非root用户在运行的Docker容器中的角色。然后我们学习了如何以root用户身份访问Docker容器以获得额外的权限。

理想情况下，我们不应该允许root访问Docker容器。这增加了更多的安全问题。相反，我们应该创建一个单独的用户来访问容器。这是容器世界中的标准安全步骤。

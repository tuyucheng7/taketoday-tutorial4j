---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 一、概述

[Docker](https://www.baeldung.com/ops/docker-guide)是在隔离环境中打包应用程序的有用工具。它简化了在多个平台上部署应用程序的过程。[docker run](https://docs.docker.com/engine/reference/commandline/run/)命令用于从Docker镜像启动新容器。默认情况下，docker run命令只在[容器](https://www.baeldung.com/ops/docker-container-shell)中执行一条命令。但是，在某些情况下，我们可能需要在单个docker run命令中运行多个命令。

在本教程中，我们将讨论如何在Docker容器启动时运行多个命令。

## 2.使用docker run命令

要在docker run命令中执行多个命令，我们可以使用[&&](https://www.baeldung.com/linux/conditional-expressions-shell-script)运算符将命令链接在一起。&&运算符执行第一个命令，如果成功，则执行第二个命令。

但是，为了避免在主机[shell](https://www.baeldung.com/linux/sh-vs-bash)上运行第二个命令，我们必须使用sh命令的-c选项同时执行多个命令。

让我们使用sh -c运行whoami 和date命令：

```shell
$ docker run centos:latest sh -c "whoami && date"
root
Sun Dec 18 10:10:12 UTC 2022
```

这会在单个运行命令中同时执行whoami 和date命令。我们也可以使用; 带有sh的-c选项的运算符可以运行多个命令。此外，让我们使用-w选项指定要在Docker容器中执行的命令的工作目录：

```shell
$ docker run -w /home centos:latest sh -c "whoami ; pwd"
root
/home
```

在这里，我们可以看到whoami和[pwd](https://www.baeldung.com/linux/run-script-different-working-dir)命令都执行了，但是这次/home是docker容器执行这些命令的默认工作目录。

## 3.在Dockerfile中使用CMD/ENTRYPOINT

除了在 run 命令中运行多个命令，我们还可以在[Dockerfile](https://www.baeldung.com/category/docker/tag/dockerfile)的[CMD/ENTRYPOINT](https://www.baeldung.com/ops/dockerfile-run-cmd-entrypoint)部分指定多个命令。

Dockerfile的CMD和ENTRYPOINT定义了在容器启动时执行的默认命令。如果我们向ENTRYPOINT和CMD部分添加多个命令，Docker 将按顺序运行它们。

让我们看看Dockerfile在ENTRYPOINT部分指定多个命令：

```shell
FROM centos 
ENTRYPOINT ["sh", "-c", "whoami && date"]
```

为了运行容器，我们需要先构建镜像：

```shell
$ docker build -f Dockerfile -t baeldung_run .
Sending build context to Docker daemon  2.048kB
Step 1/2 : FROM centos
 ---> 5d0da3dc9764
Step 2/2 : ENTRYPOINT ["sh", "-c", "whoami && date"]
 ---> Running in dd027b5ba1e9
Removing intermediate container dd027b5ba1e9
 ---> a43dfa09d48b
Successfully built a43dfa09d48b
Successfully tagged baeldung_run:latest
```

让我们使用上面的baeldung_run:latest镜像运行一个容器：

```shell
$ docker run -itd --name baeldung_run baeldung_run 
b2a8ff012797d6110fd73dfffbf3c39e081f111dc50aac5d9d62fa73845b8a59
```

现在，我们可以通过查看baeldung_run容器的日志文件来验证命令的执行情况：

```shell
$ docker logs -f baeldung_run
root
Sun Dec 18 10:13:44 UTC 2022
```

从上面的输出我们可以看出，两条命令都执行成功了。

我们还可以使用CMD指令运行多个命令。让我们看看带有CMD指令的Dockerfile：

```shell
FROM centos 
CMD ["sh", "-c", "whoami && date"]
```

同样，我们需要先创建Docker镜像，然后运行一个容器。在容器日志中，我们将看到使用ENRTYPOINT指令生成的相同输出。

我们应该注意CMD和ENTRYPOINT指令不能相互替代。它们都有不同的用途。但是，在ENTRYPOINT和CMD中运行多个命令遵循相同的语法。 

## 4. 总结

在本文中，我们讨论了如何在正在运行的Docker容器中运行多个命令。

首先，我们学习了使用docker run命令执行多个命令。之后，我们使用Dockerfile中的ENTRYPOINT/CMD指令进行了探索。

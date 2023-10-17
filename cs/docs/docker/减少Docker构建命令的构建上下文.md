---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

Docker构建上下文由位于特定路径或 URL 的文件或文件夹组成[。](https://www.baeldung.com/tag/docker/)在构建期间，这些文件被发送到Docker守护进程，以便镜像可以将它们用作文件。

在本教程中，我们将了解Docker构建上下文以及与之相关的问题。我们将探索各种方法来减少构建镜像的构建上下文。

## 2. 了解Docker构建上下文

在我们继续讨论这个问题之前，让我们首先了解Docker构建上下文及其工作原理。Docker 构建上下文是文件和目录的集合，当我们运行docker build时，Docker 引擎可以访问这些文件和目录，而任何不属于构建上下文的内容都将无法被[Dockerfile](https://docs.docker.com/engine/reference/builder/)中的命令访问。

在某些情况下，Docker CLI 和Docker引擎可能不在同一台机器上运行。在docker build期间，Docker CLI 将文件发送到Docker引擎以构建镜像。

我们还可以使用 GIT 仓库的 URL 作为构建上下文。结果，构建上下文成为指定 git 仓库的内容。

## 3.理解问题

我们将在构建镜像时了解与构建上下文相关的问题。为了进一步理解构建上下文的问题，让我们举个例子并创建一个示例Dockerfile：

```shell
FROM   centos:7
MAINTAINER maintainer@baeldung.com
COPY jdk-8u202-linux-x64.rpm / 
```

要构建镜像，我们需要运行以下命令：

```shell
$ docker build -t javaapplication .
```

上述命令的输出：

```shell
Sending build context to Docker daemon  178.4MB
Step 1/3 : FROM   centos:7
 ---> eeb6ee3f44bd
Step 2/3 : MAINTAINER maintainer@baeldung.com
 ---> Using cache
 ---> 4c798858cf11
Step 3/3 : COPY jdk-8u202-linux-x64.rpm /
 ---> Using cache
 ---> 9c58f775bb80
Successfully built 9c58f775bb80
Successfully tagged test:latest
```

现在让我们将大小为186M的jdk-8u202-linux-x64.tar.gz放入同一目录，而不更改Dockerfile。

在运行docker build命令时，我们将获得以下输出：

```shell
Sending build context to Docker daemon  372.5MB
Step 1/3 : FROM   centos:7
 ---> eeb6ee3f44bd
Step 2/3 : MAINTAINER maintainer@baeldung.com
 ---> Using cache
 ---> 4c798858cf11
Step 3/3 : COPY jdk-8u202-linux-x64.rpm /
 ---> Using cache
 ---> 9c58f775bb80
Successfully built 9c58f775bb80
Successfully tagged test:latest
```

在这里我们可以看到Docker守护进程的构建上下文从178.4MB增加到372.5MB，尽管我们没有对Dockerfile进行任何更改。

Docker 构建上下文的关键点之一是它递归地包含当前工作目录的所有文件和文件夹，并将它们发送给Docker守护进程。

## 4.解决方案使用。docker忽略文件

为了减少Docker构建上下文。我们可以使用.dockerignore文件来解决构建上下文问题。

Docker CLI在将其发送到Docker守护程序之前在上下文的根目录中搜索名为.dockerignore的文件。如果文件存在，CLI 会排除与其模式匹配的目录和文件。这将防止大文件或敏感目录被不必要地发送到Docker守护进程。

在这里，我们将jdk-8u202-linux-x64.tar.gz文件添加到.dockerignore文件中，以便在创建构建时忽略它：

```shell
$ echo "jdk-8u202-linux-x64.tar.gz" > .dockerignore
```

现在让我们构建Docker镜像：

```shell
$ docker build -t baeldung  .
Sending build context to Docker daemon  178.4MB
Step 1/3 : FROM   centos:7
 ---> eeb6ee3f44bd
```

在这里，我们可以清楚地看到发送给Docker守护进程的Docker构建上下文已经从372.5MB减少到178.4MB。

## 5. 使用 EOF 文件创建

我们将使用带有 EOF 的docker build命令直接创建一个Dockerfile。

让我们假设以下Dockerfile：

```shell
FROM   centos:7
MAINTAINER maintainer@baeldung.com
RUN echo "Welcome to Bealdung"
```

为上述Dockerfile构建镜像。我们运行docker build命令并获得以下输出：

```shell
$ docker build -t baeldung .
Sending build context to Docker daemon  372.5MB
Step 1/3 : FROM   centos:7
 ---> eeb6ee3f44bd
Step 2/3 : MAINTAINER maintainer@baeldung.com
 ---> Using cache
 ---> a7088e6a3e53
Step 3/3 : RUN echo "Welcome to Bealdung"
 ---> Using cache
 ---> 1fc84e62de75
Successfully built 1fc84e62de75
Successfully tagged baeldung:latest
```

在这里，我们可以看到发送到Docker守护进程的Docker构建上下文是372.5MB。如果我们使用以下方式运行相同的Dockerfile：

```shell
$ docker build -t test -<<EOF
FROM   centos:7
MAINTAINER maintainer@baeldung.com
RUN echo "Welcome to Bealdung"
EOF
```

上述命令的输出如下：

```shell
Sending build context to Docker daemon  2.048kB
Step 1/3 : FROM   centos:7
 ---> eeb6ee3f44bd
Step 2/3 : MAINTAINER maintainer@baeldung.com
 ---> Using cache
 ---> a7088e6a3e53
Step 3/3 : RUN echo "Welcome to Bealdung"
 ---> Running in 6240c486fcb5
Welcome to Bealdung
Removing intermediate container 6240c486fcb5
 ---> 1fc84e62de75
Successfully built 1fc84e62de75
Successfully tagged baeldung:latest
```

在这里，我们可以看到构建上下文已从372 MB减少到2.048KB。 此解决方案仅适用于我们不使用任何COPY和ADD命令的情况。

## 6. 优化Docker镜像

现在让我们讨论优化现有Docker镜像的各种方法。开发镜像最具挑战性的方面是使镜像尺寸尽可能小。Dockerfile中的每条指令都会为镜像添加一层，我们应该在进入下一层之前尝试移除任何不需要的伪影。

为了编写一个真正高效的Dockerfile，我们传统上需要使用 shell 技巧和其他逻辑来使层尽可能小，并确保每一层都具有它需要的来自前一层的工件，而没有其他任何东西。

### 6.1. 使用多阶段构建

在多阶段构建中，我们在单个Dockerfile中使用多个 FROM 语句。每条 FROM 指令使用不同的基础，每条 FROM 指令开始构建的新阶段。人工制品可以有选择地从一个阶段复制到另一个阶段。

让我们看一个在Dockerfile中使用多阶段的示例：

```shell
FROM centos:7 as builder
RUN yum -y install maven
COPY spring-boot-application /spring-boot-application
WORKDIR /spring-boot-application/
RUN mvn clean package -Dmaven.test.skip=true
FROM centos:7
RUN yum install -y epel-release \
    && yum install -y maven wget \
    && yum -y install java-1.8.0-openjdk \
    && yum clean all 
COPY --from=builder /spring-boot-application/target/spring-boot-application-0.0.1-SNAPSHOT.jar /
CMD ["java -jar ","-c","/spring-boot-application-0.0.1-SNAPSHOT.jar && tail -f /dev/null"]
```

在这里，在上面的Dockerfile中，我们使用了两个From语句。首先，我们在第一层使用[mvn](https://www.baeldung.com/maven)命令构建了spring-boot-application-0.0.1-SNAPSHOT.jar 。然后我们使用“COPY–from=builder”命令在第二层使用相同的 jar 文件 。这样我们就可以删除第一层并显着减小尺寸。

### 6.2. 减少级数

在构建镜像时，我们要注意Dockerfile创建的层。RUN 命令为每次执行创建一个新层。可以通过合并图层来减小镜像大小。

通常，用户运行这样的命令：

```shell
RUN apt-get -y update
RUN apt-get install -y python
```

上面的Dockerfile将创建两层。但是将这两个命令组合在一起将在最终镜像中创建一个图层：

```shell
RUN apt-get -y update && apt-get install -y python
```

因此，巧妙的命令组合可以生成更小的镜像。

### 6.3. 构建自定义基础镜像

Docker 缓存镜像。每当我们有同一层的多个实例时，我们应该优化层并创建自定义基础镜像。加载时间将加快，跟踪将更容易。

## 七、总结

在本教程中，我们了解了Docker中构建上下文的概念。我们首先讨论了Docker构建上下文的细节和执行。

然后，我们解决了使用docker build命令创建Docker镜像时构建上下文增加的问题。首先，我们探索了问题的用例，然后在Docker中使用不同的方式解决了它们。

最后，我们研究了优化Docker镜像的不同方法。

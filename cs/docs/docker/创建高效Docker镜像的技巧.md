---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在过去几年中，Docker 已成为Linux上容器化的事实标准。Docker 易于使用并提供轻量级虚拟化，随着越来越多的服务在云中运行，使其成为构建应用程序和微服务的理想选择。

虽然创建我们的第一张[图片](https://www.baeldung.com/docker-images-vs-containers)相对容易，但构建高效的图片需要深谋远虑。在本教程中，我们将看到有关如何编写高效Docker镜像的示例以及每个建议背后的原因。

让我们从使用官方镜像开始。

## 2. 以官方形象为基础

### 2.1. 什么是官方图片？

[官方Docker镜像](https://docs.docker.com/docker-hub/official_images/)是由Docker赞助的团队创建和维护的，或者至少由他们批准的。他们在 GitHub 项目上公开管理Docker镜像。他们还会在发现漏洞时进行更改，并确保镜像是最新的并遵循最佳实践。

[让我们通过使用Nginx](https://hub.docker.com/_/nginx)官方镜像的示例更清楚地了解这一点。网络服务器的创建者维护着这个形象。

假设我们想使用 Nginx 来托管我们的静态网站。我们可以创建我们的Dockerfile并将其基于官方镜像：

```shell
FROM nginx:1.19.2
COPY my-static-website/ /usr/share/nginx/html
```

然后我们可以构建我们的镜像：

```shell
$ docker build -t my-static-website .
```

最后，运行它：

```shell
$ docker run -p 8080:80 -d my-static-website
```

我们的Dockerfile只有两行。基础官方镜像负责 Nginx 服务器的所有细节，比如默认配置文件和应该公开的端口。

更具体地说，基础镜像阻止 Nginx 成为守护进程并结束初始进程。这种行为在其他环境中是预期的，但在Docker中，这被解释为应用程序结束，因此容器终止。解决办法是配置 Nginx 不成为守护进程。这是官方镜像中的[配置：](https://github.com/nginxinc/docker-nginx/blob/1.19.2/stable/buster/Dockerfile#L110)

```shell
CMD ["nginx", "-g", "daemon off;"]
```

当我们的镜像基于官方镜像时，我们可以避免难以调试的意外错误。官方镜像维护者是Docker和我们要使用的软件方面的专家，因此我们可以从他们的所有知识中获益，并可能节省时间。

### 2.2. 镜像由其创建者维护

虽然在之前解释的意义上不是官方的，但Docker Hub中还有其他镜像也由应用程序的创建者维护。

让我们用一个例子来说明这一点。[EMQX](https://hub.docker.com/r/emqx/emqx)是一个 MQTT 消息代理。假设我们想将此代理用作我们应用程序中的微服务之一。我们可以基于他们的镜像并添加我们的配置文件。[或者更好的是，我们可以使用他们提供的通过环境变量](https://www.baeldung.com/ops/docker-container-environment-variables)配置 EMQX 。

例如，要更改 EMQX 默认监听的端口，我们可以添加EMQX_LISTENER__TCP__EXTERNAL环境变量：

```shell
$ docker run -d -e EMQX_LISTENER__TCP__EXTERNAL=9999 -p 9999:9999 emqx/emqx:v4.1.3
```

作为特定软件背后的社区，他们最有能力为他们的软件提供Docker镜像。

在某些情况下，我们不会为我们要使用的应用程序找到任何类型的官方镜像。即使在这些情况下，我们也会从Docker Hub中搜索可以用作参考的镜像中受益。

我们以 H2 为例。H2 是一个用Java编写的轻量级关系数据库。虽然没有 H2 的官方镜像，但[第三方](https://hub.docker.com/r/oscarfonts/h2)创建了一个并对其进行了很好的记录。我们可以使用他们的[GitHub 项目](https://github.com/oscarfonts/docker-h2)来学习如何将 H2 用作独立服务器，甚至协作使项目保持最新。

即使我们仅使用Docker镜像项目作为构建镜像的起点，我们也可能学到比从头开始更多的东西。

## 3. 尽可能避免构建新镜像

在跟上Docker的步伐时，我们可能会养成总是创建新镜像的习惯，即使与基础镜像相比变化不大。对于这些情况，我们可能会考虑将我们的配置直接添加到正在运行的容器中，而不是构建镜像。

每次更改时都需要重新构建自定义Docker镜像。之后还需要将它们上传到注册表。如果镜像包含敏感信息，我们可能需要将其存储在私有仓库中。在某些情况下，我们可能会通过使用基础镜像并动态配置它们来获得更多好处，而不是每次都构建自定义镜像。

让我们以 HAProxy 为例来说明这一点。与 Nginx 一样，HAProxy 可以用作反向代理。Docker 社区维护其[官方形象](https://hub.docker.com/_/haproxy)。

假设我们需要配置 HAProxy 以将请求重定向到我们应用程序中的适当微服务。所有这些逻辑都可以写在一个配置文件中，比方说my-config.cfg。Docker镜像要求我们将该配置放置在特定路径上。

让我们看看如何使用安装在正在运行的容器上的自定义配置来运行 HAProxy：

```shell
$ docker run -d -v my-config.cfg:/usr/local/etc/haproxy/haproxy.cfg:ro haproxy:2.2.2
```

这样，即使升级 HAProxy 也变得更加直接，因为我们只需要更改标签即可。当然，我们还需要确认我们的配置对于新版本仍然有效。

如果我们正在构建一个由许多容器组成的解决方案，我们可能已经在使用编排器，例如DockerSwarm 或Kubernetes。它们提供了存储配置然后将其链接到正在运行的容器的方法。Swarm 称它们为[Configs](https://docs.docker.com/engine/swarm/configs/)，而Kubernetes称它们为[ConfigMaps](https://kubernetes.io/docs/concepts/configuration/configmap/)。

编排工具已经考虑到我们可能会在我们使用的镜像之外存储一些配置。在某些情况下，将我们的配置保留在镜像之外可能是最好的折衷方案。

## 4.创建精简镜像

镜像大小很重要有两个原因。首先，较轻的镜像传输速度更快。当我们在开发机器中构建镜像时，它可能看起来不像是游戏规则改变者。尽管如此，当我们在 CI/CD 管道上构建多个镜像并可能部署到多个服务器时，每个部署节省的总时间可能是可感知的。

其次，为了获得更精简的镜像版本，我们需要删除镜像未使用的额外包。这将帮助我们减少攻击面，从而提高镜像的安全性。

让我们看看减少Docker镜像大小的两种简单方法。

### 4.1. 可用时使用 Slim 版本

在这里，我们有两个主要选择：Debian 的精简版和Alpine Linux发行版。

slim 版本是 Debian 社区为从标准镜像中删除不必要的文件所做的努力。 许多Docker镜像已经是基于 Debian 的精简版。

例如，HAProxy 和 Nginx 镜像基于 Debian 发行版debian:buster-slim的精简版。多亏了这一点，这些镜像从数百 MB 减少到只有几十 MB。

在其他一些情况下，图片会提供标准全尺寸版本和纤薄版本。例如，最新的[Python 镜像](https://hub.docker.com/_/python)提供了一个 slim 版本，目前是python:3.7.9-slim，它比标准镜像小了近十倍。

另一方面，许多镜像都提供了 Alpine 版本，就像我们之前提到的 Python 镜像一样。基于 Alpine 的镜像通常大小约为 10 MB。

Alpine Linux的设计从一开始就考虑到了资源效率和安全性。这使得它非常适合基础Docker镜像。

要记住的一点是，Alpine Linux几年前选择将系统库从更常见的glibc更改为musl。虽然大多数软件都可以正常运行，但如果我们选择 Alpine 作为我们的基础镜像，我们会很好地彻底测试我们的应用程序。

### 4.2. 使用多阶段构建

[多阶段构建](https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds)功能允许在同一Dockerfile中的多个阶段构建镜像，通常在下一阶段使用前一阶段的结果。让我们看看这有什么用。

假设我们想要使用 HAProxy 并使用其 REST API 数据[平面 API](https://www.haproxy.com/blog/announcing-haproxy-dataplane-api-20/)对其进行动态配置。由于此 API 二进制文件在基础 HAProxy 镜像中不可用，因此我们需要在构建时下载它。

我们可以在一个阶段下载 HAProxy API 二进制文件并将其提供给下一个阶段：

```shell
FROM haproxy:2.2.2-alpine AS downloadapi
RUN apk add --no-cache curl
RUN curl -L https://github.com/haproxytech/dataplaneapi/releases/download/v2.1.0/dataplaneapi_2.1.0_Linux_x86_64.tar.gz --output api.tar.gz
RUN tar -xf api.tar.gz
RUN cp build/dataplaneapi /usr/local/bin/

FROM haproxy:2.2.2-alpine
COPY --from=downloadapi /usr/local/bin/dataplaneapi /usr/local/bin/dataplaneapi
...
```

第一阶段downloadapi下载最新的 API 并解压缩tar文件。第二阶段复制二进制文件，以便 HAProxy 稍后可以使用它。我们不需要卸载curl或删除下载的tar文件，因为第一阶段已完全丢弃，不会出现在最终镜像中。

在其他一些情况下，多级镜像的优势更为明显。例如，如果我们需要从源代码构建，最终镜像将不需要任何构建工具。第一阶段可以安装所有构建工具并构建二进制文件，下一阶段将仅复制这些二进制文件。

即使我们并不总是使用此功能，但知道它存在也是件好事。在某些情况下，它可能是瘦身的最佳选择。

## 5.总结

容器化将继续存在，而Docker是开始使用它的最简单方法。它的简单性帮助我们快速提高工作效率，尽管有些课程只能通过经验学习。

在本教程中，我们回顾了一些构建更健壮和安全镜像的技巧。由于容器是现代应用程序的基石，它们越可靠，我们的应用程序就越强大。

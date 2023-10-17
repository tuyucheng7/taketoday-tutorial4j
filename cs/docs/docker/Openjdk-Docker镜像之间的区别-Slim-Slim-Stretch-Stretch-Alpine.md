---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.baeldung.com/ops/docker-guide)是一个容器化平台，可帮助开发人员将他们的应用程序捆绑到一个称为[容器的](https://www.baeldung.com/tag/docker-container)单元中。[OpenJDK](https://www.baeldung.com/oracle-jdk-vs-openjdk)是Java开发工具包 (JDK) 的免费社区维护版本。OpenJDK 的Docker镜像是预配置的环境，其中包括 OpenJDK 运行时和工具。

选择正确的 OpenJDKDocker镜像至关重要，因为它会影响我们软件的性能、大小和安全性。不同的镜像有不同的妥协，例如镜像的大小或速度。[因此，了解 OpenJDKDocker镜像](https://www.baeldung.com/ops/efficient-docker-images)之间的差异对于做出最终决定至关重要。

在本教程中，我们将了解 OpenJDK 的各种Docker镜像之间的区别。

注意：自 2022 年 7 月起，Docker Hub 已弃用 OpenJDK 镜像。建议用户使用合适的替代品，例如 Amazon Corretto 和 Eclipse Temurin。

## 2. 理解差异

这些镜像之间的主要区别在于基本[操作系统](https://www.baeldung.com/cs/os-types)、集成包和整体镜像大小。Slim 和 Slim-Stretch 基于 Debian [Linux](https://www.baeldung.com/linux/)，而 Stretch 基于[Debian](https://www.debian.org/)的完整“延伸”版本。另一方面，[Alpine](https://www.baeldung.com/linux/shell-alpine-docker)使用Alpine Linux发行版，以其紧凑的尺寸和安全措施而闻名。

在最小和最大的镜像中，Slim 最小，而 Stretch 最大。Alpine 也是一个小镜像，但它基于不同的Linux发行版。不同镜像的性能因工作负载和用例而异。与较大的镜像相比，Slim 和 Alpine 镜像通常更快并且使用的资源更少。但是，对于需要更多依赖项的应用程序，可能需要更大的镜像。Alpine 镜像有一个庞大而活跃的社区来支持和更新镜像，而 Slim 和 Stretch 镜像可能有一个较小的社区。

## 3.苗条

Slim 镜像是可用的最小的 OpenJDKDocker镜像，其[Java 8](https://www.baeldung.com/ubuntu-install-jdk)版本的大小约为295MB。它针对生产使用进行了优化，并且仅包含运行Java应用程序所需的最少包。我们来看看拉取Java 8版本的OpenJDK slim镜像的命令：

```shell
$ docker pull openjdk:8-jdk-slim
8-jdk-slim: Pulling from library/openjdk
1efc276f4ff9: Pull complete 
a2f2f93da482: Pull complete 
1a2de4cc9431: Pull complete 
9013b84ebbe7: Pull complete 
Digest: sha256:19578a1e13b7a1e4cab9b227fb7b5d80e14665cf4024c6407d72ba89842a97ed
Status: Downloaded newer image for openjdk:8-jdk-slim
docker.io/library/openjdk:8-jdk-slim
```

Slim 镜像是需要小镜像和快速启动时间的应用程序的不错选择，例如微服务和无服务器功能。让我们检查一下openjdkL8-jdk-slim的镜像大小：

```shell
$ docker images openjdk:8-jdk-slim
REPOSITORY   TAG          IMAGE ID       CREATED        SIZE
openjdk      8-jdk-slim   80e75f92be33   7 months ago   295MB
```

使用 Slim 镜像的主要优点是体积小，启动时间快。这个包只包含我们运行Java应用程序所需的组件，不包含任何无关的包。需要较少依赖项的应用程序将从该镜像中受益。由于最小的包集，它可能不适合所有应用程序。

## 4. 修身弹力

Slim-Stretch 镜像对于需要较小镜像但需要来自 Debian “stretch”版本的附加软件的软件来说是一个明智的选择。这可能涉及需要不属于先前 Debian 发行版的工具或库的软件。例如，libfontconfig1在 Slim 镜像中不可用，而 Slim-Stretch 包含来自 Debian stretch版本的其他工具和库，包括libfontconfig1。

使用 Slim-Stretch 的主要好处是它有以前的 Debian 发行版中没有的额外软件包，但同时继承了 Slim 镜像的小占用空间。缺点是 Slim-Stretch 可能无法针对生产使用进行微调。我们来看看拉取Java 8版本的openjdk :8-jdk-slim-stretch镜像的命令：

```shell
$ docker pull openjdk:8-jdk-slim-stretch
8-jdk-slim-stretch: Pulling from library/openjdk
fc7181108d40: Pull complete 
73f08ce352c8: Pull complete 
eac271a34b40: Pull complete 
774220066612: Pull complete 
Digest: sha256:9fa43d5e1c45e42c0dbbf3713d49edd8e7cc01be9e1c4ef5e723efd6dfb47929
Status: Downloaded newer image for openjdk:8-jdk-slim-stretch
docker.io/library/openjdk:8-jdk-slim-stretch
```

让我们检查一下openjdk:8-jdk-slim-stretch的镜像大小：

```shell
$ docker images openjdk:8-jdk-slim-stretch
REPOSITORY   TAG                  IMAGE ID       CREATED       SIZE
openjdk      8-jdk-slim-stretch   4de02be2e9ab   3 years ago   269MB
```

Slim-Stretch 镜像与 Slim 镜像非常相似，但附带了一个来自 Debian “stretch”版本的额外软件包。

## 5. 伸展

Stretch 镜像是 Slim、Slim-Stretch 和 Strech 镜像中最重的镜像。与其他镜像相比，它具有更广泛的包和依赖项范围，使其成为某些应用程序的合适选择。Stretch 中有libgtk-3-dev，它提供 GTK+ 版本3，但它不存在于 Slim 或 Slim-Stretch 中。一些带有图形用户界面的应用程序需要这个库，它可能不包含在较小的镜像中。

使用 Stretch 的主要好处是其全面的包和依赖项，这对于特定应用程序至关重要。但是，它比其他镜像大得多，可能没有针对生产目的进行优化。我们来看看拉取Java 8版本的openjdk :8-jdk-stretch镜像的命令：

```shell
$ docker pull openjdk:8-jdk-stretch
8-jdk-stretch: Pulling from library/openjdk
3192219afd04: Pull complete 
17c160265e75: Pull complete 
cc4fe40d0e61: Pull complete 
9d647f502a07: Pull complete 
d108b8c498aa: Pull complete 
1bfe918b8aa5: Pull complete 
dafa1a7c0751: Pull complete 
Digest: sha256:d8d62fe0d8e9f3e6e62921c7d738c9a962efd6887b2b282dc2f852d7f1ee4512
Status: Downloaded newer image for openjdk:8-jdk-stretch
docker.io/library/openjdk:8-jdk-stretch
```

让我们检查一下openjdk:8-jdk-stretch的镜像大小：

```shell
$ docker images openjdk:8-jdk-stretch
REPOSITORY   TAG             IMAGE ID       CREATED       SIZE
openjdk      8-jdk-stretch   0bfcee65c8ca   3 years ago   488MB
```

Stretch 镜像是需要大量包和依赖项的应用程序的实用选择，特别是那些需要较小镜像中没有的特定库或工具的应用程序。

## 6. 高山

Alpine[镜像](https://www.baeldung.com/linux/bash-alpine-docker-images)依赖于Alpine Linux操作系统，该操作系统以其紧凑的尺寸和安全功能而闻名。它是可访问的最紧凑的 OpenJDKDocker镜像。

使用 Alpine 的主要好处是其小巧的尺寸和强大的安全功能。但是，它可能不适合所有软件应用程序，因为它具有不同的包管理器并且可能不包含某些应用程序所需的所有包。下面看一下拉取Java 8版本openjdk :8-jdk-alpine镜像的命令：

```shell
$ docker pull openjdk:8-jdk-alpine
8-jdk-alpine: Pulling from library/openjdk
e7c96db7181b: Pull complete 
f910a506b6cb: Pull complete 
c2274a1a0e27: Pull complete 
Digest: sha256:94792824df2df33402f201713f932b58cb9de94a0cd524164a0f2283343547b3
Status: Downloaded newer image for openjdk:8-jdk-alpine
docker.io/library/openjdk:8-jdk-alpine
```

让我们检查一下openjdk:8-jdk-alpine的镜像大小：

```shell
$ docker images openjdk:8-jdk-alpine
REPOSITORY   TAG            IMAGE ID       CREATED       SIZE
openjdk      8-jdk-alpine   a3562aa0b991   3 years ago   105MB
```

Alpine 镜像是需要小镜像和安全设置的应用程序的明智之选，例如那些在容器化或云原生环境中运行的应用程序。

## 7. 选择正确的 OpenJDK 镜像

根据我们的需要选择正确的 OpenJDKDocker镜像是一个至关重要的决定，它会影响我们应用程序的性能、大小、稳定性和安全性。通过了解可用镜像之间的差异并考虑镜像大小、性能要求和依赖关系等因素。因此，我们可以优化应用程序的性能。

选择 OpenJDKDocker镜像时，重要的是要考虑镜像的大小、应用程序的性能要求以及应用程序所需的特定依赖项等因素。选择理想的 OpenJDKDocker镜像的最佳实践是从满足我们要求的最小镜像开始。此外，我们可以试验各种镜像以确定最佳性能并定期使用安全补丁更新我们的镜像。

## 八、总结

在本文中，我们了解了 OpenJDK 的各种Docker镜像之间的差异。Slim 是为生产使用而优化的最小镜像。Slim-Stretch 包括来自 Debian “stretch”发行版的附加软件包。Stretch 包含更大的软件包和依赖项集，Alpine 是基于具有强大安全功能的Alpine Linux发行版的最小镜像。

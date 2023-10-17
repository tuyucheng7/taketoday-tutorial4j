---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 简介

[在上一篇文章中](https://www.baeldung.com/docker-images-vs-containers)，我们解释了Docker镜像和Docker容器之间的区别。简而言之，镜像就像一个Java类，而容器就像Java对象。

在本教程中，我们将重点介绍删除Docker镜像的各种方法。

## 2. 为什么要删除Docker镜像？

Docker 引擎存储镜像并运行容器。 为此， Docker引擎保留一定数量的磁盘空间作为镜像、容器和其他所有内容(例如全局Docker卷或网络)的“存储池”。

一旦该存储池已满，Docker 引擎将停止工作。我们无法再创建或下载新镜像，并且我们的容器无法运行。

Docker镜像占据了Docker Engine存储池的大部分，因此我们移除Docker镜像以保持Docker运行。

我们还删除镜像以保持我们的Docker引擎井井有条和干净。例如，我们可以在开发过程中轻松创建许多不再需要的镜像。或者，我们下载一些用于测试的软件镜像，稍后我们可以处理这些镜像。

我们可以轻松删除从Docker仓库中提取的Docker镜像。 如果我们再次需要它，我们将再次从仓库中提取它。

但是我们必须小心我们自己创建的Docker镜像。一旦删除，我们自己的镜像就会消失，除非我们保存它们。我们可以通过将它们推送到仓库或[将它们导出到 TAR 文件来](https://docs.docker.com/engine/reference/commandline/save/)保存Docker镜像。

## 3. 下载PostgreSQL 13 Beta 镜像

[PostgreSQL](https://www.postgresql.org/)是一个开源关系数据库。我们将使用[前两个PostgreSQL 13 beta Docker镜像](https://www.postgresql.org/about/news/2047/)作为示例。这两张图片比较小，我们可以快速下载。因为它们是测试版软件，我们的Docker引擎中还没有它们。

我们将使用 beta 2 镜像创建一个容器。我们不会直接使用 beta 1 镜像。

在下载这两个镜像之前，我们先看看Docker镜像在存储池中占用了多少空间：

```shell
docker system df --format 'table {{.Type}}\t{{.TotalCount}}\t{{.Size}}'
```

这是测试机的输出。第一行显示我们的 71 个Docker镜像使用 7.8 GB：

```shell
TYPE                TOTAL               SIZE
Images              71                  7.813GB
Containers          1                   359.1MB
Local Volumes       203                 14.54GB
Build Cache         770                 31.54GB
```

现在我们下载两个PostgreSQL镜像并重新检查Docker存储池：

```shell
docker pull postgres:13-beta1-alpine
docker pull postgres:13-beta2-alpine
docker system df --format 'table {{.Type}}\t{{.TotalCount}}\t{{.Size}}'

```

正如预期的那样，镜像数量从 71 增加到 73，整体镜像大小从 7.8 GB 增加到 8.1 GB。

为了简洁起见，我们只显示第一行：

```shell
TYPE                TOTAL               SIZE
Images              73                  8.119GB

```

## 4.删除单个镜像

让我们用PostgreSQL 13 beta 2镜像启动一个容器。我们将secr3t设置为数据库root用户的密码，因为没有密码PostgreSQL容器将无法启动：

```shell
docker run -d -e POSTGRES_PASSWORD=secr3t postgres:13-beta2-alpine
docker ps --format 'table {{.ID}}\t{{.Image}}\t{{.Status}}'
```

这是测试机上运行的容器：

```shell
CONTAINER ID        IMAGE                      STATUS
527bfd4cfb89        postgres:13-beta2-alpine   Up Less than a second
```

现在让我们删除PostgreSQL 13 beta 2镜像。我们将使用[docker image rm](https://docs.docker.com/engine/reference/commandline/image_rm/) 删除Docker镜像。此命令删除一个或多个镜像：

```shell
docker image rm postgres:13-beta2-alpine

```

该命令失败，因为正在运行的容器仍在使用该镜像：

```shell
Error response from daemon: conflict: unable to remove repository reference "postgres:13-beta2-alpine" (must force) - container 527bfd4cfb89 is using its referenced image cac2ee40fa5a
```

因此，让我们使用从docker ps获得的 ID 来停止正在运行的容器：

```shell
docker container stop 527bfd4cfb89
```

我们现在将尝试再次删除镜像，并得到相同的错误消息。我们无法删除容器使用的镜像，无论它是否正在运行。

因此，让我们删除容器。然后我们终于可以删除镜像了：

```shell
docker container rm 527bfd4cfb89
docker image rm postgres:13-beta2-alpine

```

Docker 引擎打印镜像删除的详细信息：

```shell
Untagged: postgres:13-beta2-alpine
Untagged: postgres@sha256:b3a4ebdb37b892696a7bd7e05763b938345f29a7327fc17049c7148c03ff6a92
removed: sha256:cac2ee40fa5a40f0abe53e0138033fe7a9bcee28e7fb6c9eaac4d3a2076b1a86
removed: sha256:6a14bab707274a8007da33fe08ea56a921f356263d8fd5e599273c7ee4880170
removed: sha256:5e6ef40b9f6f8802452dbca622e498caa460736d890ca20011e7c79de02adf28
removed: sha256:dbd38ed4b347c7f3c81328742a1ddeb1872ad52ac3b1db034e41aa71c0d55a75
removed: sha256:23639f6bd6ab4b786e23d9d7c02a66db6d55035ab3ad8f7ecdb9b1ad6efeec74
removed: sha256:8294c0a7818c9a435b8908a3bcccbc2171c5cefa7f4f378ad23f40e28ad2f843
```

docker system df确认删除。镜像数量从 73 个减少到 72 个，整体镜像大小从 8.1 GB 到 8.0 GB：

```shell
TYPE                TOTAL               SIZE
Images              72                  7.966GB
```

## 5.按名称删除多个镜像

让我们再次下载我们在上一节中删除的PostgreSQL 13 beta 2镜像：

```shell
docker pull postgres:13-beta2-alpine
```

现在我们要按名称删除 beta 1 镜像和 beta 2 镜像。到目前为止，我们只使用了 beta 2 镜像。如前所述，我们没有直接使用 beta 1 镜像，所以我们现在可以删除它。

不幸的是， docker image rm不提供按名称删除的过滤器选项。相反，我们将链接Linux命令以按名称删除多个镜像。

我们将通过仓库和标签引用镜像，就像在docker pull命令中一样。仓库是postgres，标签是13-beta1-alpine和13-beta2-alpine。

因此，要按名称删除多个镜像，我们需要：

-   按仓库和标签列出所有镜像，例如postgres:13-beta2-alpine
-   [然后使用grep](https://www.linux.org/docs/man1/grep.html)命令通过正则表达式过滤这些输出行：^postgres:13-beta
-   最后，将这些行提供给docker image rm命令

让我们开始把它们放在一起。为了测试正确性，我们将只运行其中的前两个部分：

```shell
docker image ls --format '{{.Repository}}:{{.Tag}}' | grep '^postgres:13-beta'
```

在我们的测试机上，我们将得到：

```shell
postgres:13-beta2-alpine
postgres:13-beta1-alpine

```

现在鉴于此，我们可以将它添加到我们的docker image rm命令中：

```shell
docker image rm $(docker image ls --format '{{.Repository}}:{{.Tag}}' | grep '^postgres:13-beta')
```

和以前一样，我们只能在没有运行或停止的容器使用它们的情况下删除镜像。然后我们看到与上一节相同的镜像删除细节，并且docker system df显示我们在测试机器上返回到 7.8 GB 的 71 个镜像：

```shell
TYPE                TOTAL               SIZE
Images              71                  7.813GB
```

此镜像删除命令适用于Linux和Mac上的终端。[在Windows上，它需要Docker 工具箱](https://docs.docker.com/toolbox/toolbox_install_windows/)的“Docker Quickstart Terminal” 。将来，适用[于Windows的最新DockerDesktop](https://docs.docker.com/docker-for-windows/) 也可以 [在Windows10 上使用此Linux命令](https://code.visualstudio.com/blogs/2020/03/02/docker-in-wsl2)。

## 6. 按大小删除镜像

一种节省磁盘空间的好方法是先删除最大的Docker镜像。

但是，docker image ls也不能按大小排序。因此，我们将列出所有镜像并使用[sort](https://www.baeldung.com/linux/sort-command)命令对输出进行排序，以按大小查看镜像：

```shell
docker image ls | sort -k7 -h -r
```

我们的测试机输出是：

```shell
collabora/code   4.2.5.3         8ae6850294e5   3 weeks ago  1.28GB
nextcloud        19.0.1-apache   25b6e2f7e916   6 days ago   752MB
nextcloud        latest          6375cff75f7b   5 weeks ago  750MB
nextcloud        19.0.0-apache   5c44e8445287   7 days ago   750MB

```

接下来，我们将手动检查以找到我们要删除的内容。第三列的 ID 比分别位于第一列和第二列的仓库和标签更容易复制和粘贴。Docker 允许我们一次性删除多个镜像。

假设我们要删除nextcloud:latest和nextcloud:19.0.0-apache。简单地说，我们可以在我们的表中查看它们对应的 ID，并在我们的docker image rm命令中列出它们：

```shell
docker image rm 6375cff75f7b 5c44e8445287
```

和以前一样，我们只能删除未被任何容器使用的镜像，我们将看到通常的镜像删除细节。现在我们的测试机器上的 7.1 GB 的镜像减少到 69 张：

```shell
TYPE                TOTAL               SIZE
Images              69                  7.128GB
```

## 7. 按创建日期删除镜像

Docker 还可以按创建日期删除镜像。为此，我们将使用新的 docker image prune命令。与docker image rm不同，它旨在删除多个镜像甚至所有镜像。

让我们删除 2020 年 7 月 7 日之前创建的所有镜像：

```shell
docker image prune -a --force --filter "until=2020-07-07T00:00:00"
```

我们仍然只能删除未被任何容器使用的镜像，我们仍然会看到通常的镜像删除细节。此命令删除了测试机器上的两个镜像，因此我们在测试机器上有 67 个镜像和 5.7 GB：

```shell
TYPE                TOTAL               SIZE
Images              67                  5.686GB
```

另一种按创建日期删除镜像的方法是指定时间跨度而不是截止日期。假设我们要删除一周前的所有镜像：

```shell
docker image prune -a --force --filter "until=168h"
```

请注意，Docker 过滤器选项要求我们将该时间跨度转换为小时。

## 8. 修剪容器和镜像

[docker image prune](https://docs.docker.com/engine/reference/commandline/image_prune/)批量删除未使用的镜像。[它与docker container prune](https://docs.docker.com/engine/reference/commandline/container_prune/)携手并进，后者批量删除停止的容器。让我们从最后一个命令开始：

```shell
docker container prune
```

这将打印一条警告消息。我们必须输入y并按Enter继续：

```shell
WARNING! This will remove all stopped containers.
Are you sure you want to continue? [y/N] y
removed Containers:
1c3be3eba8837323820ecac5b82e84ab65ad6d24a259374d354fd561254fd12f

Total reclaimed space: 359.1MB
```

所以在测试机器上，这删除了一个停止的容器。

现在我们需要简单地讨论镜像关系。我们的Docker镜像扩展其他镜像以获得它们的功能，就像Java类扩展其他Java类一样。

[让我们看一下PostgreSQL beta 2 镜像的Dockerfile](https://github.com/docker-library/postgres/blob/bb0d97951918e6d281f510adb3896da433a52bc4/13/alpine/Dockerfile)的顶部 ，看看它扩展了什么镜像：

```shell
FROM alpine:3.12
```

所以 beta 2 镜像使用[alpine:3.12](https://hub.docker.com/_/alpine)。这就是为什么我们一开始拉取 beta 2 镜像时Docker 隐式下载alpine:3.12的原因。使用docker image ls看不到这些隐式下载的镜像。

现在假设我们删除了PostgreSQL 13 beta 2镜像。如果没有其他Docker镜像扩展alpine:3.12，那么Docker会认为alpine:3.12是一个所谓的“悬挂镜像”，这是一个曾经隐式下载的镜像，不再需要了。docker image prune删除这些悬空镜像：

```shell
docker image prune
```

此命令还要求我们输入y并按Enter继续：

```shell
WARNING! This will remove all dangling images.
Are you sure you want to continue? [y/N] y
Total reclaimed space: 0B
```

在测试机上，这并没有删除任何镜像。

docker image prune -a删除容器未使用的所有镜像，因此 如果我们没有任何容器(运行或未运行)，那么这将删除所有Docker镜像。这确实是一个危险的命令：

```shell
docker image prune -a
```

在测试机器上，这删除了所有镜像。docker system df确认既没有留下容器，也没有留下镜像：

```shell
TYPE                TOTAL               SIZE
Images              0                   0B
Containers          0                   0B

```

## 9. 强行删除容器和镜像

docker prune命令删除已停止的容器和悬挂镜像。但是如果我们想从我们的机器上删除所有的Docker镜像呢？为此，我们首先需要删除机器上运行的所有Docker容器，然后删除Docker镜像：

```shell
docker rm -f $(docker ps -qa)
```

此命令将删除所有容器。-f标志用于强制删除正在运行的Docker容器。

现在让我们使用docker rmi命令删除所有Docker镜像：

```shell
docker rmi -f $(docker images -aq)
```

docker images -qa将返回所有Docker镜像的镜像 ID。然后docker rmi命令将一个一个地删除所有镜像。同样，-f标志用于强制删除Docker镜像。

由于所有的Docker容器都已经从机器上移除了，我们也可以使用docker image prune命令来移除所有的Docker镜像。

## 10.总结

在本文中，我们首先学习如何删除单个Docker镜像。接下来，我们学习了如何按名称、大小或创建日期删除镜像。最后，我们学习了如何删除所有未使用的容器和镜像。

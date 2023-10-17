---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在本教程中，我们将了解为什么悬挂和未使用的镜像在[Docker](https://www.baeldung.com/ops/docker-guide)中很常见的一些原因。然后我们将研究一些删除它们的方法。

偶尔清理悬空和未使用的Docker镜像是一个好习惯，因为大量未使用的镜像会导致磁盘空间浪费。

## 2.Docker中未使用的对象

Docker 不会自动删除未使用的对象。相反，它将它们保留在磁盘上，直到我们明确要求它删除它们。一些未使用的对象是：

-   每个拉取的没有活动容器的镜像
-   每个处于停止状态的容器
-   与停止和删除的容器对应的卷
-   建立缓存

让我们探讨一下使用Docker如何导致不必要的镜像以及如何删除它们。

### 2.1. 悬挂的Docker镜像

当我们用具有相同名称和标签的新镜像覆盖它们时，就会创建悬挂镜像。

让我们看一个小例子，说明更新镜像将如何导致悬空镜像。下面是一个简单的Dockerfile：

```
FROM ubuntu:latest
CMD ["echo", "Hello World"]
```

让我们构建这个镜像：

```
docker build -t my-image .
```

我们可以通过运行以下命令来验证镜像是否已创建：

```
docker images
REPOSITORY   TAG       IMAGE ID       CREATED          SIZE
my-image     latest    7ed6e7202eca   32 seconds ago   72.8MB
ubuntu       latest    825d55fb6340   6 days ago       72.8MB
```

假设我们对Dockerfile添加一点更改：

```
FROM ubuntu:latest
CMD ["echo", "Hello, World!"]
```

让我们使用与之前相同的命令重建镜像并再次列出镜像：

```
docker images
REPOSITORY   TAG       IMAGE ID       CREATED              SIZE
my-image     latest    da6e74196f66   4 seconds ago        72.8MB
<none>       <none>    7ed6e7202eca   About a minute ago   72.8MB
ubuntu       latest    825d55fb6340   6 days ago           72.8MB
```

该构建创建了一个新的my-image镜像。正如我们所见，旧镜像仍然存在，但现在悬空了。它的名称和标签设置为<none>:<none>。

请注意，如果我们没有对Dockerfile进行更改，则在运行构建命令时不会重建镜像 。它将从缓存中重用。

### 2.2. 未使用的Docker镜像

未使用的镜像是没有与之关联的正在运行或已停止的容器的镜像。

未使用的镜像示例如下：

-   从注册表中提取但尚未在任何容器中使用的镜像
-   容器已被移除的任何镜像
-   标记有旧版本且不再使用的镜像
-   所有悬挂镜像

正如我们所见，未使用的镜像不一定是悬空的。我们将来可能会使用这些镜像，并且我们可能希望保留它们。但是，保留大量未使用的镜像会导致空间问题。

## 3.删除不需要的镜像

我们研究了悬挂和未使用的镜像在Docker中很常见的几个原因。现在，让我们看一下删除它们的几种方法。

### 3.1. 按 ID 或名称删除镜像

如果我们知道镜像 ID，我们可以使用docker rmi 命令删除镜像。

```
docker rmi 7ed6e7202eca
```

此命令将删除 ID 为7ed6e7202eca的镜像 (悬挂镜像)。让我们重新检查镜像：

```
docker images
REPOSITORY   TAG       IMAGE ID       CREATED          SIZE
my-image     latest    da6e74196f66   18 minutes ago   72.8MB
ubuntu       latest    825d55fb6340   6 days ago       72.8MB
```

或者，如果我们想删除一个特定的未使用的镜像，我们可以使用 带有镜像名称和标签的docker rmi命令：

```
docker rmi my-image:latest
```

### 3.2.Docker镜像修剪

如果我们不想找到悬空镜像并一一移除，我们可以使用[docker image prune](https://docs.docker.com/engine/reference/commandline/image_prune/)命令。此命令删除所有悬挂镜像。

如果我们还想删除未使用的镜像，我们可以使用 -a 标志。

让我们运行以下命令：

```
docker image prune -a
WARNING! This will remove all images without at least one container associated to them.
Are you sure you want to continue? [y/N] y
```

该命令将返回已删除的镜像 ID 列表和已释放的空间。

如果我们再次列出镜像，我们会发现没有镜像，因为我们没有运行任何容器。

### 3.3.Docker系统修剪

查找和删除所有未使用的对象可能很乏味。为了使事情更简单，我们可以使用 [docker system prune](https://docs.docker.com/engine/reference/commandline/system_prune/) 命令。

默认情况下，此命令将删除以下对象：

-   已停止的容器——处于停止状态的容器
-   至少一个容器未使用的网络
-   悬挂镜像
-   悬挂构建缓存——支持悬挂镜像的构建缓存

此外，我们可以添加 -a标志来删除所有未使用的容器、镜像、网络和整个构建缓存。当我们想要释放大量空间时，这很有用。

让我们看一个例子：

```
docker system prune -a
WARNING! This will remove:
  - all stopped containers
  - all networks not used by at least one container
  - all images without at least one container associated to them
  - all build cache

Are you sure you want to continue? [y/N]
```

## 4. 总结

在本教程中，我们研究了为什么悬挂和未使用的镜像在Docker中很常见。我们还研究了使用rmi、 image prune和 system prune命令删除它们的方法。

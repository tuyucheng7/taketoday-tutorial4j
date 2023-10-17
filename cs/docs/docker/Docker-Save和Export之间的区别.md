---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 简介

Docker生态系统有许多工具和功能，有时可能会让人感到困惑。在这篇简短的文章中，我们将了解 Docker保存和导出命令之间的区别。

## 2.Docker镜像与容器

要了解这两个命令的区别，首先要了解[Docker镜像和容器](https://www.baeldung.com/ops/docker-images-vs-containers)的区别。

Docker镜像是一个包含运行应用程序所需的所有文件的文件。这包括所有操作系统文件，以及应用程序代码和任何所需的支持库。

一个Docker容器就是一个已经启动的Docker镜像。容器本质上是一个正在运行的应用程序。容器像普通进程一样消耗内存和CPU资源，也可以访问文件系统并通过网络协议与其他容器通信。

Docker容器和镜像类似于Java类和对象。Java类是如何创建对象的蓝图，就像Docker镜像是创建容器的蓝图一样。就像一个类可以实例化为多个对象一样，一个Docker镜像可以用来启动多个容器。

考虑到这一点，我们可以仔细看看Docker save和export命令之间的区别。

## 3.码头保存

Docker save命令用于将Docker镜像保存到tar文件。此命令有助于将Docker镜像从一个注册表移动到另一个注册表或使用Linux [tar命令](https://www.baeldung.com/linux/tar-command)简单地检查镜像的内容。

默认情况下，该命令将tar文件内容打印到 STDOUT，因此典型用法是：

```shell
docker save IMAGE > /path/to/file.tar
```

请注意，我们还可以指定一个文件来打印内容，这样就不需要重定向了：

```shell
docker save -o /path/to/file.tar IMAGE 
```

在任何一种情况下， IMAGE参数都可以是以下两个值之一：

-   完全限定的镜像名称，例如“ghcr.io/baeldung/my-application:1.2.3”
-  Docker生成的镜像哈希，例如“c85146bafb83”

## 4.码头出口

Docker导出命令用于将Docker容器保存到tar文件中。这包括镜像文件以及容器运行时所做的任何更改。

语法与保存命令完全相同。就像save一样，export命令将输出发送到STDOUT，因此我们必须将其重定向到一个文件：

```shell
docker export CONTAINER > /path/to/file.tar
```

或者我们可以指定输出文件名：

```shell
docker export -o /path/to/file.tar CONTAINER
```

在这两种情况下，CONTAINER参数都可以是以下值之一：

-   容器名称，自动生成或在容器启动时指定
-  Docker引擎分配的唯一容器哈希

## 5. 差异

虽然这些命令在本质上是相似的，但也有一些差异需要注意。这两个命令都生成tar文件，但包含的信息不同。

save命令保留镜像层信息，包括所有历史记录和元数据。这使我们能够将tar文件完全导入到任何Docker注册表中，并使用它来启动新容器。

相反，导出命令不保留此信息。它包含与启动容器的镜像相同的文件，但没有历史记录和元数据。

此外，导出命令包括容器运行时所做的更改，例如新文件或修改后的文件。这意味着来自同一镜像的不同容器在导出它们时可能会生成不同的tar文件。

## 6. 总结

在本教程中，我们了解了Docker保存和导出命令之间的区别。虽然它们都具有相似的语法并创建tar文件，但它们有两个不同的用途。save命令用于镜像，而export命令用于容器。

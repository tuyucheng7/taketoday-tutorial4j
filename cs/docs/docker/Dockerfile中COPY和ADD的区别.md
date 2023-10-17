---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 简介

创建Dockerfile时，通常需要将文件从主机系统传输到Docker镜像中。这些可能是我们的应用程序在运行时需要的属性文件、本机库或其他静态内容。

Dockerfile规范提供了两种将文件从源系统复制到镜像中的方法：COPY和ADD指令。

在本文中，我们将研究它们之间的区别以及何时使用它们。

## 2. COPY和ADD的区别

乍一看，COPY和ADD指令看起来是一样的。它们具有相同的语法：

```plaintext
COPY <source> <destination>
ADD <source> <destination>
```

并且两者都将文件从主机系统复制到[Docker镜像](https://www.baeldung.com/ops/efficient-docker-images)中。

那么有什么区别呢？简而言之，ADD指令比COPY更有能力。

虽然功能相似，但ADD指令在两个方面更强大：

-   它可以处理远程 URL
-   它可以自动提取[tar文件](https://www.baeldung.com/linux/tar-command)

让我们更仔细地看看这些。

首先，ADD指令可以接受远程 URL 作为其源参数。另一方面，COPY指令只能接受本地文件。

请注意，使用ADD获取远程文件和复制通常并不理想。这是因为该文件会增加Docker镜像的整体大小。相反，我们应该使用[curl或wget](https://www.baeldung.com/linux/curl-wget)来获取远程文件并在不再需要时删除它们。

其次，ADD指令会自动将tar文件扩展到镜像文件系统中。虽然这可以减少构建镜像所需的Dockerfile步骤数，但并非在所有情况下都需要这样做。

请注意，仅当源文件在主机系统本地时才会发生自动扩展。

## 3. 何时使用ADD或COPY

根据[Dockerfile最佳实践指南，除非我们特别需要](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#add-or-copy)ADD的两个附加功能之一，否则我们应该始终更喜欢COPY而不是ADD。

如上所述，使用ADD将远程文件复制到Docker镜像会创建一个额外的层并增加文件大小。如果我们改用wget或curl，我们可以在之后删除文件，它们不会永久保留在Docker镜像中。

此外，由于ADD命令会自动扩展tar文件和某些压缩格式，它可能会导致意外文件被写入我们镜像中的文件系统。

## 4. 总结

在本快速教程中，我们了解了将文件复制到Docker镜像的两种主要方法：ADD和COPY。虽然功能相似，但大多数情况下首选COPY指令。这是因为ADD指令提供了额外的功能，应该谨慎使用并且只在需要时使用。

---
layout: post
title:  Docker Buildx简介
category: docker
copyright: docker
excerpt: Docker
---

## 1. 简介

[Docker](https://www.baeldung.com/ops/docker-dockerfile-docker-compose#docker)是一种流行的部署工具，使我们能够打包和运行应用程序。由于采用率很高，通过需要根据不同的要求扩展功能。因此，为了实现这一点，第三方使用[docker插件](https://docs.docker.com/engine/extend/legacy_plugins/)。

例如，如果我们希望数据跨不同主机持久化保存，我们使用volumes插件。另一个常用的插件是[Docker buildx](https://docs.docker.com/engine/reference/commandline/buildx/)，它通过使用BuildKit构建器扩展了[镜像](https://www.baeldung.com/ops/docker-images-vs-containers#docker-images)的构建能力。因此，使用该插件，我们可以为不同的平台和架构构建镜像。此外，它支持使用自定义上下文进行并行多阶段构建。

在本教程中，我们将介绍Docker buildx。

## 2.安装buildx

首先，要运行buildx ，我们需要[安装 Docker](https://www.baeldung.com/ops/docker-guide)。Docker buildx支持从[Docker 引擎](https://docs.docker.com/engine/)19.00 开始提供。

让我们首先检查我们的Docker版本：

```shell
$ docker --version
Docker version 19.03.8, build afacb8b

```

接下来，我们通过设置环境变量启用Docker实验性功能：

```shell
$ export DOCKER_CLI_EXPERIMENTAL=enabled

```

为了确保我们的设置在会话结束后仍然存在，我们将变量添加到$HOME/.bashrc中用于[Bash](https://www.gnu.org/software/bash/manual/html_node/index.html)。有了它，我们现在应该可以访问buildx：

```shell
$ docker buildx

Usage:  docker buildx COMMAND

Build with BuildKit

Management Commands:
  imagetools  Commands to work on images in registry

Commands:
  bake        Build from a file
  build       Start a build
  create      Create a new builder instance
  inspect     Inspect current builder instance
  ls          List builder instances
  rm          Remove a builder instance
  stop        Stop builder instance
  use         Set the current builder instance
  version     Show buildx version information

Run 'docker buildx COMMAND --help' for more information on a command.

```

这显示了常用命令和每个命令的语法。

## 3.使用buildx构建

buildx执行所有[Docker构建](https://docs.docker.com/engine/reference/commandline/build/)功能。因此，我们可以轻松地运行并执行它们。例如，指定目标平台、构建缓存和输出配置。除此之外， buildx还提供了额外的功能。

首先是同时为多个平台构建镜像的能力。其次，在单个[dockerfile](https://www.baeldung.com/ops/docker-dockerfile-docker-compose#dockerfile)中为较小的镜像进行多阶段构建。最后，在构建过程中自定义输入、参数或变量的能力。

让我们通过创建一个实例来深入研究一个例子：

```shell
$ docker buildx create --name ourbuilder
ourbuilder
```

这将创建一个名为ourbuilder的构建实例。

接下来，我们将其设置为活动目录：

```shell
$ docker buildx use ourbuilder
```

接下来，让我们创建一个dockerfile来运行一个简单的节点应用程序：

```shell
# Base image
FROM node:14-alpine

# Set working directory
WORKDIR /app

# Copy application files
COPY . .

# Install dependencies
RUN npm install --production

# Expose the port
EXPOSE 3000

# Start the application
CMD ["node", "app.js"]

```

这里我们使用node.js基础镜像并将工作目录设置为/app。然后我们将应用程序文件复制到容器中。然后我们安装所有依赖项，暴露端口 3000 并启动应用程序：

```shell
$ docker buildx build --platform linux/amd64,linux/arm64 -t ourapp:latest .
time="2023-06-01T07:13:20+03:00" level=warning msg="No output specified for docker-container driver.
  Build result will only remain in the build cache. To push result image into registry use --push or to load image into docker use --load"
#1 [internal] booting buildkit
#1 pulling image moby/buildkit:buildx-stable-1
#1 pulling image moby/buildkit:buildx-stable-1 73.2s done
#1 creating container buildx_buildkit_ourbuilder0
#1 creating container buildx_buildkit_ourbuilder0 2.1s done
#1 DONE 75.4s

#3 [internal] load .dockerignore
#3 transferring context: 0.0s
#3 transferring context: 2B 0.1s done
#3 DONE 0.3s

#2 [internal] load build definition from Dockerfile
#2 transferring dockerfile: 294B 0.0s done
#2 DONE 0.4s

#4 [linux/amd64 internal] load metadata for docker.io/library/node:14-alpin...
#4 DONE 4.7s
.... truncated ..... 

```

我们使用–platform标志指定目标平台。在这种情况下，我们的目标是 x86 (Linux/amd64) 和 ARM (Linux/arm64) 架构。我们还提供了标签-t ourapp:latest以使用名称ourapp和 latest 标签来标记构建的镜像。这 。指定构建上下文，即当前目录。

Docker buildx auto 处理多平台构建并为每个目标架构生成单独的镜像。

## 4. 总结

在本教程中，我们探索了Dockerbuildx，这是一种扩展Docker镜像构建和管理能力的工具。它通过支持并行构建、自定义构建上下文和多阶段构建来简化流程。

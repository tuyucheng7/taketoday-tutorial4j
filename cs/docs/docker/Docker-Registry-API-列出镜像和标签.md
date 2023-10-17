---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在本教程中，我们将探讨如何在远程[Docker](https://www.baeldung.com/tag/docker/)注册表中列出镜像以及如何获取镜像的标签。

这对于找出注册表中可用的特定镜像的哪些版本并决定使用哪个版本很有用。

## 2.Docker注册表 API

Docker 注册表提供了一个 API 来与注册表交互。该API 包含DockerCLI 在后台使用的各种[端点，用于执行拉取、推送和标记镜像等各种任务。](https://docs.docker.com/registry/spec/api/#detail)

我们还可以直接使用这些端点与注册表交互，而无需使用DockerCLI。

让我们看一下注册表 API 端点的格式：

```xml
/<api-version>/<repository-name>/<resource>/<params>

```

让我们检查一下这个端点的不同组件：

-   API 版本——API的版本。例如，当前版本是 v2 。
-   Repository(image) name –镜像的名称。如果它是嵌套仓库，名称还可以包含由斜杠分隔的路径。例如/ubuntu/nginx 或/redis。
-   资源——我们想要与之交互的 API 的细分。例如，清单 将与特定镜像的清单一起使用。
-   参数——这些是可选参数，可用于进一步优化操作。例如，manifests/latest将获取最新标签的清单。

基于上述规则，下面是端点的具体示例：

```shell
GET /v2/ubuntu/nginx/manifests/latest

```

## 3.DockerRegistry API V2

在撰写本文时，V2 是 Registry API 的当前版本。让我们探索如何使用它来列出来自远程注册表的镜像和标签。

假设我们在 URL https://my-registry.io 上部署了一个注册表。我们将使用curl来执行HTTP请求。

### 3.1. 清单镜像

要在注册表中列出镜像，我们可以使用 _/ catalog 端点：

```shell
$ curl -X GET my-registry.io/v2/_catalog
{"repositories":["centos","ubuntu"]}

```

我们应该注意，如果已启用，可能需要身份验证才能访问某些仓库。在这种情况下，我们可以使用-u 选项将用户名和密码作为参数传递给curl命令。

```shell
$ curl -u user:password -X GET my-registry.io/v2/_catalog 
{"repositories":["centos","ubuntu"]}
```

### 3.2. 分页列表镜像

有时，注册表会有大量镜像。在这种情况下，我们可以将n=<number of results>参数添加到 _/ catalog端点以获得分页响应：

```shell
$ curl -X GET my-registry.io/v2/_catalog?n=1
{"repositories":["centos"]}

```

响应现在只包含第一张图片。

我们可以使用对第一个请求的响应来获取下一页结果。这需要对curl命令进行两处更改：

-    _/ catalog端点的last参数 包含上一个请求中返回的最后一个镜像名称。
-   将新请求链接到前一个请求的标头。标头具有以下格式：

```shell
Link: <my-registry.io/v2/_catalog?n=1&last=centos>; rel="next"

```

URL 两边的括号是必需的。该 URL 与新请求的 URL 相同。rel =”next” 标头表示新请求是根据 [RFC 5988 的](https://tools.ietf.org/html/rfc5988)前一个请求的延续。

那么让我们提出下一个请求：

```shell
$ curl -H 'Link: <my-registry.io/v2/_catalog?n=1&last=centos>; rel="next"' -X GET "my-registry.io/v2/_catalog?n=1&last=centos"
{"repositories":["ubuntu"]}

```

响应返回包含一个镜像的下一页结果。

### 3.3. 上市标签

要列出[镜像的标签](https://www.baeldung.com/ops/docker-tag)，我们可以使用/tags/list 端点：

```shell
$ curl -X GET my-registry.io/v2/ubuntu/tags/list
{"name":"ubuntu","tags":["latest","16.04"]}

```

响应包含镜像的名称和与其关联的标签数组。

我们还可以使用与镜像列表相同的规则进行分页。

## 4.Docker注册表 API V1

Registry API v1 在Docker17.06 中被弃用并在Docker17.12 中被移除。但是，如果我们遇到在弃用之前托管的注册中心，我们可以使用 v1 API。让我们探讨一下在这种情况下我们的请求是如何变化的。

### 4.1. 清单镜像

v1 中没有直接端点来列出镜像。相反，我们可以使用docker search命令来搜索包含给定字符串的镜像：

```shell
$ docker search my-registry.io/centos

```

这将返回名称或描述中包含字符串“centos”的镜像列表。

如果我们不指定注册表，搜索将在默认注册表中执行。默认仓库是Docker Hub仓库。

例如，下图显示了在未指定任何仓库的情况下搜索术语“ubuntu”时的输出。

[![Docker 搜索 ubuntu 命令在终端中运行，结果显示与搜索查询匹配的所有镜像](https://www.baeldung.com/wp-content/uploads/2022/05/Screenshot-2022-05-22-at-11.13.01-AM.png)](https://www.baeldung.com/wp-content/uploads/2022/05/Screenshot-2022-05-22-at-11.13.01-AM.png)

### 4.2. 上市标签

列出标签的方法类似于 v2 API。但是，在这种情况下输出不同：

```shell
$ curl -X GET https://registry.hub.docker.com/v1/repositories/baeldung/mesos-marathon-demo/tags
[{"layer": "", "name": "32"}, {"layer": "", "name": "33"}, {"layer": "", "name": "34"}]

```

如我们所见，我们有一个对象数组，而不是单个标签数组。每个对象包括标签的名称和镜像的图层 ID。

如果需要，我们可以将响应转换为数组。为此，让我们将输出通过管道传输到[jq命令并](https://www.baeldung.com/linux/jq-command-json)解析 JSON：

```shell
$ curl -s GET https://registry.hub.docker.com/v1/repositories/baeldung/mesos-marathon-demo/tags | jq -r '[.[].name]'
[
  "32",
  "33",
  "34"
]

```

让我们了解 这里使用的jq命令和表达式：

-   -r标志 将输出作为原始文本返回。
-   表达式.[].name从curl输出返回每个对象的名称属性。
-   表达式周围的方括号 [] 表示我们要将输出收集到一个数组中。

最后，需要与curl命令一起使用的-s标志来抑制进度条输出。

## 5.总结

在本文中，我们探讨了如何使用Docker Registry API 列出远程注册表中的镜像和标签。

我们还研究了如何发送对镜像和标签的分页请求以及如何解析 JSON 响应。

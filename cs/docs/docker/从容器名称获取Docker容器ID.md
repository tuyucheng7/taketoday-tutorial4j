---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.docker.com/)是一种被广泛采用的容器化技术。可以在容器中运行各种应用程序。

虽然我们可以在启动容器时控制容器的名称，但 ID 是由Docker生成的。我们可能需要这个 ID 才能在Docker主机上执行某些操作，因此从名称中找到容器的 ID 是一个非常常见的需求。

在这个简短的教程中，我们将讨论根据名称查找容器 ID 的各种方法。

## 2. 设置示例

让我们创建一些容器作为示例：

```shell
$ docker container run --rm --name web-server-1 -d nginx:alpine
$ docker container run --rm --name web-server-10 -d nginx:alpine
$ docker container run --rm --name web-server-11 -d nginx:alpine
```

现在，让我们检查一下这些容器是否已经创建：

```shell
$ docker container ls -a
CONTAINER ID   IMAGE          COMMAND                  CREATED          STATUS          PORTS     NAMES
80f1bc1e7feb   nginx:alpine   "/docker-entrypoint.…"   36 seconds ago   Up 36 seconds   80/tcp    web-server-11
acdea168264a   nginx:alpine   "/docker-entrypoint.…"   36 seconds ago   Up 36 seconds   80/tcp    web-server-10
0cbfc6c17009   nginx:alpine   "/docker-entrypoint.…"   37 seconds ago   Up 36 seconds   80/tcp    web-server-1
```

正如我们所看到的，我们有三个使用nginx镜像处于运行状态的容器。

## 3.显示短容器ID

Docker 为每个容器分配一个唯一的 ID。完整的容器 ID 是 64 个字符的十六进制字符串。但是，在大多数情况下，此容器 ID 的短版本就足够了。短容器 ID 表示完整容器 ID 的前 12 个字符。 

[让我们使用Docker的容器 ls](https://docs.docker.com/engine/reference/commandline/container_ls/)子命令显示短容器 ID ：

```shell
$ docker container ls --all --quiet --filter "name=web-server-10"
acdea168264a
```

在此示例中，我们使用了–filter选项，它根据条件过滤输出。在我们的例子中，过滤是在容器的名称上完成的。

此外，我们还在命令中使用了–all和–quiet选项。-all选项是显示所有容器所必需的，因为默认情况下，它只显示正在运行的容器。–quiet选项仅用于显示容器 ID。

我们还可以结合使用[grep](https://www.baeldung.com/linux/grep-sed-awk-differences#grep)和[awk](https://www.baeldung.com/linux/awk-guide)命令来显示短容器 ID：

```shell
$ docker container ls --all | grep web-server-10 | awk '{print $1}'
acdea168264a
```

在这里，awk命令打印输出的第一列，它表示短容器 ID。

我们应该注意，grep和awk命令可能并非在所有平台上都可用。因此这种方法的可移植性较差。

## 4.显示完整容器ID

在大多数情况下，一个简短的容器 ID 就足够了。但是，在极少数情况下，需要完整的容器 ID 以避免歧义。

我们可以使用Docker的container ls child 命令来显示完整的容器 ID：

```shell
$ docker container ls --all --quiet --no-trunc --filter "name=web-server-10"
acdea168264a08f9aaca0dfc82ff3551418dfd22d02b713142a6843caa2f61bf
```

在这里，我们在命令中使用了–no-trunc选项。此选项会覆盖默认行为并禁用输出截断。

我们可以使用grep和awk命令的组合来获得相同的结果：

```shell
$ docker container ls --all --no-trunc | grep web-server-10 | awk '{print $1}'
acdea168264a08f9aaca0dfc82ff3551418dfd22d02b713142a6843caa2f61bf
```

Docker 的[container inspect](https://docs.docker.com/engine/reference/commandline/container_inspect/) child 命令以 JSON 格式显示有关容器的详细信息。我们可以用它来显示容器 ID：

```shell
$ docker container inspect web-server-10 --format={{.Id}}
acdea168264a08f9aaca0dfc82ff3551418dfd22d02b713142a6843caa2f61bf
```

在此示例中，我们使用了–format选项，它使用 Go 模板从 JSON 输出中提取Id字段。

## 5. 使用精确匹配显示容器 ID

我们不能在所有场景中都使用基本的grep或container ls子命令。例如，如果容器名称部分匹配，这种天真的方法将不起作用。让我们看一个例子。

让我们显示web-server-1容器的 ID ：

```shell
$ docker container ls --all --quiet --filter "name=web-server-1"
80f1bc1e7feb
acdea168264a
0cbfc6c17009
```

此处，输出显示三个容器 ID。发生这种情况是因为名称web-server-1部分匹配其他两个容器 – web-server-10和web-server-11。为了避免这种情况，我们可以使用正则表达式。

现在，让我们对容器名称使用正则表达式：

```shell
$ docker container ls --all --quiet --filter "name=^web-server-1$"
0cbfc6c17009
```

在此示例中，我们使用脱字符 (^)和美元 ($)符号强制与容器名称完全匹配。

以类似的方式，我们可以将-w选项与grep命令一起使用来强制执行完全匹配：

```shell
$ docker container ls --all | grep -w web-server-1 | awk '{print $1}'
0cbfc6c17009
```

## 六，总结

在本文中，我们了解了如何使用名称查找容器 ID。

首先，我们使用container ls child 命令以及grep和 awk命令的组合来显示短容器 ID。

然后我们使用–no-trunc选项和container inspect child 命令来显示完整的容器 ID。

最后，我们使用正则表达式来确保容器名称的精确匹配。

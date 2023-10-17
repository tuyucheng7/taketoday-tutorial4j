---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.docker.com/)是最流行的容器技术之一。它将应用程序及其依赖项打包在一起，并允许它在隔离的环境中运行。这种技术极大地增加了应用程序的可移植性。默认情况下，容器存储是短暂的，这对于有状态的应用程序来说并不理想。然而，我们可以使用[卷](https://www.baeldung.com/ops/docker-volumes)来克服这个限制。

在本教程中，我们将了解如何列出卷并显示有关它们的详细信息。

## 2. 设置示例

让我们创建一些带有属性的卷作为示例：

```shell
$ docker volume create dangling-volume
$ docker volume create narendra-volume --driver local --opt type=tmpfs --opt device=tmpfs
$ docker volume create labeled-volume --label owner=narendra
```

本教程的后一部分展示了我们如何使用这些属性来过滤卷。在下一节中，我们将了解如何验证我们的示例是否已正确设置。

## 3.显示基本卷信息

Docker 的卷 列表子命令将显示每个卷的简短摘要：

```shell
$ docker volume list
DRIVER    VOLUME NAME
local     dangling-volume
local     labeled-volume
local     narendra-volume
```

有时，我们只需要卷名。在这种情况下，我们可以使用–quiet选项：

```shell
$ docker volume list --quiet
dangling-volume
labeled-volume
narendra-volume
```

上述方法适用于少量卷，但当有很多卷要列出时可能会很乏味。在这种情况下，我们可以使用–filter选项。此选项允许我们根据某些属性执行过滤。让我们看几个例子。

### 3.1. 按名称过滤

我们可以使用名称过滤器列出名称包含特定字符串的卷。让我们列出名称包含“narendra”的卷：

```shell
$ docker volume list --filter name=narendra
DRIVER    VOLUME NAME
local     narendra-volume
```

### 3.2. 过滤 标签

标签用于标记资源。一个非常常见的场景是对符合特定条件的资源进行分组。例如，开发人员可以使用他们的用户名作为标签，这样他们就可以轻松识别他们自己创建的卷。让我们用一个例子来理解这一点：

```shell
$ docker volume list --filter label=owner=narendra
DRIVER    VOLUME NAME
local     labeled-volume
```

在此示例中，过滤器匹配带有标签owner=narendra 的卷，这是我们在设置示例卷时添加的。

### 3.3. 过滤 驱动程序名称

有时，我们需要根据驱动程序的名称来隔离卷。在这种情况下，我们可以使用驱动过滤器：

```shell
$ docker volume list --filter driver=local
DRIVER    VOLUME NAME
local     dangling-volume
local     labeled-volume
local     narendra-volume
```

### 3.4. 悬挂卷

卷会占用 docker 主机上的空间，因此最好清理所有未使用的卷。因为删除错误的卷会导致数据丢失，所以我们在删除它们之前必须格外小心。作为安全检查，我们可以通过使用悬挂过滤器来确保任何容器都没有引用卷。让我们看看实际效果。

首先，让我们创建一个使用卷的容器：

```shell
$ docker container run -d --name dangling-volume-demo -v narendra-volume:/tmpwork \
   -v labeled-volume:/data busybox
fa3f6fd8261293a92da7efbca4b04040a1838cf57b2703795324eb70a3d84143

```

在此示例中，容器使用我们三个卷中的两个：narendra-volume和labeled-volume。现在让我们确认第三卷是唯一一个显示为悬空/未使用的卷：

```shell
$ docker volume list --filter dangling=true
DRIVER    VOLUME NAME
local     dangling-volume
```

## 4.显示详细的卷信息

list子命令显示有关卷的非常有限的信息。有时这还不够。例如，如果我们知道卷的详细信息，调试就会容易得多。在这种情况下，我们可以使用volume inspect子命令来获取有关卷的其他信息。此命令显示卷的创建时间戳、挂载点等信息。让我们看一个示例：

```shell
$ docker volume inspect labeled-volume
[
    {
        "CreatedAt": "2022-05-30T22:34:53+05:30",
        "Driver": "local",
        "Labels": {
            "owner": "narendra"
        },
        "Mountpoint": "/var/lib/docker/volumes/labeled-volume/_data",
        "Name": "labeled-volume",
        "Options": {},
        "Scope": "local"
    }
]
```

## 5. 显示容器特定的卷信息

另一个非常常见的场景是查找给定容器使用的卷。开发人员通常需要此信息来调试应用程序。我们可以使用容器检查子命令获取有关特定容器卷的信息。 此命令返回有关Docker对象的低级信息，例如它们的状态、主机配置、网络设置等。我们可以指定 Mounts 部分来收集有关卷的安装信息：

```shell
$ docker container inspect --format '{{ json .Mounts }}' dangling-volume-demo | python3 -m json.tool
[
    {
        "Type": "volume",
        "Name": "narendra-volume",
        "Source": "/var/lib/docker/volumes/narendra-volume/_data",
        "Destination": "/tmpwork",
        "Driver": "local",
        "Mode": "z",
        "RW": true,
        "Propagation": ""
    },
    {
        "Type": "volume",
        "Name": "labeled-volume",
        "Source": "/var/lib/docker/volumes/labeled-volume/_data",
        "Destination": "/data",
        "Driver": "local",
        "Mode": "z",
        "RW": true,
        "Propagation": ""
    }
]
```

请注意，在此示例中，我们已将输出通过管道传输到[Python](https://www.python.org/)解释器，以使 JSON 输出更易于阅读。然而，这完全是可选的。

## 六，总结

在本文中，我们看到了列出 docker 卷的一些实际示例。首先，我们使用了list子命令。然后我们看到了如何使用过滤器。最后，我们使用inspect child 命令来显示有关卷的详细信息。

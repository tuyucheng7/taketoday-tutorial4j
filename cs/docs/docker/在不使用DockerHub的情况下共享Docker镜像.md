---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

假设我们需要共享一个存在于我们机器本地的[Docker镜像。](https://www.baeldung.com/ops/docker-guide)为了解决这个问题，[Docker Hub](https://hub.docker.com/)来拯救。

Docker Hub 是一个基于云的中央仓库，可以在其中存储Docker镜像。所以我们需要做的就是将我们的Docker镜像推送到DockerHub，之后，任何人都可以拉取相同的Docker镜像。

作为基于云的仓库，Docker Hub 需要额外的网络带宽来上传和下载Docker镜像。此外，随着镜像大小的增加，上传/下载镜像所需的时间也会增加。因此，这种共享Docker镜像的方法并不总是有用的。

在本教程中，我们将讨论一种无需使用Docker Hub即可共享Docker镜像的方法。当发送方和接收方连接到同一个专用网络时，这种方法被证明很方便。

## 2. 将Docker镜像保存为tar存档

假设有一个Docker镜像baeldung ，我们需要将其从机器 A 传输到机器 B。为此，首先，我们将使用 [docker save](https://docs.docker.com/engine/reference/commandline/save/)命令将Docker镜像转换为.tar文件：

```shell
$ docker save --output baeldung.tar baeldung
```

上面的命令将创建一个名为baeldung.tar的tar存档。 或者，我们也可以使用文件重定向来达到类似的效果：

```shell
$ docker save baeldung > baeldung.tar
```

docker save命令可以使用多个Docker镜像创建单个tar存档：

```shell
$ docker save -o ubuntu.tar ubuntu:18.04 ubuntu:16.04 ubuntu:latest
```

## 3.传输tar存档

我们创建的tar存档存在于机器 A 上。现在让我们将[baeldung.tar](https://www.baeldung.com/linux/transfer-files-ssh)[文件](https://www.baeldung.com/linux/transfer-files-ssh)[传输到机器 B。我们可以使用](https://www.baeldung.com/linux/transfer-files-ssh)scp或ftp等协议。

此步骤非常灵活，并且在很大程度上取决于机器 A 和机器 B 所在的环境。

## 4. 将tar存档加载到Docker镜像中

到目前为止，我们已经创建了Docker镜像的tar存档并将其移动到我们的目标机器 B。

现在，我们将使用[docker load](https://docs.docker.com/engine/reference/commandline/load/)命令从tar存档baeldung.tar创建实际的Docker镜像：

```shell
$ docker load --input baeldung.tar 
Loaded image: baeldung:latest
```

同样，我们还可以使用文件的重定向来转换tar存档：

```shell
$ docker load < baeldung.tar
Loaded image: baeldung:latest
```

现在让我们通过运行docker images 命令来验证镜像是否已成功加载：

```shell
$ docker images
baeldung                                        latest                            277bcd6563ce        About a minute ago       466MB
```

请注意，如果Docker镜像baeldung已经存在于目标机器上(在我们的示例中为机器 B)，则docker load命令会将现有镜像的标签重命名为空字符串 <none>：

```shell
$ docker load --input baeldung.tar 
cfd97936a580: Loading layer [==================================================>]  466MB/466MB
The image baeldung:latest already exists, renaming the old one with ID sha256:
  277bcd6563ce2b71e43b7b6b7e12b830f5b329d21ab690d59f0fd85b01045574 to empty string
```

## 5.缺点

使用这种方法，我们失去了重用Docker镜像缓存层的自由。因此，每次我们运行docker save命令时，它都会创建整个Docker镜像的tar存档。

另一个缺点是我们需要通过保存所有tar存档来手动维护Docker镜像版本。

因此，建议在测试环境中或当我们限制访问Docker Hub时使用此方法。

## 六，总结

在本教程中，我们了解了docker save和docker load命令以及如何使用这些命令传输Docker镜像。

我们还讨论了所涉及的缺点以及这种方法可以证明有效的理想情况。

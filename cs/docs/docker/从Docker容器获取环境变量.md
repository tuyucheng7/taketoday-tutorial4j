---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

Docker 是一个容器化平台，它将应用程序及其所有依赖项打包在一起。理想情况下，这些应用程序需要特定的环境才能启动。在Linux中，我们使用环境变量来满足这个要求。这些变量决定了应用程序的行为。

在本教程中，我们将学习检索在运行Docker容器时设置的所有环境变量。就像有多种方法可以[将环境变量传递给Docker容器](https://www.baeldung.com/ops/docker-container-environment-variables)一样，设置后也有不同的方法来获取这些变量。

在我们进一步深入之前，让我们先了解一下环境变量的必要性。

## 2. 了解Linux中的环境变量

环境变量是一组动态的键值对，可在系统范围内访问。这些变量可以帮助系统定位包、配置任何服务器的行为甚至使 bash 终端输出直观。

默认情况下，主机上存在的环境变量不会传递给Docker容器。原因是Docker容器应该与主机环境隔离。所以，如果我们想在Docker容器中使用一个环境，那么我们必须明确地设置它。

现在让我们看看从Docker容器内部获取环境变量的不同方法。

## 3.使用docker exec命令获取

出于演示目的，让我们首先运行一个[Alpine](https://hub.docker.com/_/alpine)Docker容器并将一些环境变量传递给它：

```shell
docker run -itd --env "my_env_var=baeldung" --name mycontainer alpine
9de9045b5264d2de737a7ec6ba23c754f034ff4f35746317aeefcea605d46e84
```

在这里，我们在名为mycontainer的Docker容器中 传递值为baeldung的my_env_var 。

现在让我们使用[docker exec](https://docs.docker.com/engine/reference/commandline/exec/)命令获取名为my_env_var的环境变量：

```shell
$ docker exec mycontainer /usr/bin/env
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
HOSTNAME=9de9045b5264
my_env_var=baeldung
HOME=/root

```

在这里，我们在Docker容器内执行/usr/bin/env实用程序。使用此实用程序，你可以查看Docker容器内设置的所有环境变量。请注意，我们的my_env_var也出现在输出中。

我们也可以使用下面的命令来达到类似的效果：

```shell
$ docker exec mycontainer /bin/sh -c /usr/bin/env
HOSTNAME=9de9045b5264
SHLVL=1
HOME=/root
my_env_var=baeldung
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
PWD=/

```

请注意，与之前的输出相比，有更多的环境变量。之所以如此，是因为这次我们是在/bin/sh二进制文件的帮助下执行命令。该二进制文件隐式设置了一些额外的环境变量。

此外，/bin/sh shell 并非必须存在于所有Docker镜像中。例如，在包含 /bin/bash shell的[centosDocker镜像中，](https://hub.docker.com/_/centos)我们将 使用以下 命令检索 环境 变量：  

```shell
$ docker run -itd --env "container_type=centos" --name centos_container centos
aee6f2718f18723906f7ab18ab9c37a539b6b2c737f588be71c56709948de9eb
$ docker exec centos_container bash -c /usr/bin/env
container_type=centos
HOSTNAME=aee6f2718f18
PWD=/
HOME=/root
SHLVL=1
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
_=/usr/bin/env
```

我们还可以使用docker exec命令获取单个环境变量的值：

```shell
$ docker exec mycontainer printenv my_env_var
baeldung

```

[printenv](https://man7.org/linux/man-pages/man1/printenv.1.html)是另一个在Linux中显示环境变量的命令行实用程序。在这里，我们将环境变量名称my_env_var作为参数传递给printenv。这将打印my_env_var的值。

这种方法的缺点是Docker容器必须处于运行状态才能检索环境变量。

## 4.使用docker inspect命令获取

现在让我们看看另一种在Docker容器处于停止状态时获取环境变量的方法。为此，我们将使用[docker inspect命令。](https://docs.docker.com/engine/reference/commandline/inspect/)

docker inspect提供所有Docker资源的详细信息。输出为 JSON 格式。因此，我们可以根据我们的要求过滤输出。

让我们操作docker inspect命令只显示容器的环境变量：

```shell
$ docker inspect mycontainer --format "{{.Config.Env}}"
[my_env_var=baeldung PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin]
```

在这里，我们使用–format选项从docker inspect输出中过滤了环境变量。同样，my_env_var出现在输出中。

我们还可以使用docker inspect命令获取单个环境变量：

```shell
$ docker inspect mycontainer | jq -r '.[].Config.Env[]|select(match("^my_env_var"))|.[index("=")+1:]'
baeldung
```

[jq](https://www.baeldung.com/linux/jq-command-json)是一个轻量级的 JSON 处理器，可以解析和转换 JSON 数据。在这里，我们将docker inspect的 JSON 输出传递 给jq命令。然后它搜索my_env_var变量并通过将其拆分为“=”来显示其值。

请注意，我们也可以将容器 ID 与docker exec和docker inspect命令一起使用。

与docker exec不同， docker inspect命令适用于已停止和正在运行的容器。

## 5.总结

在本文中，我们学习了如何从Docker容器中检索所有环境变量。我们首先讨论了Linux中环境变量的重要性。然后我们查看了docker exec和docker inspect命令来检索环境变量。

docker exec方法有一些限制，而 docker inspect命令在所有情况下都运行。

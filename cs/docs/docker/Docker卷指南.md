---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

Docker容器用于在隔离环境中运行应用程序。默认情况下，当容器停止时，容器内的所有更改都会丢失。如果我们想在运行之间保留数据，Docker卷和绑定挂载可以提供帮助。 

在本教程中，我们将了解Docker卷，以及如何管理它们并将它们连接到容器。

## 2. 什么是卷？

### 2.1.Docker文件系统

[docker 容器运行镜像](https://www.baeldung.com/docker-images-vs-containers)中定义的软件堆栈。镜像由一组只读层组成，这些层在称为联合文件系统的文件系统上工作。当我们启动一个新容器时，Docker 会在镜像层的顶部添加一个读写层，使容器能够像在标准Linux文件系统上一样[运行](https://www.baeldung.com/ops/docker-container-filesystem)。

因此，容器内的任何文件更改都会在读写层创建一个工作副本。但是，当容器停止或删除时，该读写层就会丢失。

![泊坞窗层系统](https://www.baeldung.com/wp-content/uploads/2021/01/layers.png)

我们可以通过运行写入然后读取文件的命令来尝试：

```shell
$ docker run bash:latest \ 
  bash -c "echo hello > file.txt && cat file.txt"
```

结果是：

```plaintext
hello
```

但是，如果我们仅使用输出文件内容的命令运行相同的镜像：

```shell
$ docker run bash:latest bash -c "cat file.txt" 
cat: can't open 'file.txt': No such file or directory
```

容器的第二次运行在一个干净的文件系统上运行，因此找不到该文件。

### 2.2. 绑定坐骑

Docker[绑定挂载](https://docs.docker.com/storage/bind-mounts/)是从容器到主机目录的高性能连接。它允许主机与容器共享自己的文件系统，可以设置为只读或读写。

这允许我们使用容器来运行我们不想安装在主机上的工具，但仍然可以使用主机的文件。例如，如果我们想为特定脚本使用自定义版本的bash ，我们可能会在bash容器中执行该脚本 ，并安装到我们当前的工作目录：

```shell
$ docker run -v $(pwd):/var/opt/project bash:latest \ 
  bash -c "echo Hello > /var/opt/project/file.txt"
```

-v选项可用于所有形式的挂载，并在本例中指定主机上的源 - $(pwd)输出中的工作目录 - 以及容器中的目标挂载点-/var/opt/项目。

运行此命令后，我们将在主机的工作目录中找到file.txt。这是一种在Docker容器调用之间提供持久文件的简单方法，尽管它在容器代表主机工作时最有用。

一个很好的用例是在Docker中执行各种版本的语言构建工具，以避免在开发人员机器上出现安装冲突。

我们应该注意到Windows bash shell有时需要$(pwd -W)以bash shell可以传递给Docker的形式提供工作目录。

### 2.3. Docker卷

绑定挂载使用主机文件系统，但[Docker卷](https://docs.docker.com/storage/volumes/)是Docker原生的。数据保存在连接到主机的存储上的某个地方——通常是本地文件系统。卷本身的生命周期比容器的生命周期长，允许它持续存在直到不再需要为止。卷可以在容器之间共享。

在某些情况下，卷处于主机无法直接使用的形式。

## 3.管理卷

[Docker 允许我们通过docker 卷](https://docs.docker.com/engine/reference/commandline/volume/)命令集管理卷。我们可以给卷一个明确的名称(命名卷)，或者允许Docker生成一个随机的名称(匿名卷)。

### 3.1. 创建卷

我们可以使用create子命令并将名称作为参数传递来创建卷：

```shell
$ docker volume create data_volume
data_volume
```

如果未指定名称，Docker 会生成一个随机名称：

```shell
$ docker volume create  
d7fb659f9b2f6c6fd7b2c796a47441fa77c8580a080e50fb0b1582c8f602ae2f
```

### 3.2. 上市卷

ls子命令显示Docker已知的所有卷：

```shell
$ docker volume ls
DRIVER 	VOLUME NAME
local 	data_volume
local   d7fb659f9b2f6c6fd7b2c796a47441fa77c8580a080e50fb0b1582c8f602ae2f
```

我们可以使用-f或–filter标志进行过滤，并传递key=value参数以获得更高的精度：

```shell
$ docker volume ls -f name=data
DRIVER 	VOLUME NAME
local 	data_volume
```

### 3.3. 检查体积

要显示一个或多个卷的详细信息，我们使用inspect子命令：

```shell
$ docker volume inspect ca808e6fd82590dd0858f8f2486d3fa5bdf7523ac61d525319742e892ef56f59
[
  {
    "CreatedAt": "2020-11-13T17:04:17Z",
    "Driver": "local",
    "Labels": null,
    "Mountpoint": "/var/lib/docker/volumes/ca808e6fd82590dd0858f8f2486d3fa5bdf7523ac61d525319742e892ef56f59/_data",
    "Name": "ca808e6fd82590dd0858f8f2486d3fa5bdf7523ac61d525319742e892ef56f59",
    "Options": null,
    "Scope": "local"
  }
]
```

我们应该注意到 卷的驱动程序描述了Docker主机如何定位卷。例如，卷可以通过 nfs 在远程存储上。在此示例中，卷位于本地存储中。

### 3.4. 删除卷

要单独删除一个或多个卷，我们可以使用rm子命令：

```shell
$ docker volume rm data_volume
data_volume
```

### 3.5. 修剪卷

我们可以使用prune子命令删除所有未使用的卷：

```shell
$ docker volume prune
WARNING! This will remove all local volumes not used by at least one container.
Are you sure you want to continue? [y/N] y
Deleted Volumes:
data_volume
```

## 4. 启动带有卷的容器

### 4.1. 使用 -v

正如我们在前面的例子中看到的，我们可以使用-v 选项启动一个绑定挂载的容器 ：

```shell
$ docker run -v $(pwd):/var/opt/project bash:latest \
  bash -c "ls /var/opt/project"
```

此语法还支持安装卷：

```shell
$ docker run -v data-volume:/var/opt/project bash:latest \
  bash -c "ls /var/opt/project"
```

由于我们的卷是空的，因此这不会列出挂载点的任何内容。但是，如果我们要在容器的一次调用期间写入卷：

```shell
$ docker run -v data-volume:/var/opt/project bash:latest \
  bash -c "echo Baeldung > /var/opt/project/Baeldung.txt"
```

然后我们随后使用安装了这个卷的容器将能够访问该文件：

```shell
$ docker run -v data-volume:/var/opt/project bash -c "ls /var/opt/project"
Baeldung.txt

```

-v选项 包含三个部分，以冒号分隔：

-   源目录或卷名
-   容器内的挂载点
-   (可选) ro如果挂载是只读的

### 4.2. 使用–mount选项

我们可能更愿意使用更不言自明的 –mount选项来指定我们希望挂载的卷：

```shell
$ docker run --mount \
  'type=volume,src=data-volume,\
  dst=/var/opt/project,volume-driver=local,\
  readonly' \ 
  bash -c "ls /var/opt/project"
```

–mount的输入是一串键值对，以逗号分隔。我们在这里设置：

-   type – as volume表示卷安装
-   src – 到卷的名称，尽管如果我们一直在进行绑定安装，这可能是一个源目录
-   dst – 作为容器中的目标挂载点
-   volume-driver——本 例中的本地驱动程序
-   readonly – 使这个挂载只读；我们可以选择 rw进行读/写

我们应该注意，如果卷不存在，上述命令也会创建一个卷。

### 4.3. 使用–volumes-from共享卷

我们应该注意到，将卷附加到容器会在容器和卷之间创建长期连接。即使容器已经退出，关系仍然存在。这允许我们使用一个已经退出的容器作为模板，将同一组卷安装到一个新的卷上。

假设我们 在带有数据卷挂载的容器中运行我们的echo脚本。稍后，我们可以列出我们使用过的[所有容器：](https://www.baeldung.com/ops/docker-list-containers)

```shell
$ docker ps -a
CONTAINER ID   IMAGE             COMMAND                  CREATED              STATUS                          PORTS      NAMES
4920602f8048   bash              "docker-entrypoint.s…"   7 minutes ago        Exited (0) 7 minutes ago                   exciting_payne
```

我们可以通过复制这个容器使用的卷来运行我们的下一个容器：

```shell
$ docker run --volumes-from 4920 \
  bash:latest \
  bash -c "ls /var/opt/project"
Baeldung.txt

```

在实践中–volumes-from通常用于链接正在运行的容器之间的卷。Jenkins 使用它在作为Docker容器运行的代理之间共享数据。

## 5.总结

在本文中，我们了解了Docker通常如何创建具有新文件系统的容器，以及绑定挂载和卷如何允许在容器生命周期之外长期存储数据。

我们看到了如何列出和管理Docker卷，以及如何通过命令行将卷连接到正在运行的容器。

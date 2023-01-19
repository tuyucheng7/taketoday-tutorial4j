## 1. 概述

在本教程中，我们介绍如何使用[docker-compose]()独立于其他容器重新构建容器。

## 2. 问题描述

**我们定义一个docker-compose.yml配置文件，其中包含两个容器配置**：一个引用最新的ubuntu镜像，另一个引用最新的alpine镜像。我们将为每个容器添加带有“tty:true”的伪终端，以防止容器在启动时直接退出：

```yaml
version: "3.9"
services:
    ubuntu:
        image: "ubuntu:latest"
        tty: true
    alpine:
        image: "alpine:latest"
        tty: true
```

现在让我们构建容器并启动它们，使用带有-d选项的docker-compose up命令让它们在后台运行：

```shell
$ docker-compose up -d

Container {folder-name}-alpine-1  Creating
Container {folder-name}-ubuntu-1  Creating
Container {folder-name}-ubuntu-1  Created
Container {folder-name}-alpine-1  Created
Container {folder-name}-ubuntu-1  Starting
Container {folder-name}-alpine-1  Starting
Container {folder-name}-alpine-1  Started
Container {folder-name}-ubuntu-1  Started
```

然后可以检查我们的容器是否按预期运行：

```shell
$ docker-compose ps
NAME                         COMMAND             SERVICE             STATUS              PORTS
{folder-name}-alpine-1   "/bin/sh"           alpine              running
{folder-name}-ubuntu-1   "bash"              ubuntu              running
```

接下来，我们将演示如何在不影响alpine容器的情况下重新构建和重启ubuntu容器。

## 3. 独立重新构建和重启一个容器

**将容器的名称添加到docker-compose up命令就可以了**。我们将添加build选项以在启动容器之前构建镜像，并添加force-recreate标志，因为我们没有更改镜像：

```shell
$ docker-compose up -d --force-recreate --build ubuntu
Container {folder-name}-ubuntu-1  Recreate
Container {folder-name}-ubuntu-1  Recreated
Container {folder-name}-ubuntu-1  Starting
Container {folder-name}-ubuntu-1  Started
```

可以看到，ubuntu容器被重新构建并重新启动，而对alpine容器没有任何影响。

## 4. 如果容器依赖于另一个容器

现在我们稍微修改一下docker-compose.yml文件，使ubuntu容器依赖于alpine容器：

```yaml
version: "3.9"
services:
    ubuntu:
        image: "ubuntu:latest"
        tty: true
        depends_on:
            - "alpine"
    alpine:
        image: "alpine:latest"
        tty: true
```

我们**停止以前的容器并使用新配置从头开始重新构建它们**：

```shell
$ docker-compose stop
Container {folder-name}-alpine-1  Stopping
Container {folder-name}-ubuntu-1  Stopping
Container {folder-name}-ubuntu-1  Stopped
Container {folder-name}-alpine-1  Stopped

$ docker-compose up -d
Container {folder-name}-alpine-1  Created
Container {folder-name}-ubuntu-1  Recreate
Container {folder-name}-ubuntu-1  Recreated
Container {folder-name}-alpine-1  Starting
Container {folder-name}-alpine-1  Started
Container {folder-name}-ubuntu-1  Starting
Container {folder-name}-ubuntu-1  Started
```

在这种情况下，我们需要**添加no-deps选项，来明确告诉docker-compose不要重启链接的容器**：

```shell
$ docker-compose up -d --force-recreate --build --no-deps ubuntu
Container {folder-name}-ubuntu-1  Recreate
Container {folder-name}-ubuntu-1  Recreated
Container {folder-name}-ubuntu-1  Starting
Container {folder-name}-ubuntu-1  Started
```

## 5. 总结

在本教程中，我们介绍了如何使用docker-compose重新构建容器。
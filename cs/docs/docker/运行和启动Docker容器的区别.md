---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.baeldung.com/ops/docker-guide) 提供了一个有用的 [CLI](https://docs.docker.com/engine/reference/commandline/cli/) 来与容器交互。在本教程中，我们将看到r un 和start命令，并通过一些实际示例突出显示它们的不同之处。

## 2.运行容器

Docker 的[运行](https://docs.docker.com/engine/reference/commandline/run/)命令是其创建和启动命令的组合。它在其特定镜像上创建一个容器，然后启动它。例如，让我们运行一个[Postgres](https://hub.docker.com/_/postgres)容器：

```shell
docker run --name postgres_example -p 5432:5432 -v /volume:/var/lib/postgresql/data -e POSTGRES_PASSWORD=my_password -d postgres

```

[让我们用docker ps](https://docs.docker.com/engine/reference/commandline/ps/)看看我们正在运行的容器：

```shell
CONTAINER ID   IMAGE     COMMAND                  CREATED          STATUS         PORTS                                       NAMES
52b7c79bfaa8   postgres  "docker-entrypoint.s…"   22 seconds ago   Up 20 seconds  0.0.0.0:5432->5432/tcp, :::5432->5432/tcp   postgres_example

```

如果我们使用[docker logs](https://docs.docker.com/engine/reference/commandline/logs/)，我们还可以查看有关已启动容器的更多信息，例如：

```plaintext
starting PostgreSQL 13.2
listening on IPv4 address "0.0.0.0", port 5432
listening on IPv6 address "::", port 5432
listening on Unix socket "/var/run/postgresql/.s.PGSQL.5432"
database system is ready to accept connections
```

## 3.启动一个容器

Docker 的 [启动](https://docs.docker.com/engine/reference/commandline/start/) 命令启动一个停止的容器。容器可能会因为不同的原因而停止——例如，当它消耗了太多内存并被主机操作系统杀死时。

为了演示这一点，让我们手动停止我们之前创建的容器：

```shell
docker stop 52b7c79bfaa8
```

在这种情况下，我们容器的运行列表将显示一个退出的容器：

```shell
CONTAINER ID   IMAGE     COMMAND                  CREATED          STATUS                    PORTS                                       NAMES
52b7c79bfaa8   postgres  "docker-entrypoint.s…"   2 minutes ago    Exited (0) 2 seconds ago  0.0.0.0:5432->5432/tcp, :::5432->5432/tcp   postgres_example

```

让我们也看看日志：

```plaintext
received fast shutdown request
aborting any active transactions
shutting down
database system is shut down
```

万一容器挂了，我们可能想使用docker start重新启动它：

```shell
docker start 52b7c79bfaa8
```

如果启动容器时没有发生错误，我们将返回到正在运行的容器状态。Docker 还提供了 [docker restart](https://docs.docker.com/engine/reference/commandline/restart/)命令，它将停止和启动合并为一个命令。

## 4. 总结

在本教程中，我们简要讨论了Docker中的运行 和 启动命令。

我们已经看到了使用docker run运行容器的示例。如果一个容器停止了，我们可以用docker start重新启动它 。

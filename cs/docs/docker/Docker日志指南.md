---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.baeldung.com/ops/docker-guide)是一个操作系统级的虚拟化平台，它允许我们在容器中托管应用程序。此外，它有助于分离应用程序和基础设施以实现快速软件交付。

[Docker容器](https://docs.docker.com/engine/reference/commandline/container/)生成的日志文件包含各种有用的信息。每当事件发生时，Docker容器都会创建日志文件。

Docker 将日志生成到 STDOUT 或 STDERR，包括日志来源、输出流数据和时间戳。调试和查找问题的根本原因可以使用日志文件来完成。

在本教程中，我们将研究以不同方式访问Docker日志。

## 2. 了解Docker日志

在Docker中，主要有两种类型的日志文件。Docker 守护进程日志提供了对Docker服务整体状态的洞察。Docker容器日志涵盖与特定容器相关的所有日志。

我们将主要探讨访问Docker容器日志的不同命令。我们将使用 docker [logs](https://docs.docker.com/engine/reference/commandline/logs/)命令并通过直接访问系统上的日志文件来检查容器日志。

日志文件对于调试问题很有用，因为它们提供了有关所发生情况的详细信息。通过分析Docker日志，我们可以更快地诊断和解决问题。

## 3.使用docker logs 命令

在我们继续之前，让我们先运行一个示例 PostgresDocker容器：

```shell
$ docker run -itd -e POSTGRES_USER=baeldung -e POSTGRES_PASSWORD=baeldung -p 5432:5432 -v /data:/var/lib/postgresql/data --name postgresql-baedlung postgres
Unable to find image 'postgres:latest' locally
latest: Pulling from library/postgres
214ca5fb9032: Pull complete 
...
95df4ec75c64: Pull complete 
Digest: sha256:2c954f8c5d03da58f8b82645b783b56c1135df17e650b186b296fa1bb71f9cfd
Status: Downloaded newer image for postgres:latest
bce34bb3c6175fe92c50d6e5c8d2045062c2b502b9593a258ceb6cafc9a2356a

```

为了说明，让我们检查一下postgresql-baedlung容器的containerId：

```shell
$ docker ps 
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
bce34bb3c617        postgres            "docker-entrypoint.s…"   12 seconds ago      Up 10 seconds       0.0.0.0:5432->5432/tcp   postgresql-baedlung
```

从上述命令的输出中我们可以看到，postgresql-baedlung正在运行，containerId 为“bce34bb3c617”。现在让我们探索用于监控日志的docker logs命令：

```shell
$ docker logs bce34bb3c617
2022-05-16 18:13:58.868 UTC [1] LOG:  starting PostgreSQL 14.2 (Debian 14.2-1.pgdg110+1)
  on x86_64-pc-linux-gnu, compiled by gcc (Debian 10.2.1-6) 10.2.1 20210110, 64-bit
2022-05-16 18:13:58.869 UTC [1] LOG:  listening on IPv4 address "0.0.0.0", port 5432
2022-05-16 18:13:58.869 UTC [1] LOG:  listening on IPv6 address "::", port 5432
```

在这里，日志包含带有时间戳的输出流数据。上面的命令不包含连续的日志输出。要查看容器的连续日志输出，我们需要在docker logs命令中使用“–follow”选项。

“–follow”选项是最有用的Docker选项之一，因为它允许我们监控容器的实时日志：

```shell
$ docker logs --follow  bce34bb3c617
2022-05-16 18:13:58.868 UTC [1] LOG:  starting PostgreSQL 14.2 (Debian 14.2-1.pgdg110+1)
  on x86_64-pc-linux-gnu, compiled by gcc (Debian 10.2.1-6) 10.2.1 20210110, 64-bit
...
2022-05-16 18:13:59.018 UTC [1] LOG:  database system is ready to accept connections
```

上述命令的缺点之一是它将包含从一开始就包含的所有日志。让我们检查命令以查看带有最近记录的连续日志输出：

```shell
$ docker logs --follow --tail 1 bce34bb3c617
2022-05-16 18:13:59.018 UTC [1] LOG:  database system is ready to accept connections 
```

我们还可以在docker log命令中使用“since”选项来查看特定时间的文件：

```shell
$ docker logs --since 2022-05-16  bce34bb3c617
2022-05-16 18:13:58.868 UTC [1] LOG:  starting PostgreSQL 14.2 (Debian 14.2-1.pgdg110+1)
  on x86_64-pc-linux-gnu, compiled by gcc (Debian 10.2.1-6) 10.2.1 20210110, 64-bit
...
2022-05-16 18:13:59.018 UTC [1] LOG:  database system is ready to accept connections
```

或者，我们也可以使用[docker container logs](https://docs.docker.com/engine/reference/commandline/container_logs/) 命令代替docker logs命令：

```shell
$ docker container logs --since 2022-05-16  bce34bb3c617
2022-05-16 18:13:58.868 UTC [1] LOG:  starting PostgreSQL 14.2 (Debian 14.2-1.pgdg110+1)
  on x86_64-pc-linux-gnu, compiled by gcc (Debian 10.2.1-6) 10.2.1 20210110, 64-bit
...
2022-05-16 18:13:59.018 UTC [1] LOG:  database system is ready to accept connections
```

在这里，我们可以从上面的输出中看到这两个命令的工作方式完全相同。docker 容器日志命令在较新版本中已弃用。

## 4.使用默认日志文件

Docker 以 JSON 格式存储所有 STDOUT 和 STDERR 输出。此外，还可以从主机监控所有实时Docker日志。默认情况下，Docker 使用json-file日志驱动程序将日志文件存储在主机上的专用目录中。日志文件目录位于运行容器的主机上的 /var/lib/docker/containers/<container_id>。

为了演示，让我们检查一下postgress-baeldung容器的日志文件 ：

```shell
$ cat /var/lib/docker/containers/bce34bb3c6175fe92c50d6e5c8d2045062c2b502b9593a258ceb6cafc9a2356a/
  bce34bb3c6175fe92c50d6e5c8d2045062c2b502b9593a258ceb6cafc9a2356a-json.log 
{"log":"\r\n","stream":"stdout","time":"2022-05-16T18:13:58.833312658Z"}
{"log":"PostgreSQL Database directory appears to contain a database; Skipping initialization\r\n","stream":"stdout","time":"2022-05-16T18:13:58.833360038Z"}
{"log":"\r\n","stream":"stdout","time":"2022-05-16T18:13:58.833368499Z"}
```

在上面的输出中，我们可以看到数据是 JSON 格式的。

## 5.清除日志文件

有时我们的系统磁盘空间不足，我们注意到Docker日志文件占用了大量空间。为此，我们首先需要找到日志文件，然后将其删除。此外，请确保清除日志文件不会影响正在运行的容器的状态。

下面是清除存储在主机上的所有日志文件的命令：

```shell
$ truncate -s 0 /var/lib/docker/containers/*/*-json.log 
```

请注意，上述命令不会删除日志文件。相反，它将删除日志文件中的所有内容。通过执行以下命令，我们可以删除与特定容器关联的日志文件：

```shell
$ truncate -s 0 /var/lib/docker/containers/dd207f11ebf083f97355be1ae18420427dd2e80b061a7bf6fb0afc326ad04b10/*-json.log 
```

在容器启动时，我们还可以使用docker run命令的“–log-opt max-size”和“ –log-opt max-file”选项从外部限制日志文件的大小：

```shell
$ docker run --log-opt max-size=1k --log-opt max-file=5 -itd -e POSTGRES_USER=baeldung -e POSTGRES_PASSWORD=baeldung -p 5432:5432
  -v /data:/var/lib/postgresql/data --name postgresql-baedlung postgres
3eec82654fe6c6ffa579752cc9d1fa034dc34b5533b8672ebe7778449726da32
```

现在，让我们检查/var/lib/docker/containers/3eec82654fe6c6ffa579752cc9d1fa034dc34b5533b8672ebe7778449726da32目录中的日志文件数量和日志文件大小：

```shell
$ ls -la
total 68
drwx------. 4 root root 4096 May 17 02:06 .
drwx------. 5 root root  222 May 17 02:07 ..
drwx------. 2 root root    6 May 17 02:02 checkpoints
-rw-------. 1 root root 3144 May 17 02:02 config.v2.json
-rw-r-----. 1 root root  587 May 17 02:06 3eec82654fe6c6ffa579752cc9d1fa034dc34b5533b8672ebe7778449726da32-json.log
-rw-r-----. 1 root root 1022 May 17 02:06 3eec82654fe6c6ffa579752cc9d1fa034dc34b5533b8672ebe7778449726da32-json.log.1
-rw-r-----. 1 root root 1061 May 17 02:06 3eec82654fe6c6ffa579752cc9d1fa034dc34b5533b8672ebe7778449726da32-json.log.2
-rw-r-----. 1 root root 1056 May 17 02:06 3eec82654fe6c6ffa579752cc9d1fa034dc34b5533b8672ebe7778449726da32-json.log.3
-rw-r-----. 1 root root 1058 May 17 02:06 3eec82654fe6c6ffa579752cc9d1fa034dc34b5533b8672ebe7778449726da32-json.log.4
-rw-r--r--. 1 root root 1501 May 17 02:02 hostconfig.json
-rw-r--r--. 1 root root   13 May 17 02:02 hostname
-rw-r--r--. 1 root root  174 May 17 02:02 hosts
drwx------. 2 root root    6 May 17 02:02 mounts
-rw-r--r--. 1 root root   69 May 17 02:02 resolv.conf
-rw-r--r--. 1 root root   71 May 17 02:02 resolv.conf.hash
```

在这里，我们可以看到创建了五个日志文件，每个日志文件的大小最大为 1 kb。如果我们删除一些日志文件，在这种情况下，我们将生成一个具有相同日志文件名的新日志。

我们也可以在/etc/docker/daemon.json文件中提供log max-size和max-file的配置。让我们看看 daemon.json 文件的配置：

```json
{
    "log-driver": "json-file",
    "log-opts": {
        "max-size": "1k",
        "max-file": "5" 
    }
}
```

在这里，我们在 daemon.json 中提供了相同的配置，重要的是，所有新容器都将使用此配置运行。更新daemon.json文件后，我们需要重启Docker服务。

## 6. 将Docker容器日志重定向到单个文件

默认情况下，Docker容器日志文件存储在/var/lib/docker/containers/<containerId>目录中。此外，我们还可以将Docker容器日志重定向到其他文件。

为了说明，让我们看看重定向容器日志的命令：

```shell
$ docker logs -f containername &> baeldung-postgress.log &
```

在这里，在上面的命令中，我们将所有实时日志重定向到baeldung-postgress.log文件。此外，我们使用&在后台运行此命令，因此它会一直运行直到明确停止。

## 七、总结

在本教程中，我们学习了监控容器日志的不同方法。首先，我们查看了docker 日志，以及用于监控实时日志的docker 容器日志命令。后来，我们使用默认的容器日志文件监视日志。

最后，我们研究了日志文件的清除和重定向。简而言之，我们研究了监控和截断日志文件。

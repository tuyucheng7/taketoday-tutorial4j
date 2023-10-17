---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在本文中，我们将回顾如何使用Docker来管理数据库。

在第一章中，我们将介绍在本地计算机上安装数据库。然后我们将发现数据持久性如何跨容器工作。

最后，我们将讨论在Docker生产环境中实施数据库的可靠性。

## 2. 在本地运行Docker镜像

### 2.1. 从一个标准的Docker镜像开始

首先，我们必须安装[Docker Desktop](https://www.docker.com/get-started)。[然后，我们应该从Docker Hub](https://hub.docker.com/)找到我们数据库的现有镜像。找到它后，我们将从页面右上角选择docker pull命令。

在本教程中，我们将使用 PostgreSQL，所以命令是：

```shell
$docker pull postgres
```

下载完成后，docker run命令将在Docker容器中创建一个正在运行的数据库。对于 PostgreSQL，必须使用-e选项指定POSTGRES_PASSWORD环境变量：

```shell
$docker run -e POSTGRES_PASSWORD=password postgres
```

接下来，我们将测试我们的数据库容器连接。

### 2.2. 将Java项目连接到数据库

让我们尝试一个简单的测试。我们将使用 JDBC 数据源将本地Java项目连接到数据库。连接字符串应使用本地主机上的默认PostgreSQL端口5432：

```java
jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password
```

错误应该通知我们端口未打开。事实上，数据库正在侦听来自容器网络内部的连接，而我们的Java项目在它之外运行。

要修复它，我们需要将容器端口映射到我们的本地主机端口。我们将为PostgreSQL使用默认端口 5432：

```shell
$docker run -p 5432:5432 -e POSTGRES_PASSWORD=password postgres
```

现在连接正常了，我们应该可以使用我们的 JDBC 数据源了。

### 2.3. 运行 SQL 脚本

现在，我们可以从 shell 连接到我们的数据库，例如，运行初始化脚本。

首先，让我们找到正在运行的容器 ID：

```shell
$docker ps
CONTAINER ID   IMAGE      COMMAND                  CREATED          STATUS          PORTS                    NAMES
65d9163eece2   postgres   "docker-entrypoint.s…"   27 minutes ago   Up 27 minutes   0.0.0.0:5432->5432/tcp   optimistic_hellman
```

然后，我们将运行带有交互式-it选项的docker exec命令以在容器内运行 shell：

```shell
$docker exec -it 65d9163eece2 bash
```

最后，我们可以使用命令行客户端连接到数据库实例并粘贴我们的 SQL 脚本：

```sql
root@65d9163eece2:/# psql -U postgres
postgres=#CREATE DATABASE TEST;
CREATE TABLE PERSON(
  ID INTEGER PRIMARY KEY,
  FIRST_NAME VARCHAR(1000),
  LAST_NAME VARCHAR(1000)
);
...
```

例如，如果我们要加载一个大的转储文件，我们必须避免复制粘贴。我们可以直接从主机运行 import 命令，而不是使用docker exec命令：

```shell
$docker exec 65d9163eece2 psql -U postgres < dump.sql
```

## 3. 使用Docker卷持久化数据

### 3.1. 为什么我们需要卷？

只要我们使用相同的容器，我们的基本设置就可以工作，每次我们需要重新启动时都会停止/启动 docker 容器。如果我们再次使用docker run，将创建一个新的空容器，我们将丢失数据。事实上，Docker 默认将数据保存在一个临时目录中。

现在，我们将学习如何修改此卷映射。

### 3.2.Docker卷设置

第一项任务是检查我们的容器以查看我们的数据库使用了哪个卷：

```shell
$docker inspect -f "{{ .Mounts }}" 65d9163eece2
[{volume f1033d3 /var/lib/docker/volumes/f1033d3/_data /var/lib/postgresql/data local true }] 
```

我们可以看到卷f1033d3已将容器目录/var/lib/postgresql/data映射到在主机文件系统中创建的临时目录/var/lib/docker/volumes/f1033d3/_data 。

我们必须通过将-v选项添加到我们在第 2.1 章中使用的docker run命令来修改此映射：

```shell
$docker run -v C:\docker-db-volume:/var/lib/postgresql/data -e POSTGRES_PASSWORD=password postgres
```

现在，我们可以在C:docker-db-volume目录下看到创建的数据库文件。[我们可以在这篇专门的文章](https://www.baeldung.com/ops/docker-volumes)中找到高级卷配置。

因此，每次我们使用docker run命令时，数据都会随着不同的容器执行而持久化。

此外，我们可能希望在团队成员之间或跨不同环境共享配置。我们可以使用Docker Compose文件，它每次都会创建新的容器。在这种情况下，卷是强制性的。

下一章将介绍Docker数据库在生产环境中的具体使用。

## 4. 在生产中使用 Docker

Docker Compose非常适合作为无状态服务共享配置和管理容器。如果服务失败或无法处理工作负载，我们可以配置Docker Compose自动创建新容器。这对于为 REST 后端构建生产集群非常有用，REST 后端在设计上是无状态的。

然而，数据库是有状态的，它们的管理更复杂：让我们回顾一下不同的上下文。

### 4.1. 单实例数据库

假设我们正在构建一个非关键环境，用于测试或生产，它可以容忍停机时间(在部署、备份或故障期间)。

在这种情况下，我们不需要高可用集群，我们可以简单地使用Docker Compose作为单实例数据库：

-   我们可以使用一个简单的卷来存储数据，因为容器将在同一台机器上执行
-   [我们可以使用全局模式](https://docs.docker.com/compose/compose-file/compose-file-v3/#mode)限制它一次运行一个容器

让我们看一个极简主义的工作示例：

```yaml
version: '3'
services:       
  database:
    image: 'postgres'
    deploy:
      mode: global
    environment:
      - POSTGRES_PASSWORD=password
    ports:
      - "5432:5432"
    volumes:
      - "C:/docker-db-volume:/var/lib/postgresql/data"
```

使用此配置，我们的产品将一次只创建一个容器，并重用C:docker-db-volume目录中的数据文件。

但是，在此配置中进行定期备份更为重要。如果出现配置错误，该目录可能会被容器删除或损坏。

### 4.2. 复制数据库

现在让我们假设我们的生产环境很关键。

在这种情况下，[Docker Swarm](https://docs.docker.com/engine/swarm/)和[Kubernetes](https://kubernetes.io/fr/)等编排工具对无状态容器很有用：它们提供垂直和水平集群，具有负载平衡、故障转移和自动缩放功能。

不幸的是，由于我们的数据库容器是有状态的，因此这些解决方案不提供卷复制机制。

另一方面，构建自制配置是危险的，因为它会导致严重的数据丢失。例如：

-   使用NFS 或 NAS 等共享存储的卷不能保证在另一个实例中重新启动数据库时不会丢失数据
-   在master-slave集群上，让一个Docker编排选出多个master节点是一个常见的错误，会导致数据损坏

到目前为止，我们的不同选择是：

-   数据库不使用Docker，实现数据库专用或硬件复制机制
-   不要将Docker用于数据库，并订阅平台即服务解决方案，如 OpenShift、Amazon AWS 或 Azure
-   使用Docker特定的复制机制，如[KubeDB](https://kubedb.com/)和[Portworx](https://portworx.com/)

## 5.总结

在本文中，我们回顾了适用于开发、测试和非关键生产的基本配置。

最后，我们得出总结，Docker 在高可用性环境中使用时存在缺点。因此，应该避免或与专门针对数据库集群的解决方案结合使用。

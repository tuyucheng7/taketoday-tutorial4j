---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在本教程中，我们将学习如何使用Docker[安装](https://www.baeldung.com/ops/docker-guide)[PostgreSQL](https://www.postgresql.org/)。通常，我们使用公共Docker镜像运行Docker容器。同样，我们可以从Docker Hub拉取PostgreSQL数据库服务器的预配置Docker镜像。我们还将演示如何在Docker上安装、配置和运行 PostgreSQL。

[首先，我们将使用PostgreSQL 公共镜像](https://hub.docker.com/_/postgres)运行带有PostgreSQL数据库的Docker容器。然后我们将创建一个自定义的Dockerfile以在Docker容器中安装PostgreSQL服务器。我们还将学习如何使用Docker容器备份和恢复数据库。

现在让我们深入了解如何使用PostgreSQL数据库运行Docker容器。

## 2. 了解PostgreSQL数据库

在继续运行PostgreSQL数据库的Docker容器之前，让我们先了解一下PostgreSQL数据库。PostgreSQL 是一个开源的 RDMS，类似于 MySQL。它是一个面向对象的数据库，但我们可以处理结构化和非结构化数据。

PostgreSQL 数据库引擎可在各种平台上运行，包括Windows、Mac OS X和Linux。它还提供高级数据类型和性能优化功能来存储和扩展复杂的数据库工作负载。

## 3. 使用公共镜像设置 PostgreSQL

要使用Docker运行 PostgreSQL，我们首先需要拉取[Docker Hub](https://hub.docker.com/_/postgres)上可用的postgres公共镜像：

```shell
$ docker pull postgres
Using default tag: latest
latest: Pulling from library/postgres
1fe172e4850f: Pull complete 
...
c08147da7b54: Pull complete 
Digest: sha256:ab0be6280ada8549f45e6662ab4f00b7f601886fcd55c5976565d4636d87c8b2
Status: Downloaded newer image for postgres:latest
docker.io/library/postgres:latest
```

在上面的命令中，我们拉取了postgres 最新的稳定镜像。我们还可以使用以下命令拉取特定版本的postgres镜像：

```shell
$ docker pull postgres:14.2
14.2: Pulling from library/postgres
Digest: sha256:e3d8179786b8f16d066b313f381484a92efb175d1ce8355dc180fee1d5fa70ec
Status: Downloaded newer image for postgres:14.2
docker.io/library/postgres:14.2
```

现在我们将使用postgres:latest镜像和以下命令运行Docker容器：

```shell
$ docker run -itd -e POSTGRES_USER=baeldung -e POSTGRES_PASSWORD=baeldung -p 5432:5432 -v /data:/var/lib/postgresql/data --name postgresql postgres
5aeda2b20a708296d22db4451d0ca57e8d23acbfe337be0dc9b526a33b302cf5
```

上面的命令使用环境变量POSTGRES_USER和POSTGRES_PASSWORD来设置PostgreSQL 数据库的用户名和密码。默认情况下，PostgreSQL 数据库运行在 5432端口上。我们在docker run命令中使用“-p 5432:5432”暴露了主机上的5432端口。

为了备份数据，我们还将/var/lib/postgresql/data目录挂载到postgres容器宿主机的/data目录下。

psql是一个命令行实用程序，用于以交互方式访问PostgreSQL数据库。现在让我们使用psql连接数据库：

```shell
$ PGPASSWORD=baeldung psql -U baeldung 
```

为了从所有数据库中获取列表，我们将使用命令l ：

```shell
$ PGPASSWORD=baeldung psql -U baeldung -c '\l' 
                                    List of databases
    Name    |   Owner    | Encoding |  Collate   |   Ctype    |     Access privileges     
------------+------------+----------+------------+------------+---------------------------
 baeldung   | baeldung   | UTF8     | en_US.utf8 | en_US.utf8 | 
 postgres   | baeldung   | UTF8     | en_US.utf8 | en_US.utf8 | 
 template0  | baeldung   | UTF8     | en_US.utf8 | en_US.utf8 | =c/baeldung            +
            |            |          |            |            | baeldung=CTc/baeldung
 template1  | baeldung   | UTF8     | en_US.utf8 | en_US.utf8 | =c/baeldung            +
            |            |          |            |            | baeldung=CTc/baeldung
(4 rows)
```

在上面的输出中，我们可以获得PostgreSQL服务器上存在的所有数据库的详细信息。

## 4. 使用定制的Dockerfile设置 PostgreSQL

我们还可以通过创建自定义的Dockerfile来设置PostgreSQL数据库服务器。在这里，我们将创建一个Dockerfile，其中包含使用 CentOS 作为基础镜像安装Postgres所需的所有命令：

```shell
FROM centos:7
COPY startUpScript.sh /
RUN yum install -y epel-release maven wget \
&& yum clean all \
&& yum install -y  https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm \
&& yum install -y postgresql11-server postgresql11-contrib \
&& chown root /startUpScript.sh \
&& chgrp root /startUpScript.sh \
&& chmod 777 /startUpScript.sh
CMD ["/bin/bash","-c","/startUpScript.sh && tail -f /dev/null"]
```

在上面的Dockerfile中，我们使用startUpScript.sh在成功安装后启动PostgreSQL数据库服务器。让我们看看startUpScript.sh文件：

```shell
#!/bin/bash
su -l postgres -c /usr/pgsql-11/bin/initdb
su -l postgres -c "/usr/pgsql-11/bin/pg_ctl -D /var/lib/pgsql/11/data -l /tmp/pg_logfile start"
createdb -U postgres baeldung
```

在startUpScript.sh中，我们首先初始化了PostgreSQL数据库，然后创建了虚拟数据库baeldung。

## 5.在Docker上安装pgAdmin

到目前为止，PostgreSQL 服务器处于活动状态并在5432端口上运行。现在我们将安装[pgAdmin](https://www.pgadmin.org/)，这是一个基于 Web 的用户界面工具，用于管理PostgreSQL数据库和服务。 pgAdmin 可用于在PostgreSQL数据库上运行 SQL 查询。

要从 UI 执行所有查询，我们可以使用 pgAdmin。为此，我们需要 使用以下命令拉取[pgAdmin镜像：](https://hub.docker.com/r/dpage/pgadmin4/)

```shell
$ docker pull dpage/pgadmin4:latest
latest: Pulling from dpage/pgadmin4
40e059520d19: Pull complete 
...
6d23acfae6ef: Pull complete 
Digest: sha256:f820e5579857a7210599f998c818777a2f6f39172b50fbeb2faaa1a70413e9ac
Status: Downloaded newer image for dpage/pgadmin4:latest
docker.io/dpage/pgadmin4:latest
```

为了演示，让我们使用以下命令运行容器：

```shell
$ docker run --name pgadmin-baeldung -p 5051:80 -e "PGADMIN_DEFAULT_EMAIL=user@baeldung.com" -e "PGADMIN_DEFAULT_PASSWORD=baeldung" -d dpage/pgadmin4
```

在上面的命令中，我们将PGADMIN_DEFAULT_EMAIL和PGADMIN_DEFAULT_PASSWORD作为环境变量提供给pgadmin-baeldung容器：

[![Docker注册服务器](https://www.baeldung.com/wp-content/uploads/2022/05/docker-register-server.png)](https://www.baeldung.com/wp-content/uploads/2022/05/docker-register-server.png)

我们可以使用 pgAdmin GUI 轻松访问PostgreSQL数据库。为了访问数据库，我们必须使用 pgAdmin 建立到 Postgres 服务器的连接。我们可以通过登录 pgAdmin 来完成此操作。

## 6.备份和恢复数据

在本节中，我们将学习如何使用Docker命令备份和恢复PostgreSQL中的数据。

首先，为了备份数据，我们将创建一个虚拟数据库baeldung和一个表baeldungauthor。

```shell
$ createdb -h localhost -p 5432 -U baeldung baeldung
```

创建表的命令如下：

```shell
CREATE TABLE baeldungauthor (
   AUTHOR_ID INT PRIMARY KEY     NOT NULL,
   AUTHOR_NAME           TEXT    NOT NULL,
   AUTHOR_AGE            INT     NOT NULL,
   AUTHOR_LEVEL        INT     NOT NULL
);
```

让我们列出数据库中创建的表：

```shell
psql -U baeldung -d baeldung -c "\d"
              List of relations
 Schema |      Name      | Type  |   Owner    
--------+----------------+-------+------------
 public | baedlungauthor | table | baeldung
(1 row)
```

现在我们将使用以下命令获取表baeldungauthor的模式详细信息：

```shell
psql -U baeldung -d baeldung -c "\d baedlungauthor"
              Table "public.baedlungauthor"
    Column    |  Type   | Collation | Nullable | Default 
--------------+---------+-----------+----------+---------
 author_id    | integer |           | not null | 
 author_name  | text    |           | not null | 
 author_age   | integer |           | not null | 
 author_level | integer |           | not null | 
Indexes:
    "baedlungauthor_pkey" PRIMARY KEY, btree (author_id)
```

到目前为止，我们已经创建了一个数据库和一个表。现在让我们看看为Docker容器备份数据库的命令：

```shell
$ docker exec -t postgresql pg_dumpall -c -U baeldung > dump.sql
```

在上面的命令中，我们使用pg_dumpall来备份baeldung数据库。它是用于备份数据库的标准PostgreSQL工具。我们提供了数据库服务器的用户名来访问权限。

现在让我们检查恢复数据库的命令：

```shell
$ cat dump.sql | docker exec -i postgresql psql -U baeldung
```

在这里，简而言之，我们 使用psql命令恢复了baeldung数据库的所有表。

## 七、总结

在本文中，我们学习了如何使用Docker容器安装PostgreSQL数据库。我们探索了拉取、设置和运行 Postgres 的Docker容器的每个步骤。

此外，我们还讨论了访问PostgreSQL数据库服务器的两种方式。首先，我们演示了 pgAdmin 访问运行在Docker容器上的PostgreSQL数据库服务器。然后我们使用psql对PostgreSQL中的数据库执行查询。

接下来，我们使用Docker Hub上的 Postgres 公共镜像运行带有PostgreSQL数据库的Docker容器。我们还创建了自定义的Dockerfile以在Docker容器中安装PostgreSQL服务器。

最后，我们深入探讨了使用Docker容器对PostgreSQL数据库中的数据进行备份和恢复。

---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

容器因其众多优势而成为 IT 行业最热门的话题。组织正在以惊人的速度为其业务采用基于容器的解决方案。[据 451 Research 称](https://451research.com/451-research-says-application-containers-market-will-grow-to-reach-4-3bn-by-2022)，应用程序容器市场在未来几年将增长四倍。

今天，我们甚至拥有 MySQL、[MongoDB](https://www.baeldung.com/linux/mongodb-as-docker-container)、[PostgreSQL](https://www.baeldung.com/ops/postgresql-docker-setup)等容器化形式的数据库。但是，本文将探索设置和运行MySQL容器的选项。首先，我们将备份现有的MySQL数据库。接下来，我们将构建一个 YAML 形式的容器配置，并使用docker-compose运行它，docker-compose 是一个开源工具包，用于将一堆应用程序容器组合在一起。

事不宜迟，让我们深入了解它的细节。

## 2.构建MySQL容器配置

在本节中，我们将使用docker-compose工具构建MySQL容器。但是，YAML 还使用Dockerfile中的镜像作为当前路径中的基本配置。

### 2.1. Docker Compose

首先，让我们创建带有版本和服务标签的 YAML 文件。我们在YAML 文件的版本标签下定义文件格式版本。MySQL服务使用我们在上下文中定义的Dockerfile中的镜像信息。

此外，我们还指示该工具使用在.env文件中定义为环境变量的默认参数。最后，ports标签将绑定容器和主机端口 3306。让我们看看我们用来启动MySQL服务的docker-compose YAML 文件的内容：

```plaintext
# cat docker-compose.yml
version: '3.3'
services:
### MySQL Container
  mysql:
    build:
      context: /home/tools/bael/dung/B015
      args:
        - MYSQL_DATABASE=${MYSQL_DATABASE}
        - MYSQL_USER=${MYSQL_USER}
        - MYSQL_PASSWORD=${MYSQL_PASSWORD}
        - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
    ports:
      - "${MYSQL_PORT}:3306"
```

### 2.2.Dockerfile创建

docker-compose在内部使用指定路径下的Dockerfile来构建镜像并为MySQL设置环境。我们的Dockerfile从 DockerHub 下载镜像并使用定义的变量启动容器：

```plaintext
# cat Dockerfile
FROM mysql:latest

MAINTAINER baeldung.com

RUN chown -R mysql:root /var/lib/mysql/

ARG MYSQL_DATABASE
ARG MYSQL_USER
ARG MYSQL_PASSWORD
ARG MYSQL_ROOT_PASSWORD

ENV MYSQL_DATABASE=$MYSQL_DATABASE
ENV MYSQL_USER=$MYSQL_USER
ENV MYSQL_PASSWORD=$MYSQL_PASSWORD
ENV MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD

ADD data.sql /etc/mysql/data.sql

RUN sed -i 's/MYSQL_DATABASE/'$MYSQL_DATABASE'/g' /etc/mysql/data.sql
RUN cp /etc/mysql/data.sql /docker-entrypoint-initdb.d

EXPOSE 3306
```

现在，让我们看一下下面Dockerfile片段中给出的所有指令：

-   FROM – 一个有效的Dockerfile以FROM语句开头，它描述了镜像名称和版本标签。在我们的例子中，我们使用带有最新标签的mysql镜像。
-   MAINTAINER – 将作者信息设置为通过docker inspect可见的容器元数据。
-   RUN – 在mysql镜像之上执行命令，随后形成一个新层。结果镜像被提交并用于Dockerfile中定义的后续步骤。
-   ARG——在构建时传递变量。在这里，我们将四个用户变量作为构建参数传递。
-   ENV——我们使用$符号来表示Dockerfile中的环境变量。在上面的代码片段中，我们使用了四个变量。
-  ADD– 在构建期间，它将文件添加到容器中以备将来使用。
-   EXPOSE – 使服务在Docker容器之外可用。

### 2.3. 设置环境

另外，我们可以在当前路径下创建一个环境变量文件为.env 。该文件包含撰写文件中涉及的所有变量：

```plaintext
# cat .env
MYSQL_DATABASE=my_db_name
MYSQL_USER=baeldung
MYSQL_PASSWORD=pass
MYSQL_ROOT_PASSWORD=pass
MYSQL_PORT=3306
```

### 2.4. MySQL备份文件

为了演示，让我们从现有数据库表中进行备份。在这里，我们通过data.sql文件自动将相同的Customers表导入到我们的MySQL容器中。

下面，我们使用SELECT查询展示了表数据，该查询从请求的表中获取数据：

```plaintext
mysql> select * from Customers;
+--------------+-----------------+---------------+-----------+------------+---------+
| CustomerName | ContactName     | Address       | City      | PostalCode | Country |
+--------------+-----------------+---------------+-----------+------------+---------+
| Cardinal     | Tom B. Erichsen | Skagen 21     | Stavanger | 4006       | Norway  |
| Wilman Kala  | Matti Karttunen | Keskuskatu 45 | Helsinki  | 21240      | Finland |
+--------------+-----------------+---------------+-----------+------------+---------+
2 rows in set (0.00 sec)
```

作为MySQLRDBMS 包的一部分，mysqldump实用程序用于将数据库中的所有数据备份到文本文件中。使用带有内联参数的简单命令，我们可以快速备份MySQL表：

-   -u: MySQL用户名
-   -p：MySQL密码

```plaintext
# mysqldump -u [user name] –p [password] [database_name] > [dumpfilename.sql]

# mysqldump -u root -p my_db_name > data.sql
Enter password:
```

在较高级别，备份文件将删除所选数据库中名为Customers的任何表，并将所有备份数据插入其中：

```plaintext
# cat data.sql
-- MySQL dump 10.13  Distrib 8.0.26, for Linux (x86_64)
...
... output truncated ...
...
DROP TABLE IF EXISTS `Customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Customers` (
  `CustomerName` varchar(255) DEFAULT NULL,
...
... output truncated ...
...
INSERT INTO `Customers` VALUES ('Cardinal','Tom B. Erichsen','Skagen 21','Stavanger','4006','Norway'),('Wilman Kala','Matti Karttunen','Keskuskatu 45','Helsinki','21240','Finland');
/*!40000 ALTER TABLE `Customers` ENABLE KEYS */;
UNLOCK TABLES;
...
... output truncated ...
...
-- Dump completed on 2022-07-28  1:56:09
```

但是，数据库的创建或删除不在创建的转储文件中进行管理。我们将在data.sql文件中添加以下代码片段，如果数据库不存在，它会创建数据库。它通过管理数据库和表来完善这个圈子。最后，它还通过USE命令使用创建的数据库：

```plaintext
--
-- Create a database using `MYSQL_DATABASE` placeholder
--
CREATE DATABASE IF NOT EXISTS `MYSQL_DATABASE`;
USE `MYSQL_DATABASE`;
```

目前，目录结构如下所示：

```plaintext
# tree -a
.
├── data.sql
├── docker-compose.yml
├── Dockerfile
└── .env
```

## 3. 启动MySQL服务器容器

现在，我们都准备好通过docker-compose启动一个容器。要启动MySQL容器，我们需要执行docker-compose up。

当我们浏览输出行时，我们可以看到它们在MySQL镜像之上的每个步骤中形成了新层。

随后，它还会创建数据库并加载data.sql文件中指定的数据：

```plaintext
# docker-compose up
Building mysql
Sending build context to Docker daemon  7.168kB
Step 1/15 : FROM mysql:latest
 ---> c60d96bd2b77
Step 2/15 : MAINTAINER baeldung.com
 ---> Running in a647bd02b91f
Removing intermediate container a647bd02b91f
 ---> fafa500c0fac
Step 3/15 : RUN chown -R mysql:root /var/lib/mysql/
 ---> Running in b37e1d5ba079

...
... output truncated ...
...

Step 14/15 : RUN cp /etc/mysql/data.sql /docker-entrypoint-initdb.d
 ---> Running in 34f1d9807bad
Removing intermediate container 34f1d9807bad
 ---> 927b68a43976
Step 15/15 : EXPOSE 3306
 ---> Running in defb868f4207
Removing intermediate container defb868f4207
 ---> 6c6f435f52a9
Successfully built 6c6f435f52a9
Successfully tagged b015_mysql:latest
Creating b015_mysql_1 ... done
Attaching to b015_mysql_1
mysql_1  | 2022-07-28 00:49:03+00:00 [Note] [Entrypoint]: Entrypoint script for MySQL Server 8.0.26-1debian10 started.

...
... output truncated ...
...

mysql_1  | 2022-07-28 00:49:16+00:00 [Note] [Entrypoint]: Creating database my_db_name
mysql_1  | 2022-07-28 00:49:16+00:00 [Note] [Entrypoint]: Creating user baeldung
mysql_1  | 2022-07-28 00:49:16+00:00 [Note] [Entrypoint]: Giving user baeldung access to schema my_db_name
mysql_1  |
mysql_1  | 2022-07-28 00:49:16+00:00 [Note] [Entrypoint]: /usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/data.sql
...
... output truncated ...
...
```

我们可以使用-d选项以分离模式运行容器：

```plaintext
# docker-compose up -d
Building mysql
Sending build context to Docker daemon  7.168kB
Step 1/15 : FROM mysql:latest
 ---> c60d96bd2b77
...
... output truncated ...
...
Step 15/15 : EXPOSE 3306
 ---> Running in 958e1d4af340
Removing intermediate container 958e1d4af340
 ---> c3516657c4c8
Successfully built c3516657c4c8
Successfully tagged b015_mysql:latest
Creating b015_mysql_1 ... done
#
```

## 4.MySQL客户端就绪

必须安装[客户端](https://www.baeldung.com/linux/mysql-client-utilities)才能轻松访问MySQL服务器。根据我们的需要，我们可以在主机或任何其他机器或容器上安装客户端，这些机器或容器具有与服务器容器的 IP 可达性：

```plaintext
$ sudo apt install mysql-client -y
Reading package lists... Done
Building dependency tree
Reading state information... Done
mysql-client is already the newest version (5.7.37-0ubuntu0.18.04.1).
...
... output truncated ...
...
```

现在，我们提取MySQL客户端的安装路径和版本：

```plaintext
$ which mysql
/usr/bin/mysql
$ mysql --version
mysql  Ver 14.14 Distrib 5.7.37, for Linux (x86_64) using  EditLine wrapper
```

## 5.服务器客户端通信

我们可以使用客户端应用程序访问已部署的MySQL服务器。在本节中，我们将了解如何通过客户端访问MySQL服务器。

让我们使用docker ps命令查看创建的容器id和状态：

```plaintext
# docker ps | grep b015_mysql
9ce4da8eb682   b015_mysql                "docker-entrypoint.s…"   21 minutes ago   Up 21 minutes         0.0.0.0:3306->3306/tcp, :::3306->3306/tcp, 33060/tcp                                                                    b015_mysql_1
```

接下来，让我们获取容器 IP 地址以使用已安装的客户端服务访问数据库。如果我们发出docker inspect命令，我们将看到有关容器的 JSON 格式的详细信息。我们还可以从生成的 JSON 中选择任何字段。在这里，我们从range.NetworkSettings.Networks -> IPAddress获取 IP 地址：

```plaintext
# docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' 9ce4da8eb682
172.19.0.2
```

然后我们可以使用客户端使用配置的主机和端口信息登录MySQLServer：

```plaintext
# mysql -h 172.17.0.2 -P 3306 --protocol=tcp -u root -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
...
... output truncated ...
...
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| my_db_name         |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)
mysql> use my_db_name
...
... output truncated ...
...

Database changed

```

在这里，我们可以看到从data.sql文件中自动恢复了数据：

```plaintext
mysql> select * from Customers;
+--------------+-----------------+---------------+-----------+------------+---------+
| CustomerName | ContactName     | Address       | City      | PostalCode | Country |
+--------------+-----------------+---------------+-----------+------------+---------+
| Cardinal     | Tom B. Erichsen | Skagen 21     | Stavanger | 4006       | Norway  |
| Wilman Kala  | Matti Karttunen | Keskuskatu 45 | Helsinki  | 21240      | Finland |
+--------------+-----------------+---------------+-----------+------------+---------+
2 rows in set (0.00 sec)
```

现在，让我们尝试向现有数据库表中添加更多行。我们将使用INSERT查询向表中添加数据：

```plaintext
mysql> INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country) VALUES ('White Clover Markets', 'Karl Jablonski', '305 - 14th Ave. S. Suite 3B', 'Seattle', '98128', 'USA');
Query OK, 1 row affected (0.00 sec)
```

我们也成功地在恢复的表中插入了一个新行。恭喜！让我们看看结果：

```plaintext
mysql> select * from Customers;
+----------------------+-----------------+-----------------------------+-----------+------------+---------+
| CustomerName         | ContactName     | Address                     | City      | PostalCode | Country |
+----------------------+-----------------+-----------------------------+-----------+------------+---------+
| Cardinal             | Tom B. Erichsen | Skagen 21                   | Stavanger | 4006       | Norway  |
| Wilman Kala          | Matti Karttunen | Keskuskatu 45               | Helsinki  | 21240      | Finland |
| White Clover Markets | Karl Jablonski  | 305 - 14th Ave. S. Suite 3B | Seattle   | 98128      | USA     |
+----------------------+-----------------+-----------------------------+-----------+------------+---------+
3 rows in set (0.00 sec)
```

或者，MySQL服务器容器随MySQL客户端安装一起提供。但是，它只能在容器内用于任何测试目的。现在，让我们登录到Docker容器并尝试使用默认的MySQL客户端访问MySQL服务器。

docker exec命令有助于使用容器 ID 登录到正在运行的容器。选项-i保持 STDIN 打开，-t将分配伪 TTY，最后，末尾的/bin/bash使我们进入 BASH 提示符：

```plaintext
# docker exec -it 9ce4da8eb682 /bin/bash
root@9ce4da8eb682:/# mysql -h localhost -u root -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
...
... output truncated ...
...
mysql>
```

## 六，总结

总之，我们讨论了使用docker-compose启动MySQL服务器容器的步骤。它还自动从备份文件中恢复数据库和表。此外，我们还访问了恢复的数据并执行了一些 CRUD 操作。

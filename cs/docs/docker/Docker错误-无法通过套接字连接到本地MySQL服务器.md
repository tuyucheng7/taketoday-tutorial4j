---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在当今的技术时代，组织必须快速发布应用程序才能吸引和留住业务。这使得团队能够以更低的成本更快地构建和配置部署环境。然而，容器化技术可以拯救构建轻量级基础设施。

[本文将阐明在基于容器](https://www.baeldung.com/ops/docker-container-shell)的环境中部署和连接到MySQL服务器的方法。

现在，让我们深入了解它的本质。

## 2. MySQL容器部署

首先，让我们了解一下MySQL容器部署涉及的步骤。基本上，MySQL遵循客户端-服务器架构模型。在这里，服务器是一个带有数据库的容器镜像，而客户端用于访问主机上的数据库。

我们将部署工作流程分为三个部分。

### 2.1.MySQL服务器部署

现在，让我们将MySQL服务器实例引入 Docker。我们可以简单地基于从Docker Hub拉取的MySQL镜像构建一个容器。[在选择从Docker Hub](https://hub.docker.com/_/mysql)拉取哪个版本的镜像时，我们应该考虑两点：

-   官方镜像标记——这些是来自MySQL开发团队的更安全和精选的镜像。
-   最新标签——除非我们对MySQL版本有任何保留，否则我们可以使用仓库中可用的最新版本。

[让我们使用docker pull](https://docs.docker.com/engine/reference/commandline/pull/)命令从Docker Hub中拉取官方的MySQL镜像：

```plaintext
$ docker pull mysql:latest
latest: Pulling from library/mysql
f003217c5aae: Pull complete
…
… output truncated …
…
70f46ebb971a: Pull complete
db6ea71d471d: Waiting
c2920c795b25: Downloading [=================================================> ]  105.6MB/107.8MB
26c3bdf75ff5: Download complete
```

通常，镜像是以清单[文件](https://www.baeldung.com/ops/docker-images-vs-containers)中描述的有序形式紧密耦合的不同层。我们的docker pull命令将从blob 存储中获取镜像层，并使用清单文件自动创建镜像：

```plaintext
…
… output truncated …
…
4607fa685ac6: Pull complete
Digest: sha256:1c75ba7716c6f73fc106dacedfdcf13f934ea8c161c8b3b3e4618bcd5fbcf195
Status: Downloaded newer image for mysql:latest
docker.io/library/mysql:latest
```

如上所示，捆绑的镜像获得一个哈希码以供将来参考。

事不宜迟，让我们运行容器。[docker run](https://docs.docker.com/engine/reference/commandline/run/)命令通常在镜像层之上创建可写容器层。我们需要使用-name参数提供容器名称，并使用带有最新标签的MySQL镜像。[此外，我们将通过环境变量](https://www.baeldung.com/ops/docker-container-environment-variables)MYSQL_ROOT_PASSWORD设置MySQL服务器密码。在我们的例子中，密码设置为“baeldung”。

最后，-d选项帮助我们将容器作为守护进程运行。输出抛出另一个哈希码用于未来的容器管理：

```plaintext
$ docker run --name bael-mysql-demo -e MYSQL_ROOT_PASSWORD=baeldung -d mysql:latest
fedf880ce2b690f9205c7a37f32d75f669fdb1da2505e485e44cadd0b912bd35
```

我们可以通过[ps](https://docs.docker.com/engine/reference/commandline/ps/)命令查看一个主机中所有[正在运行的容器](https://www.baeldung.com/ops/docker-list-containers)：

```plaintext
$ docker ps
CONTAINER ID   IMAGE          COMMAND                  CREATED          STATUS          PORTS                 NAMES
fedf880ce2b6   mysql:latest   "docker-entrypoint.s…"   17 seconds ago   Up 16 seconds   3306/tcp, 33060/tcp   bael-mysql-demo
```

### 2.2. MySQL客户端安装

必须安装客户端才能轻松访问MySQL服务器。根据我们的需要，我们可以将客户端安装在主机上，也可以安装在与服务器容器具有 IP 可达性的任何其他机器或容器上：

```plaintext
$ sudo apt install mysql-client -y
Reading package lists... Done
Building dependency tree
Reading state information... Done
mysql-client is already the newest version (5.7.37-0ubuntu0.18.04.1).
…
… output truncated …
…
```

现在，让我们启用MySQL客户端安装路径和版本的提取：

```plaintext
$ which mysql
/usr/bin/mysql
$ mysql --version
mysql  Ver 14.14 Distrib 5.7.37, for Linux (x86_64) using  EditLine wrapper
```

### 2.3. 建立沟通

接下来，让我们使用安装好的客户端登录服务器。传统上，我们使用带有用户名和密码的MySQL命令进行服务器登录。但是，它不适用于基于容器的解决方案：

```plaintext
$ mysql -u root -p
ERROR 2002 (HY000): Can't connect to local MySQL server through socket '/var/run/mysqld/mysqld.sock' (2)
```

如上所示，我们最终会出现套接字错误。在这里，重要的是要了解MySQL服务器是一个容器，而不是简单地安装在主机上。正如上一节中强调的那样，容器是具有自己的计算资源、网络和存储的轻量级服务器。

[inspect](https://docs.docker.com/engine/reference/commandline/inspect/)命令有助于 为MySQL服务器实例分配[IP 地址：](https://www.baeldung.com/ops/docker-network-information)

```plaintext
$ docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' bael-mysql-demo
172.17.0.2
```

让我们在客户端的主机选项中提供上述 IP 地址，默认端口号和协议类型为 TCP：

```plaintext
$ mysql -h 172.17.0.2 -P 3306 --protocol=tcp -u root -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
…
… output truncated …
…
mysql>
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
4 rows in set (0.00 sec)

mysql> exit
Bye
```

恭喜，我们已经成功登录到MySQL服务器了！

## 3.总结

综上所述，我们已经详细了解了部署MySQL服务器容器、在主机上安装MySQL客户端以及最后使用容器信息在它们之间建立连接的步骤。

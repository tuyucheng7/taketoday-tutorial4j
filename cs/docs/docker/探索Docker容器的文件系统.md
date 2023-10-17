---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

当我们使用Docker时，有时我们需要检查容器内的配置或日志文件。

在本快速教程中，我们将了解如何检查[Docker容器](https://www.baeldung.com/docker-images-vs-containers)的文件系统以帮助我们解决此类情况。

## 2. 互动探索

如果我们获得对它们的 shell 访问权限，我们可以交互地探索大多数容器的文件系统。

### 2.1. 使用Shell访问运行容器

让我们使用带有-it选项的docker run命令直接通过 shell 访问启动一个容器：

```shell
$ docker run -it alpine
/# ls -all
...
-rwxr-xr-x    1 root     root             0 Mar  5 13:21 .dockerenv
drwxr-xr-x    1 root     root           850 Jan 16 21:52 bin
drwxr-xr-x    5 root     root           360 Mar  5 13:21 dev
drwxr-xr-x    1 root     root           508 Mar  5 13:21 etc
drwxr-xr-x    1 root     root             0 Jan 16 21:52 home
....

```

在这里，我们以交互模式启动了Alpine Linux容器并连接到它的 shell。

但是，如果我们想探索不直接属于Linux发行版的东西，会发生什么情况呢？

```shell
$ docker run -it cassandra
 ... 
INFO [MigrationStage:1] 2020-03-05 13:44:36,734 - Initializing system_auth.resource_role_permissons_index 
INFO [MigrationStage:1] 2020-03-05 13:44:36,739 - Initializing system_auth.role_members 
INFO [MigrationStage:1] 2020-03-05 13:44:36,743 - Initializing system_auth.role_permissions 
INFO [MigrationStage:1] 2020-03-05 13:44:36,747 - Initializing system_auth.roles 
INFO [main] 2020-03-05 13:44:36,764 - Waiting for gossip to settle... 
...
```

Cassandra docker 容器带有一个默认的启动命令，它运行 Cassandra。结果，我们不再连接到 shell。

相反，我们只看到标准输出填充了应用程序的日志消息。

但是，我们可以绕过默认的启动命令。

让我们将/bin/bash附加参数传递给docker run命令：

```shell
$ docker run -it cassandra /bin/bash
root@a71f71e98598:/# ls -all
total 4
...
-rwxr-xr-x   1 root root    0 Mar  5 13:30 .dockerenv
drwxr-xr-x   1 root root  920 Aug 14  2019 bin
drwxr-xr-x   1 root root    0 Mar 28  2019 boot
drwxr-xr-x   5 root root  360 Mar  5 13:30 dev
lrwxrwxrwx   1 root root   34 Aug 14  2019 docker-entrypoint.sh -> usr/local/bin/docker-entrypoint.sh
drwxr-xr-x   1 root root 1690 Mar  5 13:30 etc
...

```

不幸的是，这有一个严重的副作用。实际的 Cassandra 应用程序不再启动，我们必须从 shell 手动执行此操作。

当我们使用这种方式时，我们假设我们可以控制容器的启动。在生产环境中，这可能是不可能的。

### 2.2. 在正在运行的容器中生成Shell

幸运的是，我们可以使用docker exec命令，它 允许我们连接到正在运行的容器。

让我们首先启动我们要探索的容器：

```shell
$ docker run cassandra
...
INFO  [MigrationStage:1] 2020-03-05 13:44:36,734 - Initializing system_auth.resource_role_permissons_index
INFO  [MigrationStage:1] 2020-03-05 13:44:36,739 - Initializing system_auth.role_members
INFO  [MigrationStage:1] 2020-03-05 13:44:36,743 - Initializing system_auth.role_permissions
INFO  [MigrationStage:1] 2020-03-05 13:44:36,747 - Initializing system_auth.roles
INFO  [main] 2020-03-05 13:44:36,764 - Waiting for gossip to settle...
...

```

接下来，我们使用docker ps识别容器 ID ：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             
00622c0645fb        cassandra           "docker-entrypoint.s…"   2 minutes ago  

```

然后，我们将/bin/bash作为带有-it选项的参数传递给docker exec：

```shell
$ docker exec -it 00622c0645fb /bin/bash
root@00622c0645fb:/# ls -all
...
-rwxr-xr-x   1 root root    0 Mar  5 13:44 .dockerenv
drwxr-xr-x   1 root root  920 Aug 14  2019 bin
drwxr-xr-x   1 root root    0 Mar 28  2019 boot
drwxr-xr-x   5 root root  340 Mar  5 13:44 dev
lrwxrwxrwx   1 root root   34 Aug 14  2019 docker-entrypoint.sh -> usr/local/bin/docker-entrypoint.sh
drwxr-xr-x   1 root root 1690 Mar  5 13:44 etc
...
```

在这里，我们使用Bash作为我们选择的 shell。这可能因容器所基于的Linux发行版而异。

相比之下，我们的第一个示例使用 Alpine Linux，它默认带有Bourne Shell：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED            
8408c85b3c57        alpine              "/bin/sh"           3 seconds ago  

```

由于Bash不可用，我们将/bin/sh作为参数传递给docker exec：

```shell
$ docker exec -it 8408c85b3c57 /bin/sh
/ # ls -all
...
-rwxr-xr-x    1 root     root             0 Mar  5 14:19 .dockerenv
drwxr-xr-x    1 root     root           850 Jan 16 21:52 bin
drwxr-xr-x    5 root     root           340 Mar  5 14:19 dev
drwxr-xr-x    1 root     root           508 Mar  5 14:19 etc
drwxr-xr-x    1 root     root             0 Jan 16 21:52 home
...
```

## 3.非交互式探索

有时，容器停止了，我们无法交互运行它，或者它根本就没有 shell。

例如，[hello-world](https://hub.docker.com/_/hello-world)是一个[从头](https://hub.docker.com/_/scratch)开始的最小容器。因此，无法进行 shell 访问。

幸运的是，在这两种情况下，我们都可以将文件系统转储到我们的主机以进行进一步探索。

让我们看看如何做到这一点。

### 3.1. 导出文件系统

我们可以使用docker export命令将容器的文件系统导出到[tar](https://www.baeldung.com/linux/zip-unzip-command-line)文件中。

让我们首先运行 hello-world 容器：

```shell
$ docker run hello-world

Hello from Docker!
This message shows that your installation appears to be working correctly.
....

```

同样，我们首先通过将-a标志传递给docker ps来获取已停止容器的容器 ID ：

```shell
$ docker ps -a
CONTAINER ID        IMAGE                 COMMAND                  CREATED             
a0af60c72d93        hello-world           "/hello"                 3 minutes ago       
...

```

然后我们使用docker export的-o选项将文件系统转储到 hello.tar文件中：

```shell
$ docker export -o hello.tar a0af60c72d93

```

最后，我们使用带有-tvf标志的tar实用程序打印存档的内容：

```shell
$ tar -tvf hello.tar
-rwxr-xr-x root/0            0 2020-03-05 16:55 .dockerenv
....
drwxr-xr-x root/0            0 2020-03-05 16:55 dev/pts/
drwxr-xr-x root/0            0 2020-03-05 16:55 dev/shm/
....
-rwxr-xr-x root/0            0 2020-03-05 16:55 etc/resolv.conf
-rwxrwxr-x root/0         1840 2019-01-01 03:27 hello
...

```

或者，我们可以使用任何档案浏览器来查看里面的内容。

### 3.2. 复制文件系统

我们还可以使用docker cp命令复制整个文件系统。

我们也试试这个。

首先，我们将容器中从根目录 (/)开始的完整文件系统复制到测试目录：

```shell
$ docker cp a0af60c72d93:/ ./test

```

接下来，让我们打印测试目录的内容：

```shell
$ ls -all test/
total 28
..
drwxr-xr-x  4 baeldung users 4096 Mar  5 16:55 dev
-rwxr-xr-x  1 baeldung users    0 Mar  5 16:55 .dockerenv
drwxr-xr-x  2 baeldung users 4096 Mar  5 16:55 etc
-rwxrwxr-x  1 baeldung users 1840 Jan  1  2019 hello

```

## 4. 总结

在本快速教程中，我们讨论了如何探索Docker容器的文件系统。

我们可以直接使用docker run命令启动大多数具有 shell 访问权限的容器。此外，我们可以在docker exec 的帮助下生成一个用于运行容器的 shell。

当涉及到停止容器或最小容器时，我们可以简单地导出甚至复制整个文件系统到本地。

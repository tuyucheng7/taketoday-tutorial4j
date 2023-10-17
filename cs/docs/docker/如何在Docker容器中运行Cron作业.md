---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

作为系统管理员，我们总是会遇到调度任务的需要。我们可以通过在Linux系统中使用cron服务来实现。我们还可以在容器系统中启用cron调度服务。

在本教程中，我们将讨论在Docker容器中启用cron服务的两种不同方式。在第一种方法中，我们将使用Dockerfile将cron服务嵌入到 docker 镜像中。对于第二种方法，我们将说明如何在容器中安装调度服务。

## 2. Cron 服务——使用Dockerfile方法

使用Dockerfile构建镜像是创建容器镜像最简单的方法之一。那么我们该怎么做呢？基本上，Dockerfile是一个简单的文本文件，其中包含一组构建镜像的指令。我们需要提供调度任务和cron详细信息，以及从Dockerfile调用cron服务。

### 2.1. 编写Dockerfile

让我们快速看一个例子：

```plaintext
$ tree
.
├── Dockerfile
└── get_date.sh
0 directories, 2 files

```

通常，Dockerfile的第一行以FROM命令开头，该命令将从配置的注册表中获取请求的镜像。在我们的例子中，默认注册表配置为DockerHub。然后是MAINTAINER，它是用于捕获作者信息的元数据。ADD指令将get_date.sh脚本从主机的镜像构建路径复制到镜像的目标路径。

将脚本复制到构建镜像后，RUN指令赋予可执行权限。不仅如此，RUN指令有助于将任何 shell 命令作为当前层之上的新镜像层执行，并提交结果。RUN更新apt仓库并在镜像中安装最新的cron服务。它还在crontab中执行cron调度。

现在我们将使用CMD指令启动cron服务：

```plaintext
$ cat Dockerfile

# Dockerfile to create image with cron services
FROM ubuntu:latest
MAINTAINER baeldung.com

# Add the script to the Docker Image
ADD get_date.sh /root/get_date.sh

# Give execution rights on the cron scripts
RUN chmod 0644 /root/get_date.sh

#Install Cron
RUN apt-get update
RUN apt-get -y install cron

# Add the cron job
RUN crontab -l | { cat; echo "* * * * * bash /root/get_date.sh"; } | crontab -

# Run the command on container startup
CMD cron

```

### 2.2. 构建和运行 Cron 镜像

一旦Dockerfile准备就绪，我们就可以使用docker build命令构建镜像。点(.) 指示Docker引擎从当前路径获取Dockerfile。构建命令为Dockerfile上给出的每条指令创建 docker 层，以形成最终构建镜像。典型的构建输出如下所示：

```plaintext
$ docker build .
Sending build context to Docker daemon  3.072kB
Step 1/8 : FROM ubuntu:latest
---> ba6acccedd29
Step 2/8 : MAINTAINER baeldung.com
---> Using cache
---> e6b3946b2382
Step 3/8 : ADD get_date.sh /root/get_date.sh
---> 4976f058d428
Step 4/8 : RUN chmod 0644 /root/get_date.sh
---> Running in 423a4e9adbab
Removing intermediate container 423a4e9adbab
---> 76d972a082ba
Step 5/8 : RUN apt-get update
---> Running in badc0d84f6ff
Get:1 http://security.ubuntu.com/ubuntu focal-security InRelease [114 kB]
...
... output truncated ...
...
Removing intermediate container badc0d84f6ff
---> edb8a19b891c
Step 6/8 : RUN apt-get -y install cron
---> Running in efd9b8a67d98
Reading package lists...
Building dependency tree...
...
... output truncated ...
...
Done.
invoke-rc.d: could not determine current runlevel
invoke-rc.d: policy-rc.d denied execution of start.
Removing intermediate container efd9b8a67d98
---> 2b80000d32a1
Step 7/8 : RUN crontab -l | { cat; echo "* * * * * bash /root/get_date.sh"; } | crontab -
---> Running in 1bdd3e0cc877
no crontab for root
Removing intermediate container 1bdd3e0cc877
---> aa7c82aa7c11
Step 8/8 : CMD cron
---> Running in cf2d44873b36
Removing intermediate container cf2d44873b36
---> 8cee091ca87d
Successfully built 8cee091ca87d

```

由于我们预先将cron服务安装到镜像中，并将任务嵌入到 crontab 中，因此当我们运行容器时，cron作业会自动激活。或者，我们可以使用docker run命令启动容器。随后， docker run的“ -it ”选项有助于使用 bash 提示符进入容器。

下图显示了使用cron在容器中执行get_date.sh脚本：

```plaintext
$ docker run -it 8cee091ca87d /bin/bash
root@88f5bb1f0a08:/#
root@88f5bb1f0a08:/# date
Mon Nov 15 14:30:21 UTC 2021

root@88f5bb1f0a08:/# ls -ltrh ~/date.out
ls: cannot access '/root/date.out': No such file or directory

root@88f5bb1f0a08:/# ls -ltrh /root/get_date.sh
-rw-r--r-- 1 root root 18 Nov 15 14:20 /root/get_date.sh

root@88f5bb1f0a08:/# crontab -l
* * * * * bash /root/get_date.sh

root@88f5bb1f0a08:/# ls -ltrh ~/date.out
-rw-r--r-- 1 root root 29 Nov 15 14:31 /root/date.out

root@88f5bb1f0a08:/# cat /root/date.out
Mon Nov 15 14:31:01 UTC 2021

```

## 3. Cron 服务——实时容器方法

或者，我们可以在Docker容器中设置cron服务来运行cron作业。那么作案手法是怎样的呢？

让我们使用docker run命令快速运行一个ubuntu容器。通常，容器是一个轻量级操作系统，不会将cron服务作为其默认包。

我们需要进入容器的交互式 shell 并使用apt仓库命令安装cron服务：

```plaintext
$ docker run -it ubuntu:latest /bin/bash
Unable to find image 'ubuntu:latest' locally
latest: Pulling from library/ubuntu
7b1a6ab2e44d: Pull complete
Digest: sha256:626ffe58f6e7566e00254b638eb7e0f3b11d4da9675088f4781a50ae288f3322
Status: Downloaded newer image for ubuntu:latest
root@77483fc20fc9:/#
root@77483fc20fc9:/# which cron
root@77483fc20fc9:/# apt update -y
Get:1 http://archive.ubuntu.com/ubuntu focal InRelease [265 kB]
...
... output truncated ...
...
All packages are up to date.
root@77483fc20fc9:/# apt upgrade -y
... 
... output truncated ...
...
root@77483fc20fc9:/# apt install cron vim -y
Reading package lists... Done
...
... output truncated ...
...
Done.
invoke-rc.d: could not determine current runlevel
invoke-rc.d: policy-rc.d denied execution of start.
root@77483fc20fc9:/# which cron
/usr/sbin/cron

$ docker cp get_data.sh 77483fc20fc9: /root/get_date.sh

```

我们可以使用docker cp命令将get_date.sh从宿主机复制到容器中。crontab -e使用vi编辑器编辑cron作业。下面的cron配置每分钟运行一次脚本。此外，输出指示脚本执行的时间戳：

```plaintext
root@77483fc20fc9:/# export EDITOR=vi
root@77483fc20fc9:/# crontab -e
* * * * * bash /root/get_date.sh

root@77483fc20fc9:/# date
Mon Nov 17 11:15:21 UTC 2021

root@77483fc20fc9:/# ls -ltrh ~/date.out
ls: cannot access '/root/date.out': No such file or directory

root@77483fc20fc9:/# ls -ltrh /root/get_date.sh
-rw-r--r-- 1 root root 18 Nov 17 11:09 /root/get_date.sh

root@77483fc20fc9:/# ls -ltrh ~/date.out
-rw-r--r-- 1 root root 29 Nov 17 11:16 /root/date.out

root@77483fc20fc9:/# cat /root/date.out
Mon Nov 17 11:16:01 UTC 2021

```

## 4. 总结

在本文中，我们探讨了在Docker容器内运行cron作业的具体细节。使用Dockerfile将cron服务和任务嵌入到镜像中，它会根据cron计划配置自动执行脚本。

虽然cron可以在运行的容器中安装和配置，但它只是一个运行时构造，除非我们使用docker commit构造一个镜像。

根据我们的使用情况，这两种选择都有其优势。

---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

容器化技术帮助我们以更低的成本快速构建和配置部署环境。秉承“一次编写，随处部署”的座右铭，我们使用容器化来解决现代应用程序的复杂性。

在本教程中，我们将深入探讨Docker镜像层，它们是容器化技术的基本构建块。

## 2.镜像层

Docker镜像是通过连接许多只读层创建的，这些只读层相互堆叠以形成一个完整的镜像。[Docker](https://www.baeldung.com/ops/docker-guide)和 Podman等平台将这些层组合在一起，将它们描绘成一个统一的对象。

例如，让我们从注册表中拉取一个MySQL镜像并快速浏览一下：

```plaintext
# docker pull mysql
Using default tag: latest
latest: Pulling from library/mysql
492d84e496ea: Pull complete
bbe20050901c: Pull complete
e3a5e171c2f8: Pull complete
c3aceb7e4f48: Pull complete
269002e5cf58: Pull complete
d5abeb1bd18e: Pull complete
cbd79da5fab6: Pull complete
Digest: sha256:cdf3b62d78d1bbb1d2bd6716895a84014e00716177cbb7e90f6c6a37a21dc796
Status: Downloaded newer image for mysql:latest
```

上面代码片段中以“Pull complete”结尾的每一行都代表从注册表中拉取的层以形成镜像。 正如我们所见，MySQL镜像有七层。

### 2.1. 镜像层创建

现在让我们更深入地了解层是如何通过Dockerfile插图构建的。

Dockerfile中的RUN、COPY和ADD等指令创建新层，而其他指令仅创建中间层。前一个命令对图层大小有影响，但后者没有。

让我们通过Dockerfile构建一个镜像。我们可以从此[链接引用](https://www.baeldung.com/ops/docker-cron-job)Dockerfile。我们使用docker build命令通过Dockerfile创建镜像：

```plaintext
# docker build -t layer-demo/latest .
Sending build context to Docker daemon  3.072kB
Step 1/8 : FROM ubuntu:latest
 ---> df5de72bdb3b
Step 2/8 : MAINTAINER baeldung.com
 ---> Running in 2c90e21f29e2
Removing intermediate container 2c90e21f29e2
 ---> 460d0651cc3d
Step 3/8 : ADD get_date.sh /root/get_date.sh
 ---> 492d1b205a94
Step 4/8 : RUN chmod 0644 /root/get_date.sh
 ---> Running in 08d04f1db0de
Removing intermediate container 08d04f1db0de
 ---> 480ba7f4bc50
Step 5/8 : RUN apt-get update
...
... output truncated ...
...
 ---> 28182a44db71
Step 6/8 : RUN apt-get -y install cron
...
... output truncated ...
...
 ---> 822f3eeca346
Step 7/8 : RUN crontab -l | { cat; echo "* * * * * bash /root/get_date.sh"; } | crontab -
 ---> Running in 635190dfb8d7
no crontab for root
Removing intermediate container 635190dfb8d7
 ---> 2822aac1f51b
Step 8/8 : CMD cron
 ---> Running in 876f0d5aca27
Removing intermediate container 876f0d5aca27
 ---> 5fc87be0f286
Successfully built 5fc87be0f286
```

这里发生了什么事？要创建此镜像，我们注意到它需要八个步骤，一个用于Dockerfile中的每条指令。最初，Ubuntu 镜像 [Image ID: df5de72bdb3b ] 从注册表中提取：

1.  它使用上一步的镜像[镜像 ID： df5de72bdb3b ]旋转中间容器[容器 ID： 2c90e21f29e2 ]。
2.  之后，指令在中间容器[Container ID: 2c90e21f29e2 ]上执行。
3.  随后，中间容器通过提交转换为镜像[Image ID：460d0651cc3d ]，导致删除中间容器[Container ID：2c90e21f29e2 ]。
4.  移除中间容器后，镜像变成只读层。然后它会执行Dockerfile中的下一条指令。

但是，创建新图层的步骤与上面相同。中间层不能影响镜像大小，而使用RUN、ADD和COPY的普通层能够增加镜像大小。

## 3.图层大小

通常，镜像的大小完全由与其关联的图层决定。docker history命令显示与镜像关联的每个层的大小。

在下面的示例中，大小为 0B 的层表示中间层，而RUN、COPY和ADD指令对镜像大小有贡献：

```plaintext
# docker history layer-demo/latest
IMAGE          CREATED       CREATED BY                                      SIZE      COMMENT
5fc87be0f286   8 hours ago   /bin/sh -c #(nop)  CMD ["/bin/sh" "-c" "cron…   0B      
2822aac1f51b   8 hours ago   /bin/sh -c crontab -l | { cat; echo "* * * *…   208B
822f3eeca346   8 hours ago   /bin/sh -c apt-get -y install cron              987kB
28182a44db71   8 hours ago   /bin/sh -c apt-get update                       36MB
480ba7f4bc50   8 hours ago   /bin/sh -c chmod 0644 /root/get_date.sh         5B
492d1b205a94   8 hours ago   /bin/sh -c #(nop) ADD file:1f79f73be93042145…   5B
460d0651cc3d   8 hours ago   /bin/sh -c #(nop)  MAINTAINER baeldung.com      0B
df5de72bdb3b   4 weeks ago   /bin/sh -c #(nop)  CMD ["bash"]                 0B
      4 weeks ago   /bin/sh -c #(nop) ADD file:396eeb65c8d737180…   77.8MB 
```

现在，让我们总结所有层的大小，从基础镜像开始：

-   df5de72bdb3b – 77.800000 MB ## 步骤 1：基本 Ubuntu 镜像
-   492d1b205a94 – 0.000005 MB ## 步骤 3：添加指令
-   480ba7f4bc50 – 0.000005 MB ## 第 4 步：运行指令
-   28182a44db71 – 36.000000 MB ## 步骤 5：运行指令
-   822f3eeca346 – 0.987000 MB ## 第 6 步：运行指令
-   2822aac1f51b – 0.000208 MB ## 第 7 步：运行指令

将上述所有数字相加得出 114.787 MB，可以进一步四舍五入为 115 MB。正如我们所见，计算出的总和与docker image命令中的layer-demo:latest image size完全匹配：

```plaintext
# docker images 
REPOSITORY            TAG       IMAGE ID       CREATED       SIZE
layer-demo/latest     latest    5fc87be0f286   8 hours ago   115MB
ubuntu                latest    df5de72bdb3b   4 weeks ago   77.8MB
```

## 4.悬挂镜像

悬挂镜像是镜像形成过程中创建的镜像层。但是，在镜像创建之后，这些图层将与任何标记的镜像没有任何关系。因此，删除所有这些镜像是安全的，因为它们会占用不必要的磁盘空间。

要列出所有悬挂镜像，我们可以使用docker image命令，在搜索过滤器中将悬挂属性设置为 true：

```plaintext
# docker images --filter "dangling=true"
```

下面的命令显示悬空镜像，然后随后将其删除：

```plaintext
# docker images --quiet --filter=dangling=true | xargs --no-run-if-empty docker rmi
```

## 5.总结

在本文中，我们了解了Docker镜像层的概念和层的创建。此外，我们还讨论了可用于识别与镜像关联的层列表以及每个层的大小的命令。

最后，我们看到了中间层是如何创建的，并了解到如果我们不经常清除悬挂镜像，它们就会留在我们的系统中。

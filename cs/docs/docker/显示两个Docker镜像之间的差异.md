---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

随着越来越多的应用程序使用[Docker](https://www.baeldung.com/ops/docker-guide)进行部署，我们有必要了解其生态系统的一些基本原则。虽然可用工具可以轻松打包和部署应用程序，但在某些时候，我们可能需要对部署的某些方面进行故障排除。

调试部署的一项更常见的任务是检查和比较镜像。在本教程中，我们将了解Docker镜像的结构以及如何查看两个镜像之间的差异。

## 2.关于Docker镜像

作为一些背景，Docker镜像是关于如何创建容器的说明。我们可以将它们视为在容器内运行应用程序所需的完整文件和目录集。这包括操作系统、第 3 方库和我们的应用程序代码。

在幕后，这些[镜像](https://www.baeldung.com/ops/docker-images-vs-containers)本质上只是tar文件。在构建镜像时，我们在其中创建了不同的层。每一层都是文件和目录的集合。

通常，我们从现有镜像开始并向其添加。例如，要将[Spring Boot应用程序](https://www.baeldung.com/spring-boot)构建为镜像，我们将从现有的[OpenJDKDocker镜像](https://hub.docker.com/_/eclipse-temurin/)开始。这包含运行任何Java应用程序所需的操作系统和 JDK 文件。从那里，我们将添加我们自己的Java文件，通常是应用程序的 fat jar，以及任何所需的第 3 方库。

在构建结束时，我们有一个镜像包含运行我们的应用程序所需的所有文件。接下来，我们将看看如何检查单个镜像。

## 3. 检查Docker镜像

有几种不同的方法来检查镜像。让我们从在本地仓库中查找所有镜像开始：

```shell
$ docker image ls
spring-petclinic    2.7.0-SNAPSHOT    0f9d2d05687b   2 months ago    266MB
spring-petclinic    2.6.0-SNAPSHOT    1d79d5bd7779   3 months ago    265MB
```

通过可用镜像列表，我们现在可以查看特定镜像。

我们可以做的第一件事是运行检查命令：

```shell
$ docker inspect 0f9d2d05687b
[
    {
        "Id": "sha256:0f9d2d05687b8c816cbf54f63cf7e5aa7144d28e1996d468bfaf555a3882610a",
        "RepoTags": [
            "spring-petclinic:2.7.0-SNAPSHOT"
        ],
        "Architecture": "amd64",
        "Os": "linux",
        "Size": 266141567,
        "VirtualSize": 266141567,
        ...
    }
]
```

此命令为我们提供了有关镜像的许多详细信息，包括创建时间、其中的不同层等等。

然而，它并没有告诉我们很多关于里面的内容。为此，我们必须首先将镜像保存到文件系统：

```shell
$ docker save 0f9d2d05687b > 0f9d2d05687b.tar
```

此命令会将镜像保存为tar文件。现在我们可以使用熟悉的[tar命令](https://www.baeldung.com/linux/tar-command)来检查它：

```shell
$ tar tvf 0f9d2d05687b.tar
drwxr-xr-x  0 0      0           0 Dec 31  1979 02805fa4a4f35efdcf3804bc1218af1bc22d28ee521cc944cab5cac5dbe5abfe/
-rw-r--r--  0 0      0           3 Dec 31  1979 02805fa4a4f35efdcf3804bc1218af1bc22d28ee521cc944cab5cac5dbe5abfe/VERSION
-rw-r--r--  0 0      0         477 Dec 31  1979 02805fa4a4f35efdcf3804bc1218af1bc22d28ee521cc944cab5cac5dbe5abfe/json
-rw-r--r--  0 0      0        1024 Dec 31  1979 02805fa4a4f35efdcf3804bc1218af1bc22d28ee521cc944cab5cac5dbe5abfe/layer.tar
drwxr-xr-x  0 0      0           0 Dec 31  1979 0f915e8772f0e40420852f1e2929e4ae9408327cbda6c546c71cca7c3e2f094a/
-rw-r--r--  0 0      0           3 Dec 31  1979 0f915e8772f0e40420852f1e2929e4ae9408327cbda6c546c71cca7c3e2f094a/VERSION
-rw-r--r--  0 0      0         477 Dec 31  1979 0f915e8772f0e40420852f1e2929e4ae9408327cbda6c546c71cca7c3e2f094a/json
-rw-r--r--  0 0      0     3622400 Dec 31  1979 0f915e8772f0e40420852f1e2929e4ae9408327cbda6c546c71cca7c3e2f094a/layer.tar
```

tar命令可以列出并从镜像中提取特定文件，具体取决于我们要查找的信息。

## 4. 显示两个Docker镜像之间的差异

到目前为止，我们已经了解了镜像的结构以及如何检查它。接下来，让我们看看如何比较两个镜像并找到它们的差异。

根据我们想要比较的信息，有不同的工具可以提供帮助。正如我们在上面看到的，内置镜像命令可以为我们提供大小和日期信息。

但是如果我们要比较两张图片的内容，就需要借助第三方工具了。下面，我们将看看其中的几个。

### 4.1. 容器差异

一种这样的工具是[Google 的 container-diff](https://github.com/GoogleContainerTools/container-diff)。尽管它的名字如此，但它可以比较两个镜像的各个方面并提供格式良好的报告。

例如，让我们比较前面示例中的两个Spring宠物诊所镜像：

```shell
$ /usr/local/bin/container-diff diff \
daemon://spring-petclinic:2.6.0-SNAPSHOT \
daemon://spring-petclinic:2.7.0-SNAPSHOT \
--type=file
```

这给了我们每个镜像文件的差异。

输出通常分为三个部分。首先，它告诉第一个镜像中存在哪些文件，但第二个镜像中不存在：

```plaintext
/workspace/BOOT-INF/lib/byte-buddy-1.12.10.jar                                                    3.7M
/workspace/BOOT-INF/lib/classgraph-4.8.139.jar                                                    551.7K
/workspace/BOOT-INF/lib/ehcache-3.10.0.jar                                                        1.7M
/workspace/BOOT-INF/lib/h2-2.1.212.jar                                                            2.4M
/workspace/BOOT-INF/lib/hibernate-core-5.6.9.Final.jar                                            7.1M
/workspace/BOOT-INF/lib/jackson-annotations-2.13.3.jar                                            73.9K
```

接下来，它告诉我们第二张镜像中存在哪些文件，但第一张镜像中不存在：

```plaintext
These entries have been deleted from spring-petclinic:2.6.0-SNAPSHOT:
FILE                                                                        SIZE
/workspace/BOOT-INF/lib/byte-buddy-1.11.22.jar                              3.5M
/workspace/BOOT-INF/lib/classgraph-4.8.115.jar                              525.4K
/workspace/BOOT-INF/lib/ehcache-3.9.9.jar                                   1.7M
/workspace/BOOT-INF/lib/h2-1.4.200.jar                                      2.2M
/workspace/BOOT-INF/lib/hibernate-core-5.6.7.Final.jar                      7.1M
/workspace/BOOT-INF/lib/jackson-annotations-2.13.2.jar                      73.9K
```

在这种情况下，将这两个部分结合起来可以非常快速地告诉我们应用程序的每个版本之间的依赖关系是如何变化的。

最后，最后一部分说明了哪些文件存在于两者中，但不同：

```plaintext
These entries have been changed between spring-petclinic:2.6.0-SNAPSHOT and spring-petclinic:2.7.0-SNAPSHOT:
FILE                                                                                                 SIZE1        SIZE2
/layers/config/metadata.toml                                                                         16.6K        1.9K
/workspace/META-INF/maven/org.springframework.samples/spring-petclinic/pom.xml                       13.3K        13.3K
/workspace/BOOT-INF/classes/org/springframework/samples/petclinic/owner/OwnerController.class        7.8K         7.7K
/workspace/org/springframework/boot/loader/ExecutableArchiveLauncher.class                           6.6K         7.5K
/workspace/org/springframework/boot/loader/JarLauncher.class                                         3.9K         2.5K
/workspace/BOOT-INF/classpath.idx                                                                    3.2K         3.2K
/workspace/org/springframework/boot/loader/data/RandomAccessDataFile$FileAccess.class                3.2K         3.2K
/workspace/BOOT-INF/classes/db/h2/data.sql                                                           2.8K         3K
/workspace/org/springframework/boot/loader/data/RandomAccessDataFile$DataInputStream.class           2.6K         2.7K
```

这部分使得识别两个版本之间发生变化的任何特定类和属性文件变得非常容易。

### 4.2. 潜水

另一个用于检查Docker镜像的优秀开源工具是[dive](https://github.com/wagoodman/dive)。使用dive检查镜像为我们提供了更传统的内容视图，因为它了解如何检查镜像内部的每一层。这允许它使用传统的文件系统树来呈现每个镜像：

[![潜水单镜像视图](https://www.baeldung.com/wp-content/uploads/2022/07/dive-single-image-view-1024x390.jpg)](https://www.baeldung.com/wp-content/uploads/2022/07/dive-single-image-view-scaled.jpg)

使用键盘，我们可以浏览镜像的每一层，并准确查看它添加、修改或删除了哪些文件。

虽然dive工具本身不支持比较镜像，但我们可以通过在两个终端上并排运行它来手动执行此操作：

[![潜水比较两个码头镜像](https://www.baeldung.com/wp-content/uploads/2022/07/dive-compare-two-docker-images-1024x383.jpg)](https://www.baeldung.com/wp-content/uploads/2022/07/dive-compare-two-docker-images-scaled.jpg)

通过这种方式，我们可以看到哪些层是相同的(基于大小)，对于不同的层，我们可以看到哪些文件不同。

## 5.总结

在本文中，我们查看了Docker镜像的结构，并了解了检查它们的各种方法。虽然Docker客户端包含一些用于检查单个镜像的工具，但比较两个镜像需要使用 3rd 方工具。

Google 的container-diff是专门为比较镜像而构建的一个选项。虽然dive实用程序非常适合检查单个镜像的图层，但它也可用于通过简单地同时针对两个镜像运行它来识别差异。

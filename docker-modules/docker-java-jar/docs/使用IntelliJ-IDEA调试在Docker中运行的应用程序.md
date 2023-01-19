## 1. 概述

在本教程中，我们介绍如何**在IntelliJ中调试Docker容器**。假设我们有一个准备好进行测试的Docker镜像，IntelliJ可以从其[官网](https://www.jetbrains.com/idea/download)下载。

对于本文，我们参考这个[简单的基于单个Java类的应用程序]()，它可以很容易地进行[容器化、构建和测试]()。

在开始测试之前，我们需要确保Docker引擎已启动并在我们的计算机上运行。

## 2. 使用Dockerfile配置

使用Dockerfile配置时，**我们只需选择我们的Dockerfile并为镜像名称、镜像标签、容器名称和配置名称提供适当的名称**，还可以添加端口映射(如果有)：

<img src="../assets/img.png">

保存此配置后，我们可以从debug选项中选择此配置并点击debug。它首先构建镜像，在Docker引擎中注册镜像，然后运行容器化应用程序。

## 3. 使用Docker镜像配置

当使用Docker镜像配置时，**我们需要提供我们预先构建的应用程序的镜像名称、镜像标签和容器名称**。我们可以使用标准的Docker命令来构建镜像并在Docker引擎中注册容器，还可以添加端口映射(如果有)：

<img src="../assets/img_1.png">

保存此配置后，我们可以从debug选项中选择此配置并点击debug，它只是选择预先构建的Docker镜像和容器并运行它。

## 4. 使用远程JVM调试配置

**远程JVM配置将自身附加到任何预运行的Java进程**，因此我们需要先单独运行Docker容器。

下面是为Java 8运行Docker镜像的命令：

```shell
docker run -d -p 8080:8080  -p 5005:5005 -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" docker-java-jar:latest
```

如果我们使用的是Java 11，可以使用这个命令：

```shell
docker run -d -p 8080:8080  -p 5005:5005 -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n" docker-java-jar:latest
```

这里的docker-java-jar是我们的镜像名称，latest是它的标签。除了正常的HTTP端口8080之外，我们还映射了一个额外的端口5005，用于使用-p扩展进行远程调试。**我们使用-d扩展在分离模式下运行docker，使用-e将JAVA_TOOL_OPTIONS作为环境变量传递给Java进程**。

在JAVA_TOOL_OPTIONS中，我们传递值-agentlib:jdwp=transport=dt_shmem,address=,server=y,**suspend=n以允许Java进程启动[JDB](https://www.tutorialspoint.com/jdb/jdb_quick_guide.htm)调试会话**,并传递值address=*:5005以指定5005将是我们的远程调试端口。

因此上面的命令启动我们的Docker容器，我们现在可以配置远程调试来连接它：

<img src="../assets/img_2.png">

我们可以看到，在配置中我们指定它使用5005端口连接到远程JVM。

现在，如果我们从debug选项中选择此配置并单击debug，它将通过附加到已经运行的Docker容器来启动调试会话。

## 5. 总结

在本文中，我们介绍了可用于在IntelliJ中调试容器化应用程序的不同配置选项。
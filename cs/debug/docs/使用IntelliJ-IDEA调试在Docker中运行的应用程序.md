## 一、概述

在本教程中，**我们将了解如何** **在 IntelliJ 中调试 Docker 容器**。我们假设我们有一个准备好进行测试的 Docker 镜像。有多种[构建 Docker 镜像的](https://www.baeldung.com/spring-boot-docker-images)方法。

IntelliJ 可以从其[官网](https://www.jetbrains.com/idea/download)下载。

对于本文，我们将参考这个[单一的基于类的 Java 应用程序](https://github.com/eugenp/tutorials/tree/master/docker-modules/docker-java-jar)。它可以很容易地进行[码头化、构建和测试](https://www.baeldung.com/java-dockerize-app)。

在开始测试之前，我们需要确保 Docker 引擎已启动并在我们的计算机上运行。

## 2. 使用*Dockerfile*配置

使用 Docker 文件配置时，**我们只需选择我们的\*Dockerfile\* 并为镜像名称、镜像标签、容器名称和配置名称提供适当的名称。**我们还可以添加端口映射（如果有）：

[![使用 docker 文件配置](https://www.baeldung.com/wp-content/uploads/2022/08/configuration-using-docker-file.png)](https://www.baeldung.com/wp-content/uploads/2022/08/configuration-using-docker-file.png)

保存此配置后，我们可以从调试选项中选择此配置并点击调试。它首先构建镜像，在 Docker 引擎中注册镜像，然后运行 dockerized 应用程序。

## 3. 使用 Docker 镜像配置

在使用 Docker 镜像配置时，**我们需要提供我们预构建的应用程序的镜像名称、镜像标签和容器名称。**我们可以使用标准的 Docker 命令来构建镜像并在 Docker 引擎中注册容器。我们还可以添加端口映射（如果有）：

[![使用 docker 镜像配置](https://www.baeldung.com/wp-content/uploads/2022/08/configuration-using-docker-image.png)](https://www.baeldung.com/wp-content/uploads/2022/08/configuration-using-docker-image.png)

保存此配置后，我们可以从调试选项中选择此配置并点击调试。它只是选择预先构建的 Docker 镜像和容器并运行它。

## 4. 使用远程 JVM 调试配置

**远程 JVM 配置将自身附加到任何预运行的 Java 进程。**所以我们需要先单独运行Docker容器。

下面是为 Java 8 运行 Docker 镜像的命令：

```java
docker run -d -p 8080:8080  -p 5005:5005 -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" docker-java-jar:latest复制
```

如果我们使用的是 Java 11，我们会改用这个命令：

```java
docker run -d -p 8080:8080  -p 5005:5005 -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n" docker-java-jar:latest复制
```

这里的*docker-java-jar*是我们的镜像名称，*latest*是它的标签。除了正常的 HTTP 端口 8080 之外，我们还映射了一个额外的端口 5005 *，*用于使用*-p*扩展进行远程调试。我们**使用\*-d\*扩展在分离模式下运行 docker，使用\*-e\*将\*JAVA_TOOL_OPTIONS\*作为环境变量传递给 Java 进程**。

在*JAVA_TOOL_OPTIONS* 中，我们传递值*-agentlib:jdwp=transport=dt_shmem,address=,server=y,suspend=n*以**允许 Java 进程启动[\*JDB\*](https://www.tutorialspoint.com/jdb/jdb_quick_guide.htm)调试会话**并传递值*address=\*:5005*以指定 5005将是我们的远程调试端口。

所以上面的命令启动了我们的 Docker 容器，我们现在可以配置远程调试配置来连接它：

[![使用远程 jvm 调试配置](https://www.baeldung.com/wp-content/uploads/2022/08/configuration-using-remote-jvm-debug.png)](https://www.baeldung.com/wp-content/uploads/2022/08/configuration-using-remote-jvm-debug.png)

我们可以看到在配置中我们已经指定它使用 5005 端口连接到远程 JVM。

现在，如果我们从调试选项中选择此配置并单击调试，它将通过附加到已经运行的 Docker 容器来启动调试会话。

## 5.结论

在本文中，我们了解了可用于在 IntelliJ 中调试 dockerized 应用程序的不同配置选项。
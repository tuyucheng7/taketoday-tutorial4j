## 一、概述

随着微服务架构越来越受欢迎，巨型单体应用程序正在成为过去。Java 并没有停滞不前，而是在适应现代需求。例如，Oracle、Red Hat、BellSoft 和其他贡献者正在积极开发[GraalVM](https://www.baeldung.com/graal-java-jit-compiler)项目。此外，微服务专用框架[Quarkus](https://www.baeldung.com/spring-boot-vs-quarkus)于一年前发布。就Spring Boot而言，VMware已经在Spring Native项目上做了两年。

因此，得益于 VMware 和 BellSoft 之间的合作，Spring Native 成为了端到端的原生图像解决方案，其中包括基于GraalVM 源代码的工具[Liberica Native Image Kit 。](https://bell-sw.com/pages/liberica-native-image-kit/)Spring Native 和 Liberica NIK 允许开发人员创建 Spring Boot 应用程序的本机可执行文件，以优化资源消耗并最大限度地减少启动时间。

在本教程中，我们将通过以三种方式构建和运行相同的应用程序来探索如何将本机图像技术与 Spring Boot 应用程序一起使用——作为经典的 JAR 文件；作为 Liberica JDK 和 Spring Native 的本地图像容器；并使用 Liberica Native Image Kit 作为原生图像。然后我们将比较它们的启动速度。在每种情况下，我们都将使用Spring Native 项目中的[petclinic](https://github.com/spring-projects/spring-petclinic) JDBC 应用程序作为示例。

## 2.安装Liberica JDK

首先，让我们为您的系统安装 Java 运行时。我们可以访问 [Liberica JDK 下载页面](https://bell-sw.com/pages/downloads/)并选择适合我们平台的版本。让我们使用 JDK 11，x86 Linux 标准 JDK 包。

有两种安装 Liberica JDK 的方法。一种是通过包管理器或下载*.tar.gz 包*（或Windows 的*.zip包）。*

后者是一种更高级的方法，但不用担心，它只需要四个步骤。我们首先需要切换到我们要安装的目录：

```shell
cd directory_path_name复制
```

在不离开目录的情况下，我们可以运行：

```shell
wget https://download.bell-sw.com/java/11.0.14.1+1/bellsoft-jdk11.0.14.1+1-linux-amd64.tar.gz复制
```

如果我们没有*wget*命令，我们可以通过 brew install *wget*（适用于 Linux 和 Mac）安装它。

这样，我们将运行时解压到我们所在的目录中：

```shell
tar -zxvf bellsoft-jdk11.0.14.1+1-linux-amd64.tar.gz复制
```

安装完成后，如果要节省磁盘空间，我们可以删除*.tar.gz文件。*

最后，我们需要通过指向 Liberica JDK 目录来设置*JAVA_HOME变量：*

```shell
export JAVA_HOME=$(pwd)/jdk-11.0.14.1复制
```

**请注意：macOS 和 Windows 用户可以参考[Liberica JDK 安装指南](https://bell-sw.com/pages/liberica_install_guide-11.0.14/)以获取说明。**

## 3.获取Spring Native项目

我们可以通过运行以下命令获取带有 petclinic 应用程序示例的 Spring Native 项目：

```shell
git clone https://github.com/spring-projects-experimental/spring-native.git复制
```

## 4. 构建 JAR 文件

我们想使用整个 Spring Native 项目中的一个示例，因此通过运行以下命令进入带有 spring petclinic JDBC 的目录：

```shell
export PROJECT_DIR=$(pwd)/spring-native/samples/petclinic-jdbc && cd $PROJECT_DIR复制
```

要构建 JAR 文件，我们可以应用此命令：

```shell
./mvnw clean install复制
```

这将为我们提供 24 MB 的*target/petclinic-jdbc-0.0.1-SNAPSHOT.jar*。我们将通过运行来测试它：

```shell
java -jar target/petclinic-jdbc-0.0.1-SNAPSHOT.jar复制
```

## 5. 使用 Liberica JDK 构建原生镜像容器

现在让我们容器化我们的应用程序。

确保我们的[Docker 守护进程](https://www.baeldung.com/ops/docker-cannot-connect)正在运行。请注意，如果我们使用 Windows 或 macOS x86，我们至少需要为 Docker 分配 8 GB 的内存。在 Spring petclinic JDBC 应用程序目录中，我们需要输入命令：

```shell
./mvnw spring-boot:build-image复制
```

这将使用我们可以启动的 Spring Boot 构建本机图像容器：

```shell
docker run -it docker.io/library/petclinic-jdbc:0.0.1-SNAPSHOT复制
```

如果我们使用 Apple M1，由于缺少必要的 Docker buildpack，我们将无法执行此步骤。然而，最新版本的 Liberica Native Image Kit 与 Apple Silicon 完全兼容，因此我们可以继续下一步，使用 NIK 构建原生图像。

## 6. 使用 Liberica NIK 构建原生镜像

我们将使用 Liberica Native Image Kit 构建另一个版本的 petclinic 本机映像。下面我们可以找到安装 NIK for Linux 的步骤。对于 macOS 或 Windows，让我们参考[Liberica NIK 安装指南](https://bell-sw.com/pages/liberica_install_guide-native-image-kit-21.3.0/)。

我们首先需要切换到我们要安装的目录：

```shell
cd directory_path_name复制
```

然后我们 为我们的平台[下载 Liberica NIK Core 。](https://bell-sw.com/pages/downloads/native-image-kit/)它包含基于 Liberica VM 和 GraalVM 的原生镜像工具包，无需额外的语言，因此是构建 Java 原生镜像的绝佳工具。

在我们的例子中，我们将获得适用于 Linux 的 Java 11 的 NIK 版本：

```shell
wget https://download.bell-sw.com/vm/22.0.0.2/bellsoft-liberica-vm-openjdk11-22.0.0.2-linux-amd64.tar.gz复制
```

然后我们通过运行解压文件：

```shell
tar -xzf bellsoft-liberica-vm-openjdk11-22.0.0.2-linux-amd64.tar.gz复制
```

通过指向 Liberica NIK 定义 $JAVA_HOME 变量：

```shell
export JAVA_HOME=$(pwd)/bellsoft-liberica-vm-openjdk11-22.0.0.2复制
```

现在，我们转到 petclinic JDBC 应用程序目录：

```shell
cd $PROJECT_DIR复制
```

我们可以通过运行以下命令来创建本机图像：

```shell
./mvnw -Pnative install复制
```

它涉及构建的“本机”配置文件，并生成大小为 102.3 MB 的*target/petclinic-jdbc二进制文件。*

## 7.比较启动时间

现在让我们测试我们的应用程序和图像的速度。我们使用带有 SSD 的 Intel(R) Core(TM) i7-8750H CPU PC 来运行它们：

-   JAR 文件在大约 3.3 秒内启动
-   我们构建的第一个容器在大约 0.07 秒内启动
-   使用 NIK Core 制作的原始图像在 0.068 秒内启动。

## 八、结论

即使项目仍处于 Beta 阶段，Spring 本机图像也已构建并运行良好。启动时间的减少是巨大的。

当 Spring Native 与 Liberica Native Image Kit 一起发布时，我们可以期待更好的结果，用作构建原生图像的端到端解决方案。
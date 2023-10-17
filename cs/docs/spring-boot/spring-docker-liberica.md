## 1. 简介

在本教程中，我们将探索运行使用Spring Boot作为 Docker 容器创建的标准Java应用程序的方法。更具体地说，我们将在 Alpaquita Linux 之上使用 Liberica JDK 来创建将运行我们的应用程序的 Docker 映像。

Liberica JDK 和 Alpaquita Linux 是 BellSoft 产品的一部分。[BellSoft](https://bell-sw.com/?utm_source=baeldung&utm_medium=link_in_atricle&utm_campaign=baeldung_docker_spring_app_alpaquita&utm_content=home_page&utm_term=face)是一家致力于让Java成为云原生应用程序首选语言的组织。通过他们有针对性的产品，他们承诺以更低的成本提供更好的体验。

## 2. 一个简单的Spring Boot应用程序

让我们首先用Java创建一个简单的应用程序，我们将继续对其进行容器化。我们将使用[Spring Boot](https://spring.io/projects/spring-boot)来创建此应用程序。Spring Boot 可以轻松地以最少的配置创建独立的、生产级的基于 Spring 的应用程序。

初始化Spring Boot应用程序的最简单方法是使用[Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/html/cli.html)。它允许我们直接从我们最喜欢的命令行使用 start.spring.io 创建一个新项目：

```powershell
$ spring init --build=gradle --dependencies=web spring-bellsoft
```

在这里，我们添加web作为依赖项，允许我们使用 RESTful API 和 Apache Tomcat 作为默认嵌入式容器来构建应用程序。我们在这里选择 Gradle 作为构建工具。但是，它选择Java作为语言，并默认选择许多其他内容。

然后，我们可以将生成的项目导入到我们最喜欢的 IDE(例如 IntelliJ Idea)中，并开始开发应用程序。如前所述，我们将保持这一点非常简单。因此，我们将添加一个简单的 REST API，它将数字作为输入并返回等于或小于该数字的斐波那契数列：

```java
@RestController
public class DemoController {

    @GetMapping("/api/v1/fibs")
    public List<Integer> getFibonacciSeriesBelowGivenInteger(Integer input) {
        List<Integer> result;
        if (input == 0)
            result = List.of(0);
        else {
            int n = 0; int m = 1;
            result = new ArrayList<>(Arrays.asList(n));
            while (m <= input) {
                result.add(m);
                m = n + m; n = m - n;
            }
        }
        return result;
    }
}
```

在 Gradle 中构建应用程序就像运行以下命令一样简单，该命令使用之前生成的 Gradle 包装器：

```powershell
./gradlew clean build
```

生成的项目的默认打包是 JAR ，这意味着上述命令成功后将在输出目录“ ./build/libs ”中创建最终的可执行 JAR 。我们可以使用这个 JAR 文件启动应用程序：

```powershell
java -jar ./build/libs/spring-bellsoft-0.0.1-SNAPSHOT.jar
```

然后，我们可以调用 API 并查看它是否正常工作：

```powershell
$ curl http://localhost:8080/api/v1/fibs?input=5
[0,1,1,2,3,5]
```

我们为本教程的其余部分创建一个简单的应用程序的工作到此结束。我们将使用这个可部署的应用程序来容器化该应用程序。

## 3. 容器化我们的应用程序

容器是打包代码及其所有依赖项的标准软件单元。它是操作系统虚拟化的一种形式，提供一致的应用程序部署方式。如今，它已成为在云环境中运行任何应用程序的默认选择。

我们需要一个容器平台来[将我们的简单应用程序作为容器运行](https://www.baeldung.com/dockerizing-spring-boot-application)。容器平台除其他外还提供容器引擎来创建和管理容器。[Docker](https://www.docker.com/)是最流行的平台，旨在构建、共享和运行容器应用程序。

容器引擎从容器镜像创建容器。容器映像是一个不可变的静态文件，其中包含容器运行所需的所有内容。但是，它共享主机的操作系统内核。因此，它提供了完全的隔离，但仍然是轻量级的：

[![Docker-容器-堆栈](https://www.baeldung.com/wp-content/uploads/2023/09/Docker-Container-Stack.jpg)](https://www.baeldung.com/wp-content/uploads/2023/09/Docker-Container-Stack.jpg)

创建 Docker 映像的方法之一是将创建映像的方法描述为 Dockerfile。然后，我们可以使用 Docker 守护进程从 Dockerfile 创建镜像。Docker 的原始镜像格式现已成为[开放容器计划 (OCI)](https://opencontainers.org/)镜像规范。

将应用程序作为容器运行的主要优势之一是它可以跨多个环境提供一致的部署体验。例如，假设我们交付使用Java 17构建的简单应用程序，但部署在具有Java11 运行时的环境中。

为了避免这种意外，容器映像允许我们打包应用程序的所有关键依赖项，例如操作系统二进制文件/库和Java运行时。通过这样做，我们可以确保我们的应用程序无论部署在哪个环境中都会表现相同。

## 4.Liberica运行时容器

容器镜像由多层堆叠而成。每一层代表对文件系统的特定修改。通常，我们从最符合应用程序要求的基础映像开始，并在其之上构建附加层。

BellSoft 提供了多种针对在基于云的环境中运行Java应用程序进行高度优化的映像。它们是使用 Alpaquita Linux 和 Liberica JDK 构建的。在使用这些图像之前，让我们先检查一下其 BellSoft 组件的优点。

### 4.1. Alpaquita Linux 的优点

[Alpaquita Linux](https://bell-sw.com/alpaquita-linux/?utm_source=baeldung&utm_medium=link_in_atricle&utm_campaign=baeldung_docker_spring_app_alpaquita&utm_content=prod_alpaquita-linux&utm_term=alpaquita_linux)是一个基于Alpine Linux的轻量级操作系统。它专为Java量身定制，并针对云原生应用程序的部署进行了优化。其结果是基础映像大小为 3.22 MB，并且需要少量资源才能运行。

Alpaquita Linux 有两个版本，一个基于优化的[musl libc](https://www.musl-libc.org/)，另一个基于[glib libc](https://www.gnu.org/software/libc/)。这里，libc 指的是ISO C 标准中指定的[C 编程语言的标准库。](https://en.wikipedia.org/wiki/C_standard_library)它为多个任务提供宏、类型定义和函数。

除了针对Java应用程序进行优化之外，Alpaquita Linux 还为我们的部署提供了多种安全功能。其中包括网络功能、自定义构建选项和进程隔离。它还包括内核强化，例如内核锁定和内核模块签名。

此外，Alpaquita Linux 针对部署进行了优化，因为它使用内核模块压缩来减小包的大小。它提供了可靠且快速的堆栈来运行具有内核优化和内存管理等性能功能的应用程序。

Alpaquita Linux仅封装了少量操作系统组件。[但是，我们可以从Alpaquita APK 存储库](https://bell-sw.com/pages/repositories/?utm_source=baeldung&utm_medium=link_in_atricle&utm_campaign=baeldung_docker_spring_app_alpaquita&utm_content=repos)安装额外的模块和附加包。最重要的是，Alpaquita Linux 的 LTS 版本有四年的支持生命周期。

### 4.2. Liberica JDK 的优点

[Liberica JDK](https://bell-sw.com/libericajdk/?utm_source=baeldung&utm_medium=link_in_atricle&utm_campaign=baeldung_docker_spring_app_alpaquita&utm_content=prod_libericajdk&utm_term=libericajdk)是用于现代Java部署的开源Java运行时。它来自 OpenJDK 的主要贡献者 BellSoft，并承诺为Java应用程序的云、服务器和桌面使用提供单一运行时。还建议将其用于基于 Spring 的Java应用程序。

Liberica JDK 支持各种架构，例如 x86 64/32 位、ARM、PowerPC 和 SPARC。它还支持多种操作系统，例如 Windows、macOS 和大多数 Linux 发行版。此外，它支持当今使用的几乎所有Java版本。

Liberica JDK 的关键优势之一是它相当轻量。基于 Alpaquita Linux forJava 17的 Liberica 运行时容器小于 70 MB。它承诺更好的性能，同时减少流量、存储、内存消耗和总成本。

它还具有用于使用 JDK 运行时的多种工具。可以完全访问用于监控和更新的工具。此外，用户还可以访问 Liberica Administration Center (LAC)，这是一种用于运行时监控、许可证控制和安全更新的企业工具。

Liberica JDK 针对JavaSE 规范进行了 TCK 验证，并在每次发布之前进行了彻底的暴露测试。此外，BellSoft通过错误修复、安全补丁和其他所需的改进来保证 Liberica JDK 至少八年的生命周期。

## 5. 创建容器镜像

现在，我们准备使用 Alpaquita Linux 和 Liberica JDK 来容器化我们的简单应用程序。第一步是选择具有这些依赖项的基础映像。我们始终可以创建基础镜像，但值得庆幸的是，[BellSoft 在 Docker Hub 上维护了多个镜像](https://hub.docker.com/r/bellsoft/liberica-runtime-container)可供选择。

### 5.1. 选择 Liberica 运行时容器

这些是 Alpaquita Linux 映像，具有不同的 Liberica JDK lite 和 Liberica JRE 选项可供选择。我们通常可以从标签中识别这一点，该标签可能包含以下内容之一：

- jdk：带有 Liberica JDK Lite 版本的 Alpaquita Linux 映像
- jdk-all：打包 Liberica JDK，可用于使用 jlink 工具创建自定义运行时
- jre：仅包含用于运行Java应用程序的 Liberica JRE

在这里，除了图像包含的 JDK 版本之外，图像的标签还显示了有关图像的许多其他信息。让我们看一下 BellSoft 使用的图像标签的约定：

```powershell
[JDK type]-Java version-[slim]-[OS release]-[libc type]-[date]
```

在这里，标签的不同部分讲述了图像的特定方面，并帮助我们从许多可用图像中立即选择：

- JDK type：JDK 的类型(JDK、jdk-all 和 jre，如我们之前所见)
- Java版本：JDK符合的Java版本
- slim：指示图像是否是苗条的
- OS version：操作系统的版本(目前只有stream)
- libc 类型：标准 C 库的类型(glibc 或 musl，如我们之前所见)
- 日期：图像的发布日期

### 5.2. 容器化我们的应用程序

现在，我们已经有了图像标签中的所有信息来选择正确的标签。例如，如果我们希望应用程序使用带有 glibc 的Java 17，我们必须选择标签“ jdk-17-glibc ”。

一旦我们选择了正确的基础镜像标签，下一步就是创建一个 Dockerfile 来定义我们如何为应用程序创建容器镜像：

```powershell
FROM bellsoft/liberica-runtime-container:jdk-17-glibc
VOLUME /tmp
ARG JAR_FILE=build/libs/java-bellsoft-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

这个相当简单的 Dockerfile 声明我们希望从提到的 Liberica 运行时容器开始并复制我们的应用程序 fat JAR。我们还定义了入口点，其中包含在容器实例化后运行应用程序的指令。

我们应该将此 Dockerfile 放置在应用程序代码库目录的根目录中。然后，我们可以使用以下命令在本地存储库中创建容器镜像：

```powershell
docker buildx build -t spring-bellsoft .
```

默认情况下，这将从注册表 Docker Hub 中提取基础映像，并为我们的应用程序创建容器映像。然后，我们可以将此图像作为容器运行：

```powershell
docker run --name fibonacci -d -p 8080:8080 spring-bellsoft
```

请注意，我们已将本地端口 8080 与容器端口 8080 进行映射。因此，我们可以像本教程前面所做的那样访问我们的应用程序：

```powershell
$ curl http://localhost:8080/api/v1/fibs?input=5 
[0,1,1,2,3,5]
```

我们使用 BellSoft 发布的 Liberica 运行时容器对本教程前面创建的简单应用程序进行容器化的工作到此结束。当然，尝试更复杂的应用程序和我们可用的Liberica 运行时容器的其他变体会很有趣。

## 六，总结

在本教程中，我们了解了为基于Spring Boot的简单Java应用程序创建容器的基础知识。我们探索了选择[BellSoft Liberica](https://bell-sw.com/libericajdk/?utm_source=baeldung&utm_medium=link_in_atricle&utm_campaign=baeldung_docker_spring_app_alpaquita&utm_content=prod_libericajdk&utm_term=libericajdk)运行时容器来执行此操作的选项。在此过程中，我们创建了一个简单的应用程序并将其容器化。

这有助于我们了解 Alpaquita Linux 和 Liberica JDK(Liberica 运行时容器的组成部分)的优势。这些是 BellSoft 的一些核心产品，该公司致力于优化基于云的环境的Java应用程序。
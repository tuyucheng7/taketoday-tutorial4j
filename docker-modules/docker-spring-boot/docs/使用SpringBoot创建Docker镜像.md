## 1. 简介

随着越来越多的组织转向容器和虚拟服务器，Docker正在成为软件开发工作流程中更重要的一部分。为此，Spring Boot 2.3中的一项重要新功能是能够轻松地为Spring Boot应用程序创建Docker镜像。

在本教程中，我们介绍如何为Spring Boot应用程序创建Docker镜像。

## 2. 传统的Docker构建

使用Spring Boot构建Docker镜像的传统方法是使用Dockerfile，下面是一个简单的例子：

```dockerfile
FROM openjdk:8-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/demo-app-1.0.0.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

然后我们可以使用docker build命令来创建Docker镜像。这适用于大多数应用程序，但也有一些缺点。

首先，我们使用Spring Boot创建的fat jar，**这可能会影响启动时间，尤其是在容器化环境中**。我们可以通过添加jar文件的分解内容来节省启动时间。

其次，Docker镜像是分层构建的，Spring Boot fat jar的性质导致所有应用程序代码和第三方库都放在一个层中。**这意味着即使只有一行代码发生变化，也必须重建整个层**。

通过在构建之前分解jar，应用程序代码和第三方库都有自己的层，这允许我们利用Docker的缓存机制。现在，当一行代码发生变化时，只需要重建相应的层。

考虑到这一点，让我们看看Spring Boot如何改进创建Docker镜像的过程。

## 3. Buildpacks

**Buildpacks是一种提供框架和应用程序依赖关系的工具**。

例如，给定一个Spring Bootfat jar，buildpack将为我们提供Java运行时。这允许我们跳过Dockerfile并自动获得一个合理的Docker镜像。

Spring Boot包括对Buildpacks的Maven和Gradle支持。例如，使用Maven构建时，我们运行以下命令：

```shell
./mvn spring-boot:build-image
```

下面是一些相关的输出：

```shell
[INFO] Building jar: target/demo-1.0.0.jar
...
[INFO] Building image 'docker.io/library/demo:1.0.0'
...
[INFO]  > Pulling builder image 'gcr.io/paketo-buildpacks/builder:base-platform-api-0.3' 100%
...
[INFO]     [creator]     ===> DETECTING
[INFO]     [creator]     5 of 15 buildpacks participating
[INFO]     [creator]     paketo-buildpacks/bellsoft-liberica 2.8.1
[INFO]     [creator]     paketo-buildpacks/executable-jar    1.2.8
[INFO]     [creator]     paketo-buildpacks/apache-tomcat     1.3.1
[INFO]     [creator]     paketo-buildpacks/dist-zip          1.3.6
[INFO]     [creator]     paketo-buildpacks/spring-boot       1.9.1
...
[INFO] Successfully built image 'docker.io/library/demo:1.0.0'
[INFO] Total time:  44.796 s
```

第一行显示我们构建了标准的fat jar，就像任何典型的Maven包一样。

下一行开始构建Docker镜像，紧接着，我们看到构建程序在拉取[Packeto](https://paketo.io/)构建器。

Packeto是云原生构建包的实现，**它负责分析我们的项目并确定所需的框架和库**。在我们的例子中，它确定我们有一个Spring Boot项目，并添加了所需的构建包。

最后，我们看到生成的Docker镜像和总构建时间。请注意，第一次构建时，我们通常需要花费大量时间下载构建包并创建不同的层。

buildpacks的一大特点是Docker镜像是多层的。因此，如果我们只更改我们的应用程序代码，后续构建会快得多：

```shell
...
[INFO]     [creator]     Reusing layer 'paketo-buildpacks/executable-jar:class-path'
[INFO]     [creator]     Reusing layer 'paketo-buildpacks/spring-boot:web-application-type'
...
[INFO] Successfully built image 'docker.io/library/demo:1.0.0'
...
[INFO] Total time:  10.591 s
```

## 4. 分层Jar

在某些情况下，我们可能想使用构建包，也许我们的基础设施已经绑定到另一个工具，或者我们已经有了要重用的自定义Dockerfile。

**由于这些原因，Spring Boot还支持使用分层jar构建Docker镜像**。为了理解它的工作原理，让我们看一个典型的Spring Boot fat jar结构：

```bash
org/
    springframework/
        boot/
    loader/
...
BOOT-INF/
    classes/
...
lib/
...
```

fat jar由3个主要部分组成：

-   启动Spring应用程序所需的Bootstrap类
-   应用程序代码
-   第3方库

对于分层jar，结构看起来很相似，但我们会得到一个新的layers.idx文件，它将fat jar中的每个目录映射到一个层：

```yaml
- "dependencies":
    - "BOOT-INF/lib/"
- "spring-boot-loader":
    - "org/"
- "snapshot-dependencies":
- "application":
    - "BOOT-INF/classes/"
    - "BOOT-INF/classpath.idx"
    - "BOOT-INF/layers.idx"
    - "META-INF/"
```

开箱即用，Spring Boot提供了四层：

-   dependencies：来自第三方的依赖项
-   snapshot-dependencies：来自第三方的快照依赖项
-   resources：静态资源
-   application：应用程序代码和资源

**目的是将应用程序代码和第三方库放入反映它们更改频率的层中**。例如，应用程序代码可能是更改最频繁的，因此它有自己的层。此外，每一层都可以自行发展，只有当一个层发生变化时，才会为Docker镜像重建它。

现在我们了解了新的分层jar结构，让我们看看如何利用它来创建Docker镜像。

### 4.1 创建分层Jar

首先，我们必须设置我们的项目来创建一个分层的jar，对于Maven，我们需要在POM中的Spring Boot插件部分添加一个新配置：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <layers>
            <enabled>true</enabled>
        </layers>
    </configuration>
</plugin>
```

通过这种配置，maven package命令(及其任何相关命令)将使用前面提到的四个默认层生成一个新的分层jar。

### 4.2 查看和提取层

接下来，我们需要从jar中提取层，以便Docker映像具有适当的层。要检查任何分层jar的层，我们可以运行以下命令：

```shell
java -Djarmode=layertools -jar demo-1.0.0.jar list
```

要提取它们，我们可以运行：

```shell
java -Djarmode=layertools -jar demo-1.0.0.jar extract
```

### 4.3 创建Docker镜像

将这些层合并到Docker镜像中的最简单方法是使用Dockerfile：

```bash
FROM adoptopenjdk:11-jre-hotspot as builder
ARG JAR_FILE=target/.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

这个Dockerfile从我们的fat jar中提取层，然后将每一层复制到Docker镜像中，**每个COPY指令都会在最终的Docker镜像中生成一个新层**。

如果我们构建这个Dockerfile，我们可以看到分层jar中的每一层都作为自己的层添加到Docker镜像中：

```shell
...
Step 6/10 : COPY --from=builder dependencies/ ./
 ---> 2c631b8f9993
Step 7/10 : COPY --from=builder snapshot-dependencies/ ./
 ---> 26e8ceb86b7d
Step 8/10 : COPY --from=builder spring-boot-loader/ ./
 ---> 6dd9eaddad7f
Step 9/10 : COPY --from=builder application/ ./
 ---> dc80cc00a655
...
```

## 5. 结论

在本教程中，我们介绍了使用Spring Boot构建Docker镜像的各种方法。使用buildpacks，我们可以得到合适的Docker镜像，无需样板文件或自定义配置。或者，我们可以使用分层jar来获得更加定制化的Docker镜像。
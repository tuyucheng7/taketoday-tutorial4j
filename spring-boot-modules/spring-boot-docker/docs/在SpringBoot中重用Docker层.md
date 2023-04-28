## 1. 简介

Docker是创建自包含应用程序的事实标准，从2.3.0版本开始，Spring Boot包含了一些增强功能来帮助我们创建高效的Docker镜像，因此，它**允许将应用程序分解为不同的层**。

换句话说，源代码驻留在它自己的层中。因此，它可以独立重建，提高效率和启动时间。在本教程中，我们介绍如何利用Spring Boot的新功能来重用Docker层。

## 2. Docker中的分层Jar

Docker容器由基础镜像和附加层组成，构建层后，它们将保持缓存状态。因此，后面的构建会快得多：

<img src="../assets/img.png">

下层的变化也会重建上层，因此，不经常更改的层应该保留在底部，而经常更改的层应放在顶部。

同样，Spring Boot允许将工件(Jar)的内容映射到层中，下面是层的默认映射：

<img src="../assets/img_1.png">

正如我们所见，应用程序有自己的层，修改源代码时，只重建独立层。**加载程序和依赖项保持缓存状态，减少了 Docker镜像的创建和启动时间**，让我们看看如何使用Spring Boot来做到这一点。

## 3. 使用Spring Boot创建高效的Docker镜像

在构建Docker镜像的传统方式中，Spring Boot使用fat jar方式。单个工件嵌入了所有依赖项和应用程序源代码；因此，我们源代码中的任何更改都会强制重建整个层。

### 3.1 使用Spring Boot进行层配置

**Spring Boot 2.3.0版本引入了两个新特性来改进Docker镜像生成**：

-   [Buildpack支持]()为应用程序提供Java运行时，因此现在可以跳过Dockerfile并自动构建Docker镜像
-   [分层jar]()帮助我们充分利用Docker层生成

在本教程中，我们将扩展分层jar方法。

最初，我们会在Maven中设置分层jar，打包工件时，我们将生成层。可以使用下面命令检查一下jar文件：

```shell
jar tf target/spring-boot-docker-1.0.0.jar
```

正如我们所看到的，在fat jar内的BOOT-INF文件夹中创建了新层[.idx文件。]()当然，它将依赖关系、资源和应用程序源代码映射到独立的层：

```shell
BOOT-INF/layers.idx
```

同样，文件的内容分解了存储的不同层：

```shell
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

### 3.2 与层交互

使用下面命令列出工件内部的所有层：

```shell
java -Djarmode=layertools -jar target/docker-spring-boot-1.0.0.jar list
```

输出结果提供了layers.idx文件内容的简单视图：

```shell
dependencies
spring-boot-loader
snapshot-dependencies
application
```

我们还可以将层提取到文件夹中：

```shell
java -Djarmode=layertools -jar target/docker-spring-boot-1.0.0.jar extract
```

然后，我们可以在Dockerfile中重用这些文件夹，稍后我们会看到：

```shell
$ ls
application/
snapshot-dependencies/
dependencies/
spring-boot-loader/
```

### 3.3 Dockerfile配置

为了充分利用Docker的功能，我们需要将层添加到我们的镜像中。首先，我们将fat jar文件添加到基础镜像中：

```shell
FROM adoptopenjdk:11-jre-hotspot as builder
ARG JAR_FILE=target/.jar
COPY ${JAR_FILE} application.jar
```

其次，我们提取工件的层：

```shell
RUN java -Djarmode=layertools -jar application.jar extract
```

最后，我们复制提取的文件夹以添加相应的Docker层：

```shell
FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

使用此配置，当我们更改源代码时，我们只需要重建application层，其余的层将保持缓存状态。

## 4. 自定义层

看起来一切都很顺利，但是如果我们仔细观察，我们的**构建之间并没有共享dependencies层**。也就是说，所有这些层都是单一的，甚至是内部的。因此，如果我们更改内部库的类，我们也会重新构建所有依赖层。

### 4.1 使用Spring Boot自定义层配置

在Spring Boot中，可以通过单独的配置文件调整[自定义层：](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/#repackage-layers-configuration)

```xml
<layers xmlns="http://www.springframework.org/schema/boot/layers"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/boot/layers
                     https://www.springframework.org/schema/boot/layers/layers-2.3.xsd">
    <application>
        <into layer="spring-boot-loader">
            <include>org/springframework/boot/loader/**</include>
        </into>
        <into layer="application"/>
    </application>
    <dependencies>
        <into layer="snapshot-dependencies">
            <include>*:*:*SNAPSHOT</include>
        </into>
        <into layer="dependencies"/>
    </dependencies>
    <layerOrder>
        <layer>dependencies</layer>
        <layer>spring-boot-loader</layer>
        <layer>snapshot-dependencies</layer>
        <layer>application</layer>
    </layerOrder>
</layers>
```

如我们所见，我们将依赖项和资源映射和排序到层中。此外，我们可以根据需要添加任意数量的自定义层。

我们将文件命名为layers.xml，然后在Maven中，我们可以配置这个文件来自定义层：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <layers>
            <enabled>true</enabled>
            <configuration>${project.basedir}/src/layers.xml</configuration>
        </layers>
    </configuration>
</plugin>
```

如果我们打包工件，结果与默认行为类似。

### 4.2 添加新层

让我们创建一个internal-dependencies来添加我们的应用程序类：

```xml
<into layer="internal-dependencies">
    <include>cn.tuyucheng.taketoday.docker:*:*</include>
</into>
```

此外，我们排序新层：

```xml
<layerOrder>
    <layer>internal-dependencies</layer>
</layerOrder>
```

现在，如果我们列出fat jar中的层，就会出现新的internal-dependencies：

```shell
dependencies
spring-boot-loader
internal-dependencies
snapshot-dependencies
application
```

### 4.3 Dockerfile配置

提取后，我们可以将新的internal-dependencies层添加到我们的Docker镜像中：

```shell
COPY --from=builder internal-dependencies/ ./
```

因此，如果我们生成镜像，可以看到Docker会将internal-dependencies构建为一个新层：

```shell
$ mvn package
$ docker build -f src/main/docker/Dockerfile . --tag spring-docker-demo
....
Step 8/11 : COPY --from=builder internal-dependencies/ ./
 ---> 0e138e074118
.....
```

之后，我们可以查看Docker镜像中层的组成历史：

```shell
$ docker history --format "{{.ID}} {{.CreatedBy}} {{.Size}}" spring-docker-demo
c0d77f6af917 /bin/sh -c #(nop)  ENTRYPOINT ["java" "org.s… 0B
762598a32eb7 /bin/sh -c #(nop) COPY dir:a87b8823d5125bcc4… 7.42kB
80a00930350f /bin/sh -c #(nop) COPY dir:3875f37b8a0ed7494… 0B
0e138e074118 /bin/sh -c #(nop) COPY dir:db6f791338cb4f209… 2.35kB
e079ad66e67b /bin/sh -c #(nop) COPY dir:92a8a991992e9a488… 235kB
77a9401bd813 /bin/sh -c #(nop) COPY dir:f0bcb2a510eef53a7… 16.4MB
2eb37d403188 /bin/sh -c #(nop)  ENV JAVA_HOME=/opt/java/o… 0B
```

如我们所见，该层现在包含项目的内部依赖项。

## 5. 总结

在本教程中，我们演示了如何生成高效的Docker镜像，简而言之，我们使用了新的Spring Boot功能来创建分层 jar，对于简单的项目，我们可以使用默认配置。并且演示了一种更高级的配置来重用这些层。
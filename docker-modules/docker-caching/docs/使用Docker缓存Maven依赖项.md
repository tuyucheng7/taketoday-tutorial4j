## 1. 简介

在本教程中，我们介绍如何在Docker中构建Maven项目。首先，我们将从一个简单的单模块Java项目开始，演示如何利用Docker中的多阶段构建对构建过程进行容器化。接下来，我们演示如何使用Buildkit来缓存多个构建之间的依赖关系。最后，我们介绍如何在多模块应用程序中利用层缓存。

## 2. 多阶段分层构建

对于本文，我们将创建一个简单的Java应用程序，将Guava作为依赖项。我们将使用[maven-assembly插件]()创建一个fat JAR。项目代码和Maven配置在本文中并不是关键主题，因此不会过多介绍。

**多阶段构建是优化Docker构建过程的好方法，它使我们能够将整个过程保存在一个文件中，并帮助我们使Docker镜像尽可能小**。在第一阶段，我们将运行Maven构建并创建我们的fat JAR，在第二阶段，我们会复制JAR并定义一个入口点：

```dockerfile
FROM maven:alpine as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn package

FROM openjdk:8-jdk-alpine 
COPY --from=build /usr/app/target/single-module-caching-1.0.0-jar-with-dependencies.jar /app/runner.jar
ENTRYPOINT java -jar /app/runner.jar
```

这种方法使我们可以将最终的Docker镜像保持得更小，因为它不包含Maven可执行文件或我们的源代码。

然后我们创建Docker镜像：

```shell
docker build -t maven-caching .
```

接下来，我们从镜像启动一个容器：

```shell
docker run maven-caching
```

当我们更改代码中的某些内容并重新运行构建时，我们会注意到Maven package任务之前的所有命令都被缓存并立即执行。**由于我们的代码更改比项目依赖项更频繁，我们可以将依赖项下载和代码编译分开，以使用Docker层缓存来缩短构建时间**：

```dockerfile
FROM maven:alpine as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD pom.xml $HOME
RUN mvn verify --fail-never
ADD . $HOME
RUN mvn package

FROM openjdk:8-jdk-alpine 
COPY --from=build /usr/app/target/single-module-caching-1.0.0-jar-with-dependencies.jar /app/runner.jar
ENTRYPOINT java -jar /app/runner.jar
```

**当我们仅更改代码时运行后续构建会更快，因为Docker将从缓存中获取层**。

## 3. 使用BuildKit缓存

**Docker版本18.09引入了BuildKit作为对现有构建系统的彻底改造**，此次大型修改背后的想法是提高性能、存储管理和安全性。我们可以利用BuildKit在多个构建之间保持状态，这样，Maven不会每次都下载依赖项，因为我们有永久存储。要在我们的Docker安装中启用BuildKit，我们需要编辑daemon.json文件：

```json
...
{
"features": {
    "buildkit": true
}}
...
```

启用BuildKit后，我们可以将Dockerfile更改为：

```dockerfile
FROM maven:alpine as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN --mount=type=cache,target=/root/.m2 mvn -f $HOME/pom.xml clean package

FROM openjdk:8-jdk-alpine
COPY --from=build /usr/app/target/single-module-caching-1.0.0-jar-with-dependencies.jar /app/runner.jar
ENTRYPOINT java -jar /app/runner.jar
```

**当我们更改代码或pom.xml文件时，Docker将始终执行ADD和RUN Maven命令**。第一次运行时构建时间最长，因为Maven必须下载依赖项；**而后续运行将使用本地依赖项，执行速度更快**。

这种方法需要维护Docker卷作为依赖项的存储。有时，我们必须强制Maven使用Dockerfile中的-U标志更新我们的依赖项。

## 4. 多模块Maven项目的缓存

在前面的部分中，我们演示了如何利用不同的方法来加快单模块Maven项目Docker镜像的构建时间。对于更复杂的应用程序，这些方法不是最佳的。多模块Maven项目通常有一个模块作为我们应用程序的入口点，一个或多个模块包含我们的逻辑，并作为依赖项列出。

由于子模块被列为依赖项，它们将阻止Docker进行层缓存并触发Maven再次下载所有依赖项。**这种使用BuildKit的解决方案在大多数情况下都很好，但正如我们所说，它可能需要时不时地强制更新以获取更新的子模块**。为了避免这种情况，我们可以将我们的项目分层并使用Maven增量构建：

```dockerfile
FROM maven:alpine as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

ADD pom.xml $HOME
ADD core/pom.xml $HOME/core/pom.xml
ADD runner/pom.xml $HOME/runner/pom.xml

RUN mvn -pl core verify --fail-never
ADD core $HOME/core
RUN mvn -pl core install
RUN mvn -pl runner verify --fail-never
ADD runner $HOME/runner
RUN mvn -pl core,runner package

FROM openjdk:8-jdk-alpine
COPY --from=build /usr/app/runner/target/runner-1.0.0-jar-with-dependencies.jar /app/runner.jar
ENTRYPOINT java -jar /app/runner.jar
```

在这个Dockerfile中，我们复制所有的pom.xml文件并增量构建每个子模块，最后打包整个应用程序。经验法则是，我们构建的子模块在链的后面更频繁地更改。

## 5. 总结

在本文中，我们介绍了如何使用Docker构建Maven项目。首先，我们介绍了如何利用分层来缓存不经常更改的部分。接下来，我们介绍了如何使用BuildKit来保持构建之间的状态。最后，我们演示了如何使用增量构建来构建多模块Maven项目。
## 1. 概述

本机图像提供各种优势，如即时启动和减少内存消耗。因此，有时我们可能希望构建Java应用程序的本地映像。

在本教程中，我们将探索 Spring Native 以使用 Buildpacks 和 GraalVM 的原生构建工具编译和构建原生图像。

## 2. 基本设置

作为先决条件，我们将确保安装[Docker，稍后需要它来运行本机图像](https://www.baeldung.com/dockerizing-spring-boot-application)。

然后，我们将创建一个名为baeldung-spring-native的简单Spring Boot项目，并在整个教程中使用它来构建本机映像。

接下来，让我们添加一个指向 Spring 存储库的链接，以下载后面部分所需的依赖项和插件：

```xml
<repositories>
    <repository>
        <id>spring-release</id>
        <name>Spring release</name>
        <url>https://repo.spring.io/release</url>
    </repository>
</repositories>
<pluginRepositories>
    <pluginRepository>
        <id>spring-release</id>
        <name>Spring release</name>
        <url>https://repo.spring.io/release</url>
    </pluginRepository>
</pluginRepositories>
```

然后，我们将添加最新的[spring-native](https://repo.spring.io/artifactory/release/org/springframework/experimental/spring-native/) Maven 依赖项：

```xml
<dependency>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-native</artifactId>
    <version>0.10.0</version>
</dependency>
```

但是，对于Gradle项目，Spring Native是由Spring AOT插件自动添加的。

我们应该注意，每个 Spring Native 版本只支持特定的Spring Boot版本——例如，Spring Native 0.10.0 支持Spring Boot2.5.1。因此，我们应该确保在我们的pom.xml中使用兼容的Spring BootMaven 依赖项。

接下来，我们将添加一些属性以使用Java8 进行编译：

```xml
<properties>
    <java.version>1.8</java.version>
    <maven.compiler.target>1.8</maven.compiler.target>
    <maven.compiler.source>1.8</maven.compiler.source>
</properties>
```

最后，我们将创建SpringNativeApp类：

```java
public class SpringNativeApp {
    public static void main(String[] args) {
        System.out.println("Hello, World! This is a Baeldung Spring Native Application");
    }
}
```

## 3.构建包

现在我们的Spring Boot项目baeldung-spring-native已准备好基本设置，让我们[将 buildpacks 集成到我们的Spring Boot项目中以构建原生图像](https://www.baeldung.com/spring-boot-docker-images#buildpacks)。

### 3.1.Spring BootMaven 插件

首先，我们需要[spring-boot-maven-plugin](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-maven-plugin)和使用[PaketoJavabuildpacks](https://paketo.io/docs/buildpacks/language-family-buildpacks/java/)的原生镜像配置：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <image>
                    <builder>paketobuildpacks/builder:tiny</builder>
                    <env>
                        <BP_NATIVE_IMAGE>true</BP_NATIVE_IMAGE>
                    </env>
                </image>
            </configuration>
        </plugin>
    </plugins>
</build>

```

在这里，我们将使用各种可用构建器(如base和full )中的tiny构建器来构建本机映像。此外，我们通过为BP_NATIVE_IMAGE环境变量提供真实值来启用 buildpack。

同样，在使用 Gradle 时，我们可以将tiny builder 连同BP_NATIVE_IMAGE环境变量添加到build.gradle文件中：

```plaintext
bootBuildImage {
    builder = "paketobuildpacks/builder:tiny"
    environment = [
        "BP_NATIVE_IMAGE" : "true"
    ]
}
```

### 3.2. Spring AOT 插件

接下来，我们需要添加执行[提前转换的](https://www.baeldung.com/ahead-of-time-compilation)[Spring AOT](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#spring-aot)插件，这有助于改善本机图像的占用空间和兼容性。

因此，让我们将最新的[spring-aot-maven-plugin](https://repo.spring.io/artifactory/release/org/springframework/experimental/spring-aot-maven-plugin/) Maven 依赖项添加到我们的pom.xml中：

```xml
<plugin>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-aot-maven-plugin</artifactId>
    <version>0.10.0</version>
</plugin>

```

同样，对于一个Gradle项目，我们可以在build.gradle 文件中添加最新的[org.springframework.experimental.aot](https://repo.spring.io/artifactory/release/org/springframework/experimental/aot/org.springframework.experimental.aot.gradle.plugin/)依赖：

```plaintext
plugins {
    id 'org.springframework.experimental.aot' version '0.10.0'
}
```

此外，正如我们之前提到的，这会自动将 Spring Native 依赖项添加到 Gradle 项目中。

Spring AOT 插件提供[了几个选项来确定源代码生成](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#spring-aot-configuration)。例如，像removeYamlSupport和removeJmxSupport这样的选项分别删除了[Spring Boot Yaml](https://www.baeldung.com/spring-yaml)和Spring Boot[JMX](https://www.baeldung.com/java-management-extensions)支持。

### 3.3. 构建和运行图像

而已！我们已准备好使用 Maven 命令构建我们的Spring Boot项目的本机映像：

```powershell
$ mvn spring-boot:build-image
```

Maven 命令应该创建一个名为baeldung-spring-native:0.0.1-SNAPSHOT的Spring Boot应用程序的本机映像。

注意：构建原生镜像会消耗大量资源。因此，当我们在构建原生镜像时遇到问题时，我们必须增加分配给 Docker 的内存和 CPU。

最后，我们可以使用docker run命令在 Docker 上运行我们的应用程序镜像：

```powershell
$ docker run --rm -p 8080:8080 baeldung-spring-native:0.0.1-SNAPSHOT
```

因此，我们的应用程序应该几乎立即启动并提供如下输出：

```powershell
Hello, World! This is a Baeldung Spring Native Application
```

## 4. GraalVM 原生构建工具

作为 Paketo 构建包的替代方案，我们可以使用[GraalVM](https://www.baeldung.com/graal-java-jit-compiler#project-graal)的[原生构建工具，使用 GraalVM 的](https://github.com/graalvm/native-build-tools)原生图像编译器来编译和构建原生图像。

### 4.1. 本机图像编译器安装

作为先决条件，我们必须安装[SDKMAN 才能使设置过程顺利进行](https://www.baeldung.com/java-sdkman-intro)。然后，我们可以使用[SDKMAN 为Java8 安装 GraalVM](https://www.baeldung.com/java-sdkman-intro#install-and-manage-java-versions)：

```powershell
$ sdk install java 21.0.0.2.r8
```

接下来，我们将设置JAVA_HOME指向 GraalVM 的21.0.0.2.r8发行版。

最后，让我们安装由已安装的 GraalVM 的21.0.0.2.r8发行版提供的本机图像编译器：

```powershell
$ gu install native-image
```

### 4.2. 春天的AOT

除了spring-native依赖项，我们还将在我们的pom.xml中添加本机构建工具所需的最新[spring-aot](https://repo.spring.io/artifactory/release/org/springframework/experimental/spring-aot/) Maven 依赖项：

```xml
<dependency>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-aot</artifactId>
    <version>0.10.0</version>
</dependency>
```

### 4.3.Spring BootMaven 插件

与 Paketo 构建包类似，GraalVM 的原生构建工具也需要spring-boot-maven-plugin：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

### 4.4. Spring AOT Maven 插件

此外，我们将使用生成目标将spring-aot-maven-plugin添加到我们的pom.xml中：

```xml
<plugin>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-aot-maven-plugin</artifactId>
    <version>0.10.0</version>
    <executions>
        <execution>
            <id>generate</id>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 4.5. 本机配置文件

接下来，我们将添加一个名为native的配置文件，其中包含一些插件的构建支持，例如[native-maven-plugin](https://search.maven.org/search?q=g:org.graalvm.buildtools a:native-maven-plugin)和spring-boot-maven-plugin：

```xml
<profiles>
    <profile>
        <id>native</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <version>0.9.0</version>
                    <executions>
                        <execution>
                            <id>build-native</id>
                            <goals>
                                <goal>build</goal>
                            </goals>
                            <phase>package</phase>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <classifier>exec</classifier>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

此配置文件将在打包阶段从构建中调用本机映像编译器。

但是，在使用 Gradle 时，我们会将最新的[org.graalvm.buildtools.native](https://search.maven.org/search?q=g:org.graalvm.buildtools.native a:org.graalvm.buildtools.native.gradle.plugin)插件添加到build.gradle文件中：

```plaintext
plugins {
    id 'org.graalvm.buildtools.native' version '0.9.0'
}
```

### 4.6. 构建并运行

而已！我们已准备好通过在 Maven包命令中提供本机配置文件来构建我们的本机映像：

```powershell
$ mvn -Pnative -DskipTests package
```

Maven 命令将在目标文件夹中创建baeldung-spring-native执行程序文件。因此，我们可以通过简单地访问执行程序文件来运行我们的应用程序：

```powershell
$ target/baeldung-spring-native
Hello World!, This is Baeldung Spring Native Application
```

## 5.总结

在本教程中，我们探索了 Spring Native 以及构建包和 GraalVM 的原生构建工具。

首先，我们创建了一个简单的Spring Boot项目并使用 Paketo buildpacks 构建了一个原生镜像。然后，我们检查了 GraalVM 的原生构建工具，以使用 GraalVM 的原生图像编译器构建和运行原生图像。
## 1. 概述

在本快速教程中，我们将专注于将 Maven 项目打包到可执行 Jar 文件中。

在创建jar文件时，我们通常希望在不使用 IDE 的情况下轻松运行它。为此，我们将讨论使用这些方法中的每一种来创建可执行文件的配置和优缺点。

## 延伸阅读：

## [Apache Maven 教程](https://www.baeldung.com/maven)

使用 Apache Maven 构建和管理Java项目的快速实用指南。

[阅读更多](https://www.baeldung.com/maven)→

## [Maven 本地存储库在哪里？](https://www.baeldung.com/maven-local-repository)

快速教程向你展示 Maven 存储其本地存储库的位置以及如何更改它。

[阅读更多](https://www.baeldung.com/maven-local-repository)→

## [带有 Maven BOM 的 Spring](https://www.baeldung.com/spring-maven-bom)

了解如何在你的 Spring Maven 项目中使用 BOM、物料清单。

[阅读更多](https://www.baeldung.com/spring-maven-bom)→

## 2.配置

我们不需要任何额外的依赖来创建一个可执行的jar。我们只需要创建一个 MavenJava项目，并至少有一个带有main(...)方法的类。

在我们的示例中，我们创建了名为ExecutableMavenJar的Java类。

我们还需要确保我们的pom.xml包含这些元素：

```xml
<modelVersion>4.0.0</modelVersion>
<groupId>com.baeldung</groupId>
<artifactId>core-java</artifactId>
<version>0.1.0-SNAPSHOT</version>
<packaging>jar</packaging>
```

这里最重要的方面是类型——要创建一个可执行的jar，请仔细检查配置是否使用了jar类型。

现在我们可以开始使用各种解决方案了。

### 2.1. 手动配置

让我们在maven-dependency-plugin的帮助下从手动方法开始。

我们首先将所有必需的依赖项到我们将指定的文件夹中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-dependencies</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
            <configuration>
                <outputDirectory>
                    ${project.build.directory}/libs
                </outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

有两个重要方面需要注意。

首先，我们指定目标copy-dependencies，它告诉 Maven 将这些依赖项到指定的outputDirectory中。 在我们的例子中，我们将在项目构建目录(通常是目标文件夹)中创建一个名为libs的文件夹。

其次，我们将创建可执行文件和类路径感知jar，并在第一步中依赖项的链接：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <classpathPrefix>libs/</classpathPrefix>
                <mainClass>
                    com.baeldung.executable.ExecutableMavenJar
                </mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

其中最重要的部分是清单配置。我们添加一个包含所有依赖项的类路径(文件夹libs/)，并提供有关主类的信息。

请注意，我们需要提供类的完全限定名称，这意味着它将包含包名称。

这种方法的优点和缺点是：

-   优点——透明的过程，我们可以在其中指定每个步骤
-   缺点——手动；依赖项不在最终的jar中，这意味着我们的可执行jar仅在libs文件夹对于jar可访问和可见时才会运行

### 2.2. Apache Maven 程序集插件

Apache Maven Assembly Plugin 允许用户将项目输出及其依赖项、模块、站点文档和其他文件聚合到一个可运行的包中。

程序集插件中的主要目标是[单一](https://maven.apache.org/plugins/maven-assembly-plugin/single-mojo.html)目标，用于创建所有程序集(所有其他目标已弃用，将在未来版本中删除)。

我们看一下pom.xml中的配置：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
            <configuration>
                <archive>
                <manifest>
                    <mainClass>
                        com.baeldung.executable.ExecutableMavenJar
                    </mainClass>
                </manifest>
                </archive>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
            </configuration>
        </execution>
    </executions>
</plugin>
```

与手动方法类似，我们需要提供有关主类的信息。不同之处在于 Maven Assembly Plugin 会自动将所有需要的依赖项到一个jar文件中。

在配置代码的descriptorRefs部分，我们提供了将添加到项目名称中的名称。

我们示例中的输出将命名为core-java-jar-with-dependencies.jar。

-   优点– jar文件中的依赖项，只有一个文件
-   缺点——打包我们的工件的基本控制，例如，没有类重定位支持

### 2.3. Apache Maven 阴影插件

Apache Maven Shade 插件提供了将工件打包到uber-jar中的功能，其中包含运行项目所需的所有依赖项。此外，它支持对某些依赖项的包进行着色(即重命名)。

让我们看一下配置：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <shadedArtifactAttached>true</shadedArtifactAttached>
                <transformers>
                    <transformer implementation=
                      "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.baeldung.executable.ExecutableMavenJar</mainClass>
                </transformer>
            </transformers>
        </configuration>
        </execution>
    </executions>
</plugin>
```

此配置包含三个主要部分。

首先，<shadedArtifactAttached>将所有依赖项标记为要打包到jar中。

其次，我们需要指定[转换器的实现](https://maven.apache.org/plugins/maven-shade-plugin/usage.html)；我们在示例中使用了标准的。

最后，我们需要指定应用程序的主类。

输出文件将命名为core-java-0.1.0-SNAPSHOT-shaded.jar，其中core-java是我们的项目名称，后跟快照版本和插件名称。

-   优点– jar文件中的依赖项，对我们的工件进行打包的高级控制，以及着色和类重定位
-   缺点——复杂的配置(特别是如果我们想使用高级功能)

### 2.4. 一罐 Maven 插件

创建可执行jar 的另一种选择是 One Jar 项目。

这提供了一个自定义类加载器，它知道如何从存档中的 jars 加载类和资源，而不是从文件系统中的jars加载类和资源。

让我们看一下配置：

```xml
<plugin>
    <groupId>com.jolira</groupId>
    <artifactId>onejar-maven-plugin</artifactId>
    <executions>
        <execution>
            <configuration>
                <mainClass>org.baeldung.executable.
                  ExecutableMavenJar</mainClass>
                <attachToBuild>true</attachToBuild>
                <filename>
                  ${project.build.finalName}.${project.packaging}
                </filename>
            </configuration>
            <goals>
                <goal>one-jar</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

如配置所示，我们需要指定主类并将所有依赖项附加到构建中，方法是使用attachToBuild = true。

此外，我们应该提供输出文件名。此外，Maven 的目标是一个罐子。请注意 One Jar 是一种商业解决方案，它将使依赖jar在运行时不会扩展到文件系统中。

-   优点——干净的委托模型，允许类位于 One Jar 的顶层，支持外部jar并且可以支持本地库
-   缺点——自 2012 年以来未得到积极支持

### 2.5. Spring Boot Maven 插件

最后，我们要看的最后一个解决方案是 Spring Boot Maven 插件。

这允许打包可执行jar或war存档并“就地”运行应用程序。

要使用它，我们至少需要使用 Maven 版本 3.2。详细说明可[在此处](https://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/maven-plugin/)获得。

让我们看一下配置：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
            <configuration>
                <classifier>spring-boot</classifier>
                <mainClass>
                  com.baeldung.executable.ExecutableMavenJar
                </mainClass>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Spring plugin 和其他插件有两个不同点：执行的目标叫repackage，classifier 叫spring-boot。

请注意，我们不需要 Spring Boot 应用程序即可使用此插件。

-   优点——jar文件中的依赖项，我们可以在每个可访问的位置运行它，高级控制打包我们的工件，从jar文件中排除依赖项等， war文件的打包
-   缺点——添加可能不必要的 Spring 和 Spring Boot 相关类

### 2.6. 具有可执行 Tomcat 的 Web 应用程序

在最后一部分，我们要介绍一个打包在 aj ar文件中的独立 Web 应用程序。

为此，我们需要使用不同的插件，专为创建可执行 jar 文件而设计：

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.0</version>
    <executions>
        <execution>
            <id>tomcat-run</id>
            <goals>
                <goal>exec-war-only</goal>
            </goals>
            <phase>package</phase>
            <configuration>
                <path>/</path>
                <enableNaming>false</enableNaming>
                <finalName>webapp.jar</finalName>
                <charset>utf-8</charset>
            </configuration>
        </execution>
    </executions>
</plugin>
```

目标设置为exec-war-only，我们服务器的路径在配置标签内指定，具有其他属性，如finalName、charset等。

为了构建 aj ar，我们运行man package，这将导致在我们的目标目录中创建webapp.jar。

要运行该应用程序，我们只需在我们的控制台中编写java -jar target/webapp.jar并尝试通过在浏览器中指定localhost:8080 / 来测试它。

-   优点——只有一个文件，易于部署和运行
-   缺点——文件的大小要大得多，因为将 Tomcat 嵌入式分发打包在 aw ar文件中

请注意，这是该插件的最新版本，它支持 Tomcat7 服务器。为避免错误，我们可以检查我们对 Servlet 的依赖是否已按提供的范围设置，否则，在可执行jar的运行时会发生冲突：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <scope>provided</scope>
</dependency>
```

## 3.总结

在本文中，我们描述了使用各种 Maven 插件创建可执行jar的多种方法。

可以在这些 GitHub 项目中找到本教程的完整实现：[可执行 jar](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-jar)和[可执行 war](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-thymeleaf-5)。

如何测试？为了将项目编译成可执行的jar ，请使用mvn clean package命令运行 Maven 。

希望本文能提供更多见解，并帮助你根据需要找到首选方法。

一个简短的最后说明：我们要确保我们捆绑的 jar 的许可证不会禁止这种操作。通常情况并非如此，但值得考虑。
## 1. 概述

构建一个软件项目通常包括这样的任务，例如下载依赖项、将额外的jar放在类路径中、将源代码编译成二进制代码、运行测试、将编译后的代码打包成可部署的工件(例如JAR、WAR和ZIP文件)以及部署这些工件到应用程序服务器或仓库。

[Apache Maven](https://maven.apache.org/)将这些任务自动化，最大限度地降低了人工构建软件时人为出错的风险，并将编译和打包代码的工作与代码构建的工作分开。

在本教程中，我们将探索这个用于描述、构建和管理Java软件项目的强大工具，它使用以XML编写的核心信息-项目对象模型(POM)。

## 2. 为什么要使用Maven？

Maven的主要特点是：

-   **遵循最佳实践的简单项目设置**：Maven尝试通过提供项目模板(名为archetypes)来避免尽可能多的配置
-   **依赖关系管理**：它包括自动更新、下载和验证兼容性，以及报告依赖闭包(也称为传递依赖关系)
-   **项目依赖和插件之间的隔离**：使用Maven，项目依赖从依赖仓库中检索，而任何插件的依赖从插件仓库中检索，从而在插件开始下载其他依赖项时减少冲突
-   **中央仓库系统**：项目依赖可以从本地文件系统或者公共仓库加载，比如[Maven Central](https://search.maven.org/classic/)

**为了了解如何在你的系统上安装Maven，请查看**[本教程](https://www.baeldung.com/install-maven-on-windows-linux-mac)。

## 3. 项目对象模型

Maven项目的配置是通过项目对象模型(POM)完成的，由pom.xml文件表示。POM描述项目、管理依赖项并配置用于构建软件的插件。

POM还定义了多模块项目的模块之间的关系，让我们看一下典型的POM文件的基本结构：

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>tuyucheng</artifactId>
    <packaging>jar</packaging>
    <version>1.0.0</version>
    <name>cn.tuyucheng.taketoday</name>
    <url>http://maven.apache.org</url>
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.8.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                //...
            </plugin>
        </plugins>
    </build>
</project>
```

让我们仔细看看这些结构。

### 3.1 项目标识符

Maven使用一组标识符(也称为坐标)来唯一标识项目并指定项目工件的打包方式：

-   groupId：创建项目的公司或组的唯一基本名称
-   artifactId：项目的唯一名称
-   version：项目的一个版本
-   packaging：一种打包方法(例如WAR/JAR/ZIP)

其中的前三个(groupId:artifactId:version)组合在一起形成唯一标识符，并且是你指定项目将使用的外部库(例如JAR)版本的机制。

### 3.2 依赖关系

项目使用的这些外部库称为依赖项，Maven中的依赖项管理功能可确保从中央仓库自动下载这些库，因此你不必将它们存储在本地。

这是Maven的一个关键特性，并提供以下好处：

-   **通过显着减少远程仓库的下载次数来使用更少的存储空间**
-   **使签出项目更快**
-   **提供了一个有效的平台，用于在你的组织内外交换二进制工件，而无需每次都从源代码构建工件**

为了声明对外部库的依赖，你需要提供库的groupId、artifactId和version，让我们看一个例子：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.16</version>
</dependency>
```

当Maven处理依赖项时，它会将Spring Core库下载到本地Maven仓库中。

### 3.3 仓库

Maven中的仓库用于保存构建工件和不同类型的依赖项，默认本地仓库位于用户主目录下的.m2/repository文件夹中。

如果工件或插件在本地仓库中可用，Maven将使用它。否则，它将从中央仓库下载并存储在本地仓库中；默认的中央仓库是[Maven Central](https://search.maven.org/classic/#search|ga|1|centra)。

某些库(例如JBoss服务器)在中央仓库中不可用，但在备用仓库中可用。对于这些库，你需要在pom.xml文件中提供备用仓库的URL：

```xml
<repositories>
    <repository>
        <id>JBoss repository</id>
        <url>http://repository.jboss.org/nexus/content/groups/public/</url>
    </repository>
</repositories>
```

**请注意，你可以在项目中使用多个仓库**。

### 3.4 属性

自定义属性有助于使你的pom.xml文件更易于阅读和维护，在经典用例中，你将使用自定义属性来定义项目依赖项的版本。

**Maven属性是值占位符，可以使用${name}表示法在pom.xml中的任何位置访问**，其中name是属性。

让我们看一个例子：

```xml
<properties>
    <spring.version>5.3.16</spring.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>${spring.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>${spring.version}</version>
    </dependency>
</dependencies>
```

现在，如果你想将Spring升级到更新的版本，你只需更改<spring.version\>属性标签内的值，并且所有在其<version\>标签中使用该属性的依赖项都将更新。

属性也经常用于定义构建路径变量：

```xml
<properties>
    <project.build.folder>${project.build.directory}/tmp/</project.build.folder>
</properties>

<plugin>
    // ...
    <outputDirectory>${project.resources.build.folder}</outputDirectory>
    // ...
</plugin>
```

### 3.5 构建

构建部分也是Maven POM中非常重要的部分，它提供有关默认Maven目标、已编译项目的目录和应用程序的最终名称的信息，默认构建部分如下所示：

```xml
<build>
    <defaultGoal>install</defaultGoal>
    <directory>${basedir}/target</directory>
    <finalName>${artifactId}-${version}</finalName>
    <filters>
        <filter>filters/filter1.properties</filter>
    </filters>
    // ...
</build>
```

**已编译项目的默认输出文件夹名为target，打包工件的最终名称由artifactId和version组成，但你可以随时更改它**。

### 3.6 使用Profile

Maven的另一个重要特性是它对Profile的支持，Profile基本上是一组配置值，通过使用Profile，你可以为不同的环境(例如生产/测试/开发)自定义构建：

```xml
<profiles>
    <profile>
        <id>production</id>
        <build>
            <plugins>
                <plugin>
                    //...
                </plugin>
            </plugins>
        </build>
    </profile>
    <profile>
        <id>development</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <build>
            <plugins>
                <plugin>
                    // ...
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

正如你在上面的示例中看到的，默认Profile设置为development，如果要运行production Profile，可以使用以下Maven命令：

```bash
mvn clean install -Pproduction
```

## 4. Maven构建生命周期

每个Maven构建都遵循指定的生命周期，你可以执行多个构建生命周期目标，包括编译项目代码、创建包以及在本地Maven依赖项仓库中安装存档文件的目标。

### 4.1 生命周期阶段

以下列表显示了最重要的Maven生命周期阶段：

-   validate：检查项目的正确性
-   compile：将提供的源代码编译成二进制工件
-   test：执行单元测试
-   package：将编译后的代码打包到归档文件中
-   integration-test：执行额外的测试，这需要打包
-   verify：检查包是否有效
-   install：将包文件安装到本地Maven仓库中
-   deploy：将包文件部署到远程服务器或仓库

### 4.2 插件和目标

Maven插件是一个或多个目标的集合，目标是分阶段执行的，这有助于确定目标执行的顺序。

Maven官方支持的丰富插件列表可在[此处](https://maven.apache.org/plugins/)获得，**还有一篇关于如何使用各种插件构建可执行JAR的有趣文章，可在**[此处](https://www.baeldung.com/executable-jar-with-maven)**找到**。

为了更好地了解默认情况下在哪些阶段运行哪些目标，请查看[默认的Maven生命周期绑定](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Built-in_Lifecycle_Bindings)。

要完成上述任何一个阶段，我们只需要调用一个命令：

```bash
mvn <phase>
```

例如，mvn clean install将删除先前创建的jar/war/zip文件和编译的类(clean)，并执行安装新存档(install)所需的所有阶段。

**请注意，插件提供的目标可以与生命周期的不同阶段相关联**。

## 5. 你的第一个Maven项目

在本节中，我们将使用Maven的命令行功能来创建Java项目。

### 5.1 生成一个简单的Java项目

为了构建一个简单的Java项目，让我们运行以下命令：

```bash
mvn archetype:generate \
  -DgroupId=cn.tuyucheng.taketoday \
  -DartifactId=tuyucheng \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DarchetypeVersion=1.4 \
  -DinteractiveMode=false
```

groupId是表示创建项目的组或个人的参数，通常是公司域名的倒写。artifactId是项目中使用的基础包名称，我们使用标准的archetype。在这里，我们使用最新的原型版本来确保我们的项目是使用更新的和最新的结构创建的。

由于我们没有指定版本和打包类型，因此这些将设置为默认值-版本将设置为1.0-SNAPSHOT，默认情况下打包为jar。

**如果你不知道要提供哪些参数，你始终可以指定interactiveMode = true，这样Maven就会询问所有必需的参数**。

命令完成后，我们在src/main/java文件夹中有一个包含App.java类的Java项目，它只是一个简单的“Hello World”程序。

我们在src/test/java中也有一个示例测试类。该项目的pom.xml看起来类似于：

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>tuyucheng</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>tuyucheng</name>
    <url>http://www.example.com</url>
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

如你所见，默认情况下提供了JUnit依赖项。

### 5.2 编译和打包项目

下一步是编译项目：

```bash
mvn compile
```

Maven将运行编译阶段所需的所有生命周期阶段，以构建项目的源代码。如果你只想运行测试阶段，可以使用：

```bash
mvn test
```

现在让我们调用package阶段，它将生成编译后的归档jar文件：

```bash
mvn package
```

### 5.3 执行应用程序

最后，我们将使用exec-maven-plugin执行我们的Java项目，让我们在pom.xml中配置必要的插件：

```xml
<build>
    <sourceDirectory>src</sourceDirectory>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.6.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.0.0</version>
            <configuration>
                <mainClass>cn.tuyucheng.taketoday.java.App</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

第一个插件[maven-compiler-plugin](https://search.maven.org/search?q=a:maven-compiler-plugin)负责使用Java 1.8版本编译源代码，[exec-maven-plugin](https://search.maven.org/search?q=a:exec-maven-plugin)在我们的项目中搜索mainClass。

要执行应用程序，我们运行以下命令：

```bash
mvn exec:java
```

## 6. 总结

在本文中，我们讨论了Apache Maven构建工具的一些更受欢迎的特性。

该仓库上的所有代码示例都是使用Maven构建的，因此你可以轻松访问我们的[GitHub项目网站](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/maven-modules/maven-simple)以查看各种Maven配置。
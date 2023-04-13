## 1. 概述

Spring Boot是快速启动新项目的优秀框架。它帮助开发人员快速创建新应用程序的方法之一是定义一组适合大多数用户的依赖项。

但是，在某些情况下，**可能需要覆盖一个或多个依赖的版本**。

在本教程中，我们将了解如何覆盖Spring Boot管理的依赖项及其版本。

## 2. Spring Boot BOM

让我们先看看Spring Boot是如何管理依赖关系的。简而言之，Spring Boot使用[物料清单(BOM)](https://www.baeldung.com/spring-maven-bom)来定义依赖项和版本。

大多数Spring Boot项目继承自[spring-boot-starter-parent](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.5)工件，它本身继承自[spring-boot-dependencies](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-dependencies/3.0.5)工件。**后一个工件是Spring Boot BOM**，它只是一个具有大型dependencyManagement部分的Maven POM文件：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <!--...-->
        </dependency>
        <dependency>
            <!--...-->
        </dependency>
    </dependencies>
</dependencyManagement>
```

通过使用Maven的dependencyManagement，**BOM可以指定默认库版本(如果我们的应用程序选择使用它们)**。让我们看一个例子。

Spring Boot BOM中的条目之一如下：

```xml
<dependency>
    <groupId>org.apache.activemq</groupId>
    <artifactId>activemq-amqp</artifactId>
    <version>${activemq.version}</version>
</dependency>
```

这意味着项目中依赖于ActiveMQ的任何工件都将默认获得此版本。

另外，请注意**版本是使用属性占位符指定的**。这是Spring Boot BOM中的常见做法，它在自己的properties部分中提供此属性和其他属性的值。

## 3. 覆盖Spring Boot托管依赖版本

现在我们了解了Spring Boot如何管理依赖版本，让我们看看如何覆盖它们。

### 3.1 Maven

对于Maven，我们有两个选项来覆盖Spring Boot管理的依赖项。首先，对于Spring Boot BOM使用属性占位符指定版本的任何依赖项，**我们只需要在我们的项目POM中设置该属性**：

```xml
<properties>
    <activemq.version>5.16.3</activemq.version>
</properties>
```

这将导致任何使用activemq.version属性的依赖项使用我们指定的版本，而不是Spring Boot BOM中的版本。

此外，如果版本是在BOM的dependency标签中明确指定的，而不是作为占位符，那么我们可以简单地在项目依赖条目中显式覆盖version：

```xml
<dependency>
    <groupId>org.apache.activemq</groupId>
    <artifactId>activemq-amqp</artifactId>
    <version>5.16.3</version>
</dependency>
```

### 3.2 Gradle

**Gradle需要一个插件来支持Spring Boot BOM的依赖管理**。因此，要开始使用，我们必须包含插件并导入BOM：

```groovy
apply plugin: "io.spring.dependency-management"
dependencyManagement {
    imports {
        mavenBom 'io.spring.platform:platform-bom:2.5.5'
    }
}
```

现在，如果我们想要覆盖特定版本的依赖项，我们只需要将BOM中的相应属性指定为Gradle ext属性：

```groovy
ext['activemq.version'] = '5.16.3'
```

如果BOM中没有要覆盖的属性，我们始终可以在声明依赖项时直接指定版本：

```groovy
compile 'org.apache.activemq:activemq-amqp:5.16.3'
```

### 3.3 注意事项

这里有几个注意事项值得一提。

对于初学者来说，重要的是要记住Spring Boot是使用BOM中指定的库版本构建和测试的。**每当我们指定不同的库版本时，都存在引入不兼容的风险**。因此，每当我们偏离标准依赖版本，就必须测试我们的应用程序。

另外，请记住，**这些技巧仅在我们使用Spring Boot物料清单(BOM)时适用**。对于Maven来说，这意味着使用Spring Boot Parent。对于Gradle，这意味着使用Spring依赖插件。

## 4. 查找依赖版本

我们已经了解了Spring Boot如何管理依赖版本以及我们如何覆盖它们。在本节中，我们将了解如何找到我们的项目正在使用的库版本。**这对于识别库版本和确认我们应用于项目的任何覆盖都得到遵循很有用**。 

### 4.1 Maven

Maven提供了一个[目标(goal)](https://www.baeldung.com/maven-goals-phases)，我们可以使用该目标来显示所有依赖项及其版本的列表。例如，如果我们运行以下命令：

```shell
mvn dependency:tree
```

我们应该看到类似于以下内容的输出：

```shell
[INFO] cn.tuyucheng.taketoday:dependency-demo:jar:1.0.0
[INFO] +- org.springframework.boot:spring-boot-starter-web:jar:2.5.7-SNAPSHOT:compile
[INFO] |    +- org.springframework.boot:spring-boot-starter:jar:2.5.7-SNAPSHOT:compile
[INFO] |    |   +- org.springframework.boot:spring-boot:jar:2.5.7-SNAPSHOT:compile
[INFO] |    |   +- org.springframework.boot:spring-boot-autoconfigure:jar:2.5.7-SNAPSHOT:compile
[INFO] |    |   +- org.springframework.boot:spring-boot-starter-logging:jar:2.5.7-SNAPSHOT:compile
[INFO] |    |   |   +- ch.qos.logback:logback-classic:jar:1.2.6:compile
[INFO] |    |   |   |   \- ch.qos.logback:logback-core:jar:1.2.6:compile
```

输出显示作为项目依赖项的所有工件和版本。**这些依赖项以树形结构呈现**，可以轻松识别每个工件是如何导入到项目中的。

在上面的示例中，logback-classic工件是spring-boot-starter-logging库的依赖项，而spring-boot-starter-log库本身是spring-boot-starter模块的依赖项。因此，我们可以向上导航树回到我们的顶级项目。

### 4.2 Gradle

Gradle提供了生成类似依赖树的任务。例如，如果我们运行以下命令：

```shell
gradle dependencies
```

我们将得到类似于以下内容的输出：

```shell
compileClasspath - Compile classpath for source set 'main'.
\--- org.springframework.boot:spring-boot-starter-web -> 1.3.8.RELEASE
     +--- org.springframework.boot:spring-boot-starter:1.3.8.RELEASE
     |    +--- org.springframework.boot:spring-boot:1.3.8.RELEASE
     |    |    +--- org.springframework:spring-core:4.2.8.RELEASE
     |    |    \--- org.springframework:spring-context:4.2.8.RELEASE
     |    |         +--- org.springframework:spring-aop:4.2.8.RELEASE
```

就像Maven输出一样，我们可以很容易地识别为什么每个工件被拉入项目，以及正在使用的版本。

## 5. 总结

在文章中，我们了解了Spring Boot是如何管理依赖版本的。我们还看到了如何在Maven和Gradle中覆盖这些依赖版本。最后，我们看到了如何在两种项目类型中验证依赖项版本。
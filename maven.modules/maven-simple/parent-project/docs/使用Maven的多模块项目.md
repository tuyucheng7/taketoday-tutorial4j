## 1. 概述

在本教程中，我们将学习如何使用Maven构建多模块项目。

首先，我们将讨论什么是多模块项目，并了解采用这种方法的好处，然后我们将设置我们的示例项目。有关Maven的详细介绍，请查看[本教程](https://www.baeldung.com/maven)。

## 2. Maven的多模块项目

多模块项目是从管理一组子模块的聚合器POM构建的，在大多数情况下，聚合器位于项目的根目录中，并且必须具有pom类型的打包方式(<packaging\>标签的值)。

子模块是常规的Maven项目，它们可以单独构建，也可以通过聚合器POM构建。

通过聚合器POM构建项目，每个具有不同于pom的打包类型的项目将生成一个构建的存档文件。

## 3. 使用多模块的好处

使用这种方法的显著优势是我们**可以减少重复**。

假设我们有一个由多个模块组成的应用程序，一个前端模块和一个后端模块，现在想象一下我们对它们进行了处理并更改了功能，这会影响它们。在这种情况下，如果没有专门的构建工具，我们就必须分别构建这两个组件或者编写脚本来编译代码、运行测试并显示结果。然后，当我们在项目中得到更多的模块时，它会变得更难管理和维护。

在现实世界中，项目可能需要某些Maven插件来在[构建生命周期](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)中执行各种操作、共享依赖项和Profiles以及包含其他[BOM项目](https://www.baeldung.com/spring-maven-bom)。

因此，在利用多模块时，我们可以**在单个命令中构建应用程序的模块**，如果顺序很重要，Maven会为我们解决，**我们还可以与其他模块共享大量配置**。

## 4. 父POM

**Maven以每个pom.xml文件都具有隐式父POM的方式支持继承，它称为Super POM**，该Super POM位于Maven二进制文件中，这两个文件由Maven合并并形成Effective POM。

**我们可以创建自己的pom.xml文件，它将作为父项目为我们服务**，然后我们可以在其中包含所有具有依赖关系的配置，并将其设置为我们子模块的父级，这样它们就会继承它。

除了继承之外，Maven还提供了聚合的概念，利用此功能的父POM称为聚合POM。基本上，这种POM**在其pom.xml文件中显式声明其模块**。

## 5. 子模块

子模块或子项目是从父POM继承的常规Maven项目，正如我们已经知道的，继承允许我们可以与子模块共享配置和依赖关系。但是，如果我们想一次性构建或发布我们的项目，我们必须在父POM中显式声明我们的子模块。最终，我们的父POM将成为父POM，聚合POM也是如此。

## 6. 构建应用程序

现在我们了解了Maven的子模块和层次结构，让我们构建一个示例应用程序来演示它们，我们将使用Maven的命令行界面来生成我们的项目。

该应用程序将由三个模块组成，分别代表：

-   我们领域的核心部分(core模块)
-   提供一些REST API的Web服务(service模块)
-   包含某种面向用户的Web资产的Web应用程序(webapp模块)

由于我们将专注于Maven，因此这些服务的实现将保持未定义状态。

### 6.1 生成父POM

**首先，让我们创建一个父项目**：

```plaintext
mvn archetype:generate -DgroupId=cn.tuyucheng.taketoday -DartifactId=parent-project
```

生成父级后，我们必须打开位于父级目录中的pom.xml文件并将packaging添加为pom：

```xml
<packaging>pom</packaging>
```

通过将packaging设置为pom类型，我们声明该项目将充当父项目或聚合器；它不会产生更多的工件。

现在，当我们的聚合器完成时，我们可以生成我们的子模块。

但是，我们需要注意的是，这是所有要共享的配置所在的位置，最终将在子模块中重复使用。除此之外，我们可以在这里使用dependencyManagement或pluginManagement。

### 6.2 创建子模块

由于我们的父POM被命名为parent-project，我们需要确保我们在父目录中并运行generate命令：

```plaintext
cd parent-project
mvn archetype:generate -DgroupId=cn.tuyucheng.taketoday -DartifactId=core
mvn archetype:generate -DgroupId=cn.tuyucheng.taketoday -DartifactId=service
mvn archetype:generate -DgroupId=cn.tuyucheng.taketoday -DartifactId=webapp
```

请注意使用的命令，它与我们用于父级的相同。这里的问题是，这些模块是常规的Maven项目，但Maven认识到它们是嵌套的，当我们将目录定位到parent-project时，发现父项目有pom类型的打包，它会相应地修改pom.xml文件。

在parent-project的pom.xml中，它将在modules部分添加所有子模块：

```xml
<modules>
    <module>core</module>
    <module>service</module>
    <module>webapp</module>
</modules>
```

在各个子模块的pom.xml中，它将在parent部分中添加父项目：

```xml
<parent>
    <artifactId>parent-project</artifactId>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <version>1.0.0</version>
</parent>
```

接下来Maven会成功生成三个子模块。

重要的是要注意子模块只能有一个父模块，但是，我们可以导入许多BOM。有关BOM文件的更多详细信息，请参阅[本文](https://www.baeldung.com/spring-maven-bom)。

### 6.3 构建项目

**现在我们可以同时构建所有三个模块**，在父项目目录中，我们将运行：

```bash
mvn package
```

这将构建所有模块，我们应该看到命令的以下输出：

```plaintext
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO] parent-project                                                     [pom]
[INFO] core                                                               [jar]
[INFO] service                                                            [jar]
[INFO] webapp                                                             [war]
...
[INFO] Reactor Summary for parent-project 1.0.0:
[INFO] parent-project ..................................... SUCCESS [  0.272 s]
[INFO] core ............................................... SUCCESS [  2.043 s]
[INFO] service ............................................ SUCCESS [  0.627 s]
[INFO] webapp ............................................. SUCCESS [  0.572 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

Reactor列出了parent-project，但由于它是pom类型，因此将其排除在外，并且构建结果为所有其他模块生成了三个单独的.jar文件。在这种情况下，构建发生在其中三个子模块中。

此外，Maven Reactor将分析我们的项目并以正确的顺序构建它。因此，如果我们的webapp模块依赖于service模块，Maven将首先构建service，然后是webapp。

### 6.3 在父项目中启用依赖管理

依赖关系管理是一种集中多模块父项目及其子项目的依赖信息的机制。

当你有一组项目或模块继承了共同的父级时，你可以将有关依赖项的所有必需信息放在公共的pom.xml文件中，这将简化对子POM中工件的引用。

让我们来看看一个示例父级的pom.xml：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>5.3.16</version>
        </dependency>
        // ...
    </dependencies>
</dependencyManagement>
```

通过在父级中声明spring-core的版本，所有依赖于spring-core的子模块都可以只使用groupId和artifactId来声明依赖，并且版本将被继承：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
    </dependency>
    // ...
</dependencies>
```

此外，你可以在父级的pom.xml中为依赖管理提供排除项，这样特定的库就不会被子模块继承：

```xml
<exclusions>
    <exclusion>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </exclusion>
</exclusions>
```

最后，如果子模块需要使用不同版本的托管依赖项，你可以在子模块的pom.xml文件中覆盖托管版本：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>4.3.30.RELEASE</version>
</dependency>
```

**请注意，虽然子模块继承自其父项目，但父项目不一定具有它聚合的任何模块。另一方面，父项目也可以聚合不从它继承的项目**。

有关继承和聚合的更多信息，请参阅[此文档](https://maven.apache.org/pom.html#Aggregation)。

### 6.4 更新子模块并构建项目

我们可以更改每个子模块的打包类型，例如让我们通过更新pom.xml文件，将webapp模块的打包方式改为war：

```xml
<packaging>war</packaging>
```

并在插件列表中添加maven-war-plugin：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>3.3.2</version>
            <configuration>
               <failOnMissingWebXml>false</failOnMissingWebXml>
            </configuration>
        </plugin>
    </plugins>
</build>
```

现在我们可以使用mvn clean install命令测试项目的构建，Maven日志的输出应类似于以下内容：

```plaintext
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO] 
[INFO] parent-project                                                     [pom]
[INFO] core                                                               [jar]
[INFO] service                                                            [jar]
[INFO] webapp                                                             [war]
//............. 
[INFO] Reactor Summary for parent-project 1.0.0:
[INFO] 
[INFO] parent-project ..................................... SUCCESS [  0.272 s]
[INFO] core ............................................... SUCCESS [  2.043 s]
[INFO] service ............................................ SUCCESS [  0.627 s]
[INFO] webapp ............................................. SUCCESS [  1.047 s]
```

## 7. 总结

在本文中，我们讨论了使用Maven多模块的好处，我们还区分了常规Maven的父POM和聚合POM。最后，我们探讨了如何设置一个简单的多模块来开始使用。

Maven是一个很棒的工具，但它本身很复杂，如果我们想了解有关Maven的更多详细信息，我们可以查看[Sonatype Maven参考](https://books.sonatype.com/mvnref-book/reference/index.html)或[Apache Maven指南](https://maven.apache.org/guides/index.html)。如果我们寻求Maven的多模块设置的高级用法，我们可以看看[Spring Boot项目](https://github.com/spring-projects/spring-boot)如何利用它的用法。
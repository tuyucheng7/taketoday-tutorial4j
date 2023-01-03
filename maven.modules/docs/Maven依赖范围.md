## 1. 概述

Maven是Java生态系统中最受欢迎的构建工具之一，其核心功能之一是依赖关系管理。

在本教程中，我们将描述和探索有助于管理Maven项目中的传递依赖项的机制-依赖范围。

## 2. 传递依赖

**Maven中有两种类型的依赖关系：直接依赖关系和传递依赖关系**。

直接依赖项是我们明确包含在项目中的那些，这些可以使用<dependency\>标签包含：

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```

**另一方面，直接依赖关系需要传递依赖关系，Maven会自动在我们的项目中包含所需的传递依赖项**。

我们可以使用mvn dependency:tree命令列出项目中的所有依赖项，包括传递依赖项。

## 3. 依赖范围

依赖范围可以帮助限制依赖的传递性，他们还为不同的构建任务修改类路径，**Maven有六个默认的依赖范围**。

重要的是要了解每个范围(import除外)都会对传递依赖项产生影响。

### 3.1 compile

**当没有提供其他范围时，这是默认范围**。

具有此范围的依赖项在所有构建任务的项目类路径中都可用，它们还会传播到依赖项目。

更重要的是，这些依赖关系也是可传递的：

```xml
<dependency>
    <groupId>commons-lang</groupId>
    <artifactId>commons-lang</artifactId>
    <version>2.6</version>
</dependency>
```

### 3.2 provided

我们使用这个范围来**标记应该由JDK或容器在运行时提供的依赖项**。

这个范围的一个很好的用例是部署在某个容器中的Web应用程序，其中容器本身已经提供了一些库。例如，这可能是一个已经在运行时提供Servlet API的Web服务器。

在我们的项目中，我们可以使用provided范围定义这些依赖项：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>
```

provided依赖项仅在编译时和项目的测试类路径中可用，这些依赖关系也不是可传递的。

### 3.3 runtime

**在运行时需要具有此范围的依赖项**，但是我们不需要它们来编译项目代码。因此，标有runtime范围的依赖项将存在于运行时和测试类路径中，但它们将从编译类路径中消失。

JDBC驱动程序是应该使用runtime范围的依赖项的一个很好的例子：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.28</version>
    <scope>runtime</scope>
</dependency>
```

### 3.4 test

我们使用此范围来指示在应用程序的标准运行时不需要依赖性，但仅用于测试目的。

**test依赖项不可传递，仅存在于测试和执行类路径中**。

此范围的标准用例是将JUnit等测试库添加到我们的应用程序中：

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

### 3.5 system

**system范围与provided范围非常相似，主要区别在于system要求我们直接指向系统上的特定jar**。

值得一提的是[system范围已被弃用](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#system-dependencies)。

要记住的重要一点是，如果依赖项不存在或位于与systemPath指向的位置不同的位置，则使用system范围依赖项构建项目可能会在不同的机器上失败：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>custom-dependency</artifactId>
    <version>1.3.2</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/custom-dependency-1.3.2.jar</systemPath>
</dependency>
```

### 3.6 import

**它仅适用于依赖类型pom**。

import指示此依赖项应替换为其POM中声明的所有有效依赖项。

在这里，下面的custom-project依赖项将替换为custom-project的pom.xml文件<dependencyManagement\>部分中声明的所有依赖项。

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>custom-project</artifactId>
    <version>1.3.2</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

## 4. 范围和传递性

每个依赖范围以其自己的方式影响传递依赖项，这意味着不同的传递依赖项可能最终会出现在具有不同范围的项目中。

但是，provided范围和test的依赖项永远不会包含在主项目中。

让我们详细看看这意味着什么：

-   对于compile范围，所有具有runtime范围的依赖项将被拉入项目中的runtime范围，并且所有具有compile范围的依赖项将被拉入项目中的compile范围。
-   对于provided范围，runtime和compile范围依赖项都将与项目中provided范围一起引入。
-   对于test范围，runtime和compile范围的传递依赖项都将与项目中的test范围一起被拉入。
-   对于runtime范围，runtime和compile范围的传递依赖项都将与项目中的runtime范围一起被拉入。

## 5. 总结

在这篇简短的文章中，我们重点介绍了Maven依赖范围、它们的用途以及它们如何操作的细节。

要更深入地了解Maven，[官方文档](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)是一个很好的起点。
## 1. 概述

在本快速教程中，我们将了解 Maven 这一基于项目对象模型 (POM) 概念的工具如何使用 BOM 或“物料清单”。

有关 Maven 的更多详细信息，你可以查看我们的文章[Apache Maven 教程](https://www.baeldung.com/maven)。

## 2. 依赖管理概念

要了解什么是 BOM 以及我们可以使用它做什么，我们首先需要学习基本概念。

### 2.1. 什么是 Maven POM？

Maven POM 是一个 XML 文件，其中包含 Maven 用来导入依赖项和构建项目的信息和配置(关于项目)。

### 2.2. 什么是 Maven BOM？

BOM 代表物料清单。BOM 是一种特殊的 POM，用于控制项目依赖项的版本，并提供一个中心位置来定义和更新这些版本。

BOM 提供了向我们的模块添加依赖项的灵活性，而无需担心我们应该依赖的版本。

### 2.3. 传递依赖

Maven 可以在我们的pom.xml中发现我们自己的依赖项所需的库并自动包含它们。从中收集库的依赖级别的数量没有限制。

当 2 个依赖项引用特定工件的不同版本时，就会出现冲突。Maven 将包含哪一个？

这里的答案是“最近的定义”。这意味着使用的版本将是依赖关系树中最接近我们项目的版本。这称为依赖调解。

让我们看下面的例子来阐明依赖中介：

```java
A -> B -> C -> D 1.4  and  A -> E -> D 1.0
```

此示例显示项目A依赖于B和E。B 和E有自己的依赖项，它们会遇到不同版本的工件D。工件D 1.0 将用于A项目的构建，因为通过E的路径更短。

有不同的技术来确定应包括哪个版本的工件：

-   我们始终可以通过在项目的 POM 中显式声明来保证版本。例如，为了保证使用D 1.4，我们应该将其显式添加为pom.xml文件中的依赖项。
-   我们可以使用Dependency Management部分来控制工件版本，我们将在本文后面解释。

### 2.4. 依赖管理

简单地说，依赖管理是一种集中依赖信息的机制。

当我们有一组项目继承了一个共同的父项目时，我们可以将所有依赖信息放在一个名为 BOM 的共享 POM 文件中。

以下是如何编写 BOM 文件的示例：

```xml
<project ...>
	
    <modelVersion>4.0.0</modelVersion>
    <groupId>baeldung</groupId>
    <artifactId>Baeldung-BOM</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>BaelDung-BOM</name>
    <description>parent pom</description>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>test</groupId>
                <artifactId>a</artifactId>
                <version>1.2</version>
            </dependency>
            <dependency>
                <groupId>test</groupId>
                <artifactId>b</artifactId>
                <version>1.0</version>
                <scope>compile</scope>
            </dependency>
            <dependency>
                <groupId>test</groupId>
                <artifactId>c</artifactId>
                <version>1.0</version>
                <scope>compile</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

如我们所见，BOM 是一个普通的 POM 文件，带有一个dependencyManagement部分，我们可以在其中包含所有工件的信息和版本。

### 2.5. 使用 BOM 文件

有两种方法可以在我们的项目中使用之前的 BOM 文件，然后我们就可以声明我们的依赖项，而不必担心版本号。

我们可以从父级继承：

```xml
<project ...>
    <modelVersion>4.0.0</modelVersion>
    <groupId>baeldung</groupId>
    <artifactId>Test</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>Test</name>
    <parent>
        <groupId>baeldung</groupId>
        <artifactId>Baeldung-BOM</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
</project>
```

正如我们所看到的，我们的项目 Test 继承了 Baeldung-BOM。

我们还可以导入 BOM。

在较大的项目中，继承的方法效率不高，因为项目只能继承一个父项目。导入是另一种选择，因为我们可以根据需要导入任意数量的 BOM。

让我们看看如何将 BOM 文件导入到我们的项目 POM 中：

```xml
<project ...>
    <modelVersion>4.0.0</modelVersion>
    <groupId>baeldung</groupId>
    <artifactId>Test</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>Test</name>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>baeldung</groupId>
                <artifactId>Baeldung-BOM</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 2.6. 覆盖 BOM 相关性

工件版本的优先顺序是：

1.  我们项目pom中神器直接声明的版本
2.  父项目中工件的版本
3.  导入的 pom 中的版本，考虑到导入文件的顺序
4.  依赖调解

-   我们可以通过在项目的 pom 中使用所需版本明确定义工件来覆盖工件的版本
-   如果同一个工件在 2 个导入的 BOM 中定义了不同的版本，则 BOM 文件中最先声明的版本将获胜

### 3. 春季物料清单

我们可能会发现第三方库或另一个 Spring 项目引入了对旧版本的传递依赖。如果我们忘记显式声明直接依赖项，就会出现意想不到的问题。

为了克服这些问题，Maven 支持 BOM 依赖的概念。

我们可以在 dependencyManagement 部分导入spring - framework-bom以确保所有 Spring 依赖项都处于同一版本：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-framework-bom</artifactId>
            <version>4.3.8.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

当我们使用 Spring 工件时，我们不需要指定版本属性，如下例所示：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-web</artifactId>
    </dependency>
<dependencies>
```

### 4. 总结

在这篇简短的文章中，我们展示了 Maven 物料清单概念以及如何将工件的信息和版本集中在一个公共 POM 中。

简而言之，我们可以继承或导入它以利用 BOM 的好处。
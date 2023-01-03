## 1. 概述

手动构建一个复杂的项目是相当麻烦的，使用构建工具有更简单的方法来做到这一点。众所周知，Java项目的主要构建工具之一是[Maven](https://www.baeldung.com/maven)，Maven有助于标准化应用程序的构建和部署。

在本教程中，我们将讨论Maven工件是什么以及它的关键元素是什么。我们还将查看Maven坐标、依赖项管理，最后是Maven仓库。

## 2. 什么是Maven

**我们可以使用Maven来构建和管理任何基于Java的项目**，它提供了许多功能，例如：

-   构建和编译
-   文件和报告
-   依赖关系管理
-   资源管理
-   项目更新
-   部署

每个Maven项目都有自己的[POM](https://www.baeldung.com/maven-super-simplest-effective-pom)文件，我们可以配置Maven来构建一个项目或多个项目。

多项目通常在根目录中定义一个POM文件，并在<modules\>部分列出各个项目。简而言之，**一次Maven构建会产生一个或多个工件**。

## 3. 什么是Maven工件

工件(artifact)是项目可以使用或生产的元素，在Maven术语中，**工件是在Maven项目构建后生成的输出**。例如，它可以是jar、war或任何其他可执行文件。

此外，Maven工件包括五个关键元素，groupId、artifactId、version、packaging和classifier。这些是我们用来识别工件的元素，被称为Maven坐标。

## 4. Maven坐标

**Maven坐标是给定工件的groupId、artifactId和version值的组合**。此外，Maven使用坐标来查找与groupId、artifactId和version的值匹配的任何组件。

在坐标元素中，我们必须定义groupId、artifactId和version。packaging元素是可选的，我们不能直接定义classifier。

例如，下面的pom.xml配置文件显示了Maven坐标的示例：

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>cn.tuyucheng.taketoday.java</artifactId>
    <packaging>jar</packaging>
    <version>1.0.0</version>
    <name>cn.tuyucheng.taketoday.java</name>
    <url>http://maven.apache.org</url>
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.1.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

让我们详细看看每个Maven坐标。

### 4.1 groupId元素

**groupId元素是项目起源处的组的标识符**，使用此元素可以更轻松、更快捷地组织和查找项目。

另外，groupId遵循与Java包相同的命名规则，我们通常选择项目的顶层包名作为groupId。

例如，org.apache.commons的groupId对应于${repository_home}/org/apache/commons。

### 4.2 artifactId元素

**artifactId元素是组内项目的标识符**，默认情况下，它用于构造项目的最终名称。因此，这个名称具有一些规范，理想情况下它的长度应该很小。命名artifactId的最佳做法是使用实际项目名称作为前缀，这样做的好处是可以更容易地找到工件。

与groupId一样，artifactId将自身显示为代表groupId的目录树下的子目录。

例如，在org.apache.commons的groupId下，commons-lang3的artifactId将确定工件位于：${repository_home}/org/apache/commons/commons-lang3/。

### 4.3 version元素

version用作工件标识符的一部分，**它定义了Maven项目的当前版本**。我们应该注意到Maven定义了一组版本规范，以及RELEASE和SNAPSHOT的概念，我们将在后面介绍。

版本表示为目录树中的子目录，该子目录由groupId和artifactId组成。

例如，org.apache.commons groupId下的artifactId commons-lang3的版本3.1.1将确定工件位于：${repository_home}/org/apache/commons/commons-lang3/3.1.1/。

### 4.4 packaging元素

该元素用于指定项目生成的工件类型，**packaging可以是描述任何二进制软件格式的任何东西**，包括ZIP、EAR、WAR、SWC、NAR、SWF、SAR。

此外，packaging定义了在项目的默认生命周期内执行的不同目标。例如，package阶段执行jar:jar目标用于jar类型工件，以及war:war目标用于war类型工件。

### 4.5 classifier元素

**出于技术原因，我们通常在交付相同的代码但作为几个单独的工件时使用分类器**。

例如，如果我们想要使用不同的Java编译器构建一个JAR的两个工件，我们可以使用分类器轻松完成，因为它允许使用groupId:artifactId:version的相同组合生成两个不同的工件。

此外，我们可以在打包源代码、工件的JavaDoc或组装二进制文件时使用分类器。

对于我们上面的commons-lang3示例，要查找的工件是：commons-lang3-3.10-javadoc.jar或commons-lang3-3.10-sources.jar，位于${repository_home}/org/apache/commons/commons-lang3/3.1.0/。

## 5. 发布与快照工件

现在，让我们看看快照工件和发布工件之间的区别。

### 5.1 发布工件

**发布工件表示该版本是稳定的，可以在集成测试、客户资格认证、预生产等开发过程之外使用**。

此外，**发布工件是唯一的**，运行mvn deploy命令会将我们的项目部署到仓库。但是，在同一个版本的同一个项目上，后续执行同一个命令会失败。

### 5.2 快照工件

**快照工件表示该项目正在开发中**，当我们安装或发布组件时，Maven会检查版本属性。如果它包含字符串“SNAPSHOT”，Maven会将此键转换为UTC(协调世界时)格式的日期和时间值。

例如，如果我们的项目是1.0-SNAPSHOT版本并且我们将其工件部署在Maven仓库中，则Maven会将此版本转换为“1.0-202301003-160800”，假设我们在2023年1月03日16:08 UTC进行部署。

换句话说，当我们部署快照时，我们并不是在交付软件组件，而只是交付它的快照。

## 6. 依赖管理

依赖管理在Maven世界中是必不可少的，例如，当一个项目在其运行过程(编译、执行)中依赖于其他类时，就需要识别这些依赖并将这些依赖从远程仓库导入到本地仓库。因此，项目会对这些库产生依赖，这些库最终会被添加到项目的类路径中。

此外，Maven的依赖管理基于几个概念：

-   仓库：存储工件必不可少的
-   范围：允许我们指定在哪个上下文中使用依赖项
-   传递性：允许我们管理依赖项的依赖关系
-   继承：从父级继承的POM可以通过仅提供依赖项的groupId和artifactId而不提供version属性来设置它们的依赖项，Maven从父POM文件中获取适当的版本。

使用Maven，**依赖管理是通过pom.xml完成的**。例如，Maven项目中的依赖声明如下所示：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.maven</groupId>
        <artifactId>maven-plugin-api</artifactId>
        <version>3.8.4</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-web</artifactId>
        <version>5.3.15</version>
    </dependency>
</dependencies>
```

## 7. Maven仓库

**Maven使用仓库来存储构建项目所需的依赖项和插件等元素**，这使得集中这些元素成为可能，这些元素通常用于多个项目。

正如我们之前提到的，仓库使用一组坐标存储工件：groupId、artifactId、version和packaging。此外，Maven使用特定的目录结构来组织仓库的内容并允许它找到所需的元素：${repository_home}/groupId/artifactId/version。

例如，让我们考虑一下这个POM配置。

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>myApp</artifactId>
    <version>1.0.0</version>
</project>
```

通过上述配置，我们的项目将存储在${repository_home}/cn/tuyucheng/taketoday/myApp/1.0.0/路径下的仓库中。

## 8. 总结

在本文中，我们讨论了Maven工件的概念及其坐标系统。此外，我们还学习了相关概念，例如依赖项和仓库。
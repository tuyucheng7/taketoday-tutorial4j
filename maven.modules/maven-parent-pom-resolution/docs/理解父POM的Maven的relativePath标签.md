## 1. 概述

在本教程中，我们将了解[Maven](https://www.baeldung.com/maven-guide)的Parent POM解析。首先，我们将介绍默认行为。然后，我们将讨论自定义它的可能性。

## 2. 默认父POM解析

如果我们想指定一个父POM，我们可以通过命名groupId、artifactId和version来实现(即所谓的GAV坐标)，**Maven不会通过首先在仓库中搜索来解析父POM**，我们可以在[Maven模型文档](https://maven.apache.org/ref/3.0/maven-model/maven.html)中找到详细信息并总结行为：

1.  如果父文件夹中有一个pom.xml文件，并且如果这个文件有匹配的GAV坐标，则它被归类为项目的父POM
2.  如果没有，Maven将恢复到仓库

将一个Maven项目放入另一个项目是管理[多模块项目](https://www.baeldung.com/maven-multi-module)时的最佳实践。例如，我们有一个具有以下GAV坐标的aggregator项目：

```xml
<groupId>cn.tuyucheng.taketoday.maven-parent-pom-resolution</groupId>
<artifactId>aggregator</artifactId>
<version>1.0.0</version>
```

然后我们可以将模块放入子文件夹中，并将aggregator称为父级：

<img src="../assets/img.png">

因此，module1 POM可以包含以下部分：

```xml
<artifactId>module1</artifactId>
<parent>
    <groupId>cn.tuyucheng.taketoday.maven-parent-pom-resolution</groupId>
    <artifactId>aggregator</artifactId>
    <version>1.0.0</version>
</parent>
```

无需将aggregator POM安装到仓库中，甚至不需要在aggregator POM中声明module1。但我们必须知道，这仅适用于项目的本地签出(例如，在构建项目时)。如果项目被解析为来自Maven仓库中的依赖项，则父POM也应该在仓库中可用。

我们必须确保aggregator POM具有匹配的GAV坐标，否则，我们会得到一个构建错误：

```bash
[ERROR]     Non-resolvable parent POM for cn.tuyucheng.taketoday.maven-parent-pom-resolution:module1:1.0.0:
  Could not find artifact cn.tuyucheng.taketoday.maven-parent-pom-resolution:aggregator:pom:1.0.0
  and 'parent.relativePath' points at wrong local POM @ line 7, column 13
```

## 3. 自定义父POM的位置

如果父POM不在父文件夹中，我们需要使用<relativePath\>标签来引用该位置。例如，如果我们有第二个模块应该从module1继承设置，而不是从aggregator继承设置，我们必须命名同级文件夹：

<img src="../assets/img_1.png">

```xml
<artifactId>module2</artifactId>
<parent>
    <groupId>cn.tuyucheng.taketoday.maven-parent-pom-resolution</groupId>
    <artifactId>module1</artifactId>
    <version>1.0.0</version>
    <relativePath>../module1/pom.xml</relativePath>
</parent>
```

当然，我们应该只使用在每个环境中可用的相对路径(主要是同一个Git仓库中的路径)以确保我们构建的可移植性。

## 4. 禁用本地文件解析

要跳过本地文件搜索并直接在Maven仓库中搜索父POM，我们需要显式地将<relativePath\>设置为空值：

```xml
<parent>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>external-project</artifactId>
    <version>1.0.0</version>
    <relativePath/>
</parent>
```

<img src="../assets/img_2.png">

每当我们从[Spring Boot](https://robintegg.com/2019/01/20/why-does-spring-initializr-set-the-parent-pom-relativepath-to-empty.html)等外部项目继承时，这都应该是最佳实践。

## 5. IDE

有趣的是，IntelliJ IDEA(当前版本：2022.3.1)附带了一个Maven插件，该插件在父POM解析方面不同于外部Maven运行时。与[Maven的POM Schema](https://maven.apache.org/xsd/maven-4.0.0.xsd)不同，它以这种方式解释<relativePath\>标签：

>   [...] Maven首先在当前构建项目的反应堆中寻找父pom [...]

这意味着，对于IDE内部解析，只要父项目注册为IntelliJ Maven项目，父POM的位置就无关紧要，这可能有助于简单地开发项目而不显式构建它们(如果它们不在同一个Git仓库的范围内)。但是如果我们尝试使用外部Maven运行时构建项目，它将失败。

## 6. 总结

在本文中，我们了解到Maven不会通过首先搜索Maven仓库来解析父POM，而是在本地搜索它，而且我们在从外部项目继承时明确必须停用此行为。此外，IDE可能会额外解析工作区中的项目，这可能会在我们使用外部Maven运行时时导致错误。
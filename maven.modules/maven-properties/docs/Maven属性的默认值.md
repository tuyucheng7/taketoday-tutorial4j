## 1. 概述

[Apache Maven](https://www.baeldung.com/maven)是一个功能强大的构建自动化工具，主要用于Java项目。Maven使用项目对象模型或POM，其中包含有关项目和配置详细信息的信息来构建项目。在POM内部，我们能够定义可在POM本身或[多模块](https://www.baeldung.com/maven-multi-module)配置项目中的任何子POM中使用的属性。

**Maven属性允许我们在一个地方定义值，并在我们的项目定义中的几个不同位置使用它们**。

在这篇简短的文章中，我们将介绍如何配置默认值，以及如何使用它们。

## 2. POM中的默认值

**最常见的是，我们在POM中定义Maven属性的默认值**。为了演示这一点，我们将创建一个属性来保存库依赖项的默认值，让我们从在POM中定义属性及其默认值开始：

```xml
<properties>
    <junit.version>4.13</junit.version>
</properties>
```

在此示例中，我们创建了一个名为junit.version的属性并分配了默认值4.13。

## 3. settings.xml中的默认值

**我们还可以在用户的settings.xml中定义Maven属性**，如果用户需要为属性设置自己的默认值，这将非常有用。我们在settings.xml中定义属性及其值的方式与在POM中定义它们的方式相同。

默认情况下我们可以在用户主目录的.m2目录中找到settings.xml；或者如果你当初安装Maven的时候指定了自己的路径，则可以在$MAVEN_HOME/conf中找到该文件。

## 4. 命令行上的默认值

**我们可以在执行Maven命令时在**[命令行](https://www.baeldung.com/maven-arguments)**上为属性定义默认值**，在此示例中，我们将默认值4.13更改为4.12：

```bash
mvn install -Djunit.version=4.12
```

## 5. 在POM中使用属性

我们可以在POM的其他地方引用我们的默认属性值，所以让我们继续定义junit依赖项并使用我们的属性来检索版本号：

```xml
<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>${junit.version}</version>
    </dependency>
</dependencies>
```

我们通过使用${junit.version}语法来引用值junit.version。

## 6. 总结

在这篇简短的文章中，我们了解了如何以三种不同的方式为Maven属性定义默认值，正如我们所看到的，它们有助于我们在不同的地方重复使用相同的值，而只需要对其进行管理在一个地方。
## 1. 概述

[Maven Wrapper](https://maven.apache.org/wrapper/)是需要特定版本Maven的项目(或根本不想安装Maven的用户)的绝佳选择，我们可以只使用特定于项目的包装器脚本，而不是在操作系统中安装它的多个版本。

在这篇简短的文章中，我们演示如何为现有的Maven项目设置Maven Wrapper。

## 2. 设置Maven包装器

有两种方法可以在项目中配置它，其中最简单的一种是使用适当的插件来自动化它或通过应用手动安装。

### 2.1 插件

让我们使用这个[Maven Wrapper插件](https://maven.apache.org/wrapper/maven-wrapper-plugin/)在一个简单的Spring Boot项目中进行自动安装。

首先，我们需要进入项目的主文件夹并运行以下命令：

```bash
mvn -N wrapper:wrapper
```

我们还可以指定Maven的版本：

```bash
mvn -N wrapper:wrapper -Dmaven=3.5.2
```

选项-N表示-non-recursive，以便包装器仅应用于当前目录的主项目，而不应用于任何子模块。

执行目标后，我们会在项目中拥有更多的文件和目录：

-   mvnw：这是一个可执行的Unix shell脚本，用于代替完全安装的Maven
-   mvnw.cmd：它是上述脚本的批处理版本
-   mvn：包含Maven Wrapper Java库及其属性文件的隐藏文件夹

### 2.2 手动

使用手动方法，我们可以将上面看到的文件和文件夹从另一个项目复制到当前项目的主文件夹中。

之后，我们需要在位于.mvn/wrapper/maven-wrapper.properties文件中的包装器属性文件中指定要使用的Maven版本。

例如，我们的属性文件有以下行：

```properties
distributionUrl=https://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.5.2/apache-maven-3.5.2-bin.zip
```

因此，将下载并使用3.5.2版本。

## 3. 用例

包装器应该适用于不同的操作系统，例如：

-   Linux
-   OSX
-   Windows
-   Solaris

之后，我们可以像这样在Unix系统上运行我们的目标：

```bash
./mvnw clean install
```

以及适用于Windows的以下命令：

```bash
mvnw.cmd clean install
```

如果我们在wrapper属性中没有指定的Maven，它将被下载并安装在系统的文件夹$USER_HOME/.m2/wrapper/dists中。

让我们运行我们的Spring-Boot项目：

```bash
./mvnw spring-boot:run
```

输出与本地安装的Maven相同：

<img src="../assets/img_1.png">

注意：我们使用可执行文件mvnw代替mvn，它现在代表Maven命令行程序。

现在我们知道了什么是Maven包装器，让我们回答一个常见问题：mvnw文件应该添加到我们的项目中吗？

简短的回答是否定的。mvnw文件不一定是我们项目的一部分，但是，包括它们可能是有益的。例如，它将允许任何克隆我们项目的人在不安装Maven的情况下构建它。

## 4. 总结

在本教程中，我们了解了如何在Maven项目中设置和使用Maven Wrapper。
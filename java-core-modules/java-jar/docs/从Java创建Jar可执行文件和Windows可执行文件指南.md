## 1. 概述

在本教程中，我们将从学习如何将Java程序打包到可执行的JavaARchive (JAR) 文件开始。然后，我们将了解如何使用该可执行 JAR 生成 Microsoft Windows 支持的可执行文件。

我们将使用Java 附带的jar命令行工具来创建 JAR 文件。然后，我们将学习使用jpackage工具(在Java16 及更高版本中作为jdk.jpackage提供)来生成可执行文件。

## 2. jar和jpackage命令的基础知识

JAR[文件](https://www.baeldung.com/java-create-jar)是已编译的Java类文件和其他资源的容器。它基于流行的 ZIP 文件格式。

可执行 JAR 文件也是一个 JAR 文件，但也包含一个主类。主类在清单文件中引用，我们将在稍后讨论。

为了运行以 JAR 格式交付的应用程序，我们必须拥有Java运行时环境 (JRE)。

与 JAR 文件不同，特定于平台的可执行文件可以在其构建的平台上本地运行。例如，该平台可以是 Microsoft Windows、Linux 或 Apple macOS。

为了获得良好的最终用户体验，最好为客户提供特定于平台的可执行文件。

### 2.1. 罐子命令_

创建 JAR 文件的一般语法是：

```java
jar cf jar-file input-file(s)
```

让我们来看看在使用jar命令创建新存档时可以使用的一些选项：

-   c指定我们要创建一个 JAR 文件
-   f指定我们希望输出到一个文件
-   m用于包含来自现有清单文件的清单信息
-   jar-file是我们希望生成的 JAR 文件的名称。JAR 文件通常带有.jar扩展名，但这不是必需的。
-   input-file(s)是我们要包含在我们的 JAR 文件中的以空格分隔的文件名列表。通配符也可以在这里使用。

一旦我们创建了一个 JAR 文件，我们就会经常检查它的内容。要查看 JAR 文件包含的内容，我们使用以下语法：

```java
jar tf jar-file

```

这里，t表示我们要列出 JAR 文件的内容。f选项表示我们要检查的 JAR 文件是在命令行上指定的。

### 2.2. jpackage命令_

jpackage命令行工具帮助我们为模块化和非模块化Java应用程序生成可安装的[包 ](https://www.baeldung.com/java14-jpackage)。

它使用[jlink ](https://www.baeldung.com/jlink)命令为我们的应用程序生成Java运行时映像。结果，我们获得了针对特定平台的自包含应用程序包。

由于应用程序包是为目标平台构建的，因此该系统必须包含以下内容：

-   应用程序本身
-   一个JDK
-   打包工具需要的软件。对于 Windows，jpackage需要 WiX 3.0 或更高版本。

[下面是jpackage ](https://www.baeldung.com/java14-jpackage)命令的常用形式：

```
jpackage --input . --main-jar MyAppn.jar
```

## 3. 创建可执行文件

现在让我们创建一个可执行的 JAR 文件。准备就绪后，我们将着手生成 Windows 可执行文件。

### 3.1. 创建可执行 JAR 文件

创建可执行 JAR 非常简单。我们首先需要一个Java项目，其中至少有一个带有main()方法的类。我们为示例创建了一个名为MySampleGUIAppn的Java类。

第二步是创建清单文件。让我们将清单文件创建为MySampleGUIAppn.mf：

```java
Manifest-Version: 1.0
Main-Class: MySampleGUIAppn

```

我们必须确保此清单文件末尾有换行符才能正常工作。

一旦清单文件准备就绪，我们将创建一个可执行 JAR：

```java
jar cmf MySampleGUIAppn.mf MySampleGUIAppn.jar MySampleGUIAppn.class MySampleGUIAppn.java
```

让我们查看我们创建的 JAR 的内容：

```java
jar tf MySampleGUIAppn.jar
```

这是一个示例输出：

```java
META-INF/
META-INF/MANIFEST.MF
MySampleGUIAppn.class
MySampleGUIAppn.java
```

接下来，我们可以通过 CLI 或 GUI 运行我们的 JAR 可执行文件。

让我们在命令行上运行它：

```java
java -jar MySampleGUIAppn.jar
```

在 GUI 中，我们只需双击相关的 JAR 文件即可。那应该像任何其他应用程序一样正常启动它。

### 3.2. 创建 Windows 可执行文件

现在我们的可执行 JAR 已准备就绪并可以运行，让我们为示例项目生成一个 Windows 可执行文件：

```java
jpackage --input . --main-jar MySampleGUIAppn.jar
```

此命令需要一段时间才能完成。完成后，它会在当前工作文件夹中生成一个exe文件。可执行文件的文件名将与清单文件中提到的版本号连接在一起。我们将能够像启动任何其他 Windows 应用程序一样启动它。

以下是我们可以与jpackage命令一起使用的一些特定于 Windows 的选项：

-   –type : 指定msi而不是默认的exe格式
-   –win-console : 使用控制台窗口启动我们的应用程序
-   –win-shortcut : 在 Windows 开始菜单中创建一个快捷方式文件
-   –win-dir-chooser：让最终用户指定自定义目录来安装可执行文件
-   –win-menu –win-menu-group：让最终用户在“开始”菜单中指定自定义目录

## 4。总结

在本文中，我们了解了有关 JAR 文件和可执行 JAR 文件的一些基础知识。我们还了解了如何将Java程序转换为 JAR 可执行文件，然后再转换为 Microsoft Windows 支持的可执行文件。
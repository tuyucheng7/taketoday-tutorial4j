## 1. 概述

在本教程中，我们将探索[Java 14](https://openjdk.java.net/projects/jdk/14/)中引入的新打包工具，名为[jpackage](https://openjdk.java.net/jeps/343)。

## 2. 简介

**jpackage是一个命令行工具，用于为Java应用程序创建本机安装程序和包**。

它是jdk.incubator.jpackage模块下的一个孵化功能。换句话说，该工具的命令行选项或应用程序布局还不稳定。一旦稳定，Java SE平台或JDK将在LTE版本中包含此功能。

## 3. 为什么选择jpackage？

分发软件以向最终用户提供可安装的软件包是标准做法。该软件包与用户的本机平台兼容，并隐藏了内部依赖项和设置配置。例如，我们在macOS上使用DMG文件，在Windows上使用MSI文件。

这允许以我们最终用户熟悉的方式分发、安装和卸载应用程序。

jpackage允许开发人员为他们的JAR文件创建这样一个可安装的包。**用户不必显式复制JAR文件，甚至不必安装Java即可运行应用程序**。可安装包负责所有这些。

## 4. 打包先决条件

使用jpackage命令的关键先决条件是：

1.  用于打包的系统必须包含待打包的应用程序、JDK和打包工具所需的软件。

2.  并且，它需要有jpackage使用的底层打包工具：

    -  Linux上的RPM、DEB：在RedHat Linux上，我们需要rpm-build包；在Ubuntu Linux上，我们需要fakeroot包
    -  macOS上的PKG、DMG：当使用–mac-sign选项请求对包进行签名以及使用–icon选项自定义DMG镜像时，需要Xcode命令行工具
    -  Windows上的EXE、MSI：在Windows上，我们需要第三方工具WiX 3.0或更高版本

3.  最后，应用程序包必须构建在目标平台上。这意味着要为多个平台打包应用程序，我们必须在每个平台上运行打包工具。

## 5. 包创建

让我们为应用程序JAR创建一个示例包。如上一节所述，应预先构建应用程序JAR，并将其用作jpackage工具的输入。

例如，我们可以使用下面的命令来创建一个包：

```bash
jpackage --input target/ \
  --name JPackageDemoApp \
  --main-jar JPackageDemoApp.jar \
  --main-class com.baeldung.java14.jpackagedemoapp.JPackageDemoApp \
  --type dmg \
  --java-options '--enable-preview'
```

让我们来看看使用的每个选项：

-   –input：输入jar文件的位置
-   –name：为可安装包命名
-   –main-jar：在应用程序启动时启动的JAR文件
-   –main-class：JAR中的主类名称，用于在应用程序启动时启动。如果主JAR中的MANIFEST.MF文件包含主类名，则这是可选的。
-   –type：我们想要创建什么样的安装程序？这取决于我们运行jpackage命令的基本操作系统。在macOS上，我们可以将包类型作为DMG或PKG传递。该工具支持Windows上的MSI和EXE选项以及Linux上的DEB和RPM选项。
-   –java-options：传递给Java运行时的选项

上面的命令将为我们创建JPackageDemoApp.dmg文件。

然后我们可以使用这个文件在macOS平台上安装该应用程序。安装后，我们就可以像使用任何其他软件一样使用该应用程序。

## 6. 总结

在本文中，我们看到了Java 14中引入的jpackage命令行工具的用法。
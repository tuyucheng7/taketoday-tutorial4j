## 1. 概述

在本教程中，我们将学习如何更改 IntelliJ 项目中的 JDK 版本。这将适用于IntelliJ 的社区版和终极版。

## 2.项目结构设置

IntelliJ 将项目使用的 JDK 版本存储在其项目结构中。有两种方法可以找到它：

-   通过

    菜单导航

    ：

    -   导航到文件 -> 项目结构

-   通过

    键盘快捷键

    ：

    -   对于 OSX，我们按⌘ + ；
    -   对于 Windows，我们按Ctrl + Shift + Alt + S

然后我们会看到一个类似于此的弹出对话框：

[![项目结构](https://www.baeldung.com/wp-content/uploads/2019/09/Screenshot-2019-09-22-at-19.13.50.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Screenshot-2019-09-22-at-19.13.50.png)

在项目 SDK部分下，我们将能够通过组合框选择一个新的 JDK用于项目。更新到新版本的 Java 后，该项目将开始重新索引其源文件和库，以确保自动完成和其他 IDE 功能同步。

## 3. 常见问题

更改 JDK 时，我们需要记住这只会影响 IntelliJ 使用的 JDK。因此，当通过命令行运行 Java 项目时，它仍然会使用JAVA_HOME 环境变量中指定的 JDK。

此外，更改Project SDK 不会更改所用构建工具的 JVM 版本。[因此，在 IntelliJ 中使用 Maven](https://www.baeldung.com/maven-java-version)或 Gradle时，更改 Project SDK 不会更改用于这些构建工具的 JVM。

## 4。总结

在本文中，我们说明了两种可以更改 IntelliJ 项目中使用的 Java 版本的方法。我们还强调了更改 Java 版本时需要注意的注意事项。

要了解有关 IntelliJ项目结构的更多信息，请访问官方[文档](https://www.jetbrains.com/help/idea/project-page.html?keymap=primary_default_for_windows)。
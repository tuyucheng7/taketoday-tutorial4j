## 一、概述

在这个简短的教程中，我们将了解几种确定计算机上是否安装了 Java 的方法。

## 2.命令行

首先，**让我们打开一个命令窗口或终端并输入：**

```bash
> java -version复制
```

**如果安装了 Java** 并且正确配置了 PATH，我们的输出将类似于：

```plaintext
java version "1.8.0_31"
Java(TM) SE Runtime Environment (build 1.8.0_31-b13)
Java HotSpot(TM) Client VM (build 25.31-b07, mixed mode, sharing)复制
```

否则，我们会看到如下所示的错误消息，我们需要在别处检查：

```plaintext
'java' is not recognized as an internal or external command,
operable program or batch file.复制
```

我们看到的确切消息会因使用的操作系统和安装的 Java 版本而异。

## 3.未设置路径时

转到命令行并键入*java -version* 可以确定是否安装了 Java。但是，如果我们看到一条错误消息，则可能仍安装了 Java——我们只需要进一步调查。

许多关于使用*java -version 的*讨论 都提到了 JAVA_HOME 环境变量。**这是误导，因为 JAVA_HOME 不会影响我们的\*java -version\*结果。**

此外，JAVA_HOME 应该指向 JDK 和其他使用 JDK 功能的应用程序，例如 Maven，使用它。

有关更多信息，请查看我们的文章 [JAVA_HOME 应指向 JDK](https://www.baeldung.com/maven-java-home-jdk-jre)以及[如何设置 JAVA_HOME](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)。

因此，让我们看看查找 Java 的替代方法，以防命令行失败。

### 3.1. 视窗 10

在 Windows 上，我们可以在应用程序列表中找到它：

1.  *按下*开始按钮
2.  向下滚动应用程序列表到 J
3.  打开*Java*文件夹
4.  单击*关于 Java*

[![如何检查java是否安装了关于java的windows应用程序](https://www.baeldung.com/wp-content/uploads/2018/12/how_to_check_if_java_installed_windows_apps_about_java.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/how_to_check_if_java_installed_windows_apps_about_java.jpg)

我们还可以查看已安装的程序和功能：

1.  在*搜索*栏中，键入*控制面板*
2.  单击*程序*
3.  如果出现*Java*图标，则表示已安装 Java
4.  如果没有，请单击*“程序和功能”，*然后在 J 中查找已安装的 Java 版本

[![如何检查java是否安装了windows程序特性](https://www.baeldung.com/wp-content/uploads/2018/12/how_to_check_if_java_installed_windows_programs_features.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/how_to_check_if_java_installed_windows_programs_features.jpg)

### 3.2. 苹果操作系统

要查看 Mac 上是否安装了 Java 7 或更高版本，我们可以：

1.  转到*系统偏好设置*
2.  寻找*Java*图标

[![如何检查java是否安装了mac sys prefs](https://www.baeldung.com/wp-content/uploads/2018/12/how_to_check_if_java_installed_mac_sys_prefs.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/how_to_check_if_java_installed_mac_sys_prefs.jpg)

对于早期版本的 Java，我们需要：

1.  打开*查找器*
2.  转到*应用程序*文件夹
3.  转到 *实用程序* 文件夹
4.  查找*Java 首选项*应用程序

### 3.3. *尼克斯

*nix 世界中几乎没有不同的[包管理器。](https://www.tecmint.com/linux-package-management/)

在基于 Debian 的发行版中，我们可以使用*aptitude search*命令：

```bash
$ sudo aptitude search jdk jre复制
```

如果结果前有 *i*，则表示安装了包：

```bash
...
i   oracle-java8-jdk                - Java™ Platform, Standard Edition 8 Develop
...复制
```

## 4. 其他命令行工具

除了 *java -version 之外，*我们还可以使用其他一些命令行工具来了解我们的 Java 安装。

### 4.1. Windows *where*命令

在 Windows 中，我们可以使用 *where*命令来查找我们的*java.exe*所在的位置：

```plaintext
> where java复制
```

我们的输出将类似于：

```plaintext
C:\Apps\Java\jdk1.8.0_31\bin\java.exe复制
```

但是，与 *java -version*一样，此命令仅在我们的 PATH 环境变量指向 bin 目录时才有用。

### 4.2. Mac OS X 和 *nix *which*和*whereis*

在 *nix 系统或 Mac 上的终端应用程序中，我们可以使用 *which*命令：

```bash
$ which java复制
```

输出告诉我们 Java 命令在哪里：

```plaintext
/usr/bin/java复制
```

现在让我们使用*whereis*命令：

```bash
$ whereis java -b复制
```

*whereis*命令 还为我们提供了 Java 安装的路径：

```plaintext
/usr/bin/java复制
```

与 *java -version*一样，这些命令只会在路径上找到 Java。*我们可以在安装了 Java 后使用这些，但想确切地知道当我们使用java*命令时将运行什么 。

## 5.结论

在这篇简短的文章中，我们讨论了如何查明 Java 是否安装在 Windows 10、Mac OS X 或 Linux/Unix 机器上，即使它不在 PATH 上也是如此。

我们还查看了一些用于定位 Java 安装的有用命令。
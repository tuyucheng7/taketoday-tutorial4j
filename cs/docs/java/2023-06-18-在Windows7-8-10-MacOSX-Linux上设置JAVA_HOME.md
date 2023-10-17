---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java
copyright: java
excerpt: Java
---

## 1.概述

在本快速教程中，我们将了解如何在Windows、MacOSX和Linux上设置JAVA_HOME变量。

## 2.窗户

### 2.1.Windows10和8

1.  打开搜索并键入高级系统设置。
2.  在显示的选项中，选择查看高级系统设置链接。
3.  在“高级”选项卡下，单击“环境变量”。
4.  在系统变量部分，单击新建(或单用户设置的用户变量)。
5.  将JAVA_HOME设置为Variablename，将JDK安装路径设置为Variablevalue，然后单击OK。
6.  单击确定并单击应用以应用更改。

### 2.2.Windows7的

1.  在桌面上，右键单击我的电脑并选择属性。
2.  在“高级”选项卡下，单击“环境变量”。
3.  在系统变量部分，单击新建(或单用户设置的用户变量)。
4.  将JAVA_HOME设置为Variablename，将JDK安装路径设置为Variablevalue，然后单击OK。
5.  单击确定并单击应用以应用更改。

打开命令提示符并检查JAVA_HOME变量的值：

```bash
echo %JAVA_HOME%
```

结果应该是JDK安装的路径：

```shell
C:\Program Files\Java\jdk1.8.0_111
```

## 3.苹果电脑

### 3.1.单用户–MacOSX10.5或更新版本

从OSX10.5开始，Apple引入了一个[命令行工具](https://developer.apple.com/library/content/qa/qa1170/_index.html)(/usr/libexec/java_home)，该工具可以动态查找在Java首选项中为当前用户指定的最高Java版本。

在任何文本编辑器中打开~/.bash_profile并添加以下内容：

```bash
export JAVA_HOME=$(/usr/libexec/java_home)
```

保存并关闭文件。

打开终端并运行source命令以应用更改：

```bash
source ~/.bash_profile
```

现在我们可以检查JAVA_HOME变量的值：

```bash
echo $JAVA_HOME
```

结果应该是JDK安装的路径：

```bash
/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home
```

### 3.2.单用户–MacOSX旧版本

对于旧版本的OSX，我们必须设置JDK安装的确切路径。

在任何编辑器中打开~/.bash_profile并添加以下内容：

```bash
export JAVA_HOME=/path/to/java_installation
```

保存并关闭文件。

打开终端并运行source命令以应用更改：

```bash
source ~/.bash_profile
```

现在我们可以检查JAVA_HOME变量的值：

```bash
echo $JAVA_HOME
```

结果应该是JDK安装的路径：

```bash
/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home
```

### 3.3.全局设置

要为所有用户全局设置JAVA_HOME，步骤与单个用户相同，但我们使用文件/etc/profile。

## 4.Linux

当然，我们要在这里操作PATH，所以这里有关于如何操作的[详细说明。](https://www.baeldung.com/linux/path-variable)

### 4.1.单用户

要在Linux中为单个用户设置JAVA_HOME，我们可以使用/etc/profile或/etc/environment(系统范围设置的首选)或~/.bashrc(用户特定设置)。

在任何文本编辑器中打开~/.bashrc并添加以下内容：

```bash
export JAVA_HOME=/path/to/java_installation
```

保存并关闭文件。

运行source命令加载变量：

```bash
source ~/.bashrc
```

现在我们可以检查JAVA_HOME变量的值：

```bash
echo $JAVA_HOME
```

结果应该是JDK安装的路径：

```bash
/usr/lib/jvm/java-8-oracle
```

### 4.2.全局设置

要在Linux中为所有用户设置JAVA_HOME，我们可以使用/etc/profile或/etc/environment(首选)。

在任何文本编辑器中打开/etc/environment并添加以下内容：

```bash
JAVA_HOME=/path/to/java_installation
```

请注意/etc/environment不是脚本而是赋值表达式的列表(这就是不使用export的原因)。该文件在登录时被读取。

要使用/etc/profile设置JAVA_HOME，以下是我们将添加到文件中的内容：

```bash
export JAVA_HOME=/path/to/java_installation
```

运行source命令加载变量：

```bash
source /etc/profile
```

现在我们可以检查JAVA_HOME变量的值：

```bash
echo $JAVA_HOME
```

结果应该是JDK安装的路径：

```bash
/usr/lib/jvm/java-8-oracle
```

## 5.总结

在本文中，我们介绍了在Windows、MacOSX和Linux上设置JAVA_HOME环境变量的方法。

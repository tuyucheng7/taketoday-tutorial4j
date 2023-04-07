## 1. 概述

在本快速教程中，我们将了解如何在 Windows、Mac OS X 和 Linux 上设置JAVA_HOME变量。

## 2.窗户

### 2.1. Windows 10 和 8

1.  打开搜索并键入高级系统设置。
2.  在显示的选项中，选择查看高级系统设置链接。
3.  在“高级”选项卡下，单击“环境变量”。
4.  在系统变量部分，单击新建(或单用户设置的用户变量)。
5.  将JAVA_HOME设置为Variable name，将 JDK 安装路径设置为Variable value，然后单击OK。
6.  单击确定并单击应用以应用更改。

### 2.2. Windows 7的

1.  在桌面上，右键单击我的电脑并选择属性。
2.  在“高级”选项卡下，单击“环境变量”。
3.  在系统变量部分，单击新建(或单用户设置的用户变量)。
4.  将JAVA_HOME设置为Variable name，将 JDK 安装路径设置为Variable value，然后单击OK。
5.  单击确定并单击应用以应用更改。

打开命令提示符并检查JAVA_HOME变量的值：

```bash
echo %JAVA_HOME%复制
```

结果应该是 JDK 安装的路径：

```shell
C:\Program Files\Java\jdk1.8.0_111复制
```

## 3.苹果电脑

### 3.1. 单用户 – Mac OS X 10.5 或更新版本

从 OS X 10.5 开始，Apple 引入了一个[命令行工具](https://developer.apple.com/library/content/qa/qa1170/_index.html)( /usr/libexec/java_home )，该工具可以动态查找在 Java 首选项中为当前用户指定的最高 Java 版本。

在任何文本编辑器中打开~/.bash_profile并添加以下内容：

```bash
export JAVA_HOME=$(/usr/libexec/java_home)复制
```

保存并关闭文件。

打开终端并运行 source 命令以应用更改：

```bash
source ~/.bash_profile复制
```

现在我们可以检查JAVA_HOME变量的值：

```bash
echo $JAVA_HOME复制
```

结果应该是 JDK 安装的路径：

```bash
/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home复制
```

### 3.2. 单用户 – Mac OS X 旧版本

对于旧版本的 OS X，我们必须设置 JDK 安装的确切路径。

在任何编辑器中打开~/.bash_profile并添加以下内容：

```bash
export JAVA_HOME=/path/to/java_installation复制
```

保存并关闭文件。

打开终端并运行 source 命令以应用更改：

```bash
source ~/.bash_profile复制
```

现在我们可以检查JAVA_HOME变量的值：

```bash
echo $JAVA_HOME复制
```

结果应该是 JDK 安装的路径：

```bash
/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home复制
```

### 3.3. 全局设置

要为所有用户全局设置JAVA_HOME，步骤与单个用户相同，但我们使用文件/etc/profile。

## 4. Linux

当然，我们要在这里操作 PATH，所以这里有关于如何操作的[详细说明。](https://www.baeldung.com/linux/path-variable)

### 4.1. 单用户

要在 Linux 中为单个用户设置JAVA_HOME ，我们可以使用/etc/profile或/etc/environment(系统范围设置的首选)或 ~/.bashrc(用户特定设置)。

在任何文本编辑器中打开 ~ /.bashrc并添加以下内容：

```bash
export JAVA_HOME=/path/to/java_installation复制
```

保存并关闭文件。

运行 source 命令加载变量：

```bash
source ~/.bashrc复制
```

现在我们可以检查JAVA_HOME变量的值：

```bash
echo $JAVA_HOME复制
```

结果应该是 JDK 安装的路径：

```bash
/usr/lib/jvm/java-8-oracle复制
```

### 4.2. 全局设置

要在 Linux 中为所有用户设置JAVA_HOME，我们可以使用/etc/profile或/etc/environment(首选)。

在任何文本编辑器中打开/etc/environment并添加以下内容：

```bash
JAVA_HOME=/path/to/java_installation复制
```

请注意/etc/environment不是脚本而是赋值表达式的列表(这就是不使用export的原因)。该文件在登录时被读取。

要使用/etc/profile设置JAVA_HOME，以下是我们将添加到文件中的内容：

```bash
export JAVA_HOME=/path/to/java_installation复制
```

运行 source 命令加载变量：

```bash
source /etc/profile复制
```

现在我们可以检查JAVA_HOME变量的值：

```bash
echo $JAVA_HOME复制
```

结果应该是 JDK 安装的路径：

```bash
/usr/lib/jvm/java-8-oracle复制
```

## 5.总结

在本文中，我们介绍了在 Windows、Mac OS X 和 Linux 上设置JAVA_HOME环境变量的方法。
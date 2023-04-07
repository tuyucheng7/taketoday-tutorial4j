## 一、概述

通常，当我们在 Java 项目中需要[JAR文件时，我们会将它们作为外部库放在类路径中，而不会提取它们。](https://www.baeldung.com/java-view-jar-contents)但是，我们有时希望将它们添加到我们的文件系统中。

在本教程中，我们将探讨如何在命令行中将 JAR 文件提取到指定目录。**我们将使用 Linux 和 Bash 作为示例来介绍每种方法**。

## 二、问题简介

在本教程中，我们将使用[Guava](https://www.baeldung.com/guava-guide)的[JAR 文件](https://github.com/google/guava/releases)作为示例。撰写本文时的最新版本是 guava- *31.1-jre.jar*。

Java 提供了*jar*命令来创建、更新、查看和提取 JAR 文件。接下来，让我们使用*jar*命令提取 Guava 的 JAR 文件：

```bash
$ tree /tmp/test/jarTest
/tmp/test/jarTest
└── guava-31.1-jre.jar

0 directories, 1 file

$ pwd
/tmp/test/jarTest

$ jar xf guava-31.1-jre.jar 

$ tree /tmp/test/jarTest   
/tmp/test/jarTest
├── com
│   └── google
│       ├── common
│       │   ├── annotations
│       │   │   ├── Beta.class
...
27 directories, 2027 files
复制
```

如上面的输出所示，**我们可以使用\*jar xf\*提取一个 JAR 文件，***jar*命令默认将其提取到当前目录。

但是***jar\*****命令****不支持解压文件到指定目录。**

接下来，让我们看看如何在行动中实现这一点。

## 3、解压前进入目标目录

我们已经了解到*jar*命令默认将给定的 JAR 文件提取到当前工作目录。

于是，一个解决问题的思路就产生了：如果我们要将JAR文件解压到指定目标，**可以先进入目标目录，然后启动\*jar\*命令。**

接下来，让我们确定这个想法是否按预期工作。

### 3.1. 测试想法

首先，让我们创建一个新目录*/tmp/test/newTarget*：

```bash
$ mkdir /tmp/test/newTarget

$ tree /tmp/test/newTarget
/tmp/test/newTarget
0 directories, 0 files
复制
```

接下来我们进入目标目录，然后解压Guava的JAR文件：

```bash
$ cd /tmp/test/newTarget && jar xf /tmp/test/jarTest/guava-31.1-jre.jar 
$ tree /tmp/test/newTarget
/tmp/test/newTarget
├── com
│   └── google
│       ├── common
│       │   ├── annotations
│       │   │   ├── Beta.class
...复制
```

如上例所示，这个想法可行。我们已经将 Guava 的 JAR 文件提取到我们想要的目录中。

然而，如果我们重新审视我们执行过的命令，我们会发现有一些不便之处：

-   如果是新目录，我们必须首先手动创建目标目录。
-   当*cd*命令更改我们当前的工作目录时，我们必须传递 JAR 文件及其绝对路径。
-   执行命令后，我们留在目标目录中，而不是我们开始的地方。

那么接下来，让我们看看如何改进我们的解决方案。

### 3.2. 创建*xJarTo()*函数

我们可以创建一个 shell 函数来自动创建目录并调整 JAR 文件的路径。让我们先看看这个函数，然后了解它是如何工作的：

```bash
#!/bin/bash

xJarTo() {
    the_pwd="$(pwd)"
    the_jar="$1"
    the_dir="$2"

    if [[ "$the_jar" =~ ^[^/].* ]]; then
        the_jar="${the_pwd}/$the_jar"
    fi
    echo "Extracting $the_jar to $the_dir ..."
    mkdir -p "$the_dir"
    cd "$the_dir" && jar xf "$the_jar"
    cd "$the_pwd"
}
复制
```

该函数首先将用户的当前工作目录 ( *pwd* ) 存储在 *the_pwd*变量中。

然后，它检查 JAR 文件的路径：

-   *如果以“ /* ”开头——绝对路径，所以我们直接使用用户输入
-   否则——相对路径。我们需要在用户的当前工作目录前添加以构建要使用的绝对路径

因此，该函数同时适应绝对和相对 JAR 路径。

接下来，我们在进入目标目录之前执行*mkdir -p命令。****-p\*****选项告诉 \*mkdir\*命令在给定路径中创建缺少的目录（如果有的话）。**

*然后，我们使用cd*进入目标目录，使用准备好的JAR文件路径执行 *jar* *xf命令。*

最后，我们导航回用户的当前工作目录。

### 3.3. 测试功能

现在我们了解了*xJarTo()*函数的工作原理，让我们获取该函数的*[源](https://www.baeldung.com/linux/source-command)*代码并测试它是否按预期工作。

首先我们测试目标目录是新建的，JAR文件是绝对路径的场景：

```bash
$ pwd
/tmp/test

$ xJarTo /tmp/test/jarTest/guava-31.1-jre.jar /tmp/a_new_dir
Extracting /tmp/test/jarTest/guava-31.1-jre.jar to /tmp/a_new_dir ...

$ tree /tmp/a_new_dir
/tmp/a_new_dir
├── com
│   └── google
│       ├── common
│       │   ├── annotations
│       │   │   ├── Beta.class
...
$ pwd
/tmp/test复制
```

如上面的输出所示，该函数按预期工作。新目录是即时创建的，提取的内容位于*/tmp/a_new_dir*下。此外，我们在调用该函数后仍在*/tmp/test*下。

接下来我们测试一下相对JAR路径的场景：

```bash
$ pwd
/tmp/test/jarTest

$ xJarTo guava-31.1-jre.jar /tmp/another_new_dir
Extracting /tmp/test/jarTest/guava-31.1-jre.jar to /tmp/another_new_dir ...

$ tree /tmp/another_new_dir
/tmp/another_new_dir
├── com
│   └── google
│       ├── common
│       │   ├── annotations
│       │   │   ├── Beta.class
...

$ pwd
/tmp/test/jarTest复制
```

这一次，因为我们在*/tmp/test/jarTest*下，我们将*guava-31.1-jre.jar*直接传递给函数。事实证明，该功能也能完成这项工作。

## 4.使用其他解压命令

JAR 文件使用 ZIP 压缩。所以**所有的解压工具都可以解压JAR文件**。如果解压缩实用程序支持解压缩到指定目录，则可以解决问题。

这种方法需要我们系统上至少有一个可用的解压缩工具。否则，我们需要安装它。值得一提的是，**在系统上安装软件包通常需要\*root\*或 [\*sudo\*](https://www.baeldung.com/linux/sudo-command)权限**。

那么接下来，我们就以[*unzip*](https://linux.die.net/man/1/unzip)为例，展示一下流行的[*yum*和*apt*](https://www.baeldung.com/linux/yum-and-apt)包管理器的安装命令：

```bash
# apt
sudo apt install unzip

# yum
sudo yum install unzip复制
```

***unzip\*命令支持\*-d\*选项以将 ZIP 存档解压缩到不同的目录**：

```bash
$ unzip guava-31.1-jre.jar -d /tmp/unzip_new_dir
$ tree /tmp/unzip_new_dir
/tmp/unzip_new_dir
├── com
│   └── google
│       ├── common
│       │   ├── annotations
│       │   │   ├── Beta.class
...
复制
```

正如我们所见，*unzip -d*将 JAR 文件解压缩到指定目录。

## 5.结论

在本文中，我们了解了将 JAR 文件提取到指定目录的两种方法。

为此，我们可以创建一个 shell 函数来包装标准 JAR 命令。或者，如果我们在系统上有其他可用的解压缩工具，例如*unzip*，我们可以使用它们相应的选项将 JAR 内容解压缩到所需目录。
## **一、简介**

在本快速教程中，我们将学习如何在 Windows、Mac 和 Linux 上查找*[JAVA_HOME 。](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)* 

众所周知，*JAVA_HOME*是一个环境变量，我们通常使用它来定位 java 可执行文件，例如*java*和 *[javac](https://www.baeldung.com/javac)*。

## **2. Windows 特定的查找*****JAVA_HOME 的方法\*** 

如果我们使用 Windows 作为操作系统，首先我们需要打开命令行 ( *cmd* ) 并键入：

```bash
echo %JAVA_HOME%复制
```

**如果我们的环境中定义了\*JAVA_HOME\*** ，那么上面的命令会打印出来。

*或者为了显示java* 可执行文件的位置，我们可以尝试：

```java
where java复制
```

## **3. macOS 和 Linux 特定的查找 \*JAVA_HOME 的方法\***

如果我们使用的是 macOS 或 Linux，我们可以打开我们的终端并输入：

```bash
echo $JAVA_HOME复制
```

**如果我们的环境中定义了\*JAVA_HOME\*** ，那么上面的命令会打印出来。

或者我们可以尝试：

```bash
which java复制
```

这可能只是向我们展示了*/usr/bin/java，*这实际上不是很有用，因为它是一个符号链接。为了解决这个问题，我们将使用*dirname*和*readlink。*

对于 Linux：

```bash
dirname $(dirname $(readlink -f $(which javac)))复制
```

对于 macOS：

```bash
$(dirname $(readlink $(which javac)))/java_home复制
```

此命令打印当前使用的 java 文件夹。

## **4.使用Java查找\*JAVA_HOME\***

如果我们能够自己运行*java*，那么**我们也有一种几乎与平台无关的方式：**

```bash
java -XshowSettings:properties -version复制
```

运行此命令会输出许多属性，其中之一是*java.home。*

尽管如此，要解析它，我们仍然需要一个特定于平台的工具。

对于 Linux 和 macOS *，*我们将使用*grep*：

```bash
java -XshowSettings:properties -version 2>&1 > /dev/null | grep 'java.home' 
复制
```

对于 Windows，我们将使用*findstr*：

```bash
java -XshowSettings:properties -version 2>&1 | findstr "java.home"复制
```

## **5.结论**

在这篇简短的文章中，我们学习了如何**在不同的操作系统上** **查找 \*JAVA_HOME 。\***

如果这不起作用，则可能是[我们在安装 Java 时没有 正确设置*JAVA_HOME*](https://www.baeldung.com/java-check-is-installed)变量。
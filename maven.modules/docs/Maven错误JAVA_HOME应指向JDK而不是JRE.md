## 1. 概述

在本教程中，我们将讨论Maven在配置错误时抛出的一个异常：JAVA_HOME should point to a JDK, not a JRE。

[Maven](https://www.baeldung.com/maven)是构建代码的强大工具，我们将深入了解为什么会发生此错误，并了解如何解决它。

## 2. JAVA_HOME问题

安装Maven后，**我们必须设置JAVA_HOME环境变量，以便该工具知道在哪里可以找到要执行的JDK命令**，Maven目标针对项目的源代码运行适当的Java命令。

例如，最常见的场景是通过执行javac命令来编译代码。

**如果JAVA_HOME没有指向有效的JDK安装，Maven将在每次执行时抛出错误**：

```bash
mvn -version

# Output...
The JAVA_HOME environment variable is not defined correctly
This environment variable is needed to run this program
NB: JAVA_HOME should point to a JDK, not a JRE
```

## 3. JDK或JRE

Maven如何验证JAVA_HOME路径？

在运行任何目标之前，**Maven会检查JAVA_HOME指定的路径中是否存在java命令**，或者通过询问操作系统是否安装默认的JDK。**如果未找到可执行文件，Maven将终止并显示错误**。

这是Linux(Apache Maven v3.8.4)的mvn可执行文件检查：

```shell
if [ -z "$JAVA_HOME" ] ; then
    JAVACMD=`which java`
else
    JAVACMD="$JAVA_HOME/bin/java"
fi

if [ ! -x "$JAVACMD" ] ; then
    echo "The JAVA_HOME environment variable is not defined correctly" >&2
    echo "This environment variable is needed to run this program" >&2
    echo "NB: JAVA_HOME should point to a JDK not a JRE" >&2
    exit 1
fi
```

乍一看，这种检查似乎是合理的，但我们必须考虑到**JDK和JRE都有一个bin文件夹，并且都包含一个可执行的java文件**。

因此，**可以将JAVA_HOME配置为指向JRE安装，从而隐藏此特定错误并在稍后阶段引起问题**。JRE的主要目的是运行Java代码，而**JDK还可以进行编译和调试**，这是[JRE和JDK的重要区别](https://www.baeldung.com/jvm-vs-jre-vs-jdk)。

由于这个原因，mvn compile命令会失败，正如我们将在下一小节中看到的那样。

### 3.1 由于JRE而不是JDK导致编译失败

与默认设置一样，如果我们有一个“标准”配置，它们将非常有用。

例如，如果我们在Ubuntu 18.04系统上安装Java 11并且没有设置JAVA_HOME环境变量，Maven仍然会很高兴地找到我们的JDK并将其用于不同的目标，包括编译。

但是如果我们设法在我们的系统上设置了一个非标准的配置(更不用说搞得一团糟了)，Maven的帮助就不再足够了，它甚至可能具有误导性。

假设我们在Ubuntu 18.04上有以下配置：

-   JDK 11
-   JRE 8
-   JAVA_HOME设置为JRE 8安装路径

如果我们进行基本检查：

```bash
mvn --version
```

我们将得到像这样有意义的输出：

```bash
Apache Maven 3.8.4 (97c98ec64a1fdfee7767ce5ffb20918da4f719f3; 2023-01-03T18:41:47Z)
Maven home: /opt/apache-maven-3.8.4
Java version: 1.8.0_191, vendor: Oracle Corporation, runtime: /usr/lib/jvm/java-8-openjdk-amd64/jre
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "4.15.0-42-generic", arch: "amd64", family: "unix"
```

让我们看看如果我们尝试编译一个项目会发生什么：

```bash
mvn clean compile
```

现在，我们得到一个错误：

```bash
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.7.0:compile (default-compile) on project my-app: Compilation failure
[ERROR] No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?
```

### 3.2 修复项目级别的编译错误

与Maven的许多其他情况一样，建议设置有意义的系统范围设置，在这种情况下，我们将更改JAVA_HOME变量的值，如第5节所述，以指向JDK而不是JRE。

但是，如果我们出于某种原因无法设置默认值，**我们仍然可以覆盖项目级别的设置**，让我们看看如何做到这一点。

首先，我们将打开项目的pom.xml，转到build/pluginManagement/plugins部分并查看maven-compiler-plugin的条目：

```xml
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.7.0</version>
</plugin>
```

然后，我们将向其添加一个配置，以便它使用自定义可执行文件并跳过在JAVA_HOME/bin目录中搜索javac：

```xml
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.7.0</version>
    <configuration>
        <fork>true</fork>
        <executable>/usr/lib/jvm/java-11-openjdk-amd64/bin/javac</executable>
    </configuration>
</plugin>
```

在Unix系统上，此可执行文件应该是具有足够权限的脚本。在Windows系统上，它应该是一个.exe文件。

接下来，我们将再次尝试编译该项目：

```bash
mvn clean compile
```

现在，构建-包括使用maven-compiler-plugin的编译阶段成功了。

## 4. 检查JAVA_HOME配置

检查JAVA_HOME是否指向实际的JDK非常简单，我们可以在终端中打印其内容，也可以运行以下shell命令之一：

### 4.1 在Linux上检查JAVA_HOME

只需打开一个终端并输入：

```bash
> $JAVA_HOME/bin/javac -version
```

如果JAVA_HOME指向JDK，则输出应如下所示：

```bash
> javac 1.X.0_XX
```

如果JAVA_HOME不指向JDK，操作系统将抛出一条错误消息：

```bash
> bash: /bin/javac: No such file or directory
```

### 4.2 在Windows上检查JAVA_HOME

打开命令提示符并输入：

```bash
%JAVA_HOME%\bin\javac -version
```

如果JAVA_HOME指向JDK，则输出应如下所示：

```bash
> javac 1.X.0_XX
```

如果JAVA_HOME未指向JDK，操作系统将抛出一条错误消息：

```bash
> The system cannot find the path specified.
```

## 5. 如何解决问题

首先，我们需要知道在哪里可以找到我们的JDK：

-   如果我们使用软件包安装程序安装了JDK发行版，我们应该能够使用操作系统搜索实用程序找到路径
-   如果发行版是便携式的，让我们检查一下我们提取它的文件夹

**一旦我们知道了JDK的路径，我们就可以设置我们的JAVA_HOME环境变量，使用**[适合我们特定操作系统的结果](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)。

## 6. 总结

在这个简短的教程中，我们讨论了Maven中的“JAVA_HOME should point to a JDK not a JRE”错误并检查了其根本原因。

最后，我们讨论了如何检查JAVA_HOME环境变量，以及如何确保它指向JDK。
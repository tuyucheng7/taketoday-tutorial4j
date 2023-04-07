## 一、概述

[类路径](https://en.wikipedia.org/wiki/Classpath)是 Java 世界中的一个基本概念。当我们编译或启动 Java 应用程序时，JVM 会在类路径中查找并加载类。

*我们可以通过java/* j *avac*命令的*-cp*选项或通过*CLASSPATH*环境变量来定义类路径中的元素。无论我们采用哪种方法来设置类路径，我们都需要遵循类路径语法。

在本快速教程中，我们将讨论类路径语法，尤其是 Windows 和 Linux 操作系统上的类路径分隔符。

## 2.类路径分隔符

类路径语法实际上非常简单：由路径分隔符分隔的路径列表。但是，[路径分隔符](https://www.baeldung.com/java-file-vs-file-path-separator#path-separator)本身是系统相关的。

**虽然分号 (;) 在 Microsoft Windows 系统上用作分隔符，但在类 Unix 系统上使用冒号 ( \*:) ：\***

```bash
# On Windows system:
CLASSPATH="PATH1;PATH2;PATH3"

# On Linux system:
CLASSPATH="PATH1:PATH2:PATH3"复制
```

## 3. Linux 上误导性的手册页

我们了解到类路径分隔符可能因操作系统而异。

但是，如果我们仔细查看 Linux 上的 Java*手册*页，就会发现类路径分隔符是分号 ( *;* )。

例如，最新（ver.17）OpenJDK 的*java*命令的*手册页显示：*

>   *–class-path* classpath、*-classpath* classpath 或*-cp* classpath
>   以分号 ( *;* ) 分隔的目录列表、JAR 存档和 ZIP 存档以搜索类文件。
>   ……

[另外，我们可以在Oracle JDK](https://docs.oracle.com/en/java/javase/17/docs/specs/man/java.html#standard-options-for-java)的手册中找到确切的文本。

这是因为 Java 目前针对不同的系统使用相同的手册内容。今年早些时候已经创建了相应的[错误问题。](https://bugs.openjdk.java.net/browse/JDK-8262004)

此外，Java 已经清楚地记录了路径分隔符是系统依赖于*File*类的*[pathSeparatorChar](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#pathSeparatorChar)*字段的。

## 4。结论

在这篇简短的文章中，我们讨论了不同操作系统上的类路径语法。

此外，我们在 Linux 上的 Java 手册页中讨论了有关路径分隔符的错误。

我们应该记住，路径分隔符是系统相关的。**在类 Unix 系统上使用冒号，而在 Microsoft Windows 系统上使用分号。**
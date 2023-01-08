## 1. 概述

不同的操作系统使用不同的字符作为文件和路径分隔符。当我们的应用程序必须在多个平台上运行时，我们需要正确处理这些。

Java 帮助我们选择合适的分隔符并提供函数来帮助我们创建适用于主机操作系统的路径。

在这个简短的教程中，我们将了解如何编写代码以使用正确的文件和路径分隔符。

## 2.文件分隔符

文件分隔符是用于分隔构成特定位置路径的目录名称的字符。

### 2.1. 获取文件分隔符

在Java中有几种获取文件分隔符的方法。

我们可以使用File.separator将分隔符作为字符串获取：

```java
String fileSeparator = File.separator;
```

我们还可以使用File.separatorChar将此分隔符作为字符获取：

```java
char fileSeparatorChar = File.separatorChar;
```

从Java7 开始，我们还可以使用FileSystems：

```java
String fileSeparator = FileSystems.getDefault().getSeparator();
```

输出将取决于主机操作系统。文件分隔符在 Windows 上为 ，在 macOS 和基于 Unix 的操作系统上为/ 。

### 2.2. 构建文件路径

Java 提供了几种方法来从其目录列表构造文件路径。

在这里，我们使用Paths类：

```java
Path path = Paths.get("dir1", "dir2");
```

让我们在 Microsoft Windows 上测试它：

```java
assertEquals("dir1dir2", path.toString());
```

同样，我们可以在 Linux 或 Mac 上进行测试：

```java
assertEquals("dir1/dir2", path.toString());

```

我们还可以使用 文件类：

```java
File file = new File("file1", "file2");
```

让我们在 Microsoft Windows 上测试它：

```java
assertEquals("file1file2", file.toString());
```

同样，我们可以在 Linux 或 Mac 上进行测试：

```java
assertEquals("file1/file2", file.toString());
```

如我们所见，我们可以只提供路径字符串来构造文件路径——我们不需要显式地包含文件分隔符。

## 3.路径分隔符

路径分隔符是操作系统常用的字符，用于分隔路径列表中的各个路径。

### 3.1. 获取路径分隔符

我们可以使用File.pathSeparator将路径分隔符作为字符串获取：

```java
String pathSeparator = File.pathSeparator;
```

我们还可以将路径分隔符作为char：

```arduino
char pathSeparatorChar = File.pathSeparatorChar;
```

这两个示例都返回路径分隔符。它在 Windows 上是分号 (; )，在 Mac 和基于 Unix 的操作系统上是冒号 (:)。

### 3.2. 构建文件路径

我们可以使用分隔符作为分隔符将文件路径构造为 字符串。

让我们试试String.join方法：

```java
String[] pathNames = { "path1", "path2", "path3" };
String path = String.join(File.pathSeparator, pathNames);
```

这里我们在 Windows 上测试我们的代码：

```java
assertEquals("path1;path2;path3", path);
```

文件路径在 Linux 或 Mac 上看起来会有所不同：

```java
assertEquals("path1:path2:path3", path);
```

同样，我们可以使用StringJoiner类：

```java
public static StringJoiner buildPathUsingStringJoiner(String path1, String path2) {
    StringJoiner joiner = new StringJoiner(File.pathSeparator);
    joiner.add(path1);
    joiner.add(path2);
    return joiner;
}
```

让我们在 Microsoft Windows 上测试我们的代码：

```java
assertEquals("path1;path2", buildPathUsingStringJoiner("path1", "path2"));
```

它在 Mac 或 Unix 上的表现会有所不同：

```java
assertEquals("path1:path2", buildPathUsingStringJoiner("path1", "path2"));
```

## 4。总结

在这篇简短的文章中，我们学习了如何使用系统特定的文件分隔符构造路径，以便我们的代码可以在多个操作系统上运行。

我们看到了如何使用内置类Path和File来构造文件路径，并且我们看到了如何获得必要的分隔符以与String连接实用程序一起使用。
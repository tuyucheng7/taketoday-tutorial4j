## 一、概述

[在本教程中，我们将探索使用NIO.2 API](https://www.baeldung.com/java-nio-2-file-api)在 Java 中创建符号链接的不同方法，并探索硬文件链接和软文件链接之间的区别。

## 2. 硬链接与软/符号链接

首先，让我们定义文件链接是什么以及它们的预期行为是什么。文件链接是一个指针，它透明地引用存储在文件系统中的文件。

一个常见的误解是认为文件链接是快捷方式，所以让我们检查一下它们的行为：

-   快捷方式是引用目标文件的常规文件
-   软/符号链接是一个文件指针，其行为与链接到的文件相同——如果目标文件被删除，则链接不可用
-   硬链接是一个文件指针，它反映了它链接到的文件，所以它基本上就像一个克隆。如果目标文件被删除，链接文件仍然有效

[大多数操作系统(Linux、Windows、Mac)已经支持软/硬文件链接，因此使用NIO API](https://docs.oracle.com/javase/tutorial/essential/io/links.html)处理它们应该不是问题。

## 3. 创建链接

首先，我们必须创建一个要链接的目标文件，所以让我们将一些数据排序到一个文件中：

```java
public Path createTextFile() throws IOException {		
    byte[] content = IntStream.range(0, 10000)
      .mapToObj(i -> i + System.lineSeparator())
      .reduce("", String::concat)
      .getBytes(StandardCharsets.UTF_8);
    Path filePath = Paths.get("", "target_link.txt");
    Files.write(filePath, content, CREATE, TRUNCATE_EXISTING);
    return filePath;		
}

```

让我们创建一个指向现有文件的符号链接，确保创建的文件是一个符号链接：

```java
public void createSymbolicLink() throws IOException {
    Path target = createTextFile();
    Path link = Paths.get(".","symbolic_link.txt");
    if (Files.exists(link)) {
        Files.delete(link);
    }
    Files.createSymbolicLink(link, target);
}

```

接下来，让我们看一下硬链接的创建：

```java
public void createHardLink() throws IOException {
    Path target = createTextFile();
    Path link = Paths.get(".", "hard_link.txt");
    if (Files.exists(link)) {
        Files.delete(link);
    }
    Files.createLink(link, target);
}

```

通过列出文件及其差异，我们可以看到软/符号链接文件大小很小，而硬链接使用与链接文件相同的空间：

```bash
 48K	target_link.txt
 48K	hard_link.txt
4.0K	symbolic_link.txt

```

为了清楚地了解可以抛出哪些可能的异常，让我们看一下操作的已检查异常：

-   UnsupportedOperationException – 当 JVM 不支持特定系统中的文件链接时
-   FileAlreadyExistsException – 当链接文件已经存在时，默认不支持覆盖
-   IOException – 当发生 IO 错误时，例如无效的文件路径
-   SecurityException – 当无法创建链接文件或由于文件权限受限而无法访问目标文件时

## 4.链接操作

现在，如果我们有一个带有现有文件链接的给定文件系统，就可以识别它们并显示它们的目标文件：

```java
public void printLinkFiles(Path path) throws IOException {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
        for (Path file : stream) {
            if (Files.isDirectory(file)) {
                printLinkFiles(file);
            } else if (Files.isSymbolicLink(file)) {
                System.out.format("File link '%s' with target '%s' %n", 
                  file, Files.readSymbolicLink(file));
            }
        }
    }
}

```

如果我们在当前路径中执行它：

```java
printLinkFiles(Paths.get("."));

```

我们会得到输出：

```plaintext
File link 'symbolic_link.txt' with target 'target_link.txt'

```

请注意，硬链接文件不能简单地通过NIO 的 API进行识别， 需要[低级操作](https://stackoverflow.com/questions/11045321/get-hard-link-count-in-java)才能处理此类文件。

## 5.总结

本文介绍了不同类型的文件链接、它们与快捷方式的区别，以及如何使用适用于市场上主流文件系统的纯 Java API 创建和操作它们。
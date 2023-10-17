## 1. 概述

在本快速教程中，我们将熟悉几种判断目录是否为空的方法。

## 2. 使用Files.newDirectoryStream

从Java7 开始，[Files.newDirectoryStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#newDirectoryStream(java.nio.file.Path)) 方法返回一个 [DirectoryStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/DirectoryStream.html) <Path>以迭代目录中的所有条目。所以我们可以使用这个 API 来检查给定的目录是否为空：

```java
public boolean isEmpty(Path path) throws IOException {
    if (Files.isDirectory(path)) {
        try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
            return !directory.iterator().hasNext();
        }
    }

    return false;
}
```

对于非目录输入，我们甚至不尝试加载目录条目就返回false ：

```java
Path aFile = Paths.get(getClass().getResource("/notDir.txt").toURI());
assertThat(isEmpty(aFile)).isFalse();
```

另一方面，如果输入是目录，我们将尝试打开 指向该目录的DirectoryStream 。然后，当且仅当第一个 hasNext() 方法调用返回 false时，我们才会认为该目录为空。否则，它不是空的：

```java
Path currentDir = new File("").toPath().toAbsolutePath();
assertThat(isEmpty(currentDir)).isFalse();
```

DirectoryStream 是一个 Closeable 资源，所以我们将它包装在一个try- with [-resources 块](https://www.baeldung.com/java-try-with-resources)中。正如我们所料， isEmpty 方法对空目录返回 true ：

```java
Path path = Files.createTempDirectory("baeldung-empty");
assertThat(isEmpty(path)).isTrue();
```

在这里，我们使用 [Files.createTempDirectory](https://www.baeldung.com/java-nio-2-file-api#creating-temporary-files) 创建一个空的临时目录。

## 3. 使用文件列表

从 JDK 8 开始，[Files.list](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#list(java.nio.file.Path)) 方法在内部使用 Files.newDirectoryStream API 来公开 Stream<Path>。每个 路径 都是给定父目录中的一个条目。因此，我们也可以使用这个 API 来达到同样的目的：

```java
public boolean isEmpty(Path path) throws IOException {
    if (Files.isDirectory(path)) {
        try (Stream<Path> entries = Files.list(path)) {
            return !entries.findFirst().isPresent();
        }
    }
        
    return false;
}
```

同样，我们只使用 findFirst 方法接触第一个条目。如果返回的 [Optional](https://www.baeldung.com/java-optional) 为空，则目录也为空。

[Stream 由 I/O 资源支持](https://www.baeldung.com/java-stream-close)，因此我们确保使用 try-with-resources 块适当地释放它[。 ](https://www.baeldung.com/java-stream-close)

## 4.低效的解决方案

Files.list 和 Files.newDirectoryStream 都会懒惰地 迭代目录条目。因此，他们将非常有效地处理巨大的目录。但是，在这种情况下，这样的解决方案将无法正常工作：

```java
public boolean isEmpty(Path path) {
    return path.toFile().listFiles().length == 0;
}
```

这将急切地加载目录中的所有条目，这在处理大目录时效率非常低。

## 5.总结

在这个简短的教程中，我们熟悉了一些检查目录是否为空的有效方法。
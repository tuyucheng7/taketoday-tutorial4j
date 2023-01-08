## 1. 概述

在本快速教程中，我们将学习列出目录中文件的不同方法。

## 2.上市

我们可以在引用目录的[java.io.File对象上使用](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)[listFiles()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#listFiles())方法列出目录中的所有文件： 

```java
public Set<String> listFilesUsingJavaIO(String dir) {
    return Stream.of(new File(dir).listFiles())
      .filter(file -> !file.isDirectory())
      .map(File::getName)
      .collect(Collectors.toSet());
}
```

如我们所见，listFiles()返回一个File对象数组，这些对象是目录的内容。

我们将从该数组创建一个流。然后我们将过滤掉所有不是子目录的值。最后，我们会将结果收集到一个集合中。

请注意，我们选择了Set类型而不是List。事实上，不能保证listFiles()返回文件的顺序。

在新实例化的File上使用listFiles()方法需要谨慎，因为它可能为空。当提供的目录无效时会发生这种情况。结果，它会抛出一个NullPointerException：

```java
assertThrows(NullPointerException.class,
        () -> listFiles.listFilesUsingJavaIO(INVALID_DIRECTORY));
```

使用listFiles()的另一个缺点是它会一次读取整个目录。因此，对于包含大量文件的文件夹来说可能会出现问题。

因此，让我们讨论另一种方法。

## 3.目录流

Java 7 引入了一个名为[DirectoryStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#newDirectoryStream(java.nio.file.Path))的listFiles替代品。创建目录流是为了与 for-each 结构很好地配合以迭代目录的内容。这意味着，我们不是一次读取所有内容，而是遍历目录的内容。

让我们用它来列出目录的文件：

```java
public Set<String> listFilesUsingDirectoryStream(String dir) throws IOException {
    Set<String> fileSet = new HashSet<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
        for (Path path : stream) {
            if (!Files.isDirectory(path)) {
                fileSet.add(path.getFileName()
                    .toString());
            }
        }
    }
    return fileSet;
}
```

上面，我们让 Java通过[try-with-resources](https://www.baeldung.com/java-try-with-resources)构造来处理DirectoryStream资源的关闭。同样，我们返回过滤掉目录的文件夹中的一组文件。

尽管名称令人困惑，但DirectoryStream并不是[Stream API](https://www.baeldung.com/java-8-streams)的一部分。

现在我们将了解如何使用 Stream API 列出文件。

## 4.Java8 中的列表

Java 8在[java.nio.file.Files中引入了一个新的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html)[list()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#list(java.nio.file.Path))方法。list 方法返回目录中延迟填充的条目流。

### 4.1. 使用Files.list()

让我们看一个简单的例子：

```java
public Set<String> listFilesUsingFilesList(String dir) throws IOException {
    try (Stream<Path> stream = Files.list(Paths.get(dir))) {
        return stream
          .filter(file -> !Files.isDirectory(file))
          .map(Path::getFileName)
          .map(Path::toString)
          .collect(Collectors.toSet());
    }
}
```

同样，我们返回文件夹中包含的一组文件。虽然这可能看起来类似于listFiles() ，但我们获取文件Path的方式不同。

在这里， list()方法返回一个Stream对象，该对象延迟填充目录的条目。因此，我们可以更有效地处理大型文件夹。

同样，我们使用try-with-resources构造创建了流，以确保目录资源在读取流后关闭。

### 4.2. 与File.list()的比较

我们不能将Files类提供的list()方法与File 对象的list()方法混淆。后者返回目录所有条目名称的字符串数组，包括文件和目录。

## 5. 步行

除了列出文件之外，我们可能希望遍历目录到比其直接文件条目更深的一个或多个级别。在这种情况下，我们可以使用[walk()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#walk(java.nio.file.Path,int,java.nio.file.FileVisitOption...))：

```java
public Set<String> listFilesUsingFileWalk(String dir, int depth) throws IOException {
    try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
        return stream
          .filter(file -> !Files.isDirectory(file))
          .map(Path::getFileName)
          .map(Path::toString)
          .collect(Collectors.toSet());
    }
}
```

walk ()方法以其参数提供的深度遍历目录。在这里，我们遍历了文件树并将所有文件的名称收集到一个Set中。

此外，我们可能希望在迭代每个文件时采取一些行动。在这种情况下，我们可以通过提供描述我们要采取的操作的[访问者来使用](https://www.baeldung.com/java-visitor-pattern)[walkFileTree()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#walkFileTree(java.nio.file.Path,java.nio.file.FileVisitor))方法：

```java
public Set<String> listFilesUsingFileWalkAndVisitor(String dir) throws IOException {
    Set<String> fileList = new HashSet<>();
    Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (!Files.isDirectory(file)) {
                fileList.add(file.getFileName().toString());
            }
            return FileVisitResult.CONTINUE;
        }
    });
    return fileList;
}
```

当我们想要进行额外的读取、移动或删除文件时，此方法会派上用场。

如果我们尝试传入有效文件而不是目录，walk ()和walkFileTree()方法不会抛出 NullPointerException 。事实上，Stream保证至少返回一个元素，即提供的文件本身：

```java
Set<String> expectedFileSet = Collections.singleton("test.xml");
String filePathString = "src/test/resources/listFilesUnitTestFolder/test.xml";
assertEquals(expectedFileSet, listFiles.listFilesUsingFileWalk(filePathString, DEPTH));
```

## 六，总结

在这篇简短的文章中，我们探索了列出目录中文件的不同方法。

首先，我们使用listFiles()获取文件夹的所有内容。然后我们使用DirectoryStream来延迟加载目录的内容。我们还使用了Java 8 引入的list()方法。

最后，我们演示了用于处理文件树的walk()和walkFileTree()方法。
## 1. 概述

在本快速教程中，我们将仔细研究如何在Java中查找特定目录中最后修改的文件。

首先，我们将从[遗留 IO](https://www.baeldung.com/java-io-vs-nio#1-io---javaio)和[现代 NIO](https://www.baeldung.com/java-io-vs-nio#2-nio---javanio) API 开始。然后，我们将看到如何使用[Apache Commons IO](https://commons.apache.org/proper/commons-io/)库来完成同样的事情。

## 2. 使用java.io API

遗留的java.io包提供了File类来封装文件和目录路径名的抽象表示。

值得庆幸的是，File类带有一个名为lastModified() 的便捷方法。此方法返回由抽象路径名表示的文件的最后修改时间。

现在让我们看看如何使用java.io.File类来实现预期目的：

```java
public static File findUsingIOApi(String sdir) {
    File dir = new File(sdir);
    if (dir.isDirectory()) {
        Optional<File> opFile = Arrays.stream(dir.listFiles(File::isFile))
          .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

        if (opFile.isPresent()){
            return opFile.get();
        }
    }

    return null;
}
```

如我们所见，我们使用[Java 8 Stream API](https://www.baeldung.com/java-8-streams)循环遍历一组文件。然后，我们调用max()操作来获取具有最近修改的文件。

请注意，我们使用[Optional实例](https://www.baeldung.com/java-optional)来封装最后修改的文件。

请记住，这种方法被认为是过时的和过时的。但是，如果我们想与Java遗留 IO 世界保持兼容，我们可以使用它。

## 3.使用java.nio API

NIO API的引入是文件系统管理的一个转折点。Java 7 中发布的新[版本 NIO.2](https://www.baeldung.com/java-nio-2-file-api)带有一组增强功能，可以更好地管理和操作文件。

事实上，java.nio.file.Files类在Java中操作文件和目录时提供了极大的灵活性。

那么，让我们看看如何使用Files类来获取目录中最后修改的文件：

```java
public static Path findUsingNIOApi(String sdir) throws IOException {
    Path dir = Paths.get(sdir);
    if (Files.isDirectory(dir)) {
        Optional<Path> opPath = Files.list(dir)
          .filter(p -> !Files.isDirectory(p))
          .sorted((p1, p2)-> Long.valueOf(p2.toFile().lastModified())
            .compareTo(p1.toFile().lastModified()))
          .findFirst();

        if (opPath.isPresent()){
            return opPath.get();
        }
    }

    return null;
}
```

与第一个示例类似，我们依靠 Steam API 来仅获取文件。然后，我们借助[lambda 表达式](https://www.baeldung.com/java-8-lambda-expressions-tips)根据最后修改时间对文件进行排序。

## 4.使用 Apache Commons IO

Apache Commons IO 将文件系统管理提升到了一个新的水平。它提供了一组方便的类、文件比较器、文件过滤器等等。

对我们来说幸运的是，该库提供了 LastModifiedFileComparator类，它可以用作 比较器来按文件的最后修改时间对文件数组进行排序。

首先，我们需要在我们的pom.xml中添加[commons-io依赖](https://search.maven.org/classic/#artifactdetails|commons-io|commons-io|2.7|jar)：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

最后，让我们展示如何使用 Apache Commons IO 在文件夹中查找最后修改的文件：

```java
public static File findUsingCommonsIO(String sdir) {
    File dir = new File(sdir);
    if (dir.isDirectory()) {
        File[] dirFiles = dir.listFiles((FileFilter)FileFilterUtils.fileFileFilter());
        if (dirFiles != null && dirFiles.length > 0) {
            Arrays.sort(dirFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            return dirFiles[0];
        }
     }

    return null;
}
```

如上所示，我们使用 单例实例LASTMODIFIED_REVERSE对我们的文件数组进行倒序排序。

由于数组是反向排序的，那么最后修改的文件就是数组的第一个元素。

## 5.总结

在本教程中，我们探讨了在特定目录中查找最后修改文件的不同方法。在此过程中，我们使用了属于 JDK 和 Apache Commons IO 外部库的 API。
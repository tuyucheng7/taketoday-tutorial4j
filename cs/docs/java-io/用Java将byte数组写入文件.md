## 1. 概述

在本快速教程中，我们将学习几种将Java字节数组写入文件的不同方法。我们将从头开始，使用JavaIO 包。接下来，我们将看一个使用JavaNIO 的示例。之后，我们将使用 Google Guava 和 Apache Commons IO。

## 2.Java IO

Java 的 IO 包从 JDK 1.0 开始就有了，它提供了一组用于读写数据的类和接口。

让我们使用 FileOutputStream将图像写入文件：

```java
File outputFile = tempFolder.newFile("outputFile.jpg");
try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
    outputStream.write(dataForWriting);
}
```

我们打开一个输出流到我们的目标文件，然后我们可以简单地将我们的byte[] dataForWriting传递给write方法。请注意，我们在这里使用[try-with-resources块](https://www.baeldung.com/java-try-with-resources)来确保在抛出IOException时关闭OutputStream 。

## 3.Java NIO

Java NIO 包在Java1.4 中引入，[NIO 的文件系统 API](https://www.baeldung.com/java-nio-2-file-api)作为扩展在Java7 中引入。Java NIO 使用缓冲且是非阻塞的，而JavaIO 使用阻塞流。java.nio.file包中创建文件资源的语法更加简洁。

我们可以使用Files类将byte[]写在一行中：

```java
Files.write(outputFile.toPath(), dataForWriting);
```

我们的示例要么创建一个文件，要么截断现有文件并打开它进行写入。我们还可以使用Paths.get(“path/to/file”)或Paths.get(“path”, “to”, “file”)来构造描述文件存储位置的路径。Path是表示路径的JavaNIO 本机方式。

如果我们需要覆盖文件打开行为，我们也可以向 write方法提供[OpenOption](https://www.baeldung.com/java-file-options) 。

## 4.谷歌番石榴

[Guava](https://www.baeldung.com/guava-write-to-file-read-from-file)是 Google 的一个库，它提供了多种类型来执行Java中的常见操作，包括 IO。

让我们将[Guava](https://search.maven.org/search?q=g:com.google.guava a:guava)导入到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>

```

### 4.1. 番石榴文件

与JavaNIO 包一样，我们可以将byte[]写在一行中：

```java
Files.write(dataForWriting, outputFile);
```

Guava 的Files.write方法也采用可选的OptionOptions并使用与java.nio.Files.write相同的默认值。

不过这里有一个问题：Guava Files.write方法标有@Beta注解。[根据文档](https://github.com/google/guava#important-warnings)，这意味着它可以随时更改，因此不建议在库中使用。

所以，如果我们正在编写一个库项目，我们应该考虑使用ByteSink。

### 4.2. 字节汇

我们还可以创建一个ByteSink来写入我们的byte[]：

```java
ByteSink byteSink = Files.asByteSink(outputFile);
byteSink.write(dataForWriting);
```

ByteSink是我们可以写入字节的目的地。 它向目的地提供一个OutputStream 。

如果我们需要使用java.nio.files.Path或提供特殊的OpenOption，我们可以使用MoreFiles类获取我们的ByteSink：

```java
ByteSink byteSink = MoreFiles.asByteSink(outputFile.toPath(), 
    StandardOpenOption.CREATE, 
    StandardOpenOption.WRITE);
byteSink.write(dataForWriting);
```

## 5.阿帕奇共享IO

Apache [Commons IO](https://www.baeldung.com/apache-commons-io)提供了一些常见的文件任务。

让我们导入最新版本的[commons-io](https://search.maven.org/search?q=g:commons-io a:commons-io)：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>

```

现在，让我们使用FileUtils类编写byte[]：

```java
FileUtils.writeByteArrayToFile(outputFile, dataForWriting);
```

FileUtils.writeByteArrayToFile方法类似于我们使用的其他方法，我们给它一个代表我们想要的目的地和我们正在写入的二进制数据的文件。如果我们的目标文件或任何父目录不存在，它们将被创建。

## 六，总结

在这个简短的教程中，我们学习了如何使用普通Java和两个流行的Java实用程序库：Google Guava 和 Apache Commons IO 将二进制数据从byte[]写入文件。
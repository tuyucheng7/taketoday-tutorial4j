## 1. 概述

在这篇简短的文章中，我们将研究java.nio包中的[MappedByteBuffer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/MappedByteBuffer.html) 。该实用程序对于高效的文件读取非常有用。

## 2. MappedByteBuffer是如何工作的

当我们加载文件的一个区域时，我们可以将它加载到以后可以访问的特定内存区域。

当我们知道我们需要多次读取一个文件的内容时，优化代价高昂的过程是个好主意，例如通过将该内容保存在内存中。因此，后续对该文件部分的查找将仅转到主内存，而无需从光盘加载数据，从而大大减少了延迟。

使用MappedByteBuffer时我们需要注意的一件事是当我们处理来自光盘的非常大的文件时——我们需要确保文件适合内存。

否则，我们可能会填满整个内存，结果会遇到常见的OutOfMemoryException。我们可以通过仅加载文件的一部分来克服这个问题——例如基于使用模式。

## 3. 使用MappedByteBuffer读取文件

假设我们有一个名为fileToRead.txt的文件，其中包含以下内容：

```plaintext
This is a content of the file
```

该文件位于/resource目录中，因此我们可以使用以下函数加载它：

```java
Path getFileURIFromResources(String fileName) throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    return Paths.get(classLoader.getResource(fileName).getPath());
}
```

要从文件创建MappedByteBuffer ，首先我们需要从中创建一个FileChannel 。创建通道后，我们可以在其上调用map()方法，传入MapMode、我们要读取的位置以及指定我们想要的字节数的大小参数：

```java
CharBuffer charBuffer = null;
Path pathToRead = getFileURIFromResources("fileToRead.txt");

try (FileChannel fileChannel (FileChannel) Files.newByteChannel(
  pathToRead, EnumSet.of(StandardOpenOption.READ))) {
 
    MappedByteBuffer mappedByteBuffer = fileChannel
      .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

    if (mappedByteBuffer != null) {
        charBuffer = Charset.forName("UTF-8").decode(mappedByteBuffer);
    }
}
```

一旦我们将文件映射到内存映射缓冲区中，我们就可以将其中的数据读取到 CharBuffer 中。需要注意的重要一点是，虽然我们在调用传递MappedByteBuffer 的decode()方法时正在读取文件的内容，但我们是从内存中读取的，而不是从光盘中读取的。因此，读取将非常快。

我们可以断言我们从文件中读取的内容是fileToRead.txt文件的实际内容：

```java
assertNotNull(charBuffer);
assertEquals(
  charBuffer.toString(), "This is a content of the file");
```

从mappedByteBuffer每次后续读取都将非常快，因为文件的内容已映射到内存中，并且无需从磁盘查找数据即可完成读取。

## 4. 使用MappedByteBuffer写入文件

假设我们想使用MappedByteBuffer API 将一些内容写入文件fileToWriteTo.txt。为此，我们需要打开FileChannel并对其调用map()方法，传入FileChannel.MapMode.READ_WRITE。

接下来，我们可以使用 MappedByteBuffer 的put()方法将CharBuffer的内容保存到文件中：

```java
CharBuffer charBuffer = CharBuffer
  .wrap("This will be written to the file");
Path pathToWrite = getFileURIFromResources("fileToWriteTo.txt");

try (FileChannel fileChannel = (FileChannel) Files
  .newByteChannel(pathToWrite, EnumSet.of(
    StandardOpenOption.READ, 
    StandardOpenOption.WRITE, 
    StandardOpenOption.TRUNCATE_EXISTING))) {
    
    MappedByteBuffer mappedByteBuffer = fileChannel
      .map(FileChannel.MapMode.READ_WRITE, 0, charBuffer.length());
    
    if (mappedByteBuffer != null) {
        mappedByteBuffer.put(
          Charset.forName("utf-8").encode(charBuffer));
    }
}
```

我们可以通过读取文件的内容断言charBuffer的实际内容已写入文件：

```java
List<String> fileContent = Files.readAllLines(pathToWrite);
assertEquals(fileContent.get(0), "This will be written to the file");
```

## 5.总结

在本快速教程中，我们查看了java.nio包中的MappedByteBuffer构造。

这是一种非常有效的多次读取文件内容的方法，因为文件被映射到内存中，后续读取不需要每次都去光盘。
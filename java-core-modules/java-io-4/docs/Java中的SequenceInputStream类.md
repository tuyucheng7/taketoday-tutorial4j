## 1. 概述

在本教程中，我们将学习如何在Java中使用SequenceInputStream类。特别是，此类有助于连续读取一系列文件或流。

有关JavaIO 和其他相关Java类的更多基础知识，我们可以阅读[Java IO 教程](https://www.baeldung.com/java-io)。

## 2.使用SequenceInputStream类

SequenceInputStream将两个或多个InputStream对象作为源。它按照给定的顺序从一个源接一个地读取。当它从第一个InputStream完成读取时，它会自动从第二个 InputStream 开始读取。这个过程一直持续到它完成从所有源流的读取。

### 2.1. 对象创建

我们可以使用两个InputStream对象初始化一个SequenceInputStream ：

```java
InputStream first = new FileInputStream(file1);
InputStream second = new FileInputStream(file2);
SequenceInputStream sequenceInputStream = new SequenceInputStream(first, second);
```

我们还可以使用InputStream对象的枚举来实例化它：

```java
Vector<InputStream> inputStreams = new Vector<>();
for (String fileName: fileNames) {
    inputStreams.add(new FileInputStream(fileName));
}
sequenceInputStream = new SequenceInputStream(inputStreams.elements());
```

### 2.2. 从流中读取

SequenceInputStream提供了两种简单的方法来读取输入源。第一种方法读取一个字节，而第二种方法读取一个字节数组。

要读取单个字节的数据，我们使用read()方法：

```java
int byteValue = sequenceInputStream.read();
```

在上面的示例中，read 方法返回流中的下一个字节 (0 – 255) 值。如果流结束，则返回 -1。

我们还可以读取字节数组：

```java
byte[] bytes = new byte[100];
sequenceInputStream.read(bytes, 0, 50);
```

在上面的示例中，它读取50 个字节并将它们从索引0开始放置。

### 2.3. 显示序列读取的示例

取两个字符串作为输入源来演示读取顺序：

```java
InputStream first = new ByteArrayInputStream("One".getBytes());
InputStream second = new ByteArrayInputStream("Magic".getBytes());
SequenceInputStream sequenceInputStream = new SequenceInputStream(first, second);
StringBuilder stringBuilder = new StringBuilder();
int byteValue;
while ((byteValue = sequenceInputStream.read()) != -1) {
    stringBuilder.append((char) byteValue);
}
assertEquals("OneMagic", stringBuilder.toString());
```

在上面的例子中，如果我们打印stringBuilder.toString()它会显示以下输出：

```java
OneMagic
```

## 3.总结

在这篇简短的文章中，我们了解了如何使用SequenceInputStream。它只是将所有底层输入流合并为一个流。
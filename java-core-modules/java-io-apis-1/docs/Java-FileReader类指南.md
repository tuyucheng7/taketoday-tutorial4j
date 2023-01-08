## 1. 概述

顾名思义，[FileReader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/FileReader.html)是一个可以轻松读取文件内容的Java类。

在本教程中，我们将学习Reader的基本概念以及如何使用FileReader类在Java中对字符流进行读取操作。

## 2.读者 基础

如果我们查看FileReader类的代码，就会注意到该类包含用于创建FileReader对象的最少代码，并且没有其他方法。

这就提出了诸如“谁在这门课背后做繁重的工作？”之类的问题。

[要回答这个问题，就必须了解Java中Reader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Reader.html)类的概念和层次结构。

Reader是一个抽象基类，通过其具体实现之一可以读取字符。它定义了以下从任何介质(如内存或文件系统)读取字符的基本操作：

-   读取单个字符
-   读取字符数组
-   标记并重置字符流中的给定位置
-   读取字符流时跳过位置
-   关闭输入流

自然地， Reader类的所有实现都必须实现所有抽象方法，即read()和close()。此外，大多数实现还覆盖其他继承的方法以提供额外的功能或更好的性能。

### 2.1. 何时使用FileReader

现在我们对Reader有了一些了解，我们准备将焦点带回FileReader类。

FileReader从InputStreamReader继承其功能，这是一个Reader实现，旨在从输入流中读取字节作为字符。

让我们在类定义中查看此层次结构：

```java
public class InputStreamReader extends Reader {}

public class FileReader extends InputStreamReader {}
```

通常，我们可以使用InputStreamReader从任何输入源读取字符。

然而，当谈到从文件中读取文本时，使用 InputStreamReader就像用剑切苹果一样。当然，正确的工具是一把刀，这正是FileReader 所承诺的。

当我们想使用系统的默认字符集从文件中读取文本时，我们可以使用FileReader 。对于任何其他高级功能，直接使用InputStreamReader类是理想的选择。

## 3. 使用FileReader读取文本文件

让我们来完成一个使用FileReader实例从HelloWorld.txt文件中读取字符的编码练习。

### 3.1. 创建文件读取器

作为一个方便的类，FileReader提供了三个重载的构造函数，可用于初始化一个可以从文件中读取作为输入源的读取器。

让我们来看看这些构造函数：

```java
public FileReader(String fileName) throws FileNotFoundException {
    super(new FileInputStream(fileName));
}

public FileReader(File file) throws FileNotFoundException {
    super(new FileInputStream(file));
}

public FileReader(FileDescriptor fd) {
    super(new FileInputStream(fd));
}
```

在我们的例子中，我们知道输入文件的文件名。因此，我们可以使用第一个构造函数来初始化读取器：

```java
FileReader fileReader = new FileReader(path);
```

### 3.2. 读取单个字符

接下来，让我们创建readAllCharactersOneByOne()，一种从文件中一次读取一个字符的方法：

```java
public static String readAllCharactersOneByOne(Reader reader) throws IOException {
    StringBuilder content = new StringBuilder();
    int nextChar;
    while ((nextChar = reader.read()) != -1) {
        content.append((char) nextChar);
    }
    return String.valueOf(content);
}
```

从上面的代码可以看出，我们在循环中使用了 read()方法一个一个地读取字符，直到它返回 -1，这意味着没有更多的字符可以读取了。

现在，让我们通过验证从文件中读取的文本是否与预期文本匹配来测试我们的代码：

```java
@Test
public void givenFileReader_whenReadAllCharacters_thenReturnsContent() throws IOException {
    String expectedText = "Hello, World!";
    File file = new File(FILE_PATH);
    try (FileReader fileReader = new FileReader(file)) {
        String content = FileReaderExample.readAllCharactersOneByOne(fileReader);
        Assert.assertEquals(expectedText, content);
    }
}
```

### 3.3. 读取字符数组

我们甚至可以使用继承的read(char cbuf[], int off, int len)方法一次读取多个字符：

```java
public static String readMultipleCharacters(Reader reader, int length) throws IOException {
    char[] buffer = new char[length];
    int charactersRead = reader.read(buffer, 0, length);
    if (charactersRead != -1) {
        return new String(buffer, 0, charactersRead);
    } else {
        return "";
    }
}
```

 在读取数组中的多个字符时，read()的返回值存在细微差别。这里的返回值要么是读取的字符数，要么是-1(如果读取器已到达输入流的末尾)。

接下来，让我们测试一下我们代码的正确性：

```java
@Test
public void givenFileReader_whenReadMultipleCharacters_thenReturnsContent() throws IOException {
    String expectedText = "Hello";
    File file = new File(FILE_PATH);
    try (FileReader fileReader = new FileReader(file)) {
        String content = FileReaderExample.readMultipleCharacters(fileReader, 5);
        Assert.assertEquals(expectedText, content);
    }
}
```

## 4. 限制

我们已经看到FileReader类依赖于默认的系统字符编码。

因此，对于需要为字符集、缓冲区大小或输入流使用自定义值的情况，我们必须使用InputStreamReader。

此外，我们都知道 I/O 周期是昂贵的并且会给我们的应用程序带来延迟。因此，通过在我们的FileReader对象周围包装一个[BufferedReader](https://www.baeldung.com/java-buffered-reader)来最小化 I/O 操作的数量符合我们的最大利益：

```java
BufferedReader in = new BufferedReader(fileReader);
```

## 5.总结

在本教程中，我们了解了Reader的基本概念，以及FileReader如何通过一些示例使对文本文件的读取操作变得简单。
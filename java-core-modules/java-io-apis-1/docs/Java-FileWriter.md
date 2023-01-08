## 1. 概述

在本教程中，我们将学习和理解java.io包中的FileWriter类。

## 2.文件写入器

FileWriter是 专门用于写入字符文件的[OutputStreamWriter](https://www.baeldung.com/java-outputstream)。它不公开任何新操作，但使用从OutputStreamWriter和Writer类继承的操作。

在Java11 之前，FileWriter使用默认字符编码和默认字节缓冲区大小。但是，Java 11 引入了四个接受[Charset 的](https://www.baeldung.com/java-char-encoding)新构造函数，从而允许用户指定Charset。不幸的是，我们仍然无法修改字节缓冲区大小，它被设置为 8192。

### 2.1. 实例化FileWriter

如果我们使用Java11 之前的Java版本，则FileWriter类中有五个构造函数。

让我们看一下各种构造函数：

```java
public FileWriter(String fileName) throws IOException {
    super(new FileOutputStream(fileName));
}

public FileWriter(String fileName, boolean append) throws IOException {
    super(new FileOutputStream(fileName, append));
}

public FileWriter(File file) throws IOException {
    super(new FileOutputStream(file));
}

public FileWriter(File file, boolean append) throws IOException {
    super(new FileOutputStream(file, append));
}

public FileWriter(FileDescriptor fd) {
    super(new FileOutputStream(fd));
}
```

Java 11 引入了四个额外的构造函数：

```java
public FileWriter(String fileName, Charset charset) throws IOException {
    super(new FileOutputStream(fileName), charset);
}

public FileWriter(String fileName, Charset charset, boolean append) throws IOException {
    super(new FileOutputStream(fileName, append), charset);
}

public FileWriter(File file, Charset charset) throws IOException {
    super(new FileOutputStream(file), charset);
}

public FileWriter(File file, Charset charset, boolean append) throws IOException {
    super(new FileOutputStream(file, append), charset);
}
```

### 2.2. 将字符串 写入文件

现在让我们使用FileWriter构造函数之一来创建FileWriter的实例，然后写入文件：

```java
try (FileWriter fileWriter = new FileWriter("src/test/resources/FileWriterTest.txt")) {
    fileWriter.write("Hello Folks!");
}
```

我们使用了接受文件名的FileWriter的单参数构造函数。然后我们使用从Writer 类继承的write(String str)操作。由于FileWriter是AutoCloseable，我们使用了[try-with-resources](https://www.baeldung.com/java-try-with-resources)这样我们就不必显式关闭FileWriter。

在执行上面的代码时，字符串将被写入指定的文件：

```plaintext
Hello Folks!
```

FileWriter不保证 FileWriterTest.txt 文件是否可用或是否已创建。它依赖于底层平台。

我们还必须注意，某些平台可能只允许一个FileWriter实例打开文件。在这种情况下，如果涉及的文件已经打开，则FileWriter类的其他构造函数将失败。

### 2.3. 将字符串 附加到文件

我们经常需要将数据附加到文件的现有内容中。现在让我们看一个支持追加的FileWriter的例子：

```java
try (FileWriter fileWriter = new FileWriter("src/test/resources/FileWriterTest.txt", true)) {
    fileWriter.write("Hello Folks Again!");
}
```

如我们所见，我们使用了接受文件名和布尔标志append的双参数构造函数。将标志append传递为true创建一个FileWriter，它允许我们将文本附加到文件的现有内容。

在执行代码时，我们会将String附加到指定文件的现有内容：

```java
Hello Folks!Hello Folks Again!

```

## 3.总结

在本文中，我们了解了便利类FileWriter以及创建FileWriter的几种方法。然后我们用它来将数据写入文件。
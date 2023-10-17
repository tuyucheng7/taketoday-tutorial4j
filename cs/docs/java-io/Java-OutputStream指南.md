## 1. 概述

在本教程中，我们将探索有关Java类OutputStream的详细信息。O utputStream是一个抽象类。这用作表示字节输出流的所有类的超类。

随着我们的深入，我们将更详细地研究“输出”和“流”等这些词的含义。

## 二、Java IO简介

OutputStream 是JavaIO API 的一部分，它定义了在Java中执行 I/O 操作所需的类。这些都打包在java.io命名空间中。这是自 1.0 版以来Java中可用的核心包之一。

从Java1.4 开始，我们还将JavaNIO 打包在命名空间java.nio中，它支持非阻塞输入和输出操作。然而，本文的重点是作为JavaIO 一部分的ObjectStream 。

[可以在此处](https://docs.oracle.com/javase/8/docs/technotes/guides/io/index.html)找到与JavaIO 和JavaNIO 相关的详细信息 。

### 2.1. 输入输出

Java IO 基本上提供了一种从源读取数据并将数据写入目标的机制。输入代表来源而输出代表这里的目的地。

这些源和目标可以是从文件、管道到网络连接的任何内容。

### 2.2. 溪流

Java IO 提供了流的概念，它基本上表示连续的数据流。流可以支持许多不同类型的数据，如字节、字符、对象等。

此外，与源或目标的连接是流所代表的。因此，它们分别以InputStream或OutputStream 的形式出现。

## 3、 OutputStream的接口

OutputStream实现了一堆接口，这些接口为其子类提供了一些独特的特性。让我们快速浏览一下。

### 3.1. 可关闭

接口Closeable提供了一个名为close() 的方法，它处理关闭数据源或数据目标。OutputStream的每个实现都必须提供此方法的实现。他们可以在这里执行释放资源的操作。

### 3.2. 可自动关闭

接口AutoCloseable还提供了一个名为close()的方法，其行为与Closeable中的方法类似。然而，在这种情况下，方法close()会在退出 try-with-resource 块时自动调用。

有关 try-with-resource 的更多详细信息，请参见 [此处](https://www.baeldung.com/java-try-with-resources)。

### 3.3. 可冲洗

接口Flushable提供了一个名为flush()的方法，它处理将数据刷新到目的地。

OutputStream的特定实现可能会选择缓冲先前写入的字节以进行优化，但对flush() 的调用会使其立即写入目标。

## 4.OutputStream中的方法

OutputStream有几个方法，每个实现类都必须为它们各自的数据类型实现这些方法。

这些不同于它从Closeable和Flushable接口继承的close()和flush()方法。

### 4.1. 写(int b)

我们可以使用此方法将一个特定字节写入OutputStream。由于参数“int”包含四个字节，作为合约，只有第一个低位字节被写入，其余三个高位字节被忽略：

```java
public static void fileOutputStreamByteSingle(String file, String data) throws IOException {
    byte[] bytes = data.getBytes();
    try (OutputStream out = new FileOutputStream(file)) {
        out.write(bytes[6]);
    }
}
```

如果我们用数据调用这个方法“Hello World！”，我们得到的结果是一个包含以下文本的文件：

```plaintext
W
```

正如我们所见，这是索引为第六的字符串的第七个字符。

### 4.2. 写(字节[] b，int off，int length)

write()方法的这个重载版本 用于将字节数组的子序列写入OutputStream。

它可以从参数指定的字节数组中写入“length”字节数，从“off”确定的偏移量开始到 OutputStream ：

```java
public static void fileOutputStreamByteSubSequence(
  String file, String data) throws IOException {
    byte[] bytes = data.getBytes();
    try (OutputStream out = new FileOutputStream(file)) {
        out.write(bytes, 6, 5);
    }
}
```

如果我们现在使用与以前相同的数据调用此方法，我们将在输出文件中得到以下文本：

```plaintext
World
```

这是我们数据的子字符串，从索引 5 开始，由五个字符组成。

### 4.3. 写(字节[] b)

这是write()方法的另一个重载版本，它可以按照OutputStream的参数指定写入整个字节数组。

这与调用write(b, 0, b.lengh)具有相同的效果：

```java
public static void fileOutputStreamByteSequence(String file, String data) throws IOException {
    byte[] bytes = data.getBytes();
    try (OutputStream out = new FileOutputStream(file)) {
        out.write(bytes);
    }
}
```

当我们现在用相同的数据调用这个方法时，我们的输出文件中有整个字符串：

```plaintext
Hello World!
```

## 5. OutputStream的直接子类

现在我们将讨论OutputStream的一些直接已知子类，它们分别表示它们定义的OutputStream的特定数据类型。

除了实现从OutputStream继承的方法之外，它们还定义了自己的方法。

我们不会深入讨论这些子类的细节。

### 5.1. 文件输出流

顾名思义，FileOutputStream是将数据写入File的OutputStream。FileOutputStream与任何其他OutputStream一样，可以写入原始字节流。

作为上一节的一部分，我们已经检查了FileOutputStream中的不同方法。

### 5.2. 字节数组输出流

ByteArrayOutputStream是OutputStream的一种实现，可以将数据写入字节数组。随着ByteArrayOutputStream向其写入数据，缓冲区不断增长。

我们可以将缓冲区的默认初始大小保持为 32 字节，或者使用可用的构造函数之一设置特定大小。

这里需要注意的重要一点是方法close() 实际上没有任何效果。即使在调用close()之后，也可以安全地调用ByteArrayOutputStream中的其他方法。

### 5.3. 过滤器输出流

OutputStream 主要将字节流写入目的地，但它也可以在这样做之前转换数据。FilterOutputStream表示执行特定数据转换的所有此类类的超类。FilterOutputStream始终使用现有的OutputStream构造。

FilterOutputStream的一些示例是 BufferedOutputStream、CheckedOutputStream、CipherOutputStream、DataOutputStream、DeflaterOutputStream、DigestOutputStream、InflaterOutputStream、PrintStream。

### 5.4. 对象输出流

ObjectOutputStream可以将Java对象的原始数据类型和图形写入目标。我们可以使用现有的OutputStream构造一个ObjectOutputStream来写入特定的目的地，如 File。

请注意，对象必须为ObjectOutputStream实现Serializable才能将它们写入目的地。[你可以在此处](https://www.baeldung.com/java-serialization)找到有关Java序列化的更多详细信息 。

### 5.5. 管道输出流

PipedOutputStream可用于创建通信管道。PipedOutputStream可以写入连接的PipedInputStream可以 读取的数据。

PipedOutputStream具有一个构造函数，用于将它与PipedInputStream连接起来。或者，我们可以稍后使用PipedOutputStream中提供的称为connect()的方法来完成此操作。

## 6.输出流缓冲

输入和输出操作通常涉及相对昂贵的操作，如磁盘访问、网络活动等。经常执行这些操作会降低程序的效率。

We have “buffered streams” of data inJavato handle these scenarios. BufferedOutputStream writes data to a buffer instead which is flushed to the destination less often, when the buffer gets full, or the method flush() is called.

BufferedOutputStream extends FilterOutputStream discussed earlier and wraps an existing OutputStream to write to a destination:

```java
public static void bufferedOutputStream(
  String file, String ...data) throws IOException {
 
    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
        for(String s : data) {
            out.write(s.getBytes());
            out.write(" ".getBytes());
        }
    }
}Copy
```

The critical point to note is that every call to write() for each data argument only writes to the buffer and does not result in a potentially expensive call to the File.

在上面的例子中，如果我们用“Hello”、“World!”这样的数据调用这个方法，这只会导致当代码从调用方法close的 try-with-resources 块退出时将数据写入文件()在BufferedOutputStream上。

这会产生一个包含以下文本的输出文件：

```plaintext
Hello World!
```

## 7. 用OutputStreamWriter写文本

如前所述，字节流表示可能是一堆文本字符的原始数据。现在我们可以获取字符数组并自己执行到字节数组的转换：

```java
byte[] bytes = data.getBytes();
```

Java 提供了方便的类来弥补这一差距。对于OutputStream的情况，此类是OutputStreamWriter。OutputStreamWriter包装了一个OutputStream并可以直接将字符写入所需的目的地。

我们还可以选择 为OutputStreamWriter 提供编码字符集：

```java
public static void outputStreamWriter(String file, String data) throws IOException {
    try (OutputStream out = new FileOutputStream(file); 
        Writer writer = new OutputStreamWriter(out,"UTF-8")) {
        writer.write(data);
    }
}
```

现在我们可以看到，我们不必在使用FileOutputStream 之前执行字符数组到字节数组的转换。 OutputStreamWriter 为我们做了这件事很方便。

当我们用“Hello World!”之类的数据调用上述方法时，结果会生成一个文本如下的文件，这并不奇怪：

```plaintext
Hello World!
```

## 八、总结

在本文中，我们讨论了Java抽象类OutputStream。我们了解了它实现的接口和它提供的方法。

然后我们讨论了Java 中可用的OutputStream的一些子类。我们最后谈到了缓冲和字符流。
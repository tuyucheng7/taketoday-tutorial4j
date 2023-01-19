## 1. 概述

在本快速教程中，我们将了解使用 Kotlin 扩展方法将内容写入文件的各种方式——在其标准库中可用。

## 2.科特林文件扩展名

[Kotlin 以java.io.File](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/index.html)的扩展方法的形式提供了多种写入文件的方法。

我们将使用其中的几个来演示我们可以使用 Kotlin 实现此目的的不同方法：

-   writeText—— 让我们直接从一个 字符串中写入
-   writeBytes –使我们能够直接从ByteArray写入
-   printWriter – 为我们提供了一个PrintWriter
-   bufferedWriter –允许我们使用BufferedWriter进行写入

让我们更详细地讨论它们。

## 3.直接写

从给定的源直接写入 文件 是我们可以期望使用 Kotlin 扩展方法的最简单策略。

### 3.1. 写文本

可能是最直接的扩展方法，w riteText将内容作为String参数直接写入指定文件。给定的内容是以UTF-8(默认)或任何其他指定 [字符集](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/write-text.html#kotlin.io$writeText(java.io.File, kotlin.String, java.nio.charset.Charset)/charset)编码的文本：

```java
File(fileName).writeText(fileContent)
```

此方法在内部委托 writeBytes ，如下所述。但首先，它使用指定的字符集将给定的内容转换为字节数组。

### 3.2. 写字节

同样，我们可以使用字节作为输入。writeBytes方法 将一个[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)作为参数并直接将其写入指定文件。当我们将内容作为字节数组而不是纯文本时，这很有用。

```java
File(fileName).writeBytes(fileContentAsArray)
```

如果给定的文件存在，它将被覆盖。

## 4.使用 Writer写入文件

Kotlin 还提供扩展方法，为我们提供 Java [Writer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Writer.html) 实例。

### 4.1. 印刷机

如果我们想使用 Java [PrintWriter](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/PrintWriter.html)，Kotlin 提供了一个 [printWriter](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/print-writer.html) 函数，正是为了这个目的。有了它，我们可以将对象的格式化表示打印到OutputStream：

```java
File(fileName).printWriter()
```

此方法返回一个新的PrintWriter实例。接下来，我们可以利用 [use](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/use.html)方法来处理它：

```java
File(fileName).printWriter().use { out -> out.println(fileContent) }
```

通过 使用， 我们可以在终止后关闭的资源上执行一个函数。无论函数执行成功还是抛出异常，资源都会被关闭。

### 4.2. 缓冲写入器

同样，Kotlin 也提供了一个[bufferedWriter](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/buffered-writer.html)函数，它为我们提供了一个 Java [BufferedWriter。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/BufferedWriter.html)

然后，通过它，我们可以更有效地将文本写入字符输出流。

```java
File(fileName).bufferedWriter()
```

与PrintWriter类似，此函数返回一个新的BufferedWriter 实例，稍后我们可以使用它来写入文件的内容。

```java
File(fileName).bufferedWriter().use { out -> out.write(fileContent) }
```

## 5.总结

在本文中，我们看到了使用 Kotlin 扩展方法写入文件的不同方式。
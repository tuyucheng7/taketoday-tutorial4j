## 1. 概述

在本教程中，我们将熟悉几种将 InputStream 的内容到 Kotlin 文件的方法。

[当然，在标准库和第三方库](https://www.baeldung.com/convert-input-stream-to-a-file)方面，Java 和 Kotlin 在这方面存在重叠。然而，我们在这里只介绍标准库中最惯用的方法，因为它们现在已经相当成熟了。

## 2.(通常)错误的方式

[一个简单的解决方案是首先从InputStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/InputStream.html) 中读取所有字节 ，然后将其写入文件：

```kotlin
val file: File = // from somewhere
val bytes = inputStream.readBytes()
file.writeBytes(bytes)
```

我们可能会受到诱惑或咨询使用这种方法。然而，使用这种方法，我们很容易给自己带来大麻烦，尤其是当输入大小未知或很大时。这是因为我们在开始将其写入文件之前将整个内容加载到内存中。基本上，我们表现得好像内存是无限的，但显然不是。

此外，其他方法通常使用内部缓冲区从 InputStream读取。因此，即使对于小型流，这种方法也不是那么优越。

所以我们最好避免这种诱惑，让我们熟悉更好的方法。

## 3. copyTo() 扩展函数

第一种方法是 在 Kotlin的InputStream 上使用[copyTo(output)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-input-stream/copy-to.html) 扩展函数 。此函数从接收 InputStream 到给定 [OutputStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/OutputStream.html)的所有内容：

```kotlin
val content = "Hello World".repeat(1000)
val file: File = createTempFile()
val inputStream = ByteArrayInputStream(content.toByteArray())

inputStream.use { input ->
    file.outputStream().use { output ->
        input.copyTo(output)
    }
}

assertThat(file).hasContent(content)
```

在上面的示例中，首先，我们创建了一个临时文件，然后将 InputStream 中的所有内容到该文件中。最后，我们还会验证内容是否按预期进行了。我们还可以对任何 [File](https://www.baeldung.com/java-io-file) 实例执行相同的操作，例如从路径中获取的实例：

```kotlin
val file = File("path/to/file")
```

请注意，File.outputStream [()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.io.-file/output-stream.html)扩展函数将 File 实例转换为 OutputStream。这是必要的，因为 copyTo() 函数需要一个 OutputStream 实例作为目标。

显然，上面示例中最重要的部分是input.copyTo(output)，它处理的繁重工作。

此外，我们应该负责任地行事，并在用完后关闭底层资源。因此，我们 相当广泛地[使用 use()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/use.html) 函数来自动管理这些资源。 这种技术在 JVM 社区中也称为 [try-with-resources 。](https://www.baeldung.com/kotlin/try-with-resources)

### 3.1. 缓冲

在后台，默认情况下， 每次从 InputStream读取时， copyTo() 函数都会[缓冲 8 KB](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/jvm/src/kotlin/io/IOStreams.kt#L103)。这是提高性能的一个非常常见的技巧，尤其是当它涉及内核时(例如，从打开的文件或套接字中读取)。

非常有趣的是，这个扩展函数可以通过第二个参数自定义缓冲区大小：

```kotlin
input.copyTo(output, 16  1024)
```

如上所示，我们缓冲 16 KB，而不是默认的 8。

## 4. Files.copy() 

除了 Kotlin 扩展函数之外，我们还可以使用 [Files.copy(stream, path)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#copy(java.io.InputStream,java.nio.file.Path,java.nio.file.CopyOption...)) 实用程序从 InputStream 到给定的 [Path](https://www.baeldung.com/java-nio-2-path) 实例：

```kotlin
inputStream.use { input ->
    Files.copy(input, Paths.get("./copied"))
}

assertThat(File("./copied")).hasContent(content)
```

在这里，我们将流内容 到当前目录中名为copied 的文件中。同样，如果我们已经有一个 File 实例，我们可以在其上使用 [toPath()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#toPath()) 方法，以便仍然能够使用 Files.copy() 方法：

```kotlin
val file: File = // from somewhere
Files.copy(input, file.toPath())
```

同样， Files.copy() 方法也使用与之前方法相同的[缓冲技术](https://github.com/openjdk/jdk/blob/38ff85c824750e7da66fd86f5bde1c4587e529c4/src/java.base/share/classes/java/nio/file/Files.java#L3170)。

## 5. Java 9+ 中的transferTo() 

Java 9 引入了一个名为[InputStream.transferTo(output)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/InputStream.html#transferTo(java.io.OutputStream))的新方法，用于将InputStream 的内容传输 到 OutputStream：

```kotlin
val file = createTempFile()
val inputStream = ByteArrayInputStream(content.toByteArray())

inputStream.use { input ->
    file.outputStream().use { output ->
        input.transferTo(output)
    }
}

assertThat(file).hasContent(content)
```

由于此方法需要目标文件的 OutputStream ，因此我们再次使用相同的File.outputStream() 扩展函数。非常有趣的是， Files.copy() 方法在底层[使用了这个方法](https://github.com/openjdk/jdk/blob/38ff85c824750e7da66fd86f5bde1c4587e529c4/src/java.base/share/classes/java/nio/file/Files.java#L3170)。

## 六，总结

在本教程中，我们学习了几种将 InputStream 的内容到 Kotlin 文件的方法。尽管它们在 API 级别上有所不同，但它们的实现细节非常相似，因为它们都使用相同的缓冲方法。
## 1. 概述

在这个简短的教程中，我们将了解如何将InputStream读入String。

[Kotlin](https://www.baeldung.com/kotlin) 提供了一种执行转换的简单方法。但是，在使用资源时仍然需要考虑一些细微差别。另外，我们将涵盖特殊情况，例如读取停止字符。

## 2. 缓冲阅读器

InputStream是围绕有序字节流的抽象。底层数据源可以是文件、网络连接或任何其他发出字节的源。让我们使用一个包含以下数据的简单文件：

```plaintext
Computer programming can be a hassle
It's like trying to take a defended castle
```

我们可能尝试的第一个解决方案是逐行手动[读取文件：](https://www.baeldung.com/kotlin-read-file)

```java
val reader = BufferedReader(inputStream.reader())
val content = StringBuilder()
try {
    var line = reader.readLine()
    while (line != null) {
        content.append(line)
        line = reader.readLine()
    }
} finally {
    reader.close()
}
```

首先，我们使用BufferedReader类来包装InputStream ，然后读取直到流中没有任何行。此外，我们通过try-finally语句包围读取逻辑以最终关闭流。总之，有很多样板代码。

我们可以让它更紧凑和可读吗？

绝对地！首先，我们可以使用readText()函数来简化代码片段。它将输入流完全读取为[String](https://www.baeldung.com/kotlin-string-comparison)。因此，我们可以重构我们的代码片段如下：

```java
val reader = BufferedReader(inputStream.reader())
var content: String
try {
    content = reader.readText()
} finally {
    reader.close()
}
```

但是，我们仍然有那个try-finally块。幸运的是，Kotlin 允许以伪自动方式处理[资源管理。](https://www.baeldung.com/kotlin-try-with-resources)让我们看看接下来的代码行：

```java
val content = inputStream.bufferedReader().use(BufferedReader::readText)
assertEquals(fileFullContent, content)

```

这个单线解决方案看起来很简单，然而，在幕后发生了很多事情。上面代码中很重要的一点是use()函数的调用。此[扩展函数在实现](https://www.baeldung.com/kotlin-extension-methods)Closable接口的资源上执行块。最后，当块被执行时，Kotlin 为我们关闭了资源。

## 3.停止字符

同时，可能会出现需要读取到特定字符的内容的情况。让我们为InputStream类定义一个扩展函数：

```java
fun InputStream.readUpToChar(stopChar: Char): String {
    val stringBuilder = StringBuilder()
    var currentChar = this.read().toChar()
    while (currentChar != stopChar) {
        stringBuilder.append(currentChar)
        currentChar = this.read().toChar()
        if (this.available() <= 0) {
            stringBuilder.append(currentChar)
            break
        }
    }
    return stringBuilder.toString()
}
```

此函数从输入流中读取字节，直到出现停止字符。同时，为了防止死循环，我们调用available()方法检查流是否还有剩余数据。因此，如果流中没有停止符，则会读取整个流。

另一方面，并非InputStream类的所有子类都提供available()方法的实现。因此，我们必须确保在使用扩展函数之前正确实现了该方法。

让我们回到我们的示例并读取文本直到第一个空白字符(' ')：

```java
val content = inputStream.use { it.readUpToChar(' ') }
assertEquals("Computer", content)

```

结果，我们将得到直到停止字符的文本。同样，不要忘记用use()函数包装块以自动关闭流。

## 4。总结

在本文中，我们了解了如何在 Kotlin 中将InputStream转换为String。Kotlin 提供了一种处理数据流的简洁方法，但了解内部发生的事情总是值得的。
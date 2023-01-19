## 一、简介

当我们学习一门新的编程语言时，通常会从控制台 I/O 开始。在本教程中，我们将探索使用 Kotlin 处理控制台 I/O 的一些替代方法。

## 2. 使用 Kotlin 标准库

[Kotlin 标准库](https://kotlinlang.org/api/latest/jvm/stdlib/index.html)为我们提供了基于 JDK 内置支持的 I/O 处理扩展。

要打印到控制台，我们可以使用打印功能。如果我们运行以下代码片段：

```java
print("Hello from Kotlin")
```

我们将在终端上看到以下消息：

```bash
Hello from Kotlin
```

在幕后，此函数使用 Java 的System.out.print方法。此外，该库还为我们提供了println替代函数，它在消息末尾添加了行分隔符。

为了从控制台读取，我们可以使用readLine函数：

```java
val inputText = readLine()
```

有趣的是，这不是Scanner.readLine的同义词，就像print是System.out.print的同义词一样。不过，现在让我们看看Scanner的用武之地。

## 3. 使用 Java 标准库

Kotlin 与 Java 具有很好的互操作性。因此，我们可以在我们的程序中使用来自 JDK 的标准 I/O 类，以备不时之需。

让我们在这里探讨其中的一些。

### 3.1. 使用扫描器类

使用[Scanner](https://www.baeldung.com/java-scanner)类非常简单；我们只需要创建一个实例并使用nextLine方法：

```java
val scanner = Scanner(System.`in`)
val readText = scanner.nextLine()
```

请注意，我们使用反引号转义in属性，因为它是 Kotlin 中的关键字。

### 3.2. 使用BufferedReader类

要使用[BufferedReader](https://www.baeldung.com/java-buffered-reader)类从标准输入流中读取，我们首先需要使用System.in实例化它：

```java
val reader = BufferedReader(InputStreamReader(System.`in`))
```

然后我们可以使用它的方法——例如，readLine()：

```java
val readText = reader.readLine()
```

### 3.3. 使用控制台类

与前两个类不同，[Console](https://www.baeldung.com/java-console-input-output)类具有处理控制台 I/O 的其他方法，如readPassword和printf。

为了使用Console类，我们需要从System类中获取一个实例：

```java
val console = System.console()
```

现在，我们可以使用它的readLine()方法，其中包括：

```java
val readText = console.readLine()
```

## 4。总结

在本教程中，我们介绍了如何使用 Kotlin 处理 I/O 以及如何使用 JDK 中的等效类。有关这些 JDK 类的更多详细信息，请务必查看我们关于[Scanner](https://www.baeldung.com/bufferedreader-vs-console-vs-scanner-in-java)[、](https://www.baeldung.com/bufferedreader-vs-console-vs-scanner-in-java)[BufferedReader](https://www.baeldung.com/bufferedreader-vs-console-vs-scanner-in-java)[和](https://www.baeldung.com/bufferedreader-vs-console-vs-scanner-in-java)[Console](https://www.baeldung.com/bufferedreader-vs-console-vs-scanner-in-java)[的教程](https://www.baeldung.com/bufferedreader-vs-console-vs-scanner-in-java)。

此外，由于 Kotlin 与 Java 的互操作性，我们可以使用额外的 Java 库来处理 I/O。
## 1. 概述

在本快速教程中，我们将了解在 Kotlin 中读取文件的各种方式。

我们将涵盖将整个文件作为字符串读取以及将其读入单个行列表的两种用例。还可以从完整的绝对路径或项目资源中获取它。

## 2. 读取文件

让我们首先创建一个将由 Kotlin 读取的输入文件。我们创建一个名为Kotlin 的文件。 并将其放在我们的代码可以访问的目录中。

该文件的内容可能是：

```plaintext
Hello to Kotlin. It's:
1. Concise
2. Safe
3. Interoperable
4. Tool-friendly
```

现在让我们看看读取这个文件的不同方式。我们应该传递上面创建的文件的完整路径作为初始方法的输入，以及我们资源中最后两个方法的相对路径。

### 2.1. 每行

使用指定的[字符集(默认为 UTF-8)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/Charset.html)逐行读取文件并为每一行调用操作：

```java
fun readFileLineByLineUsingForEachLine(fileName: String) 
  = File(fileName).forEachLine { println(it) }
```

### 2.2. 使用线条

调用给定的块回调，为其提供文件中所有行的序列。

处理完成后，文件将关闭：

```java
fun readFileAsLinesUsingUseLines(fileName: String): List<String>
  = File(fileName).useLines { it.toList() }
```

### 2.3. 缓冲阅读器

返回一个新的[BufferedReader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/BufferedReader.html) 来读取文件的内容。

一旦我们有了BufferedReader，我们就可以读取其中的所有行：

```java
fun readFileAsLinesUsingBufferedReader(fileName: String): List<String>
  = File(fileName).bufferedReader().readLines()
```

### 2.4. 读行

直接读取文件内容作为行列表：

```java
fun readFileAsLinesUsingReadLines(fileName: String): List<String> 
  = File(fileName).readLines()
```

不建议将此方法用于大文件。

### 2.5. 输入流

为文件构造一个新的[FileInputStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/FileInputStream.html)并将其作为结果返回。

一旦我们有了输入流，我们就可以将它转换成字节，然后转换成一个完整的String：

```java
fun readFileAsTextUsingInputStream(fileName: String)
  = File(fileName).inputStream().readBytes().toString(Charsets.UTF_8)
```

### 2.6. 阅读文本

将文件的全部内容读取为指定[字符集的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/Charset.html)字符串(默认为 UTF-8)：

```java
fun readFileDirectlyAsText(fileName: String): String 
  = File(fileName).readText(Charsets.UTF_8)
```

不建议将此方法用于大文件，并且文件大小有 2 GB 的内部限制。

### 2.7. 获取资源

查找具有给定名称的资源并返回URL对象：

```java
fun readFileUsingGetResource(fileName: String) 
  = this::class.java.getResource(fileName).readText(Charsets.UTF_8)
```

如果它找到资源，它将返回一个URL，可以通过调用readText方法来处理，如前所示。如果找不到资源，则返回null。使用 getResource时，传入的fileName不是绝对文件名，而是相对于我们项目资源的名称。

### 2.8. 获取资源流

查找具有给定名称的资源并返回InputStream实例：

```java
fun readFileAsLinesUsingGetResourceAsStream(fileName: String) 
  = this::class.java.getResourceAsStream(fileName).bufferedReader().readLines()
```

如果它找到资源，它将返回一个InputStream，它可以如前所示进行处理——例如，通过获取一个BufferedReader。如果找不到资源，则返回null。使用getResourceAsStream时，传入的fileName不是绝对文件名，而是相对于我们项目资源的名称。

## 3.总结

在本文中，我们看到了在 Kotlin 中读取文件的各种方式。根据用例，我们可以选择逐行读取文件或将其完全作为文本读取。我们可以通过绝对方式引用该文件，也可以在资源中查找。
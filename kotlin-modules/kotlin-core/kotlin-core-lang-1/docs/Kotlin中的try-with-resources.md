## 1. 简介

托管语言(例如那些针对JVM的语言)会自动处理最常见的资源：内存。

然而，我们需要处理各种资源，而不仅仅是内存：文件、网络连接、流、窗口等。而且，**就像内存一样，当不再需要时需要释放这些资源**。

在本文中，我们将了解如何在Kotlin中自动管理资源，以及它与[Java的try-with-resources构造](https://www.baeldung.com/java-try-with-resources)有何不同。

## 2. 自动资源管理

在Java中使用资源(伪代码)时，我们可以区分三个不同的阶段：

```java
resource = acquireResource()
try {
    useResource(resource)
} finally {
    releaseResource(resource)
}
```

如果语言或库负责释放资源(finally部分)，那么我们称之为**自动资源管理**，这样的功能**使我们不必记住释放资源**。

此外，由于资源管理通常与块作用域相关联，如果我们同时处理多个资源，它们将始终以正确的顺序释放。

在Java中，持有资源并符合自动资源管理条件的对象实现了一个特定的接口：[Closeable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Closeable.html)用于I/O相关资源和[AutoCloseable](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/AutoCloseable.html)。

此外，Java 7改进了预先存在的Closeable接口以扩展AutoCloseable。

因此，Kotlin具有相同的资源持有者概念：即实现Closeable或AutoCloseable的对象。

## 3. Kotlin中的use函数

为了自动管理资源，一些语言有一个专门的构造：例如，Java 7引入了[try-with-resources](https://www.baeldung.com/java-try-with-resources)，而C#有[using关键字](https://docs.microsoft.com/en-us/dotnet/csharp/language-reference/keywords/using-statement)。

有时，它们为我们提供了一种模式，例如[C++中的RAII](https://en.wikipedia.org/wiki/Resource_acquisition_is_initialization)。在其他一些情况下，它们为我们提供了一种库方法。

Kotlin属于后一类。根据设计，**它没有类似于Java中的try-with-resources的语言结构**。

**相反，我们可以在其标准库中找到一个名为use的扩展方法**。

我们稍后会详细介绍它，现在，我们只需要知道每个资源持有者对象都有我们可以调用的use方法。

### 3.1 如何使用它

一个简单的例子：

```kotlin
val writer = FileWriter("test.txt")
writer.use {
    writer.write("something")
}
```

我们可以在任何实现AutoCloseable或Closeable的对象上调用use函数，就像Java中的try-with-resources一样。

该方法接收一个lambda表达式，执行它，并在执行离开块时处理释放资源(通过对其调用close())，无论是正常还是异常。

所以，在这种情况下，在使用之后，writer不再可用，因为Kotlin已经自动关闭了它。

### 3.2 较短的形式

在上面的示例中，为了清楚起见，我们使用了一个名为writer的变量，从而创建了一个闭包。

但是，use接收带有单个参数的lambda表达式-持有资源的对象：

```kotlin
FileWriter("test.txt")
    .use { w -> w.write("something") }
```

在块内部，我们还可以使用隐式变量it：

```kotlin
FileWriter("test.txt")
    .use { it.write("something") }
```

因此，正如我们所看到的，我们不必为对象指定一个明确的名称。但是，通常最好保持清晰，而不是编写过于简洁的代码。

### 3.3 use()的定义

让我们看一下Kotlin中use函数的定义，可以在其标准库中找到：

```kotlin
public inline fun <T : Closeable?, R> T.use(block: (T) -> R): R
```

我们可以看到，**在<T : Closeable?, R>部分，use被定义为Java的Closeable接口上的扩展函数**。

可以在我们的[介绍性文章](https://www.baeldung.com/kotlin)中找到有关扩展方法的更多信息。

当然，[use函数](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/use.html)被记录为Kotlin标准库的一部分。

### 3.4 Closeable与AutoCloseable

如果我们仔细观察上一节中的示例，我们可以看到use函数签名仅在Closeable接口上定义，**这是因为Kotlin的标准库以Java 6为目标**。

在Java 7之前的版本中，AutoCloseable是不存在的，当然，Closeable也没有对其进行扩展。

实际上，实现AutoCloseable但不实现Closeable的类很少见。不过，我们可能会遇到其中之一。

在这种情况下，我们只需要添加对Java 7、8或我们所针对的任何版本的Kotlin扩展的依赖：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jdk8</artifactId>
</dependency>
```

可以在[Maven Central](https://search.maven.org/search?q=a:kotlin-stdlib-jdk8)上找到最新版本的依赖项。

这为我们提供了在AutoCloseable接口上定义的另一个user扩展函数：

```kotlin
public inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R
```

## 4. 总结

在本教程中，我们了解了Kotlin标准库中的一个简单扩展函数是如何自动管理JVM已知的各种资源所需要的。
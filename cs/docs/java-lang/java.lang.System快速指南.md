## 1. 概述

在本教程中，我们将快速了解java.lang.System类及其特性和核心功能。

## 2. 输入输出

System是java.lang的一部分，它的主要功能之一是让我们可以访问标准I/O流。

简单地说，它暴露了三个字段，每个流一个：

- out
- err
- in

### 2.1 System.out

System.out指向标准输出流，将其公开为PrintStream，我们可以使用它来将文本打印到控制台：

```java
System.out.print("some inline message");
```

System的高级用法是调用System.setOut，我们可以使用它来自定义System.out将写入的位置：

```java
// Redirect to a text file
System.setOut(new PrintStream("filename.txt"));
```

### 2.2 System.err

System.err很像System.out。这两个字段都是PrintStream的实例，都用于将消息打印到控制台。

但是System.err代表标准错误，我们专门用它来输出错误消息：

```java
System.err.print("some inline error message");
```

控制台通常会以不同于输出流的方式呈现错误流。

有关详细信息，请查看[PrintStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/PrintStream.html)文档。

### 2.3 System.in

System.in指向标准in，将其公开为InputStream，我们可以使用它从控制台读取输入。

虽然涉及更多，但我们仍然可以管理：

```java
public String readUsername(int length) throws IOException {
    byte[] name = new byte[length];
    System.in.read(name, 0, length); // by default, from the console
    return new String(name);
}
```

通过调用System.in.read，应用程序停止并等待来自标准输入的输入。无论下一个长度字节是什么，都将从流中读取并存储在字节数组中。

用户键入的任何其他内容都保留在流中，等待另一个读取调用。

当然，在这么低的级别上操作可能具有挑战性且容易出错，因此我们可以使用BufferedReader稍微清理一下：

```java
public String readUsername() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    return reader.readLine();
}
```

通过上述安排，readLine将从System.in读取，直到用户点击返回，这更接近我们的预期。

请注意，在这种情况下，我们有意不关闭流。关闭标准in意味着在程序的生命周期内无法再次读取！

最后，System.in的高级用法是调用System.setIn将其重定向到不同的InputStream。

## 3. 实用方法

System为我们提供了许多方法来帮助我们完成以下事情：

- 访问控制台
- 复制数组
- 观察日期和时间
- 退出JRE
- 访问运行时属性
- 访问环境变量，以及
- 管理垃圾回收

### 3.1 访问控制台

Java 1.6引入了另一种与控制台交互的方式，而不是直接使用System.out和in。

我们可以通过调用System.console来访问它：

```java
public String readUsername() {
    Console console = System.console();	 	 
	 	 
    return console == null ? null : console.readLine("%s", "Enter your name: ");	 	 
}
```

请注意，根据底层操作系统以及我们启动Java以运行当前程序的方式，控制台可能会返回null，因此请始终确保在使用.

查看[Console](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Console.html)文档了解更多用途。

### 3.2 复制数组

System.arraycopy是一种将一个数组复制到另一个数组的旧C风格方法。

大多数情况下，arraycopy旨在将一个完整的数组复制到另一个数组中：

```java
int[] a = {34, 22, 44, 2, 55, 3};
int[] b = new int[a.length];

System.arraycopy(a, 0, b, 0, a.length);
assertArrayEquals(a, b);
```

但是，我们可以指定两个数组的起始位置，以及要复制的元素数量。

例如，假设我们要从a复制2个元素，从a[1\]开始到b，从b[3\]开始：

```java
System.arraycopy(a, 1, b, 3, 2); 
assertArrayEquals(new int[] {0, 0, 0, 22, 44, 0}, b);
```

另外，请记住arraycopy会抛出：

- NullPointerException如果任一数组为null
- IndexOutOfBoundsException如果副本引用超出其范围的任一数组
- ArrayStoreException如果复制导致类型不匹配

### 3.3 观察日期和时间

System中有两种与时间相关的方法。一个是currentTimeMillis，另一个是nanoTime。

currentTimeMillis返回自Unix纪元以来经过的毫秒数，即January 1, 1970 12:00 AM UTC：

```java
public long nowPlusOneHour() {
    return System.currentTimeMillis() + 3600  1000L;
}

public String nowPrettyPrinted() {
    return new Date(System.currentTimeMillis()).toString();
}
```

nanoTime返回相对于JVM启动的时间。我们可以多次调用它来标记应用程序中时间的流逝：

```java
long startTime = System.nanoTime();
// ...
long endTime = System.nanoTime();

assertTrue(endTime - startTime < 10000);

```

请注意，由于nanoTime的粒度非常细，因此执行endTime – startTime < 10000比执行endTime < startTime更安全，因为[可能会出现数值溢出](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#nanoTime())。

### 3.4 退出程序

如果我们想以编程方式退出当前执行的程序，System.exit就可以了。

要调用exit，我们需要指定一个退出代码，该代码将被发送到启动程序的控制台或shell。

按照Unix的约定，状态为0表示正常退出，而非零表示发生错误：

```java
if (error) {
    System.exit(1);
} else {
    System.exit(0);
}
```

请注意，对于现在的大多数程序来说，需要调用它会很奇怪。例如，当在Web服务器应用程序中调用时，它可能会关闭整个站点！

### 3.5 访问运行时属性

系统使用getProperty提供对运行时属性的访问。

我们可以使用setProperty和clearProperty来管理它们：

```java
public String getJavaVMVendor() {
    System.getProperty("java.vm.vendor");
}
    
System.setProperty("abckey", "abcvaluefoo");
assertEquals("abcvaluefoo", System.getProperty("abckey"));

System.clearProperty("abckey");
assertNull(System.getProperty("abckey"));
```

通过-D指定的属性可通过getProperty访问。

我们还可以提供默认值：

```java
System.clearProperty("dbHost");
String myKey = System.getProperty("dbHost", "db.host.com");
assertEquals("db.host.com", myKey);
```

System.getProperties提供了所有系统属性的集合：

```java
Properties properties = System.getProperties();
```

我们可以从中进行任何属性操作：

```java
public void clearAllProperties() {
    System.getProperties().clear();
}
```

### 3.6 访问环境变量

系统还使用getenv提供对环境变量的只读访问。

例如，如果我们想访问PATH环境变量，我们可以这样做：

```java
public String getPath() {
    return System.getenv("PATH");
}
```

### 3.7 管理垃圾收集

通常，垃圾收集工作对我们的程序来说是不透明的。不过，有时我们可能想直接向JVM提出建议。

System.runFinalization是一种允许我们建议JVM运行其终结例程的方法。

System.gc是一种允许我们建议JVM运行其垃圾收集例程的方法。

由于这两种方法的契约不保证终结或垃圾收集将运行，因此它们的用处很小。

但是，它们可以作为优化来执行，比如在桌面应用程序最小化时调用gc：

```java
public void windowStateChanged(WindowEvent event) {
    if ( event == WindowEvent.WINDOW_DEACTIVATED ) {
        System.gc(); // if it ends up running, great!
    }
}
```

有关定稿的更多信息，请查看我们的[finalize指南](https://www.baeldung.com/java-finalize)。

## 4. 总结

在本文中，我们了解了System提供的一些字段和方法。完整列表可以在[官方System文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html)中找到。
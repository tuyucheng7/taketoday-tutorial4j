## 1. 概述

在这个简短的教程中，我们将了解如何使用对象表达式在Kotlin中创建匿名内部类。

首先，我们将熟悉用于匿名内部类的Kotlin API，然后我们将更深入地了解这一切在字节码级别是如何表示的。

## 2. 匿名内部类

在Java中，可以使用“new ClassName() {...}”语法创建[匿名内部类](https://www.baeldung.com/java-anonymous-classes)。例如，我们在这里为[NIO](https://www.baeldung.com/java-io-vs-nio)的Channel接口创建一个匿名内部类：

```java
Channel channel = new Channel() {
            
    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() throws IOException {
        // omitted
    }
};
```

**要在Kotlin中创建匿名内部类，我们必须使用对象表达式**。例如，以下是我们如何在Kotlin中实现相同的目的：

```kotlin
val channel = object : Channel {
    override fun isOpen() = false

    override fun close() {
        // omitted
    }
}
```

我们没有使用new关键字，而是使用“object :”来表示对象表达式语法。**如果超类型有一个构造函数，我们应该将必要的参数传递给该构造函数**：

```kotlin
val maxEntries = 10000
val lruCache = object : LinkedHashMap<String, Int>(50, 0.75f) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Int>?): Boolean {
        return size > maxEntries
    }
}
```

在这里，我们使用具有自定义初始容量和负载因子的[LinkedHashMap](https://www.baeldung.com/java-linked-hashmap)创建一个匿名LRU缓存，**当我们不想将任何参数传递给构造函数时，我们仍然必须放置空括号**：

```kotlin
val map = object : LinkedHashMap<String, Int>() {
    // omitted
}
```

此外，我们可以从一个类加多个接口或仅扩展多个接口：

```kotlin
val serializableChannel = object : Channel, Serializable {
    // omitted
}
```

非常有趣的是，我们甚至**可以在不扩展另一个类或接口的情况下创建匿名内部类**：

```kotlin
val obj = object {
    val question = "answer"
    val answer = 42
}
println("The ${obj.question} is ${obj.answer}")
```

现在我们知道了如何创建匿名内部类，让我们看看字节码是什么样子的。

## 3. 字节码级别

要[查看生成的字节码](https://www.baeldung.com/java-class-view-bytecode)，我们应该首先使用kotlinc编译Kotlin代码，如果Kotlin文件名为Anonymous.kt，则以下命令将Kotlin代码编译成类文件：

```bash
$ kotlinc Anonymous.kt
```

**然后我们可以通过“javap -p -c -v”查看字节码**。例如，以下是channel变量在字节码级别的表示方式：

```bash
$ javap -c -p -v AnonymousKt
0: new           #11            // class AnonymousKt$main$channel$1
3: dup
4: invokespecial #14           // Method AnonymousKt$main$channel$1."<init>":()V
// omitted
InnerClasses:
  public static final #11;    // class AnonymousKt$main$channel$1
```

如上所示，**Kotlin编译器首先为“new Channel { ... }”表达式生成一个静态内部类**。然后，它调用构造函数([<init\>](https://www.baeldung.com/jvm-init-clinit-methods)方法)从中创建一个实例。

当我们将一些参数传递给构造函数时，字节码如下所示：

```bash
16: bipush        10   // capacity
18: ldc           #17  // float 0.75f (load factor)
20: invokespecial #20  // Method AnonymousKt$main$lruCache$1."<init>":(IIF)V
```

生成的静态内部类的构造函数将两个整数和一个浮点数作为输入-IIF部分，其中一个整数实际上帮助我们捕获maxEntries变量的值，另外两个是构造函数参数：

```kotlin
val maxEntries = 10
val lruCache = object : LinkedHashMap<String, Int>(10, 0.75f) {
    // omitted
}
```

这样，匿名内部类就可以访问封闭变量，这就是[闭包](https://kotlinlang.org/docs/reference/lambdas.html#closures)对匿名内部类的作用。

关键要点是，**Kotlin编译器将匿名内部类转换为字节码中的静态内部类**。

## 4. 总结

在这个快速教程中，我们了解了如何使用对象表达式来声明匿名内部类，并熟悉了对象表达式的内部表示。
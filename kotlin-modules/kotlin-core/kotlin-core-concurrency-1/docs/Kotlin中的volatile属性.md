## 1. 概述

在本快速教程中，我们将了解如何在Kotlin中使用volatile属性控制变量的内存可见性。

首先，我们将介绍Kotlin中的volatile属性。然后我们将深入挖掘并在字节码级别查看这些属性的内部表示。

## 2. volatile属性

来自Java背景的人可能想知道，在Kotlin中，[Java的volatile](https://www.baeldung.com/java-volatile)关键字等同于什么？

为了使事情更具体，让我们看一下这个例子：

```kotlin
object TaskExecutor : Runnable {

    private var shouldContinue = true

    override fun run() {
        while (shouldContinue) {}
        println("Done")
    }

    fun stop() {
        shouldContinue = false
    }
}
```

只要shouldContinue变量设置为true，TaskExecutor单例对象就会继续自旋等待：

```kotlin
fun main() {
    val thread = Thread(TaskExecutor)
    thread.start()

    TaskExecutor.stop()

    thread.join()
}
```

人们可能期望一旦我们调用stop()方法，忙等待将立即结束并且TaskExecutor将在控制台打印“Done”。

**但是，由于**[共享多处理器体系结构](https://www.baeldung.com/java-volatile#shared-multiprocessor-architecture)**和CPU缓存的性质，忙等待可能会延迟结束。雪上加霜，说不定根本就没完没了**。

为了避免对shouldContinue变量进行此类处理器或运行时优化，我们应该使用[@Volatile](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-volatile/)注解对该属性进行标注：

```kotlin
object TaskExecutor : Runnable {

    @Volatile
    private var shouldContinue = true

    // same as before
}
```

@Volatile注解会将注解属性的JVM支持字段标记为volatile，**因此，对该字段的写入立即对其他线程可见**。此外，读取将始终看到最新的更改。

简单的说，@Volatile注解相当于Java在Kotlin中的volatile关键字。

## 3. JVM表示

现在我们已经对易失性属性的Kotlin API有了足够的了解，让我们检查一下字节码。

如果我们 用kotlinc编译TaskExecutor单例对象：

```bash
$ kotlinc TaskExecutor.kt
```

然后使用javap检查生成的字节码：

```bash
$ javap -v -p -c TaskExecutor
// omitted for brevity
private staticvolatileboolean shouldContinue;
    descriptor: Z
    flags: (0x004a) ACC_PRIVATE, ACC_STATIC, ACC_VOLATILE
```

**然后，我们将看到shouldContinue字段被编译为带有ACC_VOLATILE标志的静态值，这意味着它是一个volatile属性**。

## 4. 总结

在这个简短的教程中，我们了解了如何使用volatile属性控制变量的内存可见性。此外，我们快速浏览了为@Volatile注解生成的字节码。

要更详细地讨论volatile属性如何影响变量的内存可见性，强烈建议查看我们的[Java中的Volatile关键字指南](https://www.baeldung.com/java-volatile)。
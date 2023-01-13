## 1. 概述

在本快速教程中，我们将了解什么是带有接收器的 lambda，以及它们如何带来更多的简单性和可读性。

## 2. 带有接收器的 Lambda

让我们从Kotlin 中接受 lambda 作为输入的普通[扩展函数开始：](https://www.baeldung.com/kotlin-extension-methods)

```kotlin
fun <T> T.applyThenReturn(f: (T) -> Unit): T {
    f(this)
    return this
}

```

使用这个扩展函数，我们可以将一个函数应用于一个值并返回相同的值：

```kotlin
val name = "Baeldung".applyThenReturn { n -> println(n.toUpperCase()) }
```

如上所示，要对值进行操作，我们必须使用 lambda 参数。Kotlin 中的普通 Lambda 函数就像这样：一组显式参数和由->分隔的 lambda 函数体。

这很好，但是如果我们可以做类似的事情呢：

```kotlin
val name = "Baeldung".applyThenReturn { println(toUpperCase()) }
```

我们不是对 lambda 参数( 上例中的n )进行操作，而是在this 引用上调用toUpperCase() 方法 。基本上，我们假装每个非限定函数调用都使用 “Baeldung” 字符串作为调用的接收者。这使得 lambda 主体更加简洁。

事实证明，这种形式的 lambda 表达式实际上可以在 Kotlin 中通过 lambda with receivers 实现。为了使用这个特性，我们应该稍微不同地定义 lambda 表达式：

```kotlin
fun <T> T.apply(f: T.() -> Unit): T {
    f() // or this.f()
    return this
}
```

如上所示，我们将类型参数移到了括号之外。此外， 我们只调用等于 this.f()的f()函数，而不是f(this ) 。同样，每个非限定函数调用都使用 T 的一个实例作为调用接收者。

标准库和第三方库都广泛使用带有接收器的 lambda 来提供更好的开发人员体验。Kotlin 中的几个内置作用域函数正在使用此功能，包括[apply()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/apply.html) 作用域函数：

```kotlin
inline fun <T> T.apply(block: T.() -> Unit): T {
    // omitted
    block()
    return this
}
```

## 3.字节码表示

要了解这两个函数在字节码级别有何不同，让我们分别编译它们。为此，我们可以使用 kotlinc 实用程序。编译完成后，我们可以使用 javap 工具查看生成的字节码：

```bash
>> kotlinc Receiver.kt
>> javap -c -p com.baeldung.receiver.ReceiverKt
public static final <T> T applyThenReturn(T, kotlin.jvm.functions.Function1<? super T, kotlin.Unit>);
    Code:
       // omitted
       6: aload_1
       7: aload_0
       8: invokeinterface #49, 2 // InterfaceMethod Function1.invoke:(Ljava/lang/Object;)Ljava/lang/Object;

```

因此，正如我们所见，Kotlin 编译器将applyThenReturn 函数转换为接受两个参数的静态方法：一个用于传递扩展函数接收器，另一个用于封装 lambda 主体。在这里， Function1<? super T, kotlin.Unit> 是一个具有一个输入的函数，不返回任何内容或返回 Unit。

此外，要调用 lambda，它只需将第一个参数 ( aload_0 ) 传递给 invoke 方法。令人惊讶的是，带有接收器的 lambda 的字节码与上面的完全相同。

同样，在调用站点之一，Kotlin 将两个函数调用都转换为简单的静态方法调用。

底线是，即使正常的 lambda 和带有接收器的 lambda 在编译时不同，但它们在字节码级别完全相同。 

## 4。总结

在本教程中，首先，我们看到了如何利用带有接收器的 lambda 来构建更好、更易读的程序结构。除了熟悉 API 之外，我们还了解了这种类型的 lambda 在字节码级别是如何表示的。
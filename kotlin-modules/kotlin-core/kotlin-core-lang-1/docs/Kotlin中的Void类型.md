## 1. 简介

在本教程中，我们将了解Kotlin中的Void类型，以及在Kotlin中表示void或nothing的其他基本方法。

## 2. Void与Java中的void

为了理解Void在Kotlin中的使用，让我们首先回顾一下Java中的Void类型是什么，**以及它与Java原始关键字void有何不同**。

**Void类作为java.lang包的一部分，它充当对包装Java基本类型void的对象的引用**，它可以被认为类似于其他包装器类，例如Integer是基本类型int的包装器。

现在，Void不属于其他流行的包装类，因为我们需要返回它而不是原始void的用例并不多。但是，在诸如泛型之类的应用程序中，我们不能使用原始类型，而是使用Void类。

## 3. Kotlin中的Void

**Kotlin被设计为与Java完全可互操作，因此Java代码可以在Kotlin文件中使用**。

让我们尝试使用Java的Void类型作为Kotlin函数中的返回类型：

```kotlin
fun returnTypeAsVoidAttempt1(): Void {
    println("Trying with Void return type")
}
```

但是此函数无法编译并导致以下错误：

```bash
Error: Kotlin: A 'return' expression required in a function with a block body ('{...}')
```

这个错误其实是有道理的，毕竟类似的函数在Java中也会给出类似的错误。

为了解决这个问题，我们将尝试添加一个return语句。但是，**由于Void是Java的一个不可实例化的最终类，我们只能从这样的函数中返回null**：

```kotlin
fun returnTypeAsVoidAttempt2(): Void {
    println("Trying with Void as return type")
    return null
}
```

此解决方案也不起作用，并因以下错误而失败：

```bash
Error: Kotlin: Null can not be a value of a non-null type Void
```

出现上述消息的原因是，与Java不同，我们无法在Kotlin中从非null返回类型返回null。

**在Kotlin中，我们需要使用?运算符**：

```kotlin
fun returnTypeAsVoidSuccess(): Void? {
    println("Function can have Void as return type")
    return null
}
```

现在我们终于有了一个可行的解决方案，但正如我们接下来将看到的，有更好的方法可以达到相同的结果。

## 4. Kotlin中的Unit

**Kotlin中的Unit可以用作不返回任何有意义的函数的返回类型**：

```kotlin
fun unitReturnTypeForNonMeaningfulReturns(): Unit {
    println("No meaningful return")
}
```

默认情况下，Java void映射到Kotlin中的Unit类型，**这意味着从Kotlin调用时在Java中返回void的任何方法都将返回Unit**-例如System.out.println()函数。

```kotlin
@Test
fun givenJavaVoidFunction_thenMappedToKotlinUnit() {
    assertTrue(System.out.println() is Unit)
}
```

此外，**Unit是默认返回类型并且声明它是可选的**，因此，以下函数也有效：

```kotlin
fun unitReturnTypeIsImplicit() {
    println("Unit Return type is implicit")
}
```

## 5. Kotlin中的Nothing

Nothing是Kotlin中的一种特殊类型，用于表示一个从不存在的值。**如果函数的返回类型是Nothing，那么该函数不会返回任何值，甚至默认返回类型Unit也不返回**。

例如，下面的函数总是抛出异常：

```kotlin
fun alwaysThrowException(): Nothing {
    throw IllegalArgumentException()
}
```

**正如我们所理解的，Nothing返回类型的概念是完全不同的，并且在Java中没有对应的概念**。在后者中，一个函数将始终默认为void返回类型，即使可能存在像上面的示例这样的情况，该函数可能永远不会返回任何东西。

**Kotlin中的Nothing返回类型使我们免于潜在的错误和不必要的代码**，当调用任何返回类型为Nothing的函数时，编译器将不会执行超出此函数调用的范围，并给我们适当的警告：

```kotlin
fun invokeANothingOnlyFunction() {
    alwaysThrowException() // Function that never returns
    var name = "Tom" // Compiler warns that this is unreachable code
}
```

## 6. 总结

在本教程中，我们了解了Java中的void与Void以及如何在Kotlin中使用它们，我们还了解了Unit和Nothing类型以及它们作为不同场景的返回类型的适用性。
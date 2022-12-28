## 1. 概述

在本教程中，我们将讨论使用assertFailsWith方法在Kotlin中测试异常。

首先我们介绍如何使用JUnit 5库来测试异常，然后继续讨论assertFailsWith方法。

## 2. 使用JUnit 5

异常会破坏正常的程序执行流程，并且是任何JVM语言的重要组成部分，处理执行是优雅地处理此类问题的技术。

**在Kotlin中，我们可以使用不同的库来测试是否抛出异常，其中最受欢迎的是JUnit库**。

JUnit 5 Jupiter API提供了几种断言方法来确定测试用例的通过或失败状态。更具体地说，JUnit 5提供了一些更适合Kotlin语言的断言方法。

**JUnit 5中我们可以用来在Kotlin中断言异常的一个方法是assertThrows()**，此外，此函数允许我们在断言调用之后添加一段代码，以使其更具可读性：

```kotlin
fun whenInvalidArray_thenThrowsException() {
    assertThrows<ArrayIndexOutOfBoundsException> {
        val array = intArrayOf(1, 2, 3)
        array[5]
    }
}
```

在此示例中，当从代码块中抛出预期的ArrayIndexOutOfBoundsException时，断言成功。此外，当代码块抛出ArrayIndexOutOfBoundsException派生类型的异常时，断言也有效。

## 3. 使用Kotlin的assertFailsWith方法

Kotlin标准库也提供了测试异常的函数，**我们可以使用assertFailsWith方法来断言代码块因异常类型而失败**。

**此外，我们还可以定义一条消息作为函数的参数**，该消息是可选的，仅当断言失败时才用作失败消息的前缀：

```kotlin
fun givenInvalidArray_thenThrowsException() {
    assertFailsWith<ArrayIndexOutOfBoundsException>(
        message = "No exception found",
        block = {
            val array = intArrayOf(1, 2, 3)
            array[5]
        }
    )
}
```

在上面的示例中，当block代码块抛出ArrayIndexOutOfBoundsException时断言通过。如果断言失败，则显示消息。

**或者，我们可以使用assertFailsWith方法来断言代码块因特定的异常类而失败**，此异常类与代码块一起定义为函数的参数：

```kotlin
fun givenInvalidFormat_thenThrowsException() {
    assertFailsWith(
        exceptionClass = NumberFormatException::class,
        block = { "Kotlin Tutorials in Tuyucheng".toInt() }
    )
}
```

在此示例中，断言期望从代码块中抛出NumberFormatException。

此外，**我们还可以使用assertFailsWith方法将特定异常类和消息作为参数与代码块一起包括在内**：

```kotlin
fun givenInvalidOperation_thenThrowsException() {
    assertFailsWith(
        exceptionClass = ArithmeticException::class,
        message = "No exception found",
        block = { 50 * 12 / 0 }
    )
}
```

在上面的这种情况下，assertFailsWith方法断言代码块抛出定义的ArithmeticException，指定的消息用于断言失败的场景。

### 3.1 断言异常属性

assertFailsWith方法总是返回异常类型，**可以进一步检查返回值以断言异常属性**：

```kotlin
fun givenInvalidNumericFormat_thenThrowsException() {
    val exception = assertFailsWith<NumberFormatException>(
        block = { Integer.parseInt("abcdefgh") }
    )
    assertThat(exception.message, equalTo("For input string: \"abcdefgh\""))
}
```

在此示例中，如果断言成功，assertFailsWith方法将返回异常，这进一步使我们能够断言异常的message属性。

**assertFailsWith方法总是捕获相同类型或其子类型的异常**，但是我们应该始终尝试精确的类型，以便测试有用，而不仅仅是指定Exception或RuntimeException。

**由于assertFailsWith方法包含在Kotlin标准库中，因此它减少了在我们的测试代码中包含任何额外库的开销**。因此，当我们想要编写简单的单元测试来断言异常时，它很有用。但是，如果我们想编写涉及多个断言的单元测试，JUnit断言方法将更合适。

## 4. 总结

在本教程中，我们讨论了使用assertFailsWith方法在Kotlin中测试异常。
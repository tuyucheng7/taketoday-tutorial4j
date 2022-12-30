## 1. 概述

在本教程中，我们将看到针对经典编程挑战的简单解决方案：确定一个数字是偶数(可被2整除)还是奇数(不可被2整除)。我们将了解rem()运算符在Kotlin中的工作原理，并查看如何使用它来检查数字能否被2整除的示例。

## 2. rem运算符

与许多编程语言一样，Kotlin提供余数/模运算符，由符号“%”或rem()函数表示。rem()运算给出两个数相除的余数，例如，5 % 2返回1，因为2在5中拟合两次，而1从操作中保留下来，**这是模除法**。

由于运算符重载，Kotlin将x % y转换为x.rem(y)。要更好地理解运算符重载的工作原理，请阅读我们的文章[Kotlin中的运算符重载](https://www.baeldung.com/kotlin/operator-overloading)。Kotlin过去称此运算符为“mod”，是modulo的缩写，这在1.1版中更改为rem()(请参阅[补丁说明](https://kotlinlang.org/docs/whatsnew11.html#rem-operator))。

### 2.1 模运算

让我们看一下我们对模运算的期望的一些示例：

```kotlin
@Test
fun `Given the remainder function, assertions on the remainder should pass`() {
    assertTrue(10 % 4 == 2)
    assertTrue(25 % 5 == 0)
    assertTrue(24 % 5 == 4)
    assertTrue(100 % 20 == 0)
}
```

### 2.2 检查偶数或奇数

让我们准备几个变量作为我们的测试对象：

```kotlin
val a = 42
val b = 25
```

为了检查一个数字是偶数还是奇数，我们取除以2的余数并验证：

-   余数是0吗？那么这个数字一定是偶数。
-   余数是1吗？那么这个数字一定是奇数。

让我们将这个概念应用到我们的变量中：

```kotlin
@Test
fun `Given odd or even numbers should get the right response from rem`() {
    val a = 42
    val b = 25

    val aRem2 = a % 2
    val bRem2 = b % 2
    val isAEven: Boolean = aRem2 == 0
    val isBEven: Boolean = bRem2 == 0

    assertTrue(isAEven)
    assertFalse(isBEven)
}
```

扩展我们刚刚学到的内容，让我们继续使用两个函数isOdd()和isEven()：

```kotlin
fun isEven(value: Int) = value % 2 == 0
fun isOdd(value: Int) = value % 2 == 1
```

让我们编写一些单元测试来验证我们的功能：

```kotlin
@Test
fun `Given odd and even values the isEven function should answer correctly`() {
    assertTrue(isEven(2))
    assertTrue(isEven(4))
    assertTrue(isEven(6))

    assertFalse(isEven(1))
    assertFalse(isEven(3))
}

@Test
fun `Given odd and even values the isOdd function should answer correctly`() {
    assertTrue(isOdd(1))
    assertTrue(isOdd(3))
    assertTrue(isOdd(5))

    assertFalse(isOdd(2))
    assertFalse(isOdd(4))
}
```

## 3. 总结

我们了解了rem()函数和%运算符以及它们在Kotlin中的使用方式，我们将运算符应用于几个变量，以了解如何在我们的代码中使用它，并将这个概念推广到工具函数中。
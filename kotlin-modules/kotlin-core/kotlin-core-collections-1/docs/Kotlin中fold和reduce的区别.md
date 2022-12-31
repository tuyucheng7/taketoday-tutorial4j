## 1. 概述

在本教程中，我们将探讨Kotlin中fold()和reduce()方法之间的区别。

尽管这两个函数都遍历一个集合并应用给定的操作，但它们是完全不同的。

## 2. reduce()

**reduce()方法将给定集合转换为单个结果**，它需要一个[lambda函数](https://www.baeldung.com/kotlin-lambda-expressions)运算符来将一对元素组合成一个所谓的累加值。

然后它从左到右遍历集合，并逐步将累加值与下一个元素组合起来。

为了实际看到这一点，让我们使用reduce来计算数字列表的总和：

```kotlin
val numbers: List<Int> = listOf(1, 2, 3)
val sum: Int = numbers.reduce { acc, next -> acc + next }
assertEquals(6, sum)
```

在空列表的情况下会发生什么？实际上，没有正确的值可以返回，所以reduce()抛出一个RuntimeException：

```kotlin
val emptyList = listOf<Int>()
assertThrows<RuntimeException> { emptyList.reduce { acc, next -> acc + next } }
```

有人可能会争辩说，在这种情况下返回0将是一个有效结果。正如我们将看到的，fold()在这方面提供了更大的灵活性。

要了解另一个特征，让我们看一下reduce()的签名：

```kotlin
inline fun <S, T : S> Iterable<T>.reduce(operation: (acc: S, T) -> S): S
```

该函数定义了泛型S和S的子类型T。最后，reduce()返回类型S的一个值。

让我们假设在前面的示例中，总和可能超出Int的范围。因此，我们将结果类型更改为Long：

```kotlin
// doesn't compile
val sum: Long = numbers.reduce<Long, Int> { acc, next -> acc.toLong() + next.toLong() }
```

显然，它无法编译，因为Long不是Int的超类型。要修复编译错误，我们可以将Long类型更改为Number，因为Number是Int的超类型。

但是，**它并没有解决更改结果类型的一般问题**。

## 3. fold()

因此，让我们看看是否可以通过使用fold()实现前面的求和示例来解决这些问题：

```kotlin
val sum: Int = numbers.fold(0){ acc, next -> acc + next }
assertEquals(6, sum)
```

在这里，我们提供了一个初始值。**与reduce()相反，如果集合为空，则返回初始值**。

为了更深入地挖掘，让我们看一下fold()的签名：

```kotlin
inline fun <T, R> Iterable<T>.fold(initial: R, operation: (acc: R, T) -> R): R
```

与reduce()相比，它指定了两个任意泛型类型T和R。

因此，我们可以将结果类型更改为Long：

```kotlin
val sum: Long = numbers.fold(0L){ acc, next -> acc + next.toLong() }
assertEquals(6L, sum)
```

**一般来说，改变结果类型的能力是一个非常强大的工具**。例如，如果我们使用正确的结果类型，我们可以轻松地将集合拆分为偶数和奇数：

```kotlin
val (even, odd) = numbers.fold(Pair(mutableListOf<Int>(), mutableListOf<Int>())) { eoPair, number ->
    eoPair.apply {
        when (number % 2) {
            0 -> first += number
            else -> second += number
        }
    }
}

assertEquals(listOf(2), even)
assertEquals(listOf(1, 3), odd)
```

## 4. reduce和fold的变体

我们已经看到了这两个函数的基本变体；然而，Kotlin标准库也提供了它们的一些变体。

**如果我们需要从右到左以相反的顺序遍历集合，我们可以使用foldRight()**：

```kotlin
val reversed = numbers.foldRight(listOf<Int>()) { next, acc -> acc + next }
assertEquals(listOf(3,2,1), reversed)
```

我们应该注意，**当我们使用foldRight()时，lambda参数的顺序是相反的：foldRight(...) { next , acc -> ... }**。

同样，我们可以使用reduceRight()。

此外，我们可能还想**访问集合中每个元素的索引**：

```kotlin
val reversedIndexes = numbers.foldRightIndexed(listOf<Int>()) { i, _, acc -> acc + i }
assertEquals(listOf(2,1,0), reversedIndexes)
```

同样，我们可以使用reduceRightIndexed()。

此外，foldIndexed()和reduceIndexed()提供对索引的访问，这些索引具有基本的从左到右的顺序。

## 5. 总结

所以，我们已经看到了fold()和reduce()之间的区别。

一方面，如果我们只对一个非空集合进行操作，并将所有元素组合成一个相同类型的结果，那么reduce()是一个不错的选择。另一方面，如果我们想提供一个初始值或改变结果类型，那么fold()给了我们做这件事的灵活性。

要深入了解，我们建议你查看[Kotlin的集合转换](Kotlin中的集合转换.md)。
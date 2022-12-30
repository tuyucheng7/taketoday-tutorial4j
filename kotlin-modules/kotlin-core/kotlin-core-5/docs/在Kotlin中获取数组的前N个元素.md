## 1. 概述

在本教程中，我们将了解如何获取Kotlin数组或集合的前N个元素，我们将首先介绍如何通过遍历数组并将值存储在临时变量中来实现这一点。然后，我们将了解如何利用Kotlin中内置的take()和takeWhile()函数，这些函数将帮助我们用更少的代码实现相同的结果。

## 2. 经典的迭代方式

执行此操作的一种方法是手动实现此功能，执行此操作的经典方法是遍历原始数组并将值存储在一个临时变量中，然后返回这个临时变量作为结果，丢弃其余变量。让我们编写一个单元测试来演示这个概念：

```kotlin
@Test
fun `Given an array take first n elements`() {
    val originalArray = intArrayOf(1, 2, 3, 4, 5)

    val n = 3
    val tempArray = IntArray(n)

    for (i in 0 until n) {
        tempArray[i] = originalArray[i]
    }

    assertContentEquals(intArrayOf(1, 2, 3), tempArray)
}
```

## 3. take()

Kotlin标准库(内置)涵盖了同样的问题，**我们可以使用提供的函数来做我们想做的事情**，不折不扣！从[Kotlin文档](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/take.html)中，我们看到take()函数“返回一个包含前n个元素的列表”-这个定义与我们的问题陈述完全一致。

使用它就像在我们的原始数组上调用它一样简单，让我们添加另一个单元测试来检查相同的结果：

```kotlin
@Test
fun `Given an array take first n elements using kotlin function`() {
    val originalArray = intArrayOf(1, 2, 3, 4, 5)

    val n = 3

    val tempArray: List<Int> = originalArray.take(n)

    assertContentEquals(listOf(1, 2, 3), tempArray)
}
```

一个可能被忽视的小细节：我们现在直接处理List而不是Array，区别很细微，但类型是不同的。要了解两者之间的区别，我们的文章[Kotlin中数组与集合的区别](https://www.baeldung.com/kotlin/list-vs-array)可能会有所帮助。

### 3.1 从列表中获取元素

我们可以从上一段中注意到，Kotlin已经更喜欢使用List而不是Array，可以合理地预期可用于Array的相同功能也可用于List。通过稍微更改我们之前的一些单元测试，我们可以看到语法完全按预期工作：

```kotlin
@Test
fun `Given a list take first n elements using kotlin function`() {
    val originalList = listOf(1, 2, 3, 4, 5)

    val n = 3

    val tempList = originalList.take(n)

    assertContentEquals(listOf(1, 2, 3), tempList)
}
```

### 3.2 takeWhile()函数

take()的高级版本是takeWhile()，从[Kotlin文档](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/take-while.html)中，我们了解到它“返回一个包含满足给定谓词的第一个元素的列表”。

让我们在我们的列表中试验这个函数，以了解我们如何调用它：

```kotlin
@Test
fun `Given a list take elements while predicate`() {
    val list = listOf(1, 2, 3, 4, 5)

    val takenList = list.takeWhile { it < 4 }

    assertContentEquals(takenList, listOf(1, 2, 3))
}
```

**在未排序的集合上使用此函数时必须小心**，因为takeWhile()将从头到尾迭代，直到不再满足条件。让我们看看使用不同的列表会发生什么：

```kotlin
@Test
fun `Given a list take elements while predicate in an unsorted list`() {
    val list = listOf(5, 3, 2, 4, 1)

    val takenList = list.takeWhile { it < 4 }

    assertContentEquals(takenList, listOf())
}
```

当集合满足谓词时，我们将从集合的开头获取元素，由于集合的开头不满足谓词，因此我们不会从集合中获取任何元素。

## 4. 转换回数组

如果Array确实是我们所需要的，我们可以使用List上可用的toTypedArray()函数：

```kotlin
val list: List<Int> = listOf(1, 2, 3, 4)
val array: Array<Int> = list.toTypedArray()
```

如果我们使用Int或Long等原始类型集合，我们可能希望利用更具体的函数，例如toIntArray()或toLongArray()。

有关此主题的更多信息，请查看我们的文章[在Kotlin中将集合转换为数组](https://www.baeldung.com/kotlin/convert-list-to-array)。

## 5. 总结

在本教程中，我们学习了如何编写代码来获取Array或List的前N个元素，我们首先通过手动遍历集合来学习经典地实现。然后，我们学习了如何利用内置的take()和takeWhile()函数来获得相同的结果，最后，我们了解了如何将结果转换回数组。
## 1. 简介

Kotlin的标准库非常全面，尽管基于Java标准库，但它包含许多Java缺乏合适替代品的扩展函数。

**在日常工程中特别有用的一组函数是集合的聚合函数**，此类函数允许我们将集合归约为单个值。

更具体地说，任何可迭代对象或原始类型数组都有其快捷收集器。

在本教程中，我们将研究其中之一-[sum()](https://www.baeldung.com/kotlin/aggregate-operations)函数。

## 2. Sum聚合函数

**Kotlin标准库中有几个sum()函数**：一个在[Iterable](https://www.baeldung.com/kotlin/lists)接口上的扩展函数用于每种基本数字类型，然后一个用于每个基本数字类型的数组。在内部，它们都非常相似：

```kotlin
public fun Iterable<Int>.sum(): Int {
    var sum: Int = 0
    for (element in this) {
        sum += element
    }
    return sum
}
```

正如我们所见，这个函数甚至可以在空集合上工作，返回0：

```kotlin
assert(listOf<Int>().sum() == 0) // true
```

它会给出一个适当的总和：

```kotlin
listOf(
    16, 96, 100, 15, 30,
    70, 11, 31, 52, 70,
    42, 50, 56, 93, 57
).sum()
    .also { assertEquals(789, it) }
```

**由于此函数仅适用于整数List，因此不可能有null潜入某处**，这意味着这个函数永远不会抛出任何异常。

事实上，如果Int类型溢出，它甚至也不会抛出：

```kotlin
listOf(Int.MAX_VALUE - 2, 1, 2, 4)
    .sum()
    .also { assert(-2147483644 == it) } // true
```

因此，我们需要密切关注我们的数据，每当list.average() * list.size > Int.MAX_VALUE时，我们必须将整个List转换为Long或接受精度损失并使用Double。

如果我们的集合非常庞大(比O(100000)大得多)，将它分成几个块并并行处理它可能是有益的：

```kotlin
val executor = Executors.newFixedThreadPool(4)
listOf(16, 96, 100, 15, 30, 70, 11, 31, 52, 70, 42, 50, 56, 93, 57)
    .asSequence()
    .chunked(chunkSize) // Must be calculated based on the collection size and the number of available CPU cores
    .map { executor.submit(Callable { it.sum() }) }
    .map { it.get() }
    .sum()
    .also { assertEquals(789, it) }
```

由于我们的任务严重依赖CPU，因此在这里使用协程没有意义，一个带有任务的简单线程池就足够了。

但是，必须注意的是，在现代CPU上，算术运算速度非常快，除非在非常极端的情况下，否则我们不太可能获得任何好处。

## 3. 总结

在本文中，我们描述了Kotlin标准库集合函数之一sum()，**在编写我们自己的函数之前检查Kotlin库是否已经有我们需要的函数总是一个好主意**，很可能情况确实如此。

sum()函数适用于原始类型的任何集合或数组，为Byte、Short和Int返回一个Int，并为Long、Double和Float返回与List相同的类型。算法中没有溢出检查，因此我们隐含地假设元素的总和将适合结果类型。
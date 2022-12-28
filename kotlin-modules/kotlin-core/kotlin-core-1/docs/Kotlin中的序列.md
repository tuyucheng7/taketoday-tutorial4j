## 1. 概述

Kotlin语言引入了序列作为处理集合的一种方式，它们与Java Streams非常相似，但是，它们在底层使用了不同的关键概念。在本教程中，我们将简要讨论什么是序列以及我们为什么需要它们。

## 2. 理解序列

序列是类型为T的容器Sequence<T\>，它也是一个接口，包括像map()和filter()这样的中间操作，以及像count()和find()这样的终端操作。

与Java中的Stream一样，Kotlin中的Sequence是延迟执行的。不同之处在于，如果我们使用一个序列通过多个操作来处理一个集合，我们不会在每个步骤结束时得到一个中间结果。因此，**我们不会在处理完每个步骤后引入一个新的集合**。

它在处理大型集合时具有提升应用程序性能的巨大潜力。另一方面，**在处理小型集合时，序列会产生开销**。

## 3. 创建序列

### 3.1 从元素

要从元素创建序列，我们只需使用sequenceOf()函数：

```kotlin
val seqOfElements = sequenceOf("first" ,"second", "third")
```

### 3.2 从函数

要创建一个无限序列，我们可以调用generateSequence()函数：

```kotlin
val seqFromFunction = generateSequence(Instant.now()) {it.plusSeconds(1)}
```

### 3.3 从块

我们还可以从任意长度的块中创建一个序列，让我们看一个使用yield()和yieldAll()的例子，它接收单个元素，而yieldAll()接收一个集合：

```kotlin
val seqFromChunks = sequence { 
    yield(1)
    yieldAll((2..5).toList())
}
```

这里值得一提的是，所有的chunk都会一个接一个的产生元素。换句话说，**如果我们有一个无限集合生成器，我们应该把它放在最后**。

### 3.4 从集合

要从Iterable接口的集合创建一个序列，我们应该使用asSequence()函数：

```kotlin
val seqFromIterable = (1..10).asSequence()
```

## 4. 惰性和急切处理

让我们比较两个实现。第一个，不使用序列，是急切的：

```kotlin
val withoutSequence = (1..10).filter { it % 2 == 1 }.map { it * 2 }.toList()
```

第二个，使用序列，是惰性的：

```kotlin
val withSequence = (1..10).asSequence().filter { it % 2 == 1 }.map { it * 2 }.toList()
```

每种情况下引入了多少个中间集合？

在第一个示例中，每个运算符都引入了一个中间集合。因此，所有十个元素都传递给map()函数。**在第二个例子中，没有引入中间集合，因此map()函数只有五个元素作为输入**。

## 5. 总结

在本教程中，我们简要讨论了Kotlin中的序列。我们已经了解了如何以不同的方式创建序列，此外，我们还看到了处理带有序列和不带有序列的集合的区别。
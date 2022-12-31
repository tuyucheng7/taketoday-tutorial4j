## 1. 概述

集合结构(如List)中最流行的操作之一是从包含的元素中获取随机项，虽然这是一个简单的操作，但有时实现起来可能不是一件显而易见的事情。

在本教程中，我们将看到一些使用Kotlin在不同上下文中执行此操作的有效实现。

## 2. 实现

从列表中选择随机项的基本概念是生成随机索引以使用List.get()方法获取特定元素。

### 2.1 单个随机项

正如我们提到的，我们需要首先生成随机索引，然后从列表中获取元素：

```kotlin
fun randomElementFromGivenList() {
    val list = listOf(1, 2, 3, 4, 5)
    val randomIndex = Random.nextInt(list.size)
    val randomElement = list[randomIndex]

    // here we can use the selected element to print it for example
    println(randomElement)
}
```

从Kotlin 1.3开始，有一个内置方法可以从列表中获取随机项：

```kotlin
val list = listOf(1, 2, 3, 4, 5)
val randomElement = list.random()
```

### 2.2 具有序列的随机元素

序列是类型为T的容器Sequence<T\>，它也是一个接口，包括像map()和filter()这样的中间操作，以及像count()和find()这样的终端操作。

我们可以利用可用于Sequence类型的操作从列表中获取随机元素，使用自Kotlin 1.2以来可用的内置shuffled()方法：

```kotlin
fun randomElementUsingSequences() {

    val list = listOf("one", "two", "three", "four", "five")
    val randomElement = list.asSequence().shuffled().find { true }

    // use the selected element or print it out
}
```

### 2.3 多个随机元素

有时，我们需要从列表中随机选择多个元素，如果我们不关心在后续执行中获取相同的元素，我们可以将它们保留在列表中：

```kotlin
fun randomElementsFromGivenList() {

    val list = listOf("one", "two", "three", "four", "five")
    val numberOfElements = 2

    val randomElements = list.asSequence().shuffled().take(numberOfElements).toList()

    // use the selected elements or print it out
}
```

如果我们总是期望每次执行都有不同的元素，或者要求是随机提取所有项目，我们需要从列表中删除选定的元素。

重要的是要记住List类型是不可变的，所以我们不能添加或更新原始列表。因此，我们需要使用MutableList来删除元素：

```kotlin
val list = mutableListOf("one", "two", "three", "four", "five")
val randomElements = list.asSequence().shuffled().take(numberOfElements).toList()

list.removeIf { i -> randomElements.contains(i) }
```

### 2.4 多线程环境中的随机元素

我们需要确保为访问多线程应用程序的Random实例的每个进程获得不同的值，由于Kotlin没有原生支持来执行此操作，因此我们需要使用Java的ThreadLocalRandom类：

```kotlin
fun randomElementMultithreaded() {

    val list = listOf("one", "two", "three", "four", "five")
    val randomIndex: Int = ThreadLocalRandom.current().nextInt(list.size)
    val randomElement = list[randomIndex]

    // use the selected element or print it out
}
```

## 3. 总结

在这篇文章中，我们看到了一些在考虑不同用例和场景的情况下从Kotlin列表中获取随机元素的有效实现。
## 1. 概述

在本快速教程中，我们将看到几种通过索引迭代Kotlin集合的不同方法。

## 2. 仅索引

**要仅使用集合索引迭代Kotlin中的任何集合，我们可以在给定集合上使用**[indices](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/indices.html)**扩展属性**：

```kotlin
val colors = listOf("Red", "Green", "Blue")
for (i in colors.indices) {
    println(colors[i])
}
```

indices属性将返回集合中所有有效索引的范围。当然，这适用于所有集合，甚至数组：

```kotlin
val colorArray = arrayOf("Red", "Green", "Blue")
for (i in colorArray.indices) {
    println(colorArray[i])
}
```

**也可以在Kotlin中使用**[范围表达式](https://www.baeldung.com/kotlin-ranges)**来实现相同的目的**：

```kotlin
for (i in 0 until colors.size) {
    println(colors[i]) 
}
```

我们甚至可以使用这个紧凑的版本：

```kotlin
(0 until colors.size).forEach { println(colors[it]) }
```

首先，我们创建一个介于0(含)和集合大小(不含)之间的范围，然后我们将为每个索引调用相同的println()。然而，与第一种方法相比，这种方法的可读性稍差。因此，**建议尽可能使用索引扩展属性**。

## 3. 索引与值

**如果我们想基于索引和值进行迭代，我们可以使用**[forEachIndexed()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/for-each-indexed.html)**扩展函数**：

```kotlin
colors.forEachIndexed { i, v -> println("The value for index $i is $v") }
```

如上所示，我们使用lambda迭代索引和值组合，lambda接收两个参数：索引作为第一个参数，值作为第二个参数。

此外，**如果我们更喜欢for表达式，我们可以使用**[withIndex()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/with-index.html)**扩展函数**：

```kotlin
for (indexedValue in colors.withIndex()) {
    println("The value for index ${indexedValue.index} is ${indexedValue.value}")
}
```

withIndex()函数返回[IndexedValue](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-indexed-value/)数据类实例的集合，**因为它是一个数据类，我们可以在它上面使用**[析构模式](https://www.baeldung.com/kotlin-destructuring-declarations)：

```kotlin
for ((i, v) in colors.withIndex()) {
    println("The value for index $i is $v")
}
```

使用析构模式更加优雅和可读，因此它是这里的首选方法。

最后，**有时我们可能需要根据元素的索引或值来过滤元素**。为此，我们可以使用[filterIndexed()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/filter-indexed.html)扩展函数：

```kotlin
colors.filterIndexed { i, v -> i % 2 == 0 }
```

与forEachIndexed()类似，此函数也接收具有相同参数的lambda表达式，如果我们不需要参数，我们可以通过下划线省略它：

```kotlin
colors.filterIndexed { i, _ -> i % 2 == 0 }
```

## 4. 总结

在这个简短的教程中，我们熟悉了使用索引或索引值组合迭代或过滤任何集合的不同方法。此外，我们还快速回顾了一些优雅的Kotlin概念，例如析构模式。
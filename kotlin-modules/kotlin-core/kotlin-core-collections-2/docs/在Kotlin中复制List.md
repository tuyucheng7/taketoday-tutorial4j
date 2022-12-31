## 1. 概述

在这个简短的教程中，我们将学习如何在Kotlin中复制[List](https://www.baeldung.com/kotlin/lists)。

## 2. List

为了在Kotlin中复制一个列表，我们可以使用[toList()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/to-list.html)扩展函数：

```kotlin
val cities = listOf("Berlin", "Munich", "Hamburg")
val copied = cities.toList()
assertThat(copied).containsAll(cities)
```

如上所示，该函数创建了一个新的List，并将源List的所有元素依次添加到其中。同样，我们可以使用[toMutableList()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/to-mutable-list.html)扩展函数来创建一个可变集合：

```kotlin
val mutableCopy = cities.toMutableList()
assertThat(mutableCopy).containsAll(cities)
```

两个扩展函数都从源集合创建一个副本，唯一不同的是，后者后会返回一个[MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/) 。

**请注意，这些只是创建源集合的**[浅副本](https://www.baeldung.com/java-deep-copy)，也就是说，这两个List实例是[Java堆](https://www.baeldung.com/java-stack-heap)中的不同对象，但它们的内容是相同的对象：

```kotlin
assertThat(copied).isNotSameAs(cities)
assertThat(copied[0]).isSameAs(cities[0])
```

## 3. 总结

在本教程中，我们学习了几种在Kotlin中将List的内容复制到另一个List的方法。
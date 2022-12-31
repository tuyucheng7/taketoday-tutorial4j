## 1. 概述

在这个简短的教程中，我们将学习几种在Kotlin中将[Map](https://www.baeldung.com/kotlin/maps)的所有条目复制到另一个Map的方法。

## 2. 复制Map

从Kotlin 1.1开始，**我们可以使用**[toMap()](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/src/kotlin/collections/Maps.kt#L598)**扩展函数将Map的所有条目复制到另一个**[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/)**中**：

```kotlin
val bookInfo = mapOf("name" to "1984", "author" to "Orwell")
val copied = bookInfo.toMap()
assertThat(copied).isNotSameAs(bookInfo)
assertThat(copied).containsAllEntriesOf(bookInfo)
```

当然，完成后，我们将无法更改的Map内容，因为它是不可变的。为了能够在后更改Map，我们可以使用[toMutableMap()](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/src/kotlin/collections/Maps.kt#L610)扩展函数：

```kotlin
val mutableCopy = bookInfo.toMutableMap()
assertThat(mutableCopy).containsAllEntriesOf(bookInfo)
```

也可以用另一个Map实例的内容填充一个预先存在的Map实例：

```kotlin
val destination = mutableMapOf<String, String>()
bookInfo.toMap(destination)
assertThat(destination).containsAllEntriesOf(bookInfo)
```

在上面的示例中，我们将另一个Map传递给[toMap(map)](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/src/kotlin/collections/Maps.kt#L616)扩展函数，这样，我们将接收Map(bookInfo)的所有条目复制到给定的Map。

在Kotlin 1.1之前，我们可以直接使用底层的Map实现来实现同样的事情。例如，在这里，我们使用[java.util.HashMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html)构造函数从另一个Map复制所有内容：

```kotlin
val copied = java.util.HashMap(bookInfo)
assertThat(copied).containsAllEntriesOf(bookInfo)
```

**请注意，这些只是创建给定Map的**[浅副本](https://www.baeldung.com/java-deep-copy)；也就是说，两个Map实例是[Java堆](https://www.baeldung.com/java-stack-heap)中的不同对象，但它们的内容是相同的对象：

```kotlin
assertSame(copied["name"], bookInfo["name"])
```

## 3. 总结

在本教程中，我们学习了几种在Kotlin中将Map的内容复制到另一个Map的方法。
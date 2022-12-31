## 1. 概述

在本教程中，我们将讨论[Kotlin](https://www.baeldung.com/kotlin-overview)中的集合聚合操作。

## 2. Kotlin中的聚合操作

Kotlin中的聚合操作是在元素集合(例如数组和列表)上执行的，并根据集合的内容返回单个累加值。

### 2.1 count()、sum()和average()

我们可以使用count()函数来查找集合中元素的数量：

```kotlin
val numbers = listOf(1, 15, 3, 8)
val count = numbers.count()

assertEquals(4, count)
```

随后，要找到集合中所有元素的总和，我们可以使用sum()函数：

```kotlin
val sum = numbers.sum()

assertEquals(27, sum)
```

同样，要找到集合中元素的平均值，我们可以使用average()函数：

```kotlin
val average = numbers.average()

assertEquals(6.75, average)
```

### 2.2 sumBy()和sumByDouble()

**我们可以使用sumBy()函数来查找通过将选择器函数应用于集合中的每个元素而映射的所有值的总和**。此函数始终返回一个整数值。尽管如此，我们还可以将此函数与Byte、Short、Long和Float元素的列表一起使用：

```kotlin
val sumBy = numbers.sumBy { it * 5 }

assertEquals(135, sumBy)
```

要操作返回Double值的函数，我们可以使用sumByDouble()函数：

```kotlin
val sumByDouble = numbers.sumByDouble { it.toDouble() / 8 }

assertEquals(3.375, sumByDouble())
```

从Kotlin 1.4开始，有一个新的[sumOf()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/sum-of.html)方法具有不同的重载版本，可以通过更好的API实现相同的目的：

```kotlin
data class Employee(val name: String, val salary: Int, val age: UInt)

val employees = listOf(Employee("A", 3500, 23u), Employee("B", 2000, 30u))

assertEquals(5500, employees.sumOf { it.salary })
assertEquals(53u, employees.sumOf { it.age })
```

如上所示，我们可以使用相同的函数对Int和UInt值求和并分别返回Int和UInt求和。**因此，没有必要为每种数据类型使用不同名称的函数，就像sumBy()和sumByDouble()一样**。

更具体地说，我们可以将sumOf()扩展函数与Int、Long、Double、UInt、ULong、BigInteger和BigDecimal数据类型一起使用。

### 2.3 min()和max()

**要找到集合中最大的元素，我们可以使用max()函数**：

```kotlin
val maximum = numbers.max()

assertEquals(15, maximum)
```

**同样，我们可以使用min()函数来查找集合中的最小元素**：

```kotlin
val minimum = numbers.min()

assertEquals(1, minimum)
```

如果集合中没有元素，这两个函数都返回null。

**从Kotlin 1.4开始，这些函数已被弃用**。有两个名为[maxOrNull()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/max-or-null.html)和[minOrNull()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/min-or-null.html)的新函数与其余的集合API一致，并且更明确地说明了它们可能返回的内容。

除了这两个之外，以下部分中描述的max和min相关函数也已弃用，取而代之的是新的orNull替代方案。

### 2.4 maxBy()和minBy()

maxBy()和minBy()函数将集合中的元素转换为可比较的类型，并将它们与计算值进行比较。

要从给定的选择器函数中找到产生最大值的第一个元素，我们可以使用maxBy()函数：

```kotlin
val maxBy = numbers.maxBy { it % 5 }

assertEquals(3, maxBy)
```

此外，我们可以使用minBy()函数从给定的选择器函数中找到产生最小值的第一个元素：

```kotlin
val minBy = numbers.minBy { it % 5 }

assertEquals(15, minBy)
```

当集合中没有元素时，这两个函数都返回null。

### 2.5 maxWith()和minWith()

maxWith()和minWith()函数**将集合中的元素相互比较，并根据比较器的返回值对它们进行排序**。

我们可以使用maxWith()来根据Comparator对象找到具有最大值的第一个元素：

```kotlin
val strings = listOf("Berlin", "Kolkata", "Prague", "Barcelona")
val maxWith = strings.maxWith(compareBy { it.length % 4 })

assertEquals("Kolkata", maxWith)
```

同样，要根据Comparator对象返回第一个具有最小值的元素，我们可以使用minWith()函数：

```kotlin
val minWith = strings.minWith(compareBy { it.length % 4 })

assertEquals("Barcelona", minWith)
```

如果集合中没有元素，这两个函数都返回null。

## 3. fold和reduce函数

fold()和reduce()函数可用于对元素集合按顺序应用操作并返回累积结果，这些函数需要的两个参数是累加值和集合的元素。

这两个函数的区别很简单，fold()函数采用初始值，lambda的第一次调用将接收该初始值和集合的第一个元素作为参数。相反，reduce()函数不采用初始值，而是在第一次调用lambda时使用集合的第一个和第二个元素作为参数。

我们在需要为操作定义默认值的情况下使用fold()。或者，在我们的操作仅依赖于集合中定义的值的情况下，reduce()很有用。

**另一个区别是reduce()函数在空集合上执行时会抛出异常**，但是，由于fold()函数需要一个初始值，因此在空集合的情况下它将用作默认值，因此不会抛出异常。

### 3.1 fold()和foldRight()

**fold()函数采用累加器的初始值**，然后它通过从左到右对当前累加器值和集合的每个元素应用操作来累加值：

```kotlin
val numbers = listOf(1, 15, 3, 8)
val result = numbers.fold(100) { total, it -> total - it }

assertEquals(73, result)
```

对lambda函数的第一次调用将使用参数100和1。

**foldRight()函数的工作方式与fold()函数类似，但它会从右到左遍历集合中的元素**。此外，操作参数的顺序也会发生变化，首先使用元素，然后使用累加值：

```kotlin
val result = numbers.foldRight(100) { it, total -> total - it }

assertEquals(73, result)
```

在上面的示例中，对lambda函数的第一次调用将使用参数100和8。

### 3.2 foldIndexed()和foldRightIndexed()

**当我们想要基于元素索引应用操作时，foldIndexed()函数很有用**。它从初始值开始累加值，然后从左到右对当前累加器值和定义集合中具有其索引的每个元素应用操作。**此外，foldIndexed()函数将元素索引作为操作参数以及集合的累加值和元素**：

```kotlin
val result = numbers.foldIndexed(100) { index, total, it ->
    if (index.minus(2) >= 0) total - it else total
}

assertEquals(89, result)
```

此处对lambda表达式的第一次调用将使用参数100和1。由于参数1不满足lambda表达式中的索引条件，因此调用下一个最佳参数3，因为它满足索引条件。

foldRightIndexed()函数的工作方式与foldIndexed()函数类似，从右到左遍历集合：

```kotlin
val result = numbers.foldRightIndexed(100) { index, it, total ->
    if (index.minus(2) >= 0) total - it else total
}

assertEquals(89, result)
```

在这种情况下，对lambda表达式的第一次调用将使用参数100和8。由于参数8满足lambda表达式中的索引条件，因此它用于累加值。

### 3.3 reduce()和reduceRight()

当我们有一个元素列表并且想将其缩减为单个值时，reduce()函数很有用。此函数从第一个元素开始累加值，然后从左到右对当前累加器值和集合的每个元素应用操作：

```kotlin
val result = numbers.reduce { total, it -> total - it }

assertEquals(-25, result)
```

此处对lambda的第一次调用将使用参数1和15。

**reduceRight()函数的工作方式与reduce()函数类似，但它从右到左遍历集合**。因此，此函数从最后一个元素开始累积值：

```kotlin
val result = numbers.reduceRight() { it, total -> total - it }

assertEquals(-11, result)
```

在这种情况下，对lambda的第一次调用将使用参数8和3。

### 3.4 reduceIndexed()和reduceRightIndexed()

reduceIndexed()函数从第一个元素开始累积值，然后从左到右将操作应用于当前累加器值以及定义集合中具有其索引的每个元素：

```kotlin
val result = numbers.reduceIndexed { index, total, it ->
    if (index.minus(2) >= 0) total - it else total
}

assertEquals(-10, result)
```

此处对lambda表达式的第一次调用将使用参数1和15。由于参数15不满足lambda表达式中的索引条件，因此调用下一个参数3，因为它满足索引条件。

**reduceRightIndexed()函数的工作方式与reduceIndexed()函数类似，但它从右到左遍历集合**：

```kotlin
val result = numbers.reduceRightIndexed { index, it, total ->
    if (index.minus(2) >= 0) total - it else total
}

assertEquals(5, result)
```

在这种情况下，对lambda表达式的第一次调用将使用参数8和3。由于参数3满足lambda表达式中的索引条件，因此它用于累加值。

### 3.5 中间结果

到目前为止，我们已经看到了fold和reduce函数，它们一个接一个地对一系列值应用一些转换。当这条链结束时，他们返回最终折叠或归约的值。

**从Kotlin 1.4开始，我们还可以访问除最终值之外的所有中间结果**。更具体地说，对于折叠，我们可以使用[runningFold()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/running-fold.html)：

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
assertEquals(listOf(0, 1, 3, 6, 10, 15), numbers.runningFold(0) {total, it -> total + it})
```

如上所示，此函数以零作为初始值折叠给定列表。**在操作期间，它保留所有中间值并返回它们**。

使用[runningReduce()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/running-reduce.html)归约也是如此：

```kotlin
assertEquals(listOf(1, 3, 6, 10, 15), numbers.runningReduce { total, it -> total + it })
```

## 4. 总结

在本文中，我们看到了Kotlin中用于处理集合的各种聚合操作。请参阅我们的[Kotlin教程](https://www.baeldung.com/kotlin-overview)以了解有关Kotlin功能的更多信息。
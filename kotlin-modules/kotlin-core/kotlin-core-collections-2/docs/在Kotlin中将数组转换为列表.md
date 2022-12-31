## 1. 概述

在本教程中，我们将探讨如何在Kotlin中将[数组](https://www.baeldung.com/kotlin/arrays)转换为[列表](https://www.baeldung.com/kotlin/lists)，尽管在实践中这两种数据结构在性能和内存占用方面存在显着差异，但实际上差异较小。原因是大多数时候当我们需要一个List时，我们无论如何都会[使用ArrayList](https://www.baeldung.com/java-arraylist-linkedlist#4-applications-and-limitations)。我们还要记住，Kotlin鼓励其用户不要修改数据结构，而是在更新时创建新的数据结构，而差异变得非常小。

尽管如此，一些Kotlin标准库函数仅适用于Collection后代，因此了解如何执行转换很有帮助。

## 2. 包装转换

ArrayList是实现List接口的数组的包装器。将我们作为参数的数组包装到这样的包装器中以使其成为List是非常有意义的。

将数组包装到List中的最简单方法是[asList()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/as-list.html)函数：

```kotlin
val array = intArrayOf(1, 2, 3, 4)
val list = array.asList()

assertEquals(listOf(1, 2, 3, 4), list)
```

对于对象数组，此函数将实际执行委托给JavaArrays.asList()函数。对于原始类型数组，它返回一个实现AbstractList接口的临时对象，**新列表由原始数组支持**。因此，对数组的任何更改都会影响转换后的列表：

```kotlin
array[0] = 0
assertEquals(listOf(0, 2, 3, 4), list)
```

此外，从时间和空间复杂度的角度来看，**转换算法的运行时间均为O(1)**。

## 3. 转换

将Array转换为List的另一种逻辑方法是将所有元素到新的数据结构中，Kotlin提供了几种方法来做到这一点，我们可以选择是否希望结果列表可变。

### 3.1 使用Array.toCollection方法

Kotlin在Array类上提供了一个扩展函数：Array.toCollection，**此函数需要一个MutableCollection对象作为参数，并将给定数组中的所有元素附加到集合中**。

如果我们想将一个数组转换为一个新的mutableList，我们可以简单地将一个空的MutableList对象传递给函数，例如，一个ArrayList：

```kotlin
val myList = givenArray.toCollection(ArrayList())
assertThat(myList).isEqualTo(expectedList)
```

当然，如果我们想将该数组中的元素附加到现有的MutableList中，这个函数更适合：

```kotlin
val existingList = mutableListOf("I have an element already.")
val appendedList = givenArray.toCollection(existingList)
assertThat(appendedList).isEqualTo(mutableListOf("I have an element already.", givenArray))
```

最后，**我们应该注意toCollection函数返回的对象与我们传递给它的对象是同一个对象。因此，返回的列表是可变的**。

### 3.2 使用Array.toList方法

其次，要将任何Kotlin [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/)转换为其对应的[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/)版本，**我们可以使用**[toList()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/to-list.html)**扩展函数**：

```kotlin
val array = intArrayOf(1, 2, 3, 4)
val list = array.toList()

assertEquals(listOf(1, 2, 3, 4), list)
```

由于它是一个List，我们将无法在转换后对其进行修改。**如果我们以后需要修改列表，我们应该使用**[toMutableList()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/to-mutable-list.html)**扩展函数**：

```kotlin
val array = intArrayOf(1, 2, 3, 4)
val mutable = array.toMutableList()

assertEquals(listOf(1, 2, 3, 4), mutable)

mutable.add(5)
assertEquals(listOf(1, 2, 3, 4, 5), mutable)
```

如上所示，转换后，我们在转换后的列表中添加了一个元素，这是可能的，因为此函数返回[MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/)的实例而不是普通的List。

### 3.3 使用listOf方法

最后，技术上还有第三种方法可以将数组到新列表。**在Kotlin中**，[扩展运算符](https://www.baeldung.com/kotlin/varargs-spread-operator#spread-operator)(*)**可以将数组实例分解为可变参数**，listOf和mutableListOf()函数接收[可变参数](https://www.baeldung.com/kotlin/varargs-spread-operator#varargs)元素作为参数，所以我们可以使用这些函数创建一个测试方法：

```kotlin
val myList = listOf(givenArray)
assertThat(myList).isEqualTo(expectedList)
```

在示例中，结果列表是不可变的。如果我们需要一个可变列表，我们可以调用mutableListOf。

### 3.4 链接和复杂性

**所有这些函数都将数组值到新列表中**，因此，转换后的数组和列表之间没有残留链接。这意味着如果我们更改数组元素，该更改将不会反映在转换后的列表中：

```kotlin
array[0] = 0
assertEquals(listOf(1, 2, 3, 4), list) // list didn't change
```

此外，**转换算法的时间和空间复杂度为O(n)**，因为我们需要从源数组中所有元素。

## 4. 总结

我们看到了如何将Kotlin Array转换为对应的List或MutableList，根据未来的使用情况，包装我们的数组以使其表现得像List可能就足够了。如果我们想向List中添加更多元素或者能够独立更改原始Array，那么最好使用函数，我们还可以将数组中的元素连接到现有集合中。
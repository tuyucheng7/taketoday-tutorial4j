## 1. 概述

在本快速教程中，我们将探索如何在Kotlin中将[List](https://www.baeldung.com/kotlin/lists)转换为[数组](https://www.baeldung.com/kotlin/arrays)。此外，我们将在日常工作中讨论在Arrays和ArrayList之间进行选择的偏好。

为简单起见，我们将在单元测试中使用断言来验证转换结果。

## 2. 将List转换为数组

**当我们想在Kotlin中将List转换为Array时，**[扩展函数](https://www.baeldung.com/kotlin/extension-methods)**Collection.toTypedArray()就是一种方法**，让我们看一个例子：

```kotlin
val myList = listOf("one", "two", "three", "four", "five")
val expectedArray = arrayOf("one", "two", "three", "four", "five")
assertThat(myList.toTypedArray()).isEqualTo(expectedArray)
```

如果我们执行上面的测试，它就会通过。由于toTypedArray是Collection接口上的扩展函数，因此它可用于所有Collection对象，例如Set和List。

我们知道，在Kotlin中，除了Array<T\>之外，我们还有原始类型数组类型，例如IntArray、LongArray等，由于[原始类型数组比它们的类型化数组更有效](https://www.baeldung.com/kotlin/intarray-vs-arrayint)，**我们应该更倾向于使用原始类型数组**。Kotlin在对应的Collection<Primitive_Type>接口上提供了不同的方便的扩展函数，例如Collection<Long\>.toLongArray()：

```kotlin
val myList = listOf(1L, 2L, 3L, 4L, 5L)
val expectedArray = longArrayOf(1L, 2L, 3L, 4L, 5L)
assertThat(myList.toLongArray()).isEqualTo(expectedArray)
```

如我们所见，这些方法使我们能够轻松地将集合转换为原始数组。

## 3. 优先使用List而不是数组

我们已经看到，在Kotlin中将List转换为数组并不是一件难事，此外，我们知道ArrayList和Array是非常相似的数据结构。

那么，可能会出现一个问题：在做Kotlin项目时，List和Array应该如何选择？本节的标题给出了答案：我们应该更倾向于List而不是数组。

接下来，让我们讨论一下为什么我们应该更喜欢List。

与Java相比，Kotlin对Arrays做了相当多的改进-例如，引入原始类型数组、泛型支持等。然而，我们仍然应该更倾向于List/MutableList而不是Array，这是因为：

-   数组是可变的：我们应该尽量减少应用程序中的可变性。所以，不可变的List应该是我们的首选。
-   数组是固定大小的：我们不能在创建数组后调整它的大小。但是，另一方面，向MutableList添加或删除元素非常容易。
-   Array<T\>是不变的：例如，val a : Array<Number\> = Array<Int\>(1) { 42 }不编译(类型不匹配)。但是，List<T\>是协变的：val l: List<Number\> = listOf(42, 42, 42)很好。
-   Lists/MutableLists比数组有更多的功能，性能几乎和数组一样好。

因此，**除非我们面临性能关键的情况，否则优先使用List而不是数组是一种很好的做法**。

## 4. 总结

在本文中，我们学习了如何在Kotlin中将List转换为数组，此外，我们已经讨论了为什么我们在日常使用中应该更喜欢List而不是数组。
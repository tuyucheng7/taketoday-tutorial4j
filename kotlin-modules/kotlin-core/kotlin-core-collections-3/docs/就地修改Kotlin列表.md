## 1. 概述

List是开发人员在日常工作生活中最常用的数据结构之一，有时，**高效地使用**[List](https://www.baeldung.com/kotlin/lists)**可能是极其必要的**，尤其是当它们包含大量元素时。

**就地修改List意味着在我们遍历List的同时更新它的值**，如果我们在List中执行的操作依赖于List的先前元素，这可能很有用。

在本文中，我们将介绍几种可用于有效更新List的方法。

## 2. 可变和不可变List

在Kotlin中，List是可变的或不可变的，**可变List允许我们追加和修改元素**。另一方面，我们无法在创建不可变List后对其进行修改。

由于我们想要进行就地修改，因此我们将在本文中使用可变List。

## 3. 使用迭代器就地修改

我们可以使用迭代器迭代一个集合，MutableList接口包含listIterator()方法，该方法返回一个MutableListIterator类。

**MutableListIterator具有典型的hasNext()和next()方法来遍历List**，但它还包含一个set方法，此方法更改通过next()方法检索到的最后一个元素的值。例如，在下面的代码片段中，我们遍历List并将每个偶数元素替换为数字0：

```kotlin
fun replaceEvenNumbersBy0Iterator(list: MutableList<Int>): MutableList<Int> {
    val iterator = list.listIterator()
    while(iterator.hasNext()) {
        val value = iterator.next()
        if (value % 2 == 0) {
            iterator.set(0)
        }
    }

    return list
}
```

首先，我们使用mutableListOf内置方法创建一个可变List，并通过执行listIterator()方法获取迭代器。

然后，我们简单地验证元素是否是偶数。如果是，我们使用set方法修改检索到的最后一个元素。

## 4. 使用List Setter就地修改

**另一种就地修改List元素的方法是设置特定索引的值**。使用for循环，我们从0迭代到N–1，其中N是List中元素的数量。

然后，我们简单地验证条件，并使用索引号直接在该位置设置值：

```kotlin
fun replaceEvenNumbersBy0Direct(list: MutableList<Int>): MutableList<Int> {
    for (i in 0 until list.size) {
        val value = list[i]
        if (value % 2 == 0) {
            list[i] = 0
        }
    }

    return list
}
```

## 5. 创建扩展方法

在Kotlin中，**可以在不继承或创建装饰器类的情况下扩展类的功能**。这是**通过一个名为extension的函数**的特殊声明来完成的。

我们将在MutableList类上创建一个[扩展方法](https://www.baeldung.com/kotlin/extension-methods)，允许我们对List的每个元素应用操作，该方法将接收一个函数作为参数，我们会将此函数应用于List中的每个元素。

让我们考虑一个例子：

```kotlin
fun <Int> MutableList<Int>.mapInPlace(mutator: (Int) -> (Int)) {
    this.forEachIndexed { i, value ->
        val changedValue = mutator(value)

        this[i] = changedValue
    }
}
```

我们正在为MutableList类中的Int元素创建一个名为mapInPlace的新方法，该方法将在我们的应用程序上下文中可用。这样，我们就可以避免修改MutableList源代码，并可以将其包含在选定的位置。

**该方法需要一个mutator参数，它是将应用于list中每个元素的函数**。

修改器将根据我们的条件验证所提供的List中的元素是否应该更改。让我们看一个增变函数的例子：

```kotlin
fun <Int> MutableList<Int>.mapInPlace(mutator: (Int) -> (Int)) {
    this.forEachIndexed { i, value ->
        val changedValue = mutator(value)

        this[i] = changedValue
    }
}

val list = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
list.mapInPlace {
    element -> if (element % 2 == 0) 0 else element
}
// [1, 0, 3, 0, 5, 0, 7, 0, 9, 0]
println(list)
```

如我们所见，对于List中的每个元素，我们都会验证它是否为偶数，如果是这样，我们返回0，否则，**我们返回没有变化的元素。此返回值用作List的新元素**。

我们还可以使用Kotlin关键字it来访问括号内的元素：

```kotlin
fun <Int> MutableList<Int>.mapInPlace(mutator: (Int) -> (Int)) {
    this.forEachIndexed { i, value ->
        val changedValue = mutator(value)

        this[i] = changedValue
    }
}

val list = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
list.mapInPlace {
     if (it % 2 == 0) 0 else it
}
// [1, 0, 3, 0, 5, 0, 7, 0, 9, 0]
println(list)
```

mapInPlace函数的一个可能改进是在插入元素之前检查元素是否真的发生了变化，**我们可以简单地将mutator函数返回的值与当前元素进行比较**：

```kotlin
fun <Int> MutableList<Int>.mapInPlace(mutator: (Int) -> (Int)) {
    this.forEachIndexed { i, value ->
        val changedValue = mutator(value)

        if (value != changedValue) {
            this[i] = changedValue
        }
    }
}
```

这个函数可以很容易地推广到任何类型的List中：

```kotlin
fun <T> MutableList<T>.mapInPlace(mutator: (T) -> (T)) {
    this.forEachIndexed { i, value ->
        val changedValue = mutator(value)

        if (value != changedValue) {
            this[i] = changedValue
        }
    }
}
```

我们还可以使用Array类的扩展方法：

```kotlin
fun <T> Array<T>.mapInPlace(mutator: (T) -> (T)) {
    this.forEachIndexed { i, value ->
        val changedValue = mutator(value)

        if (value != changedValue) {
            this[i] = changedValue
        }
    }
}

```

## 6. 总结

在本文中，我们了解了如何通过应用不同的方法在遍历List时更新List。
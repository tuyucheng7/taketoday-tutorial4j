## 1. 概述

在本快速教程中，我们将探索如何在Kotlin中的[List](https://www.baeldung.com/kotlin/lists)和[Set](https://www.baeldung.com/kotlin/collections-api#2-set)之间进行转换。

## 2. 问题简介

这个问题看起来很简单，我们知道List和Set都是[Collection](https://www.baeldung.com/kotlin/collections-api)接口的子类型。此外，[Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/)接口是Collection接口的超类型。

标准的Kotlin库为Iterable接口添加了一组方便的[扩展函数](https://www.baeldung.com/kotlin/extension-methods)，以便我们可以轻松地在List和Set之间进行转换，例如toSet()、toHashSet()、toList()等。 

在本教程中，我们将介绍如何在Kotlin中的List和Set之间进行转换。此外，我们还将讨论这些扩展函数之间的区别。

顺便说一句，为简单起见，我们将使用单元测试来验证结果。

## 3. 将List转换为Set

将List转换为Set的**最直接方法是使用具体Set类的构造函数**，例如：

```kotlin
val inputList = listOf("one", "two", "three", "four", "one", "three", "five")
val expectedSet = setOf("one", "two", "three", "four", "five")
assertThat(HashSet(inputList)).isInstanceOf(HashSet::class.java).isEqualTo(expectedSet)
```

上面的代码使用HashSet构造函数将List转换为HashSet，同样，我们也可以根据需要使用它们的构造函数将List转换为TreeSet或LinkedHashSet。

**由于Set仅包含唯一元素，因此List中的重复元素不会添加到Set中**。

值得一提的是，**我们调用构造函数得到的Sets是可变的**。但是，我们可以在需要时[将可变集合转换为不可变集合](https://www.baeldung.com/kotlin/mutable-collection-to-immutable)。

我们之前提到过方便的扩展函数，例如，我们可以通过调用list.toSet()方法将List转换为Set：

```kotlin
assertThat(inputList.toSet()).isInstanceOf(LinkedHashSet::class.java).isEqualTo(expectedSet)
```

眼尖的人可能已经注意到，在上面的断言中，我们验证了转换后的Set对象是一个LinkedHashSet实例，尽管我们调用了toSet()方法。

**这是因为Kotlin的扩展函数Iterator.toSet返回一个不可变的LinkedHashSet实例**。所以，如果我们想要一个可变的LinkedHashSet对象，toMutableSet()是正确的选择。

此外，还有toHashSet()和toSortedSet()方法，它们允许我们获得所需的Set类型。另外，我们需要注意这两个方法返回的Set实例是可变的。

## 4. 将Set转换为List

同样，我们可以使用具体的List类型的构造函数将Set转换为List，例如，我们可以将Set转换为ArrayList：

```kotlin
val inputSet = setOf("one", "two", "three", "four", "five")
val expectedList = listOf("one", "two", "three", "four", "five")
assertThat(ArrayList(inputSet)).isEqualTo(expectedList)
```

当然，我们可以调用不同类型的构造函数来获取所需的List类型，比如LinkedList、ArrayDeque等。

**此外，我们应该注意，我们通过调用其构造函数获得的List对象是可变的**。

我们在上面的测试中通过“isEqualTo”来验证转换后的ArrayList对象，我们知道List的equals方法检查元素的值和顺序，我们的测试通过了，因为setOf()方法返回了一个不可变的LinkedHashSet。也就是说，元素的顺序是固定的。但是，如果我们将HashSet转换为List，则无法保证结果List中元素的顺序。

除了使用构造函数之外，我们还可以使用方便的扩展函数toList()将Set转换为List：

```kotlin
assertThat(inputSet.toList()).isEqualTo(expectedList)
```

**toList方法返回一个不可变的ArrayList对象**，如果我们需要一个可变的ArrayList，我们可以调用toMutableList方法：inputSet.toMutableList()。

## 5. 总结

在本文中，我们学习了如何在Kotlin中进行Set和List之间的转换。
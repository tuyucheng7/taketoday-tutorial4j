## 1. 概述

[List](https://www.baeldung.com/kotlin/lists)是Kotlin中非常常见的集合类型。

在本教程中，我们将学习如何在Kotlin中比较两个List。

## 2. 问题简介

List是元素的有序集合，此外，**List可以包含重复值**。

因此，当我们讨论比较两个List时，根据需要，有几种情况我们认为两个List是相等的：

-   两个List具有相同的大小、包含相同的元素并且具有相同的顺序。
-   两个List具有相同的大小并包含相同的元素，但是，我们不关心元素的顺序。

一个例子可以很快解释它。假设我们有一个targetList：

```kotlin
private val targetList = listOf("one", "two", "three", "four", "five")
```

如上面的代码所示，我们在targetList中有五个元素。

然后，让我们创建更多不同案例的List：

```kotlin
private val listExactlySame = listOf("one", "two", "three", "four", "five")
private val listInDiffSizeButWithSameElements = listOf("one", "two", "three", "four", "five", "five", "five")
private val listInDiffSizeAndElements = listOf("one", "two", "three", "four", "five", "five", "five", "I am a new element")
private val listInDiffOrder = listOf("two", "one", "three", "five", "four")
private val listInDiffElement = listOf("ONE", "two", "three", "four", "FIVE")
```

接下来，让我们构建方法来涵盖我们上面提到的两个比较场景，以确定这些List是否与我们的targetList“相同”。

为简单起见，我们将使用单元测试断言来验证我们的比较方法是否按预期工作。

## 3. 比较元素和顺序

**在Kotlin中，我们使用**[结构相等性](https://www.baeldung.com/kotlin/equality-operators#structural-equality)**(==)来评估两个值是否相同或相等**，这与Java中的equals()方法相同。

因此，如果list1 == list2为真，则两个List具有相同的大小并且包含相同的元素且顺序完全相同。

接下来，让我们用我们的List来测试它：

```kotlin
targetList.let {
    assertThat(listExactlySame == it).isTrue
    assertThat(listInDiffOrder == it).isFalse
    assertThat(listInDiffSizeButWithSameElements == it).isFalse
    assertThat(listInDiffSizeAndElements == it).isFalse
    assertThat(listInDiffElement == it).isFalse
}
```

如上面的代码所示，如果我们使用结构相等来比较两个List，则只有listExactlySame和targetList应该相等。如果我们运行测试，它就会通过。

因此，**==是比较两个List以查看它们是否相等的最直接方法**。

## 4. 忽略顺序比较元素

当我们面对“忽略顺序”的要求时，我们中的一些人可能会想到这样的想法：list1.size() == list2.size() && list1.containsAll(list2) &&list2.containsAll(list1)。

确实，这解决了问题；但是，List的containsAll是一种昂贵的方法，作为List的查找成本O(N)。**因此，假定两个List具有相同的大小，list1.containsAll(list2)的成本是O(N^2)**。

为了获得更好的性能，我们可以使用[Set](https://www.baeldung.com/kotlin/collections-api#2-set)来解决问题，HashSet上的查找函数仅花费O(1)。因此，我们可以通过先将[两个List都转换为HashSet](https://www.baeldung.com/kotlin/convert-list-to-set#converting-a-list-into-a-set)，然后检查两个集合的结构是否相等来解决问题，总成本将为O(N)。

既然我们知道该怎么做，那么实现就不会是一个挑战了：

```kotlin
fun <T> equalsIgnoreOrder(list1:List<T>, list2:List<T>) = list1.size == list2.size && list1.toSet() == list2.toSet()
```

**值得一提的是，Kotlin的List.toSet()函数返回一个LinkedHashSet对象，它是HashSet的子类型**。

实现非常简单，但是，为了使函数调用流畅，我们可以在List上创建一个[扩展函数](https://www.baeldung.com/kotlin/extension-methods)：

```kotlin
fun <T> List<T>.equalsIgnoreOrder(other: List<T>) = this.size == other.size && this.toSet() == other.toSet()
```

这样，函数调用将类似于list1.equalsIgnoreOrder(list2)。

此外，**由于扩展函数只有一个参数，我们可以添加**[中缀符号](https://www.baeldung.com/kotlin/infix-functions)**使函数调用更易于阅读**：

```kotlin
infix fun <T> List<T>.equalsIgnoreOrder(other: List<T>) = this.size == other.size && this.toSet() == other.toSet()

if (list1 equalsIgnoreOrder list2) ....
```

现在，让我们使用示例List对其进行测试：

```kotlin
targetList.let {
    assertThat(listExactlySame equalsIgnoreOrder it).isTrue
    assertThat(listInDiffOrder equalsIgnoreOrder it).isTrue
    assertThat(listInDiffSizeButWithSameElements equalsIgnoreOrder it).isFalse
    assertThat(listInDiffSizeAndElements equalsIgnoreOrder it).isFalse
    assertThat(listInDiffElement equalsIgnoreOrder it).isFalse
}
```

如上面的断言所示，如果我们忽略元素的顺序，listInDiffOrder和listExactlySame应该等于我们的targetList。

当我们执行它时，测试通过了；因此，我们的equalsIgnoreOrder函数按预期工作。

## 5. 总结

在本文中，我们探讨了如何在检查或不检查元素顺序的情况下比较两个List对象。

此外，我们还了解到使用Kotlin的扩展函数和中缀符号有助于编写易于阅读的代码。
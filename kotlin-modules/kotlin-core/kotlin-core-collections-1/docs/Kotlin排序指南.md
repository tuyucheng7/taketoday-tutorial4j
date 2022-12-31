## 1. 概述

Kotlin使用[扩展方法](https://www.baeldung.com/kotlin-extension-methods)建立在Java Collection框架之上，这极大地提高了可用性和可读性，而无需第三方依赖项，例如[Apache Commons](https://www.baeldung.com/apache-commons-collection-utils)或[Guava](https://www.baeldung.com/guava-collections)。

在本教程中，我们将重点介绍Kotlin中的排序。此外，我们将使用kotlin.comparisons包来实现复杂的排序规则。

## 2. 对集合进行排序

Kotlin提供了多个实用程序来简化集合排序的过程，让我们探讨其中的几种方法。

### 2.1 sort

**对集合进行排序的最简单方法是调用sort方法**，此方法将使用元素的自然顺序。此外，默认情况下它会按升序排列，因此“a”在“b”之前，“1”在“2”之前：

```kotlin
val sortedValues = mutableListOf(1, 2, 7, 6, 5, 6)
sortedValues.sort()
println(sortedValues)
```

上面代码的结果是：

```powershell
[1, 2, 5, 6, 6, 7]
```

重要的是要注意我们使用了可变集合，**原因是sort方法会原地排序**。如果我们希望结果作为一个新列表返回，那么我们只需要使用sorted方法来代替。

此外，我们可以使用sortDescending或reverse方法进行降序排序。

### 2.2 sortBy

如果我们需要按给定对象的特定属性进行排序，我们可以使用sortBy。**sortBy方法允许我们将选择器函数作为参数传递**，选择器函数将接收对象并返回我们想要排序的值：

```kotlin
val sortedValues = mutableListOf(1 to "a", 2 to "b", 7 to "c", 6 to "d", 5 to "c", 6 to "e")
sortedValues.sortBy { it.second }
println(sortedValues)
```

上面代码的结果是：

```powershell
[(1, a), (2, b), (7, c), (5, c), (6, d), (6, e)]
```

同样，集合需要是可变的，因为sortBy方法将就地排序。如果我们希望结果作为一个新列表返回，那么我们需要使用sortedBy方法而不是sortBy方法。

和以前一样，对于降序，我们可以使用sortByDescending或reverse方法。

### 2.3 sortWith

**对于更高级的用法(例如组合多个规则)，我们可以使用sortWith方法**。

我们可以将Comparator对象作为参数传递。在Kotlin中，我们有多种方法来创建Comparator对象，我们将在下一节中介绍：

```kotlin
val sortedValues = mutableListOf(1 to "a", 2 to "b", 7 to "c", 6 to "d", 5 to "c", 6 to "e")
sortedValues.sortWith(compareBy({it.second}, {it.first}))
println(sortedValues)
```

而上面代码的结果是先按字母排序，再按数字排序：

```powershell
[(1, a), (2, b), (5, c), (7, c), (6, d), (6, e)]
```

因为sortWith会就地进行排序，所以我们需要使用可变集合。如果我们希望结果作为一个新集合返回，那么我们需要使用sortedWith方法而不是sortWith方法。

对于降序，我们可以使用reverse方法或者定义正确的Comparator。

## 3. 比较

Kotlin包含一个非常有用的包来构建比较器—Kotlin.comparisons，在以下部分中，我们将讨论：

-   比较器创建
-   空值的处理
-   颠倒顺序
-   比较器规则扩展

### 3.1 比较器创建

为了简化我们Comparator的创建，Kotlin带来了很多工厂方法来让我们的代码更具表现力。

可用的最简单的比较器工厂是naturalOrder()，不需要参数，默认情况下顺序是升序的：

```kotlin
val ascComparator = naturalOrder<Long>()
```

对于具有多个属性的对象，我们可以使用compareBy方法。作为参数，我们给出了可变数量的函数(排序规则)，每个函数都将返回一个Comparable对象。然后，将依次调用这些函数，直到结果Comparable对象的计算结果不相等或调用所有函数为止。

在下一个示例中，it.first值用于比较，只有当值相等时，才会调用it.second来打破平局：

```kotlin
val complexComparator = compareBy<Pair<Int, String?>>({it.first}, {it.second})
```

随意探索kotlin.comparisons以发现所有可用的工厂。

### 3.2 空值的处理

使用空值处理改进我们的Comparator的一种简单方法是使用nullsFirst或nullsLast方法，这些方法将分别在第一位或最后一位对空值进行排序：

```kotlin
val sortedValues = mutableListOf(1 to "a", 2 to null, 7 to "c", 6 to "d", 5 to "c", 6 to "e")
sortedValues.sortWith(nullsLast(compareBy { it.second }))
println(sortedValues)
```

上述代码的结果将是：

```powershell
[(1, a), (7, c), (5, c), (6, d), (6, e), (2, null)]
```

我们可以看到结果集合中的最后一个值是空值。

### 3.3 反转顺序

要反转顺序，我们可以使用reverseOrder方法或reversed方法，前一种方法没有参数并返回降序，后一种方法可以应用于Comparator对象，它将返回其反转的Comparator对象。

要使用降序自然顺序构建比较器，我们可以这样做：

```powershell
reverseOrder()
```

### 3.4 比较器规则扩展

比较器对象可以通过kotlin.comparable包中可用的then方法与其他排序规则组合或扩展。

只有当第一个比较器的计算结果相等时，才会使用第二个比较器。

我们的学生名单包含每个人的年龄和姓名，我们希望他们从小到大排序，当他们年龄相同时，根据名字降序排列：

```kotlin
val students = mutableListOf(21 to "Helen", 21 to "Tom", 20 to "Jim") 

val ageComparator = compareBy<Pair<Int, String?>> {it.first}
val ageAndNameComparator = ageComparator.thenByDescending {it.second}
println(students.sortedWith(ageAndNameComparator))
```

上述代码的结果将是：

```powershell
[(20, Jim), (21, Tom), (21, Helen)]
```

## 4. 总结

在本快速教程中，我们了解了如何使用sort、sortBy和sortWith方法在Kotlin中对集合进行排序。

后来，我们还使用kotlin.comparisons包来创建Comparator对象并使用额外的排序规则来增强它们。
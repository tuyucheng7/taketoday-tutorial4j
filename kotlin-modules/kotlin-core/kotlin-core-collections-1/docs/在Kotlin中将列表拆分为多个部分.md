## 1. 简介

假设我们有一个像[a, b, c, d, e, f]这样的数组，我们想把元素分成不同的组，如[[a, b], [c, d], [e, f]]或[[a, b, c], [d], [e,f]]。

**在本教程中，我们将在检查Kotlin的groupBy、chunked和windowed之间的一些差异的同时实现这一点**。

## 2. 将列表拆分成对列表

对于我们的示例，我们将使用两个列表，一个包含偶数个元素，一个包含奇数个元素：

```kotlin
val evenList = listOf(0, "a", 1, "b", 2, "c")
val unevenList = listOf(0, "a", 1, "b", 2, "c", 3)
```

显然，我们可以将evenList精确地分成三对。然而，我们的unevenList将有一个额外的元素。

在本节的其余部分，我们将看到拆分两个列表的各种实现，包括它们如何处理unevenList中的额外元素。

### 2.1 使用groupBy

首先，让我们使用groupBy实现一个解决方案，**我们将创建一个包含升序数字的列表，并使用groupBy来拆分它们**：

```kotlin
val numberList = listOf(1, 2, 3, 4, 5, 6)
numberList.groupBy { (it + 1) / 2 }.values
```

这给出了期望的结果：

```kotlin
[[1, 2], [3, 4], [5, 6]]
```

它是如何工作的？好吧，groupBy在每个元素上执行提供的函数(it + 1) / 2 ：

-   (1 + 1) / 2 = 1
-   (2 + 1) / 2 = 1.5，四舍五入为 1
-   (3 + 1) / 2 = 2
-   (4 + 1) / 2 = 2.5，四舍五入为 2
-   (5 + 1) / 2 = 3
-   (6 + 1) / 2 = 3.5，四舍五入为 3

然后，groupBy对列表中给出相同结果的元素进行分组。

现在，当我们对一个不均匀的列表做同样的事情时：

```kotlin
val numberList = listOf(1, 2, 3, 4, 5, 6, 7)
numberList.groupBy { (it + 1) / 2 }.values
```

我们得到所有的对和一个额外的元素：

```kotlin
[[1, 2], [3, 4], [5, 6], [7]]
```

但是，**如果我们更进一步地使用一些随机数**：

```kotlin
val numberList = listOf(1, 3, 8, 20, 23, 30)
numberList.groupBy { (it + 1) / 2 }.values
```

**我们会得到一些完全不需要的东西**：

```kotlin
[[1], [3], [8], [20], [23], [30]]
```

原因很简单；对每个元素应用(it + 1) / 2函数给出：1, 2, 4, 10, 12, 15。所有结果都不同，因此没有元素组合在一起。

当我们使用evenList或unevenList时，情况更糟—**代码无法编译**，因为该函数无法应用于字符串。

### 2.2 使用groupBy和withIndex

真的，如果我们想将任意列表成对分组，**我们不想通过函数修改值，而是修改索引**：

```kotlin
evenList.withIndex()
    .groupBy { it.index / 2 }
    .map { it.value.map { it.value } }
```

这将返回我们想要的对列表：

```kotlin
[[0, "a"], [1, "b"], [2, "c"]]
```

此外，如果我们使用unevenList，我们甚至可以得到单独的元素：

```kotlin
[[0, "a"], [1, "b"], [2, "c"], [3]]
```

### 2.3 使用groupBy和foldIndexed

我们可以更进一步，而不仅仅是使用index，并使用foldIndexed进行更多编程以节省一些分配：

```kotlin
evenList.foldIndexed(ArrayList<ArrayList<Any>>(evenList.size / 2)) { index, acc, item ->
    if (index % 2 == 0) {
        acc.add(ArrayList(2))
    }
    acc.last().add(item)
    acc
}
```

虽然有点冗长，但**foldIndexed解决方案只是对每个元素执行操作**，而withIndex函数首先创建一个迭代器并包装每个元素。

### 2.4 使用chunked

但是，**我们可以使用chunked更优雅地做到这一点**。因此，让我们将该方法应用于我们的evenList：

```kotlin
evenList.chunked(2)
```

evenList为我们提供了我们想要的对：

```kotlin
[[0, "a"], [1, "b"], [2, "c"]]
```

而unevenList为我们提供了对和额外的元素：

```kotlin
[[0, "a"], [1, "b"], [2, "c"], [3]]
```

### 2.5 使用windowed

**chunked效果非常好，但有时我们需要更多的控制**。

例如，我们可能需要指定是否只需要对，或者是否需要包含额外的元素。**windowed方法为我们提供了一个partialWindows布尔值**，它指示我们是否需要部分结果。

默认情况下，partialWindows为false。因此，以下语句产生相同的结果：

```kotlin
evenList.windowed(2, 2)
unevenList.windowed(2, 2, false)
```

两者都返回没有单独元素的列表：

```kotlin
[[0, "a"], [1, "b"], [2, "c"]]
```

最后，当我们将partialWindows设置为true以包含部分结果时：

```kotlin
unevenList.windowed(2, 2, true)
```

我们将获得对列表加上单独的元素：

```kotlin
[[0, "a"], [1, "b"], [2, "c"], [3]]
```

## 3. 总结

使用groupBy是一个很好的编程练习，但它可能很容易出错，一些错误可以简单地通过使用索引来解决。

为了优化代码，我们甚至可以使用foldIndexed，但是，这会产生更多的代码。幸运的是，chunked方法为我们提供了开箱即用的相同功能。

此外，windowed方法提供了额外的配置选项。如果可能，最好使用chunked方式，如果我们需要额外的配置，我们应该使用windowed方式。
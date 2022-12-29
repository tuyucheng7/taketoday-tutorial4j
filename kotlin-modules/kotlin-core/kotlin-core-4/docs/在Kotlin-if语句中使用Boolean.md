## 1. 概述

[空安全](https://www.baeldung.com/kotlin/null-safety)是Kotlin的内置功能，它使我们能够全面处理可为空的值。

在本教程中，我们将探索如何在if语句中检查可为null的布尔值(Boolean?)。

## 2. 问题简介

在Kotlin中，没有Java中的int或boolean等原始数据类型。相反，我们有“包含”的原始类型，如Int和Boolean。

Kotlin的可空布尔类型Boolean?与Java的布尔类型非常相似，两者都可能具有true、false或null值。

例如，如果我们想在if语句中测试Java的布尔类型，我们会这样做：

```java
Boolean b = ...;
if (b != null && b) {
   /* Do something when if b is true */
} else {
   /* Do something if b is false or null */
}
```

现在，我们可以将它“翻译”成Kotlin：

```kotlin
val b: Boolean? = ...
if (b != null && b) {
    ...
} else {
    ...
}
```

它就像b不为空一样工作-**Kotlin巧妙地转换Boolean?到Boolean**。

但是，**如果我们使用**[var](https://www.baeldung.com/kotlin/const-var-and-val-keywords)**将布尔变量声明为可变变量，则智能转换将不起作用**：

```kotlin
var b: Boolean? = ...
if (b != null && b) {
    /* ^ compilation error: 
         Kotlin: Smart cast to 'Boolean' is impossible, 
                   because 'b' is a mutable property that could have been changed by this time
    */
    ...
```

我们可以快速修复if语句：if(b != null && b!!)。但是，b != null && b!!看起来有点笨拙，不容易阅读。

那么接下来，让我们看看如何检查Boolean?以更好的方式输入if语句。

## 3. 比较一个值

检查if语句中可为空的布尔变量的一种直接方法**是将变量与预期值进行比较**。

一个例子可以快速解释这一点：

```kotlin
var b: Boolean? = ...
if (b == true) {
    /* Do something when if b is true */
} else {
    /* Do something if b is false or null */
}
```

上面的检查将处理路由到两个分支：“true”和“false or null”情况，与“b != null && b!!"相比，"b == true"更容易理解。

然而，有时，**我们希望单独处理三种情况(true/false/null)**，那么当然我们可以写一些类似if-else if-else的逻辑来实现。在Kotlin中，**执行此操作的一种惯用方法是使用**[when](https://www.baeldung.com/kotlin/when)**表达式**：

```kotlin
var b: Boolean? = null
when (b) {
    true -> ...
    false -> ...
    else -> ...
}
```

到目前为止，我们已经学会了如何惯用地检查Kotlin中的Boolean?类型变量。

现在，让我们向前迈出一步。在实践中，我们常常希望依赖于一个布尔变量来做一些工作，比如上面的例子。如果我们经常需要在应用程序中执行这些例程，我们可以创建一些函数来简化我们的实现。

接下来，让我们看看它们的实际效果。

## 4. 创建一些内联函数

通常，在我们检查一个布尔变量后，后续的处理可能分为两类：

-   If-Then-Do：做一些事情而不关心执行的返回值，例如：if (b == true) println(“it's true”)
-   If-Then-Get：做一些事情并获取返回值，例如：var result :Int? = if (b == true) 5 + 7 else null

接下来，让我们创建一些函数来简化这些实现。

### 4.1 创建If-Then-Do函数

我们先来看一下不关心执行返回值的场景，我们可以创建三个函数来简化If-Then-Do例程的实现：

```kotlin
inline fun doIfTrue(b: Boolean?, block: () -> Unit) {
    if (b == true) block()
}

inline fun doIfFalse(b: Boolean?, block: () -> Unit) {
    if (b == false) block()
}

inline fun doIfFalseOrNull(b: Boolean?, block: () -> Unit) {
    if (b != true) block()
}
```

正如我们在上面的代码中看到的，我们已经声明了三个函数，每个函数接收两个参数，一个可空布尔值和一个[高阶函数](https://www.baeldung.com/kotlin/interfaces-higher-order-functions)块。

**由于我们想忽略block函数的返回值，因此我们定义了block来返回**[Unit](https://www.baeldung.com/kotlin/void-type#unit_in_kotlin)**类型**。

如果我们仔细研究这些函数，它们的实现对我们来说并不陌生。我们比较Boolean?变量为所需的布尔值，如果满足条件，我们将执行块函数。

值得一提的是，**我们已将它们声明为**[内联函数](https://www.baeldung.com/kotlin/inline-functions)**以获得更好的性能**。

这些函数允许我们以这种形式编写If-Then-Do例程：

```kotlin
var b: Boolean? = ...
doIfTrue(b) {
    doThis()
    doThat()
}
```

### 4.2 测试If-Then-Do函数

现在，让我们编写一个测试来验证我们的If-Then-Do函数是否按预期工作：

```kotlin
val aList = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8)

doIfTrue(true) { aList[0] = -1 }; assertThat(aList[0]).isEqualTo(-1)
doIfTrue(false) { aList[1] = -1 }; assertThat(aList[1]).isEqualTo(1)
doIfTrue(null) { aList[2] = -1 }; assertThat(aList[2]).isEqualTo(2)

doIfFalse(true) { aList[3] = -1 }; assertThat(aList[3]).isEqualTo(3)
doIfFalse(false) { aList[4] = -1 }; assertThat(aList[4]).isEqualTo(-1)
doIfFalse(null) { aList[5] = -1 }; assertThat(aList[5]).isEqualTo(5)

doIfFalseOrNull(true) { aList[6] = -1 }; assertThat(aList[6]).isEqualTo(6)
doIfFalseOrNull(false) { aList[7] = -1 }; assertThat(aList[7]).isEqualTo(-1)
doIfFalseOrNull(null) { aList[8] = -1 }; assertThat(aList[8]).isEqualTo(-1)
```

我们要测试三个函数，每个函数有三个不同的测试用例：true、false和null，如上面的测试函数所示，为简单起见，我们准备了一个包含9个元素的可变列表。

我们传递给每个测试用例的块函数正在更新列表中唯一索引处的元素，函数调用后，我们验证列表中的相应元素是否得到更新。

当我们运行它时，测试就会通过。因此，我们的函数按预期工作。

### 4.3 创建If-Then-Get函数

现在我们了解了If-Then-Do函数，If-Then-Get函数非常相似，唯一的区别是块函数现在返回泛型R类型：

```kotlin
inline fun <R> getIfTrue(b: Boolean?, block: () -> R): R? {
    return if (b == true) block() else null
}

inline fun <R> getIfFalse(b: Boolean?, block: () -> R): R? {
    return if (b == false) block() else null
}

inline fun <R> getIfFalseOrNull(b: Boolean?, block: () -> R): R? {
    return if (b != true) block() else null
}
```

如上面的函数所示，这里，为了简单起见，如果条件不匹配，我们将返回一个空值。在实践中，我们可以根据实际需要进行调整。

例如，如果需要，我们可以使块函数返回可为空的R(R?)类型。此外，我们可以再添加一个参数作为“else”情况的默认值：

```kotlin
inline fun <R> getIfTrue(b: Boolean?, orElse: R? = null, block: () -> R?): R? {
    return if (b == true) block() else orElse
} 
```

例如，我们可以这样使用这些函数：

```kotlin
var b: Boolean? = ...
val someValue: Int? = getIfTrue(b){
    val a = calcA()
    val b = calcB(a)
    return a + b
}
```

### 4.4 测试If-Then-Get函数

我们再次使用预定义列表测试If-Then-Get函数。

我们调用我们的函数以通过唯一索引从列表中获取元素并验证结果是否符合预期：

```kotlin
val aList = listOf("one", "two", "three")
assertThat(getIfTrue(true) { aList[0] }).isEqualTo("one")
assertThat(getIfTrue(false) { aList[0] }).isNull()
assertThat(getIfTrue(null) { aList[0] }).isNull()

assertThat(getIfFalse(true) { aList[1] }).isNull()
assertThat(getIfFalse(false) { aList[1] }).isEqualTo("two")
assertThat(getIfFalse(null) { aList[1] }).isNull()

assertThat(getIfFalseOrNull(true) { aList[2] }).isNull()
assertThat(getIfFalseOrNull(false) { aList[2] }).isEqualTo("three")
assertThat(getIfFalseOrNull(null) { aList[2] }).isEqualTo("three")
```

如果我们执行上面的测试，它就会通过。

## 5. 总结

在这篇文章中，我们探讨了如何惯用地检查Boolean?输入if语句，我们还了解到，有时，Kotlin的when表达式可以使我们的代码更易于阅读。

此外，我们还看到了一些方便的内联辅助函数，可以流畅地编写If-Then-Do和If-Then-Get例程。
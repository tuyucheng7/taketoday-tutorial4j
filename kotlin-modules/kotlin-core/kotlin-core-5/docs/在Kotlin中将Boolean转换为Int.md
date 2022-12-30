## 1. 概述

布尔值是一种基本数据类型，它只能有true值和false值。

在本快速教程中，我们将探讨如何将Boolean实例转换为Int，例如，将true转换为1并将false转换为0。

## 2. 问题介绍

有时，将Boolean实例转换为Int可能会简化一些计算。例如，给定一个布尔对象列表，我们想计算列表中有多少个“true”。

如果我们可以将所有“true”转换为1，将“false”转换为0，我们可以通过调用Int列表上的sum()来快速获取“true”的数量。

接下来，我们将看到两种进行转换的方法。此外，为简单起见，我们将使用单元测试断言来验证结果。

## 3. 使用Kotlin扩展

实现Boolean到Int的转换逻辑对我们来说不是挑战，我们可以很容易地创建一个辅助函数来做到这一点：

```kotlin
fun booleanToInt(b: Boolean) = if (b) 1 else 0
```

但是，这可能会破坏函数调用的流畅性。例如，假设布尔参数b是使用其他复杂函数调用计算的，下面是调用此booleanToInt()函数的一种方式：

```kotlin
booleanToInt(complex().calls().to().getBoolean())
```

或者，我们可以通过变量调用它：

```kotlin
val b = complex().calls().to().getBoolean()
booleanToInt(b)
```

Kotlin的一项重要功能是[Kotlin扩展](https://kotlinlang.org/docs/extensions.html)，**它允许我们通过添加新功能来扩展类或接口，而无需继承它们**。此外，扩展使代码更易于阅读。

例如，我们可以通过添加一个[扩展函数](https://www.baeldung.com/kotlin/extension-methods)来扩展标准的Boolean类：

```kotlin
fun Boolean.toInt() = if (this) 1 else 0
```

现在，我们可以流畅地调用toInt()函数：complex().calls().to().getBoolean().toInt()。

接下来，让我们编写一个简单的测试，看看它是否按预期工作：

```kotlin
assertThat(true.toInt()).isEqualTo(1)
assertThat(false.toInt()).isEqualTo(0)
```

毫不奇怪，如果我们运行测试，测试就会通过。

除了扩展函数，Kotlin还允许我们创建[扩展属性](https://kotlinlang.org/docs/extensions.html#extension-properties)；那么接下来，让我们为Boolean类添加一个新的属性intValue：

```kotlin
val Boolean.intValue
    get() = if (this) 1 else 0
```

然后，我们可以像访问常规属性一样访问intValue属性：

```kotlin
assertThat(true.intValue).isEqualTo(1)
assertThat(false.intValue).isEqualTo(0)
```

从示例中我们可以看出，使用扩展可以使我们的代码更易于阅读。

## 4. 使用compareTo()函数

可能我们很多人都没有注意到**Boolean类实现了Comparable接口**：

```kotlin
public class Boolean private constructor() : Comparable<Boolean> { ... }
```

这意味着我们可以比较两个布尔对象。在Kotlin中，我们甚至可以使用“大于(>)”或“小于(<)”运算符来比较两个布尔值，尽管我们很少这样做：

```kotlin
assertThat(true > false).isTrue
```

上面的测试通过，因此，**在Kotlin中，true“大于”false**。

此外，我们可以使用b.compareTo(false)将b对象转换为Int。**如果b值为true，b.compareTo(false)将返回1，否则，该方法返回0。通过这种方式，我们将布尔值b转换为整数**。

让我们创建一个测试来检查它是否按预期工作：

```kotlin
assertThat(true.compareTo(false)).isEqualTo(1)
assertThat(false.compareTo(false)).isEqualTo(0)
```

如果我们运行，测试就会通过。因此，布尔值被转换为预期的整数。

敏锐的眼睛可能会发现我们将布尔值与false进行了比较，我们可能会问，为什么我们不将其与true进行比较呢？这是因为，假设a和b是两个布尔对象，a.compareTo(b)遵循以下规则：

-   a>b：它返回1
-   a=b：它返回0
-   a<b：它返回-1

正如我们所提到的，true > false，因此如果我们与较小的值(false)进行比较，结果将为0或1。

但是，如果我们将布尔值与true进行比较，比如b.compareTo(true)，结果将是0或-1：

```kotlin
assertThat(true.compareTo(true)).isEqualTo(0)
assertThat(false.compareTo(true)).isEqualTo(-1)
```

b.compareTo(false)方法可能不像之前提到的Kotlin扩展那么简单，但是，我们不需要添加额外的函数来进行转换。

## 5. 总结

在本文中，我们通过示例学习了两种将Boolean对象转换为Int的方法。

首先，我们已经解决了Kotlin的扩展方式来解决问题，它可以使函数调用流畅也易于阅读。然而，在每个项目中为这个任务编写扩展可能有点乏味，因此，我们讨论了另一种有趣的方法：使用compareTo(false)方法将Boolean转换为Int。
## 1. 概述

在这个快速教程中，我们将了解如何初始化不带元素的可变List、Map和Set。

## 2. 空可变集合

在Kotlin中，使用几个元素初始化一个非空集合很容易。例如，这里我们用两个String初始化一个[MutableSet](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/)：

```kotlin
val colors = mutableSetOf("Red", "Green")
```

如上所示，**编译器将从传递的元素中推断出Set的组件类型**。初始化后，我们也可以添加更多元素：

```kotlin
colors += "Blue"
```

另一方面，**要初始化一个没有元素的MutableSet，我们应该以某种方式传递泛型类型信息，否则，编译器将无法推断组件类型**。例如，要初始化一个空的MutableSet：

```kotlin
val colorSet = mutableSetOf<String>()
```

在这里，我们将泛型类型信息传递给函数本身。此外，**我们可以显式声明类型注释来实现相同的目的**：

```kotlin
val colorSetTagged: MutableSet<String> = mutableSetOf()
```

同样，我们也可以定义空的可变List和Map：

```kotlin
val map = mutableMapOf<String, String>()
val list = mutableListOf<String>()
```

同样，我们可以使用显式类型注释：

```kotlin
val mapTagged: MutableMap<String, String> = mutableMapOf()
val listTagged: MutableList<String> = mutableListOf()
```

当我们显式声明变量类型时，我们不需要将泛型信息传递给函数。

## 3. 总结

在这个简短的教程中，我们看到了如何定义没有元素的可变集合。
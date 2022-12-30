## 1. 概述

在本教程中，我们将讨论在[Kotlin](https://www.baeldung.com/kotlin)中比较String的不同方法。

## 2. 比较运算符

让我们从“==”运算符开始。

我们可以用它来检查两个字符串在结构上是否相等，**这相当于**[在Java中使用equals](https://www.baeldung.com/java-compare-strings)**方法**：

```kotlin
val first = "kotlin"
val second = "kotlin"
val firstCapitalized = "KOTLIN"
assertTrue { first == second }
assertFalse { first == firstCapitalized }
```

现在，让我们考虑引用相等运算符“===”，如果两个变量指向同一个对象，则返回true，**这相当于在Java中使用==**。

每当我们使用引号初始化一个新的String对象时，它都会自动放入字符串池中，因此，以这种方式创建的两个相等的字符串将始终引用同一个对象：

```kotlin
assertTrue { first === second }
```

但是，如果我们使用构造函数创建一个新的字符串，我们就明确地告诉Kotlin我们想要一个新对象，因此，将创建一个新的字符串并将其放入堆中：

```kotlin
val third = String("kotlin".toCharArray())
assertTrue { first == third }
assertFalse { first === third }
```

## 3. 与equals比较

**equals方法返回与“==”运算符相同的结果**：

```kotlin
assertTrue { first.equals(second) }
assertFalse { first.equals(firstCapitalized) }
```

**当我们想要进行不区分大小写的比较时，我们可以使用equals方法并为第二个可选参数ignoreCase传递true**：

```kotlin
assertTrue { first.equals(firstCapitalized, true) }
```

## 4. 与compareTo比较

Kotlin还有一个compareTo方法，我们可以用它来比较两个字符串的顺序。同样，与equals方法一样，compareTo方法也带有一个可选的ignoreCase参数：

```kotlin
assertTrue { first.compareTo(second) == 0 }
assertTrue { first.compareTo(firstCapitalized) == 32 }
assertTrue { firstCapitalized.compareTo(first) == -32 }
assertTrue { first.compareTo(firstCapitalized, true) == 0 }
```

compareTo方法对相等的字符串返回零，如果参数的ASCII值较小则返回正值，如果参数的ASCII值较大则返回负值。在某种程度上，我们可以像阅读减法一样阅读它。

在最后一个示例中，由于ignoreCase参数，两个字符串被认为是相等的。

## 5. 总结

在这篇简短的文章中，我们使用一些基本示例了解了在Kotlin中比较字符串的不同方法。
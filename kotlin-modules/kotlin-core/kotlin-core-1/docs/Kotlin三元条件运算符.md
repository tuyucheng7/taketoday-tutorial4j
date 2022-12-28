## 1. 概述

简而言之，**Kotlin中没有三元运算符**，但是，使用if和when表达式有助于填补这一空白。

在本教程中，我们将研究几种不同的方法来模拟三元运算符。

## 2. if和when表达式

与其他语言不同，**Kotlin中的if和when是表达式，可以将此类表达式的结果分配给一个变量**。

利用这个事实，if和when都可以用它们自己的方式代替三元运算符。

### 2.1 使用if-else

让我们看一下如何使用if表达式来模拟三元运算符：

```kotlin
val result = if (a) "yes" else "no"
```

在上面的表达式中，如果a设置为true，我们的result变量将设置为yes。否则，它被设置为no。

值得注意的是，**result类型取决于右侧的表达式**，通常，类型是Any。例如，如果右侧是布尔类型，则result也将是布尔值：

```kotlin
val result: Boolean = if (a == b) true else false
```

### 2.2 使用when

我们还可以使用when表达式来创建伪三元运算符：

```kotlin
val result = when(a) {
    true -> "yes"
    false -> "no"
}
```

代码简单、直接且易于阅读。如果a为true，则将result指定为yes。否则，将其分配为no。

### 2.3 ?:运算符

有时，我们使用if表达式在它不为null时提取一些值，或者在它为null时返回默认值：

```kotlin
val a: String? = null
val result = if (a != null) a else "Default"
```

也可以对when表达式执行相同的操作：

```kotlin
val result = when(a) {
    null -> "Default"
    else -> a
}
```

由于这是一种非常常见的模式，Kotlin为其提供了一个特殊的运算符：

```kotlin
val result = a ?: "Default"
```

?:被称为[Elvis运算符](https://kotlinlang.org/docs/reference/null-safety.html#elvis-operator)，如果操作数不为null，它返回操作数。否则，它将返回在?:运算符右侧指定的默认值。

### 2.4 DSL

创建模仿三元运算符的DSL肯定是一种诱惑。但是，Kotlin的语言限制[过于严格，无法100%再现传统的三元语法](https://victor.kropp.name/blog/kotlin-dsls-good-bad-and-ugly/)。

因此，让我们避免这种诱惑并简单地使用前面提到的解决方案之一。

## 3. 总结

虽然Kotlin没有三元运算符，但我们可以使用一些替代方案(if和when)，正如我们之前看到的，它们不是语法糖，而是完整的表达式。
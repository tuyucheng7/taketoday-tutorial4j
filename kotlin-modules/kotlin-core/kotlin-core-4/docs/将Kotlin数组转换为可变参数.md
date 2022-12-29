## 1. 简介

正如我们所知，我们可以向方法参数添加一个[vararg](https://kotlinlang.org/docs/functions.html#variable-number-of-arguments-varargs)参数修饰符，它允许我们将一个或多个值传递给该方法。

但是，这些值必须单独传递。这也意味着**不允许按原样为可变参数传递数组**。

让我们看看如何解决这个问题并将Kotlin数组转换为可变参数。

## 2. 扩展运算符

在Kotlin中使用扩展(*)运算符，我们可以将一个原始数组解压缩为其元素。

这开启了将数组间接传递给需要可变参数的函数的可能性。

### 2.1 一个简单的例子

让我们举个例子：

```kotlin
private fun concat(vararg strings: String): String {
    return strings.fold("") { acc, next -> "$acc$next" }
}
```

在这里，我们有一个简单的函数，它接收多个字符串并将它们拼接起来。

**我们可以使用扩展运算符将数组传递给此函数**：

```kotlin
val strings = arrayOf("ab", "cd")
assertTrue { concat(*strings) == "abcd" }
```

请注意，我们只能将类型化数组与扩展运算符一起使用。但是，我们始终可以将其他集合转换为类型化数组，并将它们与扩展运算符一起使用：

```java
val listOfStrings = listOf("ab", "cd")
assertTrue { concat(*listOfStrings.toTypedArray()) == "abcd" }
```

### 2.2 参数顺序

有趣的是，与Java不同，我们可以**将可变参数放在Kotlin中的任何位置**，它不必是函数中的最后一个参数。

但是，这样的函数应该具有非vararg参数的默认值，如果这是不可能的，我们可以改为使用命名参数调用它们：

```kotlin
private fun concat2(vararg strings: String, initialValue: String = "01"): String {
    return strings.fold(initialValue) { acc, next -> "$acc$next" }
}
private fun concat3(vararg strings: String, initialValue: String): String {
    return strings.fold(initialValue) { acc, next -> "$acc$next" }
}
assertTrue { concat2(*strings) == "01abcd" }
assertTrue { concat3(strings = *strings, initialValue = "01") == "01abcd" }
```

### 2.3 组合多个点差操作

与在Java中一样，一个函数只能有一个可变参数。

虽然，**可以将多个数组传递给单个可变参数**：

```kotlin
assertTrue { concat(*strings, "ef", *moreStrings) == "abcdefghij" }
```

## 3. 总结

有多种方法可以将Kotlin数组转换为可变参数。首先，我们可以简单地将一个带有扩展运算符的数组传递给函数调用，也可以传递多个数组。此外，我们甚至可以将文字和数组组合到带有可变参数的函数中。
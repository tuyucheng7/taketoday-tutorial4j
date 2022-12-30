## 1. 概述

在本教程中，我们将了解一些检查字符串是否为数字的常用方法。首先，我们将讨论解析方法和使用正则表达式。

最后，我们将看看另一种只检测正整数的方法。

## 2. 通过解析为Double或Int

检查字符串是否为数字的一种方法是**将其解析为Double或Int**(或其他[内置数字类型](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/#inheritors))，如果此解析尝试不返回null，我们可以安全地假设String是一个数字：

```kotlin
fun isNumericToX(toCheck: String): Boolean {
    return toCheck.toDoubleOrNull() != null
}
```

此方法可以处理Double和所有数字类型，如Int、Float等：

```scss
assertTrue { isNumeric("11") }
assertTrue { isNumeric("-11") }
assertTrue { isNumeric("011") }
assertTrue { isNumeric("11.0F") }
assertTrue { isNumeric("11.0D") }
assertTrue { isNumeric("11.234") }
assertTrue { isNumeric("11.234e56") }
assertTrue { isNumeric("     123      ") }
```

这种方法非常通用，可以处理所有数字类型。但是，我们也可以对其他数字类型执行检查，我们可以通过将toDoubleOrNull替换为其他转换方法来实现：

1.  toIntOrNull()
2.  toLongOrNull()
3.  toFloatOrNull()
4.  toShortOrNull()
5.  toByteOrNull()

值得注意的是，**我们可以在Kotlin中的所有数字类型上调用这些转换方法中的每一个**。

## 3. 使用正则表达式

我们还可以**使用正则表达式来检查String是否为数字**：

```kotlin
fun isNumeric(toCheck: String): Boolean {
    val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
    return toCheck.matches(regex)
}
```

在这里，我们使用正则表达式来检查字符串是否为数字，**正则表达式的结构与十进制数相匹配**。

让我们仔细看看：

-   `-?`：我们检查数字的开头是否有零个或一个减号(“-”)。
-   `[0-9]`：我们检查字符串中是否有一位或多位数字，如果没有数字，此操作将失败。值得注意的是，这只匹配0、1、2、3、4、5、6、7、8和9。但是，如果我们想查找其他Unicode数字，我们可以使用“\d”而不是“[0 -9]”。
-   `(\\.[0-9]+)?`：我们检查是否有小数点(“.”)符号；如果是这样，它后面必须至少跟一个数字。

## 4. 使用isDigit()和all()

另一种检查字符串是否只包含数字的方法是**结合使用**[all](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/all.html)**和**[isDigit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-digit.html)**方法**：

```kotlin
fun isNumeric(toCheck: String): Boolean {
    return toCheck.all { char -> char.isDigit() }
}
```

在这里，我们检查字符串的每个字符是否都是数字，如果所有字符在传递给isDigit方法时都返回true，则all方法返回true。

这也意味着**此方法不适用于负数或浮点数**。

## 5. 总结

我们研究了多种检查字符串是否为数字的方法。首先，我们使用了Kotlin必须提供的各种解析函数(toDoubleOrNull()、toIntOrNull()等)，这种方法似乎是最通用的，涵盖了最多的用例。

后来，我们还研究了涵盖相对较少用例的其他选项，这些方法使用正则表达式和Kotlin工厂函数来检查数字字符串。
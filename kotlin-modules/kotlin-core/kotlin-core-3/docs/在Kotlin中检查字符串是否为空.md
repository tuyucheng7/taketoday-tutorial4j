## 1. 概述

根据我们的可空性约束和其他要求，Kotlin中有一些扩展可以帮助我们确定字符串是否为空。

在这个简短的教程中，我们将熟悉那些在Kotlin中检查字符串是否为空甚至空白的函数。

## 2. 空字符串

**如果字符串的长度为零**，则字符串为空(empty)。为了确定一个不可为null的字符串是否为空，我们可以使用[isEmpty()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-empty.html)扩展函数：

```kotlin
val empty = ""
assertTrue { empty.isEmpty() }
```

另一方面，要检查一个String是否不为空，除了否定运算符，我们还可以使用[isNotEmpty()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-not-empty.html)扩展函数：

```kotlin
val nonEmpty = "42"
assertTrue { nonEmpty.isNotEmpty() }
```

除了String之外，**这些扩展还适用于各种**[CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/)，**例如**[StringBuilder](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/index.html)：

```kotlin
val sb = StringBuilder()
assertTrue { sb.isEmpty() }
```

在这里，我们确保给定的StringBuilder实际上是空的。

对于可为null的String字符串，还可以使用[isNullOrEmpty()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-null-or-empty.html)函数检查值是null还是空： 

```kotlin
val nullStr: String? = null
val emptyNullable: String? = ""

assertTrue { nullStr.isNullOrEmpty() }
assertTrue { emptyNullable.isNullOrEmpty() }
```

### 2.1 简化条件逻辑

有时我们可能需要传统的if-else条件分支来处理空值：

```kotlin
val ipAddress = request.getHeader("X-FORWARDED-FOR")
val source = if (ipAddress.isEmpty()) "default-value" else ipAddress
```

从Kotlin 1.3开始，**我们可以通过使用**[ifEmpty()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/if-empty.html)[高阶函数](https://kotlinlang.org/docs/lambdas.html)**的更函数式的方法来避免这种命令式风格**：

```kotlin
val source = request.getHeader("X-FORWARDED-FOR").ifEmpty { "default-value" }
```

正如我们所看到的，这更加简单和简洁！

## 3. 空白字符串

**当字符串的长度为零或仅包含空格字符时，字符串为空白**。为了检查不可为null的字符串是否为空白，我们可以使用[isBlank()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-blank.html)函数：

```kotlin
val blank = "    "
val empty = ""
val notBlank = "  42"

assertTrue { empty.isBlank() }
assertTrue { blank.isBlank() }
assertTrue { notBlank.isNotBlank() }
```

与isNotEmpty()类似，[isNotBlank()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-not-blank.html)函数用作否定运算符的更具可读性的替代方法。

同样，与isNullOrEmpty()非常相似，有一个[isNullOrBlank()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-null-or-blank.html)函数：

```kotlin
val nullStr: String? = null
val blankNullable: String? = "   "

assertTrue { nullStr.isNullOrBlank() }
assertTrue { blankNullable.isNullOrBlank() }
```

如果值为null或空白，则此函数返回true。除了所有这些相似性之外，[ifBlank()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/if-blank.html)高阶函数还帮助我们为空白值提供默认值。

## 4. 总结

在本教程中，我们熟悉了一些可以帮助我们检查字符串是否为空的函数。
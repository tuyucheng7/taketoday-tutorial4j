## 1. 概述

在这个简短的教程中，我们将学习如何从Kotlin中的字符串中删除所有非字母数字字符。

## 2. 删除非字母数字字符

为了从字符串中删除所有非字母数字字符，**我们可以使用**[正则表达式](https://www.baeldung.com/kotlin/regular-expressions)**和**[replace()](https://www.baeldung.com/kotlin/string-replace-substring)**扩展函数**，更具体地说，以下正则表达式匹配所有非字母数字字符：

```kotlin
val nonAlphaNum = "[^a-zA-Z0-9]".toRegex()
```

上面的正则表达式将匹配任何不是小写字母、大写字母或数字的字符(因为^是否定修饰符)。因此，使用这个正则表达式，我们可以去掉字符串中的所有非字母数字字符：

```kotlin
val text = "This notebook costs 2000€ (including tax)"
val nonAlphaNum = "[^a-zA-Z0-9]".toRegex()
val justAlphaNum = text.replace(nonAlphaNum, "")
assertEquals("Thisnotebookcosts2000includingtax", justAlphaNum)
```

如上所示，我们删除了欧元符号、括号和空格字符。

## 3. 支持Unicode字母

**不幸的是，同一个简单的正则表达式不能识别不同语言的字母和数字**。例如，在这里，德语变音符号被删除，就好像它是一个非字母数字字符一样：

```kotlin
assertEquals("hnlich", "ähnlich".replace(nonAlphaNum, ""))
```

如上所示，我们不小心删除了“ähnlich”中的“ä”字母，其他Unicode字母和数字也是如此：

```kotlin
assertEquals("", "آب".replace(nonAlphaNum, "")) // water in Persian
assertEquals("", "۴۲".replace(nonAlphaNum, "")) // 42 in Arabic
assertEquals("ao", "año".replace(nonAlphaNum, "")) // year in Spanish
```

在这里，我们要删除一些波斯字母、一个带有阿拉伯数字的数字和一个带有重音符号的西班牙语字母，即使它们是合法的字母或数字。为了解决这个问题，我们有两个解决方案。

首先，**我们可以使用**[\p{}属性标记](https://www.regular-expressions.info/unicode.html#prop)**调整正则表达式以包含Unicode字母和数字**：

```kotlin
val nonAlphaNum = "[^a-zA-Z0-9\\p{L}\\p{M}*\\p{N}]".toRegex()
```

“\\\p{L}\\\p{M}*”等同于所有带有各种标记和重音符号的Unicode字母，此外，“\\\p{N}”等同于所有Unicode数字。现在，有了这个正则表达式，我们也应该能够识别Unicode字母和数字：

```kotlin
assertEquals("Thisnotebookcosts2000includingtax", justAlphaNum)
assertEquals("ähnlich", "ähnlich".replace(nonAlphaNum, ""))
assertEquals("آب", "آب".replace(nonAlphaNum, ""))
assertEquals("۴۲", "۴۲".replace(nonAlphaNum, ""))
assertEquals("año", "año".replace(nonAlphaNum, ""))
```

如上所示，我们不会在这里意外地删除一些合法的字母数字字符。。

作为第二种解决方案，**我们可以利用**[isLetterOrDigit()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/is-letter-or-digit.html)**扩展函数来过滤掉非字母数字字符**：

```kotlin
fun String.onlyAlphanumericChars() =
    this.asSequence().filter { it.isLetterOrDigit() }.joinToString("")
```

在这里，我们使用[asSequence()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/as-sequence.html)扩展函数来[避免在此过程中创建不必要的中间数组和集合](https://www.baeldung.com/kotlin/sequences)。除此之外，逻辑非常简单，因为我们只保留字母数字字符并将它们拼接到一个新字符串中。

## 4. 总结

在本教程中，我们学习了几种在Kotlin中从字符串中删除所有非字母数字字符的方法。
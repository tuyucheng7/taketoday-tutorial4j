## 1. 概述

在本文中，我们将了解一些将Java字符数组转换为字符串的方法。

**首先，我们将使用原生String构造函数。然后，我们将看看工厂函数。最后，我们将使用StringBuilder执行字符数组到字符串的转换**。

## 2. 使用字符串构造函数

我们可以简单地使用String构造函数来转换字符数组：

```kotlin
val charArray = charArrayOf('t', 'u', 'y', 'u')
val convertedString = String(charArray)
```

在这里，我们首先使用[charArrayOf](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/char-array-of.html)辅助函数创建一个[CharArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-array/)。

然后我们**使用String类的构造函数，它接收一个CharArray**。结果，数组被转换为String。

## 3. 使用joinToString()方法

我们还可以使用另一种简单的技术将字符数组转换为字符串，使用[Array<Char\>.joinToString()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/join-to-string.html)方法，我们可以从一个初始化的字符数组创建一个字符串：

```kotlin
val charArray: Array<Char> = arrayOf('t', 'u', 'y', 'u')
val convertedString = charArray.joinToString()
```

**在这里，我们创建一个Char类型的**[数组](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/)。

然后，我们调用joinToString()工厂函数并将数组转换为字符串。

## 4. 使用StringBuilder

我们可以使用的另一种技术是使用StringBuilder进行转换：

```kotlin
val charArray = charArrayOf('t', 'u', 'y', 'u')
val convertedString = StringBuilder().append(charArray).toString()
```

**我们调用 StringBuilder的**[append()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/append.html)**方法，它接收一个CharArray**。

随后，我们调用toString()方法，它返回一个String。

## 5. 总结

有多种方法可以将字符数组转换为String，使用String构造函数似乎是最容易使用的。
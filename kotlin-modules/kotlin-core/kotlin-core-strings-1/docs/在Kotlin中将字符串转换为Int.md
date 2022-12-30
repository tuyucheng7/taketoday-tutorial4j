## 1. 概述

在这个快速教程中，我们将熟悉在Kotlin中将String转换为Int的几种方法。

## 2. String到Int的转换

在最简单的情况下，**我们可以使用**[toInt()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/to-int.html)**扩展函数将String转换为其对应的Int值**，当String包含有效的数字内容时，一切都应该顺利进行：

```kotlin
val intValue = "42".toInt()
assertEquals(42, intValue)
```

另一方面，如果String不包含有效的数字数据，则相同的函数将抛出[NumberFormatException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/NumberFormatException.html)：

```kotlin
assertThrows<NumberFormatException> { "0x2a".toInt() }
assertThrows<NumberFormatException> { "2.5".toInt() }
assertThrows<NumberFormatException> { "2.5 inch".toInt() }
assertThrows<NumberFormatException> { "invalid".toInt() }
```

如上所示，这些字符串不是有效的整数，因此会抛出异常。

如果我们不喜欢异常，**我们可以使用**[toIntOrNull()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/to-int-or-null.html)**扩展函数，它为无效整数返回null**：

```kotlin
assertNull("invalid".toIntOrNull())
assertEquals(42, "42".toIntOrNull())
```

因此，当String包含有效整数值时，toIntOrNull()的行为类似于toInt()。另一方面，对于无效的整数值，它只是吃掉异常并返回null。

### 2.1 基数转换

**有时字符串包含10以外的基数(或基数)整数值**，为了转换这些值，我们可以将基数作为第二个参数传递：

```kotlin
val intValue = "2a".toInt(16)
assertEquals(42, intValue)
```

在这里，“2a”是一个有效的十六进制值，所以我们将16作为基数传递。类似地，我们也可以将基数传递给toIntOrNull()函数：

```kotlin
assertEquals(42, "2a".toIntOrNull(16))
```

### 2.2 无符号值

**甚至可以将字符串转换为**[无符号整数](https://www.baeldung.com/kotlin/unsigned-integers)，Kotlin从Kotlin 1.3开始支持它们：

```kotlin
assertEquals(42u, "42".toUInt())
assertEquals(42u, "2a".toUInt(16))
assertNull("2a".toUIntOrNull())
assertEquals(42u, "2a".toUIntOrNull(16))
```

如上所示，[toUInt()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/to-u-int.html)和[toUIntOrNull()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/to-u-int-or-null.html)与它们的带符号的等价物非常相似，从Kotlin 1.5开始，无符号整数是一个稳定的功能。但是，如果我们使用的是早期版本的Kotlin，则必须选择使用[@ExperimentalUnsignedTypes](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-experimental-unsigned-types/)注解才能使用此实验性功能。

## 3. 总结

在本教程中，我们了解了如何将Kotlin字符串转换为它们相应的Int值。
## 1. 概述

在本快速教程中，我们将评估在Kotlin中将String转换为Long的不同可用选项，反之亦然。

## 2. 基本转换

**要将任何String转换为其Long等价物，我们可以使用toLong()**[扩展函数](https://www.baeldung.com/kotlin-extension-methods)：

```kotlin
val number = "42".toLong()
assertEquals(42, number)
```

**当String不是有效的数值时，此扩展函数会抛出一个**[NumberFormatException](https://www.baeldung.com/java-number-format-exception)**实例**：

```kotlin
assertThrows<NumberFormatException> { "the answer is 42".toLong() }
```

与toLong()相反，如果给定的String不是有效数字，则[toLongOrNull()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/to-long-or-null.html)扩展函数将返回null：

```kotlin
val number: Long? = "42".toLongOrNull()
assertEquals(42, number)

assertNull("the answer is 42".toLongOrNull())
```

如上所示，此函数捕获抛出的异常并返回null。

## 3. 基数转换

除了十进制数字之外，**我们还可以将十六进制、二进制或八进制等其他数字系统中的String转换为Long实例**，我们所要做的就是将基数传递给toLong(radix)或toLongOrNull(radix)函数：

```kotlin
assertEquals(15, "f".toLong(16))
assertEquals(15, "F".toLong(16))
assertEquals(15, "17".toLong(8))
assertEquals(15, "1111".toLongOrNull(2))
```

同样，如果给定的String在特定数字系统中不是有效数字，则toLong(radix)会抛出异常。此外，toLongOrNull(radix)在这些情况下返回null：

```kotlin
assertThrows<NumberFormatException> { "fg".toLong(16) }
assertNull("8".toLongOrNull(8))
```

十六进制数字不能有字母“g”，八进制数只包含[1, 7]范围内的数字，因此这两个示例都是无效数字。

## 4. 无符号Long值

从[Kotlin 1.3](https://kotlinlang.org/docs/reference/whatsnew13.html#unsigned-integers)开始，除了有符号整数外，Kotlin还支持[无符号整数](Kotlin中的无符号整数.md)。当然，也可以将String转换为这些无符号数：

```kotlin
assertEquals(42uL, "42".toULong())
```

同样，我们可以针对非十进制系统：

```kotlin
assertEquals(15uL, "f".toULong(16))
```

此外，无符号转换提供与有符号转换相同的API：

```kotlin
assertEquals(15uL, "17".toULongOrNull(8))
assertNull("179".toULongOrNull(8))
assertThrows<NumberFormatException> { "179".toULong(8) }
```

由于无符号整数仍然是一个实验性功能，我们应该使用[ExperimentalUnsignedTypes](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-experimental-unsigned-types/)注解来抑制编译器警告。

## 5. Long到String

除了将String转换为Long之外，还可以执行相反方向的转换。为此，我们可以使用toString()函数：

```kotlin
assertEquals("42", 42.toString())
```

甚至可以将十进制Long值转换为其他数字系统中相应的String：

```kotlin
assertEquals("101010", 42.toString(2))
assertEquals("2a", 42uL.toString(16))
```

在这里，我们将一个无符号和一个有符号的Long转换为其二进制和十六进制表示形式。

## 6. 总结

在这个简短的教程中，我们了解了如何将Long值转换为其相应的String表示形式，反之亦然。我们也确实简单地谈到了Kotlin 1.3中引入的新的无符号数。
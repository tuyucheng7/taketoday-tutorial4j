## 1. 概述

在本快速教程中，我们将熟悉几种将整数转换为相应的十六进制值的方法。

## 2. Integer.toHexString()

将整数转换为相应的十六进制形式的最简单方法是使用[Integer.toHexString(int)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#toHexString(int))方法：

```kotlin
val hex = Integer.toHexString(4001)
assertEquals("fa1", hex)
```

如上所示，toHexString()方法按预期生成十六进制值。**值得一提的是，此方法会在后台将负数转换为其无符号值**。

## 3. toString()扩展函数

从Kotlin 1.1开始，我们可以对整数使用[toString(radix: Int)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/to-string.html)扩展函数将它们转换为十六进制值：

```kotlin
val number = 4001
assertEquals("fa1", number.toString(16))
```

如上所示，此函数接收基数作为其参数；显然，我们传递16是为了请求十六进制值。

**有趣的是，在无符号转换上Integer.toHexString()和toString()之间存在细微差别**，更具体地说，前者首先将整数转换为其无符号等效值。因此，对于负数，它们返回不同的值：

```kotlin
val number = -100
assertNotEquals(Integer.toHexString(number), number.toString(16))
```

为了解决这个问题，我们可以在调用toString()之前将整数转换为其等效的无符号long值：

```kotlin
assertEquals(Integer.toHexString(number), number.toUInt().toString(16))
```

如上所示，我们使用[Kotlin 1.3](https://www.baeldung.com/kotlin/unsigned-integers)中引入的无符号整数功能进行无符号转换。

或者，我们可以使用“0xffffffffL”执行[按位与运算](https://www.baeldung.com/kotlin/byte-arrays-to-hex-strings#loops-and-bitwise-operations)，将整数转换为其无符号long格式：

```kotlin
val unsignedLong = number.toLong() and 0xffffffffL
assertEquals(Integer.toHexString(number), unsignedLong.toString(16))
```

这可能很有用，尤其是当toUInt()不可用时。

## 4. 格式说明符

**除了上述方式之外，我们还可以使用**[format()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/format.html)**扩展函数来进行这种转换**。为此，我们必须使用“%x”格式说明符调用format()方法：

```kotlin
val number = 4001
assertEquals("fa1", "%x".format(number))
```

“ %x”说明符会将给定的数字转换为其十六进制形式。

## 5. 总结

在本教程中，我们学习了几种将整数转换为相应的十六进制字符串的方法。
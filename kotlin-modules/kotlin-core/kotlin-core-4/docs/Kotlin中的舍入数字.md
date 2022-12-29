## 1. 简介

众所周知，四舍五入以牺牲精度为代价使数字更短更简单。

在本教程中，我们将了解一些在Kotlin中对数字进行舍入的方法。

## 2. BigDecimal四舍五入

[BigDecimal](https://www.baeldung.com/java-bigdecimal-biginteger#bigdecimal)类提供了一种四舍五入Double数字的简单方法：

```kotlin
val rawPositive = 0.34444
val roundedUp = rawPositive.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
assertTrue(roundedUp == 0.4)
```

使用setScale()，我们指定Double必须四舍五入到的小数位数。为了说明这一点，我们将scale设置为2：

```kotlin
val roundedUp = rawPositive.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
assertTrue(roundedUp == 0.35)
```

如上所示，当我们将scale设置为2时，该数字四舍五入到小数点后两位。

另外，**我们还可以指定舍入方式**。例如，使用RoundingMode.UP，我们可以从零四舍五入。

[RoundingMode.HALF_UP](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/math/RoundingMode.html#HALF_UP)是一种常用的舍入方式，这也是学校通常教授的舍入模式。

同样，如果我们设置RoundingMode.DOWN，我们可以将数字向下舍入为零：

```kotlin
val rawPositive = 0.34444
val rawNegative = -0.3444

val roundedDown = rawPositive.toBigDecimal().setScale(1, RoundingMode.DOWN).toDouble()
assertTrue(roundedDown == 0.3)

val roundedDownNegative = rawNegative.toBigDecimal().setScale(1, RoundingMode.DOWN).toDouble()
assertTrue(roundedDownNegative == -0.3)
```

我们将舍入模式设置为RoundingMode.DOWN，我们还将scale设置为1。正如预期的那样，该数字向下舍入为零，保留一位小数。

我们可以使用许多其他舍入模式。

例如，RoundingMode.CEILING舍入到正无穷大：

```kotlin
val roundedCeiling = rawPositive.toBigDecimal().setScale(1, RoundingMode.CEILING).toDouble()
assertTrue(roundedCeiling == 0.4)
```

同样，RoundingMode.FLOOR向负无穷大舍入：

```kotlin
val roundedFloor = rawPositive.toBigDecimal().setScale(1, RoundingMode.FLOOR).toDouble()
assertTrue(roundedFloor == 0.3)
```

我们还有一些舍入模式：

```kotlin
val roundedHalfUp = 1.55.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toDouble()
assertTrue(roundedHalfUp == 1.6)

val roundedHalfEven = 1.55.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toDouble()
assertTrue(roundedHalfEven == 1.6)

val roundedHalfDown = 1.55.toBigDecimal().setScale(1, RoundingMode.HALF_DOWN).toDouble()
assertTrue(roundedHalfDown == 1.5)
```

**所有这些模式都向最近的邻居旋转，但是，当邻居距离相等时，行为会有所不同**：

-   RoundingMode.HALF_UP四舍五入
-   RoundingMode.HALF_DOWN向下舍入
-   RoundingMode.HALF_EVEN朝向最近的偶数邻居

## 3. 使用String.format()

我们还可以使用[String.format()](https://www.baeldung.com/string/format)来四舍五入小数：

```kotlin
val raw1 = 0.34
val raw2 = 0.35
val raw3 = 0.36

val rounded1: Double = String.format("%.1f", raw1).toDouble()
assertTrue(rounded1 == 0.3)

val rounded2: Double = String.format("%.1f", raw2).toDouble()
assertTrue(rounded2 == 0.4)

val rounded3: Double = String.format("%.1f", raw3).toDouble()
assertTrue(rounded3 == 0.4)
```

在这里，[String.format()](https://www.baeldung.com/string/format)**的行为类似于RoundingMode.HALF_UP**。但是，没有办法修改舍入模式。

此外，我们应该**考虑指定**[Locale](https://www.baeldung.com/java-localization-messages-formatting)，通过这样做，我们可以避免非英语区域设置中的错误，这方面的例子是法语或斯拉夫语言环境：

```kotlin
val rounded: Double = String.format("%.1f", raw1).toDouble()
```

当默认区域设置为Locale.French时运行上面的代码将导致NumberFormatException。为了解决这个问题，我们应该指定Locale：

```kotlin
val rounded: Double = String.format(Locale.ENGLISH, "%.1f", raw1).toDouble() 
assertTrue(rounded == 0.3)
```

## 4. 使用DecimalFormat

类似于[String.format()](https://www.baeldung.com/string/format)，我们可以使用[DecimalFormat](https://www.baeldung.com/java-decimalformat)格式化数字：

```kotlin
val df = DecimalFormat("#.#", DecimalFormatSymbols(Locale.ENGLISH))
val raw1 = 0.34.toBigDecimal()
val raw2 = 0.35.toBigDecimal()
val raw3 = 0.36.toBigDecimal()

val rounded = df.format(raw1).toDouble()
assertTrue(rounded == 0.3)

val rounded = df.format(raw2).toDouble()
assertTrue(rounded == 0.4)

val rounded = df.format(raw3).toDouble()
assertTrue(rounded == 0.4)
```

在这里，**默认使用RoundingMode.HALF_EVEN，我们还可以指定舍入模式**：

```kotlin
val df = DecimalFormat("#.#", DecimalFormatSymbols(Locale.ENGLISH))
df.roundingMode = RoundingMode.FLOOR

val rounded3Floor = df.format(raw3).toDouble()
assertTrue(rounded3Floor == 0.3)
```

## 5. 总结

总而言之，我们可以通过多种方式在Kotlin中执行舍入，尽管我们选择的方式可能取决于我们的用例。
## 1. 概述

在本教程中，我们将了解将double值转换为String并删除其小数位的不同方法。

当我们只想截断小数部分和四舍五入时，我们将看看如何做到这一点。

## 2. 使用转换截断

如果我们的 double值在int范围内，我们可以将其转换为int。 强制转换会截断小数部分，这意味着它会在不进行任何舍入的情况下将其截断。

这种方法的速度大约是我们将要看到的其他方法的 10 倍。

一旦它是一个 int，我们就可以将它传递给 String类的valueOf方法：

```java
String truncated = String.valueOf((int) doubleValue);
```

当我们保证double 值在[int](https://www.baeldung.com/java-primitives)[的范围内 ](https://www.baeldung.com/java-primitives)时，我们可以自信地使用这种方法。但是如果我们的值超过了这个值，转换就不会像我们想要的那样工作。

## 3. 使用String.format()舍入

现在，剩下的方法不像强制转换那样受限，但它们有自己的细微差别。

例如，另一种方法是使用String类的format方法。该方法的第一个参数指定我们正在格式化一个小数点后为零的浮点值：

```java
String rounded = String.format("%.0f", doubleValue);
```

格式方法使用HALF_UP 舍入，如果小数部分后的值是 .5 或以上，则舍入。否则，它返回小数点前的数字。

虽然简单，但String.format是执行此操作最慢的方法。

## 4. 使用NumberFormat.format()

NumberFormat类也提供了类似于String类的格式化方法，但是 NumberFormat 更快，并且我们可以通过它指定舍入方式来实现截断或舍入。

setMaximumFractionDigits()方法告诉格式化程序小数点后要包含在输出中的小数位数：

```javascript
NumberFormat nf = NumberFormat.getNumberInstance();
nf.setMaximumFractionDigits(0);
String rounded = nf.format(doubleValue);
```

奇怪的是，默认情况下NumberFormat 不使用HALF_UP。相反，它默认使用HALF_EVEN舍入，这意味着它会像正常情况一样舍入，但 .5 除外，在这种情况下，它会选择最接近的偶数。

虽然HALF_EVEN有助于统计分析，但让我们使用 HALF_UP来保持一致：

```java
nf.setRoundingMode(RoundingMode.HALF_UP);
String rounded = nf.format(doubleValue);
```

而且，我们可以通过将格式化程序设置为使用FLOOR舍入模式来更改它并实现截断：

```java
nf.setRoundingMode(RoundingMode.FLOOR);
String truncated = nf.format(doubleValue)
```

现在，它将截断而不是舍入。

## 5. 使用DecimalFormat.format()

与NumberFormat类似，DecimalFormat类可用于格式化双精度值。但是，我们可以通过为构造函数提供特定模式来告诉格式化程序我们想要什么输出，而不是使用方法调用来设置输出格式：

```javascript
DecimalFormat df = new DecimalFormat("#,###");
df.setRoundingMode(RoundingMode.HALF_UP);
String rounded = df.format(doubleValue);
```

“#,###”模式表示我们希望格式化程序只返回输入的整数部分。它还表示我们希望将数字分成三组，并用逗号分隔。

相同的舍入默认值适用于此处，因此如果我们想要输出截断值，我们可以将舍入模式设置为FLOOR：

```java
df.setRoundingMode(RoundingMode.FLOOR);
String truncated = df.format(doubleValue)
```

## 6. 使用 BigDecimal.toString()

我们要看的最后一种方法是BigDecimal，我们将包括它，因为它在更大的double s上表现优于 NumberFormat 和 DecimalFormat。

我们可以使用BigDecimal的 setScale 方法来判断我们是要舍入还是截断：

```java
double largeDouble = 345_345_345_345.56;
BigDecimal big = new BigDecimal(largeDouble);
big = big.setScale(0, RoundingMode.HALF_UP);
```

请记住， BigDecimal是不可变的，因此，与字符串一样，我们需要重置该值。

然后，我们只需调用 BigDecimal的 toString：

```java
String rounded = big.toString();
```

## 七、总结

在本教程中，我们研究了在删除小数位的同时将double转换 为String的不同方法。我们提供了可以输出四舍五入或截断值的方法。
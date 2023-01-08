## 1. 概述

在本教程中，我们将描述Math类，它提供有用的静态方法来执行指数、对数等数值运算。

## 2. 基本数学函数

我们将介绍的第一组方法是基本的数学函数，例如绝对值、平方根、两个值之间的最大值或最小值。

### 2.1. 绝对()

abs()方法返回给定值的绝对值：

```java
Math.abs(-5); // returns 5
```

同样，在接下来我们将看到的其他函数中， abs()接受 int、long、float 或 double作为参数 并返回相关参数。

### 2.2. 战俘()

计算并返回第一个参数的第二个幂次方的值：

```java
Math.pow(5,2); // returns 25
```

我们将在 [此处](https://www.baeldung.com/java-math-pow)更详细地讨论此方法。

### 2.3. 开方()

返回double的四舍五入正平方根：

```java
Math.sqrt(25); // returns 5
```

如果参数为[NaN](https://www.baeldung.com/java-not-a-number)或小于零，则结果为[NaN](https://www.baeldung.com/java-not-a-number)。

### 2.4. cbrt()

同样， cbrt()返回double的立方根 ：

```java
Math.cbrt(125); // returns 5
```

### 2.5. 最大限度()

正如该方法的名称所示，它返回两个值之间的最大值：

```java
Math.max(5,10); // returns 10
```

同样，该方法接受 int、long、float 或 double。

### 2.6. 分钟() 

同样， min()返回两个值之间的最小值：

```java
Math.min(5,10); // returns 5
```

### 2.7. 随机的()

返回 大于或等于 0.0 且小于 1.0的伪随机双 精度数：

```java
double random = Math.random()
```

为此，该方法会在首次调用时 创建java.util.Random() 数字生成器的单个实例 。

之后，对该方法的所有调用都使用同一个实例。请注意，该方法是同步的，因此可以被多个线程使用。

[我们可以在本文](https://www.baeldung.com/java-generate-random-long-float-integer-double)中找到有关如何生成随机数的更多示例。

### 2.8. 符号()

当我们必须知道值的符号时很有用：

```java
Math.signum(-5) // returns -1
```

如果参数大于零，则此方法返回 1.0，否则返回 -1.0。如果参数为正零或负零，则结果与参数相同。

输入可以是 浮点数 或 双精度数。

### 2.9. 签名()

接受两个参数并返回带有第二个参数符号的第一个参数：

```java
Math.copySign(5,-1); // returns -5
```

参数也可以是 float或 double。

## 3. 指数函数和对数函数

除了基本的数学函数外，Math 类还包含求解指数函数和对数函数的方法。

### 3.1. 表达式()

exp()方法接收一个 双精度参数并返回欧拉数的参数幂 ( e )`x`：

```java
Math.exp(1); // returns 2.718281828459045
```

### 3.2. expm1()

与上述方法类似， expm1()计算欧拉数的幂次接收到的参数，但它会添加 -1 ( e`x` -1)：

```java
Math.expm1(1); // returns 1.718281828459045
```

### 3.3. 日志()

返回双精度值的自然对数 ：

```java
Math.log(Math.E); // returns 1
```

### 3.4. log10()

它返回参数以 10 为底的对数：

```java
Math.log10(10); // returns 1
```

### 3.5. log1p()

与log() 类似 ，但它将参数 ln(1 + x) 加 1：

```java
Math.log1p(Math.E); // returns 1.3132616875182228
```

## 4. 三角函数

当我们必须使用几何公式时，我们总是需要三角函数；数学课为我们提供了这些。 

### 4.1. 罪()

接收表示角度(以弧度为单位)的单个 双精度参数并返回三角正弦值：

```java
Math.sin(Math.PI/2); // returns 1
```

### 4.2. 余弦()

同样， cos()返回角度的三角余弦值(以弧度为单位)：

```java
Math.cos(0); // returns 1
```

### 4.3. 谭()

返回角度的三角正切值(以弧度为单位)：

```java
Math.tan(Math.PI/4); // returns 1
```

### 4.4. 出生()，cosh()，tanh()

它们分别返回双精度值的双曲正弦、双曲余弦和双曲正切值：

```java
Math.sinh(Math.PI);

Math.cosh(Math.PI);

Math.tanh(Math.PI);
```

### 4.5. 盐()

返回接收到的参数的反正弦：

```java
Math.asin(1); // returns pi/2
```

结果是范围内的角度 – pi /2 到 pi /2。

### 4.6. acos()

返回接收到的参数的反余弦值：

```java
Math.acos(0); // returns pi/2
```

结果是 0 到 pi范围内的角度。

### 4.7. 为了()

返回接收到的参数的反正切值：

```java
Math.atan(1); // returns pi/4
```

结果是范围内的角度 – pi /2 到 pi /2。

### 4.8. 阿坦2()

最后， atan2()接收纵坐标y和横坐标x，并返回直角坐标(x,y)到极坐标(r, ϑ)转换后的角度ϑ：

```java
Math.atan2(1,1); // returns pi/4
```

### 4.9. toDegrees()

当我们需要将弧度转换为度时，此方法很有用：

```java
Math.toDegrees(Math.PI); // returns 180
```

### 4.10. toRadians()

另一方面， toRadians()可用于进行相反的转换：

```java
Math.toRadians(180); // returns pi
```

请记住，我们在本节中看到的大多数方法都接受以弧度为单位的参数，因此，当我们有一个以度为单位的角度时，应该在使用三角方法之前使用此方法。

有关更多示例，请查看 [此处。](https://www.baeldung.com/java-math-sin-degrees)

## 5.四舍五入等功能

最后，让我们看一下舍入方法。

### 5.1. 天花板()

当我们必须将整数舍入为大于或等于参数的最小双精度值时， ceil()很有用 ：

```java
Math.ceil(Math.PI); // returns 4
```

在[本文](https://www.baeldung.com/java-round-up-nearest-hundred)[中](https://www.baeldung.com/java-round-up-nearest-hundred)，我们使用此方法将数字四舍五入到最接近的百位。

### 5.2. 地面()

要将数字四舍五入 为小于或等于参数的最大双精度数，我们应该使用floor()：

```java
Math.floor(Math.PI); // returns 3
```

### 5.3. 得到指数()

返回参数的无偏指数。

参数可以是 double或 float：

```java
Math.getExponent(333.3); // returns 8

Math.getExponent(222.2f); // returns 7
```

### 5.4. IEEE余数()

计算第一个(股息)和第二个(除数)参数之间的除法，并按照 [IEEE 754 标准](https://en.wikipedia.org/wiki/IEEE_754)的规定返回余数：

```java
Math.IEEEremainder(5,2); // returns 1
```

### 5.5. 下一个之后()

当我们需要知道double或 float值的相邻值时，此方法很有用：

```java
Math.nextAfter(1.95f,1); // returns 1.9499999

Math.nextAfter(1.95f,2); // returns 1.9500002
```

它接受两个参数，第一个是你想知道相邻数字的值，第二个是方向。

### 5.6. 下一个()

与前面的方法类似，但是这个方法只返回正无穷大方向上的相邻值：

```java
Math.nextUp(1.95f); // returns 1.9500002
```

### 5.7. 运行()

返回一个double ，它是参数最接近的整数值：

```java
Math.rint(1.95f); // returns 2.0
```

### 5.8. 圆形的()

与上述方法相同，但如果参数是浮点数 ， 则此方法返回一个 int值；如果参数是 双 精度数，则返回一个long值：

```java
int result = Math.round(1.95f); // returns 2

long result2 = Math.round(1.95) // returns 2
```

### 5.9. 头皮()

Scalb 是“scale binary”的缩写。此函数执行一次移位、一次转换和一次双倍乘法：

```java
Math.scalb(3, 4); // returns 32^4
```

### 5.10. ulp()

ulp() 方法返回一个数字到它最近的邻居的距离：

```java
Math.ulp(1); // returns 1.1920929E-7
Math.ulp(2); // returns 2.3841858E-7
Math.ulp(4); // returns 4.7683716E-7
Math.ulp(8); // returns 9.536743E-7
```

### 5.11. 海波()

返回其参数的平方和的平方根：

```java
Math.hypot(4, 3); // returns 5
```

该方法在没有中间溢出或下溢的情况下计算平方根。

在[这篇文章中](https://www.baeldung.com/java-distance-between-two-points)，我们使用这种方法来计算两点之间的距离。

## 6.Java8 数学函数

Java 8 中重新访问了 Math 类，以包含执行最常见算术运算的新方法 。 

[我们在另一篇文章中](https://www.baeldung.com/java-8-math)讨论了这些方法。

## 7.常量字段

除了方法之外， Math 类还声明了两个常量字段：

```java
public static final double E

public static final double PI
```

分别表示越接近自然对数底的值和越接近 pi的值。

## 八、总结

在本文中，我们描述了Java为数学运算提供的 API。
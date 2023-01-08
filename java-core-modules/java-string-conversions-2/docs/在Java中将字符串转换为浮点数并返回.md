## 1. 概述

将数据从Float转换为String，反之亦然，这是Java中的一个普通操作。但是，执行此操作的许多方法可能会导致混淆和不确定选择哪一个。

在本文中，我们将展示和比较所有可用选项。

## 2.浮动到字符串

首先，让我们看一下将Float值转换为 String的最常用方法。

### 2.1. 字符串连接

我们可以使用的最直接的解决方案是将浮点值与空String连接起来。

让我们看一个例子：

```java
float givenFloat = 1.25f;

String result = givenFloat + "";

assertEquals("1.25", result);
```

同样，我们可以将一个Float对象添加到空 String中并得到相同的结果。当我们使用Float对象时，它的toString()方法会自动调用：

```java
Float givenFloat = 1.25f;

String result = givenFloat + "";

assertEquals("1.25", result);

```

如果 Float对象为 null，则串联结果将为“null” String：

```java
Float givenFloat = null;

String result = givenFloat + "";

assertEquals("null", result);
```

### 2.2. Float.toString()

我们可以使用的另一个选项是用于String转换的Float类的静态toString()方法 。我们可以将float原始值或Float对象传递给toString()方法：

```java
Float givenFloat = 1.25f;

String result = Float.toString(givenFloat);

assertEquals("1.25", result);
```

如果我们将 null 作为参数传递给该方法，我们将在运行时得到NullPointerException ：

```java
Float givenFloat = null;

assertThrows(NullPointerException.class, () -> Float.toString(givenFloat));
```

### 2.3. String.valueOf() 值

同样，我们可以使用String的静态valueOf方法：

```java
Float givenFloat = 1.25f;

String result = String.valueOf(givenFloat);

assertEquals("1.25", result);
```

与 Float.toString()不同，如果我们将 null 作为参数传递， String.valueOf()将不会抛出异常，而是返回“null”字符串：

```java
Float givenFloat = null;

String result = String.valueOf(givenFloat);

assertEquals("null", result);
```

### 2.4. 字符串格式()

[String的 format()静态方法](https://www.baeldung.com/string/format)为我们提供了额外的格式化选项。我们必须知道，在不限制小数位数的情况下，即使没有小数部分，结果也将包含尾随零，如下例所示：

```java
Float givenFloat = 1.25f;

String result = String.format("%f", givenFloat);

assertEquals("1.250000", result);
```

当我们格式化指定小数位数的浮点数时，format ()方法也会对结果进行四舍五入：

```java
Float givenFloat = 1.256f;

String result = String.format("%.2f", givenFloat);

assertEquals("1.26", result);
```

如果我们传递一个 null Float，那么转换后的结果将是一个“null” String：

```java
Float givenFloat = null;

String result = String.format("%f", givenFloat);

assertEquals("null", result);
```

### 2.5. 十进制格式

最后，DecimalFormat [类](https://www.baeldung.com/java-decimalformat)有一个format()方法，允许将浮点值转换为自定义格式的字符串。优点是我们可以在结果String中精确定义我们想要的小数位数。

让我们看看如何在示例中使用它：

```java
Float givenFloat = 1.25f;

String result = new DecimalFormat("#.0000").format(givenFloat);

assertEquals("1.2500", result);
```

如果在我们应用格式后，没有小数部分，DecimalFormat将返回整数：

```java
Float givenFloat = 1.0025f;

String result = new DecimalFormat("#.##").format(givenFloat);

assertEquals("1", result);
```

如果我们将 null 作为参数传递，那么我们将得到一个IllegalArgumentException：

```java
Float givenFloat = null;

assertThrows(IllegalArgumentException.class, () -> new DecimalFormat("#.000").format(givenFloat));
```

## 3.浮动的字符串

接下来，让我们看看将String值转换为 Float的最常见方法。

### 3.1. Float.parseFloat()

最常见的方法之一是使用Float的静态方法：parseFloat()。它将返回一个原始的float值，由String argument 表示。此外，忽略前导和尾随空格：

```java
String givenString = "1.25";

float result = Float.parseFloat(givenString);

assertEquals(1.25f, result);
```

如果String参数为 null ，我们将得到一个NullPointerException ：

```java
String givenString = null;

assertThrows(NullPointerException.class, () -> Float.parseFloat(givenString));
```

如果String参数不包含可解析的float，我们会得到一个NumberFormatException：

```java
String givenString = "1.23x";

assertThrows(NumberFormatException.class, () -> Float.parseFloat(givenString));
```

### 3.2. Float.valueOf()

同样，我们可以使用Float的静态valueOf()方法。区别在于 valueOf()返回一个 Float对象。具体来说，它调用parseFloat()方法并将其装箱到一个 Float对象中：

```java
String givenString = "1.25";

Float result = Float.valueOf(givenString);

assertEquals(1.25f, result);
```

同样，如果我们传递一个不可解析的String，我们将得到一个 NumberFormatException：

```java
String givenString = "1.25x";

assertThrows(NumberFormatException.class, () -> Float.valueOf(givenString));
```

### 3.3. 十进制格式

我们也可以使用DecimalFormat将String转换为Float。主要优点之一是指定自定义小数点分隔符。

```java
String givenString = "1,250";
DecimalFormatSymbols symbols = new DecimalFormatSymbols();
symbols.setDecimalSeparator(',');
DecimalFormat decimalFormat = new DecimalFormat("#.000");
decimalFormat.setDecimalFormatSymbols(symbols);

Float result = decimalFormat.parse(givenString).floatValue();

assertEquals(1.25f, result);
```

### 3.4. Float 的构造函数

最后，我们可以直接使用Float的构造函数进行转换。 在内部它将使用Float的静态parseFloat()方法并创建Float对象：

```java
String givenString = "1.25";

Float result = new Float(givenString);

assertEquals(1.25f, result);
```

从Java9 开始， 此构造函数已被[弃用。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Float.html#(java.lang.String)) 相反，我们应该考虑使用其他静态工厂方法，例如parseFloat()或valueOf()。

## 4。总结

在本文中，我们探讨了将String 实例转换为float或 Float实例并返回的多种方法。

对于简单的转换，String连接和 Float.toString()将是转换为String的首选选项。如果我们需要更复杂的格式化，那么DecimalFormat是完成这项工作的最佳工具。为了将字符串转换为浮点值，如果我们需要 float基元 ，我们可以使用Float.parseFloat() ；如果我们更喜欢 Float对象，则可以使用Float.valueOf() 。同样，对于自定义格式，DecimalFormat是最佳选择。
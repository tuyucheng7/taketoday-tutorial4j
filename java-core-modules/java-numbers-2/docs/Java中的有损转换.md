## 1. 概述

在本快速教程中，我们将讨论Java中有损转换的概念及其背后的原因。

同时，我们将探索一些方便的转换技术来避免这种错误。

## 2.有损转换

有损转换只是在处理数据时丢失信息。


在Java中，它对应于在将一种类型转换为另一种类型时丢失变量值或精度的可能性。

当我们尝试将大尺寸类型的变量分配给小尺寸类型时，Java 会在编译代码时产生错误，incompatible types: possible lossy conversion 。

例如，让我们尝试将long分配给int：

```java
long longNum = 10;
int intNum = longNum;
```

编译此代码时，Java 将发出错误：

```shell
incompatible types: possible lossy conversion from long to int
```

在这里，Java 会发现long和int不兼容并导致有损转换错误。因为在int范围 -2,147,483,648 到 2,147,483,647之外可以有long值。

同样，让我们尝试将float分配给long：

```java
float floatNum = 10.12f;
long longNum = floatNum;
incompatible types: possible lossy conversion from float to long
```

由于float可以具有没有相应long值的十进制值。因此，我们将收到相同的错误。

同样，将double数字赋值给int也会导致同样的错误：

```java
double doubleNum = 1.2;
int intNum = doubleNum;
incompatible types: possible lossy conversion from double to int
```

double值对于int来说可能太大或太小，十进制值将在转换中丢失。因此，这是一个潜在的有损转换。

此外，我们在执行简单计算时可能会遇到此错误：

```java
int fahrenheit = 100;
int celcius = (fahrenheit - 32)  5.0 / 9.0;
```

当double与int相乘时，我们得到double的结果。因此，它也是一种潜在的有损转换。

因此，有损转换中的不兼容类型可以具有不同的大小或类型(整数或小数)。


## 3. 原始数据类型

在Java中，有许多[原始数据类型](https://www.baeldung.com/java-primitives)及其相应的[包装类](https://www.baeldung.com/java-wrapper-classes)可用。

接下来，让我们编译一个方便的Java中所有可能的有损转换列表：

-   短字节或字符_
-   字符到字节或短
-   int到byte、short或char
-   long到byte、short、char或int
-   float到byte、short、char、int或long
-   double到byte、short、char、int、long或float

请注意，尽管short和char具有相同的大小。不过，从short到char的转换是有损的，因为char是一种无符号数据类型。

## 4.转换技术

### 4.1. 在原始类型之间转换

[转换原语](https://www.baeldung.com/java-primitive-conversions)以避免有损转换的简单方法是通过向下转换；换句话说，将较大尺寸的类型转换为较小尺寸的类型。因此，它也被称为缩小原始转换。

例如，让我们使用向下转换将长数字转换为短数字：

```java
long longNum = 24;
short shortNum = (short) longNum;
assertEquals(24, shortNum);
```

同样，让我们将double转换为int：

```java
double doubleNum = 15.6;
int integerNum = (int) doubleNum;
assertEquals(15, integerNum);
```

但是，我们应该注意，通过向下转换将值过大或过小的大型类型转换为小型类型可能会导致意外值。

让我们转换short范围之外的long值：

```java
long largeLongNum = 32768; 
short minShortNum = (short) largeLongNum;
assertEquals(-32768, minShortNum);

long smallLongNum = -32769;
short maxShortNum = (short) smallLongNum;
assertEquals(32767, maxShortNum);
```

如果我们仔细分析转换，我们会发现这些不是预期值。

换句话说，当Java在从大型类型转换时达到小型类型的最大值时，下一个数字是小型类型的最低值，反之亦然。

让我们通过示例来理解这一点。当值为 32768 的largeLongNum转换为short时，shortNum1的值为 -32768 。因为short的最大值是 32767，所以，Java 会寻找下一个 short 的最小值。

同样，当smallLongNum转换为short时。shortNum2的值为32767，因为Java会求出short的下一个最大值。

另外，让我们看看将long的最大值和最小值转换为int时会发生什么：

```java
long maxLong = Long.MAX_VALUE; 
int minInt = (int) maxLong;
assertEquals(-1, minInt);

long minLong = Long.MIN_VALUE;
int maxInt = (int) minLong;
assertEquals(0, maxInt);
```

### 4.2. 在包装对象和原始类型之间转换

要直接将包装器对象转换为原语，我们可以在包装器类中使用各种方法，例如intValue()、shortValue()和longValue()。这称为拆箱。

例如，让我们将Float对象转换为long：

```java
Float floatNum = 17.564f;
long longNum = floatNum.longValue();
assertEquals(17, longNum);
```

此外，如果我们查看longValue或类似方法的实现，我们会发现缩小原始转换的使用：

```java
public long longValue() {
    return (long) value;
}
```

但是，有时应避免缩小原始转换以保存有价值的信息：

```java
Double doubleNum = 15.9999;
long longNum = doubleNum.longValue();
assertEquals(15, longNum);

```

转换后，longNum的值为15。而doubleNum为 15.9999，非常接近 16。

相反，我们可以使用Math.round()转换为最接近的整数：

```java
Double doubleNum = 15.9999;
long longNum = Math.round(doubleNum);

assertEquals(16, longNum);
```

### 4.3. 在包装器对象之间转换

为此，让我们使用已经讨论过的转换技术。

首先，我们将包装器对象转换为原始值，向下转型并将其转换为另一个包装器对象。换句话说，我们将执行拆箱、向下转换和装箱技术。

例如，让我们将Double对象转换为Integer对象：

```java
Double doubleNum = 10.3;
double dbl = doubleNum.doubleValue(); // unboxing
int intgr = (int) dbl; // downcasting
Integer intNum = Integer.valueOf(intgr);
assertEquals(Integer.valueOf(10), intNum);

```

最后，我们使用Integer。valueOf()将原始类型int转换为Integer对象。这种类型的转换称为装箱。

## 5.总结

在本文中，我们借助大量示例探讨了Java中有损转换的概念。此外，我们还编制了一个方便的列表，列出了所有可能的有损转换。

在此过程中，我们已经将缩小原始转换确定为一种转换原始数字并避免有损转换错误的简单技术。

同时，我们还探索了在Java中进行数字转换的其他便捷技术。
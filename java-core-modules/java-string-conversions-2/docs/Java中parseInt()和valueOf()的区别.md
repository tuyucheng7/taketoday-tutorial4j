## 1. 概述

众所周知，将[数字字符串转换 为 int 或 Integer](https://www.baeldung.com/java-convert-string-to-int-or-integer)是Java中非常常见的操作。

在本教程中，我们将通过两个非常流行的静态方法，java.lang.Integer 类的parseInt()和valueOf()来 帮助我们进行这种转换。此外，我们还将通过简单示例了解这两种方法之间的一些差异。

## 2. parseInt()方法

java.lang.Integer类提供了parseInt()方法的三种变体。让我们看看它们中的每一个。

### 2.1. 将字符串转换为整数

parseInt()的第一个变体接受一个String作为参数并返回原始数据类型int。当它无法将String转换为整数时，它会抛出 NumberFormatException 。

让我们看看它的签名：

```java
public static int parseInt(String s) throws NumberFormatException
```

现在，我们将看到一些示例，其中我们将有符号/无符号数字字符串作为参数传递给它，以了解如何从字符串解析为整数：

```java
@Test
public void whenValidNumericStringIsPassed_thenShouldConvertToPrimitiveInt() {
    assertEquals(11, Integer.parseInt("11")); 
    assertEquals(11, Integer.parseInt("+11")); 
    assertEquals(-11, Integer.parseInt("-11"));
}
```

### 2.2. 指定基数

parseInt() 方法的第二个变体接受一个String和一个int作为参数，并返回原始数据类型int。就像我们看到的第一个变体一样，它也会在无法将字符串转换为整数时抛出NumberFormatException ：

```java
public static int parseInt(String s, int radix) throws NumberFormatException
```

默认情况下， parseInt() 方法假定给定的String 是一个以 10 为底的整数。这里，参数radix是用于字符串到整数转换的基数或基数。

为了更好地理解这一点，让我们看几个例子，我们将一个字符串和基数参数一起传递给parseInt()：

```java
@Test
public void whenValidNumericStringWithRadixIsPassed_thenShouldConvertToPrimitiveInt() {
    assertEquals(17, Integer.parseInt("11", 16));
    assertEquals(10, Integer.parseInt("A", 16)); 
    assertEquals(7, Integer.parseInt("7", 8));
}
```

现在，让我们了解如何使用基数进行字符串转换。例如，在基数为 13 的数字系统中，一串数字(例如 398)表示十进制数(基数/基数为 10)632。换句话说，在这种情况下，计算方式如下 – 3 × 13 2 + 9 × 13 1 + 8 × 13 0 = 632。

同样，在上面的例子中Integer.parseInt(“11”, 16)计算返回17，1 × 16 1 + 1 × 16 0 = 17。

### 2.3. 将子字符串转换为整数

最后，parseInt() 方法的第三个变体接受一个CharSequence、子字符串的两个整数beginIndex和endIndex，以及另一个整数基数作为参数。如果传递了任何无效的字符串，它会抛出NumberFormatException：

```java
public static int parseInt(CharSequence s, int beginIndex, int endIndex, int radix) throws NumberFormatException
```

JDK 9在Integer类中引入了这个静态方法。现在，让我们看看它的实际效果：

```java
@Test
public void whenValidNumericStringWithRadixAndSubstringIsPassed_thenShouldConvertToPrimitiveInt() {
    assertEquals(5, Integer.parseInt("100101", 3, 6, 2));
    assertEquals(101, Integer.parseInt("100101", 3, 6, 10));
}
```

让我们了解如何将子字符串转换为具有给定基数的整数。这里，string 是“100101”，beginIndex和endIndex分别是 3 和 6。因此，子串是“101”。对于expectedNumber1，传递的基数是 2，这意味着它是二进制的。因此，子字符串“101”被转换为整数5。此外，对于expectedNumber2，传递的基数为10，即为十进制。因此，子字符串“101”被转换为整数 101。

此外，我们可以看到Integer.parseInt()在传递任何无效字符串时抛出NumberFormatException ：

```java
@Test(expected = NumberFormatException.class)
public void whenInValidNumericStringIsPassed_thenShouldThrowNumberFormatException(){
    int number = Integer.parseInt("abcd");
}
```

## 3. valueOf()方法

接下来，让我们看一下java.lang.Integer类提供的valueOf()方法的三种变体。

### 3.1. 将字符串转换为整数

valueOf()方法的第一个变体接受一个String作为参数并返回包装器类Integer。 如果传递任何非数字字符串，它会抛出NumberFormatException：

```java
public static Integer valueOf(String s) throws NumberFormatException
```

有趣的是，它在其实现中使用了parseInt(String s, int radix) 。

接下来，让我们看几个从有符号/无符号数字字符串到整数的转换示例：

```java
@Test
public void whenValidNumericStringIsPassed_thenShouldConvertToInteger() {
    Integer expectedNumber = 11;
    Integer expectedNegativeNumber = -11;
        
    assertEquals(expectedNumber, Integer.valueOf("11"));
    assertEquals(expectedNumber, Integer.valueOf("+11"));
    assertEquals(expectedNegativeNumber, Integer.valueOf("-11"));
}
```

### 3.2. 将整数转换为整数

valueOf()的第二个变体接受一个int作为参数并返回包装类Integer。此外，如果向它传递任何其他数据类型(例如float )，它会生成编译时错误。

这是它的签名：

```java
public static Integer valueOf(int i)
```

除了int到Integer的转换之外，该方法还可以接受一个char作为参数并返回其 Unicode 值。

为了进一步理解这一点，让我们看几个例子：

```java
@Test
public void whenNumberIsPassed_thenShouldConvertToInteger() {
    Integer expectedNumber = 11;
    Integer expectedNegativeNumber = -11;
    Integer expectedUnicodeValue = 65;
        
    assertEquals(expectedNumber, Integer.valueOf(11));
    assertEquals(expectedNumber, Integer.valueOf(+11));
    assertEquals(expectedNegativeNumber, Integer.valueOf(-11));
    assertEquals(expectedUnicodeValue, Integer.valueOf('A'));
}
```

### 3.3. 指定基数

valueOf()的第三个变体接受一个String和一个int作为参数，并返回包装器类Integer。 此外，与我们见过的所有其他变体一样，它也会在无法将给定字符串转换为Integer类型时抛出NumberFormatException ：

```java
public static Integer valueOf(String s, int radix) throws NumberFormatException
```

此方法还在其实现中使用了parseInt(String s, int radix) 。

默认情况下，valueOf () 方法假定给定的String 表示以 10 为底的整数。此外，此方法接受另一个参数来更改默认基数。

让我们解析一些String对象：

```java
@Test
public void whenValidNumericStringWithRadixIsPassed_thenShouldConvertToInetger() {
    Integer expectedNumber1 = 17;
    Integer expectedNumber2 = 10;
    Integer expectedNumber3 = 7;
        
    assertEquals(expectedNumber1, Integer.valueOf("11", 16));
    assertEquals(expectedNumber2, Integer.valueOf("A", 16));
    assertEquals(expectedNumber3, Integer.valueOf("7", 8));
}
```

## 4. parseInt()和valueOf()的区别

总而言之，这里是valueOf () 和parseInt()方法之间的主要区别：

|               Integer.valueOf()               |                 Integer.parseInt()                 |
| :---------------------------------------------: | :--------------------------------------------------: |
|            它返回一个Integer对象。            |               它返回一个原始的int。                |
|       此方法接受String和int作为参数。       |            此方法仅接受String作为参数。            |
|   它在其方法实现中使用Integer.parseInt() 。   |       它不使用任何辅助方法将字符串解析为整数。       |
| 此方法接受一个字符作为参数并返回其 Unicode 值。 | 此方法将在将字符作为参数传递时产生不兼容的类型错误。 |

## 5.总结

在本文中，我们了解了java.lang.Integer类的parseInt()和valueOf()方法的不同实现。我们还研究了这两种方法之间的差异。
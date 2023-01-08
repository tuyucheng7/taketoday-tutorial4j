## 1. 概述

在本教程中，我们将简要介绍在Java中以二进制格式打印整数的不同方法。

首先，我们从概念上看一下。然后，我们将学习一些用于转换的内置Java函数。

## 2.使用整数到二进制的转换

在本节中，我们将编写自定义方法以在Java中将整数转换为二进制格式的字符串。在写代码之前，我们先了解一下如何将一个整数转换成二进制格式。

要将整数n转换为二进制格式，我们需要：

1.  存储数n除以 2 的余数，并用商的值更新数n
2.  重复步骤 1 直到数字 n 大于零
3.  最后，以相反的顺序打印余数

让我们看一个将 7 转换为等效的二进制格式的示例：

1.  首先，将 7 除以 2：余数 1，商 3
2.  二、3除以2：余数1，商1
3.  然后，1 除以 2：余数 1，商 0
4.  最后，倒序打印余数，因为上一步的商是 0: 111

接下来我们来实现上面的算法：

```java
public static String convertIntegerToBinary(int n) {
    if (n == 0) {
        return "0";
    }
    StringBuilder binaryNumber = new StringBuilder();
    while (n > 0) {
        int remainder = n % 2;
        binaryNumber.append(remainder);
        n /= 2;
    }
    binaryNumber = binaryNumber.reverse();
    return binaryNumber.toString();
}
```

## 3.使用整数#toBinaryString方法

Java 的[Integer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html)类有一个名为[toBinaryString](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#toBinaryString(int))的方法，用于将整数转换为其等效的二进制字符串。

让我们看一下Integer # toBinaryString方法的签名：

```java
public static String toBinaryString(int i)
```

它接受一个整数参数并返回该整数的二进制字符串表示形式：

```java
int n = 7;
String binaryString = Integer.toBinaryString(n);
assertEquals("111", binaryString);
```

## 4.使用整数#toString方法

[现在，让我们看一下Integer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#toString(int,int))[ # ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#toString(int,int))[toString](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#toString(int,int))方法的签名：

```java
public static String toString(int i, int radix)
```

Integer #toString方法是Java中的一个内置方法，它有两个参数。首先，它需要一个要转换为字符串的整数。其次，它采用将整数转换为字符串表示时要使用的基数。

它以基数指定的基数返回整数输入的字符串表示形式。

让我们使用此方法将基数值为 2 的整数转换为二进制格式：

```java
int n = 7;
String binaryString = Integer.toString(n, 2);
assertEquals("111", binaryString);
```

正如我们所看到的，我们在调用Integer#toString方法将整数n转换为其二进制字符串表示形式时传递了基数值 2。

## 5.总结

总之，我们研究了整数到二进制的转换。此外，我们还看到了几个内置的Java方法，可以将整数转换为二进制格式的字符串。
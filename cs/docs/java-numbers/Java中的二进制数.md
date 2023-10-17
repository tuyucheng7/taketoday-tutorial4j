## 1. 概述

二进制数系统使用 0 和 1 来表示数字。计算机使用二进制数来存储任何数据并对其执行操作。

在本教程中，我们将学习如何将二进制转换为十进制，反之亦然。此外，我们将对它们执行加法和减法。

## 2. 二进制文字

Java 7 引入了二进制文字。它简化了二进制数的使用。

要使用它，我们需要在数字前加上 0B 或 0b：

```java
@Test
public void given_binaryLiteral_thenReturnDecimalValue() {

    byte five = 0b101;
    assertEquals((byte) 5, five);

    short three = 0b11;
    assertEquals((short) 3, three);

    int nine = 0B1001;
    assertEquals(9, nine);

    long twentyNine = 0B11101;
    assertEquals(29, twentyNine);

    int minusThirtySeven = -0B100101;
    assertEquals(-37, minusThirtySeven);

}
```

## 3.二进制数转换

在本节中，我们将学习如何将二进制数转换为十进制格式，反之亦然。在这里，我们将首先使用内置的Java函数进行转换，然后我们将为此编写自定义方法。

### 3.1. 十进制转二进制数

Integer有一个名为toBinaryString 的函数，用于将十进制数转换为二进制字符串：

```java
@Test
public void given_decimalNumber_then_convertToBinaryNumber() {
    assertEquals("1000", Integer.toBinaryString(8));
    assertEquals("10100", Integer.toBinaryString(20));
}
```

现在，我们可以尝试为这种转换编写自己的逻辑。在写代码之前，我们先了解一下如何将十进制数转换成二进制数。

要将十进制数 n转换为二进制格式，我们需要：

1.  将n除以 2，记下商q和余数r
2.  将q除以2，记下商和余数
3.  重复步骤 2，直到商为 0
4.  以相反的顺序连接所有余数

让我们看一个将 6 转换成等效的二进制格式的示例：

1.  首先，6 除以 2：商 3，余数 0
2.  然后，将 3 除以 2：商 1，余数 1
3.  最后，将 1 除以 2：商 0，余数 1
4.  110

现在让我们实现上面的算法：

```java
public Integer convertDecimalToBinary(Integer decimalNumber) {

    if (decimalNumber == 0) {
        return decimalNumber;
    }

    StringBuilder binaryNumber = new StringBuilder();
    Integer quotient = decimalNumber;

    while (quotient > 0) {
        int remainder = quotient % 2;
        binaryNumber.append(remainder);
        quotient /= 2;
    }

    binaryNumber = binaryNumber.reverse();
    return Integer.valueOf(binaryNumber.toString());
}
```

### 3.2. 二进制转十进制数

为了解析二进制字符串， Integer类提供了一个 parseInt函数：

```java
@Test
public void given_binaryNumber_then_ConvertToDecimalNumber() {
    assertEquals(8, Integer.parseInt("1000", 2));
    assertEquals(20, Integer.parseInt("10100", 2));
}
```

这里， parseInt函数接受两个参数作为输入：

1.  要转换的二进制字符串
2.  必须转换输入字符串的数字系统的基数或基数

现在，让我们尝试编写自己的逻辑将二进制数转换为十进制数：

1.  从最右边的数字开始
2.  将每个数字乘以该数字的 2^{position} - 此处，最右边数字的位置为零，并且随着我们向左侧移动而增加
3.  将所有乘法的结果相加得到最终的十进制数

再一次，让我们看看我们的方法：

1.  首先，101011 = (12^5) + (02^4) + (12^3) + (02^2) + (12^1) + (12^0 )
2.  接下来，101011 = (132) + (016) + (18) + (04) + (12) + (11)
3.  那么，101011 = 32 + 0 + 8 + 0 + 2 + 1
4.  最后，101011 = 43

让我们最后对上述步骤进行编码：

```java
public Integer convertBinaryToDecimal(Integer binaryNumber) {

    Integer decimalNumber = 0;
    Integer base = 1;

    while (binaryNumber > 0) {
        int lastDigit = binaryNumber % 10;
        binaryNumber = binaryNumber / 10;
        decimalNumber += lastDigit  base;
        base = base  2;
    }
    return decimalNumber;
}
```

## 4.算术运算

在本节中，我们将专注于对二进制数执行算术运算。

### 4.1. 添加

就像小数加法一样，我们从最右边的数字开始加法。

在添加两个二进制数字时，我们需要记住以下规则：

-   0 + 0 = 0
-   0 + 1 = 1
-   1 + 1 = 10 
-   1 + 1 + 1 = 11 

这些规则可以实现为：

```java
public Integer addBinaryNumber(Integer firstNum, Integer secondNum) {
    StringBuilder output = new StringBuilder();
    int carry = 0;
    int temp;
    while (firstNum != 0 || secondNum != 0) {
        temp = (firstNum % 10 + secondNum % 10 + carry) % 2;
        output.append(temp);

        carry = (firstNum % 10 + secondNum % 10 + carry) / 2;
        firstNum = firstNum / 10;
        secondNum = secondNum / 10;
    }
    if (carry != 0) {
        output.append(carry);
    }
    return Integer.valueOf(output.reverse().toString());
}
```

### 4.2. 减法

二进制数的减法有很多种。在本节中，我们将学习一个补码方法来做减法。

让我们首先了解什么是数字的补码。

一个数的补数是将二进制数的每一位取反得到的数。这意味着只需将 1 替换为 0 并将 0 替换为 1：

```java
public Integer getOnesComplement(Integer num) {
    StringBuilder onesComplement = new StringBuilder();
    while (num > 0) {
        int lastDigit = num % 10;
        if (lastDigit == 0) {
            onesComplement.append(1);
        } else {
            onesComplement.append(0);
        }
        num = num / 10;
    }
    return Integer.valueOf(onesComplement.reverse().toString());
}
```

要使用补码对两个二进制数进行减法，我们需要：

1.  计算减数 s的补码
2.  添加 s和被减数
3.  如果在步骤 2 中生成进位，则将该进位添加到步骤 2 的结果中以获得最终答案。
4.  如果第2步没有产生进位，则第2步结果的补码就是最终答案。但在这种情况下，答案是否定的

让我们实现上面的步骤：

```java
public Integer substractBinaryNumber(Integer firstNum, Integer secondNum) {
    int onesComplement = Integer.valueOf(getOnesComplement(secondNum));
    StringBuilder output = new StringBuilder();
    int carry = 0;
    int temp;
    while (firstNum != 0 || onesComplement != 0) {
        temp = (firstNum % 10 + onesComplement % 10 + carry) % 2;
        output.append(temp);
        carry = (firstNum % 10 + onesComplement % 10 + carry) / 2;

        firstNum = firstNum / 10;
        onesComplement = onesComplement / 10;
    }
    String additionOfFirstNumAndOnesComplement = output.reverse().toString();
    if (carry == 1) {
        return addBinaryNumber(Integer.valueOf(additionOfFirstNumAndOnesComplement), carry);
    } else {
        return getOnesComplement(Integer.valueOf(additionOfFirstNumAndOnesComplement));
    }
}
```

## 5.总结

在本文中，我们学习了如何将二进制数转换为十进制数，反之亦然。然后，我们对二进制数进行加减等算术运算。
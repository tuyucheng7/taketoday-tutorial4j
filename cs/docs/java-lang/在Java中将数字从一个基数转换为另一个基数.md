## 一、概述

在本教程中，我们将探讨如何**在 Java 中将数字从一种基数转换为另一种基数**。将数字从以 2 为基数转换为以 5 为基数的数字有两种方法，反之亦然。

## 2.*整数*类

*java.lang*包中有*Integer*类，它是一个包装类，它将原始类型*int*包装为*Integer*对象。这个类有几个方法用于操作*int*和将 int 转换为*String*对象以及将*String*转换为*int*类型。***需要parseInt()\*****和*****toString()\*****方法来帮助我们将数字从一个基数转换为另一个****基数**。

### 2.1. *parseInt()*方法

*parseInt()*方法有两个参数：*String* s 和*int* *radix：*

```java
public static int parseInt(String s, int radix)复制
```

**它以作为第二个参数给出的指定基数返回给定字符串参数的整数值**。字符串参数必须是指定基数中的数字。[它还会在Java 官方文档](https://docs.oracle.com/javase/7/docs/api/java/lang/Integer.html#toString())中提到的情况下抛出*NumberFormatException*。

### 2.2. *toString()*方法

*toString()* 方法与前面提到的parseInt *()*方法结合使用，将数字从一种基数转换为另一种基数：

```java
public static String toString(int i, int radix)复制
```

根据[它的签名，](https://docs.oracle.com/javase/7/docs/api/java/lang/Integer.html#toString())这个方法有两个参数，*int* *i*和一个int类型的*基数**。* **该方法返回一个字符串，表示指定基数的值，即第二个参数**。在未提供第二个参数的情况下，它使用基数 10 作为默认值。

## *3. 使用整数*类方法*parseInt()* 和*toString()*转换数字基数

正如我们之前所暗示的，在 Java 中有两种方法可以将数字从一种基数转换为另一种基数。第一种也是最简单的方法是使用*Integer*类方法*parseInt()*和*toString()*进行从给定基数到目标基数的转换次数。让我们创建一个同时使用*parseInt()*和*toString()*进行基本转换的方法：

```java
public static String convertNumberToNewBase(String number, int base, int newBase){
    return Integer.toString(Integer.parseInt(number, base), newBase);
}复制
```

我们的方法*convertNumberToNewBase()接受三个参数，一个表示指定基数中数字的字符串，或者第二个参数类型为**int*的基数系统。第三个参数是我们转换的新基数的一个*整数* 。该方法返回新基数中的字符串。*parseInt()*接受字符串参数及其基数并返回一个整数值。*此整数值作为toString()*方法的第一个参数传递，该方法将整数转换为第二个参数中给定的新基数中的字符串。

让我们看一个例子：

```java
@Test
void whenConvertingBase10NumberToBase5_ThenResultShouldBeDigitsInBase5() {
    assertEquals(convertNumberToNewBase("89", 10, 5), "324");
}复制
```

字符串*“89”*以 10 为基数给出，用于转换为基数 5。我们的方法返回字符串结果*“324”，*这确实是一个基数 5 数字系统中的数字。

## 4. 使用自定义方法转换数基

我们在数字基数之间进行转换的另一种方法是用 Java 编写我们自己的自定义方法来执行此任务。建议的方法应具有三个参数，一串数字，一个指定基数的*int*和另一个表示要转换的新基数的*int 。*

实现这一目标的一种合乎逻辑的方法是创建子方法来处理我们的数字转换代码的较小方面。我们将定义一种方法，用于将任何数字从 2 进制转换为 9 和 16 为十进制（以 10 为基数），另一种方法用于将数字从 10 进制转换为 2 进制为 9 和 16：

```java
public static String convertFromDecimalToBaseX(int num, int newBase) throws IllegalArgumentException {
    if ((newBase < 2 || newBase > 10) && newBase != 16) {
        throw new IllegalArgumentException("New base must be from 2 - 10 or 16");
    }
    String result = "";
    int remainder;
    while (num > 0) {
        remainder = num % newBase;
        if (newBase == 16) {
            if (remainder == 10) {
                result += 'A';
            } else if (remainder == 11) {
                result += 'B';
            } else if (remainder == 12) {
                result += 'C';
            } else if (remainder == 13) {
                result += 'D';
            } else if (remainder == 14) {
                result += 'E';
            } else if (remainder == 15) {
                result += 'F';
            } else {
                result += remainder;
            }
        } else {
            result += remainder;
        }
        num /= newBase;
    }
    return new StringBuffer(result).reverse().toString();
}复制
```

convertFromDecimalToBaseX *()*方法有两个参数，一个整数和一个用于转换的新基数。**将整数连续除以新基数，取余数得到结果**。该方法还有一个条件，用于 16 进制数。另一种方法使用将 base 16 char 字符转换为十进制值的子方法将任何给定的 base 转换为 base 10：

```java
public static int convertFromAnyBaseToDecimal(String num, int base) {
    if (base < 2 || (base > 10 && base != 16)) {
        return -1;
    }
    int val = 0;
    int power = 1;
    for (int i = num.length() - 1; i >= 0; i--) {
        int digit = charToDecimal(num.charAt(i));
        if (digit < 0 || digit >= base) {
            return -1;
        }
        val += digit * power;
        power = power * base;
    }
    return val;
}
public static int charToDecimal(char c) {
    if (c >= '0' && c <= '9') {
        return (int) c - '0';
    } else {
        return (int) c - 'A' + 10;
    }
}复制
```

我们将这些方法结合起来，得到一个强大的方法，可以将数字从任何基数转换为另一个基数：

```java
public static String convertNumberToNewBaseCustom(String num, int base, int newBase) {
    int decimalNumber = convertFromAnyBaseToDecimal(num, base);
    String targetBase = "";
    try {
        targetBase = convertFromDecimalToBaseX(decimalNumber, newBase);
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
    }
    return  targetBase;
}复制
```

让我们测试我们的自定义方法以确认它按预期工作：

```java
@Test
void whenConvertingBase2NumberToBase8_ThenResultShouldBeDigitsInBase8() {
    assertEquals(convertNumberToNewBaseCustom("11001000", 2, 8), "310");
}复制
```

将2 进制的字符串“11001000”转换为 8 进制时，该方法返回字符串*“310” 。*

## 5.结论

在本教程中，我们学习了如何使用*java.lang.Integer*类方法*toString()*和*parseInt() 在 Java 中将数字从一种基数转换为另一种基数。*我们将这两个方法放到另一个方法中以实现可重用性。我们还更进一步，编写了我们自己的自定义方法，将一串数字转换为以 10 为底的等效数字，然后将其转换为另一个数字基数。
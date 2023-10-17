## 1. 概述

在这个简短的教程中，我们将讨论自守数并学习几种在Java程序中找到它们的方法。

## 2. 什么是自守数？

自守数是一个数字，其平方末尾的数字与数字本身相同。

例如，25 是自守数，因为 25 的平方是 625，末尾是 25。同样，76 是自守数，因为 76 的平方是 5776，末尾也是 76。

在数学中，自守数也称为循环数。

自守数的更多示例是 0、1、5、6、25、76、376、625、9376 等。

0 和 1 被称为平凡自守数，因为它们在每个基数中都是自守数。

## 3. 判断一个数是否自守

有许多算法可用于确定数字是否自守。接下来，我们将看到几种方法。

### 3.1. 遍历数字并进行比较

这是确定数字是否自守的一种方法：

1.  得到数字并计算它的平方
2.  获取方块的最后一位并将其与数字的最后一位进行比较
    -   如果最后一位数字不相等，则该数不是自守数
    -   如果最后一位数字相等，则转到下一步
3.  删除数字和平方的最后一位
4.  重复步骤 2 和 3，直到比较数字的所有数字

上述方法以相反的方式循环输入数字的数字。

让我们编写一个Java程序来实现这种方法。isAutomorphicUsingLoop ()方法接受一个整数作为输入并检查它是否是自守的：

```java
public boolean isAutomorphicUsingLoop(int number) {
    int square = number  number;

    while (number > 0) {
        if (number % 10 != square % 10) {
            return false;
        }
        number /= 10;
        square /= 10;
    }
    
    return true;
}
```

在这里，我们首先计算number的平方。然后，我们遍历 number 的数字并将其最后一位数字与square的最后一位数字一一比较。

在任何阶段，如果最后的数字不相等，我们将返回false并退出该方法。否则，我们去掉最后相等的数字并对number的剩余数字重复该过程。

让我们测试一下：

```java
assertTrue(AutomorphicNumber.isAutomorphicUsingLoop(76));
assertFalse(AutomorphicNumber.isAutomorphicUsingLoop(9));
```

### 3.2. 直接比较数字

我们还可以用更直接的方式判断一个数是否自守：

1.  获取数字并计算位数 ( n )

2.  计算数字的平方

3.  从正方形中 获取最后

    n 位数字

    

    -   如果正方形的最后n 位数字是原始数字，则该数字是自守的
    -   否则它不是自守数

在这种情况下，我们不需要遍历数字的数字。相反，我们可以使用编程框架的现有库。

我们可以使用[Math](https://www.baeldung.com/java-lang-math)类来执行数字运算，例如计算给定数字中的数字并从其平方中获取最后的数字：

```java
public boolean isAutomorphicUsingMath(int number) {
    int square = number  number;

    int numberOfDigits = (int) Math.floor(Math.log10(number) + 1);
    int lastDigits = (int) (square % (Math.pow(10, numberOfDigits)));

    return number == lastDigits;
}
```

与第一种方法类似，我们从计算number的平方开始。然后，我们不是一个一个地比较number和square的最后一位，而是使用[Math.floor()](https://www.baeldung.com/java-lang-math#floor)一次获得number中的总数numberOfDigits。之后，我们使用[Math.pow()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Math.html#pow(double,double))从正方形中提取尽可能多的数字。最后，我们将输入数字与提取的数字lastDigits进行比较。

如果number和lastDigits相等，则该数字是自守的，我们返回true，否则，我们返回false。

让我们测试一下：

```java
assertTrue(AutomorphicNumber.isAutomorphicUsingMath(76));
assertFalse(AutomorphicNumber.isAutomorphicUsingMath(9));
```

## 4。总结

在本文中，我们探讨了自守数。我们还研究了几种确定数字是否为自守数的方法以及相应的Java程序。
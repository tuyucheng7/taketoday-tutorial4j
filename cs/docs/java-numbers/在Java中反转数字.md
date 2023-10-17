## 1. 概述

在本教程中，我们将了解如何在Java中使用数学方法反转数字。首先，我们将了解执行此操作所需的数学运算，然后我们将通过三种不同的方式来实现它。

## 2.解决方法

首先，让我们举个例子看看到底会发生什么。例如，我们希望数字 1234 变成 4321。这可以通过以下方法实现：

1.  获取数字的最后一位
    -   我们可以应用模数来获得最后一位数字
    -   第一次迭代 – 1234 % 10 = 4
    -   第二次迭代 – 123 % 10 = 3
2.  将反转后的数字乘以 10 并添加上一步中找到的数字
    -   第一次迭代 – 0  10 + 4 = 4(因为没有反向数字开始，我们在第一次迭代中乘以 0)
    -   第二次迭代 – 4  10 + 3 = 43
3.  将原数除以10，从第一步开始重复，直到数不为0
    -   第一次迭代 – 1234 / 10 = 123
    -   第二次迭代 – 123 / 10 = 12

## 3. 数学实现

我们想将上面的数学运算转换成代码。这可以通过三种不同的方式实现：使用while循环、for循环或递归。

下面的方法还通过使用要反转的数字的绝对值并在原始数字为负数的情况下将反转后的数字乘以 -1 来迎合负值。

### 3.1. while循环

[while](https://www.baeldung.com/java-while-loop)循环在列表中排在第一位，因为它是转换上述数学运算的最清晰方法：

```java
int reversedNumber = 0;
int numberToReverse = Math.abs(number);

while (numberToReverse > 0) {
    int mod = numberToReverse % 10;
    reversedNumber = reversedNumber  10 + mod;
    numberToReverse /= 10;
}

return number < 0 ? reversedNumber  -1 : reversedNumber;
```

### 3.2. for循环

使用[for](https://www.baeldung.com/java-for-loop)循环，逻辑与前面相同。我们跳过for循环的初始化语句，并在终止条件中使用正在反转的数字：

```java
int reversedNumber = 0;
int numberToReverse = Math.abs(number);

for (; numberToReverse > 0; numberToReverse /= 10) {
    int mod = numberToReverse % 10;
    reversedNumber = reversedNumber  10 + mod;
}

return number < 0 ? reversedNumber  -1 : reversedNumber;
```

### 3.3. 递归

对于[递归](https://www.baeldung.com/java-recursion)，我们可以使用一个包装器方法来调用返回反转数字的递归方法：

```java
int reverseNumberRecWrapper(int number) {
    int output = reverseNumberRec(Math.abs(number), 0);
    return number < 0 ? output  -1 : output;
}
```

递归方法以与前面示例相同的方式实现数学运算：

```java
int reverseNumberRec(int numberToReverse, int recursiveReversedNumber) {

    if (numberToReverse > 0) {
        int mod = numberToReverse % 10;
        recursiveReversedNumber = recursiveReversedNumber  10 + mod;
        numberToReverse /= 10;
        return reverseNumberRec(numberToReverse, recursiveReversedNumber);
    }

    return recursiveReversedNumber;
}
```

递归方法在每次迭代中返回当前反转的数字，并且每次迭代缩短要反转的数字。这种情况一直发生到要反转的数字为 0，此时我们返回完全反转的数字。

## 4。总结

在本文中，我们探讨了使用while循环、for循环和递归来反转数字的三种不同实现。
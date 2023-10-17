## 1. 概述

在本文中，我们将了解如何找到小于给定数字的最大 2 次方。

对于我们的示例，我们将采用样本输入 9。2 0为 1，我们可以找到小于给定输入的 2 次方的最小有效输入为 2。因此，我们将仅将大于 1 的输入视为有效的。

## 2. 朴素的方法

让我们从 2 0开始，它是 1，我们将继续将该数字乘以 2，直到找到一个小于输入的数字：

```java
public long findLargestPowerOf2LessThanTheGivenNumber(long input) {
    Assert.isTrue(input > 1, "Invalid input");
    long firstPowerOf2 = 1;
    long nextPowerOf2 = 2;
    while (nextPowerOf2 < input) {
        firstPowerOf2 = nextPowerOf2;
        nextPowerOf2 = nextPowerOf2  2;
    }
    return firstPowerOf2;
}
```

让我们了解示例输入= 9 的代码。

firstPowerOf2的初始值为1，nextPowerOf2为 2。正如我们所见，2 < 9 为真，我们进入了 while 循环。

对于第一次迭代，firstPowerOf2为 2，nextPowerOf2为 2  2 = 4。同样 4 < 9 所以让我们继续 while 循环。

对于第二次迭代，firstPowerOf2为 4，nextPowerOf2为 4  2 = 8。现在 8 < 9，让我们继续。

对于第三次迭代，firstPowerOf2为 8，nextPowerOf2为 8  2 = 16。while 条件 16 < 9 为假，因此跳出 while 循环。8是小于9的2的最大次方。

让我们运行一些测试来验证我们的代码：

```java
assertEquals(8, findPowerOf2LessThanTheGivenNumber(9));
assertEquals(16, findPowerOf2LessThanTheGivenNumber(32));

```

我们解决方案的时间复杂度为 O(log 2 (N))。在我们的例子中，我们迭代了 log 2 (9) = 3 次。

## 3. 使用Math.log

Log base 2 将给出我们可以递归地将一个数除以 2 的次数，换句话说，一个数的log 2给出 2 的幂。让我们看一些例子来理解这一点。

log 2 (8) = 3 和 log 2 (16) = 4。通常，我们可以看到 y = log 2 x 其中 x = 2 y。

因此，如果我们找到一个可以被 2 整除的数字，我们会从中减去 1，以避免出现该数字是 2 的完美幂的情况。

[Math.log](https://www.baeldung.com/java-lang-math)是 log 10。要计算 log 2 (x)，我们可以使用公式 log 2 (x)=log 10 (x)/log 10 (2)

让我们把它放在代码中：

```java
public long findLargestPowerOf2LessThanTheGivenNumberUsingLogBase2(long input) {
    Assert.isTrue(input > 1, "Invalid input");
    long temp = input;
    if (input % 2 == 0) {
        temp = input - 1;
    }
    long power = (long) (Math.log(temp) / Math.log(2));
    long result = (long) Math.pow(2, power);
    return result;
}
```

假设我们的样本输入为9，temp的初始值为9。

9 % 2 是 1，所以我们的临时变量是 9。 这里我们使用模除法，这将给出 9/2 的余数。

为了找到 log 2 (9)，我们做 log 10 (9) / log 10 (2) = 0.95424 / 0.30103 ~= 3。

现在，结果是 2 3即 8。

让我们验证我们的代码：

```java
assertEquals(8, findLargestPowerOf2LessThanTheGivenNumberUsingLogBase2(9));
assertEquals(16, findLargestPowerOf2LessThanTheGivenNumberUsingLogBase2(32));
```

实际上，Math.pow将执行与方法 1 中相同的迭代。因此我们可以说对于此解决方案，时间复杂度也是 O(Log 2 (N))。

## 4. 位技术

对于这种方法，我们将使用位移技术。首先，考虑到我们有 4 位来表示数字，让我们看一下 2 的幂的二进制表示

| 20   | 1    | 0001 |
| ---- | ---- | ------ |
| 21   | 2    | 0010 |
| 22   | 4    | 0100 |
| 23   | 8    | 1000 |

仔细观察，我们可以观察到我们可以通过左移 1 的字节来计算 2 的幂。IE。2 2是将字节左移 1 位 2 位，依此类推。

让我们使用位移技术编写代码：

```java
public long findLargestPowerOf2LessThanTheGivenNumberUsingBitShiftApproach(long input) {
    Assert.isTrue(input > 1, "Invalid input");
    long result = 1;
    long powerOf2;
    for (long i = 0; i < Long.BYTES  8; i++) {
        powerOf2 = 1 << i;
        if (powerOf2 >= input) {
            break;
        }
        result = powerOf2;
    }
    return result;
}
```

在上面的代码中，我们使用long作为我们的数据类型，它使用 8 个字节或 64 位。所以我们将计算 2 到 2 64的幂。我们正在使用位移运算符<<来计算 2 的幂。对于我们的示例输入 9，在第 4次迭代之后，powerOf2的值= 16 和结果= 8，我们在 16 > 9 时跳出循环输入。

让我们检查一下我们的代码是否按预期工作：

```java
assertEquals(8, findLargestPowerOf2LessThanTheGivenNumberUsingBitShiftApproach(9));
assertEquals(16, findLargestPowerOf2LessThanTheGivenNumberUsingBitShiftApproach(32));
```

这种方法的最坏情况时间复杂度也是 O(log 2 (N))，类似于我们在第一种方法中看到的情况。但是，这种方法更好，因为位移运算比乘法更有效。

## 5. 按位与

对于我们的下一个方法，我们将使用这个公式2 n AND 2 n -1 = 0。

让我们看一些例子来理解它是如何工作的。

4的二进制表示是0100，3是0011。

让我们对这两个数字进行[按位与操作。](https://www.baeldung.com/java-bitwise-operators)0100和0011是0000。我们可以对任何 2 的幂和小于它的数说同样的话。让我们以 16 (2 4 ) 和 15 为例，分别表示为1000和0111。同样，我们看到这两个数的按位与运算结果为 0。我们也可以说，除这 2 之外的任何其他数字的与运算结果都不会为 0。

让我们看看使用按位与解决这个问题的代码：

```java
public long findLargestPowerOf2LessThanTheGivenNumberUsingBitwiseAnd(long input) { 
    Assert.isTrue(input > 1, "Invalid input");
    long result = 1;
    for (long i = input - 1; i > 1; i--) {
        if ((i & (i - 1)) == 0) {
            result = i;
            break;
        }
    }
    return result;
}
```

在上面的代码中，我们遍历小于输入的数字。每当我们找到一个数字的按位与且 number-1 为零时，我们就会跳出循环，因为我们知道该数字将是 2 的幂。在本例中，对于我们的样本输入9，我们跳出循环当i = 8 且i – 1 = 7 时。

现在，让我们验证几个场景：

```java
assertEquals(8, findLargestPowerOf2LessThanTheGivenNumberUsingBitwiseAnd(9));
assertEquals(16, findLargestPowerOf2LessThanTheGivenNumberUsingBitwiseAnd(32));
```

当输入为 2 的精确幂时，这种方法的最坏情况时间复杂度为 O(N/2)。如我们所见，这不是最有效的解决方案，但了解这种技术是件好事，因为它可能会出现解决类似问题时很方便。

## 六，总结

我们已经看到了寻找小于给定数的最大 2 次方的不同方法。我们还注意到按位运算在某些情况下如何简化计算。
## 1. 概述

在本快速教程中，我们将探讨在 Java中获取整数位数的不同方法。

我们还将分析不同的方法，以确定哪种算法最适合每种情况。

## 延伸阅读：

## [如何在Java中将数字四舍五入到 N 位小数](https://www.baeldung.com/java-round-decimal-number)

在Java中处理常见的十进制数舍入问题的几种方法概述

[阅读更多](https://www.baeldung.com/java-round-decimal-number)→

## [在Java中检查字符串是否为数字](https://www.baeldung.com/java-check-string-number)

探索确定字符串是否为数字的不同方法。

[阅读更多](https://www.baeldung.com/java-check-string-number)→

## [十进制格式实用指南](https://www.baeldung.com/java-decimalformat)

探索Java的 DecimalFormat 类及其实际用法。

[阅读更多](https://www.baeldung.com/java-decimalformat)→

## 2.整数的位数

对于这里讨论的方法，我们只考虑正整数。如果我们期望任何负输入，那么我们可以在使用任何这些方法之前先使用Math.abs(number) 。

### 2.1. 基于字符串的解决方案

获取Integer中位数的最简单方法可能是将其转换为String并调用length()方法。这将返回我们数字的字符串表示形式的长度：

```java
int length = String.valueOf(number).length();
```

但是，这可能不是最佳方法，因为此语句涉及为每个评估的String分配内存。JVM 必须解析我们的数字并将其数字到一个单独的字符串中，并执行许多其他不同的操作(如保留临时副本、处理 Unicode 转换等)。

如果我们只有几个数字要评估，那么我们可以使用此解决方案，因为此方法与任何其他方法之间的差异可以忽略不计，即使对于大数字也是如此。

### 2.2. 对数法

对于以十进制形式表示的数字，如果我们将它们的对数以 10 为底并向上舍入，我们将得到该数字的位数：

```java
int length = (int) (Math.log10(number) + 1);
```

请注意，未定义任何数字的log 10 0 ，因此如果我们期望任何值为0的输入，那么我们也可以对其进行检查。

对数方法比基于字符串的方法快得多，因为它不必经过任何数据转换过程。它只涉及一个简单、直接的计算，没有任何额外的对象初始化或循环。

### 2.3. 重复乘法

在这个方法中，我们将使用一个临时变量(初始化为 1)并不断地将它乘以 10，直到它变得大于我们的数字。在此过程中，我们还将使用一个长度变量，它将跟踪数字的长度：

```java
int length = 0;
long temp = 1;
while (temp <= number) {
    length++;
    temp = 10;
}
return length;
```

在此代码中，temp = 10与编写temp = (temp << 3) + (temp << 1)相同。由于与移位运算符相比，乘法运算在某些处理器上通常是一项成本更高的运算，因此后者可能更高效一些。

### 2.4. 除以二的幂

如果我们知道我们的数字范围，那么我们可以使用一个变体来进一步减少我们的比较。此方法将数字除以 2 的幂(例如 1、2、4、8 等)：

```java
int length = 1;
if (number >= 100000000) {
    length += 8;
    number /= 100000000;
}
if (number >= 10000) {
    length += 4;
    number /= 10000;
}
if (number >= 100) {
    length += 2;
    number /= 100;
}
if (number >= 10) {
    length += 1;
}
return length;
```

它利用了任何数字都可以表示为2的幂的加法这一事实。例如，15可以表示为8+4+2+1，它们都是2的幂。

对于 15 位数字，我们将在之前的方法中进行 15 次比较，而在这种方法中仅进行 4 次比较。

### 2.5. 分而治之

与此处描述的所有其他方法相比，这可能是最庞大的方法；但是，它也是 最快的，因为我们不执行任何类型的转换、乘法、加法或对象初始化。

我们可以在三四个简单的if语句中得到答案：

```java
if (number < 100000) {
    if (number < 100) {
        if (number < 10) {
            return 1;
        } else {
            return 2;
        }
    } else {
        if (number < 1000) {
            return 3;
        } else {
            if (number < 10000) {
                return 4;
            } else {
                return 5;
            }
        }
    }
} else {
    if (number < 10000000) {
        if (number < 1000000) {
            return 6;
        } else {
            return 7;
        }
    } else {
        if (number < 100000000) {
            return 8;
        } else {
            if (number < 1000000000) {
                return 9;
            } else {
                return 10;
            }
        }
    }
}
```

与前面的方法类似，如果我们知道数字的范围，我们只能使用这种方法。

## 3. 基准测试

现在我们已经很好地了解了潜在的解决方案，让我们使用[Java Microbenchmark Harness (JMH)](https://www.baeldung.com/java-microbenchmark-harness)对我们的方法进行一些简单的基准测试。

下表显示了每个操作的平均处理时间(以纳秒为单位)：

```plaintext
Benchmark                            Mode  Cnt   Score   Error  Units
Benchmarking.stringBasedSolution     avgt  200  32.736 ± 0.589  ns/op
Benchmarking.logarithmicApproach     avgt  200  26.123 ± 0.064  ns/op
Benchmarking.repeatedMultiplication  avgt  200   7.494 ± 0.207  ns/op
Benchmarking.dividingWithPowersOf2   avgt  200   1.264 ± 0.030  ns/op
Benchmarking.divideAndConquer        avgt  200   0.956 ± 0.011  ns/op
```

基于String的解决方案是最简单的，也是成本最高的操作，因为它是唯一需要数据转换和新对象初始化的解决方案。

对数方法比以前的解决方案更有效，因为它不涉及任何数据转换。此外，作为单行解决方案，它可以很好地替代基于字符串的方法。

重复乘法涉及与数字长度成比例的简单乘法；例如，如果一个数字的长度为 15 位，则此方法将涉及 15 次乘法。

然而，下一个方法利用了每个数字都可以用 2 的幂表示的事实(类似于 BCD 的方法)。它将同一个等式简化为四次除法运算，因此比前者效率更高。

最后，正如我们可以推断的那样，最有效的算法是冗长的分而治之实现，它仅用三到四个简单的if语句即可提供答案。如果我们有大量需要分析的数字数据集，我们可以使用它。

## 4。总结

在这篇简短的文章中，我们概述了一些查找整数位数的方法，并比较了每种方法的效率。
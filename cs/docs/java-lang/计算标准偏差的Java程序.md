## 1. 概述

[标准偏差](https://en.wikipedia.org/wiki/Standard_deviation)(符号为 sigma – σ)是衡量数据围绕平均值分布的程度。

在这个简短的教程中，我们将了解如何在Java中计算标准偏差。

## 2. 计算标准偏差

标准偏差使用 ( ∑ ( Xi – ų ) ^ 2 ) / N 的公式平方根计算，其中：

-   ∑为各元素之和
-   Xi是数组的每个元素
-   ų 是数组元素的平均值
-   N 是元素的数量

我们可以借助Java的[Math](https://www.baeldung.com/java-lang-math)类轻松计算标准偏差：

```java
public static double calculateStandardDeviation(double[] array) {

    // get the sum of array
    double sum = 0.0;
    for (double i : array) {
        sum += i;
    }

    // get the mean of array
    int length = array.length;
    double mean = sum / length;

    // calculate the standard deviation
    double standardDeviation = 0.0;
    for (double num : array) {
        standardDeviation += Math.pow(num - mean, 2);
    }

    return Math.sqrt(standardDeviation / length);
}
```

让我们测试一下我们的方法：

```java
double[] array = {25, 5, 45, 68, 61, 46, 24, 95};
System.out.println("List of elements: " + Arrays.toString(array));

double standardDeviation = calculateStandardDeviation(array);
System.out.format("Standard Deviation = %.6f", standardDeviation);
```

结果将如下所示：

```plaintext
List of elements: [25.0, 5.0, 45.0, 68.0, 61.0, 46.0, 24.0, 95.0]
Standard Deviation = 26.732179
```

## 3.总结

在本快速教程中，我们学习了如何在Java中计算标准偏差。
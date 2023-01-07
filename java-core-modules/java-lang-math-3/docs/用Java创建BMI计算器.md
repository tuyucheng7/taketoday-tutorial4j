## 1. 概述

在本教程中，我们将使用Java创建一个 BMI 计算器。

在继续实施之前，首先让我们了解 BMI 的概念。

## 2. 什么是体重指数？

BMI 代表身体质量指数。它是从个人身高和体重得出的值。

借助BMI，我们可以了解一个人的体重是否健康。

我们来看看BMI的计算公式：

BMI = 体重(千克)/(身高(米) 身高(米))

根据 BMI 范围将一个人分类为体重不足、正常、超重或肥胖：

| 体重指数范围 |  类别  |
| :----------: | :----: |
|   `< 18.5`   |  减持  |
| `18.5 - 25`  | 普通的 |
|  `25 - 30`   |  超重  |
|    `> 30`    |  肥胖  |

例如，让我们计算一个体重为 100kg(千克)、身高为 1.524m(米)的人的 BMI。

体重指数 = 100 / (1.524  1.524)

体重指数 = 43.056

由于 BMI 大于 30，此人被归类为“超重”。

## 3.计算BMI的Java程序

Java 程序由计算 BMI 的公式和简单的if – else语句组成。使用上面的公式和表格，我们可以找出一个人所在的类别：

```java
static String calculateBMI(double weight, double height) {

    double bmi = weight / (height  height);

    if (bmi < 18.5) {
        return "Underweight";
    }
    else if (bmi < 25) {
        return "Normal";
    }
    else if (bmi < 30) {
        return "Overweight";
    }
    else {
       return "Obese";
    }
}
```

## 4.测试

让我们通过提供“肥胖”个体的身高和体重来测试代码：

```java
@Test
public void whenBMIIsGreaterThanThirty_thenObese() {
    double weight = 50;
    double height = 1.524;
    String actual = BMICalculator.calculateBMI(weight, height);
    String expected = "Obese";

    assertThat(actual).isEqualTo(expected);
}
```

运行测试后，我们可以看到实际结果和预期的一样。

## 5.总结

在本文中，我们学习了用Java创建 BMI 计算器。我们还通过编写 JUnit 测试来测试实现。
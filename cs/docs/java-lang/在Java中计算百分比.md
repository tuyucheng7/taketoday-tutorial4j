## 1. 概述

在本快速教程中，我们将实现一个 CLI 程序来计算Java中的百分比。

但首先，让我们定义如何以数学方式计算百分比。

## 2. 数学公式

在数学中，百分比是以 100 的分数表示的数字或比率。通常使用百分号“%”表示。

让我们考虑一个学生在总 y 分中获得 x 分。计算该学生获得的百分比分数的公式为：

>   百分比 = (x/y)100

## 3.Java程序

现在我们清楚了如何以数学方式计算百分比，让我们用Java构建一个程序来计算它：

```java
public class PercentageCalculator {

    public double calculatePercentage(double obtained, double total) {
        return obtained  100 / total;
    }

    public static void main(String[] args) {
        PercentageCalculator pc = new PercentageCalculator();
        Scanner in = new Scanner(System.in);
        System.out.println("Enter obtained marks:");
        double obtained = in.nextDouble();
        System.out.println("Enter total marks:");
        double total = in.nextDouble();
        System.out.println(
          "Percentage obtained: " + pc.calculatePercentage(obtained, total));
    }
}
```

该程序从 CLI 获取学生的分数(获得的分数和总分)，然后调用calculatePercentage()方法来计算它的百分比。

这里我们选择双精度作为输入和输出的数据类型，因为它可以存储精度高达 16 位的十进制数。因此，它应该足以满足我们的用例。

## 4.输出

让我们运行这个程序并查看结果：

```java
Enter obtained marks:
87
Enter total marks:
100
Percentage obtained: 87.0

Process finished with exit code 0
```

## 5.总结

在本文中，我们了解了如何以数学方式计算百分比，然后编写了一个JavaCLI 程序来计算它。
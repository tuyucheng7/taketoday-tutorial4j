## 1. 概述

我们经常需要使用数学工具，有时java.lang.Math根本不够用。[幸运的是，Apache Commons 的目标是使用Apache Commons Math](https://commons.apache.org/proper/commons-math/)填补标准库的漏洞。

Apache Commons Math 是最大的Java数学函数和实用程序开源库。鉴于本文只是介绍，我们将只概述该库并展示最引人注目的用例。

## 2. 从 Apache Commons Math 开始

### 2.1. Apache Commons Math 的使用

Apache Commons Math 由数学函数(例如erf)、表示数学概念的结构(如复数、多项式、向量等)以及我们可以应用于这些结构的算法(求根、优化、曲线拟合、计算几何图形的交集等)。

### 2.2. Maven 配置

如果使用的是 Maven，只需添加[此依赖](https://search.maven.org/classic/#search|ga|1|a%3A"commons-math3")项：

```xml
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-math3</artifactId>
  <version>3.6.1</version>
</dependency>

```

### 2.3. 套餐概览

Apache Commons Math 分为几个包：

-   org.apache.commons.math3.stat –统计和统计测试
-   org.apache.commons.math3.distribution——概率分布
-   org.apache.commons.math3.random –随机数、字符串和数据生成
-   org.apache.commons.math3.analysis –求根、积分、插值、多项式等。
-   org.apache.commons.math3.linear –矩阵，求解线性系统
-   org.apache.commons.math3.geometry –几何(欧氏空间和二进制空间划分)
-   org.apache.commons.math3.transform –变换方法(快速傅立叶)
-   org.apache.commons.math3.ode –常微分方程积分
-   org.apache.commons.math3.fitting –曲线拟合
-   org.apache.commons.math3.optim –函数最大化或最小化
-   org.apache.commons.math3.genetics——遗传算法
-   org.apache.commons.math3.ml –机器学习(集群和神经网络)
-   org.apache.commons.math3.util –扩展 java.lang.Math 的通用数学/统计函数
-   org.apache.commons.math3.special –特殊函数(Gamma、Beta)
-   org.apache.commons.math3.complex –复数
-   org.apache.commons.math3.fraction——有理数

## 3.统计、概率和随机性

### 3.1. 统计数据

包org.apache.commons.math3.stat提供了多种统计计算工具。例如，要计算均值、标准差等，我们可以使用DescriptiveStatistics：

```java
double[] values = new double[] {65, 51 , 16, 11 , 6519, 191 ,0 , 98, 19854, 1, 32};
DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
for (double v : values) {
    descriptiveStatistics.addValue(v);
}

double mean = descriptiveStatistics.getMean();
double median = descriptiveStatistics.getPercentile(50);
double standardDeviation = descriptiveStatistics.getStandardDeviation();

```

在这个包中，我们可以找到用于计算协方差、相关性或执行统计测试(使用TestUtils)的工具。

### 3.2. 概率和分布

在核心Java中，Math.random()可用于生成随机值，但这些值均匀分布在 0 和 1 之间。

有时，我们想使用更复杂的分布来产生随机值。为此，我们可以使用org.apache.commons.math3.distribution提供的框架。

以下是如何根据均值为 10、标准差为 3 的正态分布生成随机值：

```java
NormalDistribution normalDistribution = new NormalDistribution(10, 3);
double randomValue = normalDistribution.sample();

```

或者我们可以获得离散分布的取值概率P(X = x) ，或者连续分布的累积概率P(X <= x)。

## 4.分析

分析相关的函数和算法可以在org.apache.commons.math3.analysis中找到。

### 4.1. 寻根

根是函数值为 0 的值。Commons-Math 包括几个[求根算法的实现](https://commons.apache.org/proper/commons-math/userguide/analysis.html#a4.3_Root-finding)。

在这里，我们尝试找到v -> (v  v) – 2的根：

```java
UnivariateFunction function = v -> Math.pow(v, 2) - 2;
UnivariateSolver solver = new BracketingNthOrderBrentSolver(1.0e-12, 1.0e-8, 5);
double c = solver.solve(100, function, -10.0, 10.0, 0);

```

首先，我们首先定义函数，然后定义求解器，然后设置所需的精度。最后，我们调用solve() API。

求根操作将使用多次迭代来执行，因此需要在执行时间和准确性之间找到折衷方案。

### 4.2. 计算积分

集成的工作方式几乎类似于寻根：

```java
UnivariateFunction function = v -> v;
UnivariateIntegrator integrator = new SimpsonIntegrator(1.0e-12, 1.0e-8, 1, 32);
double i = integrator.integrate(100, function, 0, 10);

```

我们首先定义一个函数，然后在现有的[可用集成解决方案](https://commons.apache.org/proper/commons-math/userguide/analysis.html#a4.5_Integration)中选择一个集成器，然后设置所需的精度，最后进行集成。

## 5. 线性代数

如果我们有一个 AX = B 形式的线性方程组，其中 A 是实数矩阵，B 是实数向量——Commons Math 提供了表示矩阵和向量的结构，还提供求解器来求X 的值：

```java
RealMatrix a = new Array2DRowRealMatrix(
  new double[][] { { 2, 3, -2 }, { -1, 7, 6 }, { 4, -3, -5 } },
  false);
RealVector b = new ArrayRealVector(n
  ew double[] { 1, -2, 1 }, 
  false);

DecompositionSolver solver = new LUDecomposition(a).getSolver();

RealVector solution = solver.solve(b);

```

情况非常简单：我们从双精度数组定义矩阵a，并从向量数组定义向量b。

然后，我们创建一个LUDecomposition，它为 AX = B 形式的方程提供求解器。顾名思义，LUDecomposition依赖于[LU 分解](https://en.wikipedia.org/wiki/LU_decomposition)，因此仅适用于方阵。

对于其他矩阵，存在不同的求解器，通常使用最小二乘法求解方程。

## 6.几何

包org.apache.commons.math3.geometry提供了几个表示几何对象的类和几个操作它们的工具。重要的是要注意，这个包分为不同的子包，关于我们想要使用的几何类型：

重要的是要注意，这个包分为不同的子包，关于我们想要使用的几何类型：

-   org.apache.commons.math3.geometry.euclidean.oned——一维欧氏几何
-   org.apache.commons.math3.geometry.euclidean.twod –二维欧几里德几何
-   org.apache.commons.math3.geometry.euclidean.threed – 3D 欧几里德几何
-   org.apache.commons.math3.geometry.spherical.oned——一维球面几何
-   org.apache.commons.math3.geometry.spherical.twod –二维球面几何

最有用的类可能是Vector2D、Vector3D、Line和Segment。它们分别用于表示 2D 向量(或点)、3D 向量、线和线段。

使用上述类时，可以执行一些计算。例如，以下代码执行两条二维线的交点计算：

```java
Line l1 = new Line(new Vector2D(0, 0), new Vector2D(1, 1), 0);
Line l2 = new Line(new Vector2D(0, 1), new Vector2D(1, 1.5), 0);

Vector2D intersection = l1.intersection(l2);

```

使用这些结构来获取一个点到一条线的距离，或者一条线到另一条线的最近点(在 3D 中)也是可行的。

## 7. 优化、遗传算法和机器学习

Commons-Math 还为与优化和机器学习相关的更复杂的任务提供了一些工具和算法。

### 7.1. 优化

优化通常包括最小化或最大化成本函数。优化算法可以在org.apache.commons.math3.optim和org.apache.commons.math3.optimization中找到。它包括线性和非线性优化算法。

我们可以注意到optim和optimization包中有重复的类：optimization包大部分已弃用，将在 Commons Math 4 中删除。

### 7.2. 遗传算法

遗传算法是一种元启发式算法：当确定性算法太慢时，它们是为问题找到可接受的解决方案的解决方案。可以在[此处](https://www.baeldung.com/java-genetic-algorithm)找到遗传算法的概述。

包org.apache.commons.math3.genetics提供了一个使用遗传算法执行计算的框架。它包含可用于表示种群和染色体的结构，以及执行变异、交叉和选择操作的标准算法。

以下课程提供了一个良好的起点：

-   [GeneticAlgorithm——](https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/org/apache/commons/math3/genetics/GeneticAlgorithm.html)遗传算法框架
-   [人口](https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/org/apache/commons/math3/genetics/Population.html)——代表人口的界面
-   [染色体](https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/org/apache/commons/math3/genetics/Chromosome.html)——代表染色体的界面

### 7.3. 机器学习

Commons-Math 中的机器学习分为两部分：聚类和神经网络。

聚类部分包括根据向量关于距离度量的相似性在向量上放置标签。提供的聚类算法基于 K-means 算法。

神经网络部分给出了表示网络([Network](https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/index.html?org/apache/commons/math3/ml/neuralnet/Network.html))和神经元([Neuron](https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/index.html?org/apache/commons/math3/ml/neuralnet/Neuron.html))的类。人们可能会注意到，与最常见的神经网络框架相比，它提供的功能有限，但它对于要求不高的小型应用程序仍然有用。

## 8. 公用事业

### 8.1. 快速数学

FastMath是一个位于org.apache.commons.math3.util中的静态类，其工作方式与java.lang.Math完全相同。

它的目的是提供至少与我们在java.lang.Math中可以找到的相同功能，但实现速度更快。因此，当程序严重依赖数学计算时，最好将对Math.sin()的调用(例如)替换为对FastMath.sin()的调用，以提高应用程序的性能。另一方面，请注意FastMath不如java.lang.Math 准确。

### 8.2. 通用和特殊功能

Commons-Math 提供了未在java.lang.Math中实现的标准数学函数(如阶乘)。这些函数中的大部分都可以在包org.apache.commons.math3.special和org.apache.commons.math3.util中找到。

例如，如果我们想计算 10 的阶乘，我们可以简单地这样做：

```java
long factorial = CombinatorialUtils.factorial(10);

```

与算术相关的函数(gcd、lcm等)可以在ArithmeticUtils中找到，与组合相关的函数可以在CombinatorialUtils中找到。其他一些特殊函数，如erf，可以在org.apache.commons.math3.special中访问。

### 8.3. 分数和复数

也可以使用 commons-math 处理更复杂的类型：分数和复数。这些结构允许我们对这类数字执行特定的计算。

然后，我们可以计算两个分数的和并将结果显示为分数的字符串表示形式(即以“a / b”的形式)：

```java
Fraction lhs = new Fraction(1, 3);
Fraction rhs = new Fraction(2, 5);
Fraction sum = lhs.add(rhs);

String str = new FractionFormat().format(sum);

```

或者，我们可以快速计算复数的幂：

```java
Complex first = new Complex(1.0, 3.0);
Complex second = new Complex(2.0, 5.0);

Complex power = first.pow(second);

```

## 9.总结

在本教程中，我们介绍了使用 Apache Commons Math 可以做的一些有趣的事情。

不幸的是，本文无法涵盖整个分析或线性代数领域，因此仅提供最常见情况的示例。

然而，要了解更多信息，我们可以阅读写得很好的[文档](https://commons.apache.org/proper/commons-math/userguide/)，它提供了库各个方面的大量细节。
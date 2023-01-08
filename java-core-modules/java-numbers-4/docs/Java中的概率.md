## 1. 概述

在本教程中，我们将通过几个示例了解如何使用Java实现概率。

## 2.模拟基本概率

在Java中模拟概率，我们首先要做的就是生成[随机](https://www.baeldung.com/cs/randomness)数。幸运的是，Java 为我们提供了大量的[随机数生成器](https://www.baeldung.com/java-generating-random-numbers)。

在这种情况下，我们将使用[SplittableRandom](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/SplittableRandom.html)类，因为它提供高质量的随机性并且速度相对较快：

```java
SplittableRandom random = new SplittableRandom();
```

然后我们需要生成一个范围内的数字并将其与从该范围内选择的另一个数字进行比较。范围内的每个数字都有相等的机会被抽中。因为我们知道范围，所以我们知道绘制我们选择的数字的概率。这样我们就可以控制概率：

```java
boolean probablyFalse = random.nextInt(10) == 0
```

在这个例子中，我们抽取了0到9的数字。因此，抽到0的概率等于10%。现在，让我们得到一个随机数并测试选择的数字是否低于抽取的数字：

```java
boolean whoKnows = random.nextInt(1, 101) <= 50
```

在这里，我们抽取了 1 到 100 之间的数字。我们的随机数小于或等于 50 的机会恰好是 50%。

## 3.均匀分布

到目前为止生成的值属于均匀分布。这意味着每个事件，例如掷骰子上的某个数字，都有均等的发生机会。

### 3.1. 以给定的概率调用函数

现在，假设我们想要时不时地执行一个任务并控制它的概率。例如，我们经营一个电子商务网站，我们想给 10% 的用户打折。

为此，让我们实施一个采用三个参数的方法：在一定百分比的情况下调用的供应商，在其余情况下调用的第二个供应商，以及概率。

首先，我们使用[Vavr将](https://www.baeldung.com/vavr)SplittableRandom声明为[Lazy](https://javadoc.io/doc/io.vavr/vavr/0.9.2/io/vavr/Lazy.html) 。这样我们只会在第一次请求时实例化它一次：

```java
private final Lazy<SplittableRandom> random = Lazy.of(SplittableRandom::new);

```

然后，我们将实现概率管理功能：

```java
public <T> withProbability(Supplier<T> positiveCase, Supplier<T> negativeCase, int probability) {
    SplittableRandom random = this.random.get();
    if (random.nextInt(1, 101) <= probability) {
        return positiveCase.get();
    } else {
        return negativeCase.get();
    }
}
```

### 3.2. 使用蒙特卡洛方法的抽样概率

让我们反转我们在上一节中看到的过程。为此，我们将使用[蒙特卡罗](https://en.wikipedia.org/wiki/Monte_Carlo_method)方法测量概率。它生成大量随机事件并计算其中有多少满足提供的条件。当概率很难或不可能通过分析计算时，它很有用。

例如，如果我们看六面骰子，我们知道掷出某个数字的概率是 1/6。但是，如果我们有一个面数未知的神秘骰子，就很难说出概率是多少。我们可以不分析骰子，而是掷它多次并计算某些事件发生了多少次。

让我们看看如何实现这种方法。首先，我们将尝试生成一百万次概率为 10% 的数字 1 并计算它们：

```java
int numberOfSamples = 1_000_000;
int probability = 10;
int howManyTimesInvoked = 
  Stream.generate(() -> randomInvoker.withProbability(() -> 1, () -> 0, probability))
    .limit(numberOfSamples)
    .mapToInt(e -> e)
    .sum();
```

然后，生成数的总和除以样本数将是事件概率的近似值：

```java
int monteCarloProbability = (howManyTimesInvoked  100) / numberOfSamples;

```

请注意，计算出的概率是近似值。样本数量越多，近似值就越好。

## 4. 其他分布

均匀分布适用于对游戏等事物进行建模。为了使游戏公平，所有事件通常需要具有相同的发生概率。

然而，在现实生活中，分布通常更为复杂。不同的事情发生的机会是不相等的。

例如，非常矮的人很少，非常高的人也很少。大多数人都是平均身高，这意味着人的身高服从[正态分布](https://en.wikipedia.org/wiki/Normal_distribution)。如果我们需要生成随机的人体身高，那么生成随机的英尺数是不够的。

幸运的是，我们不需要自己实现底层数学模型。我们需要知道使用哪个发行版以及如何配置它，例如，使用统计数据。

Apache Commons 库为我们提供了多个发行版的实现。让我们用它来实现正态分布：

```java
private static final double MEAN_HEIGHT = 176.02;
private static final double STANDARD_DEVIATION = 7.11;
private static NormalDistribution distribution =  new NormalDistribution(MEAN_HEIGHT, STANDARD_DEVIATION);

```

使用这个 API 非常简单——[示例](https://commons.apache.org/proper/commons-math/javadocs/api-3.2/org/apache/commons/math3/distribution/NormalDistribution.html#sample())方法从分布中抽取一个随机数：

```java
public static double generateNormalHeight() {
    return distribution.sample();
}
```

最后，让我们反转这个过程：

```java
public static double probabilityOfHeightBetween(double heightLowerExclusive, double heightUpperInclusive) {
    return distribution.probability(heightLowerExclusive, heightUpperInclusive);
}
```

结果，我们将得到一个人的身高在两个界限之间的概率。在这种情况下，较低和较高的高度。

## 5.总结

在本文中，我们学习了如何生成随机事件以及如何计算它们发生的概率。我们使用均匀分布和正态分布来模拟不同的情况。
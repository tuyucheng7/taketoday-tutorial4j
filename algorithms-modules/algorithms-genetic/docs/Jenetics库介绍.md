## 1. 简介

[本系列的目的](https://www.baeldung.com/java-genetic-algorithm)是解释遗传算法的概念并展示最知名的实现。

在本教程中，我们将描述一个非常强大的 JeneticsJava库，可用于解决各种优化问题。

如果你觉得需要进一步了解遗传算法，我们建议你从[本文](https://www.baeldung.com/java-genetic-algorithm)开始。

## 2. 它是如何工作的？

根据其[官方文档](http://jenetics.io/)，Jenetics 是一个基于用Java编写的进化算法的库。进化算法源于生物学，因为它们使用受生物进化启发的机制，例如繁殖、突变、重组和选择。

Jenetics 是使用JavaStream接口实现的，因此它可以与JavaStream API的其余部分一起顺利工作。

主要特点是：

-   无摩擦最小化——不需要改变或调整适应度函数；我们只需更改Engine类的配置，就可以开始我们的第一个应用程序了
-   无依赖性——使用 Jenetics 不需要运行时第三方库
-  Java8 就绪——完全支持Stream和 lambda 表达式
-   多线程——进化步骤可以并行执行

为了使用 Jenetics，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.jenetics</groupId>
    <artifactId>jenetics</artifactId>
    <version>3.7.0</version>
</dependency>
```

最新版本可以[在 Maven Central 中](https://search.maven.org/classic/#search|ga|1|a%3A"jenetics")找到。

## 3.用例

为了测试 Jenetics 的所有功能，我们将尝试解决各种众所周知的优化问题，从简单的二进制算法开始，以背包问题结束。

### 3.1. 简单遗传算法

假设我们需要解决最简单的二进制问题，我们需要优化 1 位在由 0 和 1 组成的染色体中的位置。首先，我们需要定义适合问题的工厂：

```java
Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(10, 0.5));
```

我们创建了长度为 10 的BitChromosome，染色体中 1 的概率等于 0.5。

现在，让我们创建执行环境：

```java
Engine<BitGene, Integer> engine
  = Engine.builder(SimpleGeneticAlgorithm::eval, gtf).build();
```

eval()方法返回位数：

```java
private Integer eval(Genotype<BitGene> gt) {
    return gt.getChromosome().as(BitChromosome.class).bitCount();
}
```

在最后一步，我们开始进化并收集结果：

```java
Genotype<BitGene> result = engine.stream()
  .limit(500)
  .collect(EvolutionResult.toBestGenotype());
```

最终结果将类似于：

```plaintext
Before the evolution:
[00000010|11111100]
After the evolution:
[00000000|11111111]
```

我们设法优化了 1 在基因中的位置。

### 3.2. 子集和问题

Jenetics 的另一个用例是解决[子集求和问题](https://en.wikipedia.org/wiki/Subset_sum_problem)。简而言之，优化的挑战在于，给定一组整数，我们需要找到一个和为零的非空子集。

Jenetics 中有预定义的接口来解决此类问题：

```java
public class SubsetSum implements Problem<ISeq<Integer>, EnumGene<Integer>, Integer> {
    // implementation
}
```

如我们所见，我们实现了具有三个参数的Problem<T, G, C>：

-   <T> – 问题适应度函数的参数类型，在我们的例子中是一个不可变的、有序的、固定大小的整数序列ISeq<Integer>
-   <G> – 进化引擎正在使用的基因类型，在本例中为可数整数基因EnumGene<Integer>
-   <C> – 适应度函数的结果类型；这是一个整数

为了使用Problem<T, G, C>接口，我们需要覆盖两个方法：

```java
@Override
public Function<ISeq<Integer>, Integer> fitness() {
    return subset -> Math.abs(subset.stream()
      .mapToInt(Integer::intValue).sum());
}

@Override
public Codec<ISeq<Integer>, EnumGene<Integer>> codec() {
    return codecs.ofSubSet(basicSet, size);
}
```

在第一个中，我们定义了我们的适应度函数，而第二个是一个包含用于创建常见问题编码的工厂方法的类，例如，从给定的基本集中找到最佳的固定大小子集，就像我们的例子一样。

现在我们可以继续进行主要部分。一开始，我们需要创建一个子集以在问题中使用：

```java
SubsetSum problem = of(500, 15, new LCG64ShiftRandom(101010));
```

请注意，我们使用的是Jenetics提供的 LCG64ShiftRandom 生成器。在下一步中，我们正在构建解决方案的引擎：

在下一步中，我们正在构建解决方案的引擎：

```java
Engine<EnumGene<Integer>, Integer> engine = Engine.builder(problem)
  .minimizing()
  .maximalPhenotypeAge(5)
  .alterers(new PartiallyMatchedCrossover<>(0.4), new Mutator<>(0.3))
  .build();
```

我们尝试通过设置表型年龄和用于改变后代的改变者来最小化结果(最佳结果为 0)。下一步我们可以得到结果：

```java
Phenotype<EnumGene<Integer>, Integer> result = engine.stream()
  .limit(limit.bySteadyFitness(55))
  .collect(EvolutionResult.toBestPhenotype());
```

请注意，我们正在使用bySteadyFitness()返回一个谓词，如果在给定的世代数后没有找到更好的表型并收集最佳结果，它将截断进化流。如果我们幸运的话，并且随机创建的集合有一个解决方案，我们会看到类似的东西：

如果我们幸运的话，并且随机创建的集合有一个解决方案，我们会看到类似的东西：

```plaintext
[85|-76|178|-197|91|-106|-70|-243|-41|-98|94|-213|139|238|219] --> 0
```

否则，子集之和将不为 0。

### 3.3. 背包首次适配问题

Jenetics 库使我们能够解决更复杂的问题，例如[背包问题](https://en.wikipedia.org/wiki/Knapsack_problem)。简而言之，在这个问题中，我们的背包空间有限，我们需要决定将哪些物品放入其中。

让我们从定义袋子尺寸和物品数量开始：

```java
int nItems = 15;
double ksSize = nItems  100.0 / 3.0;
```

在下一步中，我们将生成一个包含KnapsackItem对象(由大小和值字段定义)的随机数组，我们将使用 First Fit 方法将这些项目随机放入背包中：

```java
KnapsackFF ff = new KnapsackFF(Stream.generate(KnapsackItem::random)
  .limit(nItems)
  .toArray(KnapsackItem[]::new), ksSize);
```

接下来，我们需要创建引擎：

```java
Engine<BitGene, Double> engine
  = Engine.builder(ff, BitChromosome.of(nItems, 0.5))
  .populationSize(500)
  .survivorsSelector(new TournamentSelector<>(5))
  .offspringSelector(new RouletteWheelSelector<>())
  .alterers(new Mutator<>(0.115), new SinglePointCrossover<>(0.16))
  .build();
```

这里有几点需要注意：

-   人口规模为 500
-   后代将通过锦标赛和[轮盘赌](https://www.baeldung.com/cs/genetic-algorithms-roulette-selection)选择
-   正如我们在上一小节中所做的那样，我们还需要为新创建的后代定义修改器

Jenetics 还有一个非常重要的特性。我们可以轻松地收集整个模拟期间的所有统计数据和见解。我们将使用EvolutionStatistics类来做到这一点：

```java
EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
```

最后，让我们运行模拟：

```java
Phenotype<BitGene, Double> best = engine.stream()
  .limit(bySteadyFitness(7))
  .limit(100)
  .peek(statistics)
  .collect(toBestPhenotype());
```

请注意，我们会在每一代之后更新评估统计数据，限制为稳定的 7 代，总共最多 100 代。更详细地说，有两种可能的情况：

-   我们达到 7 代稳定，然后模拟停止
-   我们无法在少于 100 代的情况下获得 7 个稳定的世代，因此由于第二个限制() ，模拟停止

有最大代数限制很重要，否则，模拟可能不会在合理的时间内停止。

最后的结果包含了很多信息：

```plaintext
+---------------------------------------------------------------------------+
|  Time statistics                                                          |
+---------------------------------------------------------------------------+
|             Selection: sum=0,039207931000 s; mean=0,003267327583 s        |
|              Altering: sum=0,065145069000 s; mean=0,005428755750 s        |
|   Fitness calculation: sum=0,029678433000 s; mean=0,002473202750 s        |
|     Overall execution: sum=0,111383965000 s; mean=0,009281997083 s        |
+---------------------------------------------------------------------------+
|  Evolution statistics                                                     |
+---------------------------------------------------------------------------+
|           Generations: 12                                                 |
|               Altered: sum=7 664; mean=638,666666667                      |
|                Killed: sum=0; mean=0,000000000                            |
|              Invalids: sum=0; mean=0,000000000                            |
+---------------------------------------------------------------------------+
|  Population statistics                                                    |
+---------------------------------------------------------------------------+
|                   Age: max=10; mean=1,792167; var=4,657748                |
|               Fitness:                                                    |
|                      min  = 0,000000000000                                |
|                      max  = 716,684883338605                              |
|                      mean = 587,012666759785                              |
|                      var  = 17309,892287851708                            |
|                      std  = 131,567063841418                              |
+---------------------------------------------------------------------------+
```

这一次，我们能够在最佳场景中放置总价值为 716,68 的物品。我们还可以看到进化和时间的详细统计数据。

如何测试？

这是一个相当简单的过程——只需打开与问题相关的主文件并首先运行算法。一旦我们有了一个大概的想法，那么我们就可以开始使用参数了。

## 4. 总结

在本文中，我们介绍了基于实际优化问题的 Jenetics 库功能。

[该代码在GitHub 上](https://github.com/eugenp/tutorials/tree/master/algorithms-modules/algorithms-genetic)作为 Maven 项目提供。请注意，我们提供了更多优化挑战的代码示例，例如[Springsteen Record](https://softwareengineering.stackexchange.com/questions/326378/finding-the-best-combination-of-sets-that-gives-the-maximum-number-of-unique-ite)(是的，它存在！)和旅行商问题。

对于该系列中的所有文章，包括遗传算法的其他示例，请查看以下链接：

-   [如何在Java中设计遗传算法](https://www.baeldung.com/java-genetic-algorithm)
-   [Java 中的旅行商问题](https://www.baeldung.com/java-simulated-annealing-for-traveling-salesman)
-   [蚁群优化](https://www.baeldung.com/java-ant-colony-optimization)
-   Jenetics库介绍(本)
## 1. 简介

本系列的目的是解释遗传算法的概念。

遗传算法旨在通过使用与自然界相同的过程来解决问题——它们使用选择、重组和变异的组合来演化出问题的解决方案。

让我们首先使用最简单的二元遗传算法示例来解释这些算法的概念。

## 2. 遗传算法如何工作

遗传算法是进化计算的一部分，是人工智能快速发展的领域。

算法从一组称为人口的解决方案(由个人表示)开始。一个种群的解决方案被用来形成一个新的种群，因为新种群有可能比旧种群更好。

被选来形成新解决方案(后代)的个体是根据他们的适应性来选择的——他们越适合，他们繁殖的机会就越大。

## 3. 二元遗传算法

让我们看一下简单遗传算法的基本过程。

### 3.1. 初始化

在初始化步骤中，我们生成一个随机Population作为第一个解决方案。首先，我们需要确定Population的大小以及我们期望的最终解决方案是什么：

```java
SimpleGeneticAlgorithm.runAlgorithm(50,
  "1011000100000100010000100000100111001000000100000100000000001111");
```

在上面的例子中，Population size 是 50，正确的解决方案由我们随时可能更改的二进制位串表示。

在下一步中，我们将保存我们想要的解决方案并创建一个随机Population：

```java
setSolution(solution);
Population myPop = new Population(populationSize, true);
```

现在我们准备运行程序的主循环。

### 3.2. 健身检查

在程序的主循环中，我们将通过适应度函数来评估每个个体(简单来说，个体越好，适应度函数的值就越高)：

```java
while (myPop.getFittest().getFitness() < getMaxFitness()) {
    System.out.println(
      "Generation: " + generationCount
      + " Correct genes found: " + myPop.getFittest().getFitness());
    
    myPop = evolvePopulation(myPop);
    generationCount++;
}
```

让我们从解释我们如何获得最适者开始：

```java
public int getFitness(Individual individual) {
    int fitness = 0;
    for (int i = 0; i < individual.getDefaultGeneLength()
      && i < solution.length; i++) {
        if (individual.getSingleGene(i) == solution[i]) {
            fitness++;
        }
    }
    return fitness;
}
```

正如我们所观察到的，我们一点一点地比较两个Individual对象。如果我们找不到完美的解决方案，我们需要进行下一步，即Population的进化。

### 3.3. 后代

在这一步中，我们需要创建一个新的Population。首先，我们需要根据适应度从种群中选择两个父个体对象。请注意，允许当前一代的最佳个人原封不动地传给下一代是有益的。这种策略称为精英主义：

```java
if (elitism) {
    newPopulation.getIndividuals().add(0, pop.getFittest());
    elitismOffset = 1;
} else {
    elitismOffset = 0;
}
```

为了选择两个最佳个人对象，我们将应用[锦标赛选择策略](https://en.wikipedia.org/wiki/Tournament_selection)：

```java
private Individual tournamentSelection(Population pop) {
    Population tournament = new Population(tournamentSize, false);
    for (int i = 0; i < tournamentSize; i++) {
        int randomId = (int) (Math.random()  pop.getIndividuals().size());
        tournament.getIndividuals().add(i, pop.getIndividual(randomId));
    }
    Individual fittest = tournament.getFittest();
    return fittest;
}
```

每场比赛的获胜者(最适合的人)被选出进入下一阶段，即交叉：

```java
private Individual crossover(Individual indiv1, Individual indiv2) {
    Individual newSol = new Individual();
    for (int i = 0; i < newSol.getDefaultGeneLength(); i++) {
        if (Math.random() <= uniformRate) {
            newSol.setSingleGene(i, indiv1.getSingleGene(i));
        } else {
            newSol.setSingleGene(i, indiv2.getSingleGene(i));
        }
    }
    return newSol;
}
```

在交叉中，我们在随机选择的位置交换来自每个选定个体的位。整个过程在以下循环中运行：

```java
for (int i = elitismOffset; i < pop.getIndividuals().size(); i++) {
    Individual indiv1 = tournamentSelection(pop);
    Individual indiv2 = tournamentSelection(pop);
    Individual newIndiv = crossover(indiv1, indiv2);
    newPopulation.getIndividuals().add(i, newIndiv);
}
```

正如我们所看到的，在交叉之后，我们将新的后代放在一个新的Population中。此步骤称为验收。

最后，我们可以执行Mutation。突变用于维持从一代到下一代的遗传多样性。我们使用了位反转类型的突变，其中随机位被简单地反转：

```java
private void mutate(Individual indiv) {
    for (int i = 0; i < indiv.getDefaultGeneLength(); i++) {
        if (Math.random() <= mutationRate) {
            byte gene = (byte) Math.round(Math.random());
            indiv.setSingleGene(i, gene);
        }
    }
}
```

[本教程](http://www.obitko.com/tutorials/genetic-algorithms/crossover-mutation.php)很好地描述了所有类型的突变和交叉。

然后我们重复第 3.2 和 3.3 小节中的步骤，直到我们达到终止条件，例如，最佳解决方案。

## 4. 提示和技巧

为了实现高效的遗传算法，我们需要调整一组参数。本节应该为你提供一些基本建议，说明如何从最重要的参数入手：

-   交叉率——应该很高，大约80%-95%
-   变异率——应该很低，在0.5%-1%左右。
-   人口规模——好的人口规模大约是20-30，然而，对于一些问题，规模 50-100 更好
-   选择——基本的[轮盘赌选择](https://www.baeldung.com/cs/genetic-algorithms-roulette-selection)可以与精英主义的概念一起使用
-   交叉和变异类型——取决于编码和问题

请注意，调整建议通常是对遗传算法进行经验研究的结果，它们可能会根据提出的问题而有所不同。

## 5.总结

本教程介绍了遗传算法的基础知识。你可以在没有该领域任何先前知识的情况下学习遗传算法，只需要基本的计算机编程技能。

[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/algorithms-modules/algorithms-genetic)中提供了本教程中代码片段的完整源代码。

另请注意，我们使用[Lombok](https://projectlombok.org/)来生成 getter 和 setter。[你可以在本文](https://www.baeldung.com/intro-to-project-lombok)中查看如何在你的 IDE 中正确配置它。

有关遗传算法的更多示例，请查看我们系列的所有文章：

-   如何设计遗传算法？(这个)
-   [Java 中的旅行商问题](https://www.baeldung.com/java-simulated-annealing-for-traveling-salesman)
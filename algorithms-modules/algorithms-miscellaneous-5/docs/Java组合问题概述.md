## 1. 概述

在本教程中，我们将学习如何解决一些常见的组合问题。它们在日常工作中很可能不是很有用；但是，从算法的角度来看，它们很有趣。我们可能会发现它们很方便用于测试目的。

请记住，有许多不同的方法可以解决这些问题。我们试图使所提供的解决方案易于掌握。

## 2. 生成排列

首先，让我们从排列开始。排列是重新排列序列以使其具有不同顺序的行为。

正如我们从数学中知道的那样，对于一个包含n 个元素的序列，有n! 不同的排列。n！被称为[阶乘](https://www.baeldung.com/java-calculate-factorial)运算：

>   n！= 1  2  ...  n

因此，例如，对于序列[1, 2, 3] 有六种排列：

```plaintext
[1, 2, 3]

[1, 3, 2]

[2, 1, 3]

[2, 3, 1]

[3, 1, 2]

[3, 2, 1]
```

阶乘增长非常快——对于 10 个元素的序列，我们有 3,628,800 种不同的排列！在这种情况下，我们谈论置换序列，其中每个元素都是不同的。

### 2.1. 算法

考虑以递归方式生成排列是个好主意。让我们介绍一下状态的概念。它将由两部分组成：当前排列和当前处理的元素的索引。

在这种状态下唯一要做的工作是将元素与每个剩余元素交换，然后转换到具有修改后的序列且索引增加 1 的状态。

让我们用一个例子来说明。

我们想要为四个元素的序列生成所有排列 - [1, 2, 3, 4]。因此，将有 24 个排列。下图展示了算法的部分步骤：

[![排列](https://www.baeldung.com/wp-content/uploads/2019/12/permutations-1-300x286.png)](https://www.baeldung.com/wp-content/uploads/2019/12/permutations-1.png)

 

 

树的每个节点都可以理解为一个状态。顶部的红色数字表示当前处理的元素的索引。节点中的绿色数字表示交换。

因此，我们从状态[1, 2, 3, 4]开始，索引等于零。我们将第一个元素与每个元素交换——包括第一个，它什么都不交换——然后进入下一个状态。

现在，我们想要的排列位于右侧的最后一列。

### 2.2. Java实现

用Java编写的算法很短：

```java
private static void permutationsInternal(List<Integer> sequence, List<List<Integer>> results, int index) {
    if (index == sequence.size() - 1) {
        permutations.add(new ArrayList<>(sequence));
    }

    for (int i = index; i < sequence.size(); i++) {
        swap(sequence, i, index);
        permutationsInternal(sequence, permutations, index + 1);
        swap(sequence, i, index);
    }
}
```

我们的函数采用三个参数：当前处理的序列、结果(排列)和当前正在处理的元素的索引。

首先要做的是检查我们是否到达了最后一个元素。如果是这样，我们将序列添加到结果列表中。

然后，在 for 循环中，我们执行交换，对方法进行递归调用，然后将元素交换回来。

最后一部分是一个小的性能技巧——我们可以一直对同一个序列对象进行操作，而不必为每个递归调用创建一个新的序列。

将第一个递归调用隐藏在外观方法下也是一个好主意：

```java
public static List<List<Integer>> generatePermutations(List<Integer> sequence) {
    List<List<Integer>> permutations = new ArrayList<>();
    permutationsInternal(sequence, permutations, 0);
    return permutations;
}

```

请记住，所示算法仅适用于唯一元素的序列！ 对具有重复元素的序列应用相同的算法会给我们重复。

## 3. 生成集合的幂集

另一个流行的问题是生成集合的幂集。让我们从定义开始：

>   集合S的幂集(或幂集)是S的所有子集的集合，包括空集和S本身

因此，例如，给定一个集合[a, b, c]，幂集包含八个子集：

```plaintext
[]

[a]

[b]

[c]

[a, b]

[a, c]

[b, c]

[a, b, c]
```

我们从数学中知道，对于包含n 个元素的集合，幂集应该包含2^n个子集。这个数字也快速增长，但没有阶乘那么快。

### 3.1. 算法

这一次，我们也会递归地思考。现在，我们的状态将由两部分组成：集合中当前正在处理的元素的索引和累加器。

我们需要在每个状态下做出两个选择的决定：是否将当前元素放入累加器。当我们的索引到达集合的末尾时，我们就有了一个可能的子集。通过这种方式，我们可以生成每个可能的子集。

### 3.2. Java实现

我们用Java编写的算法非常可读：

```java
private static void powersetInternal(
  List<Character> set, List<List<Character>> powerset, List<Character> accumulator, int index) {
    if (index == set.size()) {
        results.add(new ArrayList<>(accumulator));
    } else {
        accumulator.add(set.get(index));
        powerSetInternal(set, powerset, accumulator, index + 1);
        accumulator.remove(accumulator.size() - 1);
        powerSetInternal(set, powerset, accumulator, index + 1);
    }
}
```

我们的函数有四个参数：我们要为其生成子集的集合、生成的幂集、累加器和当前处理的元素的索引。

为简单起见，我们将集合保存在列表中。我们希望快速访问索引指定的元素，我们可以使用List实现它，但不能使用Set。

此外，单个元素由单个字母表示(Java中的Character类)。

首先，我们检查索引是否超过设置的大小。如果是，那么我们将累加器放入结果集中，否则我们：

-   将当前考虑的元素放入累加器
-   使用递增索引和扩展累加器进行递归调用
-   从累加器中删除我们之前添加的最后一个元素
-   使用不变的累加器和递增的索引再次调用

同样，我们使用外观方法隐藏实现：

```java
public static List<List<Character>> generatePowerset(List<Character> sequence) {
    List<List<Character>> powerset = new ArrayList<>();
    powerSetInternal(sequence, powerset, new ArrayList<>(), 0);
    return powerset;
}
```

## 4.生成组合

现在，是时候解决组合问题了。我们定义如下：

>   k - 集合S的组合是 S 中k个不同元素的子集，其中项的顺序无关紧要

[k组合](https://www.baeldung.com/cs/generate-k-combinations)的数量由二项式系数描述：

 

[![二项式](https://www.baeldung.com/wp-content/uploads/2019/12/binomial.png)](https://www.baeldung.com/wp-content/uploads/2019/12/binomial.png)

因此，例如，对于集合[a, b, c]我们有三个2 -组合：

```plaintext
[a, b]

[a, c]

[b, c]
```

组合有很多组合用法和解释。例如，假设我们有一个由 16 支球队组成的足球联赛。我们可以看到多少种不同的比赛？

答案是[![16_2](https://www.baeldung.com/wp-content/uploads/2019/12/16_2.gif)](https://www.baeldung.com/wp-content/uploads/2019/12/16_2.gif)，计算结果为 120。

### 4.1. 算法

从概念上讲，我们将做一些类似于之前的幂集算法的事情。我们将有一个递归函数，其状态由当前处理的元素的索引和一个累加器组成。

同样，我们对每个状态都有相同的决定：是否将元素添加到累加器？ 不过这一次，我们有一个额外的限制——我们的累加器不能有超过k个元素。

值得注意的是，二项式系数不一定需要很大。例如：

[![100_2](https://www.baeldung.com/wp-content/uploads/2019/12/100_2.gif)](https://www.baeldung.com/wp-content/uploads/2019/12/100_2.gif)等于 4,950，而

[![100_50](https://www.baeldung.com/wp-content/uploads/2019/12/100_50.gif)](https://www.baeldung.com/wp-content/uploads/2019/12/100_50.gif)有30位！

### 4.2. Java实现

为简单起见，我们假设集合中的元素是整数。

我们来看看该算法的Java实现：

```java
private static void combinationsInternal(
  List<Integer> inputSet, int k, List<List<Integer>> results, ArrayList<Integer> accumulator, int index) {
  int needToAccumulate = k - accumulator.size();
  int canAcculumate = inputSet.size() - index;

  if (accumulator.size() == k) {
      results.add(new ArrayList<>(accumulator));
  } else if (needToAccumulate <= canAcculumate) {
      combinationsInternal(inputSet, k, results, accumulator, index + 1);
      accumulator.add(inputSet.get(index));
      combinationsInternal(inputSet, k, results, accumulator, index + 1);
      accumulator.remove(accumulator.size() - 1);
  }
}
```

这次，我们的函数有五个参数：输入集、k参数、结果列表、累加器和当前处理的元素的索引。

我们首先定义辅助变量：

-   needToAccumulate – 指示我们需要添加多少元素到我们的累加器以获得正确的组合
-   canAcculumate – 指示我们可以向累加器添加多少个元素

现在，我们检查我们的累加器大小是否等于k 。如果是这样，那么我们可以将的数组放入结果列表中。

在另一种情况下，如果我们在集合的剩余部分中仍然有足够的元素，我们将进行两个单独的递归调用：将当前处理的元素放入累加器和不将当前处理的元素放入累加器。这部分类似于我们之前生成幂集的方式。

当然，这个方法可以编写得更快一些。例如，我们可以稍后声明needToAccumulate和canAcculumate变量。但是，我们专注于可读性。

同样，外观方法隐藏了实现：

```java
public static List<List<Integer>> combinations(List<Integer> inputSet, int k) {
    List<List<Integer>> results = new ArrayList<>();
    combinationsInternal(inputSet, k, results, new ArrayList<>(), 0);
    return results;
}
```

## 5.总结

在本文中，我们讨论了不同的组合问题。此外，我们还展示了使用Java实现来解决这些问题的简单算法。在某些情况下，这些算法可以帮助满足不寻常的测试需求。
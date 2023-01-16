## 1. 简介

背包问题是一个有很多应用的组合优化问题。在本教程中，我们将用Java解决这个问题。

## 2. 背包问题

在背包问题中，我们有一组物品。每个项目都有重量和价值：

[![BAEL_3349_Items](https://www.baeldung.com/wp-content/uploads/2019/10/BAEL_3349_Items.png)](https://www.baeldung.com/wp-content/uploads/2019/10/BAEL_3349_Items.png)

我们想把这些物品装进一个背包里。但是，它有重量限制：

[![BAEL_3349_背包](https://www.baeldung.com/wp-content/uploads/2019/10/BAEL_3349_Knapsack.png)](https://www.baeldung.com/wp-content/uploads/2019/10/BAEL_3349_Knapsack.png)

因此，我们需要选择总重量不超过重量限制的物品，并且它们的总价值尽可能高。 例如，上述示例的最佳解决方案是选择 5 公斤的物品和 6 公斤的物品，这在重量限制内给出了 40 美元的最大值。

背包问题有多种变体。在本教程中，我们将重点关注[0-1 背包问题](https://www.baeldung.com/cs/knapsack-problem-np-completeness)。在 0-1 背包问题中，每个项目都必须选择或留下。我们不能拿走部分数量的物品。此外，我们不能多次拿取一件物品。

## 3. 数学定义

现在让我们用数学符号形式化 0-1 背包问题。给定一组n项和重量限制W，我们可以将优化问题定义为：

[![背包-1](https://www.baeldung.com/wp-content/uploads/2019/10/knapsack-1.png)](https://www.baeldung.com/wp-content/uploads/2019/10/knapsack-1.png)

这个问题是 NP 难的。因此，目前还没有多项式时间算法可以求解。然而，对于这个问题，有一个使用动态规划的[伪多项式时间算法。](https://en.wikipedia.org/wiki/Pseudo-polynomial_time)

## 4.递归求解

我们可以使用递归公式来解决这个问题：

[![背包-2](https://www.baeldung.com/wp-content/uploads/2019/10/knapsack-2.png)](https://www.baeldung.com/wp-content/uploads/2019/10/knapsack-2.png)

在该公式中，M(n,w)是n件重量限制为w的最优解。它是以下两个值中的最大值：

-   重量限制为w的(n-1)件物品的最优解(不包括第n件物品)
-   第n项的值加上来自(n-1)项的最优解和w减去第 n 项的权重(包括第n项)

如果第n个项目的重量超过当前重量限制，我们将不包括它。因此，属于上述两种情况的第一类。

我们可以用Java实现这个递归公式：

```java
int knapsackRec(int[] w, int[] v, int n, int W) {
    if (n <= 0) { 
        return 0; 
    } else if (w[n - 1] > W) {
        return knapsackRec(w, v, n - 1, W);
    } else {
        return Math.max(knapsackRec(w, v, n - 1, W), v[n - 1] 
          + knapsackRec(w, v, n - 1, W - w[n - 1]));
    }
}

```

在每个递归步骤中，我们需要评估两个次优解。因此，此递归解决方案的运行时间为O(2 n )。

## 5. 动态规划解法

动态规划是一种线性化其他指数级困难规划问题的策略。这个想法是存储子问题的结果，这样我们以后就不必重新计算它们。

我们还可以用动态规划解决 0-1 背包问题。要使用动态规划，我们首先创建一个维度从 0 到n和 0 到W的二维表。然后，我们使用自下而上的方法通过此表计算最优解：

```java
int knapsackDP(int[] w, int[] v, int n, int W) {
    if (n <= 0 || W <= 0) {
        return 0;
    }

    int[][] m = new int[n + 1][W + 1];
    for (int j = 0; j <= W; j++) {
        m[0][j] = 0;
    }

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= W; j++) { 
            if (w[i - 1] > j) {
                m[i][j] = m[i - 1][j];
            } else {
                m[i][j] = Math.max(
                  m[i - 1][j], 
                  m[i - 1][j - w[i - 1]] + v[i - 1]);
            }
        }
    }
    return m[n][W];
}

```

在此解决方案中，我们对项目编号n和重量限制W进行了嵌套循环。因此，它的运行时间是O(nW)。

## 六. 总结

在本教程中，我们展示了 0-1 背包问题的数学定义。然后我们用Java实现为这个问题提供了一个递归的解决方案。最后，我们使用动态规划来解决这个问题。
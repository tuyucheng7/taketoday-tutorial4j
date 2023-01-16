## 1. 简介

在本文中，我们描述了 Levenshtein 距离，也称为编辑距离。此处介绍的算法是由俄罗斯科学家 Vladimir Levenshtein 于 1965 年设计的。

我们将提供此算法的迭代和递归Java实现。

## 2.什么是编辑距离？

[Levenshtein 距离是两个](https://www.baeldung.com/cs/levenshtein-distance-computation)字符串之间差异的度量。从数学上讲，给定两个字符串 x和y ，距离测量将x转换为y所需的最少字符编辑次数。

通常允许三种类型的编辑：

1.  插入字符c
2.  删除一个字符c
3.  用c '替换字符c

示例：如果x = 'shot'和y = 'spot'，则两者之间的编辑距离为 1，因为可以通过将 ' h '替换为 ' p ' 将'shot'转换为'spot '。

在问题的某些子类中，与每种类型的编辑相关的成本可能不同。

例如，用位于键盘附近的字符替换的成本较低，否则成本较高。为简单起见，我们将在本文中将所有成本视为相等。

编辑距离的一些应用是：

1.  拼写检查器——检测文本中的拼写错误并在字典中找到最接近的正确拼写
2.  抄袭检测(参考 – [IEEE 论文](http://ieeexplore.ieee.org/document/4603758/))
3.  DNA 分析——寻找两个序列之间的相似性
4.  语音识别(参考 -[微软研究院](https://www.microsoft.com/en-us/research/publication/context-dependent-phonetic-string-edit-distance-for-automatic-speech-recognition/))

## 3.算法制定

让我们分别取两个长度为m和n的字符串 x和y。我们可以将每个字符串表示为x[1:m]和y[1:n]。

我们知道，在转换结束时，两个String将是等长的，并且在每个位置都有匹配的字符。所以，如果我们考虑每个字符串的第一个字符，我们有三个选择：

1.  替代：
    1.  确定用y[1]替换x[1]的成本 ( D1 ) 。如果两个字符相同，则此步骤的成本将为零。如果没有，那么成本将是一个
    2.  经过步骤 1.1，我们知道两个String都以相同的字符开头。因此，总成本现在是步骤 1.1 的成本与将字符串 x[2:m]的其余部分转换为y[2:n]的成本之和。
2.  插入：
    1.  在x中插入一个字符来匹配y中的第一个字符，这一步的成本是 one
    2.  在 2.1 之后，我们处理了一个来自y的字符。因此，总成本现在是步骤 2.1(即 1)的成本与将完整x[1:m]转换为剩余y (y[2:n])的成本之和
3.  删除：
    1.  从x中删除第一个字符，这一步的成本是一个
    2.  在 3.1 之后，我们处理了x中的一个字符，但完整的y仍有待处理。总成本将是 3.1(即 1)的成本与将剩余x转换为完整y的成本之和

解决方案的下一部分是找出从这三个选项中选择哪个选项。由于我们不知道最终哪个选项会导致成本最低，因此我们必须尝试所有选项并选择最佳选项。

## 4. 朴素的递归实现

我们可以看到，第 3 节中每个选项的第二步主要是相同的编辑距离问题，但在原始字符串的子字符串上。这意味着在每次迭代之后，我们都会遇到相同的问题，但字符串更小。

这个观察是制定递归算法的关键。递归关系可以定义为：

D(x[1:m], y[1:n]) = 分钟 {

D(x[2:m], y[2:n]) + 将 x[1] 替换为 y[1] 的成本，

D(x[1:m], y[2:n]) + 1,

D(x[2:m], y[1:n]) + 1

}

我们还必须为我们的递归算法定义基本情况，在我们的例子中是当一个或两个字符串变为空时：

1.  当两个String都为空时，则它们之间的距离为零

2.  当其中一个

    字符串

    为空时，它们之间的编辑距离就是另一个

    字符串的长度，

    因为我们需要大量的插入/删除操作才能将一个字符串转换为另一个字符串：

    -   示例：如果一个字符串是“dog”而另一个字符串是“”(空)，我们需要在空字符串中插入三个使其成为“dog” ，或者我们需要在“dog”中删除三个使其为空。因此它们之间的编辑距离是 3

该算法的简单递归实现：

```java
public class EditDistanceRecursive {

   static int calculate(String x, String y) {
        if (x.isEmpty()) {
            return y.length();
        }

        if (y.isEmpty()) {
            return x.length();
        } 

        int substitution = calculate(x.substring(1), y.substring(1)) 
         + costOfSubstitution(x.charAt(0), y.charAt(0));
        int insertion = calculate(x, y.substring(1)) + 1;
        int deletion = calculate(x.substring(1), y) + 1;

        return min(substitution, insertion, deletion);
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
          .min().orElse(Integer.MAX_VALUE);
    }
}
```

该算法具有指数复杂度。在每一步中，我们都分成三个递归调用，构建O(3^n)复杂度。

在下一节中，我们将看到如何对此进行改进。

## 5.动态规划方法

在分析递归调用时，我们观察到子问题的参数是原始字符串的后缀。这意味着只能有mn 个唯一的递归调用(其中m和n是x和y的多个后缀)。因此，最优解的复杂度应该是二次的，O(mn)。

让我们看看一些子问题(根据第 4 节中定义的递归关系)：

1.  D(x[1:m], y[1:n])的子问题是D(x[2:m], y[2:n]), D(x[1:m], y[2 :n])和D(x[2:m], y[1:n])
2.  D(x[1:m], y[2:n])的子问题是D(x[2:m], y[3:n]), D(x[1:m], y[3 :n])和D(x[2:m], y[2:n])
3.  D(x[2:m], y[1:n])的子问题是D(x[3:m], y[2:n]), D(x[2:m], y[2 :n])和D(x[3:m], y[1:n])

在所有三种情况下，子问题之一是D(x[2:m], y[2:n])。我们可以计算一次并在需要时再次使用结果，而不是像我们在天真的实现中那样计算三次。

这个问题有很多重叠的子问题，但是如果我们知道子问题的解，我们就很容易找到原问题的答案。因此，我们具有制定动态规划解决方案所需的两个属性，即[重叠子问题](https://en.wikipedia.org/wiki/Overlapping_subproblems)和[最优子结构](https://en.wikipedia.org/wiki/Optimal_substructure)。

我们可以通过引入[memoization](https://en.wikipedia.org/wiki/Memoization)来优化 naive 实现，即，将子问题的结果存储在一个数组中，并重用缓存的结果。

或者，我们也可以使用基于表的方法迭代地实现它：

```java
static int calculate(String x, String y) {
    int[][] dp = new int[x.length() + 1][y.length() + 1];

    for (int i = 0; i <= x.length(); i++) {
        for (int j = 0; j <= y.length(); j++) {
            if (i == 0) {
                dp[i][j] = j;
            }
            else if (j == 0) {
                dp[i][j] = i;
            }
            else {
                dp[i][j] = min(dp[i - 1][j - 1] 
                 + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)), 
                  dp[i - 1][j] + 1, 
                  dp[i][j - 1] + 1);
            }
        }
    }

    return dp[x.length()][y.length()];
}

```

该算法的性能明显优于递归实现。但是，它涉及大量的内存消耗。

这可以通过观察我们只需要表格中三个相邻单元格的值来找到当前单元格的值来进一步优化。

## 六. 总结

在本文中，我们描述了什么是 Levenshtein 距离以及如何使用递归和基于动态规划的方法计算它。

Levenshtein 距离只是字符串相似性的度量之一，其他一些度量是[Cosine Similarity](https://en.wikipedia.org/wiki/Cosine_similarity)(使用基于标记的方法并将字符串视为向量)、[Dice Coefficient](https://en.wikipedia.org/wiki/Sørensen–Dice_coefficient)等。
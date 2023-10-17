## 1. 概述

在本文中，我们将讨论[Minimax 算法](https://www.baeldung.com/cs/minimax-algorithm)及其在 AI 中的应用。由于它是一种博弈论算法，我们将使用它来实现一个简单的游戏。

我们还将讨论使用该算法的优势，并了解如何对其进行改进。

## 2.简介

Minimax 是一种决策算法，通常用于基于回合的两人游戏。该算法的目标是找到最佳下一步。

在算法中，一个玩家被称为最大化者，另一个玩家被称为最小化者。如果我们给游戏板分配一个评估分数，一个玩家会尝试选择得分最高的游戏状态，而另一个玩家会选择得分最低的状态。

换句话说，最大化 器努力获得最高分，而最小化器试图通过尝试反击来获得最低分。

它基于[零和博弈的](https://en.wikipedia.org/wiki/Zero-sum_game)概念。在零和博弈中，总效用分数在玩家之间分配。一个玩家得分的增加导致另一个玩家得分的减少。因此，总分始终为零。一名球员要赢，另一名球员必须输。此类游戏的示例有国际象棋、扑克牌、西洋跳棋、井字棋。

一个有趣的事实——1997 年，IBM 的国际象棋计算机 Deep Blue(使用 Minimax 构建)击败了 Garry Kasparov(国际象棋世界冠军)。

## 3.极小极大算法

我们的目标是为玩家找到最佳着法。为此，我们可以只选择具有最佳评估分数的节点。为了使过程更智能，我们还可以向前看并评估潜在对手的动作。

对于每一步，我们都可以在我们的计算能力允许的范围内预测尽可能多的动作。该算法假设对手的表现最佳。

从技术上讲，我们从根节点开始并选择可能的最佳节点。我们根据节点的评估分数评估节点。在我们的例子中，评估函数只能将分数分配给结果节点(叶子)。因此，我们递归地到达具有分数的叶子并反向传播分数。

考虑下面的博弈树：

[![极大极小值](https://www.baeldung.com/wp-content/uploads/2017/07/minimax.png)](https://www.baeldung.com/wp-content/uploads/2017/07/minimax.png)

Maximizer 从根节点开始，选择得分最高的着法。不幸的是，只有叶子有评估分数，因此算法必须递归地到达叶节点。在给定的游戏树中，当前轮到最小化器从叶节点中选择一个移动，因此将选择分数最低的节点(此处为节点 3 和 4)。它一直以类似的方式选择最佳节点，直到到达根节点。

现在，让我们正式定义算法的步骤：

1.  构建完整的博弈树
2.  使用评估函数评估叶子的分数
3.  考虑到玩家类型，从叶子到根的备份分数：
    -   对于最大玩家，选择得分最高的孩子
    -   对于最小玩家，选择得分最低的孩子
4.  在根节点，选择最大值的节点并执行相应的移动

## 4.实施

现在，让我们来实现一个游戏。

在游戏中，我们有一个有n根骨头的堆。两位玩家都必须轮流拿起 1,2 或 3 根骨头。不能拿走任何骨头的玩家将输掉比赛。每个玩家都发挥最佳状态。给定n的值，让我们编写一个 AI。

为了定义游戏规则，我们将实现GameOfBones类：

```java
class GameOfBones {
    static List<Integer> getPossibleStates(int noOfBonesInHeap) {
        return IntStream.rangeClosed(1, 3).boxed()
          .map(i -> noOfBonesInHeap - i)
          .filter(newHeapCount -> newHeapCount >= 0)
          .collect(Collectors.toList());
    }
}
```

此外，我们还需要Node和Tree类的实现：

```java
public class Node {
    int noOfBones;
    boolean isMaxPlayer;
    int score;
    List<Node> children;
    // setters and getters
}
public class Tree {
    Node root;
    // setters and getters
}
```

现在我们将实现该算法。它需要一棵博弈树来向前看并找到最佳着法。让我们来实现它：

```java
public class MiniMax {
    Tree tree;

    public void constructTree(int noOfBones) {
        tree = new Tree();
        Node root = new Node(noOfBones, true);
        tree.setRoot(root);
        constructTree(root);
    }

    private void constructTree(Node parentNode) {
        List<Integer> listofPossibleHeaps 
          = GameOfBones.getPossibleStates(parentNode.getNoOfBones());
        boolean isChildMaxPlayer = !parentNode.isMaxPlayer();
        listofPossibleHeaps.forEach(n -> {
            Node newNode = new Node(n, isChildMaxPlayer);
            parentNode.addChild(newNode);
            if (newNode.getNoOfBones() > 0) {
                constructTree(newNode);
            }
        });
    }
}
```

现在，我们将实施checkWin方法，该方法将通过为双方玩家选择最佳动作来模拟一场比赛。它将分数设置为：

-   +1，如果最大化者获胜
-   -1，如果最小化器获胜

如果第一个玩家(在我们的例子中是最大化者)获胜，checkWin 将返回true ：

```java
public boolean checkWin() {
    Node root = tree.getRoot();
    checkWin(root);
    return root.getScore() == 1;
}

private void checkWin(Node node) {
    List<Node> children = node.getChildren();
    boolean isMaxPlayer = node.isMaxPlayer();
    children.forEach(child -> {
        if (child.getNoOfBones() == 0) {
            child.setScore(isMaxPlayer ? 1 : -1);
        } else {
            checkWin(child);
        }
    });
    Node bestChild = findBestChild(isMaxPlayer, children);
    node.setScore(bestChild.getScore());
}
```

在这里，如果玩家是最大化者， findBestChild方法会找到得分最高的节点。否则，它返回得分最低的孩子：

```java
private Node findBestChild(boolean isMaxPlayer, List<Node> children) {
    Comparator<Node> byScoreComparator = Comparator.comparing(Node::getScore);
    return children.stream()
      .max(isMaxPlayer ? byScoreComparator : byScoreComparator.reversed())
      .orElseThrow(NoSuchElementException::new);
}
```

最后，让我们用一些n值(堆中骨骼的数量)来实现一个测试用例：

```java
@Test
public void givenMiniMax_whenCheckWin_thenComputeOptimal() {
    miniMax.constructTree(6);
    boolean result = miniMax.checkWin();
 
    assertTrue(result);
 
    miniMax.constructTree(8);
    result = miniMax.checkWin();
 
    assertFalse(result);
}
```

## 5.改进

对于大多数问题，构造一棵完整的博弈树是不可行的。在实践中，我们可以开发一个部分树(仅将树构建到预定义的层数)。

然后，我们必须实现一个评估函数，它应该能够为玩家决定当前状态的好坏。

即使我们不构建完整的博弈树，计算具有高分支因子的博弈的着法也可能非常耗时。

幸运的是，有一个选项可以找到最佳着法，而无需探索博弈树的每个节点。我们可以按照一些规则跳过一些分支，这不会影响最终的结果。这个过程称为 修剪。[Alpha-beta 剪枝](https://en.wikipedia.org/wiki/Alpha–beta_pruning)是 minimax 算法的一种流行变体。

## 六. 总结

Minimax 算法是计算机棋盘游戏最流行的算法之一。它广泛应用于回合制游戏。当玩家对游戏有完整的信息时，这可能是一个不错的选择。

对于分支因子特别高的游戏(例如围棋游戏)，它可能不是最佳选择。尽管如此，如果实施得当，它可以成为一个非常聪明的人工智能。
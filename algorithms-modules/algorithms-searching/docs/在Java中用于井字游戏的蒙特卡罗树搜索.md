## 1. 概述

在本文中，我们将探讨蒙特卡洛树搜索 (MCTS) 算法及其应用。

我们将通过在Java中实现 Tic-Tac-Toe 游戏来详细了解它的阶段。我们将设计一个可以用于许多其他实际应用程序的通用解决方案，只需进行最少的更改。

## 2.简介

简单地说，蒙特卡洛树搜索是一种概率搜索算法。它是一种独特的决策算法，因为它在具有大量可能性的开放式环境中效率很高。

如果你已经熟悉[Minimax](https://en.wikipedia.org/wiki/Minimax)等博弈论算法，它需要一个函数来评估当前状态，并且必须计算博弈树中的许多级别以找到最佳着法。

[不幸的是，在像围棋](https://en.wikipedia.org/wiki/Go_(game))这样有高分支因子(随着树的高度增加导致数百万种可能性)的游戏中这样做是不可行的，而且很难编写一个好的评估函数来计算当前状态是。

蒙特卡罗树搜索将[蒙特卡罗方法](https://en.wikipedia.org/wiki/Monte_Carlo_method)应用于博弈树搜索。由于它基于对游戏状态的随机抽样，因此不需要暴力破解每种可能性。此外，它不一定要求我们编写评估或良好的启发式函数。

而且，一个简短的旁注——它彻底改变了计算机围棋的世界。[自 2016 年 3 月以来，随着 Google 的AlphaGo](https://en.wikipedia.org/wiki/AlphaGo)(使用 MCTS 和神经网络构建)击败[李世石](https://en.wikipedia.org/wiki/Lee_Sedol)(围棋世界冠军)，它已成为一个流行的研究课题。

## 3. 蒙特卡洛树搜索算法

现在，让我们探讨该算法的工作原理。最初，我们将构建一个带有根节点的前瞻树(游戏树)，然后我们将通过随机推出不断扩展它。在此过程中，我们将维护每个节点的访问次数和获胜次数。

最后，我们将选择具有最有希望的统计信息的节点。

该算法由四个阶段组成；让我们详细探讨所有这些。

### 3.1. 选择

在这个初始阶段，算法从根节点开始并选择一个子节点，以便它选择具有最大获胜率的节点。我们还想确保每个节点都有公平的机会。

这个想法是不断选择最佳子节点，直到我们到达树的叶节点。选择这样一个子节点的一个好方法是使用 UCT(Upper Confidence Bound applied to trees)公式：
[![公式](https://www.baeldung.com/wp-content/uploads/2017/06/formula.png)](https://www.baeldung.com/wp-content/uploads/2017/06/formula.png)其中

-   w i = 第i步后的获胜次数
-   n i = 第i步之后的模拟次数
-   c = 勘探参数(理论上等于√2)
-   t = 父节点的模拟总数

该公式确保没有一个国家会成为饥饿的受害者，并且它也比其他国家更经常地发挥有前途的分支。

### 3.2. 扩张

当它不能再应用 UCT 来寻找后继节点时，它通过从叶节点追加所有可能的状态来扩展博弈树。

### 3.3. 模拟

Expansion 之后，算法任意选取一个子节点，从选定的节点开始模拟随机博弈，直到达到博弈的结果状态。如果在播放过程中随机或半随机选择节点，则称为轻播放。你还可以通过编写质量启发式或评估函数来选择大量播放。

### 3.4. 反向传播

这也称为更新阶段。一旦算法到达游戏结束，它就会评估状态以确定哪个玩家获胜。它向上遍历到根并增加所有访问节点的访问分数。如果该位置的玩家赢得了比赛，它还会更新每个节点的获胜分数。

MCTS 不断重复这四个阶段，直到某个固定的迭代次数或某个固定的时间量。

在这种方法中，我们根据随机移动估计每个节点的获胜分数。迭代次数越多，估计就越可靠。算法估计在搜索开始时会不太准确，并在足够长的时间后不断改进。同样，这完全取决于问题的类型。

## 4.试运行

[![Webp.net-gifmaker-2](https://www.baeldung.com/wp-content/uploads/2017/06/Webp.net-gifmaker-2.gif)](https://www.baeldung.com/wp-content/uploads/2017/06/Webp.net-gifmaker-2.gif)[![传奇](https://www.baeldung.com/wp-content/uploads/2017/06/legend.png)](https://www.baeldung.com/wp-content/uploads/2017/06/legend.png)

在这里，节点包含作为总访问量/获胜分数的统计信息。

## 5.实施

现在，让我们实现一个井字游戏——使用蒙特卡洛树搜索算法。

我们将为 MCTS 设计一个通用解决方案，该解决方案也可用于许多其他棋盘游戏。我们将查看本文中的大部分代码。

虽然为了解释清楚，我们可能不得不跳过一些次要细节(与 MCTS 没有特别相关)，但你总能在 GitHub 上找到完整的实现。

首先，我们需要Tree和Node类的基本实现以具有树搜索功能：

```java
public class Node {
    State state;
    Node parent;
    List<Node> childArray;
    // setters and getters
}
public class Tree {
    Node root;
}
```

由于每个节点都会有一个特定的问题状态，让我们也实现一个State类：

```java
public class State {
    Board board;
    int playerNo;
    int visitCount;
    double winScore;

    // copy constructor, getters, and setters

    public List<State> getAllPossibleStates() {
        // constructs a list of all possible states from current state
    }
    public void randomPlay() {
        / get a list of all possible positions on the board and 
           play a random move /
    }
}
```

现在，让我们实现MonteCarloTreeSearch类，它将负责从给定的游戏位置找到下一个最佳移动：

```java
public class MonteCarloTreeSearch {
    static final int WIN_SCORE = 10;
    int level;
    int opponent;

    public Board findNextMove(Board board, int playerNo) {
        // define an end time which will act as a terminating condition

        opponent = 3 - playerNo;
        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(opponent);

        while (System.currentTimeMillis() < end) {
            Node promisingNode = selectPromisingNode(rootNode);
            if (promisingNode.getState().getBoard().checkStatus() 
              == Board.IN_PROGRESS) {
                expandNode(promisingNode);
            }
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getBoard();
    }
}
```

在这里，我们不断迭代所有四个阶段，直到预定义的时间，最后，我们得到一棵具有可靠统计数据的树，以做出明智的决策。

现在，让我们为所有阶段实施方法。

我们将从选择阶段开始，这也需要实施 UCT：

```java
private Node selectPromisingNode(Node rootNode) {
    Node node = rootNode;
    while (node.getChildArray().size() != 0) {
        node = UCT.findBestNodeWithUCT(node);
    }
    return node;
}
public class UCT {
    public static double uctValue(
      int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return ((double) nodeWinScore / (double) nodeVisit) 
          + 1.41  Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.getState().getVisitCount();
        return Collections.max(
          node.getChildArray(),
          Comparator.comparing(c -> uctValue(parentVisit, 
            c.getState().getWinScore(), c.getState().getVisitCount())));
    }
}
```

该阶段推荐一个叶子节点，在扩展阶段应该进一步扩展：

```java
private void expandNode(Node node) {
    List<State> possibleStates = node.getState().getAllPossibleStates();
    possibleStates.forEach(state -> {
        Node newNode = new Node(state);
        newNode.setParent(node);
        newNode.getState().setPlayerNo(node.getState().getOpponent());
        node.getChildArray().add(newNode);
    });
}
```

接下来，我们编写代码来选择一个随机节点并从中模拟随机播放。此外，我们将有一个更新函数来传播从叶到根的分数和访问计数：

```java
private void backPropogation(Node nodeToExplore, int playerNo) {
    Node tempNode = nodeToExplore;
    while (tempNode != null) {
        tempNode.getState().incrementVisit();
        if (tempNode.getState().getPlayerNo() == playerNo) {
            tempNode.getState().addScore(WIN_SCORE);
        }
        tempNode = tempNode.getParent();
    }
}
private int simulateRandomPlayout(Node node) {
    Node tempNode = new Node(node);
    State tempState = tempNode.getState();
    int boardStatus = tempState.getBoard().checkStatus();
    if (boardStatus == opponent) {
        tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
        return boardStatus;
    }
    while (boardStatus == Board.IN_PROGRESS) {
        tempState.togglePlayer();
        tempState.randomPlay();
        boardStatus = tempState.getBoard().checkStatus();
    }
    return boardStatus;
}
```

现在我们完成了 MCTS 的实施。我们所需要的只是一个 Tic-Tac-Toe 特定的Board类实现。请注意，使用我们的实现来玩其他游戏；我们只需要更改Board类。

```java
public class Board {
    int[][] boardValues;
    public static final int DEFAULT_BOARD_SIZE = 3;
    public static final int IN_PROGRESS = -1;
    public static final int DRAW = 0;
    public static final int P1 = 1;
    public static final int P2 = 2;
    
    // getters and setters
    public void performMove(int player, Position p) {
        this.totalMoves++;
        boardValues[p.getX()][p.getY()] = player;
    }

    public int checkStatus() {
        / Evaluate whether the game is won and return winner.
           If it is draw return 0 else return -1 /         
    }

    public List<Position> getEmptyPositions() {
        int size = this.boardValues.length;
        List<Position> emptyPositions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (boardValues[i][j] == 0)
                    emptyPositions.add(new Position(i, j));
            }
        }
        return emptyPositions;
    }
}
```

我们刚刚实现了一个在 Tic-Tac-Toe 中无法击败的 AI。让我们写一个单元案例来证明 AI 对 AI 总是会导致平局：

```java
@Test
public void givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw() {
    Board board = new Board();
    int player = Board.P1;
    int totalMoves = Board.DEFAULT_BOARD_SIZE  Board.DEFAULT_BOARD_SIZE;
    for (int i = 0; i < totalMoves; i++) {
        board = mcts.findNextMove(board, player);
        if (board.checkStatus() != -1) {
            break;
        }
        player = 3 - player;
    }
    int winStatus = board.checkStatus();
 
    assertEquals(winStatus, Board.DRAW);
}
```

## 六、优点

-   它不一定需要有关游戏的任何战术知识
-   一个通用的 MCTS 实现可以重复用于任意数量的游戏，只需稍作修改
-   专注于赢得比赛的机会更高的节点
-   适用于具有高分支因子的问题，因为它不会在所有可能的分支上浪费计算
-   算法非常易于实现
-   执行可以在任何给定时间停止，它仍然会建议到目前为止计算出的下一个最佳状态

## 7.缺点

如果 MCTS 在没有任何改进的情况下以其基本形式使用，它可能无法建议合理的移动。如果没有充分访问节点会导致估计不准确，则可能会发生这种情况。

但是，可以使用一些技术改进 MCTS。它涉及领域特定以及领域无关的技术。

在领域特定技术中，模拟阶段产生更真实的结果而不是随机模拟。虽然它需要游戏特定技术和规则的知识。

## 8.总结

乍一看，很难相信依赖随机选择的算法可以产生智能人工智能。然而，经过深思熟虑的 MCTS 实施确实可以为我们提供一个可以用于许多游戏和决策问题的解决方案。
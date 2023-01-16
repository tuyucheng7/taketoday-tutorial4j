## 1. 简介

在本文中，我们将探讨使用Java导航迷宫的可能方法。

将迷宫视为黑白图像，黑色像素代表墙壁，白色像素代表路径。两个白色像素比较特殊，一个是迷宫的入口，另一个是出口。

给定这样一个迷宫，我们想找到一条从入口到出口的路径。

## 2. 迷宫建模

我们将迷宫视为一个二维整数数组。数组中数值的含义将按照以下约定：

-   0 -> 道路
-   1 -> 墙
-   2 -> 迷宫入口
-   3 -> 迷宫出口
-   4 -> 从入口到出口路径的单元格部分

我们将迷宫建模为图形。入口和出口是两个特殊的节点，它们之间的路径是有待确定的。

一个典型的图有两个属性，节点和边。边决定了图形的连通性，并将一个节点链接到另一个节点。

因此，我们假设每个节点有四个隐式边，将给定节点连接到它的左、右、上和下节点。

让我们定义方法签名：

```java
public List<Coordinate> solve(Maze maze) {
}
```

该方法的输入是一个迷宫，其中包含二维数组，命名约定如上。

该方法的响应是一个节点列表，它形成了从入口节点到出口节点的路径。

## 3.递归回溯(DFS)

### 3.1. 算法

一种相当明显的方法是探索所有可能的路径，如果存在，最终将找到一条路径。但这种方法的复杂性呈指数级增长，并且无法很好地扩展。

但是，可以自定义上述暴力解决方案，通过回溯和标记访问过的节点，在合理的时间内获得路径。该算法也[称为深度优先搜索。](https://www.baeldung.com/cs/graph-algorithms-bfs-dijkstra)

该算法可以概括为：

1.  如果我们在墙上或已经访问过的节点，返回失败
2.  否则如果我们是出口节点，则返回成功
3.  否则，将节点添加到路径列表中并在所有四个方向上递归行进。如果返回失败，则从路径中删除该节点并返回失败。找到出口时，路径列表将包含唯一路径

让我们将此算法应用到图 1(a) 所示的迷宫中，其中 S 是起点，E 是出口。

对于每个节点，我们按顺序遍历每个方向：右、下、左、上。

在 1(b) 中，我们探索了一条路径并撞到了墙上。然后我们回溯直到找到一个有非墙邻居的节点，并探索另一条路径，如 1(c) 所示。

我们再次撞墙，重复这个过程，最终找到了出口，如图1(d)所示：

[![dfs-1](https://www.baeldung.com/wp-content/uploads/2018/02/dfs-1-244x300.png)](https://www.baeldung.com/wp-content/uploads/2018/02/dfs-1-244x300.png)[![dfs-2](https://www.baeldung.com/wp-content/uploads/2018/02/dfs-2-240x300.png)](https://www.baeldung.com/wp-content/uploads/2018/02/dfs-2-240x300.png) [![dfs-3](https://www.baeldung.com/wp-content/uploads/2018/02/dfs-3-242x300.png)](https://www.baeldung.com/wp-content/uploads/2018/02/dfs-3-242x300.png)[![dfs-4](https://www.baeldung.com/wp-content/uploads/2018/02/dfs-4-239x300-2.png)](https://www.baeldung.com/wp-content/uploads/2018/02/dfs-4-239x300-2.png)

### 3.2. 执行

现在让我们看看Java实现：

首先，我们需要定义四个方向。我们可以用坐标来定义它。这些坐标，当添加到任何给定坐标时，将返回相邻坐标之一：

```java
private static int[][] DIRECTIONS 
  = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

```

我们还需要一个实用方法来添加两个坐标：

```java
private Coordinate getNextCoordinate(
  int row, int col, int i, int j) {
    return new Coordinate(row + i, col + j);
}
```

我们现在可以定义方法签名solve。 这里的逻辑很简单——如果有从入口到出口的路径，则返回路径，否则，返回一个空列表：

```java
public List<Coordinate> solve(Maze maze) {
    List<Coordinate> path = new ArrayList<>();
    if (
      explore(
        maze, 
        maze.getEntry().getX(),
        maze.getEntry().getY(),
        path
      )
      ) {
        return path;
    }
    return Collections.emptyList();
}
```

让我们定义上面引用的explore方法。如果有路径，则返回 true，参数path中包含坐标列表。该方法具有三个主要块。

首先，我们丢弃无效节点，即迷宫外或墙的一部分的节点。之后，我们将当前节点标记为已访问，这样我们就不会一次又一次地访问同一个节点。

最后，如果找不到出口，我们将向所有方向递归移动：

```java
private boolean explore(
  Maze maze, int row, int col, List<Coordinate> path) {
    if (
      !maze.isValidLocation(row, col) 
      || maze.isWall(row, col) 
      || maze.isExplored(row, col)
    ) {
        return false;
    }

    path.add(new Coordinate(row, col));
    maze.setVisited(row, col, true);

    if (maze.isExit(row, col)) {
        return true;
    }

    for (int[] direction : DIRECTIONS) {
        Coordinate coordinate = getNextCoordinate(
          row, col, direction[0], direction[1]);
        if (
          explore(
            maze, 
            coordinate.getX(), 
            coordinate.getY(), 
            path
          )
        ) {
            return true;
        }
    }

    path.remove(path.size() - 1);
    return false;
}
```

该解决方案使用的堆栈大小达到迷宫的大小。

## 4. 变体——最短路径(BFS)

### 4.1. 算法

上面描述的递归算法找到路径，但它不一定是最短路径。为了找到最短路径，我们可以使用另一种称为[广度优先搜索的](https://en.wikipedia.org/wiki/Breadth-first_search)图遍历方法。

在 DFS 中，首先探索一个孩子及其所有孙子，然后再转向另一个孩子。而在 BFS 中，我们将在继续探索孙子之前探索所有直接的孩子。这将确保同时探索距父节点特定距离的所有节点。

该算法可以概述如下：

1.  在队列中添加起始节点
2.  当队列不为空时，弹出一个节点，执行以下操作：
    1.  如果我们到达墙或节点已经被访问，跳到下一次迭代
    2.  如果到达出口节点，则从当前节点回溯到起始节点以找到最短路径
    3.  否则，将四个方向上的所有直接邻居添加到队列中

这里的一件重要事情是节点必须跟踪它们的父节点，即从它们被添加到队列的位置。一旦遇到出口节点，这对于找到路径很重要。

以下动画显示了使用此算法探索迷宫时的所有步骤。我们可以观察到，在进入下一个级别之前，首先探索了相同距离的所有节点：

[![bfs-1](https://www.baeldung.com/wp-content/uploads/2018/02/bfs-1.gif)](https://www.baeldung.com/wp-content/uploads/2018/02/bfs-1.gif)

### 4.2. 执行

现在让我们用Java实现这个算法。我们将重用上一节中定义的DIRECTIONS变量。

让我们首先定义一个实用方法来从给定节点回溯到它的根。一旦找到出口，这将用于跟踪路径：

```java
private List<Coordinate> backtrackPath(
  Coordinate cur) {
    List<Coordinate> path = new ArrayList<>();
    Coordinate iter = cur;

    while (iter != null) {
        path.add(iter);
        iter = iter.parent;
    }

    return path;
}
```

现在让我们定义核心方法solve。我们将重用 DFS 实现中使用的三个块，即验证节点、标记已访问节点和遍历相邻节点。

我们只做一个小的修改。我们将使用 FIFO 数据结构来跟踪邻居并迭代它们，而不是递归遍历：

```java
public List<Coordinate> solve(Maze maze) {
    LinkedList<Coordinate> nextToVisit 
      = new LinkedList<>();
    Coordinate start = maze.getEntry();
    nextToVisit.add(start);

    while (!nextToVisit.isEmpty()) {
        Coordinate cur = nextToVisit.remove();

        if (!maze.isValidLocation(cur.getX(), cur.getY()) 
          || maze.isExplored(cur.getX(), cur.getY())
        ) {
            continue;
        }

        if (maze.isWall(cur.getX(), cur.getY())) {
            maze.setVisited(cur.getX(), cur.getY(), true);
            continue;
        }

        if (maze.isExit(cur.getX(), cur.getY())) {
            return backtrackPath(cur);
        }

        for (int[] direction : DIRECTIONS) {
            Coordinate coordinate 
              = new Coordinate(
                cur.getX() + direction[0], 
                cur.getY() + direction[1], 
                cur
              );
            nextToVisit.add(coordinate);
            maze.setVisited(cur.getX(), cur.getY(), true);
        }
    }
    return Collections.emptyList();
}
```

## 5.总结

在本教程中，我们描述了两种主要的图形算法深度优先搜索和广度优先搜索来解决迷宫问题。我们还谈到了 BFS 如何给出从入口到出口的最短路径。

如需进一步阅读，请查找其他解决迷宫的方法，例如 A 和 Dijkstra 算法。
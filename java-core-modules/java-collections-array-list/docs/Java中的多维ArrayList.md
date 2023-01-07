## 1. 概述

创建多维ArrayList在编程过程中经常出现。在许多情况下，需要创建二维ArrayList或三维ArrayList。

在本教程中，我们将讨论如何在 Java中创建多维ArrayList 。

## 2.二维 [ArrayList](https://www.baeldung.com/java-arraylist)

假设我们要表示一个有 3 个顶点的[图](https://www.baeldung.com/java-graphs)，编号为 0 到 2。此外，我们假设图中有 3 条边 (0, 1)、(1, 2) 和 (2, 0)，其中一对顶点代表一条边。

 我们可以通过创建和填充 ArrayList 的ArrayList来表示二维ArrayList中的边。

首先，让我们创建一个新的二维ArrayList：

```java
int vertexCount = 3;
ArrayList<ArrayList<Integer>> graph = new ArrayList<>(vertexCount);
```

接下来，我们将用另一个ArrayList初始化ArrayList的每个元素：

```java
for(int i=0; i < vertexCount; i++) {
    graph.add(new ArrayList());
}
```

最后，我们可以将所有边 (0, 1)、(1, 2) 和 (2, 0) 添加到我们的二维ArrayList中：

```java
graph.get(0).add(1);
graph.get(1).add(2);
graph.get(2).add(0);
```

我们还假设我们的图不是有向图。因此，我们还需要将边 (1, 0)、(2, 1) 和 (0, 2) 添加到我们的二维ArrayList中：

```java
graph.get(1).add(0);
graph.get(2).add(1);
graph.get(0).add(2);
```

然后，为了遍历整个图，我们可以使用双 for 循环：

```java
int vertexCount = graph.size();
for (int i = 0; i < vertexCount; i++) {
    int edgeCount = graph.get(i).size();
    for (int j = 0; j < edgeCount; j++) {
        Integer startVertex = i;
        Integer endVertex = graph.get(i).get(j);
        System.out.printf("Vertex %d is connected to vertex %d%n", startVertex, endVertex);
    }
}
```

## 3. 三维ArrayList

在上一节中，我们创建了一个二维ArrayList。按照相同的逻辑，让我们创建一个三维ArrayList：

假设我们想要表示一个 3-D 空间。因此，这个 3-D 空间中的每个点都将由三个坐标表示，比如 X、Y 和 Z。

除此之外，让我们假设每个点都有一种颜色，红色、绿色、蓝色或黄色。现在，每个点 (X, Y, Z) 及其颜色都可以用一个三维ArrayList 来表示。

为简单起见，假设我们正在创建一个 (2 x 2 x 2) 3-D 空间。它将有八个点：(0, 0, 0), (0, 0, 1), (0, 1, 0), (0, 1, 1), (1, 0, 0), (1, 0 , 1), (1, 1, 0), 和 (1, 1, 1)。

让我们首先初始化变量和 3-D ArrayList：

```java
int x_axis_length = 2;
int y_axis_length = 2;
int z_axis_length = 2;	
ArrayList<ArrayList<ArrayList<String>>> space = new ArrayList<>(x_axis_length);
```

然后，让我们用ArrayList<ArrayList<String>>初始化ArrayList的每个元素：

```java
for (int i = 0; i < x_axis_length; i++) {
    space.add(new ArrayList<ArrayList<String>>(y_axis_length));
    for (int j = 0; j < y_axis_length; j++) {
        space.get(i).add(new ArrayList<String>(z_axis_length));
    }
}
```

现在，我们可以为空间中的点添加颜色。让我们为点 (0, 0, 0) 和 (0, 0, 1) 添加红色：

```java
space.get(0).get(0).add(0,"Red");
space.get(0).get(0).add(1,"Red");
```

然后，让我们为点 (0, 1, 0) 和 (0, 1, 1) 设置蓝色：

```java
space.get(0).get(1).add(0,"Blue");
space.get(0).get(1).add(1,"Blue");
```

同样，我们可以继续在空间中填充其他颜色的点。

请注意，坐标为 (i, j, k) 的点的颜色信息存储在以下 3-D ArrayList元素中：

```java
space.get(i).get(j).get(k)

```

正如我们在这个例子中看到的， 空间变量是一个ArrayList。 此外，这个ArrayList的每个元素都是一个二维ArrayList(类似于我们在第 2 节中看到的)。

请注意，我们 空间 ArrayList中元素的索引表示 X 坐标，而出现在该索引处的每个二维ArrayList表示 (Y, Z) 坐标。

## 4。总结

在本文中，我们讨论了如何在 Java中创建多维ArrayList 。我们看到了如何使用二维ArrayList表示图形。此外，我们还探讨了如何使用 3-D ArrayList表示 3-D 空间坐标。

第一次，我们使用ArrayList的ArrayList， 而第二次，我们使用二维ArrayList的ArrayList。同样，要创建一个 N-Dimensional ArrayList，我们可以扩展相同的概念。
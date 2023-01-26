## 1. 概述

图的深度优先遍历(或搜索)类似于树的深度优先遍历。这里唯一的问题是，与树不同，图可能包含循环(一个节点可能会被访问两次)。
为避免多次处理节点，需要使用布尔数组visited。

例子：

```
输入: n = 4, e = 6 
0 -> 1, 0 -> 2, 1 -> 2, 2 -> 0, 2 -> 3, 3 -> 3 
输出: 顶点1的DFS : 1 2 0 3 
```

<img src="../assets/img-10.gif">

```
输入: n = 4, e = 6 
2 -> 0, 0 -> 2, 1 -> 2, 0 -> 1, 3 -> 3, 1 -> 3 
输出: 顶点2的DFS : 2 0 1 3 
```

<img src="../assets/img-11.gif">

## 2. 算法实现

深度优先搜索是一种用于遍历或搜索树或图数据结构的算法。
该算法从根节点开始(在图的情况下选择某个任意节点作为根节点)并在回溯之前沿着每个分支尽可能地搜索。
所以基本思路是从根节点或任意节点开始，标记该节点，移动到相邻的未标记节点，继续这个循环，直到没有未标记的相邻节点。
然后回溯并检查其他未标记的节点并遍历它们。最后，打印路径中的节点。

```
算法:
创建一个递归函数，该函数接收节点的索引和visited数组。
1. 将当前节点标记为已访问并打印该节点。
2. 遍历所有相邻和未标记的节点，并以相邻节点的索引调用递归函数。
```

下面是简单的深度优先遍历的实现。

```java
public class DFS_Graph {
  private int V;
  private LinkedList<Integer>[] adj;

  @SuppressWarnings("unchecked")
  DFS_Graph(int V) {
    this.V = V;
    adj = new LinkedList[V];
    for (int i = 0; i < V; i++)
      adj[i] = new LinkedList<>();
  }

  void addEdge(int v, int w) {
    adj[v].add(w);
  }

  void DFS(int v) {
    boolean[] visited = new boolean[V];
    DFSUtil(v, visited);
  }

  private void DFSUtil(int v, boolean[] visited) {
    visited[v] = true;
    System.out.print(v + " ");
    Iterator<Integer> i = adj[v].iterator();
    while (i.hasNext()) {
      int n = i.next();
      if (!visited[n])
        DFSUtil(n, visited);
    }
  }
}

class DFS_GraphUnitTest {

  @Test
  void givenGraph_whenPrintDFS_thenCorrect() {
    DFS_Graph graph = new DFS_Graph(4);
    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(1, 2);
    graph.addEdge(2, 0);
    graph.addEdge(2, 3);
    graph.addEdge(3, 3);
    System.out.println("以下是深度优先遍历" + "(从顶点2开始)");
    graph.DFS(2);
  }
}
```

时间复杂度：O(V+E)，其中V是图中的顶点数，E是图中的边数。

空间复杂度：O(V)，因为需要一个额外大小为V的visited数组。

处理断开连接的图：

这将通过处理极端案例来实现。

上面的代码仅遍历从给定源顶点可到达的顶点。从给定顶点可能无法到达所有顶点，如在断开连接图中。
要对此类图进行完整的DFS遍历，需要在DFS之后从所有未访问的节点运行DFS。

递归函数保持不变。

```
算法
1. 创建一个递归函数，该函数接收节点的索引和visited数组。
2. 将当前节点标记为已访问并打印该节点。
3. 遍历所有相邻和未标记的节点，并以相邻节点的索引调用递归函数。
4. 运行一个从0到顶点数的循环，并检查该节点是否在以前的DFS中未被访问，使用当前节点调用递归函数。
```

以下为具体实现：


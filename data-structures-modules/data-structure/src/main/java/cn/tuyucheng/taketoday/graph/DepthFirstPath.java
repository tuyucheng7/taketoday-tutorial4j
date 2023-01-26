package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.stack.Stack;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: javastudy
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-17 15:24
 */
@SuppressWarnings("all")
public class DepthFirstPath {
  private boolean[] marked;
  private int s;
  private int[] edgeTo;

  public DepthFirstPath(Graph G, int s) {
    this.marked = new boolean[G.V()];
    this.s = s;
    this.edgeTo = new int[G.V()];
    dfs(G, s);
  }

  private void dfs(Graph G, int v) {
    marked[v] = true;
    for (Integer node : G.adj(v)) {
      if (!marked[node]) {
        edgeTo[node] = v;
        dfs(G, node);
      }
    }
  }

  public boolean hasPathTo(int v) {
    return marked[v];
  }

  public Stack<Integer> pathTo(int v) {
    if (!hasPathTo(v)) {
      return null;
    }
    Stack<Integer> stack = new Stack<>();
    for (int i = v; i != s; i = edgeTo[i]) {
      stack.push(i);
    }
    stack.push(s);
    return stack;
  }
}
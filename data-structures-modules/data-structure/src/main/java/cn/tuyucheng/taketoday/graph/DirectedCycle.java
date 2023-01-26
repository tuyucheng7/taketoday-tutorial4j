package cn.tuyucheng.taketoday.graph;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: javastudy
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-17 15:24
 */
@SuppressWarnings("all")
public class DirectedCycle {
  private boolean[] marked;
  private boolean hasCycle;
  private boolean[] onStack;

  public DirectedCycle(Digraph digraph) {
    this.marked = new boolean[digraph.V()];
    this.hasCycle = false;
    this.onStack = new boolean[digraph.V()];
    for (int i = 0; i < digraph.V(); i++) {
      if (!marked[i]) {
        dfs(digraph, i);
      }
    }
  }

  private void dfs(Digraph digraph, int v) {
    marked[v] = true;
    onStack[v] = true;
    for (Integer integer : digraph.adj(v)) {
      if (!marked[integer]) {
        dfs(digraph, integer);
      }
      if (onStack[integer]) {
        this.hasCycle = true;
        return;
      }
    }
    onStack[v] = false;
  }

  public boolean hasCycle() {
    return this.hasCycle;
  }
}
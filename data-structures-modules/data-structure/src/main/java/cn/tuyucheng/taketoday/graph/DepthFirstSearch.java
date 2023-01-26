package cn.tuyucheng.taketoday.graph;

public class DepthFirstSearch {
  private boolean[] marked;
  private int count;

  public DepthFirstSearch(Graph G, int s) {
    this.marked = new boolean[G.V()];
    this.count = 0;
    dfs(G, s);
  }

  private void dfs(Graph G, int v) {
    marked[v] = true;
    for (Integer integer : G.adj(v)) {
      if (!marked[integer]) {
        dfs(G, integer);
      }
    }
    count++;
  }

  public boolean marked(int w) {
    return marked[w];
  }

  public int count() {
    return count;
  }
}
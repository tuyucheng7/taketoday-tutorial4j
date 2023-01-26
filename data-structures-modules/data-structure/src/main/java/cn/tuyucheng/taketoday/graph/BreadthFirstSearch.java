package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.queue.Queue;

public class BreadthFirstSearch {
  private boolean[] marked;
  private int count;
  private Queue<Integer> waitSearch;

  public BreadthFirstSearch(Graph g, int s) {
    this.marked = new boolean[g.V()];
    this.count = 0;
    this.waitSearch = new Queue<>();
    bfs(g, s);
  }

  private void bfs(Graph G, int v) {
    marked[v] = true;
    waitSearch.enQueue(v);
    while (!waitSearch.isEmpty()) {
      Integer node = waitSearch.deQueue();
      for (Integer integer : G.adj(node)) {
        if (!marked[integer]) {
          bfs(G, integer);
        }
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
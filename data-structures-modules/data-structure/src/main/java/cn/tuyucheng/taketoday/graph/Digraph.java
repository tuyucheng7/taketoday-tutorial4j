package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.queue.Queue;

/**
 * @author: tuyucheng
 * @date 2021/12/319:01
 * @Description: 代码是我心中的一首诗
 */
public class Digraph {
  private int V;
  private int E;
  private Queue<Integer>[] adj;

  public Digraph(int V) {
    this.V = V;
    this.E = 0;
    this.adj = new Queue[V];
    for (int i = 0; i < adj.length; i++) {
      adj[i] = new Queue<>();
    }
  }

  public int V() {
    return this.V;
  }

  public int E() {
    return this.E;
  }

  public void addEdge(int v, int w) {
    adj[v].enQueue(w);
    E++;
  }

  public Queue<Integer> adj(int v) {
    return adj[v];
  }

  private Digraph reverse() {
    Digraph digraph = new Digraph(V);
    for (int i = 0; i < V; i++) {
      for (Integer integer : adj[i]) {
        digraph.addEdge(integer, i);
      }
    }
    return digraph;
  }
}
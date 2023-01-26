package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.queue.Queue;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: javastudy
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-17 15:24
 */
@SuppressWarnings("all")
public class EdgeWeightGraph {
  private final int V;
  private int E;
  private Queue<Edge>[] adj;

  public EdgeWeightGraph(int V) {
    this.V = V;
    this.E = 0;
    this.adj = new Queue[V];
    for (int i = 0; i < adj.length; i++) {
      adj[i] = new Queue<>();
    }
  }

  public int V() {
    return V;
  }

  public int E() {
    return E;
  }

  public void addEdge(Edge e) {
    int x = e.either();
    int y = e.other(x);
    adj[x].enQueue(e);
    adj[y].enQueue(e);
    E++;
  }

  public Queue<Edge> adj(int v) {
    return adj[v];
  }

  public Queue<Edge> edges() {
    Queue<Edge> edges = new Queue<>();
    for (int i = 0; i < V; i++) {
      for (Edge edge : adj(i)) {
        if (edge.other(i) < i) {
          edges.enQueue(edge);
        }
      }
    }
    return edges;
  }
}
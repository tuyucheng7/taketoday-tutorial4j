package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.queue.TuyuchengQueue;

public class TuyuchengGraph {
  private TuyuchengQueue<Integer>[] queues;
  private final int V;
  private int E;

  public TuyuchengGraph(int V) {
    queues = new TuyuchengQueue[V];
    this.V = V;
    this.E = 0;
    for (int i = 0; i < V; i++) {
      queues[i] = new TuyuchengQueue<>();
    }
  }

  public int getV() {
    return V;
  }

  public int getE() {
    return E;
  }

  public void addEdge(int x, int y) {
    queues[x].enQueue(y);
    queues[y].enQueue(x);
    E++;
  }

  public TuyuchengQueue<Integer> getQueue(int v) {
    return queues[v];
  }
}
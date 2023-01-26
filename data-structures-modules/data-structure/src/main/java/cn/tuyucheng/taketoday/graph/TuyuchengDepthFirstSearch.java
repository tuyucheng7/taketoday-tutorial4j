package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.queue.TuyuchengQueue;

public class TuyuchengDepthFirstSearch {
  private boolean[] isMarked;
  private int count;

  public TuyuchengDepthFirstSearch(TuyuchengGraph graph, int s) {
    isMarked = new boolean[graph.getV()];
    this.count = 0;
    depthFirstSearch(graph, s);
  }

  private void depthFirstSearch(TuyuchengGraph graph, int s) {
    isMarked[s] = true;
    TuyuchengQueue<Integer> sQueue = graph.getQueue(s);
    for (Integer integer : sQueue) {
      if (!marked(integer))
        depthFirstSearch(graph, integer);
    }
    count++;
  }

  public boolean marked(int w) {
    return isMarked[w];
  }

  public int count() {
    return count;
  }
}
package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.queue.TuyuchengQueue;

public class TuyuchengBreadthFirstSearch {
  private boolean[] marked;
  private int count;
  private TuyuchengQueue<Integer> waitSearch;

  public TuyuchengBreadthFirstSearch(TuyuchengGraph graph, int s) {
    marked = new boolean[graph.getV()];
    this.count = 0;
    waitSearch = new TuyuchengQueue<>();
    breadthFirstSearch(graph, s);
  }

  private void breadthFirstSearch(TuyuchengGraph graph, int s) {
    marked[s] = true;
    waitSearch.enQueue(s);
    while (!waitSearch.isEmpty()){
      Integer node = waitSearch.deQueue();
      for (Integer integer : graph.getQueue(node)) {
        if(!isMarked(integer)){
          breadthFirstSearch(graph,integer);
        }
      }
    }
    count++;
  }

  private boolean isMarked(int w) {
    return marked[w];
  }

  public int getCount() {
    return count;
  }
}
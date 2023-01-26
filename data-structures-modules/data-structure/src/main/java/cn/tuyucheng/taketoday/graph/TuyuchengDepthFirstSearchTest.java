package cn.tuyucheng.taketoday.graph;

public class TuyuchengDepthFirstSearchTest {
  public static void main(String[] args) {
    TuyuchengGraph graph = new TuyuchengGraph(13);
    graph.addEdge(0, 5);
    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(0, 6);
    graph.addEdge(5, 3);
    graph.addEdge(5, 4);
    graph.addEdge(3, 4);
    graph.addEdge(4, 6);
    graph.addEdge(7, 8);
    graph.addEdge(9, 11);
    graph.addEdge(9, 10);
    graph.addEdge(9, 12);
    graph.addEdge(11, 12);

    TuyuchengDepthFirstSearch depthFirstSearch = new TuyuchengDepthFirstSearch(graph, 0);
    int count = depthFirstSearch.count();
    System.out.println("与起点0相通的顶点数量为:" + count);
    boolean marked5 = depthFirstSearch.marked(5);
    System.out.println("顶点5是否与0相通" + marked5);
    boolean marked7 = depthFirstSearch.marked(7);
    System.out.println("顶点7是否与0相通" + marked7);
  }
}
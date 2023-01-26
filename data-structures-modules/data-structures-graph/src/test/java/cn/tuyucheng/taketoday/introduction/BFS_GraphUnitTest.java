package cn.tuyucheng.taketoday.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BFS_GraphUnitTest {

    @Test
    @DisplayName("givenGraph_whenPrintBFS_thenCorrect")
    void givenGraph_whenPrintBFS_thenCorrect() {
        BFS_Graph graph = new BFS_Graph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 3);
        System.out.println("Following is Breadth First Traversal " + "(starting from vertex 2)");
        graph.BFS(2);
    }
}
package cn.tuyucheng.taketoday.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DFS_GraphUnitTest {

    @Test
    @DisplayName("givenGraph_whenPrintDFS_thenCorrect")
    void givenGraph_whenPrintDFS_thenCorrect() {
        DFS_Graph graph = new DFS_Graph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 3);
        System.out.println("以下是深度优先遍历" + "(从顶点2开始)");
        graph.DFS(2);
    }
}
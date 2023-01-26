package cn.tuyucheng.taketoday.introduction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class AdjacencyListGraphUnitTest {

    @Test
    @DisplayName("givenAdjList_whenCreateGrapth_thenPrint")
    void givenAdjList_whenCreateGrapth_thenPrint() {
        AdjacencyListGraph graph = new AdjacencyListGraph();
        int V = 5;
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++)
            adj.add(new ArrayList<>());
        graph.addEdge(adj, 0, 1);
        graph.addEdge(adj, 0, 4);
        graph.addEdge(adj, 1, 2);
        graph.addEdge(adj, 1, 3);
        graph.addEdge(adj, 1, 4);
        graph.addEdge(adj, 2, 3);
        graph.addEdge(adj, 3, 4);
        graph.printGraph(adj);
    }
}
package cn.tuyucheng.taketoday.introduction;

import java.util.ArrayList;

public class AdjacencyListGraph {

    /**
     * 在无向图中添加边
     * @param adj 邻接表
     */
    void addEdge(ArrayList<ArrayList<Integer>> adj, int u, int v) {
        adj.get(v).add(u);
        adj.get(u).add(v);
    }

    void printGraph(ArrayList<ArrayList<Integer>> adj) {
        for (int i = 0; i < adj.size(); i++) {
            System.out.println("\nAdjacency list of vertex" + i);
            System.out.print("head");
            for (int j = 0; j < adj.get(i).size(); j++)
                System.out.print(" -> " + adj.get(i).get(j));
            System.out.println();
        }
    }
}
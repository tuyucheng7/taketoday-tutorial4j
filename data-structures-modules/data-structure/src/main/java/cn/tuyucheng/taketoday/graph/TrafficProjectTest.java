package cn.tuyucheng.taketoday.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: javastudy
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-17 15:24
 */
@SuppressWarnings("all")
public class TrafficProjectTest {
  public static void main(String[] args) {
    File file = new File("datastructure/traffic_project.txt");
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
    ) {
      Integer totalNumber = Integer.parseInt(br.readLine());
      Graph graph = new Graph(totalNumber);
      int totalRoad = Integer.parseInt(br.readLine());
      for (int i = 0; i < totalRoad; i++) {
        String[] s = br.readLine().split(" ");
        graph.addEdge(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
      }
      DepthFirstSearch depthFirstSearch = new DepthFirstSearch(graph, 9);
      System.out.println("九号城市与十号城市是否连通:" + depthFirstSearch.marked(10));
      System.out.println("九号城市与八号城市是否连通:" + depthFirstSearch.marked(8));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
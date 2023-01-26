package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.stack.Stack;

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
public class DepthFirstPathTest {
  public static void main(String[] args) {
    File file = new File("data-structure/road_find.txt");
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
    ) {
      int cityNumber = Integer.parseInt(br.readLine());
      int edgeNumber = Integer.parseInt(br.readLine());
      Graph graph = new Graph(cityNumber);
      for (int i = 0; i < edgeNumber; i++) {
        String[] s = br.readLine().split(" ");
        graph.addEdge(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
      }
      DepthFirstPath depthFirstPath = new DepthFirstPath(graph, 0);
      Stack<Integer> stack = depthFirstPath.pathTo(4);
      System.out.print("路径为:\t");
      for (Integer integer : stack) {
        System.out.printf("%d\t", integer);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
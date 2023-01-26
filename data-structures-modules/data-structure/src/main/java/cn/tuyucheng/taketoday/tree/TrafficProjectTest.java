package cn.tuyucheng.taketoday.tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TrafficProjectTest {
  public static void main(String[] args) {
    File file = new File("data-structure/traffic_project.txt");
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
      int totalNumber = Integer.parseInt(br.readLine());
      UF_Tree ufTree = new UF_Tree(totalNumber);
      int totalRoads = Integer.parseInt(br.readLine());
      for (int i = 0; i < totalRoads; i++) {
        String[] s = br.readLine().split(" ");
        ufTree.union(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
      }
      int count = ufTree.count() - 1;
      System.out.println("还需要" + count + "条道路才能连通");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
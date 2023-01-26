package cn.tuyucheng.taketoday.tree;

import java.util.Scanner;

@SuppressWarnings("all")
public class TuyuchengUnionFindTest {
  public static void main(String[] args) {
    TuyuchengUnionFind ufTree = new TuyuchengUnionFind(5);
    System.out.println("初始情况下,并查集中有:" + ufTree.groupCount() + "个分组");
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println("请输入第一个要合并的元素:");
      int p = scanner.nextInt();
      System.out.println("请输入第二个要合并的元素:");
      int q = scanner.nextInt();
      if (ufTree.isConnected(p, q)) {
        System.out.println(p + "元素和" + q + "元素已经在同一个组中了");
        continue;
      }
      ufTree.union(p, q);
      System.out.println("当前并查集中还有:" + ufTree.groupCount() + "个分组");
    }
  }
}
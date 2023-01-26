package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.stack.Stack;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: javastudy
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-17 15:24
 */
@SuppressWarnings("all")
public class TopLogicalTest {
  public static void main(String[] args) {
    Digraph digraph = new Digraph(6);
    digraph.addEdge(0, 2);
    digraph.addEdge(2, 4);
    digraph.addEdge(4, 5);
    digraph.addEdge(0, 3);
    digraph.addEdge(3, 4);
    digraph.addEdge(1, 3);

    TopLogical topLogical = new TopLogical(digraph);
    Stack<Integer> stack = topLogical.order();
    System.out.printf("%s\t", "拓扑排序的结果为:");
    for (Integer integer : stack) {
      System.out.printf("%d\t", integer);
    }
  }
}
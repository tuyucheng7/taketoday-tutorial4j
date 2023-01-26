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
public class TopLogical {
  private Stack<Integer> stack;

  public TopLogical(Digraph digraph) {
    DirectedCycle directedCycle = new DirectedCycle(digraph);
    if (!directedCycle.hasCycle()) {
      DepthFirstOrder depthFirstOrder = new DepthFirstOrder(digraph);
      stack = depthFirstOrder.reversePost();
    }
  }

  public boolean hasCycle() {
    return stack == null;
  }

  public Stack<Integer> order() {
    return this.stack;
  }
}
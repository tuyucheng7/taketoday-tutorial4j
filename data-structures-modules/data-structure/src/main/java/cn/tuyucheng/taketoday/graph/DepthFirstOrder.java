package cn.tuyucheng.taketoday.graph;

import cn.tuyucheng.taketoday.stack.Stack;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: java-study
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-16 04:32
 */
@SuppressWarnings("all")
public class DepthFirstOrder {
  private boolean[] marked;
  private Stack<Integer> reversePost;

  public DepthFirstOrder(Digraph digraph) {
    this.marked = new boolean[digraph.V()];
    this.reversePost = new Stack<>();
    for (int i = 0; i < digraph.V(); i++) {
      if (!marked[i]) {
        dfs(digraph, i);
      }
    }
  }

  private void dfs(Digraph digraph, int v) {
    marked[v] = true;
    for (Integer integer : digraph.adj(v)) {
      if (!marked[integer]) {
        dfs(digraph, integer);
      }
    }
    reversePost.push(v);
  }

  public Stack<Integer> reversePost() {
    return reversePost;
  }
}
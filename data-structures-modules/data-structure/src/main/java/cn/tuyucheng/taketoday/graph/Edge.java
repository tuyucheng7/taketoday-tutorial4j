package cn.tuyucheng.taketoday.graph;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: javastudy
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-17 15:24
 */
@SuppressWarnings("all")
public class Edge implements Comparable<Edge> {
  private int v;
  private int w;
  private double weight;

  public Edge(int v, int w, double weight) {
    this.v = v;
    this.w = w;
    this.weight = weight;
  }

  public double weight() {
    return this.weight;
  }

  public int either() {
    return v;
  }

  public int other(int vertex) {
    if (vertex == v) {
      return w;
    }
    return v;
  }

  @Override
  public int compareTo(Edge o) {
    int cmp;
    if (this.weight() > o.weight()) {
      cmp = 1;
    } else if (this.weight() < o.weight()) {
      cmp = -1;
    } else {
      cmp = 0;
    }
    return cmp;
  }
}
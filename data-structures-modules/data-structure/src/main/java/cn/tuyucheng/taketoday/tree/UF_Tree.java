package cn.tuyucheng.taketoday.tree;

public class UF_Tree {
  private int[] eleAndGroup;
  private int[] size;
  private int count;

  public UF_Tree(int count) {
    this.count = count;
    this.eleAndGroup = new int[count];
    for (int i = 0; i < eleAndGroup.length; i++) {
      eleAndGroup[i] = i;
    }
    this.size = new int[count];
    for (int i = 0; i < this.size.length; i++) {
      size[i] = 1;
    }
  }

  public int count() {
    return this.count;
  }

  public int find(int p) {
    while (true) {
      if (p == eleAndGroup[p]) {
        return p;
      }
      p = eleAndGroup[p];
    }
  }

  public boolean connected(int p, int q) {
    return find(p) == find(q);
  }

  public void union(int p, int q) {
    if (connected(p, q)) {
      return;
    }
    int x = size[p];
    int y = size[q];
    if (x < y) {
      eleAndGroup[p] = find(q);
      size[y] += size[x];
    } else {
      eleAndGroup[q] = find(p);
      size[x] += size[y];
    }
    count--;
  }
}
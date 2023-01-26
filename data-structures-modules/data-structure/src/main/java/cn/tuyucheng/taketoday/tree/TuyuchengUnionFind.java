package cn.tuyucheng.taketoday.tree;

public class TuyuchengUnionFind {
  private int[] elementsAndGroup;
  private int[] size;
  private int count;

  public TuyuchengUnionFind(int count) {
    this.count = count;
    this.elementsAndGroup = new int[count];
    for (int i = 0; i < elementsAndGroup.length; i++) {
      elementsAndGroup[i] = i;
    }
    this.size = new int[elementsAndGroup.length];
    for (int i = 0; i < size.length; i++) {
      size[i] = 1;
    }
  }

  public int groupCount() {
    return count;
  }

  public int find(int p) {
    while (true){
      if(p == elementsAndGroup[p]){
        return p;
      }
      p = elementsAndGroup[p];
    }
  }

  public boolean isConnected(int p, int q) {
    return find(p) == find(q);
  }

  public void union(int p, int q) {
    if (isConnected(p, q)) {
      return;
    }
    int pGroup = find(p);
    int qGroup = find(q);
    for (int i = 0; i < elementsAndGroup.length; i++) {
      if(elementsAndGroup[i] == pGroup){
        elementsAndGroup[i] = qGroup;
      }
    }
    count--;
  }
}
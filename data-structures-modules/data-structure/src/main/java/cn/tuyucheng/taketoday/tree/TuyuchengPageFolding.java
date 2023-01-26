package cn.tuyucheng.taketoday.tree;

public class TuyuchengPageFolding {

  public static void main(String[] args) {
    Node<String> tree = createTree(3);
  }

  private static Node<String> createTree(int i) {
    
    return null;
  }

  class Node<T>{
    private T value;
    private Node left;
    private Node right;

    Node(T value, Node left, Node right) {
      this.value = value;
      this.left = left;
      this.right = right;
    }
  }
}
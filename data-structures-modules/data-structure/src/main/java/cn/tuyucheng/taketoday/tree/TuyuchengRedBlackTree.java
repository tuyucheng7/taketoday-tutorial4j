package cn.tuyucheng.taketoday.tree;

public class TuyuchengRedBlackTree<Key extends Comparable<Key>, Value> {
  public static final boolean RED = true;
  public static final boolean BLACK = false;
  private Node root;
  private int size;

  public int getSize() {
    return size;
  }

  public boolean isRed(Node node) {
    if (node == null) {
      return false;
    }
    return node.color == RED;
  }

  public void put(Key key, Value value) {
    root = put(root, key, value);
    root.color = RED;
  }

  public Node put(Node node, Key key, Value value) {
    if (node == null) {
      size++;
      return new Node(key, value, null, null, RED);
    }
    int compare = key.compareTo(node.key);
    if (compare < 0) {
      node.left = put(node.left, key, value);
    } else if (compare > 0) {
      node.right = put(node.right, key, value);
    } else {
      node.value = value;
    }
    if (isRed(node.right) && !isRed(node.left)) {
      node = leftRotate(node);
    }
    if (isRed(node.left) && isRed(node.left.left)) {
      node = rightRotate(node);
    }
    if (isRed(node.left) && isRed(node.right)) {
      filpColors(node);
    }
    return node;
  }

  public Value get(Key key) {
    return get(root, key);
  }

  public Value get(Node node, Key key) {
    if (node == null) {
      return null;
    }
    int compare = key.compareTo(node.key);
    if (compare < 0) {
      return get(node.left, key);
    } else if (compare > 0) {
      return get(node.right, key);
    } else {
      return node.value;
    }
  }

  private void filpColors(Node node) {
    node.color = RED;
    node.left.color = BLACK;
    node.right.color = BLACK;
  }

  private Node rightRotate(Node node) {
    Node left = node.left;
    left.right = node;
    node.left = left.right;
    left.color = node.color;
    node.color = RED;
    return left;
  }

  private Node leftRotate(Node node) {
    Node right = node.right;
    node.right = right.left;
    right.left = node;
    right.color = node.color;
    node.color = RED;
    return right;
  }

  class Node {
    private Key key;
    private Value value;
    private Node left;
    private Node right;
    private boolean color;

    Node(Key key, Value value, Node left, Node right, boolean color) {
      this.key = key;
      this.value = value;
      this.left = left;
      this.right = right;
      this.color = color;
    }
  }
}
package cn.tuyucheng.taketoday.tree;

import cn.tuyucheng.taketoday.queue.TuyuchengQueue;

public class TuyuchengBinaryTree<Key extends Comparable<Key>, Value> {
  private Node root;
  private int size;

  public TuyuchengBinaryTree() {
    root = null;
    size = 0;
  }

  public void put(Key key, Value value) {
    root = put(root, key, value);
  }

  public int getSize() {
    return size;
  }

  public Node put(Node node, Key key, Value value) {
    if (node == null) {
      size++;
      return new Node(key, value, null, null);
    }
    int compare = key.compareTo(node.key);
    if (compare < 0) {
      node.left = put(node.left, key, value);
    } else if (compare > 0) {
      node.right = put(node.right, key, value);
    } else
      node.value = value;
    return node;
  }

  public Value getKey(Key key) {
    return getKey(key, root);
  }

  public Value getKey(Key key, Node node) {
    if (root == null) {
      return null;
    }
    int compare = key.compareTo(node.key);
    if (compare < 0) {
      return getKey(key, node.left);
    } else if (compare > 0) {
      return getKey(key, node.right);
    } else {
      return node.value;
    }
  }

  public void delete(Key key) {
    delete(root, key);
  }

  public Node delete(Node node, Key key) {
    if (node == null) {
      return null;
    }
    int compare = key.compareTo(node.key);
    if (compare < 0) {
      node.left = delete(node.left, key);
    } else if (compare > 0) {
      node.right = delete(node.right, key);
    } else {
      size--;
      if (node.left == null) {
        return node.right;
      }
      if (node.right == null) {
        return node.left;
      }
      Node right = node.right;
      while (right.left != null) {
        right = right.left;
      }
      Node x = node.right;
      while (x.left != null) {
        if (x.left.left == null) {
          x.left = null;
        } else {
          x = x.left;
        }
      }
      right.left = node.left;
      right.right = node.right;
      node = right;
    }
    return node;
  }

  public Key min() {
    return min(root);
  }

  public Key min(Node node) {
    if (node.left == null) {
      return node.key;
    }
    return min(node.left);
  }

  public Key max() {
    return max(root);
  }

  public Key max(Node node) {
    if (node.right == null) {
      return node.key;
    }
    return max(node.right);
  }

  public TuyuchengQueue<Key> preOrderTraversal() {
    TuyuchengQueue<Key> keys = new TuyuchengQueue<>();
    preOrderTraversal(root, keys);
    return keys;
  }

  public void preOrderTraversal(Node node, TuyuchengQueue<Key> keys) {
    if (node == null) {
      return;
    }
    keys.enQueue(node.key);
    if (node.left != null) {
      preOrderTraversal(node.left, keys);
    }
    if (node.right != null) {
      preOrderTraversal(node.right, keys);
    }
  }

  public TuyuchengQueue<Key> middleOrderTraversal() {
    TuyuchengQueue<Key> keys = new TuyuchengQueue<>();
    middleOrderTraversal(root, keys);
    return keys;
  }

  public void middleOrderTraversal(Node node, TuyuchengQueue<Key> keys) {
    if (node == null) {
      return;
    }
    if (node.left != null) {
      middleOrderTraversal(node.left, keys);
    }
    keys.enQueue(node.key);
    if (node.right != null) {
      middleOrderTraversal(node.right, keys);
    }
  }

  public TuyuchengQueue<Key> postOrderTraversal() {
    TuyuchengQueue<Key> keys = new TuyuchengQueue<>();
    postOrderTraversal(root, keys);
    return keys;
  }

  public void postOrderTraversal(Node node, TuyuchengQueue<Key> keys) {
    if (node == null) {
      return;
    }
    if (node.left != null) {
      postOrderTraversal(node.left, keys);
    }
    if (node.right != null) {
      postOrderTraversal(node.right, keys);
    }
    keys.enQueue(node.key);
  }

  public TuyuchengQueue<Key> levelOrderTraversal() {
    TuyuchengQueue<Key> keys = new TuyuchengQueue<>();
    TuyuchengQueue<Node> nodes = new TuyuchengQueue<>();
    nodes.enQueue(root);
    while (!nodes.isEmpty()) {
      Node node = nodes.deQueue();
      keys.enQueue(node.key);
      if (node.left != null) {
        nodes.enQueue(node.left);
      }
      if (node.right != null) {
        nodes.enQueue(node.right);
      }
    }
    return keys;
  }

  public int maxDepth() {
    return maxDepth(root);
  }

  public int maxDepth(Node node) {
    if (node == null) {
      return 0;
    }
    int max;
    int leftMax = 0;
    int rightMax = 0;
    if (node.left != null) {
      leftMax = maxDepth(node.left);
    }
    if (node.right != null) {
      rightMax = maxDepth(node.right);
    }
    max = leftMax > rightMax ? leftMax + 1 : rightMax + 1;
    return max;
  }

  class Node {
    private Key key;
    private Value value;
    private Node left;
    private Node right;

    Node(Key key, Value value, Node left, Node right) {
      this.key = key;
      this.value = value;
      this.left = left;
      this.right = right;
    }
  }
}
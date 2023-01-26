package cn.tuyucheng.taketoday.tree;

import cn.tuyucheng.taketoday.queue.TuyuchengQueue;

public class PageFoldingTest {
  public static void main(String[] args) {
    Node<String> tree = createTree(3);
    printTree(tree);
  }

  // 通过模拟对折纸,产生树
  public static Node<String> createTree(int n) {
    Node<String> root = null;
    for (int i = 0; i < n; i++) {
      // 1.当前是第一次对折
      if (i == 0) {
        root = new Node<>("down", null, null);
        continue;
      }
      // 2.当前不是第一次对折
      // 定义一个辅助队列,通过层序遍历的思想,找到叶子结点,给叶子结点添加子结点
      TuyuchengQueue<Node> queue = new TuyuchengQueue<>();
      queue.enQueue(root);
      // 循环遍历队列
      while (!queue.isEmpty()) {
        // 从队列中弹出一个结点
        Node<String> node = queue.deQueue();
        // 如果有左子结点,则把左子结点放入到队列中
        if (node.left != null) {
          queue.enQueue(node.left);
        }
        // 如果有右子结点,则把右子结点放入到队列中
        if (node.right != null) {
          queue.enQueue(node.right);
        }
        // 如果同时没有左子结点和右子结点,只需要给该结点添加左子结点和右子结点
        if (node.left == null && node.right == null) {
          node.left = new Node<>("down", null, null);
          node.right = new Node<>("up", null, null);
        }
      }
    }
    return root;
  }

  public static void printTree(Node tree) {
    if (tree == null) {
      return;
    }
    if (tree.left != null) {
      printTree(tree.left);
    }
    System.out.print(tree.item + " ");
    if (tree.right != null) {
      printTree(tree.right);
    }
  }


  private static class Node<T> {
    public T item;
    public Node left;
    public Node right;

    public Node(T item, Node left, Node right) {
      this.item = item;
      this.left = left;
      this.right = right;
    }
  }
}
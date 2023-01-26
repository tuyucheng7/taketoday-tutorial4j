## 1. 问题描述

给定一棵二叉树，执行后序遍历。

我们在以下文章中讨论了后序遍历的方法。

- [树的遍历(中序、前序和后序)](TreeTraversals.md)
- [迭代后序遍历(使用两个栈)](Iterative_PostOrderTwoStack.md)
- [迭代后序遍历(使用一个栈)](Iterative_PostOrderOneStack.md)

在该方法中，讨论基于DFS的解决方案。我们在HashSet中添加访问过的节点。

## 2. 算法实现

```java
public class PostOrderTraversal {
  Node root;

  public PostOrderTraversal(Node root) {
    this.root = root;
  }

  public void postOrderUsintHash(Node root) {
    Node temp = root;
    HashSet<Node> visited = new HashSet<>();
    while (temp != null && !visited.contains(temp)) {
      // 访问左子树
      if (temp.left != null && !visited.contains(temp.left))
        temp = temp.left;
        // 访问右子树
      else if (temp.right != null && !visited.contains(temp.right))
        temp = temp.right;
        // 打印父节点
      else {
        System.out.print(temp.key + " ");
        visited.add(temp);
        temp = root;
      }
    }
  }
}
```

替代解决方案：

我们可以在每个节点上保留visited标志，而不使用单独的HashSet。

```java
class GFG {
  static class Node {
    int data;
    Node left, right;
    boolean visited;
  }

  static void postorder(Node head) {
    Node temp = head;
    while (temp != null && temp.visited == false) {
      if (temp.left != null && temp.left.visited == false)
        temp = temp.left;
      else if (temp.right != null && temp.right.visited == false)
        temp = temp.right;
      else {
        System.out.printf("%d ", temp.data);
        temp.visited = true;
        temp = head;
      }
    }
  }

  static Node newNode(int data) {
    Node node = new Node();
    node.data = data;
    node.left = null;
    node.right = null;
    node.visited = false;
    return (node);
  }

  public static void main(String[] args) {
    Node root = newNode(8);
    root.left = newNode(3);
    root.right = newNode(10);
    root.left.left = newNode(1);
    root.left.right = newNode(6);
    root.left.right.left = newNode(4);
    root.left.right.right = newNode(7);
    root.right.right = newNode(14);
    root.right.right.left = newNode(13);
    postorder(root);
  }
}
```

上述解决方案的时间复杂度为O(n<sup>2</sup>)，在最坏的情况下，我们在访问每个节点后将指针移回头部。
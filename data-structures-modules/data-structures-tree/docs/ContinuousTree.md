## 1. 问题描述

如果在每个父节点到叶节点路径中，两个相邻的节点之间key的绝对差为1，则树是连续树。给定一棵二叉树，我们需要检查树是否连续。

示例：

```
输入:                3
                    / 
                   2   4
                  /    
                 1   3   5
输出: "Yes"
// 3->2->1 每两个相邻节点的绝对差为1
// 3->2->3 每两个相邻节点的绝对差为1
// 3->4->5 每两个相邻节点的绝对差为1

输入 :               7
                    / 
                   5   8
                  /    
                 6   4   10
输出: "No"
// 7->5->6 这里7和5的绝对差不是1。
// 7->5->4 这里7和5的绝对差不是1。
// 7->8->10 这里8和10的绝对差不是1。
```

## 2. 算法分析

该问题需要我们遍历树。要检查的重要事项是确保处理所有极端情况。极端情况包括空树、单节点树、只有左子节点的节点和只有右子节点的节点。

在树遍历中，我们递归地检查左右子树是否连续。我们还要检查当前节点的key和它的子节点的key之间的差是否为1。

## 3. 算法实现

以下是具体的实现：

```java
public class ContinuousTree {
  Node root;

  public ContinuousTree(Node root) {
    this.root = root;
  }

  public boolean isContinuous(Node root) {
    // 如果根节点为null，返回true
    if (root == null)
      return true;
    // 如果只有一个根节点，返回true
    if (root.left == null && root.right == null)
      return true;
    // 如果左子树为null，则递归检查右子树
    if (root.left == null)
      return (Math.abs(root.key - root.right.key) == 1) && isContinuous(root.right);
    // 如果右子树为null，则递归检查左子树
    if (root.right == null)
      return (Math.abs(root.key - root.left.key) == 1) && isContinuous(root.left);
    // 如果左右子树都不为null，则全部检查
    return (Math.abs(root.key - root.left.key) == 1)
        && (Math.abs(root.key - root.right.key) == 1)
        && isContinuous(root.left)
        && isContinuous(root.right);
  }
}

class ContinuousTreeUnitTest {

  @Test
  void givenContinuousTree_whenCheckIfContinuous_thenReturnTrue() {
    ContinuousTree tree = createContinuousTree();
    assertTrue(tree.isContinuous(tree.root));
  }

  @Test
  void givenNotContinuousTree_whenCheckIfContinuous_thenReturnFalse() {
    ContinuousTree tree = createNonContinuousTree();
    assertFalse(tree.isContinuous(tree.root));
  }

  private ContinuousTree createContinuousTree() {
    Node root = new Node(3);
    root.left = new Node(2);
    root.right = new Node(4);
    root.left.left = new Node(1);
    root.left.right = new Node(3);
    root.right.right = new Node(5);
    return new ContinuousTree(root);
  }

  private ContinuousTree createNonContinuousTree() {
    Node root = new Node(3);
    root.left = new Node(2);
    root.right = new Node(4);
    root.left.left = new Node(1);
    root.left.right = new Node(3);
    root.right.right = new Node(6);
    return new ContinuousTree(root);
  }
}
```

## 4. 使用BFS

我们只需逐层遍历每个节点，并检查父节点和子节点之间的差值是否为1，如果对所有节点都为true，则返回true，否则返回false。

```java
public class ContinuousTree {
  Node root;

  public ContinuousTree(Node root) {
    this.root = root;
  }

  public boolean isContinuousBFS(Node root) {
    if (root == null)
      return true;
    Queue<Node> nodes = new LinkedList<>();
    nodes.add(root);
    Node temp;
    while (!nodes.isEmpty()) {
      temp = nodes.peek();
      nodes.remove();
      if (temp.left != null) {
        if (Math.abs(temp.key - temp.left.key) != 1)
          return false;
        nodes.add(temp.left);
      }
      if (temp.right != null) {
        if (Math.abs(temp.key - temp.right.key) != 1)
          return false;
        nodes.add(temp.right);
      }
    }
    return true;
  }
}
```

时间复杂度：O(n)
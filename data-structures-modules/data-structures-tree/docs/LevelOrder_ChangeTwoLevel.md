## 1. 问题描述

给定一棵二叉树，以这样的方式打印层序遍历：前两层从左到右打印，接下来的两层从右到左打印，然后接下来的两层从左到右打印，以此类推。
所以，问题是在每两个层之后反转二叉树的层序遍历的方向。

例子：

```
输入: 
            1     
          /   
        2       3
      /       /  
     4    5    6    7
    /   /   /   /  
   8  9 3   1 4  2 7  2
     /     /     
    16    17  18   19
输出:
1
2 3
7 6 5 4
2 7 2 4 1 3 9 8
16 17 18 19

解释：在上面的例子中，前两层从左到右打印，接下来的两层从右到左打印，最后一层从左到右打印。
```

## 2. 算法分析

我们在这里使用队列和栈。队列用于执行正常的层序遍历。堆栈用于在每两层之后反转遍历方向。

在进行正常层序遍历时，前两个层的节点在它们从队列中弹出时打印。对于接下来的两个层，我们没有打印节点，而是将它们压入栈。
当当前层的所有节点都弹出时，我们打印栈中的节点。这样，我们利用栈以从右到左的顺序打印节点。
现在对于接下来的两个层，我们再次对从左到右的打印节点进行正常层序遍历。然后对于接下来的两个节点，我们利用栈来实现从右到左的顺序。

这样，我们将通过使用队列和栈来实现所需的修改层序遍历的方向。

```java
public class LevelOrderChangeTwo {
  Node root;

  public LevelOrderChangeTwo(Node root) {
    this.root = root;
  }

  public void modifiedLevelOrderUsingStackAndQueue(Node root) {
    if (root == null)
      return;
    if (root.left == null && root.right == null) {
      System.out.print(root.key + " ");
      return;
    }
    // 维护一个栈，用于从右到左打印
    Stack<Node> stack = new Stack<>();
    // 维护一个队列，用于从左到右打印
    Queue<Node> queue = new LinkedList<>();
    Node temp;
    // 当count为2时用于改变层序遍历的打印方向
    int count = 0;
    // 存储当前层的节点数
    int nodeCounts;
    queue.add(root);
    // 用于改变层序遍历的打印方向
    boolean rightToLeft = false;
    while (!queue.isEmpty()) {
      count++;
      nodeCounts = queue.size();
      // 执行正常的从左到右打印
      for (int i = 0; i < nodeCounts; i++) {
        temp = queue.peek();
        queue.remove();
        if (!rightToLeft)
          System.out.print(temp.key + " ");
        else
          // 如果该else语句执行，则当前已经打印了两行，现在队列中包含的是第三层的节点，因此执行四次for循环将第三层的四个节点压入栈中，
          stack.push(temp);
        // 继续将第四层的节点添加到队列中，当进入下一次while循环时，rightToLeft仍然为true，因此下一次while循环会继续将第四层的节点压入栈中
        if (temp.left != null)
          queue.add(temp.left);
        if (temp.right != null)
          queue.add(temp.right);
      }
      // 当rightToLeft为true时，从左到右打印
      if (rightToLeft) {
        while (!stack.isEmpty()) {
          temp = stack.peek();
          stack.pop();
          System.out.print(temp.key + " ");
        }
      }
      // 每打印两层节点，清空count标志，更改rightToLeft变量
      if (count == 2) {
        rightToLeft = !rightToLeft;
        count = 0;
      }
      System.out.println();
    }
  }
}
```

时间复杂度：在执行层序遍历时，每个节点最多遍历两次，因此时间复杂度为O(n)。

## 3. 方法二

我们在这里使用队列和栈，但方式不同。使用宏#定义更改方向(Dir)((Dir) =1 – (Dir))。
在下面的实现中，指示队列或栈中推操作的顺序。这样，我们将通过使用队列和栈来实现所需的修改层序遍历。

```java
public class LevelOrderChangeTwo {
  Node root;

  static final int LEFT = 0;
  static final int RIGHT = 1;

  public LevelOrderChangeTwo(Node root) {
    this.root = root;
  }

  private static int changeDirection(int Dir) {
    Dir = 1 - Dir;
    return Dir;
  }

  public void modifiedLevelOrderOtherMethod(Node root) {
    if (root == null)
      return;
    int dir = LEFT;
    Node temp;
    Queue<Node> Q = new LinkedList<>();
    Stack<Node> S = new Stack<>();
    S.add(root);
    while (!Q.isEmpty() || !S.isEmpty()) {
      while (!S.isEmpty()) {
        temp = S.peek();
        S.pop();
        System.out.print(temp.key + " ");
        if (dir == LEFT) {
          if (temp.left != null)
            Q.add(temp.left);
          if (temp.right != null)
            Q.add(temp.right);
        } else {
          if (temp.right != null)
            Q.add(temp.right);
          if (temp.left != null)
            Q.add(temp.left);
        }
      }
      System.out.println();
      while (!Q.isEmpty()) {
        temp = Q.peek();
        Q.remove();
        System.out.print(temp.key + " ");
        if (dir == LEFT) {
          if (temp.left != null)
            S.add(temp.left);
          if (temp.right != null)
            S.add(temp.right);
        } else {
          if (temp.right != null)
            S.add(temp.right);
          if (temp.left != null)
            S.add(temp.left);
        }
      }
      System.out.println();

      dir = changeDirection(dir);
    }
  }
}
```

时间复杂度：每个节点也要遍历两次。时间复杂度仍然是O(n)。
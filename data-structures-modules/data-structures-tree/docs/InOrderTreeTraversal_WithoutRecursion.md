## 1. 算法分析

使用栈是无需递归即可遍历树的替代方法。下面是一个使用栈遍历二叉树的算法。

```
1. 创建一个空栈S。
2. 将根节点初始化为current节点。
3. 将当前节点push到S中并设置current = current.left直到current为null。
4. if current为null并且栈S不为空，则
   a. 从栈中弹出顶部的节点。
   b. 打印弹出的节点数据，设置current = popped_item.right。
   c. 转至步骤3。
5. 如果current为null并且栈S为空，遍历结束。
```

让我们以下面的树为例：

```
          1
        /   
       2     3
      /  
     4    5

Step 1 创建一个空栈：S = null

Step 2 将current设置为根节点：current -> 1

Step 3 push current节点并设置current = current.left直到current为null
     current -> 1
     push 1: Stack S -> 1
     current -> 2
     push 2: Stack S -> 2, 1
     current -> 4
     push 4: Stack S -> 4, 2, 1
     current = NULL

Step 4 从S弹出节点
     a) Pop 4: Stack S -> 2, 1
     b) 打印"4"
     c) current = null /4的右子节点/ 并转到第3步
由于current为null因此step3不做任何事情。

Step 4 再次从S弹出节点
     a) Pop 2: Stack S -> 1
     b) 打印"2"
     c) current -> 5/2的右子节点/ 并转到第3步

Step 3 push 5到栈中并使current为null
     Stack S -> 5, 1
     current = null

Step 4 再次从S弹出节点
     a) Pop 5: Stack S -> 1
     b) 打印"5"
     c) current = null /5的右子节点/ 并转到第3步
由于current为null因此step3不做任何事情。

Step 4 再次从S弹出节点
     a) Pop 1: Stack S -> null
     b) 打印"1"
     c) current -> 3 /1的右子节点/  

Step 3 push 3到栈中并使current为null
     Stack S -> 3
     current = null

Step 4 再次从S弹出节点
     a) Pop 3: Stack S -> null
     b) 打印"3"
     c) current = null /3的右子节点/  

由于栈S为空且current为null，因此遍历已完成。
```

## 2. 算法实现

```java
public class InOrderTraversal {
  Node root;

  void inOrderUsingStack(Node root) {
    if (root == null)
      return;
    Stack<Node> nodes = new Stack<>();
    Node current = root;
    // 遍历树
    while (current != null || nodes.size() > 0) {
      // 一直往current的左子树遍历
      while (current != null) {
        // 添加每一层节点
        nodes.add(current);
        current = current.left;
      }
      current = nodes.pop();
      System.out.print(current.key + " ");
      // 我们已经访问了左子树及父节点。现在，应该访问右子树
      current = current.right;
    }
  }
}
```

时间复杂度：O(n)
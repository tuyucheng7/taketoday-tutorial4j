## 1. 算法分析

我们在前一篇文章中讨论了使用两个栈的简单迭代后序遍历。在这篇文章中，将讨论一种只使用一个栈的方法。

其思想是使用left指针向下移动到最左边的节点。向下移动时，将根和根的右子节点push到栈中。一旦我们到达最左边的节点，如果它没有右子节点，就打印它。
如果它有一个右子节点，则更改根，以便先处理右子节点。

下面是详细的算法。

```
1.1 创建一个空栈stack
2.1 当root不为null时执行以下while循环
    a) 将root的右子节点和root顺序push到stack中
    b) 设置root为root的左子节点，即root = root.left
2.2 从栈中弹出一个节点并将其设置为root。
    a) If弹出的节点有右子节点并且右子节点位于栈顶，然后从栈中移除右子节点，将root push回并将root设置为root的右子节点。
    b) Else，打印root的数据并将root设置为null。
2.3 当栈不为空时，重复步骤2.1和2.2。
```

让我们考虑以下树：

<img src="../assets/Iterative_PostOrderTwoStack.png">

以下是使用一个栈打印上述树的后序遍历的步骤：

```
1. 存在1的右子节点。
   Push 3到栈中，Push 1到栈中，移动到左子节点。
        Stack: 3, 1

2. 存在2的右子节点。
   Push 5到栈中，Push 2到栈中，移动到左子节点。
        Stack: 3, 1, 5, 2

3. 不存在4的右子节点。
   Push 4到栈中. 移动到左子节点。
        Stack: 3, 1, 5, 2, 4

4. current节点为null. 
   从栈中弹出4，4的右子节点不存在。
   打印4，设置current节点为null。
        Stack: 3, 1, 5, 2

5. current节点为null. 
    从栈中弹出2，由于2的右子节点(5)等于栈顶元素，
    从栈中弹出5，现在将2 push进栈。
    将current节点移动到2的右子节点，即5
        Stack: 3, 1, 2

6. 不存在5的右子节点，将5 push进栈，移动到左子节点。
        Stack: 3, 1, 2, 5

7. current节点为null，从栈中弹出5，5的右子节点不存在。 
   打印5，设置current节点为null。
        Stack: 3, 1, 2

8. current节点为null，从栈中弹出2。
   2的右子节点不等于栈顶元素。
   打印2，设置current节点为null。
        Stack: 3, 1

9. current节点为null，从栈中弹出1。
   由于1的右子节点(3)等于栈顶元素，因此从栈中弹出3。
   现在将1 push进栈，将current节点移动到1的右子节点，即3。
        Stack: 1

10. 重复上述步骤并打印6、7和3。
    弹出1和打印1。
```

## 2. 算法实现

```java
public class PostOrderTraversal {
  Node root;

  public PostOrderTraversal(Node root) {
    this.root = root;
  }

  public void postOrderIterativeUsingOneStack(Node root) {
    if (root == null)
      return;
    Stack<Node> stack = new Stack<>();
    stack.push(root);
    Node previous = null;
    while (!stack.isEmpty()) {
      Node current = stack.peek();
      if (previous == null || previous.left == current || previous.right == current) {
        if (current.left != null)
          stack.push(current.left);
        else if (current.right != null) {
          stack.push(current.right);
        } else {
          stack.pop();
          System.out.print(current.key + " ");
        }
      } else if (current.left == previous) {
        if (current.right != null)
          stack.push(current.right);
        else {
          stack.pop();
          System.out.print(current.key + " ");
        }
      } else if (current.right == previous) {
        stack.pop();
        System.out.print(current.key + " ");
      }
      previous = current;
    }
  }
}
```

## 3. 方法二

向左遍历时直接push根节点两次。如果你发现stack pop()与root相同则弹出，然后转到root.right。否则打印root。


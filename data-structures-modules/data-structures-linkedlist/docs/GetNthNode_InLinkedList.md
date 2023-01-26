## 1. 问题描述

编写一个GetNth()方法，该方法接收一个整数索引(从0开始)作为参数，返回存储在该索引位置的节点中的数据值。

示例：

```
输入: 1->10->30->14,  index = 2
输出: 30
解释: 2索引处的值为30
```

## 2. 算法实现

```
1. 初始化count = 0，current = head
2. 循环遍历链表
   a. 如果count等于传递的索引，则返回当前节点的值
   b. 自增count
   c. 移动current指针。
```

以下为具体实现：

```java
public class LinkedList {

  // 将索引作为参数并返回索引处的节点数据
  public int GetNth(int index) {
    // 初始化辅助指针current和count
    Node current = head;
    int count = 0;
    while (current != null) {
      if (count == index)
        return current.data;
      count++;
      current = current.next;
    }
    // 如果我们走到这行，调用方请求一个不存在的索引，所以我们断言失败。
    assert (false);
    return 0;
  }
}
```

时间复杂度：O(n)

## 3. 递归实现

```
getNthUsingRecursive(node head, int n)

1. 初始化count
2. 如果count == n
     返回node.data
3. 返回getNth(head.next, int n - 1)
```

算法实现：

```java
public class LinkedList {

  public int getNthUsingRecursive(Node head, int n) {
    int count = 0;
    if (head == null)
      return -1;
    // 基准条件，当递归次数达到n次时，返回当前节点的数据
    if (count == n)
      return head.data;
      // 递归调用，将head向前移动，并令n-1
    else return getNthUsingRecursive(head.next, n - 1);
  }
}
```

时间复杂度：O(n)
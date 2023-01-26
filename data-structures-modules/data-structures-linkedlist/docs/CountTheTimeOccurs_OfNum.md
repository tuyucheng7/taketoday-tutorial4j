## 1. 问题描述

给定一个单链表和一个key，计算给定key在链表中出现的次数。例如，如果给定的链表是1->2->1->2->1->3->1，并且给定的key是1，那么输出应该是4。

## 2. 无递归算法

```
1. 初始count = 0。
2. 循环遍历链表的每个元素：
   a) 如果元素数据等于传递的数字，则增加count。
3. 返回count。
```

以下为具体实现：

```java
public class LinkedList {

  public int countOccursTime(int key) {
    Node current = head;
    int count = 0;
    while (current != null) {
      if (key == current.data)
        // 如果当前遍历的节点值等于key，自增count
        count++;
      current = current.next;
    }
    return count;
  }
}
```

时间复杂度：O(n)

辅助空间：O(1)

## 3. 递归实现

```
算法：int countOccursTimeUsingRecursive(Node head,int key)

if head is null
return frequency
if(head.data == key)
  frequency++
countOccursTimeUsingRecursive(head.next, key)
```

以下为具体实现：

```java
public class LinkedList {
  static int frequency = 0;

  public static int countOccursTimeUsingRecursive(Node head, int key) {
    // 递归基准条件
    if (head == null)
      return frequency;
    if (head.data == key)
      frequency++;
    return countOccursTimeUsingRecursive(head.next, key);
  }
}
```

上面的实现中我们使用到了一个frequency的静态类变量，我们也可以使用局部变量，如下所示：

```java
public class LinkedList {

  public int countOccursTimeUsingRecursiveWithGloabl(Node head, int key) {
    if (head == null)
      return 0;
    if (head.data == key)
      return 1 + countOccursTimeUsingRecursiveWithGloabl(head.next, key);
    return countOccursTimeUsingRecursiveWithGloabl(head.next, key);
  }
}
```

时间复杂度：O(n)
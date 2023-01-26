## 1. 问题描述

给定一个循环链表，计算其中的节点数。例如，对于以下的链表，输出应该为5。

<img src="../assets/CircularLinkedList_Introduction-1.png">

## 2. 算法实现

我们可以参考循环链表的遍历。在遍历时，我们同时记录节点数count。

```java
public class CountNodes {

  public static int countNodes(Node head) {
    Node temp = head;
    int count = 0;
    if (head != null) {
      do {
        temp = temp.next;
        count++;
      } while (temp != head);
    }
    return count;
  }
}
```
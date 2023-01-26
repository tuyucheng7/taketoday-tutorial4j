## 1. 问题描述

给定一个双链表，任务是找到该双链表的大小。例如，下面链表的大小为4。

<img src="../assets/FindSizeOf_DoublyLinkedList.png">

双链表是一种链式数据结构，由一组称为节点的顺序链接记录组成。每个节点包含两个称为链接的字段，它们是对节点序列中上一个节点和下一个节点的引用。

双向链表的遍历可以是任意方向。事实上，如果需要，遍历的方向可以多次更改。

## 2. 算法分析

1. 初始化size为0。
2. 初始化节点指针，temp=head。
3. 执行以下操作，知道temp不为null。
    1. temp = temp.next
    2. size++
4. 返回size。

## 3. 算法实现

```java
public class DoublyLinkedList {
  Node head;

  public int findSize(Node head) {
    // 初始化size
    int size = 0;
    Node temp = head;
    // 遍历链表，直到temp不为null
    while (temp != null) {
      // 自增size，并移动temp指针
      size++;
      temp = temp.next;
    }
    return size;
  }
}
```
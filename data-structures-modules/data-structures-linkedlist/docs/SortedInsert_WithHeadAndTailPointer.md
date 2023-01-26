## 1. 问题描述

双链表是由一组称为节点的顺序链接记录组成的链表。每个节点包含两个字段，分别引用节点序列中的前一个节点和下一个节点。

任务是通过插入节点创建一个双链表，以便链表在从左到右打印时保持升序的顺序。此外，我们需要维护两个指针，head(指向第一个节点)和tail(指向最后一个节点)。

示例：

```
输入: 40 50 10 45 90 100 95
输出: 10 40 45 50 90 95 100

输入: 30 10 50 43 56 12
输出: 10 12 30 43 50 56
```

## 2. 算法分析

该算法可以通过以下方式完成：

1. 如果链表为空，则使left和right指针都指向要插入的节点，并使其previous和next字段都指向null。
2. 如果要插入的节点的值小于链表的第一个节点的值，则从第一个节点的previous字段连接该节点。
3. 如果要插入的节点的值大于链表最后一个节点的值，则从最后一个节点的next字段连接该节点。
4. 如果要插入的节点的值介于第一个节点和最后一个节点的值之间，则检查适当的位置并建立连接。

## 3. 算法实现

```java
public class InsertSortedDoublyLinkedList {
  DoublyLinkedList doublyLinkedList;
  Node head;
  Node tail;

  public InsertSortedDoublyLinkedList(DoublyLinkedList doublyLinkedList) {
    this.doublyLinkedList = doublyLinkedList;
    head = doublyLinkedList.head;
  }

  public void nodeInsertTail(int key) {
    // 创建新节点
    Node newNode = new Node(key);
    newNode.next = null;
    // 如果新插入的节点为第一个节点，则将新节点作为head和tail节点
    if (head == null) {
      newNode.previous = null;
      head = newNode;
      tail = newNode;
      return;
    }
    // 如果新插入的节点为第一个节点，则将新节点与原始链表的头节点连接，并将新节点作为新的头节点
    if (newNode.data < head.data) {
      newNode.previous = null;
      head.previous = newNode;
      newNode.next = head;
      head = newNode;
      return;
    }
    // 如果新插入的节点为最后一个节点，则将新节点与原始链表的tail节点连接，并将新节点作为新的尾节点
    if (newNode.data > tail.data) {
      tail.next = newNode;
      newNode.previous = tail;
      tail = newNode;
      return;
    }
    // 如果都不是，则新节点位于头尾节点之间，遍历链表，找到新节点的下一个节点
    Node temp = head.next;
    while (temp.data < newNode.data)
      temp = temp.next;
    // 建立新节点与其上一个节点与下一个节点的连接
    temp.previous.next = newNode;
    newNode.previous = temp.previous;
    temp.previous = newNode;
    newNode.next = temp;
  }
}
```
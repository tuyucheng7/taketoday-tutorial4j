## 1. 问题描述

给定一个单链表，我们需要将其转换为循环链表。例如，给定以下具有四个节点的单链表，我们希望将此单链表转换为循环链表。

<img src="../assets/ConvertSinglyLinkedList_ToCircularLinkedList-1.png">

将上述单链表转换为循环链表：

<img src="../assets/ConvertSinglyLinkedList_ToCircularLinkedList-2.png">

## 2. 算法实现

其思想是遍历单链表并检查该节点是否是最后一个节点(即是否指向null)。如果节点是最后一个节点，则将其指向起始节点(即头节点)。

以下是具体实现：

```java
public class ConvertSinglyLinkedList {

  public static Node convert(Node head) {
    // 声明一个节点变量start并将头节点分配给start节点
    Node start = head;
    // 循环直到head.next不为null，然后移动head = head.next。
    while (head.next != null)
      head = head.next;
    // 如果head.next指向null，则将head.next指向start
    head.next = start;
    return start;
  }

  public static void displayList(Node node) {
    Node start = node;
    while (node.next != start) {
      System.out.print(" " + node.data);
      node = node.next;
    }
    // 显示循环链表的最后一个节点
    System.out.print(" " + node.data);
  }

  static Node push(Node head, int data) {
    Node newNode = new Node();
    newNode.data = data;
    newNode.next = (head);
    (head) = newNode;
    return head;
  }
}
```
## 1. 问题描述

设计具有以下操作的栈。

1. push(Stack s, x)：添加一个元素x到栈s中。
2. pop(Stack s)：从栈s顶部移除元素。
3. merge(Stack s1, Stack s2)：将s2的内容合并到s1中。

所有上述操作的时间复杂度应为O(1)。

## 2. 算法分析

如果我们使用栈的数组实现，那么合并不可能在O(1)时间内完成，因为我们必须执行以下步骤。

1. 删除旧的数组。
2. 为s1创建一个新数组，其大小等于s1旧数组的大小加上s2的大小。
3. 将s1和s2的旧内容到s1的新数组

上述操作需要O(n)个时间。

我们可以使用带有两个指针的链表，一个指向第一个节点的指针(当从头部开始添加和删除元素时也用作top)。
另一个指针为指向最后一个节点的指针，这样我们就可以在s1的末尾快速连接s2的链表。以下是所有操作。

1. push()：使用第一个指针在链表的开头添加新元素。
2. pop()：使用第一个指针从链表的开头删除元素。
3. merge()：将第二个链表的第一个指针作为第一个链表的第二个指针的下一个指针。

如果不允许使用额外的指针，我们可以这样做吗？

我们可以用循环链表来完成。这样做的目的是跟踪链表中的最后一个节点。最后一个节点的下一个节点指向栈的top。

1. push()：将新元素添加为最后一个节点的下一个节点。
2. pop()：删除最后一个节点的下一个节点。
3. merge()：将第二个链表的top(最后一个)连接到第一个链表的top(最后一个)。并将第二个链表的最后一个作为整个链表的最后一个。

## 3. 具体实现

```java
public class MergableStack {
  Node head;
  Node tail;

  MergableStack() {
    head = null;
    tail = null;
  }

  void push(int newData) {
    Node newNode = new Node(newData);
    if (head == null) {
      head = newNode;
      head.next = null;
      head.previous = null;
    } else {
      newNode.previous = tail;
      tail.next = newNode;
    }
    tail = newNode;
  }

  void pop() {
    if (head == null)
      System.out.println("Stack Underflow");
    if (head == tail) {
      head = null;
      tail = null;
    } else {
      Node n = tail;
      tail = tail.previous;
      n.previous = null;
      tail.next = null;
    }
  }

  void merge(MergableStack stack) {
    head.previous = stack.tail;
    stack.tail.next = head;
    head = stack.head;
    stack.head = null;
    stack.tail = null;
  }

  void display() {
    if (tail != null) {
      Node n = tail;
      while (n != null) {
        System.out.print(n.data + " ");
        n = n.previous;
      }
      System.out.println();
    } else
      System.out.println("Stack Underflow");
  }

  static class Node {
    Node previous;
    Node next;
    int data;

    public Node(int data) {
      this.data = data;
      previous = null;
      next = null;
    }
  }
}
```
## 1. 问题描述

如何实现一个以O(1)时间复杂度支持以下操作的栈？

1. push()将元素添加到栈顶部。
2. pop()从栈顶部删除元素。
3. findMiddle()，它将返回栈的中间元素。
4. deleteMiddle()，它将删除中间元素。

push和pop是标准的栈操作。

## 2. 算法分析

重要的问题是，使用链表还是数组来实现栈？

请注意，我们需要找到并删除中间元素。从中间删除一个元素对于数组来说不是O(1)。此外，我们可能需要在push元素时将mid(中间)指针向上移动，
在pop元素时将mid指针向下移动。在单链表中，不能双向移动mid指针。

因此，说到这里，就自然想到使用双向链表(DLL)。我们可以通过维护mid指针在O(1)时间内删除中间元素。
我们可以使用DDL的previous和next指针在两个方向上移动mid指针。

以下是push()、pop()、findMiddle()和deleteMiddle()操作的实现。
如果栈中有偶数个元素，findMiddle()返回第二个中间元素。例如，如果栈包含 {1, 2, 3, 4}，则findMiddle()将返回3。

## 3. 算法实现

```java
public class MiddleStack {
  Node head;
  Node previous;
  Node next;
  Node mid;
  int size;

  void push(int newData) {
    Node newNode = new Node(newData);
    if (size == 0) {
      newNode.next = null;
      newNode.previous = null;
      head = newNode;
      mid = newNode;
      size++;
      return;
    }
    head.next = newNode;
    newNode.previous = head;
    head = head.next;
    if (size % 2 != 0)
      mid = mid.next;
    size++;
  }

  int pop() {
    int data = -1;
    if (size == 0)
      System.out.println("stack is empty");
    if (size != 0) {
      if (size == 1) {
        head = null;
        mid = null;
      } else {
        data = head.data;
        head = head.previous;
        head.next = null;
        if (size % 2 == 0)
          mid = mid.previous;
      }
      size--;
    }
    return data;
  }

  int findMiddle() {
    if (size == 0) {
      System.out.println("stack is empty");
      return -1;
    }
    return mid.data;
  }

  void deleteMiddle() {
    if (size != 0) {
      if (size == 1) {
        head = null;
        mid = null;
      } else if (size == 2) {
        head = head.previous;
        mid = mid.previous;
        head.next = null;
      } else {
        mid.next.previous = mid.previous;
        mid.previous.next = mid.next;
        if (size % 2 == 0)
          mid = mid.previous;
        else
          mid = mid.next;
      }
      size--;
    }
  }

  static class Node {
    Node previous;
    Node next;
    int data;

    public Node(int data) {
      this.data = data;
    }
  }
}
```
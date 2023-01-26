## 1. 概述

在本文中，我们将在Java中实现两种链表反转算法。

## 2. 链表数据结构

链表是一种线性数据结构，其中每个元素中的指针确定顺序。链表的每个元素都包含一个存储列表数据的data字段和一个指向序列中下一个元素的pointer字段。此外，我们可以使用head指针指向链表的起始元素：

<img src="../assets/Reversing_LinkedList-1.png">

我们反转链表后，head会指向原链表的最后一个元素，每个元素的指针都会指向原链表的前一个元素：

<img src="../assets/Reversing_LinkedList-2.png">

在Java中，我们有一个LinkedList类来提供List和Deque接口的双向链表实现。但是，我们将在本文中使用通用的单链表数据结构。

让我们首先从一个ListNode类开始，以表示链表的一个元素：

```java
public class ListNode {
  private int data;
  private ListNode next;

  ListNode(int data) {
    this.data = data;
    this.next = null;
  }
  // standard getters and setters
}
```

ListNode类有两个字段：

+ 一个整数值，表示元素的数据
+ 指向下一个元素的指针/引用

链表可能包含多个ListNode对象。例如，我们可以使用循环构造上述示例链表：

```
private ListNode constructLinkedList() {
  ListNode head = null;
  ListNode tail = null;
  for (int i = 1; i <= 5; i++) {
    ListNode node = new ListNode(i);
    if (head == null) {
      head = node;
    } else {
      tail.setNext(node);
    }
    tail = node;
  }
  return head;
}
```

## 3. 迭代算法实现

让我们用Java实现迭代算法：

```
ListNode reverseList(ListNode head) {
  ListNode previous = null;
  ListNode current = head;
  while (current != null) {
    ListNode nextElement = current.getNext();
    current.setNext(previous);
    previous = current;
    current = nextElement;
  }
  return previous;
}
```

在这个迭代算法中，我们使用两个ListNode变量，previous和current来表示链表中的两个相邻元素。对于每次迭代，我们反转这两个元素，然后切换到接下来的两个元素。

最后，current指针将为空，previous指针将是旧链表的最后一个元素。所以previous也是反转后链表的新头指针，我们从方法中返回它就可以了。

我们可以通过一个简单的单元测试来验证这个迭代实现：

```
@Test
void givenLinkedList_whenIterativeReverse_thenOutputCorrectResult() {
  ListNode head = constructLinkedList();
  ListNode node = head;
  for (int i = 1; i <= 5; i++) {
    assertNotNull(node);
    assertEquals(i, node.getData());
    node = node.getNext();
  }
  LinkedListReversal reversal = new LinkedListReversal();
  node = reversal.reverseList(head);
  for (int i = 5; i >= 1; i--) {
    assertNotNull(node);
    assertEquals(i, node.getData());
    node = node.getNext();
  }
```

在这个单元测试中，我们首先构造一个包含五个节点的链表。此外，我们验证链表中的每个节点是否包含正确的数据值。
然后，我们调用reverseList()来反转链表。最后，我们检查反转后的链表以确保数据按预期反转。

## 4. 递归算法实现

现在，让我们用Java实现递归算法：

```
ListNode reverseListRecursive(ListNode head) {
  if (head == null) {
    return null;
  }
  if (head.getNext() == null) {
    return head;
  }
  ListNode node = reverseListRecursive(head.getNext());
  head.getNext().setNext(head);
  head.setNext(null);
  return node;
}
```

在reverseListRecursive()方法中，我们递归地访问链表中的每个元素，直到到达最后一个。最后一个元素将成为反转链表的新头部。
此外，我们将访问的元素附加到部分反向链表的末尾。

类似地，我们可以通过一个简单的单元测试来验证这个递归实现：

```
@Test
void givenLinkedList_whenRecursiveReverse_thenOutputCorrectResult() {
  ListNode head = constructLinkedList();
  ListNode node = head;
  for (int i = 1; i <= 5; i++) {
    assertNotNull(node);
    assertEquals(i, node.getData());
    node = node.getNext();
  }
  LinkedListReversal reversal = new LinkedListReversal();
  node = reversal.reverseListRecursive(head);
  for (int i = 5; i >= 1; i--) {
    assertNotNull(node);
    assertEquals(i, node.getData());
    node = node.getNext();
  }
}
```
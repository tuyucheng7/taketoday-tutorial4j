## 1. 概述

在本文中，我们将介绍Java中循环链表的实现。

## 2. 循环链表

循环链表是链表的一种变体，其中最后一个节点指向第一个节点，完成一个完整的节点循环。换句话说，链表的这种变体末尾没有空元素。

通过这一简单的更改，我们获得了一些好处：

+ 循环链表中的任何节点都可以作为起点
+ 因此，可以从任何节点开始遍历整个链表
+ 由于循环链表的最后一个节点有指向第一个节点的指针，因此很容易进行enqueue和dequeue操作

总之，这在队列数据结构的实现中非常有用。

就性能而言，它与其他链表实现相同，但有一点不同：从最后一个节点到头节点的遍历可以在恒定时间内完成。对于传统的链表，这是一个线性操作。

## 3. Java实现

让我们从创建一个辅助节点类开始，该类将存储int值和指向下一个节点的指针：

```java
static class Node {
  int value;
  Node nextNode;

  public Node(int value) {
    this.value = value;
  }
}
```

现在，让我们创建循环链表中的第一个和最后一个节点，通常称为头部和尾部：

```java
public class CircularLinkedList {
  private Node head = null;
  private Node tail = null;
  // ....
}
```

在下一小节中，我们将了解可以对循环链表执行的最常见操作。

### 3.1 插入元素

我们要介绍的第一个操作是插入新节点。插入新元素时，我们需要处理两种情况：

+ 头节点为空：也就是说，没有添加任何元素。在这种情况下，我们将添加的新节点作为链表的头节点和尾节点，因为只有一个节点。
+ 头节点不为空：也就是说，链表中已经添加了一个或多个元素。在这种情况下，现有的尾节点应该指向新节点，新添加的节点将成为尾节点。

在上述两种情况下，尾节点的nextNode都会指向头节点。

让我们创建一个addNode()方法，该方法将要插入的值作为参数：

```
public void addNode(int value) {
  Node newNode = new Node(value);
  if(head == null){
    head = newNode;
  }else {
    tail.nextNode = newNode;
  }
  tail = newNode;
  tail.nextNode = head;
}
```

现在，我们可以在循环链表中添加一些数字：

```
private CircularLinkedList createCircularLinkedList() {
  CircularLinkedList cll = new CircularLinkedList();
  cll.addNode(13);
  cll.addNode(7);
  cll.addNode(24);
  cll.addNode(1);
  cll.addNode(8);
  cll.addNode(37);
  cll.addNode(46);
  return cll;
}
```

### 3.2 查找元素

我们将研究的下一个操作是搜索以确定链表中是否存在元素。

为此，我们将链表中的一个节点(通常是头节点)固定为currentNode，并使用该节点的nextNode遍历整个链表，直到找到所需的元素。

让我们添加一个新方法containsNode()，该方法将searchValue作为参数：

```
public boolean containsNode(int searchValue) {
  Node currentNode = head;
  if (head == null) {
    return false;
  } else {
    do {
      if (currentNode.value == searchValue) {
        return true;
      }
      currentNode = currentNode.nextNode;
    } while (currentNode != null);
    return false;
  }
}
```

现在，让我们添加几个测试来验证上面创建的链表是否包含我们添加的元素，并且没有新元素：

```
@Test
void givenACircularLinkedList_WhenAddingElements_ThenListContainsThoseElements() {
  CircularLinkedList cll = createCircularLinkedList();
  assertTrue(cll.containsNode(8));
  assertTrue(cll.containsNode(37));
}

@Test
void givenACircularLinkedList_WhenLookingForNonExistingElement_ThenReturnsFalse() {
  CircularLinkedList cll = createCircularLinkedList();
  assertFalse(cll.containsNode(11));
}
```

### 3.3 删除元素

接下来，我们看看删除操作。

一般来说，我们删除一个元素后，需要更新被删除节点前一个节点的nextNode引用，使其指向被删除节点的nextNode引用。

然而，我们需要考虑一些特殊情况：

+ 循环链表只有一个元素，我们希望删除该元素 - 在这种情况下，我们只需要将head节点和tail节点设置为null
+ 要删除的元素是头节点 - 我们必须将head.nextNode作为新的头节点
+ 要删除的元素是尾节点 - 我们需要将要删除的节点的前一个节点作为新的尾节点

让我们看一下删除元素的实现：

```
public void deleteNode(int valueToDelete) {
  Node currentNode = head;
  if (head == null) {
    return;
  }
  do {
    Node nextNode = currentNode.nextNode;
    if (nextNode.value == valueToDelete) {
      if (tail == head) {
        head = null;
        tail = null;
      } else {
        currentNode.nextNode = nextNode.nextNode;
        if (head == nextNode) {
          head = head.nextNode;
        }
        if (tail == nextNode) {
          tail = currentNode;
        }
      }
      break;
    }
    currentNode = nextNode;
  } while (currentNode != head);
}
```

现在，让我们创建一些测试，以验证删除操作在所有情况下都能按预期进行：

```
@Test
void givenACircularLinkedList_WhenDeletingInOrderHeadMiddleTail_ThenListDoesNotContainThoseElements() {
  CircularLinkedList cll = createCircularLinkedList();

  assertTrue(cll.containsNode(13));
  cll.deleteNode(13);
  assertFalse(cll.containsNode(13));

  assertTrue(cll.containsNode(1));
  cll.deleteNode(1);
  assertFalse(cll.containsNode(1));

  assertTrue(cll.containsNode(46));
  cll.deleteNode(46);
  assertFalse(cll.containsNode(46));
}

@Test
void givenACircularLinkedList_WhenDeletingInOrderTailMiddleHead_ThenListDoesNotContainThoseElements() {
  CircularLinkedList cll = createCircularLinkedList();

  assertTrue(cll.containsNode(46));
  cll.deleteNode(46);
  assertFalse(cll.containsNode(46));

  assertTrue(cll.containsNode(1));
  cll.deleteNode(1);
  assertFalse(cll.containsNode(1));

  assertTrue(cll.containsNode(13));
  cll.deleteNode(13);
  assertFalse(cll.containsNode(13));
}

@Test
void givenACircularLinkedListWithOneNode_WhenDeletingElement_ThenListDoesNotContainTheElement() {
  CircularLinkedList cll = new CircularLinkedList();
  cll.addNode(1);
  cll.deleteNode(1);
  assertFalse(cll.containsNode(1));
}
```

### 3.4 遍历链表

在最后一节中，我们将介绍循环链表的遍历。与搜索和删除操作类似，对于遍历，我们将currentNode固定为head，并使用该节点的nextNode遍历整个链表。

让我们添加一个新的方法traverseList()，用于打印添加到链表中的元素：

```
public void traverseList() {
  Node currentNode = head;
  if (head != null) {
    do {
      logger.info(currentNode.value + " ");
      currentNode = currentNode.nextNode;
    } while (currentNode != head);
  }
}
```

正如我们所看到的，在上面的示例中，在遍历过程中，我们只需打印每个节点的值，直到返回到头节点。

## 4. 总结

在本文中，我们了解了如何在Java中实现循环链表，并探讨了一些最常见的操作。

首先，我们了解了什么是循环链表，它包含了一些最常见的特性以及与传统链表的区别。然后，我们了解了如何在循环链表实现中插入、搜索、删除和遍历。
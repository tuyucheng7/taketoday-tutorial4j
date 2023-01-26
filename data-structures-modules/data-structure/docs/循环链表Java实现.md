## 1. 简介

在本教程中，我们将研究Java中循环链表的实现。

## 2.循环链表

循环链表是链表的一种变体，[其中](https://www.baeldung.com/java-linkedlist) 最后一个节点指向第一个节点，完成一个完整的节点循环。换句话说，链表的这种变体在末尾没有空元素。

通过这个简单的更改，我们获得了一些好处：

-   循环链表中的任意一个节点都可以作为起点
-   因此，可以从任何节点开始遍历整个列表
-   由于循环链表的最后一个节点有指向第一个节点的指针，所以很容易进行入队和出队操作

总而言之，这在队列数据结构的实现中非常有用。

在性能方面，它与其他链表实现相同，除了一件事：从最后一个节点到头节点的遍历可以在常数时间内完成。对于传统的链表，这是一个线性操作。

## 3.Java实现

让我们从创建一个辅助 Node 类开始，该类将存储 int值和指向下一个节点的指针：

```java
class Node {

    int value;
    Node nextNode;

    public Node(int value) {
        this.value = value;
    }
}
```

现在让我们创建循环链表中的第一个和最后一个节点，通常称为头和尾：

```java
public class CircularLinkedList {
    private Node head = null;
    private Node tail = null;

    // ....
}
```

在接下来的小节中，我们将看看我们可以在循环链表上执行的最常见的操作。

### 3.1. 插入元素

我们要介绍的第一个操作是插入新节点。在插入新元素时，我们需要处理两种情况：

-   头节点为 null ，即尚未添加任何元素。在这种情况下，我们将使我们添加的新节点同时作为列表的头部和尾部，因为只有一个节点
-   头节点不为 null ，也就是说，列表中已经添加了一个或多个元素。在这种情况下，现有的尾巴应该指向新节点，新添加的节点将成为尾巴

在以上两种情况下，tail的nextNode都会指向head

让我们创建一个addNode方法，它将要插入的值作为参数：

```java
public void addNode(int value) {
    Node newNode = new Node(value);

    if (head == null) {
        head = newNode;
    } else {
        tail.nextNode = newNode;
    }

    tail = newNode;
    tail.nextNode = head;
}
```

现在我们可以将一些数字添加到我们的循环链表中：

```java
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

### 3.2. 查找元素

我们要看的下一个操作是搜索以确定列表中是否存在某个元素。

为此，我们将列表中的一个节点(通常是head)固定为currentNode 并使用该节点的nextNode遍历整个列表，直到找到所需的元素。

让我们添加一个 以searchValue作为参数的新方法containsNode ：

```java
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
        } while (currentNode != head);
        return false;
    }
}
```

现在，让我们添加一些测试来验证上面创建的列表是否包含我们添加的元素而不包含新元素：

```java
@Test
 public void givenACircularLinkedList_WhenAddingElements_ThenListContainsThoseElements() {
    CircularLinkedList cll = createCircularLinkedList();

    assertTrue(cll.containsNode(8));
    assertTrue(cll.containsNode(37));
}

@Test
public void givenACircularLinkedList_WhenLookingForNonExistingElement_ThenReturnsFalse() {
    CircularLinkedList cll = createCircularLinkedList();

    assertFalse(cll.containsNode(11));
}
```

### 3.3. 删除元素

接下来，我们将看看删除操作。

一般来说，我们删除一个元素后，需要更新前一个节点的nextNode引用，使其指向被删除节点的nextNode引用。

但是，我们需要考虑一些特殊情况：

-   循环链表只有一个元素，我们要移除该元素——此时只需要将头节点和尾节点设置为null
-   要删除的元素是头节点——我们必须将head.nextNode作为新的头节点
-   要删除的元素是尾 节点——我们需要将要删除的节点的前一个节点作为新的尾节点

我们看一下删除元素的实现：

```java
public void deleteNode(int valueToDelete) {
    Node currentNode = head;
    if (head == null) { // the list is empty
        return;
    }
    do {
        Node nextNode = currentNode.nextNode;
        if (nextNode.value == valueToDelete) {
            if (tail == head) { // the list has only one single element
                head = null;
                tail = null;
            } else {
                currentNode.nextNode = nextNode.nextNode;
                if (head == nextNode) { //we're deleting the head
                    head = head.nextNode;
                }
                if (tail == nextNode) { //we're deleting the tail
                    tail = currentNode;
                }
            }
            break;
        }
        currentNode = nextNode;
    } while (currentNode != head);
}
```

现在让我们创建一些测试来验证删除是否在所有情况下都按预期工作：

```java
@Test
public void givenACircularLinkedList_WhenDeletingInOrderHeadMiddleTail_ThenListDoesNotContainThoseElements() {
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
public void givenACircularLinkedList_WhenDeletingInOrderTailMiddleHead_ThenListDoesNotContainThoseElements() {
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
public void givenACircularLinkedListWithOneNode_WhenDeletingElement_ThenListDoesNotContainTheElement() {
    CircularLinkedList cll = new CircularLinkedList();
    cll.addNode(1);
    cll.deleteNode(1);
    assertFalse(cll.containsNode(1));
}
```

### 3.4. 遍历列表

我们将在最后一节中查看循环链表的遍历。与查找和删除操作类似，对于遍历，我们将currentNode固定为head ，并使用该节点的nextNode遍历整个列表。

让我们添加一个新方法traverseList来打印添加到列表中的元素：

```java
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

正如我们所看到的，在上面的例子中，在遍历过程中，我们简单地打印每个节点的值，直到我们回到头节点。

## 4. 总结

在本教程中，我们了解了如何在Java中实现循环链表并探索了一些最常见的操作。

首先，我们了解了循环链表到底是什么，包括一些最常见的特征以及与传统链表的区别。然后，我们看到了如何在我们的循环链表实现中插入、搜索、删除和遍历项目。
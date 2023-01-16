## 1. 概述

在本教程中，我们将解释如何在Java中查找链表的中间元素。

我们将在下一节介绍主要问题，并展示解决这些问题的不同方法。

## 2. 跟踪尺寸

当我们向列表中添加新元素时，只需跟踪大小即可轻松解决此问题。如果我们知道大小，我们也知道中间元素在哪里，所以解决方案很简单。

让我们看一个使用LinkedList的Java实现的示例：

```java
public static Optional<String> findMiddleElementLinkedList(
  LinkedList<String> linkedList) {
    if (linkedList == null || linkedList.isEmpty()) {
        return Optional.empty();
    }

    return Optional.of(linkedList.get(
      (linkedList.size() - 1) / 2));
}
```

如果我们检查LinkedList类的内部代码，我们可以看到在这个例子中我们只是遍历列表直到到达中间元素：

```java
Node<E> node(int index) {
    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++) {
            x = x.next;
        }
        return x;
    } else {
        Node<E> x = last;
        for (int i = size - 1; i > index; i--) {
            x = x.prev;
        }
        return x;
    }
}
```

## 3.不知道大小找中间

我们经常遇到只有链表头节点的问题，我们需要找到中间元素。在这种情况下，我们不知道列表的大小，这使得这个问题更难解决。

我们将在下一节中展示解决此问题的几种方法，但首先，我们需要创建一个类来表示列表的一个节点。

让我们创建一个Node类，它存储String值：

```java
public static class Node {

    private Node next;
    private String data;

    // constructors/getters/setters
  
    public boolean hasNext() {
        return next != null;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public String toString() {
        return this.data;
    }
}
```

此外，我们将在我们的测试用例中使用这个辅助方法来创建一个仅使用我们的节点的单向链表：

```java
private static Node createNodesList(int n) {
    Node head = new Node("1");
    Node current = head;

    for (int i = 2; i <= n; i++) {
        Node newNode = new Node(String.valueOf(i));
        current.setNext(newNode);
        current = newNode;
    }

    return head;
}
```

### 3.1. 首先找到尺寸

解决这个问题最简单的方法是先找到列表的大小，然后按照我们之前使用的相同方法迭代直到中间元素。

让我们看看这个解决方案的实际应用：

```java
public static Optional<String> findMiddleElementFromHead(Node head) {
    if (head == null) {
        return Optional.empty();
    }

    // calculate the size of the list
    Node current = head;
    int size = 1;
    while (current.hasNext()) {
        current = current.next();
        size++;
    }

    // iterate till the middle element
    current = head;
    for (int i = 0; i < (size - 1) / 2; i++) {
        current = current.next();
    }

    return Optional.of(current.data());
}
```

如我们所见，此代码遍历列表两次。因此，该方案性能较差，不推荐使用。

### 3.2. 在一次迭代中找到中间元素

我们现在将通过在列表中仅进行一次迭代来找到中间元素来改进之前的解决方案。

要迭代地做到这一点，我们需要两个指针同时遍历列表。一个指针每次迭代会前进2个节点，另一个指针每次迭代只会前进一个节点。

当较快的指针到达列表的末尾时，较慢的指针将在中间：

```java
public static Optional<String> findMiddleElementFromHead1PassIteratively(Node head) {
    if (head == null) {
        return Optional.empty();
    }

    Node slowPointer = head;
    Node fastPointer = head;

    while (fastPointer.hasNext() && fastPointer.next().hasNext()) {
        fastPointer = fastPointer.next().next();
        slowPointer = slowPointer.next();
    }

    return Optional.ofNullable(slowPointer.data());
}
```

我们可以使用包含奇数和偶数元素的列表通过简单的单元测试来测试此解决方案：

```java
@Test
public void whenFindingMiddleFromHead1PassIteratively_thenMiddleFound() {
 
    assertEquals("3", MiddleElementLookup
      .findMiddleElementFromHead1PassIteratively(
        createNodesList(5)).get());
    assertEquals("2", MiddleElementLookup
      .findMiddleElementFromHead1PassIteratively(
        reateNodesList(4)).get());
}
```

### 3.3. 递归一次找到中间元素

一次性解决此问题的另一种方法是使用递归。我们可以迭代到列表的末尾以了解大小，并且 在回调中，我们只计算到大小的一半。

为了在Java中做到这一点，我们将创建一个辅助类来在执行所有递归调用期间保留列表大小和中间元素的引用：

```java
private static class MiddleAuxRecursion {
    Node middle;
    int length = 0;
}
```

现在，让我们实现递归方法：

```java
private static void findMiddleRecursively(
  Node node, MiddleAuxRecursion middleAux) {
    if (node == null) {
        // reached the end
        middleAux.length = middleAux.length / 2;
        return;
    }
    middleAux.length++;
    findMiddleRecursively(node.next(), middleAux);

    if (middleAux.length == 0) {
        // found the middle
        middleAux.middle = node;
    }
    
    middleAux.length--;
}
```

最后，让我们创建一个调用递归方法的方法：

```java
public static Optional<String> findMiddleElementFromHead1PassRecursively(Node head) {
 
    if (head == null) {
        return Optional.empty();
    }

    MiddleAuxRecursion middleAux = new MiddleAuxRecursion();
    findMiddleRecursively(head, middleAux);
    return Optional.of(middleAux.middle.data());
}
```

同样，我们可以像以前一样测试它：

```java
@Test
public void whenFindingMiddleFromHead1PassRecursively_thenMiddleFound() {
    assertEquals("3", MiddleElementLookup
      .findMiddleElementFromHead1PassRecursively(
        createNodesList(5)).get());
    assertEquals("2", MiddleElementLookup
      .findMiddleElementFromHead1PassRecursively(
        createNodesList(4)).get());
}
```

## 4. 总结

在本文中，我们介绍了在Java中查找链表中间元素的问题，并展示了解决该问题的不同方法。

我们从跟踪大小的最简单方法开始，然后继续使用解决方案从列表的头节点找到中间元素。
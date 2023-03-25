## 1. 概述

在本教程中，我们将展示如何使用Java的ArrayDeque类——它是Deque接口的一个实现。

ArrayDeque(也称为“Array Double Ended Queue”，发音为“ArrayDeck”)是一种特殊的可增长数组，它允许我们从两侧添加或删除元素。

ArrayDeque实现可以用作堆栈(后进先出)或队列(先进先出)。

## 2. API概览

对于每个操作，我们基本上有两种选择。

第一组包含在操作失败时抛出异常的方法。另一组返回状态或值：

| 操作    | 方法      | 方法抛出异常 |
|-------| ----------- | ----------------- |
| 从头部插入 | offerFirst(e) | addFirst(e)      |
| 从头部移除 | pollFirst() | removeFirst()    |
| 从头部检索 | peekFirst() | getFirst()       |
| 从尾部插入 | offerLast(e) | addLast(e)       |
| 从尾部去除 | pollLast()  | removeLast()     |
| 从尾部检索 | peekLast()  | getLast()        |

## 3. 使用方法

让我们看几个简单的示例，了解如何使用ArrayDeque。

### 3.1 使用ArrayDeque作为堆栈

我们将从一个示例开始，说明如何将类视为堆栈——并推送一个元素：

```java
@Test
public void whenPush_addsAtFirst() {
    Deque<String> stack = new ArrayDeque<>();
    stack.push("first");
    stack.push("second");
 
    assertEquals("second", stack.getFirst());
}
```

我们还看看我们如何从ArrayDeque中弹出一个元素——当用作Stack时：

```java
@Test
public void whenPop_removesLast() {
    Deque<String> stack = new ArrayDeque<>();
    stack.push("first");
    stack.push("second");
 
    assertEquals("second", stack.pop());
}
```

当堆栈为空时，pop方法会抛出NoSuchElementException。

### 3.2 使用ArrayDeque作为队列

现在让我们从一个简单的例子开始，展示我们如何在ArrayDeque中提供一个元素当用作简单的Queue时：

```java
@Test
public void whenOffer_addsAtLast() {
    Deque<String> queue = new ArrayDeque<>();
    queue.offer("first");
    queue.offer("second");
 
    assertEquals("second", queue.getLast());
}
```

让我们看看我们如何从ArrayDeque轮询元素，当用作Queue时：

```java
@Test
public void whenPoll_removesFirst() {
    Deque<String> queue = new ArrayDeque<>();
    queue.offer("first");
    queue.offer("second");
 
    assertEquals("first", queue.poll());
}
```

如果队列为空，则poll方法返回空值。

## 4. ArrayDeque是如何实现的

[![数组双端队列](https://www.baeldung.com/wp-content/uploads/2017/11/ArrayDeque-300x137.jpg)](https://www.baeldung.com/wp-content/uploads/2017/11/ArrayDeque.jpg)
在引擎盖下，ArrayDeque由一个数组支持，该数组在填充时将其大小加倍。

最初，数组的大小被初始化为 16。它被实现为一个双端队列，其中维护两个指针，即头和尾。

让我们在较高的层次上看看这个逻辑的实际应用。

### 4.1 ArrayDeque作为堆栈

[![堆](https://www.baeldung.com/wp-content/uploads/2017/11/Stack-300x187.jpg)](https://www.baeldung.com/wp-content/uploads/2017/11/Stack.jpg)
可以看出，当用户使用push方法添加元素时，它会将头指针移动一个。

当我们弹出一个元素时，它将头部位置的元素设置为null以便该元素可以被垃圾收集，然后将头部指针向后移动一个。

### 4.2 ArrayDeque作为队列

[![队列](https://www.baeldung.com/wp-content/uploads/2017/11/Queue-300x196.jpg)](https://www.baeldung.com/wp-content/uploads/2017/11/Queue.jpg)
当我们使用offer方法 添加一个元素时，它会将尾指针移动一个。

而当用户轮询一个元素时，它将头部位置的元素设置为空，以便该元素可以被垃圾收集，然后移动头部指针。

### 4.3 关于ArrayDeque的注意

最后，关于这个特定的实现，还有一些值得理解和记住的注意事项：

-   它不是线程安全的
-   不接受空元素
-   工作速度明显快于同步堆栈
-   是一个比LinkedList更快的队列，因为引用的局部性更好
-   大多数操作都摊销了常数时间复杂度
-   ArrayDeque返回的迭代器是快速失败的
-   当头指针和尾指针在添加元素时彼此相遇时，ArrayDeque会自动将数组的大小加倍

## 5. 总结

在这篇简短的文章中，我们说明了ArrayDeque中方法的用法。
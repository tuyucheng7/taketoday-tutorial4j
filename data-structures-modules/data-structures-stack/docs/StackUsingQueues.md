## 1. 问题描述

问题与[使用栈实现队列](QueueUsingStacks.md)相反。我们给定一个队列数据结构，它支持像enqueue()和dequeue()这样的标准操作。
我们需要仅使用Queue实例和实例上允许的队列操作来实现Stack数据结构。

<img src="../assets/StackUsingQueues.png">

可以使用两个队列来实现栈。让要实现的栈为“s”，用于实现的队列为“q1”和“q2”。栈“s”可以通过两种方式实现。

## 2. 使push操作成本高昂

此方法确保新输入的元素始终位于“q1”的前面，因此pop操作仅从“q1”出列。 “q2”用于将每个新元素放在“q1”的前面。

1. push(s, x)操作步骤如下：
    + enqueue x到q2。
    + 逐个将q1的元素dequeue，然后enqueue进q2。
    + 交换q1和q2的名称。
2. pop(s, x)操作步骤如下：
    + 从q1 dequeue元素并返回。

以下是上述方法的具体实现：

```java
public class Stack {
  Queue<Integer> q1 = new LinkedList<>();
  Queue<Integer> q2 = new LinkedList<>();
  int currentSize;

  Stack() {
    currentSize = 0;
  }

  void push(int x) {
    currentSize++;
    q2.add(x);
    while (!q1.isEmpty()) {
      q2.add(q1.peek());
      q1.remove();
    }
    Queue<Integer> q = q1;
    q1 = q2;
    q2 = q;
  }

  void pop() {
    if (q1.isEmpty())
      return;
    q1.remove();
    currentSize--;
  }

  int top() {
    if (q1.isEmpty())
      return -1;
    return q1.peek();
  }

  int size() {
    return currentSize;
  }
}
```

## 3. 使pop操作成本高昂

在push操作中，新元素总是enqueue到q1。在pop()操作中，如果q2为空，则q1中除了最后一个元素之外的所有元素都被移动到q2。

最终，最后一个元素从q1中dequeue并返回。

1. push(s, x)操作：
    + enqueue x到q1(假设q1的大小不受限制)。
2. pop(s)操作：
    + 逐个从q1中dequeue元素，然后enqueue进q2。最后一个元素除外。
    + dequeue q1中的最后一个元素，该元素为最终的结果，保存该元素。
    + 交换q1和q2的名称。
    + 返回步骤2中保存的元素。

```java
public class Stack {
  Queue<Integer> q1 = new LinkedList<>();
  Queue<Integer> q2 = new LinkedList<>();
  int currentSize;

  public Stack() {
    currentSize = 0;
  }

  void remove() {
    if (q1.isEmpty())
      return;
    while (q1.size() != 1) {
      q2.add(q1.peek());
      q1.remove();
    }
    q1.remove();
    currentSize--;
    Queue<Integer> q = q1;
    q1 = q2;
    q2 = q;
  }

  void add(int x) {
    currentSize++;
    q1.add(x);
  }

  int top() {
    if (q1.isEmpty())
      return -1;
    while (q1.size() != 1){
      q2.add(q1.peek());
      q1.remove();
    }
    Integer temp = q1.peek();
    q1.remove();
    q2.add(temp);
    Queue<Integer> q = q1;
    q1 = q2;
    q2 = q;
    return temp;
  }
  
  int size(){
    return currentSize;
  }
}
```
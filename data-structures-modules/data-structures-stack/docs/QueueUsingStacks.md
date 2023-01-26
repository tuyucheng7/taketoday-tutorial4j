## 1. 问题描述

问题与[这篇]()文章相反，给定一个带有push和pop操作的栈数据结构，任务是使用栈数据结构的实例和对它们的操作来实现一个队列。

<img src="../assets/QueueUsingStacks.png">

一个队列可以使用两个栈来实现。设要实现的队列为q，用于实现q的栈为stack1和stack2。q可以通过两种方式实现。

## 2. 通过使enqueue操作成本高昂

此方法确保最早输入的元素始终位于stack1的顶部，因此deQueue操作仅从stack1弹出。要将元素置于stack1的顶部，使用stack2。

```
enQueue(q, x): 
当stack1不为空时，将所有内容从stack1 push到stack2。
将x push到stack1(假设栈的大小不受限制)。
将所有内容 push回到stack1。
这里的时间复杂度为O(n)。

deQueue(q): 
如果stack1为空，则出现错误。
从stack1弹出一个元素并将其返回。
这里的时间复杂度为O(1)。
```

以下是上述方法的具体实现：

```java
public class Queue {
  Stack<Integer> stack1 = new Stack<>();
  Stack<Integer> stack2 = new Stack<>();

  void enQueue(int x) {
    // 移动所有stack1元素到stack2
    while (!stack1.isEmpty())
      stack2.push(stack1.pop());
    // push元素到stack1
    stack1.push(x);
    // 将stack2所有元素push回stack1
    while (!stack2.isEmpty())
      stack1.push(stack2.pop());
  }

  int deQueue() {
    // 如果stack1为null，没有元素可用
    if (stack1.isEmpty()) {
      System.out.println("Queue is empty");
      return -1;
    }
    // 返回stack1的top元素
    Integer x = stack1.peek();
    stack1.pop();
    return x;
  }
}
```

+ 时间复杂度：
    + push操作：O(n)，在最坏的情况下，我们将整个stack1清空到stack2中。
    + pop操作：O(1)，与栈中的pop操作相同。
+ 辅助空间：O(n)，使用栈存储值。

## 3. 使deQueue操作成本高昂

在这种方法中，在enQueue操作中，新元素进入stack1的顶部。在deQueue操作中，如果stack2为空，则所有元素都将移动到stack2，最后返回stack2的顶部元素。

```
enQueue(q, x):
将x push到stack1(假设栈的大小不受限制)。
这里的时间复杂度为O(1)

deQueue(q):
1. 如果两个栈都为空，则返回错误。
2. 如果stack2为空
    当stack1不为空时，将所有内容从stack1 push到stack2。
3. 从stack2弹出元素并将其返回。
这里的时间复杂度为O(n)
```

这种方法肯定比上一种方法更好。

方法1在enQueue操作中移动所有元素两次，而方法2(在deQueue操作中)仅在stack2为空时移动元素一次。因此，enQueue操作的摊销复杂度变为Theta。

以下是上述方法的具体实现：

```java
public class Queue {
  Stack<Integer> stack1 = new Stack<>();
  Stack<Integer> stack2 = new Stack<>();

  void push(Stack<Integer> stack, int x) {
    // push x到栈中
    stack.push(x);
  }

  int pop(Stack<Integer> stack) {
    // 如果栈为空，没有元素可用
    if (stack.isEmpty()) {
      System.out.println("Stack empty");
      return -1;
    }
    // 返回栈中的top元素
    return stack.pop();
  }

  void enQueue(Queue q, int x) {
    push(q.stack1, x);
  }

  int deQueue(Queue q) {
    int x;
    // 如果两个栈都为null，没有元素可用
    if (q.stack1.isEmpty() && q.stack2.isEmpty()) {
      System.out.println("Q is Empty");
      return -1;
    }
    // 如果stack2为空，将stack1所有元素移动到stack2
    if (q.stack2.isEmpty()) {
      while (!q.stack1.isEmpty()) {
        x = pop(stack1);
        push(q.stack2, x);
      }
    }
    x = pop(q.stack2);
    return x;
  }
}
```

+ 时间复杂度：
    + push操作：O(1)，与栈中的pop操作相同。
    + pop操作：O(n)，在最坏的情况下，我们将整个栈1清空到栈2中。
+ 空间复杂度：O(n)，使用栈存储值。

## 4. 其他方法

还可以使用一个用户栈和一个函数调用栈来实现队列。下面是修改后的方法2，其中递归(或函数调用堆栈)用于仅使用一个用户定义的栈实现队列。

```
enQueue(x):
1. push x到stack1

deQueue:

1. 如果stack1为空，则出现错误。
2. 如果stack1只有一个元素，则返回它。
3. 递归地从stack1中弹出所有内容，存储弹出的元素到变量res中，将res push回stack1并返回res。
```

步骤3确保始终返回最后弹出的元素，并且由于当stack1中只有一个元素时递归停止(步骤2)，
因此我们在deQueue()中获得stack1的最后一个元素，并且在步骤中所有其他元素都被推回。

使用函数调用栈实现方法2：

```java
public class Queue {
  Stack<Integer> stack = new Stack<>();

  void push(Stack<Integer> stack, int x) {
    stack.push(x);
  }

  int pop(Stack<Integer> stack) {
    if (stack.isEmpty()) {
      System.out.println("stack is empty");
      return -1;
    }
    return stack.pop();
  }

  void enQueue(Queue q, int x) {
    push(q.stack, x);
  }

  int deQueue(Queue q) {
    int x, res;
    // 如果栈为空，则没有元素可用
    if (stack.isEmpty()) {
      System.out.println("stack is empty");
      return -1;
    } else if (stack.size() == 1) // 如果栈只有一个元素，直接弹出并返回
      return pop(q.stack);
    else {
      // 从栈中弹出一个元素
      x = pop(q.stack);
      // 保存上一个deQueue的元素
      res = deQueue(q);
      // push回栈中
      push(q.stack, x);
      return res;
    }
  }
}
```

+ 时间复杂度：
    + push操作：O(1)，与栈中的pop操作相同。
    + pop操作：O(n)，与上述方法的区别在于，在此方法中，返回元素，并在一次调用中恢复所有元素。
+ 空间复杂度：O(n)，使用栈存储值。
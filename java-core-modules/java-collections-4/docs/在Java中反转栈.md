## 一、概述

在本文中，我们将研究使用 Java 反转堆栈的不同方法。堆栈是一种 LIFO（后进先出）数据结构，支持从同一侧插入（push）和移除（pop）元素[。](https://www.baeldung.com/java-stack)

我们可以将堆栈想象成桌子上的一堆盘子；从顶部操作时最安全。

## 2. 问题：反转堆栈

让我们深入探讨问题陈述。我们得到了一个*Stack*对象作为输入，我们需要以相反的顺序返回包含元素的堆栈。这是一个例子。

输入：[1, 2, 3, 4, 5, 6, 7, 8, 9]

输出：[9, 8, 7, 6, 5, 4, 3, 2, 1]

输入是前九个自然数的栈，我们代码的输出应该是倒序的相同自然数。***我们可以将这个问题扩展到任何类型的堆栈，例如， String\*****元素****的堆栈\*，\**Node\*等自定义对象的堆栈等。**

例如：

输入：[“红”、“蓝”、“绿”、“黄”]

输出：[“黄”、“绿”、“蓝”、“红”]

## *3. 使用队列*反转堆栈

[*在本节中，让我们看看如何使用队列*](https://www.baeldung.com/java-queue)数据结构来解决问题。Queue是一种FIFO（先进先出）数据结构，支持从后面添加元素，从前面移除元素*。*

给定一堆元素作为输入，**我们可以从堆栈的顶部选择元素，一次一个，并将它们插入到我们的队列中**。从我们的第一个自然数示例开始，我们将从指向 9 的栈顶开始。我们在每一步都将栈顶元素插入队列的后端，最终，我们会清空栈。此时我们已经按以下顺序用元素填充了队列：

（后）[1, 2, 3, 4, 5, 6, 7, 8, 9]（前）。

完成后，**我们可以从队列中删除元素，这发生在前端，并将其推回我们的堆栈**。此活动完成后，我们将留下所需的输出堆栈 [9, 8, 7, 6, 5, 4, 3, 2, 1]。

这是代码的样子，考虑到堆栈是*Integer*类型：

```java
public Stack reverseIntegerStack(Stack<Integer> inputStack) {
    Queue<Integer> queue = new LinkedList<>();
    while (!inputStack.isEmpty()) {
        queue.add(inputStack.pop());
    }
    while (!queue.isEmpty()) {
        inputStack.add(queue.remove());
    }
    return inputStack;
}
复制
```

## 4. 使用递归反转堆栈

让我们讨论一种不使用任何额外数据结构来解决这个问题的方法。**[递归](https://www.baeldung.com/java-recursion)是计算机科学中的一个核心概念，它处理只要满足前提条件就重复调用自身的方法的思想**。任何递归函数都应该有两个组成部分：

-   递归调用：在我们的例子中，对方法的递归调用将从给定堆栈中删除元素
-   停止条件：递归将在给定堆栈为空时结束

对递归方法的每次调用都会添加到 JVM 内存中的调用堆栈中。我们可以利用这个事实来反转给定的堆栈。**递归调用将从栈顶移除一个元素并将其添加到内存调用栈中。** 

**当输入栈为空时，内存调用栈以相反的顺序包含栈中的元素。**调用堆栈的顶部包含数字 1，而最底部的调用堆栈包含数字 10。此时，我们从调用堆栈中取出项目 **并将元素插入堆栈底部，反转元素的原始顺序。**

让我们看看代码中的两步递归算法：

```java
private void reverseStack(Stack<Integer> stack) {
    if (stack.isEmpty()) {
        return;
    }
    int top = stack.pop();
    reverseStack(stack);
    insertBottom(stack, top);
}复制
```

reverseStack *()* 方法以递归方式从堆栈中弹出顶部元素。一旦输入栈为空，我们将当前在调用栈中的元素插入到栈底：

```java
private void insertBottom(Stack<Integer> stack, int value) {
    if (stack.isEmpty()) {
        stack.add(value);
    } else {
        int top = stack.pop();
        insertBottom(stack, value);
        stack.add(top);
    }
}复制
```

## 5. 比较反转堆栈的方法

我们讨论了反转给定堆栈问题的两种方法。这些算法适用于任何类型的*堆栈*。**使用附加\*Queue\* 数据结构的第一个解决方案以 O(n) 时间复杂度反转堆栈**。*但是，由于我们使用Queue*形式的额外空间，空间复杂度也是 O(n)。

另一方面，由于递归调用，递归解决方案[的时间复杂度为 O(n²)，](https://www.baeldung.com/cs/master-theorem-asymptotic-analysis)**但没有额外的空间复杂度**，因为我们使用程序调用堆栈来存储堆栈的元素。

## 六，结论

在本文中，我们讨论了在 Java 中反转*堆栈*的两种方法，并比较了算法的运行时间和空间复杂性。
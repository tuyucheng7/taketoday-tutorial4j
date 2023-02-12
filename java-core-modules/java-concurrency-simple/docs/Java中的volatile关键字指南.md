## 1. 概述

在没有必要同步的情况下，编译器、Java运行时系统或CPU处理器可能会应用各种优化。尽管这些优化在大多数情况下都是有益的，但有时它们可能会导致一些微妙的问题。

缓存和重排序是在并发上下文中可能会让我们感到惊讶的优化之一。Java和JVM提供了很多方法来控制[内存顺序](https://www.baeldung.com/java-variable-handles#memory-ordering)，volatile关键字就是其中之一。

在本教程中，我们将重点关注Java语言中这个基础但经常被误解的概念-volatile关键字。首先，我们将从一些有关底层计算机体系结构如何工作的背景知识开始，然后我们将熟悉Java中的内存顺序。

## 2. 共享多处理器架构

处理器负责执行程序指令。因此，他们需要从RAM中检索程序指令和所需数据。

由于CPU每秒能够执行大量指令，因此从RAM中获取指令对它们来说并不理想。为了改善这种情况，处理器目前使用[无序执行](https://en.wikipedia.org/wiki/Out-of-order_execution)、[分支预测](https://en.wikipedia.org/wiki/Branch_predictor)、[推测性执行](https://en.wikipedia.org/wiki/Speculative_execution)，当然还有缓存等技巧。

这是以下内存层次结构发挥作用的地方：

<img src="../assets/img_2.png">

随着不同的核心执行更多的指令并处理更多数据，它们会用更多相关数据和指令填充缓存。**这将以引入高速[缓存一致性](https://en.wikipedia.org/wiki/Cache_coherence)挑战为代价提高整体性能**。

**简而言之，当一个线程更新缓存值时，我们应该三思而后行**。

## 3. 何时使用volatile

为了进一步扩展缓存一致性，我们将从《[Java并发实践](https://www.oreilly.com/library/view/java-concurrency-in/0321349601/)》一书中借用一个示例：

```java
public class TaskRunner {
    private static int number;
    private static boolean ready;

    private static class Reader extends Thread {

        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }
            System.out.println(number);
        }
    }

    public static void main(String[] args) {
        new Reader().start();
        number = 42;
        ready = true;
    }
}
```

TaskRunner类维护两个简单的变量。在它的main()方法中，它创建另一个线程，只要ready变量为false，它就会在ready变量上自旋。当ready变为true时，线程将简单的打印number变量。

**许多人可能希望这个程序在短暂的延迟后简单地打印42。然而实际上，延迟可能要长得多。它甚至可能永远挂起或打印0！**

**这些异常的原因是缺乏适当的内存可见性和重排序**。让我们更详细地评估它们。

### 3.1 内存可见性

在这个简单的示例中，我们有两个应用程序线程：主线程和Reader线程。让我们设想一个场景，操作系统在两个不同的CPU核心上调度这些线程，其中：

+ 主线程在其核心缓存中有其ready和number变量的副本。
+ Reader线程也有它的副本。
+ 主线程更新缓存的值。

在大多数现代处理器上，写请求不会在发出后立即应用。事实上，**处理器倾向于将这些写操作排队到一个特殊的写入缓冲区中**。一段时间后，他们会立即将这些写操作应用到主内存。

尽管如此，**当主线程更新number和ready变量时，并不能保证Reader线程会看到什么。换句话说，Reader线程可能会立即看到更新后的值，或者在一些延迟之后，或者根本看不到！**

这种内存可见性可能会导致依赖可见性的程序出现活性问题。

### 3.2 重排序

更糟糕的是，**Reader线程可能会看到这些写入的顺序与实际程序顺序不同**。例如，由于我们首先更新了number变量：

```java
public static void main(String[] args) { 
    new Reader().start();
    number = 42; 
    ready = true; 
}
```

我们可能希望Reader线程打印42。**但实际上可能会看到0作为打印值**。

重排序是一种用于提高性能的优化技术.有趣的是，不同的组件可能会应用此优化：

+ 处理器可能会以不同于程序顺序的顺序刷新其写入缓冲区
+ 处理器可能会应用无序执行技术
+ JIT编译器可以通过重排序进行优化

### 3.3 volatile内存排序

**为了确保对变量的更新可预测地传播到其他线程，我们应该对这些变量应用volatile修饰符**：

```java
public class TaskRunner {
    private volatile static int number;
    private volatile static boolean ready;
    // same as before
}
```

通过这种方式，我们告知Java运行时和处理器，不要重新排序任何涉及volatile变量的指令。此外，处理器知道他们应该立即刷新对这些变量的任何更新。

## 4. volatile和线程同步

对于多线程应用程序，我们需要确保一些规则以实现一致的行为：

+ 互斥：一次只有一个线程执行一个临界区
+ 可见性：一个线程对共享数据所做的更改对其他线程可见，以保持数据一致性

同步方法和同步块提供上述两种属性，但以牺牲应用程序性能为代价。

volatile是一个非常有用的关键字，因为**它可以在不提供互斥的情况下帮助确保数据更改的可见性**。因此，它在我们可以让多个线程并行执行一段代码但需要确保可见性属性的地方很有用。

## 5. Happens-Before

volatile变量的内存可见性影响超出了volatile变量本身。

为了使事情更具体，我们假设线程A写入一个volatile变量，然后线程B读取同一个volatile变量。在这种情况下，**在写入volatile变量之前对A可见的值将在读取volatile变量后对B可见**：

<img src="../assets/img_3.png">

**从技术上讲，对volatile字段的任何写入都发生在同一字段的每次后续读取之前**。这是Java内存模型([JMM](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html))的volatile变量规则。

### 5.1 背负

**由于happens-before内存排序的优势，有时我们可以利用另一个volatile变量的可见性属性**。例如，在我们的特定示例中，我们只需要将ready变量标记为volatile：

```java
public class TaskRunner {
    private static int number; // not volatile
    private volatile static boolean ready;

    // same as before
}
```

在将true写入ready变量之前的任何内容在读取ready变量之后都可见。因此，number变量依赖于ready变量强制执行的内存可见性。简单地说，**即使它不是一个volatile的变量，但它表现出一种volatile的行为**。

使用这些语义，我们可以仅将类中的几个变量定义为volatile并优化可见性保证。

## 6. 总结

在本文中，我们探讨了volatile关键字及其功能，以及从Java 5开始对其进行的改进。
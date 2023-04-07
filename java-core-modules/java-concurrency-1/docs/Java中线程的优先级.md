## 1. 概述

在本教程中，我们将讨论**Java线程调度程序如何根据优先级执行线程**。此外，我们还将介绍Java中线程优先级的类型。

## 2. 优先级类型

在Java中，线程的优先级是1到10之间的一个整数，整数越大，优先级越高。线程调度程序使用来自每个线程的这个整数来确定应该允许执行哪个线程。**Thread类定义了三种类型的优先级**：

-   最低优先级
-   正常优先级
-   最高优先级

Thread类将这些优先级类型定义为常量MIN_PRIORITY、NORM_PRIORITY和MAX_PRIORITY，其值分别为1、5和 0。**NORM_PRIORITY是新线程的默认优先级**。

## 3. 线程执行概述

**JVM支持一种称为固定优先级抢占式调度的调度算法**。所有Java线程都有一个优先级，JVM首先为优先级最高的线程提供服务。

当我们创建一个线程时，它会继承其默认优先级。当多个线程准备执行时，JVM选择并执行具有最高优先级的[Runnable线程](https://www.baeldung.com/java-thread-lifecycle)。如果此线程停止或变得不可运行，则优先级较低的线程将执行。**如果两个线程具有相同的优先级，JVM将以FIFO顺序执行它们**。

有两种情况可能导致不同的线程运行：

-   优先级高于当前线程的线程变为可运行
-   当前线程退出可运行状态或[yield](https://www.baeldung.com/java-thread-yield)(暂停，允许其他线程)

一般来说，在任何时候，最高优先级的线程都在运行。但有时，**线程调度程序可能会选择低优先级的线程来执行以避免饥饿**。

## 4. 了解和改变线程的优先级

Java的Thread类提供了检查线程优先级和修改线程优先级的方法。[getPriority()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Thread.html#getPriority())实例方法返回表示其优先级的整数。[setPriority()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Thread.html#setPriority(int))实例方法接收1到10之间的整数来更改线程的优先级。如果我们传递1-10范围之外的值，该方法将抛出错误。

## 5. 总结

在这篇简短的文章中，我们了解了如何使用抢占式调度算法在Java中按优先级执行多个线程。我们进一步检查了优先级范围和默认线程优先级。此外，我们还分析了用于检查线程优先级并在必要时对其进行操作的Java方法。
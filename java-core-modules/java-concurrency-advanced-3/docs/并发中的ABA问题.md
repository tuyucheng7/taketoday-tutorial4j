## 1. 概述

在本教程中，我们将介绍并发编程中ABA问题的理论背景。我们将看到它的根本原因以及解决方案。

## 2. Compare and Swap

为了了解根本原因，让我们简要回顾一下[比较和交换](https://www.baeldung.com/lock-free-programming#1-compare-and-swap)的概念。

比较和交换(CAS)是无锁算法中的一种常用技术，用于**确保如果另一个线程同时修改了同一空间，则一个线程对共享内存的更新将失败**。

我们通过在每次更新中**使用两条信息来实现这一点：更新值和原始值**。然后CAS首先将现有值与原始值进行比较。如果相等，则将现有值与更新后的值交换。

当然，这种情况也可能发生在引用上。

## 3. ABA问题

现在，ABA问题是一个反常现象，单独使用CAS方法无法解决问题。

例如，假设一个操作读取了一些共享内存(A)，以准备更新它。然后，另一个操作临时修改该共享内存(B)，然后恢复它(A)。之后，**一旦第一个操作执行CAS，它就会看起来好像没有进行任何更改**，从而使检查的完整性无效。

**虽然在许多情况下这不会造成问题，但有时A并不像我们想象的那样等于A**。让我们看看这在实践中的效果。

### 3.1 示例

为了通过一个实际示例来演示这个问题，让我们考虑一个简单的银行帐户类，其中包含一个int变量balance保存实际余额。我们还有两个方法：一个用于提款(withdrawals)，一个用于存款(deposits)。这些操作使用CAS来减少和增加帐户的余额。

### 3.2 问题出在哪里？

让我们考虑一个线程1和线程2在同一个银行账户上运行的多线程场景。

当线程1想要提取一些钱时，它会读取实际余额以便在稍后的CAS操作中使用该值来比较余额。然而，由于某种原因，线程1执行有点慢-也许它被阻塞了。

**同时，当线程1挂起时，线程2使用相同的机制对帐户执行两个操作**。首先，它更改线程1已经读取的原始值，但随后又将其更改回原始值。

一旦线程1恢复，它看起来好像没有任何变化，CAS会成功：

<img src="../assets/img_1.png">

## 4. Java示例

为了更好地形象化这一点，让我们看一些代码。在这里，我们将使用Java，但问题本身并不是特定于语言的。

### 4.1 Account类

首先，我们的Account类将余额保存在[AtomicInteger](https://www.baeldung.com/java-atomic-variables)中，该类为我们提供了Java中整数的CAS。此外，还有另一个AtomicInteger用于计算成功事务的数量。最后，我们有一个[ThreadLocal](https://www.baeldung.com/java-threadlocal)变量来捕获给定线程的CAS操作失败次数。

```java
public class Account {
    private AtomicInteger balance;
    private AtomicInteger transactionCount;
    private ThreadLocal<Integer> currentThreadCASFailureCount;
    // ...
}
```

### 4.2 存款

接下来，我们可以为我们的Account类实现存款方法：

```java
public boolean deposit(int amount) {
    int current = balance.get();
    boolean result = balance.compareAndSet(current, current + amount);
    if (result) {
        transactionCount.incrementAndGet();
    } else {
        int currentCASFailureCount = currentThreadCASFailureCount.get();
        currentThreadCASFailureCount.set(currentCASFailureCount + 1);
    }
    return result;
}
```

请注意，AtomicInteger.compareAndSet(...)只不过是AtomicInteger.compareAndSwap()方法的包装，用于反映CAS操作的布尔结果。

### 4.3 取款

同样，提款方法可以实现为：

```java
public boolean withdraw(int amount) {
    int current = getBalance();
    maybeWait();
    boolean result = balance.compareAndSet(current, current - amount);
    if (result) {
        transactionCount.incrementAndGet();
    } else {
        int currentCASFailureCount = currentThreadCASFailureCount.get();
        currentThreadCASFailureCount.set(currentCASFailureCount + 1);
    }
    return result;
}
```

为了能够演示ABA问题，我们创建了一个maybeWait()方法来模拟一些耗时的操作，为其他线程提供了一些额外的时间来对余额执行修改。

现在，我们将线程1挂起两秒钟：

```java
private void maybeWait() {
    if ("thread1".equals(Thread.currentThread().getName())) {
        sleepUninterruptibly(2, TimeUnit.SECONDS);
    }
}
```

### 4.4 ABA情景

最后，我们可以编写一个单元测试来检查ABA问题是否可能。

我们要做的是创建两个线程，我们之前的线程1和线程2。线程1将读取余额并延迟。线程2在线程1休眠时会更改余额，然后再将其改变回来。

一旦线程1醒来，它并没有变得更聪明，它的操作仍然会成功。

在一些初始化之后，我们可以创建线程1，该线程需要一些额外的时间来执行CAS操作。完成之后，它不会意识到内部状态已更改，因此CAS失败计数将为零而不是ABA场景中预期的1：

```java
@Test 
void abaProblemTest() {
    // ...
    Runnable thread1 = () -> {
        assertTrue(account.withdraw(amountToWithdrawByThread1));

        assertTrue(account.getCurrentThreadCASFailureCount() > 0); // test will fail!
    };
    // ...
}
```

同样，我们可以创建线程2，它将在线程1之前完成，并更改帐户余额然后将其更改回原始值。在这种情况下，我们预计不会出现任何CAS问题。

```java
@Test
void abaProblemTest() {
    // ...
    Runnable thread2 = () -> {
        assertTrue(account.deposit(amountToDepositByThread2));
        assertEquals(defaultBalance + amountToDepositByThread2, account.getBalance());
        assertTrue(account.withdraw(amountToWithdrawByThread2));

        assertEquals(defaultBalance, account.getBalance());

        assertEquals(0, account.getCurrentThreadCASFailureCount());
    };
    // ...
}
```

运行线程后，线程1将获得预期的余额，尽管来自线程2的额外两个事务不是预期的：

```java
@Test
void abaProblemTest() {
    // ...

    assertEquals(defaultBalance - amountToWithdrawByThread1, account.getBalance());
    assertEquals(4, account.getTransactionCount());
}
```

## 5. 基于值与基于引用的场景

在上面的例子中，我们可以发现一个重要的事实-我们在场景结束时得到的AtomicInteger与我们开始时的完全一样。除了未能捕获线程2进行的两个额外事务外，在这个特定示例中没有发生任何异常。

这背后的原因是我们基本上使用了值类型而不是引用类型。

### 5.1 基于引用的异常

我们可能会遇到以重用为目的使用引用类型的ABA问题。在这种情况下，在ABA场景结束时，我们得到了匹配的引用，因此CAS操作成功，但是，该引用可能指向与最初不同的对象，这可能会导致歧义。

## 6. 解决方案

现在我们已经很好地了解了问题，让我们深入研究一些可能的解决方案。

### 6.1 垃圾回收

对于引用类型，**垃圾回收(GC)可以在大多数情况下保护我们免受ABA问题的影响**。

当线程1在我们正在使用的给定内存地址处具有对象引用时，线程2所做的任何事情都不会导致另一个对象使用相同的地址。该对象仍然存在，并且它的地址不会被重用，直到没有对它的引用。

虽然这适用于引用类型，但问题是当我们在无锁数据结构中依赖GC时。

当然，有些语言不提供GC也是事实。

### 6.2 危险指示器(信号探针)

危险指针在某种程度上与前一个有点相关-**我们可以在没有自动垃圾回收机制的语言中使用它们**。

简而言之，线程在共享数据结构中跟踪有问题的指针。这样，每个线程都知道指针定义的给定内存地址上的对象可能已被另一个线程修改。

那么现在，让我们看看其他几个解决方案。

### 6.3 不变性

当然，**使用不可变对象可以解决这个问题，因为我们不会在整个应用程序中重用对象**。每当发生变化时，就会创建一个新对象，因此CAS肯定会失败。

但是，我们的最终解决方案也允许可变对象。

### 6.4 双重CAS

双重CAS方法背后的想法是**维护另一个变量，即版本号**，然后在比较中也使用它。在这种情况下，如果我们有旧版本号，CAS操作将失败，这只有在另一个线程同时修改了我们的变量时才有可能。

在Java中，[AtomicStampedReference](https://www.baeldung.com/java-atomicstampedreference)和[AtomicMarkableReference](https://www.baeldung.com/java-atomicmarkablereference)是该方法的标准实现。

## 7. 总结

在本文中，我们了解了ABA问题以及一些防止它的技术。
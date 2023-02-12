## 1. 概述

简单地说，当涉及并发时，共享可变状态很容易导致问题。如果对共享可变对象的访问管理不当，应用程序很快就会变得容易出现一些难以检测的并发错误。

在本文中，我们将重新审视使用锁来处理并发访问，探索与锁相关的一些缺点，最后，引入原子变量作为替代方案。

## 2. 锁

让我们来看看这个类：

```java
public class UnsafeCounter {
    private int counter;

    int getValue() {
        return counter;
    }

    void increment() {
        counter++;
    }
}
```

在单线程环境中，这非常有效；然而，一旦我们允许多个线程写入，我们就会开始得到不一致的结果。

这是因为简单的自增操作(counter++)，它可能看起来像一个原子操作，但实际上是三个操作的组合：获取值、自增和将更新后的值写回。

**如果两个线程试图同时获取和更新该值，可能会导致更新丢失**。

管理对对象的访问的方法之一是使用锁。这可以通过在increment()方法签名中使用synchronized关键字来实现。synchronized关键字确保一次只能有一个线程进入该方法(要了解有关锁定和同步的更多信息，请参阅[Java中的同步关键字指南](https://www.baeldung.com/java-synchronized))：

```java
public class SafeCounterWithLock {
    private volatile int counter;

    int getValue() {
        return counter;
    }

    synchronized void increment() {
        counter++;
    }
}
```

此外，我们需要添加volatile关键字以确保线程之间适当的引用可见性。

**使用锁可以解决这个问题。然而，性能遭到了影响**。

当多个线程试图获取锁时，其中一个线程获胜，而其余线程要么被阻塞，要么被挂起。

**挂起然后恢复线程的过程非常昂贵**，会影响系统的整体效率。

在一个小程序(如上述SafeCounterWithLock类)中，上下文切换所花费的时间可能会远远超过实际代码的执行时间，从而大大降低整体效率。

## 3. 原子操作

有一个研究分支专注于为并发环境创建非阻塞算法。这些算法利用Compare-and-swap(CAS)等低级原子机器指令来确保数据完整性。

典型的CAS操作适用于三个操作数：

1. 要操作的内存位置(M)
2. 变量的现有预期值(A)
3. 需要设置的新值(B)

**CAS操作以原子方式将M中的值更新为B，但前提是M中的现有值与A匹配，否则不执行任何操作**。

在这两种情况下，都会返回M中的现有值。这将三个步骤(获取值、比较值和更新值)组合到单个机器级别操作中。

当多个线程试图通过CAS更新同一个值时，其中一个线程获胜并更新该值。**然而，与锁不同的是，其他线程不会被挂起**；相反，它们只是被告知它们没有更新资格。然后线程可以继续做进一步的工作，并且完全避免了上下文切换。

缺点是核心程序逻辑变得更加复杂。这是因为我们必须处理CAS操作失败的情况。我们可以一次又一次地重试直到成功，或者我们什么都不做，继续前进，这取决于用例。

## 4. Java中的原子变量

Java中最常用的原子变量类是[AtomicInteger](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html)、[AtomicLong](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/AtomicLong.html)、[AtomicBoolean](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/AtomicBoolean.html)和[AtomicReference](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/AtomicReference.html)。这些类分别代表一个int、long、boolean和Object对象，可以进行原子更新。这些类公开的主要方法有：

+ get()：从内存中获取值，以便可以看见其他线程所做的更改；相当于读取一个volatile变量
+ set()：将值写入内存，以便其他线程可以看到此更改；相当于写入一个volatile变量
+ lazySet()：最终将值写入内存，可能会通过后续的相关内存操作重新排序。一个用例是取消引用，以便进行垃圾回收，它永远不会被再次访问。在这种情况下，通过延迟null volatile写入来实现更好的性能
+ compareAndSet()：与第3节中描述的相同，成功时返回true，否则返回false
+ weakCompareAndSet()：与第3节所述相同，但在某种意义上更弱，即它不会创建happens-before排序。这意味着它可能不一定会看到对其他变量所做的更新。从[Java 9](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html#weakCompareAndSet(int,int))开始，此方法已在所有原子实现中都被弃用，取而代之的是[weakCompareAndSetPlain()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html#weakCompareAndSetPlain(int,int))。weakCompareAndSet()的内存效果显而易见，但它的名称暗示了volatile内存效应。为了避免这种混淆，他们弃用了这种方法，并添加了四种具有不同内存效果的方法，例如weakCompareAndSetPlain()或[weakCompareAndSetVolatile()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html#weakCompareAndSetVolatile(int,int))

使用AtomicInteger实现的线程安全计数器如以下示例所示：

```java
public class SafeCounterWithoutLock {
    private final AtomicInteger counter = new AtomicInteger(0);

    int getValue() {
        return counter.get();
    }

    void increment() {
        while (true) {
            int existingValue = getValue();
            int newValue = existingValue + 1;
            if (counter.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }
}
```

如你所见，我们重试compareAndSet()操作并在失败时再次重试，因为我们要确保对increment()方法的调用总是将值增加1。

## 5. 总结

在本快速教程中，我们描述了另一种处理并发的方法，可以避免与锁相关的缺点。我们还研究了Java中原子变量类公开的一些主要方法。
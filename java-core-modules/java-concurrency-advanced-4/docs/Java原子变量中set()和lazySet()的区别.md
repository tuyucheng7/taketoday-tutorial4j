## 1. 概述

在本教程中，我们将了解Java[原子类](https://www.baeldung.com/java-atomic-variables)(如AtomicInteger和AtomicReference)的set()和lazySet()方法之间的区别。

## 2. 原子变量–快速回顾

**Java中的原子变量允许我们能够轻松地对类引用或字段执行线程安全操作，而无需添加诸如监视器或互斥锁之类的并发原语**。

它们在java.util.concurrent.atomic包下定义，尽管它们的API因原子类型而异，但它们中的大多数都支持set()和lazySet()方法。

为了简单起见，我们将在本文中使用AtomicReference和AtomicInteger，但同样的原理也适用于其他原子类型。

## 3. set方法

**set()方法等效于写入[volatile](https://www.baeldung.com/java-volatile)字段**。

调用set()后，当我们从不同的线程使用get()方法访问该字段时，更改立即可见。这意味着该值已从CPU缓存刷新到所有CPU内核共用的内存层。

为了演示上述功能，让我们创建一个最小的[生产者-消费者](https://www.baeldung.com/java-producer-consumer-problem)控制台应用程序：

```java
public class Application {

    AtomicInteger atomic = new AtomicInteger(0);

    public static void main(String[] args) {
        Application app = new Application();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                app.atomic.set(i);
                System.out.println("Set: " + i);
                Thread.sleep(100);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                synchronized (app.atomic) {
                    int counter = app.atomic.get();
                    System.out.println("Get: " + counter);
                }
                Thread.sleep(100);
            }
        }).start();
    }
}
```

在控制台中，我们应该看到一系列“Set”和“Get”消息：

```shell
Set: 3
Set: 4
Get: 4
Get: 5
```

**能够证明[缓存一致性](https://en.wikipedia.org/wiki/Cache_coherence)的事实是“Get”语句中的值总是等于或大于它们上面的“Set”语句中的值**。

这种行为虽然非常有用，但也会影响性能。如果我们可以在不需要缓存一致性的情况下这种情况，那就太好了。

## 4. lazySet()方法

lazySet()方法与set()方法相同，但没有缓存刷新。

**换句话说，我们的更改最终只会对其他线程可见。这意味着从不同线程对更新后的AtomicReference调用get()可能会得到旧值**。

为了实际看到这一点，让我们在之前的控制台应用程序中更改第一个线程的Runnable：

```java
for (int i = 0; i < 10; i++) {
    app.atomic.lazySet(i);
    System.out.println("Set: " + i);
    Thread.sleep(100);
}
```

新的“Set”和“Get”消息可能并不总是递增的：

```shell
Set: 4
Set: 5
Get: 4
Get: 5
```

由于线程的性质，我们可能需要重复运行应用程序才能看到这种行为。即使生产者线程已将AtomicInteger设置为5，消费者线程也会首先检索值4，这意味着当使用lazySet()时，系统最终是一致的。

**用更专业的术语来说，我们说lazySet()方法不充当代码中的happens-before边缘**，与它们的set()对应物相反。

## 5. 何时使用lazySet()

目前还不清楚我们什么时候应该使用lazySet()，因为它与set()的区别很微妙。我们需要仔细分析问题，不仅要确保性能得到提升，还要确保在多线程环境中的正确性。

**我们可以使用它的一种方法是，一旦我们不再需要对象引用，就用null替换它**。这样，我们就表明该对象符合垃圾回收条件，而不会产生任何性能损失。我们假设其他线程可以使用已弃用的值，直到他们看到AtomicReference为null。

不过，一般来说，**当我们想要对原子变量进行更改时，我们应该使用lazySet()，并且我们知道更改不需要立即对其他线程可见**。

## 6. 总结

在本文中，我们了解了原子类的set()和lazySet()方法之间的区别。我们还了解了何时使用哪种方法。
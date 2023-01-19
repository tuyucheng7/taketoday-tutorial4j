## 1. 概述

在本教程中，我们将了解Java原子类(如AtomicInteger和AtomicReference)的set()和lazySet()方法之间的区别。

## 2. 原子变量–快速回顾

Java中的原子变量允许我们能够轻松地对类引用或字段执行线程安全操作，而无需添加诸如监视器或互斥锁之类的并发原语。

它们在java.util.concurrent.atomic包下定义，尽管它们的API因原子类型而异，但它们中的大多数都支持set()和lazySet()方法。

为简单起见，我们将在本文中使用AtomicReference和AtomicInteger，但同样的原理也适用于其他原子类型。

## 3. set方法

set()方法等效于写入volatile字段。

在调用set()后，当我们从另一个线程使用get()方法访问该字段时，更改立即可见。这意味着该值已从CPU缓存刷新到所有CPU内核共用的内存层。

为了演示上述功能，我们创建一个最简单的生产者-消费者应用程序：

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

在控制台中，我们应该看到输出一系列“Set”和“Get”消息：

```text
Set: 3
Set: 4
Get: 4
Get: 5
```

能够证明内存缓存一致性的是“Get”语句中的值总是等于或大于它们上面的“Set”语句中的值。

这种行为虽然非常有用，但也会影响性能。如果我们可以在不需要缓存一致性的情况下这种情况，那就太好了。

## 4. lazySet()方法

lazySet()方法与set()方法相同，但没有缓存刷新。

换句话说，我们的更改只是最终对其他线程可见。这意味着从不同线程对更新后的AtomicReference调用get()可能会得到旧值。

要了解这一点，让我们在之前的应用程序中更改第一个线程的Runnable：

```text
for (int i = 0; i < 10; i++) {
    app.atomic.lazySet(i);
    System.out.println("Set: " + i);
    Thread.sleep(100);
}
```

运行程序后输出的“Set”和“Get”消息可能并不总是递增的：

```text
Set: 4
Set: 5
Get: 4
Get: 5
```

由于线程的性质，我们可能需要重复运行应用程序才能看到这种行为。
即使生产者线程已将AtomicInteger设置为5，消费者线程也会首先检索值4，这意味着当使用lazySet()时，系统最终是一致的。

用更专业的术语来说，我们说lazySet()方法不充当代码中的happens-before边缘，与其对应的set()方法相反。

## 5. 何时使用lazySet()

我们何时应该使用lazySet()尚不明确，因为它与set()的区别很细微。我们需要仔细分析这个问题，不仅要确保性能得到提升，还要确保在多线程环境中的正确性。

我们可以使用它的一种方法是，一旦不再需要对象引用，就用null替换它。
这样，我们就表明该对象有资格进行垃圾回收，而不会产生任何性能损失。我们假设其他线程可以使用已弃用的值，直到他们看到AtomicReference为空。

但是，一般来说，当我们想要对原子变量进行更改时，我们应该使用lazySet()，并且我们知道更改不需要立即对其他线程可见。

## 6. 总结

在本文中，我们了解了原子类的set()和lazySet()方法之间的区别。我们还了解了何时使用哪种方法。
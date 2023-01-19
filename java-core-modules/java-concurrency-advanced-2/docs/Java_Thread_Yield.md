## 1. 概述

在本教程中，我们将探讨Thread类中的yield()方法。

我们将把它与Java中可用的其他并发常用用法进行比较，并说明它的实际应用。

## 2. yield()介绍

根据官方文档所建议的，yield()提供了一种机制，通知“调度器”，
当前线程愿意放弃当前对处理器的使用，但希望尽快重新获得调度机会。

“调度器”可以自由地遵守或忽略这些信息，事实上，根据操作系统的不同，它有不同的行为。

```java
public class ThreadYield {
    public static void main(String[] args) {
        Runnable r = () -> {
            int counter = 0;
            while (counter < 2) {
                System.out.println(Thread.currentThread().getName());
                counter++;
                Thread.yield();
            }
        };
        new Thread(r).start();
        new Thread(r).start();
    }
}
```

当我们多次尝试运行上述程序时，我们可能会得到不同的结果；如下所示：

```text
Thread-0
Thread-0
Thread-1
Thread-1
```

```text
Thread-0
Thread-1
Thread-1
Thread-0
```

因此，正如你所看到的，yield()的行为是不确定的，并且依赖于平台。

## 3. 与其他的方法使用相比

还有其他影响线程执行的结构。
它们包括Object类中的wait()、notify()和notifyAll()、以及Thread类中的join()和sleep()。

### 3.1 yield()与wait()

+ yield()是在当前线程的上下文中调用的，而wait()只能在同步块或方法中显式获取的锁上调用。
+ 与yield()不同，wait()可以在尝试再次调度线程之前指定等待的最短时间段。
+ 使用wait()，还可以通过调用相关锁对象上的notify()或notifyAll()随时唤醒线程。

### 3.2 yield()与sleep()

+ yield()只能试探性地尝试暂停当前线程的执行，但不能保证它将在何时重新调度，而sleep()可以强制调度程序将当前线程的执行暂停至少一段时间。

### 3.3 yield()与join()

+ 当前线程可以在任何其他线程上调用join()，这会使当前线程在继续之前等待其他线程结束。
+ 或者，它可以使用一个时间段作为参数，该参数指示当前线程在恢复之前应该等待的最长时间。

## 4. yield()的用法

如官方文档所说，很少有必要使用yield()，因此应该避免使用，除非根据其行为明确目标。

尽管如此，yield()的一些用途包括设计并发控制结构，在计算量大的程序中提高系统响应能力等。

然而，这些用法必须伴随着详细的分析和基准测试，以确保预期的结果。

## 5. 总结

在这篇简短的文章中，我们讨论了Thread类中的yield()方法，并通过代码片段了解了它的行为和限制。

我们还探讨了它与Java中可用的其他并发用法的比较，最后说明了yield()可能有用的一些用例。
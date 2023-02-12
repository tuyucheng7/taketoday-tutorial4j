## 1. 概述

虽然多线程有助于提高应用程序的性能，但它也带来了一些问题。在本教程中，我们将在Java示例的帮助下研究两个这样的问题，[死锁和活锁](https://www.baeldung.com/cs/deadlock-livelock-starvation)。

## 2. 死锁

### 2.1 什么是死锁？

**当两个或多个线程永远等待另一个线程持有的锁或资源时，就会发生[死锁](https://www.baeldung.com/cs/os-deadlock)**。因此，应用程序可能会停止或失败，因为死锁线程无法继续运行。

经典的[哲学家进餐问题](https://www.baeldung.com/java-dining-philoshophers)很好地演示了多线程环境中的同步问题，并且经常被用作死锁的示例。

### 2.2 死锁示例

首先，让我们看一个简单的Java示例来理解死锁。

在这个例子中，我们将创建两个线程T1和T2，线程T1调用operation1，线程T2调用operation2。

为了完成它们的操作，线程T1需要先获取lock1然后再获取lock2，而线程T2需要先获取lock2然后再获取lock1。因此，基本上，两个线程都试图以相反的顺序获取锁。

现在，让我们编写DeadlockExample类：

```java
public class DeadlockExample {
    private final Lock lock1 = new ReentrantLock(true);
    private final Lock lock2 = new ReentrantLock(true);

    public static void main(String[] args) {
        DeadlockExample deadlock = new DeadlockExample();
        new Thread(deadlock::operation1, "T1").start();
        new Thread(deadlock::operation2, "T2").start();
    }

    public void operation1() {
        lock1.lock();
        print("lock1 acquired, waiting to acquire lock2.");
        sleep(50);

        lock2.lock();
        print("lock2 acquired");

        print("executing first operation.");

        lock2.unlock();
        lock1.unlock();
    }

    public void operation2() {
        lock2.lock();
        print("lock2 acquired, waiting to acquire lock1.");
        sleep(50);

        lock1.lock();
        print("lock1 acquired");

        print("executing second operation.");

        lock1.unlock();
        lock2.unlock();
    }

    public void print(String message) {
        System.out.println("Thread " + Thread.currentThread().getName() + ": " + message);
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

现在让我们运行这个死锁示例并注意输出：

```shell
Thread T1: lock1 acquired, waiting to acquire lock2.
Thread T2: lock2 acquired, waiting to acquire lock1.
```

一旦我们运行该程序，我们可以看到该程序导致死锁并且永远不会退出。日志显示线程T1正在等待由线程T2持有的lock2，同样，线程T2正在等待由线程T1持有的lock1。

### 2.3 避免死锁

死锁是Java中常见的并发问题。因此，我们应该设计一个Java应用程序来避免任何潜在的死锁情况。

首先，我们应该避免为一个线程获取多个锁的需要。但是，如果一个线程确实需要多个锁，我们应该确保每个线程以相同的顺序获取锁，以**避免锁获取中的任何循环依赖**。

我们还可以使用**定时锁尝试**，例如[Lock](https://www.baeldung.com/java-concurrent-locks)接口中的tryLock方法，以确保线程在无法获取锁时不会无限阻塞。

## 3. 活锁

### 3.1 什么是活锁？

活锁是另一个并发问题，类似于死锁。在活锁中，**两个或多个线程不断在彼此之间传输状态**，而不是像我们在死锁示例中看到的那样无限等待。因此，线程无法执行它们各自的任务。

活锁的一个很好的例子是消息传递系统，当发生异常时，消息消费者回滚事务并将消息放回队列的头部。然后从队列中重复读取相同的消息，只会导致另一个异常并被放回队列中，消费者永远不会从队列中获取任何其他消息。

### 3.2 活锁示例

现在，为了演示活锁条件，我们将采用我们之前讨论过的相同死锁示例。同样在此示例中，线程T1调用operation1，线程T2调用operation2。但是，我们将稍微改变这些操作的逻辑。

两个线程都需要两个锁来完成它们的工作。每个线程都获得了它的第一个锁，但发现第二个锁不可用。因此，为了让另一个线程先完成，每个线程释放它的第一个锁并尝试再次获取这两个锁。

让我们用LiveLockExample类来演示活锁：

```java
public class LiveLockExample {
    private final Lock lock1 = new ReentrantLock(true);
    private final Lock lock2 = new ReentrantLock(true);

    public static void main(String[] args) {
        LiveLockExample liveLock = new LiveLockExample();
        new Thread(liveLock::operation1, "T1").start();
        new Thread(liveLock::operation2, "T2").start();
    }

    public void operation1() {
        while (true) {
            tryLock(lock1, 50);
            print("lock1 acquired, trying to acquire lock2.");
            sleep(50);

            if (tryLock(lock2)) {
                print("lock2 acquired.");
            } else {
                print("cannot acquire lock2, releasing lock1.");
                lock1.unlock();
                continue;
            }

            print("executing first operation.");
            break;
        }
        lock2.unlock();
        lock1.unlock();
    }

    public void operation2() {
        while (true) {
            tryLock(lock2, 50);
            print("lock2 acquired, trying to acquire lock1.");
            sleep(50);

            if (tryLock(lock1)) {
                print("lock1 acquired.");
            } else {
                print("cannot acquire lock1, releasing lock2.");
                lock2.unlock();
                continue;
            }

            print("executing second operation.");
            break;
        }
        lock1.unlock();
        lock2.unlock();
    }

    public void print(String message) {
        System.out.println("Thread " + Thread.currentThread().getName() + ": " + message);
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void tryLock(Lock lock, long millis) {
        try {
            lock.tryLock(millis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean tryLock(Lock lock) {
        return lock.tryLock();
    }
}
```

现在，让我们运行这个例子：

```shell
Thread T2: lock2 acquired, trying to acquire lock1.
Thread T1: lock1 acquired, trying to acquire lock2.
Thread T2: cannot acquire lock1, releasing lock2.
Thread T2: lock2 acquired, trying to acquire lock1.
Thread T1: cannot acquire lock2, releasing lock1.
Thread T1: lock1 acquired, trying to acquire lock2.
Thread T2: cannot acquire lock1, releasing lock2.
Thread T2: lock2 acquired, trying to acquire lock1.
Thread T1: cannot acquire lock2, releasing lock1.
Thread T1: lock1 acquired, trying to acquire lock2.
... 
```

正如我们在日志中看到的，两个线程都在重复获取和释放锁。因此，没有一个线程能够完成操作。

### 3.3 避免活锁

为了避免活锁，我们需要知道导致活锁的条件，然后提出相应的解决方案。

例如，如果我们有两个线程在重复获取和释放锁，导致活锁，我们可以设计代码，让线程以随机间隔重试获取锁。这将为线程提供获得所需锁的公平机会。

在我们之前讨论的消息传递系统示例中，解决活性问题的另一种方法是将失败的消息放在[单独的队列](https://www.baeldung.com/spring-amqp-error-handling#dead-letter-queue)中进行进一步处理，而不是将它们再次放回同一个队列中。

## 4. 总结

在本教程中，我们讨论了死锁和活锁。此外，我们还研究了Java示例来演示这些问题中的每一个，并简要介绍了如何避免这些问题。
## 1. 概述

在本文中，我们将学习在Java中使用同步块。

简单地说，在多线程环境中，当两个或多个线程试图同时更新可变共享数据时，就会出现[争用条件](https://www.baeldung.com/cs/race-conditions)。Java提供了一种机制，通过同步线程对共享数据的访问来避免争用条件。

标记为synchronized的一段逻辑代码称为synchronized块，**在任何给定时间只允许一个线程执行**。

## 2. 为什么要同步？

让我们考虑一个典型的争用条件，我们计算总和，多个线程执行calculate()方法：

```java
public class SynchronizedMethods {

    private int sum = 0;

    public void calculate() {
        setSum(getSum() + 1);
    }

    // standard setters and getters
}
```

然后让我们编写一个简单的测试：

```java
@Test
@Disabled
void givenMultiThread_whenNonSyncMethod() throws InterruptedException {
    ExecutorService service = Executors.newFixedThreadPool(3);
    TuyuchengSynchronizedMethods method = new TuyuchengSynchronizedMethods();

    IntStream.range(0, 1000)
        .forEach(count -> service.submit(method::calculate));
    service.awaitTermination(100, TimeUnit.MILLISECONDS);

    assertEquals(1000, method.getSum());
}
```

我们使用一个带有3个线程的线程池ExecutorService来执行calculate()方法1000次。

如果我们串行执行，预期的输出将是1000，**但我们的多线程执行几乎每次都会失败，实际输出不一致**：

```shell
org.opentest4j.AssertionFailedError: 
Expected :1000
Actual   :985
```

当然，我们并不觉得这个结果出乎意料。

避免争用条件的一个简单方法是使用synchronized关键字使操作线程安全。

## 3. synchronized关键字

我们可以在不同级别上使用synchronized关键字：

+ 实例方法
+ 静态方法
+ 代码块

当我们使用同步块时，Java在内部使用监视器(也称为监视器锁或内部锁)来提供同步。这些监视器绑定到对象上；因此，同一对象的所有同步块只能有一个线程同时执行它们。

### 3.1 同步实例方法

我们可以在方法声明中添加synchronized关键字使方法同步：

```java
public synchronized void synchronisedCalculate() {
    setSum(getSum() + 1);
}
```

请注意，一旦我们同步了该方法，测试用例就会通过，实际输出为1000：

```java
@Test
void givenMultiThread_whenMethodSync() throws InterruptedException {
    ExecutorService service = Executors.newFixedThreadPool(3);
    TuyuchengSynchronizedMethods method = new TuyuchengSynchronizedMethods();

    IntStream.range(0, 1000)
        .forEach(count -> service.submit(method::synchronisedCalculate));
    service.awaitTermination(100, TimeUnit.MILLISECONDS);

    assertEquals(1000, method.getSyncSum());
}
```

实例方法在拥有该方法的类的实例上同步，这意味着该类的每个实例只有一个线程可以执行该方法。

### 3.2 同步静态方法

静态方法就像实例方法一样同步：

```java
public static synchronized void syncStaticCalculate() {
    staticSum = staticSum + 1;
}
```

这些方法在与该类关联的Class对象上同步。由于每个JVM的每个类只存在一个Class对象，因此每个类的静态同步方法内部只能有一个线程执行，而不管它具有多少个实例。

让我们测试一下：

```java
@Test
void givenMultiThread_whenStaticSyncMethod() throws InterruptedException {
    ExecutorService service = Executors.newCachedThreadPool();

    IntStream.range(0, 1000)
        .forEach(count -> service.submit(TuyuchengSynchronizedMethods::syncStaticCalculate));
    service.awaitTermination(100, TimeUnit.MILLISECONDS);

    assertEquals(1000, TuyuchengSynchronizedMethods.staticSum);
}
```

### 3.3 方法中的同步块

有时我们不想同步整个方法，只想同步其中的一些代码。我们可以通过将synchronized应用于代码块来实现这一点：

```java
public void performSynchronisedTask() {
    synchronized (this) {
        setCount(getCount()+1);
    }
}
```

然后我们可以测试更改：

```java
@Test
void givenMultiThread_whenBlockSync() throws InterruptedException {
    ExecutorService service = Executors.newFixedThreadPool(3);
    TuyuchengSynchronizedBlocks synchronizedBlocks = new TuyuchengSynchronizedBlocks();

    IntStream.range(0, 1000)
        .forEach(count -> service.submit(synchronizedBlocks::performSynchronisedTask));
    service.awaitTermination(500, TimeUnit.MILLISECONDS);

    assertEquals(1000, synchronizedBlocks.getCount());
}
```

请注意，我们将this参数传递给synchronized块。这是监视器对象。同步块内的代码在监视器对象上同步。简单地说，每个监视器对象只有一个线程可以在该代码块内执行。

如果该方法是静态的，我们将传递类的Class对象来代替this对象引用，并且该Class对象将是同步块的监视器：

```java
public static void performStaticSyncTask(){
    synchronized (SynchronisedBlocks.class) {
        setStaticCount(getStaticCount() + 1);
    }
}
```

让我们测试静态方法中的块：

```java
@Test
void givenMultiThread_whenStaticSyncBlock() throws InterruptedException {
    ExecutorService service = Executors.newCachedThreadPool();

    IntStream.range(0, 1000)
        .forEach(count -> service.submit(TuyuchengSynchronizedBlocks::performStaticSyncTask));
    service.awaitTermination(500, TimeUnit.MILLISECONDS);

    assertEquals(1000, TuyuchengSynchronizedBlocks.getStaticCount());
}
```

### 3.4. 可重入性

**同步方法和同步块背后的锁是可重入的**。这意味着当前线程可以在持有同步锁的同时一遍又一遍地获取相同的同步锁：

```java
@Test
void givenHoldingTheLock_whenReentrant_thenCanAcquireItAgain() {
    Object lock = new Object();
    synchronized (lock) {
        System.out.println("First time acquiring it");

        synchronized (lock) {
            System.out.println("Entering again");

            synchronized (lock) {
                System.out.println("And again");
            }
        }
    }
}
```

如上所示，在同步块中，我们可以重复获取同一个监视器锁。

## 4. 总结

在这篇简短的文章中，我们探讨了使用synchronized关键字实现线程同步的不同方法。

我们还了解了争用条件如何影响我们的应用程序以及同步如何帮助我们避免这种情况。有关在Java中使用锁的线程安全的更多信息，请参阅我们的[java.util.concurrent.Locks](https://www.baeldung.com/java-concurrent-locks)文章。
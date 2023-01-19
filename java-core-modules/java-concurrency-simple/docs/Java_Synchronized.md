## 1. 概述

本教程将介绍如何在Java中使用同步块。

简单地说，在多线程环境中，当两个或多个线程试图同时更新可变共享数据时，就会出现争用情况。
Java提供了一种机制，通过同步线程对共享数据的访问来避免竞争条件。

标记为synchronized的一段逻辑代码称为synchronized块，在任何给定时间只允许一个线程执行。

## 2. 为什么要同步？

让我们考虑一个典型的线程竞争，其中我们计算总和，多个线程执行calculate()方法：

```java
public class TuyuchengSynchronizedMethods {
    private int sum = 0;

    void calculate() {
        setSum(getSum() + 1);
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
```

然后，让我们编写一个简单的测试：

```java
public class TuyuchengSynchronizeMethodsUnitTest {

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
}
```

我们使用一个带3个线程的线程池ExecutorService来执行calculate()1000次。

如果我们按顺序执行，预期的输出将是1000，但我们的多线程执行几乎每次都会失败，实际输出不一致：

```
org.opentest4j.AssertionFailedError: 
Expected :1000
Actual   :985
```

当然，我们并不觉得这个结果出人意料。

避免竞争条件的一种简单方法是使用synchronized关键字使操作线程安全。

## 3. synchronized关键字

我们可以在不同级别上使用synchronized关键字：

+ 实例方法
+ 静态方法
+ 代码块

当我们使用同步块时，Java在内部使用监视器(也称为监视器锁或内部锁)来提供同步。
这些监视器被绑定到一个对象上；因此，同一对象的所有同步块只能有一个线程同时执行它们。

### 3.1 同步实例方法

我们可以在方法声明中添加synchronized关键字以使方法同步：

```java
public class TuyuchengSynchronizedMethods {
    private int sum = 0;

    public synchronized void synchronisedCalculate() {
        setSum(getSum() + 1);
    }
}
```

请注意，一旦我们同步了该方法，测试用例就会通过，实际输出为1000：

```java
public class TuyuchengSynchronizeMethodsUnitTest {

    @Test
    void givenMultiThread_whenMethodSync() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        TuyuchengSynchronizedMethods method = new TuyuchengSynchronizedMethods();

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(method::synchronisedCalculate));
        service.awaitTermination(100, TimeUnit.MILLISECONDS);

        assertEquals(1000, method.getSyncSum());
    }
}
```

实例方法在拥有该方法的类的实例上同步，这意味着该类的每个实例只有一个线程可以执行该方法。

### 3.2 同步静态方法

静态方法与实例方法一样可以添加synchronized关键字：

```java
public class TuyuchengSynchronizedMethods {
    static int staticSum = 0;

    static synchronized void syncStaticCalculate() {
        staticSum = staticSum + 1;
    }
}
```

这些方法在与类关联的Class对象上同步。由于每个JVM的每个类只存在一个Class对象，因此每个类的静态同步方法内部只能执行一个线程，而不管它有多少实例。

让我们测试一下：

```java
public class TuyuchengSynchronizeMethodsUnitTest {

    @Test
    void givenMultiThread_whenStaticSyncMethod() throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(TuyuchengSynchronizedMethods::syncStaticCalculate));
        service.awaitTermination(100, TimeUnit.MILLISECONDS);

        assertEquals(1000, TuyuchengSynchronizedMethods.staticSum);
    }
}
```

## 3.3 方法中的同步块

有时我们不想同步整个方法，只想同步其中的一些代码。我们可以通过将synchronized应用于代码块来实现这一点：

```java
public class TuyuchengSynchronizedBlocks {
    private int count = 0;

    void performSynchronisedTask() {
        synchronized (this) {
            setCount(getCount() + 1);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
```

```java
public class TuyuchengSynchronizedBlockUnitTest {

    @Test
    void givenMultiThread_whenBlockSync() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        TuyuchengSynchronizedBlocks synchronizedBlocks = new TuyuchengSynchronizedBlocks();

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(synchronizedBlocks::performSynchronisedTask));
        service.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertEquals(1000, synchronizedBlocks.getCount());
    }
}
```

注意，我们将this参数传递给了synchronized块。这是监视器对象。同步块内的代码在监视器对象上同步。
简单地说，每个监视器对象只有一个线程可以在该代码块内执行。

如果该方法是静态的，我们将传递类的Class对象来代替this对象引用，并且该Class对象将是同步块的监视器：

```java
public class TuyuchengSynchronizedBlocks {
    private static int staticCount = 0;

    static void performStaticSyncTask() {
        synchronized (TuyuchengSynchronizedBlocks.class) {
            setStaticCount(getStaticCount() + 1);
        }
    }

    static int getStaticCount() {
        return staticCount;
    }

    private static void setStaticCount(int staticCount) {
        TuyuchengSynchronizedBlocks.staticCount = staticCount;
    }
}
```

```java
public class TuyuchengSynchronizedBlockUnitTest {

    @Test
    void givenMultiThread_whenStaticSyncBlock() throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(TuyuchengSynchronizedBlocks::performStaticSyncTask));
        service.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertEquals(1000, TuyuchengSynchronizedBlocks.getStaticCount());
    }
}
```

### 3.4. 可重入性

同步方法和同步块背后的锁是可重入的。这意味着当前线程可以在保持同步锁的同时一次又一次地获取相同的同步锁：

```java
public class TuyuchengSynchronizedBlockUnitTest {

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
}
```

如上所示，当我们处于同步块中时，我们可以重复获取相同的监视器锁。

## 4. 总结

在这篇简短的文章中，我们探讨了使用synchronized关键字实现线程同步的不同方法。

我们还了解了竞争条件如何影响我们的应用程序，以及同步如何帮助我们避免这种情况。
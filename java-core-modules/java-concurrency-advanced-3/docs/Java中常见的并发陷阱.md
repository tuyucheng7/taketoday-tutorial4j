## 1. 概述

在本教程中，我们将看到Java中一些[最常见的并发问题](https://www.baeldung.com/cs/aba-concurrency)。我们还将学习如何避免它们及其主要原因。

## 2. 使用线程安全对象

### 2.1 共享对象

线程主要通过[共享对相同对象的访问](https://www.baeldung.com/java-thread-safety)来进行通信，因此，在对象发生变化时读取它可能会产生意想不到的结果。此外，同时更改一个对象可能会使它处于损坏或不一致的状态。

**我们可以避免此类并发问题并构建可靠代码的主要方法是使用[不可变对象](https://www.baeldung.com/java-immutable-object)**，这是因为它们的状态不能被多个线程的干扰修改。

但是，我们并不能总是使用不可变对象。在这些情况下，我们必须找到使我们的可变对象线程安全的方法。

### 2.2 使集合线程安全

与任何其他对象一样，集合在内部维护状态。这些状态可以通过多个线程同时更改集合来改变。因此，**我们可以在多线程环境中安全地使用集合的一种方法是[同步它们](https://www.baeldung.com/java-synchronized-collections)**：

```java
Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
List<Integer> list = Collections.synchronizedList(new ArrayList<>());
```

一般来说，同步有助于我们实现互斥。更具体地说，**这些集合一次只能由一个线程访问**。因此，我们可以避免使集合处于不一致的状态。

### 2.3 特殊的多线程集合

现在，让我们考虑这样一个场景：即读操作远远多于写操作。**如果使用同步集合，我们的应用程序可能会受到严重的性能影响**。如果两个线程想同时读取集合，则其中一个线程必须等待另一个线程完成。

为此，Java提供了可由多个线程同时访问的并发集合，如[CopyOnWriteArrayList](https://www.baeldung.com/java-copy-on-write-arraylist)和[ConcurrentHashMap](https://www.baeldung.com/java-concurrent-map)：

```java
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
Map<String, String> map = new ConcurrentHashMap<>();
```

CopyOnWriteArrayList通过为添加或删除等可变操作创建底层数组的单独副本来实现线程安全。尽管它的写操作性能比Collections.synchronizedList差，但当我们需要比写操作多得多的读操作时，它为我们提供了更好的性能。

ConcurrentHashMap基本上是线程安全的，并且比围绕非线程安全Map的Collections.synchronizedMap包装器更高效。它实际上是线程安全Map的线程安全Map，允许不同的操作在其子Map中同时发生。

### 2.4 使用非线程安全类型

我们经常会使用像[SimpleDataFormat](https://www.baeldung.com/java-simple-date-format)之类的内置对象来解析和格式化日期对象。SimpleDataFormat类在执行操作时会改变其内部状态。

我们需要非常小心地使用它们，因为它们不是线程安全的。**由于争用条件等原因，它们的状态在多线程应用程序中可能会变得不一致**。

那么，我们如何才能安全地使用SimpleDataFormat呢？我们有几种选择：

+ 每次使用时创建一个新的SimpleDateFormat实例
+ 限制使用ThreadLocal<SimpleDateFormat\>对象创建的对象数量，它保证每个线程都有自己的SimpleDataFormat实例
+ 使用synchronized关键字或锁同步多个线程的并发访问

SimpleDataFormat只是其中的一个例子，我们可以将这些技术用于任何非线程安全类型。

## 3. 争用条件

**当两个或多个线程访问共享数据并试图同时更改数据时，就会出现[争用情况](https://www.baeldung.com/cs/race-conditions)**。因此，争用条件可能会导致运行时错误或意外结果。

### 3.1 争用条件示例

让我们考虑以下代码：

```java
class Counter {
    private int counter = 0;

    public void increment() {
        counter++;
    }

    public int getValue() {
        return counter;
    }
}
```

Counter类的设计使得每次调用increment方法都会将counter加1。但是，如果一个Counter对象被多个线程引用，那么线程之间的干扰可能会阻止这种情况按预期发生。

我们可以将counter++语句分解为3个步骤：

+ 检索counter的当前值
+ 将检索到的值递增1
+ 将递增后的值存储回counter

现在，假设两个线程thread1和thread2同时调用increment()方法，它们的交错执行可能遵循以下顺序：

+ thread1检索counter的当前值：0
+ thread2检索counter的当前值：0
+ thread1递增检索到的值：结果是1
+ thread2递增检索到的值：结果是1
+ thread1将结果存储在counter中：结果现在是1
+ thread2将结果存储在counter中：结果现在是1

我们期望counter的值为2，但它最终是1。

### 3.2 基于同步的解决方案

我们可以通过同步临界代码来解决不一致问题：

```java
class SynchronizedCounter {
    private int counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public synchronized int getValue() {
        return counter;
    }
}
```

在任何时候，只允许一个线程访问对象的同步方法，因此这会强制counter读写的一致性。

### 3.3 内置解决方案

我们可以用JDK内置的AtomicInteger对象替换上面的代码。这个类提供了递增整数类型的的原子方法，并且是比编写我们自己的代码更好的解决方案。因此，我们可以直接调用它的方法而无需同步：

```java
AtomicInteger atomicInteger = new AtomicInteger(3);
atomicInteger.incrementAndGet();
```

在这种情况下，SDK为我们解决了问题。否则，我们也可以编写自己的代码，将临界区代码封装在自定义的线程安全类中。这种方法有助于我们将代码的复杂性降至最低并最大限度地提高代码的可重用性。

## 4. 集合中的争用条件

### 4.1 问题所在

我们可能会陷入的另一个陷阱是认为同步集合为我们提供了比实际情况更多的保护。

让我们检查下面的代码：

```java
private void putIfAbsentList_NonAtomicOperation_ProneToConcurrencyIssues() {
    List<String> list = Collections.synchronizedList(new ArrayList<>());
    if (!list.contains("foo")) {
        list.add("foo");
    }
}
```

**我们集合的每个操作都是同步的，但是多个方法调用的任何组合都不是同步的**。更具体地说，在这两个操作之间，另一个线程可以修改我们的集合，从而导致不期望的结果。

例如，两个线程可以同时进入if块，然后更新集合，每个线程将foo值添加到集合中。

### 4.2 集合的解决方案

我们可以使用同步来保护代码不被多个线程同时访问：

```java
private void putIfAbsentList_AtomicOperation_ThreadSafe() {
    List<String> list = Collections.synchronizedList(new ArrayList<>());
    synchronized (list) {
        if (!list.contains("foo")) {
            list.add("foo");
        }
    }
}
```

我们没有在方法中添加synchronized关键字，而是创建了一个关于list的临界区，它一次只允许一个线程执行此操作。

我们应该注意，我们可以对list对象上的其他操作使用synchronized(list)，以保证**一次只有一个线程可以对该对象执行任何操作**。

### 4.3 ConcurrentHashMap的内置解决方案

现在，让我们考虑出于同样的原因使用Map，即仅在条目不存在时才添加它。

ConcurrentHashMap为此类问题提供了更好的解决方案，我们可以使用它的原子方法putIfAbsent()：

```java
Map<String, String> map = new ConcurrentHashMap<>();
map.putIfAbsent("foo", "bar");
```

或者，如果我们想计算值，可以使用原子方法computeIfAbsent()：

```java
map.computeIfAbsent("foo", key -> key + "bar");
```

我们应该注意，这些方法是Map接口的一部分，它们提供了一种方便的方法来避免围绕添加操作编写条件逻辑。在尝试进行多线程原子调用时，它们确实为我们提供了帮助。

## 5. 内存一致性问题

当多个线程对应该是相同数据的内容具有不一致的视图时，就会出现内存一致性问题。

除了主内存之外，大多数现代计算机体系结构都使用缓存层次结构(L1、L2和L3缓存)来提高总体性能。**因此，任何线程都可以缓存变量，因为与主内存相比，它提供了更快的访问速度**。

### 5.1 问题描述

让我们回顾一下我们的Counter类：

```java
class Counter {
    private int counter = 0;

    public void increment() {
        counter++;
    }

    public int getValue() {
        return counter;
    }
}
```

让我们考虑以下场景：其中thread1递增counter变量，然后thread2读取其值。可能会发生以下事件序列：

+ thread1从自己的缓存中读取counter值：counter为0
+ thread1递增counter并将其写回自己的缓存：counter为1
+ thread2从自己的缓存中读取counter值：counter为0

当然，预期的事件序列也可能发生，thread2将读取正确的值(1)，**但不能保证一个线程所做的更改每次都对其他线程都是可见的**。

### 5.2 解决方案

为了避免内存一致性错误，**我们需要建立一个happens-before的关系**。这种关系只是保证一个特定语句的内存更新对另一个特定语句是可见的。

有几种策略可以创建happens-before关系。其中之一是同步，我们已经看过了。

**同步可确保互斥性和内存一致性**。但是，这会带来性能成本。

我们还可以通过使用volatile关键字来避免内存一致性问题。简而言之，**对volatile变量的每次更改对其他线程始终可见**。

让我们使用volatile重写我们的Counter示例：

```java
class SynchronizedVolatileCounter {
    private volatile int counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public int getValue() {
        return counter;
    }
}
```

我们应该注意，**我们仍然需要同步increment()操作，因为volatile关键字不能确保我们互斥**。使用简单的原子变量访问比通过同步代码访问这些变量更有效。

### 5.3 非原子long值和double值

因此，如果我们在没有正确同步的情况下读取变量，我们可能会看到一个过期的值。**对于long值和double值，非常令人惊讶的是，除了过期的值之外，甚至可能看到完全随机的值**。

**根据[JLS-17](https://docs.oracle.com/javase/specs/jls/se14/html/jls-17.html#jls-17.7)，JVM可以将64位操作视为两个独立的32位操作**。因此，在读取long值或double值时，可能读取更新的32位以及过期的32位。因此，我们可能会在并发上下文中观察到随机的long值或double值。

另一方面，volatile类型的long和double值的写入和读取始终是原子的。

## 6. 滥用同步

[同步机制](https://www.baeldung.com/java-thread-safety)是实现线程安全的有力工具，它依赖于内部锁和外部锁的使用。让我们还记住这样一个事实，即每个对象都有不同的锁，并且一次只有一个线程可以获取锁。

但是，如果我们不注意并仔细为临界区代码选择正确的锁，则可能会发生意外行为。

### 6.1 在this引用上同步

方法级同步是许多并发问题的解决方案。但是，如果过度使用，也可能导致其他并发问题。这种同步方法依赖于this引用作为锁，也称为内部锁。

我们可以在以下示例中看到如何将方法级同步转换为块级同步，并将this引用作为锁。

这些方法是等价的：

```java
public synchronized void foo() {
    // ...
}
```

```java
public void foo() {
    synchronized(this) {
        // ...
    }
}
```

当线程调用此类方法时，其他线程无法同时访问该对象。这可能会降低并发性能，因为所有内容最终都以单线程运行。当对象的读取频率高于更新频率时，这种方法尤其糟糕。

此外，我们代码的客户端也可能获取this锁。在最坏的情况下，此操作可能会导致死锁。

### 6.2 死锁

[死锁](https://www.baeldung.com/java-dining-philoshophers)描述了两个或多个线程相互阻塞的情况，每个线程都在等待获取由其他线程持有的资源。

让我们考虑一下这个例子：

```java
public class DeadlockExample {
    public static Object lock1 = new Object();
    public static Object lock2 = new Object();

    public static void main(String[] args) {
        Thread threadA = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("ThreadA: Holding lock 1...");
                sleep();
                System.out.println("ThreadA: Waiting for lock 2...");

                synchronized (lock2) {
                    System.out.println("ThreadA: Holding lock 1 & 2...");
                }
            }
        });
        Thread threadB = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("ThreadB: Holding lock 2...");
                sleep();
                System.out.println("ThreadB: Waiting for lock 1...");

                synchronized (lock1) {
                    System.out.println("ThreadB: Holding lock 1 & 2...");
                }
            }
        });
        threadA.start();
        threadB.start();
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}
```

在上面的代码中，我们可以清楚地看到首先threadA获取lock1，threadB获取lock2。然后，threadA尝试获取threadB已经获取的lock2，threadB尝试获取threadA已经获取的lock1。因此，他们都不会继续执行，这意味着他们陷入了僵局。

我们可以通过更改其中一个线程中获取锁的顺序来解决此问题。

请注意，这只是一个示例，还有许多其他示例可能导致死锁。

## 7. 总结

在本文中，我们探讨了在多线程应用程序中可能遇到的几个并发问题示例。

首先，我们了解到我们应该选择不可变或线程安全的对象或操作。

然后，我们看到了几个争用条件的示例，以及如何使用同步机制避免它们。此外，我们还了解了与内存相关的争用条件以及如何避免它们。

虽然同步机制可以帮助我们避免许多并发问题，但我们很容易滥用它并产生其他问题。出于这个原因，我们研究了当这种机制使用不当时可能面临的几个问题。
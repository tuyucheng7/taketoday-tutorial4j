## 1. 概述

在本文中，我们将了解如何获得对特定键的锁定以防止对该键的并发操作而不会妨碍对其他键的操作。

一般来说，我们需要实现两种方法并了解如何操作它们：

+ void lock(String key)
+ void unlock(String key)

为了本教程的简单性，我们始终假设我们的键是字符串。在正确定义equals和hashCode方法的唯一条件下，你可以将它们替换为你需要的对象类型，因为我们将它们用作[HashMap](https://www.baeldung.com/java-hashmap)的键。

## 2. 一种简单的互斥锁

首先，假设我们想要在相应的键已被使用时阻塞任何请求的操作。在这里，我们定义一个boolean tryLock(String key)方法，而不是我们想象中的lock方法。

具体来说，我们的目标是维护一组键，我们将随时使用正在使用的密钥键这些键。因此，当请求对某个键执行新操作时，如果我们发现该键已被另一个线程使用，我们将不得不拒绝它。

我们在这里面临的问题是Set没有线程安全的实现。因此，我们将使用由[ConcurrentHashMap](https://www.baeldung.com/java-concurrent-map)支持的Set。使用ConcurrentHashMap可以保证我们在多线程环境中的数据一致性。

让我们看看实际效果：

```java
public class SimpleExclusiveLockByKey {
    public static final Set<String> usedKeys = ConcurrentHashMap.newKeySet();

    public boolean tryLock(String key) {
        return usedKeys.add(key);
    }

    public void unlock(String key) {
        usedKeys.remove(key);
    }
}
```

以下是我们如何使用这个类：

```java
String key = "key";
SimpleExclusiveLockByKey lockByKey = new SimpleExclusiveLockByKey();
try {
    lockByKey.tryLock(key);
    // insert the code that needs to be executed only if the key lock is available
} finally { // CRUCIAL
    lockByKey.unlock(key);
}
```

**在[finally](https://www.baeldung.com/java-finally-keyword)块中调用unlock方法至关重要**。这样，即使我们的代码在try括号内抛出异常，我们也会解锁键。

## 3. 通过键获取和释放锁

现在，让我们进一步深入研究这个问题，我们不想简单地拒绝对相同键的同时操作，但我们宁愿让新的传入操作等到键上的当前操作完成。

应用程序流程将是：

+ 第一个线程请求一个键上的锁：它获取键上的锁
+ 第二个线程请求锁定同一个键：线程2被告知等待
+ 第一个线程释放键上的锁
+ 第二个线程获得键上的锁并可以执行其操作

### 3.1 使用线程计数器定义锁

在这种情况下，我们自然会想到使用[Lock](https://www.baeldung.com/java-concurrent-locks)。简而言之，Lock是一个用于线程同步的对象，它允许阻塞线程，直到可以获取锁。Lock是一个接口-我们将使用它的基本实现ReentrantLock。

让我们首先将Lock包装在一个内部类中。类将能够跟踪当前等待锁定键的线程数。它将公开两个方法，一个用于递增线程计数器，另一个用于递减线程计数器：

```java
private static class LockWrapper {
    private final Lock lock = new ReentrantLock();
    private final AtomicInteger numberOfThreadsInQueue = new AtomicInteger(1);

    private LockWrapper addThreadInQueue() {
        numberOfThreadsInQueue.incrementAndGet();
        return this;
    }

    private int removeThreadFromQueue() {
        return numberOfThreadsInQueue.decrementAndGet();
    }
}
```

### 3.2 让锁处理排队线程

此外，我们将继续使用ConcurrentHashMap。但是，我们不会像以前那样简单地提取Map的键，而是使用LockWrapper对象作为值：

```java
private static ConcurrentHashMap<String, LockWrapper> locks = new ConcurrentHashMap<>();
```

当一个线程想要获取一个键上的锁时，我们需要查看这个键是否已经存在LockWrapper：

+ 如果不是，我们将为给定键实例化一个新的LockWrapper，并将计数器设置为1
+ 如果是，我们将返回现有的LockWrapper并自增其关联的计数器

让我们看看这是如何完成的：

```java
public void lock(String key) {
    LockWrapper lockWrapper = locks.compute(key, (k, v) -> v == null ? new LockWrapper() : v.addThreadInQueue());
    lockWrapper.lock.lock();
}
```

由于使用了HashMap的compute方法，代码非常简洁。让我们详细介绍一下此方法的功能：

+ compute方法应用于对象锁，以键作为其第一个参数：检索与locks中的键对应的初始值
+ 作为compute的第二个参数给出的[BiFunction](https://www.baeldung.com/java-bifunction-interface)应用于键和初始值：结果给出一个新值
+ 新值替换locks中与键对应的初始值。

### 3.3 解锁并选择性删除Map Entry

此外，当线程释放锁时，我们将减少与LockWrapper关联的线程数。如果计数降为零，那么我们将从ConcurrentHashMap中删除该键：

```java
public void unlock(String key) {
    LockWrapper lockWrapper = locks.get(key);
    lockWrapper.lock.unlock();
    if (lockWrapper.removeThreadFromQueue() == 0) {
        // NB : We pass in the specific value to remove to handle the case where another thread would queue right before the removal
        locks.remove(key, lockWrapper);
    }
}
```

### 3.4 汇总

简而言之，让我们看看我们整个类最终的样子：

```java
public class LockByKey {

    private static class LockWrapper {
        private final Lock lock = new ReentrantLock();
        private final AtomicInteger numberOfThreadsInQueue = new AtomicInteger(1);

        private LockWrapper addThreadInQueue() {
            numberOfThreadsInQueue.incrementAndGet();
            return this;
        }

        private int removeThreadFromQueue() {
            return numberOfThreadsInQueue.decrementAndGet();
        }
    }

    private static ConcurrentHashMap<String, LockWrapper> locks = new ConcurrentHashMap<>();

    public void lock(String key) {
        LockWrapper lockWrapper = locks.compute(key, (k, v) -> v == null ? new LockWrapper() : v.addThreadInQueue());
        lockWrapper.lock.lock();
    }

    public void unlock(String key) {
        LockWrapper lockWrapper = locks.get(key);
        lockWrapper.lock.unlock();
        if (lockWrapper.removeThreadFromQueue() == 0) {
            // NB : We pass in the specific value to remove to handle the case where another thread would queue right before the removal
            locks.remove(key, lockWrapper);
        }
    }
}
```

用法和我们之前的非常相似：

```java
String key = "key"; 
LockByKey lockByKey = new LockByKey(); 
try { 
    lockByKey.lock(key);
    // insert your code here 
} finally { // CRUCIAL 
    lockByKey.unlock(key); 
}
```

## 4. 允许同时执行多个操作

最后但同样重要的是，让我们考虑另一种情况：我们希望将允许同时对同一个键执行操作的线程数限制为某个整数n，而不是一次只允许一个线程对给定键执行操作。为了简单起见，我们将设置n=2。

让我们详细描述一下我们的用例：

+ 第一个线程想要获取键上的锁：将被允许这样做
+ 第二个线程想要获取相同的锁：它也将被允许
+ 第三个线程请求对同一个键的锁：它必须排队，直到前两个线程中的一个释放它的锁

信号量就是为此而生的，它用于限制同时访问资源的线程数的对象。

全局功能和代码看起来与锁非常相似：

```java
public class SimultaneousEntriesLockByKey {
    private static final int ALLOWED_THREADS = 2;

    private static final ConcurrentHashMap<String, Semaphore> semaphores = new ConcurrentHashMap<>();

    public void lock(String key) {
        Semaphore semaphore = semaphores.compute(key, (k, v) -> v == null ? new Semaphore(ALLOWED_THREADS) : v);
        semaphore.acquireUninterruptibly();
    }

    public void unlock(String key) {
        Semaphore semaphore = semaphores.get(key);
        semaphore.release();
        if (semaphore.availablePermits() == ALLOWED_THREADS) {
            semaphores.remove(key, semaphore);
        }
    }
}
```

用法是相同的：

```java
String key = "key"; 
SimultaneousEntriesLockByKey lockByKey = new SimultaneousEntriesLockByKey(); 
try { 
    lockByKey.lock(key); 
    // insert your code here 
} finally { // CRUCIAL 
    lockByKey.unlock(key); 
}
```

## 5. 总结

在本文中，我们了解了如何在键上加锁以完全阻止并发操作，或将并发操作的数量限制为一个(使用锁)或多个(使用信号量)。
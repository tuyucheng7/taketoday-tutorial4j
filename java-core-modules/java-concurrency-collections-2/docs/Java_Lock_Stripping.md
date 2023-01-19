## 1. 概述

在本教程中，我们学习如何实现细粒度同步，也称为“Lock Striping(锁条带化)”，这是一种在保持良好性能的同时处理对数据结构的并发访问的模式。

## 2. 问题

由于HashMap的非同步特性，它不是线程安全的数据结构。这意味着来自多线程环境的操作可能会导致数据不一致。

为了克服这个问题，我们可以使用Collections#synchronizedMap方法转换原始Map，也可以使用HashTable数据结构。两者都会返回Map接口的线程安全实现，但它们是以性能为代价的。

使用单个锁对象定义对数据结构的独占访问的方法称为粗粒度同步。在粗粒度同步实现中，对对象的每次访问都必须由一个线程一次完成，最终导致的是所有线程顺序访问。我们的目标是允许并发线程在数据结构上操作，同时确保线程安全。

## 3. Lock Striping

为了实现我们的目标，我们将使用Lock Striping模式。锁条带化是一种在多个存储桶或条带上发生锁定的技术，这意味着访问一个桶只锁定该桶，而不是锁定整个数据结构。

有两种方法可以做到这一点：

+ 首先，我们可以为每个任务使用一个锁，从而最大限度地提高任务之间的并发性，但这样会占用更多的内存。
+ 或者，我们可以对每个任务使用单个锁，这样可以减少内存的使用，但也会影响并发性能。

为了帮助我们管理这种性能-内存权衡，Guava提供了一个名为Striped的类。它类似于ConcurrentHashMap中的逻辑，但Striped类做了自己的增强，它使用信号量或可重入锁减少了不同任务的同步。

## 4. 快速示例

让我们通过一个简单的例子来帮助我们理解这种模式的好处。

我们比较HashMap与ConcurrentHashMap，以及单个锁与条带锁，从而进行四个实验。对于每个实验，我们将在底层Map上执行并发读取和写入。不同的是我们如何访问每个存储桶。

为此，我们将创建两个类：SingleLock和StripedLock。它们是抽象类ConcurrentAccessExperiment的具体实现。

### 4.1 依赖

由于我们会使用Guava的Striped类，因此我们需要添加guava依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

### 4.2 主流程

ConcurrentAccessExperiment类实现了前面描述的行为：

```java
public abstract class ConcurrentAccessExperiment {

    public final Map<String, String> doWork(Map<String, String> map, int threads, int slots) {
        CompletableFuture<?>[] requests = new CompletableFuture<?>[threads  slots];

        for (int i = 0; i < threads; i++) {
            requests[slots  i + 0] = CompletableFuture.supplyAsync(putSupplier(map, i));
            requests[slots  i + 1] = CompletableFuture.supplyAsync(getSupplier(map, i));
            requests[slots  i + 2] = CompletableFuture.supplyAsync(getSupplier(map, i));
            requests[slots  i + 3] = CompletableFuture.supplyAsync(getSupplier(map, i));
        }
        CompletableFuture.allOf(requests).join();

        return map;
    }

    protected abstract Supplier<?> putSupplier(Map<String, String> map, int key);

    protected abstract Supplier<?> getSupplier(Map<String, String> map, int key);
}
```

需要注意的是，由于我们的测试受CPU限制，所以我们将存储桶的数量限制为可用处理器的倍数。

### 4.3 使用ReentrantLock进行并发访问

现在我们将实现异步任务的方法。我们的SingleLock类使用ReentrantLock为整个数据结构定义了一个锁：

```java
public class SingleLock extends ConcurrentAccessExperiment {
    ReentrantLock lock;

    public SingleLock() {
        lock = new ReentrantLock();
    }

    protected Supplier<?> putSupplier(Map<String, String> map, int key) {
        return (() -> {
            lock.lock();
            try {
                return map.put("key" + key, "value" + key);
            } finally {
                lock.unlock();
            }
        });
    }

    protected Supplier<?> getSupplier(Map<String, String> map, int key) {
        return (() -> {
            lock.lock();
            try {
                return map.get("key" + key);
            } finally {
                lock.unlock();
            }
        });
    }
}
```

### 4.4 锁条带的并发访问

然后，StripedLock类为每个桶定义一个条带锁：

```java
public class StripedLock extends ConcurrentAccessExperiment {
    Striped<Lock> stripedLock;

    public StripedLock(int buckets) {
        stripedLock = Striped.lock(buckets);
    }

    protected Supplier<?> putSupplier(Map<String, String> map, int key) {
        return (() -> {
            int bucket = key % stripedLock.size();
            Lock lock = stripedLock.get(bucket);
            lock.lock();
            try {
                return map.put("key" + key, "value" + key);
            } finally {
                lock.unlock();
            }
        });
    }

    protected Supplier<?> getSupplier(Map<String, String> map, int key) {
        return (() -> {
            int bucket = key % stripedLock.size();
            Lock lock = stripedLock.get(bucket);
            lock.lock();
            try {
                return map.get("key" + key);
            } finally {
                lock.unlock();
            }
        });
    }
}
```

## 5. 测试结果

通过使用JMH(Java Microbenchmark Harness)运行我们的基准测试时，我们可以看到类似于以下内容(请注意，吞吐量越高越好)：

```text
Benchmark                                                Mode  Cnt  Score   Error   Units
ConcurrentAccessBenchmark.singleLockConcurrentHashMap   thrpt   10  0,059 ± 0,006  ops/ms
ConcurrentAccessBenchmark.singleLockHashMap             thrpt   10  0,061 ± 0,005  ops/ms
ConcurrentAccessBenchmark.stripedLockConcurrentHashMap  thrpt   10  0,065 ± 0,009  ops/ms
ConcurrentAccessBenchmark.stripedLockHashMap            thrpt   10  0,068 ± 0,008  ops/ms
```

## 6. 总结

在本教程中，我们探讨了如何在类似Map的结构中使用Lock Striping实现更好性能的不同方法。我们创建了一个基准测试来将结果与几个实现进行比较。

从我们的基准测试结果中，我们可以了解不同的并发策略如何显著影响整个过程。Striped Lock模式带来了相当大的改进，因为它在HashMap和ConcurrentHashMap中都获得了约10%的额外分数。
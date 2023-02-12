## 1. 概述

在本教程中，我们将看到**在Java中实现[互斥量](https://www.baeldung.com/cs/what-is-mutex)的不同方法**。

## 2. 互斥锁

在多线程应用程序中，两个或多个线程可能需要同时访问共享资源，从而导致意外行为。这种共享资源的例子有数据结构、输入输出设备、文件和网络连接。

我们称这种情况为竞争条件。并且，访问共享资源的程序部分称为临界区。**因此，为了避免竞争条件，我们需要同步对临界区的访问**。

互斥是最简单的同步器类型-它**确保一次只有一个线程可以执行计算机程序的临界区**。

要访问临界区，线程先获取互斥锁，然后访问临界区，最后释放互斥锁。同时，**所有其他线程阻塞直到互斥量释放**。一旦一个线程退出临界区，另一个线程就可以进入临界区。

## 3. 为什么要互斥？

首先，我们以SequenceGenerator类为例，它通过每次将currentValue递增1来生成下一个序列：

```java
public class SequenceGenerator {

	private int currentValue = 0;

	public int getNextSequence() {
		currentValue = currentValue + 1;
		return currentValue;
	}
}
```

现在，让我们创建一个测试用例，看看当多个线程尝试并发访问该方法时该方法的行为：

```java
@Test
public void givenUnsafeSequenceGenerator_whenRaceCondition_thenUnexpectedBehavior() throws Exception {
    int count = 1000;
    Set<Integer> uniqueSequences = getUniqueSequences(new SequenceGenerator(), count);
    Assert.assertEquals(count, uniqueSequences.size());
}

private Set<Integer> getUniqueSequences(SequenceGenerator generator, int count) throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(3);
    Set<Integer> uniqueSequences = new LinkedHashSet<>();
    List<Future<Integer>> futures = new ArrayList<>();

    for (int i = 0; i < count; i++) {
        futures.add(executor.submit(generator::getNextSequence));
    }

    for (Future<Integer> future : futures) {
        uniqueSequences.add(future.get());
    }

    executor.awaitTermination(1, TimeUnit.SECONDS);
    executor.shutdown();

    return uniqueSequences;
}
```

一旦我们执行这个测试用例，我们可以看到它大部分时间都失败了，原因类似于：

```shell
java.lang.AssertionError: expected:<1000> but was:<989>
  at org.junit.Assert.fail(Assert.java:88)
  at org.junit.Assert.failNotEquals(Assert.java:834)
  at org.junit.Assert.assertEquals(Assert.java:645)
```

uniqueSequences的大小应该等于我们在测试用例中执行getNextSequence方法的次数。但是，由于竞争条件，情况并非如此。显然，我们不希望出现这种行为。

所以，为了避免这种竞争条件，我们需要**确保一次只有一个线程可以执行getNextSequence方法**。在这种情况下，我们可以使用互斥量来同步线程。

有多种方法，我们可以在Java中实现互斥锁。因此，接下来，我们将看到为我们的SequenceGenerator类实现互斥锁的不同方法。

## 4. 使用synchronized关键字

首先，我们将讨论[synchronized关键字](https://www.baeldung.com/java-synchronized)，这是在Java中实现互斥量的最简单方法。

Java中的每个对象都有一个与之关联的内部锁。**synchronized方法和synchronized块使用这种内部锁来限制临界区的访问**，一次只能有一个线程。

因此，当线程调用同步方法或进入同步块时，它会自动获取锁。当方法或块完成或从中抛出异常时，锁将释放。

让我们将getNextSequence更改为具有互斥锁，只需添加synchronized关键字即可：

```java
public class SequenceGeneratorUsingSynchronizedMethod extends SequenceGenerator {
    
    @Override
    public synchronized int getNextSequence() {
        return super.getNextSequence();
    }
}
```

synchronized块类似于synchronized方法，对临界区和我们可以用于锁定的对象有更多的控制。

那么，现在让我们看看如何**使用synchronized块在自定义互斥对象上进行同步**：

```java
public class SequenceGeneratorUsingSynchronizedBlock extends SequenceGenerator {
    
    private Object mutex = new Object();

    @Override
    public int getNextSequence() {
        synchronized (mutex) {
            return super.getNextSequence();
        }
    }
}
```

## 5. 使用ReentrantLock

[ReentrantLock](https://www.baeldung.com/java-concurrent-locks)类是在Java 1.5中引入的。它比synchronized关键字方法提供更多的灵活性和控制。

让我们看看如何使用ReentrantLock来实现互斥：

```java
public class SequenceGeneratorUsingReentrantLock extends SequenceGenerator {

	private ReentrantLock mutex = new ReentrantLock();

	@Override
	public int getNextSequence() {
		try {
			mutex.lock();
			return super.getNextSequence();
		} finally {
			mutex.unlock();
		}
	}
}
```

## 6. 使用信号量

与ReentrantLock一样，[Semaphore](https://www.baeldung.com/java-semaphore)类也是在Java 1.5中引入的。

在互斥锁的情况下，只有一个线程可以访问临界区，而**信号量允许固定数量的线程访问临界区**。因此，**我们也可以通过将信号量中允许的线程数设置为一个来实现互斥量**。

现在让我们使用Semaphore创建另一个线程安全版本的SequenceGenerator：

```java
public class SequenceGeneratorUsingSemaphore extends SequenceGenerator {

	private Semaphore mutex = new Semaphore(1);

	@Override
	public int getNextSequence() {
		try {
			mutex.acquire();
			return super.getNextSequence();
		} catch (InterruptedException e) {
			// exception handling code
		} finally {
			mutex.release();
		}
	}
}
```

## 7. 使用Guava的Monitor类

到目前为止，我们已经看到了使用Java提供的特性来实现互斥锁的选项。

但是，Google的Guava库的Monitor类是ReentrantLock类的更好替代品。根据其[文档](https://guava.dev/releases/19.0/api/docs/com/google/common/util/concurrent/Monitor.html)，使用Monitor的代码比使用ReentrantLock的代码更易读且更不容易出错。

首先，我们将为[Guava](https://search.maven.org/search?q=a:guava)添加Maven依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

现在，我们将使用Monitor类编写SequenceGenerator的另一个子类：

```java
public class SequenceGeneratorUsingMonitor extends SequenceGenerator {
    
    private Monitor mutex = new Monitor();

    @Override
    public int getNextSequence() {
        mutex.enter();
        try {
            return super.getNextSequence();
        } finally {
            mutex.leave();
        }
    }
}
```

## 8. 总结

在本教程中，我们研究了互斥体的概念。此外，我们还看到了用Java实现它的不同方法。
## 1. 概述

Spring [ThreadPoolTaskExecutor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor.html)是一个Java Bean，它围绕[java.util.concurrent.ThreadPoolExecutor](https://www.baeldung.com/java-executor-service-tutorial)实例提供抽象并将其公开为Spring [org.springframework.core.task.TaskExecutor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/TaskExecutor.html)。此外，它可以通过corePoolSize、maxPoolSize、queueCapacity、allowCoreThreadTimeOut和keepAliveSeconds的属性进行高度配置。在本教程中，我们将了解corePoolSize和maxPoolSize属性。

## 2. corePoolSize与maxPoolSize

刚接触这种抽象的用户可能很容易对这两个配置属性的区别感到困惑。因此，让我们分别看一下。

### 2.1 corePoolSize

**corePoolSize是在不超时的情况下保持活动状态的最小worker(工作线程)数**。它是ThreadPoolTaskExecutor的可配置属性。但是，ThreadPoolTaskExecutor抽象将此值的设置委托给底层的java.util.concurrent.ThreadPoolExecutor。澄清一下，所有线程都可能超时-如果我们将allowCoreThreadTimeOut设置为true，则有效地将corePoolSize的值设置为0。

### 2.2 maxPoolSize

相反，**maxPoolSize定义了可以创建的最大线程数**。同样，ThreadPoolTaskExecutor的maxPoolSize属性也将其值委托给底层的java.util.concurrent.ThreadPoolExecutor。澄清一下，**maxPoolSize取决于queueCapacity**，因为ThreadPoolTaskExecutor只会在其队列中的元素数超过queueCapacity时创建一个新线程。

## 3. 那有什么区别呢？

corePoolSize和maxPoolSize之间的区别似乎很明显。然而，他们的行为有一些微妙之处。

当我们向ThreadPoolTaskExecutor提交新任务时，如果正在运行的线程少于corePoolSize，即使池中有空闲线程，或者正在运行的线程少于maxPoolSize并且由queueCapacity定义的队列已满，它也会创建一个新线程。

接下来，让我们看一些代码，以查看每个属性何时开始生效的示例。

## 4. 示例

首先，假设我们有一个从ThreadPoolTaskExecutor执行新线程的方法，名为startThreads：

```java
public void startThreads(ThreadPoolTaskExecutor taskExecutor, CountDownLatch countDownLatch, int numThreads) {
    for (int i = 0; i < numThreads; i++) {
        taskExecutor.execute(() -> {
            try {
                Thread.sleep(100L * ThreadLocalRandom.current().nextLong(1, 10));
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
```

让我们测试ThreadPoolTaskExecutor的默认配置，它定义了一个线程的corePoolSize、一个无界的maxPoolSize和一个无界的queueCapacity。因此，我们希望无论启动多少任务，都只会运行一个线程：

```java
@Test
public void whenUsingDefaults_thenSingleThread() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.afterPropertiesSet();

    CountDownLatch countDownLatch = new CountDownLatch(10);
    this.startThreads(taskExecutor, countDownLatch, 10);

    while (countDownLatch.getCount() > 0) {
        Assert.assertEquals(1, taskExecutor.getPoolSize());
    }
}
```

现在，让我们将corePoolSize更改为最多5个线程，并确保它的行为与我们所说的的一样。因此，无论提交给ThreadPoolTaskExecutor的任务数量有多少，我们都希望启动5个线程：

```java
@Test
public void whenCorePoolSizeFive_thenFiveThreads() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(5);
    taskExecutor.afterPropertiesSet();

    CountDownLatch countDownLatch = new CountDownLatch(10);
    this.startThreads(taskExecutor, countDownLatch, 10);

    while (countDownLatch.getCount() > 0) {
        Assert.assertEquals(5, taskExecutor.getPoolSize());
    }
}
```

同样，我们可以将maxPoolSize增加到10，同时将corePoolSize保留为5。因此，我们预计只启动5个线程。澄清一下，只有5个线程启动，因为queueCapacity仍然是无界的：

```java
@Test
public void whenCorePoolSizeFiveAndMaxPoolSizeTen_thenFiveThreads() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(5);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.afterPropertiesSet();

    CountDownLatch countDownLatch = new CountDownLatch(10);
    this.startThreads(taskExecutor, countDownLatch, 10);

    while (countDownLatch.getCount() > 0) {
        Assert.assertEquals(5, taskExecutor.getPoolSize());
    }
}
```

此外，我们现在将重复之前的测试，但将queueCapacity增加到10并启动20个线程。因此，我们现在期望总共启动10个线程：

```java
@Test
public void whenCorePoolSizeFiveAndMaxPoolSizeTenAndQueueCapacityTen_thenTenThreads() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(5);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setQueueCapacity(10);
    taskExecutor.afterPropertiesSet();

    CountDownLatch countDownLatch = new CountDownLatch(20);
    this.startThreads(taskExecutor, countDownLatch, 20);

    while (countDownLatch.getCount() > 0) {
        Assert.assertEquals(10, taskExecutor.getPoolSize());
    }
}
```

同样，如果我们将queueCapacity设置为0并且只启动10个任务，那么我们的ThreadPoolTaskExecutor中也会有10个线程。

## 5. 总结

ThreadPoolTaskExecutor是围绕java.util.concurrent.ThreadPoolExecutor的强大抽象，提供用于配置corePoolSize、maxPoolSize和queueCapacity的选项。在本教程中，我们了解了corePoolSize和maxPoolSize属性，以及maxPoolSize如何与queueCapacity协同工作，从而使我们能够轻松地为任何用例创建线程池。
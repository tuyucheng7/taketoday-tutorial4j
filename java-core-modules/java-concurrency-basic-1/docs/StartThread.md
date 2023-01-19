## 1. 概述

在本文中，我们将探讨Java中启动线程和执行并行任务的不同方法。

特别是在处理无法在主线程上运行的长时间或重复操作时，或者在等待操作结果时无法暂停UI交互时，这非常有用。

要了解有关线程的更多细节，请务必阅读关于[Java线程生命周期]()的文章。

## 2. 运行线程的基础知识

通过使用Thread框架，我们可以轻松编写一些在并行线程中运行的逻辑。

让我们通过继承Thread类来尝试一个基本示例：

```java
import java.util.concurrent.TimeUnit;

public class NewThread extends Thread {
    public void run() {
        long startTime = System.currentTimeMillis();
        int i = 0;
        while (true) {
            System.out.println(this.getName() + ": 新线程正在运行..." + i++);
            try {
                // 等待一秒钟，以免打印太快
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // ...
        }
    }
}
```

现在我们编写第二个类来初始化和启动线程：

```java
public class SingleThreadExample {
    public static void main(String[] args) {
        NewThread t = new NewThread();
        t.start();
    }
}
```

我们应该在处于NEW状态(相当于not started)的线程上调用start()方法。否则，Java将抛出IllegalThreadStateException的异常。

现在假设我们需要启动多个线程：

```java
public class MultipleThreadsExample {
    public static void main(String[] args) {
        NewThread t1 = new NewThread();
        t1.setName("MyThread-1");
        NewThread t2 = new NewThread();
        t2.setName("MyThread-2");
        t1.start();
        t2.start();
    }
}
```

我们的代码看起来仍然非常简单，与我们可以在网上找到的示例非常相似。

当然，这远不是实际开发中的生产代码，在生产代码中，以正确的方式管理资源、避免过多的上下文切换或过多的内存使用至关重要。

因此，为了做好生产准备，我们现在需要编写额外的样板来处理：

+ 新线程的持续创建
+ 并发活动线程的数量
+ 线程释放：为了避免泄漏，对守护进程线程来说非常重要

如果我们愿意，我们可以为所有这些案例场景甚至更多场景编写自己的代码，但我们为什么要重新发明轮子呢？

## 3. ExecutorService框架

ExecutorService实现了线程池设计模式(也称为复制工作者或工作者团队模型)，并负责上面提到的线程管理，此外，它还添加了一些非常有用的功能，如线程可重用性和任务队列。

线程的可重用性尤其重要：在大型应用程序中，分配和取消分配许多线程对象会产生巨大的内存管理开销。

对于工作线程，我们将线程创建所造成的开销降至最低。

为了简化池配置，ExecutorService提供了一个简单的构造函数和一些定制选项，例如队列类型、线程的最小和最大数量以及它们的命名约定。

## 4. 使用Executor启动任务

多亏了这个强大的框架，我们可以将思维方式从传统的创建线程，启动线程转变为提交任务。

让我们看看如何将异步任务提交给executor：

```
ExecutorService executor = Executors.newFixedThreadPool(10);
...
executor.submit(() -> {
  new Task();
});
```

我们可以使用两种方法：execute(不返回任何内容)和submit(返回封装计算结果的Future对象)。

## 5. 使用CompletableFutures启动任务

为了从Future对象中检索最终结果，我们可以使用对象中可用的get()方法，但这将阻止父线程，直到计算结束。

或者，我们可以通过向任务添加更多逻辑来避免阻塞，但我们必须增加代码的复杂性。

Java1.8在Future构造之上引入了一个新的框架，以更好地处理计算结果：CompletableFuture。

CompletableFuture实现CompletableStage，它添加了大量方法来附加回调，并避免在结果准备好后运行操作所需的所有管道。

提交任务的实现要简单得多：

```
CompletableFuture.supplyAsync(() -> "Hello");
```

supplyAsync()接收一个包含我们想要异步执行的代码的Supplier-在我们的例子中是lambda参数。

任务现在隐式提交到ForkJoinPool.commonPool()，或者我们可以指定我们自己的Executor作为第二个参数。

## 6. 运行延迟或定期任务

在处理复杂的web应用程序时，我们可能需要在特定时间运行任务，或者定期运行。

Java有一些工具可以帮助我们运行延迟或重复的操作：

+ java.util.Timer
+ java.util.concurrent.ScheduledThreadPoolExecutor

### 6.1 Timer

Timer是一种安排任务以便在后台线程中执行的工具。

任务可以安排为一次性执行，也可以定期重复执行。

如果我们想在延迟一秒钟后运行任务，让我们看看代码是什么样子的：

```
TimerTask task = new TimerTask() {
  public void run() {
    System.out.println("Task performed on: " + new Date() + "n" 
      + "Thread's name: " + Thread.currentThread().getName());
  }
};
Timer timer = new Timer("Timer");
long delay = 1000L;
timer.schedule(task, delay);
```

现在，让我们添加一个定期计划：

```
timer.scheduleAtFixedRate(repeatedTask, delay, period);
```

这一次，任务将在指定的延迟后运行，并在一段时间后重复执行。

### 6.2 ScheduledThreadPoolExecutor

ScheduledThreadPoolExecutor的方法与Timer类类似：

```
ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
ScheduledFuture<Object> resultFuture = executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
```

我们将使用scheduleAtFixedRate()用于定期任务：

```
ScheduledFuture<Object> resultFuture = executorService.scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);
```

上面的代码将在100毫秒的初始延迟后执行一个任务，之后每450毫秒执行一次相同的任务。

如果处理器无法在下一次任务发生之前及时完成任务处理，ScheduledExecutorService将等到当前任务完成后再开始下一次任务。

为了避免这种等待时间，我们可以使用scheduleWithFixedDelay()，正如其名称所述，它保证任务迭代之间有固定长度的延迟。

### 6.3 哪种工具更好？

如果我们运行上面的例子，计算结果看起来是一样的。

那么，我们如何选择正确的工具呢？

当一个框架提供多种选择时，了解底层技术以做出明智的决策是很重要的。

让我们试着了解背后更深的原理。

Timer：

+ 不提供实时保证：它使用Object.wait(long)方法调度任务。
+ 只有一个后台线程，因此任务按顺序运行，而长时间运行的任务可能会延迟其他任务。
+ TimerTask中抛出的运行时异常会杀死唯一可用的线程，从而杀死计时器

ScheduledThreadPoolExecutor：

+ 可以配置任意数量的线程
+ 可以利用所有可用的CPU核心
+ 捕获运行时异常，并允许我们在需要时处理它们(通过从ThreadPoolExecutor重写afterExecute方法)
+ 取消任务抛出异常，同时让其他任务继续运行
+ 依靠操作系统调度系统来跟踪时区、延迟、solar time等。
+ 如果我们需要在多个任务之间进行协调，例如等待提交的所有任务完成，则提供协作API。
+ 为线程生命周期的管理提供更好的API

现在的选择是显而易见的，对吗？

## 7. Future和ScheduledFuture区别

在我们的代码示例中，我们可以看到ScheduledThreadPoolExecutor返回一种特定类型的Future：ScheduledFuture。

ScheduledFuture继承了Future和Delayed接口，从而继承了额外的方法getDelay()
，该方法返回与当前任务相关的剩余延迟。RunnableScheduledFuture对其进行了扩展，它添加了一个方法来检查任务是否是周期性的。

ScheduledThreadPoolExecutor通过内部类ScheduledFutureTask实现所有这些构造，并使用它们控制任务生命周期。

## 8. 总结

在本文中，我们尝试了不同的框架来启动线程和并行运行任务。

然后，我们深入研究了Timer和ScheduledThreadPoolExecutor之间的区别。
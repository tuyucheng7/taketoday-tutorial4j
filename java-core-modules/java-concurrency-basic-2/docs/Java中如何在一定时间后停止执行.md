## 1. 概述

在本文中，我们将学习如何在一段时间后结束长时间运行的执行。我们将探讨这个问题的各种解决方案。此外，我们将介绍他们的一些陷阱。

## 2. 使用循环

假设我们正在一个循环中处理一组元素，例如电商应用程序中产品的一些细节，但可能不需要处理所有产品。

事实上，我们只希望处理到某个特定时间，然后，我们希望停止执行，并显示到该时间为止集合已处理的内容。

让我们来看一个简单的例子：

```java
long start = System.currentTimeMillis();
long end = start + 30  1000;
while (System.currentTimeMillis() < end) {
    // Some expensive operation on the item.
}
```

在这里，如果时间超过30秒的限制，循环将中断。上述解决方案中有一些值得注意的地方：

+ 低精度：**循环的运行时间可能超过规定的时间限制**，这取决于每次迭代可能需要的时间。例如，如果每次循环可能需要长达7秒的时间，那么总时间可能会长达35秒，这比所需的30秒时间限制长约17%。
+ 阻塞：**在主线程中进行这样的处理可能不是一个好主意，因为它会在很长一段时间内阻塞它**。相反，这些操作应该与主线程分离。

在下一节中，我们将讨论基于中断的方法如何消除这些限制。

## 3. 使用中断机制

在这里，我们将使用一个单独的线程来执行长时间运行的操作，主线程将在超时时向工作线程发送一个中断信号。

如果工作线程仍处于活动状态，它将捕获信号并停止执行。如果工作线程在超时之前完成，则不会对工作线程产生影响。

让我们看一下工作线程：

```java
class LongRunningTask implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < Long.MAX_VALUE; i++) {
            if (Thread.interrupted()) {
                return;
            }
        }
    }
}
```

在这里，for循环通过Long.MAX_VALUE模拟长时间运行的操作。除此之外，可能还有其他任何操作。**检查中断标志很重要，因为并非所有操作都是可中断的**。因此，在这些情况下，我们应该手动检查标志。

此外，我们应该在每次迭代中检查这个标志，以确保线程在最多一次循环的延迟内停止执行自身。

接下来，我们将介绍发送中断信号的三种不同机制。

### 3.1 使用Timer

我们可以创建一个[TimerTask](https://www.baeldung.com/java-timer-and-timertask)，在超时时中断工作线程：

```java
public class TimeOutTask extends TimerTask {
    private final Thread thread;
    private final Timer timer;

    public TimeOutTask(Thread thread, Timer timer) {
        this.thread = thread;
        this.timer = timer;
    }

    @Override
    public void run() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            timer.cancel();
        }
    }
}
```

在这里，我们定义了一个TimerTask，它在创建时接收一个工作线程，**将在调用其run()方法时中断工作线程**。[Timer](https://www.baeldung.com/java-timer-and-timertask)将在三秒钟延迟后触发TimerTask：

```java
Thread thread = new Thread(new LongRunningTask());
thread.start();

Timer timer = new Timer();
TimeOutTask timeOutTask = new TimeOutTask(thread, timer);
timer.schedule(timeOutTask, 3000);
```

### 3.2 使用Future#get方法

我们也可以使用[Future](https://www.baeldung.com/java-future)的get()方法，而不是使用Timer：

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
Future future = executor.submit(new LongRunningTask());
try {
    future.get(7, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    future.cancel(true);
} catch (Exception e) {
    // handle other exceptions
} finally {
    executor.shutdownNow();
}
```

在这里，我们使用ExecutorService提交返回Future实例的工作线程，其get()方法将阻塞主线程，直到指定的时间。它将在指定的超时后引发TimeoutException。在catch块中，我们通过调用Future对象上的cancel()方法来中断工作线程。

与前一种方法相比，这种方法的主要优点是**它使用一个线程池来管理线程，而Timer只使用单个线程(无池)**。

### 3.3 使用ScheduledExecutorService

我们还可以使用[ScheduledExecutorService](https://www.baeldung.com/java-executor-service-tutorial#ScheduledExecutorService)中断任务，该类是ExecutorService的扩展，提供了相同的功能，并添加了几个处理执行调度的方法，这可以在设定时间单位的特定延迟后执行给定任务：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
Future future = executor.submit(new LongRunningTask());
Runnable cancelTask = () -> future.cancel(true);

executor.schedule(cancelTask, 3000, TimeUnit.MILLISECONDS);
executor.shutdown();
```

在这里，我们使用newScheduledThreadPool()方法创建了一个大小为2的调度线程池。ScheduledExecutorService#schedule方法接收参数[Runnable](https://www.baeldung.com/java-runnable-vs-extending-thread)、delay和TimeUnit。

上述程序将任务安排在提交后的三秒钟后执行，此任务将取消原始的长时间运行的任务。

请注意，与前面的方法不同，我们没有通过调用Future#get方法来阻塞主线程。因此，**它是上述所有方法中最建议使用的方法**。

## 4. 保证性

**无法保证执行会在一定时间后停止，主要原因是并非所有的阻塞方法都是可中断的**。事实上，只有少数定义明确的方法是可中断的。因此，**如果一个线程被中断，并且设置了一个标志，那么在它到达其中一个可中断的方法之前，不会发生其他任何事情**。

例如，只有在使用[InterruptibleChannel](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/channels/InterruptibleChannel.html)创建的流上调用读写方法时，它们才是可中断的。[BufferedReader](https://www.baeldung.com/java-buffered-reader)不是InterruptibleChannel，因此，如果线程使用它来读取文件，那么在read()方法中阻塞的线程上调用interrupt()就没有效果。

然而，我们可以在循环中每次读取之后显式地检查中断标志，这将为延迟停止线程提供合理的保证。但是，这并不能保证在经过一段严格的时间后停止线程，因为我们不知道读取操作需要多长时间。

另一方面，Object对象的wait()方法是可中断的。因此，在设置中断标志后，在wait方法中阻塞的线程将立即抛出InterruptedException。

我们可以通过在方法签名中根据throws InterruptedException子句来识别阻塞方法。

一个重要的建议是**避免使用不推荐使用的**[Thread.stop()](https://docs.oracle.com/javase/7/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html)**方法**，停止线程会导致它解锁已锁定的所有监视器，这种情况的发生是因为[ThreadDeath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ThreadDeath.html)异常在堆栈中向上传播。

如果以前受这些监视器保护的任何对象处于不一致状态，则不一致的对象将对其他线程可见，这可能会导致很难检测和推理的任意行为。

## 5. 中断设计

在上一节中，我们强调了使用可中断方法停止执行的重要性。因此，我们的代码需要从设计的角度考虑这个期望。

假设我们有一个长时间运行的任务要执行，我们需要确保它不会比指定的时间花费更多的时间。此外，假设任务可以拆分为单独的步骤。

让我们为任务步骤创建一个类：

```java
class Step {
    private static int MAX = Integer.MAX_VALUE / 2;
    int number;

    public Step(int number) {
        this.number = number;
    }

    public void perform() throws InterruptedException {
        Random rnd = new Random();
        int target = rnd.nextInt(MAX);
        while (rnd.nextInt(MAX) != target) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }
}
```

在这里，Step#perform方法在每次迭代时都会询问标志，试图找到一个target随机整数。当该标志被激活时，该方法抛出InterruptedException。

现在，让我们定义将执行所有步骤的任务：

```java
public class SteppedTask implements Runnable {
    private List<Step> steps;

    public SteppedTask(List<Step> steps) {
        this.steps = steps;
    }

    @Override
    public void run() {
        for (Step step : steps) {
            try {
                step.perform();
            } catch (InterruptedException e) {
                // handle interruption exception
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
```

这里，SteppedTask有一个要执行的Step集合。for循环执行每个步骤，并处理InterruptedException，以便在任务发生异常时停止任务。

最后，让我们看一个使用可中断任务的示例：

```java
List<Step> steps = Stream.of(
  	new Step(1),
  	new Step(2),
  	new Step(3),
  	new Step(4))
.collect(Collectors.toList());

Thread thread = new Thread(new SteppedTask(steps));
thread.start();

Timer timer = new Timer();
TimeOutTask timeOutTask = new TimeOutTask(thread, timer);
timer.schedule(timeOutTask, 10000);
```

首先，我们创建一个包含4个Step的SteppedTask。其次，我们使用线程运行任务。最后，我们使用Timer和timeOutTask在10秒后中断线程。

通过这种设计，我们可以确保在执行任何步骤时可以中断长时间运行的任务。正如我们之前所看到的，缺点是不能保证它会在指定的确切时间停止，但肯定比不可中断的任务要好。

## 6. 总结

在本教程中，我们学习了在给定时间后停止执行的各种技术，以及每种技术的优缺点。
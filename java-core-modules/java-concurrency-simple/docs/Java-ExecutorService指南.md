## 1. 概述

[ExecutorService](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html)是一个JDK API，可以简化异步模式下运行的任务。一般来说，ExecutorService会自动提供一个线程池和一个用于为其分配任务的API。

## 2. 实例化ExecutorService

### 2.1 Executors类的工厂方法

创建ExecutorService最简单的方法是使用Executors类的工厂方法之一。

例如，下面这行代码将创建一个包含10个线程的线程池：

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

还有其他几种工厂方法可以创建满足特定用例的预定义ExecutorService。要找到满足你需求的最佳方法，请查阅[Oracle的官方文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html)。

### 2.2 直接创建ExecutorService

因为ExecutorService是一个接口，所以可以使用其任何实现的实例。[java.util.concurrent](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html)包中有多种实现可供选择，或者你也可以创建自己的实现。

例如，ThreadPoolExecutor类有一些构造函数，我们可以使用它们来配置ExecutorService及其内部线程池：

```java
ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
```

其实上面的代码与Executors中的工厂方法newSingleThreadExecutor()的[源代码](https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/util/concurrent/Executors.java#L133)非常相似。在大多数情况下，不需要进行详细的手动配置。

## 3. 将任务分配给ExecutorService

ExecutorService可以执行Runnable和Callable任务。为了简单起见，我们创建两个基本任务。请注意，我们在这里使用lambda表达式而不是匿名内部类：

```java
public class Java8ExecutorServiceIntegrationTest {
    private Runnable runnableTask;
    private Callable<String> callableTask;
    private List<Callable<String>> callableTasks;

    @BeforeEach
    public void init() {
        runnableTask = () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        callableTask = () -> {
            TimeUnit.MILLISECONDS.sleep(300);
            return "Task's execution";
        };

        callableTasks = new ArrayList<>();
        callableTasks.add(callableTask);
        callableTasks.add(callableTask);
        callableTasks.add(callableTask);
    }
}
```

我们可以使用多种方法将任务分配给ExecutorService，包括从Executor接口继承的execute()，以及submit()、invokeAny()和invokeAll()。

**execute()**方法返回值为void，并且不提供任何获取任务执行结果或检查任务状态(是否正在运行)的可能性：

```java
executorService.execute(runnableTask);
```

**submit()**将Callable或Runnable任务提交给ExecutorService，并返回Future类型的结果：

```java
Future<String> future = executorService.submit(callableTask);
```

**invokeAny()**将一组任务分配给ExecutorService，使每个任务运行，并返回一个任务成功执行的结果(如果执行成功)：

```java
String result = executorService.invokeAny(callableTasks);
```

**invokeAll()**将一组任务分配给ExecutorService，使每个任务运行，并以Future类型的对象列表的形式返回所有任务执行的结果：

```java
List<Future<String>> futures = executorService.invokeAll(callableTasks);
```

在进一步讨论之前，我们需要讨论另外两个问题：关闭ExecutorService和处理Future的返回类型。

## 4. 关闭ExecutorService

一般情况下，当没有任务要处理时ExecutorService不会自动销毁。它将保持存活并等待新的任务到来。

在某些情况下，这非常有用，例如当应用程序需要处理不定期出现的任务，或者在编译期任务的数量未知时。

另一方面，应用程序可能会执行完毕但不会停止，因为等待的ExecutorService将导致JVM继续运行。

要正确关闭ExecutorService，我们可以使用shutdown()和shutdownNow() API。

**shutdown()**方法不会立即销毁ExecutorService。它将使ExecutorService停止接收新任务并在所有正在运行的线程完成当前任务的执行后关闭：

```java
executorService.shutdown();
```

**shutdownNow()**方法会尝试立即销毁ExecutorService，但它不保证所有正在运行的线程都会同时停止：

```java
List<Runnable> notExecutedTasks = executorService.shutDownNow();
```

此方法返回等待处理的任务列表。由开发人员决定如何处理这些任务。

关闭ExecutorService([Oracle推荐使用](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html))的一种好方法是将这两种方法与**awaitTermination()**方法结合使用：

```java
private List<Runnable> smartShutdown(ExecutorService executorService) {
    List<Runnable> notExecutedTasks = new ArrayList<>();
    executorService.shutdown();
    try {
        if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
            notExecutedTasks = executorService.shutdownNow();
        }
    } catch (InterruptedException e) {
        notExecutedTasks = executorService.shutdownNow();
    }
    return notExecutedTasks;
}
```

使用这种方法，ExecutorService将首先停止接收新任务，然后等待指定的时间段以等待所有任务完成。如果该时间到期，则立即停止执行。

## 5. Future接口

submit()和invokeAll()方法分别返回一个Future类型的对象和集合，这使我们能够获得任务执行的结果或检查任务的状态(是否正在运行)。

Future接口提供了一个特殊的阻塞方法get()，该方法返回Callable任务执行的实际结果。或者在Runnable任务的情况下返回null：

```java
Future<String> future = executorService.submit(callableTask);
String result = null;
try {
    result = future.get();
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```

在任务仍在运行时调用get()方法将导致执行阻塞，直到任务正确执行并且结果可用。

当任务长时间运行时，调用get()方法会导致很长时间的阻塞，应用程序的性能可能会下降。如果生成的数据不是那么重要，可以通过使用超时来避免此类问题：

```java
String result = future.get(200, TimeUnit.MILLISECONDS);
```

如果执行时间长于指定的时间(在本例中为200毫秒)，将抛出TimeoutException。

我们可以使用isDone()方法检查分配的任务是否已经处理完成。

Future接口还提供cancel()方法用来取消任务执行，并提供isCancelled()方法检查是否取消：

```java
boolean canceled = future.cancel(true);
boolean isCancelled = future.isCancelled();
```

## 6. ScheduledExecutorService接口

ScheduledExecutorService在预定义的延迟和/或定期运行任务。

同样，实例化ScheduledExecutorService的最佳方法是使用Executors类的工厂方法。

在本节中，我们使用带有一个线程的ScheduledExecutorService：

```java
ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
```

要在固定延迟后安排单个任务的执行，请使用ScheduledExecutorService的scheduled()方法。

两个scheduled()方法允许你执行Runnable或Callable的任务：

```java
Future<String> resultFuture = executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
```

scheduleAtFixedRate()方法允许我们在固定延迟后定期运行任务。上面的代码在执行callableTask之前会延迟一秒钟。

以下代码块将在100毫秒的初始延迟后运行任务。之后，它将每450毫秒运行一次相同的任务：

```java
Future<String> resultFuture = service.scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);
```

如果处理器运行分配的任务所需的时间超过scheduleAtFixedRate()方法的period参数，则ScheduleExecutorService将等到当前任务完成后再开始下一个任务。

如果需要在任务迭代之间有固定长度的延迟，则应使用scheduleWithFixedDelay()。

例如，以下代码将保证在当前任务执行结束和另一个任务执行开始之间有150毫秒的暂停：

```java
service.scheduleWithFixedDelay(task, 100, 150, TimeUnit.MILLISECONDS);
```

根据scheduleAtFixedRate()和scheduleWithFixedDelay()方法约定，任务的周期执行将在ExecutorService终止或任务执行期间抛出异常时结束。

## 7. ExecutorService与Fork/Join

在Java 7发布后，许多开发人员决定用fork/join框架取代ExecutorService框架。

然而，这并不总是正确的决定。尽管fork/join具有简单性和频繁的性能提升，但它减少了开发人员对并发执行的控制。

ExecutorService使开发人员能够控制生成的线程数量以及应该由单独线程运行的任务的粒度。ExecutorService的最佳用例是处理独立任务，例如根据“一个任务一个线程”的方案处理事务或请求。

相比之下，[根据Oracle的文档](https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html)，fork/join的设计目的是加速可以递归地分解成更小部分的工作。

## 8. 总结

尽管ExecutorService相对简单，但仍有一些常见的陷阱。

让我们总结一下：

**未使用的ExecutorService仍然会保持处于活动状态**：请参阅第4节中有关如何关闭ExecutorService的详细说明。

**使用固定长度线程池时线程池容量错误**：确定应用程序有效运行任务需要多少线程非常重要。太大的线程池会导致不必要的开销，只是为了创建大多数处于等待模式的线程。由于队列中的任务等待时间太长，太少会使应用程序看起来没有响应。

**在任务取消后调用Future的get()方法**：试图获取已取消任务的结果会触发CancellationException。

**使用Future的get()方法意外长时间阻塞**：我们应该使用超时来避免意外等待。
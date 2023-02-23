## 1. 概述

在本文中，我们将重点关注不同类型的调度器，我们将在编写基于RxJava Observable的subscribeOn和observeOn方法的多线程程序时使用这些调度器。

调度器提供了指定在何处以及何时执行与Observable链操作相关的任务的机会。

我们可以从类[Schedulers](http://reactivex.io/RxJava/javadoc/rx/schedulers/Schedulers.html)中描述的工厂方法中获得一个Scheduler。

## 2. 默认线程行为

**默认情况下，Rx是单线程的**，这意味着Observable和我们可以应用于它的运算符链将在调用其subscribe()方法的同一线程上通知其观察者。

observeOn和subscribeOn方法将Scheduler作为参数，顾名思义，它是我们可以用来调度单个操作的工具。

我们将使用createWorker方法创建我们的Scheduler实现，该方法返回一个[Scheduler.Worker](http://reactivex.io/RxJava/javadoc/rx/Scheduler.Worker.html)。worker接收操作并在单个线程上按顺序执行它们。

在某种程度上，worker本身就是一个调度器，但为了避免混淆，我们不会将其称为调度器。

### 2.1 安排操作

我们可以通过创建一个新的worker并安排一些操作来在任何Scheduler上安排作业：

```java
Scheduler scheduler = Schedulers.immediate();
Scheduler.Worker worker = scheduler.createWorker();
worker.schedule(() -> result += "action");
 
Assert.assertTrue(result.equals("action"));
```

然后，该操作在分配给worker的线程上排队。

### 2.2 取消操作

Scheduler.Worker扩展了Subscription，在worker上调用unsubscribe方法将导致队列被清空并取消所有挂起的任务。我们可以通过示例看到：

```java
Scheduler scheduler = Schedulers.newThread();
Scheduler.Worker worker = scheduler.createWorker();
worker.schedule(() -> {
    result += "First_Action";
    worker.unsubscribe();
});
worker.schedule(() -> result += "Second_Action");
 
Assert.assertTrue(result.equals("First_Action"));
```

第二个任务永远不会执行，因为它之前的任务取消了整个操作。正在执行的动作将被中断。

## 3. Schedulers.newThread

每次通过subscribeOn()或observeOn()请求时，此调度器都会简单地启动一个新线程。

这几乎不是一个好的选择，不仅因为启动线程时涉及延迟，还因为该线程未被重用：

```java
Observable.just("Hello")
    .observeOn(Schedulers.newThread())
    .doOnNext(s ->
        result2 += Thread.currentThread().getName()
    )
    .observeOn(Schedulers.newThread())
    .subscribe(s ->
        result1 += Thread.currentThread().getName()
    );
Thread.sleep(500);
Assert.assertTrue(result1.equals("RxNewThreadScheduler-1"));
Assert.assertTrue(result2.equals("RxNewThreadScheduler-2"));
```

当worker完成后，线程将简单地终止。只有当任务是粗粒度的时候才能使用这个调度器：它需要很多时间才能完成，但是任务数量很少，以至于线程根本不可能被重用。

```java
Scheduler scheduler = Schedulers.newThread();
Scheduler.Worker worker = scheduler.createWorker();
worker.schedule(() -> {
    result += Thread.currentThread().getName() + "_Start";
    worker.schedule(() -> result += "_worker_");
    result += "_End";
});
Thread.sleep(3000);
Assert.assertTrue(result.equals("RxNewThreadScheduler-1_Start_End_worker_"));
```

当我们在NewThreadScheduler上调度worker时，我们看到worker被绑定到一个特定的线程。

## 4. Schedulers.immediate

Schedulers.immediate是一个特殊的调度器，它以阻塞方式而不是异步方式在客户端线程中调用任务，并在操作完成时返回：

```java
Scheduler scheduler = Schedulers.immediate();
Scheduler.Worker worker = scheduler.createWorker();
worker.schedule(() -> {
    result += Thread.currentThread().getName() + "_Start";
    worker.schedule(() -> result += "_worker_");
    result += "_End";
});
Thread.sleep(500);
Assert.assertTrue(result.equals("main_Start_worker__End"));
```

事实上，通过immediate调度器订阅Observable通常与根本不订阅任何特定调度器具有相同的效果：

```java
Observable.just("Hello")
    .subscribeOn(Schedulers.immediate())
    .subscribe(s ->
        result += Thread.currentThread().getName()
    );
Thread.sleep(500);
Assert.assertTrue(result.equals("main"));
```

## 5. Schedulers.trampoline

trampoline调度器与immediate非常相似，因为它也在同一个线程中调度任务，有效地阻塞。

但是，即将执行的任务会在所有先前计划的任务完成时执行：

```java
Observable.just(2, 4, 6, 8)
    .subscribeOn(Schedulers.trampoline())
    .subscribe(i -> result += "" + i);
Observable.just(1, 3, 5, 7, 9)
    .subscribeOn(Schedulers.trampoline())
    .subscribe(i -> result += "" + i);
Thread.sleep(500);
Assert.assertTrue(result.equals("246813579"));
```

immediate立即调用给定任务，而trampoline等待当前任务完成。

trampoline的worker在调度第一个任务的线程上执行每个任务。对schedule的第一次调用是阻塞的，直到队列被清空：

```java
Scheduler scheduler = Schedulers.trampoline();
Scheduler.Worker worker = scheduler.createWorker();
worker.schedule(() -> {
    result += Thread.currentThread().getName() + "Start";
    worker.schedule(() -> {
        result += "_middleStart";
        worker.schedule(() ->
            result += "_worker_"
        );
        result += "_middleEnd";
    });
    result += "_mainEnd";
});
Thread.sleep(500);
Assert.assertTrue(result.equals("mainStart_mainEnd_middleStart_middleEnd_worker_"));
```

## 6. Schedulers.from

调度器在内部比java.util.concurrent中的执行器(Executor)更复杂-因此需要一个单独的抽象。

但是因为它们在概念上非常相似，所以毫不奇怪有一个包装器可以使用from工厂方法将Executor转换为Scheduler：

```java
private ThreadFactory threadFactory(String pattern) {
    return new ThreadFactoryBuilder()
        .setNameFormat(pattern)
        .build();
}

@Test
public void givenExecutors_whenSchedulerFrom_thenReturnElements() throws InterruptedException {
    ExecutorService poolA = newFixedThreadPool(10, threadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);
    ExecutorService poolB = newFixedThreadPool(10, threadFactory("Sched-B-%d"));
    Scheduler schedulerB = Schedulers.from(poolB);

    Observable<String> observable = Observable.create(subscriber -> {
        subscriber.onNext("Alfa");
        subscriber.onNext("Beta");
        subscriber.onCompleted();
    });;

    observable
        .subscribeOn(schedulerA)
        .subscribeOn(schedulerB)
        .subscribe(
            x -> result += Thread.currentThread().getName() + x + "_",
            Throwable::printStackTrace,
            () -> result += "_Completed"
        );
    Thread.sleep(2000);
    Assert.assertTrue(result.equals("Sched-A-0Alfa_Sched-A-0Beta__Completed"));
}
```

schedulerB的使用时间很短，但它几乎不会在schedulerA上安排一个新的操作，它会完成所有工作。因此，多个subscribeOn方法不仅会被忽略，而且还会引入少量开销。

## 7. Schedulers.io

这个调度器类似于newThread，除了已经启动的线程被回收并且可以处理未来的请求。

此实现的工作方式类似于java.util.concurrent中的ThreadPoolExecutor，具有无限线程池。每次请求一个新的worker时，要么启动一个新线程(然后保持空闲一段时间)，要么重用空闲线程：

```java
Observable.just("io")
    .subscribeOn(Schedulers.io())
    .subscribe(i -> result += Thread.currentThread().getName());
 
Assert.assertTrue(result.equals("RxIoScheduler-2"));
```

我们需要小心处理任何类型的无限资源-在Web服务等缓慢或无响应的外部依赖项的情况下，io调度器可能会启动大量线程，导致我们自己的应用程序变得无响应。

在实践中，遵循Schedulers.io几乎总是更好的选择。

## 8. Schedulers.computation

默认情况下，Computation调度器将并行运行的线程数限制为availableProcessors()的值，如Runtime.getRuntime()实用程序类中所示。

因此，当任务完全受CPU限制时，我们应该使用computation调度器；也就是说，它们需要计算能力并且没有阻塞代码。

它在每个线程前面使用了一个无界队列，因此如果任务被调度，但是所有的核心都被占用，它就会被排队。但是，每个线程之前的队列将继续增长：

```java
Observable.just("computation")
    .subscribeOn(Schedulers.computation())
    .subscribe(i -> result += Thread.currentThread().getName());
Assert.assertTrue(result.equals("RxComputationScheduler-1"));
```

如果出于某种原因，我们需要与默认值不同的线程数，我们始终可以使用rx.scheduler.max-computation-threads系统属性。

通过使用更少的线程，我们可以确保始终有一个或多个CPU核心空闲，即使在重负载下，computation线程池也不会使服务器饱和。根本不可能拥有比核心更多的计算线程。

## 9. Schedulers.test

此调度器仅用于测试目的，我们永远不会在生产代码中看到它。它的主要优点是能够提前时钟，模拟任意流逝的时间：

```java
List<String> letters = Arrays.asList("A", "B", "C");
TestScheduler scheduler = Schedulers.test();
TestSubscriber<String> subscriber = new TestSubscriber<>();

Observable<Long> tick = Observable
    .interval(1, TimeUnit.SECONDS, scheduler);

Observable.from(letters)
    .zipWith(tick, (string, index) -> index + "-" + string)
    .subscribeOn(scheduler)
    .subscribe(subscriber);

subscriber.assertNoValues();
subscriber.assertNotCompleted();

scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
subscriber.assertNoErrors();
subscriber.assertValueCount(1);
subscriber.assertValues("0-A");

scheduler.advanceTimeTo(3, TimeUnit.SECONDS);
subscriber.assertCompleted();
subscriber.assertNoErrors();
subscriber.assertValueCount(3);
assertThat(
    subscriber.getOnNextEvents(), 
    hasItems("0-A", "1-B", "2-C"));
```

## 10. 默认调度器

RxJava中的一些Observable运算符具有替代形式，允许我们设置运算符将用于其操作的调度器。其他人不在任何特定的Scheduler上运行或在特定的默认Scheduler上运行。

例如，delay运算符获取上游事件并在给定时间后将它们推送到下游。显然，它不能在那段时间保持原来的线程，所以它必须使用不同的Scheduler：

```java
ExecutorService poolA = newFixedThreadPool(10, threadFactory("Sched1-"));
Scheduler schedulerA = Schedulers.from(poolA);
Observable.just('A', 'B')
    .delay(1, TimeUnit.SECONDS, schedulerA)
    .subscribe(i -> result+= Thread.currentThread().getName() + i + " ");

Thread.sleep(2000);
Assert.assertTrue(result.equals("Sched1-A Sched1-B "));
```

在不提供自定义schedulerA的情况下，delay以下的所有运算符都将使用computation调度器。

其他支持自定义调度器的重要运算符有buffer、interval、range、timer、skip、take、timeout等。如果我们不为此类运算符提供调度器，则会使用computation调度器，这在大多数情况下是安全的默认设置。

## 11. 总结

在真正的响应式应用程序中，所有长时间运行的操作都是异步的，因此需要很少的线程和调度器。

掌握调度器对于使用RxJava编写可扩展且安全的代码至关重要。subscribeOn和observeOn之间的区别在高负载下尤其重要，因为每个任务都必须在我们期望的时候精确执行。

最后但同样重要的是，我们必须确保下游使用的调度器能够跟上上游调度器产生的负载。有关更多信息，请参阅这篇关于[背压](https://www.baeldung.com/rxjava-backpressure)的文章。
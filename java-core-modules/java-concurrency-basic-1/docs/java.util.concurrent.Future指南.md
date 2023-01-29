## 1. 概述

在本教程中，我们将介绍[Future](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Future.html)接口。它是自Java 1.5以来一直存在的接口，在处理异步调用和并发处理时非常有用。

## 2. 创建Future

简单地说，Future类表示异步计算的未来结果，这个结果最终会在处理完成后出现在Future中。

让我们看看如何编写创建并返回Future实例的方法。

长时间运行的方法非常适合异步处理和Future接口，因为我们可以在等待Future封装的任务完成时执行其他代码。

Future异步特性的一些操作示例如下：

+ 计算密集型应用(数学和科学计算)
+ 操作大型数据结构(大数据)
+ 远程方法调用(下载文件、HTML文件、web服务)

### 2.1 使用FutureTask实现Future

在我们的例子中，我们将创建一个非常简单的类来计算整数的平方。这显然不属于需要长期运行的方法，但我们可以对其进行Thread.sleep()调用，使其在完成之前睡眠1秒：

```java
class SquareCalculator {
    private final ExecutorService executor;

    SquareCalculator(ExecutorService executor) {
        this.executor = executor;
    }

    Future<Integer> calculate(Integer input) {
        return executor.submit(() -> {
            Thread.sleep(1000);
            return input * input;
        });
    }
}
```

实际执行计算的代码包含在call()方法中，并作为lambda表达式提供。正如我们所看到的，除了前面提到的sleep()调用之外，它并没有什么特别之处。

当我们将注意力集中在[Callable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Callable.html)和[ExecutorService](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html)的使用上时，它会变得更加有趣。

Callable是一个接口，表示返回结果的任务，它有一个call()方法。在这里，我们使用lambda表达式创建了它的一个实例。

创建一个Callable的实例不会发生任何事情。我们需要将此实例传递给ExecutorService，而ExecutorService将负责在新线程中启动任务，并将有价值的Future对象返回给我们。这就是ExecutorService的作用所在。

有几种方法可以访问ExecutorService实例，其中大多数都是由工具类[Executors](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html)的静态工厂方法提供的。在本例中，我们使用了基本的newSingleThreadExecutor()，它为我们提供了一个ExecutorService，能够一次处理一个线程。

一旦有了ExecutorService对象，我们只需要调用submit()，将我们的Callable作为参数传递。然后submit()将启动任务并返回一个[FutureTask](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/FutureTask.html)对象，它是Future接口的实现之一。

## 3. 使用Future

到目前为止，我们已经学习了如何创建Future的实例。

在本节中，我们将通过探索Future API中的所有方法来学习如何使用这个实例。

### 3.1 使用isDone()和get()获得结果

现在我们需要调用calculate()，并使用返回的Future来获取结果。来自Future API的两个方法可以帮助我们完成这个任务。

[Future.isDone()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Future.html#isDone())告诉我们executor是否已完成任务处理。如果任务完成，它将返回true；否则，返回false。

从计算中返回实际结果的方法是[Future.get()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Future.html#get())。我们可以看到，这个方法会阻塞执行，直到任务完成。但是在我们的示例中，这不是问题，因为我们将通过调用isDone()来检查任务是否完成。

通过结合使用这两个方法，我们可以在等待主任务完成的同时运行其他代码：

```java
@Test
void givenExecutorIsSingleThreaded_whenTwoExecutionsAreTriggered_thenRunInSequence() throws ExecutionException, InterruptedException {
    squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());
    Future<Integer> result = squareCalculator.calculate(4);
    while (!result.isDone()) {
        System.out.println("Calculating...");
        TimeUnit.MILLISECONDS.sleep(300);
    }
    assertEquals(16, result.get());
}
```

在这个例子中，我们在控制台打印一些简单的日志，让用户知道程序正在执行计算。

get()方法将阻塞执行，直到任务完成。同样这也不是问题，因为在我们的示例中，只有在确保任务完成后才会调用get()。所以在这种情况下，future.get()总是会立即返回。

值得一提的是，get()方法有一个重载版本，它接收timeout和[TimeUnit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TimeUnit.html)作为参数：

```java
Integer result = future.get(500, TimeUnit.MILLISECONDS);
```

[get(long，TimeUnit)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Future.html#get(long,java.util.concurrent.TimeUnit))和[get()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Future.html#get())之间的区别在于，如果任务没有在指定的超时时间之前返回，前者将抛出[TimeoutException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TimeoutException.html)。

### 3.2 使用cancel()取消Future

假设我们触发了一个任务，但出于某种原因，我们不再关心结果。我们可以利用[Future.cancel(boolean)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Future.html#cancel(boolean))告诉executor停止操作并中断其底层线程：

```java
@Test
void whenCancelFutureAndCallGet_thenThrowException() {
    squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());
    Future<Integer> future = squareCalculator.calculate(4);
    boolean canceled = future.cancel(true);
    
    assertTrue(canceled, "Future was canceled");
    assertTrue(future.isCancelled(), "Future are canceled");
    
    assertThrows(CancellationException.class, future::get);
}
```

从上面的代码来看，我们的Future实例永远不会完成它的操作。事实上，如果我们在调用cancel()之后尝试从该实例调用get()，结果将是[CancellationException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CancellationException.html)。[Future.isCancelled()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Future.html#isCancelled())告诉我们Future是否已被取消。这对于避免出现CancellationException非常有用。

对cancel()的调用也可能失败，在这种情况下，返回的值将为false。需要注意的是，cancel()将布尔值作为参数，这控制执行任务的线程是否应该被中断。

## 4. 使用线程池实现多线程

我们当前的ExecutorService是单线程的，因为它是通过[Executors.newSingleThreadExecutor()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html#newSingleThreadExecutor())创建的。为了突出这个单线程，让我们同时触发两个计算：

```java
@Test
void givenExecutorIsSingleThreaded_whenTwoExecutionsAreTriggered_thenRunInSequence() throws ExecutionException, InterruptedException {
    squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());
    
    Future<Integer> result1 = squareCalculator.calculate(4);
    Future<Integer> result2 = squareCalculator.calculate(1000);
      
    while (!result1.isDone() || !result2.isDone()) {
        LOG.debug(String.format("Task 1 is %s and Task 2 is %s.", result1.isDone() ? "done" : "not done", result2.isDone() ? "done" : "not done"));
        TimeUnit.MILLISECONDS.sleep(300);
    }
    
    assertEquals(16, result1.get());
    assertEquals(1000000, result2.get());
}
```

现在让我们分析这段代码的输出：

```shell
15:33:38.743 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is not done and Task 2 is not done. 
15:33:39.056 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is not done and Task 2 is not done. 
15:33:39.363 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is not done and Task 2 is not done. 
15:33:39.675 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is not done and Task 2 is not done. 
15:33:39.988 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is done and Task 2 is not done. 
15:33:40.297 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is done and Task 2 is not done. 
15:33:40.610 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is done and Task 2 is not done. 
15:33:40.919 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Test givenExecutorIsSingleThreaded_whenTwoExecutionsAreTriggered_thenRunInSequence took 2178 ms 
```

很明显，这个过程不是并行的。我们可以看到，第二个任务在第一个任务完成后才开始，这使得整个过程大约需要2秒钟才能完成。

为了使我们的程序真正多线程化，我们应该使用不同的ExecutorService。让我们看看如果使用工厂方法[Executors.newFixedThreadPool()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html#newFixedThreadPool(int))提供的线程池，测试的输出会发生怎样的变化。

```java
@Test
void givenExecutorIsMultiThread_whenTwoExecutionsAreTriggered_thenRunInParallel() throws ExecutionException, InterruptedException {
    squareCalculator = new SquareCalculator(Executors.newFixedThreadPool(2));
    
    Future<Integer> future1 = squareCalculator.calculate(4);
    Future<Integer> future2 = squareCalculator.calculate(1000);
      
    while (!future1.isDone() || !future2.isDone()) {
        LOG.debug(String.format("Task 1 is %s and Task 2 is %s.", future1.isDone() ? "done" : "not done", future2.isDone() ? "done" : "not done"));
        TimeUnit.MILLISECONDS.sleep(300);
    }
    
    assertEquals(16, future1.get());
    assertEquals(1000000, future2.get());
}
```

通过对SquareCalculator类的简单更改，我们现在有了一个能够同时使用两个线程的ExecutorService。

如果我们再次运行完全相同的代码，我们将得到以下输出：

```shell
15:35:15.771 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is not done and Task 2 is not done. 
15:35:16.074 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is not done and Task 2 is not done. 
15:35:16.386 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is not done and Task 2 is not done. 
15:35:16.700 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Task 1 is not done and Task 2 is not done. 
15:35:17.015 [main] DEBUG [c.t.t.c.f.SquareCalculatorIntegrationTest] >>> Test givenExecutorIsMultiThreaded_whenTwoExecutionsAreTriggered_thenRunInParallel took 1246 ms
```

现在看起来好多了。我们可以看到这两个任务同时开始和结束运行，整个过程大约只需要1秒就能完成。

还有其他工厂方法可以用来创建线程池，比如[Executors.newCachedThreadPool()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html#newCachedThreadPool())，它会重用之前使用过的可用线程，以及[Executors.newScheduledThreadPool()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html#newScheduledThreadPool(int))，它安排任务在给定延迟后运行。

## 5. ForkJoinTask概述

[ForkJoinTask](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ForkJoinTask.html)是一个实现Future的抽象类，能够运行由ForkJoinPool中少量实际线程托管的大量任务。

在本节中，我们将快速介绍ForkJoinPool的主要特性。有关该主题的详细介绍，请阅读[Java中Fork/Join框架介绍](https://www.baeldung.com/java-fork-join)一文。

ForkJoinTask的主要特点是它通常会生成新的子任务，作为完成其主任务所需工作的一部分。它通过调用[fork()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ForkJoinTask.html#fork())生成新任务，并使用[join()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ForkJoinTask.html#join())收集所有结果，就如该类类名所描述的那样。

有两个抽象类实现了ForkJoinTask：[RecursiveTask](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/RecursiveTask.html)，它在完成时返回一个值；以及[RecursiveAction](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/RecursiveAction.html)，它不返回任何内容。顾名思义，这些类主要用于递归任务，如文件系统导航或复杂的数学计算。

让我们扩展前面的示例，创建一个类，给定一个整数，该类将计算其所有阶乘元素的平方和。例如，如果我们将数字4传给FactorialSquareCalculator，我们应该得到4²+3²+2²+1²之和的结果，即30。

首先，我们需要创建一个RecursiveTask的具体实现，并实现其compute()方法。该方法是我们编写业务逻辑的地方：

```java
class FactorialSquareCalculator extends RecursiveTask<Integer> {
    public static final long serialVersionUID = 1L;

    final private Integer n;

    FactorialSquareCalculator(Integer n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n <= 1)
            return n;
        FactorialSquareCalculator calculator = new FactorialSquareCalculator(n - 1);
        calculator.fork();
        return n * n + calculator.join();
    }
}
```

请注意，我们是如何通过在compute()方法中创建FactorialSquareCalculator的新实例来实现递归的。通过调用非阻塞方法fork()，我们要求ForkJoinPool启动该子任务的执行。

join()方法将返回该计算的结果，我们将把当前计算的结果与上一步相加。

现在我们只需要创建一个ForkJoinPool来处理执行和线程管理：

```java
@Test
void whenCalculatesFactorialSquare_thenReturnCorrectValue() {
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    FactorialSquareCalculator calculator = new FactorialSquareCalculator(10);
    forkJoinPool.execute(calculator);
      
    assertEquals(385, calculator.join(), "The sum of the squares from 1 to 10 is 385");
}
```

## 6. 总结

在本文中，我们全面介绍了Future接口，涉及它的所有方法。我们还演示了如何利用线程池来触发多个并行操作。还简要介绍了ForkJoinTask类的主要方法fork()和join()。

还有许多其他关于Java中并行和异步操作的文章。以下是与Future接口密切相关的三个，其中一些在文章中已经提到：

+ [CompletableFuture介绍](https://www.baeldung.com/java-completablefuture)：Future的实现，在Java 8中引入了许多额外的特性。
+ [Java中的Fork/Join框架介绍](https://www.baeldung.com/java-fork-join)：关于我们在第5节中介绍的ForkJoinTask的详细说明。
+ [Java ExecutorService介绍](https://www.baeldung.com/java-executor-service-tutorial)：全面介绍ExecutorService接口。
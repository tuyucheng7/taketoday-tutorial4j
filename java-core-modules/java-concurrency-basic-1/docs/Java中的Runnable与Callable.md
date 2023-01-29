## 1. 概述

自Java早期以来，多线程一直是Java的一个主要方面。Runnable是为表示多线程任务而提供的核心接口，Java 1.5提供了Callable作为Runnable的改进版本。

在本教程中，我们将探讨两个接口的差异和应用。

## 2. 执行机制

两个接口都被设计为表示可以由多个线程运行的任务。Runnable任务可以使用Thread类或ExecutorService运行，而Callable任务只能使用后者运行。

## 3. 返回值

让我们更深入地了解一下这些接口处理返回值的方式。

### 3.1 Runnable

Runnable接口是一个函数接口，只有一个run()方法，它不接收任何参数，也不返回任何值。

这适用于我们不需要执行线程返回结果的情况，例如，对传入的事件做日志记录：

```java
public interface Runnable {
    void run();
}
```

让我们通过一个例子来理解这一点：

```java
public class EventLoggingTask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(EventLoggingTask.class);

    @Override
    public void run() {
        String message = "Message read from the event queue";
        logger.info("Message read from event queue is " + message);
    }
}
```

在本例中，线程将从队列中读取一条消息，并将其记录在日志文件中。任务没有返回任何值；

我们可以使用ExecutorService启动该任务：

```java
public class TaskRunner {

    public static void main(String[] args) {
        executeTask();
    }

    private static void executeTask() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        EventLoggingTask task = new EventLoggingTask();
        Future<?> future = executorService.submit(task);
        executorService.shutdown();
    }
}
```

在这种情况下，submit()方法返回的Future对象将不包含任何值。

### 3.2 Callable

Callable接口是一个泛型接口，包含一个call()方法，该方法返回一个泛型类型V的值：

```java
public interface Callable<V> {
    V call() throws Exception;
}
```

让我们来看一个计算数字阶乘的例子：

```java
public class FactorialTask implements Callable<Integer> {
    int number;

    public FactorialTask(int number) {
        this.number = number;
    }

    public Integer call() throws InvalidParameterException {
        int fact = 1;
        if (number < 0)
            throw new InvalidParameterException("Number must be positive");

        for (int count = number; count > 1; count--)
            fact = fact * count;

        return fact;
    }

    private static class InvalidParameterException extends Exception {
        public InvalidParameterException(String message) {
            super(message);
        }
    }
}
```

call()方法的结果将在Future对象中返回：

```java
class FactorialTaskManualTest {
    private ExecutorService executorService;

    @BeforeEach
    void setup() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Test
    void whenTaskSubmitted_ThenFutureResultObtained() throws ExecutionException, InterruptedException {
        FactorialTask task = new FactorialTask(5);
        Future<Integer> future = executorService.submit(task);
		
        assertEquals(120, future.get().intValue());
    }

    @AfterEach
    void cleanup() {
        executorService.shutdown();
    }
}
```

## 4. 异常处理

让我们看看它们在异常处理方面的不同。

### 4.1 Runnable

**由于方法签名没有指定“throws”子句，因此无法传播进一步受检异常**。

### 4.2 Callable

Callable的call()方法包含“throws Exception”子句，因此我们可以轻松地进一步传播受检异常：

```java
public class FactorialTask implements Callable<Integer> {

    public Integer call() throws InvalidParamaterException {
        int fact = 1;
        if (number < 0)
            throw new InvalidParamaterException("Number must be positive");
        for (int count = number; count > 1; count--)
            fact = fact * count;
        return fact;
    }
}
```

如果使用ExecutorService运行Callable，则会在Future对象中收集异常。可以通过调用Future.get()方法来检查该对象，这将抛出一个ExecutionException，它包装了原始异常：

```java
@Test
void whenException_ThenCallableThrowsIt() throws ExecutionException, InterruptedException {
    FactorialTask task = new FactorialTask(-5);
    Future<Integer> future = executorService.submit(task);
	
    assertThrows(ExecutionException.class, future::get);
}
```

在上面的测试中，当我们传递一个无效的数字时，会抛出ExecutionException。我们可以在此异常对象上调用getCause()方法来获取原始的受检异常。

如果我们不调用Future类的get()方法，那么call()方法引发的异常将不会被引出，并且任务仍将标记为已完成：

```java
@Test
void whenException_ThenCallableDoesntThrowsItIfGetIsNotCalled() {
    FactorialTask task = new FactorialTask(-5);
    Future<Integer> future = executorService.submit(task);
	
    assertFalse(future.isDone());
}
```

尽管我们已经在FactorialTask抛出了InvalidParameterException异常，但上述测试仍将成功通过。

## 5. 总结

在本文中，我们探讨了Runnable和Callable接口之间的区别。
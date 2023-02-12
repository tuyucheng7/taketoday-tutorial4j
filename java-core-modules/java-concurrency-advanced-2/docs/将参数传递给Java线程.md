## 1. 概述

在本教程中，我们将介绍可用于将参数传递给Java[线程](https://www.baeldung.com/java-thread-lifecycle)的不同选项。

## 2. 线程基础

作为快速提醒，我们可以通过实现[Runnable](https://www.baeldung.com/java-runnable-vs-extending-thread)或[Callable](https://www.baeldung.com/java-runnable-callable)在Java中创建线程。

要运行线程，我们可以调用Thread#start(通过传递Runnable实例)或通过[使用线程池](https://www.baeldung.com/thread-pool-java-and-guava)将任务提交给[ExecutorService](https://www.baeldung.com/java-executor-service-tutorial)。

**不过，这两种方法都不接收任何额外的参数**。

让我们看看如何将参数传递给线程。

## 3. 在构造函数中传递参数

我们可以向线程传递参数的第一种方法是简单地将其提供给它们的构造函数中的Runnable或Callable。

让我们创建一个AverageCalculator，它接收一个数字数组并返回其平均值：

```java
public class AverageCalculator implements Callable<Double> {
    int[] numbers;

    public AverageCalculator(int... parameter) {
        this.numbers = parameter == null ? new int[0] : parameter;
    }

    @Override
    public Double call() {
        return IntStream.of(this.numbers).average().orElse(0d);
    }
}
```

接下来，我们将向AverageCalculator线程提供一些数字并验证输出：

```java
@Test
void whenSendingParameterToCallable_thenSuccessful() throws Exception {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<Double> result = executorService.submit(new AverageCalculator(1, 2, 3));
    try {
        assertEquals(Double.valueOf(2.0), result.get());
    } finally {
        executorService.shutdown();
    }
}
```

请注意，**这有效的原因是我们在启动线程之前已经将其状态交给了我们的类**。

## 4. 通过闭包发送参数

向线程传递参数的另一种方法是[创建闭包](https://www.baeldung.com/cs/closure)。

闭包是一个可以继承其父作用域的作用域-**我们可以在Lambdas和匿名内部类中看到它**。

让我们扩展前面的示例并创建两个线程。

第一个将计算平均值：

```java
executorService.submit(() -> IntStream.of(numbers).average().orElse(0d));
```

第二个将求和：

```java
executorService.submit(() -> IntStream.of(numbers).sum());
```

让我们看看如何将相同的参数传递给两个线程并获得结果：

```java
@Test
void whenParametersToThreadWithLambda_thenParametersPassedCorrectly() throws Exception {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    int[] numbers = new int[]{4, 5, 6};
    try {
        Future<Integer> sumResult = executorService.submit(() -> IntStream.of(numbers).sum());
        Future<Double> averageResult = executorService.submit(() -> IntStream.of(numbers)
            .average()
            .orElse(0d));
        assertEquals(Integer.valueOf(15), sumResult.get());
        assertEquals(Double.valueOf(5.0), averageResult.get());
    } finally {
        executorService.shutdown();
    }
}
```

需要记住的一件重要事情是[保持参数为有效final](https://www.baeldung.com/java-8-lambda-expressions-tips)状态，否则我们将无法将它们交给闭包。

**此外，相同的并发规则适用于此处和任何地方**。如果我们在线程运行时更改numbers数组中的值，则无法保证在不引入一些[同步](https://www.baeldung.com/java-synchronized)的情况下其他线程会看到此更改。

最后，如果我们使用的是旧版本的Java，那么也可以用匿名内部类：

```java
final int[] numbers = { 1, 2, 3 };
Thread parameterizedThread = new Thread(new Callable<Double>() {
    @Override
    public Double call() {
        return calculateTheAverage(numbers);
    }
});
parameterizedThread.start();
```

## 5. 总结

在本文中，我们介绍了可用于将参数传递给Java线程的不同方式。
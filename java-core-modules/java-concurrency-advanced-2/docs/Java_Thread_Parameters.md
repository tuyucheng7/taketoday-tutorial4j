## 1. 概述

在本文中，我们将介绍可用于向Java线程传递参数的不同方法。

## 2. Thread基础

首先，我们可以通过实现Runnable或Callable在Java中创建线程。

要运行线程，我们可以调用Thread#start(通过传递Runnable实例)或通过使用线程池将任务提交给ExecutorService。

不过，这两种方法都不接收任何额外的参数。

让我们看看如何将参数传递给线程。

## 3. 在构造函数中传递参数

我们可以向线程传递参数的第一种方法是简单地将其提供给它们的构造函数中的Runnable或Callable。

让我们创建一个AverageCalculator，它接收一个数组并返回其平均值：

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

接下来，我们将向AverageCalculator线程提供一些数字，并验证输出：

```java
class ParameterizedThreadUnitTest {

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
}
```

注意，这样做之所以有效，是因为我们在启动线程之前已经将其状态交给了类。

## 4. 通过闭包发送参数

向线程传递参数的另一种方法是创建闭包。

闭包是一个可以继承其父作用域的作用域，我们可以在lambdas和匿名内部类中看到它。

让我们扩展前面的示例并创建两个线程。

第一个将计算平均值：

```text
executorService.submit(() -> IntStream.of(numbers).average().orElse(0d));
```

第二个将求和：

```text
executorService.submit(() -> IntStream.of(numbers).sum());
```

让我们看看如何将相同的参数传递给两个线程并获得结果：

```java
class ParameterizedThreadUnitTest {

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
}
```

需要记住的一件重要事情是，有效地保持参数为final状态，否则我们将无法将其交给闭包。

同样，这里的并发规则适用于所有地方。
如果我们在线程运行时更改numbers数组中的一个值，则无法保证在不引入一些同步的情况下其他线程会看到此更改。

最后，如果我们使用的是旧版本的Java，那么也可以用匿名内部类：

```text
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
## 1. 概述

本文介绍CompletableFuture类的功能和用例，该类是作为Java 8并发API改进而引入的。

## 2. Java中的异步计算

异步计算很难推理。通常，我们希望将任何计算看作一系列步骤，
但在异步计算的情况下，表示为回调的操作往往分散在代码中，或者深入嵌套在彼此内部。
当我们需要处理其中一个步骤中可能发生的错误时，情况会变得更糟糕。

Java 5中添加了Future接口作为异步计算的结果，但它没有任何方法来组合这些计算或处理可能的错误。

Java 8引入了CompletableFuture类。除了Future接口，它还实现了CompletionStage接口。
这个接口定义了一个异步计算步骤的契约，我们可以将它与其他步骤结合起来。

CompletableFuture同时也是一个框架，有大约50种不同的方法用于组合、合并和执行异步计算步骤以及处理错误。

如此庞大的API可能会让人不知所措，但这些API大多属于几个明确且不同的用例。

## 3. 将CompletableFuture用作简单的Future

首先，CompletableFuture类实现了Future接口，所以我们可以将它作为一个Future的实现，但需要额外的完成逻辑。

例如，我们可以使用无参构造函数创建这个类的实例来表示Future的结果，将其交给消费者，并在将来的某个时间使用complete()方法完成它。
消费者可以使用get()方法阻塞当前线程，直到提供此结果。

在下面的例子中，我们有一个方法可以创建一个CompletableFuture实例，然后在另一个线程中进行一些计算，并立即返回Future。

计算完成后，该方法通过将结果提供给complete()方法来完成Future：

```java
class CompletableFutureLongRunningUnitTest {

    private Future<String> calculateAsync() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            TimeUnit.MILLISECONDS.sleep(500);
            completableFuture.complete("Hello");
            return null;
        });
        return completableFuture;
    }
}
```

为了分离计算，我们使用Executor API。这种创建和完成CompletableFuture的方法可以与任何并发机制或API(包括原始Thread)一起使用。

请注意，calculateAsync()方法返回一个Future的实例。

我们只需调用该方法，接收Future的实例，并在已经准备好阻塞，以获取结果时调用它的get()方法。

还要注意get()方法会抛出一些受检异常，
即ExecutionException(封装计算期间发生的异常)和InterruptedException(表示执行方法的线程被中断的异常)：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenRunningCompletableFutureAsynchronously_thenGetMethodWaitsForResult() throws InterruptedException, ExecutionException {
        Future<String> completableFuture = calculateAsync();
        String result = completableFuture.get();
        assertEquals("Hello", result);
    }
}
```

如果我们已经知道一个计算的结果，我们可以使用静态completedFuture()方法和一个表示这个计算结果的参数。
因此，Future的get()方法永远不会阻塞，而是立即返回此结果：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenRunningCompletableFutureWithResult_thenGetMethodReturnsImmediately() throws ExecutionException, InterruptedException {
        Future<String> completableFuture = CompletableFuture.completedFuture("Hello");
        String result = completableFuture.get();
        assertEquals("Hello", result);
    }
}
```

作为替代方案，我们可能希望[取消Future的执行]()。

## 4. 封装计算逻辑的CompletableFuture

上面的代码允许我们选择任何并发执行机制，但是如果我们想跳过这些样板代码，简单地异步执行一些代码呢？

静态方法runAsync()和supplyAsync()允许我们从Runnable和Supplier函数类型中创建一个CompletableFuture实例。

Runnable和Supplier都是函数式接口，由于新的Java 8特性，允许将它们的实例作为lambda表达式传递。

Runnable接口与线程中使用的旧接口相同，不允许返回值。

Supplier接口是一个泛型函数接口，它有一个没有参数且返回泛型类型值的方法。

这允许我们提供一个lambda表达式作为Supplier实例，用于进行计算并返回结果。这很简单：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenCreatingCompletableFutureWithSupplyAsync_thenFutureReturnValue() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
        assertEquals("Hello", future.get());
    }
}
```

## 5. 异步计算的处理结果

处理计算结果最通用的方法是将其提供给函数。thenApply()方法正是这样做的；
它接收一个Function实例，用它来处理结果，并返回一个Future，其中包含一个函数返回的值：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenAddingThenApplyToFuture_thenFunctionExecutesAfterComputationIsFinished() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> completableFuture = future.thenApply(s -> s + " World");
        assertEquals("Hello World", completableFuture.get());
    }
}
```

如果我们不需要在Future链中返回值，我们可以使用Consumer函数接口的实例。它的单一方法接受一个参数并返回void。

在CompletableFuture中有一个用于此用例的方法。thenAccept()方法接收Consumer并将计算结果传递给Consumer。
然后最后的Future.get()调用返回Void类型的实例：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenAddingThenAcceptToFuture_thenFunctionExecutesAfterComputationIsFinished() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<Void> future = completableFuture.thenAccept(s -> LOG.debug("Computation returned: {}", s));
        future.get();
    }
}
```

最后，如果我们既不需要计算的值，也不想在链的末尾返回某个值，那么我们可以将一个Runnable的lambda传递给thenRun()方法。
在下面的示例中，我们只是在调用future.get()后在控制台中打印日志:

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenAddingThenRunToFuture_thenFunctionExecutesAfterComputationIsFinished() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<Void> future = completableFuture.thenRun(() -> LOG.debug("Computation finished."));
        future.get();
    }
}
```

## 6. 组合Future

CompletableFuture API最强大的功能是能够在计算步骤链中组合CompletableFuture实例。

这种链接的结果本身就是一个CompletableFuture，允许进一步的链接和组合。这种方法在函数式语言中普遍存在，通常被称为一元设计模式。

在下面的示例中，我们使用thenCompose()方法按顺序链接两个Future。

请注意，此方法使用一个返回CompletableFuture实例的Function。
此方法的参数是上一计算步骤的结果。这允许我们在下一个CompletableFuture的lambda中使用该值：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenUsingThenCompose_thenFuturesExecuteSequentially() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));
        assertEquals("Hello World", completableFuture.get());
    }
}
```

thenCompose()方法与thenApply()一起实现了一元模式的基本构建块。
它们与Stream和Optional类的map()和flatMap()方法密切相关，Java 8中也提供了这些方法。

这两种方法都接收一个Function参数并将其应用于计算结果，但thenCompose(flatMap)方法接收一个返回另一个相同类型对象的函数。
这种功能结构允许将这些类的实例组合为构建块。

如果我们想要执行两个独立的Future并对它们结果进行处理，我们可以使用thenCombine()方法，
该方法接收Future和一个带有两个参数的BiFunction来处理这两个结果：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenUsingThenCombine_thenWaitForExecutionOfBothFutures() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCombine(CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> s1 + s2);
        assertEquals("Hello World", completableFuture.get());
    }
}
```

一个更简单的情况是，我们想用两个Future的结果进行处理，但不需要将任何结果值传递给一个Future链。thenAcceptBoth()用于这种情况：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenUsingThenAcceptBoth_thenWaitForExecutionOfBothFutures() {
        CompletableFuture.supplyAsync(() -> "Hello")
                .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> LOG.debug("Computation resulted: {}", s1 + s2));
    }
}
```

此时，thenAcceptBoth()方法接收的是带有两个参数的BiConsumer函数。

## 7. thenApply()和thenCompose()之间的区别

在前面的部分中，我们已经演示了关于thenApply()和thenCompose()的用法。
这两个API都有助于链接不同的CompletableFuture调用，但这两个方法的用法不同。

### 7.1 thenApply()

我们可以使用此方法处理上一次调用的结果。但是，要记住的关键点是返回类型将组合所有调用。

因此，当我们想要转换CompletableFuture调用的结果时，此方法非常有用：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenPassingTransformation_thenFunctionExecutionWithThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> finalResult = compute().thenApply(s -> s + 1);
        assertEquals(11, finalResult.get());
    }

    public CompletableFuture<Integer> compute() {
        return CompletableFuture.supplyAsync(() -> 10);
    }
}
```

### 7.2 thenCompose()

thenCompose()方法与thenApply()类似，都返回一个新的CompletionStage。
但是，thenCompose()使用前一个阶段作为参数。它将扁平化并直接返回带有结果的Future，而不是我们在thenApply()中观察到的嵌套Future：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenPassingPreviousStage_thenFunctionExecutionWithThenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> finalResult = compute().thenCompose(this::computeAnother);
        assertEquals(20, finalResult.get());
    }

    public CompletableFuture<Integer> computeAnother(Integer i) {
        return CompletableFuture.supplyAsync(() -> 10 + i);
    }
}
```

因此，如果我们想链接多个CompletableFuture，那么最好使用thenCompose()。

另外，请注意这两种方法之间的区别类似于map()和flatMap()之间的区别。

## 8. 并行运行多个Future

当我们需要并行执行多个Future时，我们通常希望等待它们全部执行，然后再处理它们的组合结果。

CompletableFuture.allOf()静态方法允许等待作为可变参数提供的所有Future完成：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenFutureCombinedWithAllOfCompletes_thenAllFuturesAreDone() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello ");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Beautiful ");
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);
        combinedFuture.get();

        assertTrue(future1.isDone());
        assertTrue(future2.isDone());
        assertTrue(future3.isDone());

        String combined = Stream.of(future1, future2, future3)
                .map(CompletableFuture::join)
                .collect(Collectors.joining());
        assertEquals("Hello Beautiful World", combined);
    }
}
```

请注意，CompletableFuture.allOf()的返回类型CompletableFuture<Void>。
这种方法的局限性在于，它不会返回所有Future的组合结果。相反，我们必须手动从Future中获取结果。
幸好的是，CompletableFuture.join()方法和Java 8 Streams API使其变得简单：

CompletableFuture.join()方法类似于get()方法，但如果Future不能正常完成，它会抛出一个非受检异常。
这使得可以将其用作Stream.map()方法中的方法引用。

## 9. 处理错误

对于异步计算步骤链中的错误处理，我们必须以类似的方式采取try/catch用法。

CompletableFuture类允许我们用一种特殊的handle方法来处理异常，而不是在语法块中捕获异常。
此方法接收两个参数：计算结果(如果成功完成)和抛出的异常(如果某些计算步骤没有正常完成)。

在下面的示例中，当问候语的异步计算因未提供name而出现错误时，我们使用handle()方法提供默认值：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenFutureThrows_thenHandleMethodReceivesException() throws ExecutionException, InterruptedException {
        String name = null;
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            if (name == null) {
                throw new RuntimeException("Computation error!");
            }
            return "Hello, " + name;
        }).handle((s, ex) -> s != null ? s : "Hello, Stranger!");
        assertEquals("Hello, Stranger!", completableFuture.get());
    }
}
```

作为一种替代方案，假设我们想要像第一个例子中那样，用一个值手动完成Future，但也能够用一个异常完成它。
completeExceptionally()方法就是为此而设计的。
下面示例中的completableFuture.get()方法引发ExecutionException，其原因是RuntimeException：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenCompletingFutureExceptionlly_thenGetMethodThrows() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(new RuntimeException("Calculation failed!"));
        assertThrows(ExecutionException.class, completableFuture::get);
    }
}
```

在上面的示例中，我们本可以使用handle()方法异步处理异常，但使用get()方法，我们可以使用更典型的同步异常处理方法。

## 10. 异步方法

CompletableFuture类中的大多数流式API方法都有两个带有Async后缀的变体。
这些方法通常用于在另一个线程中运行相应的执行步骤。

没有Async后缀的方法使用调用线程运行下一个执行阶段。相比之下，
不带Executor参数的Async方法使用Executor的公共fork/join池实现运行一个步骤，
Executor是通过ForkJoinPool.commonPool()方法访问的。
最后，带有Executor参数的Async方法使用传递的Executor运行一个步骤。

下面是一个修改后的示例，它使用Function实例处理计算结果。
唯一明显的区别是thenApplyAsync()方法，在背后，Function被包装到ForkJoinTask实例中。
这使我们能够更多地并行计算并更有效地利用系统资源：

```java
class CompletableFutureLongRunningUnitTest {
    @Test
    void whenAddingThenApplyAsyncToFuture_thenFunctionExecutesAfterComputationIsFinished() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future = completableFuture.thenApplyAsync(s -> s + " World");
        assertEquals("Hello World", future.get());
    }
}
```

## 11. JDK 9 CompletableFuture API

Java 9通过以下更改增强了CompletableFuture API：

+ 增加了新的工厂方法
+ 支持延迟和超时
+ 改进了对子类化的支持

以及新的实例API：

+ Executor defaultExecutor()
+ CompletableFuture<U> newIncompleteFuture()
+ CompletableFuture<T> copy()
+ CompletionStage<T> minimalCompletionStage()
+ CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor)
+ CompletableFuture<T> completeAsync(Supplier<? extends T> supplier)
+ CompletableFuture<T> orTimeout(long timeout, TimeUnit unit)
+ CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit)

我还有一些静态工具方法：

+ Executor delayedExecutor(long delay, TimeUnit unit, Executor executor)
+ Executor delayedExecutor(long delay, TimeUnit unit)
+ <U> CompletionStage<U> completedStage(U value)
+ <U> CompletionStage<U> failedStage(Throwable ex)
+ <U> CompletableFuture<U> failedFuture(Throwable ex)

最后，为了解决超时问题，Java 9又引入了两个新方法：

+ orTimeout()
+ completeOnTimeout()

更多详细内容请阅读[Java 9 CompletableFuture API的增强]()。

## 12. 总结

在本文中，我们介绍了CompletableFuture类的方法和典型用例。

本文的源代码可在GitHub上获得。
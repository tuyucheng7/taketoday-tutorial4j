## 1. 概述

本教程是CompletableFuture类的功能和用例指南，该类作为Java 8并发API改进引入。

## 2. Java中的异步计算

异步计算很难推理。通常，我们希望将任何计算视为一系列步骤，但在异步计算的情况下，**表示为回调的操作往往分散在代码中，或者深入嵌套在彼此内部**。当我们需要处理其中一个步骤中可能发生的错误时，情况会变得更糟糕。

Future接口是在Java 5中添加的，作为异步计算的结果，但它没有任何方法来组合这些计算或处理可能的错误。

**Java 8引入了CompletableFuture类**。除了Future接口，它还实现了CompletionStage接口。该接口定义了我们可以与其他步骤组合的异步计算步骤的契约。

CompletableFuture同时是一个构建块和一个框架，**具有大约50种不同的方法来组合、合并和执行异步计算步骤以及处理错误**。

如此庞大的API可能会让人不知所措，但这些API大多属于几个明确且不同的用例。

## 3. 使用CompletableFuture作为简单的Future

首先，CompletableFuture类实现了Future接口，因此我们**可以将其用作Future实现，但需要额外的完成逻辑**。

例如，我们可以使用无参数构造函数创建此类的实例来表示一些未来的结果，将其分发给消费者，并在将来的某个时间使用complete()方法完成它。消费者可以使用get()方法阻塞当前线程，直到提供此结果。

在下面的示例中，我们有一个创建CompletableFuture实例的方法，然后在另一个线程中剥离一些计算并立即返回Future。

计算完成后，该方法通过将结果提供给complete()方法来完成Future：

```java
private Future<String> calculateAsync() throws InterruptedException {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();
    
    Executors.newCachedThreadPool().submit(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        completableFuture.complete("Hello");
        return null;
    });
      
    return completableFuture;
}
```

为了剥离计算，我们使用Executor API。这种创建和完成CompletableFuture的方法可以与任何并发机制或API(包括原始线程)一起使用。

请注意，**calculateAsync()方法返回一个Future实例**。

我们只需调用该方法，接收Future实例，并在我们准备好阻塞结果时调用它的get方法。

此外，请注意get()方法会抛出一些受检异常，即ExecutionException(封装计算期间发生的异常)和InterruptedException(表示线程在活动之前或活动期间被中断的异常)：

```java
@Test
void whenRunningCompletableFutureAsynchronously_thenGetMethodWaitsForResult() throws InterruptedException, ExecutionException {
    Future<String> completableFuture = calculateAsync();

    // ... 
    
    String result = completableFuture.get();
    assertEquals("Hello", result);
}
```

**如果我们已经知道计算的结果**，我们可以使用静态的completedFuture()方法和一个代表这个计算结果的参数。因此，Future的get()方法永远不会阻塞，而是立即返回这个结果：

```java
@Test
void whenRunningCompletableFutureWithResult_thenGetMethodReturnsImmediately() throws ExecutionException, InterruptedException {
    Future<String> completableFuture = CompletableFuture.completedFuture("Hello");
    String result = completableFuture.get();
    
    assertEquals("Hello", result);
}
```

作为替代方案，我们可能想要[取消Future的执行](https://www.baeldung.com/java-future#2-canceling-a-future-with-cancel)。

## 4. 具有封装计算逻辑的CompletableFuture

上面的代码允许我们选择任何并发执行机制，但是如果我们想跳过这些样板代码，并简单地异步执行一些代码呢？

静态方法runAsync()和supplyAsync()允许我们从Runnable和Supplier函数类型中相应地创建一个CompletableFuture实例。

Runnable和Supplier都是函数式接口，由于新的Java 8特性，允许将它们的实例作为lambda表达式传递。

Runnable接口与线程中使用的旧接口相同，并且不允许返回值。

Supplier接口是一个泛型函数接口，具有一个没有参数并返回参数化类型值的方法。

这使我们能够**提供lambda表达式作为Supplier实例，用于进行计算并返回结果**。很简单：

```java
@Test
void whenCreatingCompletableFutureWithSupplyAsync_thenFutureReturnValue() throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
    
    assertEquals("Hello", future.get());
}
```

## 5. 异步计算的处理结果

处理计算结果最通用的方法是将其提供给函数。thenApply()方法正是这样做的；它接收一个Function实例，用它来处理结果，并返回一个包含函数返回值的Future：

```java
@Test
void whenAddingThenApplyToFuture_thenFunctionExecutesAfterComputationIsFinished() throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
      
    CompletableFuture<String> completableFuture = future.thenApply(s -> s + " World");
    
    assertEquals("Hello World", completableFuture.get());
}
```

如果我们不需要在Future链中返回值，我们可以使用Consumer函数接口的实例。它的单一方法接收一个参数并返回void。

CompletableFuture中有一个用于此用例的方法。thenAccept()方法接收一个Consumer并将计算结果传递给它。然后最后的Future.get()调用返回一个Void类型的实例：

```java
@Test
void whenAddingThenAcceptToFuture_thenFunctionExecutesAfterComputationIsFinished() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
    
    CompletableFuture<Void> future = completableFuture.thenAccept(s -> LOG.debug("Computation returned: {}", s));
    
    future.get();
}
```

最后，如果我们既不需要计算值也不想在链的末尾返回一些值，那么我们可以将一个Runnable的lambda传递给thenRun()方法。在下面的示例中，我们只是在调用future.get()后在控制台中简单地打印一行:

```java
@Test
void whenAddingThenRunToFuture_thenFunctionExecutesAfterComputationIsFinished() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
    
    CompletableFuture<Void> future = completableFuture.thenRun(() -> LOG.debug("Computation finished."));
      
    future.get();
}
```

## 6. 组合Future

CompletableFuture API最强大的功能是**能够在一系列计算步骤中组合CompletableFuture实例**。

这种链接的结果本身就是一个允许进一步链接和组合的CompletableFuture。这种方法在函数式语言中无处不在，通常被称为一元设计模式。

**在下面的示例中，我们使用thenCompose()方法顺序链接两个Future**。

请注意，此方法接收一个返回CompletableFuture实例的Function。这个函数的参数是上一个计算步骤的结果。这允许我们在下一个CompletableFuture的lambda中使用这个值：

```java
@Test
void whenUsingThenCompose_thenFuturesExecuteSequentially() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture 
        = CompletableFuture.supplyAsync(() -> "Hello")
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));
    
    assertEquals("Hello World", completableFuture.get());
}
```

thenCompose()方法与thenApply()一起实现了monadic模式的基本构建块。它们与Stream和Optional类的map()和flatMap()方法密切相关，这些方法在Java 8中也可用。

两种方法都接收一个Function参数并将其应用于计算结果，但是thenCompose(flatMap)方法接收一个函数，**该函数返回另一个相同类型的对象**。这种函数结构允许将这些类的实例组合成构建块。

如果我们想执行两个独立的Future并对它们的结果做一些事情，我们可以使用thenCombine()方法接收一个Future和一个带有两个参数的BiFunction来处理两个结果：

```java
@Test
void whenUsingThenCombine_thenWaitForExecutionOfBothFutures() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture 
        = CompletableFuture.supplyAsync(() -> "Hello")
            .thenCombine(CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> s1 + s2);
    
    assertEquals("Hello World", completableFuture.get());
}
```

一个更简单的情况是当我们想要对两个Future的结果进行处理但不需要将任何结果值传递到Future链下时。thenAcceptBoth方法可以提供帮助：

```java
@Test
void whenUsingThenAcceptBoth_thenWaitForExecutionOfBothFutures() {
    CompletableFuture.supplyAsync(() -> "Hello")
        .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"), 
            (s1, s2) -> LOG.debug("Computation resulted: {}", s1 + s2));
}
```

## 7. thenApply()和thenCompose()之间的区别

在我们之前的部分中，我们展示了有关thenApply()和thenCompose()的示例。这两个API都有助于链接不同的CompletableFuture调用，但这两个方法的用法是不同的。

### 7.1 thenApply()

**我们可以使用此方法来处理上一次调用的结果**。但是，要记住的关键一点是返回类型将组合所有调用。

因此，当我们想要转换CompletableFuture调用的结果时，这个方法很有用：

```java
@Test
void whenPassingTransformation_thenFunctionExecutionWithThenApply() throws ExecutionException, InterruptedException {
    CompletableFuture<Integer> finalResult = compute().thenApply(s -> s + 1);
    
    assertEquals(11, finalResult.get());
}

public CompletableFuture<Integer> compute() {
    return CompletableFuture.supplyAsync(() -> 10);
}
```

### 7.2 thenCompose()

thenCompose()方法与thenApply()类似，两者都返回一个新的CompletionStage。但是，**thenCompose()使用前一个阶段作为参数**。它将扁平化并直接返回带有结果的Future，而不是像我们在thenApply()中观察到的那样嵌套Future：

```java
@Test
void whenPassingPreviousStage_thenFunctionExecutionWithThenCompose() throws ExecutionException, InterruptedException {
    CompletableFuture<Integer> finalResult = compute().thenCompose(this::computeAnother);
    
    assertEquals(20, finalResult.get());
}

public CompletableFuture<Integer> computeAnother(Integer i) {
    return CompletableFuture.supplyAsync(() -> 10 + i);
}
```

因此，如果想法是链接CompletableFuture方法，那么最好使用thenCompose()。

另外请注意，这两种方法之间的区别类似于[map()和flatMap()之间的区别](https://www.baeldung.com/java-difference-map-and-flatmap)。

## 8. 并行运行多个Future

当我们需要并行执行多个Future时，我们通常希望等待所有Future都执行完毕，然后再处理它们的组合结果。

CompletableFuture.allOf()静态方法允许等待作为可变参数提供的所有Future的完成：

```java
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
```

请注意，CompletableFuture.allOf()的返回类型CompletableFuture<Void\>。此方法的局限性在于它不会返回所有Futures的组合结果。相反，我们必须手动从Future中获取结果。幸运的是，CompletableFuture.join()方法和Java 8 Streams API使它变得简单：

```java
String combined = Stream.of(future1, future2, future3)
    .map(CompletableFuture::join)
    .collect(Collectors.joining(" "));

assertEquals("Hello Beautiful World", combined);
```

CompletableFuture.join()方法类似于get()方法，但如果Future无法正常完成，它会抛出一个非受检异常。这使得可以将其用作Stream.map()方法中的方法引用。

## 9. 处理错误

对于异步计算步骤链中的错误处理，我们必须以类似的方式调整throw/catch习惯用法。

CompletableFuture类允许我们在特殊的handle方法中处理它，而不是在语法块中捕获异常。此方法接收两个参数：计算结果(如果成功完成)和抛出的异常(如果某些计算步骤未正常完成)。

在以下示例中，我们使用handle方法在问候语的异步计算因未提供名称而因错误而完成时提供默认值：

```java
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
```

作为替代方案，假设我们想要手动完成Future的值，如第一个示例中所示，但也有能力以异常完成它。completeExceptionally()方法就是为此而设计的。以下示例中的completableFuture.get()方法抛出一个ExecutionException，其原因是RuntimeException：

```java
@Test
void whenCompletingFutureExceptionlly_thenGetMethodThrows() {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();
    
    completableFuture.completeExceptionally(new RuntimeException("Calculation failed!"));
      
    assertThrows(ExecutionException.class, completableFuture::get); // ExecutionException
}
```

在上面的示例中，我们本可以使用handle()方法异步处理异常，但使用get()方法，我们可以使用更典型的同步异常处理方法。

## 10. 异步方法

CompletableFuture类中大多数的流式API方法都有两个带有Async后缀的附加变体。这些方法通常用于**在另一个线程中运行相应的执行步骤**。

没有Async后缀的方法使用调用线程运行下一个执行阶段。相比之下，不带Executor参数的Async方法使用Executor的公共fork/join池实现运行一个步骤，只要[parallelism > 1](https://www.baeldung.com/java-when-to-use-parallel-stream#2-common-thread-pool)，就可以使用ForkJoinPool.commonPool()访问该实现。最后，带有Executor参数的Async方法使用传递的Executor运行一个步骤。

下面是一个修改后的示例，它使用Function实例处理计算结果。唯一可见的区别是thenApplyAsync()方法，但在底层，函数的应用被包装到ForkJoinTask实例中(有关fork/join框架的更多信息，请参阅文章“[Java中的Fork/Join框架指南](https://www.baeldung.com/java-fork-join)”)。这使我们能够更多地并行化计算并更有效地使用系统资源：

```java
@Test
void whenAddingThenApplyAsyncToFuture_thenFunctionExecutesAfterComputationIsFinished() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
      
    CompletableFuture<String> future = completableFuture.thenApplyAsync(s -> s + " World");
      
    assertEquals("Hello World", future.get());
}
```

## 11. JDK 9 CompletableFuture API

Java 9通过以下更改增强了CompletableFuture API：

+ 添加了新的工厂方法
+ 支持延迟和超时
+ 改进了对子类化的支持

以及新的实例API：

+ Executor defaultExecutor()
+ CompletableFuture<U\> newIncompleteFuture()
+ CompletableFuture<T\> copy()
+ CompletionStage<T\> minimalCompletionStage()
+ CompletableFuture<T\> completeAsync(Supplier<? extends T\> supplier, Executor executor)
+ CompletableFuture<T\> completeAsync(Supplier<? extends T\> supplier)
+ CompletableFuture<T\> orTimeout(long timeout, TimeUnit unit)
+ CompletableFuture<T\> completeOnTimeout(T value, long timeout, TimeUnit unit)

我们现在还有一些静态实用方法：

+ Executor delayedExecutor(long delay, TimeUnit unit, Executor executor)
+ Executor delayedExecutor(long delay, TimeUnit unit)
+ <U\> CompletionStage<U\> completedStage(U value)
+ <U\> CompletionStage<U\> failedStage(Throwable ex)
+ <U\> CompletableFuture<U\> failedFuture(Throwable ex)

最后，为了解决超时问题，Java 9又引入了两个新功能：

+ orTimeout()
+ completeOnTimeout()

这里有详细的文章供进一步阅读：[Java 9 CompletableFuture API增强](https://www.baeldung.com/java-9-completablefuture)。

## 12. 总结

在本文中，我们描述了CompletableFuture类的方法和典型用例。
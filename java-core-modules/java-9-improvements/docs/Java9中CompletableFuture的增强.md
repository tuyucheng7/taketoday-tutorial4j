## 1. 简介

Java 9对CompletableFuture类进行了一些更改，此类更改是作为[JEP 266](https://openjdk.java.net/jeps/266)的一部分引入的，以解决自JDK 8引入以来的常见抱怨和建议，更具体地说，是对延迟和超时的支持，对子类化的更好支持和一些工具方法。

在代码方面，该API添加了8个新方法和5个新静态方法。为了完成这些方法的添加，2400行代码中大约有1500行被更改(根据Open JDK)。

## 2. 添加的实例API

如前所述，Java 9添加了8个实例方法，它们分别是：

1.  Executor defaultExecutor()
2.  CompletableFuture<U> newIncompleteFuture()
3.  CompletableFuture<T> ()
4.  CompletionStage<T> minimalCompletionStage()
5.  CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor)
6.  CompletableFuture<T> completeAsync(Supplier<? extends T> supplier)
7.  CompletableFuture<T> orTimeout(long timeout, TimeUnit unit)
8.  CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit)

### 2.1 defaultExecutor()

方法签名：Executor defaultExecutor()

返回用于未指定Executor的异步方法的默认Executor。

```java
new CompletableFuture().defaultExecutor()
```

这可以被返回executor的子类覆盖，该executor至少提供一个独立线程。

### 2.2 newIncompleteFuture()

方法签名：CompletableFuture<U> newIncompleteFuture()

newIncompleteFuture也称为“虚拟构造函数”，用于获取相同类型的新的CompletableFuture实例。

```java
new CompletableFuture().newIncompleteFuture()
```

这个方法在对CompletableFuture进行子类化时特别有用，主要是因为它在几乎所有返回新CompletionStage的方法中都在内部使用，允许子类控制这些方法返回的子类型。

### 2.3 copy()

方法签名：CompletableFuture<T> copy()

此方法返回一个新的CompletableFuture：

-   当this正常完成时，新的也正常完成
-   当this以异常X完成时，新的也会以异常完成，原因为CompletionException(X)。

```java
new CompletableFuture().copy()
```

这种方法作为“防御性复制”的一种形式可能很有用，可以防止客户端完成，同时仍然能够在CompletableFuture的特定实例上安排相关操作。

### 2.4 minimumCompletionStage()

方法签名：CompletionStage<T> minimumCompletionStage()

此方法返回一个新的CompletionStage，其行为与copy方法描述的完全相同，但是，这种新实例在每次尝试检索或设置解析值时都会引发UnsupportedOperationException 。

```java
new CompletableFuture().minimalCompletionStage()
```

可以使用CompletionStage API上提供的toCompletableFuture方法检索具有所有可用方法的新CompletableFuture。

### 2.5 completeAsync()

completeAsync方法应该用于使用Supplier提供的值异步完成CompletableFuture。

方法签名：

```java
CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor)
CompletableFuture<T> completeAsync(Supplier<? extends T> supplier)
```

这两个重载方法的区别在于存在第二个参数，其中可以指定运行任务的Executor。如果没有提供，将使用默认的Executor(由defaultExecutor方法返回)。

### 2.6 orTimeout()

方法签名：CompletableFuture<T> orTimeout(long timeout, TimeUnit unit)

```java
new CompletableFuture().orTimeout(1, TimeUnit.SECONDS)
```

使用TimeoutException异常地解决CompletableFuture，除非它在指定的超时之前完成。

### 2.7 completeOnTimeout()

方法签名：CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit)

```java
new CompletableFuture().completeOnTimeout(value, 1, TimeUnit.SECONDS)
```

使用指定的值正常完成CompletableFuture，除非它在指定的超时之前完成。

## 3. 添加的静态API

添加的一些静态工具方法包括：

1.  Executor delayExecutor(long delay，TimeUnit unit，Executor executor)
2.  Executor delayExecutor(long delay，TimeUnit unit)
3.  <U> CompletionStage<U> completedStage(U value)
4.  <U> CompletionStage<U> failedStage(Throwable ex)
5.  <U> CompletableFuture<U> failedFuture(Throwable ex)

### 3.1 delayExecutor

方法签名：

```java
Executor delayedExecutor(long delay, TimeUnit unit, Executor executor)
Executor delayedExecutor(long delay, TimeUnit unit)
```

返回一个新的Executor，该Executor在给定的延迟(如果不为正，则不延迟)之后将任务提交给给定的基本Executor。每次延迟都从调用返回的Executor的execute方法开始。如果未指定Executor，则将使用默认Executor(ForkJoinPool.commonPool())。

### 3.2 completedStage和failedStage

方法签名：

```java
<U> CompletionStage<U> completedStage(U value)
<U> CompletionStage<U> failedStage(Throwable ex)
```

此工具方法返回已解析的CompletionStage实例，这些实例要么正常完成并带有值(completedStage)，要么异常完成(failedStage)并带有给定的异常。

### 3.3 failedFuture

方法签名：<U> CompletableFuture<U> failedFuture(Throwable ex)

failedFuture方法增加了指定已完成的异常CompleatebleFuture实例的能力。

## 4. 示例用例

### 4.1 延迟

本例将展示如何将具有特定值的CompletableFuture的完成延迟一秒。这可以通过使用completeAsync方法和delayedExecutor来实现。

```java
CompletableFuture<Object> future = new CompletableFuture<>();
future.completeAsync(() -> input, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
```

### 4.2 完成超时值

另一种实现延迟结果的方法是使用completeOnTimeout方法；本例定义了一个CompletableFuture，如果它在1秒后仍未解析，则将使用给定的输入解析该CompletableFuture。

```java
CompletableFuture<Object> future = new CompletableFuture<>();
future.completeOnTimeout(input, 1, TimeUnit.SECONDS);
```

### 4.3 超时

另一种可能的场景是超时，它通过TimeoutException异常解决未来异常。例如，下面的CompletableFuture在1秒后超时，因为它在此之前没有完成。

```java
CompletableFuture<Object> future = new CompletableFuture<>();
future.orTimeout(1, TimeUnit.SECONDS);
```

## 5. 总结

总之，Java 9对CompletableFuture API进行了一些增强，现在它对子类化有了更好的支持，这要归功于newIncompleteFuture虚拟构造函数，可以控制大多数CompletionStage API返回的CompletionStage实例。

如前所述，它对延迟和超时有更好的支持。添加的工具方法遵循合理的模式，为CompletableFuture提供了一种指定已解析实例的便捷方法。
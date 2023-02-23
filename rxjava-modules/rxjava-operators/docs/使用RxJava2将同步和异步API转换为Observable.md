## 1. 概述

在本教程中，我们将学习**如何使用RxJava 2运算符将传统的同步和异步API转换为Observables**。

我们将创建一些简单的函数来帮助我们详细讨论这些运算符。

## 2. Maven依赖

首先，我们必须添加[RxJava2](https://search.maven.org/search?q=a:rxjava)和[RxJava 2 Extensions](https://search.maven.org/search?q=a:rxjava2-extensions)作为Maven依赖：

```xml
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.2.2</version>
</dependency>
<dependency>
    <groupId>com.github.akarnokd</groupId>
    <artifactId>rxjava2-extensions</artifactId>
    <version>0.20.4</version>
</dependency>
```

## 3. 运算符

RxJava 2为响应式编程的各种用例定义了[大量运算符](https://github.com/ReactiveX/RxJava/wiki/Alphabetical-List-of-Observable-Operators)。

但我们只会讨论一些常用于根据其性质将同步或异步方法转换为Observable的运算符。**这些运算符将函数作为参数并发出从该函数返回的值**。

除了普通的运算符，RxJava 2还定义了一些用于扩展功能的运算符。

让我们探讨如何利用这些运算符来转换同步和异步方法。

## 4. 同步方法转换

### 4.1 使用fromCallable()

该运算符返回一个Observable，当订阅者订阅它时，它会调用作为参数传递的函数，然后发出从该函数返回的值。让我们创建一个返回整数并转换它的函数：

```java
AtomicInteger counter = new AtomicInteger();
Callable<Integer> callable = () -> counter.incrementAndGet();
```

现在，让我们将它转换成一个Observable并通过订阅它来测试它：

```java
Observable<Integer> source = Observable.fromCallable(callable);

for (int i = 1; i < 5; i++) {
    source.test()
        .awaitDone(5, TimeUnit.SECONDS)
        .assertResult(i);
    assertEquals(i, counter.get());
}
```

**每次订阅包装的Observable时，fromCallable()运算符都会延迟执行指定的函数**。为了测试这种行为，我们使用循环创建了多个订阅者。

由于响应式流在默认情况下是异步的，因此订阅者将立即返回。在大多数实际场景中，Callable函数都会有某种延迟才能完成其执行。因此，**我们在测试Callable函数的结果之前添加了最长5秒的等待时间**。

另请注意，我们使用了Observable的[test()](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#test--)方法。**此方法在[测试Observables](https://www.baeldung.com/rxjava-testing)时很方便**。它创建一个TestObserver并订阅了我们的Observable。

### 4.2 使用start()

start()运算符是RxJava 2 Extension模块的一部分。它将异步调用指定的函数并返回一个发出结果的Observable：

```java
Observable<Integer> source = AsyncObservable.start(callable);

for (int i = 1; i < 5; i++) {
    source.test()
        .awaitDone(5, TimeUnit.SECONDS)
        .assertResult(1);
    assertEquals(1, counter.get());
}
```

该函数会立即调用，而不是在订阅者订阅生成的Observable时调用。**对此Observable对象的多个订阅会观察到相同的返回值**。

## 5. 异步方法转换

### 5.1 使用fromFuture()

众所周知，在Java中创建异步方法的最常见方法是使用Future实现，fromFuture方法将Future作为其参数并发出从Future.get()方法获得的值。

首先，让我们将之前创建的函数设为异步函数：

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Integer> future = executor.submit(callable);
```

接下来我们通过转换它来进行测试：

```java
Observable<Integer> source = Observable.fromFuture(future);

for (int i = 1; i < 5; i++) {
    source.test()
        .awaitDone(5, TimeUnit.SECONDS)
        .assertResult(1);
    assertEquals(1, counter.get());
}
executor.shutdown();
```

**再次请注意，每个订阅都观察到相同的返回值**。

现在，Observable的dispose()方法在防止内存泄漏方面非常有用。但在这种情况下，由于Future.get()的阻塞性质，它不会取消Future。

因此，我们可以通过结合源Observable对象的doOnDispose()函数和future的cancel方法来确保取消future：

```java
source.doOnDispose(() -> future.cancel(true));
```

### 5.2 使用startFuture()

顾名思义，此运算符将立即启动指定的Future并在订阅者订阅时发出返回值。与缓存结果以供下次使用的fromFuture运算符不同，**此运算符将在每次被订阅时执行异步方法**：

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
Observable<Integer> source = AsyncObservable.startFuture(() -> executor.submit(callable));

for (int i = 1; i < 5; i++) {
    source.test()
        .awaitDone(5, TimeUnit.SECONDS)
        .assertResult(i);
    assertEquals(i, counter.get());
}
executor.shutdown();
```

### 5.3 使用deferFuture()

**此运算符聚合从Future方法返回的多个Observable，并返回从每个Observable获得的返回值流**。每当新订阅者订阅时，这将启动传递的异步工厂函数。

因此，让我们首先创建异步工厂函数：

```java
List<Integer> list = Arrays.asList(new Integer[] { counter.incrementAndGet(), 
    counter.incrementAndGet(), counter.incrementAndGet() });
ExecutorService exec = Executors.newSingleThreadExecutor();
Callable<Observable<Integer>> callable = () -> Observable.fromIterable(list);
```

然后我们可以做一个快速测试：

```java
Observable<Integer> source = AsyncObservable.deferFuture(() -> exec.submit(callable));
for (int i = 1; i < 4; i++) {
    source.test()
        .awaitDone(5, TimeUnit.SECONDS)
        .assertResult(1,2,3);
}
exec.shutdown();
```

## 6. 总结

在本教程中，我们学习了如何将同步和异步方法转换为RxJava 2 Observable对象。

当然，我们在这里展示的示例是基本实现。但是我们可以将RxJava 2用于更复杂的应用程序，例如视频流和需要分段发送大量数据的应用程序。
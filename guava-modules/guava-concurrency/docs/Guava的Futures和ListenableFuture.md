## 1. 简介

Guava 为我们提供了ListenableFuture ，它在默认的JavaFuture 之上提供了丰富的 API。让我们看看我们如何利用它来发挥我们的优势。

## 2. Future , ListenableFuture和Futures

让我们简要了解一下这些不同的类是什么以及它们之间的关系。

### 2.1. 未来

从Java 5 开始， 我们可以使用[java.util.concurrent.Future](https://www.baeldung.com/java-future) 来表示异步任务。

Future允许我们访问已经完成或将来可能完成的任务的结果，并支持取消它们。

### 2.2. 可听的未来

使用java.util.concurrent.Future时缺少的一项功能是添加侦听器以在完成时运行的功能，这是大多数流行的异步框架提供的一项常见功能。

Guava 通过允许我们将侦听器附加到它的[com.google.common.util.concurrent.ListenableFuture](https://guava.dev/releases/29.0-jre/api/docs/com/google/common/util/concurrent/ListenableFuture.html)来解决这个问题。

### 2.3. 期货

Guava 为我们提供了方便的类[com.google.common.util.concurrent.Futures](https://guava.dev/releases/29.0-jre/api/docs/com/google/common/util/concurrent/Futures)，使我们更容易使用他们的ListenableFuture。

这个类提供了多种与ListenableFuture 交互的方式，其中包括支持添加成功/失败回调，并允许我们通过聚合或转换来协调多个未来。

## 3. 简单使用

现在让我们看看如何以最简单的方式使用ListenableFuture；创建和添加回调。

### 3.1. 创造ListenableFuture

我们可以获得ListenableFuture的最简单方法是将任务提交给ListeningExecutorService(很像我们如何使用普通的ExecutorService 来获得普通的Future)：

```java
ExecutorService execService = Executors.newSingleThreadExecutor();
ListeningExecutorService lExecService = MoreExecutors.listeningDecorator(execService);

ListenableFuture<Integer> asyncTask = lExecService.submit(() -> {
    TimeUnit.MILLISECONDS.sleep(500); // long running task
    return 5;
});
```

请注意我们如何使用MoreExecutors 类将我们的ExecutorService装饰为ListeningExecutorService。 关于MoreExecutors可以参考 [线程池在 Guava 中的实现](https://www.baeldung.com/thread-pool-java-and-guava#Implementation)。

如果我们已经有一个返回Future的 API， 并且我们需要将其转换为ListenableFuture，这很容易 通过初始化其具体实现ListenableFutureTask 来完成：

```java
// old api
public FutureTask<String> fetchConfigTask(String configKey) {
    return new FutureTask<>(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        return String.format("%s.%d", configKey, new Random().nextInt(Integer.MAX_VALUE));
    });
}

// new api
public ListenableFutureTask<String> fetchConfigListenableTask(String configKey) {
    return ListenableFutureTask.create(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        return String.format("%s.%d", configKey, new Random().nextInt(Integer.MAX_VALUE));
    });
}
```

我们需要知道，除非我们将它们提交给Executor，否则这些任务不会运行。 直接与ListenableFutureTask交互并不常见，仅在极少数情况下才会使用(例如：实现我们自己的ExecutorService)。实际使用参考Guava的 [AbstractListeningExecutorService](https://github.com/google/guava/blob/v18.0/guava/src/com/google/common/util/concurrent/AbstractListeningExecutorService.java)。

如果我们的异步任务不能使用ListeningExecutorService或提供的Futures实用方法，我们也可以使用com.google.common.util.concurrent.SettableFuture，我们需要手动设置未来值。对于更复杂的用法，我们还可以考虑com.google.common.util.concurrent.AbstractFuture。

### 3.2. 添加监听器/回调

我们可以将侦听器添加到ListenableFuture的一种方法是使用Futures.addCallback()注册回调，以便我们在成功或失败时访问结果或异常：

```java
Executor listeningExecutor = Executors.newSingleThreadExecutor();

ListenableFuture<Integer> asyncTask = new ListenableFutureService().succeedingTask()
Futures.addCallback(asyncTask, new FutureCallback<Integer>() {
    @Override
    public void onSuccess(Integer result) {
        // do on success
    }

    @Override
    public void onFailure(Throwable t) {
        // do on failure
    }
}, listeningExecutor);
```

我们也可以通过直接将它添加到ListenableFuture来添加一个监听器。请注意，此侦听器将在未来成功或异常完成时运行。另外请注意，我们无权访问异步任务的结果：

```java
Executor listeningExecutor = Executors.newSingleThreadExecutor();

int nextTask = 1;
Set<Integer> runningTasks = ConcurrentHashMap.newKeySet();
runningTasks.add(nextTask);

ListenableFuture<Integer> asyncTask = new ListenableFutureService().succeedingTask()
asyncTask.addListener(() -> runningTasks.remove(nextTask), listeningExecutor);
```

## 4. 用法复杂

现在让我们看看如何在更复杂的场景中使用这些期货。

### 4.1. 扇入

我们有时可能需要调用多个异步任务并收集它们的结果，通常称为扇入操作。

Guava 为我们提供了两种方法。但是，我们应该根据我们的要求谨慎选择正确的方法。假设我们需要协调以下异步任务：

```java
ListenableFuture<String> task1 = service.fetchConfig("config.0");
ListenableFuture<String> task2 = service.fetchConfig("config.1");
ListenableFuture<String> task3 = service.fetchConfig("config.2");
```

扇入多个期货的一种方法是使用Futures.allAsList()方法。如果所有期货都成功，这使我们能够按照提供的期货的顺序收集所有期货的结果。如果这些期货中的任何一个失败，那么整个结果就是一个失败的期货：

```java
ListenableFuture<List<String>> configsTask = Futures.allAsList(task1, task2, task3);
Futures.addCallback(configsTask, new FutureCallback<List<String>>() {
    @Override
    public void onSuccess(@Nullable List<String> configResults) {
        // do on all futures success
    }

    @Override
    public void onFailure(Throwable t) {
        // handle on at least one failure
    }
}, someExecutor);
```

如果我们需要收集所有异步任务的结果，不管它们是否失败，我们可以使用Futures.successfulAsList()。这将返回一个列表，其结果将与传递给参数的任务具有相同的顺序，并且失败的任务将null分配给它们在列表中的相应位置：

```java
ListenableFuture<List<String>> configsTask = Futures.successfulAsList(task1, task2, task3);
Futures.addCallback(configsTask, new FutureCallback<List<String>>() {
    @Override
    public void onSuccess(@Nullable List<String> configResults) {
        // handle results. If task2 failed, then configResults.get(1) == null
    }

    @Override
    public void onFailure(Throwable t) {
        // handle failure
    }
}, listeningExecutor);
```

在上面的用法中我们应该小心，如果未来的任务通常在成功时返回null，它将与失败的任务(也将结果设置为null)无法区分。

### 4.2. 带组合器的扇入

如果我们需要协调返回不同结果的多个期货，上述解决方案可能不够。在这种情况下，我们可以使用扇入操作的组合器变体来协调这种未来组合。

类似于简单的扇入操作，Guava 为我们提供了两种变体；一种在所有任务成功完成时成功，另一种即使某些任务失败也成功，分别使用Futures.whenAllSucceed() 和Futures.whenAllComplete() 方法。

让我们看看如何使用Futures.whenAllSucceed()来组合来自多个期货的不同结果类型：

```java
ListenableFuture<Integer> cartIdTask = service.getCartId();
ListenableFuture<String> customerNameTask = service.getCustomerName();
ListenableFuture<List<String>> cartItemsTask = service.getCartItems();

ListenableFuture<CartInfo> cartInfoTask = Futures.whenAllSucceed(cartIdTask, customerNameTask, cartItemsTask)
    .call(() -> {
        int cartId = Futures.getDone(cartIdTask);
        String customerName = Futures.getDone(customerNameTask);
        List<String> cartItems = Futures.getDone(cartItemsTask);
        return new CartInfo(cartId, customerName, cartItems);
    }, someExecutor);

Futures.addCallback(cartInfoTask, new FutureCallback<CartInfo>() {
    @Override
    public void onSuccess(@Nullable CartInfo result) {
        //handle on all success and combination success
    }

    @Override
    public void onFailure(Throwable t) {
        //handle on either task fail or combination failed
    }
}, listeningExecService);
```

如果我们需要允许某些任务失败，我们可以使用Futures.whenAllComplete()。虽然语义与上面的大部分相似，但我们应该知道失败的期货将在调用 Futures.getDone() 时抛出ExecutionException。

### 4.3. 转换

有时我们需要在成功后转换未来的结果。Guava 为我们提供了两种方法，分别是Futures.transform() 和Futures.lazyTransform()。

让我们看看如何使用Futures.transform()来转换未来的结果。只要转换计算量不大，就可以使用它：

```java
ListenableFuture<List<String>> cartItemsTask = service.getCartItems();

Function<List<String>, Integer> itemCountFunc = cartItems -> {
    assertNotNull(cartItems);
    return cartItems.size();
};

ListenableFuture<Integer> itemCountTask = Futures.transform(cartItemsTask, itemCountFunc, listenExecService);
```

我们还可以使用Futures.lazyTransform() 将转换函数应用于java.util.concurrent.Future。我们需要记住，此选项不返回ListenableFuture ，而是返回普通的java.util.concurrent.Future ，并且每次在结果未来调用get()时都会应用转换函数。

### 4.4. 链接期货

我们可能会遇到我们的期货需要调用其他期货的情况。在这种情况下，Guava 为我们提供了async()变体来安全地链接这些未来以一个接一个地执行。

让我们看看如何使用Futures.submitAsync()从提交的Callable 内部调用未来：

```java
AsyncCallable<String> asyncConfigTask = () -> {
    ListenableFuture<String> configTask = service.fetchConfig("config.a");
    TimeUnit.MILLISECONDS.sleep(500); //some long running task
    return configTask;
};

ListenableFuture<String> configTask = Futures.submitAsync(asyncConfigTask, executor);
```

如果我们想要真正的链接，将一个未来的结果输入到另一个未来的计算中，我们可以使用Futures.transformAsync()：

```java
ListenableFuture<String> usernameTask = service.generateUsername("john");
AsyncFunction<String, String> passwordFunc = username -> {
    ListenableFuture<String> generatePasswordTask = service.generatePassword(username);
    TimeUnit.MILLISECONDS.sleep(500); // some long running task
    return generatePasswordTask;
};

ListenableFuture<String> passwordTask = Futures.transformAsync(usernameTask, passwordFunc, executor);
```

Guava 还为我们提供了Futures.scheduleAsync() 和Futures.catchingAsync()来分别提交计划任务和提供错误恢复的回退任务。虽然它们适用于不同的场景，但我们不会讨论它们，因为它们与其他async()调用类似。

## 5. 使用注意事项

现在让我们研究一下在使用期货时可能遇到的一些常见陷阱以及如何避免它们。

### 5.1. 工作执行者与倾听执行者

使用 Guava futures 时，了解工作执行器和监听执行器之间的区别很重要。例如，假设我们有一个异步任务来获取配置：

```java
public ListenableFuture<String> fetchConfig(String configKey) {
    return lExecService.submit(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        return String.format("%s.%d", configKey, new Random().nextInt(Integer.MAX_VALUE));
    });
}
```

假设我们想为上面的 future 附加一个监听器：

```javascript
ListenableFuture<String> configsTask = service.fetchConfig("config.0");
Futures.addCallback(configsTask, someListener, listeningExecutor);
```

请注意，这里的lExecService 是运行我们的异步任务的执行器，而listeningExecutor是调用我们的侦听器的执行器。

如上所述，我们应该始终考虑将这两个执行程序分开，以避免我们的侦听器和工作程序竞争相同的线程池资源的情况。共享同一个执行器可能会导致我们的繁重任务使侦听器执行饿死。或者一个写得不好的重量级监听器最终阻碍了我们重要的繁重任务。

### 5.2. 小心directExecutor()

虽然我们可以在单元测试中使用MoreExecutors.directExecutor()和MoreExecutors.newDirectExecutorService() 来更轻松地处理异步执行，但我们在生产代码中使用它们时应该小心。

当我们从上述方法中获取到执行器后，我们提交给它的任何任务，无论是重量级任务还是监听器，都将在当前线程上执行。如果当前执行上下文需要高吞吐量，这可能很危险。

例如，使用directExecutor 并在 UI 线程中向其提交重量级任务将自动阻塞我们的 UI 线程。

我们还可能面临这样一种情况，即我们的侦听 器最终会减慢所有其他侦听器的速度(即使是那些与directExecutor 无关的侦听器)。这是因为Guava是在各自的Executor 中执行一个while循环中的所有监听器，但是directExecutor 会导致监听器运行在与while循环相同的线程中。

### 5.3. 嵌套期货是不好的

在使用链式期货时，我们应该注意不要以创建嵌套期货的方式从另一个期货内部调用一个期货：

```java
public ListenableFuture<String> generatePassword(String username) {
    return lExecService.submit(() -> {
        TimeUnit.MILLISECONDS.sleep(500);
        return username + "123";
    });
}

String firstName = "john";
ListenableFuture<ListenableFuture<String>> badTask = lExecService.submit(() -> {
    final String username = firstName.replaceAll("[^a-zA-Z]+", "")
        .concat("@service.com");
    return generatePassword(username);
});
```

如果我们看到有ListenableFuture<ListenableFuture<V>> 的代码，那么我们应该知道这是一个写得很糟糕的未来，因为外部未来的取消和完成有可能竞争，并且取消可能不会传播到内心的未来。

如果我们看到上述情况，我们应该始终使用Futures.async()变体以连接的方式安全地解包这些链式期货。

### 5.4. 小心JdkFutureAdapters.listenInPoolThread()

Guava 建议我们利用其ListenableFuture 的最佳方式是将所有使用Future的代码转换为ListenableFuture。 

如果这种转换在某些情况下不可行， Guava 会使用JdkFutureAdapters.listenInPoolThread()覆盖为我们提供适配器来执行此操作 。虽然这看起来很有帮助，但Guava 警告我们这些是重量级适配器，应尽可能避免使用。

## 六. 总结

在本文中，我们了解了如何使用 Guava 的ListenableFuture来丰富我们对期货的使用，以及如何使用Futures API 来更轻松地使用这些期货。

我们还看到了在使用这些期货和提供的执行者时可能会犯的一些常见错误。
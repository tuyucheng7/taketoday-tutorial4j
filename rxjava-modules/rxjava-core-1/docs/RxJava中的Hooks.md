## 1. 概述

在本教程中，我们将学习[RxJava](https://www.baeldung.com/rx-java)钩子。我们将创建简短的示例来演示钩子在不同情况下的工作方式。

## 2. 什么是RxJava钩子？

顾名思义，RxJava钩子允许我们**钩入Observable、Completable、Maybe、Flowable和Single的生命周期**。此外，RxJava允许我们为Schedulers返回的调度器添加生命周期钩子。此外，我们还可以使用钩子指定一个全局错误处理程序。

在RxJava 1中，类RxJavaHooks用于定义钩子。但是，钩子机制在RxJava 2中完全重写了。**现在类RxJavaHooks不再可用于定义钩子。相反，我们应该使用RxJavaPlugins来实现生命周期钩子**。

RxJavaPlugins类有许多setter方法来设置钩子。这些钩子是全局的。一旦它们被设置，我们就必须调用RxJavaPlugins类的reset()方法，或者调用单个钩子的setter方法来删除它。

## 3. 用于错误处理的钩子

我们可以使用setErrorHandler()方法来处理由于下游的生命周期已经达到其终止状态而无法发出的错误。让我们看看如何实现错误处理程序并对其进行测试：

```java
RxJavaPlugins.setErrorHandler(throwable -> {
    hookCalled = true;
});

Observable.error(new IllegalStateException()).subscribe();

assertTrue(hookCalled);
```

并非所有异常都按原样抛出。但是，RxJava将检查抛出的错误是否是应该按原样通过的已命名的错误案例之一，否则它将被包装到UndeliverableException中。被命名为错误案例的异常是：

-   OnErrorNotImplementedException：当用户忘记在subscribe()方法中添加onError处理程序时
-   MissingBackpressureException：由于运算符错误或并发onNext
-   IllegalStateException：当发生一般协议违规时
-   NullPointerException：标准空指针异常
-   IllegalArgumentException：由于无效的用户输入
-   CompositeException：由于处理异常时发生崩溃

## 4. Completable钩子

RxJava [Completable](https://www.baeldung.com/rxjava-completable)有两个生命周期钩子，现在让我们来看看它们。

### 4.1 setOnCompletableAssembly

RxJava在Completable上实例化运算符和源时将调用此钩子。我们可以使用当前的Completable对象，作为钩子函数的参数提供，对其进行任何操作：

```java
RxJavaPlugins.setOnCompletableAssembly(completable -> {
    hookCalled = true;
    return completable;
});

Completable.fromSingle(Single.just(1));

assertTrue(hookCalled);
```

### 4.2 setOnCompletableSubscribe

RxJava在订阅者订阅Completable之前调用这个钩子：

```java
RxJavaPlugins.setOnCompletableSubscribe((completable, observer) -> {
    hookCalled = true;
    return observer;
});

Completable.fromSingle(Single.just(1)).test();

assertTrue(hookCalled);
```

## 5. Observable钩子

接下来，让我们看看RxJava对Observable的三个生命周期钩子。

### 5.1 setOnObservableAssembly

RxJava在Observable上实例化运算符和源时调用这个钩子：

```java
RxJavaPlugins.setOnObservableAssembly(observable -> {
    hookCalled = true;
    return observable;
});

Observable.range(1, 10);

assertTrue(hookCalled);
```

### 5.2 setOnObservableSubscribe

RxJava在订阅者订阅Observable之前调用这个钩子：

```java
RxJavaPlugins.setOnObservableSubscribe((observable, observer) -> {
    hookCalled = true;
    return observer;
});

Observable.range(1, 10).test();

assertTrue(hookCalled);
```

### 5.3 setOnConnectableObservableAssembly

这个钩子是为ConnectableObservable设计的，ConnectableObservable是Observable本身的变体。唯一的区别是它在被订阅时不会开始发射元素，只有在它的connect()方法被调用时才会发射：

```java
RxJavaPlugins.setOnConnectableObservableAssembly(connectableObservable -> {
    hookCalled = true;
    return connectableObservable;
});

ConnectableObservable.range(1, 10).publish().connect();

assertTrue(hookCalled);
```

## 6. Flowable的钩子

现在，让我们看一下为[Flowable](https://www.baeldung.com/rxjava-2-flowable)定义的生命周期钩子。

### 6.1 setOnFlowableAssembly

RxJava在Flowable上实例化运算符和源时调用此钩子：

```java
RxJavaPlugins.setOnFlowableAssembly(flowable -> {
    hookCalled = true;
    return flowable;
});

Flowable.range(1, 10);

assertTrue(hookCalled);
```

### 6.2 setOnFlowableSubscribe

RxJava在订阅者订阅Flowable之前调用这个钩子：

```java
RxJavaPlugins.setOnFlowableSubscribe((flowable, observer) -> {
    hookCalled = true;
    return observer;
});

Flowable.range(1, 10).test();

assertTrue(hookCalled);
```

### 6.3 setOnConnectableFlowableAssembly

RxJava在ConnectableFlowable上实例化运算符和源时调用此钩子。与ConnectableObservable一样，ConnectableFlowable也仅在我们调用其connect()方法时才开始发射元素：

```java
RxJavaPlugins.setOnConnectableFlowableAssembly(connectableFlowable -> {
    hookCalled = true;
    return connectableFlowable;
});

ConnectableFlowable.range(1, 10).publish().connect();

assertTrue(hookCalled);
```

### 6.4 setOnParallelAssembly

ParallelFlowable用于实现多个发布者之间的并行性。RxJava在ParallelFlowable上实例化运算符和源时调用setOnParallelAssembly()钩子：

```java
RxJavaPlugins.setOnParallelAssembly(parallelFlowable -> {
    hookCalled = true;
    return parallelFlowable;
});

Flowable.range(1, 10).parallel();

assertTrue(hookCalled);
```

## 7. Maybe钩子

[Maybe](https://www.baeldung.com/rxjava-maybe)发射器定义了两个钩子来控制它的生命周期。

### 7.1 setOnMaybeAssembly

RxJava在Maybe上实例化运算符和源时调用这个钩子：

```java
RxJavaPlugins.setOnMaybeAssembly(maybe -> {
    hookCalled = true;
    return maybe;
});

Maybe.just(1);

assertTrue(hookCalled);
```

### 7.2 setOnMaybeSubscribe

RxJava在订阅者订阅Maybe之前调用这个钩子：

```java
RxJavaPlugins.setOnMaybeSubscribe((maybe, observer) -> {
    hookCalled = true;
    return observer;
});

Maybe.just(1).test();

assertTrue(hookCalled);
```

## 8. Single钩子

RxJava也为Single发射器定义了两个基本的钩子。

### 8.1 setOnSingleAssembly

RxJava在Single上实例化运算符和源时调用此钩子：

```java
RxJavaPlugins.setOnSingleAssembly(single -> {
    hookCalled = true;
    return single;
});

Single.just(1);

assertTrue(hookCalled);
```

### 8.2 setOnSingleSubscribe

RxJava在订阅者订阅Single之前调用这个钩子：

```java
RxJavaPlugins.setOnSingleSubscribe((single, observer) -> {
    hookCalled = true;
    return observer;
});

Single.just(1).test();

assertTrue(hookCalled);
```

## 9. Schedulers钩子

与RxJava发射器一样，[Schedulers](https://www.baeldung.com/rxjava-schedulers)也有一堆钩子来控制它们的生命周期。RxJava定义了一个通用的钩子，当我们使用任何类型的调度器时都会调用它。此外，还可以实现特定于各种Schedulers的钩子。

### 9.1 setScheduleHandler

当我们使用任何调度器进行操作时，RxJava会调用这个钩子：

```java
RxJavaPlugins.setScheduleHandler((runnable) -> {
    hookCalled = true;
    return runnable;
});

Observable.range(1, 10)
    .map(v -> v * 2)
    .subscribeOn(Schedulers.single())
    .test();

hookCalled = false;

Observable.range(1, 10)
    .map(v -> v * 2)
    .subscribeOn(Schedulers.computation())
    .test();

assertTrue(hookCalled);
```

由于我们已经使用single()和compute()调度器重复了该操作，因此当我们运行它时，测试用例将在控制台中打印消息两次。

### 9.2 Computation调度器的钩子

computation调度器有两个钩子-即setInitComputationSchedulerHandler和setComputationSchedulerHandler。

当RxJava初始化computation调度器时，它会调用我们使用setInitComputationSchedulerHandler函数设置的钩子。此外，当我们使用Schedulers.computation()调度任务时，它会调用我们使用setComputationSchedulerHandler函数设置的钩子：

```java
RxJavaPlugins.setInitComputationSchedulerHandler((scheduler) -> {
    initHookCalled = true;
    return scheduler.call();
});

RxJavaPlugins.setComputationSchedulerHandler((scheduler) -> {
    hookCalled = true;
    return scheduler;
});

Observable.range(1, 10)
    .map(v -> v * 2)
    .subscribeOn(Schedulers.computation())
    .test();

assertTrue(hookCalled && initHookCalled);
```

### 9.3 IO调度器的钩子

IO调度器也有两个钩子-即setInitIoSchedulerHandler和setIoSchedulerHandler：

```java
RxJavaPlugins.setInitIoSchedulerHandler((scheduler) -> {
    initHookCalled = true;
    return scheduler.call();
});

RxJavaPlugins.setIoSchedulerHandler((scheduler) -> {
    hookCalled = true;
    return scheduler;
});

Observable.range(1, 10)
    .map(v -> v * 2)
    .subscribeOn(Schedulers.io())
    .test();

assertTrue(hookCalled && initHookCalled);
```

### 9.4 Single调度器的钩子

现在，让我们看看Single调度器的钩子：

```java
RxJavaPlugins.setInitSingleSchedulerHandler((scheduler) -> {
    initHookCalled = true;
    return scheduler.call();
});

RxJavaPlugins.setSingleSchedulerHandler((scheduler) -> {
    hookCalled = true;
    return scheduler;
});

Observable.range(1, 10)
  .map(v -> v * 2)
  .subscribeOn(Schedulers.single())
  .test();

assertTrue(hookCalled && initHookCalled);
```

### 9.5 NewThread调度器的钩子

与其他调度器一样，NewThread调度器也定义了两个钩子：

```java
RxJavaPlugins.setInitNewThreadSchedulerHandler((scheduler) -> {
    initHookCalled = true;
    return scheduler.call();
});

RxJavaPlugins.setNewThreadSchedulerHandler((scheduler) -> {
    hookCalled = true;
    return scheduler;
});

Observable.range(1, 15)
  .map(v -> v * 2)
  .subscribeOn(Schedulers.newThread())
  .test();

assertTrue(hookCalled && initHookCalled);
```

## 10. 总结

在本教程中，我们了解了各种RxJava生命周期钩子是什么以及我们如何实现它们。在这些钩子中，错误处理钩子是最值得注意的一个。但是，我们可以将其他钩子用于审计目的，例如记录订阅者数量和其他特定用例。
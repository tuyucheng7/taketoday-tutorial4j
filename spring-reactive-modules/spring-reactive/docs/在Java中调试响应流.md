## 1. 概述

一旦我们开始使用Reactor中的一些数据结构，调试[响应流](https://www.baeldung.com/reactor-core)可能是我们必须面对的主要挑战之一。

考虑到响应式编程在过去几年中越来越受欢迎，了解我们如何有效地执行此任务是一个好主意。

让我们首先使用响应式堆栈设置一个项目，看看为什么这通常很麻烦。

## 2. 有错误的场景

**我们想要模拟一个真实的场景，其中有几个异步进程正在运行，并且我们在代码中引入了一些最终会触发异常的bug**。

为了理解全局，我们的应用程序将使用和处理仅包含一个id、formattedName和一个quantity字段的简单Foo对象流。

### 2.1 分析日志输出

现在，让我们检查一个代码段以及当出现未处理的错误时生成的输出：

```java
public void processFoo(Flux<Foo> flux) {
    flux.map(FooNameHelper::concatFooName)
        .map(FooNameHelper::substringFooName)
        .map(FooReporter::reportResult)
        .subscribe();
}

public void processFooInAnotherScenario(Flux<Foo> flux) {
    flux.map(FooNameHelper::substringFooName)
        .map(FooQuantityHelper::divideFooQuantity)
        .subscribe();
}
```

运行我们的应用程序几秒钟后，我们会发现它不时地记录异常。

仔细查看其中一个错误，我们会发现与此类似的内容：

```java
Caused by: java.lang.StringIndexOutOfBoundsException: String index out of range: 15
    at j.l.String.substring(String.java:1963)
    at cn.tuyucheng.taketoday.debugging.consumer.service.FooNameHelper
      .lambda$1(FooNameHelper.java:38)
    at r.c.p.FluxMap$MapSubscriber.onNext(FluxMap.java:100)
    at r.c.p.FluxMap$MapSubscriber.onNext(FluxMap.java:114)
    at r.c.p.FluxConcatMap$ConcatMapImmediate.innerNext(FluxConcatMap.java:275)
    at r.c.p.FluxConcatMap$ConcatMapInner.onNext(FluxConcatMap.java:849)
    at r.c.p.Operators$MonoSubscriber.complete(Operators.java:1476)
    at r.c.p.MonoDelayUntil$DelayUntilCoordinator.signal(MonoDelayUntil.java:211)
    at r.c.p.MonoDelayUntil$DelayUntilTrigger.onComplete(MonoDelayUntil.java:290)
    at r.c.p.MonoDelay$MonoDelayRunnable.run(MonoDelay.java:118)
    at r.c.s.SchedulerTask.call(SchedulerTask.java:50)
    at r.c.s.SchedulerTask.call(SchedulerTask.java:27)
    at j.u.c.FutureTask.run(FutureTask.java:266)
    at j.u.c.ScheduledThreadPoolExecutor$ScheduledFutureTask
      .access$201(ScheduledThreadPoolExecutor.java:180)
    at j.u.c.ScheduledThreadPoolExecutor$ScheduledFutureTask
      .run(ScheduledThreadPoolExecutor.java:293)
    at j.u.c.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    at j.u.c.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    at j.l.Thread.run(Thread.java:748)
```

基于根本原因，并注意到堆栈跟踪中提到的FooNameHelper类，我们可以想象在某些情况下，我们的Foo对象正在使用比预期短的formattedName值进行处理。

当然，这只是一个简化的案例，解决方案似乎相当明显。

但是让我们想象这是一个真实的案例场景，在没有一些上下文信息的情况下，异常本身并不能帮助我们解决问题。

异常是作为processFoo的一部分触发的，还是作为processFooInAnotherScenario方法的一部分触发的？

在到达此阶段之前，之前的其他步骤是否影响了formattedName字段？

日志记录不会帮助我们找出这些问题。

更糟糕的是，有时甚至不会从我们的函数中抛出异常。

例如，假设我们依靠响应式Repository来持久化我们的Foo对象。如果此时出现错误，我们甚至可能不知道从哪里开始调试我们的代码。

我们需要工具来有效地调试响应流。

## 3. 使用调试会话

**弄清楚我们的应用程序发生了什么的一种选择是使用我们最喜欢的IDE启动调试会话**。

我们必须设置几个条件断点并在流中的每个步骤执行时分析数据流。

实际上，这可能是一项繁琐的任务，尤其是当我们有很多响应式进程正在运行并共享资源时。

此外，出于安全原因，在许多情况下我们无法启动调试会话。

## 4. 使用doOnErrorMethod或使用Subscribe参数记录信息

有时，**我们可以通过提供一个消费者作为subscribe方法的第二个参数来添加有用的上下文信息**：

```java
public void processFoo(Flux<Foo> flux) {

    // ...

    flux.subscribe(foo -> {
        logger.debug("Finished processing Foo with Id {}", foo.getId());
    }, error -> {
        logger.error("The following error happened on processFoo method!", error);
    });
}
```

**注意：值得一提的是，如果我们不需要对subscribe方法进行进一步的处理，我们可以将doOnError函数链接到我们的发布者上**：

```java
flux.doOnError(error -> {
    logger.error("The following error happened on processFoo method!", error);
}).subscribe();
```

现在我们将获得一些关于错误可能来自何处的指导，即使我们仍然没有太多关于生成异常的实际元素的信息。

## 5. 激活Reactor的全局调试配置

[Reactor](https://projectreactor.io/)库提供了一个[Hooks](https://projectreactor.io/docs/core/release/reference/#hooks)类，允许我们可以配置Flux和Mono运算符的行为。

**通过仅添加以下语句，我们的应用程序将检测对发布者方法的调用，包装运算符的构造，并捕获堆栈跟踪**：

```java
Hooks.onOperatorDebug();
```

激活调试模式后，我们的异常日志将包含一些有用的信息：

```shell
16:06:35.334 [parallel-1] ERROR c.t.t.d.consumer.service.FooService
  - The following error happened on processFoo method!
java.lang.StringIndexOutOfBoundsException: String index out of range: 15
    at j.l.String.substring(String.java:1963)
    at c.d.b.c.s.FooNameHelper.lambda$1(FooNameHelper.java:38)
    ...
    at j.l.Thread.run(Thread.java:748)
    Suppressed: r.c.p.FluxOnAssembly$OnAssemblyException: 
Assembly trace from producer [reactor.core.publisher.FluxMapFuseable] :
    reactor.core.publisher.Flux.map(Flux.java:5653)
    c.d.b.c.s.FooNameHelper.substringFooName(FooNameHelper.java:32)
    c.d.b.c.s.FooService.processFoo(FooService.java:24)
    c.d.b.c.c.ChronJobs.consumeInfiniteFlux(ChronJobs.java:46)
    o.s.s.s.ScheduledMethodRunnable.run(ScheduledMethodRunnable.java:84)
    o.s.s.s.DelegatingErrorHandlingRunnable
      .run(DelegatingErrorHandlingRunnable.java:54)
    o.u.c.Executors$RunnableAdapter.call(Executors.java:511)
    o.u.c.FutureTask.runAndReset(FutureTask.java:308)
Error has been observed by the following operator(s):
    |_    Flux.map ⇢ c.d.b.c.s.FooNameHelper
            .substringFooName(FooNameHelper.java:32)
    |_    Flux.map ⇢ c.d.b.c.s.FooReporter.reportResult(FooReporter.java:15)
```

正如我们所看到的，第一部分保持相对相同，但以下部分提供了以下信息：

1.  发布者的程序集跟踪-在这里我们可以确认错误首先在processFoo方法中产生
2.  在第一次触发错误后观察到错误的运算符，以及它们被链接的用户类

注意：在这个例子中，主要是为了清楚地看到这一点，我们在不同的类上添加操作。

我们可以随时打开或关闭调试模式，但这不会影响已经实例化的Flux和Mono对象。

### 5.1 在不同线程上执行运算符

要记住的另一个方面是，即使有不同的线程在流上操作，程序集跟踪也会正确生成。

让我们看看下面的例子：

```java
public void processFoo(Flux<Foo> flux) {
    flux.publishOn(Schedulers.newSingle("foo-thread"))
         // ...
        .publishOn(Schedulers.newSingle("bar-thread"))
        .map(FooReporter::reportResult)
        .subscribeOn(Schedulers.newSingle("starter-thread"))
        .subscribe();
}
```

现在，如果我们检查日志，我们会发现在这种情况下，第一部分可能会发生一些变化，但最后两部分保持不变。

**第一部分是线程堆栈跟踪，因此它只会显示特定线程执行的操作。**

正如我们所看到的，这不是我们调试应用程序时最重要的部分，因此此更改是可以接受的。

## 6. 在单个进程上激活调试输出

在每个响应式进程中检测和生成堆栈跟踪的成本很高。

因此，**我们应该只在关键情况下实施前一种方法**。

总之，**Reactor提供了一种在单个关键进程上启用调试模式的方法，这种方法占用内存较少**。

我们指的是checkpoint运算符：

```java
public void processFoo(Flux<Foo> flux) {
    
    // ...

    flux.checkpoint("Observed error on processFoo", true)
        .subscribe();
}
```

请注意，以这种方式，程序集跟踪将在检查点阶段被记录：

```shell
Caused by: java.lang.StringIndexOutOfBoundsException: String index out of range: 15
	...
Assembly trace from producer [reactor.core.publisher.FluxMap],
    described as [Observed error on processFoo] :
        r.c.p.Flux.checkpoint(Flux.java:3096)
        c.t.t.d.c.s.FooService.processFoo(FooService.java:26)
        c.t.t.d.c.c.ChronJobs.consumeInfiniteFlux(ChronJobs.java:46)
        o.s.s.s.ScheduledMethodRunnable.run(ScheduledMethodRunnable.java:84)
        o.s.s.s.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54)
        j.u.c.Executors$RunnableAdapter.call(Executors.java:511)
        j.u.c.FutureTask.runAndReset(FutureTask.java:308)
Error has been observed by the following operator(s):
    |_    Flux.checkpoint ⇢ c.t.t.d.c.s.FooService.processFoo(FooService.java:26)
```

**我们应该在响应链的末端实施checkpoint方法。**

否则，运算符将无法观察到下游发生的错误。

另外，请注意该库提供了一个重载方法。我们可以避免：

-   如果我们使用无参数选项，则为观察到的错误指定描述
-   通过仅提供自定义描述来生成填充的堆栈跟踪(这是成本最高的操作)

## 7. 记录元素序列

最后，Reactor发布者提供了另一种可能在某些情况下派上用场的方法。

**通过在我们的响应链中调用log方法，应用程序将记录流中的每个元素及其在该阶段的状态**。

让我们在示例中尝试一下：

```java
public void processFoo(Flux<Foo> flux) {
    flux.map(FooNameHelper::concatFooName)
        .map(FooNameHelper::substringFooName)
        .log();
        .map(FooReporter::reportResult)
        .doOnError(error -> {
          logger.error("The following error happened on processFoo method!", error);
        })
        .subscribe();
}
```

并检查日志：

```java
INFO  reactor.Flux.OnAssembly.1 - onSubscribe(FluxMap.MapSubscriber)
INFO  reactor.Flux.OnAssembly.1 - request(unbounded)
INFO  reactor.Flux.OnAssembly.1 - onNext(Foo(id=0, formattedName=theFo, quantity=8))
INFO  reactor.Flux.OnAssembly.1 - onNext(Foo(id=1, formattedName=theFo, quantity=3))
INFO  reactor.Flux.OnAssembly.1 - onNext(Foo(id=2, formattedName=theFo, quantity=5))
INFO  reactor.Flux.OnAssembly.1 - onNext(Foo(id=3, formattedName=theFo, quantity=6))
INFO  reactor.Flux.OnAssembly.1 - onNext(Foo(id=4, formattedName=theFo, quantity=6))
INFO  reactor.Flux.OnAssembly.1 - cancel()
ERROR c.t.t.d.consumer.service.FooService 
    - The following error happened on processFoo method!
...
```

我们可以很容易地看到这个阶段每个Foo对象的状态，以及框架如何在发生异常时取消流。

当然，这种方法也是有成本的，我们还是要适度使用。

## 8. 总结

如果我们不知道正确调试应用程序的工具和机制，我们可能会花费大量时间和精力来解决问题。

如果我们不习惯处理响应式和异步数据结构，则尤其如此，并且我们需要额外的帮助来弄清楚事情是如何工作的。
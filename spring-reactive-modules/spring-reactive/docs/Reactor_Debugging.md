## 1. 概述

一旦我们开始使用Reactor，调试响应流可能是我们必须面对的主要挑战之一。

并且考虑到响应式编程在过去几年中越来越受欢迎，了解我们如何有效地完成这个任务是一个好主意。

## 2. 有bug的场景

我们想要模拟一个真实的场景，其中有几个异步进程正在运行，并且我们在代码中引入了一些最终会触发异常的bug。

为了理解全局，我们的应用程序将使用和处理仅包含一个id、formattedName和一个quantity字段的简单Foo对象流。

### 2.1 分析日志输出

现在，让我们看一段代码以及它在出现未处理的错误时生成的输出：

```
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

在运行应用程序几秒钟后，我们会意识到它会不时记录异常。

仔细查看其中一个错误，我们会发现与此类似的内容：

```text
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

基于根本原因，并注意到堆栈跟踪中提到的FooNameHelper类，我们可以想象在某些情况下，
我们的Foo对象正在使用比预期短的formattedName值进行处理。

当然，这只是一个简化的案例，解决方案似乎相当明显。

但是让我们想象一下这是一个真实案例，如果没有一些上下文信息，异常本身并不能帮助我们解决问题。

异常是作为processFoo的一部分触发的，还是作为processFooInAnotherScenario方法的一部分触发的？

在到达这个阶段之前，之前的其他步骤是否影响了formattedName字段？

日志记录不会帮助我们弄清楚这些问题。

更糟糕的是，有时甚至不会从我们的函数中抛出异常。

例如，假设我们依靠响应式存储库来持久化我们的Foo对象。如果此时出现错误，我们甚至可能不知道从哪里开始调试我们的代码。

我们需要工具来有效地调试响应流。

## 3. 使用调试会话

弄清楚我们的应用程序发生了什么的一种选择是使用我们最喜欢的IDE启动调试会话。

我们必须设置几个条件断点并在流中的每个步骤执行时分析数据流。

实际上，这可能是一项繁琐的任务，尤其是当我们有很多反应式进程在运行并共享资源时。

此外，出于安全原因，在许多情况下我们无法启动调试会话。

## 4. 使用doOnErrorMethod或使用Subscribe参数记录信息

有时，我们可以通过提供Consumer作为subscribe方法的第二个参数来添加有用的上下文信息：


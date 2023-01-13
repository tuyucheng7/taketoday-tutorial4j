## 一、简介

当谈到任何方法或技术时，问题是：我们试图解决什么问题？

RxJava 及其扩展 RxKotlin 是 Reactive Manifesto 的实现，它要求应用程序具有[响应性、弹性、弹性和消息驱动](https://www.reactivemanifesto.org/)。实现这些原则的软件使用与外部数据源的异步通信，对请求生产者施加背压，并在系统出现故障时优雅地降级。

这应该是我们比较 Coroutines 和 Reactive Streams 库的基础。如果它们都合适，我们应该看看哪种方法更具可读性并且在生产中运行得更好。

然而，Kotlin Coroutines 库并不是 RxKotlin 的竞争对手。它的范围要广泛得多。Kotlin 协程方法由两个截然不同的概念组成：suspend词，它只是一个 Kotlin 语言关键字，以及kotlinx.coroutines库提供的该关键字的默认实现。suspend关键字保证协作并发：在进入任何suspend函数时，控制权将提供给可能需要它来继续运行的其他协程。

因此，在它们的基础上，协程与消息传递、背压甚至异步性无关。他们的主要目标是在程序的 CPU 密集型部分之间启用非阻塞等待。这种方法可以让其他协程更好地利用 CPU。当然，通过为我们提供一种创建轻量级线程类协程的方法，该库提供了一种异步编程方法，这在 JVM 世界中是新颖的。

## 2.反应系统是反应灵敏的

让我们阅读[Reactive Manifesto](https://www.reactivemanifesto.org/)，看看我们如何使用 RxKotlin 和 Kotlin Coroutines 来实现它的原则。第一点是系统的响应能力。反应要“快”、“准”，所有“问题”都要“快”。后一个原则通常听起来像“快速失败”。协程可以快速失败吗？绝对：

```kotlin
withTimeout(100.milliseconds) {
    delay(3.seconds)
    // Will throw TimeoutCancellationException
}
```

响应的另一个方面是管理资源，以便处理实际可以处理的请求。同时，控制电路要时刻留有余量。使用协程很容易实现这个目标，协程在等待异步 IO 时不会消耗资源。尽管如此，使用阻塞代码可能会使 CoroutineDispatcher饿死：

```kotlin
repeat(3) {
    starvingContext.launch {
        Thread.sleep(30000) // this code blocks the dispatchers threads
    }
}

runBlocking {
    withTimeout(100.milliseconds) {
        starvingContext.launch { println("A quick task which will never execute") }.join()
    }
}

```

相反，如果我们使用等待函数的非阻塞版本，我们会看到打印的消息：

```kotlin
repeat(3) {
    starvingContext.launch {
        delay(30000) // this function doesn't block
    }
}
runBlocking {
    withTimeout(100.milliseconds) {
        withContext(workingContext.coroutineContext) {
            println("This messages gets to be printed")
        }
    }
}
```

现在让我们看看 Rx 库在响应能力方面提供了什么：

```kotlin
Observable
    .fromCallable {
        Thread.sleep(30000)
        "result"
    }
    .subscribeOn(worker)
    .timeout(100, TimeUnit.MILLISECONDS)
    .subscribe({ msg -> println(msg) }, { worker.shutdown() })
```

我们没有将超时放在长操作周围，而是放在一个单独的运算符之后。不利的一面是，我们可能会注意到Rx 库在什么操作发生在哪里方面不太明确。例如，上面的代码中没有任何内容说明onNext和onError lambda 将在何处运行或哪个线程负责跟踪我们的超时。与此相反，Kotlin 协程将显式并发声明为其主要原则。我们甚至必须提到默认调度程序的名称，以便在它们的线程上运行协程。

在管理并行执行时，Rx 库退回到标准 JVM 线程模型：我们可以操纵线程来运行 Observable 可调用对象并执行订阅者操作。Rx 处理它的方式是创建Scheduler -s：

```kotlin
val worker = Schedulers.computation() // or .io(), or .single()
```

这种抽象比协程范围加上下文加调度程序星座更简单，但我们仍然需要管理线程池并平衡它们之间的大小。

## 3.反应系统是有弹性的

宣言指出：“弹性是通过[](https://www.reactivemanifesto.org/glossary#Replication)、遏制、 [隔离](https://www.reactivemanifesto.org/glossary#Isolation) 和 [授权](https://www.reactivemanifesto.org/glossary#Delegation)实现的。” 它还说：“每个组件的恢复都委托给另一个(外部)组件”。

错误，即 Kotin Coroutines 中的异常，只是正常的异常。如果没有额外的库，例如[ArrowKt](https://arrow-kt.io/)，就不可能在特定级别强制执行错误处理。Kotlin 中的所有异常都是未声明的。这在 Rx 中有不同的处理方式：

```kotlin
Observable.error<CustomException>(CustomException())
    .subscribe({ msg -> println(msg) }, { ex -> failed = ex !is CustomException })
    .dispose()
```

但是，错误处理程序 lambda 不是强制性的，默认行为只是跳过错误并将它们转储到System.err中。这听起来像是协程方法的一个缺点，在协程方法中，未捕获的异常会一直沸腾直到被处理，否则它们会破坏整个应用程序。

当涉及到组件监督时，每个协程都运行在自己的[上下文中](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html)，这些上下文形成了一个层次结构。父上下文负责它们的子上下文和在其中运行的协程。诚然，默认情况下，失败的孩子会毒化并停止整个层次结构。我们必须承认，在错误处理中，Rx 方法更有意义，或者如果它得到更严格的执行，它会更有意义，而协程可以从功能库中受益匪浅。

## 4.反应系统是有弹性的

在 Rx 库在[Java Fibers](https://www.baeldung.com/openjdk-project-loom#loom fiber)上重写之前，Kotlin Coroutines 将继续更具成本效益。关于扩展，Rx 和协程提供相同的功能：如果所有现有线程都忙，[缓存线程池将扩展线程数量。](https://www.baeldung.com/kotlin/create-thread-pool#2-a-cached-thread-pool-is-load-adjustable)我们是否在 Rx Scheduler或 Kotlin CoroutineDispatcher中使用该线程池并不重要。

然而，由于线程是相当大的资源，我们可以启动更少的线程。

## 5.反应式系统是消息驱动的

在这里，我们再次谈到协程是一个比 Rx 库的 Observables 和 Flowables 更底层的概念。我们当然有[工具](https://www.baeldung.com/kotlin/flow-intro)可以在我们的应用程序中设置消息交换，例如Flow和Channel：

```kotlin
val pipeline = Channel<String>()
scopeA.launch {
    (1..10).map {
        pipeline.send(it.toString())
    }
    pipeline.close()
}
withContext(scopeB.coroutineContext) {
    pipeline.consumeAsFlow().map {
        println("Received message: $it")
        it
    }.toList()
}

```

然而，Kotlin Coroutines 并不限制我们使用它们，而没有消息传递就很难使用 Rx 库：我们首先创建一个Observable，它会产生一条或多条消息。

宣言还指出，消息驱动的通信必须是异步的，允许接收方的背压，并支持非阻塞 IO。让我们看看在这些主题上协程与 Rx 的比较。

### 5.1. 异步通信

使用协程异步启动任何进程真的很容易，但我们必须明确这一点：

```kotlin
val a = async { requestOverNetwork(0) }
val b = async { requestOverNetwork(1) }
val c = async { requestOverNetwork(2) }
a.await() + b.await() + c.await()
```

在示例中，三个网络请求将并行运行，协程将在需要其结果时暂停。RxKotlin 呢？

```kotlin
Observable.merge(
    listOf(
        observeOverNetwork(0),
        observeOverNetwork(1),
        observeOverNetwork(2)
    )
).reduce { t1, t2 -> t1 + t2 }
    .subscribe(testSubscriber)
```

显然，它完成了它的工作，但协程语法更清晰。

### 5.2. 背压

同样，我们必须在我们的软件中积极实施这一原则。我们可以使用具有[固有背压机制的](https://www.baeldung.com/kotlin/flow-intro#transparent-back-pressure-from-flows)Flow ，或者 使用带或不带缓冲区的Channel 。Channel将暂停，直到其缓冲区中有空间容纳新消息。

但是我们决定是否使用Flows和Channels。在 Rx 领域，背压更为[复杂](https://www.baeldung.com/rxjava-backpressure)。

### 5.3. 非阻塞IO

协程和 Rx 库都依赖于底层 IO 实现实际上是非阻塞的。然而，协程有一个明显的优势，因为它们可以直接用语言表达非阻塞语句：

```kotlin
private suspend fun AsynchronousFileChannel.asyncRead(dst: ByteBuffer, position: Long = 0): Int = suspendCoroutine {
        read(dst, position, it, object : CompletionHandler<Int, Continuation<Int>> {
            override fun completed(result: Int, attachment: Continuation<Int>) = it.resume(result)
            override fun failed(exc: Throwable, attachment: Continuation<Int>) = it.resumeWithException(exc)
        })
    }
// then later in the application
fileChannel.asyncRead(buffer)
```

当然，Rx 方法也支持非阻塞库，例如 Netty。使用它们的配置非常相似：

```kotlin
val buffer = ByteBuffer.allocate(13)
Observable.create { emitter ->
    fileChannel.read(buffer, 0, emitter, object : CompletionHandler<Int, ObservableEmitter<String>>{
        override fun completed(result: Int, attachment: ObservableEmitter<String>) {
            emitter.onNext(String(buffer.array()))
            emitter.onComplete()
        }
        override fun failed(exc: Throwable, attachment: ObservableEmitter<String>) {
            emitter.tryOnError(exc)
        }
    })
}.subscribe(testSubscriber)
```

## 6. 将 Rx 库与协程一起使用

正如我们所发现的，协程引入的概念比响应式库的概念级别低。我们可以使用这些概念来获得相同的结果。或者，如果我们在构建应用程序时不积极遵循 Reactive Manifesto 原则，它就不会松散耦合或异步。Rx 库使我们处于更严格的基础上。

那么我们不必选择是一件好事。Kotlin 扩展提供了一组适配器函数，可以将 Rx 实体转换为 Kotin Coroutine 实体并返回：

```kotlin
val observable = Observable.just("apple")
val result = observable.awaitSingle()

flow {
    (1..5).forEach { emit(it) }
}.asObservable(this.coroutineContext).subscribe(testSubscriber)
```

一些人在 Rx 库中看到的一个缺点是它们的普遍性。如果我们在应用程序的一个地方使用 Rx 构造，那么在其他地方使用其他方法会变得非常乏味，因为常规代码和 Rx 函数之间的切换有点尴尬。使用 Kotiln 协程作为应用程序的阻塞部分和非阻塞部分之间的粘合剂可能是个好主意。

## 七、总结

在本文中，我们将 Kotlin 协程与 RxKotlin/RxJava 库进行了比较。事实证明，协程很好地涵盖了 Reactive Manifesto 的原则，错误处理可能是个例外。此外，协程更好地植根于语言并且对我们的编码风格做出更少的假设。例如，我们可以随心所欲地发挥作用，而无需立即将整个代码库转换为 monad。

另一方面，对于已经熟悉 Rx 方法的团队来说，继续使用 Reactive 风格可能会更容易。它的好处是 Kotlin 扩展提供了适配器，可以弥合 Rx 和 Kotlin 协程之间的差距，并根据需要同时使用它们。
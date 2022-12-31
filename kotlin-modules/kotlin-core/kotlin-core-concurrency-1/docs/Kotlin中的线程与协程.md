## 1. 简介

在本快速教程中，我们将在Kotlin中创建和执行线程。

稍后，我们将讨论如何完全避免它，支持[Kotlin协程](https://www.baeldung.com/kotlin-coroutines)。

## 2. 创建线程

**在Kotlin中创建线程与在Java中类似**。

我们可以扩展Thread类(尽管不推荐，因为Kotlin不支持多重继承)：

```kotlin
class SimpleThread: Thread() {
    public override fun run() {
        println("${Thread.currentThread()} has run.")
    }
}
```

或者我们可以实现Runnable接口：

```kotlin
class SimpleRunnable: Runnable {
    public override fun run() {
        println("${Thread.currentThread()} has run.")
    }
}
```

同样的，我们在Java中做的，我们可以通过调用start()方法来执行它：

```kotlin
val thread = SimpleThread()
thread.start()
        
val threadWithRunnable = Thread(SimpleRunnable())
threadWithRunnable.start()
```

或者，与Java 8一样，Kotlin支持[SAM Conversions](https://kotlinlang.org/docs/reference/kotlin-interop.html#sam-conversions)，因此我们可以利用它并传递一个lambda：

```kotlin
val thread = Thread {
    println("${Thread.currentThread()} has run.")
}
thread.start()
```

### 2.2 Kotlin thread()函数

另一种方法是考虑Kotlin提供的函数thread()：

```kotlin
fun thread(
    start: Boolean = true,
    isDaemon: Boolean = false,
    contextClassLoader: ClassLoader? = null,
    name: String? = null,
    priority: Int = -1,
    block: () -> Unit
): Thread
```

使用此函数，可以通过以下方式简单地实例化和执行线程：

```kotlin
thread(start = true) {
    println("${Thread.currentThread()} has run.")
}
```

该函数接收五个参数：

-   start：立即运行线程
-   isDaemon：将线程创建为守护线程
-   contextClassLoader：用于加载类和资源的类加载器
-   name：设置线程的名称
-   priority：设置线程的优先级

## 3. Kotlin协程

人们很容易认为产生更多线程可以帮助我们并发执行更多任务，不幸的是，情况并非总是如此。

创建太多线程实际上会使应用程序在某些情况下表现不佳；线程是在对象分配和垃圾收集期间强加开销的对象。

为了克服这些问题，**Kotlin引入了一种编写异步、非阻塞代码的新方法**:[协程](https://www.baeldung.com/kotlin-coroutines)。

与线程类似，协程可以并发运行、等待并相互通信，不同之处在于创建协程比线程便宜得多。

### 3.1 协程上下文

在介绍Kotlin提供的开箱即用的协程构建器之前，我们必须讨论协程上下文。

协程总是在一些包含各种元素的上下文中执行。

主要内容是：

-   [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-job/index.html)：模拟一个可取消的工作流，具有多个状态和一个最终完成的生命周期
-   [Dispatcher](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-coroutine-dispatcher/index.html)：确定相应协程使用哪个或哪些线程执行，有了dispatcher，**我们可以将协程的执行限制在一个特定的线程中，分派到一个线程池中，或者让它不受限制地运行**

在下一阶段描述协程时，我们将了解如何指定上下文。

### 3.2 launch

**launch函数是一个协程构建器，它在不阻塞当前线程的情况下启动一个新的协程**，并将对协程的引用作为Job对象返回：

```kotlin
runBlocking {
    val job = launch(Dispatchers.Default) {  
        println("${Thread.currentThread()} has run.") 
    }
}
```

它有两个可选参数：

-   context：执行协程的上下文，如果未定义，它会从启动它的[CoroutineScope继承上下文](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-coroutine-scope/index.html)
-   start：协程的启动选项，默认情况下，协程会立即安排执行

请注意，上面的代码是在一个共享的后台线程池中执行的，因为我们使用了在[GlobalScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-global-scope/index.html)中启动它的Dispatchers.Default。

或者，我们可以使用使用相同调度程序的GlobalScope.launch：

```kotlin
val job = GlobalScope.launch {
    println("${Thread.currentThread()} has run.")
}
```

当我们使用Dispatchers.Default或GlobalScope.launch时，我们创建了一个顶级协程，尽管它是轻量级的，但它在运行时仍然会消耗一些内存资源。

不同于在[GlobalScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-global-scope/index.html)中启动协程，就像我们通常对线程所做的那样(线程总是全局的)，我们可以在我们正在执行的操作的特定范围内启动协程：

```kotlin
runBlocking {
    val job = launch {
        println("${Thread.currentThread()} has run.")
    }
}
```

在这种情况下，我们在runBlocking协程构建器(我们将在稍后描述)中启动一个新的协程，而不指定上下文。因此，协程将继承 runBlocking的上下文。

### 3.3 async

Kotlin提供的另一个创建协程的函数是async。

**async函数创建一个新协程并返回未来结果作为Deferred<T\>的实例**：

```kotlin
val deferred = async {
    return@async "${Thread.currentThread()} has run."
}
```

deferred是一个非阻塞的可取消Future，它描述了一个对象，该对象充当最初未知结果的代理。

与launch一样，我们可以指定执行协程的上下文以及启动选项：

```kotlin
val deferred = async(Dispatchers.Unconfined, CoroutineStart.LAZY) {
    println("${Thread.currentThread()} has run.")
}
```

**在本例中，我们使用Dispatchers启动了协程，Unconfined在调用者线程中启动协程，但仅在第一个挂起点之前**。 

请注意，当协程不消耗CPU时间或更新任何共享数据时，Dispatchers.Unlimited非常适合。

**此外，Kotlin提供了Dispatchers.IO，它使用按需创建线程的共享池**：

```kotlin
val deferred = async(Dispatchers.IO) {
    println("${Thread.currentThread()} has run.")
}
```

当我们需要进行密集的I/O操作时，建议使用Dispatchers.IO。

### 3.4. runBlocking

我们之前看过runBlocking，但现在让我们更深入地讨论它。

**runBlocking是一个运行新协程并阻塞当前线程直到其完成的函数**。

举例来说，在前面的代码片段中，我们启动了协程，但我们从未等待结果。

为了等待结果，我们必须调用await()挂起方法：

```kotlin
// async code goes here

runBlocking {
    val result = deferred.await()
    println(result)
}
```

await()就是所谓的挂起函数，**挂起函数只允许从协程或另一个挂起函数中调用**，为此，我们将其包含在runBlocking调用中。

我们在main函数和测试中使用runBlocking，以便我们可以将阻塞代码链接到其他以挂起风格编写的代码。

与我们在其他协程构建器中所做的类似，我们可以设置执行上下文：

```kotlin
runBlocking(newSingleThreadContext("dedicatedThread")) {
    val result = deferred.await()
    println(result)
}
```

请注意，我们可以创建一个新线程，我们可以在其中执行协程。然而，专用线程是一种昂贵的资源。而且，当不再需要时，我们应该释放它，甚至更好地在整个应用程序中重用它。

## 4. 总结

在本教程中，我们学习了如何通过创建线程来执行异步、非阻塞代码。

作为线程的替代方案，我们还看到了Kotlin使用协程的方法是如何简单而优雅的。
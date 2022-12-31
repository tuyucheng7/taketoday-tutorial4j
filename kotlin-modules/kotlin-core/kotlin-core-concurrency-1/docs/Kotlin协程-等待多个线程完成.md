## 1. 简介

在本教程中，我们将了解如何同时执行一组任务并等待它们完成，我们将使用[协程而不是线程](https://www.baeldung.com/kotlin/threads-coroutines)作为在Kotlin中实现并发的提升方式。

## 2. 异步等待

Kotlin的[async](https://www.baeldung.com/kotlin/withcontext-vs-async-await)函数允许运行并发协程并返回Deferred<T\>结果，Deferred是一个非阻塞的可取消Future，作为最初未知结果的代理。例如，通过调用Deferred#wait方法，我们等待任务完成，然后我们得到结果。

此外，如果我们有一个Deferred集合，我们可以使用awaitAll扩展来等待所有集合完成：

```kotlin
@Test
fun whenAwaitAsyncCoroutines_thenAllTerminated() {
    val count = AtomicInteger()
    runBlocking {
        val tasks = listOf(
            async(Dispatchers.IO) { count.addAndGet(longRunningTask()) },
            async(Dispatchers.IO) { count.addAndGet(longRunningTask()) }
        )
        tasks.awaitAll()
        Assertions.assertEquals(2, count.get())
    }
}
```

在这里，我们将Dispatchers.IO传递给异步函数，但还有其他几个选项。[Dispatcher](https://www.baeldung.com/kotlin/coroutine-context-dispatchers#dispatchers)负责确定协程的执行线程(或多个线程)，**如果我们不传递任何东西，async块将使用与父块相同的调度程序**。

### 2.1 异步的结构化并发

尽管如此，还是有更好的方法来利用Kotlin的结构化并发。

结构化并发意味着协程的生命周期被限制在它的[CoroutineScope](https://www.baeldung.com/kotlin/coroutine-context-dispatchers#coroutine-scope)范围内，一个协程只能在一个作用域内启动。对于结构化并发，外部作用域只有在其所有子协程完成后才会完成，因此，它确保启动的协程不会丢失，也不会泄漏。此外，它会正确报告错误并确保它们永远不会丢失。

**因此，让我们在父协程中运行我们的并发任务以从结构化并发中获益-我们可以摆脱awaitAll并不得不显式管理它们的生命周期**：

```kotlin
@Test
fun whenParentCoroutineRunAsyncCoroutines_thenAllTerminated() {
    val count = AtomicInteger()
    runBlocking {
        withContext(coroutineContext) {
            async(Dispatchers.IO) { count.addAndGet(longRunningTask()) }
            async(Dispatchers.IO) { count.addAndGet(longRunningTask()) }
        }
        Assertions.assertEquals(2, count.get())
    }
}
```

断言在[withContext](https://www.baeldung.com/kotlin/withcontext-vs-async-await#withContext)块之后执行，withContext块作为父协程，在所有子协程也完成时完成。因此，我们不必显式调用await或awaitAll。

**除了withContext之外，还有其他构建器，例如runBlocking或coroutineScope构建器**。coroutineScope让我们为新协程声明一个作用域，但是，新协程在所有启动的子程序完成之前不会完成。

尽管runBlocking和coroutineScope构建器看起来很相似，因为它们都等待它们的主体及其所有子级完成，但请记住，**runBlocking方法会阻塞当前线程等待，而coroutineScope只是挂起**。

## 3. launch-join

**在我们不需要协程返回值的情况下，我们可以选择使用launch函数**，launch函数是CoroutineScope的扩展，它返回一个Job，我们调用Job#join方法等待Job完成。此外，如果我们有一组作业，则调用joinAll等待所有作业完成：

```kotlin
@Test
fun whenJoinLaunchedCoroutines_thenAllTerminated() {
    val count = AtomicInteger()
    runBlocking {
        val tasks = listOf(
            launch (Dispatchers.IO) { count.addAndGet(longRunningTask()) },
            launch (Dispatchers.IO) { count.addAndGet(longRunningTask()) }
        )
        tasks.joinAll()
        Assertions.assertEquals(2, count.get())
    }
}
```

与async一样，这里我们有结构化并发选项，所以让我们在父协程中创建任务：

```kotlin
@Test
fun whenParentCoroutineLaunchCoroutines_thenAllTerminated() {
    val count = AtomicInteger()
    runBlocking {
        withContext(coroutineContext) {
            launch(Dispatchers.IO) { count.addAndGet(longRunningTask()) }
            launch(Dispatchers.IO) { count.addAndGet(longRunningTask()) }
        }
        Assertions.assertEquals(2, count.get())
    }
}
```

我们之前提到的有关父协程的构建器和调度程序的所有内容也适用于launch示例。

## 4. 总结

在本教程中，我们了解了结构化并发并展示了如何使用异步或启动来并发执行协程的示例。
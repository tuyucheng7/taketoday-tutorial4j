## 1. 概述

在本文中，我们将研究Kotlin语言中的协程。简而言之，**协程允许我们以流畅的方式创建异步程序**，它们基于[Continuation-passing风格编程](https://en.wikipedia.org/wiki/Continuation-passing_style)的概念。

Kotlin语言为我们提供了基本结构，但可以通过kotlinx-coroutines-core库访问更有用的协程，一旦我们了解了Kotlin语言的基本构建块，我们就会查看这个库。

## 2. 使用BuildSequence创建协程

让我们使用[buildSequence](https://kotlinlang.org/docs/reference/coroutines/composing-suspending-functions.html)函数创建第一个协程。

让我们使用这个函数实现斐波那契数列生成器：

```kotlin
val fibonacciSeq = sequence {
    var a = 0
    var b = 1

    yield(1)

    while (true) {
        yield(a + b)

        val tmp = a + b
        a = b
        b = tmp
    }
}
```

yield函数的签名是：

```kotlin
public abstract suspend fun yield(value: T)
```

suspend关键字意味着这个函数可以被阻塞，这样的函数可以暂停buildSequence协程。

**挂起函数可以创建为标准的Kotlin函数，但我们需要注意，我们只能在协程中调用它们**；否则，我们会得到一个编译器错误。

如果我们在buildSequence中暂停调用，则该调用将转换为状态机中的专用状态。协程可以像任何其他函数一样传递并分配给变量。

在fibonacciSeq协程中，我们有两个暂停点。首先，当我们调用yield(1)时，其次当我们调用yield(a+b)时。

如果该yield函数导致某些阻塞调用，则当前线程不会阻塞它。它将能够执行一些其他代码，一旦挂起的函数完成执行，线程就可以恢复fibonacciSeq协程的执行。

我们可以通过从斐波那契数列中获取一些元素来测试我们的代码：

```kotlin
val res = fibonacciSeq
    .take(5)
    .toList()

assertEquals(res, listOf(1, 1, 2, 3, 5))
```

## 3. 为kotlinx-coroutines添加Maven依赖

让我们看看kotlinx-coroutines库，它具有构建在基本协程之上的有用结构。

让我们将依赖项添加到kotlinx-coroutines-core库。

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>1.3.9</version>
</dependency>
```

## 4. 使用launch()协程进行异步编程

kotlinx-coroutines库添加了许多有用的结构，允许我们创建异步程序。假设我们有一个昂贵的计算函数，它将一个字符串附加到输入列表：

```kotlin
suspend fun expensiveComputation(res: MutableList<String>) {
    delay(1000L)
    res.add("word!")
}
```

我们可以使用启动协程，以非阻塞方式执行该挂起函数。

```kotlin
@Test
fun givenAsyncCoroutine_whenStartIt_thenShouldExecuteItInTheAsyncWay() {
    //given
    val res = mutableListOf<String>()

    //when
    runBlocking {
        launch { expensiveComputation(res) }
        res.add("Hello,")
    }

    //then
    assertEquals(res, listOf("Hello,", "word!"))
}
```

为了能够测试我们的代码，我们将所有逻辑传递给runBlocking协程-这是一个阻塞调用，因此我们的assertEquals()可以在runBlocking()方法内部的代码之后同步执行。

请注意，在这个例子中，虽然launch()方法先被触发，但它是一个延迟计算，主线程将通过将“Hello”字符串附加到结果列表来继续。

在expensiveComputation()函数中引入的一秒延迟之后，“"word!”字符串将附加到结果。

## 5. 协程非常轻量级

让我们想象一下我们想要异步执行100000个操作的情况。生成如此大量的线程将非常昂贵，并且可能会产生OutOfMemoryException。

幸运的是，在使用协程时，情况并非如此。我们可以根据需要执行任意数量的阻塞操作，在幕后，这些操作将由固定数量的线程处理，而不会创建过多的线程：

```kotlin
@Test
fun givenHugeAmountOfCoroutines_whenStartIt_thenShouldExecuteItWithoutOutOfMemory() {
    runBlocking<Unit> {
        //given
        val counter = AtomicInteger(0)
        val numberOfCoroutines = 100_000

        //when
        val jobs = List(numberOfCoroutines) {
            launch {
                delay(1L)
                counter.incrementAndGet()
            }
        }
        jobs.forEach { it.join() }

        //then
        assertEquals(counter.get(), numberOfCoroutines)
    }
}
```

请注意，我们正在执行100,000个协程，每次运行都会增加大量延迟。然而，不需要创建太多线程，因为这些操作是使用共享后台线程池中的线程以异步方式执行的。

## 6. 取消和超时

**有时，在我们触发了一些长时间运行的异步计算之后，我们想要取消它，因为我们不再对结果感兴趣**。

当我们使用launch()协程启动异步操作时，我们可以检查isActive标志。每当主线程调用Job实例上的cancel()方法时，此标志设置为false：

```kotlin
@Test
fun givenCancellableJob_whenRequestForCancel_thenShouldQuit() {
    runBlocking<Unit> {
        //given
        val job = launch(Dispatchers.Default) {
            while (isActive) {
                //println("is working")
            }
        }

        delay(1300L)

        //when
        job.cancel()

        //then cancel successfully
    }
}
```

**这是使用取消机制的一种非常优雅和简单的方法**，在异步动作中，我们只需要检查isActive标志是否等于false并取消我们的处理。

当我们请求某些处理并且不确定该计算将花费多少时间时，建议为此类操作设置超时。如果处理没有在给定的超时时间内完成，我们将得到一个异常，我们可以对其做出适当的反应。

例如，我们可以重试操作：

```kotlin
@Test(expected = CancellationException::class)
fun givenAsyncAction_whenDeclareTimeout_thenShouldFinishWhenTimedOut() {
    runBlocking<Unit> {
        withTimeout(1300L) {
            repeat(1000) { i ->
                println("Some expensive computation $i ...")
                delay(500L)
            }
        }
    }
}
```

如果我们不定义超时，我们的线程可能会永远阻塞，因为计算会挂起，如果未定义超时，我们无法在代码中处理这种情况。

## 7. 并发运行异步操作

假设我们需要同时启动两个异步操作，然后等待它们的结果。如果我们的处理需要一秒钟，而我们需要执行该处理两次，那么同步阻塞执行的运行时间将是两秒钟。

如果我们可以在单独的线程中运行这两个操作并在主线程中等待这些结果会更好。

**我们可以利用async()协程通过同时在两个单独的线程中开始处理来实现这一点**：

```kotlin
@Test
fun givenHaveTwoExpensiveAction_whenExecuteThemAsync_thenTheyShouldRunConcurrently() {
    runBlocking<Unit> {
        val delay = 1000L
        val time = measureTimeMillis {
            //given
            val one = async(Dispatchers.Default) { someExpensiveComputation(delay) }
            val two = async(Dispatchers.Default) { someExpensiveComputation(delay) }

            //when
            runBlocking {
                one.await()
                two.await()
            }
        }

        //then
        assertTrue(time < delay * 2)
    }
}
```

在我们提交了两个昂贵的计算之后，我们通过执行runBlocking()调用来暂停协程，一旦结果one和two可用，协程将恢复，并返回结果，以这种方式执行两个任务大约需要一秒钟。

我们可以将CoroutineStart.LAZY作为第二个参数传递给async()方法，但这意味着异步计算在请求之前不会启动，因为我们在runBlocking协程中请求计算，这意味着对two.await()的调用只会在one.await()完成后进行：

```kotlin
fun givenTwoExpensiveAction_whenExecuteThemLazy_thenTheyShouldNotConcurrently() {
    runBlocking<Unit> {
        val delay = 1000L
        val time = measureTimeMillis {
            //given
            val one = async(Dispatchers.Default, CoroutineStart.LAZY) { someExpensiveComputation(delay) }
            val two = async(Dispatchers.Default, CoroutineStart.LAZY) { someExpensiveComputation(delay) }

            //when
            runBlocking {
                one.await()
                two.await()
            }
        }

        //then
        assertTrue(time > delay * 2)
    }
}
```

**这个特定示例中执行的惰性导致我们的代码同步运行**，发生这种情况是因为当我们调用await()时，主线程被阻塞，只有在任务一完成后任务二才会被触发。

我们需要注意以惰性方式执行异步操作，因为它们可能以阻塞方式运行。

## 8. 总结

在本文中，我们了解了Kotlin协程的基础知识。

我们看到序列是每个协程的主要构建块，我们描述了这种Continuation-passing编程风格的执行流程。

最后，我们查看了kotlinx-coroutines库，它提供了许多非常有用的结构来创建异步程序。
## 1. 简介

在本教程中，我们的目标是深入了解协程世界中的async和withContext，我们有一个简短的旅程，看看如何使用这两种方法，它们的异同是什么，以及每种方法的使用位置。

## 2. Kotlin协程

[协程](https://www.baeldung.com/kotlin-coroutines)是使用流畅的API以顺序方式编写异步代码的强大工具，而无需为响应式编码而头疼。Kotlin引入协程作为语言的一部分，此外，kotlinx-coroutines-core是一个用于协程更高级用法的库。

协程在协程上下文中执行，协程上下文由多个CoroutineContext.Element组成，基本元素是CoroutineId、CoroutineName、CoroutineDispatcher和Job。

CoroutineDispatcher将协程分配给执行程序，调度器可以根据事件循环和线程池等不同的执行器使用不同的调度策略，甚至可以让协程不受限制。

CoroutineScope允许我们通过关联的Job实例来管理协程，协程可以使用coroutineContext[Job]访问Job，Job是管理协程生命周期并反映其状态(如活动、完成或取消)的接口。

现在，让我们仔细看看async和withContext及其用法。

## 3. 什么是异步等待？

async是CoroutineScope的扩展，用于创建新的可取消协程。因此，它返回一个Deferred对象，其中包含代码块的未来结果，我们可以通过调用Deferred#cancel来取消协程。

**async函数遵循**[结构化并发](https://kotlinlang.org/docs/reference/coroutines/basics.html#structured-concurrency)**，因此，它会在失败的情况下取消外部协程**：

```kotlin
Assertions.assertThrows(Exception::class.java) {
    runBlocking {
        kotlin.runCatching {
            async(Dispatchers.Default) {
                doTheTask(DELAY)
                throw Exception("Exception")
            }.await()
        }
    }
}
```

默认情况下，异步协程在创建时就开始执行；但是，我们可以通过传递CoroutineStart参数来更改此行为：

```kotlin
async(Dispatchers.Default, CoroutineStart.LAZY)
```

而且，如果我们有多个相互独立的任务，我们可以用async启动它们并发执行，如果我们需要连接结果，我们可以通过在每个项目上调用Deferred#await来等待所有协程完成：

```kotlin
val time = measureTimeMillis {
    val task1 = async { doTheTask(DELAY) }
    val task2 = async { doTheTask(DELAY) }
    task1.await()
    task2.await()
}
Assertions.assertTrue(time < DELAY * 2)
```

## 4. 什么是withContext？

withContext是一个[作用域函数](https://www.baeldung.com/kotlin-scope-functions)，它允许我们创建一个新的可取消协程，如果我们传递一个CoroutineContext参数，withContext合并父上下文和我们的参数以创建一个新的CoroutineContext，然后在这个合并的上下文中执行协程。

我们还可以将调度程序传递给此函数，以便块的执行将发生在传递的调度程序的线程上，执行完成后，控制返回到之前的调度程序。

如果我们在一个父块中有多个withContext块，它们中的每一个的执行都会挂起父线程，但它们将依次执行，一个接一个：

```kotlin
val time = measureTimeMillis {
    val dispatcher = newFixedThreadPoolContext(2, "withc")
    withContext(dispatcher) {
        doTheTask(DELAY)
    }
    withContext(dispatcher) {
        doTheTask(DELAY)
    }
}
Assertions.assertTrue(time >= DELAY * 2)
```

此外，withContext有一个名为coroutineScope的扩展，它使用当前上下文，因此，不会发生上下文切换。

## 5. async-await与withContext

让我们总结一下我们对这两个函数的发现。

-   当我们需要收集协程的结果时，我们使用withContext或者async
-   withContext具有与async后接await相同的功能，但开销更少
-   当我们需要并行执行代码时，我们将它们放在几个异步块中，最后等待所有它们
-   **使用async，我们必须捕获异步主体内代码块的异常；否则，它可以终止父作用域**

## 6. 总结

在本文中，我们快速介绍了协程，然后我们演示了async-await和withContext的用法，最后，我们简要介绍了分别在哪里使用它们。
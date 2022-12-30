## 1. 概述

在本教程中，我们将比较启动Kotlin[协程](https://www.baeldung.com/kotlin/coroutines)的两种方法：runBlocking和coroutineScope。

## 2. Maven依赖

在我们可以使用协程之前，我们需要将[kotlinx-coroutines-core](https://search.maven.org/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>1.6.2</version>
</dependency>
```

在接下来的部分中，我们将研究runBlocking和coroutineScope在启动、暂停和取消协程方面有何不同。

## 3. 启动协程

runBlocking和coroutineScope都是协程构建器，这意味着它们用于启动协程，但我们在不同的上下文中使用它们。

当我们使用coroutineScope构建和启动协程时，我们创建了一个挂起点，挂起点是代码中Kotlin可以挂起当前协程的地方。但是，当没有什么可以挂起时，我们无法创建挂起点，因此**我们无法在现有协程的范围之外调用coroutineScope**。

我们有时需要从现有协程范围之外启动协程，例如从main方法或单元测试，这就是我们将使用runBlocking的地方，它桥接了阻塞代码和可挂起代码，**我们可以在任何现有协程的范围之外调用runBlocking**。顾名思义，**由runBlocking启动的协程会阻塞当前线程**；它不会创建挂起点，即使在另一个协程中使用也是如此。

## 4. 挂起协程

在上一节中，我们看到了runBlocking和coroutineScope的不同之处在于它们是否可以挂起包含它们的协程(如果存在)。在这里，我们将看看两个构建器启动的协程是否可以自行暂停。

### 4.1 暂停coroutineScope协程

**由coroutineScope启动的协程是可挂起的**，为了看到这一点，让我们首先使用具有两个线程的固定线程池创建一个[协程调度程序](https://www.baeldung.com/kotlin/coroutine-context-dispatchers)：

```kotlin
val context = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
```

接下来，让我们定义一个将启动十个协程的函数，每个协程将使用coroutineScope启动一个子协程：

```kotlin
fun demoWithCoroutineScope() = runBlocking {
    (1..10).forEach {
        launch(context) {
            coroutineScope {
                println("Start No.$it in coroutineScope on ${Thread.currentThread().name}")
                delay(500)
                println("End No.$it in coroutineScope on ${Thread.currentThread().name}")
            }
        }
    }
}
```

上面，我们从runBlocking开始，从我们的阻塞代码中创建一个桥梁。在runBlocking块内部，我们使用launch将可挂起地协程分派给上下文线程池中的任何非活动线程。最后，我们使用coroutineScope来启动一个协程，该协程将在500毫秒内调用delay()，delay()方法是一个挂起函数，它为coroutineScope启动的协程创建一个挂起点。

让我们调用这个函数来观察Kotlin是如何挂起这些协程的：

```kotlin
fun main() {
    val coroutineScopeTimeInMills = measureTimeMillis {
        demoWithCoroutineScope()
    }
    println("coroutineScopeTimeInMills = $coroutineScopeTimeInMills")
}
```

以下是示例运行的输出：

```shell
Start No.1 in coroutineScope on pool-1-thread-1
Start No.2 in coroutineScope on pool-1-thread-2
Start No.3 in coroutineScope on pool-1-thread-2
Start No.4 in coroutineScope on pool-1-thread-2
Start No.5 in coroutineScope on pool-1-thread-2
Start No.6 in coroutineScope on pool-1-thread-2
Start No.7 in coroutineScope on pool-1-thread-1
Start No.9 in coroutineScope on pool-1-thread-1
Start No.8 in coroutineScope on pool-1-thread-2
Start No.10 in coroutineScope on pool-1-thread-1
End No.1 in coroutineScope on pool-1-thread-2
End No.2 in coroutineScope on pool-1-thread-1
End No.3 in coroutineScope on pool-1-thread-1
End No.4 in coroutineScope on pool-1-thread-1
End No.5 in coroutineScope on pool-1-thread-2
End No.6 in coroutineScope on pool-1-thread-1
End No.7 in coroutineScope on pool-1-thread-2
End No.9 in coroutineScope on pool-1-thread-1
End No.8 in coroutineScope on pool-1-thread-2
End No.10 in coroutineScope on pool-1-thread-1
coroutineScopeTimeInMills = 609
```

尽管每个协程调用delay()的时间为500毫秒，但我们的总运行时间并没有超过这个数字。我们还可以看到一些协程在pool-1-thread-1上启动，但在pool-1-thread-2上完成，反之亦然。

Kotlin能够在delay()调用的暂停点挂起每个协程，由于挂起的协程不会阻塞任何线程，因此另一个协程可以介入，使用该线程启动它自己的delay()调用，然后也被挂起。在500毫秒的延迟之后，每个协程都可以在池中的任何非活动线程上恢复。

### 4.2 暂停runBlocking协程

接下来，让我们尝试用runBlocking替换coroutineScope：

```kotlin
fun demoWithRunBlocking() = runBlocking {
    (1..10).forEach {
        launch(context) {
            runBlocking {
                println("Start No.$it in runBlocking on ${Thread.currentThread().name}")
                delay(500)
                println("End No.$it in runBlocking on ${Thread.currentThread().name}")
            }
        }
    }
}
```

这一次，我们得到了一个非常不同的结果：

```shell
Start No.1 in runBlocking on pool-1-thread-1
Start No.2 in runBlocking on pool-1-thread-2
End No.1 in runBlocking on pool-1-thread-1
Start No.3 in runBlocking on pool-1-thread-1
End No.2 in runBlocking on pool-1-thread-2
Start No.4 in runBlocking on pool-1-thread-2
End No.4 in runBlocking on pool-1-thread-2
Start No.5 in runBlocking on pool-1-thread-2
End No.3 in runBlocking on pool-1-thread-1
Start No.6 in runBlocking on pool-1-thread-1
End No.5 in runBlocking on pool-1-thread-2
Start No.7 in runBlocking on pool-1-thread-2
End No.6 in runBlocking on pool-1-thread-1
Start No.8 in runBlocking on pool-1-thread-1
End No.8 in runBlocking on pool-1-thread-1
End No.7 in runBlocking on pool-1-thread-2
Start No.9 in runBlocking on pool-1-thread-1
Start No.10 in runBlocking on pool-1-thread-2
End No.9 in runBlocking on pool-1-thread-1
End No.10 in runBlocking on pool-1-thread-2
runBlockingTimeInMills = 2607
```

每个线程都在其启动的同一个线程上完成，我们的总运行时间超过2500毫秒。由runBlocking启动的协程忽略了delay()调用创建的挂起点，**runBlocking协程不可挂起**。

## 5. 取消协程

这两个协程构建器在是否可以取消协程方面也有所不同，让我们看一下每个构建器的取消行为。

### 5.1 取消coroutineScope协程

我们可以使用launch返回的Job引用在启动100ms后取消协程：

```kotlin
private fun cancelCoroutineScope() = runBlocking {
    val job = launch {
        coroutineScope {
            println("Start coroutineScope...")
            delay(500)
            println("End coroutineScope...")
        }
    }
    delay(100)
    job.cancel()
}
```

cancel()调用尝试取消job的执行，让我们测试一下：

```kotlin
fun main() = runBlocking {
    val cancelCoroutineScopeTime = measureTimeMillis {
        cancelCoroutineScope()
    }
    println("cancelCoroutineScopeTime = $cancelCoroutineScopeTime")
}
```

下面是一个示例运行的输出：

```shell
Start coroutineScope...
cancelCoroutineScopeTime = 123
```

我们可以看到协程在延迟100毫秒后不久在delay(500)的挂起点被取消。

### 5.2 取消runBlocking协程

让我们做同样的事情，但使用runBlocking代替coroutineScope：

```kotlin
private fun cancelRunBlocking() = runBlocking {
    val job = launch {
        runBlocking {
            println("Start runBlocking...")
            delay(500)
            println("End runBlocking...")
        }
    }
    delay(100)
    job.cancel()
}
```

我们可以用同样的方式对此进行测试：

```kotlin
fun main() = runBlocking {
    val cancelRunBlockingTime = measureTimeMillis {
        cancelRunBlocking()
    }
    println("cancelRunBlockingTime = $cancelRunBlockingTime")
}
```

与暂停一样，这次我们得到非常不同的输出：

```shell
Start runBlocking...
End runBlocking...
cancelRunBlockingTime = 519
```

我们可以在这里看到**使用runBlocking启动的协程是不可取消的**，由于取消发生在挂起点，并且runBlocking协程不可挂起且没有挂起点，因此允许协程完成其执行。

## 6. 总结

在本文中，我们了解到runBlocking协程无法像coroutineScope协程那样被挂起或取消，并且当我们需要从现有协程的作用域之外启动协程时，runBlocking是我们唯一的选择。
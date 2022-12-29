## 1. 概述

在本教程中，我们将学习如何在Kotlin中延迟调用函数，我们将展示如何使用调度程序、线程和协程来做到这一点。

## 2. 使用定时器安排执行

让我们看一下[Timer](https://www.baeldung.com/java-timer-and-timertask)类提供的功能，它公开了一个带有两个参数的schedule函数。第一个是TimerTask接口的实现，该接口定义了一个run方法，它将由调度程序执行；第二个参数是以毫秒为单位的执行延迟。

**最重要的是，该工具类在后台线程中安排执行**。此外，该类不仅允许我们延迟代码的执行，而且还包含更多我们可以在安排任务时使用的方法。

让我们创建一个简单的示例，其中我们将以一秒的延迟打印输出：

```kotlin
fun scheduleWithTimer() {
    val stopWatch = StopWatch.createStarted()
    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            stopWatch.stop()
            println("Function in scheduleWithTimer executed with delay " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.time))
            timer.cancel()
        }
    }, 1000)
}
```

我们在我们的任务中调用cancel方法来优雅地停止后台线程。

执行将打印到控制台：

```bash
Function in scheduleWithTimer executed with delay 1
```

## 3. 执行延迟线程

现在让我们看一下Executors类，**Executors类提供了使用newSingleThreadScheduledExecutor方法执行新线程的功能**，因此，我们创建了ScheduledExecutorService接口的一个实例。此外，该接口定义了一个方法schedule，它需要三个参数，第一个是Runnable对象，第二个和第三个定义延迟和延迟的单位。

让我们创建一个简单的例子：

```kotlin
fun scheduleWithExecutor() {
    val stopWatch = StopWatch.createStarted()
    val executor = Executors.newSingleThreadScheduledExecutor()
    executor.schedule({
        stopWatch.stop()
        println("Function in scheduleWithExecutor executed with delay " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.time))
        executor.shutdown()
    }, 2, TimeUnit.SECONDS)
}
```

该方法启动一个新线程，延迟两秒，它打印到标准输出：

```bash
Function in scheduleWithExecutor executed with delay 2
```

此外，我们关闭了线程，这是正常停止应用程序所必需的。

## 4. 使用协程延迟执行

接下来，我们将了解如何使用[协程](https://www.baeldung.com/kotlin/coroutines)延迟执行。首先，协程允许我们创建一个异步程序；此外，在协程中，我们可以用一种非常简单的方式定义延迟。让我们创建一个示例：

```kotlin
fun scheduleWithCoroutine() = runBlocking {
    val stopWatch = StopWatch.createStarted()
    launch {
        delay(3000L)
        stopWatch.stop()
        println("Function in scheduleWithCoroutine executed with delay " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.time))
    }
}
```

首先，[runBlocking](https://www.baeldung.com/kotlin/coroutines#asynchronous-programming-using-the-launch-coroutine)方法停止方法执行，直到协程完成。之后，[launch](https://www.baeldung.com/kotlin/threads-coroutines#launch)函数启动协程；**最后，delay函数将执行延迟三秒**。

执行返回：

```bash
Function in scheduleWithCoroutine executed with delay 3
```

## 5. 总结

在这篇简短的文章中，我们演示了如何以三种不同的方式延迟函数的执行，分别介绍了调度程序、线程和协程的例子。
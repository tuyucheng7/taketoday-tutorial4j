## 1. 简介

根据Kotlin协程库作者的说法，**创建另一个协程应该很容易**。由于协程比线程轻量级得多，因此我们可以在每次需要时创建一个。

虽然我们可能已经在文档中读到“协程只是轻线程”，但事实并非如此，线程代表为其执行分配的系统资源，以及一系列指令本身。另一方面，协程仅包含与这些资源的弱链接(通过其调度程序)，**但它与调用者的生命周期有相当强的联系**。

如果调用实体不再存在，它就无法从协程的结果中获益，因此，最好在不需要其结果时立即取消协程。这是工作中的结构化并发范例：所有控制流构造必须有明确的入口和出口点，并且**所有线程必须在出口之前完成**。

这些考虑需要工具来分离协程生命周期以便更好地管理，这些工具是launch{}和async{}，让我们更仔细地看看这两个作用域函数。

## 2. 将协程作为作业启动

将协程作为作业启动可能是最基本的协程操作，为此，我们必须[创建一个CoroutineScope](https://www.baeldung.com/kotlin/composing-coroutines-suspend-functions)，然后调用launch{}：

```kotlin
val job: Job = launch {
    println("I am executed, but you only see the side-effects")
}
```

**launch{}函数返回一个Job对象**，我们可以在该对象上阻塞，直到协程中的所有指令都完成，否则它们会抛出异常。**协程的实际执行也可以推迟到我们需要它并带有start参数时**，如果我们使用CoroutineStart.LAZY，只有当有人在其Job对象上调用join()时，协程才会执行，默认值为CoroutineStart.DEFAULT并立即开始执行。

还有CoroutineStart.ATOMIC和CoroutineStart.UNDISPATCHED，它在到达第一个挂起点之前阻止协程取消，它在当前线程中开始执行，然后在第一个挂起点挂起，并在继续时使用其上下文中的调度程序。

如果我们必须处理具有不同生命周期的多个作用域，我们可能会使用更直接的调用形式：

```kotlin
val customRoutine = launch {
    println("${Thread.currentThread().name}: I am launched in a class scope")
}

val globalRoutine = GlobalScope.launch {
    println("${Thread.currentThread().name}: I am launched in a global scope")
}
```

这种启动协程的形式会立即将控制权返回给调用者，它通常被称为“即发即弃”。当然，我们不必“忘记”我们调用了协程，而且，我们唯一能得到的结果是简单的“成功”或“失败”。

如果我们需要更结构化的响应，我们必须使用async{}。

## 3. 启动协程获取结果

**async{}调用类似于launch{}**但会立即返回一个Deferred<T\>对象，其中T是块参数返回的任何类型。**要获得结果，我们需要在Deferred上调用await()**。

如前所述，异步调用很容易在同一协程作用域内实现并发，如果最终结果是我们想要的，我们也可以在runBlocking{}中调用await()：

```kotlin
val futureResult: Deferred<String> = async {
    "Hello, world!"
}

runBlocking {
    println(futureResult.await())
}
```

与launch{}一样，我们可以懒于实际计算Deferred的值或通过start参数影响其线程和可取消性：

```kotlin
val first = async(start = CoroutineStart.LAZY) {
    println("${Thread.currentThread().name}: I am lazy and launched only now")
    "Hello, "
}

val second = async {
    println("${Thread.currentThread().name}: I am eager!")
    "world!"
}
runBlocking {
    println(first.await() + second.await())
}
```

该片段将在第一个协程的输出之前打印第二个协程的输出。

## 4. 总结

在本教程中，我们了解了launch{}和async{}作用域函数的相同点和不同点，它们都允许在协程中运行代码，但**只有async{}允许返回类型化的结果**，launch{}只能产生副作用。
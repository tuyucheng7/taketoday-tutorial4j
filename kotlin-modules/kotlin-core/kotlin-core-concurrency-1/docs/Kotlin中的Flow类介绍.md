## 1. 简介

如果我们**需要表示值流并异步处理它们**，Kotlin Coroutines库可以满足我们的需求，Flow类允许这样做。

**Flow概念类似于Reactive Streams中的Publisher**，或者，我们可以将其视为异步Sequence，而不是阻塞Sequence迭代器的next()调用，我们将暂停协程并用我们的线程做一些有成效的事情。

Kotlin文档还指出Flow是冷的，而不是Channel，这意味着它本质上不持有任何资源，并且在collect()启动之前不会执行任何操作。

在本教程中，让我们看看如何创建Flow以及它的常见用例和属性。

## 2. 构建和消费流

**Flow可以从基本构建器flow{}构建**，这类似于sequece{}构建器，它期望我们调用emit()函数一次或多次：

```kotlin
val flowWithSingleValue = flow { emit(0) }
```

或者，可以使用.asFlow()扩展函数将集合、范围和序列转换为流：

```kotlin
val flowFromList = listOf("mene", "mene", "tekel", "upharsin").asFlow()
```

然后我们可以消费Flow：

```kotlin
val inscription = buildString {
    listOf("mene", "mene", "tekel", "upharsin")
        .asFlow()
        .collect { append(it).append(", ") }
}.removeSuffix(", ")
assertEquals(inscription, "mene, mene, tekel, upharsin")
```

**除了简单的collect()之外，还有其他终端流运算符**，它们中的每一个都是一个启动Flow集合的挂起函数。基本上，我们可以用Flow和Sequence做同样的事情，例如将所有值表示为List或Set，归约流，或者只取第一个值：

```kotlin
val data = flow { "PEACE".asSequence().forEach { emit(it.toString()) } }
val charList = data.toList()
assertEquals(5, charList.size)

val symbols = data.toSet()
assertEquals(4, symbols.size)

val word = data.reduce { acc, c -> acc + c }
assertEquals("PEACE", word)

val firstLetter = data.first()
assertEquals("P", firstLetter)
```

## 3. 转换Flow

在调用终端运算符之前，我们还可以使用转换运算符更改每个Flow值，这里没有什么惊喜—**我们有map{}和filter{}作为我们的基本工具**：

```kotlin
val symbols = (60..100).asFlow()
    .filter { it in secretCodes }
    .map { Char(it) }
    .toList()
```

**所有转换器的基础是一个transform{}函数**，它通过调用emit()从父流创建一个新流：

```kotlin
val monthsOfSorrow = Month.values().asFlow()
    .transform {
        if (it <= Month.MAY)
            emit(it)
    }
    .toList()
```

有一些快捷方式可以限制最终集合的大小，而不是手动执行：

```kotlin
Month.values().asFlow().take(5).toList()
```

这以稍微不同的方式工作，但仍然创建另一个Flow。它在try...catch块中调用collect()并在处理了必要数量的项目后抛出异常。收集到的值被发送到下游。

## 4. Flow作为更新流

在某些情况下，Flow是无限的并且表示参数的当前状态，例如我们网站上的访问者数量。在这种情况下，处理所有值没有意义，只处理最近的值。

为此，**Flow提供了conflate()方法**：

```kotlin
val buffer = mutableListOf<Int>()
(1..10).asFlow()
    .transform {
        delay(10)
        emit(it)
    }
    .conflate()
    .collect{
        delay(50)
        buffer.add(it)
    }
assertTrue { buffer.size < 10}
```

**它会跳过两次连续调用收集器函数之间的值**。

或者也许只有Flow中的最后一个值才有意义，在这种情况下，**我们可以使用*Latest{}方法系列**，这些方法会**将参数lambda应用于最新的可访问值**，如果更新的值在此期间到达，则会取消其执行：

```kotlin
var latest = -1
(1..10).asFlow()
    .collectLatest { latest = it }
assertEquals(10, latest)
```

## 5. 组合Flow

有时，我们需要的转换是与**另一组数据的组合**，最简单的此类操作是zip()：

```kotlin
val codes = listOf(80, 69, 65, 67).asFlow()
val symbols = listOf('P', 'A', 'C', 'E').asFlow()
val list = buildList {
    codes.zip(symbols) { code, symbol -> add("$code -> $symbol") }.collect()
}
assertEquals(listOf("80 -> P", "69 -> A", "65 -> C", "67 -> E"), list)
```

**zip()方法将产生一个新的流，它是两个初始组件的叠加**。

如果我们需要两个流的最新值，可以使用combine()：

```kotlin
val arrowsI = listOf("v", "<", "^", ">").asFlow().map { delay(30); it }
val arrowsII = listOf("v", ">", "^", "<").asFlow().map { delay(20); it }
arrowsI.combine(arrowsII) { one, two -> "$one $two" }
    .collect { println(it) }
```

每次其中一个流发出一个新值时，它都会重新计算结果。输出将如下所示：

```bash
v v
v >
< >
< ^
^ <
> <
```

## 6. 异常处理

**发射器代码、转换器代码或收集器代码都可以抛出异常**，Flow必须对异常透明，**并且从try...catch块内部发出emit()违反Flow的想法**，这样，收集器抛出的任何异常都可以自己捕获。

我们可以使用catch()运算符“catch”转换过程中抛出的异常，它可以重新抛出异常，发出一个值，或者忽略异常。该操作将仅捕获上游发生的异常，即在先前的转换中发生的异常，任何后续的转换和集合仍然可能抛出异常：

```kotlin
val result = (1..10).asFlow()
    .transform {
        if (it > 3) throw IllegalArgumentException("Too much")
        emit(it)
    }
    .catch {
        when (it) {
            is IllegalArgumentException -> emit(3)
            else -> throw it // rethrow unknown exceptions
        }
    }
    .toList()
assertEquals(listOf(1, 2, 3, 3), result)
```

## 7. 来自Flow的透明背压

**Flow具有固有的背压机制**，因为emit()是一个挂起函数，它不会急切地计算，而是在收集器准备好接收新值时继续计算。所以，如果收集器很昂贵而生产者性能更高，它就不会压垮收集器：

```kotlin
val fastFlow = flow {
    (1..10).forEach {
        println("Before waiting: $it")
        delay(10)
        println("About to emit: $it")
        emit(Char(60 + it))
        println("After emitting: $it")
    }
}

fastFlow.collect {
    Thread.sleep(100) // Imitate hard CPU-bound operation
    println("Collected $it")
}
```

如果我们查看输出，我们会看到只有在收集器完成处理后发射器循环才会继续：

```bash
Before waiting: 1
About to emit: 1
Collected =
After emitting: 1
Before wating: 2
About to emit: 2
Collected >
After emitting: 2
```

## 8. Flow上下文

尽管Flow不拥有任何资源，但它**仍然必须在某个线程中运行**。emit()函数是一个挂起函数，因此必须与flow{}构建器中的其余指令一起在协程上下文中运行，**默认情况下，它是收集器的上下文**。

**如果我们想在不同的上下文中运行发射器，我们必须使用.flowOn()修饰符**：

```kotlin
flow {
    (1..10).forEach { emit(it) }
    assertTrue { "DefaultDispatcher-worker" in Thread.currentThread().name }
}.flowOn(Dispatchers.IO)
    .collect {
        println(it)
        assertTrue { "main" in Thread.currentThread().name }
    }
```

## 9. 总结

在本文中，我们仔细研究了Flow类，这是一个通用且强大的概念，可用于我们应用程序各个部分之间的异步通信。

Flow是“冷的”，也就是说，默认情况下它不包含协程上下文或线程，它类似于标准Kotlin库中的Sequence和Reactive Streams中的Publisher，我们可以转换流并将它们组合在一起，我们可以一次收集它们，将它们的内容逐个元素或仅部分地放入常规集合中-前几个元素、最新元素等。

Flow隐式地对发射器施加背压，默认情况下，它使用收集器的上下文，但可能会以其他方式配置。此外，我们应该避免从try...catch运算符内部发出值，因为它可能会破坏收集器。
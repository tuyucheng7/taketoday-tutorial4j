## 1. 概述

[协程](https://www.baeldung.com/kotlin-coroutines)是在Kotlin中构建非阻塞、并发应用程序的首选方式。在本教程中，我们将了解通道，**它们允许协程相互通信**。

## 2. 什么是通道？

**通道在概念上类似于队列**，一个或多个生产者协程写入通道，一个或多个消费者协程可以从同一通道读取，通道具有暂停发送功能和暂停接收功能，这意味着多个协程可以使用通道以非阻塞方式相互传递数据。

让我们看一个Channel的例子：

```kotlin
@Test
fun should_pass_data_from_one_coroutine_to_another() {
    runBlocking {
        // given
        val channel = Channel<String>()

        // when
        launch { // coroutine1
            channel.send("Hello World!")
        }
        val result = async { // coroutine2
            channel.receive()
        }

        // then
        assertThat(result.await()).isEqualTo("Hello World!")
    }
}
```

首先，我们创建一个通道；接下来，我们启动coroutine1并发送值“Hello World!”到通道；最后，我们使用异步协程构建器创建coroutine2，coroutine2在完成时返回一个结果，coroutine2内部的channel.receive()调用返回coroutine1写入的值。

## 3. 通道类型

有四种类型的通道，它们在一次可以容纳的值的数量上有所不同，让我们详细了解每种类型。

### 3.1 集合点通道

集合点通道没有缓冲区，**发送协程挂起，直到接收协程调用通道上的接收。类似地，消费协程挂起，直到生产者协程调用通道上的发送**。我们使用不带参数的默认Channel构造函数创建一个集合点通道。

让我们看一下此类通道的示例：

```kotlin
val basket = Channel<String>()

launch { // coroutine1
    val fruits = listOf("Apple", "Orange")
    for (fruit in fruits) {
        println("coroutine1: Sending $fruit")
        basket.send(fruit)
    }
}

launch { // coroutine2
    repeat(2) {
        delay(100)
        println("coroutine2: Received ${basket.receive()}")
    }
}
```

让我们看看这个程序的输出：

```bash
coroutine1: Sending Apple
coroutine2: Received Apple
coroutine1: Sending Orange
coroutine2: Received Orange
```

coroutine1尝试发送值“Apple”并立即挂起，因为没有接收，coroutine2收到这个值并暂停它，因为没有更多的值可以从通道接收，coroutine1现在取消挂起并将下一个值发送到通道。

### 3.2 缓冲通道

顾名思义，**缓冲通道有一个预定义的缓冲区**，我们可以在Channel构造函数中指定缓冲区的容量。

让我们更改前面的示例以查看缓冲通道的示例：

```kotlin
val basket = Channel<String>(1)

launch { // coroutine1
    val fruits = listOf("Apple", "Orange", "Banana")
    for (fruit in fruits) {
        println("coroutine1: Sending $fruit")
        basket.send(fruit)
    }
}

launch { // coroutine2
    repeat(3) {
        delay(100)
        println("coroutine2: Received ${basket.receive()}")
    }
}
```

让我们看看这个程序的输出：

```bash
coroutine1: Sending Apple
coroutine1: Sending Orange
coroutine2: Received Apple
coroutine1: Sending Banana
coroutine2: Received Orange
coroutine2: Received Banana
```

这次，coroutine1没有挂起就写了“Apple”，但是在尝试写“Orange”时它会暂停，这是因为我们创建了缓冲容量为1的通道。

因此，即使此时没有接收者接收到该值，它也可以在缓冲区中保存一个值，但coroutine1必须等待(挂起)才能将更多值写入通道，因为缓冲区已满。

一旦coroutine2从缓冲区读取值，coroutine1就会取消挂起并将下一个值写入通道。

### 3.3 无限通道

**无限通道具有无限容量的缓冲区**，但是，我们应该知道，如果缓冲区过载并且所有可用内存都已耗尽，我们可能会遇到OutOfMemoryError。**我们可以通过向Channel构造函数提供特殊常量UNLIMITED来创建无限通道**。

让我们看一下这个通道的例子：

```kotlin
val channel = Channel<Int>(UNLIMITED)

launch { // coroutine1
    repeat(100) {
        println("coroutine1: Sending $it")
        channel.send(it)
    }
}

launch { // coroutine2
    repeat(100) {
        println("coroutine2: Received ${channel.receive()}")
    }
}
```

让我们检查一下这个程序的输出：

```bash
coroutine1: Sending 0
coroutine1: Sending 1


...

coroutine1: Sending 98
coroutine1: Sending 99
coroutine2: Received 0
coroutine2: Received 1

...

coroutine2: Received 98
coroutine2: Received 99
```

正如我们所见，coroutine1将所有100个值写入通道而不会暂停，这要归功于无限的缓冲区容量。

### 3.4 合并通道

在合并通道中，**最近写入的值会覆盖之前写入的值**。因此，通道的发送方法永远不会挂起，receive方法只接收最新的值。

让我们看一下此类通道的示例：

```kotlin
val basket = Channel<String>(CONFLATED)

launch { // coroutine1
    val fruits = listOf("Apple", "Orange", "Banana")
    for (fruit in fruits) {
        println("coroutine1: Sending $fruit")
        basket.send(fruit)
    }
}

launch { // coroutine2
    println("coroutine2: Received ${basket.receive()}")
}
```

让我们检查一下这个程序的输出：

```bash
coroutine1: Sending Apple
coroutine1: Sending Orange
coroutine1: Sending Banana
coroutine2: Received Banana
```

我们看到coroutine1向通道发送了三个值，但是coroutine2只接收到最后一个值。这是因为coroutine2是一个慢消费者，当它从basket中读取时，coroutine1已经覆盖了之前写入的值。

## 4. 用通道实现生产者-消费者

在并发程序中，我们经常需要实现一个产生一系列值的程序，另一个程序在这些值可用时使用它们。

这两个程序同时运行，但它们共享一种通信机制来相互传递值，这通常被称为生产者-消费者模式。

让我们看看如何使用Kotlin协程和通道来实现生产者-消费者模式。

### 4.1 一个消费者从一个生产者那里消费

**我们可以使用produce coroutine builder方法来创建生产者协程**。

让我们看看如何创建生产者：

```kotlin
fun CoroutineScope.produceFruits(): ReceiveChannel<String> = produce {
    val fruits = listOf("Apple", "Orange", "Apple")
    for (fruit in fruits) send(fruit)
}
```

这里我们注意到produce协程返回一个ReceiveChannel，**ReceiveChannel只有receive方法**，它没有send方法，这意味着另一个协程只能从这个输出通道读取。

**我们可以定期使用for循环语法来迭代ReceiveChannel中存在的值**。

现在让我们看看如何使用生产者的值：

```kotlin
val fruitChannel = produceFruits()
for (fruit in fruitChannel) {
    println(fruit)
}
```

让我们看看这个消费者的输出：

```bash
Apple
Orange
Apple
End!
```

如我们所见，消费者代码按照生产者生成的顺序接收值。生产者和消费者协程并发运行，他们使用fruitChannel相互通信。

### 4.2 多个消费者从一个生产者消费

我们可以创建多个消费者来消费一个生产者生产的价值，**这样我们就可以在多个消费者之间分配工作**。

让我们创建一个每秒生产10个披萨订单的生产者：

```kotlin
fun CoroutineScope.producePizzaOrders(): ReceiveChannel<String> = produce {
    var x = 1
    while (true) {
        send("Pizza Order No. ${x++}")
        delay(100)
    }
}
```

现在让我们创建一个披萨订单处理器-一个消费者：

```kotlin
fun CoroutineScope.pizzaOrderProcessor(id: Int, orders: ReceiveChannel<String>) = launch {
    for (order in orders) {
        println("Processor #$id is processing $order")
    }
}
```

该协程将订单通道作为输入参数，新的披萨订单将到达此通道。

现在让我们运行三个披萨订单处理器实例并在它们之间分配工作：

```kotlin
fun main() = runBlocking {
    val pizzaOrders = producePizzaOrders()
    repeat(3) {
        pizzaOrderProcessor(it + 1, pizzaOrders)
    }

    delay(1000)
    pizzaOrders.cancel()
}
```

让我们检查一下这个程序的输出：

```bash
Processor #1 is processing Pizza Order No. 1
Processor #1 is processing Pizza Order No. 2
Processor #2 is processing Pizza Order No. 3
Processor #3 is processing Pizza Order No. 4
Processor #1 is processing Pizza Order No. 5
Processor #2 is processing Pizza Order No. 6
Processor #3 is processing Pizza Order No. 7
Processor #1 is processing Pizza Order No. 8
Processor #2 is processing Pizza Order No. 9
Processor #3 is processing Pizza Order No. 10
```

我们看到订单处理工作几乎平均分配给三个处理器。

### 4.3 一个消费者从多个生产者那里消费

我们可以从多个生产者协程写入一个通道，消费者协程可以读取来自该通道的所有消息。

让我们创建两个生产者，一位制作人将获取YouTube视频，另一位制作人将获取推文：

```kotlin
suspend fun fetchYoutubeVideos(channel: SendChannel<String>) {
    val videos = listOf("cat video", "food video")
    for (video in videos) {
        delay(100)
        channel.send(video)
    }
}
suspend fun fetchTweets(channel: SendChannel<String>) {
    val tweets = listOf("tweet: Earth is round", "tweet: Coroutines and channels are cool")
    for (tweet in tweets) {
        delay(100)
        channel.send(tweet)
    }
}
```

现在让我们启动两个生产者并消费它们产生的价值：

```kotlin
fun main() = runBlocking {
    val aggregate = Channel<String>()
    launch { fetchYoutubeVideos(aggregate) }
    launch { fetchTweets(aggregate) }

    repeat(4) {
        println(aggregate.receive())
    }

    coroutineContext.cancelChildren()
}
```

让我们检查一下这个程序的输出：

```bash
cat video
tweet: Earth is round
food video
tweet: Coroutines and channels are cool
```

我们看到我们在聚合通道中收到了两个生产者产生的价值。

## 5. 管道使用通道

**我们可以将多个生产者和消费者组合成一条链来创建数据处理管道**，让我们以一家制作比萨饼的商店为例，我们可以将披萨的制作过程分为几个步骤。为了简单起见，我们将其分为两个步骤-烘烤和打顶。

现在让我们看看如何使用协程来实现这些步骤，baking协程生成一个基本的烤披萨，由topping协程使用，topping协程应用必要的toppings，输出准备好提供服务。

让我们看一下烘焙和浇头协程的简单实现：

```kotlin
fun CoroutineScope.baking(orders: ReceiveChannel<PizzaOrder>) = produce {
    for (order in orders) {
        delay(200)
        println("Baking ${order.orderNumber}")
        send(order.copy(orderStatus = BAKED))
    }
}

fun CoroutineScope.topping(orders: ReceiveChannel<PizzaOrder>) = produce {
    for (order in orders) {
        delay(50)
        println("Topping ${order.orderNumber}")
        send(order.copy(orderStatus = TOPPED))
    }
}
```

让我们创建另一个协程来生成给定数量的虚拟披萨订单：

```kotlin
fun CoroutineScope.produceOrders(count: Int) = produce {
    repeat(count) {
        delay(50)
        send(PizzaOrder(orderNumber = it + 1))
    }
}
```

最后，让我们组合所有这些协程来创建一个管道：

```kotlin
fun main() = runBlocking {
    val orders = produceOrders(3)

    val readyOrders = topping(baking(orders))

    for (order in readyOrders) {
        println("Serving ${order.orderNumber}")
    }

    delay(3000)
    coroutineContext.cancelChildren()
}
```

首先，我们创建三个披萨订单，然后我们按顺序通过烘焙和浇头协程传递订单，最后，我们遍历准备好的订单并在每个订单到达时提供服务。

让我们检查一下输出：

```bash
Baking 1
Topping 1
Serving 1
Baking 2
Topping 2
Serving 2
Baking 3
Topping 3
Serving 3
```

如我们所见，披萨订单准备的所有步骤都按照预期的顺序进行。

## 6. 股票通道

Ticker通道是传统计时器的协程等价物，它以指定的固定时间间隔生成单位值，这种类型的通道对于定期执行作业很有用。

让我们举一个简单的股票价格提取器的例子，我们的程序将每五秒获取一次给定股票的价格，让我们看看使用ticker通道的实现：

```kotlin
fun stockPrice(stock: String): Double {
    log("Fetching stock price of $stock")
    return Random.nextDouble(2.0, 3.0)
}

fun main() = runBlocking {
    val tickerChannel = ticker(Duration.ofSeconds(5).toMillis())

    repeat(3) {
        tickerChannel.receive()
        log(stockPrice("TESLA"))
    }

    delay(Duration.ofSeconds(11).toMillis())
    tickerChannel.cancel()
}

```

让我们检查一下输出：

```bash
14:11:18 - Fetching stock price of TESLA
14:11:18 - 2.7380844072456583
14:11:23 - Fetching stock price of TESLA
14:11:23 - 2.3459508859536635
14:11:28 - Fetching stock price of TESLA
14:11:28 - 2.3137592916266994
```

在这里，我们看到每五秒打印一次新的股票价格，完成后，我们通过调用cancel方法来停止ticker通道。

## 7. 总结

在本教程中，我们了解了什么是通道以及如何将它们与协程一起使用来创建异步编程。此外，我们还使用协程和通道实现了生产者-消费者和管道模式。
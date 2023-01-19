## 一、简介

我们[最近查看了 Quasar](https://www.baeldung.com/kotlin-quasar)，它为我们提供了使异步编程更易于访问且更高效的工具。我们已经了解了我们可以用它做什么的基础知识，允许轻量级线程和消息传递。

在本教程中，我们将看到我们可以使用 Quasar 做的一些更高级的事情，以进一步推进我们的异步编程。

## 2.演员

[Actor](https://en.wikipedia.org/wiki/Actor_model)是一种著名的并发编程实践，在 Erlang 中尤其流行。Quasar 允许我们定义 Actors，这是这种编程形式的基本构建块。

演员可以：

-   开始其他演员
-   向其他演员发送消息
-   从其他演员那里接收他们做出反应的消息

这三个功能为我们提供了构建应用程序所需的一切。

在 Quasar 中，一个 actor 被表示为一个 strand——通常是一个 fiber，但如果需要的话线程也是一个选项——带有一个接收消息的通道，以及对生命周期管理和错误处理的一些特殊支持。

### 2.1. 将 Actors 添加到构建中

Actor 不是 Quasar 中的核心概念。相反，我们需要添加使我们能够访问它们[的依赖](https://search.maven.org/search?q=g:co.paralleluniverse and a:quasar-actors)项：

```xml
<dependency>
    <groupId>co.paralleluniverse</groupId>
    <artifactId>quasar-actors</artifactId>
    <version>0.8.0</version>
</dependency>
```

重要的是，我们使用与正在使用的任何其他 Quasar 依赖项相同版本的此依赖项。

### 2.2. 创建演员

我们通过对Actor类进行子类化、提供名称和 MailboxConfig 并实现doRun()方法来创建一个 actor：

```java
val actor = object : Actor<Int, String>("noopActor", MailboxConfig(5, Channels.OverflowPolicy.THROW)) {
    @Suspendable
    override fun doRun(): String {
        return "Hello"
    }
}
```

名称和邮箱配置都是可选的——如果我们不指定邮箱配置，则默认为无界邮箱。

请注意，我们需要手动将 actor 中的方法标记为@Suspendable。Kotlin 根本不要求我们声明异常，这意味着我们不声明我们正在扩展的基类上的SuspendException 。这意味着在没有更多帮助的情况下，Quasar 不会将我们的方法视为可暂停的。

一旦我们创建了一个 actor，我们就需要启动它——使用spawn()方法启动一个新的 fiber，或者使用 spawnThread() 启动一个新线程。除了纤程和线程之间的区别之外，这两者的工作方式相同。

一旦我们生成了 actor，我们就可以像对待任何其他链一样对待它。这包括能够调用join()等待它完成执行，以及 get()从中检索值：

```java
actor.spawn()

println("Noop Actor: ${actor.get()}")
```

### 2.3. 向演员发送消息

当我们生成一个新的 actor 时， spawn()和 spawnThread()方法返回一个 ActorRef实例。我们可以使用它与 actor 本身进行交互，通过发送消息供其接收。

ActorRef 实现了 SendPort接口，因此我们可以像使用 Channel的生成部分一样使用它。这使我们能够访问 send和 trySend方法，我们可以使用它们将消息传递给 actor：

```java
val actorRef = actor.spawn()

actorRef.send(1)
```

### 2.4. 接收与演员的消息

现在我们可以将消息传递给 actor，我们需要能够用它们做一些事情。我们 在 actor 本身的doRun()方法中执行此操作，我们可以在其中调用 receive()方法来获取要处理的下一条消息：

```java
val actor = object : Actor<Int, Void?>("simpleActor", null) {
    @Suspendable
    override fun doRun(): Void? {
        val msg = receive()
        println("SimpleActor Received Message: $msg")
        return null
    }
}
```

receive()方法将 阻塞在 actor 内部，直到消息可用，然后它会允许 actor 根据需要处理此消息。

通常，参与者将被设计为接收许多消息并处理所有消息。因此，actor 通常会在 doRun()方法中有一个无限循环来处理所有传入的消息：

```java
val actor = object : Actor<Int, Void?>("loopingActor", null) {
    @Suspendable
    override fun doRun(): Void? {
        while (true) {
            val msg = receive()

            if (msg > 0) {
                println("LoopingActor Received Message: $msg")
            } else {
                break
            }
        }

        return null
    }
}
```

这将继续处理传入的消息，直到我们收到 0 值。

### 2.5. 发送消息太快

在某些情况下，actor 处理消息的速度会比发送给它的消息慢。这将导致邮箱填满，并可能溢出。

默认邮箱策略具有无限容量。不过，我们可以在创建 actor 时通过提供MailboxConfig 来配置它。Quasar 也提供了邮箱溢出时如何响应的配置，但目前没有实现。

相反，无论我们指定什么，Quasar 都会使用THROW的策略：

```java
Actor<Int, String>("backlogActor", 
    MailboxConfig(1, Channels.OverflowPolicy.THROW)) {
}
```

如果我们指定了邮箱大小，并且邮箱溢出，那么 actor 内部的receive()方法将导致 actor 通过抛出异常中止。

这不是我们可以以任何方式处理的事情：

```java
try {
    receive()
} catch (e: Throwable) {
    // This is never reached
}
```

发生这种情况时， actor 外部的get()方法也会抛出异常，但这是可以处理的。在这种情况下，我们将得到一个ExecutionException，它包装了一个 QueueCapacityExceededException ，堆栈跟踪指向添加溢出消息的send()方法。

如果我们知道我们正在与邮箱大小有限的 actor 一起工作，我们可以使用 trySend()方法向它发送消息。这不会导致 actor 失败，而是会报告消息是否已成功发送：

```java
val actor = object : Actor<Int, String>("backlogTrySendActor", 
  MailboxConfig(1, Channels.OverflowPolicy.THROW)) {
    @Suspendable
    override fun doRun(): String {
        TimeUnit.MILLISECONDS.sleep(500);
        println("Backlog TrySend Actor Received: ${receive()}")

        return "No Exception"
    }
}

val actorRef = actor.spawn()

actorRef.trySend(1) // Returns True
actorRef.trySend(2) // Returns False
```

### 2.6. 阅读消息太快

在相反的情况下，我们可能有一个参与者试图以比提供消息更快的速度读取消息。通常这很好——actor 将阻塞直到消息可用然后处理它。

但是，在某些情况下，我们希望能够以其他方式处理这个问题。

在接收消息时，我们有三种选择：

-   无限期阻塞直到消息可用
-   阻塞直到消息可用或直到发生超时
-   完全不阻塞

到目前为止，我们已经使用了永远阻塞的receive()方法。

如有必要，我们可以向 receive()方法提供超时详细信息。这将导致它仅在返回之前的那段时间阻塞——如果我们超时，则返回接收到的消息或null ：

```java
while(true) {
    val msg = receive(1, TimeUnit.SECONDS)
    if (msg != null) {
        // Process Message
    } else {
        println("Still alive")
    }
}
```

在极少数情况下，我们可能根本不想阻塞，而是立即返回一条消息或 null。我们可以使用 tryReceive()方法来代替——作为我们在上面看到的trySend() 方法的镜像：

```java
while(true) {
    val msg = tryReceive()
    if (msg != null) {
        // Process Message
    } else {
        print(".")
    }
}
```

### 2.7. 过滤消息

到目前为止，我们的演员已经收到了发送给他们的每一条消息。不过，如果需要，我们可以调整它。

我们的 doRun()方法旨在表示大部分 actor 功能，从中调用的 receive()方法将为我们提供下一个要使用的方法。

我们还可以覆盖一个名为 filterMessage()的方法，该方法将确定我们是否应该处理任何给定的消息。receive()方法为我们调用它，如果它返回null ，则消息不会传递给参与者。例如，以下将过滤掉所有奇数的消息：

```java
override fun filterMessage(m: Any?): Int? {
    return when (m) {
        is Int -> {
            if (m % 2 == 0) {
                m
            } else {
                null
            }
        } else -> super.filterMessage(m)
    }
}
```

filterMessage ()方法还能够在消息通过时对其进行转换。我们返回的值是提供给 actor 的值，因此它同时充当 filter和 map。唯一的限制是返回类型必须匹配参与者的预期消息类型。

例如，以下将过滤掉所有奇数，然后将所有偶数乘以 10：

```java
override fun filterMessage(m: Any?): Int? {
    return when (m) {
        is Int -> {
            if (m % 2 == 0) {
                m  10
            } else {
                null
            }
        }
        else -> super.filterMessage(m)
    }
}
```

### 2.8. 链接参与者和错误处理

到目前为止，我们的演员都严格孤立地工作。我们确实有能力让演员互相观察，这样一个人就可以对另一个人的事件做出反应。我们可以根据需要以对称或非对称方式执行此操作。

目前，我们唯一可以处理的事件是 actor 退出——无论是故意退出还是因为某种原因失败。

当我们使用 watch()方法链接参与者时，我们允许一个参与者——观察者——被告知另一个参与者——被观察者的生命周期事件。这完全是一种单向事件，被观察的演员不会收到关于观察者的任何通知：

```java
val watcherRef = watcher.spawn()
val watchedRef = watched.spawn()
watcher.watch(watchedRef)
```

或者，我们可以使用对称版本的 link()方法。在这种情况下，两个 actor 都被告知对方的生命周期事件，而不是有一个观察者和一个被观察的 actor：

```java
val firstRef = first.spawn()
val secondRef = second.spawn()
first.watch(secondRef)
```

在这两种情况下，效果是一样的。在被观察的 actor 中发生的任何生命周期事件都会导致一条特殊的消息——类型为LifecycleMessage——被添加到观察者 actor 的输入通道。然后如前所述，这 将由 filterMessage() 方法处理。

默认实现随后会将其传递给我们的 actor 中的 handleLifecycleMessage()方法，然后它可以根据需要处理这些消息：

```java
override fun handleLifecycleMessage(m: LifecycleMessage?): Int? {
    println("WatcherActor Received Lifecycle Message: ${m}")
    return super.handleLifecycleMessage(m)
}
```

在这里， link() 和 watch()之间存在细微差别。使用 watch()时，标准的handleLifecycleMessage ()方法只是删除侦听器引用，而使用 link()时，它还会抛出一个异常，该异常将在 doRun()消息中接收，以响应 receive()调用。

这意味着使用 link()会自动导致我们的 doRun()方法在任何链接的 actor 退出时看到异常，而 watch()会强制我们实现 handleLifecycleMessage()方法以便能够对消息做出反应。

### 2.9. 注册和检索演员

到目前为止，我们只是在创建角色后立即与他们进行交互，所以我们已经能够使用作用域中的变量与他们进行交互。但是，有时候，我们需要能够在远离我们产生他们的地方与演员进行互动。

我们可以做到这一点的一种方法是使用标准编程实践——传递ActorRef变量，以便我们可以从需要的地方访问它。

Quasar 为我们提供了另一种实现这一目标的方法。我们可以使用中央 ActorRegistry注册 actor ，然后通过名称访问它们：

```java
val actorRef = actor.spawn()
actor.register()

val retrievedRef = ActorRegistry.getActor<ActorRef<Int>>("theActorName")

assertEquals(actorRef, retrievedRef)
```

这假设我们在创建 actor 时给了它一个名字，并用这个名字注册它。如果 actor 没有命名——例如，如果第一个构造函数参数为null——那么我们可以将名称传递给register()方法：

```java
actor.register("renamedActor")
```

ActorRegistry.getActor()是静态的，因此我们可以从应用程序的任何地方访问它。

如果我们尝试使用未知的名称检索演员，Quasar 将阻止直到这样的演员确实存在。这可能是永远的，所以我们也可以在检索 actor 时设置超时来避免这种情况。如果找不到请求的 actor，这将在超时时返回null ：

```java
val retrievedRef = ActorRegistry.getActor<ActorRef<Int>>("unknownActor", 1, TimeUnit.SECONDS)

Assert.assertNull(retrievedRef)
```

## 3. 演员模板

到目前为止，我们已经从第一原则编写了我们的演员。然而，有几种常见的模式被反复使用。因此，Quasar 以一种我们可以轻松重复使用它们的方式将它们打包。

这些模板通常被称为行为，借用了 Erlang 中使用的相同概念的术语。

许多这些模板被实现为 Actor和 ActorRef的子类，它们添加了额外的功能供我们使用。这将在 Actor类中提供额外的方法来覆盖或从我们实现的功能内部调用，并在 ActorRef类中提供额外的方法，以便调用代码与 actor 交互。

### 3.1. 请求/回复

演员的一个常见用例是一些调用代码会向他们发送消息，然后演员会做一些工作并发回一些结果。然后调用代码接收响应并继续使用它。Quasar 为我们提供了 RequestReplyHelper，让我们轻松地实现这两个方面。

要使用它，我们的消息必须都是 RequestMessage类的子类。这允许 Quasar 存储额外的信息以获得对正确调用代码的回复：

```java
data class TestMessage(val input: Int) : RequestMessage<Int>()
```

作为调用代码，我们可以使用 RequestReplyHelper.call()向参与者提交消息，然后根据需要返回响应或异常：

```java
val result = RequestReplyHelper.call(actorRef, TestMessage(50))
```

在 actor 内部，我们接收消息，处理它，并使用RequestReplyHelper.reply()发送回结果：

```java
val actor = object : Actor<TestMessage, Void?>() {
    @Suspendable
    override fun doRun(): Void {
        while (true) {
            val msg = receive()

            RequestReplyHelper.reply(msg, msg.input  100)
        }

        return null
    }
}
```

### 3.2. 服务器

ServerActor是对上述 内容的扩展，其中请求/回复功能是 actor 本身的一部分。这使我们能够对 actor 进行同步调用并从中获取响应——使用call()方法——或者在不需要响应的情况下对 actor 进行异步调用——使用cast( )方法。

我们通过使用 ServerActor类并将ServerHandler的实例传递给构造函数来实现这种形式的 参与者。这对于处理同步调用、从同步调用返回和处理异步调用的消息类型是通用的。

当我们实现一个 ServerHandler时，我们有几个方法需要实现：

-   init — 处理 actor 启动
-   terminate — 处理 actor 关闭
-   handleCall — 处理同步调用并返回响应
-   handleCast — 处理异步调用
-   handleInfo - 处理既不是Call也不是Cast的消息
-   handleTimeout - 当我们在配置的持续时间内没有收到任何消息时处理

实现此目的的最简单方法是子类 化 AbstractServerHandler，它具有所有方法的默认实现。这让我们能够只实现我们用例所需的位：

```java
val actor = ServerActor(object : AbstractServerHandler<Int, String, Float>() {
    @Suspendable
    override fun handleCall(from: ActorRef<>?, id: Any?, m: Int?): String {
        println("Called with message: " + m + " from " + from)
        return m.toString() ?: "None"
    }

    @Suspendable
    override fun handleCast(from: ActorRef<>?, id: Any?, m: Float?) {
        println("Cast message: " + m + " from " + from)
    }
})
```

我们的 handleCall() 和 handleCast()方法在调用时会处理要处理的消息，但也会提供消息来源的引用和用于识别调用的唯一 ID，以防它们很重要。源ActorRef和 ID 都是可选的，可能不存在。

生成一个 ServerActor将返回一个 Server实例。这是 ActorRef的一个子类，它为我们提供了call()和 cast() 的附加功能 ，以适当地发送消息，以及一个关闭服务器的方法：

```java
val server = actor.spawn()

val result = server.call(5)
server.cast(2.5f)

server.shutdown()
```

### 3.3. 代理服务器

服务器模式为我们提供了一种处理消息和给定响应的特定方式。 另一种方法是 ProxyServer，它具有相同的效果，但形式更实用。这使用[Java 动态代理](https://www.baeldung.com/java-dynamic-proxies)允许我们使用参与者实现标准的 Java 接口。

为了实现这个模式，我们需要定义一个接口来描述我们的功能：

```java
@Suspendable
interface Summer {
    fun sum(a: Int, b: Int) : Int
}
```

这可以是任何标准的 Java 接口，具有我们需要的任何功能。

然后我们将它的一个实例传递给ProxyServerActor构造函数来创建角色：

```java
val actor = ProxyServerActor(false, object : Summer {
    override fun sum(a: Int, b: Int): Int {
        return a + b
    }
})

val summerActor = actor.spawn()
```

还传递给 ProxyServerActor的布尔值是一个标志，用于指示是否将 actor 的链用于void方法。如果设置为true，那么调用链将阻塞直到方法完成，但不会有返回值。

然后，Quasar 将确保我们根据需要在 actor 内部运行方法调用，而不是在调用链上运行。由于 Java 动态代理的强大功能， spawn()或 spawnThread()返回的实例同时实现了 Server(如上所示)和我们的接口：

```java
// Calling the interface method
val result = (summerActor as Summer).sum(1, 2)

// Calling methods on Server
summerActor.shutdown()
```

在内部，Quasar 使用 我们之前看到的服务器行为实现了一个ProxyServerActor ，我们可以以相同的方式使用它。动态代理的使用只是使调用方法更容易实现。

### 3.4. 事件源

事件源模式允许我们创建一个参与者，其中发送给它的消息由多个事件处理程序处理。根据需要添加和删除这些处理程序。这遵循我们多次看到的处理异步事件的模式。这里唯一真正的区别是我们的事件处理程序在 actor strand 而不是调用 strand 上运行。

我们创建一个 没有任何特殊代码的EventSourceActor并以标准方式启动它运行：

```java
val actor = EventSourceActor<String>()
val eventSource = actor.spawn()
```

一旦 actor 被生成，我们就可以为它注册事件处理程序。这些处理程序的主体然后在 actor 的链中执行，但它们在它之外注册：

```java
eventSource.addHandler { msg ->
    println(msg)
}
```

Kotlin 允许我们将事件处理程序编写为 lambda 函数，因此可以使用我们在此处拥有的所有功能。这包括从 lambda 函数外部访问值，但这些值将跨不同的链访问——因此我们在执行此操作时需要小心，就像在任何多线程场景中一样：

```java
val name = "Baeldung"
eventSource.addHandler { msg ->
    println(name + " " + msg)
}
```

我们还获得了事件处理代码的主要好处，因为我们可以在需要时注册任意数量的处理程序，每个处理程序都专注于它的一个任务。所有处理程序都在同一条链上运行——actor 运行的链——因此处理程序需要在处理它们时考虑到这一点。

因此，让这些处理程序通过传递给另一个参与者来进行任何繁重的处理是很常见的。

### 3.5. 有限状态机

[有限状态机](https://en.wikipedia.org/wiki/Finite-state_machine)是一种标准构造，其中我们有固定数量的可能状态，并且一种状态的处理可以切换到不同的状态。我们可以用这种方式表示许多算法。

Quasar 使我们能够将有限状态机建模为 actor，因此 actor 本身维护当前状态，每个状态本质上都是一个消息处理程序。

要实现这一点，我们必须将我们的 actor 编写为 FiniteStateMachineActor的子类。然后我们有尽可能多的方法，每个方法都将处理一条消息并返回新状态以转换为：

```java
@Suspendable
fun lockedState() : SuspendableCallable<SuspendableCallable<>> {
    return receive {msg ->
        when (msg) {
            "PUSH" -> {
                println("Still locked")
                lockedState()
            }
            "COIN" -> {
                println("Unlocking...")
                unlockedState()
            }
            else -> TERMINATE
        }
    }
}
```

然后我们还需要实现 initialState()方法来告诉 actor 从哪里开始：

```java
@Suspendable
override fun initialState(): SuspendableCallable<SuspendableCallable<>> {
    return SuspendableCallable { lockedState() }
}
```

我们的每个状态方法都会做任何它需要做的事情，然后根据需要返回三个可能值之一：

-   要使用的新状态
-   特殊标记TERMINATE，表示 actor 应该关闭
-   null，表示不使用此特定消息——在这种情况下，该消息可用于我们转换到的下一个状态

## 4.反应流

[Reactive Streams](https://www.baeldung.com/java-9-reactive-streams)是一个相对较新的标准，在许多语言和平台上越来越流行。该 API 允许支持异步 I/O 的各种库和框架之间的互操作——包括 RxJava、Akka 和 Quasar 等。

Quasar 实现允许我们在 Reactive streams 和 Quasar channels 之间进行转换，这样就可以将来自这些流的事件馈送到 strand 中，或者将来自 strand 的消息馈送到流中。

反应流具有 发布者和 订阅者的概念。 发布者是可以向订阅者发布消息的东西。相反，Quasar 使用 SendPort和 ReceivePort的概念，我们使用SendPort发送消息，使用ReceivePort 接收相同的消息。Quasar 也有Topic的概念，它只是一种允许我们向多个通道发送消息的机制。

这些是相似的概念，而 Quasar 让我们可以将一个转换为另一个。

### 4.1. 将反应流添加到构建中

反应流不是 Quasar 的核心概念。相反，我们需要添加一个 使我们能够访问它们的[依赖](https://search.maven.org/search?q=g:co.paralleluniverse and a:quasar-reactive-streams)项：

```xml
<dependency>
    <groupId>co.paralleluniverse</groupId>
    <artifactId>quasar-reactive-streams</artifactId>
    <version>0.8.0</version>
</dependency>
```

重要的是，我们使用与正在使用的任何其他 Quasar 依赖项相同版本的此依赖项。同样重要的是，依赖关系与我们在应用程序中使用的 Reactive 流 API 一致。例如，quasar-reactive-streams:0.8.0依赖于 reactive-streams:1.0.2。

如果我们已经不依赖 Reactive Streams，那么这不是问题。如果我们已经依赖 Reactive 流，我们只需要关心这一点，因为我们的本地依赖将覆盖 Quasar 所依赖的。

### 4.2. 发布到反应流

Quasar 使我们能够将 Channel转换为 Publisher，这样我们就可以使用标准 Quasar 通道生成消息，但接收代码可以将其视为反应式Publisher：

```java
val inputChannel = Channels.newChannel<String>(1)
val publisher = ReactiveStreams.toPublisher(inputChannel)
```

完成此操作后，我们可以将 Publisher视为任何其他Publisher 实例，这意味着客户端代码根本不需要知道 Quasar，甚至代码是异步的。

任何发送到 inputChannel的消息都会被添加到这个流中，这样它们就可以被订阅者拉取。

此时，我们的流只能有一个订阅者。尝试添加第二个订阅者将引发异常。

如果我们想支持多个订阅者，那么我们可以使用主题来代替。这在 Reactive Streams 端看起来是一样的，但我们最终得到了一个 支持多个订阅者的发布者：

```java
val inputTopic = Topic<String>()
val publisher = ReactiveStreams.toPublisher(inputTopic)
```

### 4.3. 订阅反应流

与此相反的是将 Publisher转换为 Channel。这允许我们使用标准 Quasar 通道来消费来自 Reactive 流的消息，就好像它是任何其他通道一样：

```java
val channel = ReactiveStreams.subscribe(10, Channels.OverflowPolicy.THROW, publisher)
```

这为我们提供了通道的ReceivePort部分。完成后，我们可以像对待任何其他通道一样对待它，使用标准的 Quasar 构造来使用来自它的消息。这些消息来自 Reactive 流，无论它来自何处。

## 5.总结

我们已经看到了一些可以使用 Quasar 实现的更高级的技术。这些使我们能够编写更好、更易于维护的异步代码，并更轻松地与来自不同异步库的流进行交互。
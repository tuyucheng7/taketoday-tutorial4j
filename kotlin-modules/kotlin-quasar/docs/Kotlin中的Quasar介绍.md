## 一、简介

Quasar 是一个 Kotlin 库，它以更易于管理的方式将一些异步概念引入 Kotlin。这包括轻量级线程、通道、Actor 等。

## 2. 设置构建

要使用最新版本的 Quasar，你需要在 JDK 版本 11 或更高版本上运行。旧版本支持 JDK 7，适用于你还不能升级到 Java 11 的情况。

Quasar 附带了我们需要的四个依赖项，具体取决于你使用的功能。组合这些时，我们必须为它们中的每一个使用相同的版本。

-   [co.paralleluniverse:quasar-core——类星体的核心](https://search.maven.org/search?q=g:co.paralleluniverse and a:quasar-core)。
-   [co.paralleluniverse:quasar-kotlin](https://search.maven.org/search?q=g:co.paralleluniverse and a:quasar-kotlin) – Quasar 的 Kotlin 扩展
-   [co.paralleluniverse:quasar-actors](https://search.maven.org/search?q=g:co.paralleluniverse and a:quasar-actors) – 支持 Quasar 中的演员。我们将在以后的文章中介绍这些内容。
-   [co.paralleluniverse:quasar-reactive-streams](https://search.maven.org/search?q=g:co.paralleluniverse and a:quasar-reactive-streams) – 支持 Quasar 中的反应流。我们将在以后的文章中介绍这些内容。

为了正常工作，Quasar 需要执行一些字节码检测。这可以在运行时使用 Java 代理或在编译时完成。Java 代理是首选方法，因为它没有特殊的构建要求并且可以使用任何设置。但是，这也有缺点，因为 Java 一次只支持一个 Java 代理。

### 2.1. 从命令行运行

使用 Quasar 运行应用程序时，我们使用-javaagent标志向 JVM 指定 Java 代理。这将 quasar-core.jar文件的完整路径作为参数：

```bash
$ java -javaagent:quasar-core.jar -cp quasar-core.jar:quasar-kotlin.jar:application.jar fully.qualified.main.Class
```

### 2.2. 从 Maven 运行我们的应用程序

如果需要，我们还可以使用 Maven 添加 Java 代理。

我们可以通过几个步骤使用 Maven 完成此操作。

首先，我们设置 Dependency Plugin 来生成一个指向quasar-core.jar文件的属性：

```xml
<plugin>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.1.1</version>
    <executions>
        <execution>
            <id>getClasspathFilenames</id>
            <goals>
               <goal>properties</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

然后，我们使用 Exec 插件实际启动我们的应用程序：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>1.3.2</version>
    <configuration>
        <workingDirectory>target/classes</workingDirectory>
        <executable>echo</executable>
        <arguments>
            <argument>-javaagent:${co.paralleluniverse:quasar-core:jar}</argument>
            <argument>-classpath</argument> <classpath/>
            <argument>com.baeldung.quasar.QuasarHelloWorldKt</argument>
        </arguments>
    </configuration>
</plugin>
```

然后我们需要使用正确的调用运行 Maven 以利用它：

```bash
mvn compile dependency:properties exec:exec
```

这确保了最新的代码被编译，并且在我们执行应用程序之前指向我们的 Java 代理的属性可用。

### 2.3. 运行单元测试

如果能在我们的单元测试中获得与 Quasar 代理相同的收益，那就太好了。

我们可以设置 Surefire 在运行测试时使用相同的属性：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.1</version>
    <configuration>
        <argLine>-javaagent:${co.paralleluniverse:quasar-core:jar}</argLine>
    </configuration>
</plugin>
```

如果我们也将它用于我们的集成测试，我们可以为 Failsafe 做同样的事情。

## 3.纤维

Quasar 的核心功能是光纤。它们在概念上与线程相似，但用途略有不同。纤程比线程轻得多——占用的内存和 CPU 时间比标准线程所需的少得多。

纤维并不意味着可以直接替代线程。它们在某些情况下是更好的选择，而在其他情况下则更糟。

具体来说，它们是为执行代码将花费大量时间阻塞在其他纤程、线程或进程上的场景而设计的——例如，等待来自数据库的结果。

纤维与绿色线相似，但不同。绿色线程旨在与操作系统线程一样工作，但不直接映射到操作系统线程。这意味着绿色线程最适用于它们始终在处理的情况，而不是设计用于通常阻塞情况的纤维。

必要时，可以将纤维和线一起使用以达到所需的结果。

### 3.1. 发射光纤

我们以与启动线程的方式非常相似的方式启动纤程。我们创建了一个Fiber<V>类的实例，它包装了我们要执行的代码——以SuspendableRunnable的形式——然后调用start方法：

```java
class MyRunnable : SuspendableRunnable {
    override fun run() {
        println("Inside Fiber")
    }
}
Fiber<Void>(MyRunnable()).start()
```

如果我们愿意，Kotlin 允许我们用 lambda 表达式替换 SuspendableRunnable实例：

```java
val fiber = Fiber<Void> {
    println("Inside Fiber Lambda")
}
fiber.start()
```

甚至还有一个特殊的帮助程序 DSL，它以更简单的形式完成上述所有工作：

```java
fiber @Suspendable {
    println("Inside Fiber DSL")
}
```

这将创建纤程，创建包装提供的块的SuspendableRunnable，并开始运行。

如果你想就地使用 DSL，则比 lambda 更受欢迎。使用 lambda 选项，如果需要，我们可以将 lambda 作为变量传递。

### 3.2. 从纤程返回值

使用 带有纤程的SuspendableRunnable与带有线程的Runnable直接等效。我们还可以将 SuspensableCallable<V>与纤程一起使用，这等同于Callable with threads。

我们可以用与上面相同的方式来做到这一点，使用显式类型、lambda 或使用 DSL：

```java
class MyCallable : SuspendableCallable<String> {
    override fun run(): String {
        println("Inside Fiber")
        return "Hello"
    }
}
Fiber<String>(MyCallable()).start()

fiber @Suspendable {
    println("Inside Fiber DSL")
    "Hello"
}
```

使用 SuspendableCallable而不是 SuspendableRunnable意味着我们的 fiber 现在有一个通用的返回类型——在上面，我们有一个Fiber<String>而不是 Fiber<Unit>。

一旦我们得到了 Fiber<V>，我们就可以通过在 fiber 上使用 get()方法从中提取值——这是 SuspendableCallable返回的值：

```java
val pi = fiber @Suspendable {
    computePi()
}.get()
```

get()方法的工作方式与 java.util.concurrent.Future 相同——而且它直接以 one 的形式工作。这意味着它将阻塞直到值出现。

### 3.3. 等待光纤

在其他情况下，我们可能希望等待纤程完成执行。这通常与我们使用异步代码的原因背道而驰，但在某些情况下我们需要这样做。

与 Java 线程一样，我们有一个 join()方法，我们可以在Fiber<V> 上调用该方法，该方法将阻塞直到它完成执行：

```java
val fiber = Fiber<Void>(Runnable()).start()
fiber.join()
```

我们还可以提供超时，这样如果光纤完成的时间比预期的要长，那么我们就不会无限期地阻塞：

```java
fiber @Suspendable {
    TimeUnit.SECONDS.sleep(5)
}.join(2, TimeUnit.SECONDS)
```

如果纤程确实花费了太长时间，则 join()方法将抛出 TimeoutException以指示发生了这种情况。我们还可以以相同的方式将这些超时提供给我们之前看到的get()方法。

### 3.4. 调度纤程

光纤都在调度程序上运行。具体来说，通过FiberScheduler或其子类的某个实例 。如果未指定，则将使用默认值，它可以作为 DefaultFiberScheduler.instance直接使用。

我们可以使用几个系统属性来配置我们的调度程序：

-   co.paralleluniverse.fibers.DefaultFiberPool.parallelism – 要使用的线程数。
-   co.paralleluniverse.fibers.DefaultFiberPool.exceptionHandler – 纤程抛出异常时使用的异常处理程序
-   co.paralleluniverse.fibers.DefaultFiberPool.monitor – 监控光纤的方法
-   co.paralleluniverse.fibers.DefaultFiberPool.detailedFiberInfo – 监视器是否获取详细信息。

默认情况下，这将是一个FiberForkJoinScheduler，它在每个可用的 CPU 内核上运行一个线程，并通过 JMX 提供简短的监控信息。

对于大多数情况，这是一个不错的选择，但有时你可能需要不同的选择。另一个标准选择是FiberExecutorScheduler，它在提供的 Java Executor上运行纤程以在线程池上运行，或者如果需要，你可以提供自己的——例如，你可能需要在 AWT 或 Swing 中的特定线程上运行它们设想。

### 3.5. 悬浮方法

Quasar 根据称为可悬挂方法的概念工作。这些是特殊标记的方法，可以暂停，因此可以在纤程内运行。

通常这些方法是任何声明它们抛出 SuspendException的方法。然而，因为这并不总是可能的，我们还有一些其他的特殊情况可以使用：

-   我们用@Suspendable注解注解的任何方法
-   任何以 Java 8 lambda 方法结束的东西——它们不能声明异常，因此被特殊对待
-   由反射进行的任何调用，因为这些是在运行时而不是编译时计算的

此外，不允许将构造函数或类初始值设定项用作可挂起的方法。

我们也不能将 同步块与可暂停方法一起使用。这意味着我们不能将方法本身标记为同步的，我们不能从它内部调用同步方法，我们不能在方法内部使用同步块。

就像我们不能在可挂起方法中使用synchronized一样，它们不应该以其他方式直接阻塞执行线程——例如，使用Thread.sleep()。这样做会导致性能问题并可能导致系统不稳定。

执行任何这些操作都会从 Quasar java 代理中生成错误。在默认情况下，我们会看到控制台的输出表明发生了什么：

```plaintext
WARNING: fiber Fiber@10000004:fiber-10000004[task: ParkableForkJoinTask@40c7e038(Fiber@10000004), target: co.paralleluniverse.kotlin.KotlinKt$fiber$sc$1@7d289a68, scheduler: co.paralleluniverse.fibers.FiberForkJoinScheduler@5319f44e] is blocking a thread (Thread[ForkJoinPool-default-fiber-pool-worker-3,5,main]).
	at java.base@11/java.lang.Thread.sleep(Native Method)
	at java.base@11/java.lang.Thread.sleep(Thread.java:339)
	at java.base@11/java.util.concurrent.TimeUnit.sleep(TimeUnit.java:446)
	at app//com.baeldung.quasar.SimpleFiberTest$fiberTimeout$1.invoke(SimpleFiberTest.kt:43)
	at app//com.baeldung.quasar.SimpleFiberTest$fiberTimeout$1.invoke(SimpleFiberTest.kt:12)
	at app//co.paralleluniverse.kotlin.KotlinKt$fiber$sc$1.invoke(Kotlin.kt:32)
	at app//co.paralleluniverse.kotlin.KotlinKt$fiber$sc$1.run(Kotlin.kt:65535)
	at app//co.paralleluniverse.fibers.Fiber.run(Fiber.java:1099)
```

## 4.股线

Strands 是 Quasar 中的一个概念，它结合了 fibers 和 threads。它们允许我们根据需要交换线程和光纤，而无需我们应用程序的其他部分关心。

我们通过使用Strand.of()将 thread 或 fiber 实例包装在 Strand 类中来创建一个 Strand ：

```java
val thread: Thread = ...
val strandThread = Strand.of(thread)

val fiber: Fiber = ...
val strandFiber = Strand.of(fiber)
```

或者，我们可以使用Strand.currentStrand()为当前正在执行的线程或纤程获取一个 Strand 实例：

```java
val myFiber = fiber @Suspendable {
    // Strand.of(myFiber) == Strand.currentStrand()
}
```

完成后，我们可以使用相同的 API 与两者交互，允许我们询问 strand，等待它完成执行等等：

```java
strand.id // Returns the ID of the Fiber or Thread
strand.name // Returns the Name of the Fiber or Thread
strand.priority // Returns the Priority of the Fiber or Thread

strand.isAlive // Returns if the Fiber or Thread is currently alive
strand.isFiber // Returns if the Strand is a Fiber

strand.join() // Block until the Fiber or Thread is completed
strand.get() // Returns the result of the Fiber or Thread
```

## 5. 包装回调

纤程的主要用途之一是包装使用回调将状态返回给调用者的异步代码。

Quasar 提供了一个名为 FiberAsync<T, E>的类，我们可以将其用于这种情况。我们可以扩展它以提供基于纤程的 API，而不是为相同代码提供基于回调的 API。

这是通过编写一个实现我们的回调接口的类来完成的，它扩展了FiberAsync 类并将回调方法委托给 FiberAsync类来处理：

```java
interface PiCallback {
    fun success(result: BigDecimal)
    fun failure(error: Exception)
}

class PiAsync : PiCallback, FiberAsync<BigDecimal, Exception>() {
    override fun success(result: BigDecimal) {
        asyncCompleted(result)
    }

    override fun failure(error: Exception) {
        asyncFailed(error)
    }

    override fun requestAsync() {
        computePi(this)
    }
}
```

我们现在有一个可用于计算结果的类，我们可以将其视为一个简单的调用而不是基于回调的 API：

```java
val result = PiAsync().run()
```

这将返回成功值——我们传递给asyncCompleted()的值——或者抛出失败异常——我们传递给asyncFailed的值。

当我们使用它时，Quasar 将启动一个新的 fiber 直接绑定到当前的 fiber 并将暂停当前的 fiber 直到结果可用。这意味着我们必须在纤程内而不是在线程内使用它。这也意味着FiberAsync的实例必须在同一纤程内创建和运行才能工作。

此外，它们不可重复使用——一旦完成，我们就无法重新启动它们。

## 6.渠道

Quasar 引入了通道的概念以允许消息在不同链之间传递。[这些与Go 编程语言中的 Channels](https://gobyexample.com/channels)非常相似。

### 6.1. 创建频道

我们可以使用静态方法Channels.newChannel创建频道。

```java
Channels.newChannel(bufferSize, overflowPolicy, singleProducerOptimized, singleConsumerOptimized);
```

因此，当缓冲区已满并针对单个生产者和消费者时阻塞的示例是：

```java
Channels.newChannel<String>(1024, Channels.OverflowPolicy.BLOCK, true, true);
```

还有一些用于创建某些原始类型的通道的特殊方法 ——newIntChannel、newLongChannel、newFloatChannel和 newDoubleChannel。如果我们发送这些特定类型的消息并在纤程之间获得更高效的流，我们可以使用这些。请注意，我们永远不能使用来自多个消费者的这些原始通道——这是 Quasar 提供的效率的一部分。

### 6.2. 使用频道

生成的 Channel对象实现了两个不同的接口—— SendPort和 ReceivePort。

我们可以使用 来自正在消费消息的链的ReceivePort接口：

```java
fiber @Suspendable {
    while (true) {
        val message = channel.receive()
        println("Received: $message")
    }
}
```

然后，我们可以使用同一通道的 SendPort接口来生成将由上述内容使用的消息：

```java
channel.send("Hello")
channel.send("World")
```

出于明显的原因，我们不能在同一个链中使用这两个，但我们可以在不同的链之间共享同一个 通道实例，以允许两者之间共享消息。在这种情况下，股线可以是纤维或线。

### 6.3. 关闭渠道

在上面，我们有一个从通道读取的无限循环。这显然不理想。

我们应该更喜欢做的是在通道主动生成消息时一直循环，然后在通道结束时停止。我们可以使用 close()将通道标记为已关闭，并使用 isClosed属性来查看通道是否已关闭：

```java
fiber @Suspendable {
    while (!channel.isClosed) {
        val message = channel.receive()
        println("Received: $message")
    }
    println("Stopped receiving messages")
}

channel.send("Hello")
channel.send("World")

channel.close()
```

### 6.4. 阻塞通道

渠道，就其本质而言，是阻塞概念。ReceivePort将 阻塞，直到有消息可供处理，我们可以将SendPort配置 为阻塞，直到可以缓冲消息为止。

这利用了纤维的一个重要概念——它们是可悬浮的。当任何这些阻塞动作发生时，Quasar 将使用非常轻量级的机制来暂停 fiber 直到它可以继续它的工作，而不是重复轮询通道。这允许将系统资源用于其他地方——例如，用于处理其他光纤。

### 6.5. 等待多个频道

我们已经看到 Quasar 可以在单个通道上阻塞，直到可以执行操作为止。Quasar 还提供了跨多个通道等待的能力。

我们使用 Selector.select语句执行此操作。这个概念可能在 Go 和[Java NIO](https://www.baeldung.com/java-nio-selector)中都很熟悉。

select()方法采用SelectAction实例的 集合，并将阻塞直到执行以下操作之一：

```java
fiber @Suspendable {
    while (!channel1.isClosed && !channel2.isClosed) {
        val received = Selector.select(
          Selector.receive(channel1), 
          Selector.receive(channel2)
        )

        println("Received: $received")
    }
}
```

在上面，我们可以写入多个通道，我们的光纤将立即读取任何有可用消息的通道。选择器将仅使用可用的第一条消息，因此不会丢弃任何消息。

我们也可以使用它发送到多个频道：

```java
fiber @Suspendable {
    for (i in 0..10) {
        Selector.select(
          Selector.send(channel1, "Channel 1: $i"),
          Selector.send(channel2, "Channel 2: $i")
        )
    }
}
```

与 receive一样，这将阻塞直到可以执行第一个操作，然后执行该操作。这有一个有趣的副作用，即消息将发送到一个通道，但它发送到的通道恰好是第一个具有可用缓冲区空间的通道。这使我们能够准确地根据来自这些通道接收端的背压跨多个通道分发消息。

### 6.6. 股票频道

我们可以创建的一种特殊频道是 ticker 频道。这些在概念上类似于证券交易所代码——消费者看到每条消息并不重要，因为新消息会取代旧消息。

当我们有持续不断的状态更新时，这些非常有用——例如，证券交易所价格或完成百分比。

我们将这些创建为普通通道，但我们使用 OverflowPolicy.DISPLACE设置。在这种情况下，如果在生成新消息时缓冲区已满，那么最旧的消息将被静默丢弃以为其腾出空间。

我们只能从单条链中使用这些通道。但是，我们可以创建一个TickerChannelConsumer来跨多个链从该通道读取数据：

```java
val channel = Channels.newChannel<String>(3, Channels.OverflowPolicy.DISPLACE)

for (i in 0..10) {
    val tickerConsumer = Channels.newTickerConsumerFor(channel)
    fiber @Suspendable {
        while (!tickerConsumer.isClosed) {
            val message = tickerConsumer.receive()
            println("Received on $i: $message")
        }
        println("Stopped receiving messages on $i")
    }
}

for (i in 0..50) {
    channel.send("Message $i")
}

channel.close()
```

TickerChannelConsumer的每个实例 都可能接收发送到包装通道的所有消息——允许溢出策略丢弃的任何消息。

我们将始终以正确的顺序接收消息，并且我们可以以我们需要工作的速率消耗每个TickerChannelConsumer -一个运行缓慢的光纤不会影响任何其他光纤。

我们还将知道包装通道何时关闭 ，以便我们可以停止从我们的TickerChannelConsumer 读取数据。 这允许生产者不关心消费者阅读消息的方式，也不关心正在使用的通道类型。

### 6.7. 通道的功能转换

我们都习惯于使用[流](https://www.baeldung.com/java-8-streams-introduction)在 Java 中进行功能转换。我们可以在通道上应用这些相同的标准转换——发送和接收变化。

这些可以应用的操作包括：

-   filter – 过滤掉不符合给定 lambda 的消息
-   map – 在消息流经通道时转换消息
-   flatMap – 与map相同，只是将一条消息转化为多条消息
-   reduce –对通道应用[缩减函数](https://www.baeldung.com/java-stream-reduce)

例如，我们可以使用以下方法将 ReceivePort<String>转换为反转所有流经它的字符串：

```java
val transformOnReceive = Channels.map(channel, Function<String, String> { msg: String? -> msg?.reversed() })
```

这不会影响原来频道上的消息，仍然可以在别处消费，看不到这个改造的效果。

或者，我们可以将 SendPort<String>转换为在将所有字符串写入通道时强制所有字符串大写的类型，如下所示：

```java
val transformOnSend = Channels.mapSend(channel, Function<String, String> { msg: String? -> msg?.toUpperCase() })
```

这将影响写入的消息，在这种情况下，包装的通道将只能看到转换后的消息。但是，如果需要，我们仍然可以直接写入正在包装的通道以绕过此转换。

## 7.数据流

Quasar Core 为我们提供了一些工具来支持响应式编程。这些不像[RxJava](https://www.baeldung.com/rx-java)那样强大，但对于大多数情况来说已经足够了。

我们可以访问两个概念—— Val和 Var。 Val代表一个常量值， Var代表一个变化的值。

这两种类型都没有值或SuspendableCallable 构造，后者将在 fiber 中用于计算值：

```kotlin
val a = Var<Int>()
val b = Val<Int>()

val c = Var<Int> { a.get() + b.get() }
val d = Var<Int> { a.get()  b.get() }

// (ab) - (a+b)
val initialResult = Val<Int> { d.get() - c.get() }
val currentResult = Var<Int> { d.get() - c.get() }
```

最初， initialResult和 currentResult没有值，试图从中获取值将阻塞当前链。一旦我们给出a和 b值，我们就可以从initialResult和currentResult中读取值。 

除此之外，如果我们进一步更改 a ， 则currentResult将更新以反映这一点，但 initialResult不会：

```java
a.set(2)
b.set(4)

Assert.assertEquals(2, initialResult.get())
Assert.assertEquals(2, currentResult.get())

a.set(3)

Assert.assertEquals(2, initialResult.get()) // Unchanged
Assert.assertEquals(5, currentResult.get()) // New Value
```

如果我们尝试更改 b，那么我们将抛出异常，因为Val只能分配一个值 。

## 八、总结

本文介绍了可用于异步编程的 Quasar 库。我们在这里看到的只是我们可以用 Quasar 实现的基础。为什么不在下一个项目中尝试一下呢？
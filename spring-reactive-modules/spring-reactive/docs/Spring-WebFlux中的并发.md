## 一、简介

在本教程中，我们将探索使用[Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)编写的反应式程序中的并发性。

我们将从讨论与反应式编程相关的并发性开始。然后我们将了解 Spring WebFlux 如何在不同的反应式服务器库上提供并发抽象。

## 2.响应式编程的动机

典型的**Web 应用程序包含几个复杂的交互部分**。**许多这些交互本质上是阻塞的**，例如涉及数据库调用以获取或更新数据的交互。**然而，其他几个是独立的，可以同时执行，**可能是并行执行。

**例如，两个用户对 Web 服务器的请求可以由不同的线程处理。**在*多核*平台上，这在整体响应时间方面具有明显优势。因此，**这种并发模型被称为\*每个请求一个线程模型\***：

[![每个请求模型的线程](https://www.baeldung.com/wp-content/uploads/2020/08/Thread-per-Request-Model.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/Thread-per-Request-Model.jpg)

在上图中，每个线程一次处理一个请求。

虽然基于线程的并发为我们解决了部分问题，但它无法解决**我们在单线程中的大多数交互仍然处于阻塞**状态的事实。此外，我们用于在 Java 中实现并发的本机线程在上下文切换方面付出了巨大的代价。

同时，随着 Web 应用程序面临越来越多的请求，每个请求**一个\*线程的模型\*开始达不到预期**。

因此，**我们需要一个并发模型来帮助我们用相对较少的线程数来处理越来越多的请求**。**这是采用[响应式编程](https://www.baeldung.com/java-reactive-systems)的主要动机之一。**

## 3. 反应式编程中的并发

**响应式编程帮助我们根据数据流和通过它们传播变化来构建程序**。在完全非阻塞的环境中，这可以使我们以更好的资源利用率实现更高的并发。

然而，反应式编程是否完全背离了基于线程的并发？虽然这是一个强有力的声明，但**反应式编程肯定有一种非常不同的方法来使用线程来实现并发**。因此**，反应式编程带来的根本区别是异步性。**

换句话说，程序流从一系列同步操作转换为异步事件流。

例如，在反应模型下，对数据库的读取调用不会在获取数据时阻塞调用线程。该**调用立即返回其他人可以订阅的发布者**。订阅者可以在事件发生后对其进行处理，甚至可以自己进一步生成事件：

[![反应模型](https://www.baeldung.com/wp-content/uploads/2020/08/Reactive-Model.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/Reactive-Model.jpg)

最重要的是，反应式编程并不强调应该生成和使用哪些线程事件。相反，**重点是将程序构建为异步事件流**。

这里的发布者和订阅者不需要属于同一个线程。**这有助于我们更好地利用可用线程，从而提高整体并发性。**

## 4.事件循环

**有几种编程模型描述了并发的反应式方法**。

在本节中，我们将研究其中的一些，以了解响应式编程如何以更少的线程实现更高的并发性。

**一种这样的服务器反应式异步编程模型是\*事件循环\* \*模型\***：



[![事件循环](https://www.baeldung.com/wp-content/uploads/2020/08/Event-Loop.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/Event-Loop.jpg)

以上是一个*事件循环*的抽象设计，展示了反应式异步编程的思想：

-   **事件\*循环\*在单个线程中连续运行**，尽管我们可以拥有与可用内核数量一样多的*事件循环。*
-   ***事件循环\*按顺序处理\*事件队列\*中的**事件，并在向*平台*注册*回调后立即返回。*
-   该*平台*可以触发操作的完成，例如数据库调用或外部服务调用。
-   ***事件循环\*可以触发\*操作完成\*通知的\*回调\*，并将结果返回给原始调用者。**

事件*循环* *模型*在许多平台中实现，包括*[Node.js](https://nodejs.org/en/)*、*[Netty](https://netty.io/)*和*[Ngnix](https://www.nginx.com/)*。*[它们提供比Apache HTTP Server](https://httpd.apache.org/)*、*[Tomcat](https://www.baeldung.com/tomcat)*或*[JBoss](https://www.redhat.com/fr/technologies/jboss-middleware/application-platform)*等传统平台更好的可扩展性。

## 5. 使用 Spring WebFlux 进行响应式编程

现在我们对反应式编程及其并发模型有了足够的了解，可以探索 Spring WebFlux 中的主题。

**WebFlux 是** **Spring****的** **reactive-stack web 框架**，在 5.0 版本中加入。

让我们探索 Spring WebFlux 的服务器端堆栈，以了解它如何补充 Spring 中的传统 Web 堆栈：

[![春季网络堆栈](https://www.baeldung.com/wp-content/uploads/2020/08/Spring-Web-Stack.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/Spring-Web-Stack.jpg)

正如我们所看到的，**Spring WebFlux 与 Spring 中的传统 Web 框架并列，并不一定会取代它**。

这里有几个要点需要注意：

-   Spring WebFlux 通过功能路由扩展了传统的基于注解的编程模型。
-   此外，它使底层 HTTP 运行时适应*Reactive Streams API，*使运行时可互操作。
-   它能够支持各种反应式运行时，包括 Servlet 3.1+ 容器，如 Tomcat、Reactor、Netty 或[Undertow](https://www.baeldung.com/jboss-undertow)。
-   最后，它包括*WebClient*，一个用于 HTTP 请求的反应式非阻塞客户端，提供功能性和流畅的 API。

## 6. 支持的运行时中的线程模型

正如我们之前讨论的，**响应式程序倾向于只使用几个线程**并充分利用它们。但是，线程的数量和性质取决于我们选择的实际 Reactive Stream API 运行时。

澄清一下，**Spring WebFlux 可以通过\*HttpHandler\***提供的通用 API 来适应不同的运行时。这个 API 是一个简单的契约，只有一个方法，它提供了对不同服务器 API 的抽象，比如 Reactor Netty、Servlet 3.1 API 或 Undertow API。

让我们检查其中几个实现的线程模型。

**虽然 Netty 是 WebFlux 应用程序中的默认服务器，但只需声明正确的依赖关系即可切换到任何其他受支持的服务器**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-reactor-netty</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
</dependency>复制
```

虽然可以通过多种方式观察在 Java 虚拟机中创建的线程，但很容易从*Thread*类本身中提取它们：

```java
Thread.getAllStackTraces()
  .keySet()
  .stream()
  .collect(Collectors.toList());复制
```

### 6.1. 反应堆网络

正如我们所说，[Reactor Netty](https://netty.io/)是 Spring Boot WebFlux starter 中的默认嵌入式服务器。让我们看看 Netty 默认创建的线程。首先，我们不会添加任何其他依赖项或使用 WebClient。因此，如果我们启动一个使用其 SpringBoot 启动器创建的 Spring WebFlux 应用程序，我们可以期望看到它创建的一些默认线程：



[![1](https://www.baeldung.com/wp-content/uploads/2020/08/1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/1.jpg)

请注意，除了服务器的普通线程外，**Netty 还生成了一堆用于请求处理的工作线程**。**这些通常是可用的 CPU 内核。**这是四核机器上的输出。我们还会看到 JVM 环境中典型的一堆内务处理线程，但它们在这里并不重要。

**Netty 使用事件循环模型以反应式异步方式提供高度可扩展的并发性。**让我们看看 Netty 如何利用**Java NIO 实现事件循环来提供这种可伸缩性**：



[![Netty 线程模型](https://www.baeldung.com/wp-content/uploads/2020/08/Netty-Threading-Model.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/Netty-Threading-Model.jpg)

这里，***EventLoopGroup\*管理着一个或多个\*EventLoop，\*它必须是持续运行的**。因此，**不建议创建比可用核心数更多的\*EventLoop 。\***

EventLoopGroup进一步为*每个*新创建的*Channel分配一个**EventLoop*。因此，在*Channel*的生命周期中，所有操作都由同一个线程执行。

### 6.2. 阿帕奇汤姆猫

**传统的 Servlet 容器也支持 Spring WebFlux，例如[Apache Tomcat](https://tomcat.apache.org/)。**

**WebFlux 依赖于具有非阻塞 I/O 的 Servlet 3.1 API**。虽然它在低级适配器后面使用 Servlet API，但不能直接使用 Servlet API。

让我们看看在 Tomcat 上运行的 WebFlux 应用程序中我们期望什么样的线程：



[![2](https://www.baeldung.com/wp-content/uploads/2020/08/2-841x1024-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/2-841x1024-1.jpg)

我们在这里看到的线程数量和类型与我们之前观察到的有很大不同。

首先，**Tomcat 以更多工作线程开始，默认为 10 个**。当然，我们还会看到 JVM 和 Catalina 容器中一些典型的管家线程，在本次讨论中我们可以忽略它们。

我们需要了解带有 Java NIO 的 Tomcat 的架构，以便将其与我们上面看到的线程相关联。

**Tomcat 5 及更高版本在其连接器组件中支持 NIO，该组件主要负责接收请求**。

另一个 Tomcat 组件是 Container 组件，它负责容器管理功能。

我们在这里感兴趣的是连接器组件为支持 NIO 而实现的线程模型。它由*Acceptor*、*Poller*和*Worker*组成，作为*NioEndpoint*模块的一部分：



[![Tomcat NIO 连接器](https://www.baeldung.com/wp-content/uploads/2020/08/Tomcat-NIO-Connector.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/Tomcat-NIO-Connector.jpg)

**Tomcat 为\*Acceptor\*、\*Poller\*和\*Worker 生成一个或多个线程，\*通常带有一个专用于\*Worker\***的线程池。

虽然对 Tomcat 体系结构的详细讨论超出了本文的范围，但我们现在应该有足够的洞察力来理解我们之前看到的线程。

## 7. *WebClient中的线程模型*

**[\*WebClient\*](https://www.baeldung.com/spring-5-webclient)** 是**响应式 HTTP 客户端，它是 Spring WebFlux 的一部分**。我们可以在需要基于 REST 的通信时随时使用它，这使我们能够创建*端到端* *反应*。

正如我们之前所见，反应式应用程序只使用几个线程，因此应用程序的任何部分都没有余地阻塞线程。因此，*WebClient*在帮助我们实现 WebFlux 的潜力方面起着至关重要的作用。

### 7.1. 使用*网络客户端*

使用*WebClient*也非常简单。**我们不需要包含任何特定的依赖项，因为它是 Spring WebFlux 的一部分**。

让我们创建一个返回[*Mono*](https://www.baeldung.com/java-string-from-mono)的简单 REST 端点：

```java
@GetMapping("/index")
public Mono<String> getIndex() {
    return Mono.just("Hello World!");
}复制
```

然后我们将使用*WebClient*调用此 REST 端点并响应式地使用数据：

```java
WebClient.create("http://localhost:8080/index").get()
  .retrieve()
  .bodyToMono(String.class)
  .doOnNext(s -> printThreads());复制
```

在这里，我们还打印了使用我们之前讨论的方法创建的线程。

### 7.2. 了解线程模型

那么，线程模型在*WebClient*的情况下如何工作？

好吧，毫不奇怪，***WebClient还使用\**事件循环模型\*****实现了并发**。当然，它依赖于底层运行时来提供必要的基础设施。

**如果我们在 Reactor Netty 上运行\*WebClient\*，它会共享 Netty 用于服务器的事件循环**。因此，在这种情况下，我们可能不会注意到创建的线程有太大差异。

然而，***WebClient\*也支持 Servlet 3.1+ 容器，如 Jetty，但它的工作方式不同**。

如果我们比较在使用和不使用*WebClient运行*[Jetty](https://www.eclipse.org/jetty/)的 WebFlux 应用程序上创建的线程，我们会注意到一些额外的线程。

在这里，*WebClient*必须创建它的*事件循环*。所以我们可以看到这个事件循环创建的固定数量的处理线程：



[![3](https://www.baeldung.com/wp-content/uploads/2020/08/3.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/3.jpg)

**在某些情况下，** **为客户端和服务器提供单独的线程池可以提供更好的性能**。虽然这不是 Netty 的默认行为，但如果需要，始终可以为*WebClient声明一个专用线程池。*

我们将在后面的部分中看到这是如何实现的。

## 8. 数据访问库中的线程模型

正如我们之前看到的，**即使是一个简单的应用程序通常也包含几个需要连接的部分。**

这些部分的典型示例包括数据库和消息代理。**与其中许多连接的现有库仍然处于阻塞状态，但这种情况正在迅速改变。**

**现在有几个数据库提供用于连接的反应库**。**其中许多库在[Spring Data](https://www.baeldung.com/spring-data)**中可用，而我们也可以直接使用其他库。

我们对这些库使用的线程模型特别感兴趣。

### 8.1. 春季数据 MongoDB

[Spring Data MongoDB为构建在](https://www.baeldung.com/spring-data-mongodb-tutorial)[MongoDB Reactive Streams 驱动程序](https://mongodb.github.io/mongo-java-driver/)之上的 MongoDB 提供反应性存储库支持。最值得注意的是，该驱动程序完全实现了 Reactive Streams API，以提供具有*非阻塞背压的***异步流处理**。

在 Spring Boot 应用程序中为 MongoDB 的反应式存储库设置支持就像添加依赖项一样简单：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>复制
```

这将允许我们创建一个存储库，并使用它以非阻塞方式在 MongoDB 上执行一些基本操作：

```java
public interface PersonRepository extends ReactiveMongoRepository<Person, ObjectId> {
}
.....
personRepository.findAll().doOnComplete(this::printThreads);复制
```

那么当我们在 Netty 服务器上运行这个应用程序时，我们期望看到什么样的线程呢？

好吧，毫不奇怪，我们不会看到太大的区别，因为**Spring** **Data 反应式存储库使用可用于服务器的相同事件循环。**

### 8.2. 反应堆卡夫卡

**Spring 仍在构建对反应式 Kafka 的全面支持。**但是，我们确实有 Spring 之外可用的选项。

**[Reactor Kafka](https://projectreactor.io/docs/kafka/release/reference/#_introduction)是基于 Reactor 的 Kafka 反应式 API**。Reactor Kafka 支持使用函数式 API 发布和使用消息，还具有*非阻塞背压*。

首先，我们需要在我们的应用程序中添加所需的依赖项以开始使用 Reactor Kafka：

```xml
<dependency>
    <groupId>io.projectreactor.kafka</groupId>
    <artifactId>reactor-kafka</artifactId>
    <version>1.3.10</version>
</dependency>复制
```

这应该使我们能够以非阻塞方式向 Kafka 生成消息：

```java
// producerProps: Map of Standard Kafka Producer Configurations
SenderOptions<Integer, String> senderOptions = SenderOptions.create(producerProps);
KafkaSender<Integer, String> sender =  KafkaSender.create(senderOptions);
Flux<SenderRecord<Integer, String, Integer>> outboundFlux = Flux
  .range(1, 10)
  .map(i -> SenderRecord.create(new ProducerRecord<>("reactive-test", i, "Message_" + i), i));
sender.send(outboundFlux).subscribe();复制
```

同样，我们也应该能够以非阻塞方式使用来自 Kafka 的消息：

```java
// consumerProps: Map of Standard Kafka Consumer Configurations
ReceiverOptions<Integer, String> receiverOptions = ReceiverOptions.create(consumerProps);
receiverOptions.subscription(Collections.singleton("reactive-test"));
KafkaReceiver<Integer, String> receiver = KafkaReceiver.create(receiverOptions);
Flux<ReceiverRecord<Integer, String>> inboundFlux = receiver.receive();
inboundFlux.doOnComplete(this::printThreads)复制
```

这非常简单且不言自明。

我们订阅了 Kafka 中的主题*reactive-test*，并获得了*Flux*消息。

对**我们来说有趣的是创建的线程**：



[![4](https://www.baeldung.com/wp-content/uploads/2020/08/4.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/4.jpg)

**我们可以看到一些不是典型的 Netty 服务器的线程**。

这表明 Reactor Kafka 管理着自己的线程池，有少数工作线程专门参与 Kafka 消息处理。当然，我们会看到一堆其他与 Netty 和 JVM 相关的线程，我们可以忽略。

**Kafka 生产者使用单独的网络线程向代理发送请求。***此外，它们在单线程池化调度*程序上向应用程序传递响应。

另一方面，Kafka 消费者在每个消费者组中都有一个线程阻塞以侦听传入消息。然后传入的消息被安排在不同的线程池上进行处理。

## 9. WebFlux 中的调度选项

到目前为止，我们已经看到**反应式编程在只有几个线程的完全非阻塞环境中真正闪耀**。但这也意味着，如果确实有一个部分在阻塞，则会导致更差的性能。这是因为阻塞操作可以完全冻结事件循环。

那么，**我们如何处理响应式编程中长时间运行的进程或阻塞操作呢？**

老实说，最好的选择就是避开它们。然而，这并不总是可行的，**我们可能需要为应用程序的这些部分制定专门的调度策略**。

Spring WebFlux**提供了一种机制，可以将处理切换到数据流链之间的不同线程池**。这可以为我们提供对某些任务所需的调度策略的精确控制。当然，*WebFlux*能够基于底层反应库中可用的线程池抽象（称为调度程序）提供此功能。

### 9.1. 反应堆

在[Reactor](https://projectreactor.io/)中，***Scheduler\*类定义了执行模型，以及执行发生的位置**。

*[Schedulers](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html)*类提供了许多执行上下文，例如*immediate*、*single*、*elastic*和*parallel*。这些提供了不同类型的线程池，可用于不同的作业。此外，我们始终可以使用预先存在的[*ExecutorService*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html)创建自己的[*Scheduler*](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Scheduler.html)。

*Schedulers*为我们提供了多种执行上下文，而Reactor**也为我们提供了不同的执行上下文切换方式**。这些是*publishOn*和*subscribeOn*方法。

我们可以在链中的任何位置将*publishOn*与*Scheduler一起使用，该**Scheduler*会影响所有后续运算符。

虽然我们也可以在链中的任何位置将*subscribeOn*与*Scheduler*一起使用，但它只会影响发射源的上下文。

如果我们还记得的话， Netty 上的*WebClient*共享为服务器创建的相同*事件循环*作为默认行为。然而，我们可能有充分的理由为 WebClient 创建一个专用的线程池。

让我们看看我们如何在 Reactor 中实现这一点，Reactor 是 WebFlux 中的默认反应库：

```java
Scheduler scheduler = Schedulers.newBoundedElastic(5, 10, "MyThreadGroup");

WebClient.create("http://localhost:8080/index").get()
  .retrieve()
  .bodyToMono(String.class)
  .publishOn(scheduler)
  .doOnNext(s -> printThreads());复制
```

早些时候，我们没有观察到在使用或不使用*WebClient*的 Netty 上创建的线程有任何差异。然而，如果我们现在运行上面的代码，**我们将观察到一些新线程被创建**：



[![5](https://www.baeldung.com/wp-content/uploads/2020/08/5.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/5.jpg)

在这里**我们可以看到作为\*有界弹性线程池\***的一部分创建的线程。*这是来自WebClient*的响应在订阅后发布的地方。

这留下了用于处理服务器请求的主线程池。

### 9.2. RxJava

**RxJava 中**的默认行为与 Reactor 中的默认行为没有太大区别。

*Observable*和我们在其上应用的运算符链执行它们的工作并在调用订阅的同一线程上通知观察者。此外，[RxJava](https://github.com/ReactiveX/RxJava)与 Reactor 一样，提供了将前缀或自定义调度策略引入链中的方法。

RxJava 还有**一个\*[Schedulers](http://reactivex.io/RxJava/javadoc/io/reactivex/schedulers/Schedulers.html)类，它为\*[\*Observable\*](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html)链提供了多种执行模型**。这些包括*new thread*、*immediate*、*trampoline*、*io*、*computation*和*test*。当然，它也允许我们从 Java [*Executor定义一个*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executor.html)*[Scheduler](http://reactivex.io/documentation/scheduler.html)* 。

此外，RxJava 还**提供了两个扩展方法来实现这一点**，*subscribeOn*和*observeOn*。

*subscribeOn*方法通过指定*Observable*应该在其上运行的不同*调度程序来更改默认行为。*另一方面，observeOn 方法指定了一个不同的调度程序，Observable*可以**使用*它向观察者发送通知。

正如我们之前所讨论的，Spring WebFlux 默认使用 Reactor 作为其响应式库。但由于它与 Reactive Streams API 完全兼容，因此**可以切换到另一个 Reactive Streams 实现，例如 RxJava**（对于 RxJava 1.x 及其 Reactive Streams 适配器）。

我们需要显式添加依赖项：

```xml
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.2.21</version>
</dependency>复制
```

然后我们可以开始在我们的应用程序中使用 RxJava 类型，比如*Observable，*以及 RxJava 特定的*调度*器：

```java
io.reactivex.Observable
  .fromIterable(Arrays.asList("Tom", "Sawyer"))
  .map(s -> s.toUpperCase())
  .observeOn(io.reactivex.schedulers.Schedulers.trampoline())
  .doOnComplete(this::printThreads);复制
```

因此，如果我们运行这个应用程序，除了常规的 Netty 和 JVM 相关线程之外，**我们应该看到一些与我们的 RxJava \*Scheduler\***相关的线程：



[![6](https://www.baeldung.com/wp-content/uploads/2020/08/6.jpg)](https://www.baeldung.com/wp-content/uploads/2020/08/6.jpg)

## 10.结论

在本文中，我们从并发的上下文中探讨了响应式编程的前提。我们观察了传统编程和反应式编程中并发模型的差异。这使我们能够检查 Spring WebFlux 中的并发模型，以及它采用线程模型来实现它。

然后我们结合不同的 HTTP 运行时和反应库探索了 WebFlux 中的线程模型。我们还了解了使用*WebClient*与数据访问库时线程模型有何不同。

最后，我们谈到了在 WebFlux 的反应程序中控制调度策略的选项。
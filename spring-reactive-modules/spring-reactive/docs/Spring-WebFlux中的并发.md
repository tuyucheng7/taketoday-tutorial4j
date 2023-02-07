## 1. 简介

在本教程中，我们将探索使用[Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)编写的响应式程序中的并发性。

我们将从讨论与响应式编程相关的并发性开始。然后我们将了解Spring WebFlux如何在不同的响应式服务器库上提供并发抽象。

## 2. 响应式编程的动机

**典型的Web应用程序由几个复杂的交互部分组成。许多这些交互本质上是阻塞的**，例如涉及数据库调用以获取或更新数据的交互。**然而，其他几个是独立的，可以同时执行**，可能是并行执行。

**例如，两个用户对Web服务器的请求可以由不同的线程处理**。在多核平台上，这在整体响应时间方面具有明显优势。因此，**这种并发模型被称为每个请求一个线程(thread-per-request)模型**：

<img src="../assets/img_2.png">

在上图中，每个线程一次处理一个请求。

虽然基于线程的并发性为我们解决了部分问题，但它无法解决**我们在单线程中的大多数交互仍然处于阻塞状态**的事实。此外，我们用于在Java中实现并发的本机线程在上下文切换方面付出了巨大的代价。

同时，随着Web应用程序面临越来越多的请求，**每个请求一个线程的模型开始达不到预期**。

因此，**我们需要一个并发模型来帮助我们用相对较少的线程数来处理越来越多的请求**。**这是采用[响应式编程](https://www.baeldung.com/java-reactive-systems)的主要动机之一。**

## 3. 响应式编程中的并发

**响应式编程帮助我们根据数据流和通过数据流传播变化来构建程序**。在完全非阻塞的环境中，这可以使我们以更好的资源利用率实现更高的并发。

然而，响应式编程是否完全背离了基于线程的并发性？虽然这是一个强有力的声明，但**响应式编程在使用线程来实现并发方面肯定有一种非常不同的方法**。因此，**响应式编程带来的根本区别是异步性。**

换句话说，程序流从一系列同步操作转换为异步事件流。

例如，在响应式模型下，对数据库的读取调用不会在获取数据时阻塞调用线程。**该调用会立即返回其他人可以订阅的发布者**。订阅者可以在事件发生后对其进行处理，甚至可以自己进一步生成事件：

<img src="../assets/img_3.png">

最重要的是，响应式编程并不强调应该生成和使用哪些线程事件。相反，**重点是将程序构建为异步事件流**。

这里的发布者和订阅者不需要属于同一个线程。**这有助于我们更好地利用可用线程，从而提高整体并发性。**

## 4. 事件循环

**有几种编程模型描述了并发的响应式方法**。

在本节中，我们将研究其中的一些，以了解响应式编程如何以更少的线程实现更高的并发性。

**一种这样的服务器响应式异步编程模型是事件循环模型**：

<img src="../assets/img_4.png">

上面是一个事件循环的抽象设计，展示了响应式异步编程的思想：

-   **事件循环在单个线程中连续运行**，尽管我们可以拥有与可用内核数量一样多的事件循环
-   **事件循环按顺序处理事件队列中的事件**，并在向平台注册回调后立即返回
-   该平台可以触发操作的完成，例如数据库调用或外部服务调用
-   **事件循环可以触发操作完成通知的回调，并将结果返回给原始调用者**

事件循环模型在许多平台中实现，包括[Node.js](https://nodejs.org/en/)、[Netty](https://netty.io/)和[Ngnix](https://www.nginx.com/)。它们提供比[Apache HTTP Server](https://httpd.apache.org/)、[Tomcat](https://www.baeldung.com/tomcat)或[JBoss](https://www.redhat.com/fr/technologies/jboss-middleware/application-platform)等传统平台更好的可扩展性。

## 5. 使用Spring WebFlux进行响应式编程

现在我们对响应式编程及其并发模型有了足够的了解，可以开始在Spring WebFlux中探索这个主题。

**WebFlux是Spring的响应式堆栈Web框架**，在5.0版本中添加。

让我们探索Spring WebFlux的服务器端堆栈，以了解它如何补充Spring中的传统Web堆栈：

<img src="../assets/img_5.png">

正如我们所看到的，**Spring WebFlux与Spring中的传统Web框架并列，并不一定是取代它**。

这里有几个要点需要注意：

-   Spring WebFlux通过函数式路由扩展了传统的基于注解的编程模型
-   此外，它使底层HTTP运行时适应Reactive Streams API，使运行时可互操作
-   它能够支持各种响应式运行时，包括Servlet 3.1+容器，如Tomcat、Reactor、Netty或[Undertow](https://www.baeldung.com/jboss-undertow)
-   最后，它包括WebClient，一个用于HTTP请求的响应式非阻塞客户端，提供函数式和流式的API

## 6. 支持的运行时中的线程模型

正如我们之前所讨论的，**响应式程序倾向于只使用几个线程并充分利用它们**。但是，线程的数量和性质取决于我们选择的实际Reactive Stream API运行时。

澄清一下，**Spring WebFlux可以通过HttpHandler提供的通用API来适应不同的运行时**。这个API是一个简单的契约，只有一个方法，它提供了对不同服务器API的抽象，比如Reactor Netty、Servlet 3.1 API或Undertow API。

让我们来看看在其中一些中实现的线程模型。

**虽然Netty是WebFlux应用程序中的默认服务器，但只需声明正确的依赖项即可切换到任何其他受支持的服务器**：

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
</dependency>
```

虽然可以通过多种方式观察在Java虚拟机中创建的线程，但很容易从Thread类本身中提取它们：

```java
Thread.getAllStackTraces()
    .keySet()
    .stream()
    .collect(Collectors.toList());
```

### 6.1 Reactor Netty

正如我们所说，[Reactor Netty](https://netty.io/)是Spring Boot WebFlux Starter中的默认嵌入式服务器。让我们看看Netty默认创建的线程。首先，我们不会添加任何其他依赖项或使用WebClient。因此，如果我们启动一个使用其Spring Boot启动器创建的Spring WebFlux应用程序，我们可以期望看到它创建的一些默认线程：

<img src="../assets/img_6.png">

请注意，除了服务器的普通线程外，**Netty还生成了一堆用于请求处理的工作线程**。**这些通常是可用的CPU内核**。这是四核计算机上的输出。我们还会看到JVM环境中典型的一堆内务处理线程，但它们在这里并不重要。

**Netty使用事件循环模型以响应式异步方式提供高度可扩展的并发性。**让我们看看Netty如何利用**Java NIO实现事件循环来提供这种可伸缩性**：

<img src="../assets/img_7.png">

在这里，**EventLoopGroup管理着一个或多个EventLoop，它必须是持续运行的**。因此，**不建议创建比可用核心数更多的EventLoop**。

EventLoopGroup进一步为每个新创建的Channel分配一个EventLoop。因此，在Channel的生命周期中，所有操作都由同一个线程执行。

### 6.2 Apache Tomcat

**传统的Servlet容器也支持Spring WebFlux，例如[Apache Tomcat](https://tomcat.apache.org/)**。

**WebFlux依赖于具有非阻塞I/O的Servlet 3.1 API**。虽然它在低级适配器后面使用Servlet API，但不能直接使用Servlet API。

让我们看看在Tomcat上运行的WebFlux应用程序中我们期望什么样的线程：

<img src="../assets/img_8.png">

我们在这里看到的线程数量和类型与我们之前观察到的有很大不同。

首先，**Tomcat以更多的工作线程开始，默认为10个**。当然，我们还会看到JVM和Catalina容器中一些典型的内务处理线程，在本次讨论中我们可以忽略它们。

我们需要了解使用Java NIO的Tomcat的架构，以便将其与我们上面看到的线程相关联。

**Tomcat 5及更高版本在其Connector组件中支持NIO，该组件主要负责接收请求**。

另一个Tomcat组件是Container组件，它负责容器管理功能。

我们在这里感兴趣的是连接器组件为支持NIO而实现的线程模型。它由Acceptor、Poller和Worker组成，作为NioEndpoint模块的一部分：

<img src="../assets/img_9.png">

**Tomcat为Acceptor、Poller和Worker生成一个或多个线程，通常带有一个专用于Worker**的线程池。

虽然对Tomcat体系结构的详细讨论超出了本文的范围，但我们现在应该有足够的洞察力来理解我们之前看到的线程。

## 7. WebClient中的线程模型

[WebClient](https://www.baeldung.com/spring-5-webclient)**是响应式HTTP客户端，它是Spring WebFlux的一部分**。我们可以在需要基于REST的通信时随时使用它，这使我们能够创建端到端响应式应用程序。

正如我们之前看到的，响应式应用程序只使用几个线程，因此应用程序的任何部分都没有余地阻塞线程。因此，WebClient在帮助我们实现WebFlux的潜力方面起着至关重要的作用。

### 7.1 使用WebClient

使用WebClient也非常简单。**我们不需要包含任何特定的依赖项，因为它是Spring WebFlux的一部分**。

让我们创建一个返回[Mono](https://www.baeldung.com/java-string-from-mono)的简单REST端点：

```java
@GetMapping("/index")
public Mono<String> getIndex() {
    return Mono.just("Hello World!");
}
```

然后我们将使用WebClient调用此REST端点并响应式地消费数据：

```java
WebClient.create("http://localhost:8080/index").get()
    .retrieve()
    .bodyToMono(String.class)
    .doOnNext(s -> printThreads());
```

在这里，我们还打印了使用我们之前讨论的方法创建的线程。

### 7.2 理解线程模型

那么，在WebClient的情况下，线程模型是如何工作的呢？

好吧，毫不奇怪，**WebClient也使用事件循环模型实现了并发**。当然，它依赖于底层运行时来提供必要的基础设施。

**如果我们在Reactor Netty上运行WebClient，它会共享Netty用于服务器的事件循环**。因此，在这种情况下，我们可能不会注意到创建的线程有太大差异。

但是，**WebClient在Servlet 3.1+容器(如Jetty)上也受支持，但它的工作方式有所不同**。

如果我们比较在使用和不使用WebClient运行[Jetty](https://www.eclipse.org/jetty/)的WebFlux应用程序上创建的线程，我们会注意到一些额外的线程。

在这里，WebClient必须创建它的事件循环。所以我们可以看到这个事件循环创建的固定数量的处理线程：

<img src="../assets/img_10.png">

**在某些情况下，为客户端和服务器使用单独的线程池可以提供更好的性能**。虽然这不是Netty的默认行为，但如果需要，始终可以为WebClient声明一个专用线程池。

我们将在后面的部分中看到这是如何实现的。

## 8. 数据访问库中的线程模型

正如我们之前看到的，**即使是一个简单的应用程序通常也包含几个需要连接的部分**。

这些部分的典型示例包括数据库和消息代理。**与其中许多连接的现有库仍然处于阻塞状态，但这种情况正在迅速改变**。

**现在有几个数据库提供用于连接的响应式库。其中许多库在[Spring Data](https://www.baeldung.com/spring-data)中可用**，而我们也可以直接使用其他库。

我们对这些库使用的线程模型特别感兴趣。

### 8.1 Spring Data MongoDB

[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)为构建在[MongoDB Reactive Streams驱动程序](https://mongodb.github.io/mongo-java-driver/)之上的MongoDB提供响应式Repository支持。最值得注意的是，**该驱动程序完全实现了Reactive Streams API，以提供具有非阻塞背压的异步流处理**。

在Spring Boot应用程序中为MongoDB的响应式Repository设置支持就像添加依赖项一样简单：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

这将允许我们创建一个Repository，并使用它以非阻塞方式在MongoDB上执行一些基本操作：

```java
public interface PersonRepository extends ReactiveMongoRepository<Person, ObjectId> {
}
.....
personRepository.findAll().doOnComplete(this::printThreads);
```

那么，当我们在Netty服务器上运行这个应用程序时，我们期望看到什么样的线程呢？

好吧，毫不奇怪，我们不会看到太大的区别，因为**Spring Data响应式Repository使用可用于服务器的相同事件循环**。

### 8.2 Reactor Kafka

**Spring仍在构建对响应式Kafka的全面支持**。但是，我们确实有Spring之外可用的选项。

**[Reactor Kafka](https://projectreactor.io/docs/kafka/release/reference/#_introduction)是基于Reactor的Kafka响应式API**。Reactor Kafka支持使用函数式API发布和消费消息，并且还具有非阻塞背压。

首先，我们需要在我们的应用程序中添加所需的依赖项才能开始使用Reactor Kafka：

```xml
<dependency>
    <groupId>io.projectreactor.kafka</groupId>
    <artifactId>reactor-kafka</artifactId>
    <version>1.3.10</version>
</dependency>
```

这应该使我们能够以非阻塞方式向Kafka生成消息：

```java
// producerProps: Map of Standard Kafka Producer Configurations
SenderOptions<Integer, String> senderOptions = SenderOptions.create(producerProps);
KafkaSender<Integer, String> sender =  KafkaSender.create(senderOptions);
Flux<SenderRecord<Integer, String, Integer>> outboundFlux = Flux
    .range(1, 10)
    .map(i -> SenderRecord.create(new ProducerRecord<>("reactive-test", i, "Message_" + i), i));
sender.send(outboundFlux).subscribe();
```

同样，我们也应该能够以非阻塞方式消费来自Kafka的消息：

```java
// consumerProps: Map of Standard Kafka Consumer Configurations
ReceiverOptions<Integer, String> receiverOptions = ReceiverOptions.create(consumerProps);
receiverOptions.subscription(Collections.singleton("reactive-test"));
KafkaReceiver<Integer, String> receiver = KafkaReceiver.create(receiverOptions);
Flux<ReceiverRecord<Integer, String>> inboundFlux = receiver.receive();
inboundFlux.doOnComplete(this::printThreads)
```

这是非常简单和不言自明的。

我们订阅了Kafka中的主题reactive-test，并获得了Flux消息。

**对我们来说有趣的是创建的线程**：

<img src="../assets/img_11.png">

**我们可以看到一些不是典型的Netty服务器的线程**。

这表明Reactor Kafka管理着自己的线程池，其中有少数工作线程专门参与Kafka消息处理。当然，我们会看到一堆其他与Netty和JVM相关的线程，我们可以忽略这些。

**Kafka生产者使用单独的网络线程向代理发送请求**。此外，它们在单线程池调度程序上向应用程序传递响应。

另一方面，Kafka消费者在每个消费者组中都有一个线程阻塞以监听传入的消息。然后传入的消息被安排在不同的线程池上进行处理。

## 9. WebFlux中的调度选项

到目前为止，我们已经看到**响应式编程在只有几个线程的完全非阻塞环境中真正大放异彩**。但这也意味着，如果确实有一个部分在阻塞，则会导致更差的性能。这是因为阻塞操作可以完全冻结事件循环。

那么，**我们如何处理响应式编程中长时间运行的进程或阻塞操作呢**？

老实说，最好的选择就是避开它们。然而，这可能并不总是可行的，**我们可能需要为应用程序的这些部分制定专门的调度策略**。

**Spring WebFlux提供了一种机制，可以在数据流链之间将处理切换到不同的线程池**。这可以为我们提供对某些任务所需的调度策略的精确控制。当然，WebFlux能够基于底层响应式库中可用的线程池抽象(称为调度程序)提供此功能。

### 9.1 Reactor

在[Reactor](https://projectreactor.io/)中，**Scheduler类定义了执行模型，以及执行发生的位置**。

[Schedulers](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html)类提供了许多执行上下文，例如immediate、single、elastic和parallel。它们提供了不同类型的线程池，可用于不同的作业。此外，我们始终可以使用预先存在的[ExecutorService](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html)创建自己的[Scheduler](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Scheduler.html)。

Schedulers为我们提供了多种执行上下文，而**Reactor也为我们提供了不同的执行上下文切换方式**。这些是publishOn和subscribeOn方法。

我们可以在链中的任何位置将publishOn与Scheduler一起使用，该Scheduler会影响所有后续运算符。

虽然我们也可以在链中的任何位置将subscribeOn与Scheduler一起使用，但它只会影响发射源的上下文。

如果我们还记得的话，Netty上的WebClient共享为服务器创建的相同事件循环作为默认行为。然而，我们可能有充分的理由为WebClient创建一个专用的线程池。

让我们看看我们如何在Reactor中实现这一点，Reactor是WebFlux中的默认响应式库：

```java
Scheduler scheduler = Schedulers.newBoundedElastic(5, 10, "MyThreadGroup");

WebClient.create("http://localhost:8080/index").get()
    .retrieve()
    .bodyToMono(String.class)
    .publishOn(scheduler)
    .doOnNext(s -> printThreads());
```

早些时候，我们没有观察到在使用或不使用WebClient的Netty上创建的线程有任何差异。然而，如果我们现在运行上面的代码，**我们将观察到正在创建一些新线程**：

<img src="../assets/img_12.png">

在这里**我们可以看到作为有界弹性线程池**的一部分创建的线程。这是来自WebClient的响应在订阅后发布的地方。

这留下了用于处理服务器请求的主线程池。

### 9.2 RxJava

**RxJava中的默认行为与Reactor中的默认行为没有太大区别**。

Observable和我们在其上应用的运算符链执行它们的工作并在调用订阅的同一线程上通知观察者。此外，[RxJava](https://github.com/ReactiveX/RxJava)与Reactor一样，提供了将前缀或自定义调度策略引入链中的方法。

**RxJava还有一个[Schedulers](http://reactivex.io/RxJava/javadoc/io/reactivex/schedulers/Schedulers.html)类，它为[Observable](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html)链提供了多种执行模型**。这些包括new thread、immediate、trampoline、io、computation和test。当然，它也允许我们从Java [Executor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executor.html)定义一个[Scheduler](http://reactivex.io/documentation/scheduler.html)。

此外，**RxJava还提供了两个扩展方法来实现这一点**，subscribeOn和observeOn。

subscribeOn方法通过指定Observable应该在其上运行的不同调度程序来更改默认行为。另一方面，observeOn方法指定了一个不同的调度程序，Observable可以使用该调度程序向观察者发送通知。

正如我们之前所讨论的，Spring WebFlux默认使用Reactor作为其响应式库。但由于它与Reactive Streams API完全兼容，因此**可以切换到另一个Reactive Streams实现，例如RxJava**(对于RxJava 1.x及其Reactive Streams适配器)。

我们需要显式添加依赖项：

```xml
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.2.21</version>
</dependency>
```

然后我们可以开始在我们的应用程序中使用RxJava类型，比如Observable，以及RxJava特定的调度程序：

```java
io.reactivex.Observable
    .fromIterable(Arrays.asList("Tom", "Sawyer"))
    .map(s -> s.toUpperCase())
    .observeOn(io.reactivex.schedulers.Schedulers.trampoline())
    .doOnComplete(this::printThreads);
```

因此，如果我们运行这个应用程序，除了常规的Netty和JVM相关线程之外，**我们应该看到一些与我们的RxJava Scheduler相关的线程**：

<img src="../assets/img_13.png">

## 10. 总结

在本文中，我们从并发的上下文中探讨了响应式编程的前提。我们观察到传统编程和响应式编程中并发模型的差异。这使我们能够检查Spring WebFlux中的并发模型，以及它对线程模型的实现。

然后我们结合不同的HTTP运行时和响应式库探索了WebFlux中的线程模型。我们还了解了使用WebClient与数据访问库时线程模型的不同之处。

最后，我们谈到了在WebFlux的响应程序中控制调度策略的选项。
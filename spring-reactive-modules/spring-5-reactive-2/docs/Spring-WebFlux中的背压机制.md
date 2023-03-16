## 1. 概述

[Spring WebFlux](https://www.baeldung.com/spring-webflux)为Web应用程序提供响应式编程支持。[响应式设计](https://www.baeldung.com/java-reactive-systems)的异步和非阻塞特性提高了性能和内存使用率。[Project Reactor](https://projectreactor.io/)提供了这些功能来有效地管理数据流。

然而，背压是这类应用程序中的常见问题。在本教程中，我们将解释它是什么以及如何在Spring WebFlux中应用背压机制来缓解它。

## 2. 响应流中的背压

由于响应式编程的非阻塞特性，服务器不会一次发送完整的流。它可以在数据可用时立即并发推送数据。因此，客户端等待接收和处理事件的时间更少。但是，还有一些问题需要克服。

**软件系统中的背压是使流量通信过载的能力**。换句话说，信息的发布者用他们无法处理的数据淹没了消费者。

最终，人们也将这一术语用作控制和处理它的机制。它是系统为控制下游力量而采取的保护措施。

### 2.1 什么是背压？

**在Reactive Streams中，背压也定义了如何调节流元素的传输**。换句话说，控制接收者可以消费多少元素。

让我们用一个例子来清楚地描述它是什么：

+ 该系统包含三个服务：发布者、消费者和图形用户界面(GUI)
+ 发布者每秒向消费者发送10000个事件
+ 消费者处理它们并将结果发送到GUI
+ GUI向用户显示结果
+ 消费者每秒只能处理7500个事件

<img src="../assets/img.png">

**在这种速度下，消费者无法管理事件(背压)**。因此，系统将崩溃，用户将看不到结果。

### 2.2 使用背压来防止系统性故障

这里的建议是应用某种背压策略来防止系统故障。目标是有效地管理收到的额外事件：

+ **控制发送的数据流将是第一个选项**。基本上，发布者需要放慢发送事件的节奏。因此，消费者不会超载。不幸的是，这并不总是可行的，我们需要找到其他可用的选项
+ **缓冲额外的数据量是第二种选择**。使用这种方法，消费者暂时存储剩余的事件，直到可以处理它们。这里的主要缺点是解除绑定导致内存崩溃的缓冲区
+ **丢弃额外的事件，失去对它们的跟踪**。即使这种解决方案也远非理想，使用这种技术系统不会崩溃

<img src="../assets/img_1.png">

### 2.3 控制背压

我们将重点介绍如何控制发布者发出的事件。基本上，可以遵循三种策略：

+ **仅当订阅者请求时才发送新事件**。这是一种在发射器请求下收集元素的拉取策略
+ **限制在客户端接收的事件数**。作为一种有限的推送策略，发布者一次只能向客户端发送最大数量的元素
+ **当消费者无法处理更多事件时取消数据流**。在这种情况下，接收者可以在任何给定时间中止传输，并稍后再次订阅流

<img src="../assets/img_2.png">

## 3. 在Spring WebFlux中处理背压

**[Spring WebFlux](https://www.baeldung.com/spring-webflux)提供了一个异步非阻塞的响应流**。Spring WebFlux中负责背压的是[Project Reactor](https://projectreactor.io/docs/core/release/reference/#reactive.backpressure)。它在内部使用[Flux函数](https://projectreactor.io/docs/core/release/reference/#_on_backpressure_and_ways_to_reshape_requests)来应用机制来控制发射器产生的事件。

WebFlux使用TCP流量控制以字节为单位调节背压。但它不处理消费者可以接收的逻辑元素。让我们看看幕后发生的交互流程：

+ WebFlux框架负责将事件转换为字节，以便通过TCP传输/接收它们
+ 消费者可能会在请求下一个逻辑元素之前启动并长时间运行作业
+ 当接收者正在处理事件时，WebFlux会在不确认的情况下对字节进行排队，因为没有对新事件的需求
+ 由于TCP协议的性质，如果有新事件，发布者将继续将它们发送到网络

<img src="../assets/img_3.png">

总之，上图显示消费者和发布者对逻辑元素的需求可能不同。Spring WebFlux在理想情况下无法管理作为整个系统交互的服务之间的背压。它独立地与消费者一起处理它，然后以相同的方式与发布者处理它。但它没有考虑到两个服务之间的逻辑需求。

因此，**Spring WebFlux不会像我们预期的那样处理背压**。下一节我们就来看看如何在Spring WebFlux中实现背压机制吧！

## 4. 使用Spring WebFlux实现背压机制

我们将使用[Flux实现](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)来处理对接收到的事件的控制。因此，我们将在读取端和写入端公开具有背压支持的请求和响应主体。然后，生产者会放慢速度或停止，直到消费者的容量释放出来。让我们看看怎么做！

### 4.1 依赖

为了实现这些示例，我们只需将[Spring WebFlux Starter](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-webflux/3.0.3)和[Reactor Test](https://central.sonatype.com/artifact/io.projectreactor/reactor-test/3.5.3)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 4.2 Request

**第一个选项是让消费者控制它可以处理的事件**。因此，发布者一直等待，直到接收者请求新事件。总之，客户端订阅Flux，然后根据自己的需求处理事件：

```java
class BackpressureUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackpressureUnitTest.class);

    @Test
    void whenRequestingChunks10_thenMessagesAreReceived() {
        Flux<Integer> request = Flux.range(1, 50);

        request.subscribe(
              integer -> LOGGER.info(String.valueOf(integer)),
              Throwable::printStackTrace,
              () -> LOGGER.debug("All 50 items have been successfully processed!!!"),
              subscription -> {
                  for (int i = 0; i < 5; i++) {
                      LOGGER.info("Requesting the next 10 elements!!!");
                      subscription.request(10);
                  }
              }
        );

        StepVerifier.create(request)
              .expectSubscription()
              .thenRequest(10)
              .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
              .thenRequest(10)
              .expectNext(11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
              .thenRequest(10)
              .expectNext(21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
              .thenRequest(10)
              .expectNext(31, 32, 33, 34, 35, 36, 37, 38, 39, 40)
              .thenRequest(10)
              .expectNext(41, 42, 43, 44, 45, 46, 47, 48, 49, 50)
              .verifyComplete();
    }
}
```

使用这种方法，发射器永远不会压倒接收器。换句话说，客户端在处理它需要的事件的控制之下。

我们将使用StepVerifier测试生产者在背压方面的行为。只有在调用thenRequest(n)时，我们才会期望接下来的n项。

### 4.3 Limit

第二种选择是使用Reactor中的limitRange()运算符，**它允许设置一次预取的元素数**。一个有趣的功能是，**即使订阅者请求处理更多事件，该限制也适用**。发射器将事件分成块，避免消耗超过每个请求的限制：

```java
@Test
void whenLimitRateSet_thenSplitIntoChunks() throws InterruptedException {
    Flux<Integer> limit = Flux.range(1, 25);

    limit.limitRate(10);
    limit.subscribe(
        value -> LOGGER.info(String.valueOf(value)),
        Throwable::printStackTrace,
        () -> LOGGER.info("Finished!!"),
        subscription -> subscription.request(15)
    );

    StepVerifier.create(limit)
        .expectSubscription()
        .thenRequest(15)
        .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .expectNext(11, 12, 13, 14, 15)
        .thenRequest(10)
        .expectNext(16, 17, 18, 19, 20, 21, 22, 23, 24, 25)
        .verifyComplete();
}
```

### 4.4 Cancel

最后，**消费者可以随时取消要接收的事件**。对于这个例子，我们将使用另一种方法。Project Reactor允许实现我们自己的Subscriber或扩展BaseSubscriber。因此，让我们看看接收器如何在任何时候通过覆盖上述类来中止接收新事件：

```java
@Test
void whenCancel_thenSubscriptionFinished() {
    Flux<Integer> cancel = Flux.range(1, 10).log();

    cancel.subscribe(new BaseSubscriber<>() {
        @Override
        protected void hookOnNext(Integer value) {
            request(3);
            LOGGER.info(String.valueOf(value));
            cancel();
        }
    });

    StepVerifier.create(cancel)
        .expectNext(1, 2, 3)
        .thenCancel()
        .verify();
}
```

## 5. 总结

在本教程中，我们展示了响应式编程中的背压是什么以及如何避免它。Spring WebFlux通过Project Reactor支持背压。因此，当发布者用太多事件压倒消费者时，它可以提供可用性、健壮性和稳定性。总之，它可以防止由于高需求而导致的系统性故障。
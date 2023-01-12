## 1. 简介

在本教程中，我们将首先了解[RSocket](http://rsocket.io/)以及它如何启用客户端-服务器通信。

## 2. 什么是RSocket？

RSocket 是一种二进制点对点通信协议，旨在用于分布式应用程序。从这个意义上说，它提供了 HTTP 等其他协议的替代方案。

RSocket 和其他协议之间的全面比较超出了本文的范围。相反，我们将关注 RSocket 的一个关键特性：它的交互模型。

RSocket 提供四种交互模型。 考虑到这一点，我们将通过一个例子来探索每一个。

## 3.Maven依赖

对于我们的示例，RSocket 只需要两个直接依赖项：

```xml
<dependency>
    <groupId>io.rsocket</groupId>
    <artifactId>rsocket-core</artifactId>
    <version>0.11.13</version>
</dependency>
<dependency>
    <groupId>io.rsocket</groupId>
    <artifactId>rsocket-transport-netty</artifactId>
    <version>0.11.13</version>
</dependency>
```

rsocket [-core](https://search.maven.org/search?q=rsocket-core)和[rsocket-transport-netty](https://search.maven.org/search?q=rsocket-transport-netty)依赖项在 Maven Central 上可用。

重要的一点是 RSocket 库经常使用[反应流](https://projectreactor.io/)。本文通篇使用[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)和[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)类，因此对它们有基本的了解会很有帮助。

## 4. 服务器设置

首先，让我们创建服务器类：

```java
public class Server {
    private final Disposable server;

    public Server() {
        this.server = RSocketFactory.receive()
          .acceptor((setupPayload, reactiveSocket) -> Mono.just(new RSocketImpl()))
          .transport(TcpServerTransport.create("localhost", TCP_PORT))
          .start()
          .subscribe();
    }

    public void dispose() {
        this.server.dispose();
    }

    private class RSocketImpl extends AbstractRSocket {}
}
```

这里我们使用RSocketFactory来设置和侦听 TCP 套接字。我们传入自定义RSocketImpl来处理来自客户端的请求。我们将在进行时向RSocketImpl添加方法。

接下来，要启动服务器，我们只需要实例化它：

```java
Server server = new Server();
```

单个服务器实例可以处理多个连接。因此，只需一个服务器实例即可支持我们的所有示例。

完成后，dispose 方法将停止服务器并释放 TCP 端口。

## 4.交互模型

### 4.1. 请求/响应

RSocket 提供了一个请求/响应模型——每个请求都会收到一个响应。

对于这个模型，我们将创建一个简单的服务，将消息返回给客户端。

让我们从向AbstractRSocket 的扩展RSocketImpl添加一个方法开始：

```java
@Override
public Mono<Payload> requestResponse(Payload payload) {
    try {
        return Mono.just(payload); // reflect the payload back to the sender
    } catch (Exception x) {
        return Mono.error(x);
    }
}
```

requestResponse 方法为每个请求返回一个结果，正如我们从Mono<Payload>响应类型中看到的那样。

Payload是包含消息内容和元数据的类。它被所有的交互模型使用。有效载荷的内容是二进制的，但有一些方便的方法支持基于String的内容。

接下来，我们可以创建我们的客户类：

```java
public class ReqResClient {

    private final RSocket socket;

    public ReqResClient() {
        this.socket = RSocketFactory.connect()
          .transport(TcpClientTransport.create("localhost", TCP_PORT))
          .start()
          .block();
    }

    public String callBlocking(String string) {
        return socket
          .requestResponse(DefaultPayload.create(string))
          .map(Payload::getDataUtf8)
          .block();
    }

    public void dispose() {
        this.socket.dispose();
    }
}
```

客户端使用RSocketFactory.connect()方法启动与服务器的套接字连接。我们使用套接字上的requestResponse 方法将有效负载发送到服务器。

我们的有效负载包含传递给客户端的字符串。当Mono <Payload>响应到达时，我们可以使用getDataUtf8() 方法来访问响应的字符串内容。

最后，我们可以运行集成测试来查看请求/响应的运行情况。我们将向服务器发送一个字符串并验证是否返回相同的字符串：

```java
@Test
public void whenSendingAString_thenRevceiveTheSameString() {
    ReqResClient client = new ReqResClient();
    String string = "Hello RSocket";

    assertEquals(string, client.callBlocking(string));

    client.dispose();
}
```

### 4.2. 即发即弃

使用即发即弃模型，客户端将不会收到来自服务器的响应。

在此示例中，客户端将以 50 毫秒的间隔向服务器发送模拟测量值。服务器将发布测量结果。

让我们在RSocketImpl类中向我们的服务器添加一个即发即弃的处理程序：

```java
@Override
public Mono<Void> fireAndForget(Payload payload) {
    try {
        dataPublisher.publish(payload); // forward the payload
        return Mono.empty();
    } catch (Exception x) {
        return Mono.error(x);
    }
}
```

这个处理程序看起来与请求/响应处理程序非常相似。但是，fireAndForget返回Mono<Void>而不是Mono<Payload>。

dataPublisher是org.reactivestreams.Publisher的一个[实例](http://www.reactive-streams.org/reactive-streams-1.0.2-javadoc/org/reactivestreams/Publisher.html?is-external=true)。因此，它使订阅者可以使用有效负载。我们将在请求/流示例中使用它。

接下来，我们将创建即发即弃的客户端：

```java
public class FireNForgetClient {
    private final RSocket socket;
    private final List<Float> data;

    public FireNForgetClient() {
        this.socket = RSocketFactory.connect()
          .transport(TcpClientTransport.create("localhost", TCP_PORT))
          .start()
          .block();
    }

    / Send binary velocity (float) every 50ms /
    public void sendData() {
        data = Collections.unmodifiableList(generateData());
        Flux.interval(Duration.ofMillis(50))
          .take(data.size())
          .map(this::createFloatPayload)
          .flatMap(socket::fireAndForget)
          .blockLast();
    }

    // ... 
}
```

套接字设置与之前完全相同。

sendData ()方法使用Flux流发送多条消息。 对于每条消息，我们调用socket::fireAndForget。

我们需要为每条消息订阅Mono<Void>响应。如果我们忘记订阅，那么socket::fireAndForget将不会执行。

flatMap操作符确保将Void响应传递给订阅者，而 blockLast 操作符充当订阅者。

我们将等到下一节运行即发即弃测试。那时，我们将创建一个请求/流客户端来接收由即发即弃客户端推送的数据。

### 4.3. 请求/流

在请求/流模型中，单个请求可能会收到多个响应。为了看到这一点，我们可以在即发即弃示例的基础上进行构建。为此，让我们请求一个流来检索我们在上一节中发送的测量值。

和以前一样，让我们首先向服务器上的RSocketImpl添加一个新的侦听器：

```java
@Override
public Flux<Payload> requestStream(Payload payload) {
    return Flux.from(dataPublisher);
}
```

requestStream处理 程序返回一个Flux<Payload>流。正如我们在上一节中回忆的那样，fireAndForget处理程序将传入数据发布到dataPublisher。 现在，我们将使用相同 的dataPublisher作为事件源来创建一个Flux流。通过这样做，测量数据将从我们的即发即弃客户端异步流向我们的请求/流客户端。

接下来让我们创建请求/流客户端：

```java
public class ReqStreamClient {

    private final RSocket socket;

    public ReqStreamClient() {
        this.socket = RSocketFactory.connect()
          .transport(TcpClientTransport.create("localhost", TCP_PORT))
          .start()
          .block();
    }

    public Flux<Float> getDataStream() {
        return socket
          .requestStream(DefaultPayload.create(DATA_STREAM_NAME))
          .map(Payload::getData)
          .map(buf -> buf.getFloat())
          .onErrorReturn(null);
    }

    public void dispose() {
        this.socket.dispose();
    }
}
```

我们以与以前的客户端相同的方式连接到服务器。

在getDataStream() 中，我们使用socket.requestStream()从服务器接收 Flux<Payload> 流。从该流中，我们从二进制数据中提取Float值。最后，流返回给调用者，允许调用者订阅它并处理结果。

现在让我们测试一下。我们将验证从即发即弃到请求/流的往返行程。

我们可以断言每个值的接收顺序与发送顺序相同。然后，我们可以断言我们收到了与发送的相同数量的值：

```java
@Test
public void whenSendingStream_thenReceiveTheSameStream() {
    FireNForgetClient fnfClient = new FireNForgetClient(); 
    ReqStreamClient streamClient = new ReqStreamClient();

    List<Float> data = fnfClient.getData();
    List<Float> dataReceived = new ArrayList<>();

    Disposable subscription = streamClient.getDataStream()
      .index()
      .subscribe(
        tuple -> {
            assertEquals("Wrong value", data.get(tuple.getT1().intValue()), tuple.getT2());
            dataReceived.add(tuple.getT2());
        },
        err -> LOG.error(err.getMessage())
      );

    fnfClient.sendData();

    // ... dispose client & subscription

    assertEquals("Wrong data count received", data.size(), dataReceived.size());
}
```

### 4.4. 渠道

通道模型提供双向通信。在此模型中，消息流在两个方向上异步流动。

让我们创建一个简单的游戏模拟来测试它。在这个游戏中，通道的每一侧都将成为玩家。随着游戏的运行，这些玩家会以随机的时间间隔向另一方发送消息。对方将对消息做出反应。

首先，我们将在服务器上创建处理程序。像以前一样，我们添加到RSocketImpl：

```java
@Override
public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
    Flux.from(payloads)
      .subscribe(gameController::processPayload);
    return Flux.from(gameController);
}
```

requestChannel 处理程序具有用于输入和输出的有效负载流。Publisher<Payload> 输入参数是从客户端接收的负载流。当它们到达时，这些有效负载被传递给gameController::processPayload函数。

作为响应，我们将不同的Flux流返回给客户端。这个流是从我们的gameController创建的，它也是一个Publisher。

以下是GameController类的摘要：

```java
public class GameController implements Publisher<Payload> {
    
    @Override
    public void subscribe(Subscriber<? super Payload> subscriber) {
        // send Payload messages to the subscriber at random intervals
    }

    public void processPayload(Payload payload) {
        // react to messages from the other player
    }
}
```

当GameController收到订阅者时，它开始向该订阅者发送消息。

接下来，让我们创建客户端：

```java
public class ChannelClient {

    private final RSocket socket;
    private final GameController gameController;

    public ChannelClient() {
        this.socket = RSocketFactory.connect()
          .transport(TcpClientTransport.create("localhost", TCP_PORT))
          .start()
          .block();

        this.gameController = new GameController("Client Player");
    }

    public void playGame() {
        socket.requestChannel(Flux.from(gameController))
          .doOnNext(gameController::processPayload)
          .blockLast();
    }

    public void dispose() {
        this.socket.dispose();
    }
}
```

正如我们在前面的示例中看到的那样，客户端连接到服务器的方式与其他客户端相同。

客户端创建自己的GameController实例。

我们使用socket.requestChannel() 将我们的Payload流发送到服务器。服务器用自己的有效负载流进行响应。

作为从服务器接收到的有效负载，我们将它们传递给我们的gameController::processPayload处理程序。

在我们的游戏模拟中，客户端和服务器互为镜像。也就是说， 每一方都发送一个有效载荷流并从另一端接收一个有效载荷流。

流独立运行，没有同步。

最后，让我们在测试中运行模拟：

```java
@Test
public void whenRunningChannelGame_thenLogTheResults() {
    ChannelClient client = new ChannelClient();
    client.playGame();
    client.dispose();
}
```

## 5.总结

在这篇介绍性文章中，我们探讨了 RSocket 提供的交互模型。示例的完整源代码可以在我们的[Github 存储库](https://github.com/eugenp/tutorials/tree/master/rsocket)中找到。

请务必查看[RSocket 网站](http://rsocket.io/)以进行更深入的讨论。特别是，[FAQ](https://rsocket.io/about/faq)和[Motivations](https://rsocket.io/about/motivations)文档提供了很好的背景。
## 1. 概述

在本教程中，我们将使用[NAT 的Java客户端](https://github.com/nats-io/java-nats)连接到[NATS 服务器](https://docs.nats.io/)并发布和接收消息。

NATS 提供三种主要的消息交换模式。发布/订阅语义将消息传递给主题的所有订阅者。请求/回复消息传递通过主题发送请求并将响应路由回请求者。

订阅者在订阅主题时也可以加入消息队列组。发送到关联主题的消息仅传递给队列组中的一个订阅者。

## 2.设置

### 2.1. Maven 依赖

首先，我们需要将 NATS 库添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>io.nats</groupId>
    <artifactId>jnats</artifactId>
    <version>1.0</version>
</dependency>
```

最新版本的库[可以在这里找到](https://search.maven.org/classic/#search|ga|1|a%3A"jnats")，Github 项目在[这里](https://github.com/nats-io/java-nats)。

### 2.2. 服务端

其次，我们需要一个 NATS 服务器来交换消息。这里有适用于所有主要平台的说明[。](https://docs.nats.io/nats-server/installation)

我们假设有一个服务器在 localhost:4222 上运行。

## 3.连接和交换消息

### 3.1. 连接到 NATS

静态 NATS 类中的connect()方法创建Connections。

如果我们想使用具有默认选项的连接并在端口 4222 上侦听本地主机，我们可以使用默认方法：

```java
Connection natsConnection = Nats.connect();

```

但是Connections有许多可配置的选项，我们想覆盖其中的一些。

我们将创建一个Options对象并将其传递给Nats：

```java
private Connection initConnection() {
    Options options = new Options.Builder()
      .errorCb(ex -> log.error("Connection Exception: ", ex))
      .disconnectedCb(event -> log.error("Channel disconnected: {}", event.getConnection()))
      .reconnectedCb(event -> log.error("Reconnected to server: {}", event.getConnection()))
      .build();

    return Nats.connect(uri, options);
}
```

NATS连接经久耐用。API 将尝试重新连接丢失的连接。

我们已经安装了回调来通知我们何时发生断开连接以及何时恢复连接。在此示例中，我们使用的是 lambda，但对于需要做的不仅仅是记录事件的应用程序，我们可以安装实现所需接口的对象。

我们可以进行快速测试。创建连接并添加 sleep 60 秒以保持进程运行：

```java
Connection natsConnection = initConnection();
Thread.sleep(60000);

```

运行这个。然后停止并启动你的 NATS 服务器：

```plaintext
[jnats-callbacks] ERROR com.baeldung.nats.NatsClient 
  - Channel disconnected: io.nats.client.ConnectionImpl@79428dc1
[reconnect] WARN io.nats.client.ConnectionImpl 
  - couldn't connect to nats://localhost:4222 (nats: connection read error)
[jnats-callbacks] ERROR com.baeldung.nats.NatsClient 
  - Reconnected to server: io.nats.client.ConnectionImpl@79428dc1
```

我们可以看到回调记录断开连接并重新连接。

### 3.2. 订阅消息

现在我们有了连接，我们可以进行消息处理了。

NATS消息是bytes[]数组的容器。除了预期的setData(byte[])和byte[] getData()方法之外，还有设置和获取消息目的地以及回复主题的方法。

我们订阅主题，即字符串。

NATS 支持同步和异步订阅。

让我们看一下异步订阅：

```java
AsyncSubscription subscription = natsConnection
  .subscribe( topic, msg -> log.info("Received message on {}", msg.getSubject()));

```

API在其线程中将消息传递给我们的MessageHandler() 。

一些应用程序可能想要控制处理消息的线程：

```java
SyncSubscription subscription = natsConnection.subscribeSync("foo.bar");
Message message = subscription.nextMessage(1000);

```

SyncSubscription有一个阻塞的nextMessage()方法，它将阻塞指定的毫秒数。我们将为我们的测试使用同步订阅以保持测试用例简单。

AsyncSubscription和SyncSubscription都有一个unsubscribe()方法，我们可以用它来关闭订阅。

```java
subscription.unsubscribe();

```

### 3.3. 发布消息

可以通过多种方式发布消息。

最简单的方法只需要一个主题字符串和消息字节：

```java
natsConnection.publish("foo.bar", "Hi there!".getBytes());

```

如果发布者希望回复或提供有关消息来源的特定信息，它也可以发送一条带有回复主题的消息：

```java
natsConnection.publish("foo.bar", "bar.foo", "Hi there!".getBytes());

```

还有一些其他组合的重载，例如传递Message而不是bytes。

### 3.4. 一个简单的消息交换

给定一个有效的Connection，我们可以编写一个验证消息交换的测试：

```java
SyncSubscription fooSubscription = natsConnection.subscribe("foo.bar");
SyncSubscription barSubscription = natsConnection.subscribe("bar.foo");
natsConnection.publish("foo.bar", "bar.foo", "hello there".getBytes());

Message message = fooSubscription.nextMessage();
assertNotNull("No message!", message);
assertEquals("hello there", new String(message.getData()));

natsConnection
  .publish(message.getReplyTo(), message.getSubject(), "hello back".getBytes());

message = barSubscription.nextMessage();
assertNotNull("No message!", message);
assertEquals("hello back", new String(message.getData()));

```

我们首先使用同步订阅订阅两个主题，因为它们在 JUnit 测试中工作得更好。然后我们向其中一个发送消息，将另一个指定为replyTo地址。

阅读来自第一个目的地的消息后，我们“翻转”主题以发送回复。

### 3.5. 通配符订阅

NATS 服务器支持主题通配符。

通配符对以“.”分隔的主题标记进行操作。特点。星号字符“”匹配单个标记。大于号“>”是一个主题剩余部分的通配符匹配，它可能不止一个标记。

例如：

-   foo. 匹配 foo.bar、foo.requests，但不匹配foo.bar.requests
-   foo.> 匹配 foo.bar、foo.requests、foo.bar.requests、foo.bar.baeldung 等。

让我们尝试一些测试：

```java
SyncSubscription fooSubscription = client.subscribeSync("foo.");

client.publishMessage("foo.bar", "bar.foo", "hello there");

Message message = fooSubscription.nextMessage(200);
assertNotNull("No message!", message);
assertEquals("hello there", new String(message.getData()));

client.publishMessage("foo.bar.plop", "bar.foo", "hello there");
message = fooSubscription.nextMessage(200);
assertNull("Got message!", message);

SyncSubscription barSubscription = client.subscribeSync("foo.>");

client.publishMessage("foo.bar.plop", "bar.foo", "hello there");

message = barSubscription.nextMessage(200);
assertNotNull("No message!", message);
assertEquals("hello there", new String(message.getData()));
```

## 4.请求/回复消息

我们的消息交换测试类似于发布/订阅消息系统上的常见习惯用法；请求/回复。NATS 明确支持这种请求/回复消息传递。

发布者可以使用我们上面使用的异步订阅方法为请求安装一个处理程序：

```java
AsyncSubscription subscription = natsConnection
  .subscribe("foo.bar.requests", new MessageHandler() {
    @Override
    public void onMessage(Message msg) {
        natsConnection.publish(message.getReplyTo(), reply.getBytes());
    }
});

```

或者他们可以在请求到达时对其做出响应。

API 提供了一个request()方法：

```java
Message reply = natsConnection.request("foo.bar.requests", request.getBytes(), 100);

```

该方法为回复创建一个临时邮箱，并为我们写入回复地址。

Request()返回响应，如果请求超时则返回null 。最后一个参数是等待的毫秒数。

我们可以修改请求/回复的测试：

```java
natsConnection.subscribe(salary.requests", message -> {
    natsConnection.publish(message.getReplyTo(), "denied!".getBytes());
});
Message reply = natsConnection.request("salary.requests", "I need a raise.", 100);
assertNotNull("No message!", reply);
assertEquals("denied!", new String(reply.getData()));

```

## 5. 消息队列

订阅者可以在订阅时指定队列组。当一条消息发布到组时，NATS 会将它传递给一个且唯一的订阅者。

队列组不会持久化消息。如果没有可用的侦听器，则丢弃该消息。

### 5.1. 订阅队列

订阅者将队列组名称指定为字符串：

```java
SyncSubscription subscription = natsConnection.subscribe("topic", "queue name");
```

当然还有一个异步版本：

```java
SyncSubscription subscription = natsConnection
  .subscribe("topic", "queue name", new MessageHandler() {
    @Override
    public void onMessage(Message msg) {
        log.info("Received message on {}", msg.getSubject());
    }
});

```

订阅在 NATS 服务器上创建队列。

### 5.2. 发布到队列

将消息发布到队列组只需要发布到相关主题：

```java
natsConnection.publish("foo",  "queue message".getBytes());
```

NATS 服务器会将消息路由到队列并选择一个消息接收者。

我们可以通过测试来验证这一点：

```java
SyncSubscription queue1 = natsConnection.subscribe("foo", "queue name");
SyncSubscription queue2 = natsConnection.subscribe("foo", "queue name");

natsConnection.publish("foo", "foobar".getBytes());

List<Message> messages = new ArrayList<>();

Message message = queue1.nextMessage(200);
if (message != null) messages.add(message);

message = queue2.nextMessage(200);
if (message != null) messages.add(message);

assertEquals(1, messages.size());
```

我们只收到一条消息。

如果我们将前两行更改为普通订阅：

```java
SyncSubscription queue1 = natsConnection.subscribe("foo");
SyncSubscription queue2 = natsConnection.subscribe("foo");

```

测试失败，因为消息已发送给两个订阅者。

## 六. 总结

在这个简短的介绍中，我们连接到 NATS 服务器并发送了发布/订阅消息和负载均衡队列消息。我们研究了 NATS 对通配符订阅的支持。我们还使用了请求/回复消息。
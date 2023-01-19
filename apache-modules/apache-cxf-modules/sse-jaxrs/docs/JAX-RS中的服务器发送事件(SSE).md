## 1. 概述

服务器发送事件 (SSE) 是一种基于 HTTP 的规范，它提供了一种建立从服务器到客户端的长期运行的单通道连接的方法。 

客户端使用Accept标头 中的媒体类型text/event-stream发起 SSE 连接。

稍后，它会在不请求服务器的情况下自动更新。

[我们可以在官方规范](https://html.spec.whatwg.org/multipage/server-sent-events.html)上查看有关规范的更多详细信息。

在本教程中，我们将介绍 SSE 的新 JAX-RS 2.1 实现。

因此，我们将研究如何使用 JAX-RS 服务器 API 发布事件。此外，我们还将探讨如何通过 JAX-RS 客户端 API 或仅通过 HTTP 客户端(如curl工具)使用它们。

## 2. 了解上交所事件

SSE 事件是由以下字段组成的文本块：

-   事件： 事件的类型。服务器可以发送许多不同类型的消息，而客户端可能只监听特定类型或者可以对每种事件类型进行不同的处理
-   数据： 服务器发送的消息。我们可以为同一事件设置多条数据线
-   Id：事件的 ID，用于在连接重试后 发送Last-Event-ID标头。它很有用，因为它可以防止服务器发送已发送的事件
-   Retry： 当前丢失时客户端重新建立连接的时间，单位毫秒。最后收到的 Id 将通过 Last-Event-ID 标头自动发送
-   ' : ': 这是一条注解，被客户端忽略

此外，两个连续事件由双换行符“ nn ”分隔。

此外，同一事件中的数据可以写成多行，如下例所示：

```bash
event: stock
id: 1
: price change
retry: 4000
data: {"dateTime":"2018-07-14T18:06:00.285","id":1,
data: "name":"GOOG","price":75.7119}

event: stock
id: 2
: price change
retry: 4000
data: {"dateTime":"2018-07-14T18:06:00.285","id":2,"name":"IBM","price":83.4611}
```

在JAX RS中，SSE 事件由SseEvent 接口抽象， 或者更准确地说，由两个子接口 OutboundSseEvent和 InboundSseEvent 抽象。

OutboundSseEvent用于 Server API 并设计发送事件，而 InboundSseEvent 用于 Client API 并抽象接收事件。

## 3. 发布上交所事件

现在我们讨论了什么是 SSE 事件，让我们看看如何构建并将其发送到 HTTP 客户端。

### 3.1. 项目设置

我们已经有了 关于设置基于 JAX RS 的 Maven 项目[的教程。](https://www.baeldung.com/jax-rs-spec-and-implementations)随意看看那里，了解如何设置依赖项并开始使用 JAX RS。

### 3.2. 上证资源法

SSE 资源方法是一种 JAX RS 方法，它：

-   可以产生文本/事件流媒体类型
-   有一个注入的SseEventSink 参数，发送事件的地方
-   也可能有一个注入的Sse 参数，用作创建事件生成器的入口点

```java
@GET
@Path("prices")
@Produces("text/event-stream")
public void getStockPrices(@Context SseEventSink sseEventSink, @Context Sse sse) {
    //...
}
```

因此，客户端应该使用以下 HTTP 标头发出第一个 HTTP 请求：

```javascript
Accept: text/event-stream

```

### 3.3. 上交所实例

SSE 实例是 JAX RS 运行时可用于注入的上下文 bean。

我们可以将其用作工厂来创建：

-   OutboundSseEvent.Builder – 允许我们创建事件
-   SseBroadcaster – 允许我们向多个订阅者广播事件

让我们看看它是如何工作的：

```java
@Context
public void setSse(Sse sse) {
    this.sse = sse;
    this.eventBuilder = sse.newEventBuilder();
    this.sseBroadcaster = sse.newBroadcaster();
}
```

现在，让我们关注事件生成器。 OutboundSseEvent.Builder负责创建OutboundSseEvent：

```java
OutboundSseEvent sseEvent = this.eventBuilder
  .name("stock")
  .id(String.valueOf(lastEventId))
  .mediaType(MediaType.APPLICATION_JSON_TYPE)
  .data(Stock.class, stock)
  .reconnectDelay(4000)
  .comment("price change")
  .build();
```

正如我们所见，构建器具有为上面显示的所有事件字段设置值的方法。此外，mediaType() 方法用于将数据字段Java对象序列化为合适的文本格式。

默认情况下，数据字段的媒体类型是text/plain。因此，在处理String数据类型时不需要明确指定。

否则，如果我们想要处理自定义对象，我们需要指定媒体类型或提供自定义MessageBodyWriter。 JAX RS 运行时为 最知名的媒体类型提供MessageBodyWriters。

Sse 实例还有两个构建器快捷方式，用于创建仅包含数据字段或类型和数据字段的事件：

```java
OutboundSseEvent sseEvent = sse.newEvent("cool Event");
OutboundSseEvent sseEvent = sse.newEvent("typed event", "data Event");
```

### 3.4. 发送简单事件

现在我们知道如何构建事件并且我们了解 SSE 资源的工作原理。让我们发送一个简单的事件。

SseEventSink 接口 抽象了单个 HTTP 连接。JAX-RS Runtime 只能通过注入 SSE 资源方法使其可用。

发送事件就像调用 SseEventSink 一样简单。发送()。 

在下一个示例中将发送一堆股票更新并最终关闭事件流：

```java
@GET
@Path("prices")
@Produces("text/event-stream")
public void getStockPrices(@Context SseEventSink sseEventSink /../) {
    int lastEventId = //..;
    while (running) {
        Stock stock = stockService.getNextTransaction(lastEventId);
        if (stock != null) {
            OutboundSseEvent sseEvent = this.eventBuilder
              .name("stock")
              .id(String.valueOf(lastEventId))
              .mediaType(MediaType.APPLICATION_JSON_TYPE)
              .data(Stock.class, stock)
              .reconnectDelay(3000)
              .comment("price change")
              .build();
            sseEventSink.send(sseEvent);
            lastEventId++;
        }
     //..
    }
    sseEventSink.close();
}
```

发送所有事件后，服务器通过显式调用close()方法或最好使用try-with-resource 关闭连接， 因为SseEventSink扩展了AutoClosable接口：

```java
try (SseEventSink sink = sseEventSink) {
    OutboundSseEvent sseEvent = //..
    sink.send(sseEvent);
}
```

在我们的示例应用程序中，如果我们访问：

```bash
http://localhost:9080/sse-jaxrs-server/sse.html
```

### 3.5. 广播活动

广播是将事件同时发送给多个客户端的过程。这是由SseBroadcaster API 完成的，只需三个简单的步骤即可完成：

首先，我们从注入的 Sse 上下文创建 SseBroadcaster对象，如前所示：

```java
SseBroadcaster sseBroadcaster = sse.newBroadcaster();
```

然后，客户应该订阅才能接收 SSE 事件。这通常在 SSE 资源方法中完成，其中注入了SseEventSink上下文实例：

```java
@GET
@Path("subscribe")
@Produces(MediaType.SERVER_SENT_EVENTS)
public void listen(@Context SseEventSink sseEventSink) {
    this.sseBroadcaster.register(sseEventSink);
}
```

最后，我们可以通过调用broadcast()方法来触发事件发布 ：

```java
@GET
@Path("publish")
public void broadcast() {
    OutboundSseEvent sseEvent = //...;
    this.sseBroadcaster.broadcast(sseEvent);
}
```

这将向每个已注册的SseEventSink 发送相同的事件。

为了展示广播，我们可以访问这个 URL：

```bash
http://localhost:9080/sse-jaxrs-server/sse-broadcast.html
```

然后我们可以通过调用 broadcast() 资源方法来触发广播：

```bash
curl -X GET http://localhost:9080/sse-jaxrs-server/sse/stock/publish
```

## 4.消费SSE事件

要使用服务器发送的 SSE 事件，我们可以使用任何 HTTP 客户端，但对于本教程，我们将使用 JAX RS 客户端 API。

### 4.1. SSE 的 JAX RS 客户端 API

要开始使用 SSE 的客户端 API，我们需要为 JAX RS 客户端实现提供依赖项。

在这里，我们将使用 Apache CXF 客户端实现：

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-rs-client</artifactId>
    <version>${cxf-version}</version>
</dependency>
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-rs-sse</artifactId>
    <version>${cxf-version}</version>
</dependency>
```

SseEventSource是此 API 的核心，它是从 WebTarget 构建的。

我们首先侦听由InboundSseEvent接口抽象的传入事件：

```java
Client client = ClientBuilder.newClient();
WebTarget target = client.target(url);
try (SseEventSource source = SseEventSource.target(target).build()) {
    source.register((inboundSseEvent) -> System.out.println(inboundSseEvent));
    source.open();
}
```

建立连接后，将为每个接收到的InboundSseEvent调用已注册的事件使用者。

然后我们可以使用 readData()方法读取原始数据字符串：

```java
String data = inboundSseEvent.readData();
```

或者我们可以使用重载版本使用合适的媒体类型获取反序列化的Java对象：

```java
Stock stock = inboundSseEvent.readData(Stock.class, MediaType.Application_Json);
```

在这里，我们只是提供了一个简单的事件消费者，它在控制台中打印传入的事件。

## 5.总结

在本教程中，我们重点介绍了如何在 JAX RS 2.1 中使用服务器发送的事件。我们提供了一个示例来展示如何将事件发送到单个客户端以及如何将事件广播到多个客户端。

最后，我们使用 JAX-RS 客户端 API 使用这些事件。
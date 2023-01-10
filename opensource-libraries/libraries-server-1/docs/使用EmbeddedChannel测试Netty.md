## 1. 简介

在本文中，我们将了解如何使用EmbeddedChannel 来测试入站和出站通道处理程序的功能。

[Netty](https://www.baeldung.com/netty) 是一个非常通用的框架，用于编写高性能异步应用程序。如果没有合适的工具，对此类应用程序进行单元测试可能会很棘手。

值得庆幸的是，该框架为我们提供了 EmbeddedChannel 类——它有助于测试 ChannelHandlers。

## 2.设置

EmbeddedChannel是 Netty 框架的一部分，所以唯一需要的依赖是 Netty 本身。

可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"io.netty" AND a%3A"netty-all")上找到依赖项：

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.24.Final</version>
</dependency>
```

## 3. EmbeddedChannel概述

EmbeddedChannel类只是AbstractChannel 的 另一种实现——无需真正的网络连接即可传输数据。

这很有用，因为我们可以通过在入站通道上写入数据来模拟传入消息，还可以检查出站通道上生成的响应。这样我们就可以单独测试每个ChannelHandler 或在整个通道管道中。

要测试一个或多个ChannelHandlers， 我们首先必须使用其构造函数之一创建一个EmbeddedChannel 实例。

初始化EmbeddedChannel 最常见的方法是将ChannelHandlers 列表传递给它的构造函数：

```java
EmbeddedChannel channel = new EmbeddedChannel(
  new HttpMessageHandler(), new CalculatorOperationHandler());
```

如果我们想更好地控制处理程序插入管道的顺序，我们可以使用默认构造函数创建一个EmbeddedChannel 并直接添加处理程序：

```java
channel.pipeline()
  .addFirst(new HttpMessageHandler())
  .addLast(new CalculatorOperationHandler());
```

此外，当我们创建一个EmbeddedChannel 时， 它将具有 DefaultChannelConfig类提供的默认配置。

当我们想要使用自定义配置时，比如降低默认的连接超时值，我们可以使用config()方法访问ChannelConfig 对象：

```java
DefaultChannelConfig channelConfig = (DefaultChannelConfig) channel
  .config();
channelConfig.setConnectTimeoutMillis(500);
```

EmbeddedChannel 包括我们可以用来读取数据和向 ChannelPipeline写入数据的方法。最常用的方法是：

-   读入站()
-   读出站()
-   writeInbound(对象...消息)
-   writeOutbound(对象...消息)

读取方法检索并删除入站/出站队列中的第一个元素。当我们需要访问整个消息队列而不删除任何元素时，我们可以使用 outboundMessages() 方法：

```java
Object lastOutboundMessage = channel.readOutbound();
Queue<Object> allOutboundMessages = channel.outboundMessages();
```

当消息成功添加到 Channel 的入站/出站管道时，write 方法返回true ：

```java
channel.writeInbound(httpRequest)
```

这个想法是我们在入站管道上写入消息，以便输出ChannelHandlers处理它们，我们希望结果可以从出站管道读取。

## 4. 测试ChannelHandlers

让我们看一个简单的例子，我们要测试一个由两个 ChannelHandler 组成的管道，它们接收 HTTP 请求并期望包含计算结果的 HTTP 响应：

```java
EmbeddedChannel channel = new EmbeddedChannel(
  new HttpMessageHandler(), new CalculatorOperationHandler());
```

第一个 HttpMessageHandler 将从 HTTP 请求中提取数据并将其传递给管道中的 第二个ChannelHandler CalculatorOperationHandler以对数据进行处理。

现在，让我们编写 HTTP 请求并查看入站管道是否处理它：

```java
FullHttpRequest httpRequest = new DefaultFullHttpRequest(
  HttpVersion.HTTP_1_1, HttpMethod.GET, "/calculate?a=10&b=5");
httpRequest.headers().add("Operator", "Add");

assertThat(channel.writeInbound(httpRequest)).isTrue();
long inboundChannelResponse = channel.readInbound();
assertThat(inboundChannelResponse).isEqualTo(15);
```

我们可以看到我们已经使用 writeInbound() 方法在入站管道上发送了 HTTP 请求，并使用 readInbound ( )读取了结果 ；inboundChannelResponse 是我们发送的数据在被入站管道中的所有ChannelHandler 处理后产生的消息。

现在，让我们检查我们的 Netty 服务器是否使用正确的 HTTP 响应消息进行响应。为此，我们将检查出站管道中是否存在消息：

```java
assertThat(channel.outboundMessages().size()).isEqualTo(1);
```

在这种情况下，出站消息是一个 HTTP 响应，所以让我们检查一下内容是否正确。我们通过读取出站管道中的最后一条消息来做到这一点：

```java
FullHttpResponse httpResponse = channel.readOutbound();
String httpResponseContent = httpResponse.content()
  .toString(Charset.defaultCharset());
assertThat(httpResponseContent).isEqualTo("15");
```

## 4. 测试异常处理

另一个常见的测试场景是异常处理。

我们可以通过实现 exceptionCaught() 方法来处理ChannelInboundHandlers 中的异常，但在某些情况下，我们不想处理异常，而是将其传递给管道中的下一个ChannelHandler。

我们可以使用 EmbeddedChannel类中的checkException() 方法 来检查是否在管道上接收到任何 Throwable 对象并重新抛出它。

这样我们就可以捕获异常 并检查ChannelHandler是否应该抛出它：

```java
assertThatThrownBy(() -> {
    channel.pipeline().fireChannelRead(wrongHttpRequest);
    channel.checkException();
}).isInstanceOf(UnsupportedOperationException.class)
  .hasMessage("HTTP method not supported");
```

我们可以在上面的示例中看到，我们已经发送了一个我们希望触发Exception的 HTTP 请求。通过使用checkException() 方法，我们可以重新抛出管道中存在的最后一个异常，因此我们可以断言它需要什么。

## 5.总结

EmbeddedChannel 是 Netty 框架提供的一个很棒的特性，可以帮助我们测试输出ChannelHandler管道 的正确性。它可用于单独测试每个ChannelHandler ，更重要的是整个管道。
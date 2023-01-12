## 1. 概述

[Netty](https://netty.io/)是一个基于 NIO 的客户端-服务器框架，它赋予Java开发人员在网络层上操作的能力。使用此框架，开发人员可以构建自己的任何已知协议甚至自定义协议的实现。

对于框架的基本了解，[介绍 Netty](https://www.baeldung.com/netty)是一个好的开始。

在本教程中，我们将了解如何在 Netty 中实现 HTTP/2 服务器和客户端。

## 2. 什么是HTTP/2？

顾名思义，[HTTP 版本 2 或简称 HTTP/2](https://httpwg.org/specs/rfc7540.html)是超文本传输协议的较新版本。

大约在 1989 年，即互联网诞生之时，HTTP/1.0 应运而生。1997年升级到1.1版。然而，直到 2015 年，它才看到重大升级版本 2。

在撰写本文时，[HTTP/3](https://blog.cloudflare.com/http3-the-past-present-and-future/)也可用，但并非所有浏览器都默认支持。

HTTP/2 仍然是被广泛接受和实施的协议的最新版本。它在多路复用和服务器推送功能等方面与以前的版本有很大不同。

HTTP/2 中的通信通过一组称为帧的字节进行，多个帧形成一个流。

在我们的代码示例中，我们将看到 Netty 如何处理[HEADERS](https://tools.ietf.org/html/rfc7540#section-6.2)、[DATA](https://tools.ietf.org/html/rfc7540#section-6.1)和[SETTINGS](https://tools.ietf.org/html/rfc7540#section-6.5)帧的交换。

## 3.服务器

现在让我们看看如何在 Netty 中创建 HTTP/2 服务器。

### 3.1. SSL上下文

Netty 支持[基于 TLS 的 HTTP/2 的 APN 协商](https://tools.ietf.org/html/rfc7301)。因此，我们需要创建服务器的第一件事是[SslContext](https://netty.io/4.1/api/io/netty/handler/ssl/SslContext.html)：

```java
SelfSignedCertificate ssc = new SelfSignedCertificate();
SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
  .sslProvider(SslProvider.JDK)
  .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
  .applicationProtocolConfig(
    new ApplicationProtocolConfig(Protocol.ALPN, SelectorFailureBehavior.NO_ADVERTISE,
      SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2))
  .build();
```

在这里，我们使用 JDK SSL 提供程序为服务器创建了一个上下文，添加了一些密码，并为 HTTP/2 配置了应用层协议协商。

这意味着我们的服务器将仅支持 HTTP/2 及其底层[协议标识符 h2](https://httpwg.org/specs/rfc7540.html#versioning)。

### 3.2. 使用ChannelInitializer引导服务器

接下来，我们需要一个用于多路复用子通道的ChannelInitializer，以便设置 Netty 管道。

我们将在此通道中使用较早的sslContext来启动管道，然后引导服务器：

```java
public final class Http2Server {

    static final int PORT = 8443;

    public static void main(String[] args) throws Exception {
        SslContext sslCtx = // create sslContext as described above
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(group)
              .channel(NioServerSocketChannel.class)
              .handler(new LoggingHandler(LogLevel.INFO))
              .childHandler(new ChannelInitializer() {
                  @Override
                  protected void initChannel(SocketChannel ch) throws Exception {
                      if (sslCtx != null) {
                          ch.pipeline()
                            .addLast(sslCtx.newHandler(ch.alloc()), Http2Util.getServerAPNHandler());
                      }
                  }
            });
            Channel ch = b.bind(PORT).sync().channel();

            logger.info("HTTP/2 Server is listening on https://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
```

作为此通道初始化的一部分，我们在我们自己的实用程序类Http2Util中定义的实用程序方法getServerAPNHandler()中将 APN 处理程序添加到管道：

```java
public static ApplicationProtocolNegotiationHandler getServerAPNHandler() {
    ApplicationProtocolNegotiationHandler serverAPNHandler = 
      new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {
        
        @Override
        protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
            if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                ctx.pipeline().addLast(
                  Http2FrameCodecBuilder.forServer().build(), new Http2ServerResponseHandler());
                return;
            }
            throw new IllegalStateException("Protocol: " + protocol + " not supported");
        }
    };
    return serverAPNHandler;
}
```

反过来，该处理程序使用其构建器和名为 Http2ServerResponseHandler 的自定义处理程序添加Netty 提供的Http2FrameCodec。

我们的自定义处理程序扩展了 Netty 的ChannelDuplexHandler并充当服务器的入站和出站处理程序。首先，它准备要发送给客户端的响应。

出于本教程的目的，我们将在io.netty.buffer.ByteBuf中定义一个静态的Hello World响应——在 Netty 中读写字节的首选对象：

```java
static final ByteBuf RESPONSE_BYTES = Unpooled.unreleasableBuffer(
  Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));
```

这个缓冲区将在我们的处理程序的channelRead方法中设置为数据帧并写入ChannelHandlerContext：

```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof Http2HeadersFrame) {
        Http2HeadersFrame msgHeader = (Http2HeadersFrame) msg;
        if (msgHeader.isEndStream()) {
            ByteBuf content = ctx.alloc().buffer();
            content.writeBytes(RESPONSE_BYTES.duplicate());

            Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
            ctx.write(new DefaultHttp2HeadersFrame(headers).stream(msgHeader.stream()));
            ctx.write(new DefaultHttp2DataFrame(content, true).stream(msgHeader.stream()));
        }
    } else {
        super.channelRead(ctx, msg);
    }
}

```

就是这样，我们的服务器已准备好发送Hello World。

要进行快速测试，请启动服务器并使用–http2选项触发 curl 命令：

```bash
curl -k -v --http2 https://127.0.0.1:8443
```

这将给出类似于以下的响应：

```plaintext
> GET / HTTP/2
> Host: 127.0.0.1:8443
> User-Agent: curl/7.64.1
> Accept: /
> 
 Connection state changed (MAX_CONCURRENT_STREAMS == 4294967295)!
< HTTP/2 200 
< 
 Connection #0 to host 127.0.0.1 left intact
Hello World Closing connection 0
```

## 4.客户

接下来，让我们看看客户端。当然，它的目的是发送一个请求，然后处理从服务器得到的响应。

我们的客户端代码将包含几个处理程序，一个用于在管道中设置它们的初始化程序类，最后是一个 JUnit 测试，用于引导客户端并将所有内容组合在一起。

### 4.1. SSL上下文

但同样，首先，让我们看看客户端的SslContext是如何设置的。我们将把它写成我们客户端 JUnit 设置的一部分：

```java
@Before
public void setup() throws Exception {
    SslContext sslCtx = SslContextBuilder.forClient()
      .sslProvider(SslProvider.JDK)
      .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
      .trustManager(InsecureTrustManagerFactory.INSTANCE)
      .applicationProtocolConfig(
        new ApplicationProtocolConfig(Protocol.ALPN, SelectorFailureBehavior.NO_ADVERTISE,
          SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2))
      .build();
}
```

如我们所见，它与服务器的 S slContext非常相似，只是我们在这里没有提供任何SelfSignedCertificate。另一个区别是我们添加了一个InsecureTrustManagerFactory来信任任何证书而无需任何验证。

重要的是，此信任管理器纯粹用于演示目的，不应在生产中使用。要改为使用受信任的证书，Netty 的[SslContextBuilder](https://netty.io/4.1/api/io/netty/handler/ssl/class-use/SslContextBuilder.html)提供了许多替代方案。

我们将在最后回到这个 JUnit 来引导客户端。

### 4.2. 处理程序

现在，让我们看一下处理程序。

首先，我们需要一个名为Http2SettingsHandler 的处理程序来处理 HTTP/2 的 SETTINGS 框架。它扩展了 Netty 的SimpleChannelInboundHandler：

```java
public class Http2SettingsHandler extends SimpleChannelInboundHandler<Http2Settings> {
    private final ChannelPromise promise;

    // constructor

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Settings msg) throws Exception {
        promise.setSuccess();
        ctx.pipeline().remove(this);
    }
}
```

该类只是初始化ChannelPromise并将其标记为成功。

它还有一个实用方法awaitSettings，我们的客户端将使用它来等待初始握手完成：

```java
public void awaitSettings(long timeout, TimeUnit unit) throws Exception {
    if (!promise.awaitUninterruptibly(timeout, unit)) {
        throw new IllegalStateException("Timed out waiting for settings");
    }
}
```

如果在规定的超时期限内未发生通道读取，则抛出IllegalStateException 。

其次，我们需要一个处理程序来处理从服务器获得的响应，我们将其命名为Http2ClientResponseHandler：

```java
public class Http2ClientResponseHandler extends SimpleChannelInboundHandler {

    private final Map<Integer, MapValues> streamidMap;

    // constructor
}
```

此类还扩展了SimpleChannelInboundHandler并声明了MapValues的streamidMap ，这是我们的Http2ClientResponseHandler的内部类：

```java
public static class MapValues {
    ChannelFuture writeFuture;
    ChannelPromise promise;

    // constructor and getters
}

```

我们添加此类是为了能够为给定的Integer键存储两个值。

当然，处理程序还有一个实用方法put，用于将值放入streamidMap：

```java
public MapValues put(int streamId, ChannelFuture writeFuture, ChannelPromise promise) {
    return streamidMap.put(streamId, new MapValues(writeFuture, promise));
}

```

接下来，让我们看看在管道中读取通道时这个处理程序做了什么。

基本上，这是我们从服务器获取 DATA 帧或ByteBuf内容作为FullHttpResponse并可以按照我们想要的方式对其进行操作的地方。

在这个例子中，我们只记录它：

```java
@Override
protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
    Integer streamId = msg.headers().getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
    if (streamId == null) {
        logger.error("HttpResponseHandler unexpected message received: " + msg);
        return;
    }

    MapValues value = streamidMap.get(streamId);

    if (value == null) {
        logger.error("Message received for unknown stream id " + streamId);
    } else {
        ByteBuf content = msg.content();
        if (content.isReadable()) {
            int contentLength = content.readableBytes();
            byte[] arr = new byte[contentLength];
            content.readBytes(arr);
            logger.info(new String(arr, 0, contentLength, CharsetUtil.UTF_8));
        }

        value.getPromise().setSuccess();
    }
}
```

在该方法的末尾，我们将ChannelPromise标记为成功以指示正确完成。

作为我们描述的第一个处理程序，此类还包含一个实用程序方法供我们的客户使用。该方法使我们的事件循环等待，直到ChannelPromise成功。或者，换句话说，它等待响应处理完成：

```java
public String awaitResponses(long timeout, TimeUnit unit) {
    Iterator<Entry<Integer, MapValues>> itr = streamidMap.entrySet().iterator();        
    String response = null;

    while (itr.hasNext()) {
        Entry<Integer, MapValues> entry = itr.next();
        ChannelFuture writeFuture = entry.getValue().getWriteFuture();

        if (!writeFuture.awaitUninterruptibly(timeout, unit)) {
            throw new IllegalStateException("Timed out waiting to write for stream id " + entry.getKey());
        }
        if (!writeFuture.isSuccess()) {
            throw new RuntimeException(writeFuture.cause());
        }
        ChannelPromise promise = entry.getValue().getPromise();

        if (!promise.awaitUninterruptibly(timeout, unit)) {
            throw new IllegalStateException("Timed out waiting for response on stream id "
              + entry.getKey());
        }
        if (!promise.isSuccess()) {
            throw new RuntimeException(promise.cause());
        }
        logger.info("---Stream id: " + entry.getKey() + " received---");
        response = entry.getValue().getResponse();
            
        itr.remove();
    }        
    return response;
}
```

### 4.3. Http2ClientInitializer

正如我们在服务器的例子中看到的那样，ChannelInitializer的目的是建立一个管道：

```java
public class Http2ClientInitializer extends ChannelInitializer {

    private final SslContext sslCtx;
    private final int maxContentLength;
    private Http2SettingsHandler settingsHandler;
    private Http2ClientResponseHandler responseHandler;
    private String host;
    private int port;

    // constructor

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        settingsHandler = new Http2SettingsHandler(ch.newPromise());
        responseHandler = new Http2ClientResponseHandler();
        
        if (sslCtx != null) {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));
            pipeline.addLast(Http2Util.getClientAPNHandler(maxContentLength, 
              settingsHandler, responseHandler));
        }
    }
    // getters
}
```

在这种情况下，我们使用新的SslHandler启动管道，以在握手过程开始时添加[TLS SNI 扩展](https://en.wikipedia.org/wiki/Server_Name_Indication)。

然后， ApplicationProtocolNegotiationHandler负责在管道中排列连接处理程序和我们的自定义处理程序：

```java
public static ApplicationProtocolNegotiationHandler getClientAPNHandler(
  int maxContentLength, Http2SettingsHandler settingsHandler, Http2ClientResponseHandler responseHandler) {
    final Http2FrameLogger logger = new Http2FrameLogger(INFO, Http2ClientInitializer.class);
    final Http2Connection connection = new DefaultHttp2Connection(false);

    HttpToHttp2ConnectionHandler connectionHandler = 
      new HttpToHttp2ConnectionHandlerBuilder().frameListener(
        new DelegatingDecompressorFrameListener(connection, 
          new InboundHttp2ToHttpAdapterBuilder(connection)
            .maxContentLength(maxContentLength)
            .propagateSettings(true)
            .build()))
          .frameLogger(logger)
          .connection(connection)
          .build();

    ApplicationProtocolNegotiationHandler clientAPNHandler = 
      new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {
        @Override
        protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
            if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                ChannelPipeline p = ctx.pipeline();
                p.addLast(connectionHandler);
                p.addLast(settingsHandler, responseHandler);
                return;
            }
            ctx.close();
            throw new IllegalStateException("Protocol: " + protocol + " not supported");
        }
    };
    return clientAPNHandler;
}

```

现在剩下要做的就是引导客户端并发送请求。

### 4.4. 引导客户端

客户端的引导在某种程度上类似于服务器的引导。之后，我们需要添加更多功能来处理发送请求和接收响应。

如前所述，我们将把它写成 JUnit 测试：

```java
@Test
public void whenRequestSent_thenHelloWorldReceived() throws Exception {

    EventLoopGroup workerGroup = new NioEventLoopGroup();
    Http2ClientInitializer initializer = new Http2ClientInitializer(sslCtx, Integer.MAX_VALUE, HOST, PORT);

    try {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.remoteAddress(HOST, PORT);
        b.handler(initializer);

        channel = b.connect().syncUninterruptibly().channel();

        logger.info("Connected to [" + HOST + ':' + PORT + ']');

        Http2SettingsHandler http2SettingsHandler = initializer.getSettingsHandler();
        http2SettingsHandler.awaitSettings(60, TimeUnit.SECONDS);
  
        logger.info("Sending request(s)...");

        FullHttpRequest request = Http2Util.createGetRequest(HOST, PORT);

        Http2ClientResponseHandler responseHandler = initializer.getResponseHandler();
        int streamId = 3;

        responseHandler.put(streamId, channel.write(request), channel.newPromise());
        channel.flush();
 
        String response = responseHandler.awaitResponses(60, TimeUnit.SECONDS);

        assertEquals("Hello World", response);

        logger.info("Finished HTTP/2 request(s)");
    } finally {
        workerGroup.shutdownGracefully();
    }
}

```

值得注意的是，这些是我们针对服务器引导程序采取的额外步骤：

-   首先，我们等待初始握手，使用Http2SettingsHandler的awaitSettings方法
-   其次，我们将请求创建为FullHttpRequest
-   第三，我们将streamId放入Http2ClientResponseHandler的streamIdMap中，并调用它的awaitResponses方法
-   最后，我们验证了响应中确实获取到了Hello World

简而言之，这就是发生的事情——客户端发送一个 HEADERS 帧，初始 SSL 握手发生，服务器在一个 HEADERS 和一个 DATA 帧中发送响应。

## 5.总结

在本教程中，我们了解了如何使用代码示例在 Netty 中实现 HTTP/2 服务器和客户端，以使用 HTTP/2 帧获取Hello World响应。

我们希望在未来看到 Netty API 在处理 HTTP/2 帧方面有更多改进，因为它仍在研究中。
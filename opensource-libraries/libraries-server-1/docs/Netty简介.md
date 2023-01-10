## 1. 简介

在本文中，我们将了解 Netty — 一个异步事件驱动的网络应用程序框架。

Netty 的主要目的是构建基于 NIO(或可能是 NIO.2)的高性能协议服务器，将网络和业务逻辑组件分离和松散耦合。它可能实现一个广为人知的协议，例如 HTTP，或者自己的特定协议。

## 2. 核心概念

Netty 是一个非阻塞框架。与阻塞 IO 相比，这导致高吞吐量。了解非阻塞 IO 对于了解 Netty 的核心组件及其关系至关重要。

### 2.1. 渠道

通道是JavaNIO 的基础。它代表一个打开的连接，可以进行读写等IO操作。

### 2.2. 未来

Netty 中Channel上的每个 IO 操作都是非阻塞的。

这意味着每个操作都会在调用后立即返回。标准Java库中有一个Future接口，但对于 Netty 来说并不方便——我们只能向Future询问操作是否完成或阻塞当前线程直到操作完成。

这就是Netty 有自己的ChannelFuture接口的原因。我们可以将回调传递给ChannelFuture，它将在操作完成时调用。

### 2.3. 事件和处理程序

Netty 使用事件驱动的应用程序范例，因此数据处理的管道是通过处理程序的事件链。事件和处理程序可以与入站和出站数据流相关。入站事件可以是以下几种：

-   通道激活和停用
-   读取操作事件
-   异常事件
-   用户事件

出站事件更简单，通常与打开/关闭连接和写入/刷新数据有关。

Netty 应用程序由几个网络和应用程序逻辑事件及其处理程序组成。通道事件处理程序的基本接口是ChannelHandler及其继承者ChannelOutboundHandler和ChannelInboundHandler。

Netty 提供了巨大的ChannelHandler 实现层次结构。值得注意的是那些只是空实现的适配器，例如ChannelInboundHandlerAdapter和ChannelOutboundHandlerAdapter。当我们只需要处理所有事件的一个子集时，我们可以扩展这些适配器。

此外，还有许多特定协议的实现，例如 HTTP，例如HttpRequestDecoder、HttpResponseEncoder、HttpObjectAggregator。在 Netty 的 Javadoc 中熟悉它们会很好。

### 2.4. 编码器和解码器

当我们使用网络协议时，我们需要执行数据序列化和反序列化。为此，Netty为能够解码传入数据的解码器引入了ChannelInboundHandler的特殊扩展。大多数解码器的基类是ByteToMessageDecoder。

为了对传出数据进行编码，Netty 对ChannelOutboundHandler进行了扩展，称为编码器。MessageToByteEncoder是大多数编码器实现的基础。我们可以使用编码器和解码器将消息从字节序列转换为Java对象，反之亦然。

## 3. 示例服务器应用程序

让我们创建一个代表一个简单协议服务器的项目，它接收请求、执行计算并发送响应。

### 3.1. 依赖关系

首先，我们需要在我们的pom.xml中提供 Netty 依赖：

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.10.Final</version>
</dependency>
```

我们可以[在 Maven Central 上](https://search.maven.org/classic/#search|gav|1|g%3A"io.netty" AND a%3A"netty-all")找到最新版本。

### 3.2. 数据模型

请求数据类将具有以下结构：

```java
public class RequestData {
    private int intValue;
    private String stringValue;
    
    // standard getters and setters
}
```

假设服务器收到请求并返回乘以 2的intValue 。响应将具有单个 int 值：

```java
public class ResponseData {
    private int intValue;

    // standard getters and setters
}
```

### 3.3. 请求解码器

现在我们需要为我们的协议消息创建编码器和解码器。

需要注意的是，Netty 与套接字接收缓冲区一起工作，它不是表示为队列，而是表示为一堆字节。这意味着当服务器未收到完整消息时，可以调用我们的入站处理程序。

我们必须确保在处理之前我们已经收到了完整的消息，并且有很多方法可以做到这一点。

首先，我们可以创建一个临时的ByteBuf并将所有入站字节附加到它，直到我们获得所需的字节数：

```java
public class SimpleProcessingHandler 
  extends ChannelInboundHandlerAdapter {
    private ByteBuf tmp;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("Handler added");
        tmp = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("Handler removed");
        tmp.release();
        tmp = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        tmp.writeBytes(m);
        m.release();
        if (tmp.readableBytes() >= 4) {
            // request processing
            RequestData requestData = new RequestData();
            requestData.setIntValue(tmp.readInt());
            ResponseData responseData = new ResponseData();
            responseData.setIntValue(requestData.getIntValue()  2);
            ChannelFuture future = ctx.writeAndFlush(responseData);
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
```

上面显示的示例看起来有点奇怪，但有助于我们理解 Netty 的工作原理。当相应的事件发生时，我们的处理程序的每个方法都会被调用。所以我们在添加处理程序时初始化缓冲区，在接收到新字节时用数据填充它，并在我们获得足够的数据时开始处理它。

我们故意不使用stringValue——以这种方式解码会不必要地复杂。这就是为什么 Netty 提供了有用的解码器类，它们是ChannelInboundHandler的实现：ByteToMessageDecoder和ReplayingDecoder 。

如上所述，我们可以使用 Netty 创建通道处理管道。所以我们可以把我们的解码器作为第一个处理程序，处理逻辑处理程序可以在它之后。

RequestData 的解码器如下所示：

```java
public class RequestDecoder extends ReplayingDecoder<RequestData> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void decode(ChannelHandlerContext ctx, 
      ByteBuf in, List<Object> out) throws Exception {
 
        RequestData data = new RequestData();
        data.setIntValue(in.readInt());
        int strLen = in.readInt();
        data.setStringValue(
          in.readCharSequence(strLen, charset).toString());
        out.add(data);
    }
}
```

这个解码器的想法非常简单。它使用ByteBuf的实现，当缓冲区中没有足够的数据用于读取操作时抛出异常。

当捕获到异常时，缓冲区将倒回到开头，解码器等待新的数据部分。解码执行后out列表不为空时解码停止。

### 3.4. 响应编码器

除了解码RequestData之外，我们还需要对消息进行编码。此操作更简单，因为我们在写操作发生时拥有完整的消息数据。

我们可以在主处理程序中将数据写入Channel，或者我们可以分离逻辑并创建一个扩展MessageToByteEncoder的处理程序，它将捕获写入ResponseData操作：

```java
public class ResponseDataEncoder 
  extends MessageToByteEncoder<ResponseData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, 
      ResponseData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getIntValue());
    }
}
```

### 3.5. 请求处理

由于我们在单独的处理程序中执行解码和编码，因此我们需要更改ProcessingHandler：

```java
public class ProcessingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) 
      throws Exception {
 
        RequestData requestData = (RequestData) msg;
        ResponseData responseData = new ResponseData();
        responseData.setIntValue(requestData.getIntValue()  2);
        ChannelFuture future = ctx.writeAndFlush(responseData);
        future.addListener(ChannelFutureListener.CLOSE);
        System.out.println(requestData);
    }
}
```

### 3.6. 服务器引导

现在让我们把它们放在一起并运行我们的服务器：

```java
public class NettyServer {

    private int port;

    // constructor

    public static void main(String[] args) throws Exception {
 
        int port = args.length > 0
          ? Integer.parseInt(args[0]);
          : 8080;
 
        new NettyServer(port).run();
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) 
                  throws Exception {
                    ch.pipeline().addLast(new RequestDecoder(), 
                      new ResponseDataEncoder(), 
                      new ProcessingHandler());
                }
            }).option(ChannelOption.SO_BACKLOG, 128)
              .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```

上述服务器引导程序示例中使用的类的详细信息可以在它们的 Javadoc 中找到。最有趣的部分是这一行：

```java
ch.pipeline().addLast(
  new RequestDecoder(), 
  new ResponseDataEncoder(), 
  new ProcessingHandler());
```

在这里，我们定义了入站和出站处理程序，它们将以正确的顺序处理请求和输出。

## 4.客户申请

客户端应该执行反向编码和解码，所以我们需要有一个RequestDataEncoder和ResponseDataDecoder：

```java
public class RequestDataEncoder 
  extends MessageToByteEncoder<RequestData> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, 
      RequestData msg, ByteBuf out) throws Exception {
 
        out.writeInt(msg.getIntValue());
        out.writeInt(msg.getStringValue().length());
        out.writeCharSequence(msg.getStringValue(), charset);
    }
}
public class ResponseDataDecoder 
  extends ReplayingDecoder<ResponseData> {

    @Override
    protected void decode(ChannelHandlerContext ctx, 
      ByteBuf in, List<Object> out) throws Exception {
 
        ResponseData data = new ResponseData();
        data.setIntValue(in.readInt());
        out.add(data);
    }
}
```

此外，我们需要定义一个ClientHandler，它将发送请求并从服务器接收响应：

```java
public class ClientHandler extends ChannelInboundHandlerAdapter {
 
    @Override
    public void channelActive(ChannelHandlerContext ctx) 
      throws Exception {
 
        RequestData msg = new RequestData();
        msg.setIntValue(123);
        msg.setStringValue(
          "all work and no play makes jack a dull boy");
        ChannelFuture future = ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) 
      throws Exception {
        System.out.println((ResponseData)msg);
        ctx.close();
    }
}
```

现在让我们引导客户端：

```java
public class NettyClient {
    public static void main(String[] args) throws Exception {
 
        String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
 
                @Override
                public void initChannel(SocketChannel ch) 
                  throws Exception {
                    ch.pipeline().addLast(new RequestDataEncoder(), 
                      new ResponseDataDecoder(), new ClientHandler());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
```

如我们所见，服务器引导有许多共同的细节。

现在我们可以运行客户端的 main 方法并查看控制台输出。正如预期的那样，我们得到了intValue等于 246 的ResponseData 。

## 5.总结

在本文中，我们快速介绍了 Netty。我们展示了它的核心组件，例如Channel和ChannelHandler。此外，我们还制作了一个简单的非阻塞协议服务器和一个客户端。
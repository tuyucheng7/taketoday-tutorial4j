## 1. 概述

在这篇简短的文章中，我们将研究 Netty 中的异常处理。

简单地说，Netty 是一个构建高性能异步和事件驱动网络应用程序的框架。I/O 操作在其生命周期内使用回调方法进行处理。

有关该框架以及如何开始使用它的更多详细信息，请参阅我们之前的[文章](https://www.baeldung.com/netty)。

## 2.Netty中的异常处理

前面说过，Netty是一个事件驱动的系统，有特定事件的回调方法。此类事件也有例外。

处理从客户端接收的数据或 I/O 操作期间可能会发生异常。发生这种情况时，将触发专用的异常捕获事件。

### 2.1. 处理源通道中的异常

异常捕获事件在触发时由 ChannelInboundHandler 或其适配器和子类的exceptionsCaught ( )方法处理。

请注意，回调已在ChannelHandler接口中弃用。它现在仅限于ChannelInboudHandler接口。

该方法接受一个Throwable对象和一个ChannelHandlerContext对象作为参数。Throwable对象可用于打印堆栈跟踪或获取本地化错误消息。

因此，让我们创建一个通道处理程序ChannelHandlerA并使用我们的实现覆盖其exceptionCaught()：

```java
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
  throws Exception {
 
    logger.info(cause.getLocalizedMessage());
    //do more exception handling
    ctx.close();
}
```

在上面的代码片段中，我们记录了异常消息并调用了ChannelHandlerContext的close()。

这将关闭服务器和客户端之间的通道。本质上导致客户端断开连接并终止。

### 2.2. 传播异常

在上一节中，我们在其来源通道中处理了异常。但是，我们实际上可以将异常传播到管道中的另一个通道处理程序。

我们将使用ChannelHandlerContext对象手动触发另一个异常捕获事件，而不是记录错误消息并调用ctx.close() 。

这将导致管道中下一个通道处理程序的exceptionCaught()被调用。

让我们修改ChannelHandlerA中的代码片段以通过调用ctx.fireExceptionCaught()传播事件：

```java
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
  throws Exception {
 
    logger.info("Exception Occurred in ChannelHandler A");
    ctx.fireExceptionCaught(cause);
}
```

此外，让我们创建另一个通道处理程序ChannelHandlerB并使用此实现覆盖其exceptionCaught() ：

```java
@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
  throws Exception {
 
    logger.info("Exception Handled in ChannelHandler B");
    logger.info(cause.getLocalizedMessage());
    //do more exception handling
    ctx.close();
}
```

在Server类中，通道按以下顺序添加到管道中：

```java
ch.pipeline().addLast(new ChannelHandlerA(), new ChannelHandlerB());
```

在所有异常都由一个指定的通道处理程序处理的情况下，手动传播异常捕获事件很有用。

## 3.总结

在本教程中，我们了解了如何使用回调方法处理 Netty 中的异常，以及如何在需要时传播异常。
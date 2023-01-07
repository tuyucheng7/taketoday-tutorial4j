## 1. 概述

在本文中，我们将探讨Java7 中新 I/O (NIO2) 的关键附加 API 之一的基础——异步通道 API。

这是涵盖该特定主题的一系列文章中的第一篇。

异步通道 API 是对Java1.4 附带的早期新 I/O (NIO) API 的增强。要了解 NIO 选择器，请点击[此链接](https://www.baeldung.com/java-nio-selector)。

NIO API 的另一个增强是新的文件系统 API。你也可以在此站点上阅读有关其[文件操作](https://www.baeldung.com/java-nio-2-file-api)和[路径操作的更多信息。](https://www.baeldung.com/java-nio-2-path)

要在我们的项目中使用 NIO2 异步通道，我们必须导入java.nio.channels包，因为其中捆绑了所需的类：

```java
import java.nio.channels.;
```

## 2. 异步通道 API 的工作原理

异步通道 API 被引入到现有的java.nio.channels包中，简单地说——通过在类名前加上Asynchronous一词作为前缀。

一些核心类包括：AsynchronousSocketChannel、AsynchronousServerSocketChannel和AsynchronousFileChannel。

你可能已经注意到，这些类在风格上与标准 NIO 通道 API 相似。

而且，大多数可用于 NIO 通道类的 API 操作在新的异步版本中也可用。主要区别在于新通道使某些操作能够异步执行。

启动操作时，异步通道 API 为我们提供了两种监视和控制未决操作的替代方法。该操作可以返回java.util.concurrent.Future对象，或者我们可以将java.nio.channels.CompletionHandler传递给它。

## 3.未来的方法

Future对象表示异步计算的结果。假设我们要创建一个服务器来监听客户端连接，我们调用AsynchronousServerSocketChannel上的静态开放API ，并可选择将返回的套接字通道绑定到一个地址：

```java
AsynchronousServerSocketChannel server 
  = AsynchronousServerSocketChannel.open().bind(null);
```

我们传入了null以便系统可以自动分配地址。然后，我们在返回的服务器SocketChannel上调用accept方法：

```java
Future<AsynchronousSocketChannel> future = server.accept();
```

当我们在旧 IO 中调用ServerSocketChannel的accept方法时，它会阻塞，直到从客户端接收到传入连接。但是AsynchronousServerSocketChannel的accept方法会立即返回一个Future对象。

Future对象的通用类型是操作的返回类型。在我们上面的例子中，它是AsynchronousSocketChannel但它也可以是Integer或String，这取决于操作的最终返回类型。

我们可以使用Future对象来查询操作的状态：

```java
future.isDone();
```

如果底层操作已经完成，则此 API 返回true 。请注意，在这种情况下，完成可能意味着正常终止、异常或取消。

我们还可以明确检查操作是否已被取消：

```java
future.isCancelled();
```

仅当操作在正常完成之前被取消时才返回true ，否则返回false。通过cancel方法执行取消：

```java
future.cancel(true);
```

该调用取消了Future对象表示的操作。该参数表示即使操作已经开始，也可以中断。操作完成后无法取消

要检索计算结果，我们使用get方法：

```java
AsynchronousSocketChannel client= future.get();
```

如果我们在操作完成之前调用此 API，它将阻塞直到完成，然后返回操作的结果。

## 4. CompletionHandler方法

使用 Future 处理操作的替代方法是使用CompletionHandler类的回调机制。异步通道允许指定完成处理程序来使用操作的结果：

```java
AsynchronousServerSocketChannel listener
  = AsynchronousServerSocketChannel.open().bind(null);

listener.accept(
  attachment, new CompletionHandler<AsynchronousSocketChannel, Object>() {
    public void completed(
      AsynchronousSocketChannel client, Object attachment) {
          // do whatever with client
      }
    public void failed(Throwable exc, Object attachment) {
          // handle failure
      }
  });
```

当 I/O 操作成功完成时，将调用已完成的回调API。如果操作失败，则调用失败的回调。

这些回调方法接受其他参数——允许我们传入任何我们认为可能适合与操作一起标记的数据。第一个参数可用作回调方法的第二个参数。

最后，一个明确的场景是——对不同的异步操作使用相同的CompletionHandler。在这种情况下，我们将受益于标记每个操作以在处理结果时提供上下文，我们将在下一节中看到这一点。

## 5.总结

在本文中，我们探索了JavaNIO2 的异步通道 API 的介绍性方面。
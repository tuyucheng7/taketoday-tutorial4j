## 1. 简介

在本快速教程中，我们演示如何使用与两个核心概念相关的[RabbitMQ](RabbitMQ简介.md) API：Connections和Channels。

## 2. RabbitMQ快速回顾

RabbitMQ是[AMQP]()(高级消息队列协议)的常用实现，被各种规模的公司广泛用于处理他们的消息传递需求。

从应用程序的角度来看，我们通常关注AMQP的主要实体：虚拟主机、交换机和队列。由于我们已经在之前的文章中介绍了这些概念，因此在这里，我们将重点关注两个较少讨论的概念的细节：连接和通道。

## 3. 连接

**客户端与RabbitMQ代理交互必须采取的第一步是建立连接**。AMPQ是一种应用层协议，因此这种连接发生在传输层协议之上。这可以是常规TCP连接，也可以是使用TLS的加密连接。连接的主要作用是提供一个安全的管道，客户端可以通过该管道与代理进行交互。

这意味着在建立连接期间，客户端必须向服务器提供有效的凭据。服务器可能支持不同的凭证类型，包括常规用户名/密码、SASL、X.509密码或任何受支持的机制。

除了安全性之外，连接建立阶段还负责协商AMPQ协议的某些方面。此时，如果客户端和/或服务器无法就协议版本或调整参数值达成一致，则连接不会建立，并且传输层连接将关闭。

### 3.1 在Java应用程序中创建连接

使用Java时，与RabbitMQ服务器通信的标准方法是使用amqp-client Java库。我们可以添加相应的Maven依赖项将这个库添加到我们的项目中：

```xml
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>5.16.0</version>
</dependency>
```

该库使用[工厂模式]()来创建新的连接。首先，我们创建一个新的ConnectionFactory实例并设置创建连接所需的所有参数。至少，这需要告知RabbitMQ主机的地址：

```java
ConnectionFactory factory = new ConnectionFactory();
factory.setHost("amqp.example.com");
```

完成这些参数的设置后，我们使用newConnection()工厂方法创建一个新的Connection实例：

```java
Connection conn = factory.newConnection();
```

## 4. 通道

**简而言之，AMQP通道是一种允许在单个连接之上复用多个逻辑流的机制**。这样可以在客户端和服务器端更好地使用资源，因为建立连接是一个相对昂贵的操作。

客户端创建一个或多个通道，以便它可以向代理发送命令。这包括与发送和/或接收消息相关的命令。

通道还提供了一些关于协议逻辑的额外保证：

-   给定通道的命令总是按照它们发送的相同顺序执行
-   给定客户端通过单个连接打开多个通道的场景，实现可以在它们之间分配可用带宽
-   双方都可以发出流量控制命令，通知对等方它应该停止发送消息

**通道的一个关键方面是它的生命周期绑定到用于创建它的连接。这意味着如果我们关闭连接，所有关联的通道也将关闭**。

### 4.1 在Java应用程序中创建通道

使用amqp-client库的Java应用程序使用前者的createChannel()方法从现有的Connection创建一个新的Channel：

```java
channel = conn.createChannel();
```

一旦有了Channel，我们就可以向服务器发送命令。例如，要创建一个队列，我们使用queueDeclare()方法：

```java
channel.queueDeclare("example.queue", true, false, true, null);
```

这段代码“声明”了一个队列，这是AMQP表达“如果队列不存在则创建”的方式。队列名称后的附加参数定义了它的附加特征：

-   durable：这个声明是持久的，这意味着它将在服务器重新启动后继续存在
-   exclusive：此队列仅限于与声明它的通道关联的连接
-   autodelete：服务器将在不再使用队列时删除队列
-   args：带有用于调整队列行为的参数的可选map；例如，我们可以使用这些参数来定义消息和死信行为的TTL

现在，要使用默认交换机将消息发布到此队列，我们使用basicPublish()方法：

```java
channel.basicPublish("", queue, null, payload);
```

此代码使用队列名称作为其路由键将消息发送到默认交换机。

## 5. 通道分配策略

让我们考虑一个使用消息系统的场景：CQRS(命令查询责任分离)应用程序。简而言之，基于CQRS的应用程序有两条独立的路径：命令和查询。命令可以更改数据但从不返回值。另一方面，查询返回值但从不修改它们。

由于命令路径从不返回任何数据，因此服务可以异步执行它们。在典型的实现中，我们有一个HTTP POST端点，它在内部构建消息并将其发送到队列以供以后处理。

**现在，对于一个必须处理几十个甚至上百个并发请求的服务来说，每次都打开连接和通道并不是一个现实的选择。相反，更好的方法是使用通道池**。当然，这会导致下一个问题：我们应该创建单个连接并从中创建通道，还是使用多个连接？

### 5.1 单连接/多通道

在此策略中，我们使用单个连接并仅创建一个通道池，其容量等于服务可以管理的最大并发连接数。对于传统的thread-per-request模型，这应该设置为与请求处理程序线程池相同的大小。

这种策略的缺点是，在较重的负载下，我们必须通过关联的通道一次发送一个命令这一事实意味着我们必须使用同步机制。这反过来又增加了命令路径中的额外延迟，我们希望将其最小化。

### 5.2 每线程连接策略

另一种选择是走向另一个极端并使用连接池，因此永远不会争用通道。对于每个Connection，我们将创建一个单独的Channel，处理程序线程将使用该Channel向服务器发出命令。

然而，我们从客户端移除同步是有代价的。代理必须为每个连接分配额外的资源，例如套接字描述符和状态信息。此外，服务器必须在客户端之间分配可用的吞吐量。

## 6. 基准策略

**为了评估这些候选策略，让我们为每个策略运行一个简单的基准测试。基准测试包括并行运行多个worker(工作线程)，每个worker发送一千条4KB的消息**。在构造时，worker会收到一个连接，它将从中创建一个通道来发送命令。它还接收迭代次数、有效负载大小和用于通知测试运行器它已完成发送消息的CountDownLatch：

```java
public class Worker implements Callable<Worker.WorkerResult> {
    
    // ... field and constructor omitted
    @Override
    public WorkerResult call() throws Exception {

        try {
            long start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                channel.basicPublish("", queue, null, payload);
            }

            long elapsed = System.currentTimeMillis() - start;
            channel.queueDelete(queue);
            return new WorkerResult(elapsed);
        } finally {
            counter.countDown();
        }
    }
    
    public static class WorkerResult {
        public final long elapsed;

        WorkerResult(long elapsed) {
            this.elapsed = elapsed;
        }
    }
}
```

除了通过递减latch来指示它已完成其工作外，worker还返回一个WorkerResult实例，其中包含发送所有消息的经过时间。虽然这里我们只有一个long值，但我们可以使用扩展它来返回更多详细信息。

控制器根据正在评估的策略创建连接工厂和worker。对于单个连接，它创建Connection实例并将其传递给每个worker：

```java
@Override
public Long call() {
    
    try {
        Connection connection = factory.newConnection();
        CountDownLatch counter = new CountDownLatch(workerCount);
        List<Worker> workers = new ArrayList<>();
        
        for( int i = 0 ; i < workerCount ; i++ ) {
            workers.add(new Worker("queue_" + i, connection, iterations, counter,payloadSize));
        }

        ExecutorService executor = new ThreadPoolExecutor(workerCount, workerCount, 0,
          TimeUnit.SECONDS, new ArrayBlockingQueue<>(workerCount, true));
        long start = System.currentTimeMillis();
        executor.invokeAll(workers);
        
        if( counter.await(5, TimeUnit.MINUTES)) {
            long elapsed = System.currentTimeMillis() - start;
            return throughput(workerCount,iterations,elapsed);
        }
        else {
            throw new RuntimeException("Timeout waiting workers to complete");
        }        
    }
    catch(Exception ex) {
        throw new RuntimeException(ex);
    }
}
```

对于多连接策略，我们为每个worker创建一个新的连接：

```java
for (int i = 0; i < workerCount; i++) {
    Connection conn = factory.newConnection();
    workers.add(new Worker("queue_" + i, conn, iterations, counter, payloadSize));
}
```

throughput函数计算基准测量将是完成所有worker所需的总时间除以worker数：

```java
private static long throughput(int workerCount, int iterations, long elapsed) {
    return (iterations  workerCount  1000) / elapsed;
}
```

请注意，我们需要将分子乘以1000，以便以秒为单位获得消息的吞吐量。

## 7. 运行基准测试

这些是我们对这两种策略进行基准测试的结果。对于每个worker计数，我们运行了基准测试10次，并使用平均值作为tar特定worker/策略的吞吐量度量。按照今天的标准，环境本身是适度的：

-   CPU：双核i7戴尔笔记本@3.0 GHz
-   总内存：16GB
-   RabbitMQ：3.10.7在Docker上运行(docker-machine with 4 GBytes RAM)

<img src="../assets/img_4.png">

对于这个特定的环境，我们看到单一连接策略略有优势。对于150个worker的情况，这种优势似乎有所增加。

## 8. 选择策略

鉴于基准测试结果，我们不能指出一个明显的方案。对于5到100之间的worker数，结果或多或少相同。在那之后，与多个连接相关的开销似乎高于在单个连接上处理多个通道。

**此外，我们必须考虑到测试worker只做一件事：将固定消息发送到队列。但真实世界的应用程序，如我们提到的CQRS，通常在发送消息之前和/或之后执行一些额外的工作。因此，要选择最佳策略，推荐的方法是使用尽可能接近生产环境的配置来运行你自己的基准测试**。

## 9. 总结

在本文中，我们探讨了RabbitMQ中通道和连接的概念，以及我们如何以不同的方式使用它们。
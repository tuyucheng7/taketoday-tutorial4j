## 1. 概述

gRPC是用于执行进程间远程过程调用(RPC)的平台，它遵循客户端-服务器模型，具有高性能，并支持最重要的计算机语言。

在本教程中，我们将重点介绍gRPC流。**流式传输允许服务器和客户端之间的多路复用消息，从而创建非常高效和灵活的进程间通信**。

## 2. gRPC流式处理的基础知识

**gRPC使用[HTTP/2](https://en.wikipedia.org/wiki/HTTP/2)网络协议进行服务间通信，HTTP/2的一个关键优势是它支持流，每个流可以多路复用共享单个连接的多个双向消息**。

在gRPC中，我们可以使用三种函数调用类型进行流式处理：

1.  服务器流式处理RPC：客户端向服务器发送单个请求，并获取它按顺序读取的多条消息。
2.  客户端流式处理RPC：客户端向服务器发送一系列消息，客户端等待服务器处理消息并读取返回的响应。
3.  双向流式处理RPC：客户端和服务器可以来回发送多条消息，消息的接收顺序与发送顺序相同，但是，服务器或客户端可以按照他们选择的顺序响应接收到的消息。

为了演示如何使用这些过程调用，我们将编写一个简单的客户端-服务器应用程序示例，用于交换股票证券的信息。

## 3. 服务定义

我们使用stock_quote.proto来定义服务接口和有效负载消息的结构：

```protobuf
service StockQuoteProvider {

  rpc serverSideStreamingGetListStockQuotes(Stock) returns (stream StockQuote) {}

  rpc clientSideStreamingGetStatisticsOfStocks(stream Stock) returns (StockQuote) {}

  rpc bidirectionalStreamingGetListsStockQuotes(stream Stock) returns (stream StockQuote) {}
}
message Stock {
  string ticker_symbol = 1;
  string company_name = 2;
  string description = 3;
}
message StockQuote {
  double price = 1;
  int32 offer_number = 2;
  string description = 3;
}
```

**StockQuoteProvider服务具有三种支持消息流的方法类型**，在下一节中，我们将介绍它们的实现。

我们从服务的方法签名中可以看出，客户端通过发送Stock消息来查询服务器，服务器使用StockQuote消息发回响应。

**我们使用pom.xml文件中定义的protobuf-maven-plugin从stock-quote.proto IDL文件生成Java代码**。

该插件在target/generated-sources/protobuf/java和/grpc-java目录中生成客户端存根和服务器端代码。

**我们将利用生成的代码来实现我们的服务器和客户端**。

## 4. 服务器实现

StockServer构造函数使用gRPC服务器来监听和分派传入的请求：

```java
public class StockServer {
    private int port;
    private io.grpc.Server server;

    public StockServer(int port) throws IOException {
        this.port = port;
        server = ServerBuilder.forPort(port)
              .addService(new StockService())
              .build();
    }
    //...
}
```

我们将StockService添加到io.grpc.Server中，StockService扩展了StockQuoteProviderImplBase，它是protobuf插件从我们的proto文件生成的。因此，**StockQuoteProviderImplBase具有三种流服务方法的存根**。

**StockService需要覆盖这些存根方法来执行我们服务的实际实现**。

接下来，我们将了解如何针对三个流式处理案例完成此操作。

### 4.1 服务器端流式传输

**客户发送一个报价请求并返回多个响应，每个响应都为商品提供不同的价格**：

```java
@Override
public void serverSideStreamingGetListStockQuotes(Stock request, StreamObserver<StockQuote> responseObserver) {
    
    for (int i = 1; i <= 5; i++) {
        StockQuote stockQuote = StockQuote.newBuilder()
              .setPrice(fetchStockPriceBid(request))
              .setOfferNumber(i)
              .setDescription("Price for stock:" + request.getTickerSymbol())
              .build();
        responseObserver.onNext(stockQuote);
    }
    responseObserver.onCompleted();
}
```

该方法创建一个StockQuote，获取价格，并标记报价编号。对于每个产品/服务，它都会向调用responseObserver::onNext的客户端发送一条消息。它使用responseObserver::onCompleted来表示RPC已完成。

### 4.2 客户端流式传输

**客户端发送多个股票，服务器返回单个StockQuote：**

```java
@Override
public StreamObserver<Stock> clientSideStreamingGetStatisticsOfStocks(final StreamObserver<StockQuote> responseObserver) {
    return new StreamObserver<>() {
        int count;
        double price = 0.0;
        StringBuffer sb = new StringBuffer();

        @Override
        public void onNext(Stock stock) {
            count++;
            price = +fetchStockPriceBid(stock);
            sb.append(":")
                  .append(stock.getTickerSymbol());
        }

        @Override
        public void onCompleted() {
            responseObserver.onNext(StockQuote.newBuilder()
                  .setPrice(price / count)
                  .setDescription("Statistics-" + sb.toString())
                  .build());
            responseObserver.onCompleted();
        }

        @Override
        public void onError(Throwable t) {
            logger.warn("error:{}", t.getMessage());
        }
    };
}
```

该方法获取一个StreamObserver<StockQuote\>作为响应客户端的参数，它返回一个StreamObserver<Stock\>，用于在其中处理客户端请求消息。

返回的StreamObserver<Stock\>会覆盖onNext()以在每次客户端发送请求时得到通知。

StreamObserver<Stock\>.onCompleted()方法在客户端发送完所有消息后被调用。对于我们收到的所有Stock消息，我们找到获取的股票价格的平均值，创建StockQuote，并调用responseObserver::onNext将结果传递给客户端。

最后，我们覆盖StreamObserver<Stock\>.onError()来处理异常终止。

### 4.3 双向流式传输

**客户端发送多个股票，服务器为每个请求返回一组价格**：

```java
@Override
public StreamObserver<Stock> bidirectionalStreamingGetListsStockQuotes(final StreamObserver<StockQuote> responseObserver) {
    return new StreamObserver<>() {
        @Override
        public void onNext(Stock request) {

            for (int i = 1; i <= 5; i++) {
                StockQuote stockQuote = StockQuote.newBuilder()
                      .setPrice(fetchStockPriceBid(request))
                      .setOfferNumber(i)
                      .setDescription("Price for stock:" + request.getTickerSymbol())
                      .build();
                responseObserver.onNext(stockQuote);
            }
        }

        @Override
        public void onCompleted() {
            responseObserver.onCompleted();
        }

        @Override
        public void onError(Throwable t) {
            logger.warn("error:{}", t.getMessage());
        }
    };
}
```

我们具有与上一个示例相同的方法签名，改变的是实现：我们不会等待客户端发送所有消息后才响应。

在这种情况下，我们在收到每条传入消息后立即调用responseObserver::onNext，并按照接收消息的顺序调用。

重要的是要注意，如果需要，我们可以很容易地更改响应的顺序。

## 5. 客户端实现

StockClient的构造函数采用gRPC通道并实例化gRPC Maven插件生成的存根类：

```java
public class StockClient {
    private StockQuoteProviderBlockingStub blockingStub;
    private StockQuoteProviderStub nonBlockingStub;

    public StockClient(Channel channel) {
        blockingStub = StockQuoteProviderGrpc.newBlockingStub(channel);
        nonBlockingStub = StockQuoteProviderGrpc.newStub(channel);
    }
    // ...
}
```

**StockQuoteProviderBlockingStub和StockQuoteProviderStub支持进行同步和异步客户端方法请求**。

接下来我们将看到三个流式处理RPC的客户端实现。

### 5.1 具有服务器端流式处理的客户端RPC

客户端向服务器发出一次调用请求股票价格并返回报价列表：

```java
public void serverSideStreamingListOfStockPrices() {
    logger.info("######START EXAMPLE######: ServerSideStreaming - list of Stock prices from a given stock");
    Stock request = Stock.newBuilder()
          .setTickerSymbol("AU")
          .setCompanyName("Austich")
          .setDescription("server streaming example")
          .build();
    Iterator<StockQuote> stockQuotes;
    try {
        logger.info("REQUEST - ticker symbol {}", request.getTickerSymbol());
        stockQuotes = blockingStub.serverSideStreamingGetListStockQuotes(request);
        for (int i = 1; stockQuotes.hasNext(); i++) {
            StockQuote stockQuote = stockQuotes.next();
            logger.info("RESPONSE - Price #" + i + ": {}", stockQuote.getPrice());
        }
    } catch (StatusRuntimeException e) {
        logger.info("RPC failed: {}", e.getStatus());
    }
}
```

我们使用blockingStub::serverSideStreamingGetListStock来发出同步请求，并使用Iterator返回StockQuotes集合。

### 5.2 具有客户端流式处理的客户端RPC

客户端向服务器发送一个Stock流，并返回一个包含一些统计数据的StockQuote：

```java
public void clientSideStreamingGetStatisticsOfStocks() throws InterruptedException {
    logger.info("######START EXAMPLE######: ClientSideStreaming - getStatisticsOfStocks from a list of stocks");
    final CountDownLatch finishLatch = new CountDownLatch(1);
    StreamObserver<StockQuote> responseObserver = new StreamObserver<>() {
        @Override
        public void onNext(StockQuote summary) {
            logger.info("RESPONSE, got stock statistics - Average Price: {}, description: {}", summary.getPrice(), summary.getDescription());
        }

        @Override
        public void onCompleted() {
            logger.info("Finished clientSideStreamingGetStatisticsOfStocks");
            finishLatch.countDown();
        }

        @Override
        public void onError(Throwable t) {
            logger.warn("Stock Statistics Failed: {}", Status.fromThrowable(t));
            finishLatch.countDown();
        }
    };

    StreamObserver<Stock> requestObserver = nonBlockingStub.clientSideStreamingGetStatisticsOfStocks(responseObserver);
    try {
        for (Stock stock : stocks) {
            logger.info("REQUEST: {}, {}", stock.getTickerSymbol(), stock.getCompanyName());
            requestObserver.onNext(stock);
            if (finishLatch.getCount() == 0) {
                return;
            }
        }
    } catch (RuntimeException e) {
        requestObserver.onError(e);
        throw e;
    }
    requestObserver.onCompleted();
    if (!finishLatch.await(1, TimeUnit.MINUTES)) {
        logger.warn("clientSideStreamingGetStatisticsOfStocks can not finish within 1 minutes");
    }
}
```

正如我们在服务器示例中所做的那样，我们使用StreamObservers来发送和接收消息。

requestObserver使用非阻塞存根将Stock集合发送到服务器。

使用responseObserver，我们得到了带有一些统计数据的StockQuote。

### 5.3 具有双向流式处理的客户端RPC

客户端发送Stock流并返回每个Stock的价格列表。

```java
public void bidirectionalStreamingGetListsStockQuotes() throws InterruptedException {
    logger.info("#######START EXAMPLE#######: BidirectionalStreaming - getListsStockQuotes from list of stocks");
    final CountDownLatch finishLatch = new CountDownLatch(1);
    StreamObserver<StockQuote> responseObserver = new StreamObserver<>() {
        @Override
        public void onNext(StockQuote stockQuote) {
            logger.info("RESPONSE price#{} : {}, description:{}", stockQuote.getOfferNumber(), stockQuote.getPrice(), stockQuote.getDescription());
        }

        @Override
        public void onCompleted() {
            logger.info("Finished bidirectionalStreamingGetListsStockQuotes");
            finishLatch.countDown();
        }

        @Override
        public void onError(Throwable t) {
            logger.warn("bidirectionalStreamingGetListsStockQuotes Failed: {0}", Status.fromThrowable(t));
            finishLatch.countDown();
        }
    };
    StreamObserver<Stock> requestObserver = nonBlockingStub.bidirectionalStreamingGetListsStockQuotes(responseObserver);
    try {
        for (Stock stock : stocks) {
            logger.info("REQUEST: {}, {}", stock.getTickerSymbol(), stock.getCompanyName());
            requestObserver.onNext(stock);
            Thread.sleep(200);
            if (finishLatch.getCount() == 0) {
                return;
            }
        }
    } catch (RuntimeException e) {
        requestObserver.onError(e);
        throw e;
    }
    requestObserver.onCompleted();

    if (!finishLatch.await(1, TimeUnit.MINUTES)) {
        logger.warn("bidirectionalStreamingGetListsStockQuotes can not finish within 1 minute");
    }
}
```

该实现与客户端流式传输案例非常相似，我们用requestObserver发送Stocks-唯一的区别是现在我们使用responseObserver得到多个响应。响应与请求分离-它们可以以任何顺序到达。

## 6. 运行服务器和客户端

使用Maven编译代码后，我们只需要打开两个命令行窗口即可。

运行服务器：

```bash
mvn exec:java -Dexec.mainClass=cn.tuyucheng.taketoday.grpc.streaming.StockServer
```

运行客户端：

```bash
mvn exec:java -Dexec.mainClass=cn.tuyucheng.taketoday.grpc.streaming.StockClient
```

## 7. 总结

在本文中，我们介绍了如何在gRPC中使用流式处理。流式传输是一项强大的功能，它允许客户端和服务器通过单个连接发送多条消息来进行通信。此外，**消息的接收顺序与发送顺序相同，但任何一方都可以按照他们希望的任何顺序读取或写入消息**。
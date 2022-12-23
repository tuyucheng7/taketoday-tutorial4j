## 1. 概述

[gRPC](gRPC简介.md)是一个用于进程间远程过程调用(RPC)的平台，它具有高性能，可以在任何环境中运行。

在本教程中，我们将重点介绍使用Java的gRPC错误处理。gRPC具有极低的延迟和高吞吐量，因此非常适合在微服务架构等复杂环境中使用。在这些系统中，充分了解网络不同组件的状态、性能和故障至关重要。因此，良好的错误处理实现对于帮助我们实现前面的目标至关重要。

## 2. gRPC中错误处理的基础知识

gRPC中的错误是第一类实体，**即gRPC中的每个调用要么是有效负载消息，要么是状态错误消息**。

**这些错误在状态消息中编码并在所有支持的语言中实现**。

通常，我们不应在响应负载中包含错误。为此，请始终使用StreamObserver::OnError，它在内部将状态错误添加到尾随标头。正如我们将在下面看到的，唯一的例外是当我们使用流时。

所有客户端或服务器gRPC库都支持[官方gRPC错误模型](https://www.grpc.io/docs/guides/error/)，Java将此错误模型封装为类**io.grpc.Status**，此类**需要一个标准[错误状态代码](https://grpc.github.io/grpc/core/md_doc_statuscodes.html)和一个可选的字符串错误消息来提供附加信息**。这种错误模型的优点是它独立于所使用的数据编码(协议缓冲区、REST等)而受到支持。但是，它非常有限，因为我们不能在状态中包含错误详细信息。 

如果你的gRPC应用程序实现了用于数据编码的协议缓冲区，那么可以[为Google API使用更丰富的错误模型](https://cloud.google.com/apis/design/errors#error_model)。**com.google.rpc.Status**类封装了这个错误模型，此类**提供com.google.rpc.Code值、错误消息和附加错误详细信息作为protobuf消息附加**。此外，我们可以利用一组预定义的protobuf错误消息，这些消息在[error_details.proto](https://github.com/googleapis/googleapis/blob/master/google/rpc/error_details.proto)中定义，涵盖了最常见的情况。在包com.google.rpc中，我们有类：RetryInfo、DebugInfo、QuotaFailure、ErrorInfo、PrecondicionFailure、BadRequest、RequestInfo、ResourceInfo和Help，它们将所有错误消息封装在[error_details.proto](https://github.com/googleapis/googleapis/blob/master/google/rpc/error_details.proto)中。

**除了这两个错误模型之外，我们还可以定义自定义错误消息，这些消息可以作为键值对添加到RPC元数据中**。

我们将编写一个非常简单的应用程序来演示如何将这些错误模型与定价服务结合使用，其中客户端发送商品名称，服务器提供定价值。

## 3. 一元RPC调用

让我们开始考虑在commodity_price.proto中定义的以下服务接口：

```protobuf
service CommodityPriceProvider {
  rpc getBestCommodityPrice(Commodity) returns (CommodityQuote) {}
}

message Commodity {
  string access_token = 1;
  string commodity_name = 2;
}

message CommodityQuote {
  string commodity_name = 1;
  string producer_name = 2;
  double price = 3;
}

message ErrorResponse {
  string commodity_name = 1;
  string access_token = 2;
  string expected_token = 3;
  string expected_value = 4;
}
```

服务的输入是商品消息。在请求中，客户端必须提供access_token和commodity_name。

服务器使用CommodityQuote同步响应，其中说明商品的commodity_name、producer_name和相关价格。

为了便于说明，我们还定义了一个自定义的ErrorResponse，这是我们将作为元数据发送给客户端的自定义错误消息的示例。

### 3.1 使用io.grpc.Status响应

在服务器的服务调用中，我们检查对有效商品的请求：

```java
public void getBestCommodityPrice(Commodity request, StreamObserver<CommodityQuote> responseObserver) {

    if (commodityLookupBasePrice.get(request.getCommodityName()) == null) {

        Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
        ErrorResponse errorResponse = ErrorResponse.newBuilder()
              .setCommodityName(request.getCommodityName())
              .setAccessToken(request.getAccessToken())
              .setExpectedValue("Only Commodity1, Commodity2 are supported")
              .build();
        Metadata metadata = new Metadata();
        metadata.put(errorResponseKey, errorResponse);
        responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("The commodity is not supported")
              .asRuntimeException(metadata));
    }
    // ...
}
```

在这个简单的示例中，如果Commodity在commodityLookupBasePrice HashTable中不存在，我们将返回一个错误。

首先，我们构建一个自定义的ErrorResponse并创建一个键值对，并将其添加到metadata.put(errorResponseKey, errorResponse)中的元数据中。

我们使用io.grpc.Status来指定错误状态，**函数responseObserver::onError将Throwable作为参数，因此我们使用asRuntimeException(metadata)将Status转换为Throwable**。asRuntimeException可以选择采用一个元数据参数(在我们的例子中为一个ErrorResponse键值对)，该参数添加到消息的尾部。

如果客户端发出无效请求，它将返回一个异常：

```java
@Test
public void whenUsingInvalidCommodityName_thenReturnExceptionIoRpcStatus() throws Exception {
    Commodity request = Commodity.newBuilder()
          .setAccessToken("123validToken")
          .setCommodityName("Commodity5")
          .build();

    StatusRuntimeException thrown = Assertions.assertThrows(StatusRuntimeException.class, () -> blockingStub.getBestCommodityPrice(request));

    assertEquals("INVALID_ARGUMENT", thrown.getStatus().getCode().toString());
    assertEquals("INVALID_ARGUMENT: The commodity is not supported", thrown.getMessage());
    Metadata metadata = Status.trailersFromThrowable(thrown);
    ErrorResponse errorResponse = metadata.get(ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance()));
    assertEquals("Commodity5", errorResponse.getCommodityName());
    assertEquals("123validToken", errorResponse.getAccessToken());
    assertEquals("Only Commodity1, Commodity2 are supported", errorResponse.getExpectedValue());
}
```

对blockingStub::getBestCommodityPrice的调用会抛出StatusRuntimeException，因为请求具有无效的商品名称。

我们使用Status::trailerFromThrowable来访问元数据，ProtoUtils::keyForProto为我们提供了ErrorResponse的元数据键。

### 3.2 使用com.google.rpc.Status响应

让我们考虑以下服务器代码示例：

```java
public void getBestCommodityPrice(Commodity request, StreamObserver<CommodityQuote> responseObserver) {
    // ...
    if (request.getAccessToken().equals("123validToken") == false) {

        com.google.rpc.Status status = com.google.rpc.Status.newBuilder()
              .setCode(com.google.rpc.Code.NOT_FOUND.getNumber())
              .setMessage("The access token not found")
              .addDetails(Any.pack(ErrorInfo.newBuilder()
                    .setReason("Invalid Token")
                    .setDomain("cn.tuyucheng.taketoday.grpc.errorhandling")
                    .putMetadata("insertToken", "123validToken")
                    .build()))
              .build();
        responseObserver.onError(StatusProto.toStatusRuntimeException(status));
    }
    // ...
}
```

在实现中，如果请求没有有效令牌，getBestCommodityPrice将返回错误。

此外，我们将状态代码、消息和详细信息设置为com.google.rpc.Status。

在这个例子中，我们使用预定义的com.google.rpc.ErrorInfo而不是我们的自定义ErrorDetails(尽管如果需要我们可以同时使用两者)。我们使用Any::pack()序列化ErrorInfo。

StatusProto::toStatusRuntimeException类将com.google.rpc.Status转换为Throwable。

原则上，我们还可以添加error_details.proto中定义的其他消息来进一步自定义响应。

客户端实现非常简单：

```java
@Test
public void whenUsingInvalidRequestToken_thenReturnExceptionGoogleRPCStatus() throws Exception {

    Commodity request = Commodity.newBuilder()
          .setAccessToken("invalidToken")
          .setCommodityName("Commodity1")
          .build();

    StatusRuntimeException thrown = Assertions.assertThrows(StatusRuntimeException.class,
          () -> blockingStub.getBestCommodityPrice(request));
    com.google.rpc.Status status = StatusProto.fromThrowable(thrown);
    assertNotNull(status);
    assertEquals("NOT_FOUND", Code.forNumber(status.getCode()).toString());
    assertEquals("The access token not found", status.getMessage());
    for (Any any : status.getDetailsList()) {
        if (any.is(ErrorInfo.class)) {
            ErrorInfo errorInfo = any.unpack(ErrorInfo.class);
            assertEquals("Invalid Token", errorInfo.getReason());
            assertEquals("cn.tuyucheng.taketoday.grpc.errorhandling", errorInfo.getDomain());
            assertEquals("123validToken", errorInfo.getMetadataMap().get("insertToken"));
        }
    }
}
```

StatusProto.fromThrowable是一种工具方法，可直接从异常中获取com.google.rpc.Status。

从status::getDetailsList我们得到了com.google.rpc.ErrorInfo的详细信息。

## 4. gRPC流错误

[gRPC流](在Java中使用gRPC进行流式传输.md)允许服务器和客户端在单个RPC调用中发送多条消息。

**在错误传播方面，我们目前使用的方法不适用于gRPC流，原因是onError()必须是RPC中调用的最后一个方法**，因为在此调用之后，框架会切断客户端和服务器之间的通信。

当我们使用流时，这不是我们想要的行为。**相反，我们希望保持连接打开以响应可能通过RPC传入的其他消息**。

**这个问题的一个好的解决方案是将错误添加到消息本身**，正如我们在commodity_price.proto中所示：

```protobuf
service CommodityPriceProvider {

  rpc getBestCommodityPrice(Commodity) returns (CommodityQuote) {}

  rpc bidirectionalListOfPrices(stream Commodity) returns (stream StreamingCommodityQuote) {}
}

message Commodity {
  string access_token = 1;
  string commodity_name = 2;
}

message StreamingCommodityQuote{
  oneof message{
    CommodityQuote comodity_quote = 1;
    google.rpc.Status status = 2;
  }
}
```

函数bidirectionalListOfPrices返回一个StreamingCommodityQuote，此消息具有oneof关键字，表明它可以使用CommodityQuote或google.rpc.Status。

在以下示例中，如果客户端发送无效令牌，则服务器会在响应正文中添加状态错误：

```java
public StreamObserver<Commodity> bidirectionalListOfPrices(StreamObserver<StreamingCommodityQuote> responseObserver) {

    return new StreamObserver<>() {
        @Override
        public void onNext(Commodity request) {

            if (request.getAccessToken().equals("123validToken") == false) {

                com.google.rpc.Status status = com.google.rpc.Status.newBuilder()
                      .setCode(Code.NOT_FOUND.getNumber())
                      .setMessage("The access token not found")
                      .addDetails(Any.pack(ErrorInfo.newBuilder()
                            .setReason("Invalid Token")
                            .setDomain("cn.tuyucheng.taketoday.grpc.errorhandling")
                            .putMetadata("insertToken", "123validToken")
                            .build()))
                      .build();
                StreamingCommodityQuote streamingCommodityQuote = StreamingCommodityQuote.newBuilder()
                      .setStatus(status)
                      .build();
                responseObserver.onNext(streamingCommodityQuote);
            }
            // ...
        }
    }
}
```

**该代码创建com.google.rpc.Status的实例并将其添加到StreamingCommodityQuote响应消息中**，它不会调用onError()，因此框架不会中断与客户端的连接。

让我们看一下客户端实现：

```java
public void onNext(StreamingCommodityQuote streamingCommodityQuote) {

    switch (streamingCommodityQuote.getMessageCase()) {
        case COMODITY_QUOTE:
            CommodityQuote commodityQuote = streamingCommodityQuote.getComodityQuote();
            logger.info("RESPONSE producer:" + commodityQuote.getCommodityName() + " price:" + commodityQuote.getPrice());
            break;
        case STATUS:
            com.google.rpc.Status status = streamingCommodityQuote.getStatus();
            logger.info("Status code:" + Code.forNumber(status.getCode()));
            logger.info("Status message:" + status.getMessage());
            for (Any any : status.getDetailsList()) {
                if (any.is(ErrorInfo.class)) {
                    ErrorInfo errorInfo;
                    try {
                        errorInfo = any.unpack(ErrorInfo.class);
                        logger.info("Reason:" + errorInfo.getReason());
                        logger.info("Domain:" + errorInfo.getDomain());
                        logger.info("Insert Token:" + errorInfo.getMetadataMap().get("insertToken"));
                    } catch (InvalidProtocolBufferException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
            break;
        // ...
    }
}
```

**客户端在onNext(StreamingCommodityQuote)中获取返回的消息，并使用switch语句来区分CommodityQuote或com.google.rpc.Status**。

## 5. 总结

**在本教程中，我们演示了如何在gRPC中为一元和基于流的RPC调用实现错误处理**。

gRPC是用于分布式系统中远程通信的绝佳框架，在这些系统中，重要的是要有一个非常健壮的错误处理实现来帮助监视系统，这在微服务等复杂架构中更为重要。
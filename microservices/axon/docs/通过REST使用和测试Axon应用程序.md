## 一、概述

Axon Framework 帮助我们构建事件驱动的微服务系统。在[Axon 框架指南](https://www.baeldung.com/axon-cqrs-event-sourcing)中，我们通过简单的 Axon [Spring Boot](https://www.baeldung.com/spring-boot-start)应用程序了解了 Axon。该应用程序可以创建和更新订单，还可以确认和运送这些订单。

[在Axon Framework 中的 Dispatching Queries](https://www.baeldung.com/axon-query-dispatching)中，我们向*OrderQueryService*添加了更多查询。

查询通常用于 UI，通常调用 REST 端点。

在本教程中，我们将为**所有查询创建 REST 端点**。我们还将使用来自集成测试的这些端点。

## 2. 在 REST 端点中使用查询

[*我们可以通过向@RestController*](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration#controller)注释类添加函数来添加 REST 端点。为此，我们将使用类*OrderRestEndpoint 。*以前我们直接在控制器中使用*QueryGateway 。*我们将为OrderQueryService替换注入的*QueryGateway* *，*这是我们[在 Axon Framework 中的 Dispatching Queries](https://www.baeldung.com/axon-query-dispatching)中实现的。这样，控制器功能唯一关心的是将行为绑定到 REST 路径。

所有端点都列在项目的*order-api.http文件中。*多亏了这个文件，当使用 IntelliJ 作为我们的 IDE 时，端点是可调用的。

### 2.1. 点对点查询

**点对点查询只有一个响应**，因此很容易实现：

```java
@GetMapping("/all-orders")
public CompletableFuture<List<OrderResponse>> findAllOrders() {
    return orderQueryService.findAllOrders();
}复制
```

Spring 等待*CompletableFuture*被解析并以 JSON 格式的负载响应。我们可以通过调用*localhost:8080/all-orders*来测试这一点，以获取一个数组中的所有订单。

从一个干净的设置中，如果我们首先使用一个帖子添加两个订单到*http://localhost:8080/order/666a1661-474d-4046-8b12-8b5896312768*和*http://localhost:8080/ship-order*，我们应该看到当我们在*http://localhost:8080/all-orders*上调用 get 时的以下内容：

```json
[
  {
    "orderId": "72d67527-a27c-416e-a904-396ebf222344",
    "products": {
      "Deluxe Chair": 1
    },
    "orderStatus": "SHIPPED"
  },
  {
    "orderId": "666a1661-474d-4046-8b12-8b5896312768",
    "products": {},
    "orderStatus": "CREATED"
  }
]复制
```

### 2.2. 流式查询

流式查询将返回事件流并最终关闭。我们可以等待流关闭并在完成后发送响应。但是，**直接流式传输效率更高**。我们通过使用[服务器发送事件](https://www.baeldung.com/spring-server-sent-events)来做到这一点：

```java
@GetMapping(path = "/all-orders-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<OrderResponse> allOrdersStreaming() {
    return orderQueryService.allOrdersStreaming();
}复制
```

通过添加媒体类型，Spring 了解我们希望将响应作为服务器发送事件。这意味着每个订单都是单独发送的。如果客户端支持server-send事件，*localhost:8080/all-orders-streaming*会一一返回所有的订单。

在数据库中拥有与点对点查询相同的项目将得到如下结果：

```json
data:{"orderId":"72d67527-a27c-416e-a904-396ebf222344","products":{"Deluxe Chair":1},"orderStatus":"SHIPPED"}

data:{"orderId":"666a1661-474d-4046-8b12-8b5896312768","products":{},"orderStatus":"CREATED"}复制
```

这些都是单独的服务器发送事件。

### 2.3. 分散-聚集查询。

组合返回到 Axon 查询的响应的逻辑已经存在于 OrderQueryService*中*。这使得**实现与点对点查询非常相似**，因为只有一个响应。例如，要使用分散聚集查询添加端点：

```java
@GetMapping("/total-shipped/{product-id}")
public Integer totalShipped(@PathVariable("product-id") String productId) {
    return orderQueryService.totalShipped(productId);
}复制
```

调用*http://localhost:8080/total-shipped/Deluxe Chair*返回运送的椅子总数，包括来自*LegacyQueryHandler*的 234 。*如果来自发货订单*调用的那个仍在数据库中，它应该返回 235。

### 2.4. 订阅查询

与流式查询相反，订阅查询可能永远不会结束。因此，等待订阅查询完成是不可取的。**我们将再次利用服务器发送事件**来添加端点：

```java
@GetMapping(path = "/order-updates/{order-id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<OrderResponse> orderUpdates(@PathVariable("order-id") String orderId) {
    return orderQueryService.orderUpdates(orderId);
}复制
```

调用*http://localhost:8080/order-updates/666a1661-474d-4046-8b12-8b5896312768*将为我们提供该产品的更新流。通过调用*http://localhost:8080/order/666a1661-474d-4046-8b12-8b5896312768/product/a6aa01eb-4e38-4dfb-b53b-b5b82961fbf3dd*，我们触发了更新。该更新作为服务器发送事件发送。

我们将同时看到初始状态和更新后的状态。连接保持打开状态以接收进一步的更新。

```json
data:{"orderId":"666a1661-474d-4046-8b12-8b5896312768","products":{},"orderStatus":"CREATED"}

data:{"orderId":"666a1661-474d-4046-8b12-8b5896312768","products":{"a6aa01eb-4e38-4dfb-b53b-b5b82961fbf3":1},"orderStatus":"CREATED"}复制
```

如我们所见，更新包含我们添加的产品。

## 3. 集成测试

对于集成测试，让我们使用[WebClient](https://www.baeldung.com/spring-5-webclient)。

对于这些测试，我们将使用[***@SpringBootTest***](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)**运行整个应用程序**，首先使用其他 REST 端点更改状态。这些其他 REST 端点触发一个或多个命令来创建一个或多个事件。[要创建订单，我们将使用在Axon 框架指南](https://www.baeldung.com/axon-cqrs-event-sourcing)中添加的端点。我们在每个测试中使用[*@DirtiesContext*](https://www.baeldung.com/spring-dirtiescontext)注释，因此在一个测试中创建的事件不会影响另一个。

我们在*src/test/resources的**application.properties*中设置*axon.axonserver.enabled=false*，而不是在集成测试期间运行 Axon Server 。这样，我们将使用非分布式网关，它运行速度更快并且不需要 Axon 服务器。网关是处理三种不同类型消息的实例。

我们可以创建一些辅助方法来使我们的测试更具可读性。这些辅助函数提供正确的类型并在需要时设置 HTTP 标头。例如：

```java
private void verifyVoidPost(WebClient client, String uri) {
    StepVerifier.create(retrieveResponse(client.post()
      .uri(uri)))
      .verifyComplete();
}复制
```

这对于调用具有 void 返回类型的 post 端点很有用。它将使用*retrieveResponse()* 辅助函数来执行调用并验证它是否完成。这些东西经常使用，需要几行代码。我们通过将测试放在辅助函数中来使测试更具可读性和可维护性。

### 3.1. 测试点对点查询

为了测试*/all-orders* REST 端点，让我们**创建一个订单，然后验证我们是否可以检索创建的订单**。为了能够做到这一点，我们首先需要创建一个*WebClient*。Web 客户端是一个反应式实例，我们可以使用它来进行 HTTP 调用。调用创建订单后，我们获取所有订单并验证结果：

```java
WebClient client = WebClient.builder()
  .clientConnector(httpConnector())
  .build();
createRandomNewOrder(client);
StepVerifier.create(retrieveListResponse(client.get()
    .uri("http://localhost:" + port + "/all-orders")))
  .expectNextMatches(list -> 1 == list.size() && list.get(0)
    .getOrderStatus() == OrderStatusResponse.CREATED)
  .verifyComplete();复制
```

由于它是反应式的，我们可以使用[reactor-test](https://search.maven.org/search?q=g:io.projectreactor a:reactor-test)中的*StepVerifier*来验证响应。

我们希望列表中只有一个*订单，即我们刚刚创建的订单。*此外，我们希望*订单*具有 CREATED 订单状态。

### 3.2. 测试流式查询

流式查询可能会返回多个订单。**我们还想测试流是否完成**。为了测试，我们将创建三个新的随机订单，然后测试流式查询响应：

```java
WebClient client = WebClient.builder()
  .clientConnector(httpConnector())
  .build();
for (int i = 0; i < 3; i++) {
    createRandomNewOrder(client);
}
StepVerifier.create(retrieveStreamingResponse(client.get()
    .uri("http://localhost:" + port + "/all-orders-streaming")))
  .expectNextMatches(o -> o.getOrderStatus() == OrderStatusResponse.CREATED)
  .expectNextMatches(o -> o.getOrderStatus() == OrderStatusResponse.CREATED)
  .expectNextMatches(o -> o.getOrderStatus() == OrderStatusResponse.CREATED)
  .verifyComplete();复制
```

通过最后的*verifyComplete() ，我们确保流已关闭。*我们应该注意，有可能以不完成的方式实现流式查询。在这种情况下，确实如此，验证它很重要。

### 3.3. 测试分散-聚集查询

要测试分散-聚集查询，我们需要确保组合多个处理程序的结果。我们使用端点运送一把椅子。然后我们取回所有运送的椅子。**由于\*LegacyQueryHandler\*为椅子返回 234，因此结果应为 235**。

```java
WebClient client = WebClient.builder()
  .clientConnector(httpConnector())
  .build();
verifyVoidPost(client, "http://localhost:" + port + "/ship-order");
StepVerifier.create(retrieveIntegerResponse(client.get()
    .uri("http://localhost:" + port + "/total-shipped/Deluxe Chair")))
  .assertNext(r -> assertEquals(235, r))
  .verifyComplete();复制
```

retrieveIntegerResponse *()*辅助函数从响应主体返回一个整数。

### 3.4. 测试订阅查询

只要我们不关闭连接，订阅查询就会保持活动状态。**我们想测试初始结果和更新。**因此，我们使用[*ScheduledExecutorService*](https://www.baeldung.com/java-executor-service-tutorial#ScheduledExecutorService)以便我们可以在测试中使用多个线程。*该服务允许从一个线程*更新订单，同时验证另一个线程中返回的订单。为了使其更具可读性，我们使用不同的方法进行更新：

```java
private void addIncrementDecrementConfirmAndShipProduct(String orderId, String productId) {
    WebClient client = WebClient.builder()
      .clientConnector(httpConnector())
      .build();
    String base = "http://localhost:" + port + "/order/" + orderId;
    verifyVoidPost(client, base + "/product/" + productId);
    verifyVoidPost(client, base + "/product/" + productId + "/increment");
    // and some more
}复制
```

该方法创建并使用自己的 Web 客户端实例，以不干扰用于验证响应的实例。

实际测试将从[执行程序](https://www.baeldung.com/java-executor-service-tutorial#ScheduledExecutorService)调用它 并验证更新：

```java
//Create two webclients, creating the id's for the test, and create an order.
ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
executor.schedule(() -> addIncrementDecrementConfirmAndShipProduct(orderId, productId), 1L, TimeUnit.SECONDS);
try {
    StepVerifier.create(retrieveStreamingResponse(receiverClient.get()
      .uri("http://localhost:" + port + "/order-updates/" + orderId)))
      .assertNext(p -> assertTrue(p.getProducts().isEmpty()))
      //Some more assertions.
      .assertNext(p -> assertEquals(OrderStatusResponse.SHIPPED, p.getOrderStatus()))
      .thenCancel()
      .verify();
} finally {
    executor.shutdown();
}复制
```

我们应该注意，我们在更新前等待一秒钟，以确保我们不会错过第一次更新。我们使用随机*UUID*来生成*productId*，它用于更新和验证结果。每次更改都应触发更新。

根据更新后的预期状态，我们添加一个断言。我们需要调用*thenCancel()* 来结束测试，因为订阅将在没有它的情况下保持打开状态。*finally*块用于确保我们始终关闭执行程序。

## 4。结论

在本文中，我们学习了如何为查询添加 REST 端点。这些可用于构建 UI。

我们还学习了如何使用*WebClient*测试这些端点。

所有这些示例和代码片段的实现都可以 [在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/axon)找到。

有关此主题的任何其他问题，另请查看 [讨论 AxonIQ](https://discuss.axoniq.io/)。
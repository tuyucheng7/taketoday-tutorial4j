## 1. 概述

Axon Framework 帮助我们构建事件驱动的微服务系统。在[Axon 框架指南 中](https://www.baeldung.com/axon-cqrs-event-sourcing)，我们通过一个简单的 Axon [Spring Boot](https://www.baeldung.com/spring-boot-start)应用程序了解了 Axon，该应用程序包括构建一个示例订单模型供我们更新和查询。那篇文章使用了一个简单的点对点查询。

在本教程中，我们将在上述示例的基础上检查我们可以在 Axon 中分派查询的所有方式。除了更深入地了解点对点查询之外，我们还将了解流式查询、分散-聚集查询和订阅查询。

## 2.查询调度

当我们向 Axon 提交查询时，框架将向所有能够回答我们查询的已注册查询处理程序发出该查询。在分布式系统中，有可能多个节点可以支持同一种查询，也有可能单个节点有多个查询处理程序可以支持同一种查询。

那么，Axon 如何决定将哪些结果包含在其响应中？答案取决于我们如何发送查询。Axon 给了我们三个选择：

-   点对点查询从任何支持我们查询的节点获得完整的答案
-   流式查询从支持我们查询的任何节点获取答案流
-   分散聚集查询从支持我们查询的所有节点获得完整答案
-   订阅查询获得到目前为止的答案，然后继续侦听任何更新

在接下来的部分中，我们将学习如何支持和分派各种查询。

## 3.点对点查询

通过点对点查询，Axon 向支持查询的每个节点发出查询。Axon 假定任何节点都能够对点对点查询给出完整的答案，并且它只会返回从第一个响应节点获得的结果。

在本节中，我们将使用点对点查询来获取系统中所有当前的Order。 

### 3.1. 定义查询

Axon 使用强类型类来表示查询类型并封装查询参数。在本例中，由于我们要查询所有订单，因此不需要任何查询参数。因此，我们可以用一个空类来表示我们的查询：

```java
public class FindAllOrderedProductsQuery {}
```

### 3.2. 定义查询处理程序

我们可以使用@QueryHandler注解注册查询处理程序。

让我们为我们的查询处理程序创建一个类，并添加一个可以支持FindAllOrderedProductsQuery查询的处理程序：

```java
@Service
public class InMemoryOrdersEventHandler implements OrdersEventHandler {
    private final Map<String, Order> orders = new HashMap<>();

    @QueryHandler
    public List<Order> handle(FindAllOrderedProductsQuery query) {
        return new ArrayList<>(orders.values());
    }
}
```

在上面的示例中，我们将handle()注册为 Axon 查询处理程序：

1.  能够响应FindAllOrderedProductsQuery 查询
2.  返回订单列表。 _ 正如我们稍后将看到的，Axon 在决定哪个查询处理程序可以响应给定查询时会考虑返回类型。这样可以更轻松地逐步迁移到新的 API。

我们使用上面的OrdersEventHandler接口，以便我们稍后可以交换使用持久数据存储的实现，例如 MongoDB。对于本教程，我们将通过将Order对象存储在内存中的Map中来使事情变得简单。因此，我们的查询处理程序只需要将Order对象作为List返回。

### 3.3. 调度点对点查询

现在我们已经定义了查询类型和查询处理程序，我们准备将FindAllOrderedProductsQuery分派给 Axon。让我们使用发出点对点FindAllOrderedProductsQuery的方法创建一个服务类：

```java
@Service
public class OrderQueryService {
    private final QueryGateway queryGateway;

    public OrderQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    public CompletableFuture<List<OrderResponse>> findAllOrders() {
        return queryGateway.query(new FindAllOrderedProductsQuery(),
            ResponseTypes.multipleInstancesOf(Order.class))
          .thenApply(r -> r.stream()
            .map(OrderResponse::new)
            .collect(Collectors.toList()));
    }
}
```

在上面的示例中，我们使用 Axon 的QueryGateway来分派 FindAllOrderedProductsQuery 的实例。我们使用网关的query()方法发出点对点查询。因为我们指定了 ResponseTypes.multipleInstancesOf(Order.class)，所以 Axon 知道我们只想与返回类型为Order对象集合的查询处理程序交谈。

最后，为了在Order模型类和外部客户端之间添加一个间接层，我们将结果包装在OrderResponse对象中。

### 3.4. 测试我们的点对点查询

我们将使用[@SpringBootTest](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)来测试我们使用 Axon 集成的查询。让我们首先将[spring-test](https://search.maven.org/search?q=g:org.springframework a:spring-test)依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <scope>test</scope>
</dependency>

```

接下来，让我们添加一个测试来调用我们的服务方法来检索订单：

```java
@SpringBootTest(classes = OrderApplication.class)
class OrderQueryServiceIntegrationTest {

    @Autowired
    OrderQueryService queryService;

    @Autowired
    OrdersEventHandler handler;

    private String orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId);
        handler.reset(Collections.singletonList(order));
    }

    @Test
    void givenOrderCreatedEventSend_whenCallingAllOrders_thenOneCreatedOrderIsReturned()
            throws ExecutionException, InterruptedException {
        List<OrderResponse> result = queryService.findAllOrders().get();
        assertEquals(1, result.size());
        OrderResponse response = result.get(0);
        assertEquals(orderId, response.getOrderId());
        assertEquals(OrderStatusResponse.CREATED, response.getOrderStatus());
        assertTrue(response.getProducts().isEmpty());
    }
}
```

在我们上面的@BeforeEach方法中，我们调用reset()，这是OrdersEventHandler中的一个便捷方法，用于从遗留系统预加载Order对象，或帮助促进迁移。在这里，我们使用它将订单预加载到我们的内存存储中以进行测试。

然后我们调用我们的服务方法并验证它在将我们的查询分派给我们之前设置的查询处理程序后是否已经检索到我们的测试订单。

## 4. 流式查询

通过流式查询，我们可以将大型集合作为流发送。

不必像点对点查询那样等到整个结果在查询处理程序端完成，结果可以分段返回。与订阅查询不同，流式查询预计会在某个时间点完成。

通过依赖[Reactor 项目](https://www.baeldung.com/reactor-core)，流式查询受益于处理大量结果的背压等特性。如果我们还没有[reactor-core](https://search.maven.org/search?q=g:io.projectreactor a:reactor-core)依赖项，我们需要添加它才能使用流式查询。

### 4.1. 定义查询

我们将重用点对点查询中的查询。

### 4.2. 定义查询处理程序

流式查询应该返回Publisher。我们可以使用 Reactor从内存映射的值创建一个 Mono ：

```java
@QueryHandler
public Publisher<Order> handleStreaming(FindAllOrderedProductsQuery query) {
    return Mono.fromCallable(orders::values).flatMapMany(Flux::fromIterable);
}
```

我们使用 flatMapMany()将Mono转换为 Publisher。

### 4.3. 调度流式查询

添加到OrderQueryService的方法与点对点查询非常相似。我们确实给了它一个不同的方法名称，所以区别很明显：

```java
public Flux<OrderResponse> allOrdersStreaming() {
    Publisher<Order> publisher = queryGateway.streamingQuery(new FindAllOrderedProductsQuery(), Order.class);
    return Flux.from(publisher).map(OrderResponse::new);
}
```

### 4.4. 测试我们的流式查询

让我们为此添加一个测试到我们的OrderQueryServiceIntegrationTest：

```java
@Test
void givenOrderCreatedEventSend_whenCallingAllOrdersStreaming_thenOneOrderIsReturned() {
    Flux<OrderResponse> result = queryService.allOrdersStreaming();
    StepVerifier.create(result)
      .assertNext(order -> assertEquals(orderId, order.getOrderId()))
      .expectComplete()
      .verify();
}
```

我们应该注意，使用expectComplete()， 我们验证流确实已完成。

## 5. 分散-聚集查询

分散-聚集查询被分派到支持该查询的所有节点中的所有查询处理程序。对于这些查询，来自每个查询处理程序的结果被组合成一个响应。如果两个节点具有相同的 Spring 应用程序名称，Axon 认为它们是等价的，并且只会使用第一个响应的节点的结果。

在本节中，我们将创建一个查询来检索与给定产品 ID 匹配的已发货产品总数。我们将模拟查询实时系统和遗留系统，以展示 Axon 将结合来自两个系统的响应。

### 5.1. 定义查询

与我们的点对点查询不同，这次我们需要提供一个参数：产品 ID。我们将使用我们的产品 ID 参数创建一个[POJO](https://www.baeldung.com/java-pojo-class) ，而不是一个空类：

```java
public class TotalProductsShippedQuery {
    private final String productId;

    public TotalProductsShippedQuery(String productId) {
        this.productId = productId;
    }

    // getter
}
```

### 5.2. 定义查询处理程序

首先，我们将查询基于事件的系统，正如我们记得的那样，它使用内存数据存储。让我们向现有的InMemoryOrdersEventHandler添加一个查询处理程序，以获取已发货产品的总数：

```java
@QueryHandler
public Integer handle(TotalProductsShippedQuery query) {
    return orders.values().stream()
      .filter(o -> o.getOrderStatus() == OrderStatus.SHIPPED)
      .map(o -> Optional.ofNullable(o.getProducts().get(query.getProductId())).orElse(0))
      .reduce(0, Integer::sum);
}
```

上面，我们检索所有内存中的 Order对象并删除所有未发货的对象。然后我们在每个 订单上调用getProducts()以获取其产品 ID 与我们的查询参数匹配的已发货产品的数量。然后我们将这些数字相加以获得我们的已发货产品总数。

由于我们想将这些结果与我们假设的遗留系统中的数字结合起来，让我们用一个单独的类和查询处理程序来模拟遗留数据：

```java
@Service
public class LegacyQueryHandler {
    @QueryHandler
    public Integer handle(TotalProductsShippedQuery query) {
        switch (query.getProductId()) {
        case "Deluxe Chair":
            return 234;
        case "a6aa01eb-4e38-4dfb-b53b-b5b82961fbf3":
            return 10;
        default:
            return 0;
        }
    }
}
```

为了本教程，此查询处理程序与我们的InMemoryOrdersEventHandler处理程序存在于同一个 Spring 应用程序中。在现实生活中，我们可能不会在同一应用程序中为同一查询类型设置多个查询处理程序。分散-聚集查询通常组合来自多个 Spring 应用程序的结果，每个应用程序都有一个处理程序。

### 5.3. 调度分散聚集查询

让我们向我们的OrderQueryService添加一个新方法来发送分散-收集查询：

```java
public Integer totalShipped(String productId) {
    return queryGateway.scatterGather(new TotalProductsShippedQuery(productId),
        ResponseTypes.instanceOf(Integer.class), 10L, TimeUnit.SECONDS)
      .reduce(0, Integer::sum);
}
```

这一次，我们使用productId参数构造我们的查询对象。我们还为scatterGather()调用设置了 10 秒超时。Axon 只会响应它可以在该时间窗口内检索到的结果。如果一个或多个处理程序未在该窗口内响应，则它们的结果将不会包含在queryGateway的响应中。

### 5.4. 测试我们的分散-聚集查询

让我们为此添加一个测试到我们的OrderQueryServiceIntegrationTest：

```java
void givenThreeDeluxeChairsShipped_whenCallingAllShippedChairs_then234PlusTreeIsReturned() {
    Order order = new Order(orderId);
    order.getProducts().put("Deluxe Chair", 3);
    order.setOrderShipped();
    handler.reset(Collections.singletonList(order));

    assertEquals(237, queryService.totalShipped("Deluxe Chair"));
}
```

上面，我们使用我们的reset()方法在我们的事件驱动系统中模拟三个订单。以前，在我们的LegacyQueryHandler中，我们在遗留系统中硬编码了 234 把运送的豪华椅子。因此，我们的测试应该总共产生 237 把豪华椅子。

## 6.订阅查询

通过订阅查询，我们得到一个初始结果，然后是一系列更新。在本节中，我们将查询我们的系统以获取 当前状态的订单，但随后保持与 Axon 的连接，以便在该订单发生时获得任何新的更新。

### 6.1. 定义查询

由于我们想要检索特定订单，因此让我们创建一个包含订单 ID 作为其唯一参数的查询类：

```java
public class OrderUpdatesQuery {
    private final String orderId;

    public OrderUpdatesQuery(String orderId) {
        this.orderId = orderId;
    }

    // getter
}
```

### 6.2. 定义查询处理程序

从我们的内存映射中检索订单的查询处理程序非常简单。让我们将它添加到我们的InMemoryOrdersEventHandler类中：

```java
@QueryHandler
public Order handle(OrderUpdatesQuery query) {
    return orders.get(query.getOrderId());
}
```

### 6.3. 发出查询更新

订阅查询仅在有更新时才有意义。Axon Framework 提供了一个QueryUpdateEmitter类，我们可以使用它来通知 Axon 如何以及何时更新订阅。让我们将该发射器注入我们的InMemoryOrdersEventHandler类，并在一个方便的方法中使用它：

```java
@Service
public class InMemoryOrdersEventHandler implements OrdersEventHandler {

    private final QueryUpdateEmitter emitter;

    public InMemoryOrdersEventHandler(QueryUpdateEmitter emitter) {
        this.emitter = emitter;
    }

    private void emitUpdate(Order order) {
        emitter.emit(OrderUpdatesQuery.class, q -> order.getOrderId()
          .equals(q.getOrderId()), order);
    }

    // our event and query handlers
}
```

我们的emitter.emit()调用告诉 Axon，任何订阅了 OrderUpdatesQuery的客户端可能需要接收更新。第二个参数是一个过滤器，它告诉 Axon 只有与提供的订单 ID 匹配的订阅才能获得更新。

我们现在可以 在任何修改订单的事件处理程序中使用我们的emitUpdate()方法。例如，如果订单已发货，则应通知任何对该订单更新的有效订阅。[让我们为上一篇文章](https://www.baeldung.com/axon-cqrs-event-sourcing)中介绍的OrderShippedEvent创建一个事件处理程序， 并让它对已发货的订单发出更新：

```java
@Service
public class InMemoryOrdersEventHandler implements OrdersEventHandler {
    @EventHandler
    public void on(OrderShippedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.setOrderShipped();
            emitUpdate(order);
            return order;
        });
    }

    // fields, query handlers, other event handlers, and our emitUpdate() method
}
```

我们可以为我们的ProductAddedEvent、 ProductCountIncrementedEvent、 ProductCountDecrementedEvent和 OrderConfirmedEvent事件做同样的事情。

### 6.4. 订阅查询

接下来，我们将构建一个用于订阅查询的服务方法。我们将使用来自[Reactor Core的](https://www.baeldung.com/reactor-core)Flux来将更新流式传输到客户端代码。

让我们将该[依赖](https://search.maven.org/search?q=g:io.projectreactor a:reactor-core)项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
```

现在，让我们将我们的服务方法实现添加到OrderQueryService：

```java
public class OrderQueryService {
    public Flux<OrderResponse> orderUpdates(String orderId) {
        return subscriptionQuery(new OrderUpdatesQuery(orderId), ResponseTypes.instanceOf(Order.class))
                .map(OrderResponse::new);
    }

    private <Q, R> Flux<R> subscriptionQuery(Q query, ResponseType<R> resultType) {
        SubscriptionQueryResult<R, R> result = queryGateway.subscriptionQuery(query,
          resultType, resultType);
        return result.initialResult()
          .concatWith(result.updates())
          .doFinally(signal -> result.close());
    }

    // our other service methods
}
```

上面的公共orderUpdates()方法将其大部分工作委托给我们的私有便捷方法subscriptionQuery()，尽管我们再次将我们的响应打包为 OrderResponse对象，因此我们没有公开我们的内部Order对象。

我们的广义subscriptionQuery()便捷方法是我们将从 Axon 获得的初始结果与任何未来更新相结合的地方。

首先，我们调用 Axon 的queryGateway.subscriptionQuery()来获取SubscriptionQueryResult对象。我们向queryGateway提供resultType 。subscriptionQuery()两次，因为我们总是期待一个 Order对象，但如果我们愿意，我们可以使用不同的类型进行更新。

接下来，我们使用result.getInitialResult()和result.getUpdates()来获取完成订阅所需的所有信息。

最后，我们关闭流。

虽然我们在这里不使用它，但 Axon Framework 也有一个[Reactive 扩展](https://github.com/AxonFramework/extension-reactor)，它提供了一个替代查询网关，可以更轻松地处理订阅查询。

### 6.5. 测试我们的订阅查询

为了帮助我们测试返回Flux的服务方法，我们将使用从[reactor-test](https://search.maven.org/search?q=g:io.projectreactor a:reactor-test)依赖项中获取的StepVerifier类：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
```

让我们添加我们的测试：

```java
class OrderQueryServiceIntegrationTest {
    @Test
    void givenOrdersAreUpdated_whenCallingOrderUpdates_thenUpdatesReturned() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(this::addIncrementDecrementConfirmAndShip, 100L, TimeUnit.MILLISECONDS);
        try {
            StepVerifier.create(queryService.orderUpdates(orderId))
              .assertNext(order -> assertTrue(order.getProducts().isEmpty()))
              .assertNext(order -> assertEquals(1, order.getProducts().get(productId)))
              .assertNext(order -> assertEquals(2, order.getProducts().get(productId)))
              .assertNext(order -> assertEquals(1, order.getProducts().get(productId)))
              .assertNext(order -> assertEquals(OrderStatusResponse.CONFIRMED, order.getOrderStatus()))
              .assertNext(order -> assertEquals(OrderStatusResponse.SHIPPED, order.getOrderStatus()))
              .thenCancel()
              .verify();
        } finally {
            executor.shutdown();
        }
    }

    private void addIncrementDecrementConfirmAndShip() {
        sendProductAddedEvent();
        sendProductCountIncrementEvent();
        sendProductCountDecrement();
        sendOrderConfirmedEvent();
        sendOrderShippedEvent();
    }

    private void sendProductAddedEvent() {
        ProductAddedEvent event = new ProductAddedEvent(orderId, productId);
        eventGateway.publish(event);
    }

    private void sendProductCountIncrementEvent() {
        ProductCountIncrementedEvent event = new ProductCountIncrementedEvent(orderId, productId);
        eventGateway.publish(event);
    }

    private void sendProductCountDecrement() {
        ProductCountDecrementedEvent event = new ProductCountDecrementedEvent(orderId, productId);
        eventGateway.publish(event);
    }

    private void sendOrderConfirmedEvent() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(orderId);
        eventGateway.publish(event);
    }

    private void sendOrderShippedEvent() {
        OrderShippedEvent event = new OrderShippedEvent(orderId);
        eventGateway.publish(event);
    }

    // our other tests
}
```

上面，我们有一个私有的 addIncrementDecrementConfirmAndShip()方法，它向 Axon 发布五个与Order相关的事件。[我们在测试开始后 100 毫秒通过ScheduledExecutorService](https://www.baeldung.com/java-executor-service-tutorial#ScheduledExecutorService)在单独的线程中调用它， 以模拟在我们开始OrderUpdatesQuery订阅后进入的事件。

在我们的主线程中，我们调用我们正在测试的 orderUpdates()查询，使用StepVerifier 允许我们对从订阅接收到的每个离散更新进行断言。

## 七. 总结

在本文中，我们探讨了在 Axon 框架内分派查询的三种方法：点对点查询、分散-聚集查询和订阅查询。
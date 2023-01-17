## 1. 概述

在本文中，我们将研究[Axon](https://axoniq.io/product-overview/axon-framework)以及它如何帮助我们在实现应用程序时考虑到[CQRS](https://martinfowler.com/bliki/CQRS.html)(命令查询责任分离)和[事件](https://martinfowler.com/eaaDev/EventSourcing.html)溯源。

在本指南中，将使用 Axon Framework 和[Axon Server](https://axoniq.io/product-overview/axon-server)。前者将包含我们的实施，后者将是我们专用的事件存储和消息路由解决方案。

我们将构建的示例应用程序侧重于Order域。为此，我们将利用 Axon 为我们提供的 CQRS 和事件溯源构建块。

请注意，许多共享概念直接来自[DDD](https://en.wikipedia.org/wiki/Domain-driven_design)，这超出了当前文章的范围。

## 2.Maven依赖

我们将创建一个 Axon /Spring Boot应用程序。因此，我们需要将最新的[axon-spring-boot-starter](https://search.maven.org/search?q=a:axon-spring-boot-starter)依赖项添加到我们的pom.xml，以及用于测试的[axon-test](https://search.maven.org/search?q=a:axon-test)依赖项。
要使用匹配的版本，我们将在依赖管理部分使用[axon-bom ：](https://search.maven.org/search?q=a:axon-bom)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.axonframework</groupId>
            <artifactId>axon-bom</artifactId>
            <version>4.5.13</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.axonframework</groupId>
        <artifactId>axon-spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>org.axonframework</groupId>
        <artifactId>axon-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 3. 轴突服务器

我们将使用[Axon Server](https://axoniq.io/product-overview/axon-server)作为我们的[事件存储](https://en.wikipedia.org/wiki/Event_store)和我们专用的命令、事件和查询路由解决方案。

作为事件存储，它为我们提供了存储事件所需的理想特性。[本文](https://axoniq.io/blog-overview/eventstore)提供了为什么这是可取的背景。

作为消息路由解决方案，它使我们可以选择将多个实例连接在一起，而无需专注于配置 RabbitMQ 或 Kafka 主题之类的东西来共享和分发消息。

可以在[此处](https://download.axoniq.io/axonserver/AxonServer.zip)下载 Axon 服务器。由于它是一个简单的 JAR 文件，因此以下操作足以启动它：

```bash
java -jar axonserver.jar
```

这将启动可通过[localhost:8024](http://localhost:8024/)访问的单个 Axon 服务器实例。该端点提供了连接的应用程序及其可以处理的消息的概述，以及对 Axon 服务器中包含的事件存储的查询机制。

Axon 服务器的默认配置以及axon-spring-boot-starter依赖项将确保我们的订单服务将自动连接到它。

## 4. 订单服务 API – 命令

我们将在考虑 CQRS 的情况下设置我们的订单服务。因此，我们将强调流经我们应用程序的消息。

首先，我们将定义命令，即意图的表达。订单服务能够处理三种不同类型的操作：

1.  创建新订单
2.  确认订单
3.  运送订单

自然地，我们的域可以处理三个命令消息 ——CreateOrderCommand、 ConfirmOrderCommand和ShipOrderCommand：

```java
public class CreateOrderCommand {
 
    @TargetAggregateIdentifier
    private final String orderId;
    private final String productId;
 
    // constructor, getters, equals/hashCode and toString 
}
public class ConfirmOrderCommand {
 
    @TargetAggregateIdentifier
    private final String orderId;
    
    // constructor, getters, equals/hashCode and toString
}
public class ShipOrderCommand {
 
    @TargetAggregateIdentifier
    private final String orderId;
 
    // constructor, getters, equals/hashCode and toString
}
```

TargetAggregateIdentifier注解告诉 Axon，[带](https://apidocs.axoniq.io/4.0/org/axonframework/modelling/command/TargetAggregateIdentifier.html)注解的字段是命令应定位到的给定聚合的 id。 我们将在本文后面简要介绍聚合。

另请注意，我们将命令中的字段标记为 最终字段。 这是有意为之的，因为它是任何消息实现不可变的最佳实践。

## 5. 订单服务API – 事件

我们的聚合将处理命令，因为它负责决定是否可以创建、确认或发货订单。

它将通过发布事件通知其余应用程序其决定。我们将拥有三种类型的事件 — OrderCreatedEvent、OrderConfirmedEvent和OrderShippedEvent：

```java
public class OrderCreatedEvent {
 
    private final String orderId;
    private final String productId;
 
    // default constructor, getters, equals/hashCode and toString
}
public class OrderConfirmedEvent {
 
    private final String orderId;
 
    // default constructor, getters, equals/hashCode and toString
}
public class OrderShippedEvent { 

    private final String orderId; 

    // default constructor, getters, equals/hashCode and toString 
}
```

## 6. 命令模型——订单聚合

现在我们已经根据命令和事件对核心 API 进行了建模，我们可以开始创建命令模型了。

[聚合](https://www.martinfowler.com/bliki/DDD_Aggregate.html)是命令模型中的常规组件，源于 DDD。其他框架也使用这个概念，例如在[这篇](https://www.baeldung.com/spring-persisting-ddd-aggregates#introduction-to-aggregates)关于使用 Spring 持久化 DDD 聚合的文章中可以看到。

由于我们的领域专注于处理订单，因此 我们将创建一个OrderAggregate作为我们的命令模型的中心。

### 6.1. 聚合类

因此，让我们创建我们的基本聚合类：

```java
@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private boolean orderConfirmed;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        AggregateLifecycle.apply(new OrderCreatedEvent(command.getOrderId(), command.getProductId()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        orderConfirmed = false;
    }

    protected OrderAggregate() { }
}
```

[Aggregate](https://docs.axoniq.io/reference-guide/v/4.0/implementing-domain-logic/command-handling/aggregate)注解是 Axon Spring 特定的注解，将此类标记为聚合。它将通知框架需要为此OrderAggregate实例化所需的 CQRS 和事件源特定构建块。

由于聚合将处理针对特定聚合实例的命令，因此我们需要使用[AggregateIdentifier](https://apidocs.axoniq.io/4.0/org/axonframework/modelling/command/AggregateIdentifier.html)注解指定标识符。

我们的聚合将在处理OrderAggregate “命令处理构造函数”中的CreateOrderCommand后开始其生命周期。为了告诉框架给定的函数能够处理命令，我们将添加[CommandHandler](https://apidocs.axoniq.io/3.1/org/axonframework/commandhandling/CommandHandler.html)注解。

在处理CreateOrderCommand时，它将通过发布OrderCreatedEvent 通知应用程序的其余部分已创建订单。要从聚合中发布事件，我们将使用 [AggregateLifecycle#apply(Object…)](https://apidocs.axoniq.io/4.0/org/axonframework/modelling/command/AggregateLifecycle.html)。

从这一点开始，我们实际上可以开始将事件溯源作为从事件流中重新创建聚合实例的驱动力。

我们从“聚合创建事件” OrderCreatedEvent开始，它在[EventSourcingHandler](https://apidocs.axoniq.io/4.0/org/axonframework/eventsourcing/EventSourcingHandler.html)注解函数中处理，以设置 Order 聚合的orderId和orderConfirmed状态。

另请注意，为了能够根据其事件获取聚合，Axon 需要一个默认构造函数。

### 6.2. 聚合命令处理程序

现在我们有了基本的聚合，我们可以开始实现剩余的命令处理程序：

```java
@CommandHandler 
public void handle(ConfirmOrderCommand command) { 
    if (orderConfirmed) {
        return;
    }
    apply(new OrderConfirmedEvent(orderId)); 
} 

@CommandHandler 
public void handle(ShipOrderCommand command) { 
    if (!orderConfirmed) { 
        throw new UnconfirmedOrderException(); 
    } 
    apply(new OrderShippedEvent(orderId)); 
} 

@EventSourcingHandler 
public void on(OrderConfirmedEvent event) { 
    orderConfirmed = true; 
}
```

我们的命令和事件源处理程序的签名只是声明handle({the-command})和on({the-event})以保持简洁的格式。

此外，我们定义了一个订单只能确认一次并在确认后发货。因此，我们将忽略前者中的命令，如果后者不是这种情况，则抛出UnconfirmedOrderException 。

这举例说明了OrderConfirmedEvent采购处理程序需要将 Order 聚合的orderConfirmed状态更新为true。

## 7. 测试命令模型

首先，我们需要通过为OrderAggregate创建一个[FixtureConfiguration](https://apidocs.axoniq.io/3.3/org/axonframework/test/aggregate/FixtureConfiguration.html) 来设置我们的测试：

```java
private FixtureConfiguration<OrderAggregate> fixture;

@Before
public void setUp() {
    fixture = new AggregateTestFixture<>(OrderAggregate.class);
}
```

第一个测试用例应该涵盖最简单的情况。当聚合处理 CreateOrderCommand时，它应该产生一个 OrderCreatedEvent：

```java
String orderId = UUID.randomUUID().toString();
String productId = "Deluxe Chair";
fixture.givenNoPriorActivity()
  .when(new CreateOrderCommand(orderId, productId))
  .expectEvents(new OrderCreatedEvent(orderId, productId));
```

接下来，我们可以测试订单确认后才能发货的决策逻辑。因此，我们有两种情况——一种是我们期望异常，另一种是我们期望 OrderShippedEvent。

让我们看一下第一种情况，我们期望出现异常：

```java
String orderId = UUID.randomUUID().toString();
String productId = "Deluxe Chair";
fixture.given(new OrderCreatedEvent(orderId, productId))
  .when(new ShipOrderCommand(orderId))
  .expectException(UnconfirmedOrderException.class);

```

现在是第二种情况，我们期待一个OrderShippedEvent：

```java
String orderId = UUID.randomUUID().toString();
String productId = "Deluxe Chair";
fixture.given(new OrderCreatedEvent(orderId, productId), new OrderConfirmedEvent(orderId))
  .when(new ShipOrderCommand(orderId))
  .expectEvents(new OrderShippedEvent(orderId));
```

## 8. 查询模型——事件处理器

到目前为止，我们已经使用命令和事件建立了核心 API，并且我们已经准备好 CQRS 订单服务的命令模型OrderAggregate。

接下来， 我们可以开始考虑我们的应用程序应该服务的查询模型之一。

这些模型之一是Order：

```java
public class Order {

    private final String orderId;
    private final String productId;
    private OrderStatus orderStatus;

    public Order(String orderId, String productId) {
        this.orderId = orderId;
        this.productId = productId;
        orderStatus = OrderStatus.CREATED;
    }

    public void setOrderConfirmed() {
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void setOrderShipped() {
        this.orderStatus = OrderStatus.SHIPPED;
    }

    // getters, equals/hashCode and toString functions
}
public enum OrderStatus {
    CREATED, CONFIRMED, SHIPPED
}
```

我们将根据通过我们系统传播的事件更新此模型。一个用于更新模型的 Spring服务bean 就可以解决问题：

```java
@Service
public class OrdersEventHandler {

    private final Map<String, Order> orders = new HashMap<>();

    @EventHandler
    public void on(OrderCreatedEvent event) {
        String orderId = event.getOrderId();
        orders.put(orderId, new Order(orderId, event.getProductId()));
    }

    // Event Handlers for OrderConfirmedEvent and OrderShippedEvent...
}
```

当我们使用axon-spring-boot-starter依赖项来启动我们的 Axon 应用程序时，框架将自动扫描所有 bean 以查找现有的消息处理函数。

由于 OrdersEventHandler具有[EventHandler](https://docs.axoniq.io/reference-guide/axon-framework/events/event-handlers)注解函数来存储Order 并对其进行更新，因此该 bean 将由框架注册为一个类，该类应该接收事件，而无需我们进行任何配置。

## 9. 查询模型——查询处理程序

接下来，要查询此模型以检索所有订单，我们应该首先向我们的核心 API 引入查询消息：

```java
public class FindAllOrderedProductsQuery { }
```

其次，我们必须更新OrdersEventHandler才能处理FindAllOrderedProductsQuery：

```java
@QueryHandler
public List<Order> handle(FindAllOrderedProductsQuery query) {
    return new ArrayList<>(orders.values());
}
```

QueryHandler注解函数将处理[FindAllOrderedProductsQuery](https://docs.axoniq.io/reference-guide/axon-framework/queries/query-handlers)并设置为返回List<Order>无论如何，类似于任何“查找所有”查询。

## 10. 把所有东西放在一起

我们用命令、事件和查询充实了我们的核心 API，并通过拥有OrderAggregate和Order模型来设置我们的命令和查询 模型。

接下来是捆绑我们基础设施的松散端。当我们使用axon-spring-boot-starter时，这会自动设置很多必需的配置。

首先，因为我们想为我们的聚合利用事件源，我们需要一个[EventStore](https://docs.axoniq.io/reference-guide/axon-framework/events/event-bus-and-event-store)。我们在第三步启动的 Axon 服务器将填补这个漏洞。


其次，我们需要一种机制来存储我们的订单 查询模型。对于这个例子，我们可以添加[h2](https://search.maven.org/search?q=g:com.h2database AND a:h2)作为内存数据库和[spring-boot-starter-data-jpa](https://search.maven.org/search?q=a:spring-boot-starter-data-jpa AND g:org.springframework.boot)以便于使用：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 10.1. 设置 REST 端点

接下来，我们需要能够访问我们的应用程序，为此我们将通过添加[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web AND g:org.springframework.boot)依赖项来利用 REST 端点：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

从我们的 REST 端点，我们可以开始调度命令和查询：

```java
@RestController
public class OrderRestEndpoint {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    // Autowiring constructor and POST/GET endpoints
}
```

[CommandGateway](https://apidocs.axoniq.io/4.0/org/axonframework/commandhandling/gateway/CommandGateway.html)用作发送命令消息的机制，而QueryGateway[反过来](https://apidocs.axoniq.io/4.1/org/axonframework/queryhandling/QueryGateway.html)发送查询消息。 与它们连接的[CommandBus](https://apidocs.axoniq.io/4.3/org/axonframework/commandhandling/CommandBus.html)和[QueryBus](https://docs.axoniq.io/reference-guide/v/3.3/part-iii-infrastructure-components/query-processing)相比，网关提供了更简单、更直接的 API 。

从这里开始，我们的OrderRestEndpoint应该有一个 POST 端点来创建、确认和发送订单：

```java
@PostMapping("/ship-order")
public CompletableFuture<Void> shipOrder() {
    String orderId = UUID.randomUUID().toString();
    return commandGateway.send(new CreateOrderCommand(orderId, "Deluxe Chair"))
                         .thenCompose(result -> commandGateway.send(new ConfirmOrderCommand(orderId)))
                         .thenCompose(result -> commandGateway.send(new ShipOrderCommand(orderId)));
}
```

这就是我们 CQRS 应用程序的命令端。请注意，网关返回 CompletableFuture，启用异步性。

现在，剩下的就是查询所有订单的 GET 端点：

```java
@GetMapping("/all-orders")
public CompletableFuture<List<Order>> findAllOrders() {
    return queryGateway.query(new FindAllOrderedProductsQuery(), ResponseTypes.multipleInstancesOf(Order.class));
}
```

在 GET 端点中，我们利用QueryGateway来分派点对点查询。为此，我们创建了一个默认的 FindAllOrderedProductsQuery，但我们还需要指定预期的返回类型。

由于我们期望返回多个Order 实例，因此我们利用静态[ResponseTypes#multipleInstancesOf(Class)](https://apidocs.axoniq.io/4.1/org/axonframework/messaging/responsetypes/ResponseTypes.html)函数。这样，我们就为订单服务的查询端提供了一个基本入口。

我们完成了设置，所以现在我们可以在启动OrderApplication后通过我们的 REST 控制器发送一些命令和查询 。

POST 到端点/ship-order将实例化一个将发布事件的OrderAggregate，这反过来将保存/更新我们的订单。来自/all-orders端点的 GET-ing 将发布一条查询消息，该消息将由OrdersEventHandler处理，这将返回所有现有订单。

## 11.总结

在本文中，我们介绍了 Axon 框架作为构建应用程序的强大基础，以利用 CQRS 和事件溯源的优势。

我们使用该框架实现了一个简单的订单服务，以展示在实践中应如何构建此类应用程序。

最后，Axon Server 充当我们的事件存储和消息路由机制，大大简化了基础架构。

所有这些示例和代码片段的实现都可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/axon)。

有关此主题的任何其他问题，另请查看[讨论 AxonIQ](https://discuss.axoniq.io/)。
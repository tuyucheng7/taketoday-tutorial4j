## 1. 概述

在本文中，我们将研究[Axon](https://www.baeldung.com/axon-cqrs-event-sourcing)如何支持具有多个实体的聚合。

[我们认为这篇文章是我们关于Axon](https://www.baeldung.com/axon-cqrs-event-sourcing)的主要指南的扩展。因此，我们将再次使用[Axon Framework](https://axoniq.io/product-overview/axon-framework)和[Axon Server](https://axoniq.io/product-overview/axon-server)。我们将在本文的代码中使用前者，后者是 Event Store 和 Message Router。

由于这是一个扩展，让我们详细介绍一下我们在基础文章中介绍的Order域。

## 2.聚合和实体

Axon 支持的聚合和实体源于[领域驱动设计](https://en.wikipedia.org/wiki/Domain-driven_design)。在深入研究代码之前，让我们首先确定在此上下文中的实体是什么：

-   一个对象不是由其属性从根本上定义的，而是由连续性和同一性的线程定义的

因此，一个实体是可识别的，但不是通过它包含的属性。此外，实体会发生变化，因为它保持了连续性。

知道了这一点，我们可以采取以下步骤，通过分享聚合在这种情况下的含义(摘自[领域驱动设计：解决软件核心的复杂性](https://www.dddcommunity.org/book/evans_2003/))：

-   聚合是一组关联对象，充当数据更改的单个单元
-   有关聚合的引用仅限于单个成员，即聚合根
-   一组一致性规则适用于聚合边界

正如第一点所指出的，聚合不是一个单一的事物，而是一组对象。对象可以是[值对象](https://martinfowler.com/bliki/ValueObject.html)，但更重要的是，它们也可以是实体。Axon 支持将聚合建模为一组相关联的对象，而不是单个对象，正如我们稍后将看到的那样。

## 3. 订单服务API：命令和事件

当我们处理消息驱动的应用程序时，我们首先在扩展聚合以包含多个实体时定义新命令。

我们的Order域当前包含一个OrderAggregate。包含在此聚合中的一个逻辑概念是OrderLine实体。订单行是指正在订购的特定产品，包括产品条目的总数。

了解这一点后，我们可以使用三个额外的操作来扩展命令 API——它由PlaceOrderCommand、ConfirmOrderCommand和ShipOrderCommand组成：

-   添加产品
-   增加订单行的产品数量
-   减少订单行的产品数量

这些操作转换为类AddProductCommand、IncrementProductCountCommand和DecrementProductCountCommand：

```java
public class AddProductCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String productId;

    // default constructor, getters, equals/hashCode and toString
}
 
public class IncrementProductCountCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String productId;

    // default constructor, getters, equals/hashCode and toString
}
 
public class DecrementProductCountCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String productId;

    // default constructor, getters, equals/hashCode and toString
}
```

TargetAggregateIdentifier仍然存在于orderId上，因为[OrderAggregate](https://apidocs.axoniq.io/4.4/org/axonframework/modelling/command/TargetAggregateIdentifier.html)仍然是系统中的聚合。

从定义中记住，实体也有一个身份。这就是为什么productId是命令的一部分。在本文的后面，我们将展示这些字段如何引用一个确切的实体。

事件将作为命令处理的结果发布，通知相关的事情已经发生。因此，事件 API 也应作为新命令 API 的结果进行扩展。

让我们看一下反映连续性增强线程的 POJO — ProductAddedEvent、ProductCountIncrementedEvent、ProductCountDecrementedEvent和ProductRemovedEvent：

```java
public class ProductAddedEvent {

    private final String orderId;
    private final String productId;

    // default constructor, getters, equals/hashCode and toString
}
 
public class ProductCountIncrementedEvent {

    private final String orderId;
    private final String productId;

    // default constructor, getters, equals/hashCode and toString
}
 
public class ProductCountDecrementedEvent {

    private final String orderId;
    private final String productId;

    // default constructor, getters, equals/hashCode and toString
}
 
public class ProductRemovedEvent {

    private final String orderId;
    private final String productId;

    // default constructor, getters, equals/hashCode and toString
}
```

## 4.聚合和实体：实现

新的 API 规定我们可以添加产品并增加或减少其计数。由于每个添加到Order的产品都会发生这种情况，因此我们需要定义允许这些操作 的不同订单行。这表示需要添加作为OrderAggregate一部分的OrderLine实体。 

如果没有指导，Axon 不知道对象是否是聚合中的实体。我们应该将[AggregateMember](https://apidocs.axoniq.io/4.4/org/axonframework/modelling/command/AggregateMember.html)注解放在公开实体的字段或方法上以将其标记为实体。

我们可以将此注解用于单个对象、对象集合和地图。在Order域中，我们最好使用OrderAggregate 上的OrderLine实体的映射。

### 4.1. 汇总调整

了解这一点，让我们增强OrderAggregate：

```java
@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private boolean orderConfirmed;

    @AggregateMember
    private Map<String, OrderLine> orderLines;

    @CommandHandler
    public void handle(AddProductCommand command) {
        if (orderConfirmed) {
            throw new OrderAlreadyConfirmedException(orderId);
        }
        
        String productId = command.getProductId();
        if (orderLines.containsKey(productId)) {
            throw new DuplicateOrderLineException(productId);
        }
        
        AggregateLifecycle.apply(new ProductAddedEvent(orderId, productId));
    }

    // previous command- and event sourcing handlers left out for conciseness

    @EventSourcingHandler
    public void on(OrderPlacedEvent event) {
        this.orderId = event.getOrderId();
        this.orderConfirmed = false;
        this.orderLines = new HashMap<>();
    }

    @EventSourcingHandler
    public void on(ProductAddedEvent event) {
        String productId = event.getProductId();
        this.orderLines.put(productId, new OrderLine(productId));
    }

    @EventSourcingHandler
    public void on(ProductRemovedEvent event) {
        this.orderLines.remove(event.getProductId());
    }
}
```

使用AggregateMember注解标记orderLines字段告诉 Axon 它是域模型的一部分。这样做允许我们在OrderLine对象中添加[CommandHandler](https://apidocs.axoniq.io/4.4/org/axonframework/commandhandling/CommandHandler.html)和[EventSourcingHandler](https://apidocs.axoniq.io/4.4/org/axonframework/eventsourcing/EventSourcingHandler.html)注解方法，就像在 Aggregate 中一样。

由于OrderAggregate包含OrderLine实体，因此它负责添加和删除产品，因此负责相应的OrderLines。该应用程序使用[Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html)，因此有ProductAddedEvent和ProductRemovedEvent [EventSourcingHandler](https://apidocs.axoniq.io/4.4/org/axonframework/eventsourcing/EventSourcingHandler.html)分别添加和删除OrderLine。

OrderAggregate决定何时添加产品或拒绝添加，因为它持有OrderLines 。此所有权指示AddProductCommand命令处理程序位于OrderAggregate中。

通过发布ProductAddedEvent通知添加成功。如果产品已经存在，则抛出DuplicateOrderLineException ，如果OrderAggregate已被确认，则抛出 OrderAlreadyConfirmedException 导致添加不成功。

最后，我们在OrderPlacedEvent处理程序中设置orderLines映射，因为它是OrderAggregate的事件流中的第一个事件。我们可以在OrderAggregate或私有构造函数中全局设置字段，但这意味着状态更改不再是事件源处理程序的唯一域。

### 4.2. 实体介绍

使用更新后的OrderAggregate，我们可以开始查看OrderLine：

```java
public class OrderLine {

    @EntityId
    private final String productId;
    private Integer count;
    private boolean orderConfirmed;

    public OrderLine(String productId) {
        this.productId = productId;
        this.count = 1;
    }

    @CommandHandler
    public void handle(IncrementProductCountCommand command) {
        if (orderConfirmed) {
            throw new OrderAlreadyConfirmedException(orderId);
        }
        
        apply(new ProductCountIncrementedEvent(command.getOrderId(), productId));
    }

    @CommandHandler
    public void handle(DecrementProductCountCommand command) {
        if (orderConfirmed) {
            throw new OrderAlreadyConfirmedException(orderId);
        }
        
        if (count <= 1) {
            apply(new ProductRemovedEvent(command.getOrderId(), productId));
        } else {
            apply(new ProductCountDecrementedEvent(command.getOrderId(), productId));
        }
    }

    @EventSourcingHandler
    public void on(ProductCountIncrementedEvent event) {
        this.count++;
    }

    @EventSourcingHandler
    public void on(ProductCountDecrementedEvent event) {
        this.count--;
    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        this.orderConfirmed = true;
    }
}
```

OrderLine应该是可识别的，如第 2 节中所定义。该实体可通过productId字段进行识别，我们用[EntityId](https://apidocs.axoniq.io/4.4/org/axonframework/modelling/command/EntityId.html)注解对其进行了标记。

使用EntityId注解标记字段告诉 Axon 哪个字段标识聚合内的实体实例。

由于OrderLine反映了正在订购的产品，因此它负责处理IncrementProductCountCommand和DecrementProductCountCommand。我们可以在实体内部使用CommandHandler注解将这些命令直接路由到适当的实体。

由于使用了 Event Sourcing，因此需要根据事件设置OrderLine的状态。OrderLine可以简单地为设置状态所需的事件包含EventSourcingHandler注解，类似于OrderAggregate。

将命令路由到正确的OrderLine实例是通过使用EntityId注解字段完成的。要正确路由，注解字段的名称应与命令中包含的字段之一相同。在此示例中，这反映在命令和实体中的productId字段中。

每当实体存储在集合或映射中时，正确的命令路由都会使EntityId成为硬性要求。如果仅定义聚合成员的单个实例，则此要求放宽为建议。

每当命令中的名称与注解字段不同时，我们应该调整EntityId注解的routingKey值。routingKey值应反映命令上的现有字段，以允许命令路由成功。

让我们通过一个例子来解释它：

```java
public class IncrementProductCountCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String productId;

    // default constructor, getters, equals/hashCode and toString
}
...
public class OrderLine {

    @EntityId(routingKey = "productId")
    private final String orderLineId;
    private Integer count;
    private boolean orderConfirmed;

    // constructor, command and event sourcing handlers
}
```

IncrementProductCountCommand保持不变，包含orderId聚合标识符和productId实体标识符。在OrderLine实体中，标识符现在称为orderLineId。

由于在IncrementProductCountCommand中没有名为orderLineId的字段，这会破坏基于字段名称的自动命令路由。

因此，EntityId注解上的routingKey字段应反映命令中的字段名称以维护此路由能力。 

## 5.总结

在本文中，我们了解了聚合包含多个实体意味着什么，以及 Axon Framework 如何支持这一概念。

我们增强了 Order 应用程序以允许 Order Lines 作为单独的实体属于OrderAggregate。

Axon 的聚合建模支持提供了AggregateMember注解，使用户能够将对象标记为给定聚合的实体。这样做允许命令直接路由到实体，并保持事件源支持到位。

所有这些示例的实现和代码片段都可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/axon)。

有关此主题的任何其他问题，另请查看[讨论 AxonIQ](https://discuss.axoniq.io/)。
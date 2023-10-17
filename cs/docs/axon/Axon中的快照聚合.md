## 1. 概述

在本文中，我们将研究 [Axon](https://www.baeldung.com/axon-cqrs-event-sourcing)如何支持聚合快照。

[我们认为这篇文章是我们关于Axon](https://www.baeldung.com/axon-cqrs-event-sourcing)的主要指南的扩展 。因此，我们将再次使用 [Axon Framework](https://axoniq.io/product-overview/axon-framework) 和 [Axon Server](https://axoniq.io/product-overview/axon-server)。我们将在本文的实现中使用前者，后者是事件存储和消息路由器。

## 2.聚合快照

让我们首先了解快照聚合的含义。当我们从应用程序中的[事件溯源](https://martinfowler.com/eaaDev/EventSourcing.html)开始时，一个自然的问题是我如何在我的应用程序中持续获取聚合性能？ 尽管有多种优化选项，但最直接的是引入快照。

聚合快照是存储聚合状态快照以改进加载的过程。当合并快照时，在命令处理之前加载聚合变成一个两步过程：

1.  检索最新的快照(如果有)，并使用它作为聚合的来源。快照带有一个序列号，定义了它代表聚合状态的时间点。
2.  从快照的序列开始检索剩余的事件，并获取聚合的其余部分。

如果应启用快照，则需要触发创建快照的过程。快照创建过程应确保快照类似于其创建点的整个聚合状态。最后，聚合加载机制(阅读：存储库)应该首先加载快照，然后加载任何剩余的事件。

## 3. Axon 中的聚合快照

Axon Framework 支持聚合快照。有关此过程的完整概述，请查看 Axon 参考指南的[这一](https://docs.axoniq.io/reference-guide/axon-framework/tuning/event-snapshots) 部分。

在该框架内，快照过程由两个主要部分组成：

-   [快照](https://apidocs.axoniq.io/latest/org/axonframework/eventsourcing/Snapshotter.html)者
-   [SnapshotTriggerDefinition](https://apidocs.axoniq.io/latest/org/axonframework/eventsourcing/SnapshotTriggerDefinition.html) _

Snapshotter是 为聚合实例构造快照的组件。默认情况下，框架将使用整个聚合的状态作为快照。

SnapshotTriggerDefinition定义了用于构建快照的Snapshotter的触发器。触发器可以是：

-   在一定数量的事件之后，或者
-   一旦加载需要一定数量，或者
-   在设定的时间。

快照的存储和检索驻留在事件存储和聚合的存储库中。为此， 事件存储包含一个不同的部分来存储快照。 在 Axon 服务器中，一个单独的快照文件反映了这一部分。

快照加载由存储库完成，为此咨询事件存储。因此， 加载聚合和合并快照完全由框架负责。

## 4.配置快照

我们将研究上一篇文章中介绍的 [Order 域](https://github.com/eugenp/tutorials/tree/master/axon)。快照构建、存储和加载已由Snapshotter、事件存储和存储库处理。

因此，要将快照引入OrderAggregate，我们只需配置SnapshotTriggerDefinition。

### 4.1. 定义快照触发器

 由于应用程序使用 Spring，我们可以将SnapshotTriggerDefinition添加到应用程序上下文中。为此，我们添加了一个配置类：

```java
@Configuration
public class OrderApplicationConfiguration {
    @Bean
    public SnapshotTriggerDefinition orderAggregateSnapshotTriggerDefinition(
      Snapshotter snapshotter,
      @Value("${axon.aggregate.order.snapshot-threshold:250}") int threshold) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, threshold);
    }
}
```

在本例中，我们选择了[EventCountSnapshotTriggerDefinition](https://apidocs.axoniq.io/latest/org/axonframework/eventsourcing/EventCountSnapshotTriggerDefinition.html)。 一旦聚合的事件计数与“阈值”匹配，此定义就会触发快照的创建。请注意，阈值可通过属性进行配置。

该定义还需要Snapshotter，Axon 会自动将其添加到应用程序上下文中。因此，它可以在构造触发器定义时作为参数连接。

我们可以使用的另一个实现是 [AggregateLoadTimeSnapshotTriggerDefinition](https://apidocs.axoniq.io/latest/org/axonframework/eventsourcing/AggregateLoadTimeSnapshotTriggerDefinition.html)。如果加载聚合超过loadTimeMillisThreshold，此定义将触发快照的创建 。 最后，由于它是一个快照触发器，它还需要 Snapshotter 来构建快照。

### 4.2. 使用快照触发器

现在SnapshotTriggerDefinition是应用程序的一部分，我们需要为OrderAggregate设置它。Axon 的[Aggregate](https://apidocs.axoniq.io/latest/org/axonframework/spring/stereotype/Aggregate.html)注解允许我们指定快照触发器的 bean 名称。 

在注解上设置 bean 名称将自动配置聚合的触发器定义：

```java
@Aggregate(snapshotTriggerDefinition = "orderAggregateSnapshotTriggerDefinition")
public class OrderAggregate {
    // state, command handlers and event sourcing handlers omitted
}
```

通过将snapshotTriggerDefinition设置为构造定义的 bean 名称，我们指示框架为此聚合配置它。

## 5. 快照实战

该配置将触发器定义阈值设置为“250”。此设置意味着 框架在发布 250 个事件后构建快照。虽然这对于大多数应用程序来说是一个合理的默认值，但这会延长我们的测试时间。

因此，为了执行测试，我们将axon.aggregate.order.snapshot-threshold属性调整为“5”。现在，我们可以更轻松地测试快照是否有效。

为此，我们启动 Axon Server 和 Order 应用程序。在向OrderAggregate发出足够的命令以生成五个事件后，我们可以通过在 Axon 服务器仪表板中搜索来检查应用程序是否存储了快照。

要搜索快照，我们需要单击左侧选项卡中的“搜索”按钮，选择左上角的“快照”，然后单击右侧橙色的“搜索”按钮。下表应显示如下单个条目：

[![Axon 服务器仪表板快照搜索](https://www.baeldung.com/wp-content/uploads/2021/09/axon-server-dashboard-snapshot-search.jpg)](https://www.baeldung.com/wp-content/uploads/2021/09/axon-server-dashboard-snapshot-search.jpg)

## 六. 总结

在本文中，我们了解了聚合快照是什么以及 Axon Framework 如何支持这一概念。

启用快照所需的唯一事情是在聚合上配置SnapshotTriggerDefinition 。快照的创建、存储和检索工作都由我们来完成。

[可以在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/axon)找到订单应用程序的实现和代码片段 。有关此主题的任何其他问题，另请查看 [讨论 AxonIQ](https://discuss.axoniq.io/)。
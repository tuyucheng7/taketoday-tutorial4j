## 1. 概述

领域驱动设计 (DDD) 是一组原则和工具，可帮助我们设计有效的软件架构以提供更高的业务价值。限界上下文是通过将整个应用程序域分离为多个语义一致的部分来从大泥球中拯救架构的核心和基本模式之一。

同时，借助[Java 9 模块系统](https://www.baeldung.com/java-9-modularity)，我们可以创建强封装模块。

在本教程中，我们将创建一个简单的商店应用程序，并了解如何在为限界上下文定义显式边界的同时利用Java9 模块。

## 2. DDD限界上下文

现在的软件系统不是简单的[CRUD应用](https://www.baeldung.com/spring-boot-crud-thymeleaf)。实际上，典型的单一企业系统由一些遗留代码库和新添加的功能组成。然而，随着每次更改，维护此类系统变得越来越困难。最终，它可能变得完全无法维护。

### 2.1. 限界上下文和无处不在的语言

为了解决上述问题，DDD 提供了限界上下文的概念。限界上下文是特定术语和规则始终适用的域的逻辑边界。在这个边界内，所有的术语、定义和概念都构成了通用语言。

特别是，通用语言的主要好处是将来自不同领域的项目成员围绕特定业务领域分组在一起。

此外，多个上下文可能适用于同一事物。但是，它在每个上下文中可能具有不同的含义。

[![一般限界上下文](https://www.baeldung.com/wp-content/uploads/2020/03/General-Bounded-Context.svg)](https://www.baeldung.com/wp-content/uploads/2020/03/General-Bounded-Context.svg)

### 2.2. 订单上下文

让我们通过定义订单上下文开始实施我们的应用程序。此上下文包含两个实体：OrderItem和CustomerOrder。

[![订单上下文](https://www.baeldung.com/wp-content/uploads/2020/03/OrderContext.svg)](https://www.baeldung.com/wp-content/uploads/2020/03/OrderContext.svg)
CustomerOrder实体是一个[聚合](https://www.baeldung.com/spring-persisting-ddd-aggregates) 根：

```java
public class CustomerOrder {
    private int orderId;
    private String paymentMethod;
    private String address;
    private List<OrderItem> orderItems;

    public float calculateTotalPrice() {
        return orderItems.stream().map(OrderItem::getTotalPrice)
          .reduce(0F, Float::sum);
    }
}
```

如我们所见，此类包含calculateTotalPrice业务方法。但是，在现实世界的项目中，它可能会复杂得多——例如，在最终价格中包括折扣和税收。

接下来，让我们创建OrderItem类：

```java
public class OrderItem {
    private int productId;
    private int quantity;
    private float unitPrice;
    private float unitWeight;
}
```

我们已经定义了实体，但我们还需要向应用程序的其他部分公开一些 API。让我们创建CustomerOrderService类：

```java
public class CustomerOrderService implements OrderService {
    public static final String EVENT_ORDER_READY_FOR_SHIPMENT = "OrderReadyForShipmentEvent";

    private CustomerOrderRepository orderRepository;
    private EventBus eventBus;

    @Override
    public void placeOrder(CustomerOrder order) {
        this.orderRepository.saveCustomerOrder(order);
        Map<String, String> payload = new HashMap<>();
        payload.put("order_id", String.valueOf(order.getOrderId()));
        ApplicationEvent event = new ApplicationEvent(payload) {
            @Override
            public String getType() {
                return EVENT_ORDER_READY_FOR_SHIPMENT;
            }
        };
        this.eventBus.publish(event);
    }
}
```

在这里，我们有一些要点要强调。placeOrder方法负责处理客户订单。处理订单后，事件将发布到EventBus。我们将在下一章讨论事件驱动的通信。该服务提供了OrderService接口的默认实现：

```java
public interface OrderService extends ApplicationService {
    void placeOrder(CustomerOrder order);

    void setOrderRepository(CustomerOrderRepository orderRepository);
}
```

此外，此服务需要CustomerOrderRepository来保存订单：

```java
public interface CustomerOrderRepository {
    void saveCustomerOrder(CustomerOrder order);
}
```

重要的是这个接口不是在这个上下文中实现的，而是由基础设施模块提供的，我们稍后会看到。

### 2.3. 航运背景

现在，让我们定义 Shipping Context。它也很简单，包含三个实体：Parcel、PackageItem和ShippableOrder。

[![ShippingContext](https://www.baeldung.com/wp-content/uploads/2020/03/ShippingContext.svg)](https://www.baeldung.com/wp-content/uploads/2020/03/ShippingContext.svg)

让我们从ShippableOrder实体开始：

```java
public class ShippableOrder {
    private int orderId;
    private String address;
    private List<PackageItem> packageItems;
}
```

在这种情况下，实体不包含paymentMethod字段。那是因为，在我们的 Shipping Context 中，我们不关心使用哪种付款方式。Shipping Context 只负责处理订单的发货。

此外，包裹实体特定于运输上下文：

```java
public class Parcel {
    private int orderId;
    private String address;
    private String trackingId;
    private List<PackageItem> packageItems;

    public float calculateTotalWeight() {
        return packageItems.stream().map(PackageItem::getWeight)
          .reduce(0F, Float::sum);
    }

    public boolean isTaxable() {
        return calculateEstimatedValue() > 100;
    }

    public float calculateEstimatedValue() {
        return packageItems.stream().map(PackageItem::getWeight)
          .reduce(0F, Float::sum);
    }
}
```

正如我们所看到的，它还包含特定的业务方法并充当聚合根。

最后，让我们定义ParcelShippingService：

```java
public class ParcelShippingService implements ShippingService {
    public static final String EVENT_ORDER_READY_FOR_SHIPMENT = "OrderReadyForShipmentEvent";
    private ShippingOrderRepository orderRepository;
    private EventBus eventBus;
    private Map<Integer, Parcel> shippedParcels = new HashMap<>();

    @Override
    public void shipOrder(int orderId) {
        Optional<ShippableOrder> order = this.orderRepository.findShippableOrder(orderId);
        order.ifPresent(completedOrder -> {
            Parcel parcel = new Parcel(completedOrder.getOrderId(), completedOrder.getAddress(), 
              completedOrder.getPackageItems());
            if (parcel.isTaxable()) {
                // Calculate additional taxes
            }
            // Ship parcel
            this.shippedParcels.put(completedOrder.getOrderId(), parcel);
        });
    }

    @Override
    public void listenToOrderEvents() {
        this.eventBus.subscribe(EVENT_ORDER_READY_FOR_SHIPMENT, new EventSubscriber() {
            @Override
            public <E extends ApplicationEvent> void onEvent(E event) {
                shipOrder(Integer.parseInt(event.getPayloadValue("order_id")));
            }
        });
    }

    @Override
    public Optional<Parcel> getParcelByOrderId(int orderId) {
        return Optional.ofNullable(this.shippedParcels.get(orderId));
    }
}
```

该服务同样使用ShippingOrderRepository按 ID 获取订单。更重要的是，它订阅了另一个上下文发布的OrderReadyForShipmentEvent事件。发生此事件时，服务会应用一些规则并运送订单。为了简单起见，我们将已发货的订单存储在[HashMap](https://www.baeldung.com/java-hashmap)中。

## 3.上下文映射

到目前为止，我们定义了两个上下文。但是，我们没有在它们之间设置任何明确的关系。为此，DDD 有上下文映射的概念。上下文映射是对系统不同上下文之间关系的可视化描述。这张地图显示了不同的部分如何共存在一起形成域。

限界上下文之间有五种主要类型的关系：

-   伙伴关系——两种环境之间的一种关系，这种关系通过合作使两个团队与相关目标保持一致
-   共享内核——一种将多个上下文的公共部分提取到另一个上下文/模块以减少代码重复的关系
-   客户-供应商——两个上下文之间的连接，其中一个上下文(上游)产生数据，另一个(下游)使用它。在这种关系中，双方都有兴趣建立尽可能好的沟通
-   Conformist——这种关系也有上游和下游，但是下游总是遵循上游的API
-   反腐败层——这种关系广泛用于遗留系统，使它们适应新的架构，并逐渐从遗留代码库迁移。Anticorruption 层充当[适配器](https://www.baeldung.com/hexagonal-architecture-ddd-spring)以转换来自上游的数据并防止意外更改

在我们的特定示例中，我们将使用共享内核关系。我们不会以纯粹的形式定义它，但它主要充当系统中事件的中介。

因此，SharedKernel 模块将不包含任何具体实现，仅包含接口。

让我们从EventBus接口开始：

```java
public interface EventBus {
    <E extends ApplicationEvent> void publish(E event);

    <E extends ApplicationEvent> void subscribe(String eventType, EventSubscriber subscriber);

    <E extends ApplicationEvent> void unsubscribe(String eventType, EventSubscriber subscriber);
}
```

该接口稍后将在我们的基础设施模块中实现。

接下来，我们创建一个带有默认方法的基本服务接口来支持事件驱动的通信：

```java
public interface ApplicationService {

    default <E extends ApplicationEvent> void publishEvent(E event) {
        EventBus eventBus = getEventBus();
        if (eventBus != null) {
            eventBus.publish(event);
        }
    }

    default <E extends ApplicationEvent> void subscribe(String eventType, EventSubscriber subscriber) {
        EventBus eventBus = getEventBus();
        if (eventBus != null) {
            eventBus.subscribe(eventType, subscriber);
        }
    }

    default <E extends ApplicationEvent> void unsubscribe(String eventType, EventSubscriber subscriber) {
        EventBus eventBus = getEventBus();
        if (eventBus != null) {
            eventBus.unsubscribe(eventType, subscriber);
        }
    }

    EventBus getEventBus();

    void setEventBus(EventBus eventBus);
}
```

因此，限界上下文中的服务接口扩展了此接口以具有与事件相关的通用功能。

## 4.Java9 模块化

现在，是时候探索Java9 模块系统如何支持定义的应用程序结构了。

Java 平台模块系统 (JPMS) 鼓励构建更可靠和强封装的模块。因此，这些功能有助于隔离我们的上下文并建立清晰的界限。

让我们看看我们最终的模块图：

[![模块图](https://www.baeldung.com/wp-content/uploads/2020/03/ModulesDiagram.svg)](https://www.baeldung.com/wp-content/uploads/2020/03/ModulesDiagram.svg)

### 4.1. 共享内核模块

让我们从 SharedKernel 模块开始，它对其他模块没有任何依赖。因此，module-info.java看起来像：

```java
module cn.tuyucheng.taketoday.dddmodules.sharedkernel {
    exports cn.tuyucheng.taketoday.dddmodules.sharedkernel.events;
    exports cn.tuyucheng.taketoday.dddmodules.sharedkernel.service;
}
```

我们导出模块接口，因此它们可用于其他模块。

### 4.2. OrderContext模块

接下来，让我们将注意力转移到 OrderContext 模块。它只需要在 SharedKernel 模块中定义的接口：

```java
module cn.tuyucheng.taketoday.dddmodules.ordercontext {
    requires cn.tuyucheng.taketoday.dddmodules.sharedkernel;
    exports cn.tuyucheng.taketoday.dddmodules.ordercontext.service;
    exports cn.tuyucheng.taketoday.dddmodules.ordercontext.model;
    exports cn.tuyucheng.taketoday.dddmodules.ordercontext.repository;
    provides cn.tuyucheng.taketoday.dddmodules.ordercontext.service.OrderService
      with cn.tuyucheng.taketoday.dddmodules.ordercontext.service.CustomerOrderService;
}
```

此外，我们可以看到该模块导出了OrderService接口的默认实现。

### 4.3. ShippingContext模块

与前面的模块类似，让我们创建 ShippingContext 模块定义文件：

```java
module cn.tuyucheng.taketoday.dddmodules.shippingcontext {
    requires cn.tuyucheng.taketoday.dddmodules.sharedkernel;
    exports cn.tuyucheng.taketoday.dddmodules.shippingcontext.service;
    exports cn.tuyucheng.taketoday.dddmodules.shippingcontext.model;
    exports cn.tuyucheng.taketoday.dddmodules.shippingcontext.repository;
    provides cn.tuyucheng.taketoday.dddmodules.shippingcontext.service.ShippingService
      with cn.tuyucheng.taketoday.dddmodules.shippingcontext.service.ParcelShippingService;
}
```

同样，我们导出ShippingService 接口的默认实现。

### 4.4. 基础设施模块

现在是描述基础设施模块的时候了。该模块包含已定义接口的实现细节。我们将从为EventBus接口创建一个简单的实现开始：

```java
public class SimpleEventBus implements EventBus {
    private final Map<String, Set<EventSubscriber>> subscribers = new ConcurrentHashMap<>();

    @Override
    public <E extends ApplicationEvent> void publish(E event) {
        if (subscribers.containsKey(event.getType())) {
            subscribers.get(event.getType())
              .forEach(subscriber -> subscriber.onEvent(event));
        }
    }

    @Override
    public <E extends ApplicationEvent> void subscribe(String eventType, EventSubscriber subscriber) {
        Set<EventSubscriber> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers == null) {
            eventSubscribers = new CopyOnWriteArraySet<>();
            subscribers.put(eventType, eventSubscribers);
        }
        eventSubscribers.add(subscriber);
    }

    @Override
    public <E extends ApplicationEvent> void unsubscribe(String eventType, EventSubscriber subscriber) {
        if (subscribers.containsKey(eventType)) {
            subscribers.get(eventType).remove(subscriber);
        }
    }
}
```

接下来，我们需要实现CustomerOrderRepository和ShippingOrderRepository接口。在大多数情况下，Order实体将存储在同一个表中，但在有界上下文中用作不同的实体模型。

单个实体包含来自业务领域或低级数据库映射的不同区域的混合代码是很常见的。对于我们的实现，我们根据限界上下文拆分了我们的实体：CustomerOrder和ShippableOrder。

首先，让我们创建一个代表整个持久模型的类：

```java
public static class PersistenceOrder {
    public int orderId;
    public String paymentMethod;
    public String address;
    public List<OrderItem> orderItems;

    public static class OrderItem {
        public int productId;
        public float unitPrice;
        public float itemWeight;
        public int quantity;
    }
}
```

我们可以看到此类包含来自CustomerOrder和ShippableOrder实体的所有字段。

为了简单起见，让我们模拟一个内存数据库：

```java
public class InMemoryOrderStore implements CustomerOrderRepository, ShippingOrderRepository {
    private Map<Integer, PersistenceOrder> ordersDb = new HashMap<>();

    @Override
    public void saveCustomerOrder(CustomerOrder order) {
        this.ordersDb.put(order.getOrderId(), new PersistenceOrder(order.getOrderId(),
          order.getPaymentMethod(),
          order.getAddress(),
          order
            .getOrderItems()
            .stream()
            .map(orderItem ->
              new PersistenceOrder.OrderItem(orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getUnitWeight(),
                orderItem.getUnitPrice()))
            .collect(Collectors.toList())
        ));
    }

    @Override
    public Optional<ShippableOrder> findShippableOrder(int orderId) {
        if (!this.ordersDb.containsKey(orderId)) return Optional.empty();
        PersistenceOrder orderRecord = this.ordersDb.get(orderId);
        return Optional.of(
          new ShippableOrder(orderRecord.orderId, orderRecord.orderItems
            .stream().map(orderItem -> new PackageItem(orderItem.productId,
              orderItem.itemWeight,
              orderItem.quantity * orderItem.unitPrice)
            ).collect(Collectors.toList())));
    }
}
```

在这里，我们通过将持久模型与适当的类型进行转换来持久化和检索不同类型的实体。

最后，让我们创建模块定义：

```java
module cn.tuyucheng.taketoday.dddmodules.infrastructure {
    requires transitive cn.tuyucheng.taketoday.dddmodules.sharedkernel;
    requires transitive cn.tuyucheng.taketoday.dddmodules.ordercontext;
    requires transitive cn.tuyucheng.taketoday.dddmodules.shippingcontext;
    provides cn.tuyucheng.taketoday.dddmodules.sharedkernel.events.EventBus
      with cn.tuyucheng.taketoday.dddmodules.infrastructure.events.SimpleEventBus;
    provides cn.tuyucheng.taketoday.dddmodules.ordercontext.repository.CustomerOrderRepository
      with cn.tuyucheng.taketoday.dddmodules.infrastructure.db.InMemoryOrderStore;
    provides cn.tuyucheng.taketoday.dddmodules.shippingcontext.repository.ShippingOrderRepository
      with cn.tuyucheng.taketoday.dddmodules.infrastructure.db.InMemoryOrderStore;
}
```

使用provides with子句，我们提供了一些在其他模块中定义的接口的实现。

此外，该模块充当依赖项的聚合器，因此我们使用requires 传递关键字。结果，需要基础设施模块的模块将传递获得所有这些依赖项。

### 4.5. 主模块

最后，让我们定义一个模块作为我们应用程序的入口点：

```java
module cn.tuyucheng.taketoday.dddmodules.mainapp {
    uses cn.tuyucheng.taketoday.dddmodules.sharedkernel.events.EventBus;
    uses cn.tuyucheng.taketoday.dddmodules.ordercontext.service.OrderService;
    uses cn.tuyucheng.taketoday.dddmodules.ordercontext.repository.CustomerOrderRepository;
    uses cn.tuyucheng.taketoday.dddmodules.shippingcontext.repository.ShippingOrderRepository;
    uses cn.tuyucheng.taketoday.dddmodules.shippingcontext.service.ShippingService;
    requires transitive cn.tuyucheng.taketoday.dddmodules.infrastructure;
}
```

由于我们刚刚在 Infrastructure 模块上设置了传递依赖，因此我们不需要在这里明确要求它们。

另一方面，我们使用 uses关键字列出这些依赖项。uses子句指示我们将在下一章中发现的 ServiceLoader 这个模块想要使用这些接口。但是，它不要求实现在编译时可用。

## 5. 运行应用程序

最后，我们几乎准备好构建我们的应用程序了。我们将利用[Maven](https://www.baeldung.com/maven)来构建我们的项目。这使得使用模块变得更加容易。

### 5.1. 项目结构

我们的项目包含[五个模块和父模块](https://www.baeldung.com/maven-multi-module-project-java-jpms)。让我们看一下我们的项目结构：

```shell
ddd-modules (the root directory)
pom.xml
|-- infrastructure
    |-- src
        |-- main
            | -- java
            module-info.java
            |-- cn.tuyucheng.taketoday.dddmodules.infrastructure
    pom.xml
|-- mainapp
    |-- src
        |-- main
            | -- java
            module-info.java
            |-- cn.tuyucheng.taketoday.dddmodules.mainapp
    pom.xml
|-- ordercontext
    |-- src
        |-- main
            | -- java
            module-info.java
            |--cn.tuyucheng.taketoday.dddmodules.ordercontext
    pom.xml
|-- sharedkernel
    |-- src
        |-- main
            | -- java
            module-info.java
            |-- cn.tuyucheng.taketoday.dddmodules.sharedkernel
    pom.xml
|-- shippingcontext
    |-- src
        |-- main
            | -- java
            module-info.java
            |-- cn.tuyucheng.taketoday.dddmodules.shippingcontext
    pom.xml
```

### 5.2. 主要应用

到现在为止，我们已经拥有了除主应用程序之外的所有内容，所以让我们定义我们的main方法：

```java
public static void main(String args[]) {
    Map<Class<?>, Object> container = createContainer();
    OrderService orderService = (OrderService) container.get(OrderService.class);
    ShippingService shippingService = (ShippingService) container.get(ShippingService.class);
    shippingService.listenToOrderEvents();

    CustomerOrder customerOrder = new CustomerOrder();
    int orderId = 1;
    customerOrder.setOrderId(orderId);
    List<OrderItem> orderItems = new ArrayList<OrderItem>();
    orderItems.add(new OrderItem(1, 2, 3, 1));
    orderItems.add(new OrderItem(2, 1, 1, 1));
    orderItems.add(new OrderItem(3, 4, 11, 21));
    customerOrder.setOrderItems(orderItems);
    customerOrder.setPaymentMethod("PayPal");
    customerOrder.setAddress("Full address here");
    orderService.placeOrder(customerOrder);

    if (orderId == shippingService.getParcelByOrderId(orderId).get().getOrderId()) {
        System.out.println("Order has been processed and shipped successfully");
    }
}
```

让我们简要讨论一下我们的主要方法。在此方法中，我们通过使用先前定义的服务来模拟一个简单的客户订单流。首先，我们创建了包含三个项目的订单，并提供了必要的运输和付款信息。接下来我们提交订单，最后检查是否发货和处理成功。

但是我们是如何获得所有依赖项的，为什么createContainer方法会返回Map<Class<?>, Object>？让我们仔细看看这个方法。

### 5.3. 使用 ServiceLoader 进行依赖注入

在这个项目中，我们没有任何[Spring IoC](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)依赖项，因此我们将使用[ServiceLoader API](https://www.baeldung.com/java-spi#4-serviceloader)来发现服务的实现。这不是一个新特性——ServiceLoader API 本身从Java6 就已经存在了。

我们可以通过调用ServiceLoader类的静态加载方法之一来获取加载器实例。load方法返回Iterable类型，以便我们可以迭代发现的实现。

现在，让我们应用加载程序来解决我们的依赖关系：

```java
public static Map<Class<?>, Object> createContainer() {
    EventBus eventBus = ServiceLoader.load(EventBus.class).findFirst().get();

    CustomerOrderRepository customerOrderRepository = ServiceLoader.load(CustomerOrderRepository.class)
      .findFirst().get();
    ShippingOrderRepository shippingOrderRepository = ServiceLoader.load(ShippingOrderRepository.class)
      .findFirst().get();

    ShippingService shippingService = ServiceLoader.load(ShippingService.class).findFirst().get();
    shippingService.setEventBus(eventBus);
    shippingService.setOrderRepository(shippingOrderRepository);
    OrderService orderService = ServiceLoader.load(OrderService.class).findFirst().get();
    orderService.setEventBus(eventBus);
    orderService.setOrderRepository(customerOrderRepository);

    HashMap<Class<?>, Object> container = new HashMap<>();
    container.put(OrderService.class, orderService);
    container.put(ShippingService.class, shippingService);

    return container;
}
```

在这里，我们为每个需要的接口调用静态加载方法，每次都会创建一个新的加载器实例。因此，它不会缓存已经解析的依赖项——相反，它每次都会创建新的实例。

通常，可以通过以下两种方式之一创建服务实例。服务实现类要么必须具有公共无参数构造函数，要么必须使用静态提供程序方法。

因此，我们的大多数服务都具有用于依赖项的无参数构造函数和设置方法。但是，正如我们已经看到的，InMemoryOrderStore类实现了两个接口：CustomerOrderRepository和ShippingOrderRepository。

但是，如果我们使用load方法请求这些接口中的每一个，我们将获得InMemoryOrderStore的不同实例。这是不可取的行为，所以让我们使用提供者方法技术来缓存实例：

```java
public class InMemoryOrderStore implements CustomerOrderRepository, ShippingOrderRepository {
    private volatile static InMemoryOrderStore instance = new InMemoryOrderStore();

    public static InMemoryOrderStore provider() {
        return instance;
    }
}
```

我们已经应用[单例模式来缓存](https://www.baeldung.com/java-singleton)InMemoryOrderStore类的单个实例，并从提供者方法中返回它。

如果服务提供者声明了提供者方法，则ServiceLoader会调用此方法来获取服务实例。否则，它将尝试通过[Reflection](https://www.baeldung.com/java-reflection)使用无参数构造函数创建一个实例。因此，我们可以在不影响我们的createContainer方法的情况下更改服务提供者机制。

最后，我们通过设置器向服务提供已解决的依赖关系，并返回配置的服务。

最后，我们可以运行该应用程序。

## 6. 总结

在本文中，我们讨论了一些重要的 DDD 概念：限界上下文、通用语言和上下文映射。虽然将系统划分为限界上下文有很多好处，但与此同时，没有必要在所有地方都应用这种方法。

接下来，我们了解了如何使用Java9 模块系统和限界上下文来创建强封装模块。

此外，我们还介绍了用于发现依赖项的默认ServiceLoader机制。
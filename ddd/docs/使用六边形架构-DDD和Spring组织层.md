## 1. 概述

在本教程中，我们将使用 DDD 实现一个 Spring 应用程序。此外，我们将在六边形架构的帮助下组织层。

通过这种方法，我们可以轻松地交换应用程序的不同层。

## 2. 六边形架构

六边形架构是一种围绕域逻辑设计软件应用程序以将其与外部因素隔离开来的模型。

领域逻辑在业务核心中指定，我们称之为内部部分，其余部分为外部部分。可以通过端口和适配器从外部访问域逻辑。 

## 三、原则

首先，我们应该定义划分代码的原则。正如已经简要解释的那样，六角形建筑定义了内部和外部部分。

我们要做的是将我们的应用程序分为三层；应用程序(外部)、域(内部)和基础设施(外部)：

[![DDD层](https://www.baeldung.com/wp-content/uploads/2019/12/DDD-Layers.png)](https://www.baeldung.com/wp-content/uploads/2019/12/DDD-Layers.png)

通过应用层，用户或任何其他程序与应用程序进行交互。该区域应包含用户界面、RESTful 控制器和 JSON 序列化库等内容。它包括任何向我们的应用程序公开入口并编排域逻辑执行的东西。

在领域层，我们保留接触和实现业务逻辑的代码。这是我们应用程序的核心。此外，该层应与应用程序部分和基础设施部分隔离。最重要的是，它还应该包含定义 API 的接口，以便与域交互的外部部分(如数据库)进行通信。

最后， 基础设施层是包含应用程序工作所需的任何内容的部分，例如数据库配置或 Spring 配置。此外，它还从领域层实现依赖于基础设施的接口。

## 4.领域层

让我们从实现我们的核心层开始，它是领域层。

首先，我们应该创建Order类：

```java
public class Order {
    private UUID id;
    private OrderStatus status;
    private List<OrderItem> orderItems;
    private BigDecimal price;

    public Order(UUID id, Product product) {
        this.id = id;
        this.orderItems = new ArrayList<>(Arrays.astList(new OrderItem(product)));
        this.status = OrderStatus.CREATED;
        this.price = product.getPrice();
    }

    public void complete() {
        validateState();
        this.status = OrderStatus.COMPLETED;
    }

    public void addOrder(Product product) {
        validateState();
        validateProduct(product);
        orderItems.add(new OrderItem(product));
        price = price.add(product.getPrice());
    }

    public void removeOrder(UUID id) {
        validateState();
        final OrderItem orderItem = getOrderItem(id);
        orderItems.remove(orderItem);

        price = price.subtract(orderItem.getPrice());
    }

    // getters
}
```

这是我们的[聚合根](https://www.baeldung.com/spring-persisting-ddd-aggregates)。任何与我们的业务逻辑相关的东西都会通过这个类。此外， Order负责将自身保持在正确的状态：

-   订单只能使用给定的 ID 并基于一个产品创建——构造函数本身也以CREATED状态初始化订单
-   一旦订单完成，就不可能更改OrderItem s
-   从域对象外部更改Order是不可能的，就像使用 setter

此外，Order类还负责创建其OrderItem。

然后让我们创建OrderItem类：

```java
public class OrderItem {
    private UUID productId;
    private BigDecimal price;

    public OrderItem(Product product) {
        this.productId = product.getId();
        this.price = product.getPrice();
    }

    // getters
}
```

如我们所见，OrderItem是基于Product创建的。它保留对它的引用并存储Product的当前价格。

接下来，我们将创建一个存储库接口(Hexagonal Architecture 中的一个端口)。接口的实现将在基础设施层：

```java
public interface OrderRepository {
    Optional<Order> findById(UUID id);

    void save(Order order);
}
```

最后，我们应该确保 在每次操作后始终保存订单。为此，我们将定义一个域服务，它通常包含不能成为根的一部分的逻辑：

```java
public class DomainOrderService implements OrderService {

    private final OrderRepository orderRepository;

    public DomainOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public UUID createOrder(Product product) {
        Order order = new Order(UUID.randomUUID(), product);
        orderRepository.save(order);

        return order.getId();
    }

    @Override
    public void addProduct(UUID id, Product product) {
        Order order = getOrder(id);
        order.addOrder(product);

        orderRepository.save(order);
    }

    @Override
    public void completeOrder(UUID id) {
        Order order = getOrder(id);
        order.complete();

        orderRepository.save(order);
    }

    @Override
    public void deleteProduct(UUID id, UUID productId) {
        Order order = getOrder(id);
        order.removeOrder(productId);

        orderRepository.save(order);
    }

    private Order getOrder(UUID id) {
        return orderRepository
          .findById(id)
          .orElseThrow(RuntimeException::new);
    }
}
```

在六边形架构中，此服务是实现端口的适配器。此外，我们不会将其注册为 Spring bean ，因为从域的角度来看，这是在内部，而 Spring 配置在外部。稍后我们将在基础设施层中手动将其与 Spring 连接起来。

因为领域层与应用层和基础设施层完全解耦，我们 也可以独立测试：

```java
class DomainOrderServiceUnitTest {

    private OrderRepository orderRepository;
    private DomainOrderService tested;
    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        tested = new DomainOrderService(orderRepository);
    }

    @Test
    void shouldCreateOrder_thenSaveIt() {
        final Product product = new Product(UUID.randomUUID(), BigDecimal.TEN, "productName");

        final UUID id = tested.createOrder(product);

        verify(orderRepository).save(any(Order.class));
        assertNotNull(id);
    }
}
```

## 5. 应用层

在本节中，我们将实现应用层。我们将允许用户通过 RESTful API 与我们的应用程序进行通信。

因此，让我们创建OrderController：

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        UUID id = orderService.createOrder(request.getProduct());

        return new CreateOrderResponse(id);
    }

    @PostMapping(value = "/{id}/products")
    void addProduct(@PathVariable UUID id, @RequestBody AddProductRequest request) {
        orderService.addProduct(id, request.getProduct());
    }

    @DeleteMapping(value = "/{id}/products")
    void deleteProduct(@PathVariable UUID id, @RequestParam UUID productId) {
        orderService.deleteProduct(id, productId);
    }

    @PostMapping("/{id}/complete")
    void completeOrder(@PathVariable UUID id) {
        orderService.completeOrder(id);
    }
}
```

这个简单的[Spring Rest 控制器](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration) 负责编排域逻辑的执行。

该控制器使外部 RESTful 接口适应我们的域。它通过从OrderService (port) 调用适当的方法来完成。

## 6.基础设施层

基础设施层包含运行应用程序所需的逻辑。

因此，我们将从创建配置类开始。首先，让我们实现一个将我们的OrderService注册为 Spring bean 的类：

```java
@Configuration
public class BeanConfiguration {

    @Bean
    OrderService orderService(OrderRepository orderRepository) {
        return new DomainOrderService(orderRepository);
    }
}
```

接下来，让我们创建负责启用我们将使用的[Spring Data存储库的配置：](https://www.baeldung.com/spring-data-mongodb-tutorial)

```java
@EnableMongoRepositories(basePackageClasses = SpringDataMongoOrderRepository.class)
public class MongoDBConfiguration {
}
```

我们使用了basePackageClasses属性，因为这些存储库只能位于基础设施层中。因此，Spring 没有理由扫描整个应用程序。此外，此类可以包含与在 MongoDB 和我们的应用程序之间建立连接相关的所有内容。

最后，我们将从 领域层实施OrderRepository 。我们将在我们的实现中使用我们的SpringDataMongoOrderRepository ：

```java
@Component
public class MongoDbOrderRepository implements OrderRepository {

    private SpringDataMongoOrderRepository orderRepository;

    @Autowired
    public MongoDbOrderRepository(SpringDataMongoOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderRepository.findById(id);
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }
}
```

此实现将我们的订单存储在 MongoDB 中。在六边形架构中，此实现也是一个适配器。

## 七、好处

这种方法的第一个优点是我们将每一层的工作分开。我们可以专注于某一层而不影响其他层。

此外，它们自然更容易理解，因为它们每个都专注于其逻辑。

另一大优势是我们将领域逻辑与其他一切隔离开来。域部分只包含业务逻辑，可以很容易地转移到不同的环境。

实际上，让我们更改基础架构层以使用[Cassandra](https://www.baeldung.com/spring-data-cassandra-tutorial)作为数据库：

```java
@Component
public class CassandraDbOrderRepository implements OrderRepository {

    private final SpringDataCassandraOrderRepository orderRepository;

    @Autowired
    public CassandraDbOrderRepository(SpringDataCassandraOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(id);
        if (orderEntity.isPresent()) {
            return Optional.of(orderEntity.get()
                .toOrder());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void save(Order order) {
        orderRepository.save(new OrderEntity(order));
    }

}
```

与 MongoDB 不同，我们现在使用OrderEntity将域持久保存在数据库中。

如果我们将特定于技术的注解添加到我们的Order领域对象，那么我们就违反了基础设施层和领域层之间的解耦。

存储库使域适应我们的持久性需求。

让我们更进一步，将我们的 RESTful 应用程序转换为命令行应用程序：

```java
@Component
public class CliOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(CliOrderController.class);

    private final OrderService orderService;

    @Autowired
    public CliOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void createCompleteOrder() {
        LOG.info("<<Create complete order>>");
        UUID orderId = createOrder();
        orderService.completeOrder(orderId);
    }

    public void createIncompleteOrder() {
        LOG.info("<<Create incomplete order>>");
        UUID orderId = createOrder();
    }

    private UUID createOrder() {
        LOG.info("Placing a new order with two products");
        Product mobilePhone = new Product(UUID.randomUUID(), BigDecimal.valueOf(200), "mobile");
        Product razor = new Product(UUID.randomUUID(), BigDecimal.valueOf(50), "razor");
        LOG.info("Creating order with mobile phone");
        UUID orderId = orderService.createOrder(mobilePhone);
        LOG.info("Adding a razor to the order");
        orderService.addProduct(orderId, razor);
        return orderId;
    }
}
```

与以前不同，我们现在硬连接了一组与我们的域交互的预定义操作。例如，我们可以使用它来使用模拟数据填充我们的应用程序。

即使我们完全改变了应用程序的目的，我们也没有触及域层。

## 八. 总结

在本文中，我们学习了如何将与我们的应用程序相关的逻辑分离到特定的层中。

首先，我们定义了三个主要层：应用程序、域和基础设施。之后，我们描述了如何填充它们并解释了优点。

然后，我们想出了每一层的实现：

[![DDD层实现](https://www.baeldung.com/wp-content/uploads/2019/12/DDD-Layers-implemented.png)](https://www.baeldung.com/wp-content/uploads/2019/12/DDD-Layers-implemented.png)最后，我们在不影响领域的情况下交换了应用程序和基础设施层。
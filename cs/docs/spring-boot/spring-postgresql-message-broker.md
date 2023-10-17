## 1. 简介

在本教程中，我们将如何使用 PostgreSQL 的LISTEN/NOTIFY命令来实现简单的消息代理机制。

## 2. PostgreSQL 的LISTEN/NOTIFY机制快速介绍

简而言之，这些命令允许连接的客户端通过常规 PostgreSQL 连接交换消息。客户端使用[NOTIFY命令](https://www.postgresql.org/docs/current/sql-notify.html)将通知以及可选的字符串有效负载发送到通道。

通道可以是任何有效的 SQL 标识符，它的工作方式类似于传统消息传递系统中的主题。这意味着有效负载将被发送到该特定通道的所有活动侦听器。当没有有效负载时，侦听器仅收到一个空通知。

要开始接收通知，客户端使用[LISTEN命令](https://www.postgresql.org/docs/current/sql-listen.html)，该命令将通道名称作为其单个参数。该命令立即返回，从而允许客户端使用同一连接继续执行其他任务。

通知机制有一些重要的属性：

- 通道名称在数据库中是唯一的
- 客户无需特殊授权即可使用LISTEN/NOTIFY
- 当在事务中使用NOTIFY时，客户端仅在事务成功完成时才会收到通知

此外，如果在事务中使用相同的负载将多个NOTIFY命令发送到同一通道，客户端将收到单个通知。

## 3. PostgreSQL 作为消息代理的案例

考虑到 PostgreSQL 通知的属性，我们可能想知道什么时候使用它代替成熟的消息代理(例如 RabbitMQ 或类似的)是一个可行的选择。与往常一样，需要进行一些权衡。一般来说，选择后者意味着：

- 更加复杂——消息代理是另一个必须监控、升级等的组件
- 处理分布式事务带来的故障模式

通知机制不会遇到这些问题：

- 假设我们使用 PostgreSQL 作为主数据库，该功能已经就位
- 无分布式事务

当然，也有局限性：

- 它是一种专有机制，需要永远拥抱 PostgreSQL(或者至少在重大重构之前)
- 不直接支持持久订阅者。在客户端开始监听消息之前发送的通知将会丢失

即使有这些限制，该机制仍有一些潜在的应用：

- “模块化整体”式应用程序中的通知总线
- 分布式缓存失效
- 轻量级消息代理，使用普通数据库表作为队列
- 事件溯源架构

## 4.在Spring Boot应用程序中使用LISTEN/NOTIFY

现在我们对LISTEN/NOTIFY机制有了基本的了解，让我们继续使用它构建一个简单的Spring Boot测试应用程序。我们将创建一个简单的 API，允许我们提交买入/卖出订单。有效负载由我们愿意购买或出售的工具符号、价格和数量组成。我们还将添加一个 API，允许我们在给定标识符的情况下查询订单。

到目前为止，没有什么特别的。但这里有一个问题：我们希望在将订单查询插入数据库后立即开始从缓存中提供订单查询服务。当然，我们可以进行缓存直写，但在需要扩展服务的分布式场景中，我们还需要分布式缓存。

这就是通知机制派上用场的地方：我们将在每次插入时发送NOTIFY，客户端将使用LISTEN将订单预加载到各自的本地缓存中。

### 4.1. 项目依赖关系

我们的示例应用程序需要 WebMVC SpringBoot 应用程序的常规依赖项集以及 PostgreSQL 驱动程序：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.12</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jdbc</artifactId>
    <version>2.7.12</version>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>

```

spring - [boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)、[spring-boot-starter-data-jdbc](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jdbc)和[postgresql](https://mvnrepository.com/artifact/org.postgresql/postgresql)的最新版本可在 Maven Central 上找到。

### 4.2. 通知服务

由于通知机制是 PostgreSQL 特有的，因此我们将其一般行为封装在一个类中：NotifierService。通过这样做，我们可以避免这些细节泄漏到应用程序的其他部分。这也简化了单元测试，因为我们可以用模拟版本替换该服务来实现不同的场景。

NotifierService有两个职责。首先，它提供了一个发送订单相关通知的外观：

```java
public class NotifierService {
    private static final String ORDERS_CHANNEL = "orders";
    private final JdbcTemplate tpl;
   
    @Transactional
    public void notifyOrderCreated(Order order) {
        tpl.execute("NOTIFY " + ORDERS_CHANNEL + ", '" + order.getId() + "'");
    }
   // ... other methods omitted
}
```

其次，它有一个Runnable实例的工厂方法，应用程序用它来接收通知。该工厂采用PGNotification对象的Consumer，该对象具有检索与通知关联的通道和有效负载的方法：

```java
public Runnable createNotificationHandler(Consumer<PGNotification> consumer) {        
    return () -> {
        tpl.execute((Connection c) -> {
            c.createStatement().execute("LISTEN " + ORDERS_CHANNEL);                
            PGConnection pgconn = c.unwrap(PGConnection.class);                 
            while(!Thread.currentThread().isInterrupted()) {
                PGNotification[] nts = pgconn.getNotifications(10000);
                if ( nts == null || nts.length == 0 ) {
                    continue;
                }                    
                for( PGNotification nt : nts) {
                    consumer.accept(nt);
                }
            }                
            return 0;
        });                
    };
}

```

在这里，为了简单起见，我们选择提供原始PGNotification。在现实场景中，我们通常要处理多个域实体，我们可以使用泛型或类似技术来扩展此类，以避免代码重复。

关于创建的Runnable 的一些注意事项：

- 数据库相关逻辑使用提供的JdbcTemplate的execute()方法。这确保了正确的连接处理/清理并简化了错误处理
- 回调将一直运行，直到当前线程被中断或某些运行时错误导致其返回。

请注意使用PGConnection而不是标准 JDBC Connection。我们需要它可以直接访问getNotifications()方法，该方法返回一个或多个排队的通知。

getNotifications()有两种变体。当不带参数调用时，它会轮询任何挂起的通知并返回它们。如果没有，则返回 null。第二个变体接受一个整数，该整数对应于返回 null 之前等待通知的最长时间。最后，如果我们传递 0(零)作为超时值，getNotifications()将阻塞，直到新通知到达。

在应用程序初始化期间，我们在 @Configuration类中使用 CommandLineRunner bean ，该类将生成一个新线程，实际开始接收通知：

```java
@Configuration
public class ListenerConfiguration {
    
    @Bean
    CommandLineRunner startListener(NotifierService notifier, NotificationHandler handler) {
        return (args) -> {
            Runnable listener = notifier.createNotificationHandler(handler);            
            Thread t = new Thread(listener, "order-listener");
            t.start();
        };
    }
}

```

### 4.3. 连接处理

虽然技术上可行，但使用同一连接处理通知和常规查询并不方便。人们必须将getNotification()的调用与控制流一起传播，从而导致代码难以阅读和维护。

相反，标准做法是运行一个或多个专用线程来处理通知。每个线程都有自己的连接，该连接将始终保持打开状态。如果这些连接是由 Hikari 或 DBCP 等池创建的，这可能会造成问题。

为了避免这些问题，我们的示例创建了一个专用的DriverDataSource，然后我们用它来创建NotifierService所需的JdbcTemplate：

```java
@Configuration
public class NotifierConfiguration {

    @Bean
    NotifierService notifier(DataSourceProperties props) {
        
        DriverDataSource ds = new DriverDataSource(
          props.determineUrl(), 
          props.determineDriverClassName(),
          new Properties(), 
          props.determineUsername(),
          props.determinePassword());
        
        JdbcTemplate tpl = new JdbcTemplate(ds);
        return new NotifierService(tpl);
    }
}

```

请注意，我们共享用于创建主 Spring 管理的数据源的相同连接属性。但是，我们不会将这个专用数据源公开为 bean，这会禁用Spring Boot的自动配置功能。

### 4.4. 通知处理程序

缓存逻辑的最后一部分是NotificationHandler类，它实现了Consumer<Notification>接口。此类的作用是处理单个通知并使用Order实例填充配置的Cache：

```java
@Component
public class NotificationHandler implements Consumer<PGNotification> {
    private final OrdersService orders;

    @Override
    public void accept(PGNotification t) {
        Optional<Order> order = orders.findById(Long.valueOf(t.getParameter()));
        // ... log messages omitted
    }
}

```

该实现使用getName()和getParameter()从通知中检索通道名称和订单标识符。在这里，我们可以假设通知始终是预期的通知。这并不是出于懒惰，而是源于NotifierService构造将在其上调用此处理程序的Runnable 的方式。

实际逻辑很简单：我们使用 OrderRepository从数据库中获取订单并将其添加到缓存中：

```java
@Service
public class OrdersService {
    private final OrdersRepository repo;
    // ... other private fields omitted
   
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        Optional<Order> o = Optional.ofNullable(ordersCache.get(id, Order.class));
        if (!o.isEmpty()) {
            log.info("findById: cache hit, id={}",id);
            return o;
        }        
        log.info("findById: cache miss, id={}",id);
        o = repo.findById(id);
        if ( o.isEmpty()) {
            return o;
        }        
        ordersCache.put(id, o.get());
        return o;
    }
}

```

## 5. 测试

要查看正在运行的通知机制，最好的方法是启动测试应用程序的两个或多个实例，每个实例配置为侦听不同的端口。我们还需要一个工作的 PostgreSQL 实例，两个实例都将连接到该实例。请参阅[application.properties](https://github.com/eugenp/tutorials/tree/master/messaging-modules/postgres-notify/src/main/resources/application.properties)文件并使用你的 PostgreSQL 实例连接详细信息进行修改。

接下来，为了启动我们的测试环境，我们将打开两个 shell 并使用 Maven 来运行应用程序。该项目的pom.xml包含一个附加配置文件instance1，它将在不同的端口上启动应用程序：

```bash
# On first shell:
$ mvn spring-boot:run
... many messages (omitted)
[  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
[  restartedMain] c.b.messaging.postgresql.Application     : Started Application in 2.615 seconds (JVM running for 2.944)
[  restartedMain] c.b.m.p.config.ListenerConfiguration     : Starting order listener thread...
[ order-listener] c.b.m.p.service.NotifierService          : notificationHandler: sending LISTEN command...

## On second shell
... many messages (omitted)
[  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081 (http) with context path ''
[  restartedMain] c.b.messaging.postgresql.Application     : Started Application in 1.984 seconds (JVM running for 2.274)
[  restartedMain] c.b.m.p.config.ListenerConfiguration     : Starting order listener thread...
[ order-listener] c.b.m.p.service.NotifierService          : notificationHandler: sending LISTEN command...

```

一段时间后，我们应该在每个日志上看到一条日志消息，通知我们应用程序已准备好接收请求。现在，让我们在另一个 shell 上使用curl创建第一个订单：

```bash
$ curl --location 'http://localhost:8080/orders/buy' \
--form 'symbol="BAEL"' \
--form 'price="13.34"' \
--form 'quantity="500"'
{"id":30,"symbol":"BAEL","orderType":"BUY","price":13.34,"quantity":500}

```

在端口 8080 上运行的应用程序实例将打印一些消息。我们还将看到 8081 实例日志显示它收到了通知：

```bash
[ order-listener] c.b.m.p.service.NotificationHandler    : Notification received: pid=5141, name=orders, param=30
[ order-listener] c.b.m.postgresql.service.OrdersService : findById: cache miss, id=30
[ order-listener] c.b.m.p.service.NotificationHandler    : order details: Order(id=30, symbol=BAEL, orderType=BUY, price=13.34, quantity=500.00)
```

这证明该机制按预期发挥作用。
最后，我们可以再次使用curl来查询instance1上创建的Order ：

```bash
curl http://localhost:8081/orders/30
{"id":30,"symbol":"BAEL","orderType":"BUY","price":13.34,"quantity":500.00}
```

正如预期的那样，我们获得了订单详细信息。而且，应用程序日志还显示该信息来自缓存：

```bash
[nio-8081-exec-1] c.b.m.postgresql.service.OrdersService   : findById: cache hit, id=30
```

## 六，总结

在本文中，我们介绍了 PostgreSQL 的NOTIFY/LISTEN机制以及如何使用它来实现无需额外组件的轻量级消息代理。
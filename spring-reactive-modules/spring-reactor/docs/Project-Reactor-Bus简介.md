## 1. 概述

在这篇简短的文章中，我们将通过为响应式、事件驱动的应用程序设置真实场景来介绍[Reactor-Bus](https://projectreactor.io/docs/core/snapshot/reference/)。

**注意：reactor-bus项目已在Reactor 3.x中删除**：因此我们项目中的Reactor版本为2.0.8.RELEASE。根据Reactor社区的描述，移除reactor-bus项目只是因为可以使用更好的组件替代，比如“Spring Core event listeners”和“Spring Cloud Stream(RabbitMQ、Kafka)”。

## 2. Reactor基础

### 2.1 为什么选择Reactor？

现代应用程序需要处理大量的并发请求并处理大量数据，标准的阻塞代码不再足以满足这些要求。

[响应式设计模式](https://en.wikipedia.org/wiki/Reactor_pattern)**是一种基于事件的架构方法，用于异步处理来自单个或多个服务处理程序的大量并发服务请求**。

而Project Reactor基于这种模式，并有一个明确的目标和积极维护的开发社区，旨在在JVM上构建非阻塞、响应式应用程序。

### 2.2 示例场景

在我们开始之前，这里有一些有趣的场景，在这些场景中利用响应式架构风格是有意义的，只是为了了解我们可以在哪里应用它：

+ 淘宝、京东、亚马逊等大型在线购物平台的通知服务
+ 为银行业提供庞大的交易处理服务
+ 股票价格同时变动的股票交易业务

## 3. Maven依赖

让我们通过在pom.xml中添加以下依赖项来开始使用Project Reactor Bus：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-bus</artifactId>
    <version>2.0.8.RELEASE</version>
</dependency>
```

我们可以在Maven Central查看最新版本的[reactor-bus](https://search.maven.org/search?q=g:io.projectreactor)。

## 4. 构建演示应用程序

为了更好地理解基于响应式的方法的好处，让我们看一个实际的例子。

我们将构建一个简单的应用程序，负责向在线购物平台的用户发送通知。例如，如果用户下了一个新订单，则应用程序会通过电子邮件或短信发送订单确认。

如果按照我们典型的同步实现，自然会受到电子邮件或短信服务吞吐量的限制。因此，流量高峰时期(例如假期)通常会造成问题。

通过响应式的方法，我们可以将系统设计得更加灵活，并更好地适应外部系统(例如网关服务器)中可能发生的故障或超时。

让我们来看看这个应用程序-从更传统的方面开始，然后转到更具响应性的构造。

### 4.1 POJO

首先，让我们创建一个POJO类来表示通知数据：

```java
public class NotificationData {

    private long id;
    private String name;
    private String email;
    private String mobile;

    // getter and setter methods
}
```

### 4.2 Service层

现在让我们定义一个简单的Service层：

```java
public interface NotificationService {
    void initiateNotification(NotificationData notificationData) throws InterruptedException;
}
```

以及接口实现，我们在其中模拟一个长时间运行的操作：

```java
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void initiateNotification(NotificationData notificationData) throws InterruptedException {
        LOGGER.info("Notification service started for Notification ID: {}", notificationData.getId());

        Thread.sleep(5000);

        LOGGER.info("Notification service ended for Notification ID: {}", notificationData.getId());
    }
}
```

请注意，为了说明通过短信或电子邮件网关发送消息的真实场景，我们在initialNotification方法中有意地通过Thread.sleep(5000)调用引入5秒钟延迟。

因此，当一个线程访问该服务时，它将被阻塞五秒钟。

### 4.3 消费者

现在让我们进入我们应用程序的更具响应性的方面并实现一个消费者-然后我们将其映射到Reactor事件总线：

```java
@Service
public class NotificationConsumer implements Consumer<Event<NotificationData>> {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void accept(Event<NotificationData> notificationDataEvent) {
        NotificationData notificationData = notificationDataEvent.getData();

        try {
            notificationService.initiateNotification(notificationData);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
```

如我们所见，我们创建的消费者实现了Consumer<T\>接口，主要逻辑驻留在accept方法中。

这是我们可以在典型的[Spring监听器](https://www.baeldung.com/spring-events#listener)实现中遇到的类似方法。

### 4.4 控制器

最后，既然我们能够消费事件，那么我们也需要生成他们。

我们将在一个简单的控制器中做到这一点：

```java
@Slf4j
@RestController
public class NotificationController {

    @Autowired
    private EventBus eventBus;

    @GetMapping("/startNotification/{param}")
    public void startNotification(@PathVariable Integer param) {
        for (int i = 0; i < param; i++) {
            NotificationData data = new NotificationData();
            data.setId(i);
            eventBus.notify("notificationConsumer", Event.wrap(data));

            LOGGER.info("Notification {} : notification task submitted successfully", i);
        }
    }
}
```

显然，我们注入了一个EventBus对象，在这里通过它来发出事件。

例如，如果客户端访问该URL并传递param的值为10，则将通过事件总线发送10个事件。

### 4.5 Java配置

现在让我们将所有内容放在一起并创建一个简单的[Spring Boot](https://www.baeldung.com/spring-boot)应用程序。

首先，我们需要配置EventBus和Environment bean：

```java
@Configuration
public class Config {

    @Bean
    public Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    public EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }
}
```

在我们的例子中，**我们使用Environment中可用的默认线程池来实例化EventBus**。

或者，我们可以使用自定义的Dispatcher实例：

```java
EventBus evBus = EventBus.create(env, 
    Environment.newDispatcher(REACTOR_CAPACITY, REACTOR_CONSUMERS_COUNT, DispatcherType.THREAD_POOL_EXECUTOR));
```

现在，我们准备创建一个主应用程序代码：

```java
import static reactor.bus.selector.Selectors.$;

@SpringBootApplication
public class NotificationApplication implements CommandLineRunner {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private NotificationConsumer notificationConsumer;

    @Override
    public void run(String... args) throws Exception {
        eventBus.on($("notificationConsumer"), notificationConsumer);
    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}
```

在我们的run方法中，**我们注册了notificationConsumer，以便在通知匹配给定选择器时触发**。

请注意，我们如何使用$属性的静态导入来创建Selector对象。

## 5. 测试用例

现在让我们创建一个测试来查看我们的NotificationApplication的运行情况：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationApplicationIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void givenAppStarted_whenNotificationTasksSubmitted_thenProcessed() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject("http://localhost:" + port + "/startNotification/10", String.class);
    }
}
```

如我们所见，**一旦请求被执行，所有十个任务都会立即提交，而不会造成任何阻塞**。一旦提交，通知事件就会被并行处理：

```java
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 0 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 1 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 2 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 3 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 4 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 5 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 6 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 7 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 8 : notification task submitted successfully 
... [http-nio-auto-1-exec-1] INFO  [c.t.t.r.c.NotificationController] >>> Notification 9 : notification task submitted successfully 
... [threadPoolExecutor-3] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 2 
... [threadPoolExecutor-2] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 1 
... [threadPoolExecutor-7] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 6 
... [threadPoolExecutor-8] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 7 
... [threadPoolExecutor-4] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 3 
... [threadPoolExecutor-6] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 5 
... [threadPoolExecutor-1] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 0 
... [threadPoolExecutor-5] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 4 
... [threadPoolExecutor-7] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 6 
... [threadPoolExecutor-3] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 2 
... [threadPoolExecutor-7] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 8 
... [threadPoolExecutor-3] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service started for Notification ID: 9 
... [threadPoolExecutor-2] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 1 
... [threadPoolExecutor-8] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 7 
... [threadPoolExecutor-5] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 4 
... [threadPoolExecutor-4] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 3 
... [threadPoolExecutor-6] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 5 
... [threadPoolExecutor-1] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 0 
... [threadPoolExecutor-7] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 8 
... [threadPoolExecutor-3] INFO  [c.t.t.r.s.i.NotificationServiceImpl] >>> Notification service ended for Notification ID: 9 
```

重要的是要记住，**在我们的场景中，不需要以任何特定的顺序处理这些事件**。

## 6. 总结

在本快速教程中，我们创建了一个简单的事件驱动应用程序，我们还了解了如何开始编写更具响应性和非阻塞性的代码。

然而，**这个场景只是触及了主题的表面，并且代表了开始尝试响应式范式的良好基础**。
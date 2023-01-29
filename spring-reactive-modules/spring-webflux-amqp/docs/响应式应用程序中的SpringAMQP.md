## 1. 概述

本教程展示了如何创建一个简单的Spring Boot响应式应用程序，该应用程序与RabbitMQ消息传递服务器集成，RabbitMQ消息传递服务器是AMQP消息传递标准的一种流行实现。

我们涵盖了点对点和发布-订阅场景，使用分布式设置突出显示了两种模式之间的差异。

请注意，我们假设你具备AMQP、RabbitMQ和Spring Boot的基本知识，尤其是交换机、队列、主题等关键概念。有关这些概念的更多信息，请参见以下链接：

-   [使用Spring AMQP进行消息传递](https://www.baeldung.com/spring-amqp)
-   [RabbitMQ简介](https://www.baeldung.com/rabbitmq)

## 2. RabbitMQ服务器设置

虽然我们可以在本地设置本地RabbitMQ，但在实践中，我们更有可能使用具有高可用性、监控、安全性等附加功能的专用安装。

为了在我们的开发机器中模拟这样的环境，我们将使用Docker创建我们的应用程序将使用的服务器。

以下命令将启动一个独立的RabbitMQ服务器：

```bash
$ docker run -d --name rabbitmq -p 5672:5672 rabbitmq:3
```

我们不会声明任何持久卷，因此未读消息将在重新启动之间丢失。该服务将在主机上的端口5672可用。

我们可以使用docker logs命令检查服务器日志，它应该会产生如下输出：

```bash
$ docker logs rabbitmq
2018-06-09 13:42:29.718 [info] <0.33.0>
  Application lager started on node rabbit@rabbit
// ... some lines omitted
2018-06-09 13:42:33.491 [info] <0.226.0>
 Starting RabbitMQ 3.7.5 on Erlang 20.3.5
 Copyright (C) 2007-2018 Pivotal Software, Inc.
 Licensed under the MPL.  See http://www.rabbitmq.com/

  ##  ##
  ##  ##      RabbitMQ 3.7.5. Copyright (C) 2007-2018 Pivotal Software, Inc.
  ##########  Licensed under the MPL.  See http://www.rabbitmq.com/
  ######  ##
  ##########  Logs: <stdout>

              Starting broker...
2018-06-09 13:42:33.494 [info] <0.226.0>
 node           : rabbit@rabbit
 home dir       : /var/lib/rabbitmq
 config file(s) : /etc/rabbitmq/rabbitmq.conf
 cookie hash    : CY9rzUYh03PK3k6DJie09g==
 log(s)         : <stdout>
 database dir   : /var/lib/rabbitmq/mnesia/rabbit@rabbit

// ... more log lines
```

由于镜像包含rabbitmqctl实用程序，因此我们可以使用它在运行镜像的上下文中执行管理任务。

例如，我们可以使用以下命令获取服务器状态信息：

```bash
$ docker exec rabbitmq rabbitmqctl status
Status of node rabbit@rabbit ...
[{pid,299},
 {running_applications,
     [{rabbit,"RabbitMQ","3.7.5"},
      {rabbit_common,
          "Modules shared by rabbitmq-server and rabbitmq-erlang-client",
          "3.7.5"},
// ... other info omitted for brevity
```

其他有用的命令包括：

-   list_exchanges：列出所有声明的交换机
-   list_queues：列出所有声明的队列，包括未读消息的数量
-   list_bindings：列出所有定义了exchange和queue之间的Bindings，也包括routing keys

## 3. Spring AMQP项目设置

一旦我们启动并运行了RabbitMQ服务器，我们就可以继续创建我们的Spring项目。此示例项目将允许任何REST客户端使用Spring AMQP模块和相应的Spring Boot starter向消息传递服务器发送和/或接收消息，以便与其通信。

我们需要添加到pom.xml项目文件中的主要依赖项是：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
    <version>2.0.3.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>2.0.2.RELEASE</version> 
</dependency>
```

spring-boot-starter-amqp带来了所有与AMQP相关的东西，而spring-boot-starter-webflux是用于实现我们的响应式REST服务器的核心依赖项。

注意：你可以在Maven Central上查看最新版本的Spring Boot Starter [AMQP](https://search.maven.org/search?q=a:spring-boot-starter-amqp)和[Webflux](https://search.maven.org/search?q=a:spring-boot-starter-webflux)模块。

## 4. 场景一：点对点消息传递

在第一个场景中，我们将使用Direct交换机，它是代理中接收来自客户端的消息的逻辑实体。

**Direct交换机会将所有传入的消息路由到一个且只有一个队列**，客户端可以从该队列使用。多个客户端可以订阅同一个队列，但只有一个客户端会收到给定的消息。

### 4.1 交换机和队列设置

在我们的场景中，我们使用封装交换机名称和路由键的DestinationInfo对象。以目的地名称为键的Map将用于存储所有可用目的地。

以下@PostConstruct方法将负责此初始设置：

```java
@Autowired
private AmqpAdmin amqpAdmin;
    
@Autowired
private DestinationsConfig destinationsConfig;

@PostConstruct
public void setupQueueDestinations() {
    destinationsConfig.getQueues()
        .forEach((key, destination) -> {
            Exchange ex = ExchangeBuilder.directExchange(destination.getExchange())
                .durable(true)
                .build();
            amqpAdmin.declareExchange(ex);
            Queue q = QueueBuilder.durable(destination.getRoutingKey())
                .build();
            amqpAdmin.declareQueue(q);
            Binding b = BindingBuilder.bind(q)
                .to(ex)
                .with(destination.getRoutingKey())
                .noargs();
            amqpAdmin.declareBinding(b);
        });
}
```

此方法使用Spring创建的adminAmqp bean来声明交换机、队列并使用给定的路由键将它们绑定在一起。

所有目的地都来自DestinationsConfig bean，它是我们示例中使用的@ConfigurationProperties类。

此类具有一个属性，该属性使用从application.yml配置文件中读取的Map构建的DestinationInfo对象进行填充。

### 4.2 生产者端点

生产者将通过向/queue/{name}位置发送HTTP POST来发送消息。

这是一个响应式端点，所以我们使用Mono来返回一个简单的确认：

```java
@SpringBootApplication
@EnableConfigurationProperties(DestinationsConfig.class)
@RestController
public class SpringWebfluxAmqpApplication {

    // ... other members omitted

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostMapping(value = "/queue/{name}")
    public Mono<ResponseEntity<?>> sendMessageToQueue(
          @PathVariable String name, @RequestBody String payload) {

        DestinationInfo d = destinationsConfig
              .getQueues().get(name);
        if (d == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        return Mono.fromCallable(() -> {
            amqpTemplate.convertAndSend(
                  d.getExchange(),
                  d.getRoutingKey(),
                  payload);
            return ResponseEntity.accepted().build();
        });
    }
}
```

我们首先检查name参数是否对应于一个有效的目的地，如果是，我们使用自动装配的amqpTemplate实例实际发送有效负载(一个简单的字符串消息)到RabbitMQ。

### 4.3 MessageListener容器工厂

为了异步接收消息，Spring AMQP使用MessageContainerListener抽象类来调解来自应用程序提供的AMQP队列和监听器的信息流。

由于我们需要此类的具体实现来附加我们的消息监听器，因此我们定义了一个工厂，将控制器代码与其实际实现隔离开来。

在我们的例子中，每次调用createMessageListenerContainer方法时，工厂方法都会返回一个新的SimpleMessageContainerListener：

```java
@Component
public class MessageListenerContainerFactory {

    @Autowired
    private ConnectionFactory connectionFactory;

    public MessageListenerContainerFactory() {}

    public MessageListenerContainer createMessageListenerContainer(String queueName) {
        SimpleMessageListenerContainer mlc = new SimpleMessageListenerContainer(connectionFactory);
        mlc.addQueueNames(queueName);
        return mlc;
    }
}
```

### 4.4 消费者端点

消费者将访问生产者使用的相同端点地址(/queue/{name})以获取消息。

此端点返回事件的Flux，其中每个事件对应于收到的消息：

```java
@Autowired
private MessageListenerContainerFactory messageListenerContainerFactory;

@GetMapping(
    value = "/queue/{name}",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<?> receiveMessagesFromQueue(@PathVariable String name) {

    DestinationInfo d = destinationsConfig
        .getQueues()
        .get(name);
    if (d == null) {
        return Flux.just(ResponseEntity.notFound()
            .build());
    }

    MessageListenerContainer mlc = messageListenerContainerFactory
        .createMessageListenerContainer(d.getRoutingKey());

    Flux<String> f = Flux.<String> create(emitter -> {
        mlc.setupMessageListener((MessageListener) m -> {
            String payload = new String(m.getBody());
            emitter.next(payload);
        });
        emitter.onRequest(v -> {
            mlc.start();
        });
        emitter.onDispose(() -> {
            mlc.stop();
        });
      });

    return Flux.interval(Duration.ofSeconds(5))
        .map(v -> "No news is good news")
        .mergeWith(f);
}
```

在对目标名称进行初始检查后，消费者端点使用MessageListenerContainerFactory和从我们的注册表中恢复的队列名称创建MessageListenerContainer。

一旦我们有了MessageListenerContainer，我们就使用它的create()构建器方法之一创建消息Flux。

在我们的特定情况下，我们使用一个带有FluxSink参数的lambda，然后我们使用它来将Spring AMQP基于监听器的异步API桥接到我们的响应式应用程序。

我们还将两个额外的lambda表达式附加到发射器的onRequest()和onDispose()回调，以便我们的MessageListenerContainer可以在Flux的生命周期之后分配/释放其内部资源。

最后，我们将生成的Flux与另一个使用interval()创建的Flux合并，后者每五秒创建一个新事件。**这些虚拟消息在我们的案例中起着重要作用**：如果没有它们，我们只会在收到消息但未能发送消息时检测到客户端断开连接，这可能需要很长时间，具体取决于你的特定用例。

### 4.5 测试

通过我们的消费者和发布者端点设置，我们现在可以使用我们的示例应用程序进行一些测试。

我们需要在application.yml中定义RabbitMQ的服务器连接详细信息和至少一个目的地，它应该如下所示：

```yaml
spring:
    rabbitmq:
        host: localhost
        port: 5672
        username: guest
        password: guest

destinations:
    queues:
        NYSE:
            exchange: nyse
            routing-key: NYSE
```

spring.rabbitmq.*属性定义了连接到在本地Docker容器中运行的RabbitMQ服务器所需的基本属性。请注意，上面显示的IP只是一个示例，在特定设置中可能会有所不同。

队列使用destinations.queues.<name>.*定义，其中<name>用作目标名称。在这里，我们声明了一个名为“NYSE”的单一目的地，它将使用“NYSE”路由键将消息发送到RabbitMQ上的“nyse”交换机。

一旦我们通过命令行或我们的IDE启动服务器，我们就可以开始发送和接收消息。我们将使用curl实用程序，这是一种适用于Windows、Mac和Linux操作系统的通用实用程序。

以下清单显示了如何将消息发送到我们的目的地以及来自服务器的预期响应：

```bash
$ curl -v -d "Test message" http://localhost:8080/queue/NYSE
* timeout on name lookup is not supported
*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> POST /queue/NYSE HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.49.1
> Accept: */*
> Content-Length: 12
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 12 out of 12 bytes
< HTTP/1.1 202 Accepted
< content-length: 0
<
* Connection #0 to host localhost left intact
```

执行此命令后，我们可以验证消息是否已被RabbitMQ接收并准备好使用，执行以下命令：

```bash
$ docker exec rabbitmq rabbitmqctl list_queues
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
NYSE    1
```

现在我们可以使用以下命令使用curl读取消息：

```bash
$ curl -v http://localhost:8080/queue/NYSE
* timeout on name lookup is not supported
*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /queue/NYSE HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.49.1
> Accept: */*
>
< HTTP/1.1 200 OK
< transfer-encoding: chunked
< Content-Type: text/event-stream;charset=UTF-8
<
data:Test message

data:No news is good news...

... same message repeating every 5 secs
```

正如我们所看到的，首先我们得到了之前存储的消息，然后我们开始每5秒接收一次我们的虚拟消息。

如果我们再次运行列出队列的命令，我们现在可以看到没有存储任何消息：

```bash
$ docker exec rabbitmq rabbitmqctl list_queues
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
NYSE    0
```

## 5. 场景二：发布-订阅

消息传递应用程序的另一个常见场景是发布-订阅模式，其中一条消息必须发送给多个消费者。

RabbitMQ提供了两种类型的交换机来支持这些类型的应用程序：Fanout和Topic。

这两种类型之间的主要区别在于，后者允许我们根据注册时提供的路由键模式(例如“alarm.mailserver.*”)过滤接收哪些消息，而前者只是简单地将传入消息复制到所有绑定队列。

RabbitMQ还支持Header交换机，它允许进行更复杂的消息过滤，但它的使用超出了本文的范围。

### 5.1 目的地设置

我们在启动时使用另一个@PostConstruct方法定义Pub/Sub目的地，就像我们在点对点场景中所做的那样。

唯一的区别是我们只创建Exchanges，但没有创建Queues-这些将按需创建并稍后绑定到Exchange，因为我们希望为每个客户端创建一个独占的Queue：

```java
@PostConstruct
public void setupTopicDestinations(
    destinationsConfig.getTopics()
        .forEach((key, destination) -> {
            Exchange ex = ExchangeBuilder
                .topicExchange(destination.getExchange())
                .durable(true)
                .build();
                amqpAdmin.declareExchange(ex);
        });
}
```

### 5.2 发布者端点

客户端将使用/topic/{name}位置可用的发布者端点来发布将发送给所有连接的客户端的消息。

与前面的场景一样，我们使用@PostMapping在发送消息后返回具有状态的Mono：

```java
@PostMapping(value = "/topic/{name}")
public Mono<ResponseEntity<?>> sendMessageToTopic(@PathVariable String name, @RequestBody String payload) {

    DestinationInfo d = destinationsConfig
        .getTopics()
        .get(name);
    
    if (d == null) {
        return Mono.just(ResponseEntity.notFound().build());
    }      
    
   return Mono.fromCallable(() -> {
       amqpTemplate.convertAndSend(
            d.getExchange(), d.getRoutingKey(),payload);   
                return ResponseEntity.accepted().build();
            });
    }
}
```

### 5.3 订阅者端点

我们的订阅者端点将位于/topic/{name}，为连接的客户端生成消息流。

这些消息包括接收到的消息和每5秒生成的虚拟消息：

```java
@GetMapping(
    value = "/topic/{name}",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<?> receiveMessagesFromTopic(@PathVariable String name) {
    DestinationInfo d = destinationsConfig.getTopics()
        .get(name);
    if (d == null) {
        return Flux.just(ResponseEntity.notFound()
            .build());
    }
    Queue topicQueue = createTopicQueue(d);
    String qname = topicQueue.getName();
    MessageListenerContainer mlc = messageListenerContainerFactory.createMessageListenerContainer(qname);
    Flux<String> f = Flux.<String> create(emitter -> {
        mlc.setupMessageListener((MessageListener) m -> {
            String payload = new String(m.getBody());
            emitter.next(payload);
        });
        emitter.onRequest(v -> {
            mlc.start();
        });
        emitter.onDispose(() -> {
            amqpAdmin.deleteQueue(qname);
            mlc.stop();
        });            
      });
    
    return Flux.interval(Duration.ofSeconds(5))
        .map(v -> "No news is good news")
        .mergeWith(f);
}
```

这段代码与我们在前面的案例中看到的基本相同，只有以下区别：首先，我们为每个新订阅者创建一个新的队列。

我们通过调用createTopicQueue()方法来做到这一点，该方法使用来自DestinationInfo实例中的信息来创建一个独占的非持久队列，然后我们使用配置的路由键将其绑定到Exchange：

```java
private Queue createTopicQueue(DestinationInfo destination) {
    Exchange ex = ExchangeBuilder
        .topicExchange(destination.getExchange())
        .durable(true)
        .build();
    amqpAdmin.declareExchange(ex);
    Queue q = QueueBuilder
        .nonDurable()
        .build();     
    amqpAdmin.declareQueue(q);
    Binding b = BindingBuilder.bind(q)
        .to(ex)
        .with(destination.getRoutingKey())
        .noargs();        
    amqpAdmin.declareBinding(b);
    return q;
}
```

请注意，尽管我们再次声明了Exchange，但RabbitMQ不会创建一个新的交换机，因为我们已经在启动时声明了它。

第二个区别是我们传递给onDispose()方法的lambda，这次它也会在订阅者断开连接时删除Queue。

### 5.4 测试

为了测试Pub-Sub场景，我们必须首先在application.yml中定义一个主题目的地，如下所示：

```yaml
destinations:
    ## ... queue destinations omitted      
    topics:
        weather:
            exchange: alerts
            routing-key: WEATHER
```

在这里，我们定义了一个主题端点，它将在/topic/weather位置可用。该端点将用于使用“WEATHER”路由键将消息发布到RabbitMQ上的“alerts”交换机。

启动服务器后，我们可以使用rabbitmqctl命令验证交换机是否已创建：

```bash
$ docker exec docker_rabbitmq_1 rabbitmqctl list_exchanges
Listing exchanges for vhost / ...
amq.topic       topic
amq.fanout      fanout
amq.match       headers
amq.headers     headers
        direct
amq.rabbitmq.trace      topic
amq.direct      direct
alerts  topic
```

现在，如果我们运行list_bindings命令，我们可以看到没有与“alerts”交换机相关的队列：

```bash
$ docker exec rabbitmq rabbitmqctl list_bindings
Listing bindings for vhost /...
        exchange        NYSE    queue   NYSE    []
nyse    exchange        NYSE    queue   NYSE    []
```

让我们通过打开两个命令shell并在每个命令shell上发出以下命令来启动几个将订阅我们目的地的订阅者：

```bash
$ curl -v http://localhost:8080/topic/weather
* timeout on name lookup is not supported
*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /topic/weather HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.49.1
> Accept: */*
>
< HTTP/1.1 200 OK
< transfer-encoding: chunked
< Content-Type: text/event-stream;charset=UTF-8
<
data:No news is good news...

# ... same message repeating indefinitely
```

最后，我们再次使用curl向我们的订阅者发送一些警报：

```bash
$ curl -v -d "Hurricane approaching!" http://localhost:8080/topic/weather
* timeout on name lookup is not supported
*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> POST /topic/weather HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.49.1
> Accept: */*
> Content-Length: 22
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 22 out of 22 bytes
< HTTP/1.1 202 Accepted
< content-length: 0
<
* Connection #0 to host localhost left intact
```

发送消息后，我们几乎可以立即在每个订阅者的shell上看到消息“Hurricane approaching!”。

如果我们现在检查可用的绑定，我们可以看到每个订阅者都有一个队列：

```bash
$ docker exec rabbitmq rabbitmqctl list_bindings
Listing bindings for vhost /...
        exchange        IBOV    queue   IBOV    []
        exchange        NYSE    queue   NYSE    []
        exchange        spring.gen-i0m0pbyKQMqpz2_KFZCd0g       
  queue   spring.gen-i0m0pbyKQMqpz2_KFZCd0g       []
        exchange        spring.gen-wCHALTsIS1q11PQbARJ7eQ       
  queue   spring.gen-wCHALTsIS1q11PQbARJ7eQ       []
alerts  exchange        spring.gen-i0m0pbyKQMqpz2_KFZCd0g     
  queue   WEATHER []
alerts  exchange        spring.gen-wCHALTsIS1q11PQbARJ7eQ     
  queue   WEATHER []
ibov    exchange        IBOV    queue   IBOV    []
nyse    exchange        NYSE    queue   NYSE    []
quotes  exchange        NYSE    queue   NYSE    []
```

一旦我们在订阅者的shell上按下Ctrl-C，我们的网关最终将检测到客户端已断开连接并将删除这些绑定。

## 6. 总结

在本文中，我们演示了如何使用spring-amqp模块创建一个与RabbitMQ服务器交互的简单响应式应用程序。

只需几行代码，我们就能够创建一个支持点对点和发布-订阅集成模式的功能性HTTP-to-AMQP网关，我们可以轻松扩展它以添加额外的功能，例如通过添加标准Spring功能的安全性。
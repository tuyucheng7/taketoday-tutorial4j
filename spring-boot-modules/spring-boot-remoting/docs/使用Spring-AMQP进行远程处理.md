## 1. 概述

在本系列的[前几部分](https://www.baeldung.com/spring-remoting-http-invoker)中，我们看到了如何利用Spring Remoting和相关技术在服务器和客户端之间的HTTP通道之上启用同步远程过程调用。

在本文中，我们将探索基于AMQP的Spring Remoting，它可以在利用本质上异步的媒介的同时执行同步RPC。

## 2. 安装RabbitMQ

我们可以使用多种与AMQP兼容的消息传递系统，我们选择RabbitMQ是因为它是一个经过验证的平台，并且在Spring中得到完全支持-这两种产品都由同一家公司(Pivotal)管理。

如果你不熟悉AMQP或RabbitMQ，你可以阅读我们的[快速介绍](https://www.baeldung.com/rabbitmq)。

因此，第一步是安装并启动RabbitMQ。有多种安装方法-只需按照[官方指南](https://www.rabbitmq.com/download.html)中提到的步骤选择你喜欢的方法即可。

## 3. Maven依赖

我们将设置服务器和客户端Spring Boot应用程序以展示AMQP Remoting的工作原理。与Spring Boot通常的情况一样，我们只需选择并导入正确的启动器依赖项，[如下所述](https://www.baeldung.com/spring-boot-starters)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

我们明确排除了spring-boot-starter-tomcat，因为我们不需要任何嵌入式HTTP服务器-如果我们允许Maven导入类路径中的所有传递依赖项，它将自动启动。

## 4. 服务器应用

### 4.1 公开服务

正如我们在之前的文章中展示的那样，我们将公开一个模拟可能的远程服务的CabBookingService。

让我们首先声明一个bean，它实现了我们想要远程调用的服务的接口。这是将在服务器端实际执行服务调用的bean：

```java
@Bean 
CabBookingService bookingService() {
    return new CabBookingServiceImpl();
}
```

然后让我们定义服务器将从中检索调用的队列。在这种情况下，为它指定一个名称就足够了，在构造函数中提供它：

```java
@Bean 
Queue queue() {
    return new Queue("remotingQueue");
}
```

正如我们已经从之前的文章中了解到的那样，Spring Remoting的主要概念之一是服务导出器，该组件实际上从某个来源(在本例中为RabbitMQ队)收集调用请求并调用服务上所需的方法实现。

在这种情况下，我们定义了一个AmqpInvokerServiceExporter，正如你所看到的，它需要对AmqpTemplate的引用。AmqpTemplate类由Spring框架提供，它简化了与AMQP兼容的消息系统的处理，就像JdbcTemplate使处理数据库更容易一样。

我们不会显式定义这样的AmqpTemplate bean，因为它将由Spring Boot的自动配置模块自动提供：

```java
@Bean AmqpInvokerServiceExporter exporter(CabBookingService implementation, AmqpTemplate template) {
    AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
    exporter.setServiceInterface(CabBookingService.class);
    exporter.setService(implementation);
    exporter.setAmqpTemplate(template);
    return exporter;
}
```

最后，我们需要定义一个容器，负责从队列中消费消息并将它们转发给某个指定的监听器。

然后我们将这个容器连接到我们在上一步中创建的服务导出器，以允许它接收排队的消息。这里的ConnectionFactory由Spring Boot自动提供，就像AmqpTemplate一样：

```java
@Bean 
SimpleMessageListenerContainer listener(ConnectionFactory facotry, AmqpInvokerServiceExporter exporter, Queue queue) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(facotry);
    container.setMessageListener(exporter);
    container.setQueueNames(queue.getName());
    return container;
}
```

### 4.2 配置

让我们记住设置application.properties文件以允许Spring Boot配置基本对象。显然，参数的值也将取决于RabbitMQ的安装方式。

例如，当RabbitMQ在运行此示例的同一台机器上运行时，以下配置可能是合理的配置：

```properties
spring.rabbitmq.dynamic=true
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.host=localhost
```

## 5. 客户端应用

### 5.1 调用远程服务

现在让我们来处理客户端。同样，我们需要定义将写入调用消息的队列。我们需要仔细检查客户端和服务器是否使用相同的名称。

```java
@Bean 
Queue queue() {
    return new Queue("remotingQueue");
}
```

在客户端，我们需要比服务器端稍微复杂的设置。事实上，我们需要定义一个带有相关Binding的Exchange：

```java
@Bean 
Exchange directExchange(Queue someQueue) {
    DirectExchange exchange = new DirectExchange("remoting.exchange");
    BindingBuilder
        .bind(someQueue)
        .to(exchange)
        .with("remoting.binding");
    return exchange;
}
```

[此处](https://www.rabbitmq.com/tutorials/tutorial-four-java.html)提供了关于RabbitMQ作为Exchange和Binding的主要概念的一个很好的介绍。

由于Spring Boot不会自动配置AmqpTemplate，因此我们必须自己设置一个，并指定路由键。为此，我们需要仔细检查路由键和交换机是否与上一步中用于定义交换机的匹配：

```java
@Bean RabbitTemplate amqpTemplate(ConnectionFactory factory) {
    RabbitTemplate template = new RabbitTemplate(factory);
    template.setRoutingKey("remoting.binding");
    template.setExchange("remoting.exchange");
    return template;
}
```

然后，就像我们对其他Spring Remoting实现所做的那样，我们定义了一个FactoryBean，它将生成远程公开的服务的本地代理。这里没什么特别的，我们只需要提供远程服务的接口：

```java
@Bean AmqpProxyFactoryBean amqpFactoryBean(AmqpTemplate amqpTemplate) {
    AmqpProxyFactoryBean factoryBean = new AmqpProxyFactoryBean();
    factoryBean.setServiceInterface(CabBookingService.class);
    factoryBean.setAmqpTemplate(amqpTemplate);
    return factoryBean;
}
```

现在，我们可以像使用本地bean一样使用远程服务：

```java
CabBookingService service = context.getBean(CabBookingService.class);
out.println(service.bookRide("13 Seagate Blvd, Key Largo, FL 33037"));
```

### 5.2 设置

同样对于客户端应用程序，我们必须正确选择application.properties文件中的值。在常见的设置中，这些将与服务器端使用的完全匹配。

### 5.3 运行示例

这应该足以演示通过RabbitMQ进行的远程调用。然后让我们启动RabbitMQ、服务器应用程序和调用远程服务的客户端应用程序。

在幕后发生的事情是AmqpProxyFactoryBean将构建一个实现CabBookingService的代理。

当在该代理上调用方法时，它会在RabbitMQ上对消息进行排队，在其中指定调用的所有参数和用于发回结果的队列的名称。

该消息从调用实际实现的AmqpInvokerServiceExporter消费。然后它将结果收集到一条消息中，并将其放在传入消息中指定名称的队列中。

AmqpProxyFactoryBean收到返回的结果，最后返回最初在服务器端生成的值。

## 6. 总结

在本文中，我们了解了如何使用Spring Remoting在消息系统之上提供RPC。

对于我们可能更喜欢利用RabbitMQ的异步性的主要场景，这可能不是我们更喜欢的方法，但在某些选定和有限的场景中，同步调用可以更容易理解并且开发起来更快更简单。
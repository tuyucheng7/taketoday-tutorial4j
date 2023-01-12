## **一、概述**

[在本系列的前几期中](https://www.baeldung.com/spring-remoting-http-invoker)，我们看到了如何利用*Spring Remoting*和相关技术在服务器和客户端之间的 HTTP 通道之上启用同步*远程过程调用。*

在本文中，我们将**探索基于\*AMQP的\**Spring Remoting\*，它可以在利用本质上异步的媒介的同时执行同步\*RPC\***。

## **2.安装RabbitMQ**

我们可以使用多种与*AMQP*兼容的消息传递系统，我们选择*RabbitMQ*是因为它是一个经过验证的平台，并且在*Spring 中得到完全支持——*这两种产品都由同一家公司 (Pivotal) 管理。

如果您不熟悉*AMQP*或*RabbitMQ*，您可以阅读我们的[快速介绍](https://www.baeldung.com/rabbitmq)。

因此，第一步是安装并启动*RabbitMQ*。有多种安装方法——只需[按照官方指南](https://www.rabbitmq.com/download.html)中提到的步骤选择您喜欢的方法即可。

## **3.Maven依赖**

我们将设置服务器和客户端*Spring Boot*应用程序以展示*AMQP Remoting*的工作原理。与*Spring Boot*的情况一样，我们只需选择并导入正确的启动器依赖项，[如下所述](https://www.baeldung.com/spring-boot-starters)：

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
</dependency>复制
```

我们明确排除了*spring-boot-starter-tomcat*，因为我们不需要任何嵌入式*HTTP服务器——如果我们允许**Maven*导入类路径中的所有传递依赖项，它将自动启动。

## **4.服务器应用**

### **4.1. 公开服务**

正如我们在之前的文章中展示的那样，我们将公开一个模拟可能的远程服务的*CabBookingService 。*

**让我们首先声明一个 bean，它实现了我们想要远程调用的服务的接口。**这是将在服务器端实际执行服务调用的 bean：

```java
@Bean 
CabBookingService bookingService() {
    return new CabBookingServiceImpl();
}复制
```

**然后让我们定义服务器将从中检索调用的队列。**在这种情况下，为它指定一个名称就足够了，在构造函数中提供它：

```java
@Bean 
Queue queue() {
    return new Queue("remotingQueue");
}复制
```

正如我们已经从之前的文章中了解到的那样，***Spring Remoting\*****的主要概念之一是\*服务导出器\***，该组件实际上从某个来源**收集调用请求——在本例中为***RabbitMQ*队列**——并调用服务上所需的方法实施**。

在这种情况下，我们定义了一个*AmqpInvokerServiceExporter* ──如您所见──需要对*AmqpTemplate*的引用。AmqpTemplate类由*Spring Framework提供，**它*简化了与 AMQP 兼容的消息系统的处理，*就像*JdbcTemplate*使*处理数据库更容易一样。

我们不会显式定义这样*的 AmqpTemplate* bean，因为它将由*Spring Boot*的自动配置模块自动提供：

```java
@Bean AmqpInvokerServiceExporter exporter(
  CabBookingService implementation, AmqpTemplate template) {
 
    AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
    exporter.setServiceInterface(CabBookingService.class);
    exporter.setService(implementation);
    exporter.setAmqpTemplate(template);
    return exporter;
}复制
```

最后，我们需要**定义一个\*容器\*，负责从队列中消费消息并将它们转发给某个指定的监听器**。

然后我们将**这个\*容器\*连接到**我们在上一步中创建的***服务导出\*****器，以允许它接收排队的消息**。这里的*ConnectionFactory*由*Spring Boot*自动提供，就像*AmqpTemplate*一样：

```java
@Bean 
SimpleMessageListenerContainer listener(
  ConnectionFactory facotry, 
  AmqpInvokerServiceExporter exporter, 
  Queue queue) {
 
    SimpleMessageListenerContainer container
     = new SimpleMessageListenerContainer(facotry);
    container.setMessageListener(exporter);
    container.setQueueNames(queue.getName());
    return container;
}复制
```

### **4.2. 配置**

让我们记住设置*application.properties*文件以允许*Spring Boot*配置基本对象。显然，参数的值也将取决于*RabbitMQ*的安装方式。

例如，当*RabbitMQ*在运行此示例的同一台机器上运行时，以下配置可能是合理的：

```plaintext
spring.rabbitmq.dynamic=true
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.host=localhost复制
```

## **五、客户申请**

### **5.1. 调用远程服务**

现在让我们来处理客户端。同样，我们需要**定义将写入调用消息的队列**。我们需要仔细检查客户端和服务器是否使用相同的名称。

```java
@Bean 
Queue queue() {
    return new Queue("remotingQueue");
}复制
```

在客户端，我们需要比服务器端稍微复杂的设置。事实上，我们需要**定义一个**带有相关*Binding的****Exchange\***：

```java
@Bean 
Exchange directExchange(Queue someQueue) {
    DirectExchange exchange = new DirectExchange("remoting.exchange");
    BindingBuilder
      .bind(someQueue)
      .to(exchange)
      .with("remoting.binding");
    return exchange;
}复制
```

[此处](https://www.rabbitmq.com/tutorials/tutorial-four-java.html)提供了关于*RabbitMQ*作为*交换*和*绑定*的主要概念的一个很好的介绍。

由于*Spring Boot*不会自动配置*AmqpTemplate*，我们必须自己设置一个，指定 ar *outing key*。为此，我们需要仔细检查*路由密钥*和*交换*是否与上一步中用于定义*交换的匹配：*

```java
@Bean RabbitTemplate amqpTemplate(ConnectionFactory factory) {
    RabbitTemplate template = new RabbitTemplate(factory);
    template.setRoutingKey("remoting.binding");
    template.setExchange("remoting.exchange");
    return template;
}复制
```

然后，就像我们对其他*Spring Remoting*实现所做的那样，我们**定义了一个\*FactoryBean\*，它将生成远程公开的服务的本地代理**。这里没什么特别的，我们只需要提供远程服务的接口：

```java
@Bean AmqpProxyFactoryBean amqpFactoryBean(AmqpTemplate amqpTemplate) {
    AmqpProxyFactoryBean factoryBean = new AmqpProxyFactoryBean();
    factoryBean.setServiceInterface(CabBookingService.class);
    factoryBean.setAmqpTemplate(amqpTemplate);
    return factoryBean;
}复制
```

**我们现在可以像使用本地 bean 一样使用远程服务：**

```java
CabBookingService service = context.getBean(CabBookingService.class);
out.println(service.bookRide("13 Seagate Blvd, Key Largo, FL 33037"));复制
```

### **5.2. 设置**

同样对于客户端应用程序，我们必须正确选择*application.properties*文件中的值。在常见的设置中，这些将与服务器端使用的完全匹配。

### **5.3. 运行示例**

这应该足以演示通过*RabbitMQ*进行的远程调用。然后让我们启动 RabbitMQ、服务器应用程序和调用远程服务的客户端应用程序。

在幕后发生的事情是***AmqpProxyFactoryBean\*将构建一个实现\*CabBookingService\***的代理。

当在该代理上调用方法时，它会在*RabbitMQ*上排队消息，在其中指定调用的所有参数和用于发回结果的队列的名称。

从调用实际实现的*AmqpInvokerServiceExporter*使用该消息。然后它将结果收集到一条消息中，并将其放在传入消息中指定名称的队列中。

AmqpProxyFactoryBean收到返回的结果，最后返回最初在服务器端生成的值*。*

## **六，结论**

在本文中，我们了解了如何使用*Spring Remoting*在消息系统之上提供 RPC。

*这可能不是我们可能更喜欢利用RabbitMQ*的异步性的主要场景的方法，但在某些选定和有限的场景中，同步调用可以更容易理解并且开发起来更快更简单。
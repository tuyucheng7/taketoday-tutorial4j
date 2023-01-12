## **一、概述**

我们在[之前的文章](https://www.baeldung.com/spring-remoting-amqp)中看到了如何使用*Spring Remoting*在异步通道之上提供*RPC作为**AMQP*队列。但是，我们也可以使用*JMS*获得相同的结果。

实际上，在本文中，我们将探索如何使用*Spring Remoting JMS*和*Apache ActiveMQ*作为消息传递中间件来设置远程调用。

## **2. 启动 Apache ActiveMQ 代理**

*Apache [ActiveMQ](https://activemq.apache.org/)*是一个**开源消息代理**，它使应用程序能够异步交换信息，并且它与*Java Message Service* *API*完全兼容。

要运行我们的实验，我们首先需要设置*ActiveMQ*的运行实例。我们可以选择几种方法：按照[官方指南](https://activemq.apache.org/getting-started.html)中描述的步骤，将其嵌入到*Java*应用程序中，或者更简单地使用以下命令启动*Docker容器：*

```bash
docker run -p 61616:61616 -p 8161:8161 rmohr/activemq:5.14.3复制
```

这将启动一个*ActiveMQ*容器，该容器在端口 8081 上公开一个简单的管理 Web GUI，我们可以通过它检查可用队列、连接的客户端和其他管理信息。*JMS*客户端将需要使用端口 61616 连接到代理并交换消息。

## **3.Maven依赖**

与之前介绍*Spring Remoting*的文章一样，我们将设置一个服务器和一个客户端*Spring Boot*应用程序来展示*JMS Remoting*的工作原理。

通常我们会仔细选择*Spring Boot 启动*器依赖项，[如下所述](https://www.baeldung.com/spring-boot-starters)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>复制
```

我们明确排除了*spring-boot-starter-tomcat*，以免类路径中有*Tomcat*相关的*.jar*文件。

反过来，这将阻止*Spring Boot*的自动配置机制在应用程序启动时启动嵌入式 Web 服务器，因为我们不需要它。

## **4.服务器应用**

### **4.1. 公开服务**

我们将设置一个服务器应用程序，它公开客户端将能够调用的*CabBookingService 。*

**第一步是声明一个 bean，它实现了我们想要向客户公开的服务接口。**这是将在服务器上执行业务逻辑的 bean：

```java
@Bean 
CabBookingService bookingService() {
    return new CabBookingServiceImpl();
}复制
```

然后让我们定义服务器将从中检索调用的队列，并在构造函数中指定其名称：

```java
@Bean 
Queue queue() {
    return new ActiveMQQueue("remotingQueue");
}复制
```

正如我们已经从之前的文章中了解到的那样，***Spring Remoting\*****的主要概念之一是\*服务导出器\*，它是从某个源收集调用请求的组件**，在本例中是*ApacheMQ*队列 ─ 并在服务实现上调用所需的方法.

为了使用*JMS*，我们定义了一个*JmsInvokerServiceExporter*：

```java
@Bean 
JmsInvokerServiceExporter exporter(CabBookingService implementation) {
    JmsInvokerServiceExporter exporter = new JmsInvokerServiceExporter();
    exporter.setServiceInterface(CabBookingService.class);
    exporter.setService(implementation);
    return exporter;
}复制
```

最后，我们需要定义一个负责消费消息的监听器。**侦听器充当\*ApacheMQ\*和*****JmsInvokerServiceExporter***之间的桥梁*，*它侦听队列上可用的调用消息，将调用转发给服务导出器并将结果序列化：

```java
@Bean SimpleMessageListenerContainer listener(
  ConnectionFactory factory, 
  JmsInvokerServiceExporter exporter) {
 
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(factory);
    container.setDestinationName("remotingQueue");
    container.setConcurrentConsumers(1);
    container.setMessageListener(exporter);
    return container;
}复制
```

### **4.2. 配置**

让我们记住设置*application.properties*文件以允许*Spring Boot*配置一些基本对象，例如侦听器所需的*ConnectionFactory 。*各个参数的取值主要取决于方式

各种参数的值主要取决于*ApacheMQ*的安装方式，以下是我们运行这些示例的同一台机器上运行的*Docker容器的合理配置：*

```plaintext
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.packages.trusted=org.springframework.remoting.support,java.lang,com.baeldung.api复制
```

spring.activemq.broker *-url*参数是对*AMQ*端口的引用。相反，需要对*spring.activemq.packages.trusted 参数*进行更深入的解释。从 5.12.2 版本开始，ActiveMQ 默认拒绝任何类型的消息

**从版本 5.12.2 开始，ActiveMQ 默认拒绝任何\*ObjectMessage\*类型的消息，用于交换序列化的\*Java\*对象，因为在某些情况下它被认为是安全攻击的潜在向量。**

总之，可以指示*AMQ*接受指定包中的序列化对象。*org.springframework.remoting.support*是包含表示远程方法调用及其结果的主要消息的包。包裹

包*com.baeldung.api*包含我们服务的参数和结果。*添加java.lang*是因为表示出租车预订结果的对象引用了一个*String*，所以我们也需要对其进行序列化。

## **五、客户申请**

### **5.1. 调用远程服务**

现在让我们来处理客户端。同样，我们需要定义将写入调用消息的队列。我们需要仔细检查客户端和服务器是否使用相同的名称。

```java
@Bean 
Queue queue() {
    return new ActiveMQQueue("remotingQueue");
}复制
```

然后我们需要设置一个导出器：

```java
@Bean 
FactoryBean invoker(ConnectionFactory factory, Queue queue) {
    JmsInvokerProxyFactoryBean factoryBean = new JmsInvokerProxyFactoryBean();
    factoryBean.setConnectionFactory(factory);
    factoryBean.setServiceInterface(CabBookingService.class);
    factoryBean.setQueue(queue);
    return factoryBean;
}复制
```

我们现在可以像使用本地 bean 一样使用远程服务：

```java
CabBookingService service = context.getBean(CabBookingService.class);
out.println(service.bookRide("13 Seagate Blvd, Key Largo, FL 33037"));复制
```

### **5.2. 运行示例**

*同样对于客户端应用程序，我们必须在应用程序的.properties*文件中正确选择值。在常见的设置中，这些将与服务器端使用的完全匹配。

这应该足以演示通过*Apache AMQ*进行的远程调用。因此，让我们首先启动*ApacheMQ*，然后是服务器应用程序，最后是将调用远程服务的客户端应用程序。

## **六，结论**

在这个快速教程中，我们了解了如何使用*Spring Remoting在**JMS*系统之上提供*RPC作为**AMQ*。

Spring Remoting 不断展示如何轻松快速地设置异步调用，而不管底层通道如何。
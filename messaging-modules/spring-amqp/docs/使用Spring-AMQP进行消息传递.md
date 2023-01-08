## 1. 概述

在本教程中，我们将使用Spring AMQP框架探索基于消息的AMQP通信。
首先，我们将介绍消息传递的一些关键概念。然后，我们编写一个实际示例。

## 2. 基于消息的通信

消息传递是一种在应用程序之间进行通信的技术。它依赖于异步消息传递，而不是基于同步-请求响应的体系架构。
**消息的生产者和消费者通过称为消息代理的中间消息层解耦**。消息代理提供消息的持久存储、消息过滤和消息转换等功能。

在用Java编写的应用程序之间进行消息传递的情况下，通常使用JMS(Java Message Service)API。
**对于不同供应商和平台之间的互操作性，我们将无法使用JMS客户端和代理。这就是AMQP派上用场的地方**。

## 3. AMQP–高级消息队列协议

AMQP是用于异步消息通信的开放标准规范。它提供了如何构建消息的描述。

### 3.1 AMQP与JMS有何不同

由于AMQP是平台中立的二进制协议标准，因此可以用不同的编程语言编写依赖库，并在不同的环境中运行。

没有基于供应商的协议锁定，从一个JMS代理迁移到另一个时就是这种情况。一些广泛使用的AMQP代理是RabbitMQ、OpenAMQ和StormMQ。

### 3.2 AMQP实体

简而言之，AMQP由Exchanges、Queues和Bindings组成：

+ *交换机*就像邮局或邮箱，客户端将消息发布到AMQP交换机。有四种内置的交换机类型：
    + Direct Exchange - 通过匹配完整的路由键将消息路由到队列。
    + Fanout Exchange – 将消息路由到绑定到它的所有队列。
    + Topic Exchange - 通过将路由键与模式匹配，将消息路由到多个队列。
    + Headers Exchange - 基于消息标头路由消息。
+ *队列*使用路由键绑定到交换器。
+ *消息*通过路由键发送到交换机。然后交换机将消息副本分发到队列。

### 3.3 Spring AMQP

Spring AMQP包含两个模块：spring-amqp和spring-rabbit。这些模块共同提供了以下内容的抽象：

+ AMQP实体 - 我们使用Message、Queue、Binding和Exchange类创建实体。
+ 连接管理 - 我们使用CachingConnectionFactory连接到我们的RabbitMQ代理。
+ 消息发布 - 我们使用RabbitTemplate发送消息。
+ 消息消费 - 我们使用@RabbitListener从队列中读取消息。

## 4. 构建一个Rabbitmq服务

我们需要一个可供我们连接的RabbitMQ代理。最简单的方法是使用Docker获取并运行RabbitMQ镜像：

```shell
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:3.8.2-management
```

我们公开5672端口，以便我们的应用程序可以连接到RabbitMQ。

并且我们还公开端口15672，以便我们可以通过管理页面：
http://localhost:15672或HTTP API：http://localhost:15672/api/index.html来访问我们的RabbitMQ控制台。

## 5. 创建Spring AMQP应用程序

现在让我们创建一个简单的应用程序，用Spring AMPQ来发送和接收一个简单的“Hello, world!“消息。

### 5.1 依赖

为了将spring-amqp和spring-rabbit模块添加到我们的项目中，我们需要将spring-boot-starter-amqp依赖项添加到我们的pom.xml或build.gradle中：

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-amqp'
}
```

### 5.2 连接到Rabbitmq服务

我们使用Spring Boot的自动配置来创建ConnectionFactory、RabbitTemplate和RabbitAdmin bean。
并且，Spring Boot默认使用用户名和密码“guest”连接到端口5672上的RabbitMQ服务。
因此，我们只需使用@SpringBootApplication标注我们的应用程序主启动类：

```java

@SpringBootApplication
public class HelloWorldMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldMessageApplication.class, args);
    }
}
```

### 5.3 创建队列

**为了创建我们的队列，我们只需定义一个Queue类型的bean**。并将其绑定到路由键为“myQueue”的默认交换机：

```java

public class HelloWorldMessageApplication {
    private static final boolean NON_DURABLE = false;
    private static final String MY_QUEUE_NAME = "myQueue";

    @Bean
    public Queue myQueue() {
        return new Queue(MY_QUEUE_NAME, NON_DURABLE);
    }
}
```

我们将队列设置为非持久队列，这样当RabbitMQ服务停止时，队列及其上的任何消息都将被删除。但是请注意，重新启动应用程序不会对队列产生任何影响。

### 5.4 发送消息

让我们使用RabbitTemplate发送一条“Hello, world!”信息：

```java
public class HelloWorldMessageApplication {

    @Bean
    public ApplicationRunner runner(RabbitTemplate template) {
        return args -> template.convertAndSend("myQueue", "Hello, world!");
    }
}
```

### 5.5 消费消息

我们通过使用@RabbitListener注解标注方法来实现消息的消费：

```java
public class HelloWorldMessageApplication {

    @RabbitListener(queues = MY_QUEUE_NAME)
    public void listen(String in) {
        System.out.println("Message read from myQueue : " + in);
    }
}
```

## 6. 运行程序

首先，我们启动RabbitMQ服务：

```shell
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:3.8.2-management
```

然后，我们通过运行HelloWorldMessageApplication.java来运行Spring Boot应用程序，执行main()方法：

```shell
mvn spring-boot:run -Dstart-class=cn.tuyucheng.taketoday.springamqp.simple.HelloWorldMessageApplication
```

在应用程序运行时，我们可以看到：

+ 应用程序向默认交换机发送一条消息，其中“myQueue”作为路由键。
+ 然后，队列“myQueue”接收到消息。
+ 最后，listen方法消费来自“myQueue”的消息并将其打印在控制台上。

我们也可以通过http://your hostname:15672访问RabbitMQ管理台查看我们的消息已经发送并消费了。

## 7. 总结

在本教程中，我们介绍了基于AMQP协议的基于消息的架构，并使用Spring AMQP构建了一个简单的应用程序。
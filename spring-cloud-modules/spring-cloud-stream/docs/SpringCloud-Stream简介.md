## 1. 概述

Spring Cloud Stream是一个**构建在Spring Boot和Spring Integration之上的框架，有助于创建事件驱动或消息驱动的微服务**。

在本文中，我们将通过一些简单的示例介绍Spring Cloud Stream的概念和构造。

## 2. Maven依赖

首先，我们需要将[Spring Cloud Starter Stream RabbitMQ](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-stream-rabbit/4.0.1) Maven依赖项作为消息传递中间件添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
    <version>3.1.3</version>
</dependency>
```

我们将从[Maven Central](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-stream-test-support/4.0.1)添加模块依赖项以启用JUnit支持：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-test-support</artifactId>
    <version>3.1.3</version>
    <scope>test</scope>
</dependency>
```

## 3. 主要概念

微服务架构遵循“[智能端点和哑管道](https://martinfowler.com/articles/microservices.html#SmartEndpointsAndDumbPipes)”原则。端点之间的通信由RabbitMQ或Apache Kafka等消息传递中间件方驱动。**服务通过这些端点或通道发布域事件进行通信**。

让我们来看看构成Spring Cloud Stream框架的概念，以及我们在构建消息驱动服务时必须了解的基本范例。

### 3.1 构造

让我们看一下Spring Cloud Stream中的一个简单服务，它监听输入绑定并向输出绑定发送响应：

```java
@SpringBootApplication
@EnableBinding(Processor.class)
public class MyLoggerServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MyLoggerServiceApplication.class, args);
	}

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public LogMessage enrichLogMessage(LogMessage log) {
		return new LogMessage(String.format("[1]: %s", log.getMessage()));
	}
}
```

注解@EnableBinding将应用程序配置为绑定接口Processor中定义的通道INPUT和OUTPUT。**这两个通道都是绑定，可以配置为使用具体的消息传递中间件或绑定器**。

让我们来看看所有这些概念的定义：

-   Bindings：以声明方式标识输入和输出通道的接口集合
-   Binder：消息传递中间件实现，例如Kafka或RabbitMQ
-   Channel：表示消息传递中间件和应用程序之间的通信管道
-   StreamListeners：bean中的消息处理方法，在MessageConverter执行特定于中间件的事件和域对象类型/POJO之间的序列化/反序列化之后，将自动调用来自通道的消息
-   Message Schemas：用于消息的序列化和反序列化，这些模式可以从某个位置静态读取或动态加载，支持域对象类型的演进

### 3.2 通信模式

**指定到目的地的消息通过发布-订阅消息模式传递**。发布者将消息分类为主题，每个主题由一个名称标识。订阅者表示对一个或多个主题感兴趣。中间件过滤消息，将感兴趣的主题传递给订阅者。

现在，可以对订阅者进行分组。消费者组是一组订阅者或消费者，由group id标识，其中来自主题或主题分区的消息以负载平衡的方式传递。

## 4. 编程模型

本节介绍构建Spring Cloud Stream应用程序的基础知识。

### 4.1 功能测试

测试支持是一个绑定程序实现，允许与通道交互并检查消息。

让我们向上面的enrichLogMessage服务发送一条消息，并检查响应是否在消息开头包含文本“[1]:”：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyLoggerServiceApplication.class)
@DirtiesContext
public class MyLoggerApplicationTests {

	@Autowired
	private Processor pipe;

	@Autowired
	private MessageCollector messageCollector;

	@Test
	public void whenSendMessage_thenResponseShouldUpdateText() {
		pipe.input()
			.send(MessageBuilder.withPayload(new LogMessage("This is my message"))
				.build());

		Object payload = messageCollector.forChannel(pipe.output())
			.poll()
			.getPayload();

		assertEquals("[1]: This is my message", payload.toString());
	}
}
```

### 4.2 自定义通道

在上面的例子中，我们使用了Spring Cloud提供的Processor接口，它只有一个输入和一个输出通道。

如果我们需要不同的东西，比如一个输入和两个输出通道，我们可以创建一个自定义处理器：

```java
public interface MyProcessor {
	String INPUT = "myInput";

	@Input
	SubscribableChannel myInput();

	@Output("myOutput")
	MessageChannel anOutput();

	@Output
	MessageChannel anotherOutput();
}
```

Spring将为我们提供此接口的正确实现。可以使用@Output("myOutput")中的注解来设置通道名称。

否则，Spring将使用方法名称作为通道名称。因此，我们有三个通道，分别称为myInput、myOutput和anotherOutput。

现在，假设我们希望在值小于10时将消息路由到一个输出，而在值大于或等于10时将消息路由到另一个输出：

```java
@Autowired
private MyProcessor processor;

@StreamListener(MyProcessor.INPUT)
public void routeValues(Integer val) {
    if (val < 10) {
        processor.anOutput().send(message(val));
    } else {
        processor.anotherOutput().send(message(val));
    }
}

private static final <T> Message<T> message(T val) {
    return MessageBuilder.withPayload(val).build();
}
```

### 4.3 有条件的调度

使用@StreamListener注解，**我们还可以使用我们用[SpEL表达式](https://www.baeldung.com/spring-expression-language)定义的任何条件过滤我们期望在消费者中的消息**。

例如，我们可以使用条件调度作为将消息路由到不同输出的另一种方法：

```java
@Autowired
private MyProcessor processor;

@StreamListener(
  	target = MyProcessor.INPUT, 
  	condition = "payload < 10")
public void routeValuesToAnOutput(Integer val) {
    processor.anOutput().send(message(val));
}

@StreamListener(
  	target = MyProcessor.INPUT, 
  	condition = "payload >= 10")
public void routeValuesToAnotherOutput(Integer val) {
    processor.anotherOutput().send(message(val));
}
```

**这种方法的唯一限制是这些方法不能返回值**。

## 5. 设置

让我们设置将处理来自RabbitMQ代理的消息的应用程序。

### 5.1 Binder配置

我们可以通过META-INF/spring.binders配置我们的应用程序以使用默认的Binder实现：

```properties
rabbit:\
org.springframework.cloud.stream.binder.rabbit.config.RabbitMessageChannelBinderConfiguration
```

或者我们可以通过包含[此依赖](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-stream-binder-rabbit/4.0.1)将RabbitMQ的binder库添加到类路径中：

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-stream-binder-rabbit</artifactId>
	<version>1.3.0.RELEASE</version>
</dependency>
```

**如果没有提供Binder实现，Spring将使用通道之间的直接消息通信**。

### 5.2 RabbitMQ配置

要将3.1节中的示例配置为使用RabbitMQ绑定器，我们需要更新位于src/main/resources的application.yml：

```yaml
spring:
    cloud:
        stream:
            bindings:
                input:
                    destination: queue.log.messages
                    binder: local_rabbit
                output:
                    destination: queue.pretty.log.messages
                    binder: local_rabbit
            binders:
                local_rabbit:
                    type: rabbit
                    environment:
                        spring:
                            rabbitmq:
                                host: <host>
                                port: 5672
                                username: <username>
                                password: <password>
                                virtual-host: /
```

输入绑定将使用名为queue.log.messages的交换机，输出绑定将使用交换机queue.pretty.log.messages。两个绑定都将使用名为local_rabbit的Binder。

请注意，我们不需要提前创建RabbitMQ交换器或队列。运行应用程序时，**两个交换机都会自动创建**。

为了测试应用程序，我们可以使用RabbitMQ管理站点来发布消息。在交换机queue.log.messages的Publish Message面板中，我们需要输入JSON格式的请求。

### 5.3 自定义消息转换

Spring Cloud Stream允许我们为特定内容类型应用消息转换。在上面的示例中，我们希望提供纯文本，而不是使用JSON格式。

为此，**我们将使用MessageConverter将自定义转换应用于LogMessage**：

```java
@SpringBootApplication
@EnableBinding(Processor.class)
public class MyLoggerServiceApplication {
	// ...

	@Bean
	public MessageConverter providesTextPlainMessageConverter() {
		return new TextPlainMessageConverter();
	}

	// ...
}
```

```java
public class TextPlainMessageConverter extends AbstractMessageConverter {

	public TextPlainMessageConverter() {
		super(new MimeType("text", "plain"));
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return (LogMessage.class == clazz);
	}

	@Override
	protected Object convertFromInternal(Message<?> message,
										 Class<?> targetClass, Object conversionHint) {
		Object payload = message.getPayload();
		String text = payload instanceof String
			? (String) payload
			: new String((byte[]) payload);
		return new LogMessage(text);
	}
}
```

应用这些更改后，返回到“Publish Message”面板，如果我们将标头“contentTypes”设置为“text/plain”并将有效负载设置为“Hello World”，它应该像以前一样工作。

### 5.4 消费者组

当运行我们的应用程序的多个实例时，**每当输入通道中有新消息时，所有订阅者都会收到通知**。

大多数时候，我们只需要处理一次消息。Spring Cloud Stream通过消费者组实现这种行为。

要启用此行为，每个消费者绑定都可以使用spring.cloud.stream.bindings.<CHANNEL\>.group属性来指定组名：

```yaml
spring:
    cloud:
        stream:
            bindings:
                input:
                    destination: queue.log.messages
                    binder: local_rabbit
                    group: logMessageConsumers
                    # ...
```

## 6. 消息驱动的微服务

在本节中，我们将介绍在微服务上下文中运行Spring Cloud Stream应用程序所需的所有功能。

### 6.1 扩大规模

当多个应用程序正在运行时，确保数据在消费者之间正确拆分非常重要。为此，Spring Cloud Stream提供了两个属性：

-   **spring.cloud.stream.instanceCount**：正在运行的应用程序数量
-   **spring.cloud.stream.instanceIndex**：当前应用程序的索引

例如，如果我们部署了上述MyLoggerServiceApplication应用程序的两个实例，则两个应用程序的属性spring.cloud.stream.instanceCount应为2，属性spring.cloud.stream.instanceIndex应分别为0和1。

如果我们按照[本文](https://www.baeldung.com/spring-cloud-data-flow-stream-processing)所述使用Spring Data Flow部署Spring Cloud Stream应用程序，则会自动设置这些属性。

### 6.2 分区

**域事件可以是分区消息。这有助于我们扩展存储和提高应用程序性能**。

域事件通常有一个分区键，因此它最终与相关消息位于同一分区中。

假设我们希望日志消息按消息中的第一个字母(这将是分区键)进行分区，并分为两个分区。

将有一个分区用于以A-M开头的日志消息，另一个分区用于N-Z。这可以使用两个属性进行配置：

-   spring.cloud.stream.bindings.output.producer.partitionKeyExpression：对有效负载进行分区的表达式
-   spring.cloud.stream.bindings.output.producer.partitionCount：组数

**有时要分区的表达式太复杂，无法只用一行编写**。对于这些情况，我们可以使用属性spring.cloud.stream.bindings.output.producer.partitionKeyExtractorClass编写自定义分区策略。

### 6.3 健康指标

在微服务上下文中，**我们还需要检测服务何时关闭或开始失败**。Spring Cloud Stream提供了属性management.health.binders.enabled以启用Binder的健康指标。

运行应用程序时，我们可以在http://<host\>:<port\>/health查询健康状态。

## 7. 总结

在本教程中，我们介绍了Spring Cloud Stream的主要概念，并通过RabbitMQ上的一些简单示例展示了如何使用它。可以在[此处](https://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/)找到有关Spring Cloud Stream的更多信息。
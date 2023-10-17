## 1. 概述

[Apache Pulsar](https://pulsar.apache.org/docs/3.0.x/)是一个分布式发布者-订阅者消息系统。[虽然 Apache Pulsar 提供的功能与Apache Kafka](https://www.baeldung.com/spring-kafka)类似，但 Pulsar 旨在克服 Kafka 高延迟、低吞吐量、扩展和异地复制困难等限制。在处理需要实时处理的大量数据时，Apache Pulsar 是一个很好的选择。

在本教程中，我们将了解如何将 Apache Pulsar 与我们的Spring Boot应用程序集成。我们将利用Pulsar 的Spring Boot配置的PulsarTemplate和PulsarListener 。我们还将了解如何根据我们的要求修改其默认配置。

## 2.Maven依赖

我们将首先运行一个独立的 Apache Pulsar 服务器，如[Apache Pulsar 简介中所述。](https://www.baeldung.com/apache-pulsar)

接下来，让我们将[spring-pulsar-spring-boot-starter](https://mvnrepository.com/artifact/org.springframework.pulsar/spring-pulsar-spring-boot-starter)库添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.pulsar</groupId>
    <artifactId>spring-pulsar-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

## 3.Pulsar客户端

为了与 Pulsar 服务器交互，我们需要配置PulsarClient。默认情况下，Spring 自动配置一个PulsarClient连接到localhost:6650上的 Pulsar 服务器：

```yaml
spring:
  pulsar:
    client:
      service-url: pulsar://localhost:6650
```

我们可以更改此配置以在不同的地址上建立连接。

要连接到安全服务器，我们可以使用pulsar+ssl 代替pulsar。 [我们还可以通过将spring.pulsar.client.属性](https://docs.spring.io/spring-pulsar/docs/0.2.0/reference/html/#appendix.application-properties.pulsar-client)添加到application.yml来配置连接超时、身份验证和内存限制等属性。

## 4. 指定自定义对象的架构

我们将为我们的应用程序使用一个简单的User类：

```java
public class User {

    private String email;
    private String firstName;

    // standard constructors, getters and setters
}
```

Spring-Pulsar 自动检测原始数据类型并生成相关模式。但是，如果我们需要使用自定义 JSON 对象，我们必须为PulsarClient配置其架构信息：

```java
spring:
  pulsar:
    defaults:
      type-mappings:
        - message-type: com.baeldung.springpulsar.User
          schema-info:
            schema-type: JSON
```

这里，message-type属性接受消息类的完全限定名称，而schema-type 提供有关要使用的模式类型的信息。对于复杂对象，模式类型属性接受AVRO或JSON值。

虽然使用属性文件指定模式是首选方法，但我们也可以通过 bean 提供此模式：

```java
@Bean
public SchemaResolverCustomizer<DefaultSchemaResolver> schemaResolverCustomizer() {
    return (schemaResolver) -> {
        schemaResolver.addCustomSchemaMapping(User.class, Schema.JSON(User.class));
    }
}
```

应将此配置添加到生产者应用程序和侦听器应用程序中。

## 5. 出版商

要在 Pulsar 主题上发布消息，我们将使用PulsarTemplate。PulsarTemplate实现了PulsarOperations接口，并提供了以同步和异步形式发布记录的方法。send方法会阻止调用以提供同步操作功能，而sendAsync方法则提供非阻塞异步操作。

在本教程中，我们将使用同步操作来发布记录。

### 5.1. 发布消息

Spring Boot 自动配置一个即用型PulsarTemplate，将记录发布到指定主题。

让我们创建一个将字符串消息发布到队列的生产者：

```java
@Component
public class PulsarProducer {

    @Autowired
    private PulsarTemplate<String> stringTemplate;

    private static final String STRING_TOPIC = "string-topic";

    public void sendStringMessageToPulsarTopic(String str) throws PulsarClientException {
        stringTemplate.send(STRING_TOPIC, str);
    }
}
```

现在，让我们尝试将User对象发送到新队列：

```java
@Autowired
private PulsarTemplate<User> template;

private static final String USER_TOPIC = "user-topic";

public void sendMessageToPulsarTopic(User user) throws PulsarClientException {
    template.send(USER_TOPIC, user);
}
```

在上面的代码片段中，我们使用 PulsarTemplate将User类的对象发送到 Apache Pulsar 的名为user-topic 的主题。

### 5.2. 生产者端定制

PulsarTemplate接受TypedMessageBuilderCustomizer来配置传出消息，并接受ProducerBuilderCustomizer 来自定义生产者的属性。

我们可以使用TypedMessageBuilderCustomizer来配置消息延迟、在特定时间发送、禁用复制并提供其他属性：

```java
public void sendMessageToPulsarTopic(User user) throws PulsarClientException {
    template.newMessage(user)
      .withMessageCustomizer(mc -> {
        mc.deliverAfter(10L, TimeUnit.SECONDS);
      })
      .send();
}
```

ProducerBuilderCustomizer 可用于添加访问模式、自定义消息路由器和拦截器，并启用或禁用分块和批处理：

```java
public void sendMessageToPulsarTopic(User user) throws PulsarClientException {
    template.newMessage(user)
      .withProducerCustomizer(pc -> {
        pc.accessMode(ProducerAccessMode.Shared);
      })
      .send();
}
```

## 6. 消费者

将消息发布到我们的主题后，我们现在将为同一主题建立一个侦听器。为了启用监听主题，我们需要使用@PulsarListener 注解来修饰监听器方法。

Spring Boot 为侦听器方法配置所有必需的组件。

我们还需要使用@EnablePulsar 来使用PulsarListener。

### 6.1. 接收消息

我们首先为前面部分中创建的字符串主题创建一个侦听器方法：

```java
@Service
public class PulsarConsumer {

    private static final String STRING_TOPIC = "string-topic";

    @PulsarListener(
      subscriptionName = "string-topic-subscription",
      topics = STRING_TOPIC,
      subscriptionType = SubscriptionType.Shared
    )
    public void stringTopicListener(String str) {
        LOGGER.info("Received String message: {}", str);
    }
}
```

在这里，在PulsarListener注释中，我们配置了该方法将在topicName中侦听的主题，并在subscriptionName属性中给出了订阅名称。

现在，让我们为User类使用的用户主题创建一个侦听器方法：

```java
private static final String USER_TOPIC = "user-topic";

@PulsarListener(
    subscriptionName = "user-topic-subscription",
    topics = USER_TOPIC,
    schemaType = SchemaType.JSON
)
public void userTopicListener(User user) {
    LOGGER.info("Received user object with email: {}", user.getEmail());
}
```

除了前面的Listener方法中提供的属性之外，我们还添加了一个schemaType 属性，该属性的值与其生成器中的值相同。

我们还将@EnablePulsar注释添加到我们的主类中：

```java
@EnablePulsar
@SpringBootApplication
public class SpringPulsarApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringPulsarApplication.class, args);
    }
}
```

### 6.2. 消费端定制

除了订阅名称和模式类型之外，PulsarListener还可以用于配置自动启动、批处理和确认模式等属性：

```java
@PulsarListener(
  subscriptionName = "user-topic-subscription",
  topics = USER_TOPIC,
  subscriptionType = SubscriptionType.Shared,
  schemaType = SchemaType.JSON,
  ackMode = AckMode.RECORD,
  properties = {"ackTimeout=60s"}
)
public void userTopicListener(User user) {
    LOGGER.info("Received user object with email: {}", user.getEmail());
}
```

在这里，我们将确认模式设置为“记录”，并将确认超时设置为 60 秒。

## 7. 使用死信主题

如果消息的确认超时或服务器收到nack，Pulsar 会尝试重新发送消息一定次数。重试次数用完后，这些未传递的消息可以发送到称为死信队列(DLQ) 的队列。

此选项仅适用于共享订阅类型。为了为我们的用户主题 队列配置 DLQ ，我们首先创建一个DeadLetterPolicy bean，它将定义应尝试重新传递的次数以及要用作 DLQ 的队列的名称：

```java
private static final String USER_DEAD_LETTER_TOPIC = "user-dead-letter-topic";
@Bean
DeadLetterPolicy deadLetterPolicy() {
    return DeadLetterPolicy.builder()
      .maxRedeliverCount(10)
      .deadLetterTopic(USER_DEAD_LETTER_TOPIC)
      .build();
}
```

现在，我们将此策略添加到我们之前创建的PulsarListener中：

```java
@PulsarListener(
  subscriptionName = "user-topic-subscription",
  topics = USER_TOPIC,
  subscriptionType = SubscriptionType.Shared,
  schemaType = SchemaType.JSON,
  deadLetterPolicy = "deadLetterPolicy",
  properties = {"ackTimeout=60s"}
)
public void userTopicListener(User user) {
    LOGGER.info("Received user object with email: {}", user.getEmail());
}
```

在这里，我们将userTopicListener配置为使用之前创建的deadLetterPolicy，并将确认时间配置为 60 秒。

我们可以创建一个单独的Listener来处理DQL中的消息：

```java
@PulsarListener(
  subscriptionName = "dead-letter-topic-subscription",
  topics = USER_DEAD_LETTER_TOPIC,
  subscriptionType = SubscriptionType.Shared
)
public void userDlqTopicListener(User user) {
    LOGGER.info("Received user object in user-DLQ with email: {}", user.getEmail());
}
```

## 八、总结

在本教程中，我们了解了如何将 Apache Pulsar 与Spring Boot应用程序一起使用，以及更改默认配置的一些方法。
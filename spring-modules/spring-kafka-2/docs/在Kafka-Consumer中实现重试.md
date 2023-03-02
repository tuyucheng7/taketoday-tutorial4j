## 1. 概述

在本文中，我们将讨论在Kafka中实现重试的重要性。

我们将探讨在Spring Boot中实现它的各种可用选项，并讨论最大限度地提高Kafka Consumer的可靠性和弹性的最佳实践。

如果这是我们第一次在Spring上配置Kafka并且我们想了解更多信息，那么让我们从一篇[介绍Spring和Kafka](https://www.baeldung.com/spring-kafka)的文章开始。

## 2. 项目设置

让我们创建一个新的Spring Boot项目并添加[spring-kafka](https://central.sonatype.com/artifact/org.springframework.kafka/spring-kafka/3.0.3)依赖：

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>3.0.1</version>
</dependency>
```

让我们创建一个对象：

```java
public class Greeting {

    private String msg;
    private String name;

    // standard constructors, getters and setters
}
```

## 3. Kafka消费者

[Kafka消费者](https://kafka.apache.org/documentation/#consumerapi)是从Kafka集群读取数据的客户端应用程序。它订阅一个或多个主题并消费已发布的消息。[生产者](https://docs.confluent.io/platform/current/clients/producer.html#)向主题发送消息，主题是存储和发布记录的类别名称。主题被分成多个分区，以允许它们水平扩展。每个分区都是一个不可变的消息序列。

[消费者](https://docs.confluent.io/platform/current/clients/consumer.html)可以通过指定偏移量从特定分区读取消息，偏移量是消息在分区内的位置。ack(确认)是消费者发送给Kafka broker的消息，表明它已经成功处理了一条记录。发送确认后，**消费者偏移量将更新**。

这确保消息被消费并且不会再次传递给当前的监听器。

### 3.1 确认模式

**ack模式确定代理何时更新消费者的偏移量**。

确认方式有以下三种：

1.  auto-commit：消费者在收到消息后立即向代理发送确认
2.  after-processing：消费者仅在成功处理消息后才向代理发送确认
3.  manual：消费者在向代理发送确认之前等待直到收到特定指令

Ack模式决定了消费者如何处理它从Kafka集群读取的消息。

让我们创建一个新的bean来创建一个新的ConcurrentKafkaListenerContainerFactory：

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, Object> multiTypeKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    // Other configurations
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
    factory.afterPropertiesSet();
    return factory;
}
```

我们可以配置几种可用的确认模式：

1.  AckMode.RECORD：在这种后处理模式下，消费者为它处理的每条消息发送一个确认
2.  AckMode.BATCH：在这种手动模式下，消费者为一批消息而不是每条消息发送确认
3.  AckMode.COUNT：在这种手动模式下，消费者在处理了特定数量的消息后发送确认
4.  AckMode.MANUAL：在这种手动模式下，消费者不会为其处理的消息发送确认
5.  AckMode.TIME：在这种手动模式下，消费者在经过一定时间后发送确认

要在Kafka中实现消息处理的重试逻辑，我们需要选择一个AckMode。

**此AckMode应允许消费者向代理指示哪些特定消息已成功处理**。

这样，代理可以将任何未确认的消息重新传递给另一个消费者。

在阻塞重试的情况下，这可能是RECORD或MANUAL模式。

## 4. 阻塞重试

如果初始尝试由于临时错误而失败，则阻塞重试使消费者能够再次尝试消费消息。

在尝试再次消费消息之前，消费者会等待一定的时间(称为重试退避期)。

此外，消费者可以使用固定延迟或指数退避策略自定义重试退避期。

它还可以在放弃并将消息标记为失败之前设置最大重试次数。

### 4.1 错误处理器

让我们在Kafka配置类上定义两个属性：

```java
@Value(value = "${kafka.backoff.interval}")
private Long interval;

@Value(value = "${kafka.backoff.max_failure}")
private Long maxAttempts;
```

为了处理消费过程中抛出的所有异常，让我们定义一个新的错误处理程序：

```java
@Bean
public DefaultErrorHandler errorHandler() {
    BackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
    DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) -> {
        // logic to execute when all the retry attemps are exhausted
    }, fixedBackOff);
    return errorHandler;
}
```

FixedBackOff类有两个参数：

-   interval：重试之间等待的时间量(以毫秒为单位)。
-   maxAttempts：在放弃之前重试操作的最大次数。

在此策略中，消费者在重试消息消费之前等待固定时间。

DefaultErrorHandler正在使用lambda函数进行初始化，该函数表示在所有重试尝试都用完时要执行的逻辑。

lambda函数有两个参数：

-   consumerRecord：表示导致错误的Kafka记录
-   exception：表示抛出的异常

### 4.2 容器工厂

让我们在容器工厂bean上添加错误处理程序：

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, Object> multiTypeKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    // Other configurations
    factory.setCommonErrorHandler(errorHandler());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
    factory.afterPropertiesSet();
    return factory;
}
```

如果存在重试策略，请将ack模式设置为AckMode.RECORD以确保如果在处理过程中发生错误，消费者将重新传递消息。

我们不应该将ack模式设置为AckMode.BATCH或AckMode.TIME，因为消费者将同时确认多条消息。

这是因为如果在处理消息时发生错误，消费者不会将批处理或时间窗口中的所有消息重新传递给自己。

因此重试策略将无法正确处理错误。

### 4.3 可重试异常和不可重试异常

我们可以指定哪些异常是可重试的，哪些是不可重试的。

让我们修改ErrorHandler：

```java
@Bean
public DefaultErrorHandler errorHandler() {
    BackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
    DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, e) -> {
        // logic to execute when all the retry attemps are exhausted
    }, fixedBackOff);
    errorHandler.addRetryableExceptions(SocketTimeoutException.class);
    errorHandler.addNotRetryableExceptions(NullPointerException.class);
    return errorHandler;
}
```

因此，我们指定了哪些异常类型应该在消费者中触发重试策略。

SocketTimeoutException被认为是可重试的，而NullPointerException被认为是不可重试的。

如果我们不设置任何可重试异常，则将使用默认的可重试异常集：

-   [MessagingException](https://docs.oracle.com/javaee/7/api/javax/mail/MessagingException.html)
-   [RetryableException](https://javadoc.io/static/io.github.openfeign/feign-core/9.2.0/feign/RetryableException.html)
-   [ListenerExecutionFailedException](https://docs.spring.io/spring-kafka/docs/current/api/org/springframework/kafka/listener/ListenerExecutionFailedException.html)

### 4.4 优点和缺点

在阻塞重试中，当消息处理失败时，消费者会阻塞，直到重试机制完成其重试或达到最大重试次数。

使用阻塞重试有几个优点和缺点。

**阻塞重试允许消费者在发生错误时重试消费消息，从而提高消息处理管道的可靠性。这有助于确保成功处理消息，即使出现暂时性错误也是如此**。 

阻塞重试可以通过抽象出重试机制来简化消息处理逻辑的实现。消费者可以专注于处理消息，而将重试机制留给处理可能发生的任何错误。

最后，如果消费者需要等待重试机制完成其重试，则阻塞重试可能会在消息处理管道中引入延迟，这会影响系统的整体性能。阻塞重试还可能导致消费者在等待重试机制完成其重试时消耗更多资源，例如CPU和内存，这会影响系统的整体可扩展性。

## 5. 非阻塞重试

非阻塞重试允许消费者异步重试消息的消费，而不会阻塞消息监听器方法的执行。

### 5.1 @RetryableTopic

让我们在KafkaListener上添加注解[@RetryableTopic](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/annotation/RetryableTopic.html)：

```java
@Component
@KafkaListener(id = "multiGroup", topics = "greeting")
public class MultiTypeKafkaListener {

    @KafkaHandler
    @RetryableTopic(
          backoff = @Backoff(value = 3000L),
          attempts = "5",
          autoCreateTopics = "false",
          include = SocketTimeoutException.class, exclude = NullPointerException.class)
    public void handleGreeting(Greeting greeting) {
        System.out.println("Greeting received: " + greeting);
    }
}
```

我们通过修改几个属性来自定义重试行为，例如：

-   backoff：此属性指定重试失败消息时要使用的退避策略。
-   attempts：此属性指定在放弃之前应重试消息的最大次数。
-   autoCreateTopics：此属性指定是否自动创建重试主题和DLT-死信主题(如果它们尚不存在)。
-   include：此属性指定应触发重试的异常。
-   exclude：此属性指定不应触发重试的异常。

当一条消息未能传递到它的目标主题时，它会自动发送到重试主题进行重试。

如果在最大尝试次数后仍无法传递消息，则会将其发送到DLT进行进一步处理。

### 5.2 优点和缺点

实现非阻塞重试有几个优点：

-   改进的性能：非阻塞重试允许在不阻塞调用线程的情况下重试失败的消息，这可以提高应用程序的整体性能
-   提高可靠性：非阻塞重试可以帮助应用程序从故障中恢复并继续处理消息，即使某些消息无法传递

但是，在实现非阻塞重试时也需要考虑一些潜在的缺点：

-   增加的复杂性：非阻塞重试会给应用程序增加额外的复杂性，因为我们需要处理重试逻辑和DLT
-   消息重复风险：如果一条消息在重试后成功传递，则在原始传递和重试都成功的情况下，该消息可能会被多次传递。我们需要考虑这种风险并采取措施防止消息重复(如果有问题)
-   消息的顺序：重试的消息异步发送到重试主题，并且可能比未重试的消息更晚地传递到原始主题。

## 6. 总结

在本教程中，我们分析了如何在Kafka主题上实现重试逻辑，包括阻塞和非阻塞方法。
## 一、概述

在本文中，我们将通过一个实际示例来学习如何为同一个 Kafka 主题配置多个侦听器。

如果这是第一次在 Spring 上配置 Kafka，那么可以从我们[对 Apache Kafka with Spring 的介绍](https://www.baeldung.com/spring-kafka)开始。

## 2.项目设置

让我们构建一个图书消费者服务，它监听图书馆内新到的图书，并出于不同的目的使用它们，例如全文内容搜索、价格索引或用户通知。

首先，让我们创建一个 Spring Boot 服务并使用[*spring-kafka*](https://search.maven.org/search?q=g:org.springframework.kafka AND a:spring-kafka)依赖：

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>复制
```

此外，让我们定义 听众将使用的*BookEvent ：*

```java
public class BookEvent {

    private String title;
    private String description;
    private Double price;

    //  standard constructors, getters and setters
}复制
```

## 3. 生产消息

[Kafka 生产者](https://docs.confluent.io/platform/current/clients/producer.html#)对生态系统至关重要，因为生产者将消息写入 Kafka 集群。考虑到这一点，首先，我们需要定义一个[生产者](https://www.baeldung.com/spring-kafka#producing-messages)，它在一个主题上写入消息，稍后由消费者应用程序使用。

按照我们的示例，让我们编写一个简单的 Kafka 生产者函数，将新的*BookEvent*对象写入“books”主题。

```java
private static final String TOPIC = "books";

@Autowired
private KafkaTemplate<String, BookEvent> bookEventKafkaTemplate;

public void sentBookEvent(BookEvent book){
    bookEventKafkaTemplate.send(TOPIC, UUID.randomUUID().toString(), book);
}复制
```

## 4. 从多个监听器中消费相同的 Kafka 主题

[Kafka 消费者](https://docs.confluent.io/platform/current/clients/consumer.html)是订阅 Kafka 集群的一个或多个主题的客户端应用程序。稍后，我们将研究如何针对同一主题设置多个侦听器。

### 4.1. 消费者配置

首先，要配置[消费者](https://www.baeldung.com/spring-kafka#1-consumer-configuration)，我们需要定义监听器需要的*ConcurrentKafkaListenerContainerFactory Bean。*

*现在，让我们定义我们将用于使用BookEvent*对象的容器工厂：

```java
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    public ConsumerFactory<String, BookEvent> consumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();
        
        // required consumer factory properties

        return new DefaultKafkaConsumerFactory<>(props);
    }
}复制
```

接下来，我们将研究用于收听传入消息的不同策略。

### 4.2. 具有相同消费者组的多个侦听器

将多个侦听器添加到同一消费者组的一种策略是提高同一消费者组内的并发级别。*因此，我们可以简单地在@KafkaListener*注释中指定它。

为了理解它是如何工作的，让我们为我们的库定义一个通知监听器：

```java
@KafkaListener(topics = "books", groupId = "book-notification-consumer", concurrency = "2")
public void bookNotificationConsumer(BookEvent event) {
    logger.info("Books event received for notification => {}", event);
}复制
```

接下来，我们将在发布三个消息后看到控制台输出。此外，让我们了解为什么消息只被消费一次：

```shell
Books event received for notification => BookEvent(title=book 1, description=description 1, price=1.0)
Books event received for notification => BookEvent(title=book 2, description=description 2, price=2.0)
Books event received for notification => BookEvent(title=book 3, description=description 3, price=3.0)复制
```

发生这种情况是因为，在内部，**对于每个并发级别，Kafka 在同一消费者组中实例化一个新的侦听器**。此外，同一消费者组内的所有侦听器实例的范围是在彼此之间分发消息以更快地完成工作并提高吞吐量。

### 4.3. 具有不同消费者群体的多个听众

如果我们需要多次使用相同的消息并为每个侦听器应用不同的处理逻辑，我们必须将 @KafkaListener 配置*为*具有不同的组 ID。通过这样做，**Kafka 将为每个监听器创建专门的消费者组，并将所有已发布的消息推送给每个监听器**。

要查看此策略的实际效果，让我们为全文搜索索引定义一个侦听器，并为价格索引定义一个侦听器。两者都将收听相同的“书籍”主题：

```java
@KafkaListener(topics = "books", groupId = "books-content-search")
public void bookContentSearchConsumer(BookEvent event) {
    logger.info("Books event received for full-text search indexing => {}", event);
}

@KafkaListener(topics = "books", groupId = "books-price-index")
public void bookPriceIndexerConsumer(BookEvent event) {
    logger.info("Books event received for price indexing => {}", event);
}复制
```

现在，让我们运行上面的代码并分析输出：

```shell
Books event received for price indexing => BookEvent(title=book 1, description=description 1, price=1.0)
Books event received for full-text search indexing => BookEvent(title=book 1, description=description 1, price=1.0)
Books event received for full-text search indexing => BookEvent(title=book 2, description=description 2, price=2.0)
Books event received for price indexing => BookEvent(title=book 2, description=description 2, price=2.0)
Books event received for full-text search indexing => BookEvent(title=book 3, description=description 3, price=3.0)
Books event received for price indexing => BookEvent(title=book 3, description=description 3, price=3.0)复制
```

正如我们所见，两个侦听器都接收到每个*BookEvent*，并且可以对所有传入消息应用独立的处理逻辑。

## 5. 何时使用不同的侦听器策略

正如我们已经了解到的，我们可以设置多个侦听器，方法是将*@KafkaListener*注释的*并发*属性配置为大于 1 的值，或者定义多个*@KafkaListener*方法来侦听同一个 Kafka 主题并分配不同的消费者 ID .

选择一种策略或另一种策略取决于我们想要实现的目标。只要我们解决性能问题以通过更快地处理消息来提高吞吐量，正确的策略就是增加同一消费者组中的侦听器数量。

然而，为了多次处理同一条消息以满足不同的需求，我们应该定义专门的监听器，具有不同的消费者组来监听同一主题。

根据经验，我们应该为每个需要满足的需求使用一个消费者组，如果我们需要使该侦听器更快，我们可以增加同一消费者组中的侦听器数量。

## 六，结论

在本文中，我们学习了如何使用 Spring Kafka 库为同一主题配置多个侦听器，并查看了图书库的实际示例。我们从生产者和消费者配置开始，然后继续使用不同的方式为同一主题添加多个侦听器。
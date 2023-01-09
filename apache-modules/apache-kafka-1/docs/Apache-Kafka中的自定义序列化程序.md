## 1. 简介

在 Apache Kafka 中传输消息期间，客户端和服务器同意使用通用语法格式。Apache Kafka 带来了默认转换器(例如String 和Long)，但也支持针对特定用例的自定义序列化程序。在本教程中，我们将看到如何实现它们。

## 2. Apache Kafka 中的序列化程序

序列化是将对象转换为字节的过程。反序列化是逆过程——将字节流转换为对象。简而言之，它将内容转换为可读和可解释的信息。

正如我们提到的，Apache Kafka 为几种基本类型提供了默认序列化器，它允许我们实现自定义序列化器：

 

[![泡沫 1](https://www.baeldung.com/wp-content/uploads/2021/08/kafka1.png)](https://www.baeldung.com/wp-content/uploads/2021/08/kafka1.png)

上图展示了通过网络向一个Kafka主题发送消息的过程。在此过程中，自定义序列化程序在生产者将消息发送到主题之前将对象转换为字节。同样，它还展示了反序列化器如何将字节转换回对象，以便消费者正确处理它。

### 2.1. 自定义序列化程序

Apache Kafka 为几种基本类型提供了预构建的序列化器和反序列化器：

-   [字符串序列化器](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/StringSerializer.html)
-   [短序列化器](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/ShortSerializer.html)
-   [整数序列化器](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/IntegerSerializer.html)
-   [长序列化器](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/LongSerializer.html)
-   [双序列化器](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/DoubleSerializer.html)
-   [字节序列化器](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/BytesSerializer.html)

但它也提供了实现自定义(反)序列化器的能力。为了序列化我们自己的对象，我们将实现[Serializer](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/Serializer.html) 接口。同样，要创建自定义反序列化器，我们将实现[Deserializer](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/Deserializer.html) 接口。

有一些方法可以覆盖两个接口：

-   configure：用于实现配置细节
-   序列化/反序列化：这些方法包括我们自定义序列化和反序列化的实际实现。
-   close : 使用此方法关闭 Kafka 会话

## 3. 在 Apache Kafka 中实现自定义序列化器

Apache Kafka 提供了定制序列化器的能力。不仅可以为消息值而且可以为密钥实现特定的转换器。

### 3.1. 依赖关系

为了实现示例，我们只需将[Kafka Consumer API](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.kafka" AND a%3A"kafka-clients") 依赖项添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.8.0</version>
</dependency>

```

### 3.2. 自定义序列化器

首先，我们将使用[Lombok](https://www.baeldung.com/intro-to-project-lombok)指定要通过 Kafka 发送的自定义对象：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {
    private String message;
    private String version;
}
```

接下来，我们将实现Kafka提供的[Serializer](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/Serializer.html)接口，供生产者发送消息：

```java
public class CustomSerializer implements Serializer<MessageDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, MessageDto data) {
        try {
            if (data == null){
                System.out.println("Null received at serializing");
                return null;
            }
            System.out.println("Serializing...");
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing MessageDto to byte[]");
        }
    }

    @Override
    public void close() {
    }
}

```

我们将覆盖接口的序列化 方法。因此，在我们的实现中，我们将使用Jackson ObjectMapper转换自定义对象。然后我们将返回字节流以正确地将消息发送到网络。

### 3.3. 自定义解串器

以同样的方式，我们将为消费者实现[Deserializer接口：](https://kafka.apache.org/24/javadoc/org/apache/kafka/common/serialization/Deserializer.html)

```java
@Slf4j
public class CustomDeserializer implements Deserializer<MessageDto> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public MessageDto deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            System.out.println("Deserializing...");
            return objectMapper.readValue(new String(data, "UTF-8"), MessageDto.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to MessageDto");
        }
    }

    @Override
    public void close() {
    }
}

```

与上一节一样，我们将覆盖接口的反序列 化方法。因此，我们将使用相同的Jackson ObjectMapper将字节流转换为自定义对象。

### 3.4. 使用示例消息

让我们看一个使用自定义序列化器和反序列化器发送和接收示例消息的工作示例。

首先，我们将创建和配置 Kafka Producer：

```java
private static KafkaProducer<String, MessageDto> createKafkaProducer() {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    props.put(ProducerConfig.CLIENT_ID_CONFIG, CONSUMER_APP_ID);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "serdes.kafka.cn.tuyucheng.taketoday.CustomSerializer");

    return new KafkaProducer(props);
}
```

我们将使用自定义类配置值序列化器属性，并使用默认的StringSerializer配置键序列化器。

其次，我们将创建 Kafka 消费者：

```java
private static KafkaConsumer<String, MessageDto> createKafkaConsumer() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, CONSUMER_APP_ID);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "serdes.kafka.cn.tuyucheng.taketoday.CustomDeserializer");

    return new KafkaConsumer<>(props);
}
```

除了我们自定义类的键和值反序列化器外，还必须包含组 ID。除此之外，我们将自动偏移重置配置设置为最早，以确保生产者在消费者开始之前发送所有消息。

一旦我们创建了生产者和消费者客户端，就可以发送示例消息了：

```java
MessageDto msgProd = MessageDto.builder().message("test").version("1.0").build();

KafkaProducer<String, MessageDto> producer = createKafkaProducer();
producer.send(new ProducerRecord<String, MessageDto>(TOPIC, "1", msgProd));
System.out.println("Message sent " + msgProd);
producer.close();
```

我们可以通过订阅主题来接收与消费者的消息：

```java
AtomicReference<MessageDto> msgCons = new AtomicReference<>();

KafkaConsumer<String, MessageDto> consumer = createKafkaConsumer();
consumer.subscribe(Arrays.asList(TOPIC));

ConsumerRecords<String, MessageDto> records = consumer.poll(Duration.ofSeconds(1));
records.forEach(record -> {
    msgCons.set(record.value());
    System.out.println("Message received " + record.value());
});

consumer.close();
```

控制台中的结果是：

```bash
Serializing...
Message sent MessageDto(message=test, version=1.0)
Deserializing...
Message received MessageDto(message=test, version=1.0)
```

## 4. 总结

在本教程中，我们展示了生产者如何使用 Apache Kafka 中的序列化程序通过网络发送消息。同样，我们还展示了消费者如何使用反序列化器来解释收到的消息。

此外，我们了解了可用的默认序列化器，最重要的是，了解了实现自定义序列化器和反序列化器的能力。
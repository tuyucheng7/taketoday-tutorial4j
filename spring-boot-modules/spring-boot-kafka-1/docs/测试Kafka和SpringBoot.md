## 1. 概述

[Apache Kafka](https://kafka.apache.org/)是一个强大的分布式容错流处理系统。在之前的教程中，我们学习了[如何使用Spring和Kafka](https://www.baeldung.com/spring-kafka)。

在本教程中，**我们将在前一个教程的基础上学习如何编写不依赖于运行的外部Kafka服务器的可靠、独立的集成测试**。

首先，我们将了解如何使用和配置Kafka的嵌入式实例。

然后我们将看到如何在我们的测试中使用流行的框架[Testcontainers](https://www.testcontainers.org/)。

## 2. 依赖关系

当然，我们需要将标准的[spring-kafka](https://central.sonatype.com/artifact/org.springframework.kafka/spring-kafka/3.0.3)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>2.7.2</version>
</dependency>
```

**然后我们需要另外两个专门用于测试的依赖项**。

首先，我们将添加[spring-kafka-test](https://central.sonatype.com/artifact/org.springframework.kafka/spring-kafka-test/3.0.3)工件：

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <version>2.6.3.RELEASE</version>
    <scope>test</scope>
</dependency>
```

最后我们将添加Testcontainers Kafka依赖项，它也可以在[Maven Central](https://central.sonatype.com/artifact/org.testcontainers/kafka/1.17.6)上获得：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <version>1.15.3</version>
    <scope>test</scope>
</dependency>
```

现在我们已经配置了所有必要的依赖项，我们可以使用Kafka编写一个简单的Spring Boot应用程序。

## 3. 一个简单的Kafka生产者-消费者应用程序

在本教程中，我们测试的重点将是一个简单的生产者-消费者Spring Boot Kafka应用程序。

让我们从定义我们的应用程序入口点开始：

```java
@SpringBootApplication
public class KafkaProducerConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerConsumerApplication.class, args);
    }
}
```

正如我们所见，这是一个标准的Spring Boot应用程序。

### 3.1 生产者设置

接下来，让我们考虑一个生产者bean，我们将使用它向给定的Kafka主题发送消息：

```java
@Component
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String payload) {
        LOGGER.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}
```

我们上面定义的KafkaProducer bean只是KafkaTemplate类的包装器。**此类提供高级线程安全操作，例如将数据发送到提供的主题，这正是我们在send方法中所做的**。

### 3.2 消费者设置

同样，我们现在将定义一个简单的消费者bean，它将监听Kafka主题并接收消息：

```java
@Component
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private CountDownLatch latch = new CountDownLatch(1);
    private String payload;

    @KafkaListener(topics = "${test.topic}")
    public void receive(ConsumerRecord<?, ?> consumerRecord) {
        LOGGER.info("received payload='{}'", consumerRecord.toString());
        payload = consumerRecord.toString();
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    // other getters
}
```

我们的简单消费者在receive方法上使用@KafkaListener注解来监听关于给定主题的消息。稍后我们将看到如何从我们的测试中配置test.topic。

此外，receive方法将消息内容存储在我们的bean中并递减latch变量的计数。**这个变量是一个简单的[线程安全计数器](https://www.baeldung.com/cs/async-vs-multi-threading)字段，我们稍后将在测试中使用它来确保我们成功收到消息**。

现在我们已经使用Spring Boot实现了简单的Kafka应用程序，让我们看看如何编写集成测试。

## 4. 关于测试的一句话

**一般来说，在编写干净的集成测试时，我们不应该依赖我们可能无法控制或可能突然停止工作的外部服务**。这可能会对我们的测试结果产生不利影响。

同样，如果我们依赖外部服务，在这种情况下，一个正在运行的Kafka代理，我们可能无法按照我们希望的测试方式设置、控制和拆除它。

### 4.1 应用程序属性

我们将从我们的测试中使用一组非常简单的应用程序配置属性。

我们将在src/test/resources/application.yml文件中定义这些属性：

```yaml
spring:
    kafka:
        consumer:
            auto-offset-reset: earliest
            group-id: tuyucheng
test:
    topic: embedded-test-topic
```

这是我们在使用嵌入式Kafka实例或本地代理时所需的最少属性集。

其中大部分是不言自明的，**但我们应该强调的是消费者属性auto-offset-reset: earliest**。此属性可确保我们的消费者组获取我们发送的消息，因为容器可能会在发送完成后启动。

此外，我们使用值embedded-test-topic配置主题属性，这是我们将在测试中使用的主题。

## 5. 使用嵌入式Kafka进行测试

在本节中，我们将了解如何使用内存中的Kafka实例来运行我们的测试。这也称为嵌入式Kafka。

我们之前添加的依赖项spring-kafka-test包含一些有用的实用程序来帮助测试我们的应用程序。**最值得注意的是，它包含EmbeddedKafkaBroker类**。

考虑到这一点，让我们继续编写我们的第一个集成测试：

```java
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class EmbeddedKafkaIntegrationTest {

    @Autowired
    private KafkaConsumer consumer;

    @Autowired
    private KafkaProducer producer;

    @Value("${test.topic}")
    private String topic;

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived() throws Exception {
        String data = "Sending with our own simple KafkaProducer";

        producer.send(topic, data);

        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertThat(consumer.getPayload(), containsString(data));
    }
}
```

让我们来看看测试的关键部分。

首先，我们首先用两个非常标准的Spring注解来装饰我们的测试类：

-   [@SpringBootTest](https://www.baeldung.com/spring-boot-testing)注解将确保我们的测试引导Spring应用程序上下文。
-   我们还使用[@DirtiesContext](https://www.baeldung.com/spring-dirtiescontext)注解，这将确保在不同测试之间清理和重置此上下文。

**关键部分来了-我们使用@EmbeddedKafka注解将EmbeddedKafkaBroker的实例注入到我们的测试中**。

此外，我们可以使用几个属性来配置嵌入式Kafka节点：

-   partitions：这是每个主题使用的分区数。为了让事情简单明了，我们只希望在我们的测试中使用一个。
-   brokerProperties：Kafka代理的附加属性。同样，我们保持简单并指定纯文本监听器和端口号。

接下来，我们自动注入我们的消费者和生产者类并配置一个主题以使用我们的application.properties中的值。

对于拼图的最后一块，**我们只需向我们的测试主题发送一条消息并验证消息是否已收到并包含我们的测试主题的名称**。

当我们运行测试时，我们将在冗长的Spring输出中看到以下内容：

```shell
...
12:45:35.099 [main] INFO  c.t.t.kafka.embedded.KafkaProducer -
  sending payload='Sending with our own simple KafkaProducer' to topic='embedded-test-topic'
...
12:45:35.103 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1]
  INFO  c.t.t.kafka.embedded.KafkaConsumer - received payload=
  'ConsumerRecord(topic = embedded-test-topic, partition = 0, leaderEpoch = 0, offset = 1,
  CreateTime = 1605267935099, serialized key size = -1, 
  serialized value size = 41, headers = RecordHeaders(headers = [], isReadOnly = false),
  key = null, value = Sending with our own simple KafkaProducer key)'
```

这证实了我们的测试工作正常。棒！**我们现在有一种方法可以使用内存中的Kafka代理编写自包含的、独立的集成测试**。

## 6. 使用TestContainers测试Kafka

有时我们可能会看到真实的外部服务与专门为测试目的提供的嵌入式内存中服务实例之间的细微差别。**虽然不太可能，但也可能是我们测试中使用的端口被占用，从而导致失败**。

考虑到这一点，在本节中，我们将看到我们之前使用[Testcontainers框架](https://www.baeldung.com/docker-test-containers)进行测试的方法的变体。我们将通过集成测试了解如何实例化和管理托管在[Docker容器](https://www.baeldung.com/tag/docker/)内的外部Apache Kafka代理。

让我们定义另一个集成测试，它与我们在上一节中看到的非常相似：

```java
@RunWith(SpringRunner.class)
@Import(testcontainers.kafka.cn.tuyucheng.taketoday.KafkaTestContainersLiveTest.KafkaTestContainersConfiguration.class)
@SpringBootTest(classes = KafkaProducerConsumerApplication.class)
@DirtiesContext
public class KafkaTestContainersLiveTest {

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    @Autowired
    private KafkaConsumer consumer;

    @Autowired
    private KafkaProducer producer;

    @Value("${test.topic}")
    private String topic;

    @Test
    public void givenKafkaDockerContainer_whenSendingWithSimpleProducer_thenMessageReceived() throws Exception {
        String data = "Sending with our own simple KafkaProducer";

        producer.send(topic, data);

        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);

        assertTrue(messageConsumed);
        assertThat(consumer.getPayload(), containsString(data));
    }
}
```

让我们来看看差异。我们声明了一个kafka字段，这是一个标准的[JUnit @ClassRule](https://www.baeldung.com/junit-4-rules)。**该字段是KafkaContainer类的一个实例，它将准备和管理运行Kafka的容器的生命周期**。

为了避免端口冲突，Testcontainers在我们的docker容器启动时动态分配一个端口号。

出于这个原因，我们使用类KafkaTestContainersConfiguration提供自定义消费者和生产者工厂配置：

```java
@Bean
public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "tuyucheng");
    // more standard configuration
    return props;
}

@Bean
public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    // more standard configuration
    return new DefaultKafkaProducerFactory<>(configProps);
}
```

然后，我们在测试开始时通过@Import注解引用此配置。

这样做的原因是我们需要一种方法将服务器地址注入我们的应用程序，如前所述，它是动态生成的。

**我们通过调用getBootstrapServers()方法来实现这一点，该方法将返回引导服务器位置**：

```shell
bootstrap.servers = [PLAINTEXT://localhost:32789]
```

现在当我们运行我们的测试时，我们应该看到Testcontainers做了几件事：

-   检查我们本地的Docker设置
-   如有必要，拉取confluentinc/cp-kafka:5.4.3 docker镜像
-   启动一个新容器并等待它准备就绪
-   最后，在我们的测试完成后关闭并删除容器

同样，通过检查测试输出来确认这一点：

```shell
13:33:10.396 [main] INFO  ? [confluentinc/cp-kafka:5.4.3]
  - Creating container for image: confluentinc/cp-kafka:5.4.3
13:33:10.454 [main] INFO  ? [confluentinc/cp-kafka:5.4.3]
  - Starting container with ID: b22b752cee2e9e9e6ade38e46d0c6d881ad941d17223bda073afe4d2fe0559c3
13:33:10.785 [main] INFO  ? [confluentinc/cp-kafka:5.4.3]
  - Container confluentinc/cp-kafka:5.4.3 is starting: b22b752cee2e9e9e6ade38e46d0c6d881ad941d17223bda073afe4d2fe0559c3
```

## 7. 总结

在本文中，我们了解了使用Spring Boot测试Kafka应用程序的几种方法。

在第一种方法中，我们看到了如何配置和使用本地内存中的Kafka代理。

然后，我们看到了如何使用Testcontainers来设置在docker容器内运行的外部Kafka代理。
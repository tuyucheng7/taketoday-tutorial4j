## 1. 简介

在本教程中，我们将介绍使用SSL身份验证将Spring Boot客户端连接到[Apache Kafka](https://www.baeldung.com/spring-kafka)代理的基本设置。

自2015年以来，安全套接字层(SSL)实际上已被弃用，取而代之的是传输层安全性(TLS)。但是，由于历史原因，Kafka(和Java)仍然指代“SSL”，在本文中我们也将遵循此约定。

## 2. SSL概述

默认情况下，Apache Kafka以明文形式发送所有数据，无需任何身份验证。

首先，我们可以配置SSL用于代理和客户端之间的加密。默认情况下，**这需要使用公钥加密的单向身份验证，其中客户端对服务器证书进行身份验证**。

此外，服务器还可以使用单独的机制(例如SSL或SASL)对客户端进行身份验证，从而实现双向身份验证或双向TLS(mTLS)。基本上，**双向SSL身份验证确保客户端和服务器都使用SSL证书来验证彼此的身份并在两个方向上相互信任**。

在本文中，**代理将使用SSL对客户端进行身份验证**，[keystore](https://www.baeldung.com/java-keystore-truststore-difference#java-keystore)和[truststore](https://www.baeldung.com/java-keystore-truststore-difference#java-keystore)将用于保存证书和密钥。

每个代理都需要自己的密钥库，其中包含私钥和公共证书。客户端使用其信任库来验证此证书并信任服务器。同样，每个客户端也需要自己的密钥库，其中包含其私钥和公共证书。服务器使用其信任库来验证和信任客户端的证书并建立安全连接。

信任库可以包含可以[签署证书](https://www.baeldung.com/openssl-self-signed-cert)的证书颁发机构(CA)。在这种情况下，**代理或客户端信任信任库中存在的由CA签名的任何证书**。这简化了证书身份验证，因为添加新客户端或代理不需要更改信任库。

## 3. 依赖和设置

我们的示例应用程序将是一个简单的Spring Boot应用程序。

为了连接到Kafka，让我们在POM文件中添加[spring-kafka](https://central.sonatype.com/artifact/org.springframework.kafka/spring-kafka/3.0.3)依赖项：

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>2.7.2</version>
</dependency>
```

我们还将使用[Docker Compose](https://www.baeldung.com/ops/docker-compose)文件来配置和测试Kafka服务器设置。最初，让我们在没有任何SSL配置的情况下执行此操作：

```yaml
---
version: '2'
services:
    zookeeper:
        image: confluentinc/cp-zookeeper:6.2.0
        environment:
            ZOOKEEPER_CLIENT_PORT: 2181
            ZOOKEEPER_TICK_TIME: 2000

    kafka:
        image: confluentinc/cp-kafka:6.2.0
        depends_on:
            - zookeeper
        ports:
            - 9092:9092
        environment:
            KAFKA_BROKER_ID: 1
            KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
            KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
            KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
            KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

现在，让我们启动容器：

```shell
docker-compose up
```

这应该会使用默认配置启动代理。

## 4. 代理配置

让我们从查看代理建立安全连接所需的最低配置开始。

### 4.1 单机代理

尽管我们在此示例中没有使用代理的独立实例，但了解启用SSL身份验证所需的配置更改很有用。

首先，**我们需要在server.properties中配置代理以监听端口9093上的SSL连接**：

```properties
listeners=PLAINTEXT://kafka1:9092,SSL://kafka1:9093
advertised.listeners=PLAINTEXT://localhost:9092,SSL://localhost:9093
```

接下来，**需要使用证书位置和凭据配置与密钥库和信任库相关的属性**：

```properties
ssl.keystore.location=/certs/kafka.server.keystore.jks
ssl.keystore.password=password
ssl.truststore.location=/certs/kafka.server.truststore.jks
ssl.truststore.password=password
ssl.key.password=password
```

最后，**必须将代理配置为对客户端进行身份验证**，以实现双向身份验证：

```properties
ssl.client.auth=required
```

### 4.2 Docker Compose

当我们使用Compose来管理我们的代理环境时，让我们将上述所有属性添加到我们的docker-compose.yml文件中：

```yaml
kafka:
    image: confluentinc/cp-kafka:6.2.0
    depends_on:
        - zookeeper
    ports:
        - 9092:9092
        - 9093:9093
    environment:
        # ...
        KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,SSL://localhost:9093
        KAFKA_SSL_CLIENT_AUTH: 'required'
        KAFKA_SSL_KEYSTORE_FILENAME: '/certs/kafka.server.keystore.jks'
        KAFKA_SSL_KEYSTORE_CREDENTIALS: '/certs/kafka_keystore_credentials'
        KAFKA_SSL_KEY_CREDENTIALS: '/certs/kafka_sslkey_credentials'
        KAFKA_SSL_TRUSTSTORE_FILENAME: '/certs/kafka.server.truststore.jks'
        KAFKA_SSL_TRUSTSTORE_CREDENTIALS: '/certs/kafka_truststore_credentials'
    volumes:
        - ./certs/:/etc/kafka/secrets/certs
```

在这里，我们在配置的ports部分公开了SSL端口(9093)。此外，我们已将certs项目文件夹安装在配置的volumes部分中。这包含所需的证书和关联的凭据。

现在，使用Compose重新启动堆栈会在代理日志中显示相关的SSL详细信息：

```shell
...
kafka_1      | uid=1000(appuser) gid=1000(appuser) groups=1000(appuser)
kafka_1      | ===> Configuring ...
<strong>kafka_1      | SSL is enabled.</strong>
....
kafka_1      | [2021-08-20 22:45:10,772] INFO KafkaConfig values:
<strong>kafka_1      |  advertised.listeners = PLAINTEXT://localhost:9092,SSL://localhost:9093
kafka_1      |  ssl.client.auth = required</strong>
<strong>kafka_1      |  ssl.enabled.protocols = [TLSv1.2, TLSv1.3]</strong>
kafka_1      |  ssl.endpoint.identification.algorithm = https
kafka_1      |  ssl.key.password = [hidden]
kafka_1      |  ssl.keymanager.algorithm = SunX509
<strong>kafka_1      |  ssl.keystore.location = /etc/kafka/secrets/certs/kafka.server.keystore.jks</strong>
kafka_1      |  ssl.keystore.password = [hidden]
kafka_1      |  ssl.keystore.type = JKS
kafka_1      |  ssl.principal.mapping.rules = DEFAULT
<strong>kafka_1      |  ssl.protocol = TLSv1.3</strong>
kafka_1      |  ssl.trustmanager.algorithm = PKIX
kafka_1      |  ssl.truststore.certificates = null
<strong>kafka_1      |  ssl.truststore.location = /etc/kafka/secrets/certs/kafka.server.truststore.jks</strong>
kafka_1      |  ssl.truststore.password = [hidden]
kafka_1      |  ssl.truststore.type = JKS
....
```

## 5. Spring Boot客户端

现在服务器设置已完成，我们将创建所需的Spring Boot组件。这些将与我们的代理交互，代理现在需要SSL进行双向身份验证。

### 5.1 生产者

首先，让我们使用[KafkaTemplate](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/core/KafkaTemplate.html)向指定主题发送消息：

```java
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message, String topic) {
        log.info("Producing message: {}", message);
        kafkaTemplate.send(topic, "key", message)
              .addCallback(
                    result -> log.info("Message sent to topic: {}", message),
                    ex -> log.error("Failed to send message", ex)
              );
    }
}
```

send方法是一个异步操作。因此，我们附加了一个简单的回调，它只在代理收到消息后记录一些信息。

### 5.2 消费者

接下来，让我们使用[@KafkaListener](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/annotation/KafkaListener.html)创建一个简单的消费者。这将连接到代理并消费与生产者使用的主题相同的消息：

```java
public class KafkaConsumer {

    public static final String TOPIC = "test-topic";

    public final List<String> messages = new ArrayList<>();

    @KafkaListener(topics = TOPIC)
    public void receive(ConsumerRecord<String, String> consumerRecord) {
        log.info("Received payload: '{}'", consumerRecord.toString());
        messages.add(consumerRecord.value());
    }
}
```

在我们的演示应用程序中，我们保持简单，消费者只需将消息存储在List中。在实际的真实世界系统中，消费者接收消息并根据应用程序的业务逻辑处理它们。

### 5.3 配置

最后，让我们在application.yml中添加必要的配置：

```yaml
spring:
    kafka:
        security:
            protocol: "SSL"
        bootstrap-servers: localhost:9093
        ssl:
            trust-store-location: classpath:/client-certs/kafka.client.truststore.jks
            trust-store-password: <password>
            key-store-location: classpath:/client-certs/kafka.client.keystore.jks
            key-store-password: <password>

        # additional config for producer/consumer 
```

在这里，我们设置了Spring Boot提供的必需属性来配置生产者和消费者。由于这两个组件都连接到同一个代理，我们可以在spring.kafka部分下声明所有基本属性。但是，如果生产者和消费者连接到不同的代理，我们将分别在spring.kafka.producer和spring.kafka.consumer部分下指定它们。

在配置的ssl部分，我们**指向JKS truststore以对Kafka代理进行身份验证**。这包含CA的证书，该证书还签署了代理证书。此外，**我们还提供了Spring客户端密钥库的路径，其中包含由CA签名的证书，该证书应该存在于代理端的信任库中**。

### 5.4 测试

当我们使用Compose文件时，让我们使用[Testcontainers](https://www.baeldung.com/spring-boot-kafka-testing#testing-kafka-with-testcontainers)框架来创建带有生产者和消费者的端到端测试：

```java
@ActiveProfiles("ssl")
@Testcontainers
@SpringBootTest(classes = KafkaSslApplication.class)
class KafkaSslApplicationLiveTest {

    private static final String KAFKA_SERVICE = "kafka";
    private static final int SSL_PORT = 9093;

    @Container
    public DockerComposeContainer<?> container =
          new DockerComposeContainer<>(KAFKA_COMPOSE_FILE)
                .withExposedService(KAFKA_SERVICE, SSL_PORT, Wait.forListeningPort());

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Test
    void givenSslIsConfigured_whenProducerSendsMessageOverSsl_thenConsumerReceivesOverSsl() {
        String message = generateSampleMessage();
        kafkaProducer.sendMessage(message, TOPIC);

        await().atMost(Duration.ofMinutes(2))
              .untilAsserted(() -> assertThat(kafkaConsumer.messages).containsExactly(message));
    }

    private static String generateSampleMessage() {
        return UUID.randomUUID().toString();
    }
}
```

当我们运行测试时，Testcontainers使用我们的Compose文件启动Kafka代理，包括SSL配置。该应用程序还从其SSL配置开始，并通过加密和经过身份验证的连接连接到代理。由于这是一个异步事件序列，因此我们使用[Awaitlity](https://www.baeldung.com/awaitlity-testing)来轮询消费者消息存储中的预期消息。这验证了代理和客户端之间的所有配置和成功的双向身份验证。

## 6. 总结

在本文中，我们介绍了Kafka代理和Spring Boot客户端之间所需的SSL身份验证设置的基础知识。

最初，我们查看了启用双向身份验证所需的代理设置。然后，我们查看了客户端所需的配置，以便通过加密和经过身份验证的连接连接到代理。最后，我们使用集成测试来验证代理和客户端之间的安全连接。
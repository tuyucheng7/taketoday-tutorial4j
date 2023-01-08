## 1. 概述

在本教程中，我们将创建一个连接到ActiveMQ以发送和接收消息的简单Spring应用程序。**我们将重点测试此应用程序以及整体测试Spring JMS的不同方法**。

## 2. 应用设置

首先，我们创建一个可用于测试的基本应用程序。我们需要添加必要的依赖项并实现消息处理。

### 2.1 依赖项

**我们需要[Spring JMS](https://search.maven.org/artifact/org.springframework/spring-jms/4.3.4.RELEASE/jar)来监听JMS消息。我们将使用[ActiveMQ-Junit](https://search.maven.org/artifact/org.apache.activemq.tooling/activemq-junit/5.17.1/jar)为部分测试启动嵌入式ActiveMQ实例，并使用[TestContainers](https://search.maven.org/artifact/org.testcontainers/testcontainers/1.17.3/jar)在其他测试中运行ActiveMQ Docker容器**：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jms</artifactId>
    <version>4.3.4.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.apache.activemq.tooling</groupId>
    <artifactId>activemq-junit</artifactId>
    <version>5.16.5</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.17.3</version>
    <scope>test</scope>
</dependency>
```

### 2.2 Application类

现在我们创建一个可以监听消息的Spring应用程序：

```java
@ComponentScan
public class JmsApplication {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(JmsApplication.class);
    }
}
```

**我们需要创建一个配置类并使用[@EnableJms注解]()启用JMS，并配置[ConnectionFactory]()以连接到我们的ActiveMQ实例**：

```java
@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }
}
```

在此之后，我们创建可以接收和处理消息的监听器：

```java
@Component
public class MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @JmsListener(destination = "queue-1")
    public void sampleJmsListenerMethod(TextMessage message) throws JMSException {
        logger.info("JMS listener received text message: {}", message.getText());
    }
}
```

我们还需要一个可以发送消息的类：

```java
@Component
public class MessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    public void sendTextMessage(String destination, String message) {
        logger.info("Sending message to {} destination with text {}", destination, message);
        jmsTemplate.send(destination, s -> s.createTextMessage(message));
    }
}
```

## 3. 使用嵌入式ActiveMQ进行测试

接下来开始测试我们的应用程序，**首先我们使用嵌入式ActiveMQ实例**。让我们创建我们的测试类并添加一个JUnit Rule来管理我们的ActiveMQ实例：

```java
@RunWith(SpringRunner.class)
public class EmbeddedActiveMqTests4 {

    @ClassRule
    public static EmbeddedActiveMQBroker embeddedBroker = new EmbeddedActiveMQBroker();

    @Test
    public void test() {
    }

    // ...
}
```

让我们运行这个空测试并观察日志，我们可以看到**嵌入式代理已通过我们的测试启动**：

```shell
INFO | Starting embedded ActiveMQ broker: embedded-broker
INFO | Using Persistence Adapter: MemoryPersistenceAdapter
INFO | ApacheActiveMQ5.14.1 (embedded-broker, ID:DESKTOP-52539-254421135-0:1) is starting
INFO | ApacheActiveMQ5.14.1 (embedded-broker, ID:DESKTOP-52539-254421135-0:1) started
INFO | For help or more information please see: http://activemq.apache.org
INFO | Connector vm://embedded-broker started
INFO | Successfully connected to vm://embedded-broker?create=false
```

在我们的测试类中执行完所有测试后，代理将停止。

我们需要配置我们的应用程序以连接到这个ActiveMQ实例，以便我们可以正确测试我们的MessageListener和MessageSender类：

```java
@Configuration
@EnableJms
static class TestConfiguration {
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(embeddedBroker.getVmURL());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }
}
```

此类使用一个特殊的ConnectionFactory，它从我们的嵌入式代理获取URL。现在我们需要通过将@ContextConfiguration注解添加到包含测试的类来使用此配置：

```java
@ContextConfiguration(classes = {TestConfiguration.class, MessageSender.class}) 
public class EmbeddedActiveMqTests {
}
```

### 3.1 发送消息

下面我们编写第一个测试并检查MessageSender类的功能。首先，我们需要通过简单地将其作为字段注入来获取对此类实例的引用：

```java
@Autowired
private MessageSender messageSender;
```

然后我们向ActiveMQ发送一条简单的消息并添加一些[断言]()来检查功能：

```java
@Test
public void whenSendingMessage_thenCorrectQueueAndMessageText() throws JMSException {
    String queueName = "queue-2";
    String messageText = "Test message";

    messageSender.sendTextMessage(queueName, messageText);

    assertEquals(1, embeddedBroker.getMessageCount(queueName));
    TextMessage sentMessage = embeddedBroker.peekTextMessage(queueName);
    assertEquals(messageText, sentMessage.getText());
}
```

**如果测试通过，则可以确定我们的MessageSender工作正常，因为在我们发送消息后队列只包含一个具有正确文本的条目**。

### 3.2 接收消息

接下来也检查一下我们的监听器类，**我们从创建一个新的测试方法并使用嵌入式代理发送消息开始**。我们的监听器设置为使用“queue-1”作为其目的地，因此我们需要确保我们在这里使用相同的名称。

我们使用[Mockito]()来检查监听器的行为，并使用[@SpyBean]()注解来获取MessageListener的实例：

```java
@SpyBean
private MessageListener messageListener;
```

然后，我们[检查该方法是否被调用]()并使用[ArgumentCaptor]()捕获接收到的方法参数：

```java
@Test
public void whenListening_thenReceivingCorrectMessage() throws JMSException {
    String queueName = "queue-1";
    String messageText = "Test message";

    embeddedBroker.pushMessage(queueName, messageText);
    assertEquals(1, embeddedBroker.getMessageCount(queueName));

    ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);

    verify(messageListener, Mockito.timeout(100)).sampleJmsListenerMethod(messageCaptor.capture());

    TextMessage receivedMessage = messageCaptor.getValue();
    assertEquals(messageText, receivedMessage.getText());
}
```

运行测试之后，应该可以正常通过。

## 4. 使用TestContainer进行测试

接下来介绍在Spring应用程序中测试JMS的另一种方法。**我们可以使用[TestContainers]()来运行ActiveMQ Docker容器并在我们的测试中连接到它**。

我们创建一个新的测试类并将Docker容器包含为JUnit Rule：

```java
@RunWith(SpringRunner.class)
public class TestContainersActiveMqTests {

    @ClassRule
    public static GenericContainer<?> activeMqContainer 
      = new GenericContainer<>(DockerImageName.parse("rmohr/activemq:5.14.3")).withExposedPorts(61616);

    @Test
    public void test() throws JMSException {
    }
}
```

**运行这个测试并观察日志。我们可以看到一些与TestContainers相关的信息，因为它正在拉取指定的docker镜像并启动容器**：

```shell
INFO | Creating container for image: rmohr/activemq:5.14.3
INFO | Container rmohr/activemq:5.14.3 is starting: e9b0ddcd45c54fc9994aff99d734d84b5fae14b55fdc70887c4a2c2309b229a7
INFO | Container rmohr/activemq:5.14.3 started in PT2.635S

```

然后我们创建一个类似于我们使用ActiveMQ实现的配置类，唯一的区别是ConnectionFactory的配置：

```java
@Bean
public ConnectionFactory connectionFactory() {
    String brokerUrlFormat = "tcp://%s:%d";
    String brokerUrl = String.format(brokerUrlFormat, activeMqContainer.getHost(), activeMqContainer.getFirstMappedPort());
    return new ActiveMQConnectionFactory(brokerUrl);
}
```

### 4.1 发送消息

现在测试我们的MessageSender类，看看它是否适用于这个Docker容器。**这次我们不能在EmbeddedBroker上使用这些方法，但是Spring JmsTemplate也很容易使用**：

```java
@Autowired
private MessageSender messageSender;

@Autowired
private JmsTemplate jmsTemplate;

@Test
public void whenSendingMessage_thenCorrectQueueAndMessageText() throws JMSException {
    String queueName = "queue-2";
    String messageText = "Test message";

    messageSender.sendTextMessage(queueName, messageText);

    Message sentMessage = jmsTemplate.receive(queueName);
    Assertions.assertThat(sentMessage).isInstanceOf(TextMessage.class);

    assertEquals(messageText, ((TextMessage) sentMessage).getText());
}
```

我们可以使用JmsTemplate读取队列的内容并检查我们的类是否发送了正确的消息。

### 4.2 接收消息

测试我们的监听器类也没有太大不同，**我们使用JmsTemplate发送消息并验证我们的监听器是否接收到了正确的文本**：

```java
@SpyBean
private MessageListener messageListener;

@Test
public void whenListening_thenReceivingCorrectMessage() throws JMSException {
    String queueName = "queue-1";
    String messageText = "Test message";

    jmsTemplate.send(queueName, s -> s.createTextMessage(messageText));

    ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);

    verify(messageListener, Mockito.timeout(100)).sampleJmsListenerMethod(messageCaptor.capture());

    TextMessage receivedMessage = messageCaptor.getValue();
    assertEquals(messageText, receivedMessage.getText());
}
```

## 5. 总结

在本文中，我们创建了一个可以使用Spring JMS发送和接收消息的基本应用程序，然后我们介绍了两种测试该JMS应用程序的方法。

首先，我们使用了一个嵌入式ActiveMQ实例，它甚至提供了一些方便的方法来与代理进行交互。其次，我们使用TestContainers来使用更能模拟真实场景的docker容器来测试我们的代码。
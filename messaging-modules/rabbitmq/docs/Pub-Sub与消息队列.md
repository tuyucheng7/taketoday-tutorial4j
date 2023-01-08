## 1. 概述

在本教程中，我们介绍消息队列和发布者/订阅者的使用。这些是分布式系统中用于两个或多个服务相互通信的常见模式。

对于本教程，所有示例都将使用RabbitMQ消息代理进行演示，因此首先按照[RabbitMQ的教程](https://www.rabbitmq.com/download.html)在本地启动和运行。如需更深入地了解RabbitMQ，请查看我们的[其他教程](RabbitMQ简介.md)。

注意：有许多RabbitMQ的替代方案可用于本教程中的相同示例，例如[Kafka](https://kafka.apache.org/)、[Google Cloud Pub-Sub](https://cloud.google.com/pubsub)和[Amazon SQS](https://aws.amazon.com/sqs/)等等。

## 2. 什么是消息队列？

消息队列由一个发布服务和多个通过队列进行通信的消费者服务组成，这种通信通常是发布者向消费者发出命令的一种方式。发布服务通常会将消息放在队列或交换机中，单个消费者服务将消费此消息并根据此消息执行操作。

考虑以下交换机：

<img src="../assets/img.png"> 


从这里，我们可以看到发布服务将消息“m<sub>n+1</sub>”放入队列。此外，我们还可以看到队列中已经存在多条消息等待消费。在右侧，我们有2个消费服务“A”和“B”，它们正在监听队列中的消息。

现在让我们考虑一段时间后的相同交换机：

<img src="../assets/img_1.png"> 

首先，我们可以看到发布者的消息已经被推送到队列的尾部。接下来，要考虑的重要部分是图片的右侧。我们可以看到消费者“A”已经读取了消息“m<sub>1</sub>”，因此，它不再在队列中可供另一个服务“B”消费。

### 2.1 在哪里使用消息队列

消息队列通常用于我们希望从服务中委派工作的地方。**在这样做时，我们希望确保工作只执行一次**。

使用消息队列在微服务架构和开发基于云或无服务器的应用程序中很流行，因为它允许我们根据负载水平扩展我们的应用程序。

例如，如果队列中有很多消息等待处理，我们可以启动多个消费者服务，这些服务监听同一个消息队列并处理涌入的消息。处理完消息后，可以在流量最小时关闭服务以节省运行成本。

### 2.2 使用RabbitMQ的示例

为了清楚起见，让我们看一个例子。我们的示例将采用披萨餐厅的形式。想象一下，人们可以通过应用程序订购比萨饼，比萨饼店的厨师会在他们进来时接单。在这个例子中，客户是我们的发布者，厨师是我们的消费者。

首先，定义我们的队列：

```java
private static final String MESSAGE_QUEUE = "pizza-message-queue";

@Bean
public Queue queue() {
    return new Queue(MESSAGE_QUEUE);
}
```

使用Spring AMQP，我们创建了一个名为“pizza-message-queue”的队列。接下来我们定义发布者，它将消息发布到新定义的队列：

```java
public class Publisher {

    private RabbitTemplate rabbitTemplate;
    private String queue;

    public Publisher(RabbitTemplate rabbitTemplate, String queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    @PostConstruct
    public void postMessages() {
        rabbitTemplate.convertAndSend(queue, "1 Pepperoni");
        rabbitTemplate.convertAndSend(queue, "3 Margarita");
        rabbitTemplate.convertAndSend(queue, "1 Ham and Pineapple (yuck)");
    }
}
```

Spring AMQP将为我们创建一个RabbitTemplate bean，它连接到我们的RabbitMQ交换机以减少配置开销。我们的发布者通过向我们的队列发送3条消息来利用它。

现在我们的比萨饼订单已经到来，我们需要一个单独的消费者应用程序。这将在示例中充当我们的厨师并读取消息：

```java
public class Consumer {
    public void receiveOrder(String message) {
        System.out.printf("Order received: %s%n", message);
    }
}
```

接下来为我们的队列创建一个MessageListenerAdapter，它将使用反射调用我们的消费者的receiveOrder方法：

```java
@Bean
public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(MESSAGE_QUEUE);
    container.setMessageListener(listenerAdapter);
    return container;
}

@Bean
public MessageListenerAdapter listenerAdapter(Consumer consumer) {
    return new MessageListenerAdapter(consumer, "receiveOrder");
}
```

从队列中读取的消息现在将被路由到Consumer类的receiveOrder方法。要运行此应用程序，我们可以创建任意数量的消费者应用程序来完成收到的订单。例如，如果队列中有400个披萨订单，那么我们可能需要超过1个消费者“厨师”，否则订单的处理会很慢。在这种情况下，我们可能会启动10个消费者实例来及时完成订单。

## 3. 什么是发布订阅？

现在我们已经介绍了消息队列，让我们来看看发布-订阅。**相反，对于消息队列，在发布-订阅架构中，我们希望我们所有的消费(订阅)应用程序至少获得我们的发布者发布到交换机的消息的1个副本**。

考虑以下交换机：

<img src="../assets/img_2.png">

在左侧，我们有一个发布者向主题发送消息“m<sub>n+1</sub>”，该主题将此消息广播到其订阅者，这些订阅者绑定到队列，每个队列都有一个监听订阅者服务等待消息。

现在让我们考虑一段时间后的同一个交换机：

<img src="../assets/img_3.png"> 

两个订阅服务都在消费“m<sub>1</sub>”，因为它们都收到了该消息的副本。此外，该主题正在向其所有订阅者分发新消息“m<sub>n+1</sub>”。

当我们需要保证每个订阅者都能获得消息的副本时，应该使用发布-订阅模式。

### 3.1 使用RabbitMQ的示例

假设我们有一个服装网站，本网站能够向用户发送推送通知以通知他们交易。我们的系统可以通过电子邮件或文本警报发送通知。在这种情况下，网站是我们的发布者，文本和电子邮件提醒服务是我们的订阅者。

首先，我们定义主题交换机并将2个队列绑定到它：

```java
private static final String PUB_SUB_TOPIC = "notification-topic";
private static final String PUB_SUB_EMAIL_QUEUE = "email-queue";
private static final String PUB_SUB_TEXT_QUEUE = "text-queue";

@Bean
public Queue emailQueue() {
    return new Queue(PUB_SUB_EMAIL_QUEUE);
}

@Bean
public Queue textQueue() {
    return new Queue(PUB_SUB_TEXT_QUEUE);
}

@Bean
public TopicExchange exchange() {
    return new TopicExchange(PUB_SUB_TOPIC);
}

@Bean
public Binding emailBinding(Queue emailQueue, TopicExchange exchange) {
    return BindingBuilder.bind(emailQueue).to(exchange).with("notification");
}

@Bean
public Binding textBinding(Queue textQueue, TopicExchange exchange) {
    return BindingBuilder.bind(textQueue).to(exchange).with("notification");
}
```

我们使用路由键“notification”绑定了2个队列，这意味着使用此路由键在主题上发布的任何消息都将发送到两个队列。更新我们之前创建的Publisher类，我们可以向我们的交换机发送一些消息：

```java
rabbitTemplate.convertAndSend(topic, "notification", "New Deal on T-Shirts: 95% off!");
rabbitTemplate.convertAndSend(topic, "notification", "2 for 1 on all Jeans!");
```

## 4. 比较

既然我们已经介绍了这两个领域，那么让我们简要比较一下这两种类型的交换机。

如前所述，**消息队列和发布-订阅架构模式都是分解应用程序以使其更具水平可扩展性的好方法**。

**使用发布-订阅或消息队列的另一个好处是通信比传统的同步通信模式更持久**。例如，如果应用程序A通过异步HTTP调用与应用程序B通信，那么如果其中一个应用程序出现故障，数据将丢失，并且必须重试请求。使用消息队列，如果消费者应用程序实例出现故障，那么另一个消费者将能够处理该消息。使用发布-订阅，如果订阅者宕机，那么一旦它恢复了，它错过的消息将可以在其订阅队列中使用。

最后，上下文是关键。选择是使用发布-订阅还是消息队列架构归结为准确定义你希望消费服务的行为方式。**要记住的最重要的因素是：“每个消费者是否都收到每条消息重要吗？**”

## 5. 总结

在本教程中，我们介绍了发布-订阅和消息队列以及它们各自的一些特征。
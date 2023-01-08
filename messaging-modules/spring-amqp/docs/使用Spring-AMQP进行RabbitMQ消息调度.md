## 1. 概述

在本教程中，我们将探讨Spring AMQP和RabbitMQ中Fanout和Topic交换机的概念。

在高层次上，**Fanout交换机会将相同的消息广播到所有绑定队列，而Topic交换机使用路由键将消息传递到特定的绑定队列**。

## 2. 配置Fanout交换机

让我们设置一个绑定了两个队列的Fanout交换机。当我们向这个交换机发送消息时，两个队列都会收到消息。Fanout交换机会忽略消息中包含的任何路由键。

**Spring AMQP允许我们在Declarables对象中聚合队列、交换机和绑定的所有声明**：

```java

@Configuration
public class BroadcastConfig {
    private static final boolean NON_DURABLE = false;
    public final static String FANOUT_QUEUE_1_NAME = "cn.tuyucheng.taketoday.spring-amqp-simple.fanout.queue1";
    public final static String FANOUT_QUEUE_2_NAME = "cn.tuyucheng.taketoday.spring-amqp-simple.fanout.queue2";
    public final static String FANOUT_EXCHANGE_NAME = "cn.tuyucheng.taketoday.spring-amqp-simple.fanout.exchange";

    @Bean
    public Declarables fanoutBindings() {
        Queue fanoutQueue1 = new Queue(FANOUT_QUEUE_1_NAME, NON_DURABLE);
        Queue fanoutQueue2 = new Queue(FANOUT_QUEUE_2_NAME, NON_DURABLE);

        FanoutExchange fanoutExchange = new FanoutExchange(FANOUT_EXCHANGE_NAME, NON_DURABLE, false);

        return new Declarables(
                fanoutQueue1,
                fanoutQueue2,
                fanoutExchange,
                BindingBuilder.bind(fanoutQueue1).to(fanoutExchange),
                BindingBuilder.bind(fanoutQueue2).to(fanoutExchange)
        );
    }
}
```

## 3. 配置Topic交换机

现在我们设置一个包含两个队列的主题交换机，每个队列都有不同的绑定模式：

```java

@Configuration
public class BroadcastConfig {
    private static final boolean NON_DURABLE = false;
    public final static String TOPIC_QUEUE_1_NAME = "cn.tuyucheng.taketoday.spring-amqp-simple.topic.queue1";
    public final static String TOPIC_QUEUE_2_NAME = "cn.tuyucheng.taketoday.spring-amqp-simple.topic.queue2";
    public final static String TOPIC_EXCHANGE_NAME = "cn.tuyucheng.taketoday.spring-amqp-simple.topic.exchange";
    public static final String BINDING_PATTERN_IMPORTANT = "*.important.*";
    public static final String BINDING_PATTERN_ERROR = "#.error";

    @Bean
    public Declarables topicBindings() {
        Queue topicQueue1 = new Queue(TOPIC_QUEUE_1_NAME, NON_DURABLE);
        Queue topicQueue2 = new Queue(TOPIC_QUEUE_2_NAME, NON_DURABLE);

        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE_NAME, NON_DURABLE, false);

        return new Declarables(
                topicQueue1,
                topicQueue2,
                topicExchange,
                BindingBuilder.bind(topicQueue1).to(topicExchange).with(BINDING_PATTERN_IMPORTANT),
                BindingBuilder.bind(topicQueue2).to(topicExchange).with(BINDING_PATTERN_ERROR)
        );
    }
}
```

**Topic交换机允许我们使用不同的路由键模式将队列绑定到它**。这非常灵活，允许我们将具有相同模式甚至多个模式的多个队列绑定到同一个队列。

当消息的路由键与模式匹配时，它将被放入队列中。
**如果一个队列有多个与消息的路由键匹配的绑定，则只有一个消息副本放置在队列中**。

我们的绑定模式可以使用星号(“*”)来匹配特定位置的单词，或者使用井号(“#”)来匹配零个或多个单词。

因此，我们的topicQueue1将接收包含三个单词模式的路由键的消息，中间的单词是“important” - 例如：
“user.important.error”或“blog.important.notification”。

而我们的topicQueue2将收到路由键以单词error结尾的消息；例如“error”、“user.important.error”或“blog.post.save.error”。

## 4. 配置生产者

我们使用RabbitTemplate的convertAndSend方法来发送我们的消息：

```java

@SpringBootApplication
public class BroadcastMessageApplication {
    private static final String ROUTING_KEY_USER_IMPORTANT_WARN = "user.important.warn";
    private static final String ROUTING_KEY_USER_IMPORTANT_ERROR = "user.important.error";

    @Bean
    public ApplicationRunner runner(RabbitTemplate rabbitTemplate) {
        String message = " payload is broadcast";
        return args -> {
            rabbitTemplate.convertAndSend(BroadcastConfig.FANOUT_EXCHANGE_NAME, "", "fanout" + message);
            rabbitTemplate.convertAndSend(BroadcastConfig.TOPIC_EXCHANGE_NAME, ROUTING_KEY_USER_IMPORTANT_WARN, "topic important warn" + message);
            rabbitTemplate.convertAndSend(BroadcastConfig.TOPIC_EXCHANGE_NAME, ROUTING_KEY_USER_IMPORTANT_ERROR, "topic important error" + message);
        };
    }
}
```

**RabbitTemplate为不同的交换机类型提供了许多重载的convertAndSend()方法**。

当我们向Fanout交换机发送消息时，路由键被忽略，消息被传递到所有绑定队列。

当我们向Topic交换机发送消息时，我们需要传递一个路由键。根据此路由键，消息将被传递到特定队列。

## 5. 配置消费者

最后，让我们配置四个消费者(每个队列一个)来消费产生的消息：

```java

@SpringBootApplication
public class BroadcastMessageApplication {

    @RabbitListener(queues = {BroadcastConfig.FANOUT_QUEUE_1_NAME})
    public void receiveMessageFromFanout1(String message) {
        System.out.println("Received fanout 1 message: " + message);
    }

    @RabbitListener(queues = {BroadcastConfig.FANOUT_QUEUE_2_NAME})
    public void receiveMessageFromFanout2(String message) {
        System.out.println("Received fanout 2 message: " + message);
    }

    @RabbitListener(queues = {BroadcastConfig.TOPIC_QUEUE_1_NAME})
    public void receiveMessageFromTopic1(String message) {
        System.out.println("Received topic 1 (" + BroadcastConfig.BINDING_PATTERN_IMPORTANT + ") message: " + message);
    }

    @RabbitListener(queues = {BroadcastConfig.TOPIC_QUEUE_2_NAME})
    public void receiveMessageFromTopic2(String message) {
        System.out.println("Received topic 2 (" + BroadcastConfig.BINDING_PATTERN_ERROR + ") message: " + message);
    }
}
```

**我们使用@RabbitListener注解配置消费者**。注解中传递的唯一参数是队列的名称。消费者在这里不知道交换机或路由键。

## 6. 运行案例

我们的项目是一个Spring Boot应用程序，因此它将初始化应用程序以及与RabbitMQ的连接，并配置所有队列、交换机和绑定。

默认情况下，我们的应用程序需要一个RabbitMQ实例在localhost的5672端口上运行。可以在application.yaml中修改这些默认值。

当我们运行我们的应用程序主类时，我们应该可以看到以下的输出：

```shell
Received topic 1 (*.important.*) message: topic important warn payload is broadcast
Received fanout 2 message: fanout payload is broadcast
Received topic 2 (#.error) message: topic important error payload is broadcast
Received fanout 1 message: fanout payload is broadcast
Received topic 1 (*.important.*) message: topic important error payload is broadcast
```

当然，我们无法保证这些消息的输出顺序。

## 7. 总结

在这个教程中，我们介绍了Spring AMQP和RabbitMQ的Fanout和Exchange交换机。
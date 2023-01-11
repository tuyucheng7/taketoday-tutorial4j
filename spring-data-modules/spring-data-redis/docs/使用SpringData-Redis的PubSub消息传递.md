## **一、概述**

在探索 Spring Data Redis 系列的第二篇文章中，我们将了解发布/订阅消息队列。

在 Redis 中，发布者没有被编程为将消息发送给特定的订阅者。相反，发布的消息被表征为通道，而不知道可能有什么（如果有）订阅者。

同样，订阅者表示对一个或多个主题感兴趣，并且只接收感兴趣的消息，而不知道有哪些（如果有）发布者。

发布者和订阅者的这种解耦可以允许更大的可扩展性和更动态的网络拓扑。

## **2.Redis配置**

让我们开始添加消息队列所需的配置。

首先，我们将定义一个*MessageListenerAdapter* bean，它包含一个名为*RedisMessageSubscriber的**MessageListener*接口的自定义实现。这个 bean 在 pub-sub 消息传递模型中充当订阅者：

```java
@Bean
MessageListenerAdapter messageListener() { 
    return new MessageListenerAdapter(new RedisMessageSubscriber());
}复制
```

*RedisMessageListenerContainer*是 Spring Data Redis 提供的一个类，它为 Redis 消息监听器提供异步行为。这是在内部调用的，根据[Spring Data Redis 文档](https://docs.spring.io/spring-data/data-redis/docs/current/api/org/springframework/data/redis/listener/RedisMessageListenerContainer.html)，“处理监听、转换和消息调度的低级细节。”

```java
@Bean
RedisMessageListenerContainer redisContainer() {
    RedisMessageListenerContainer container 
      = new RedisMessageListenerContainer(); 
    container.setConnectionFactory(jedisConnectionFactory()); 
    container.addMessageListener(messageListener(), topic()); 
    return container; 
}复制
```

我们还将使用自定义构建的*MessagePublisher*接口和*RedisMessagePublisher*实现创建一个 bean。这样，我们就可以拥有一个通用的消息发布 API，并让 Redis 实现将*redisTemplate*和*主题*作为构造函数参数：

```java
@Bean
MessagePublisher redisPublisher() { 
    return new RedisMessagePublisher(redisTemplate(), topic());
}复制
```

最后，我们将设置一个主题，发布者将向其发送消息，而订阅者将接收消息：

```java
@Bean
ChannelTopic topic() {
    return new ChannelTopic("messageQueue");
}复制
```

## **3. 发布消息**

### **3.1. 定义\*MessagePublisher\*接口**

Spring Data Redis 不提供用于消息分发的*MessagePublisher接口。*我们可以定义一个自定义接口，它将在实现中使用*redisTemplate*：

```java
public interface MessagePublisher {
    void publish(String message);
}复制
```

### **3.2. \*RedisMessagePublisher\*实现**

我们的下一步是提供*MessagePublisher*接口的实现，添加消息发布细节并使用*redisTemplate 中的功能。*

该模板包含一组非常丰富的函数，适用于广泛的操作——其中*convertAndSend*能够通过主题将消息发送到队列：

```java
public class RedisMessagePublisher implements MessagePublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic topic;

    public RedisMessagePublisher() {
    }

    public RedisMessagePublisher(
      RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
      this.redisTemplate = redisTemplate;
      this.topic = topic;
    }

    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
复制
```

如您所见，发布者实现很简单。它使用redisTemplate 的*convertAndSend* *()*方法格式化给定消息并将其发布到配置的主题。

主题实现发布和订阅语义：当消息发布时，它会发送给所有注册收听该主题的订阅者。

## **4.订阅消息**

*RedisMessageSubscriber*实现了 Spring Data Redis 提供的*MessageListener*接口：

```java
@Service
public class RedisMessageSubscriber implements MessageListener {

    public static List<String> messageList = new ArrayList<String>();

    public void onMessage(Message message, byte[] pattern) {
        messageList.add(message.toString());
        System.out.println("Message received: " + message.toString());
    }
}复制
```

请注意，还有一个名为*pattern*的第二个参数，我们在此示例中没有使用它。Spring Data Redis 文档指出此参数表示“与通道匹配的模式（如果指定）”，但它可以为*null*。

## **5. 发送和接收消息**

现在我们把它们放在一起。让我们创建一条消息，然后使用*RedisMessagePublisher*发布它：

```java
String message = "Message " + UUID.randomUUID();
redisMessagePublisher.publish(message);复制
```

当我们调用*publish(message)*时，内容被发送到 Redis，在那里它被路由到我们的发布者中定义的消息队列主题。然后将其分发给该主题的订阅者。

您可能已经注意到*RedisMessageSubscriber*是一个侦听器，它将自己注册到队列中以检索消息。

消息到达时，触发订阅者定义的*onMessage()方法。*

*在我们的示例中，我们可以通过检查RedisMessageSubscriber中的**messageList*来验证我们是否收到了已发布的消息：

```java
RedisMessageSubscriber.messageList.get(0).contains(message)
复制
```

## **六，结论**

在本文中，我们检查了使用 Spring Data Redis 的发布/订阅消息队列实现。
## 1. AWS消息传递支持

### 1.1 SQS(简单队列服务)

我们可以使用QueueMessagingTemplate将消息发送到SQS队列。

要创建这个bean，我们可以使用AmazonSQSAsync客户端，当使用Spring Boot启动器时，它在应用程序上下文中默认可用：

```java
@Bean
public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
    return new QueueMessagingTemplate(amazonSQSAsync);
}
```

然后，我们可以使用convertAndSend()方法发送消息：

```java
@Autowired
QueueMessagingTemplate messagingTemplate;
 
public void send(String topicName, Object message) {
    messagingTemplate.convertAndSend(topicName, message);
}
```

由于Amazon SQS仅接受字符串负载，因此Java对象会自动序列化为JSON。

我们还可以使用@SqsListener配置监听器：

```java
@SqsListener("spring-cloud-test-queue")
public void receiveMessage(String message, @Header("SenderId") String senderId) {
    // ...
}
```

该方法将从spring-cloud-test-queue接收消息，然后处理它们。我们还可以使用方法参数上的@Header注解来检索消息标头。

如果第一个参数是自定义Java对象而不是String，Spring将使用JSON转换将消息转换为该类型。

### 1.2 SNS(简单通知服务)

与SQS类似，我们可以使用NotificationMessagingTemplate向主题发布消息。

要创建它，我们需要一个AmazonSNS客户端：

```java
@Bean
public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS amazonSNS) {
    return new NotificationMessagingTemplate(amazonSNS);
}
```

然后，我们可以向主题发送通知：

```java
@Autowired
NotificationMessagingTemplate messagingTemplate;

public void send(String Object message, String subject) {
    messagingTemplate.sendNotification("spring-cloud-test-topic", message, subject);
}
```

在AWS支持的多个SNS端点-SQS、HTTP(S)、电子邮件和SMS中，**该项目仅支持HTTP(S)**。

我们可以在MVC控制器中配置端点：

```java
@Controller
@RequestMapping("/topic-subscriber")
public class SNSEndpointController {

    @NotificationSubscriptionMapping
    public void confirmUnsubscribeMessage(NotificationStatus notificationStatus) {
        notificationStatus.confirmSubscription();
    }

    @NotificationMessageMapping
    public void receiveNotification(@NotificationMessage String message,
                                    @NotificationSubject String subject) {
        // handle message
    }

    @NotificationUnsubscribeConfirmationMapping
    public void confirmSubscriptionMessage(NotificationStatus notificationStatus) {
        notificationStatus.confirmSubscription();
    }
}
```

**我们需要将主题名称添加到控制器级别的@RequestMapping注解中**。此控制器启用HTTP(s)端点–/topic-subscriber，SNS主题使用它来创建订阅。

例如，我们可以通过调用URL来订阅一个主题：

```shell
https://host:port/topic-subscriber/
```

**请求中的标头确定调用三种方法中的哪一种**。

当标头[x-amz-sns-message-type=SubscriptionConfirmation]存在并确认对主题的新订阅时，将调用带有@NotificationSubscriptionMapping注解的方法。

订阅后，主题将使用标头[x-amz-sns-message-type=Notification]向端点发送通知。这将调用用@NotificationMessageMapping注解的方法。

最后，当端点取消订阅主题时，会收到带有标头[x-amz-sns-message-type=UnsubscribeConfirmation]的确认请求。

这将调用用@NotificationUnsubscribeConfirmationMapping注解的方法，该方法确认取消订阅操作。

请注意@RequestMapping中的值与其订阅的主题名称无关。

## 2. 总结

在最后一篇文章中，我们探讨了Spring Cloud对AWS Messaging的支持-这是关于Spring Cloud和AWS的快速系列文章的总结。
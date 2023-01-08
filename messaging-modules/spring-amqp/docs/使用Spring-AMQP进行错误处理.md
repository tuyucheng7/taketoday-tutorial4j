## 1. 概述

异步消息传递是一种松耦合的分布式通信，在实现事件驱动架构方面越来越流行。
幸运的是，Spring框架提供了Spring AMQP模块，允许我们构建基于AMQP的消息传递解决方案。

另一方面，在这种环境中处理错误可能是一项艰巨的任务。因此，在本教程中，我们将介绍处理错误的不同策略。

## 2. 环境搭建

**对于本教程，我们将使用实现AMQP标准的RabbitMQ**。
此外，Spring AMQP提供了spring-rabbit模块，使集成变得非常容易。

我们将RabbitMQ作为独立服务器运行。通过执行以下命令在Docker容器中运行它：

```shell
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:3.8.2-management
```

## 3. 错误场景

通常，由于其分布式特性，与单体应用程序或单个打包应用程序相比，基于消息传递的系统中可能会出现更多类型的错误。

以下是可能的一些异常类型：

+ 网络或I/O相关 – 网络连接和I/O操作的一般故障。
+ 协议或基础设施相关 - 通常表示消息传递基础结构配置错误。
+ 代理相关 - 警告客户端和AMQP代理之间配置不正确的故障。例如，达到定义的限制或阈值、身份验证或无效策略配置。
+ 应用程序和消息相关 - 通常表明违反某些业务或应用程序规则的异常。

当然，以上异常类型并不完整，但包含最常见的错误类型。

我们应该注意到，Spring AMQP可以开箱即用地处理与连接相关的低级问题，例如通过应用重试或重新排队策略。
此外，大多数异常都转换为AmqpException或其子类之一。

在接下来的部分中，我们将主要关注特定于应用程序和高级别的错误，然后介绍全局错误处理策略。

## 4. 项目构建

现在，让我们定义一个简单的队列和交换机配置：

```java

@Configuration
@ConditionalOnProperty(
        value = "amqp.configuration.current",
        havingValue = "simple-dlq"
)
public class SimpleDLQAmqpConfiguration {
    public static final String QUEUE_MESSAGES = "tuyucheng-messages-queue";
    public static final String EXCHANGE_MESSAGES = "tuyucheng-messages-exchange";

    @Bean
    Queue messagesQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES)
                .build();
    }

    @Bean
    DirectExchange messagesExchange() {
        return new DirectExchange(EXCHANGE_MESSAGES);
    }

    @Bean
    Binding bindingMessages() {
        return BindingBuilder.bind(messagesQueue()).to(messagesExchange()).with(QUEUE_MESSAGES);
    }
}
```

接下来，让我们创建一个简单的生产者：

```java

@Service
public class MessageProducer {
    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);
    private int messageNumber = 0;
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage() {
        log.info("Sending message...");
        rabbitTemplate.convertAndSend(SimpleDLQAmqpConfiguration.EXCHANGE_MESSAGES,
                SimpleDLQAmqpConfiguration.QUEUE_MESSAGES, "Some message id:" + messageNumber++);
    }
}
```

最后，一个抛出异常的消费者：

```java

@Configuration
public class MessagesConsumer {
    private static final Logger log = LoggerFactory.getLogger(MessagesConsumer.class);
    private final RabbitTemplate rabbitTemplate;

    public MessagesConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = SimpleDLQAmqpConfiguration.QUEUE_MESSAGES)
    public void receiveMessage(Message message) throws BusinessException {
        log.info("Received message: {}", message.toString());
        throw new BusinessException();
    }
}
```

**默认情况下，所有失败的消息都将在目标队列的头部一次又一次地重新排队**。

让我们通过执行以下Maven命令来运行我们的示例应用程序：

```shell
mvn spring-boot:run -Dstart-class=cn.tuyucheng.taketoday.springamqp.errorhandling.ErrorHandlingApplication
```

现在我们应该可以看到类似的结果输出：

```text
WARN 22260 --- [ntContainer#0-1] s.a.r.l.ConditionalRejectingErrorHandler :
  Execution of Rabbit message listener failed.
Caused by: cn.tuyucheng.taketoday.springamqp.errorhandling.errorhandler.BusinessException: null
```


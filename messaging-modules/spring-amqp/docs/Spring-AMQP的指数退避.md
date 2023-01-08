## 一、简介

默认情况下，在[Spring AMQP](https://www.baeldung.com/spring-amqp)中，失败的消息会重新排队等待另一轮消费。因此，可能会出现无限消费循环，造成不稳定的情况和资源浪费。

**虽然使用[死信队列](https://www.baeldung.com/spring-amqp-error-handling)是处理失败消息的标准方法**，但我们可能希望重试消息消费并将系统返回到正常状态。

在本教程中，**我们将介绍两种不同的方法来实现名为[Exponential Backoff](https://en.wikipedia.org/wiki/Exponential_backoff#Binary_exponential_backoff_algorithm)**的重试策略。

## 2.先决条件

在本教程中，**我们将使用[RabbitMQ](https://www.baeldung.com/rabbitmq)，一种流行的 AMQP 实现**。因此，我们可以参考这篇[Spring AMQP](https://www.baeldung.com/spring-amqp)文章，进一步了解如何配置和使用 RabbitMQ 和 Spring。

为了简单起见，我们还将为我们的 RabbitMQ 实例使用一个 docker 镜像，尽管**任何侦听端口 5672 的 RabbitMQ 实例都可以。**

让我们启动一个 RabbitMQ docker 容器：

```bash
docker run -p 5672:5672 -p 15672:15672 --name rabbit rabbitmq:3-management复制
```

为了实现我们的示例，我们需要添加对 *spring-boot-starter-amqp 的依赖*。最新版本在 [Maven Central](https://search.maven.org/classic/#search|ga|1|(g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-amqp"))上可用：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
        <version>2.2.4.RELEASE</version>
    </dependency>
</dependencies>复制
```

## 3. 阻塞方式

我们的第一种方法将使用[Spring Retry](https://www.baeldung.com/spring-retry) fixtures。我们将创建一个简单的队列和一个配置为在重试失败消息之间等待一段时间的消费者。

首先，让我们创建我们的队列：

```java
@Bean
public Queue blockingQueue() {
    return QueueBuilder.nonDurable("blocking-queue").build();
}复制
```

其次，让我们在*RetryOperationsInterceptor*中配置退避策略，并将其连接到自定义*RabbitListenerContainerFactory*中：

```java
@Bean
public RetryOperationsInterceptor retryInterceptor() {
    return RetryInterceptorBuilder.stateless()
      .backOffOptions(1000, 3.0, 10000)
      .maxAttempts(5)
      .recoverer(observableRecoverer())
      .build();
}

@Bean
public SimpleRabbitListenerContainerFactory retryContainerFactory(
  ConnectionFactory connectionFactory, RetryOperationsInterceptor retryInterceptor) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);

    Advice[] adviceChain = { retryInterceptor };
    factory.setAdviceChain(adviceChain);

    return factory;
}复制
```

如上所示，我们配置了 1000 毫秒的初始间隔和 3.0 的乘数，最长等待时间为 10000 毫秒。此外，在尝试五次后，消息将被丢弃。

让我们添加我们的消费者并通过抛出异常来强制发送失败的消息：

```java
@RabbitListener(queues = "blocking-queue", containerFactory = "retryContainerFactory")
public void consumeBlocking(String payload) throws Exception {
    logger.info("Processing message from blocking-queue: {}", payload);

    throw new Exception("exception occured!");
}复制
```

最后，让我们创建一个测试并将两条消息发送到我们的队列：

```java
@Test
public void whenSendToBlockingQueue_thenAllMessagesProcessed() throws Exception {
    int nb = 2;

    CountDownLatch latch = new CountDownLatch(nb);
    observableRecoverer.setObserver(() -> latch.countDown());

    for (int i = 1; i <= nb; i++) {
        rabbitTemplate.convertAndSend("blocking-queue", "blocking message " + i);
    }

    latch.await();
}复制
```

请记住，*CountdownLatch*仅用作测试夹具。

让我们运行测试并检查我们的日志输出：

```plaintext
2020-02-18 21:17:55.638  INFO : Processing message from blocking-queue: blocking message 1
2020-02-18 21:17:56.641  INFO : Processing message from blocking-queue: blocking message 1
2020-02-18 21:17:59.644  INFO : Processing message from blocking-queue: blocking message 1
2020-02-18 21:18:08.654  INFO : Processing message from blocking-queue: blocking message 1
2020-02-18 21:18:18.657  INFO : Processing message from blocking-queue: blocking message 1
2020-02-18 21:18:18.875  ERROR : java.lang.Exception: exception occured!
2020-02-18 21:18:18.858  INFO : Processing message from blocking-queue: blocking message 2
2020-02-18 21:18:19.860  INFO : Processing message from blocking-queue: blocking message 2
2020-02-18 21:18:22.863  INFO : Processing message from blocking-queue: blocking message 2
2020-02-18 21:18:31.867  INFO : Processing message from blocking-queue: blocking message 2
2020-02-18 21:18:41.871  INFO : Processing message from blocking-queue: blocking message 2
2020-02-18 21:18:41.875 ERROR : java.lang.Exception: exception occured!复制
```

可以看出，此日志正确显示了每次重试之间的指数等待时间。**虽然我们的退避策略有效，但我们的消费者会被阻塞，直到重试次数用尽。**一个微不足道的改进是通过设置*@RabbitListener的**并发* 属性让我们的消费者并发执行：

```java
@RabbitListener(queues = "blocking-queue", containerFactory = "retryContainerFactory", concurrency = "2")复制
```

但是，**重试的****消息仍然会阻塞消费者实例。**因此，应用程序可能会遇到延迟问题。

在下一节中，我们将介绍一种非阻塞方式来实现类似的策略。

## 4. 非阻塞方式

另一种方法涉及多个重试队列和消息过期。事实上，当消息过期时，它会进入死信队列。换句话说，**如果 DLQ 消费者将消息发送回其原始队列，我们实际上是在执行重试循环**。

结果，**使用的重试队列数就是将发生的尝试数**。

首先，让我们为重试队列创建死信队列：

```java
@Bean
public Queue retryWaitEndedQueue() {
    return QueueBuilder.nonDurable("retry-wait-ended-queue").build();
}复制
```

让我们在重试死信队列中添加一个消费者。**该消费者的唯一责任是将消息发送回其原始队列**：

```java
@RabbitListener(queues = "retry-wait-ended-queue", containerFactory = "defaultContainerFactory")
public void consumeRetryWaitEndedMessage(String payload, Message message, Channel channel) throws Exception{
    MessageProperties props = message.getMessageProperties();

    rabbitTemplate().convertAndSend(props.getHeader("x-original-exchange"), 
      props.getHeader("x-original-routing-key"), message);
}复制
```

其次，让我们为重试队列创建一个包装器对象。该对象将保存指数退避配置：

```java
public class RetryQueues {
    private Queue[] queues;
    private long initialInterval;
    private double factor;
    private long maxWait;

    // constructor, getters and setters复制
```

第三，让我们定义三个重试队列：

```java
@Bean
public Queue retryQueue1() {
    return QueueBuilder.nonDurable("retry-queue-1")
      .deadLetterExchange("")
      .deadLetterRoutingKey("retry-wait-ended-queue")
      .build();
}

@Bean
public Queue retryQueue2() {
    return QueueBuilder.nonDurable("retry-queue-2")
      .deadLetterExchange("")
      .deadLetterRoutingKey("retry-wait-ended-queue")
      .build();
}

@Bean
public Queue retryQueue3() {
    return QueueBuilder.nonDurable("retry-queue-3")
      .deadLetterExchange("")
      .deadLetterRoutingKey("retry-wait-ended-queue")
      .build();
}

@Bean
public RetryQueues retryQueues() {
    return new RetryQueues(1000, 3.0, 10000, retryQueue1(), retryQueue2(), retryQueue3());
}复制
```

然后，我们需要一个拦截器来处理消息消费：

```java
public class RetryQueuesInterceptor implements MethodInterceptor {

    // fields and constructor

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return tryConsume(invocation, this::ack, (messageAndChannel, e) -> {
            try {
                int retryCount = tryGetRetryCountOrFail(messageAndChannel, e);
                sendToNextRetryQueue(messageAndChannel, retryCount);
            } catch (Throwable t) {
                // ...
                throw new RuntimeException(t);
            }
        });
    }复制
```

在消费者成功返回的情况下，我们只需确认消息即可。

**但是，如果消费者抛出异常并且还有剩余的尝试，我们会将消息发送到下一个重试队列：**

```java
private void sendToNextRetryQueue(MessageAndChannel mac, int retryCount) throws Exception {
    String retryQueueName = retryQueues.getQueueName(retryCount);

    rabbitTemplate.convertAndSend(retryQueueName, mac.message, m -> {
        MessageProperties props = m.getMessageProperties();
        props.setExpiration(String.valueOf(retryQueues.getTimeToWait(retryCount)));
        props.setHeader("x-retried-count", String.valueOf(retryCount + 1));
        props.setHeader("x-original-exchange", props.getReceivedExchange());
        props.setHeader("x-original-routing-key", props.getReceivedRoutingKey());

        return m;
    });

    mac.channel.basicReject(mac.message.getMessageProperties()
      .getDeliveryTag(), false);
}复制
```

同样，让我们在自定义*RabbitListenerContainerFactory*中连接我们的拦截器：

```java
@Bean
public SimpleRabbitListenerContainerFactory retryQueuesContainerFactory(
  ConnectionFactory connectionFactory, RetryQueuesInterceptor retryInterceptor) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);

    Advice[] adviceChain = { retryInterceptor };
    factory.setAdviceChain(adviceChain);

    return factory;
}复制
```

最后，我们定义了我们的主队列和一个模拟失败消息的消费者：

```java
@Bean
public Queue nonBlockingQueue() {
    return QueueBuilder.nonDurable("non-blocking-queue")
      .build();
}

@RabbitListener(queues = "non-blocking-queue", containerFactory = "retryQueuesContainerFactory", 
  ackMode = "MANUAL")
public void consumeNonBlocking(String payload) throws Exception {
    logger.info("Processing message from non-blocking-queue: {}", payload);

    throw new Exception("Error occured!");
}复制
```

让我们创建另一个测试并发送两条消息：

```java
@Test
public void whenSendToNonBlockingQueue_thenAllMessageProcessed() throws Exception {
    int nb = 2;

    CountDownLatch latch = new CountDownLatch(nb);
    retryQueues.setObserver(() -> latch.countDown());

    for (int i = 1; i <= nb; i++) {
        rabbitTemplate.convertAndSend("non-blocking-queue", "non-blocking message " + i);
    }

    latch.await();
}复制
```

然后，让我们启动我们的测试并检查日志：

```bash
2020-02-19 10:31:40.640  INFO : Processing message from non-blocking-queue: non blocking message 1
2020-02-19 10:31:40.656  INFO : Processing message from non-blocking-queue: non blocking message 2
2020-02-19 10:31:41.620  INFO : Processing message from non-blocking-queue: non blocking message 1
2020-02-19 10:31:41.623  INFO : Processing message from non-blocking-queue: non blocking message 2
2020-02-19 10:31:44.415  INFO : Processing message from non-blocking-queue: non blocking message 1
2020-02-19 10:31:44.420  INFO : Processing message from non-blocking-queue: non blocking message 2
2020-02-19 10:31:52.751  INFO : Processing message from non-blocking-queue: non blocking message 1
2020-02-19 10:31:52.774 ERROR : java.lang.Exception: Error occured!
2020-02-19 10:31:52.829  INFO : Processing message from non-blocking-queue: non blocking message 2
2020-02-19 10:31:52.841 ERROR : java.lang.Exception: Error occured!复制
```

同样，我们看到每次重试之间的等待时间呈指数级增长。**然而，消息并发处理，而不是阻塞直到每次尝试都完成**。

虽然此设置非常灵活并且有助于缓解延迟问题，但存在一个常见的陷阱。实际上，**RabbitMQ 只有在到达队列头部时才会删除过期消息**。因此，如果一条消息的有效期较长，它将阻塞队列中的所有其他消息。**因此，回复队列必须只包含具有相同过期值的消息**。

## 4。总结

如上所示，基于事件的系统可以实施指数退避策略来提高弹性。虽然实施此类解决方案可能微不足道，但重要的是要认识到某种解决方案可以很好地适应小型系统，但会在高吞吐量生态系统中引起延迟问题。
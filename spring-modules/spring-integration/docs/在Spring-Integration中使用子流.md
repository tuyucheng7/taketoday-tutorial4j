## 1. 概述

Spring Integration使得使用一些[企业集成模式](https://www.baeldung.com/spring-integration)变得容易。其中一种方式是通过其[DSL](https://www.baeldung.com/spring-integration-java-dsl)。

在本教程中，我们将了解DSL对子流的支持，以简化我们的一些配置。

## 2. 我们的任务

假设我们有一个整数序列，我们想将其分成三个不同的存储桶。

如果我们想使用Spring Integration来做到这一点，我们可以从创建三个输出通道开始：

-   像0、3、6和9这样的数字将转到multipleOfThreeChannel
-   像1、4、7和10这样的数字将转到remainderIsOneChannel
-   像2、5、8和11这样的数字会转到remainderIsTwoChannel

要了解子流有多大用处，让我们从不使用子流的情况开始。

然后，我们将使用子流来简化我们的配置：

-   publishSubscribeChannel
-   routeToRecipients
-   Filters，配置我们的if-then逻辑
-   Routers，配置我们的switch逻辑

## 3. 先决条件

在配置我们的子流之前，让我们创建这些输出通道。

我们将创建这些QueueChannels，因为它更容易演示：

```java
@EnableIntegration
@IntegrationComponentScan
public class SubflowsConfiguration {

    @Bean
    QueueChannel multipleOfThreeChannel() {
        return new QueueChannel();
    }

    @Bean
    QueueChannel remainderIsOneChannel() {
        return new QueueChannel();
    }

    @Bean
    QueueChannel remainderIsTwoChannel() {
        return new QueueChannel();
    }

    boolean isMultipleOfThree(Integer number) {
        return number % 3 == 0;
    }

    boolean isRemainderIOne(Integer number) {
        return number % 3 == 1;
    }

    boolean isRemainderTwo(Integer number) {
        return number % 3 == 2;
    }
}
```

最终，这些是我们分组的数字将结束的地方。

另请注意，Spring Integration很容易开始看起来很复杂，因此为了可读性，我们将添加一些辅助方法。

## 4. 无子流求解

现在我们需要定义我们的流程。

如果没有子流，简单的想法是定义三个独立的集成流，每种类型的数字一个。

**我们将向每个IntegrationFlow组件发送相同的消息序列，但每个组件的输出消息将不同**。

### 4.1 定义IntegrationFlow组件

首先，让我们在SubflowConfiguration类中定义每个IntegrationFlow bean：

```java
@Bean
public IntegrationFlow multipleOfThreeFlow() {
    return flow -> flow.split()
        .<Integer> filter(this::isMultipleOfThree)
        .channel("multipleOfThreeChannel");
}
```

我们的流程包含两个端点-一个Splitter后跟一个Filter。

过滤器就像它听起来的那样。但是为什么我们还需要分离器呢？我们马上就会看到这一点，但基本上，它将输入Collection拆分为单独的消息。

而且，我们当然可以用相同的方式再定义两个IntegrationFlow bean。

### 4.2 消息网关

对于每个流，我们还需要一个消息网关。

简而言之，这些从调用者那里抽象出Spring Integration Messages API，类似于REST服务如何抽象出HTTP：

```java
@MessagingGateway
public interface NumbersClassifier {

    @Gateway(requestChannel = "multipleOfThreeFlow.input")
    void multipleOfThree(Collection<Integer> numbers);

    @Gateway(requestChannel = "remainderIsOneFlow.input")
    void remainderIsOne(Collection<Integer> numbers);

    @Gateway(requestChannel = "remainderIsTwoFlow.input")
    void remainderIsTwo(Collection<Integer> numbers);
}
```

对于每个，我们需要使用@Gateway注解并指定输入通道的隐式名称，这只是bean的名称后跟“.input”。**请注意，我们可以使用此约定，因为我们使用的是基于lambda的流程**。

**这些方法是我们流程的入口点**。

### 4.3 发送消息和检查输出

现在，让我们测试一下：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SeparateFlowsConfiguration.class })
public class SeparateFlowsUnitTest {

    @Autowired
    private QueueChannel multipleOfThreeChannel;

    @Autowired
    private NumbersClassifier numbersClassifier;

    @Test
    public void whenSendMessagesToMultipleOf3Flow_thenOutputMultiplesOf3() {
        numbersClassifier.multipleOfThree(Arrays.asList(1, 2, 3, 4, 5, 6));
        Message<?> outMessage = multipleOfThreeChannel.receive(0);
        assertEquals(outMessage.getPayload(), 3);
        outMessage = multipleOfThreeChannel.receive(0);
        assertEquals(outMessage.getPayload(), 6);
        outMessage = multipleOfThreeChannel.receive(0);
        assertNull(outMessage);
    }
}
```

请注意，我们已将消息作为List发送，这就是我们需要拆分器的原因，以获取单个“列表消息”并将其转换为多个“数字消息”。

我们使用o调用receive以获取下一条可用消息而无需等待。由于我们的列表中有两个三的倍数，我们希望能够调用它两次。第三次调用receive返回null。

当然，receive返回一个Message，所以我们调用getPayload来提取数字。

同样，我们可以对其他两个做同样的事情。

**所以，这就是没有子流程的解决方案**。我们要维护三个独立的流程和三个独立的网关方法。

我们现在要做的是用一个bean替换三个IntegrationFlow bean，用一个bean替换三个网关方法。

## 5. 使用publishSubscribeChannel

publishSubscribeChannel()方法将消息广播到所有订阅子流。这样，我们可以创建一个流，而不是三个。

```java
@Bean
public IntegrationFlow classify() {
    return flow -> flow.split()
        .publishSubscribeChannel(subscription -> 
            subscription
                .subscribe(subflow -> subflow
                	.<Integer> filter(this::isMultipleOfThree)
                    .channel("multipleOfThreeChannel"))
                .subscribe(subflow -> subflow
                    .<Integer> filter(this::isRemainderOne)
                    .channel("remainderIsOneChannel"))
                .subscribe(subflow -> subflow
                    .<Integer> filter(this::isRemainderTwo)
                    .channel("remainderIsTwoChannel")));
}
```

通过这种方式，**子流是匿名的，这意味着它们不能被独立寻址**。

现在，我们只有一个流程，所以让我们也编辑我们的NumbersClassifier：

```java
@Gateway(requestChannel = "classify.input")
void classify(Collection<Integer> numbers);
```

现在，**由于我们只有一个IntegrationFlow bean和一个网关方法，所以我们只需要发送一次我们的列表**：

```java
@Test
public void whenSendMessagesToFlow_thenNumbersAreClassified() {
    numbersClassifier.classify(Arrays.asList(1, 2, 3, 4, 5, 6));

    // same assertions as before
}
```

请注意，从现在开始，只有集成流定义会发生变化，因此我们不会再次显示测试。

## 6. 使用routeToRecipients

**实现相同目的的另一种方法是routeToRecipients，这很好，因为它内置了过滤功能**。

使用这种方法，**我们可以指定广播的通道和子流**。 

### 6.1 recipient

在下面的代码中，我们将根据条件指定multipleof3Channel、remainderIs1Channel和remainderIsTwoChannel作为接收者：

```java
@Bean
public IntegrationFlow classify() {
    return flow -> flow.split()
        .routeToRecipients(route -> route
            .<Integer> recipient("multipleOfThreeChannel", this::isMultipleOfThree)       
            .<Integer> recipient("remainderIsOneChannel", this::isRemainderOne)
            .<Integer> recipient("remainderIsTwoChannel", this::isRemainderTwo));
}
```

我们也可以无条件调用recipient，routeToRecipients将无条件地发布到该目的地。

### 6.2 recipientFlow

请注意routeToRecipients允许我们定义一个完整的流程，就像publishSubscribeChannel一样。 

让我们修改上面的代码并**指定一个匿名子流作为第一个接收者**：

```java
.routeToRecipients(route -> route
    .recipientFlow(subflow -> subflow
        .<Integer> filter(this::isMultipleOfThree)
        .channel("mutipleOfThreeChannel"))
    ...);
```

**此子流将接收整个消息序列**，因此我们需要像以前一样进行过滤以获得相同的行为。

**同样，一个IntegrationFlow bean对我们来说就足够了**。

现在让我们继续讨论if-else组件。其中之一是Filter。

## 7. 使用if-then流

我们已经在前面的所有示例中使用了Filter。好消息是我们不仅可以指定进一步处理的条件，**还可以指定丢弃消息的通道或流**。

**我们可以将丢弃流和通道视为else块**：

```java
@Bean
public IntegrationFlow classify() {
    return flow -> flow.split()
        .<Integer> filter(this::isMultipleOfThree, 
            notMultiple -> notMultiple
                .discardFlow(oneflow -> oneflow
                    .<Integer> filter(this::isRemainderOne,
                        twoflow -> twoflow
                    .discardChannel("remainderIsTwoChannel"))
                .channel("remainderIsOneChannel"))
            .channel("multipleofThreeChannel");
}
```

在这种情况下，我们已经实现了if-else路由逻辑：

-   如果数字不是3的倍数，则将这些消息丢弃到丢弃流；**我们在这里使用流，因为需要更多的逻辑来知道它的目标通道**。
-   在丢弃流程中，如果数字不是余数1，则将这些消息丢弃到丢弃通道。

## 8. switch-ing计算值

最后，让我们试试route方法，它为我们提供了比routeToRecipients更多的控制。这很好，因为Router可以将流拆分成任意数量的部分，而Filter只能执行两部分。

### 8.1 channelMapping

让我们定义我们的IntegrationFlow bean：

```java
@Bean
public IntegrationFlow classify() {
    return classify -> classify.split()
        .<Integer, Integer> route(number -> number % 3, mapping -> mapping
            .channelMapping(0, "multipleOfThreeChannel")
            .channelMapping(1, "remainderIsOneChannel")
            .channelMapping(2, "remainderIsTwoChannel"));
}
```

在上面的代码中，我们通过执行除法来计算路由键：

```java
route(p -> p % 3, ...
```

基于此键，我们路由消息：

```java
channelMapping(0, "multipleof3Channel")
```

### 8.2 subFlowMapping

我们可以通过指定子流来进行更多控制，将channelMapping替换为subFlowMapping：

```java
.subFlowMapping(1, subflow -> subflow.channel("remainderIsOneChannel"))
```

或者通过调用handle方法而不是channel方法来进行更多控制：

```java
.subFlowMapping(2, subflow -> subflow
    .<Integer> handle((payload, headers) -> {
        // do extra work on the payload
        return payload;
    }))).channel("remainderIsTwoChannel");
```

在这种情况下，子流将在route()方法之后返回到主流，因此我们需要指定通道remainderIsTwoChannel。

## 9. 总结

在本教程中，我们探讨了如何使用子流以某些方式过滤和路由消息。
## 1. 概述

在本教程中，我们将学习如何使用ArgumentMatcher，并讨论它与ArgumentCaptor的区别。

## 2. Maven依赖

```xml

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.0.0</version>
    <scope>test</scope>
</dependency>
```

## 3. ArgumentMatchers

我们可以通过各种方式配置mock方法。一种选择是返回一个固定值：

```text
doReturn("Flower").when(flowerService).analyze("poppy");
```

在上面的示例中，仅当analyze方法接收的参数字符串为“poppy”时，才会返回字符串“Flower”。

但在某些情况下，**我们可能需要对更广泛的值或未知值作出响应**。

在这些场景中，**我们可以使用参数匹配器配置我们的mock方法**：

```text
when(flowerService.analyze(anyString())).thenReturn("Flower");
```

此时，通过使用anyString参数匹配器，无论我们给analyze方法传递什么值，结果都是相同的。ArgumentMatchers允许我们灵活的验证或stubbing。

**如果一个方法有多个参数，我们不能只对一些参数使用ArgumentMatchers。 Mockito要求我们通过匹配器或精确值提供所有参数**。

下面是一个错误使用参数匹配器的例子：

```text
assertThrows(InvalidUseOfMatchersException.class, () -> when(flowerService.isABigFlower("poppy", anyInt())).thenReturn(true));
```

为了解决这个问题，我们可以使用eq匹配器：

```text
when(flowerService.isABigFlower(eq("poppy"), anyInt())).thenReturn(true);
```

当我们使用匹配器时，还有两点需要注意：

+ **我们不能将它们用作返回值**；stubbing调用时我们需要一个精确的值。
+ **我们不能在verify或stubbing之外使用参数匹配器**。

根据第二点，Mockito将检测到位置的参数并抛出InvalidUseOfMatchersException。

一个错误使用的例子是：

```text
String orMatcher = or(eq("poppy"), endsWith("y"));
assertThrows(InvalidUseOfMatchersException.class, () -> verify(flowerService).analyze(orMatcher));
```

我们可以通过下面的方式实现上述代码：

```text
verify(flowerService).analyze(or(eq("poppy"), endsWith("y")));
```

Mockito还提供AdditionalMatchers来在ArgumentMatchers上实现常见的逻辑运算('not'、'and'、'or')，以匹配原始类型和非原始类型。

## 4. 自定义ArgumentMatcher

**创建我们自己的匹配器允许我们为给定场景选择最佳的方法，从而实现干净且可维护的高质量测试**。

例如，我们可以有一个传递消息的MessageController。它接收一个MessageDTO，并从中创建一个MessageService将传递的Message。

这里的验证很简单；我们将验证我们是否使用任何Message对象准确地调用了MessageService一次：

```java

@ExtendWith(MockitoExtension.class)
class MessageControllerUnitTest {

    @InjectMocks
    private MessageController messageController;

    @Mock
    private MessageService messageService;

    @Test
    void givenMsg_whenVerifyUsingAnyMatcher_thenOk() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setFrom("me");
        messageDTO.setTo("you");
        messageDTO.setText("Hello, you!");

        messageController.createMessage(messageDTO);

        verify(messageService, times(1)).deliverMessage(any(Message.class));
    }
}
```

由于**Message是在被测方法内部构造的**，因此我们必须使用any作为匹配器。

这种方法不允许我们验证Message中的数据，这可能与MessageDTO中的数据不同。

出于这个原因，我们将实现一个自定义参数匹配器：

```java
public class MessageMatcher implements ArgumentMatcher<Message> {

    private final Message left;

    public MessageMatcher(Message message) {
        this.left = message;
    }

    @Override
    public boolean matches(Message right) {
        return left.getFrom().equals(right.getFrom()) &&
                left.getTo().equals(right.getTo()) &&
                left.getText().equals(right.getText()) &&
                right.getDate() != null &&
                right.getId() != null;
    }
}
```

**要使用我们的匹配器，我们需要修改我们的测试并将any替换为argThat**：

```java
class MessageControllerUnitTest {

    @Test
    void givenMsg_whenVerifyUsingMessageMatcher_thenOk() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setFrom("me");
        messageDTO.setTo("you");
        messageDTO.setText("Hello, you!");

        messageController.createMessage(messageDTO);

        Message message = new Message();
        message.setFrom("me");
        message.setTo("you");
        message.setText("Hello, you!");

        verify(messageService, times(1)).deliverMessage(argThat(new MessageMatcher(message)));
    }
}
```

现在我们知道Message实例将具有与MessageDTO相同的数据。

## 5. 自定义ArgumentMatcher与ArgumentCaptor

两种技术，自定义ArgumentMatcher和ArgumentCaptor都可用于确保将某些参数传递给mock。

但是，**如果我们需要ArgumentCaptor对参数值进行断言以完成验证，
或者我们的自定义参数匹配器不太可能被重用，则ArgumentCaptor可能更合适**。

通过ArgumentMatcher自定义参数匹配器通常更适合stubbing。

## 6. 总结

在本文中，我们探讨了Mockito中的一个特性ArgumentMatcher。我们还讨论了它与ArgumentCaptor的不同之处。
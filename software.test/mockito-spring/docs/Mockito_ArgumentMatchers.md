## 1. 概述

在本教程中，我们将学习**如何使用ArgumentMatcher，并介绍它与ArgumentCaptor的区别**。

## 2. ArgumentMatchers

我们可以通过各种方式配置mock方法。一种选择是返回一个固定值：

```
doReturn("Flower").when(flowerService).analyze("poppy");
```

在上面的示例中，仅当analyze()方法接收的参数为“poppy”时，才会返回字符串“Flower”。

但在某些情况下，**我们可能需要对更大范围的值或未知值做出响应。**

在这些场景中，**我们可以使用ArgumentMatchers配置我们的mock方法**：

```
when(flowerService.analyze(anyString())).thenReturn("Flower");
```

现在，由于我们anyString()参数匹配器，无论我们传递给analyze()方法的参数是什么，结果都是相同的。ArgumentMatchers允许我们灵活的verify或stub。

**如果一个方法有多个参数，我们不能只对一些参数使用ArgumentMatchers**。
Mockito要求我们通过匹配器或精确值提供所有参数，**即要么给定所有精确值，要么全部使用参数匹配器**。

如下，我们演示一个错误的示例：

```java
abstract class FlowerService {
    public abstract boolean isABigFlower(String name, int petals);
}
```

```
FlowerService mock = mock(FlowerService.class);
when(mock.isABigFlower("poppy", anyInt())).thenReturn(true);
```

为了解决这个问题，并根据需要保持字符串名称“poppy”，我们可以使用eq匹配器：

```
when(mock.isABigFlower(eq("poppy"), anyInt())).thenReturn(true);
```

当我们使用匹配器时，还有两点需要注意：

+ **我们不能将它们用作返回值**；在stub调用时，我们需要精确的值。
+ **我们不能在verify或stub之外使用参数匹配器**。

根据第二点，Mockito将检测错位的参数并抛出InvalidUseOfMatcherException。

一个错误的使用例子是：

```
String orMatcher = or(eq("poppy"), endsWith("y"));
verify(mock).analyze(orMatcher);
```

正确的使用方式为：

```
verify(mock).analyze(or(eq("poppy"), endsWith("y")));
```

Mockito还提供了AdditionalMatchers，用于在ArgumentMatchers上实现常见的逻辑操作('not'、'and'、'or')，以匹配原始类型和非原始类型：

```
verify(mock).analyze(or(eq("poppy"), endsWith("y")));
```

## 4. 自定义参数匹配器

**创建我们自己的匹配器允许我们为给定场景选择最佳方法，并生成干净且可维护的高质量测试**。

例如，我们可以有一个传递消息的MessageController。它接收一个MessageApi参数，并从中创建一个Message传递给MessageService：

```java

public class MessageApi {
    private String from;
    private String to;
    private String text;
    // getters and setters ...
}

public class Message {

    private String from;
    private String to;
    private String text;
    private Date date;
    private UUID id;
    // getters and setters ...
}

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public Message createMessage(@RequestBody MessageApi messageDTO) {
        Message message = new Message();
        message.setText(messageDTO.getText());
        message.setFrom(messageDTO.getFrom());
        message.setTo(messageDTO.getTo());
        message.setDate(Date.from(Instant.now()));
        message.setId(UUID.randomUUID());

        return messageService.deliverMessage(message);
    }
}

@Service
public class MessageService {

    public Message deliverMessage(Message message) {
        return message;
    }
}
```

我们将验证我们是否使用任何Message对象准确地调用了MessageService 1次：

```java

@ExtendWith(MockitoExtension.class)
class MessageControllerUnitTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    @Test
    void createMessage_NewMessage_OK() {
        MessageApi messageApi = new MessageApi();
        messageApi.setFrom("me");
        messageApi.setTo("you");
        messageApi.setText("Hello, you!");

        messageController.createMessage(messageApi);

        Message message = new Message();
        message.setFrom("me");
        message.setTo("you");
        message.setText("Hello, you!");

        verify(messageService, times(1)).deliverMessage(any(Message.class));
    }
}
```

**由于Message是在被测方法内部构造的，因此我们必须使用any()作为匹配器**。

这种方法不允许我们验证Message内的数据，这可能与MessageApi内的数据不同。

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

**要使用我们的匹配器，我们需要修改我们的测试并将any()替换为argThat()**：

```java
class MessageControllerUnitTest {

    @Test
    void createMessage_NewMessage_OK() {
        MessageApi messageApi = new MessageApi();
        messageApi.setFrom("me");
        messageApi.setTo("you");
        messageApi.setText("Hello, you!");

        messageController.createMessage(messageApi);

        Message message = new Message();
        message.setFrom("me");
        message.setTo("you");
        message.setText("Hello, you!");

        Mockito.verify(messageService, times(1)).deliverMessage(ArgumentMatchers.argThat(new MessageMatcher(message)));
    }
}
```

现在我们验证Message实例与MessageApi是否包含相同的数据。

## 5. 自定义ArgumentMatchers与ArgumentCaptor

自定义参数匹配器和ArgumentCaptor都可用于确保将某些参数传递给mock。

但是，如果我们需要ArgumentCaptor对参数值进行断言以完成验证，或者我们的自定义参数匹配器不太可能被重用，则ArgumentCaptor可能更合适。

通过ArgumentMatcher自定义参数匹配器通常更适合stub。

## 6. 总结

在本文中，我们介绍了Mockito的一个特性ArgumentMatcher。我们还讨论了它与ArgumentCaptor的不同之处。
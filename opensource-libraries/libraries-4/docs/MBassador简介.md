## 1. 概述

简单地说，[MBassador](https://github.com/bennidi/mbassador)是一种利用[发布-订阅](https://en.wikipedia.org/wiki/Publish–subscribe_pattern)语义的高性能事件总线。

消息被广播给一个或多个对等点，而事先不知道有多少订阅者，或者他们如何使用消息。

## 2.Maven依赖

在我们可以使用该库之前，我们需要添加[mbassador](https://search.maven.org/classic/#search|ga|1|a%3A"mbassador")依赖项：

```xml
<dependency>
    <groupId>net.engio</groupId>
    <artifactId>mbassador</artifactId>
    <version>1.3.1</version>
</dependency>
```

## 3. 基本事件处理

### 3.1. 简单例子

我们将从一个发布消息的简单示例开始：

```java
private MBassador<Object> dispatcher = new MBassador<>();
private String messageString;

@Before
public void prepareTests() {
    dispatcher.subscribe(this);
}

@Test
public void whenStringDispatched_thenHandleString() {
    dispatcher.post("TestString").now();
 
    assertNotNull(messageString);
    assertEquals("TestString", messageString);
}

@Handler
public void handleString(String message) {
    messageString = message;
}

```

在这个测试类的顶部，我们看到了一个带有默认构造函数的MBassador的创建。接下来，在@Before方法中，我们调用subscribe()并传入对类本身的引用。

在subscribe() 中，调度程序检查订阅者的@Handler注解。

并且，在第一个测试中，我们调用dispatcher.post(…).now()来发送消息——这导致handleString()被调用。

这个初始测试演示了几个重要的概念。任何对象都可以是订阅者，只要它有一个或多个用@Handler注解的方法。订阅者可以有任意数量的处理程序。

为了简单起见，我们使用订阅自身的测试对象，但在大多数生产场景中，消息调度程序将在与消费者不同的类中。

处理程序方法只有一个输入参数——消息，并且不能抛出任何已检查的异常。

与subscribe()方法类似，post 方法接受任何Object。该对象被交付给订阅者。

当一条消息被发布时，它会被传递给任何订阅了该消息类型的听众。

让我们添加另一个消息处理程序并发送不同的消息类型：

```java
private Integer messageInteger; 

@Test
public void whenIntegerDispatched_thenHandleInteger() {
    dispatcher.post(42).now();
 
    assertNull(messageString);
    assertNotNull(messageInteger);
    assertTrue(42 == messageInteger);
}

@Handler
public void handleInteger(Integer message) {
    messageInteger = message;
}

```

正如预期的那样，当我们分派 一个Integer时，会调用 handleInteger( )而handleString()不会。单个调度程序可用于发送多种消息类型。

### 3.2. 死消息

那么当没有处理程序时，消息会去哪里呢？让我们添加一个新的事件处理程序，然后发送第三种消息类型：

```java
private Object deadEvent; 

@Test
public void whenLongDispatched_thenDeadEvent() {
    dispatcher.post(42L).now();
 
    assertNull(messageString);
    assertNull(messageInteger);
    assertNotNull(deadEvent);
    assertTrue(deadEvent instanceof Long);
    assertTrue(42L == (Long) deadEvent);
} 

@Handler
public void handleDeadEvent(DeadMessage message) {
    deadEvent = message.getMessage();
}

```

在此测试中，我们发送一个Long而不是Integer。handleInteger ()和handleString()都没有被调用，但是handleDeadEvent()被调用了。

当消息没有处理程序时，它会被包装在DeadMessage对象中。因为我们为Deadmessage添加了一个处理程序，所以我们捕获了它。

可以安全地忽略DeadMessage ；如果应用程序不需要跟踪死消息，则可以允许它们无处可去。

## 4.使用事件层次结构

发送String和Integer事件是有限制的。让我们创建一些消息类：

```java
public class Message {}

public class AckMessage extends Message {}

public class RejectMessage extends Message {
    int code;

    // setters and getters
}
```

我们有一个简单的基类和两个扩展它的类。

### 4.1. 发送基类消息

我们将从消息事件开始：

```java
private MBassador<Message> dispatcher = new MBassador<>();

private Message message;
private AckMessage ackMessage;
private RejectMessage rejectMessage;

@Before
public void prepareTests() {
    dispatcher.subscribe(this);
}

@Test
public void whenMessageDispatched_thenMessageHandled() {
    dispatcher.post(new Message()).now();
    assertNotNull(message);
    assertNull(ackMessage);
    assertNull(rejectMessage);
}

@Handler
public void handleMessage(Message message) {
    this.message = message;
}

@Handler
public void handleRejectMessage(RejectMessage message) {
   rejectMessage = message;
}

@Handler
public void handleAckMessage(AckMessage message) {
    ackMessage = message;
}
```

探索 MBassador——一种高性能的发布-订阅事件总线。这限制了我们使用Messages但增加了一个额外的类型安全层。

当我们发送Message时，handleMessage()会收到它。其他两个处理程序没有。

### 4.2. 发送子类消息

让我们发送一个RejectMessage：

```java
@Test
public void whenRejectDispatched_thenMessageAndRejectHandled() {
    dispatcher.post(new RejectMessage()).now();
 
    assertNotNull(message);
    assertNotNull(rejectMessage);
    assertNull(ackMessage);
}
```

当我们发送RejectMessage时，handleRejectMessage()和handleMessage()都会收到它。

由于RejectMessage扩展了Message，因此除了R ejectMessage处理程序之外， Message处理程序也接收了它。

让我们使用AckMessage验证此行为：

```java
@Test
public void whenAckDispatched_thenMessageAndAckHandled() {
    dispatcher.post(new AckMessage()).now();
 
    assertNotNull(message);
    assertNotNull(ackMessage);
    assertNull(rejectMessage);
}
```

正如我们预期的那样，当我们发送AckMessage时，handleAckMessage()和handleMessage()都会收到它。

## 5.过滤消息

按类型组织消息已经是一个强大的功能，但我们可以进一步过滤它们。

### 5.1. 按类和子类过滤

当我们发布RejectMessage或AckMessage时，我们在特定类型的事件处理程序和基类中都收到了该事件。

我们可以通过使Message抽象并创建一个类(例如GenericMessage )来解决此类型层次结构问题。但是，如果我们没有这种奢侈呢？

我们可以使用消息过滤器：

```java
private Message baseMessage;
private Message subMessage;

@Test
public void whenMessageDispatched_thenMessageFiltered() {
    dispatcher.post(new Message()).now();
 
    assertNotNull(baseMessage);
    assertNull(subMessage);
}

@Test
public void whenRejectDispatched_thenRejectFiltered() {
    dispatcher.post(new RejectMessage()).now();
 
    assertNotNull(subMessage);
    assertNull(baseMessage);
}

@Handler(filters = { @Filter(Filters.RejectSubtypes.class) })
public void handleBaseMessage(Message message) {
    this.baseMessage = message;
}

@Handler(filters = { @Filter(Filters.SubtypesOnly.class) })
public void handleSubMessage(Message message) {
    this.subMessage = message;
}
```

@Handler注解的filters参数接受一个实现IMessageFilter的类。该库提供了两个示例：

Filters.RejectSubtypes顾名思义：它将过滤掉任何子类型。在这种情况下，我们看到RejectMessage没有被handleBaseMessage() 处理。

Filters.SubtypesOnly也正如其名称所暗示的那样：它将过滤掉任何基本类型。在这种情况下，我们看到Message不是由handleSubMessage() 处理的。

### 5.2. 消息过滤器

Filters.RejectSubtypes和Filters.SubtypesOnly都实现了IMessageFilter。

RejectSubTypes将消息的类与其定义的消息类型进行比较，并且只允许通过等于其类型之一的消息，而不是任何子类。

### 5.3. 按条件过滤

幸运的是，有一种更简单的方法来过滤消息。MBassador 支持[Java EL 表达式](https://en.wikipedia.org/wiki/Unified_Expression_Language)的子集作为过滤消息的条件。

让我们根据长度过滤String消息：

```java
private String testString;

@Test
public void whenLongStringDispatched_thenStringFiltered() {
    dispatcher.post("foobar!").now();
 
    assertNull(testString);
}

@Handler(condition = "msg.length() < 7")
public void handleStringMessage(String message) {
    this.testString = message;
}
```

“foobar！” 消息长度为七个字符并被过滤。让我们发送一个更短的字符串：

```java
@Test
public void whenShortStringDispatched_thenStringHandled() {
    dispatcher.post("foobar").now();
 
    assertNotNull(testString);
}
```

现在，“foobar”只有六个字符长并且被传递。

我们的RejectMessage包含一个带有访问器的字段。让我们为此编写一个过滤器：

```java
private RejectMessage rejectMessage;

@Test
public void whenWrongRejectDispatched_thenRejectFiltered() {

    RejectMessage testReject = new RejectMessage();
    testReject.setCode(-1);

    dispatcher.post(testReject).now();
 
    assertNull(rejectMessage);
    assertNotNull(subMessage);
    assertEquals(-1, ((RejectMessage) subMessage).getCode());
}

@Handler(condition = "msg.getCode() != -1")
public void handleRejectMessage(RejectMessage rejectMessage) {
    this.rejectMessage = rejectMessage;
}
```

同样，我们可以查询对象的方法并过滤或不过滤消息。

### 5.4. 捕获过滤的消息

与DeadEvents 类似，我们可能希望捕获和处理过滤后的消息。还有一个专门的机制来捕获过滤后的事件。过滤后的事件与“死”事件的处理方式不同。

让我们编写一个测试来说明这一点：

```java
private String testString;
private FilteredMessage filteredMessage;
private DeadMessage deadMessage;

@Test
public void whenLongStringDispatched_thenStringFiltered() {
    dispatcher.post("foobar!").now();
 
    assertNull(testString);
    assertNotNull(filteredMessage);
    assertTrue(filteredMessage.getMessage() instanceof String);
    assertNull(deadMessage);
}

@Handler(condition = "msg.length() < 7")
public void handleStringMessage(String message) {
    this.testString = message;
}

@Handler
public void handleFilterMessage(FilteredMessage message) {
    this.filteredMessage = message;
}

@Handler
public void handleDeadMessage(DeadMessage deadMessage) {
    this.deadMessage = deadMessage;
}

```

通过添加FilteredMessage处理程序，我们可以跟踪因长度而被过滤的字符串。filterMessage包含我们太长的字符串，而deadMessage保持为空。

## 6. 异步消息分发和处理

到目前为止，我们所有的示例都使用了同步消息分发；当我们调用post.now()时，消息被传送到我们从中调用post()的同一个线程中的每个处理程序。

### 6.1. 异步调度

MBassador.post ()返回一个[SyncAsyncPostCommand](https://bennidi.github.io/mbassador/net/engio/mbassy/bus/publication/SyncAsyncPostCommand.html)。这个类提供了几种方法，包括：

-   now() – 同步发送消息；调用将阻塞，直到所有消息都已传递
-   asynchronously() – 异步执行消息发布

让我们在示例类中使用异步调度。我们将在这些测试中使用[Awaitility](https://www.baeldung.com/awaitlity-testing)来简化代码：

```java
private MBassador<Message> dispatcher = new MBassador<>();
private String testString;
private AtomicBoolean ready = new AtomicBoolean(false);

@Test
public void whenAsyncDispatched_thenMessageReceived() {
    dispatcher.post("foobar").asynchronously();
 
    await().untilAtomic(ready, equalTo(true));
    assertNotNull(testString);
}

@Handler
public void handleStringMessage(String message) {
    this.testString = message;
    ready.set(true);
}
```

我们在这个测试中调用了asynchronously()，并使用一个AtomicBoolean作为标志和await()来等待传递线程传递消息。

如果我们注解掉对await()的调用，我们就有测试失败的风险，因为我们在交付线程完成之前检查了testString 。

### 6.2. 异步处理程序调用

异步调度允许消息提供者在消息传递给每个处理程序之前返回到消息处理，但它仍然按顺序调用每个处理程序，并且每个处理程序都必须等待前一个处理程序完成。

如果一个处理程序执行昂贵的操作，这可能会导致问题。

MBassador 提供了一种异步处理程序调用机制。为此配置的处理程序在其线程中接收消息：

```java
private Integer testInteger;
private String invocationThreadName;
private AtomicBoolean ready = new AtomicBoolean(false);

@Test
public void whenHandlerAsync_thenHandled() {
    dispatcher.post(42).now();
 
    await().untilAtomic(ready, equalTo(true));
    assertNotNull(testInteger);
    assertFalse(Thread.currentThread().getName().equals(invocationThreadName));
}

@Handler(delivery = Invoke.Asynchronously)
public void handleIntegerMessage(Integer message) {
 
    this.invocationThreadName = Thread.currentThread().getName();
    this.testInteger = message;
    ready.set(true);
}
```

处理程序可以使用处理程序注解上的delivery = Invoke.Asynchronously属性请求异步调用。我们在测试中通过比较调度方法和处理程序中的线程名称来验证这一点。

## 7.自定义MBassador

到目前为止，我们一直在使用具有默认配置的 MBassador 实例。调度器的行为可以用注解修改，类似于我们目前所见；我们将介绍更多内容以完成本教程。

### 7.1. 异常处理

处理程序不能定义已检查的异常。相反，可以为调度程序提供IPublicationErrorHandler作为其构造函数的参数：

```java
public class MBassadorConfigurationTest
  implements IPublicationErrorHandler {

    private MBassador dispatcher;
    private String messageString;
    private Throwable errorCause;

    @Before
    public void prepareTests() {
        dispatcher = new MBassador<String>(this);
        dispatcher.subscribe(this);
    }

    @Test
    public void whenErrorOccurs_thenErrorHandler() {
        dispatcher.post("Error").now();
 
        assertNull(messageString);
        assertNotNull(errorCause);
    }

    @Test
    public void whenNoErrorOccurs_thenStringHandler() {
        dispatcher.post("Error").now();
 
        assertNull(errorCause);
        assertNotNull(messageString);
    }

    @Handler
    public void handleString(String message) {
        if ("Error".equals(message)) {
            throw new Error("BOOM");
        }
        messageString = message;
    }

    @Override
    public void handleError(PublicationError error) {
        errorCause = error.getCause().getCause();
    }
}
```

当handleString()抛出错误时，它被保存到errorCause。

### 7.2. 处理程序优先级

处理程序的调用顺序与它们的添加顺序相反，但这不是我们想要依赖的行为。即使能够在它们的线程中调用处理程序，我们可能仍然需要知道它们将以什么顺序被调用。

我们可以显式设置处理程序优先级：

```java
private LinkedList<Integer> list = new LinkedList<>();

@Test
public void whenRejectDispatched_thenPriorityHandled() {
    dispatcher.post(new RejectMessage()).now();

    // Items should pop() off in reverse priority order
    assertTrue(1 == list.pop());
    assertTrue(3 == list.pop());
    assertTrue(5 == list.pop());
}

@Handler(priority = 5)
public void handleRejectMessage5(RejectMessage rejectMessage) {
    list.push(5);
}

@Handler(priority = 3)
public void handleRejectMessage3(RejectMessage rejectMessage) {
    list.push(3);
}

@Handler(priority = 2, rejectSubtypes = true)
public void handleMessage(Message rejectMessage) 
    logger.error("Reject handler #3");
    list.push(3);
}

@Handler(priority = 0)
public void handleRejectMessage0(RejectMessage rejectMessage) {
    list.push(1);
}

```

从最高优先级到最低优先级调用处理程序。最后调用具有默认优先级(为零)的处理程序。我们看到处理程序以相反的顺序编号pop()关闭。

### 7.3. 拒绝子类型，简单的方法

上面的测试中handleMessage()发生了什么？我们不必使用RejectSubTypes.class来过滤我们的子类型。

RejectSubTypes是一个布尔标志，提供与类相同的过滤，但性能优于IMessageFilter实现。

不过，我们仍然需要使用基于过滤器的实现来仅接受子类型。

## 八. 总结

MBassador 是一个简单直接的库，用于在对象之间传递消息。消息可以以多种方式组织，可以同步或异步发送。
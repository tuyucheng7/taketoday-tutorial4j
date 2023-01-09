## 1. 概述

在这个简短的教程中，我们将重点介绍如何使用Mockito测试回调。

我们介绍两种解决方案，首先使用ArgumentCaptor，然后使用更直观的doAnswer()方法。

## 2. 回调简介

**回调是作为参数传递给方法的一段代码，该方法应在给定时间回调(执行)参数**。

此执行可能会像在同步回调中一样立即执行，但更常见的是，它可能会在稍后的时间发生，就像在异步回调中一样。

使用回调的一个常见场景是在服务交互期间，当我们需要处理来自服务调用的响应时。

在本教程中，我们将使用如下所示的Service接口作为测试用例中的协作者：

```java
public interface Service {
    void doAction(String request, Callback<Response> callback);
}
```

在Callback参数中，我们传递一个类，该类将使用reply(T response)方法处理响应：

```java
public interface Callback<T> {

    void reply(T response);
}
```

### 2.1 Service

**我们还将使用一个简单的Service来演示如何传递和调用回调**：

```java
public class ActionHandler {

    private final Service service;

    public ActionHandler(Service service) {
        this.service = service;
    }

    public void doAction() {
        service.doAction("our-request", new Callback<Response>() {
            @Override
            public void reply(Response response) {
                handleResponse(response);
            }
        });
    }
}
```

在将一些数据添加到response对象之前，handleResponses方法检查响应是否有效：

```java
public class ActionHandler {

    private void handleResponse(Response response) {
        if (response.isValid()) {
            response.setData(new Data("Successful data response"));
        }
    }
}
```

**为了清楚起见，我们在service.doAction()调用中没有使用Lambda表达式，如果使用Lambda代码如下：**

```text
service.doAction("our-request", response -> handleResponse(response));
```

## 3. 使用ArgumentCaptor

**现在让我们看看如何使用Mockito通过ArgumentCaptor来获取Callback对象**：

```java
class ActionHandlerUnitTest {

    @Mock
    private Service service;

    @Captor
    private ArgumentCaptor<Callback<Response>> callbackCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenServiceWithValidResponse_whenCallbackReceived_thenProcessed() {
        ActionHandler handler = new ActionHandler(service);
        handler.doAction();

        verify(service).doAction(anyString(), callbackCaptor.capture());

        Callback<Response> callback = callbackCaptor.getValue();
        Response response = new Response();
        callback.reply(response);

        String expectedMessage = "Successful data response";
        Data data = response.getData();
        assertEquals(expectedMessage, data.getMessage(), "Should receive a successful message: ");
    }
}
```

在这个例子中，我们首先创建一个ActionHandler，然后再调用这个handler的doAction方法。
**这只是Service中doAction方法调用的包装器**，我们在其中调用回调。

接下来，我们验证在mock Service实例上调用了doAction，将anyString()作为第一个参数，
**将callbackCaptor.capture()作为第二个参数，这是我们捕获Callback对象的地方**。然后可以使用getValue()方法返回参数的捕获值。

现在我们已经得到了Callback对象，我们创建了一个默认有效的Response对象，然后直接调用reply方法并断言响应数据具有正确的值。

## 4. 使用doAnswer()方法

现在我们来看一个常见的stubbing方法的解决方案，这种方式使用Mockito的Answer对象和doAnswer方法来stub方法doAction：

```java
class ActionHandlerUnitTest {

    @Test
    void givenServiceWithInvalidResponse_whenCallbackReceived_thenNotProcessed() {
        Response response = new Response();
        response.setValid(false);

        doAnswer((Answer<Void>) invocation -> {
            Callback<Response> callback = invocation.getArgument(1);
            callback.reply(response);

            Data data = response.getData();
            assertNull(data, "No data in invalid response: ");
            return null;
        }).when(service).doAction(anyString(), any(Callback.class));

        ActionHandler handler = new ActionHandler(service);
        handler.doAction();
    }
}
```

在我们的第二个例子中，我们首先创建了一个无效的Response对象，该对象将在稍后的测试中使用。

接下来，我们在mock Service上设置Answer，以便在调用doAction时拦截调用并使用invocation.getArgument(1)获取方法参数以获取Callback参数。

最后一步是创建ActionHandler并调用doAction，从而调用Answer。

## 5. 总结

在这篇简短的文章中，我们介绍了使用Mockito测试回调的两种不同方法。
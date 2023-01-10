## 1. 概述

通常，在我们的 Web 应用程序中处理 HTTP 调用时，我们需要一种方法来捕获有关请求和响应的某种指标。通常，这是为了监控我们的应用程序进行的 HTTP 调用的大小和频率。

[OkHttp](https://square.github.io/okhttp/)是适用于 Android 和Java应用程序的高效 HTTP 和 HTTP/2 客户端。在之前的教程中，我们了解了如何使用 OkHttp的[基础知识。](https://www.baeldung.com/guide-to-okhttp)

在本教程中，我们将了解如何使用事件捕获这些类型的指标。

## 2. 活动

顾名思义，事件为我们提供了一种强大的机制来记录与整个 HTTP 调用生命周期相关的应用程序指标。

为了订阅我们感兴趣的所有事件，我们需要做的就是定义一个EventListener并覆盖我们想要捕获的事件的方法。

例如，如果我们只想监视失败和成功的调用，这将特别有用。在那种情况下，我们只需重写与我们的事件侦听器类中的那些事件相对应的特定方法。稍后我们将更详细地看到这一点。

在我们的应用程序中使用事件至少有几个优点：

-   我们可以使用事件来监控应用程序进行的 HTTP 调用的大小和频率
-   这可以帮助我们快速确定我们的应用程序中可能存在瓶颈的地方

最后，我们还可以使用事件来确定我们的网络是否也存在潜在问题。

## 3.依赖关系

当然，我们需要将标准的[okhttp依赖](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.okhttp3" AND a%3A"okhttp")项添加 到我们的 pom.xml中：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.9.1</version>
</dependency>
```

我们还需要另一个专门用于测试的依赖项。让我们添加 OkHttp [mockwebserver 工件](https://search.maven.org/classic/#search|ga|1|g%3A"com.squareup.okhttp3" AND a%3A"mockwebserver")：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>mockwebserver</artifactId>
    <version>4.9.1</version>
    <scope>test</scope>
</dependency>
```

现在我们已经配置了所有必要的依赖项，我们可以继续编写我们的第一个事件监听器。

## 4.事件方法和顺序

但在我们开始定义我们自己的事件侦听器之前，我们将退后一步，简要看看我们可以使用哪些事件方法以及我们期望事件到达的顺序。当我们稍后深入研究一些真实的例子时，这将对我们有所帮助。

假设我们正在处理一个没有重定向或重试的成功 HTTP 调用。然后我们可以期待这个典型的方法调用流程。

### 4.1. 调用开始()

这个方法是我们的入口点，我们将在我们排队调用或我们的客户端执行它时立即调用它。

### 4.2. proxySelectStart()和proxySelectEnd()

第一个方法在代理选择之前调用，同样在代理选择之后调用，包括按尝试顺序排列的代理列表。如果没有配置代理，这个列表当然可以为空。

### 4.3. dnsStart()和dnsEnd()

这些方法在 DNS 查找之前和 DNS 解析之后立即被调用。

### 4.4. connectStart()和connectEnd()

这些方法在建立和关闭套接字连接之前被调用。

### 4.5. secureConnectStart()和secureConnectEnd()

如果我们的调用使用 HTTPS，那么我们将在connectStart和connectEnd之间穿插使用这些安全连接变体。

### 4.6. connectionAcquired()和connectionReleased()

在获取或释放连接后调用。

### 4.7. requestHeadersStart()和requestHeadersEnd()

这些方法将在发送请求标头之前和之后立即调用。

### 4.8. requestBodyStart()和requestBodyEnd()

顾名思义，在发送请求主体之前调用。当然，这只适用于包含正文的请求。

### 4.9. responseHeadersStart()和responseHeadersEnd()

当响应标头首次从服务器返回时以及接收到响应标头后立即调用这些方法。

### 4.10. responseBodyStart()和responseBodyEnd()

同样，在第一次从服务器返回响应主体时调用，并在接收到响应主体后立即调用。

除了这些方法之外，我们还可以使用三种额外的方法来捕获故障：

### 4.11. callFailed()、responseFailed()和requestFailed()

如果我们的调用永久失败，则请求写入失败，或者响应读取失败。

## 5. 定义一个简单的事件监听器

让我们从定义我们自己的偶数监听器开始。为了让事情变得非常简单，我们的事件侦听器将记录调用开始和结束的时间以及一些请求和响应标头信息：

```java
public class SimpleLogEventsListener extends EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLogEventsListener.class);

    @Override
    public void callStart(Call call) {
        LOGGER.info("callStart at {}", LocalDateTime.now());
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        LOGGER.info("requestHeadersEnd at {} with headers {}", LocalDateTime.now(), request.headers());
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        LOGGER.info("responseHeadersEnd at {} with headers {}", LocalDateTime.now(), response.headers());
    }

    @Override
    public void callEnd(Call call) {
        LOGGER.info("callEnd at {}", LocalDateTime.now());
    }
}
```

如我们所见，要创建我们的侦听器，我们需要做的就是从EventListener类扩展。然后我们可以继续并覆盖我们关心的事件的方法。

在我们的简单侦听器中，我们记录呼叫开始和结束的时间以及请求和响应标头到达时的时间。

### 5.1. 一起插入

要实际使用这个监听器，我们需要做的就是在构建OkHttpClient实例时调用eventListener方法，它应该可以正常工作：

```java
OkHttpClient client = new OkHttpClient.Builder() 
  .eventListener(new SimpleLogEventsListener())
  .build();
```

在下一节中，我们将看看如何测试我们的新侦听器。

### 5.2. 测试事件监听器

现在，我们已经定义了第一个事件监听器；让我们继续编写我们的第一个集成测试：

```java
@Rule
public MockWebServer server = new MockWebServer();

@Test
public void givenSimpleEventLogger_whenRequestSent_thenCallsLogged() throws IOException {
    server.enqueue(new MockResponse().setBody("Hello Baeldung Readers!"));
        
    OkHttpClient client = new OkHttpClient.Builder()
      .eventListener(new SimpleLogEventsListener())
      .build();

    Request request = new Request.Builder()
      .url(server.url("/"))
      .build();

    try (Response response = client.newCall(request).execute()) {
        assertEquals("Response code should be: ", 200, response.code());
        assertEquals("Body should be: ", "Hello Baeldung Readers!", response.body().string());
    }
 }
```

首先，我们使用 OkHttp MockWebServer [JUnit 规则](https://www.baeldung.com/junit-4-rules)。

这是一个轻量级的、可编写脚本的 Web 服务器，用于测试我们将用来测试事件侦听器的 HTTP 客户端。通过使用此规则，我们将为每个集成测试创建一个干净的服务器实例。

考虑到这一点，现在让我们来看看我们测试的关键部分：

-   首先，我们设置一个模拟响应，在正文中包含一条简单的消息
-   然后，我们构建我们的OkHttpClient并配置我们的SimpleLogEventsListener
-   最后，我们发送请求并使用断言检查收到的响应代码和正文

### 5.3. 运行测试

当我们运行测试时，我们会看到记录的事件：

```plaintext
callStart at 2021-05-04T17:51:33.024
...
requestHeadersEnd at 2021-05-04T17:51:33.046 with headers User-Agent: A Baeldung Reader
Host: localhost:51748
Connection: Keep-Alive
Accept-Encoding: gzip
...
responseHeadersEnd at 2021-05-04T17:51:33.053 with headers Content-Length: 23
callEnd at 2021-05-04T17:51:33.055
```

## 6. 把它们放在一起

现在让我们想象一下，我们想要构建简单的日志记录示例，并记录调用链中每个步骤的运行时间：

```java
public class EventTimer extends EventListener {

    private long start;

    private void logTimedEvent(String name) {
        long now = System.nanoTime();
        if (name.equals("callStart")) {
            start = now;
        }
        long elapsedNanos = now - start;
        System.out.printf("%.3f %s%n", elapsedNanos / 1000000000d, name);
    }

    @Override
    public void callStart(Call call) {
        logTimedEvent("callStart");
    }

    // More event listener methods
}
```

这与我们的第一个示例非常相似，但这次我们捕获了从每个事件的调用开始时起经过的时间。通常，这对于检测网络延迟可能非常有趣。

让我们看看我们是否针对像我们自己的https://www.baeldung.com/这样的真实网站运行它：

```plaintext
0.000 callStart
0.012 proxySelectStart
0.012 proxySelectEnd
0.012 dnsStart
0.175 dnsEnd
0.183 connectStart
0.248 secureConnectStart
0.608 secureConnectEnd
0.608 connectEnd
0.609 connectionAcquired
0.612 requestHeadersStart
0.613 requestHeadersEnd
0.706 responseHeadersStart
0.707 responseHeadersEnd
0.765 responseBodyStart
0.765 responseBodyEnd
0.765 connectionReleased
0.765 callEnd

```

由于此调用通过 HTTPS，我们还将看到secureConnectStart和secureConnectStart事件。

## 7. 监控失败的调用

到目前为止，我们一直专注于成功的 HTTP 请求，但我们也可以捕获失败的事件：

```java
@Test (expected = SocketTimeoutException.class)
public void givenConnectionError_whenRequestSent_thenFailedCallsLogged() throws IOException {
    OkHttpClient client = new OkHttpClient.Builder()
      .eventListener(new EventTimer())
      .build();

    Request request = new Request.Builder()
      .url(server.url("/"))
      .build();

    client.newCall(request).execute();
}
```

在此示例中，我们有意避免设置我们的模拟 Web 服务器，这当然意味着我们将看到SocketTimeoutException形式的灾难性故障。

现在让我们看一下运行测试时的输出：

```plaintext
0.000 callStart
...
10.008 responseFailed
10.009 connectionReleased
10.009 callFailed
```

正如预期的那样，我们将看到我们的呼叫开始，然后在 10 秒后，连接超时发生，因此，我们看到记录了responseFailed和callFailed事件。

## 8. 并发速记

到目前为止，我们假设我们没有[同时](https://www.baeldung.com/cs/aba-concurrency)执行多个调用。如果我们想适应这种情况，那么我们需要在配置OkHttpClient时使用eventListenerFactory方法。

我们可以使用工厂为每个 HTTP 调用创建一个新的EventListener实例。当我们使用这种方法时，可以在我们的侦听器中保持特定于调用的状态。

## 9.总结

在本文中，我们了解了如何使用 OkHttp 捕获事件。首先，我们首先解释什么是事件，了解我们可以使用哪些事件以及它们在处理 HTTP 调用时到达的顺序。

然后我们了解了如何定义一个简单的事件记录器来捕获部分 HTTP 调用以及如何编写集成测试。
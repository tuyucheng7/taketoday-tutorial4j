## 1. 概述

在本教程中，我们将学习如何使用[Jetty 中的 Reactive HTTP 客户端](https://github.com/jetty-project/jetty-reactive-httpclient)。我们将通过创建小型测试用例来演示它与不同 Reactive 库的用法。

## 2. 什么是响应式HttpClient？

Jetty 的[HttpClient](https://www.eclipse.org/jetty/documentation/current/http-client.html)允许我们执行阻塞 HTTP 请求。然而，当我们处理 Reactive API 时，我们不能使用标准的 HTTP 客户端。为了填补这个空白，Jetty 围绕HttpClient API 创建了一个包装器，以便它也支持ReactiveStreams API。

Reactive HttpClient用于通过 HTTP 调用消费或生成数据流。

我们将在此处演示的示例将有一个 Reactive HTTP 客户端，它将使用不同的 Reactive 库与 Jetty 服务器通信。我们还将讨论 Reactive HttpClient提供的请求和响应事件。

我们建议阅读我们关于[Project Reactor](https://www.baeldung.com/reactor-core)、[RxJava](https://www.baeldung.com/rx-java)和[Spring WebFlux](https://www.baeldung.com/spring-webflux)的文章，以更好地理解响应式编程概念及其术语。

## 3.Maven依赖

让我们通过将[Reactive Streams](https://search.maven.org/search?q=g:org.reactivestreams AND a:reactive-streams)、[Project Reactor](https://search.maven.org/search?q=g:org.springframework AND a:spring-webflux)、[RxJava](https://search.maven.org/search?q=g:io.reactivex.rxjava2 AND a:rxjava)、[Spring WebFlux](https://search.maven.org/search?q=g:io.projectreactor AND a:reactor-core)和[Jetty 的 Reactive HTTPClient](https://search.maven.org/search?q=g:org.eclipse.jetty AND a:jetty-reactive-httpclient)的依赖项添加到我们的pom.xml 来开始示例。 除了这些，我们还将为服务器创建添加[Jetty Server的依赖项：](https://search.maven.org/search?q=g:org.eclipse.jetty AND a:jetty-server)

```xml
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-reactive-httpclient</artifactId>
    <version>1.0.3</version>
</dependency>
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-server</artifactId>
    <version>9.4.19.v20190610</version>
</dependency>
<dependency>
    <groupId>org.reactivestreams</groupId>
    <artifactId>reactive-streams</artifactId>
    <version>1.0.3</version>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.2.12.RELEASE</version>
</dependency>
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.2.11</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webflux</artifactId>
    <version>5.1.9.RELEASE</version>
</dependency>
```

## 4.创建服务器和客户端

现在让我们创建一个服务器并添加一个请求处理程序，它只是将请求主体写入响应：

```java
public class RequestHandler extends AbstractHandler {
    @Override
    public void handle(String target, Request jettyRequest, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
        jettyRequest.setHandled(true);
        response.setContentType(request.getContentType());
        IO.copy(request.getInputStream(), response.getOutputStream());
    }
}

...

Server server = new Server(8080);
server.setHandler(new RequestHandler());
server.start();
```

然后我们可以编写HttpClient：

```java
HttpClient httpClient = new HttpClient();
httpClient.start();
```

现在我们已经创建了客户端和服务器，让我们看看如何将这个阻塞 HTTP 客户端转换为非阻塞客户端并创建请求：

```java
Request request = httpClient.newRequest("http://localhost:8080/"); 
ReactiveRequest reactiveRequest = ReactiveRequest.newBuilder(request).build();
Publisher<ReactiveResponse> publisher = reactiveRequest.response();
```

所以在这里，Jetty 提供的ReactiveRequest包装器使我们的阻塞 HTTP 客户端具有反应性。让我们继续看看它在不同反应库中的用法。

## 5. ReactiveStreams 的使用

Jetty 的HttpClient原生支持[Reactive Streams](http://www.reactive-streams.org/announce-1.0.3)，所以让我们从这里开始。

现在，Reactive Streams 只是一组接口，因此，为了我们的测试，让我们实现一个简单的阻塞订阅者：

```java
public class BlockingSubscriber implements Subscriber<ReactiveResponse> {
    BlockingQueue<ReactiveResponse> sink = new LinkedBlockingQueue<>(1);

    @Override
    public void onSubscribe(Subscription subscription) { 
        subscription.request(1); 
    }
  
    @Override 
    public void onNext(ReactiveResponse response) { 
        sink.offer(response);
    } 
   
    @Override 
    public void onError(Throwable failure) { } 

    @Override 
    public void onComplete() { }

    public ReactiveResponse block() throws InterruptedException {
        return sink.poll(5, TimeUnit.SECONDS);
    }   
}

```

请注意，我们需要根据JavaDoc调用Subscription#request ，其中声明“在通过此方法发出请求之前 ，[发布](http://www.reactive-streams.org/reactive-streams-1.0.0-javadoc/org/reactivestreams/Publisher.html)者不会发送任何事件 。”

另外请注意，我们已经添加了一个安全机制，以便我们的测试可以在 5 秒内未看到该值时退出。

现在，我们可以快速测试我们的 HTTP 请求：

```java
BlockingSubscriber subscriber = new BlockingSubscriber();
publisher.subscribe(subscriber);
ReactiveResponse response = subscriber.block();
Assert.assertNotNull(response);
Assert.assertEquals(response.getStatus(), HttpStatus.OK_200);
```

## 6.项目反应堆使用

现在让我们看看如何将 Reactive HttpClient与 Project Reactor 一起使用。发布者的创建与上一节中的几乎相同。

创建发布者后，让我们使用 Project Reactor 中的Mono类来获得响应式响应：

```java
ReactiveResponse response = Mono.from(publisher).block();
```

然后，我们可以测试结果响应：

```java
Assert.assertNotNull(response);
Assert.assertEquals(response.getStatus(), HttpStatus.OK_200);
```

### 6.1. Spring WebFlux 用法

当与 Spring WebFlux 一起使用时，将阻塞 HTTP 客户端转换为反应式客户端很容易。Spring WebFlux 附带一个反应式客户端WebClient，它可以与各种 HTTP 客户端库一起使用。我们可以使用它作为直接使用 Project Reactor 代码的替代方法。

所以首先，让我们使用JettyClientHttpConnector包装 Jetty 的 HTTP 客户端，将其与WebClient 绑定：

```java
ClientHttpConnector clientConnector = new JettyClientHttpConnector(httpClient);
```

然后将此连接器传递给WebClient以执行非阻塞 HTTP 请求：

```java
WebClient client = WebClient.builder().clientConnector(clientConnector).build();
```

接下来，让我们使用刚刚创建的响应式 HTTP 客户端进行实际的 HTTP 调用并测试结果：

```java
String responseContent = client.post()
  .uri("http://localhost:8080/").contentType(MediaType.TEXT_PLAIN)
  .body(BodyInserters.fromPublisher(Mono.just("Hello World!"), String.class))
  .retrieve()
  .bodyToMono(String.class)
  .block();
Assert.assertNotNull(responseContent);
Assert.assertEquals("Hello World!", responseContent);
```

## 7. RxJava2用法

现在让我们继续看看 Reactive HTTP 客户端如何与 RxJava2 一起使用。 

当我们在这里时，让我们稍微改变一下我们的示例，现在在请求中包含一个正文：

```java
ReactiveRequest reactiveRequest = ReactiveRequest.newBuilder(request)
  .content(ReactiveRequest.Content
    .fromString("Hello World!", "text/plain", StandardCharsets.UTF_8))
  .build();
Publisher<String> publisher = reactiveRequest
  .response(ReactiveResponse.Content.asString());

```

代码ReactiveResponse.Content.asString()将响应主体转换为字符串。如果我们只对请求的状态感兴趣，也可以使用ReactiveResponse.Content.discard()方法丢弃响应。

现在，我们可以看到使用 RxJava2 获取响应实际上与 Project Reactor 非常相似。基本上，我们只使用 Single而不是 Mono：

```java
String responseContent = Single.fromPublisher(publisher)
  .blockingGet();

Assert.assertEquals("Hello World!", responseContent);
```

## 8.请求和响应事件

Reactive HTTP 客户端在执行期间发出许多事件。它们被分类为请求事件和响应事件。这些事件有助于了解 Reactive HTTP 客户端的生命周期。

这一次，让我们通过使用 HTTP 客户端而不是请求来使我们的反应式请求略有不同：

```java
ReactiveRequest request = ReactiveRequest.newBuilder(httpClient, "http://localhost:8080/")
  .content(ReactiveRequest.Content.fromString("Hello World!", "text/plain", UTF_8))
  .build();

```

现在让我们获取HTTP 请求事件的发布者：

```java
Publisher<ReactiveRequest.Event> requestEvents = request.requestEvents();

```

现在，让我们再次使用 RxJava。这一次，我们将创建一个包含事件类型的列表，并在请求事件发生时通过订阅来填充它：

```java
List<Type> requestEventTypes = new ArrayList<>();

Flowable.fromPublisher(requestEvents)
  .map(ReactiveRequest.Event::getType).subscribe(requestEventTypes::add);
Single<ReactiveResponse> response = Single.fromPublisher(request.response());

```

然后，由于我们正在进行测试，我们可以阻止我们的响应并验证：

```java
int actualStatus = response.blockingGet().getStatus();

Assert.assertEquals(6, requestEventTypes.size());
Assert.assertEquals(HttpStatus.OK_200, actualStatus);
```

同样，我们也可以订阅响应事件。由于它们类似于请求事件订阅，我们在这里只添加了后者。包含请求和响应事件的完整实现可以在本文末尾链接的 GitHub 存储库中找到。

## 9.总结

在本教程中，我们了解了 Jetty 提供的ReactiveStreams HttpClient、它与各种 Reactive 库的用法以及与 Reactive 请求关联的生命周期事件。
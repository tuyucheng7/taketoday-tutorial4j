## 1. 概述

在本教程中，我们将了解如何使用Spring实现基于Server-Sent-Events的API。

**简单地说，Server-Sent-Events(简称SSE)是一种HTTP标准，它允许Web应用程序处理单向事件流，并在服务器发出数据时接收更新**。

Spring 4.2版本已经支持它，但是从Spring 5开始，我们现在有一种[更惯用和更方便](https://www.baeldung.com/spring-webflux)的方式来处理它。

## 2. Spring 5 Webflux与SSE

为实现这一点，**我们可以使用Reactor库提供的Flux类等实现，或者可以使用ServerSentEvent实体**，这使我们可以控制事件元数据。

### 2.1 使用Flux流式传输事件

Flux是事件流的响应式表示-它根据指定的请求或响应媒体类型进行不同的处理。

要创建SSE流媒体端点，我们必须遵循[W3C规范](https://www.w3.org/TR/eventsource/)，并将其MIME类型指定为text/event-stream：

```java
@RestController
@RequestMapping("/sse-server")
public class ServerController {

    @GetMapping(path = "/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux() {
        return Flux.interval(Duration.ofSeconds(1))
              .map(sequence -> "Flux - " + LocalTime.now().toString());
    }
}
```

**interval方法创建了一个Flux，它以增量方式发出long值。然后我们将这些值映射到我们想要的输出**。

让我们启动我们的应用程序，然后通过访问端点来尝试一下。

我们将看到浏览器如何对服务器逐秒推送的事件做出反应。有关Flux和Reactor Core的更多信息，我们可以查看[这篇](https://www.baeldung.com/reactor-core)文章。

### 2.2 使用ServerSentEvent元素

现在，我们将输出字符串包装到一个ServerSentEvent对象中，并检查这样做的好处：

```java
@GetMapping("/stream-sse")
public Flux<ServerSentEvent<String>> streamEvents() {
    return Flux.interval(Duration.ofSeconds(1))
        .map(sequence -> ServerSentEvent.<String>builder()
            .id(String.valueOf(sequence))
            .event("periodic-event")
            .data("SSE - " + LocalTime.now().toString())
            .build());
}
```

正如我们所理解的，**使用ServerSentEvent实体有几个好处**：

1. 我们可以处理事件元数据，这是我们在真实案例场景中需要的
2. 我们可以忽略“text/event-stream”媒体类型声明

在这种情况下，我们指定了一个id、一个事件名称，最重要的是，指定了事件的实际数据。

此外，我们可以添加一个comments属性和一个重试值，这将指定尝试发送事件时要使用的重新连接时间。

### 2.3  使用WebClient消费服务器发送的事件

现在让我们使用[WebClient](https://www.baeldung.com/spring-5-webclient)来消费我们的事件流：

```java
@RestController
@RequestMapping("/sse-consumer")
public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    private final WebClient client = WebClient.create("http://localhost:8081/sse-server");

    @GetMapping("/launch-sse-client")
    public String launchSSEFromSSEWebClient() {
        consumeSSE();
        return "LAUNCHED EVENT CLIENT!!! Check the logs...";
    }

    @Async
    public void consumeSSE() {
        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {
        };

        Flux<ServerSentEvent<String>> eventStream = client.get()
              .uri("/stream-sse")
              .retrieve()
              .bodyToFlux(type);

        eventStream.subscribe(content -> logger.info("Current time: {} - Received SSE: name[{}], id [{}], content[{}] ", LocalTime.now(), content.event(), content.id(), content.data()), error -> logger.error("Error receiving SSE: {}", error),
              () -> logger.info("Completed!!!"));
    }
}
```

**subscribe方法允许我们指示当我们成功接收到事件时、发生错误时以及流式传输完成时我们将如何进行**。

在我们的示例中，我们使用了retrieve方法，这是一种获取响应正文的简单直接的方法。

如果我们收到4xx或5xx响应，此方法会自动抛出WebClientResponseException，除非我们通过添加onStatus语句来处理这种场景。

另一方面，我们也可以使用exchange方法，它提供对ClientResponse的访问，并且也不会在响应失败时发出错误信号。

我们必须考虑到，如果我们不需要事件元数据，我们可以绕过ServerSentEvent包装器。

## 3. Spring MVC中的SSE Streaming

正如我们所说，SSE规范自Spring 4.2以来就得到了支持，当时引入了SseEmitter类。

**简单来说，我们将定义一个ExecutorService(单个线程)，SseEmitter将在其中完成推送数据的工作，并返回发射器实例，以这种方式保持连接打开**：

```java
@GetMapping("/stream-sse-mvc")
public SseEmitter streamSseMvc() {
    SseEmitter emitter = new SseEmitter();
    ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
    sseMvcExecutor.execute(() -> {
        try {
            for (int i = 0; true; i++) {
                SseEventBuilder event = SseEmitter.event()
                    .data("SSE MVC - " + LocalTime.now().toString())
                    .id(String.valueOf(i))
                    .name("sse event - mvc");
                emitter.send(event);
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }
    });
    return emitter;
}
```

**始终确保为您的用例场景选择正确的ExecutorService**。

通过阅读[这个](https://www.baeldung.com/spring-mvc-sse-streams)教程，我们可以了解有关Spring MVC中SSE的更多信息，并查看其他示例。

## 4. 理解Server-Sent Events

现在我们知道了如何实现SSE端点，让我们尝试通过了解一些基本概念来更深入一点。

SSE是大多数浏览器采用的规范，允许在任何时间单向流式传输事件。

“事件”只是遵循规范定义的格式的UTF-8编码文本数据流。

这种格式由一系列以换行符分隔的键值元素(id、retry、data和event，表示名称)组成。也支持comments。

**该规范不以任何方式限制数据有效负载格式；我们可以使用简单的字符串或更复杂的JSON或XML结构**。

我们必须考虑的最后一点是使用SSE流和[WebSockets](https://www.baeldung.com/spring-5-reactive-websockets)之间的区别。

**WebSockets在服务器和客户端之间提供全双工(双向)通信，而SSE使用单向通信**。

此外，WebSockets不是HTTP协议，与SSE相反，它不提供错误处理标准。

## 5. 总结

总而言之，在本文中我们了解了SSE流的主要概念，这无疑是让我们创建下一代系统的重要资源。

当我们使用这个协议时，我们现在处于一个很好的位置来理解幕后发生的事情。
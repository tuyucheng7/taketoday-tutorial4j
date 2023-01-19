## 1. 概述

在本教程中，我们将比较Spring的两个Web客户端实现，RestTemplate和Spring 5提出的响应式WebClient。

## 2. 阻塞与非阻塞客户端

对其他服务进行HTTP调用是Web应用程序中的常见需要。因此，我们需要一个Web客户端工具。

### 2.1 RestTemplate阻塞客户端

长期以来，Spring一直提供RestTemplate作为Web客户端抽象。
在底层，RestTemplate使用Java Servlet API，它基于每个请求一个线程的模型。

这意味着线程将阻塞，直到Web客户端收到响应。阻塞代码的问题是每个线程都会消耗一些内存和CPU周期。

假设有很多传入的请求，它们正在等待产生结果所需的一些慢速服务。

等待结果的请求迟早会堆积起来。因此，应用程序将创建许多线程，这将耗尽线程池或占用所有可用内存。
由于频繁的CPU上下文(线程)切换，我们也可能会遇到性能下降。

### 2.2 WebClient非阻塞客户端

另一方面，WebClient使用Spring Reactive框架提供的异步、非阻塞解决方案。

RestTemplate为每个事件(HTTP调用)使用调用者线程，而WebClient将为每个事件创建类似于“tasks”的东西。
在幕后，Reactive框架会将这些“tasks”排队并仅在适当的响应可用时执行它们。

Reactive框架使用事件驱动的架构。它提供了通过Reactive Streams API组合异步逻辑的方法。
因此，与同步/阻塞方法相比，响应式方法可以处理更多逻辑，同时使用更少的线程和系统资源。

WebClient是Spring WebFlux库的一部分。
因此，我们还可以使用具有响应类型(Mono和Flux)的函数式、流畅的API作为声明性组合来编写客户端代码。

## 3. 比较示例

为了演示这两种方法之间的差异，我们需要对许多并发客户端请求进行性能测试。

在一定数量的并行客户端请求之后，我们会看到阻塞方法的性能显着下降。

但是，无论请求的数量如何，响应式/非阻塞方法都应该提供恒定的性能。

对于本文，我们将实现两个REST端点，一个使用RestTemplate，另一个使用WebClient。
他们的任务是调用另一个慢速的REST Web服务，该服务返回Tweet集合。

下面是我们的慢速服务REST端点：

```java

@RestController
public class TweetsSlowServiceController {

    @GetMapping("/slow-service-tweets")
    private List<Tweet> getAllTweets() throws Exception {
        Thread.sleep(2000L); // delay
        return Arrays.asList(
                new Tweet("RestTemplate rules", "@user1"),
                new Tweet("WebClient is better", "@user2"),
                new Tweet("OK, both are useful", "@user1")
        );
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tweet {
    private String text;
    private String username;
}
```

### 3.1 使用RestTemplate调用慢速服务

现在让我们实现另一个REST端点，它将通过Web客户端调用我们的慢速服务。

首先，我们将使用RestTemplate：

```java

@Slf4j
@RestController
public class WebController {
    public static final int DEFAULT_PORT = 8080;

    @Setter
    private int serverPort = DEFAULT_PORT;

    @GetMapping("/tweets-blocking")
    public List<Tweet> getTweetsBlocking() {
        log.info("Starting BLOCKING Controller!");
        final String uri = getSlowServiceUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Tweet>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        List<Tweet> result = response.getBody();
        result.forEach(tweet -> log.info(tweet.toString()));
        log.info("Exiting BLOCKING Controller!");
        return result;
    }

    private String getSlowServiceUri() {
        return "http://localhost:" + serverPort + "/slow-service-tweets";
    }
}
```

当我们调用这个端点时，由于RestTemplate的同步特性，代码会阻塞等待来自我们慢速服务的响应。此方法中的其余代码将仅在收到响应时运行。

以下是我们可以在日志中看到的内容：

```
2022-09-16 12:53:26.783  INFO 6796 --- [parallel-1] c.t.t.reactive.webclient.WebController: Starting BLOCKING Controller!
2022-09-16 12:53:28.905  INFO 6796 --- [parallel-1] c.t.t.reactive.webclient.WebController: Tweet(text=RestTemplate rules, username=@user1)
2022-09-16 12:53:28.906  INFO 6796 --- [parallel-1] c.t.t.reactive.webclient.WebController: Tweet(text=WebClient is better, username=@user2)
2022-09-16 12:53:28.906  INFO 6796 --- [parallel-1] c.t.t.reactive.webclient.WebController: Tweet(text=OK, both are useful, username=@user1)
2022-09-16 12:53:28.906  INFO 6796 --- [parallel-1] c.t.t.reactive.webclient.WebController: Exiting BLOCKING Controller!
```

### 3.2 使用WebClient调用慢速服务

然后，让我们使用WebClient来调用慢速服务：

```java

@RestController
public class WebController {

    @GetMapping(value = "/tweets-non-blocking", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Tweet> getTweetsNonBlocking() {
        log.info("Starting NON-BLOCKING Controller!");
        Flux<Tweet> tweetFlux = WebClient.create()
                .get()
                .uri(getSlowServiceUri())
                .retrieve()
                .bodyToFlux(Tweet.class);
        tweetFlux.subscribe(tweet -> log.info(tweet.toString()));
        log.info("Exiting NON-BLOCKING Controller!");
        return tweetFlux;
    }
}
```

在这种情况下，WebClient返回一个发布者Flux，方法执行完成。一旦结果可用，发布者将开始向其订阅者发送数据。

请注意，调用此/tweets-non-blocking端点的客户端(在本例中为Web浏览器)也将订阅返回的Flux对象。

输出的日志如下所示：

```
2022-09-16 12:55:13.892  INFO 6796 --- [     parallel-2] c.t.t.reactive.webclient.WebController: Starting NON-BLOCKING Controller!
2022-09-16 12:55:13.893  INFO 6796 --- [     parallel-2] c.t.t.reactive.webclient.WebController: Exiting NON-BLOCKING Controller!
2022-09-16 12:55:15.902  INFO 6796 --- [ctor-http-nio-7] c.t.t.reactive.webclient.WebController: Tweet(text=RestTemplate rules, username=@user1)
2022-09-16 12:55:15.902  INFO 6796 --- [ctor-http-nio-7] c.t.t.reactive.webclient.WebController: Tweet(text=WebClient is better, username=@user2)
2022-09-16 12:55:15.902  INFO 6796 --- [ctor-http-nio-7] c.t.t.reactive.webclient.WebController: Tweet(text=OK, both are useful, username=@user1)
```

请注意，调用此端点时，方法在收到响应之前就已执行完毕。

## 4. 测试

以下是测试代码：

```java

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WebClientApplication.class)
public class WebControllerIntegrationTest {
    @Autowired
    private WebTestClient testClient;

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private WebController webController;

    @BeforeEach
    void setUp() {
        webController.setServerPort(randomServerPort);
    }

    @Test
    void whenEndpointWithBlockingClientIsCalled_thenThreeTweetsAreReceived() {
        testClient.get()
                .uri("/tweets-blocking")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Tweet.class).hasSize(3);
    }

    @Test
    void whenEndpointWithNonBlockingClientIsCalled_ThenThreeTweetsAreReceived() {
        testClient.get()
                .uri("/tweets-non-blocking")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Tweet.class).hasSize(3);
    }
}
```

## 5. 总结

在本文中，我们探讨了在Spring中使用Web客户端的两种不同方式。

RestTemplate使用Java Servlet API，因此是同步和阻塞的。

相反，WebClient是异步的，在等待响应返回时不会阻塞正在执行的线程。只有在响应准备就绪时才会生成通知。

RestTemplate仍然可以使用。但在某些情况下，与阻塞方法相比，非阻塞方法使用的系统资源要少得多。
因此，在这些情况下，WebClient是一个更可取的选择。
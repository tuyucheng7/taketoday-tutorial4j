## 1. 概述

在这篇简短的文章中，我们将讨论Spring与Vert-x的集成，并利用两全其美：强大且众所周知的Spring功能，以及来自Vert.x的响应式单事件循环。

要了解有关Vert.x的更多信息，请在[此处](https://www.baeldung.com/vertx)参阅我们的介绍性文章。

## 2. 设置

首先，让我们准备好依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-web</artifactId>
    <version>3.4.1</version>
</dependency>
```

请注意，我们已经从spring-boot-starter-web中排除了嵌入式Tomcat依赖项，因为我们将使用Verticles部署我们的服务。

你可以在[此处](https://search.maven.org/search?q=a:vertx-web)找到最新的依赖项。

## 3. Spring Vert.x应用

现在，我们将构建一个部署了两个Verticle的示例应用程序。

第一个Verticle将请求路由到处理程序，处理程序将它们作为消息发送到给定地址。另一个Verticle在给定的地址上监听。

### 3.1 Sender Verticle

ServerVerticle接收HTTP请求并将它们作为消息发送到指定地址。让我们创建一个扩展AbstractVerticle的ServerVerticle类并覆盖start()方法来创建我们的HTTP服务器：

```java
@Override
public void start() throws Exception {
    super.start();

    Router router = Router.router(vertx);
    router.get("/api/tuyucheng/articles")
      	.handler(this::getAllArticlesHandler);

    vertx.createHttpServer()
      	.requestHandler(router::accept)
      	.listen(config().getInteger("http.port", 8080));
}
```

在服务器请求处理程序中，我们传递了一个路由器对象，它将任何传入请求重定向到getAllArticlesHandler处理程序：

```java
private void getAllArticlesHandler(RoutingContext routingContext) {
    vertx.eventBus().<String>send(ArticleRecipientVerticle.GET_ALL_ARTICLES, "", 
      	result -> {
        	if (result.succeeded()) {
            	routingContext.response()
              		.putHeader("content-type", "application/json")
                    .setStatusCode(200)
                    .end(result.result()
                    .body());
        	} else {
            	routingContext.response()
              		.setStatusCode(500)
              		.end();
        	}
      	});
}
```

在处理程序方法中，我们将事件传递给Vert.x事件总线，事件ID为GET_ALL_ARTICLES。然后我们针对成功和错误场景相应地处理回调。

来自事件总线的消息将由ArticleRecipientVerticle使用，将在下一节中讨论。

### 3.2 Recipient Verticle

ArticleRecipientVerticle侦听传入消息并注入一个Spring bean，它充当Spring和Vert.x的会合点。

我们将Spring服务bean注入Verticle并调用相应的方法：

```java
@Override
public void start() throws Exception {
    super.start();
    vertx.eventBus().<String>consumer(GET_ALL_ARTICLES)
      	.handler(getAllArticleService(articleService));
}
```

在这里，articleService是一个注入的Spring bean：

```java
@Autowired
private ArticleService articleService;
```

这个Verticle会一直监听地址GET_ALL_ARTICLES上的事件总线，收到消息后，它会将其委托给getAllArticleService处理程序方法：

```java
private Handler<Message<String>> getAllArticleService(ArticleService service) {
    return msg -> vertx.<String> executeBlocking(future -> {
        try {
            future.complete(
            mapper.writeValueAsString(service.getAllArticle()));
        } catch (JsonProcessingException e) {
            future.fail(e);
        }
    }, result -> {
        if (result.succeeded()) {
            msg.reply(result.result());
        } else {
            msg.reply(result.cause().toString());
        }
    });
}
```

这将执行所需的服务操作并使用状态回复消息。正如我们在前面的部分中看到的，消息回复在ServerVerticle和回调结果中被引用。

## 4. Service类

服务类是一个简单的实现，提供与存储层交互的方法：

```java
@Service
public class ArticleService {

	@Autowired
	private ArticleRepository articleRepository;

	public List<Article> getAllArticle() {
		return articleRepository.findAll();
	}
}
```

ArticleRepository扩展了org.springframework.data.repository.CrudRepository并提供了基本的CRUD功能。

## 5. 部署Verticles

我们将部署该应用程序，就像我们部署常规Spring Boot应用程序一样。我们必须创建一个Vert.X实例，并在其中部署Verticle，在Spring上下文初始化完成后：

```java
public class VertxSpringApplication {

	@Autowired
	private ServerVerticle serverVerticle;

	@Autowired
	private ArticleRecipientVerticle articleRecipientVerticle;

	public static void main(String[] args) {
		SpringApplication.run(VertxSpringApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(serverVerticle);
		vertx.deployVerticle(articleRecipientVerticle);
	}
}
```

请注意，我们将Verticle实例注入到Spring应用程序类中。所以，我们将不得不注解Verticle类，

因此，我们必须使用@Component注解标注Verticle类、ServerVerticle和ArticleRecipientVerticle。

让我们测试应用程序：

```java
@Test
public void givenUrl_whenReceivedArticles_thenSuccess() {
    ResponseEntity<String> responseEntity = restTemplate
      	.getForEntity("http://localhost:8080/api/tuyucheng/articles", String.class);
 
    assertEquals(200, responseEntity.getStatusCodeValue());
}
```

## 6. 总结

在本文中，我们了解了如何使用Spring和Vert.x构建RESTful WebService。
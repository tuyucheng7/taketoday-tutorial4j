## 1. 概述

[到目前为止](https://www.baeldung.com/spring-cloud-series)，在我们的云应用程序中，我们已经使用网关模式来支持两个主要特性。

首先，我们将客户与每项服务隔离开来，消除了对跨源支持的需求。接下来，我们使用 Eureka 实现了定位服务实例。

在本文中，我们将研究如何使用网关模式通过单个请求从多个服务中检索数据。为此，我们将 Feign 引入我们的网关，以帮助编写对我们服务的 API 调用。

要阅读有关如何使用 Feign 客户端的信息，请查看[这篇文章](https://www.baeldung.com/spring-cloud-netflix-eureka)。

Spring Cloud现在也提供了实现这种模式的[Spring Cloud Gateway项目。](https://www.baeldung.com/spring-cloud-gateway)

## 2.设置

让我们打开网关服务器的pom.xml并添加对 Feign 的依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

作为参考——我们可以在Maven Central ( [spring-cloud-starter-feign](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.cloud" AND a%3A"spring-cloud-starter-feign") )上找到最新版本。

现在我们已经支持构建 Feign 客户端，让我们在GatewayApplication.java中启用它：

```java
@EnableFeignClients
public class GatewayApplication { ... }
```

现在让我们为图书和评级服务设置 Feign 客户端。

## 3. 假装客户

### 3.1. 预订客户端

让我们创建一个名为BooksClient.java的新接口：

```java
@FeignClient("book-service")
public interface BooksClient {
 
    @RequestMapping(value = "/books/{bookId}", method = RequestMethod.GET)
    Book getBookById(@PathVariable("bookId") Long bookId);
}
```

通过这个接口，我们指示 Spring 创建一个 Feign 客户端，该客户端将访问“ /books/{bookId }”端点。调用时，getBookById方法将对端点进行 HTTP 调用，并使用bookId参数。

为了完成这项工作，我们需要添加一个Book.java DTO：

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
 
    private Long id;
    private String author;
    private String title;
    private List<Rating> ratings;
    
    // getters and setters
}
```

让我们转到RatingsClient。

### 3.2. 评级客户端

让我们创建一个名为RatingsClient的接口：

```java
@FeignClient("rating-service")
public interface RatingsClient {
 
    @RequestMapping(value = "/ratings", method = RequestMethod.GET)
    List<Rating> getRatingsByBookId(
      @RequestParam("bookId") Long bookId, 
      @RequestHeader("Cookie") String session);
    
}
```

与BookClient一样，此处公开的方法将再次调用我们的评级服务并返回一本书的评级列表。

但是，此端点是安全的。为了能够正确访问此端点，我们需要将用户会话传递给请求。

我们使用@RequestHeader注解来做到这一点。这将指示 Feign 将该变量的值写入请求的标头。在我们的例子中，我们正在写入Cookie标头，因为 Spring Session 将在 cookie 中查找我们的会话。

在我们的例子中，我们正在写入Cookie标头，因为 Spring Session 将在 cookie 中查找我们的会话。

最后，让我们添加一个Rating.java DTO：

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rating {
    private Long id;
    private Long bookId;
    private int stars;
}
```

现在，两个客户端都已完成。让我们使用它们吧！

## 4.合并请求

网关模式的一个常见用例是拥有封装通常调用的服务的端点。这可以通过减少客户端请求的数量来提高性能。

为此，让我们创建一个控制器并将其命名为CombinedController.java：

```java
@RestController
@RequestMapping("/combined")
public class CombinedController { ... }
```

接下来，让我们连接我们新创建的假客户：

```java
private BooksClient booksClient;
private RatingsClient ratingsClient;

@Autowired
public CombinedController(
  BooksClient booksClient, 
  RatingsClient ratingsClient) {
 
    this.booksClient = booksClient;
    this.ratingsClient = ratingsClient;
}
```

最后让我们创建一个 GET 请求，它结合了这两个端点并返回一本加载了评分的书：

```java
@GetMapping
public Book getCombinedResponse(
  @RequestParam Long bookId,
  @CookieValue("SESSION") String session) {
 
    Book book = booksClient.getBookById(bookId);
    List<Rating> ratings = ratingsClient.getRatingsByBookId(bookId, "SESSION="+session);
    book.setRatings(ratings);
    return book;
}
```

请注意，我们使用从请求中提取的@CookieValue注解来设置会话值。

在那里！我们的网关中有一个组合端点，可以减少客户端和系统之间的网络调用！

## 5. 测试

让我们确保我们的新端点正常工作。

导航到LiveTest.java并为我们的组合端点添加一个测试：

```java
@Test
public void accessCombinedEndpoint() {
    Response response = RestAssured.given()
      .auth()
      .form("user", "password", formConfig)
      .get(ROOT_URI + "/combined?bookId=1");
 
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    assertNotNull(response.getBody());
 
    Book result = response.as(Book.class);
 
    assertEquals(new Long(1), result.getId());
    assertNotNull(result.getRatings());
    assertTrue(result.getRatings().size() > 0);
}
```

启动 Redis，然后运行我们应用程序中的每个服务：config、discovery、zipkin、 gateway、book和评级服务。

一切就绪后，运行新测试以确认它正在运行。

## 六. 总结

我们已经了解了如何将 Feign 集成到我们的网关中以构建专用端点。我们可以利用这些信息来构建我们需要支持的任何 API。最重要的是，我们看到我们并没有被只公开单个资源的通用 API 所困。

使用网关模式，我们可以根据每个客户的独特需求设置网关服务。这创造了解耦，让我们的服务可以根据需要自由发展，保持精简并专注于应用程序的一个领域。
## 1. 概述

有时我们需要在使用[Feign](https://www.baeldung.com/intro-to-feign)时在 HTTP 调用中设置请求标头。Feign 允许我们使用声明性语法简单地构建 HTTP 客户端。

在这个简短的教程中，我们将了解如何使用注解配置请求标头。我们还将了解如何使用拦截器来包含常见的请求标头。

## 2.例子

在本教程中，我们将使用公开 REST API 端点 的示例[书店应用程序。](https://github.com/Baeldung/spring-hypermedia-api)

我们可以轻松克隆项目并在本地运行它：

```bash
$ mvn install spring-boot:run
```

让我们深入探讨客户端实现。

## 3.使用标题注解

让我们考虑一个场景，其中特定的 API 调用应始终包含静态标头。在这种情况下，我们可以将该请求标头配置为客户端的一部分。一个典型的例子是包含一个Content-Type头。

使用@Header注解，我们可以轻松配置静态请求头。我们可以静态或动态地定义此标头值。

### 3.1. 设置静态标头值

让我们在BookClient中配置两个静态标头，即Accept-Language和Content-Type：

```less
@Headers("Accept-Language: en-US")
public interface BookClient {
    
    @RequestLine("GET /{isbn}")
    BookResource findByIsbn(@Param("isbn") String isbn);

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    void create(Book book);
}
```

在上面的代码中，标头Accept-Language包含在所有 API 中，因为它应用于BookClient。但是，create 方法有一个额外的 Content-Type 标头。

接下来，让我们看看如何使用 Feign 的Builder方法创建BookClient并传递[HEADERS](https://www.baeldung.com/java-feign-logging)日志级别：

```java
Feign.builder()
  .encoder(new GsonEncoder())
  .decoder(new GsonDecoder())
  .logger(new Slf4jLogger(type))
  .logLevel(Logger.Level.HEADERS)
  .target(BookClient.class, "http://localhost:8081/api/books");

```

现在，让我们测试创建方法：

```java
String isbn = UUID.randomUUID().toString();
Book book = new Book(isbn, "Me", "It's me!", null, null);
        
bookClient.create(book);

book = bookClient.findByIsbn(isbn).getBook();
```

然后，让我们验证输出记录器中的标头：

```bash
18:01:15.039 [main] DEBUG c.b.f.c.h.staticheader.BookClient - [BookClient#create] Accept-Language: en-US
18:01:15.039 [main] DEBUG c.b.f.c.h.staticheader.BookClient - [BookClient#create] Content-Type: application/json
18:01:15.096 [main] DEBUG c.b.f.c.h.staticheader.BookClient - [BookClient#findByIsbn] Accept-Language: en-US
```

我们应该注意，如果客户端接口和 API 方法中的标头名称相同，则它们不会相互覆盖。相反，该请求将包括所有此类值。

### 3.2. 设置动态标头值

使用@Header注解，我们还可以设置动态标头值。为此，我们需要将值表示为占位符。

让我们将x-requester-id标头包含到BookClient中，并使用占位符requester：


```java
@Headers("x-requester-id: {requester}")
public interface BookClient {
   
    @RequestLine("GET /{isbn}")
    BookResource findByIsbn(@Param("requester") String requester, @Param("isbn") String isbn);
}
```

在这里，我们将 x-requester-id 设为传递给每个方法的变量。我们使用@Param 注解来匹配变量的名称。它在运行时扩展以满足 @Headers 注解指定的标头。

现在，让我们使用x-requester-id标头调用BookClient API ：

```java
String requester = "test";
book = bookClient.findByIsbn(requester, isbn).getBook();
```

然后，让我们在输出记录器中验证请求标头：

```bash
18:04:27.515 [main] DEBUG c.b.f.c.h.s.parameterized.BookClient - [BookClient#findByIsbn] x-requester-id: test
```

## 4. 使用HeaderMaps注解

让我们想象一个场景，其中标头键和值都是动态的。在这种情况下，可能的密钥范围是提前未知的。此外，标头在同一客户端上的不同方法调用之间可能会有所不同。一个典型的例子是设置某些元数据标头。

使用带有@HeaderMap注解的Map参数 设置动态标头：

```java
@RequestLine("POST")
void create(@HeaderMap Map<String, Object> headers, Book book);
```

现在，让我们尝试使用标头映射测试create方法：

```java
Map<String,Object> headerMap = new HashMap<>();
    	
headerMap.put("metadata-key1", "metadata-value1");
headerMap.put("metadata-key2", "metadata-value2");
    	
bookClient.create(headerMap, book);
```

然后，让我们验证输出记录器中的标头：

```bash
18:05:03.202 [main] DEBUG c.b.f.c.h.dynamicheader.BookClient - [BookClient#create] metadata-key1: metadata-value1
18:05:03.202 [main] DEBUG c.b.f.c.h.dynamicheader.BookClient - [BookClient#create] metadata-key2: metadata-value2
```

## 5.请求拦截器

拦截器可以为每个请求或响应执行各种隐式任务，如日志记录或身份验证。

Feign 提供了一个RequestInterceptor接口。有了这个，我们可以添加请求标头。

当已知标头应包含在每个调用中时，添加请求拦截器是有意义的。此模式消除了调用代码对实现非功能性需求(如身份验证或跟踪)的依赖性。

让我们通过实现一个AuthorisationService来尝试一下，我们将使用它来生成授权令牌：

```java
public class ApiAuthorisationService implements AuthorisationService {

    @Override
    public String getAuthToken() {
        return "Bearer " + UUID.randomUUID();
    }
}
```

现在，让我们实现我们的自定义请求拦截器：

```java
public class AuthRequestInterceptor implements RequestInterceptor {
	
    private AuthorisationService authTokenService;
   
    public AuthRequestInterceptor(AuthorisationService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorisation", authTokenService.getAuthToken());
    }
}

```

我们应该注意到，请求拦截器可以读取、删除或改变请求模板的任何部分。

现在，让我们使用构建器方法将AuthInterceptor添加到BookClient ：

```java
Feign.builder()
  .requestInterceptor(new AuthInterceptor(new ApiAuthorisationService()))
  .encoder(new GsonEncoder())
  .decoder(new GsonDecoder())
  .logger(new Slf4jLogger(type))
  .logLevel(Logger.Level.HEADERS)
  .target(BookClient.class, "http://localhost:8081/api/books");

```

然后，让我们使用Authorization标头测试BookClient API ：

```java
bookClient.findByIsbn("0151072558").getBook();
```

现在，让我们验证输出记录器中的标头：

```bash
18:06:06.135 [main] DEBUG c.b.f.c.h.staticheader.BookClient - [BookClient#findByIsbn] Authorisation: Bearer 629e0af7-513d-4385-a5ef-cb9b341cedb5
```

多个 Request 拦截器也可以应用于Feign 客户端。尽管不保证它们的应用顺序。

## 六. 总结

在本文中，我们讨论了 Feign 客户端如何支持设置请求标头。我们使用@Headers、@HeaderMaps注解和请求拦截器实现了它。
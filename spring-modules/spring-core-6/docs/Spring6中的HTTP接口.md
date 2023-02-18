## 一、概述

Spring Framework 第 6 版和 Spring Boot 第 3 版使我们能够**使用 Java 接口定义声明式 HTTP 服务**。该方法受到流行的 HTTP 客户端库（如[Feign）](https://www.baeldung.com/spring-boot-feignclient-vs-webclient)的启发，类似于我们在 Spring Data 中定义存储库的方式。

在本教程中，我们将首先了解如何定义 HTTP 接口。然后，我们将检查可用的交换方法注释，以及支持的方法参数和返回值。接下来，我们将看到如何创建一个实际的 HTTP 接口实例，一个执行声明的 HTTP 交换的代理客户端。

最后，我们将检查如何对声明性 HTTP 接口及其代理客户端执行异常处理和测试。

## 2.HTTP接口

声明性 HTTP 接口包括用于 HTTP 交换的注释方法。我们可以使用带注释的 Java 接口简单地表达远程 API 细节，让 Spring**生成一个实现该接口并执行交换的代理**。这有助于减少样板代码。

### 2.1. 兑换方式

***@HttpExchange\*是我们可以应用于 HTTP 接口及其交换方法的根注释***。*如果我们在接口级别应用它，那么它适用于所有交换方法。这对于指定所有接口方法通用的属性（如内容类型或 URL 前缀）很有用。

所有 HTTP 方法的附加注释可用：

-   *@GetExchange*用于 HTTP GET 请求
-   *@PostExchange*用于 HTTP POST 请求
-   *@PutExchange*用于 HTTP PUT 请求
-   *@PatchExchange*用于 HTTP PATCH 请求
-   *@DelectExchange*用于 HTTP DELETE 请求

让我们使用特定于方法的注释为简单的 REST 服务定义一个示例声明性 HTTP 接口：

```java
interface BooksService {

    @GetExchange("/books")
    List<Book> getBooks();

    @GetExchange("/books/{id}")
    Book getBook(@PathVariable long id);

    @PostExchange("/books")
    Book saveBook(@RequestBody Book book);

    @DeleteExchange("/books/{id}")
    ResponseEntity<Void> deleteBook(@PathVariable long id);
}复制
```

我们应该注意，所有 HTTP 方法特定的注释都是使用*@HttpExchange*进行元注释的。因此，*@GetExchange(“/books”)*等价于*@HttpExchange(url = “/books”, method = “GET”)*。

### 2.2. 方法参数

在我们的示例界面中，我们对方法参数使用了*@PathVariable*和*@RequestBody*注释。此外，我们可以**为我们的交换方法使用以下一组方法参数**：

-   URI：动态设置请求的URL，覆盖注解属性
-   HttpMethod：动态设置请求的HTTP方法，覆盖注解属性
-   *@RequestHeader* : 添加请求头名称和值，参数可以是*Map*或*MultiValueMap*
-   *@PathVariable*：替换请求 URL 中具有占位符的值
-   *@RequestBody：*提供请求的主体作为要序列化的对象，或者[反应流](https://www.baeldung.com/reactor-core)发布者，如*Mono* 或*Flux*
-   *@RequestParam* : 添加请求参数名称和值，参数可以是*Map*或*MultiValueMap*
-   *@CookieValue* : 添加 cookie 名称和值，参数可以是*Map*或*MultiValueMap*

*我们应该注意，请求参数仅针对内容类型“application/x-www-form-urlencoded”*在请求正文中进行编码。否则，请求参数被添加为 URL 查询参数。

### 2.3. 返回值

在我们的示例界面中，交换方法返回阻塞值。但是，声明式**HTTP 接口** **交换方法同时支持阻塞和反应式****返回值。**

此外，我们可能会选择仅返回特定的响应信息，例如状态码或标头。如果我们对服务响应根本不感兴趣，则返回*void 。*

总而言之，HTTP 接口交换方法支持以下一组返回值：

-   *void, Mono<Void>*：执行请求并释放响应内容
-   *HttpHeaders, Mono<HttpHeaders>* : 执行请求，释放响应内容，返回响应头
-   *<T>, Mono<T>*：执行请求并将响应内容解码为声明的类型
-   *<T>, Flux<T>*：执行请求并将响应内容解码为声明类型的流
-   *ResponseEntity<Void>, Mono<ResponseEntity<Void>>*：执行请求，释放响应内容，返回一个包含状态和头部的*ResponseEntity* 
-   *ResponseEntity<T>, Mono<ResponseEntity<T>>*：执行请求，释放响应内容，并返回一个包含状态、头和解码主体的*ResponseEntity* 
-   *Mono<ResponseEntity<Flux<T>>*：执行请求，释放响应内容，返回一个包含状态、头部和解码后的响应体流的*ResponseEntity* 

*我们还可以使用在ReactiveAdapterRegistry*中注册的任何其他异步或反应类型。

## 3.客户端代理

现在我们已经定义了示例 HTTP 服务接口，我们需要创建一个代理来实现该接口并执行交换。

### 3.1. 代理工厂

Spring 框架为我们提供了一个***HttpServiceProxyFactory\*，我们可以使用它来为我们的 HTTP 接口生成一个客户端代理**：

```java
HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
  .builder(WebClientAdapter.forClient(webClient))
  .build();
booksService = httpServiceProxyFactory.createClient(BooksService.class);复制
```

要使用提供的工厂创建代理，除了 HTTP 接口之外，我们还需要一个反应式[Web 客户](https://www.baeldung.com/spring-5-webclient)端的实例：

```java
WebClient webClient = WebClient.builder()
  .baseUrl(serviceUrl)
  .build();复制
```

现在，我们可以将客户端代理实例注册为 Spring [bean](https://www.baeldung.com/spring-bean-annotations)或[组件](https://www.baeldung.com/spring-component-annotation)，并使用它与 REST 服务交换数据。

### 3.2. 异常处理

默认情况下，*WebClient*为任何客户端或服务器错误 HTTP 状态代码抛出*WebClientResponseException*。我们可以通过注册适用于通过客户端执行的所有响应的**默认响应状态处理程序来自定义异常处理：**

```java
BooksClient booksClient = new BooksClient(WebClient.builder()
  .defaultStatusHandler(HttpStatusCode::isError, resp ->
    Mono.just(new MyServiceException("Custom exception")))
  .baseUrl(serviceUrl)
  .build());复制
```

因此，如果我们请求一本不存在的书，我们将收到一个自定义异常：

```java
BooksService booksService = booksClient.getBooksService();
assertThrows(MyServiceException.class, () -> booksService.getBook(9));复制
```

## 4.测试

让我们看看如何测试我们的样本声明性 HTTP 接口及其执行交换的客户端代理。

### 4.1. 使用*Mockito*

由于我们的目标是测试使用声明性 HTTP 接口创建的客户端代理，因此我们需要**使用 Mockito 的**[**深度存根**](https://www.baeldung.com/mockito-fluent-apis)**功能****模拟底层[\*WebClient 的\*](https://www.baeldung.com/spring-mocking-webclient)**流畅 API ： 

```java
@Mock(answer = Answers.RETURNS_DEEP_STUBS)
private WebClient webClient;复制
```

现在，我们可以使用[Mockito 的 BDD](https://www.baeldung.com/bdd-mockito)方法来调用链接的 WebClient 方法并提供模拟响应：

```java
given(webClient.method(HttpMethod.GET)
  .uri(anyString(), anyMap())
  .retrieve()
  .bodyToMono(new ParameterizedTypeReference<List<Book>>(){}))
  .willReturn(Mono.just(List.of(
    new Book(1,"Book_1", "Author_1", 1998),
    new Book(2, "Book_2", "Author_2", 1999)
  )));复制
```

一旦我们有了模拟响应，我们就可以使用 HTTP 接口中定义的方法调用我们的服务：

```java
BooksService booksService = booksClient.getBooksService();
Book book = booksService.getBook(1);
assertEquals("Book_1", book.title());复制
```

### 4.2. 使用*模拟服务器*

如果我们想避免模拟*WebClient*，我们可以使用像**[\*MockServer\*](https://www.baeldung.com/mockserver)****这样的库来生成并返回固定的 HTTP 响应**：

```java
new MockServerClient(SERVER_ADDRESS, serverPort)
  .when(
    request()
      .withPath(PATH + "/1")
      .withMethod(HttpMethod.GET.name()),
    exactly(1)
  )
  .respond(
    response()
      .withStatusCode(HttpStatus.SC_OK)
      .withContentType(MediaType.APPLICATION_JSON)
      .withBody("{\"id\":1,\"title\":\"Book_1\",\"author\":\"Author_1\",\"year\":1998}")
  );复制
```

现在我们已经有了模拟响应和一个正在运行的模拟服务器，我们可以调用我们的服务：

```java
BooksClient booksClient = new BooksClient(WebClient.builder()
  .baseUrl(serviceUrl)
  .build());
BooksService booksService = booksClient.getBooksService();
Book book = booksService.getBook(1);
assertEquals("Book_1", book.title());复制
```

此外，我们可以验证被测代码是否调用了正确的模拟服务：

```java
mockServer.verify(
  HttpRequest.request()
    .withMethod(HttpMethod.GET.name())
    .withPath(PATH + "/1"),
  VerificationTimes.exactly(1)
);复制
```

## 5.结论

在本文中，我们探索了**Spring 版本 6 中可用的声明式 HTTP 服务接口。**我们研究了如何使用可用的交换方法注释以及支持的方法参数和返回值来定义 HTTP 接口。

我们探讨了如何创建一个实现 HTTP 接口并执行交换的代理客户端。此外，我们还了解了如何通过定义自定义状态处理程序来执行异常处理。*最后，我们了解了如何使用Mockito*和*MockServer*测试声明式接口及其客户端代理。
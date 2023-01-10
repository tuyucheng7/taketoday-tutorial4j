## 1. 概述

我们希望微服务之间的 HTTP API 调用偶尔会遇到错误。

[在带有OpenFeign](https://www.baeldung.com/spring-cloud-openfeign)的Spring Boot 中，默认错误处理程序将下游错误(例如Not Found)传播为Internal Server Error。这很少是传达错误的最佳方式。然而，Spring 和 OpenFeign 都允许我们提供自己的错误处理。

在本文中，我们将了解默认异常传播的工作原理。我们还将学习如何提供我们自己的错误。

## 2. 默认异常传播策略

Feign 客户端使用注解和配置属性使微服务之间的交互变得简单且高度可配置。但是，API 调用可能会由于任何随机技术原因、错误的用户请求或编码错误而失败。

幸运的是， Feign 和 Spring 有一个合理的错误处理默认实现。

### 2.1. Feign 中的默认异常传播

Feign 使用ErrorDecoder。错误处理的默认 类。这样，只要 Feign 收到任何非 2xx 状态代码，它就会将其传递给ErrorDecoder 的解码 方法。 如果 HTTP 响应具有 Retry-After标头，则解码 方法 返回 [RetryableException](https://www.baeldung.com/feign-retry#:~:text=Feign provides a sensible default,retry up to provided maximum.) ，否则返回FeignException。重试时，如果在默认重试次数后请求失败，则返回FeignException 。

decode方法将HTTP 方法密钥和响应存储在FeignException中。

### 2.2. Spring Rest 控制器中的默认异常传播

每当RestController收到任何未处理的异常时，它都会向客户端返回 500 Internal Server Error 响应。

此外，Spring 还提供了结构良好的错误响应，其中包含时间戳、HTTP 状态代码、错误和路径等详细信息：

```bash
{
    "timestamp": "2022-07-08T08:07:51.120+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/myapp1/product/Test123"
}
```

让我们通过一个例子来深入探讨这个问题。

## 3. 示例应用

假设我们需要构建一个简单的微服务，从另一个外部服务返回产品信息。

首先，让我们用一些属性为Product类建模：

```java
public class Product {
    private String id;
    private String productName;
    private double price;
}
```

然后，让我们使用Get Product端点实现ProductController ：

```java
@RestController("product_controller")
@RequestMapping(value ="myapp1")
public class ProductController {

    private ProductClient productClient;

    @Autowired
    public ProductController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable String id) {
        return productClient.getProduct(id);
    }
}
```

接下来，让我们看看如何将 Feign [Logger](https://www.baeldung.com/java-feign-logging)注册为Bean ：

```java
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

最后，让我们实现ProductClient以与外部 API 交互：

```java
@FeignClient(name = "product-client", url="http://localhost:8081/product/", configuration = FeignConfig.class)
public interface ProductClient {
    @RequestMapping(value = "{id}", method = RequestMethod.GET")
    Product getProduct(@PathVariable(value = "id") String id);
}
```

现在让我们使用上面的示例来探索默认错误传播。

## 4. 默认异常传播

### 4.1. 使用 WireMock 服务器

为了进行实验，我们需要使用模拟框架来模拟我们正在调用的服务。

首先，让我们包含[WireMockServer](https://www.baeldung.com/introduction-to-wiremock) Maven 依赖项：

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.33.2</version>
    <scope>test</scope>
</dependency>
```

然后，让我们配置并启动WireMockServer：

```java
WireMockServer wireMockServer = new WireMockServer(8081);
configureFor("localhost", 8081);
wireMockServer.start();
```

WireMockServer 在Feign客户端配置使用 的同一 主机 和 端口上启动。

### 4.2. Feign 客户端中的默认异常传播

Feign 的默认错误处理程序 ErrorDecoder.Default总是抛出 FeignException。

让我们用WireMock.stubFor模拟getProduct方法，让它看起来不可用：

```java
String productId = "test";
stubFor(get(urlEqualTo("/product/" + productId))
  .willReturn(aResponse()
  .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));

assertThrows(FeignException.class, () -> productClient.getProduct(productId));
```

上述测试用例中，ProductClient在遇到下游服务503错误时抛出FeignException 。

接下来，让我们尝试相同的实验，但使用 404 Not Found 响应：

```java
String productId = "test";
stubFor(get(urlEqualTo("/product/" + productId))
  .willReturn(aResponse()
  .withStatus(HttpStatus.NOT_FOUND.value())));

assertThrows(FeignException.class, () -> productClient.getProduct(productId));
```

同样，我们得到了一个通用的FeignException。在这种情况下，用户请求可能是错误的，我们的 Spring 应用程序需要知道这是一个错误的用户请求，以便它可以以不同的方式处理事情。

我们应该注意到 FeignException确实有一个 包含 HTTP 状态代码的状态 属性，但是try / catch策略根据异常的类型而不是它们的属性来路由异常。

### 4.3. Spring Rest 控制器中的默认异常传播

现在让我们看看FeignException如何 传播回请求者。

当 ProductController从 ProductClient 获取 FeignException 时， 它会将其传递给框架提供的默认错误处理实现。

让我们断言产品服务何时不可用：

```java
String productId = "test";
stubFor(WireMock.get(urlEqualTo("/product/" + productId))
  .willReturn(aResponse()
  .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));

mockMvc.perform(get("/myapp1/product/" + productId))
  .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
```

在这里，我们可以看到我们得到了 Spring INTERNAL_SERVER_ERROR。这种默认行为并不总是最好的，因为不同的服务错误可能需要不同的结果。

## 5. 使用 ErrorDecoder 在 Feign 中传播自定义异常

我们不应该总是返回默认的FeignException，而是应该返回一些基于 HTTP 状态代码的特定于应用程序的异常。

让我们在自定义ErrorDecoder实现中覆盖 decode方法：

```java
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()){
            case 400:
                return new BadRequestException();
            case 404:
                return new ProductNotFoundException("Product not found");
            case 503:
                return new ProductServiceNotAvailableException("Product Api is unavailable");
            default:
                return new Exception("Exception while getting product details");
        }
    }
}

```

在我们的自定义解码方法中，我们将返回不同的异常以及一些特定于应用程序的异常，以便为实际问题提供更多上下文。我们还可以在特定于应用程序的异常消息中包含更多详细信息。

我们应该注意到decode方法返回 FeignException而不是抛出它。

现在，让我们将 FeignConfig 中的 CustomErrorDecoder配置 为Spring Bean：

```java
@Bean
public ErrorDecoder errorDecoder() {
   return new CustomErrorDecoder();
}
```

或者，可以直接在ProductClient中配置CustomErrorDecoder：

```java
@FeignClient(name = "product-client-2", url = "http://localhost:8081/product/", 
   configuration = { FeignConfig.class, CustomErrorDecoder.class })
```

然后，让我们检查CustomErrorDecoder是否返回ProductServiceNotAvailableException：

```java
String productId = "test";
stubFor(get(urlEqualTo("/product/" + productId))
  .willReturn(aResponse()
  .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));

assertThrows(ProductServiceNotAvailableException.class, 
  () -> productClient.getProduct(productId));
```

同样，让我们编写一个测试用例以 在产品不存在时断言ProductNotFoundException ：

```java
String productId = "test";
stubFor(get(urlEqualTo("/product/" + productId))
  .willReturn(aResponse()
  .withStatus(HttpStatus.NOT_FOUND.value())));

assertThrows(ProductNotFoundException.class, 
  () -> productClient.getProduct(productId));
```

虽然我们现在从 Feign 客户端提供各种异常，但Spring在捕获所有异常时仍会产生一般的内部服务器错误。由于这不是我们想要的，让我们看看如何改进它。

## 6. 在 Spring Rest 控制器中传播自定义异常

正如我们所见，默认的Spring Boot错误处理程序提供了一个通用的错误响应。API 消费者可能需要包含相关错误响应的详细信息。理想情况下，错误响应应该能够解释问题并有助于调试。

我们可以通过多种方式覆盖RestController中的默认异常处理程序。

我们将研究一种使用[RestControllerAdvice](https://www.baeldung.com/exception-handling-for-rest-with-spring) 注解处理错误的方法。

### 6.1. 使用@RestControllerAdvice

@RestControllerAdvice 注解允许我们 将多个异常合并到一个单一的全局错误处理组件中。 

让我们想象一个场景，其中ProductController 需要根据下游错误返回不同的自定义错误响应。

首先，让我们创建ErrorResponse类来自定义错误响应：

```java
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;

    @JsonProperty(value = "code")
    private int code;

    @JsonProperty(value = "status")
    private String status;
    
    @JsonProperty(value = "message")
    private String message;
    
    @JsonProperty(value = "details")
    private String details;
}
```

现在，让我们对ResponseEntityExceptionHandler进行子类化，并在错误处理程序中包含@ExceptionHandler注解：

```java
@RestControllerAdvice
public class ProductExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ProductServiceNotAvailableException.class})
    public ResponseEntity<ErrorResponse> handleProductServiceNotAvailableException(ProductServiceNotAvailableException exception, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(
          HttpStatus.INTERNAL_SERVER_ERROR,
          exception.getMessage(),
          request.getDescription(false)),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ProductNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException exception, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(
          HttpStatus.NOT_FOUND,
          exception.getMessage(),
          request.getDescription(false)),
          HttpStatus.NOT_FOUND);
    }
}

```

在上面的代码中， ProductServiceNotAvailableException 作为 INTERNAL_SERVER_ERROR 响应返回给客户端。相反，像ProductNotFoundException这样的用户特定错误 的处理方式不同，并作为 NOT_FOUND响应 返回。

### 6.2. 测试 Spring Rest 控制器

让我们在产品服务不可用时测试ProductController：

```java
String productId = "test";
stubFor(WireMock.get(urlEqualTo("/product/" + productId))
  .willReturn(aResponse()
  .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));

MvcResult result = mockMvc.perform(get("/myapp2/product/" + productId))
  .andExpect(status().isInternalServerError()).andReturn();

ErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
assertEquals(500, errorResponse.getCode());
assertEquals("Product Api is unavailable", errorResponse.getMessage());

```

再次，让我们测试同一个ProductController，但出现未找到产品错误：

```java
String productId = "test";
stubFor(WireMock.get(urlEqualTo("/product/" + productId))
  .willReturn(aResponse()
  .withStatus(HttpStatus.NOT_FOUND.value())));

MvcResult result = mockMvc.perform(get("/myapp2/product/" + productId))
  .andExpect(status().isNotFound()).andReturn();

ErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
assertEquals(404, errorResponse.getCode());
assertEquals("Product not found", errorResponse.getMessage());

```

上面的测试展示了ProductController如何根据下游错误返回不同的错误响应。

如果我们没有实现我们的 CustomErrorDecoder，那么 RestControllerAdvice需要处理默认的 FeignException作为后备以获得通用错误响应。

## 七. 总结

在本文中，我们探讨了如何在 Feign 和 Spring 中实现默认错误处理。

此外，我们还看到了如何在 Feign 客户端中使用CustomErrorDecoder 以及 在 Rest Controller 中使用RestControllerAdvice 对其进行自定义。
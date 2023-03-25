## 1. 问题详细概述[RFC 7807\]

此RFC定义了简单的JSON和XML文档格式，可用于将问题详细信息传达给API使用者。这在[HTTP状态代码](https://restfulapi.net/http-status-codes/)不足以描述HTTP API问题的情况下非常有用。

以下是从一个银行账户转账到另一个银行账户时出现的问题示例，我们的账户余额不足。

```http request
HTTP/1.1 403 Forbidden
Content-Type: application/problem+json
Content-Language: en
{
	"status": 403,
	"type": "https://bankname.com/common-problems/low-balance",
	"title": "You not have enough balance",
	"detail": "Your current balance is 30 and you are transterring 50",
	"instance": "/account-transfer-service"
}
```

这里的关键短语是：

-   **status**：服务器生成的HTTP状态代码
-   **type**：标识问题类型以及如何缓解问题的URL，默认值为about:blank
-   **title**：问题的简短摘要
-   **detail**：特定于此事件的问题说明
-   **instance**：发生此问题的服务的URL，默认值为当前请求URL

## 2. Spring框架的支持

以下是Spring框架中用于支持问题细节规范的主要抽象：

### 2.1 ProblemDetail

它是表示问题详细模型的主要对象。如上一节所述，它包含标准字段，非标准字段可以作为属性添加。

```java
ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "message");
pd.setType(URI.create("http://my-app-host.com/errors/not-found"));
pd.setTitle("Record Not Found");
```

要添加非标准字段，请使用setProperty()方法。

```java
pd.setProperty("property-key", "property-value");
```

### 2.2 ErrorResponse

此接口公开HTTP错误响应详细信息，包括HTTP状态、响应标头和类型为“ProblemDetail”的正文。与仅发送ProblemDetail对象相比，它可用于向客户端提供更多信息。

**所有Spring MVC异常都实现了ErrorResponse接口。因此，所有MVC异常都已经符合规范**。

### 2.3 ErrorResponseException

这是一个非常基本的ErrorResponse实现，我们可以将其用作方便的基类来创建更具体的异常类。

```java
ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Null Pointer Exception");
pd.setType(URI.create("http://my-app-host.com/errors/npe"));
pd.setTitle("Null Pointer Exception");

throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, npe);
```

### 2.4 ResponseEntityExceptionHandler

它是一个方便的基类，用于根据RFC规范和任何ErrorResponseException处理所有Spring MVC异常的[@ControllerAdvice](https://howtodoinjava.com/spring-rest/exception-handling-example/)，并使用主体呈现错误响应。

```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(CustomException.class)
    public ProblemDetail handleCustomException(CustomException ex, WebRequest request) {
        ProblemDetail pd = // build object ...
        return pd;
    }
}
```

> 我们可以从任何@ExceptionHandler或任何@RequestMapping方法返回ProblemDetail或ErrorResponse来呈现RFC 7807响应。

## 3. 使用ResponseEntity发送ProblemDetail

在失败情况下，创建ProblemDetail类的新实例，填充相关信息并将其设置到ResponseEntity对象中。

当id大于100时，以下API将失败。

```java
@Value("${hostname}")
private String hostname;

@GetMapping(path = "/employees/v2/{id}")
public ResponseEntity getEmployeeById_V2(@PathVariable("id") Long id) {
    if (id < 100) {
        return ResponseEntity.ok(new Employee(id, "lokesh", "gupta", "admin@howtodoinjava.com"));
    } else {
        ProblemDetail pd = ProblemDetail
            .forStatusAndDetail(HttpStatus.NOT_FOUND, "Employee id '" + id + "' does no exist");
        pd.setType(URI.create("http://my-app-host.com/errors/not-found"));
        pd.setTitle("Record Not Found");
        pd.setProperty("hostname", hostname);
        return ResponseEntity.status(404).body(pd);
    }
}
```

让我们使用id = 101进行测试。它将返回RFC规范中的响应。

```json
{
    "detail": "Employee id '101' does no exist",
    "hostname": "localhost",
    "instance": "/employees/v2/101",
    "status": 404,
    "title": "Record Not Found",
    "type": "http://my-app-host.com/errors/not-found"
}
```

## 4. 从REST控制器抛出ErrorResponseException

发送问题详细信息的另一种方法是从[@RequestMapping](https://howtodoinjava.com/spring-mvc/spring-mvc-requestmapping-annotation-examples/)处理程序方法中抛出ErrorResponseException实例。

这在我们已经有一个无法发送给客户端的异常(例如NullPointerException)的情况下特别有用。在这种情况下，我们会在ErrorResponseException中填充基本信息并将其抛出。Spring MVC处理程序在内部处理此异常并将其解析为RFC指定的响应格式。

```java
@GetMapping(path = "/v3/{id}")
public ResponseEntity getEmployeeById_V3(@PathVariable("id") Long id) {
    try {
    	//somthing threw this exception
        throw new NullPointerException("Something was expected but it was null");
    }
    catch (NullPointerException npe) {
        ProblemDetail pd = ProblemDetail
            .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Null Pointer Exception");
        pd.setType(URI.create("http://my-app-host.com/errors/npe"));
        pd.setTitle("Null Pointer Exception");
        pd.setProperty("hostname", hostname);
        throw new ErrorResponseException(HttpStatus.NOT_FOUND, pd, npe);
    }
}
```

## 5. 将ProblemDetail添加到自定义异常

大多数应用程序创建更接近其业务域/模型的异常类。一些这样的异常可能是RecordNotFoundException、TransactionLimitException等。它们更具可读性，并且简洁地表示代码中的错误场景。

大多数时候，这些异常是RuntimeException的子类。

```java
public class RecordNotFoundException extends RuntimeException {
    private final String message;

    public RecordNotFoundException(String message) {
        this.message = message;
    }
}
```

我们从代码中的几个位置抛出这些异常。

```java
@GetMapping(path = "/v1/{id}")
public ResponseEntity getEmployeeById_V1(@PathVariable("id") Long id) {
    if (id < 100) {
        return ResponseEntity.ok(...);
    } else {
        throw new RecordNotFoundException("Employee id '" + id + "' does no exist");
    }
}
```

在此类异常中添加问题详细信息的最佳方法是在@ControllerAdvice类中。我们必须在@ExceptionHandler(RecordNotFoundException.class)方法中处理异常并添加所需的信息。

```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @Value("${hostname}")
    private String hostname;
    
    @ExceptionHandler(RecordNotFoundException.class)
    public ProblemDetail handleRecordNotFoundException(RecordNotFoundException ex, WebRequest request) {
        ProblemDetail body = ProblemDetail
              .forStatusAndDetail(HttpStatusCode.valueOf(404),ex.getLocalizedMessage());
        body.setType(URI.create("http://my-app-host.com/errors/not-found"));
        body.setTitle("Record Not Found");
        body.setProperty("hostname", hostname);
        return body;
    }
}
```

## 6. 在单元测试中验证ProblemDetail响应

我们还可以在单元测试中使用[RestTemplate](https://howtodoinjava.com/spring-boot2/resttemplate/spring-restful-client-resttemplate-example/)测试验证上述部分的问题详细响应。

```java
@Test
public void testAddEmployee_V2_FailsWhen_IncorrectId() {
    try {
        this.restTemplate.getForObject("/employees/v2/101", Employee.class);
    } catch (RestClientResponseException ex) {
        ProblemDetail pd = ex.getResponseBodyAs(ProblemDetail.class);
        assertEquals("Employee id '101' does no exist", pd.getDetail());
        assertEquals(404, pd.getStatus());
        assertEquals("Record Not Found", pd.getTitle());
        assertEquals(URI.create("http://my-app-host.com/errors/not-found"), pd.getType());
        assertEquals("localhost", pd.getProperties().get("hostname"));
    }
}
```

请注意，如果我们使用的是Spring Webflux，则可以使用[WebClient](https://howtodoinjava.com/spring-webflux/webclient-get-post-example/) API来验证返回的问题详细信息。

```java
@Test
void testAddEmployeeUsingWebFlux_V2_FailsWhen_IncorrectId() {
    this.webClient.get().uri("/employees/v2/101")
        .retrieve()
        .bodyToMono(String.class)
        .doOnNext(System.out::println)
        .onErrorResume(WebClientResponseException.class, ex -> {
            ProblemDetail pd = ex.getResponseBodyAs(ProblemDetail.class);
            // assertions ...
            return Mono.empty();
        })
        .block();
}
```

## 7. 总结

在这个Spring Boot 3教程中，我们了解了Spring框架中支持问题详细信息规范的新功能。我们可以从任何@ExceptionHandler或任何@RequestMapping方法返回ProblemDetail或ErrorResponse的实例，Spring框架将必要的模式添加到API响应中。

本教程中讨论的代码适用于Spring应用程序以及Spring Boot应用程序。
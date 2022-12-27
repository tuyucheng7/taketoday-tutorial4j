## 1. 概述

在本教程中，我们将讨论Swagger的@ApiOperation和@ApiResponse注解之间的主要区别。

## 2. Swagger的描述性文档

当我们创建REST API时，创建其适当的规范也很重要。此外，这样的规范应该是可读的、可理解的，并提供所有必要的信息。

此外，文档应该对API上所做的每个更改都有描述。手动创建REST API文档会很累，更重要的是，会很耗时。幸运的是，像Swagger这样的工具可以帮助我们完成这个过程。

[Swagger](https://swagger.io/tools/swagger-ui)**代表了一组围绕**[OpenAPI规范](https://www.baeldung.com/spring-rest-openapi-documentation)**构建的开源工具，它可以帮助我们设计、构建、记录和使用REST API**。

Swagger规范是用于记录REST API的标准，使用Swagger规范，我们可以描述我们的整个API，例如公开的端点、操作、参数、身份验证方法等。

Swagger提供了各种注解，可以帮助我们记录REST API。此外，**它还提供@ApiOperation和@ApiResponse注解来记录我们的REST API的响应**，在本教程的其余部分，我们将使用下面的控制器类并了解如何使用这些注解：

```java
@RestController
@RequestMapping("/customers")
class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }
}
```

## 3. @ApiOperation

**@ApiOperation注解用于描述单个操作**，操作是路径和HTTP方法的唯一组合。

此外，使用@ApiOperation，我们可以描述成功的REST API调用的结果。换句话说，我们可以使用这个注解来指定一般的返回类型。

让我们将注解添加到我们的方法中：

```java
@ApiOperation(value = "Gets customer by ID", response = CustomerResponse.class, notes = "Customer must exist")
@GetMapping("/{id}")
public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
    return ResponseEntity.ok(customerService.getById(id));
}
```

接下来，我们将介绍@ApiOperation注解中一些最常用的属性。

### 3.1 value属性

必需的value属性包含操作的摘要字段，简而言之，它提供了操作的简短描述。但是，我们应该将此参数保持在120个字符以内。

以下是我们如何在@ApiOperation注解中定义value属性的方式：

```java
@ApiOperation(value = "Gets customer by ID")
```

### 3.2 notes属性

使用notes，我们可以提供有关操作的更多详细信息。例如，我们可以放置一段描述端点限制的文本：

```java
@ApiOperation(value = "Gets customer by ID", notes = "Customer must exist")
```

### 3.3 响应属性

response属性包含操作的响应类型，此外，设置此属性将覆盖任何自动派生的数据类型。在@ApiOperation注解中定义的response属性应包含常规响应类型。

让我们创建一个类，该类将表示我们的方法返回的成功响应：

```java
class CustomerResponse {

    private Long id;
    private String firstName;
    private String lastName;

    // getters and setters
}
```

接下来，让我们将response属性添加到我们的注解中：

```java
@ApiOperation(value = "Gets customer by ID", response = CustomerResponse.class, notes = "Customer must exist")
@GetMapping("/{id}")
public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
    return ResponseEntity.ok(customerService.getById(id));
}
```

### 3.4 code属性

code属性表示响应代码的HTTP状态，[HTTP状态代码](https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html)有多种定义，应该使用其中之一。如果我们不提供，默认值将为200。

## 4. @ApiResponse

使用HTTP状态代码返回错误是一种常见的做法，我们可以使用@ApiResponse注解来描述操作的具体可能响应。

**@ApiOperation注解描述了操作和常规返回类型，而@ApiResponse注解描述了其余可能的返回状态代码**。

此外，注解可以应用于方法级别和类级别。此外，仅当尚未在方法级别上定义具有相同code值的@ApiResponse注解时，才会解析放置在类级别上的注解。换句话说，方法级别注解优先于类级别注解。

**我们应该在@ApiResponses注解中使用**[@ApiResponse](https://www.baeldung.com/java-swagger-set-list-response)**注解，无论我们有一个还是多个响应**。如果我们直接使用这个注解，它是不会被Swagger解析的。

让我们在我们的方法上定义@ApiResponses和@ApiResponse注解：

```java
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Customer not found")}
)
@GetMapping("/{id}")
public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
    return ResponseEntity.ok(customerService.getById(id));
}
```

我们也可以使用注解来指定成功响应：

```java
@ApiOperation(value = "Gets customer by ID", notes = "Customer must exist")
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = CustomerResponse.class),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Customer not found"),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class)}
)
@GetMapping("/{id}")
public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
    return ResponseEntity.ok(customerService.getById(id));
}
```

如果我们使用@ApiResponse注解指定成功的响应，则无需在@ApiOperation注解中定义它。

现在，让我们来看看@ApiResponse中使用的一些属性。

### 4.1 code和message属性

code和message属性都是@ApiResponse注解中的必需参数。

与@ApiOperation注解中的code属性一样，它应该包含响应的HTTP状态代码，值得一提的是，我们不能定义多个具有相同code属性的@ApiResponse。

message属性通常包含与响应一起出现的人类可读消息：

```java
@ApiResponse(code = 400, message = "Invalid ID supplied")
```

### 4.2 response属性

有时，端点使用不同的响应类型。例如，我们可以将一种类型用于成功响应，而另一种类型用于错误响应。我们可以通过将响应类与响应代码相关联，使用可选的response属性来描述它们。

首先，让我们定义一个在发生内部服务器错误时返回的类：

```java
class ErrorResponse {

    private String error;
    private String message;

    // getters and setters
}
```

其次，让我们为内部服务器错误添加一个新的@ApiResponse：

```java
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Customer not found"),
        @ApiResponse(code = 500, message = "Internal server error", response = ErrorResponse.class)}
)
@GetMapping("/{id}")
public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
    return ResponseEntity.ok(customerService.getById(id));
}
```

## 5. @ApiOperation和@ApiResponse的区别

综上所述，下表显示了@ApiOperation和@ApiResponse注解之间的主要区别：

| @ApiOperation |     @ApiResponse      |
|:-------------:|:---------------------:|
|   用于描述一个操作    |      用于描述操作的可能响应      |
|    用于成功响应     |      用于成功响应和错误响应      |
|   只能在方法级别定义   |     可以在方法或类级别上定义      |
|    可以直接使用     | 只能在@ApiResponses注解中使用 |
| 默认code属性值为200 |     没有默认的code属性值      |

## 6. 总结

在本文中，我们了解了@ApiOperation和@ApiResponse注解之间的区别。
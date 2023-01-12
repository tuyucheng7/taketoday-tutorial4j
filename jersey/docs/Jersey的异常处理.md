## 1. 简介

在本教程中，我们将看到使用[Jersey处理](https://www.baeldung.com/jersey-rest-api-with-spring)[异常](https://www.baeldung.com/java-exceptions)的不同方法，它是一个[JAX-RS](https://www.baeldung.com/jax-rs-response)实现。

JAX-RS 为我们提供了许多机制来处理异常，我们可以选择和组合这些机制。处理 REST 异常是构建更好的 API 的重要一步。在我们的用例中，我们将构建一个用于购买股票的 API，并查看每个步骤如何影响其他步骤。

## 2.场景设置

我们的最小设置涉及创建一个[存储库](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)、几个 bean 和一些端点。它从我们的资源配置开始。在那里，我们将使用@ApplicationPath和我们的端点包定义我们的起始 URL：

```java
@ApplicationPath("/exception-handling/")
public class ExceptionHandlingConfig extends ResourceConfig {
    public ExceptionHandlingConfig() {
        packages("com.baeldung.jersey.exceptionhandling.rest");
    }
}
```

### 2.1. 豆子

我们只需要两个 bean：Stock和Wallet，这样我们就可以保存Stock并购买它们。对于我们的Stock，我们只需要一个price属性来帮助验证。更重要的是，我们的Wallet类将有验证方法来帮助构建我们的场景：

```java
public class Wallet {
    private String id;
    private Double balance = 0.0;

    // getters and setters

    public Double addBalance(Double amount) {
        return balance += amount;
    }

    public boolean hasFunds(Double amount) {
        return (balance - amount) >= 0;
    }
}
```

### 2.2. 端点

同样，我们的 API 将有两个端点。这些将定义标准方法来保存和检索我们的 bean：

```java
@Path("/stocks")
public class StocksResource {
    // POST and GET methods
}
@Path("/wallets")
public class WalletsResource {
    // POST and GET methods
}
```

例如，让我们看看StocksResource中的 GET 方法：

```java
@GET
@Path("/{ticker}")
@Produces(MediaType.APPLICATION_JSON)
public Response get(@PathParam("ticker") String id) {
    Optional<Stock> stock = stocksRepository.findById(id);
    stock.orElseThrow(() -> new IllegalArgumentException("ticker"));

    return Response.ok(stock.get())
      .build();
}
```

在我们的 GET 方法中，我们抛出第一个异常。我们只会在稍后处理它，以便我们可以看到它的效果。

## 3. 当我们抛出异常时会发生什么？

当发生未处理的异常时，我们可能会暴露有关应用程序内部的敏感信息。如果我们尝试从StocksResource使用不存在的Stock的 GET 方法，我们会得到一个类似于此的页面：

[![默认异常屏幕](https://www.baeldung.com/wp-content/uploads/2022/04/baeldung-5157_default-exception-screen.png)](https://www.baeldung.com/wp-content/uploads/2022/04/baeldung-5157_default-exception-screen.png)

此页面显示应用程序服务器和版本，这可能有助于潜在的攻击者利用漏洞。此外，还有关于我们的类名和行号的信息，这也可能有助于攻击者。最重要的是，这些信息中的大部分对 API 用户来说是无用的，并且会给人留下不好的印象。

为了帮助控制异常响应，JAX-RS 提供了类ExceptionMapper和WebApplicationException。让我们看看它们是如何工作的。

## 4. 使用WebApplicationException自定义异常

使用WebApplicationException，我们可以创建自定义异常。这种特殊类型的RuntimeException允许我们定义响应状态和实体。我们将从创建一个设置消息和状态的InvalidTradeException开始：

```java
public class InvalidTradeException extends WebApplicationException {
    public InvalidTradeException() {
        super("invalid trade operation", Response.Status.NOT_ACCEPTABLE);
    }
}
```

另外值得一提的是，JAX-RS为常见的 HTTP 状态代码定义了WebApplicationException的子类。这些包括有用的异常，如NotAllowedException、BadRequestException等。但是，当我们需要更复杂的错误消息时，我们可以返回 JSON 响应。

### 4.1. JSON 异常

我们可以创建简单的Java类并将它们包含在我们的Response中。在我们的示例中，我们有一个主题属性，我们将使用它来包装上下文数据：

```java
public class RestErrorResponse {
    private Object subject;
    private String message;

    // getters and setters
}

```

因为这个异常不是要被操纵的，所以我们不用担心subject的类型。

### 4.2. 把一切都投入使用

要了解我们如何使用自定义异常，让我们定义一个购买股票的方法：

```java
@POST
@Path("/{wallet}/buy/{ticker}")
@Produces(MediaType.APPLICATION_JSON)
public Response postBuyStock(
  @PathParam("wallet") String walletId, @PathParam("ticker") String id) {
    Optional<Stock> stock = stocksRepository.findById(id);
    stock.orElseThrow(InvalidTradeException::new);

    Optional<Wallet> w = walletsRepository.findById(walletId);
    w.orElseThrow(InvalidTradeException::new);

    Wallet wallet = w.get();
    Double price = stock.get()
      .getPrice();

    if (!wallet.hasFunds(price)) {
        RestErrorResponse response = new RestErrorResponse();
        response.setSubject(wallet);
        response.setMessage("insufficient balance");
        throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE)
          .entity(response)
          .build());
    }

    wallet.addBalance(-price);
    walletsRepository.save(wallet);

    return Response.ok(wallet)
      .build();
}
```

在这个方法中，我们使用了我们迄今为止创建的所有内容。我们为不存在的股票或钱包抛出InvalidTradeException。而且，如果我们没有足够的资金，构建一个包含我们的Wallet的RestErrorResponse，并将其作为WebApplicationException抛出。

### 4.3. 用例示例

首先，让我们创建一个股票：

```bash
$ curl 'http://localhost:8080/jersey/exception-handling/stocks' -H 'Content-Type: application/json' -d '{
    "id": "STOCK",
    "price": 51.57
}'

{"id": "STOCK", "price": 51.57}
```

然后一个钱包来买它：

```bash
$ curl 'http://localhost:8080/jersey/exception-handling/wallets' -H 'Content-Type: application/json' -d '{
    "id": "WALLET",
    "balance": 100.0
}'

{"balance": 100.0, "id": "WALLET"}
```

之后，我们将使用我们的钱包购买股票：

```bash
$ curl -X POST 'http://localhost:8080/jersey/exception-handling/wallets/WALLET/buy/STOCK'

{"balance": 48.43, "id": "WALLET"}
```

我们将在回复中获得更新后的余额。此外，如果我们再次尝试购买，我们将获得详细的RestErrorResponse：

```json
{
    "message": "insufficient balance",
    "subject": {
        "balance": 48.43,
        "id": "WALLET"
    }
}

```

## 5. ExceptionMapper 未处理的异常

澄清一下，抛出WebApplicationException不足以摆脱默认错误页面。我们必须为我们的Response指定一个实体，这不是InvalidTradeException的情况。通常，尽管我们尝试处理所有场景，但仍可能会出现未处理的异常。所以从处理这些开始是个好主意。使用ExceptionMapper，我们为特定类型的异常定义捕获点，并在提交之前修改Response ：

```java
public class ServerExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        String message = exception.getMessage();
        Response response = exception.getResponse();
        Status status = response.getStatusInfo().toEnum();

        return Response.status(status)
          .entity(status + ": " + message)
          .type(MediaType.TEXT_PLAIN)
          .build();
    }
}

```

例如，我们只是将异常信息重新传递到我们的Response中，这将准确显示我们返回的内容。随后，我们可以通过在构建Response之前检查状态代码来更进一步：

```java
switch (status) {
    case METHOD_NOT_ALLOWED:
        message = "HTTP METHOD NOT ALLOWED";
        break;
    case INTERNAL_SERVER_ERROR:
        message = "internal validation - " + exception;
        break;
    default:
        message = "[unhandled response code] " + exception;
}
```

### 5.1. 处理特定异常

如果有一个特定的异常经常抛出，我们也可以为它创建一个ExceptionMapper。在我们的端点中，我们抛出一个IllegalArgumentException来进行简单的验证，所以让我们从它的映射器开始。这次，使用 JSON 响应：

```java
public class IllegalArgumentExceptionMapper
  implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(Response.Status.EXPECTATION_FAILED)
          .entity(build(exception.getMessage()))
          .type(MediaType.APPLICATION_JSON)
          .build();
    }

    private RestErrorResponse build(String message) {
        RestErrorResponse response = new RestErrorResponse();
        response.setMessage("an illegal argument was provided: " + message);
        return response;
    }
}
```

现在，每当我们的应用程序中出现未处理的IllegalArgumentException时，我们的IllegalArgumentExceptionMapper都会处理它。

### 5.2. 配置

要激活我们的异常映射器，我们必须返回到我们的 Jersey 资源配置并注册它们：

```java
public ExceptionHandlingConfig() {
    // packages ...
    register(IllegalArgumentExceptionMapper.class);
    register(ServerExceptionMapper.class);
}
```

这足以摆脱默认的错误页面。然后，根据抛出的内容，当发生未处理的异常时，Jersey 将使用我们的异常映射器之一。例如，当试图获取不存在的Stock时，将使用IllegalArgumentExceptionMapper ：

```bash
$ curl 'http://localhost:8080/jersey/exception-handling/stocks/NONEXISTENT'

{"message": "an illegal argument was provided: ticker"}
```

同样，对于其他未处理的异常，将使用更广泛的ServerExceptionMapper。例如，当我们使用错误的 HTTP 方法时：

```bash
$ curl -X POST 'http://localhost:8080/jersey/exception-handling/stocks/STOCK'

Method Not Allowed: HTTP 405 Method Not Allowed
```

## 六. 总结

在本文中，我们看到了使用 Jersey 处理异常的多种方式。此外，为什么它很重要，以及如何配置它。之后，我们构建了一个可以应用它们的简单场景。因此，我们现在拥有更友好、更安全的 API。
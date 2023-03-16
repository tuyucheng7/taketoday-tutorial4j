## 1. 概述

使用来自HTTP响应的状态代码来确定应用程序接下来应该对给定的响应做什么通常很有帮助。

在本教程中，我们将了解如何使用WebFlux的WebClient访问从REST请求返回的状态代码和响应主体。

[WebClient](https://www.baeldung.com/spring-5-webclient)是在Spring 5中引入的，可以在调用RESTful服务时用于异步I/O。

## 2. 案例

当对其他服务进行RESTful调用时，应用程序通常使用返回的[状态代码](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)来触发不同的功能。典型的用例包括**优雅的错误处理、触发请求重试和确定用户错误**。

因此，在进行REST调用时，仅获取响应代码通常是不够的。有时我们也想要响应主体。

在以下示例中，我们将了解如何解析来自REST客户端WebClient的响应主体。我们会将我们的行为链接到返回的状态代码，并使用WebClient提供的两种状态代码提取方法，onStatus和ExchangeFilterFunction。

## 3. 使用onStatus

[onStatus](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.ResponseSpec.html#onStatus-java.util.function.Predicate-java.util.function.Function-)是一种内置机制，可用于处理WebClient响应。**这使我们能够根据特定响应(例如400、500、503等)或状态类别(例如4XX和5XX等)应用细粒度的功能**：

```java
WebClient
    .builder()
    .build()
    .post()
    .uri("/some-resource")
    .retrieve()
    .onStatus(
        HttpStatus.INTERNAL_SERVER_ERROR::equals,
        response -> response.bodyToMono(String.class).map(Exception::new))
```

onStatus方法需要两个参数。第一个是接收状态代码的谓词。第二个参数的执行基于第一个参数的输出。第二个是将响应映射到Mono或Exception的函数。

在这种情况下，如果我们看到一个INTERNAL_SERVER_ERROR(即500)，我们将使用bodyToMono获取正文，然后将其映射到一个新的Exception。

**我们可以链接onStatus调用**，以便能够为不同的状态条件提供功能：

```java
Mono<String> response = WebClient
    .builder()
    .build()
    .post()
    .uri("some-resource")
    .retrieve()
    .onStatus( 
        HttpStatus.INTERNAL_SERVER_ERROR::equals,
        response -> response.bodyToMono(String.class).map(CustomServerErrorException::new)) 
    .onStatus(
        HttpStatus.BAD_REQUEST::equals,
        response -> response.bodyToMono(String.class).map(CustomBadRequestException::new))
    // ...
    .bodyToMono(String.class);

// do something with response
```

现在onStatus调用映射到我们的自定义异常。我们为两种错误状态中的每一种定义了异常类型。onStatus方法允许我们使用我们选择的任何类型。

## 4. 使用ExchangeFilterFunction

[ExchangeFilterFunction](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/ExchangeFilterFunction.html)是另一种处理特定状态代码和获取响应主体的方法。与onStatus不同，ExchangeFilterFunction是灵活的，适用于基于任何布尔表达式的过滤器功能。

我们可以受益于ExchangeFilterFunction的灵活性，**它涵盖与onStatus函数相同的类别**。

首先，我们将定义一个方法来根据给定[ClientResponse](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/ClientResponse.html)的状态代码处理返回的逻辑：

```java
private static Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
    HttpStatus status = response.statusCode();
    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
        return response.bodyToMono(String.class)
          	.flatMap(body -> Mono.error(new CustomServerErrorException(body)));
    }
    if (HttpStatus.BAD_REQUEST.equals(status)) {
        return response.bodyToMono(String.class)
          	.flatMap(body -> Mono.error(new CustomBadRequestException(body)));
    }
    return Mono.just(response);
}
```

接下来，我们将定义过滤器并使用对我们的处理程序的方法引用：

```java
ExchangeFilterFunction errorResponseFilter = ExchangeFilterFunction
  	.ofResponseProcessor(WebClientStatusCodeHandler::exchangeFilterResponseProcessor);
```

与onStatus调用类似，我们在出错时映射到异常类型。但是，使用Mono.error会将此异常包装在ReactiveException中。[处理错误](https://www.baeldung.com/spring-webflux-errors)时应牢记这种嵌套。

现在我们**将其应用于WebClient的实例以实现与onStatus链接调用相同的效果**：

```java
Mono<String> response = WebClient
    .builder()
    .filter(errorResponseFilter)
    .build()
    .post()
    .uri("some-resource")
    .retrieve()
    .bodyToMono(String.class);

// do something with response
```

## 5. 总结

在本文中，我们介绍了几种基于HTTP状态标头获取响应主体的方法。基于状态代码，onStatus方法允许我们插入特定的功能。此外，我们可以使用filter方法插入一个通用方法来处理所有响应的后处理。
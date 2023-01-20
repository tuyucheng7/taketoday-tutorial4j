## 1. 概述

在本快速教程中，我们将讨论Spring 5中引入的新ResponseStatusException类。此类支持将HTTP状态代码应用于HTTP响应。

RESTful应用程序可以通过在对客户端的响应中**返回正确的状态代码来传达HTTP请求的成功或失败**。简而言之，适当的状态代码可以帮助客户端识别应用程序在处理请求时可能发生的问题。

## 2. 响应状态

在我们深入研究ResponseStatusException之前，让我们快速看一下@ResponseStatus注解。这个注解是在Spring 3中引入的，用于将HTTP状态代码应用于HTTP响应。

我们可以使用@ResponseStatus注解来设置HTTP响应中的状态和原因：

```java
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Actor Not Found")
public class ActorNotFoundException extends Exception {
    // ...
}
```

如果在处理HTTP请求时抛出此异常，则响应将包含此注解中指定的HTTP状态。

@ResponseStatus方法的一个缺点是它与异常产生了紧密耦合。在我们的示例中，所有类型为ActorNotFoundException的异常都将在响应中生成相同的错误消息和状态代码。

## 3. 响应状态异常

ResponseStatusException是@ResponseStatus的编程替代方案，是用于将状态代码应用于HTTP响应的异常的基类。它是一个RuntimeException，因此不需要显式添加到方法签名中。

Spring提供了3个构造函数来生成ResponseStatusException：

```java
ResponseStatusException(HttpStatus status)
ResponseStatusException(HttpStatus status, java.lang.String reason)
ResponseStatusException(
  	HttpStatus status, 
  	java.lang.String reason, 
  	java.lang.Throwable cause
)
```

ResponseStatusException，构造函数参数：

-   status：设置为HTTP响应的HTTP状态
-   reason：解释HTTP响应异常集的消息
-   cause：ResponseStatusException的可抛出原因

注意：在Spring中，HandlerExceptionResolver拦截并处理任何引发但未由Controller处理的异常。

这些处理程序之一ResponseStatusExceptionResolver查找任何ResponseStatusException或@ResponseStatus注解的未捕获异常，然后提取HTTP状态代码和原因并将它们包含在HTTP响应中。

### 3.1 ResponseStatusException的好处

使用ResponseStatusException有几个好处：

-   首先，相同类型的异常可以单独处理，在响应上设置不同的状态码，降低紧耦合
-   其次，它避免了创建不必要的额外异常类
-   最后，它提供了对异常处理的更多控制，因为可以通过编程方式创建异常

## 4. 例子

### 4.1 生成ResponseStatusException

现在，让我们看一个生成ResponseStatusException的示例：

```java
@GetMapping("/actor/{id}")
public String getActorName(@PathVariable("id") int id) {
    try {
        return actorService.getActor(id);
    } catch (ActorNotFoundException ex) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Actor Not Found", ex);
    }
}
```

Spring Boot提供默认的/error映射，返回带有HTTP状态的JSON响应。

下面是响应的样子：

```http request
$ curl -i -s -X GET http://localhost:8081/actor/8
HTTP/1.1 404
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sat, 26 Dec 2020 19:38:09 GMT

{
    "timestamp": "2020-12-26T19:38:09.426+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "",
    "path": "/actor/8"
}
```

**从2.3版本开始，Spring Boot在默认错误页面上不再包含错误消息**，原因是为了降低向客户泄露信息的风险。

要更改默认行为，我们可以使用server.error.include-message属性。

让我们将它设置为always看看会发生什么：

```http request
$ curl -i -s -X GET http://localhost:8081/actor/8
HTTP/1.1 404
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sat, 26 Dec 2020 19:39:11 GMT

{
    "timestamp": "2020-12-26T19:39:11.426+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Actor Not Found",
    "path": "/actor/8"
}
```

正如我们所见，这一次，响应包含“Actor Not Found”错误消息。

### 4.2 不同的状态代码-相同的异常类型

现在，让我们看看在引发相同类型的异常时如何为HTTP响应设置不同的状态代码：

```java
@PutMapping("/actor/{id}/{name}")
public String updateActorName(@PathVariable("id") int id, @PathVariable("name") String name) {
    try {
        return actorService.updateActor(id, name);
    } catch (ActorNotFoundException ex) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide correct Actor Id", ex);
    }
}
```

下面是响应的样子：

```http request
$ curl -i -s -X PUT http://localhost:8081/actor/8/BradPitt
HTTP/1.1 400
...
{
    "timestamp": "2018-02-01T04:28:32.917+0000",
    "status": 400,
    "error": "Bad Request",
    "message": "Provide correct Actor Id",
    "path": "/actor/8/BradPitt"
}
```

## 5. 总结

在本快速教程中，我们讨论了如何在我们的程序中构造ResponseStatusException。

我们还强调了在HTTP响应中设置HTTP状态代码的编程方式比@ResponseStatus注解更好。
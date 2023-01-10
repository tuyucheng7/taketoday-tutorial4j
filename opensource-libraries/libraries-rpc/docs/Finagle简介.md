## 1. 概述

在本教程中，我们将快速了解 Finagle，Twitter 的 RPC 库。

我们将使用它来构建一个简单的客户端和服务器。

## 2.积木

在深入研究实现之前，我们需要了解将用于构建应用程序的基本概念。它们广为人知，但在 Finagle 的世界中可能具有略微不同的含义。

### 2.1. 服务

服务是由接受请求并返回包含操作的最终结果或有关失败信息的未来的类表示的函数。

### 2.2. 过滤器

过滤器也是函数。他们接受一个请求和一个服务，对请求做一些操作，将它传递给服务，对结果Future做一些操作，最后返回最终的Future。我们可以将它们视为[方面](https://www.baeldung.com/spring-aop)，因为它们可以实现围绕函数执行发生的逻辑并更改其输入和输出。

### 2.3. 期货

期货代表异步操作的最终结果。它们可能处于以下三种状态之一：挂起、成功或失败。

## 3.服务

首先，我们将实现一个简单的 HTTP 问候服务。它将从请求中获取名称参数并响应并添加习惯的“Hello”消息。

为此，我们需要创建一个类来扩展Finagle 库中的抽象服务类，实现其 应用方法。

我们所做的看起来类似于实现[功能接口](https://www.baeldung.com/java-8-functional-interfaces)。但有趣的是，我们实际上无法使用该特定功能，因为 Finagle 是用 Scala 编写的，而我们正在利用 Java-Scala 互操作性：

```java
public class GreetingService extends Service<Request, Response> {
    @Override
    public Future<Response> apply(Request request) {
        String greeting = "Hello " + request.getParam("name");
        Reader<Buf> reader = Reader.fromBuf(new Buf.ByteArray(greeting.getBytes(), 0, greeting.length()));
        return Future.value(Response.apply(request.version(), Status.Ok(), reader));
    }
}
```

## 4.过滤

接下来，我们将编写一个过滤器，将有关请求的一些数据记录到控制台。与 Service类似，我们需要实现Filter的apply方法，该方法将接受请求并返回Future响应，但这次它还将服务作为第二个参数。

基本的[Filter](https://twitter.github.io/finagle/docs/com/twitter/finagle/Filter.html) 类有四个类型参数，但通常我们不需要更改过滤器内的请求和响应的类型。

为此，我们将使用[SimpleFilter](https://twitter.github.io/finagle/docs/com/twitter/finagle/SimpleFilter.html)将四个类型参数合并为两个。我们将从请求中打印一些信息，然后简单地从提供的服务中调用apply方法：

```java
public class LogFilter extends SimpleFilter<Request, Response> {
    @Override
    public Future apply(Request request, Service<Request, Response> service) {
        logger.info("Request host:" + request.host().getOrElse(() -> ""));
        logger.info("Request params:");
        request.getParams().forEach(entry -> logger.info("t" + entry.getKey() + " : " + entry.getValue()));
        return service.apply(request);
    }
}

```

## 5.服务器

现在我们可以使用该服务和过滤器来构建一个服务器，该服务器将实际侦听请求并处理它们。

我们将为该服务器提供一个服务，该服务包含我们的过滤器和与andThen方法链接在一起的服务：

```java
Service serverService = new LogFilter().andThen(new GreetingService()); 
Http.serve(":8080", serverService);
```

## 6.客户

最后，我们需要一个客户端向我们的服务器发送请求。

为此，我们将使用Finagle 的[Http](https://twitter.github.io/finagle/docs/com/twitter/finagle/Http$.html)类中方便的newService方法创建一个 HTTP 服务。它将直接负责发送请求。

此外，我们将使用之前实现的相同日志过滤器并将其与 HTTP 服务链接。然后，我们只需要调用apply方法。

最后一个操作是异步的，其最终结果存储在Future实例中。我们可以等待这个Future成功或失败，但这将是一个阻塞操作，我们可能希望避免它。相反，我们可以实现一个在Future成功时触发的回调：

```java
Service<Request, Response> clientService = new LogFilter().andThen(Http.newService(":8080"));
Request request = Request.apply(Method.Get(), "/?name=John");
request.host("localhost");
Future<Response> response = clientService.apply(request);

Await.result(response
        .onSuccess(r -> {
            assertEquals("Hello John", r.getContentString());
            return BoxedUnit.UNIT;
        })
        .onFailure(r -> {
            throw new RuntimeException(r);
        })
);
```

请注意，我们返回BoxedUnit.UNIT。返回[Unit](https://www.scala-lang.org/api/current/scala/Unit.html) 是 Scala 处理void方法的方式，所以我们在这里这样做是为了保持互操作性。

## 七. 总结

在本教程中，我们学习了如何使用 Finagle 构建一个简单的 HTTP 服务器和客户端，以及如何在它们之间建立通信和交换消息。
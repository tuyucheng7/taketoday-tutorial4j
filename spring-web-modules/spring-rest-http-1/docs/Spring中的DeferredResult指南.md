## 1. 概述

在本教程中，我们将了解如何使用Spring MVC中的DeferredResult类来执行异步请求处理。

在Servlet 3.0中引入了异步支持，简单地说，它允许在请求接收者线程之外的另一个线程中处理HTTP请求。

从Spring 3.2开始提供的DeferredResult有助于将长时间运行的计算从http-worker线程卸载到单独的线程。

尽管另一个线程会占用一些资源进行计算，但工作线程在此期间不会被阻塞，并且可以处理传入的客户端请求。

异步请求处理模型非常有用，因为它有助于在高负载期间很好地扩展应用程序，尤其是对于IO密集型操作。

## 2. 设置

对于我们的示例，我们将使用Spring Boot应用程序。有关如何引导应用程序的更多详细信息，请参阅我们之前的[文章](https://www.baeldung.com/spring-boot-start)。

接下来，我们将使用DeferredResult演示同步和异步通信，并比较异步通信如何更好地适应高负载和IO密集型用例。

## 3. 阻塞REST服务

让我们从开发标准的阻塞式REST服务开始：

```java
@GetMapping("/process-blocking")
public ResponseEntity<?> handleReqSync(Model model) { 
    // ...
    return ResponseEntity.ok("ok");
}
```

这里的问题是请求处理线程被阻塞，直到处理完完整的请求并返回结果。在长时间运行的计算的情况下，这是一个次优的解决方案。

为了解决这个问题，我们可以更好地利用容器线程来处理客户端请求，我们将在下一节中看到。

## 4. 使用DeferredResult的非阻塞REST

为避免阻塞，我们将使用基于回调的编程模型，而不是实际结果，我们将向Servlet容器返回一个DeferredResult。

```java
@GetMapping("/async-deferredresult")
public DeferredResult<ResponseEntity<?>> handleReqDefResult(Model model) {
    LOG.info("Received async-deferredresult request");
    DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
    
    ForkJoinPool.commonPool().submit(() -> {
        LOG.info("Processing in separate thread");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
        }
        output.setResult(ResponseEntity.ok("ok"));
    });
    
    LOG.info("servlet thread freed");
    return output;
}
```

请求处理是在一个单独的线程中完成的，一旦完成，我们就会在DeferredResult对象上调用setResult操作。

让我们查看日志输出以检查我们的线程是否按预期运行：

```bash
[nio-8080-exec-6] cn.tuyucheng.taketoday.controller.AsyncDeferredResultController: 
Received async-deferredresult request
[nio-8080-exec-6] cn.tuyucheng.taketoday.controller.AsyncDeferredResultController: 
Servlet thread freed
[nio-8080-exec-6] java.lang.Thread : Processing in separate thread
```

在内部，通知容器线程并将HTTP响应传递给客户端。连接将由容器(Servlet 3.0或更高版本)保持打开状态，直到响应到达或超时。

## 5. DeferredResult回调

我们可以使用DeferredResult注册3种类型的回调：完成、超时和错误回调。

让我们使用onCompletion()方法来定义在异步请求完成时执行的代码块：

```java
deferredResult.onCompletion(() -> LOG.info("Processing complete"));
```

同样，我们可以使用onTimeout()来注册自定义代码，以便在发生超时时调用。为了限制请求处理时间，我们可以在创建DeferredResult对象时传递一个超时值：

```java
DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(500L);

deferredResult.onTimeout(() -> 
  deferredResult.setErrorResult(
    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
      .body("Request timeout occurred.")));
```

在超时的情况下，我们通过在DeferredResult注册的超时处理程序设置不同的响应状态。

让我们通过处理超过定义的5秒超时值的请求来触发超时错误：

```java
ForkJoinPool.commonPool().submit(() -> {
    LOG.info("Processing in separate thread");
    try {
        Thread.sleep(6000);
    } catch (InterruptedException e) {
        // ...
    }
    deferredResult.setResult(ResponseEntity.ok("OK")));
});
```

让我们看看日志：

```shell
[nio-8080-exec-6] cn.tuyucheng.taketoday.controller.DeferredResultController: 
servlet thread freed
[nio-8080-exec-6] java.lang.Thread: Processing in separate thread
[nio-8080-exec-6] cn.tuyucheng.taketoday.controller.DeferredResultController: 
Request timeout occurred
```

在某些情况下，长时间运行的计算会因某些错误或异常而失败。在这种情况下，我们还可以注册一个onError()回调：

```java
deferredResult.onError((Throwable t) -> {
    deferredResult.setErrorResult(
      ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An error occurred."));
});
```

如果出现错误，在计算响应时，我们将通过此错误处理程序设置不同的响应状态和消息正文。

## 6. 总结

在这篇快速文章中，我们了解了Spring MVC DeferredResult如何简化异步端点的创建。
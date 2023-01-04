## 1. 简介

这个简单的教程演示了Spring MVC 5.xx中几个异步和流对象的使用

具体来说，我们将回顾三个关键类：

-   ResponseBodyEmitter
-   SseEmitter
-   StreamingResponseBody

此外，我们还将讨论如何使用JavaScript客户端与它们交互。

## 2. ResponseBodyEmitter

ResponseBodyEmitter处理异步响应。

此外，它代表许多子类的父类——我们将在下面仔细研究其中的一个。

### 2.1 服务器端

最好使用ResponseBodyEmitter及其自己的专用异步线程，并用ResponseEntity包装(我们可以直接将发射器注入其中)：

```java
@Controller
public class ResponseBodyEmitterController {

    private ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping("/rbe")
    public ResponseEntity<ResponseBodyEmitter> handleRbe() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        executor.execute(() -> {
            try {
                emitter.send(
                      "/rbe" + " @ " + new Date(), MediaType.TEXT_PLAIN);
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return new ResponseEntity(emitter, HttpStatus.OK);
    }
}
```

因此，在上面的示例中，我们可以避免使用CompletableFutures、更复杂的异步promises或使用@Async注解。

相反，我们只需声明我们的异步实体并将其包装在ExecutorService提供的新线程中。

### 2.2 客户端

对于客户端使用，我们可以使用一个简单的XHR方法并像在通常的AJAX操作中一样调用我们的API端点：

```javascript
var xhr = function(url) {
    return new Promise(function(resolve, reject) {
        var xmhr = new XMLHttpRequest();
        //...
        xmhr.open("GET", url, true);
        xmhr.send();
       //...
    });
};

xhr('http://localhost:8080/javamvcasync/rbe')
    .then(function(success){ ... });
```

## 3. SseEmitter

SseEmitter实际上是ResponseBodyEmitter的子类，并提供额外的服务器发送事件(SSE)开箱即用支持。

### 3.1 服务器端

那么，让我们快速看一下利用这个强大实体的示例控制器：

```java
@Controller
public class SseEmitterController {
    private ExecutorService nonBlockingService = Executors
          .newCachedThreadPool();

    @GetMapping("/sse")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter();
        nonBlockingService.execute(() -> {
            try {
                emitter.send("/sse" + " @ " + new Date());
                // we could send more events
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }
}
```

相当标准的票价，但我们会注意到这个和我们通常的REST控制器之间的一些差异：

-   首先，我们返回一个SseEmitter
-   另外，我们将核心响应信息包装在自己的Thread中
-   最后，我们使用emitter.send()发送响应信息

### 3.2 客户端

这次我们的客户端工作方式有点不同，因为我们可以利用持续连接的服务器发送事件库：

```javascript
var sse = new EventSource('http://localhost:8080/javamvcasync/sse');
sse.onmessage = function (evt) {
    var el = document.getElementById('sse');
    el.appendChild(document.createTextNode(evt.data));
    el.appendChild(document.createElement('br'));
};
```

## 4. StreamingResponseBody

最后，我们可以使用StreamingResponseBody直接写入OutputStream，然后再使用ResponseEntity将写入的信息传回客户端。

### 4.1 服务器端

```java
@Controller
public class StreamingResponseBodyController {

    @GetMapping("/srb")
    public ResponseEntity<StreamingResponseBody> handleRbe() {
        StreamingResponseBody stream = out -> {
            String msg = "/srb" + " @ " + new Date();
            out.write(msg.getBytes());
        };
        return new ResponseEntity(stream, HttpStatus.OK);
    }
}
```

### 4.2 客户端

就像之前一样，我们将使用常规的XHR方法来访问上面的控制器：

```javascript
var xhr = function(url) {
    return new Promise(function(resolve, reject) {
        var xmhr = new XMLHttpRequest();
        //...
        xmhr.open("GET", url, true);
        xmhr.send();
        //...
    });
};

xhr('http://localhost:8080/javamvcasync/srb')
  .then(function(success){ ... });
```

接下来，让我们看看这些例子的一些成功使用。

## 5. 综合考虑

在我们成功编译服务器并运行上面的客户端(访问提供的index.jsp)之后，我们应该在浏览器中看到以下内容：

<img src="../assets/img.png">

以及我们终端中的以下内容：
 
<img src="../assets/img_1.png">

我们还可以直接调用端点并看到它们的流式响应出现在我们的浏览器中。

## 6. 总结

虽然Future和CompletableFuture已被证明是对Java和Spring的强大补充，但我们现在可以使用多种资源来更充分地处理高并发Web应用程序的异步和流数据。
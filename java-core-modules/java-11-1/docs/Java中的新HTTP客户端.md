## 1. 概述

在本教程中，我们将探讨Java 11的HTTP客户端API标准化，**该API实现了HTTP/2和WebSocket**。

它旨在替换自Java早期以来就存在于JDK中的遗留[HttpUrlConnection](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/HttpURLConnection.html)类。

直到最近，Java还只提供了HttpURLConnection API，它是低级的，并不以功能丰富和用户友好而著称。

因此，常用一些广泛使用的第三方库，如[Apache HttpClient](https://hc.apache.org/httpcomponents-client-ga/)、[Jetty](https://www.eclipse.org/jetty/documentation/current/http-client-api.html)和Spring的[RestTemplate](https://www.baeldung.com/rest-template)。

## 2. 背景

该更改已作为JEP 321的一部分实施。

### 2.1 作为JEP 321一部分的重大变化

1.  Java 9中孵化的HTTP API现在正式并入Java SE API。新的[HTTP API](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/package-summary.html)可以在**java.net.http.***中找到
2.  较新版本的HTTP协议旨在提高客户端发送请求和接收服务器响应的整体性能。这是通过引入许多变化来实现的，例如流多路复用、标头压缩和推送承诺。
3.  从Java 11开始，**API现在是完全异步的(之前的HTTP/1.1实现是阻塞的)**，异步调用是使用CompletableFuture实现的。CompletableFuture实现负责在前一个阶段完成后应用每个阶段，因此整个流程是异步的。
4.  新的HTTP客户端API提供了一种执行HTTP网络操作的标准方法，并支持现代Web功能(例如HTTP/2)，而无需添加第三方依赖项。
5.  新的API为HTTP1.1/2 WebSocket提供原生支持。提供核心功能的核心类和接口包括：

-   HttpClient类，java.net.http.HttpClient
-   HttpRequest类，java.net.http.HttpRequest
-   HttpResponse<T\>接口，java.net.http.HttpResponse
-   WebSocket接口，java.net.http.WebSocket

### 2.2 Java 11之前的HTTP客户端的问题

现有的HttpURLConnection API及其实现存在许多问题：

-   URLConnection API设计有多种协议，现在不再起作用(FTP、gopher等)
-   该API早于HTTP/1.1，过于抽象
-   它仅在阻塞模式下工作(即，每个请求/响应一个线程)
-   很难维护

## 3. HTTP客户端API概述

与HttpURLConnection不同，HTTP Client提供同步和异步请求机制。

API由三个核心类组成：

-   HttpRequest表示要通过HttpClient发送的请求
-   HttpClient充当多个请求共有的配置信息的容器
-   HttpResponse表示HttpRequest调用的结果

我们将在以下各节中更详细地检查它们中的每一个。首先，让我们关注一个请求。

## 4. HttpRequest

HttpRequest是一个对象，代表我们要发送的请求。可以使用HttpRequest.Builder创建新实例。

我们可以通过调用HttpRequest.newBuilder()来获取它，Builder类提供了一堆我们可以用来配置我们的请求的方法。

我们将介绍最重要的。

注意：在JDK 16中，有一个新的HttpRequest.newBuilder(HttpRequest request, BiPredicate<String,String\> filter)方法，该方法创建一个Builder，其初始状态是从现有的HttpRequest复制的。

此构建器可用于构建HttpRequest，与原始构建器等效，同时允许在构建之前修改请求状态，例如，删除标头：

```java
HttpRequest.newBuilder(request, (name, value) -> !name.equalsIgnoreCase("Foo-Bar"))
```

### 4.1 设置URI

创建请求时，我们要做的第一件事就是提供URL。

我们可以通过两种方式做到这一点-使用带有URI参数的Builder的构造函数或在Builder实例上调用方法uri(URI)：

```java
HttpRequest.newBuilder(new URI("https://postman-echo.com/get"))
 
HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/get"))
```

我们必须配置以创建基本请求的最后一件事是HTTP方法。

### 4.2 指定HTTP方法

我们可以通过调用Builder中的一种方法来定义我们的请求将使用的HTTP方法：

-   GET()
-   POST(BodyPublisher body)
-   PUT(BodyPublisher body)
-   DELETE()

稍后我们将详细介绍BodyPublisher。

现在让我们创建一个**非常简单的GET请求示例**：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/get"))
    .GET()
    .build();
```

此请求具有HttpClient所需的所有参数。

但是，我们有时需要向我们的请求添加额外的参数。以下是一些重要的：

-  HTTP协议的版本
-  标头
-  超时

### 4.3 设置HTTP协议版本

API充分利用HTTP/2协议并默认使用它，但我们可以定义我们想要使用的协议版本：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/get"))
    .version(HttpClient.Version.HTTP_2)
    .GET()
    .build();
```

**这里要提到的重要一点是，如果不支持HTTP/2，客户端将回退到例如HTTP/1.1**。

### 4.4 设置标头

如果我们想向我们的请求添加额外的标头，我们可以使用提供的构建器方法。

我们可以通过将所有标头作为键值对传递给headers()方法或对单个键值标头使用header()方法来做到这一点：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/get"))
    .headers("key1", "value1", "key2", "value2")
    .GET()
    .build();

HttpRequest request2 = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/get"))
    .header("key1", "value1")
    .header("key2", "value2")
    .GET()
    .build();
```

我们可以用来自定义请求的最后一个有用的方法是timeout()。

### 4.5 设置超时

现在让我们定义我们想要等待响应的时间量。

如果设置的时间到期，将抛出HttpTimeoutException。默认超时设置为无穷大。

可以通过在构建器实例上调用方法timeout()来使用Duration对象设置超时：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/get"))
    .timeout(Duration.of(10, SECONDS))
    .GET()
    .build();
```

## 5. 设置请求正文

我们可以使用请求构建器方法向请求添加主体：POST(BodyPublisher body)、PUT(BodyPublisher body)和DELETE()。

新的API提供了许多开箱即用的BodyPublisher实现，可简化请求正文的传递：

-   StringProcessor：从使用HttpRequest.BodyPublishers.ofString创建的String中读取正文
-   InputStreamProcessor：从使用HttpRequest.BodyPublishers.ofInputStream创建的InputStream中读取正文
-   ByteArrayProcessor：从使用HttpRequest.BodyPublishers.ofByteArray创建的字节数组中读取正文
-   FileProcessor：从给定路径的文件中读取正文，由HttpRequest.BodyPublishers.ofFile创建

如果我们不需要正文，我们可以简单地传入一个HttpRequest.BodyPublishers.noBody()：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/post"))
    .POST(HttpRequest.BodyPublishers.noBody())
    .build();
```

注意：在JDK 16中，有一个新的HttpRequest.BodyPublishers.concat(BodyPublisher...)方法可以帮助我们从一系列发布者发布的请求主体的串联中构建请求主体。串联发布者发布的请求正文在逻辑上等同于通过按顺序连接每个发布者的所有字节来发布的请求正文。

### 5.1 StringBodyPublisher

使用任何BodyPublishers实现设置请求正文非常简单和直观。

例如，如果我们想传递一个简单的字符串作为正文，我们可以使用StringBodyPublishers。

正如我们已经提到的，可以使用工厂方法ofString()创建这个对象-它只接收一个String对象作为参数并从中创建一个主体：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/post"))
    .headers("Content-Type", "text/plain;charset=UTF-8")
    .POST(HttpRequest.BodyPublishers.ofString("Sample request body"))
    .build();
```

### 5.2 InputStreamBodyPublisher

为此，必须将InputStream作为Supplier传递(以使其创建变得惰性)，因此它与StringBodyPublishers有点不同。

但是，这也很简单：

```java
byte[] sampleData = "Sample request body".getBytes();
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/post"))
    .headers("Content-Type", "text/plain;charset=UTF-8")
    .POST(HttpRequest.BodyPublishers
        .ofInputStream(() -> new ByteArrayInputStream(sampleData)))
    .build();
```

请注意我们在这里如何使用简单的ByteArrayInputStream。当然，这可以是任何InputStream实现。

### 5.3 ByteArrayProcessor

我们还可以使用ByteArrayProcessor并传递一个字节数组作为参数：

```java
byte[] sampleData = "Sample request body".getBytes();
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/post"))
    .headers("Content-Type", "text/plain;charset=UTF-8")
    .POST(HttpRequest.BodyPublishers.ofByteArray(sampleData))
    .build();
```

### 5.4 FileProcessor

要使用文件，我们可以使用提供的FileProcessor。

它的工厂方法将文件的路径作为参数，并根据内容创建一个主体：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/post"))
    .headers("Content-Type", "text/plain;charset=UTF-8")
    .POST(HttpRequest.BodyPublishers.fromFile(Paths.get("src/test/resources/sample.txt")))
    .build();
```

我们已经介绍了如何创建HttpRequest以及如何在其中设置其他参数。

现在是时候深入了解HttpClient类了，它负责发送请求和接收响应。

## 6. HttpClient

所有请求都使用HttpClient发送，可以使用HttpClient.newBuilder()方法或调用HttpClient.newHttpClient()进行实例化。

它提供了许多有用的自描述方法，我们可以用它来处理我们的请求/响应。

让我们在这里介绍其中的一些。

### 6.1 处理响应正文

与创建发布者的流式方法类似，也有专门为常见主体类型创建处理程序的方法：

```java
BodyHandlers.ofByteArray
BodyHandlers.ofString
BodyHandlers.ofFile
BodyHandlers.discarding
BodyHandlers.replacing
BodyHandlers.ofLines
BodyHandlers.fromLineSubscriber
```

注意新的BodyHandlers工厂类的使用。

在Java 11之前，我们必须这样做：

```java
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandler.asString());
```

我们现在可以简化它：

```java
HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
```

### 6.2 设置代理

我们可以通过在Builder实例上调用proxy()方法来为连接定义一个代理：

```java
HttpResponse<String> response = HttpClient
    .newBuilder()
    .proxy(ProxySelector.getDefault())
    .build()
    .send(request, BodyHandlers.ofString());
```

在我们的示例中，我们使用了默认系统代理。

### 6.3 设置重定向策略

有时我们想要访问的页面已经移动到不同的地址。

在这种情况下，我们将收到HTTP状态代码3xx，通常带有有关新URI的信息。**如果我们设置了适当的重定向策略，HttpClient可以自动将请求重定向到新的URI**。

我们可以使用Builder上的followRedirects()方法来做到这一点：

```java
HttpResponse<String> response = HttpClient.newBuilder()
    .followRedirects(HttpClient.Redirect.ALWAYS)
    .build()
    .send(request, BodyHandlers.ofString());
```

所有策略都在枚举HttpClient.Redirect中定义和描述。

### 6.4 为连接设置身份验证器

Authenticator是一个为连接协商凭据(HTTP身份验证)的对象。

它提供了不同的身份验证方案(例如基本身份验证或摘要式身份验证)。

在大多数情况下，身份验证需要用户名和密码才能连接到服务器。

我们可以使用PasswordAuthentication类，它只是这些值的持有者：

```java
HttpResponse<String> response = HttpClient.newBuilder()
    .authenticator(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(
            "username", 
            "password".toCharArray());
        }
}).build()
    .send(request, BodyHandlers.ofString());
```

在这里，我们将用户名和密码值作为明文传递。当然，这在生产场景中必须有所不同。

请注意，并非每个请求都应使用相同的用户名和密码。Authenticator类提供了许多getXXX(例如getRequestingSite())方法，可用于找出应该提供哪些值。

现在我们将探讨新的HttpClient最有用的特性之一-对服务器的异步调用。

### 6.5 发送请求-同步与异步

新的HttpClient提供了两种向服务器发送请求的可能性：

-   **send(...)：同步**(阻塞直到响应到来)
-   **sendAsync(...)：异步**(不等待响应，非阻塞)

到目前为止，send(...)方法自然会等待响应：

```java
HttpResponse<String> response = HttpClient.newBuilder()
    .build()
    .send(request, BodyHandlers.ofString());
```

此调用返回一个HttpResponse对象，并且我们确信来自应用程序流的下一条指令将仅在响应已经存在时运行。

但是，它有很多缺点，尤其是当我们处理大量数据时。

因此，**现在我们可以使用sendAsync(...)方法(它返回CompletableFeature<HttpResponse\>)来异步处理请求**：

```java
CompletableFuture<HttpResponse<String>> response = HttpClient.newBuilder()
    .build()
    .sendAsync(request, HttpResponse.BodyHandlers.ofString());
```

新的API还可以处理多个响应，并流式传输请求和响应主体：

```java
List<URI> targets = Arrays.asList(
    new URI("https://postman-echo.com/get?foo1=bar1"),
    new URI("https://postman-echo.com/get?foo2=bar2"));
HttpClient client = HttpClient.newHttpClient();
List<CompletableFuture<String>> futures = targets.stream()
    .map(target -> client
        .sendAsync(
            HttpRequest.newBuilder(target).GET().build(),
            HttpResponse.BodyHandlers.ofString())
        .thenApply(response -> response.body()))
    .collect(Collectors.toList());
```

### 6.6 为异步调用设置执行器

我们还可以定义一个Executor来提供异步调用使用的线程。

例如，通过这种方式，我们可以限制用于处理请求的线程数：

```java
ExecutorService executorService = Executors.newFixedThreadPool(2);

CompletableFuture<HttpResponse<String>> response1 = HttpClient.newBuilder()
    .executor(executorService)
    .build()
    .sendAsync(request, HttpResponse.BodyHandlers.ofString());

CompletableFuture<HttpResponse<String>> response2 = HttpClient.newBuilder()
    .executor(executorService)
    .build()
    .sendAsync(request, HttpResponse.BodyHandlers.ofString());
```

默认情况下，HttpClient使用执行器java.util.concurrent.Executors.newCachedThreadPool()。

### 6.7 定义CookieHandler

使用新的API和构建器，为我们的连接设置一个CookieHandler很简单。我们可以使用构建器方法cookieHandler(CookieHandler cookieHandler)来定义特定于客户端的CookieHandler。

让我们定义一个根本不允许接受cookie的CookieManager(CookieHandler的具体实现，它将cookie的存储与接受和拒绝cookie的策略分开)：

```java
HttpClient.newBuilder()
    .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_NONE))
    .build();
```

如果我们的CookieManager允许存储cookie，我们可以通过检查HttpClient中的CookieHandler来访问它们：

```java
((CookieManager) httpClient.cookieHandler().get()).getCookieStore()
```

现在让我们关注Http API的最后一个类-HttpResponse。

## 7. HttpResponse对象

HttpResponse类表示来自服务器的响应。它提供了许多有用的方法，但这是最重要的两个：

-   statusCode()返回响应的状态代码(类型int)(HttpURLConnection类包含可能的值)。
-   body()返回响应的主体(返回类型取决于传递给send()方法的响应BodyHandler参数)。

响应对象还有我们将介绍的其他有用方法，例如uri()、headers()、trails()和version()。

### 7.1 响应对象的URI

响应对象上的uri()方法返回我们从中接收响应的URI。

有时它可能与请求对象中的URI不同，因为可能会发生重定向：

```java
assertThat(request.uri()
    .toString(), equalTo("http://stackoverflow.com"));
assertThat(response.uri()
    .toString(), equalTo("https://stackoverflow.com/"));
```

### 7.2 响应的标头

我们可以通过在响应对象上调用方法headers()来从响应中获取标头：

```java
HttpResponse<String> response = HttpClient.newHttpClient()
    .send(request, HttpResponse.BodyHandlers.ofString());
HttpHeaders responseHeaders = response.headers();
```

它返回HttpHeaders对象，该对象表示HTTP标头的只读视图。

它有一些有用的方法可以简化标头值的搜索。

### 7.3 响应版本

方法version()定义了用于与服务器通信的HTTP协议版本。

**请记住，即使我们定义要使用HTTP/2，服务器也可以通过HTTP/1.1进行响应**。

服务器响应的版本在响应中指定：

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI("https://postman-echo.com/get"))
    .version(HttpClient.Version.HTTP_2)
    .GET()
    .build();
HttpResponse<String> response = HttpClient.newHttpClient()
    .send(request, HttpResponse.BodyHandlers.ofString());
assertThat(response.version(), equalTo(HttpClient.Version.HTTP_1_1));
```

## 8. 在HTTP/2中处理推送承诺

新的HttpClient通过PushPromiseHandler接口支持推送承诺。

**它允许服务器在请求主要资源时将内容“推送”到客户端附加资源，从而节省更多往返时间，从而提高页面渲染性能**。

真正让我们忘记资源捆绑的正是HTTP/2的多路复用特性。对于每个资源，服务器都会向客户端发送一个特殊请求，称为推送承诺。

收到的推送承诺(如果有)由给定的PushPromiseHandler处理。值为null的PushPromiseHandler拒绝任何推送承诺。

HttpClient有一个重载的**sendAsync**方法，它允许我们处理此类承诺，如下所示。

让我们首先创建一个PushPromiseHandler：

```java
private static PushPromiseHandler<String> pushPromiseHandler() {
    return (HttpRequest initiatingRequest, 
        HttpRequest pushPromiseRequest, 
        Function<HttpResponse.BodyHandler<String>, 
        CompletableFuture<HttpResponse<String>>> acceptor) -> {
        acceptor.apply(BodyHandlers.ofString())
            .thenAccept(resp -> {
                System.out.println(" Pushed response: " + resp.uri() + ", headers: " + resp.headers());
            });
        System.out.println("Promise request: " + pushPromiseRequest.uri());
        System.out.println("Promise request: " + pushPromiseRequest.headers());
    };
}
```

接下来，让我们使用sendAsync方法来处理这个推送承诺：

```java
httpClient.sendAsync(pageRequest, BodyHandlers.ofString(), pushPromiseHandler())
    .thenAccept(pageResponse -> {
        System.out.println("Page response status code: " + pageResponse.statusCode());
        System.out.println("Page response headers: " + pageResponse.headers());
        String responseBody = pageResponse.body();
        System.out.println(responseBody);
    })
    .join();

```

## 9. 总结

在本文中，我们探讨了Java 11 HttpClient API，它标准化了Java 9中引入的孵化HttpClient，并进行了更强大的更改。

在示例中，我们使用了https://postman-echo.com提供的示例REST端点。
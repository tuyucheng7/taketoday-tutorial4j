## 1. 简介

[NanoHTTPD](https://github.com/NanoHttpd/nanohttpd)是一个用Java编写的开源、轻量级 Web 服务器。

在本教程中，我们将创建一些 REST API 来探索其功能。

## 2.项目设置

让我们将[NanoHTTPD 核心依赖](https://search.maven.org/search?q=a:nanohttpd AND g:org.nanohttpd)项添加 到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.nanohttpd</groupId>
    <artifactId>nanohttpd</artifactId>
    <version>2.3.1</version>
</dependency>
```

要创建一个简单的服务器，我们需要扩展NanoHTTPD并覆盖其服务方法：

```java
public class App extends NanoHTTPD {
    public App() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static void main(String[] args ) throws IOException {
        new App();
    }

    @Override
    public Response serve(IHTTPSession session) {
        return newFixedLengthResponse("Hello world");
    }
}
```

我们将运行端口定义为8080，并将服务器定义为守护进程(无读取超时)。

一旦我们启动应用程序，URL [http://localhost:8080/](http://localhost:8080/)将返回Hello world消息。我们使用NanoHTTPD#newFixedLengthResponse方法作为构建NanoHTTPD.Response对象的便捷方式。

[让我们用cURL](https://www.baeldung.com/curl-rest)试试我们的项目：

```bash
> curl 'http://localhost:8080/'
Hello world
```

## 3. 休息API

在 HTTP 方法方面，NanoHTTPD 允许 GET、POST、PUT、DELETE、HEAD、TRACE 和其他几种方法。

简单地说，我们可以通过枚举方法找到支持的 HTTP 动词。让我们看看结果如何。

### 3.1. HTTP 获取

首先，让我们看一下 GET。比方说，我们只想在应用程序收到 GET 请求时返回内容。

与[Java Servlet 容器](https://www.baeldung.com/intro-to-servlets)不同，我们没有可用的doGet 方法——相反，我们只是通过getMethod检查值：

```java
@Override
public Response serve(IHTTPSession session) {
    if (session.getMethod() == Method.GET) {
        String itemIdRequestParameter = session.getParameters().get("itemId").get(0);
        return newFixedLengthResponse("Requested itemId = " + itemIdRequestParameter);
    }
    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, 
        "The requested resource does not exist");
}
```

那很简单，对吧？让我们通过卷曲我们的新端点来运行一个快速测试，看看请求参数itemId是否被正确读取：

```bash
> curl 'http://localhost:8080/?itemId=23Bk8'
Requested itemId = 23Bk8
```

### 3.2. HTTP POST

我们之前对 GET 做出反应并从 URL 中读取参数。

为了涵盖两种最流行的 HTTP 方法，现在是我们处理 POST(从而读取请求正文)的时候了：

```java
@Override
public Response serve(IHTTPSession session) {
    if (session.getMethod() == Method.POST) {
        try {
            session.parseBody(new HashMap<>());
            String requestBody = session.getQueryParameterString();
            return newFixedLengthResponse("Request body = " + requestBody);
        } catch (IOException | ResponseException e) {
            // handle
        }
    }
    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, 
      "The requested resource does not exist");
}
```

请注意，在我们请求请求主体之前，我们首先调用了parseBody方法。那是因为我们想加载请求主体以供以后检索。

我们将在我们的cURL命令中包含一个主体：

```bash
> curl -X POST -d 'deliveryAddress=Washington nr 4&quantity=5''http://localhost:8080/'
Request body = deliveryAddress=Washington nr 4&quantity=5
```

其余的 HTTP 方法在本质上非常相似，因此我们将跳过它们。

## 4.跨域资源共享

使用[CORS](https://www.baeldung.com/spring-cors)，我们启用跨域通信。最常见的用例是来自不同域的 AJAX 调用。

 

我们可以使用的第一种方法是为所有 API 启用 CORS。使用-cors参数，我们将允许访问所有域。我们还可以使用–cors=”http://dashboard.myApp.com http://admin.myapp.com”定义我们允许的域。

 

第二种方法是为各个 API 启用 CORS。让我们看看如何使用addHeader来实现这一点：

```java
@Override 
public Response serve(IHTTPSession session) {
    Response response = newFixedLengthResponse("Hello world"); 
    response.addHeader("Access-Control-Allow-Origin", "");
    return response;
}
```

现在当我们cURL时，我们将得到我们的 CORS 标头：

```bash
> curl -v 'http://localhost:8080'
HTTP/1.1 200 OK 
Content-Type: text/html
Date: Thu, 13 Jun 2019 03:58:14 GMT
Access-Control-Allow-Origin: 
Connection: keep-alive
Content-Length: 11

Hello world
```

## 5.文件上传

NanoHTTPD 有一个单独[的文件上传依赖](https://search.maven.org/search?q=a:nanohttpd-apache-fileupload)，所以让我们把它添加到我们的项目中：

```xml
<dependency>
    <groupId>org.nanohttpd</groupId>
    <artifactId>nanohttpd-apache-fileupload</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>
```

请注意， 还需要[servlet-api依赖](https://search.maven.org/search?q=a:javax.servlet-api AND g:javax.servlet)项(否则会出现编译错误)。

NanoHTTPD 公开的是一个名为NanoFileUpload的类：

```java
@Override
public Response serve(IHTTPSession session) {
    try {
        List<FileItem> files
          = new NanoFileUpload(new DiskFileItemFactory()).parseRequest(session);
        int uploadedCount = 0;
        for (FileItem file : files) {
            try {
                String fileName = file.getName(); 
                byte[] fileContent = file.get(); 
                Files.write(Paths.get(fileName), fileContent);
                uploadedCount++;
            } catch (Exception exception) {
                // handle
            }
        }
        return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, 
          "Uploaded files " + uploadedCount + " out of " + files.size());
    } catch (IOException | FileUploadException e) {
        throw new IllegalArgumentException("Could not handle files from API request", e);
    }
    return newFixedLengthResponse(
      Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Error when uploading");
}
```

嘿，让我们试试看：

```bash
> curl -F 'filename=@/pathToFile.txt' 'http://localhost:8080'
Uploaded files: 1
```

## 6.多条路线

nanolet类似于 servlet，但外形非常低调。我们可以使用它们来定义由单个服务器提供服务的许多路由(与之前使用一个路由的示例不同)。

[首先，让我们为nanolets](https://search.maven.org/search?q=a:nanohttpd-nanolets)添加所需的[依赖](https://search.maven.org/search?q=a:nanohttpd-nanolets)项：

```xml
<dependency>
    <groupId>org.nanohttpd</groupId>
    <artifactId>nanohttpd-nanolets</artifactId>
    <version>2.3.1</version>
</dependency>
```

现在我们将使用 RouterNanoHTTPD 扩展我们的主类，定义我们的运行端口并让服务器作为守护进程运行。

addMappings方法是我们定义处理程序的地方：

```java
public class MultipleRoutesExample extends RouterNanoHTTPD {
    public MultipleRoutesExample() throws IOException {
        super(8080);
        addMappings();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }
 
    @Override
    public void addMappings() {
        // todo fill in the routes
    }
}
```

下一步是定义我们的addMappings方法。让我们定义一些处理程序。 

第一个是 “/”路径的IndexHandler类。此类带有 NanoHTTPD 库，默认情况下返回一条Hello World消息。当我们想要不同的响应时，我们可以覆盖getText方法：

```java
addRoute("/", IndexHandler.class); // inside addMappings method
```

为了测试我们的新路线，我们可以这样做：

```bash
> curl 'http://localhost:8080' 
<html><body><h2>Hello world!</h3></body></html>
```

其次，让我们创建一个新的UserHandler 类来扩展现有的DefaultHandler。它的路线将是 / users。在这里，我们尝试了文本、MIME 类型和返回的状态代码：

```java
public static class UserHandler extends DefaultHandler {
    @Override
    public String getText() {
        return "UserA, UserB, UserC";
    }

    @Override
    public String getMimeType() {
        return MIME_PLAINTEXT;
    }

    @Override
    public Response.IStatus getStatus() {
        return Response.Status.OK;
    }
}
```

要调用此路由，我们将再次发出cURL命令：

```bash
> curl -X POST 'http://localhost:8080/users' 
UserA, UserB, UserC
```

最后，我们可以 使用新的StoreHandler类探索GeneralHandler 。我们修改了返回的消息以包含 URL 的storeId部分。

```java
public static class StoreHandler extends GeneralHandler {
    @Override
    public Response get(
      UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return newFixedLengthResponse("Retrieving store for id = "
          + urlParams.get("storeId"));
    }
}
```

让我们检查一下我们的新 API：

```bash
> curl 'http://localhost:8080/stores/123' 
Retrieving store for id = 123
```

## 7.HTTPS

为了使用 HTTPS，我们需要一个证书。请参阅[我们关于 SSL 的文章以](https://www.baeldung.com/java-ssl)获取更深入的信息。

我们可以使用像[Let's Encrypt](https://letsencrypt.org/)这样的服务，或者我们可以简单地生成一个自签名证书，如下所示：

```bash
> keytool -genkey -keyalg RSA -alias selfsigned
  -keystore keystore.jks -storepass password -validity 360
  -keysize 2048 -ext SAN=DNS:localhost,IP:127.0.0.1  -validity 9999
```

接下来，我们将把这个keystore.jks到类路径上的某个位置，比如 Maven 项目的src/main/resources文件夹。

之后，我们可以在调用 NanoHTTPD#makeSSLSocketFactory时引用它：

```java
public class HttpsExample  extends NanoHTTPD {

    public HttpsExample() throws IOException {
        super(8080);
        makeSecure(NanoHTTPD.makeSSLSocketFactory(
          "/keystore.jks", "password".toCharArray()), null);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    // main and serve methods
}
```

现在我们可以试试了。请注意--insecure参数的使用，因为默认情况下cURL将无法验证我们的自签名证书：

```bash
> curl --insecure 'https://localhost:8443'
HTTPS call is a success
```

## 8. WebSockets

NanoHTTPD 支持[WebSockets](https://www.baeldung.com/rest-vs-websockets)。

让我们创建一个最简单的 WebSocket 实现。为此，我们需要扩展NanoWSD类。我们还需要为 WebSocket 添加[NanoHTTPD依赖项：](https://search.maven.org/search?q=a:nanohttpd-websocket AND g:org.nanohttpd)

```xml
<dependency>
    <groupId>org.nanohttpd</groupId>
    <artifactId>nanohttpd-websocket</artifactId>
    <version>2.3.1</version>
</dependency>
```

对于我们的实施，我们将只回复一个简单的文本有效负载：

```java
public class WsdExample extends NanoWSD {
    public WsdExample() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public static void main(String[] args) throws IOException {
        new WsdExample();
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession ihttpSession) {
        return new WsdSocket(ihttpSession);
    }

    private static class WsdSocket extends WebSocket {
        public WsdSocket(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        //override onOpen, onClose, onPong and onException methods

        @Override
        protected void onMessage(WebSocketFrame webSocketFrame) {
            try {
                send(webSocketFrame.getTextPayload() + " to you");
            } catch (IOException e) {
                // handle
            }
        }
    }
}
```

这次我们将使用[wscat](https://github.com/websockets/wscat)而不是cURL：

```bash
> wscat -c localhost:8080
hello
hello to you
bye
bye to you
```

## 9.总结

总而言之，我们创建了一个使用 NanoHTTPD 库的项目。接下来，我们定义了 RESTful API 并探索了更多与 HTTP 相关的功能。最后，我们还实现了一个WebSocket。
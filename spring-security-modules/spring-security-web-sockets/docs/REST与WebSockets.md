## 1. 概述

在本教程中，我们将介绍客户端-服务器通信的基础知识，并通过当今可用的两个流行选项进行探索。我们将看到新加入者 WebSocket 如何与更流行的 RESTful HTTP 选择相抗衡。

## 2. 网络通信基础

在深入探讨不同选项的细节及其优缺点之前，让我们快速刷新一下网络通信的格局。这将有助于正确看待事物并更好地理解这一点。

[可以根据开放系统互连 (OSI) 模型](https://www.baeldung.com/cs/osi-model)来最好地理解网络通信。

OSI 模型将通信系统划分为七层抽象：

[![OCI模型](https://www.baeldung.com/wp-content/uploads/2019/04/OCI-Model.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/OCI-Model.jpg)

该模型的顶部是我们在本教程中感兴趣的应用程序层。但是，在比较 WebSocket 和 RESTful HTTP 时，我们将讨论前四层的一些方面。

应用层最接近最终用户，负责与参与通信的应用程序交互。该层使用了几种[流行的协议，例如 FTP、SMTP、SNMP、HTTP 和 WebSocket。](https://www.baeldung.com/cs/popular-network-protocols)

## 3. 描述 WebSocket 和 RESTful HTTP

虽然可以在任意数量的系统之间进行通信，但我们对客户端-服务器通信特别感兴趣。更具体地说，我们将专注于 Web 浏览器和 Web 服务器之间的通信。这是我们将用来比较 WebSocket 和 RESTful HTTP 的框架。

但在我们继续之前，为什么不快速了解它们是什么！

### 3.1。网络套接字

正如正式定义所言，[WebSocket](https://tools.ietf.org/html/rfc6455)是一种通信协议，其特点是通过持久的 TCP 连接进行双向全双工通信。现在，随着我们的深入，我们将详细了解该声明的每个部分。

 

WebSocket 在 2011 年被 IETF 标准化为[RFC 6455](https://tools.ietf.org/html/rfc6455)作为通信协议。当今大多数现代 Web 浏览器都支持 WebSocket 协议。

### 3.2. RESTful HTTP

虽然我们都知道[HTTP](https://tools.ietf.org/html/rfc2616)因为它在互联网上无处不在，但它也是一种应用层通信协议。HTTP 是一个基于请求-响应的协议，我们将在本教程后面更好地理解这一点。

REST (Representational State Transfer) 是一种架构风格，它对 HTTP 施加一组约束以创建 Web 服务。

## 4. WebSocket 子协议

虽然 WebSocket 定义了客户端和服务器之间双向通信的协议，但它没有对要交换的消息设置任何条件。作为子协议协商的一部分，通信各方可以同意这一点。

为重要的应用程序开发子协议并不方便。幸运的是，有许多流行的子协议(例如[STOMP](https://stomp.github.io/))可供使用。STOMP 代表 Simple Text Oriented Messaging Protocol 并在 WebSocket 上工作。Spring Boot 对 STOMP 具有一流的支持，我们将在教程中使用它。

## 5.Spring Boot中的快速设置

没有什么比看到一个工作示例更好的了。因此，我们将在 WebSocket 和 RESTful HTTP 中构建简单的用例，以进一步探索它们，然后进行比较。让我们为两者创建一个简单的服务器和客户端组件。

我们将使用 JavaScript 创建一个简单的客户端，该客户端将发送一个名称。而且，我们将使用Java创建一个服务器，它会以问候语进行响应。

### 5.1。网络套接字

要在Spring Boot中使用 WebSocket，我们需要[适当的 starter](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-websocket/2.1.4.RELEASE/jar)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

我们现在将配置 STOMP 端点：

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {
 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
    }
 
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic");
    }
}
```

让我们快速定义一个简单的 WebSocket 服务器，它接受名称并以问候语作为响应：

```java
@Controller
public class WebSocketController {
 
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(Message message) throws Exception {
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}
```

最后，让我们构建客户端来与这个 WebSocket 服务器进行通信。由于我们强调浏览器到服务器的通信，让我们在 JavaScript 中创建一个客户端：

```javascript
var stompClient = null;
function connect() {
    stompClient = Stomp.client('ws://localhost:8080/ws');
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/greetings', function (response) {
            showGreeting(JSON.parse(response.body).content);
        });
    });
}
function sendName() {
	stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}
function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}
```

这完成了我们的 WebSocket 服务器和客户端的工作示例。代码存储库中有一个 HTML 页面，它提供了一个简单的用户界面进行交互。

虽然这只是表面上的问题，但[带有 Spring 的 WebSocket 可用于构建复杂的聊天客户端](https://www.baeldung.com/websockets-spring)等等。

### 5.2. RESTful HTTP

我们现在将对 RESTful 服务进行类似的设置。我们的简单 Web 服务将接受带有名称的 GET 请求并以问候语作为响应。

这次我们改用[Spring Boot 的 web starter ：](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-web/2.1.4.RELEASE/jar)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

现在，我们将定义一个 REST 端点，利用 Spring 中提供的强大注解支持：

```java
@RestController
@RequestMapping(path = "/rest")
public class RestAPIController {
    @GetMapping(path="/{name}", produces = "application/json")
    public String getGreeting(@PathVariable("name") String name)
    {
        return "{"greeting" : "Hello, " + name + "!"}";
    }
}
```

最后，让我们在 JavaScript 中创建一个客户端：

```javascript
var request = new XMLHttpRequest()
function sendName() {
    request.open('GET', 'http://localhost:8080/rest/'+$("#name").val(), true)
    request.onload = function () {
    	var data = JSON.parse(this.response)
    	showGreeting(data.greeting)
    }
    request.send()
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}
```

差不多就是这样！同样，代码存储库中有一个 HTML 页面可以与用户界面一起使用。

尽管其简单性很深，但[定义生产级 REST API](https://www.baeldung.com/rest-with-spring-series)可能是一项更广泛的任务！

## 六、WebSocket与RESTful HTTP的比较

在创建了 WebSocket 和 RESTful HTTP 的最小但有效的示例之后，我们现在准备好了解它们如何相互对抗。在接下来的小节中，我们将根据几个标准对此进行检查。

需要注意的是，虽然我们可以直接比较 HTTP 和 WebSocket，因为它们都是应用层协议，但比较 REST 和 WebSocket 并不自然。正如我们之前看到的，REST 是一种利用 HTTP 进行通信的架构风格。

因此，我们与 WebSocket 的比较主要是关于 HTTP 中的功能或缺乏。

### 6.1。网址方案

URL定义了 Web 资源的唯一位置和检索它的机制。在客户端-服务器通信中，我们通常希望通过关联的 URL 获取静态或动态资源。

我们都熟悉 HTTP URL 方案：

```powershell
http://localhost:8080/rest
```

WebSocket URL 方案也没有太大区别：

```powershell
ws://localhost:8080/ws
```

一开始，唯一的区别似乎是冒号前面的字符，但它抽象了很多在幕后发生的事情。让我们进一步探索。

### 6.2. 握手

握手 是指通信双方之间自动协商通信协议的方式。HTTP 是一种无状态协议，以请求-响应机制工作。在每个 HTTP 请求上，都会通过套接字与服务器建立 TCP 连接。

客户端然后等待，直到服务器响应资源或错误。来自客户端的下一个请求会重复所有内容，就好像上一个请求从未发生过一样：

[![HTTP 连接](https://www.baeldung.com/wp-content/uploads/2019/04/HTTP-Connection.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/HTTP-Connection.jpg)

与 HTTP 相比，WebSocket 的工作方式非常不同，并且在实际通信之前以握手开始。

让我们看看 WebSocket 握手的组成部分：

[![WebSocket 连接](https://www.baeldung.com/wp-content/uploads/2019/04/WebSocket-Connection.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/WebSocket-Connection.jpg)

在 WebSocket的情况下，客户端在 HTTP 中发起协议握手请求，然后等待服务器响应接受从 HTTP 升级到 WebSocket 的响应。

当然，由于协议握手发生在 HTTP 上，它遵循上图中的顺序。但是一旦建立连接，客户端和服务器就会从那里切换到 WebSocket 以进行进一步的通信。

### 6.3. 联系

正如我们在上一小节中看到的，WebSocket 和 HTTP 之间的一个明显区别是 WebSocket 工作在持久的 TCP 连接上，而 HTTP 为每个请求创建一个新的 TCP 连接。

现在显然为每个请求创建新的 TCP 连接并不是很高效，HTTP 也没有意识到这一点。事实上，作为 HTTP/1.1 的一部分，引入了持久连接来缓解 HTTP 的这个缺点。

尽管如此，WebSocket 从一开始就被设计为与持久的 TCP 连接一起工作。

### 6.4. 沟通

WebSocket over HTTP 的好处是一个特定的场景，它源于客户端可以服务器可以以良好的旧 HTTP 无法实现的方式进行通信。

例如，在 HTTP 中，通常客户端发送该请求，然后服务器以请求的数据进行响应。服务器没有自己与客户端通信的通用方法。当然，已经设计了模式和解决方案来规避这种情况，例如服务器发送事件 (SSE)，但这些并不是完全自然的。

使用 WebSocket，通过持久的 TCP 通信工作，服务器和客户端都可以相互独立地发送数据，事实上，可以发送给许多通信方！这被称为双向通信。

WebSocket 通信的另一个有趣特性是它是全双工的。现在虽然这个词听起来很深奥；它只是意味着服务器和客户端都可以同时发送数据。将此与 HTTP 中发生的情况进行比较，在 HTTP 中服务器必须等到它完全接收到请求才能响应数据。

虽然双向和全双工通信的好处可能不会立即显现出来。我们将看到一些使用案例，它们释放了一些真正的力量。

### 6.5。安全

最后但同样重要的是，HTTP 和 WebSocket 都利用了 TLS 的安全性优势。虽然 HTTP 提供https作为其 URL 方案的一部分来使用它，但 WebSocket 将wss作为其 URL 方案的一部分以达到相同的效果。

因此，上一小节中 URL 的安全版本应如下所示：

```powershell
https://localhost:443/rest
wss://localhost:443/ws
```

保护 RESTful 服务或 WebSocket 通信是一个非常深入的主题，此处无法涵盖。现在，让我们说两者在这方面都得到了充分的支持。

### 6.6. 表现

我们必须了解 WebSocket 是一种有状态的协议，其中通信通过专用的 TCP 连接进行。另一方面，HTTP 本质上是一种无状态协议。这会影响它们在负载下的执行方式，但这实际上取决于用例。

由于 WebSocket 上的通信是通过可重用的 TCP 连接进行的，因此与 HTTP 相比，每条消息的开销更低。因此，它可以达到每台服务器更高的吞吐量。但是单个服务器可以扩展的范围是有限的，这就是 WebSocket 存在问题的地方。使用 WebSocket 水平扩展应用程序并不容易。

这就是 HTTP 的亮点。使用 HTTP，每个新请求都可能登陆任何服务器。这意味着为了提高整体吞吐量，我们可以轻松添加更多服务器。这应该对使用 HTTP 运行的应用程序没有影响。

显然，应用程序本身可能需要状态和会话粘性，这使说起来容易做起来难。

## 7. 我们应该在哪里使用它们？

现在，我们已经看到了足够多的基于 HTTP 的 RESTful 服务和基于 WebSocket 的简单通信来形成我们对它们的看法。但是我们应该在哪里使用什么？

重要的是要记住，虽然 WebSocket 已经摆脱了 HTTP 的缺点，但实际上它并不是 HTTP 的替代品。所以它们都有自己的位置和用途。让我们快速了解如何做出决定。

对于需要偶尔与服务器进行通信(例如获取员工记录)的大部分场景，通过 HTTP/S 使用 REST 服务仍然是明智的。但是对于较新的客户端应用程序，例如需要服务器实时更新的股票价格应用程序，利用 WebSocket 会非常方便。

概括地说，WebSocket 更适合基于推送和实时通信更恰当地定义需求的情况。此外，WebSocket 适用于需要同时向多个客户端推送消息的场景。在这些情况下，通过 RESTful 服务进行客户端和服务器通信即使不是禁止也很困难。

然而，需要从需求中得出通过 HTTP 使用 WebSocket 和 RESTful 服务。就像没有灵丹妙药一样，我们不能只指望挑选一个来解决所有问题。因此，我们必须利用我们的智慧和知识来设计一个有效的沟通模型。

## 8. 总结

在本教程中，我们回顾了网络通信的基础知识，重点介绍了应用层协议 HTTP 和 WebSocket。我们在Spring Boot中看到了一些基于 HTTP 的 WebSocket 和 RESTful API 的快速演示。

最后，我们比较了 HTTP 和 WebSocket 协议的特性，并简要讨论了何时使用它们。
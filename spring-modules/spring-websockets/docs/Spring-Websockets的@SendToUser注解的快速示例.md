## 1. 概述

在这个快速教程中，我们将说明如何**使用Spring WebSockets向特定会话或特定用户发送消息**。

上述模块的介绍可以参考[这篇](https://www.baeldung.com/websockets-spring)文章。

## 2. WebSocket配置

首先，我们需要**配置消息代理和WebSocket应用程序端点**：

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/", "/queue/");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/greeting");
    }
}
```

通过@EnableWebSocketMessageBroker，我们使用**STOMP通过WebSocket启用了代理支持的消息传递，STOMP代表面向流文本的消息传递协议**。重要的是要注意此注解需要与@Configuration结合使用。 

扩展AbstractWebSocketMessageBrokerConfigurer不是强制性的，但对于快速示例，自定义导入的配置会更容易。

在第一种方法中，我们设置了一个简单的基于内存的消息代理，以将消息传送回以“/topic”和“/queue”为前缀的目的地上的客户端。

并且，在第二个中，我们在“/greeting”注册了stomp端点。

如果我们想要启用SockJS，我们必须修改registry部分：

```java
registry.addEndpoint("/greeting").withSockJS();
```

## 3. 通过拦截器获取SessionID

**获取会话ID**的一种方法是添加一个Spring拦截器，该拦截器将在握手期间触发并从请求数据中获取信息。

这个拦截器可以直接添加到WebSocketConfig中：

```java
public void registerStompEndpoints(StompEndpointRegistry registry) {
	registry
        .addEndpoint("/greeting")
        .setHandshakeHandler(new DefaultHandshakeHandler() {

		// Get sessionId from request and set it in Map attributes
		public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
									   Map attributes) throws Exception {
			if (request instanceof ServletServerHttpRequest) {
				ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
				HttpSession session = servletRequest.getServletRequest().getSession();
				attributes.put("sessionId", session.getId());
			}
			return true;
		}
	}).withSockJS();
}
```

## 4. WebSocket端点

从Spring 5.0.5.RELEASE开始，由于@SendToUser注解的改进，无需进行任何自定义，它允许我们通过“/user/{sessionId}/...”向用户目的地发送消息，而不是“/user/{user}/...”。

这意味着注解的工作依赖于输入消息的会话ID，有效地将回复发送到会话私有的目标：

```java
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private Gson gson = new Gson();

    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(@Payload String message, Principal principal) throws Exception {
        return gson
              .fromJson(message, Map.class)
              .get("name").toString();
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
```

重要的是要注意，@SendToUser表示消息处理方法的返回值应作为消息发送到指定目的地，并以“/user/{username}”为前缀。

## 5. WebSocket客户端

```javascript
function connect() {
    var socket = new WebSocket('ws://localhost:8080/greeting');
    ws = Stomp.over(socket);

    ws.connect({}, function (frame) {
        ws.subscribe("/user/queue/errors", function (message) {
            alert("Error " + message.body);
        });

        ws.subscribe("/user/queue/reply", function (message) {
            alert("Message " + message.body);
        });
    }, function (error) {
        alert("STOMP error " + error);
    });
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Disconnected");
}
```

创建一个新的WebSocket，指向WebSocketConfiguration中映射的“/greeting”。

当我们将客户端订阅到“/user/queue/errors”和“/user/queue/reply”时，我们使用上一节中的注解信息。

正如我们所看到的，@SendToUser指向“queue/errors”，但消息将被发送到“/user/queue/errors”。

## 6. 总结

在本文中，我们探索了一种使用Spring WebSocket直接向用户或会话ID发送消息的方法。
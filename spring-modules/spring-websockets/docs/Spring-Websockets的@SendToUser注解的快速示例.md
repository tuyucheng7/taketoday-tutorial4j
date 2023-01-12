## 1. 概述

在本快速教程中，我们将说明如何使用 Spring WebSockets 向特定会话或特定用户发送消息。

上述模块的介绍可以参考[这篇文章](https://www.baeldung.com/websockets-spring)。

## 2.WebSocket配置

首先，我们需要配置消息代理和 WebSocket 应用端点：

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
  extends AbstractWebSocketMessageBrokerConfigurer {
	
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

通过@EnableWebSocketMessageBroker，我们使用STOMP通过 WebSocket 启用了代理支持的消息传递，它代表面向 流文本的消息传递协议。重要的是要注意此注解需要与 @Configuration结合使用。 

扩展AbstractWebSocketMessageBrokerConfigurer不是强制性的， 但对于快速示例，自定义导入的配置会更容易。

在第一种方法中，我们设置了一个简单的基于内存的消息代理，以将消息传送回以“/topic”和“/queue”为前缀的目的地上的客户端。

并且，在第二个中，我们在“/greeting”注册了 stomp 端点。

如果我们想要启用 SockJS，我们必须修改寄存器部分：

```java
registry.addEndpoint("/greeting").withSockJS();
```

## 3.拦截器获取Session ID

获取会话 ID的一种方法是添加一个 Spring 拦截器，它将在握手期间触发并从请求数据中获取信息。

这个拦截器可以直接在 WebSocketConfig中添加：

```java
@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
        
registry
  .addEndpoint("/greeting")
  .setHandshakeHandler(new DefaultHandshakeHandler() {

      public boolean beforeHandshake(
        ServerHttpRequest request, 
        ServerHttpResponse response, 
        WebSocketHandler wsHandler,
        Map attributes) throws Exception {
 
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest
                 = (ServletServerHttpRequest) request;
                HttpSession session = servletRequest
                  .getServletRequest().getSession();
                attributes.put("sessionId", session.getId());
            }
                return true;
        }}).withSockJS();
    }
```

## 4.WebSocket端点

从 Spring 5.0.5.RELEASE 开始，由于@SendToUser注解的改进，无需进行任何自定义，它允许我们通过“ /user/{sessionId}/… ”向用户目的地发送消息，而不是比“ /user/{user}/… ”。

这意味着注解的工作依赖于输入消息的会话 ID，有效地将回复发送到会话私有的目标：

```java
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private Gson gson = new Gson();
 
    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(
      @Payload String message, 
      Principal principal) throws Exception {
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

重要的是要注意，@SendToUser 表示消息处理方法的返回值应作为 消息发送 到指定目的地，并以“ /user/{username} ”为前缀。

## 5. WebSocket 客户端

```javascript
function connect() {
    var socket = new WebSocket('ws://localhost:8080/greeting');
    ws = Stomp.over(socket);

    ws.connect({}, function(frame) {
        ws.subscribe("/user/queue/errors", function(message) {
            alert("Error " + message.body);
        });

        ws.subscribe("/user/queue/reply", function(message) {
            alert("Message " + message.body);
        });
    }, function(error) {
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

创建一个新的WebSocket，指向 WebSocketConfiguration 中映射的“ / greeting ”。

当我们将客户端订阅到“ /user/queue/errors ”和“ /user/queue/reply ”时，我们使用上一节中的注解信息。

如我们所见，@SendToUser指向“ queue/errors ”，但消息将发送到“ /user/queue/errors ”。

## 六. 总结

在本文中，我们探索了一种使用 Spring WebSocket 直接向用户或会话 ID 发送消息的方法
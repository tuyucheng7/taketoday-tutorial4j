## 1. 简介

在[之前的文章中](https://www.baeldung.com/websockets-spring)，我们展示了如何将 WebSockets 添加到 Spring MVC 项目中。

在这里，我们将描述如何在 Spring MVC 中为 Spring WebSockets 添加安全性。在继续之前，请确保已经具备基本的 Spring MVC 安全覆盖——如果没有，请查看[这篇文章](https://www.baeldung.com/spring-security-basic-authentication)。

## 2.Maven依赖

我们的 WebSocket 实现需要两组主要的 Maven 依赖项。

首先，让我们指定我们将使用的 Spring Framework 和 Spring Security 的总体版本：

```xml
<properties>
    <spring.version>5.3.13</spring.version>
    <spring-security.version>5.7.3</spring-security.version>
</properties>
```

其次，让我们添加实现基本认证和授权所需的核心 Spring MVC 和 Spring Security 库：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>${spring-security.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>${spring-security.version}</version>
</dependency>

```

最新版本的[spring-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework" AND a%3A"spring-core")、[spring-web](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND a%3A"spring-web")、[spring-webmvc](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND a%3A"spring-webmvc")、[spring-security-web](https://search.maven.org/classic/#search|ga|1|g%3A" org.springframework" AND a%3A"spring-security-web")、[spring-security-config](https://search.maven.org/classic/#search|ga|1|g%3A" org.springframework" AND a%3A"spring-security-config")可以在 Maven Central 上找到。

最后，让我们添加所需的依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-messaging</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-messaging</artifactId>
    <version>${spring-security.version}</version>
</dependency>

```

可以在 Maven Central 上找到最新版本的[spring-websocket](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND a%3A"spring-websocket")、[spring-messaging](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND a%3A"spring-messaging")和[spring-security-messaging 。](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND a%3A"spring-security-messaging")

## 3. 基本 WebSocket 安全性

使用spring-security-messaging库的 WebSocket 特定安全性以AbstractSecurityWebSocketMessageBrokerConfigurer类及其在项目中的实现为中心：

```java
@Configuration
public class SocketSecurityConfig 
  extends AbstractSecurityWebSocketMessageBrokerConfigurer {
      //...
}
```

AbstractSecurityWebSocketMessageBrokerConfigurer类提供了由WebSecurityConfigurerAdapter提供的额外安全覆盖。

spring-security-messaging库并不是实现 WebSocket 安全性的唯一方法。如果我们坚持使用普通的spring-websocket库，我们可以实现WebSocketConfigurer接口并将安全拦截器附加到我们的套接字处理程序。

由于我们使用的是spring-security-messaging库，我们将使用AbstractSecurityWebSocketMessageBrokerConfigurer方法。

### 3.1。实现configureInbound()

configureInbound()的实现是配置AbstractSecurityWebSocketMessageBrokerConfigurer子类中最重要的一步：

```java
@Override 
protected void configureInbound(
  MessageSecurityMetadataSourceRegistry messages) { 
    messages
      .simpDestMatchers("/secured/").authenticated()
      .anyMessage().authenticated(); 
}
```

WebSecurityConfigurerAdapter允许为不同的路由指定各种应用程序范围的授权要求，而AbstractSecurityWebSocketMessageBrokerConfigurer允许指定套接字目标的特定授权要求。

### 3.2. 类型和目的地匹配

MessageSecurityMetadataSourceRegistry允许我们指定安全约束，例如路径、用户角色以及允许的消息。

类型匹配器限制允许哪些SimpMessageType以及以何种方式：

```java
.simpTypeMatchers(CONNECT, UNSUBSCRIBE, DISCONNECT).permitAll()
```

目的地匹配器限制哪些端点模式可以访问以及以何种方式访问：

```java
.simpDestMatchers("/app/").hasRole("ADMIN")
```

订阅目标匹配器映射一个与SimpMessageType.SUBSCRIBE匹配的SimpDestinationMessageMatcher实例列表：

```java
.simpSubscribeDestMatchers("/topic/").authenticated()
```

[这是类型和目标匹配的所有可用方法](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/messaging/MessageSecurityMetadataSourceRegistry.html)的完整列表。

## 4. 保护套接字路由

现在我们已经了解了基本的套接字安全和类型匹配配置，我们可以结合套接字安全、视图、STOMP(一种文本消息协议)、消息代理和套接字控制器来在我们的 Spring MVC 应用程序中启用安全的 WebSocket。

首先，让我们为基本的 Spring Security 覆盖设置我们的套接字视图和控制器：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
@ComponentScan("com.baeldung.springsecuredsockets")
public class SecurityConfig { 
    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
        http
          .authorizeRequests()
          .antMatchers("/", "/index", "/authenticate").permitAll()
          .antMatchers(
            "/secured//",
            "/secured/success", 
            "/secured/socket",
            "/secured/success").authenticated()
          .anyRequest().authenticated()
          .and()
          .formLogin()
          .loginPage("/login").permitAll()
          .usernameParameter("username")
          .passwordParameter("password")
          .loginProcessingUrl("/authenticate")
          //...
    }
}
```

其次，让我们设置具有身份验证要求的实际消息目的地：

```java
@Configuration
public class SocketSecurityConfig 
  extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
          .simpDestMatchers("/secured/").authenticated()
          .anyMessage().authenticated();
    }   
}
```

现在，在我们的 WebSocketMessageBrokerConfigurer 中，我们可以注册实际的消息和 STOMP 端点：

```java
@Configuration
@EnableWebSocketMessageBroker
public class SocketBrokerConfig 
  implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/secured/history");
        config.setApplicationDestinationPrefixes("/spring-security-mvc-socket");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/secured/chat")
          .withSockJS();
    }
}
```

让我们定义一个示例套接字控制器和端点，我们为上面提供了安全覆盖：

```java
@Controller
public class SocketController {
 
    @MessageMapping("/secured/chat")
    @SendTo("/secured/history")
    public OutputMessage send(Message msg) throws Exception {
        return new OutputMessage(
           msg.getFrom(),
           msg.getText(), 
           new SimpleDateFormat("HH:mm").format(new Date())); 
    }
}
```

## 5. 同源政策

同源策略要求与端点的所有交互都必须来自发起交互的同一域。

例如，假设的 WebSockets 实现托管在foo.com上，并且正在执行同源策略。如果用户连接到托管在foo.com上的客户端，然后打开另一个浏览器访问bar.com，那么bar.com将无法访问的 WebSocket 实现。

### 5.1。覆盖同源策略

Spring WebSockets 执行开箱即用的同源策略，而普通 WebSockets 没有。

事实上，对于任何有效的CONNECT消息类型， Spring Security 都需要一个 CSRF ( Cross Site Request Forgery ) 令牌：

```java
@Controller
public class CsrfTokenController {
    @GetMapping("/csrf")
    public @ResponseBody String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrf.getToken();
    }
}
```

通过调用/csrf的端点，客户端可以获取令牌并通过 CSRF 安全层进行身份验证。

但是，可以通过将以下配置添加到AbstractSecurityWebSocketMessageBrokerConfigurer来覆盖 Spring 的同源策略：

```java
@Override
protected boolean sameOriginDisabled() {
    return true;
}
```

### 5.2. STOMP、SockJS 支持和框架选项

通常使用[STOMP](http://jmesnil.net/stomp-websocket/doc/)和[SockJS](https://github.com/sockjs)来实现对 Spring WebSockets 的客户端支持。

SockJS 默认配置为禁止通过 HTML iframe元素进行传输。这是为了防止点击劫持的威胁。

但是，在某些用例中，允许iframe使用 SockJS 传输可能是有益的。为此，可以创建SecurityFilterChain bean：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) 
  throws Exception {
    http
      .csrf()
        //...
        .and()
      .headers()
        .frameOptions().sameOrigin()
      .and()
        .authorizeRequests();
    return http.build();
}
```

请注意，在此示例中，尽管允许通过iframe进行传输，但我们仍遵循同源策略。

## 6. Oauth2 覆盖范围

通过在标准WebSecurityConfigurerAdapter 覆盖范围之外实现 Oauth2 安全覆盖，以及扩展 - 可以实现对 Spring WebSockets 的特定于 Oauth2 的支持 。[这是](https://www.baeldung.com/rest-api-spring-oauth2-angular)一个如何实现 Oauth2 的示例。

要对 WebSocket 端点进行身份验证和访问，可以在从客户端连接到后端 WebSocket 时将Oauth2 access_token传递给查询参数。

这是一个使用 SockJS 和 STOMP 演示该概念的示例：

```javascript
var endpoint = '/ws/?access_token=' + auth.access_token;
var socket = new SockJS(endpoint);
var stompClient = Stomp.over(socket);
```

## 7. 总结

在这个简短的教程中，我们展示了如何为 Spring WebSockets 添加安全性。如果想了解有关此集成的更多信息，请查看 Spring 的[WebSocket](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/websocket.html)和[WebSocket 安全参考文档。](https://docs.spring.io/autorepo/docs/spring-security/4.2.x/reference/html/websocket.html)
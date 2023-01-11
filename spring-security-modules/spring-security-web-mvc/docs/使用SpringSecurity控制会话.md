## 1. 概述

在本教程中，我们将说明Spring Security如何允许我们控制HTTP Session。

此控制范围从会话超时到启用并发会话和其他高级安全配置。

## 2. 会话何时创建？

我们可以准确控制会话何时创建以及Spring Security将如何与之交互：

+ always – 如果会话尚不存在，则始终会创建会话。
+ ifRequired – 只有在需要时才会创建会话(默认)。
+ never – 框架本身永远不会创建会话，但如果它已经存在，它将使用它。
+ stateless – Spring Security不会创建或使用任何会话。

 ```
 <http create-session="ifRequired">...</http>
 ```

这是Java配置：

```
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
}
```

了解此配置仅控制Spring Security所做的事情，而不是整个应用程序，这一点非常重要。
如果我们指示Spring Security不创建会话，则它不会创建会话，但我们的应用程序可能会！

默认情况下，Spring Security会在需要时创建一个会话-即“ifRequired”。

对于更无状态的应用程序，“never”将确保Spring Security本身不会创建任何会话。但是如果应用程序创建了一个，Spring Security将使用它。

最后，最严格的会话创建选项“stateless”保证应用程序根本不会创建任何会话。

这是在Spring 3.1中引入的，将有效地跳过Spring
Security过滤器链的某些部分-主要是与会话相关的部分，例如HttpSessionSecurityContextRepository、SessionManagementFilter和RequestCacheFilter。

这些更严格的控制机制直接意味着不使用cookie，因此每个请求都需要重新验证。

这种无状态架构与REST API及其无状态约束配合得很好。它们还可以很好地与基本身份验证和摘要式身份验证等身份验证机制配合使用。

## 3. 背后细节

在运行身份验证过程之前，Spring Security将运行一个过滤器，负责存储请求之间的安全上下文。这是SecurityContextPersistenceFilter。

默认情况下会按照HttpSessionSecurityContextRepository策略存储上下文，该策略使用HTTP Session作为存储。

对于严格的create-session=”stateless” 属性，此策略将被替换为另一个-NullSecurityContextRepository-并且不会创建或使用会话来保留上下文。

## 4. 并发会话控制

当已通过身份验证的用户尝试再次进行身份验证时，应用程序可以通过以下几种方式之一处理该事件。
它可以使用户的活动会话无效并使用新会话再次验证用户，或者允许两个会话同时存在。

启用并发会话控制支持的第一步是在web.xml中添加以下监听器：

```
<listener>
    <listener-class>
        org.springframework.security.web.session.HttpSessionEventPublisher
    </listener-class>
</listener>
```

或者我们可以将其定义为bean：

```java

@Configuration
public class SecSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
```

这对于确保在会话被销毁时通知Spring Security会话注册表至关重要。

为了允许同一用户有多个并发会话，应在XML配置中使用<session-management>标签：

```
<http ...>
    <session-management>
        <concurrency-control max-sessions="2" />
    </session-management>
</http>
```

或者我们可以通过Java配置来做到这一点：

```java

@Configuration
public class SecSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement().maximumSessions(2);
    }
}
```

## 5. 会话超时

### 5.1 处理会话超时

会话超时后，如果用户发送带有过期会话ID的请求，他们将被重定向到可通过命名空间配置的URL：

```
<session-management>
    <concurrency-control expired-url="/sessionExpired.html" ... />
</session-management>
```

同样，如果用户发送的请求的会话id未过期但完全无效，他们也将被重定向到可配置的URL：

```text
<session-management invalid-session-url="/invalidSession.html">
    ...
</session-management>
```

这是相应的Java配置：

```java

@Configuration
public class SecSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .expiredUrl("/sessionExpired.html")
                .invalidSessionUrl("/invalidSession.html");
    }
}
```

### 5.2 使用Spring Boot配置会话超时

我们可以使用属性轻松配置嵌入式服务器的会话超时值：

```properties
server.servlet.session.timeout=15m
```

如果我们不指定持续时间单位，Spring将假定它是秒。

简而言之，使用此配置，会话将在 15 分钟不活动后过期。在这段时间之后会话被认为是无效的。

如果我们将项目配置为使用 Tomcat，我们必须记住它只支持分钟精度的会话超时，最少为一分钟。
这意味着如果我们指定超时值170s，例如，它将导致两分钟的超时。

最后，重要的是要提到，即使 Spring Session 支持类似的属性(spring.session.timeout)，如果没有指定，自动配置将回退到我们首先提到的属性的值。
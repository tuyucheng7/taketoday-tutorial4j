## 1. 概述

在我们[之前的文章](https://www.baeldung.com/csrf-thymeleaf-with-spring-security)中，我们解释了CSRF攻击如何影响Spring MVC应用程序。

本文将通过不同的案例来确定无状态REST API是否容易受到CSRF攻击，如果是，如何保护它免受这些攻击。

## 2. REST API是否需要CSRF保护？

首先，我们可以在[专门的指南](https://www.baeldung.com/spring-security-csrf#example)中找到CSRF攻击的示例。

现在，在阅读本指南后，我们可能会认为无状态REST API不会受到这种攻击的影响，因为在服务器端没有可窃取的会话。

让我们举一个典型的例子：一个Spring REST API应用程序和一个JavaScript客户端。客户端使用安全令牌作为凭据(例如JSESSIONID或[JWT](https://en.wikipedia.org/wiki/JSON_Web_Token))，REST API会在用户成功登录后发出该令牌。

**CSRF漏洞取决于客户端如何存储这些凭据并将其发送到API**。

让我们回顾一下不同的选项以及它们将如何影响我们的应用程序漏洞。

### 2.1 凭据不持久

从REST API检索到令牌后，我们可以将令牌设置为JavaScript全局变量。**这会将令牌保存在浏览器的内存中，并且仅对当前页面可用**。

这是最安全的方式：CSRF和XSS攻击总是导致在新页面上打开客户端应用程序，该页面无法访问用于登录的初始页面的内存。

但是，我们的用户每次访问或刷新页面时都必须重新登录。

在移动浏览器上，即使浏览器进入后台，也会发生这种情况，因为系统会清除内存。

**这对用户来说太受限制了，以至于这个选项很少被实现**。

### 2.2 存储在浏览器存储中的凭据

我们可以将令牌保存在浏览器存储中-例如会话存储。然后，我们的JavaScript客户端可以从中读取令牌并在所有REST请求中发送带有此令牌的authorization标头。

这是一种流行的使用方式，例如JWT：**它很容易实现并且可以防止攻击者使用CSRF攻击**。事实上，与cookie不同，浏览器存储变量不会自动发送到服务器。

**但是，此实现容易受到XSS攻击**：恶意JavaScript代码可以访问浏览器存储并随请求发送令牌。在这种情况下，我们必须[保护我们的应用程序](https://www.baeldung.com/spring-prevent-xss)。

### 2.3 存储在Cookie中的凭据

另一种选择是使用cookie来保存凭据。然后，我们的应用程序的漏洞取决于我们的应用程序如何使用cookie。

我们可以使用cookie来仅保留凭据，如JWT，但不能对用户进行身份验证。

我们的JavaScript客户端必须读取令牌并将其发送到authorization标头中的API。

**在这种情况下，我们的应用程序不容易受到CSRF的攻击**：即使cookie是通过恶意请求自动发送的，我们的REST API也会从authorization标头而不是cookie中读取凭据。但是，必须将HTTP-only标志设置为false才能让我们的客户端读取cookie。

**但是，通过这样做，我们的应用程序将容易受到上一节中谈到的XSS攻击**。

另一种方法是验证来自会话cookie的请求，并将HTTP-only标志设置为true。这通常是Spring Security提供的JSESSIONID cookie。当然，为了保持我们的API无状态，我们绝不能在服务器端使用会话。

**在这种情况下，我们的应用程序像有状态应用程序一样容易受到CSRF的攻击**：由于cookie将随任何REST请求一起自动发送，因此单击恶意链接可以执行经过身份验证的操作。

### 2.4 其他CSRF易受攻击的配置

某些配置不使用安全令牌作为凭据，但也可能容易受到CSRF攻击。

**这是[HTTP基本身份验证](https://en.wikipedia.org/wiki/Basic_access_authentication)、[HTTP摘要式身份验证](https://en.wikipedia.org/wiki/Digest_access_authentication)和[mTLS](https://en.wikipedia.org/wiki/Mutual_authentication)的情况**。

它们不是很常见，但具有相同的缺点：浏览器会在任何HTTP请求上自动发送凭据。在这些情况下，我们必须启用CSRF保护。

## 3. 在Spring Boot中禁用CSRF保护

Spring Security从版本4开始默认启用CSRF保护。

如果我们的项目不需要它，我们可以在SecurityFilterChain bean中禁用它：

```java
@Configuration
public class SpringBootSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        return http.build();
    }
}
```

## 4. 使用REST API启用CSRF保护

### 4.1 Spring配置

如果我们的项目需要CSRF保护，**我们可以通过在SecurityFilterChain bean中使用CookieCsrfTokenRepository来发送带有cookie的CSRF令牌**。

我们必须将HTTP-only标志设置为false以便能够从我们的JavaScript客户端检索它：

```java
@Configuration
public class SpringSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        return http.build();
    }
}
```

**重启应用程序后，我们的请求收到HTTP错误，这意味着启用了CSRF保护**。

我们可以通过将日志级别调整为DEBUG来确认这些错误是从CsrfFilter类发出的：

```xml
<logger name="org.springframework.security.web.csrf" level="DEBUG" />
```

它将显示：

```shell
Invalid CSRF token found for http://...
```

**此外，我们应该在浏览器中看到一个新的XSRF-TOKEN cookie存在**。

让我们在REST控制器中添加几行代码，以将信息也写入我们的API日志：

```java
CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
LOGGER.info("{}={}", token.getHeaderName(), token.getToken());
```

### 4.2 客户端配置

在客户端应用程序中，XSRF-TOKEN cookie是在第一次访问API之后设置的。我们可以使用JavaScript正则表达式检索它：

```javascript
const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
```

然后，我们必须将令牌发送到每个修改API状态的REST请求：POST、PUT、DELETE和PATCH。

**Spring期望在X-XSRF-TOKEN标头中接收它**。我们可以简单地使用JavaScript Fetch API设置它：

```javascript
fetch(url, {
    method: 'POST',
    body: JSON.stringify({ /* data to send */ }),
    headers: { 'X-XSRF-TOKEN': csrfToken },
})
```

现在，我们可以看到我们的请求正在运行，并且REST API日志中的“Invalid CSRF token”错误消失了。

因此，攻击者将无法进行CSRF攻击。例如，尝试从诈骗网站执行相同请求的脚本将收到“Invalid CSRF token”错误。

实际上，如果用户没有首先访问实际网站，则不会设置cookie，请求也会失败。

## 5. 总结

在本文中，我们回顾了可能或不可能针对REST API进行CSRF攻击的不同上下文。

然后，我们学习了如何使用Spring Security启用或禁用CSRF保护。
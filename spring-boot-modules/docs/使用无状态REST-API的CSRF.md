## 1. 概述

在我们[之前的文章](https://www.baeldung.com/csrf-thymeleaf-with-spring-security)中，我们解释了 CSRF 攻击如何影响 Spring MVC 应用程序。

本文将通过不同的案例来确定无状态 REST API 是否容易受到 CSRF 攻击，如果是，如何保护它免受这些攻击。

## 2、REST API需要CSRF保护吗？

[首先，我们可以在专门的指南](https://www.baeldung.com/spring-security-csrf#example)中找到 CSRF 攻击的示例。

现在，在阅读本指南后，我们可能会认为无状态 REST API 不会受到这种攻击的影响，因为在服务器端没有可窃取的会话。

让我们举一个典型的例子：一个Spring REST API应用程序和一个Javascript客户端。客户端使用安全令牌作为凭据(例如 JSESSIONID 或[JWT](https://en.wikipedia.org/wiki/JSON_Web_Token))，REST API 在用户成功登录后发出。

CSRF 漏洞取决于客户端如何存储这些凭据并将这些凭据发送到 API。

让我们回顾一下不同的选项以及它们将如何影响我们的应用程序漏洞。

我们将举一个典型的例子：一个Spring REST API应用程序和一个Javascript客户端。客户端使用安全令牌作为凭据(例如 JSESSIONID 或[JWT](https://en.wikipedia.org/wiki/JSON_Web_Token))，REST API 在用户成功登录后发出。

### 2.1. 凭据不持久

从 REST API 检索到令牌后，我们可以将令牌设置为JavaScript全局变量。这会将令牌保存在浏览器的内存中，并且仅对当前页面可用。

这是最安全的方式：CSRF 和 XSS 攻击总是导致在新页面上打开客户端应用程序，该页面无法访问用于登录的初始页面的内存。

但是，我们的用户每次访问或刷新页面时都必须重新登录。

在移动浏览器上，即使浏览器进入后台，也会发生这种情况，因为系统会清除内存。

这对用户来说太受限制了，以至于这个选项很少被实现。

### 2.2. 存储在浏览器存储中的凭据

我们可以将令牌保存在浏览器存储中——例如会话存储。然后，我们的JavaScript客户端可以从中读取令牌并在所有 REST 请求中发送带有此令牌的授权标头。

这是一种流行的使用方式，例如 JWT：它很容易实现并且可以防止攻击者使用 CSRF 攻击。事实上，与 cookie 不同，浏览器存储变量不会自动发送到服务器。

但是，此实现容易受到 XSS 攻击：恶意JavaScript代码可以访问浏览器存储并随请求发送令牌。在这种情况下，我们必须[保护我们的应用程序](https://www.baeldung.com/spring-prevent-xss)。

### 2.3. 存储在 Cookie 中的凭据

另一种选择是使用 cookie 来保存凭据。然后，我们的应用程序的漏洞取决于我们的应用程序如何使用 cookie。

我们可以使用 cookie 来仅保留凭据，如 JWT，但不能对用户进行身份验证。

我们的JavaScript客户端必须读取令牌并将其发送到授权标头中的 API。

在这种情况下，我们的应用程序不容易受到 CSRF 的攻击：即使 cookie 是通过恶意请求自动发送的，我们的 REST API 也会从授权标头而不是 cookie 中读取凭据。但是，必须将HTTP-only标志设置为false才能让我们的客户端读取 cookie。

但是，通过这样做，我们的应用程序将容易受到上一节中的 XSS 攻击。

另一种方法是验证来自会话 cookie 的请求，并将HTTP-only标志设置为true。这通常是 Spring Security 提供的 JSESSIONID cookie。当然，为了保持我们的 API 无状态，我们绝不能在服务器端使用会话。

在这种情况下，我们的应用程序像有状态应用程序一样容易受到 CSRF 的攻击：由于 cookie 将随任何 REST 请求自动发送，因此单击恶意链接可以执行经过身份验证的操作。

### 2.4. 其他 CSRF 易受攻击的配置

一些配置不使用安全令牌作为凭据，但也可能容易受到 CSRF 攻击。

[这是HTTP 基本身份验证](https://en.wikipedia.org/wiki/Basic_access_authentication)、[HTTP 摘要身份验证](https://en.wikipedia.org/wiki/Digest_access_authentication)和[mTLS](https://en.wikipedia.org/wiki/Mutual_authentication)的情况。

它们不是很常见，但具有相同的缺点：浏览器会在任何 HTTP 请求上自动发送凭据。在这些情况下，我们必须启用 CSRF 保护。

## 3. 在Spring Boot中禁用 CSRF 保护

Spring Security 从版本 4 开始默认启用 CSRF 保护。

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

## 4. 使用 REST API 启用 CSRF 保护

### 4.1. 弹簧配置

如果我们的项目需要 CSRF 保护，我们可以通过在SecurityFilterChain bean中使用CookieCsrfTokenRepository来发送带有 cookie 的 CSRF 令牌。

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

重启应用程序后，我们的请求收到 HTTP 错误，这意味着启用了 CSRF 保护。

我们可以通过将日志级别调整为 DEBUG 来确认这些错误是从CsrfFilter类发出的：

```xml
<logger name="org.springframework.security.web.csrf" level="DEBUG" />
```

它将显示：

```plaintext
Invalid CSRF token found for http://...
```

此外，我们应该在我们的浏览器中看到一个新的XSRF-TOKEN cookie 出现了。

让我们在 REST 控制器中添加几行，以将信息也写入我们的 API 日志：

```java
CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
LOGGER.info("{}={}", token.getHeaderName(), token.getToken());
```

### 4.2. 客户端配置

在客户端应用程序中，XSRF-TOKEN cookie 在第一次 API 访问后设置。我们可以使用JavaScript正则表达式检索它：

```javascript
const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
```

然后，我们必须将令牌发送到每个修改 API 状态的 REST 请求：POST、PUT、DELETE 和 PATCH。

Spring 期望在X-XSRF-TOKEN header中接收它。我们可以简单地使用JavaScriptFetch API 设置它：

```javascript
fetch(url, {
    method: 'POST',
    body: JSON.stringify({ /* data to send */ }),
    headers: { 'X-XSRF-TOKEN': csrfToken },
})
```

现在，我们可以看到我们的请求正在运行，并且REST API 日志中的“无效 CSRF 令牌”错误消失了。

因此，攻击者将无法进行 CSRF 攻击。例如，尝试从诈骗网站执行相同请求的脚本将收到“无效的 CSRF 令牌”错误。

实际上，如果用户没有首先访问实际网站，则不会设置 cookie，请求也会失败。

## 5.总结

在本文中，我们回顾了可能或不可能针对 REST API 进行 CSRF 攻击的不同上下文。

然后，我们学习了如何使用 Spring Security 启用或禁用 CSRF 保护。
## 1. 概述

本教程展示了如何使用 Spring 设置、配置和自定义 Digest Authentication。与上[一篇介绍基本身份验证](https://www.baeldung.com/spring-security-basic-authentication)的文章类似，我们将构建在 Spring MVC 教程之上，并使用 Spring Security 提供的 Digest Auth 机制保护应用程序。

## 2. 安全 XML 配置

关于配置，首先要了解的是，虽然 Spring Security 确实对 Digest 身份验证机制提供了完整的开箱即用支持，但这种支持并没有像 Basic Authentication那样很好地集成到命名空间中。

在这种情况下，我们需要手动定义将构成安全配置的原始 bean - DigestAuthenticationFilter和DigestAuthenticationEntryPoint：

```xml
<beans:bean id="digestFilter" 
  class="org.springframework.security.web.authentication.www.DigestAuthenticationFilter">
    <beans:property name="userDetailsService" ref="userService" />
    <beans:property name="authenticationEntryPoint" ref="digestEntryPoint" />
</beans:bean>
<beans:bean id="digestEntryPoint" 
  class="org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint">
    <beans:property name="realmName" value="Contacts Realm via Digest Authentication" />
    <beans:property name="key" value="acegi" />
</beans:bean>

<!-- the security namespace configuration -->
<http use-expressions="true" entry-point-ref="digestEntryPoint">
    <intercept-url pattern="/" access="isAuthenticated()" />

    <custom-filter ref="digestFilter" after="BASIC_AUTH_FILTER" />
</http>

<authentication-manager>
    <authentication-provider>
        <user-service id="userService">
            <user name="user1" password="user1Pass" authorities="ROLE_USER" />
        </user-service>
    </authentication-provider>
</authentication-manager>
```

接下来，我们需要将这些 bean 集成到整体安全配置中——在这种情况下，命名空间仍然足够灵活，可以让我们这样做。

第一部分是通过主<http>元素的entry-point-ref属性指向自定义入口点 bean。

第二部分是将新定义的摘要过滤器添加到安全过滤器链中。由于这个过滤器在功能上等同于BasicAuthenticationFilter，我们在链中使用相同的相对位置——这是由整个[Spring Security 标准过滤器中的](http://static.springsource.org/spring-security/site/docs/3.1.x/reference/ns-config.html#ns-custom-filters)BASIC_AUTH_FILTER别名指定的。

最后，注意摘要过滤器被配置为指向用户服务 bean——在这里，命名空间再次非常有用，因为它允许我们为<user-service>元素创建的默认用户服务指定一个 bean 名称：

```xml
<user-service id="userService">
```

## 3. 使用安全应用程序

我们将使用curl命令来使用受保护的应用程序并了解客户端如何与之交互。

让我们从请求主页开始——在请求中不提供安全凭证：

```bash
curl -i http://localhost/spring-security-mvc-digest-auth/homepage.html
```

正如预期的那样，我们得到一个带有401 Unauthorized状态码的响应：

```bash
HTTP/1.1 401 Unauthorized
Server: Apache-Coyote/1.1
Set-Cookie: JSESSIONID=CF0233C...; Path=/spring-security-mvc-digest-auth/; HttpOnly
WWW-Authenticate: Digest realm="Contacts Realm via Digest Authentication", qop="auth", 
  nonce="MTM3MzYzODE2NTg3OTo3MmYxN2JkOWYxZTc4MzdmMzBiN2Q0YmY0ZTU0N2RkZg=="
Content-Type: text/html;charset=utf-8
Content-Length: 1061
Date: Fri, 12 Jul 2013 14:04:25 GMT
```

如果此请求是由浏览器发送的，则身份验证质询将使用简单的用户/密码对话框提示用户输入凭据。

现在让我们提供正确的凭据并再次发送请求：

```bash
curl -i --digest --user 
   user1:user1Pass http://localhost/spring-security-mvc-digest-auth/homepage.html
```

请注意，我们正在通过–digest标志为curl命令启用摘要身份验证。

来自服务器的第一个响应将是相同的 - 401 Unauthorized - 但现在将通过第二个请求解释挑战并采取行动 - 这将通过200 OK成功：

```bash
HTTP/1.1 401 Unauthorized
Server: Apache-Coyote/1.1
Set-Cookie: JSESSIONID=A961E0D...; Path=/spring-security-mvc-digest-auth/; HttpOnly
WWW-Authenticate: Digest realm="Contacts Realm via Digest Authentication", qop="auth", 
  nonce="MTM3MzYzODgyOTczMTo3YjM4OWQzMGU0YTgwZDg0YmYwZjRlZWJjMDQzZWZkOA=="
Content-Type: text/html;charset=utf-8
Content-Length: 1061
Date: Fri, 12 Jul 2013 14:15:29 GMT

HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Set-Cookie: JSESSIONID=55F996B...; Path=/spring-security-mvc-digest-auth/; HttpOnly
Content-Type: text/html;charset=ISO-8859-1
Content-Language: en-US
Content-Length: 90
Date: Fri, 12 Jul 2013 14:15:29 GMT

<html>
<head></head>

<body>
	<h1>This is the homepage</h1>
</body>
</html>
```

关于这种交互的最后一点是，客户端可以在第一个请求中抢先发送正确的Authorization标头，从而完全避免服务器安全挑战和第二个请求。

## 4. Maven 依赖

[Spring Security Maven 教程](https://www.baeldung.com/spring-security-with-maven)中深入讨论了安全依赖项。简而言之，我们需要在pom.xml中定义spring-security-web和spring-security-config作为依赖项。

## 5. 总结

在本教程中，我们通过利用框架中的摘要身份验证支持将安全性引入一个简单的 Spring MVC 项目。

这些示例的实现可以在[Github 项目](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-digest-auth)中找到——这是一个基于 Eclipse 的项目，因此应该很容易导入和运行。

当项目在本地运行时，主页 html 可以在(或者，使用最少的 Tomcat 配置，在端口 80 上)访问：

http://localhost:8080/spring-security-mvc-digest-auth/homepage.html

最后，应用程序没有理由需要[在基本身份验证和摘要式身份验证之间进行选择](https://www.baeldung.com/basic-and-digest-authentication-for-a-rest-api-with-spring-security)——两者可以同时设置在同一个 URI 结构上，这样客户端在使用 Web 应用程序时可以在两种机制之间进行选择。
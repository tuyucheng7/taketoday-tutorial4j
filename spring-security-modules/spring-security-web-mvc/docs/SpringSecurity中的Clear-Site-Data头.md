## 1. 概述

为了网络优化，一些网站允许浏览器在本地存储中缓存 CSS 或 JS 等资源。这允许浏览器为每个请求保存网络往返。

因此缓存资源对于提高网页的加载时间至关重要。同样重要的是在不需要时清除缓存的数据。例如， 如果用户退出网站，浏览器应该从缓存中删除所有会话数据。

浏览器缓存数据的时间超过所需时间有两个主要问题：

-   现代网站使用一组丰富的 CSS 和 JS 文件，这些文件会消耗大量浏览器内存
-   缓存敏感数据(如会话 cookie)的网站容易受到网络钓鱼攻击

在本教程中，我们将了解 HTTP 的Clear-Site-Data响应标头如何帮助网站从浏览器中清除本地存储的数据。

## 2.清除站点数据标题

就像[Cache-Control](https://www.baeldung.com/spring-mvc-cache-headers)标头一样，Clear-Site-Data是一个 HTTP 响应标头。网站可以使用此标头指示浏览器删除缓存在本地存储中的数据。

对于需要身份验证的网站，Cache-Control 标头通常包含在/login 响应中，并允许浏览器缓存用户数据。同样，网站在/logout 响应中包含Clear-Site-Data标头，以清除属于该用户的任何缓存数据。

此时，重要的是要了解浏览器通常将本地存储分为不同的类型：

-   本地存储
-   会话存储
-   饼干

由于网站可以以其中任何一种类型存储数据，因此 Clear-Site-Data允许我们在标头中指定目标存储：

-   缓存——删除本地缓存的数据，包括私有和共享的浏览器缓存
-   cookie – 删除浏览器 cookie 中存储的数据
-   storage – 清除浏览器的本地和会话存储
-   executionContexts - 此开关告诉浏览器重新加载该 URL 的浏览器选项卡
-   (星号)——从上述所有存储区域中删除数据

因此， Clear-Site-Data 标头必须至少包含以下存储类型之一：

```plaintext
Clear-Site-Data: "cache", "cookies", "storage", "executionContexts"
```

在以下部分中，我们将在 Spring Security 中实现 /logout 服务，并在响应中包含 Clear-Site-Data 标头。

## 3. Maven依赖

在我们编写一些代码在 Spring 中添加 Clear-Site-Data 标头之前，让我们将[spring-security-web](https://search.maven.org/artifact/org.springframework.security/spring-security-web)和[spring-security-config](https://search.maven.org/artifact/org.springframework.security/spring-security-config)依赖项添加到项目中：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
```

## 4. Spring Security 中的ClearSiteDataHeaderWriter 

我们之前讨论过 Spring 提供了一个 [CacheControl](https://www.baeldung.com/spring-security-cache-control-headers) 实用程序类来在响应中写入 Cache-Control 标头。同样，Spring Security 提供了一个 ClearSiteDataHeaderWriter 类来轻松地在 HTTP 响应中添加标头：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
          .disable()
          .formLogin()
          .loginPage("/login.html")
          .loginProcessingUrl("/perform_login")
          .defaultSuccessUrl("/homepage.html", true)
          .and()
          .logout().logoutUrl("/baeldung/logout")
          .addLogoutHandler(new HeaderWriterLogoutHandler(
            new ClearSiteDataHeaderWriter(
              ClearSiteDataHeaderWriter.Directive.CACHE,
              ClearSiteDataHeaderWriter.Directive.COOKIES,
              ClearSiteDataHeaderWriter.Directive.STORAGE)));
    }
}
```

在这里，我们使用 Spring Security 实现了登录和注销页面。因此，Spring 将添加一个Clear-Site-Data标头来响应所有/baeldung/logout请求：

```plaintext
Clear-Site-Data: "cache", "cookies", "storage"
```

如果我们现在使用 curl 并向https://localhost:8080/baeldung/logout发送请求，我们将获得以下标头作为响应：

```bash
{ [5 bytes data]
< HTTP/1.1 302
< Clear-Site-Data: "cache", "cookies", "storage"
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Location: https://localhost:8080/login.html?logout
< Content-Length: 0
< Date: Tue, 17 Mar 2020 17:12:23 GMT
```

## 5. 总结

在本文中，我们研究了浏览器缓存关键用户数据的影响，即使它不是必需的。例如，浏览器不应在用户退出网站后缓存数据。

然后我们看到了 HTTP 的 Clear-Site-Data 响应标头如何允许网站强制浏览器清除本地缓存的数据。

最后，我们在 Spring Security 中使用ClearSiteDataHeaderWriter 实现了一个注销页面， 以将此标头添加到控制器的响应中。
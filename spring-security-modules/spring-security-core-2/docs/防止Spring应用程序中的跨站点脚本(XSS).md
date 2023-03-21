## 1. 概述

在构建Spring Web应用程序时，应用程序的安全方面很重要。[跨站点脚本(XSS)](https://en.wikipedia.org/wiki/Cross-site_scripting)是对Web程序最严重的攻击之一。

防止XSS攻击是Spring应用程序中的一个挑战。Spring提供了内置帮助来实现全面保护。

在本教程中，我们将使用可用的Spring Security功能。

## 2. 什么是跨站脚本(XSS)攻击？

### 2.1 问题的定义

XSS是一种常见的注入攻击类型。在XSS中，攻击者试图在Web应用程序中执行恶意代码。他们通过Web浏览器或[Postman](https://www.baeldung.com/postman-testing-collections)等HTTP客户端工具与Web应用程序交互。

XSS攻击有两种类型：

+ 反射或非持久型XSS
+ 存储型或持久型XSS

在反射型或非持久型XSS中，不受信任的用户数据被提交到Web应用程序，该应用程序会立即在响应中返回，从而将不信任的内容添加到页面中。Web浏览器假定代码来自Web服务器并执行它。这可能允许黑客向你发送一个链接，当你点击该链接时，会导致你的浏览器从你使用的站点检索你的私人数据，然后让你的浏览器将其转发给黑客的服务器。

在存储型或持久型XSS中，攻击者的输入由Web服务器存储。随后，任何未来的访问者都可能执行该恶意代码。

### 2.2 防御攻击

防止XSS攻击的主要策略是清理用户输入。

在Spring Web应用程序中，用户的输入是HTTP请求。为了防止攻击，我们应该检查HTTP请求的内容并删除任何可能被服务器或浏览器执行的内容。

对于通过Web浏览器访问的常规Web应用程序，我们可以使用[Spring Security](https://www.baeldung.com/security-spring)的内置功能(Reflected XSS)。

## 3. 使用Spring Security使应用程序XSS安全

默认情况下，Spring Security提供了几个安全头。它包括[X-XSS-Protection](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-XSS-Protection)标头。X-XSS-Protection告诉浏览器阻止看起来像XSS的内容。Spring Security可以自动将此安全标头添加到响应中。为了激活它，我们在Spring Security配置类中配置XSS支持。

使用此功能，浏览器在检测到XSS尝试时不会进行渲染。但是，某些Web浏览器还没有实现XSS审计器。在这种情况下，它们不使用X-XSS-Protection标头。为了克服这个问题，我们还可以使用[Content-Security-Policy(CSP)](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy)功能。

CSP是一个额外的安全层，有助于缓解XSS和数据注入攻击。要启用它，我们需要通过提供WebSecurityConfigurerAdapter bean来配置我们的应用程序以返回Content-Security-Policy标头：

```java
@Configuration
public class SecurityConf {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Ignoring here is only for this example. Normally people would apply their own authentication/authorization policies
        return (web) -> web.ignoring()
              .antMatchers("/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers()
              .xssProtection()
              .and()
              .contentSecurityPolicy("script-src 'self'");
        return http.build();
    }
}
```

## 4. 总结

在本文中，我们了解了如何使用Spring Security的xssProtection功能来防止XSS攻击。
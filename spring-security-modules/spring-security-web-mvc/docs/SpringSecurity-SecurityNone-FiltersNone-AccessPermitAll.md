## 1. 概述

Spring Security 提供了几种机制来将请求模式配置为不安全或允许所有访问。取决于这些机制中的每一个——这可能意味着根本不在该路径上运行安全过滤器链，或者运行过滤器链并允许访问。

## 进一步阅读：

## [Spring Security – 角色和特权](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)

如何映射 Spring Security 应用程序的角色和权限：设置、身份验证和注册过程。

[阅读更多](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)→

## 2.访问=”permitAll”

使用access=”permitAll”设置<intercept-url>元素将配置授权，以便允许该特定路径上的所有请求：

```xml
<intercept-url pattern="/login" access="permitAll" />
```

或者，通过Java配置：

```java
http.authorizeRequests().antMatchers("/login").permitAll();
```

这是在不禁用安全过滤器的情况下实现的——这些过滤器仍在运行，因此任何与 Spring Security 相关的功能仍然可用。

## 3.过滤器=“无”

这是 Spring 3.1 之前的功能，在 Spring 3.1 中已被弃用和替换。

filters属性完全禁用该特定请求路径上的 Spring Security 过滤器链：

```xml
<intercept-url pattern="/login" filters="none" />
```

当请求的处理需要 Spring Security 的某些功能时，这可能会导致问题。

由于这是 Spring 3.0 之后的已弃用功能，因此在 Spring 3.1 中使用它会导致启动时出现运行时异常：

```bash
SEVERE: Context initialization failed
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: The use of "filters='none'" is no longer supported. 
Please define a separate <http> element for the pattern you want to exclude 
and use the attribute "security='none'".
Offending resource: class path resource [webSecurityConfig.xml]
	at o.s.b.f.p.FailFastProblemReporter.error(FailFastProblemReporter.java:68)
```

## 4.安全=“无”

正如我们在上面的错误消息中看到的，Spring 3.1 将filters=”none”替换为一个新的表达式 – security=”none”。

范围也发生了变化——不再在<intercept-url>元素级别指定。相反，Spring 3.1 允许定义多个<http>元素——每个元素都有自己的安全过滤器链配置。因此，新的安全属性现在属于<http>元素级别。

在实践中，这将如下所示：

```xml
<http pattern="/resources/" security="none"/>
```

或者使用Java配置：

```java
web.ignoring().antMatchers("/resources/");
```

而不是旧的：

```xml
<intercept-url pattern="/resources/" filters="none"/>
```

与filters=”none”类似，这也将完全禁用该请求路径的 Security 过滤器链——因此当在应用程序中处理请求时，Spring Security 功能将不可用。

对于上面的示例来说这不是问题，这些示例主要处理提供静态资源——没有实际处理发生。但是，如果请求以某种方式以编程方式处理 - 那么诸如requires-channel、访问当前用户或调用安全方法等安全功能将不可用。

出于同样的原因，没有必要在已经配置了security=”none”的<http>元素上指定其他属性，因为该请求路径是不安全的，并且这些属性将被忽略。

或者，可以使用access='IS_AUTHENTICATED_ANONYMOUSLY'来允许匿名访问。

## 5. 安全注意事项=“none”

当使用多个<http>元素时，其中一些配置为security=”none”，请记住这些元素的定义顺序很重要。我们希望首先拥有特定的<http>路径，最后遵循通用模式。

还要注意，如果一个<http>元素没有指定一个模式，那么默认情况下，它映射到通用匹配模式——“/”——所以同样，这个元素需要放在最后。如果元素的顺序不正确，则安全过滤器链的创建将失败：

```bash
Caused by: java.lang.IllegalArgumentException: A universal match pattern ('/') 
is defined  before other patterns in the filter chain, causing them to be ignored. 
Please check the ordering in your <security:http> namespace or FilterChainProxy bean configuration
	at o.s.s.c.h.DefaultFilterChainValidator.checkPathOrder(DefaultFilterChainValidator.java:49)
	at o.s.s.c.h.DefaultFilterChainValidator.validate(DefaultFilterChainValidator.java:39)
```

## 六. 总结

本文讨论了使用 Spring Security 允许访问路径的选项——重点关注filters=”none”、security=”none” 和 access=”permitAll”之间的区别。
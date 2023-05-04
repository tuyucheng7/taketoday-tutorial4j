## 1. 问题

本文讨论一个Spring Security配置问题——应用程序引导过程抛出以下异常：

```bash
SEVERE: Exception starting filter springSecurityFilterChain
org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No bean named 'springSecurityFilterChain' is defined
```

## 延伸阅读：

## [Spring SecurityJava Config简介](https://www.baeldung.com/java-config-spring-security)

Spring Security的JavaConfig 快速实用指南

[阅读更多](https://www.baeldung.com/java-config-spring-security)→

## [Spring Security5 - OAuth2 登录](https://www.baeldung.com/spring-security-5-oauth2-login)

了解如何使用Spring Security5 中的 OAuth2 使用 Facebook、Google 或其他凭据对用户进行身份验证。

[阅读更多](https://www.baeldung.com/spring-security-5-oauth2-login)→

## [Servlet 3 异步支持与Spring MVC和Spring Security](https://www.baeldung.com/spring-mvc-async-security)

快速介绍Spring Security对Spring MVC中异步请求的支持。

[阅读更多](https://www.baeldung.com/spring-mvc-async-security)→

## 2. 原因

这个异常的原因很简单——Spring Security寻找一个名为springSecurityFilterChain(默认)的 bean，但找不到它。这个 bean 是web.xml中定义的主Spring SecurityFilter —— DelegatingFilterProxy所需要的：

```xml
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>springSecurityFilterChain</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

这只是一个将其所有逻辑委托给springSecurityFilterChain bean 的代理。

## 3.解决方案

上下文中缺少此 bean 的最常见原因是安全XML配置没有定义<http\>元素：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xmlns:beans="http://www.springframework.org/schema/beans"
  xmlns:sec="http://www.springframework.org/schema/security"
  xsi:schemaLocation="
    http://www.springframework.org/schema/security
    http://www.springframework.org/schema/security/spring-security-3.1.xsd
    http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.2.xsd">

</beans:beans>
```

如果XML配置使用安全命名空间——如上例，那么声明一个简单的 <http\> 元素将确保过滤器 bean 被创建并且一切都正确启动：

```xml
<http auto-config='true'>
    <intercept-url pattern="/**" access="ROLE_USER" />
</http>
```

另一个可能的原因是安全配置根本没有导入到 Web 应用程序的整体上下文中。

如果安全XML配置文件名为springSecurityConfig.xml，请确保已导入资源：

```java
@ImportResource({"classpath:springSecurityConfig.xml"})
```

或者在XML中：

```xml
<import resource="classpath:springSecurityConfig.xml" />
```

最后，可以在web.xml中更改过滤器 bean 的默认名称——通常使用带有Spring Security的现有过滤器：

```xml
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>
      org.springframework.web.filter.DelegatingFilterProxy
    </filter-class>
    <init-param>
        <param-name>targetBeanName</param-name>
        <param-value>customFilter</param-value>
    </init-param>
</filter>
```

## 4。总结

本文讨论了一个非常具体的Spring Security问题——缺少过滤器链 bean——并展示了这个常见问题的解决方案。
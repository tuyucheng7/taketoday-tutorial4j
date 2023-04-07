## **1. 问题**

本文讨论一个 Spring Security 配置问题——应用程序引导过程抛出以下异常：

```bash
SEVERE: Exception starting filter springSecurityFilterChain
org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No bean named 'springSecurityFilterChain' is defined复制
```

## 延伸阅读：

## [Spring Security Java Config简介](https://www.baeldung.com/java-config-spring-security)

Spring Security 的 Java Config 快速实用指南

[阅读更多](https://www.baeldung.com/java-config-spring-security)→

## [Spring Security 5 - OAuth2 登录](https://www.baeldung.com/spring-security-5-oauth2-login)

了解如何使用 Spring Security 5 中的 OAuth2 使用 Facebook、Google 或其他凭据对用户进行身份验证。

[阅读更多](https://www.baeldung.com/spring-security-5-oauth2-login)→

## [Servlet 3 异步支持与 Spring MVC 和 Spring Security](https://www.baeldung.com/spring-mvc-async-security)

快速介绍 Spring Security 对 Spring MVC 中异步请求的支持。

[阅读更多](https://www.baeldung.com/spring-mvc-async-security)→

## **2. 原因**

这个异常的原因很简单——Spring Security 寻找一个名为*springSecurityFilterChain*（默认）的 bean，但找不到它。*这个 bean 是web.xml*中定义的主**Spring Security Filter** —— *DelegatingFilterProxy*所需要的：

```xml
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>springSecurityFilterChain</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>复制
```

这只是一个将其所有逻辑委托给*springSecurityFilterChain* bean 的代理。

## **3.解决方案**

上下文中缺少此 bean 的最常见原因是安全 XML 配置没有**定义\*<http>\*元素**：

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

</beans:beans>复制
```

如果 XML 配置使用安全命名空间——如上例，那么声明**一个简单的 <http> 元素**将确保过滤器 bean 被创建并且一切都正确启动：

```xml
<http auto-config='true'>
    <intercept-url pattern="/**" access="ROLE_USER" />
</http>复制
```

另一个可能的原因是**安全配置根本没有导入**到 Web 应用程序的整体上下文中。

如果安全 XML 配置文件名为*springSecurityConfig.xml*，请确保**已导入资源**：

```java
@ImportResource({"classpath:springSecurityConfig.xml"})复制
```

或者在 XML 中：

```xml
<import resource="classpath:springSecurityConfig.xml" />复制
```

*最后，可以在web.xml*中更改过滤器 bean 的默认名称——通常使用带有 Spring Security 的现有过滤器：

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
</filter>复制
```

## **4。结论**

本文讨论了一个非常具体的 Spring Security 问题——缺少过滤器链 bean——并展示了这个常见问题的解决方案。
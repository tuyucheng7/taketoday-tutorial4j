## 1. 概述

在这个快速教程中，我们介绍如何在Spring Security过滤器链中编写自定义过滤器。

## 2. 创建过滤器

Spring Security默认提供了许多过滤器，大多数时候这些过滤器就足够了。**当然，有时需要通过创建一个新过滤器来放入链中实现新功能**。

我们将从实现[org.springframework.web.filter.GenericFilterBean](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/filter/GenericFilterBean.html)开始。GenericFilterBean是Spring感知的简单[javax.servlet.Filter](https://docs.oracle.com/javaee/7/api/javax/servlet/Filter.html)实现。

我们只需要实现一个方法：

```java
public class CustomFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}
}
```

## 3. 在安全配置中使用过滤器

我们可以自由选择XML配置或Java配置来将过滤器注入到Spring Security配置中。

### 3.1 Java配置

我们可以通过创建[SecurityFilterChain](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/SecurityFilterChain.html) bean以编程方式注册过滤器。

例如，它与[HttpSecurity](https://docs.spring.io/spring-security/site/docs/5.0.0.M5/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html)实例上的addFilterAfter方法一起使用：

```java
@Configuration
public class CustomWebSecurityConfigurerAdapter {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }
}

```

还有几种可能的方法：

-   [addFilterBefore(filter, class)](https://docs.spring.io/spring-security/site/docs/5.0.0.M5/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html#addFilterBefore(javax.servlet.Filter, java.lang.Class))在指定过滤器类的位置之前添加一个过滤器。
-   [addFilterAfter(filter, class)](https://docs.spring.io/spring-security/site/docs/5.0.0.M5/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html#addFilterAfter(javax.servlet.Filter, java.lang.Class))在指定过滤器类的位置之后添加一个过滤器。
-   [addFilterAt(filter, class)](https://docs.spring.io/spring-security/site/docs/5.0.0.M5/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html)在指定过滤器类的位置添加一个过滤器。
-   [addFilter(filter)](https://docs.spring.io/spring-security/site/docs/5.0.0.M5/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html#addFilter(javax.servlet.Filter))添加一个过滤器，该过滤器必须是Spring Security提供的过滤器的实例或扩展之一。

### 3.2 XML配置

我们可以使用custom-filter标签和[这些名称](https://docs.spring.io/spring-security/site/docs/4.2.13.RELEASE/reference/htmlsingle/#ns-custom-filters)之一将过滤器添加到链中，以指定过滤器的位置。

例如，可以通过after属性指出：

```xml
<http>
    <custom-filter after="BASIC_AUTH_FILTER" ref="myFilter" />
</http>

<beans:bean id="myFilter" class="cn.tuyucheng.taketoday.security.filter.CustomFilter"/>
```

以下是指定过滤器在堆栈中的确切位置的所有属性：

-   after描述了过滤器，在该过滤器之后，自定义过滤器将被放置在链中。
-   before定义了过滤器，我们的过滤器应该放在链中。
-   position允许用自定义过滤器替换显式位置的标准过滤器。

## 4. 总结

在这篇快速文章中，我们创建了一个自定义过滤器并将其注入到Spring Security过滤器链中。
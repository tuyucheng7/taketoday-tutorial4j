## 1. 概述

DelegatingFilterProxy是一个Servlet过滤器，它允许将控制权传递给有权访问Spring应用程序上下文的Filter类。Spring Security框架内部严重依赖这种技术。

在本教程中，我们会详细介绍它。

## 2. DelegatingFilterProxy

DelegatingFilterProxy的Javadoc声明它是一个：

> 标准Servlet过滤器的代理，委托给实现Filter接口的Spring管理的bean。

当使用Servlet过滤器时，我们显然需要在Java配置类或web.xml中将它们声明为过滤器类，否则Servlet容器将忽略它们。Spring的DelegatingFilterProxy提供了web.xml和应用程序上下文之间的链接。

### 2.1 DelegatingFilterProxy的内部机制

让我们来看看DelegatingFilterProxy如何将控制权转移到我们的Spring bean。

在初始化期间，DelegatingFilterProxy获取过滤器名称，并从Spring应用程序上下文中检索具有该名称的bean。此bean必须是javax.Servlet.Filter类型，即普通的Servlet过滤器。然后，传入的请求将被传递给这个过滤器bean。

**简而言之，DelegatingFilterProxy的doFilter()方法会将所有调用委托给Spring bean，使我们能够在过滤器bean中使用所有Spring功能**。

如果我们使用基于Java的配置，那么在ApplicationInitializer中的过滤器注册将定义为：

```java
@Override
protected javax.servlet.Filter[] getServletFilters() {
    DelegatingFilterProxy delegateFilterProxy = new DelegatingFilterProxy();
    delegateFilterProxy.setTargetBeanName("applicationFilter");
    return new Filter[]{delegateFilterProxy};
}
```

如果我们使用XML，那么在web.xml文件中的配置为：

```xml
<filter>
    <filter-name>applicationFilter</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
```

这意味着任何请求都会通过名为applicationFilter的Spring bean的过滤器。

### 2.2 DelegatingFilterProxy的需求

DelegatingFilterProxy是Spring Web模块中的一个类。它提供了使HTTP调用在到达实际目的地之前通过过滤器的功能。在DelegatingFilterProxy的帮助下，可以将实现javax.Servlet.Filter接口的类注入到过滤器链中。

例如，Spring Security使用DelegatingFilterProxy来利用Spring的依赖注入功能和安全过滤器的生命周期接口。

DelegatingFilterProxy还通过在Spring的应用程序上下文或web.xml中提供配置，根据请求URI路径调用特定或多个过滤器。

## 3. 创建自定义过滤器

如上所述，DelegatingFilterProxy本身是一个Servlet过滤器，它委托给实现Filter接口的特定Spring管理的bean。

在接下来的几节中，我们将创建一个自定义过滤器并使用基于Java和XML的方式对其进行配置。

### 3.1 过滤器类

我们将创建一个简单的过滤器，用于在请求进一步处理之前记录请求信息：

```java
@Component("loggingFilter")
public class CustomFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        LOGGER.info("Request Info : " + req);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
```

CustomFilter实现了javax.Servlet.Filter。这个类上带有@Component注解，可以在Spring应用程序上下文中注册为bean。这样，DelegatingFilterProxy类就可以在初始化过滤器链时找到我们的过滤器类。

**请注意，Spring bean的名称必须与在ApplicationInitializer类或web.xml中注册自定义过滤器时提供的filter-name中的值相同**，因为DelegatingFilterProxy类将在应用程序上下文中查找具有完全相同名称的过滤器bean。

如果找不到具有此名称的bean，它将在应用程序启动时引发异常。

### 3.2 通过Java配置过滤器

要使用Java配置注册自定义过滤器，我们需要重写AbstractAnnotationConfigDispatcherServletInitializer的getServletFilters()方法：

```java
public class ApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected javax.servlet.Filter[] getServletFilters() {
        DelegatingFilterProxy delegateFilterProxy = new DelegatingFilterProxy();
        delegateFilterProxy.setTargetBeanName("loggingFilter");
        return new Filter[]{delegateFilterProxy};
    }
    // some other methods here ...
}
```

### 3.3 通过web.xml配置过滤器

让我们看看web.xml中的过滤器配置是怎样的：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <filter>
        <filter-name>loggingFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>loggingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
```

filter-class标签指定的是DelegatingFilterProxy类型，而不是我们自己创建的过滤器类。如果我们运行此代码并访问任何URL，则CustomFilter的doFilter()方法将被执行并在日志中输出请求的详细信息。

## 4. 总结

在本文中，我们介绍了DelegatingFilterProxy的工作原理以及如何使用它。

Spring Security广泛使用DelegatingFilterProxy来保护Web API调用和资源，防止未经授权的访问。
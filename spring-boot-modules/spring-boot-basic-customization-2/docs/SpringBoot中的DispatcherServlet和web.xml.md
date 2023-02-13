## 1. 概述

[DispatcherServlet](https://www.baeldung.com/spring-dispatcherservlet)是Spring Web应用程序中的前端控制器。它用于在Spring MVC中创建Web应用程序和REST服务，在传统的Spring Web应用程序中，这个Servlet是在web.xml文件中定义的。

在本教程中，我们会将代码从web.xml文件迁移到Spring Boot应用程序中的DispatcherServlet。此外，我们将把web.xml中的Filter、Servlet和Listener类映射到Spring Boot应用程序。

## 2. Maven依赖

首先，我们必须将[spring-boot-starter-web](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-web) Maven依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 3. DispatcherServlet

DispatcherServlet接收所有HTTP请求并将它们委托给控制器类。

**在Servlet 3.x规范之前，DispatcherServlet将在Spring MVC应用程序的web.xml文件中注册**。从Servlet 3.x规范开始，我们可以使用ServletContainerInitializer以编程方式注册Servlet。

让我们看看web.xml文件中的DispatcherServlet示例配置：

```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

Spring Boot提供了spring-boot-starter-web库，用于使用Spring MVC开发Web应用程序。Spring Boot的主要特性之一是自动配置。**Spring Boot会自动注册和配置DispatcherServlet**，因此，我们不需要手动注册DispatcherServlet。

默认情况下，spring-boot-starter-web启动器将DispatcherServlet配置为URL模式“/”。因此，我们不需要在web.xml文件中为上面的DispatcherServlet示例完成任何额外的配置。但是，我们可以通过使用application.properties文件中的server.servlet.*属性自定义URL模式：

```properties
server.servlet.context-path=/demo
spring.mvc.servlet.path=/tuyucheng
```

通过这些自定义，DispatcherServlet被配置为处理URL模式“/tuyucheng”并且根上下文路径将是“/demo”。因此，DispatcherServlet监听http://localhost:8080/demo/tuyucheng/。

## 4. 应用配置

Spring MVC Web应用程序使用web.xml文件作为部署描述符文件。此外，它还定义了URL路径和web.xml文件中的Servlet之间的映射。

Spring Boot不再是这种情况。如果我们需要一个特殊的过滤器，我们可以在Java类配置中注册它。web.xml文件包括过滤器、Servlet和监听器。

**当我们想从传统的Spring MVC迁移到现代的Spring Boot应用程序时，我们如何将web.xml移植到新的Spring Boot应用程序**？在Spring Boot应用程序中，我们可以通过多种方式添加这些概念。

### 4.1 注册过滤器

让我们通过实现[Filter](https://www.baeldung.com/spring-boot-add-filter)接口来创建一个过滤器：

```java
@Component
public class CustomFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(CustomFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("CustomFilter is invoked");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
```

如果没有Spring Boot，我们将在web.xml文件中配置我们的CustomFilter：

```xml
<filter>
    <filter-name>customFilter</filter-name>
    <filter-class>CustomFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>customFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

为了让Spring Boot能够识别过滤器，我们只需要将其定义为带有@Component注解的bean。

### 4.2 注册Servlet

让我们通过扩展HttpServlet类来定义一个Servlet：

```java
public class CustomServlet extends HttpServlet {

    Logger logger = LoggerFactory.getLogger(CustomServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("CustomServlet doGet() method is invoked");
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("CustomServlet doPost() method is invoked");
        super.doPost(req, resp);
    }
}
```

如果没有Spring Boot，我们将在web.xml文件中配置我们的CustomServlet：

```xml
<servlet>
    <servlet-name>customServlet</servlet-name>
    <servlet-class>CustomServlet</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>customServlet</servlet-name>
    <url-pattern>/servlet</url-pattern>
</servlet-mapping>
```

在Spring Boot应用程序中，Servlet注册为Spring @Bean或通过使用嵌入式容器扫描@WebServlet注解类。

使用Spring @Bean方法，我们可以使用ServletRegistrationBean类来[注册Servlet](https://www.baeldung.com/register-servlet#registering-servlets-in-spring-boot)。

因此，我们将CustomServlet定义为具有ServletRegistrationBean类的bean：


```java
@Configuration
public class WebConf {

    @Bean
    public ServletRegistrationBean customServletBean() {
        return new ServletRegistrationBean(new CustomServlet(), "/servlet");
    }
}
```

### 4.3 注册监听器

让我们通过扩展ServletContextListener类来定义一个监听器：

```java
public class CustomListener implements ServletContextListener {

    Logger logger = LoggerFactory.getLogger(CustomListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("CustomListener is initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("CustomListener is destroyed");
    }
}
```

如果没有Spring Boot，我们将在web.xml文件中配置我们的CustomListener：

```xml
<listener>
    <listener-class>CustomListener</listener-class>
</listener>
```

要在Spring Boot应用程序中定义监听器，我们可以使用@Bean或@WebListener注解。

使用Spring @Bean方法，我们可以使用ServletListenerRegistrationBean类来注册监听器。

因此，让我们使用ServletListenerRegistrationBean类将CustomListener定义为一个bean：

```java
@Configuration
public class WebConf {

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> customListenerBean() {
        ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean();
        bean.setListener(new CustomListener());
        return bean;
    }
}
```

启动我们的应用程序后，我们可以检查日志输出以确认监听器已成功初始化：

<img src="../assets/img.png">

## 5. 总结

在这个快速教程中，我们了解了如何在Spring Boot应用程序中定义DispatcherServlet和web.xml元素，包括过滤器、Servlet和监听器。
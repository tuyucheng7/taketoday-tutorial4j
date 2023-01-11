## 1. 简介

在这个快速教程中，我们将重点关注Servlet 3 对异步请求的支持，以及 Spring MVC 和 Spring Security 如何处理这些.

Web 应用程序中异步性的最基本动机是处理长时间运行的请求。在大多数用例中，我们需要确保将 Spring Security 主体传播到这些线程。

当然，Spring Security还与 MVC 范围之外的[@Async](https://www.baeldung.com/spring-security-async-principal-propagation)[集成](https://www.baeldung.com/spring-security-async-principal-propagation)并处理 HTTP 请求。

## 2.Maven依赖

为了在 Spring MVC 中使用异步集成，我们需要在pom.xml中包含以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>5.7.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.7.3</version>
</dependency>

```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.security")找到最新版本的 Spring Security 依赖项。

## 3. Spring MVC 和@Async

根据官方[文档](https://spring.io/blog/2012/12/17/spring-security-3-2-m1-highlights-servlet-3-api-support/#servlet3-async)，Spring Security 与[WebAsyncManager](http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/web/context/request/async/WebAsyncManager.html)集成。

第一步是确保我们的springSecurityFilterChain设置为处理异步请求。我们可以在Java配置中做到这一点，通过在我们的Servlet配置类中添加以下行：

```xml
dispatcher.setAsyncSupported(true);
```

或在 XML 配置中：

```java
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    <async-supported>true</async-supported>
</filter>
<filter-mapping>
    <filter-name>springSecurityFilterChain</filter-name>
    <url-pattern>/</url-pattern>
    <dispatcher>REQUEST</dispatcher>
    <dispatcher>ASYNC</dispatcher>
</filter-mapping>
```

我们还需要在我们的 servlet 配置中启用async-supported参数：

```xml
<servlet>
    ...
    <async-supported>true</async-supported>
    ...
</servlet>
```

现在我们已经准备好发送带有SecurityContext的异步请求了。

Spring Security 中的内部机制将确保当在另一个线程中提交响应导致用户注销时，我们的SecurityContext不再被清除。

## 4. 用例

让我们用一个简单的例子来看看这个：

```java
@Override
public Callable<Boolean> checkIfPrincipalPropagated() {
    Object before 
      = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("Before new thread: " + before);

    return new Callable<Boolean>() {
        public Boolean call() throws Exception {
            Object after 
              = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("New thread: " + after);
            return before == after;
        }
    };
}
```

我们要检查 Spring SecurityContext是否传播到新线程。

上面介绍的方法将自动执行包含SecurityContext的Callable，如日志中所示：

```plaintext
web - 2017-01-02 10:42:19,011 [http-nio-8081-exec-3] INFO
  o.baeldung.web.service.AsyncService - Before new thread:
  org.springframework.security.core.userdetails.User@76507e51:
  Username: temporary; Password: [PROTECTED]; Enabled: true;
  AccountNonExpired: true; credentialsNonExpired: true;
  AccountNonLocked: true; Granted Authorities: ROLE_ADMIN

web - 2017-01-02 10:42:19,020 [MvcAsync1] INFO
  o.baeldung.web.service.AsyncService - New thread:
  org.springframework.security.core.userdetails.User@76507e51:
  Username: temporary; Password: [PROTECTED]; Enabled: true;
  AccountNonExpired: true; credentialsNonExpired: true;
  AccountNonLocked: true; Granted Authorities: ROLE_ADMIN
```

如果不设置要传播的SecurityContext，第二个请求将以空值结束。

还有其他重要的用例将异步请求与传播的SecurityContext一起使用：

-   我们想要发出多个可以并行运行并且可能需要大量时间来执行的外部请求
-   我们需要在本地进行一些重要的处理，并且我们的外部请求可以并行执行
-   其他代表即发即弃的场景，例如发送电子邮件

请注意，如果我们的多个方法调用之前以同步方式链接在一起，则将它们转换为异步方法可能需要同步结果。

## 5. 总结

在这个简短的教程中，我们演示了 Spring 对在经过身份验证的上下文中处理异步请求的支持。

从编程模型的角度来看，新功能看似简单。但肯定有一些方面确实需要更深入的了解。
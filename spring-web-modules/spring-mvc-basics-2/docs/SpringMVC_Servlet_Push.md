## 1. 概述

服务器推送技术是HTTP/2(RFC 7540)的一部分，它允许我们从服务器端主动向客户端发送资源。
这是HTTP/1.X基于拉取方法的一个重大变化。

Spring 5带来的新特性之一是Jakarta EE 8 Servlet 4.0 API附带的服务器推送支持。
在本文中，我们介绍如何使用服务器推送并将其与Spring MVC控制器集成。

## 2. Maven依赖

```xml
<!-- @formatter:off -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.13</version>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>
<!-- @formatter:on -->
```

## 3. HTTP/2 需求

**要使用服务器推送，我们需要在支持HTTP/2和Servlet 4.0 API的容器中运行应用程序**。

此外，我们还需要**客户端的HTTP/2支持**；当然，目前大多数浏览器都支持这种功能。

## 4. PushBuilder

PushBuilder接口负责实现服务器推送。在Spring MVC中，我们可以注入PushBuilder作为@RequestMapping注解方法的参数。

**此时，重要的是要知道：如果客户端不支持HTTP/2，那么参数引用将作为null传递**。

以下是PushBuilder接口提供的核心API：

+ path(String path) – 指示我们将要发送的资源
+ push() - 将资源发送到客户端
+ addHeader(String name, String value) - 指示我们将用于推送资源的header

## 5. 快速案例

为了演示它的使用，我们创建带有一个logo.png的demo.jsp页面。

```html
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>PushBuilder demo</title>
</head>
<body>
<span>PushBuilder demo</span>
<br>
<img src="<c:url value="/resources/logo.png"/>" alt="Logo"
height="126" width="411">
<br>
<!-- Content -->
</body>
</html>
```

并且使用PushController公开两个端点，一个使用服务器推送，另一个不使用：

```java
@Controller
public class PushController {

    @GetMapping(path = "/demoWithPush")
    public String demoWithPush(PushBuilder pushBuilder) {
        if (null != pushBuilder) {
            pushBuilder.path("resources/logo.png").push();
        }
        return "demo";
    }

    @GetMapping(path = "/demoWithoutPush")
    public String demoWithoutPush() {
        return "demo";
    }
}
```

使用Chrome开发工具，我们可以通过调用这两个端点来观察区别。

当我们调用demoWithoutPush()方法时，视图和资源由客户端使用拉取技术发布和使用：

<img src="../assets/push1.png">

当我们调用demoWithPush()方法时，我们可以看到服务器的推送使用情况，以及服务器如何提前交付资源，从而缩短了加载时间：

<img src="../assets/push2.png">

在许多情况下，服务器推送技术可以缩短应用程序页面的加载时间。
也就是说，我们确实需要考虑，尽管我们减少了延迟，但我们可能会增加带宽，这取决于我们服务的资源数量。

将此技术与缓存、资源最小化和CDN等其他策略相结合，并在应用程序上运行性能测试，以确定使用服务器推送的理想端点。

## 6. 概述

在本文中，我们演示了如何使用PushBuilder接口将服务器推送技术与Spring MVC结合使用，并比较了使用它与标准拉取技术时的加载时间。
## 1. 简介

Spring MVC是使用前端控制器模式构建的传统应用程序。[DispatcherServlet](https://www.baeldung.com/spring-dispatcherservlet)作为前端控制器，负责路由和请求处理。

与任何Web应用程序或网站一样，当找不到所请求的资源时，Spring MVC会返回HTTP 404响应代码。在本教程中，我们将了解Spring MVC中404错误的常见原因。

## 2. 404响应的可能原因

### 2.1 URI错误

假设我们有一个映射到/greeting并呈现greeting.jsp的GreetingController ：

```java
@Controller
public class GreetingController {

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public String get(ModelMap model) {
        model.addAttribute("message", "Hello, World!");
        return "greeting";
    }
}
```

相应的视图呈现消息变量的值：

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Greeting</title>
    </head>
    <body>
        <h2>${message}</h2>
    </body>
</html>
```

正如预期的那样，向/greeting发出GET请求有效：

```bash
curl http://localhost:8080/greeting
```

我们将看到一个带有消息“Hello World”的HTML页面：

```html
<html>
    <head>
        <title>Greeting</title>
    </head>
    <body>
        <h2>Hello, World!</h2>
    </body>
</html>
```

看到404的最常见原因之一是使用了不正确的URI。例如，向/greetings而不是/greeting发出GET请求是错误的：

```bash
curl http://localhost:8080/greetings
```

在这种情况下，我们会在服务器日志中看到一条警告消息：

```bash
[http-nio-8080-exec-6] WARN  o.s.web.servlet.PageNotFound - 
  No mapping found for HTTP request with URI [/greetings] in DispatcherServlet with name 'mvc'
```

客户端会看到一个错误页面：

```html
<html>
    <head>
        <title>Home</title>
    </head>
    <body>
        <h1>Http Error Code : 404. Resource not found</h1>
    </body>
</html>
```

**为避免这种情况，我们需要确保已正确输入URI**。

### 2.2 不正确的Servlet映射

如前所述，DispatcherServlet是Spring MVC中的前端控制器。因此，就像在标准的基于Servlet的应用程序中一样，我们需要使用web.xml文件为Servlet创建一个映射。

我们在servlet标签内定义Servlet，并将其映射到servlet-mapping标签内的URI。我们需要确保url-pattern的值是正确的，因为看到将Servlet映射到“/*”的建议很常见，注意尾随星号：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app ...>
    <!-- Additional config omitted -->
    <servlet>
        <servlet-name>mvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>mvc</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>
    <!-- Additional config omitted -->
</web-app>
```

现在，如果我们请求/greeting，我们会在服务器日志中看到一条警告：

```bash
curl http://localhost:8080/greeting
WARN  o.s.web.servlet.PageNotFound - No mapping found for HTTP request with URI 
  [/WEB-INF/view/greeting.jsp] in DispatcherServlet with name 'mvc'
```

这次错误指出未找到greeting.jsp，用户看到一个空白页面。

要修复错误，我们需要将DispatcherServlet映射到“/”(没有尾随星号)：

```xml
<servlet-mapping>
    <servlet-name>mvc</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

修复映射后，一切都应该正常工作，请求/greeting现在显示消息“Hello, World!”：

```bash
curl http://localhost:8080/greeting
<html>
    <head>
        <title>Greeting</title>
    </head>
    <body>
        <h2>Hello, World!</h2>
    </body>
</html>
```

问题背后的原因是，如果我们将DispatcherServlet映射到“/*”，那么我们就是在告诉应用程序到达我们应用程序的每个请求都将由DispatcherServlet提供服务。但是，这不是正确的方法，因为DispatcherServlet无法执行此操作。相反，Spring MVC期望ViewResolver的实现为JSP文件等视图提供服务。

## 3. 总结

在这篇快速文章中，我们解释了如何在Spring MVC中调试404错误。我们讨论了从我们的Spring应用程序收到404响应的两个最常见的原因。第一个是在发出请求时使用了错误的URI。第二个是将DispatcherServlet映射到web.xml中错误的url-pattern。
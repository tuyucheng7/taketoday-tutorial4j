## 1. 简介

在本教程中，我们将处理 [Jakarta EE Servlet](https://www.baeldung.com/intro-to-servlets)应用程序中的异常——以便在发生错误时提供优雅的预期结果。

## 2. Jakarta EE Servlet 异常

[首先，我们将使用API 注解](https://tomcat.apache.org/tomcat-9.0-doc/servletapi/index.html)定义一个 Servlet (查看 [Servlets Intro](https://www.baeldung.com/intro-to-servlets)了解更多详细信息)，并使用默认的GET处理器来抛出异常：

```java
@WebServlet(urlPatterns = "/randomError")
public class RandomErrorServlet extends HttpServlet {

    @Override
    protected void doGet(
      HttpServletRequest req, 
      HttpServletResponse resp) {
        throw new IllegalStateException("Random error");
    }
}
```

## 3. 默认错误处理

现在让我们简单地将应用程序部署到我们的 servlet 容器中(我们假设应用程序在http://localhost:8080/javax-servlets下运行)。

当我们访问地址http://localhost:8080/javax-servlets/randomError时，我们将看到默认的 servlet 错误处理：

[![小服务程序](https://www.baeldung.com/wp-content/uploads/2018/06/servlet.png)](https://www.baeldung.com/wp-content/uploads/2018/06/servlet.png)

默认错误处理由 servlet 容器提供，并且可以在[容器](https://tomcat.apache.org/tomcat-7.0-doc/config/valve.html#Error_Report_Valve)或应用程序级别进行自定义。

## 4.自定义错误处理

我们可以使用web.xml文件描述符定义自定义错误处理，我们可以在其中定义以下类型的策略：

-   状态代码错误处理——它允许我们将 HTTP 错误代码([客户端](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#4xx_Client_errors)和[服务器](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#5xx_Server_errors))映射到静态 HTML 错误页面或错误处理 servlet
-   异常类型错误处理——它允许我们将异常类型映射到静态 HTML 错误页面或错误处理 servlet

### 4.1. HTML 页面的状态代码错误处理

我们可以在web.xml中为 HTTP 404 错误设置自定义错误处理策略：

```xml
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xmlns="http://java.sun.com/xml/ns/javaee"
  xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
    http://java.sun.com/xml/ns/javaee/web-app_3_1.xsd"
  version="3.1">

    <error-page>
        <error-code>404</error-code>
        <location>/error-404.html</location> <!-- /src/main/webapp/error-404.html-->
    </error-page>

</web-app>

```

现在，从浏览器访问 http://localhost:8080/javax-servlets/invalid.html——获取静态 HTML 错误页面。

### 4.2. 使用 Servlet 进行异常类型错误处理

我们可以在web.xml中为java.lang.Exception设置自定义错误处理策略 ：

```xml
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xmlns="http://java.sun.com/xml/ns/javaee"
  xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
    http://java.sun.com/xml/ns/javaee/web-app_3_1.xsd"
  version="3.1">
    <error-page> 
        <exception-type>java.lang.Exception</exception-type> 
        <location>/errorHandler</location> 
    </error-page>
</web-app>
```

在ErrorHandlerServlet中，我们可以使用请求中提供的[错误属性访问错误详细信息：](https://tomcat.apache.org/tomcat-7.0-doc/servletapi/constant-values.html)

```java
@WebServlet(urlPatterns = "/errorHandler")
public class ErrorHandlerServlet extends HttpServlet {

    @Override
    protected void doGet(
      HttpServletRequest req, 
      HttpServletResponse resp) throws IOException {
 
        resp.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("<html><head><title>Error description</title></head><body>");
            writer.write("<h2>Error description</h2>");
            writer.write("<ul>");
            Arrays.asList(
              ERROR_STATUS_CODE, 
              ERROR_EXCEPTION_TYPE, 
              ERROR_MESSAGE)
              .forEach(e ->
                writer.write("<li>" + e + ":" + req.getAttribute(e) + " </li>")
            );
            writer.write("</ul>");
            writer.write("</html></body>");
        }
    }
}

```

现在，我们可以访问 http://localhost:8080/javax-servlets/randomError来查看自定义错误 servlet 的工作情况。

注意：我们在web.xml中定义的异常类型过于宽泛，我们应该更详细地指定我们要处理的所有异常。

我们还可以在ErrorHandlerServlet组件中使用[容器提供的 servlet 记录器](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/ServletContext.html#log-java.lang.String-)来记录其他详细信息：

```java
Exception exception = (Exception) req.getAttribute(ERROR_EXCEPTION);
if (IllegalArgumentException.class.isInstance(exception)) {
    getServletContext()
      .log("Error on an application argument", exception);
}
```

值得了解 servlet 提供的日志记录机制之外的内容，请查看[slf4j 上的指南](https://www.baeldung.com/slf4j-with-log4j2-logback)以获取更多详细信息。

## 5.总结

在这篇简短的文章中，我们了解了 servlet 应用程序中的默认错误处理和指定的自定义错误处理，而无需添加外部组件或库。
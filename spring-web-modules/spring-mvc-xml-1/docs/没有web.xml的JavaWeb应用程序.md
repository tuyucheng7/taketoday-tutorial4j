## 1. 概述

在本教程中，我们将使用[Servlet 3.0+](https://tomcat.apache.org/tomcat-7.0-doc/servletapi/index.html)创建一个Java Web应用程序。

我们将看一下三个注解：@WebServlet、@WebFilter和@WebListener，它们可以帮助我们修改web.xml文件。

## 2. Maven依赖

为了使用这些新注解，我们需要包含[javax.servlet-api](https://search.maven.org/artifact/javax.servlet/javax.servlet-api)依赖项：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
</dependency>
```

## 3. 基于XML的配置

在Servlet 3.0之前，我们会在web.xml文件中配置Java Web应用程序：

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
         version="2.5">
    <listener>
        <listener-class>cn.tuyucheng.taketoday.servlets3.web.listeners.RequestListener</listener-class>
    </listener>
    <servlet>
        <servlet-name>uppercaseServlet</servlet-name>
        <servlet-class>cn.tuyucheng.taketoday.servlets3.web.servlets.UppercaseServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>uppercaseServlet</servlet-name>
        <url-pattern>/uppercase</url-pattern>
    </servlet-mapping>
    <filter>
        <filter-name>emptyParamFilter</filter-name>
        <filter-class>cn.tuyucheng.taketoday.servlets3.web.filters.EmptyParamFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>emptyParamFilter</filter-name>
        <url-pattern>/uppercase</url-pattern>
    </filter-mapping>
</web-app>
```

让我们开始用Servlet 3.0中引入的相应注解替换每个配置部分。

## 4. 小服务程序

JEE 6随Servlet 3.0一起发布，这使我们能够对Servlet定义使用注解，从而最大限度地减少Web应用程序对web.xml文件的使用。

例如，我们可以定义一个Servlet，并用@WebServlet注解暴露出来

让我们为URL模式/uppercase定义一个Servlet，它将输入请求参数的值转换为大写：

```java
@WebServlet(urlPatterns = "/uppercase", name = "uppercaseServlet")
public class UppercaseServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String inputString = request.getParameter("input").toUpperCase();

        PrintWriter out = response.getWriter();
        out.println(inputString);
    }
}
```

请注意，我们为我们现在可以引用的Servlet(uppercaseServlet)定义了一个名称，我们将在下一节中使用它。

使用@WebServlet注解，我们将替换web.xml文件中的servlet和servlet-mapping部分。

## 5. 过滤器

过滤器是用于拦截请求或响应、执行预处理或后处理任务的对象。

我们可以使用@WebFilter注解定义过滤器。

让我们创建一个过滤器来检查输入请求参数是否存在：

```java
@WebFilter(urlPatterns = "/uppercase")
public class EmptyParamFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String inputString = servletRequest.getParameter("input");

        if (inputString != null && inputString.matches("[A-Za-z0-9]+")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            servletResponse.getWriter().println("Missing input parameter");
        }
    }

    // implementations for other methods
}
```

使用@WebFilter注解，我们将替换web.xml文件中的过滤器和过滤器映射部分。

## 6. 监听器

我们经常需要根据某些事件触发操作。这是听众来救援的地方。这些对象将监听事件并执行我们指定的行为。

像以前一样，我们可以使用@WebListener注解定义一个监听器。

让我们创建一个监听器，每次我们向服务器执行请求时都会计数，我们将实现ServletRequestListener，监听ServletRequestEvent：

```java
@WebListener
public class RequestListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        HttpServletRequest request = (HttpServletRequest)event.getServletRequest();
        if (!request.getServletPath().equals("/counter")) {
            ServletContext context = event.getServletContext();
            context.setAttribute("counter", (int) context.getAttribute("counter") + 1);
        }
    }

    // implementations for other methods
}
```

请注意，我们排除了对URL模式/counter的请求。

使用@WebListener注解，我们将替换web.xml文件中的listener部分。

## 7. 构建并运行

对于接下来的内容，请注意为了测试，我们为/counter端点添加了第二个Servlet，它只返回计数器Servlet上下文属性。

所以，让我们使用Tomcat作为应用服务器。

如果我们使用3.1.0之前的maven-war-plugin版本，我们需要将属性[failOnMissingWebXml](https://www.baeldung.com/eclipse-error-web-xml-missing)设置为false。

现在，我们可以将[.war文件部署到Tomcat](https://www.baeldung.com/tomcat-deploy-war)，并访问我们的Servlet。

让我们试试我们的/uppercase端点：

```bash
curl http://localhost:8080/spring-mvc-java/uppercase?input=texttouppercase

TEXTTOUPPERCASE
```

我们还应该看看我们的错误处理看起来如何：

```bash
curl http://localhost:8080/spring-mvc-java/uppercase

Missing input parameter
```

最后，对我们的侦听器进行快速测试：

```bash
curl http://localhost:8080/spring-mvc-java/counter

Request counter: 2
```

## 8. 仍然需要XML

即使在Servlet 3.0中引入了所有功能，在某些用例中我们仍然需要web.xml文件，其中包括：

-   我们不能用注解定义过滤器顺序——如果我们有多个过滤器需要按特定顺序应用，我们仍然需要<filter-mapping\>部分
-   要定义[会话超时](https://www.baeldung.com/servlet-session-timeout)，我们仍然需要使用<session-config\>部分
-   我们仍然需要<security-role\>元素来进行基于容器的授权
-   为了指定欢迎文件，我们仍然需要一个<welcome-file-list\>部分

或者，Servlet 3.0也通过ServletContainerInitializer引入了一些编程支持，这也可以填补其中的一些空白。

## 9. 总结

在本教程中，我们通过使用等效的注解在不使用web.xml文件的情况下配置了一个Java Web应用程序。
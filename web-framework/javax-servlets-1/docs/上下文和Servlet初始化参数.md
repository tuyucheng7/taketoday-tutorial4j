## 1. 概述

[Servlet](https://en.wikipedia.org/wiki/Java_servlet)是在 servlet 容器中运行的纯Java类。

HTTP servlet(一种特定类型的 servlet)是JavaWeb 应用程序中的一等公民。HTTP servlet 的 API旨在通过典型的请求-处理-响应循环处理 HTTP 请求，在客户端-服务器协议中实现。

此外，servlet 可以使用请求/响应参数形式的键值对来控制客户端(通常是 Web 浏览器)和服务器之间的交互。

这些参数可以被初始化并绑定到应用程序范围(上下文参数)和特定于 servlet 的范围(servlet 参数)。

在本教程中，我们将学习如何定义和访问上下文和 servlet 初始化参数。

## 2.初始化Servlet参数

我们可以使用注解和标准部署描述符—— “web.xml”文件来定义和初始化 servlet 参数。值得注意的是，这两个选项并不相互排斥。

让我们深入探讨这些选项中的每一个。

### 2.1. 使用注解

使用注解初始化 servlet 参数允许我们将配置和源代码保存在同一个地方。

在本节中，我们将演示如何使用注解定义和访问绑定到特定 servlet 的初始化参数。

为此，我们将实现一个简单的UserServlet类，它通过纯 HTML 表单收集用户数据。

首先，让我们看一下呈现表单的 JSP 文件：

```html
<!DOCTYPE html>
<html>
    <head>
        <title>Context and Initialization Servlet Parameters</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Please fill the form below:</h2>
        <form action="${pageContext.request.contextPath}/userServlet" method="post">
            <label for="name"><strong>Name:</strong></label>
            <input type="text" name="name" id="name">
            <label for="email"><strong>Email:</strong></label>
            <input type="text" name="email" id="email">
            <input type="submit" value="Send">
        </form>
    </body>
</html>

```

请注意，我们已经使用[EL](https://mvnrepository.com/artifact/javax.el/el-api/2.2)(表达式语言)对表单的操作属性进行了编码。这允许它始终指向“/userServlet”路径，而不管应用程序文件在服务器中的位置。

“ ${pageContext.request.contextPath}”表达式为表单设置一个动态 URL，它总是相对于应用程序的上下文路径。

这是我们最初的 servlet 实现：

```java
@WebServlet(name = "UserServlet", urlPatterns = "/userServlet", initParams={
@WebInitParam(name="name", value="Not provided"), 
@WebInitParam(name="email", value="Not provided")}))
public class UserServlet extends HttpServlet {
    // ...    
    
    @Override
    protected void doGet(
      HttpServletRequest request, 
      HttpServletResponse response)
      throws ServletException, IOException {
        processRequest(request, response);
        forwardRequest(request, response, "/WEB-INF/jsp/result.jsp");
    }
    
    protected void processRequest(
      HttpServletRequest request, 
      HttpServletResponse response)
      throws ServletException, IOException {
 
        request.setAttribute("name", getRequestParameter(request, "name"));
        request.setAttribute("email", getRequestParameter(request, "email"));
    }
    
    protected void forwardRequest(
      HttpServletRequest request, 
      HttpServletResponse response, 
      String path)
      throws ServletException, IOException { 
        request.getRequestDispatcher(path).forward(request, response); 
    }
    
    protected String getRequestParameter(
      HttpServletRequest request, 
      String name) {
        String param = request.getParameter(name);
        return !param.isEmpty() ? param : getInitParameter(name);
    }
}

```

在这种情况下，我们通过使用initParams和@WebInitParam注解定义了两个 servlet 初始化参数， name和email。

请注意，我们使用了 HttpServletRequest 的getParameter()方法从 HTML 表单中检索数据，并使用getInitParameter()方法访问 servlet 初始化参数。

getRequestParameter()方法检查名称和电子邮件请求参数是否为空字符串。

如果它们是空字符串，则会为它们分配匹配初始化参数的默认值。

doPost ()方法首先检索用户在 HTML 表单中输入的姓名和电子邮件(如果有)。然后它处理请求参数并将请求转发到“result.jsp”文件：

```java
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Data</title>
    </head>
    <body>
        <h2>User Information</h2>
        <p><strong>Name:</strong> ${name}</p>
        <p><strong>Email:</strong> ${email}</p>
    </body>
</html>
```

如果我们将示例 Web 应用程序部署到应用程序服务器(例如[Apache Tomcat、](https://tomcat.apache.org/) [Oracle GlassFish](http://www.oracle.com/technetwork/middleware/glassfish/overview/index.html)或[JBoss WidlFly](http://www.wildfly.org/))并运行它，它应该首先显示 HTML 表单页面。

一旦用户填写了姓名和电子邮件字段并提交了表单，它将输出数据：

```plaintext
User Information
Name: the user's name
Email: the user's email 

```

如果表单只是空白，它将显示 servlet 初始化参数：

```plaintext
User Information 
Name: Not provided 
Email: Not provided

```

在此示例中，我们展示了如何使用注解定义 servlet 初始化参数，以及如何使用g etInitParameter()方法访问它们。

### 2.2. 使用标准部署描述符

这种方法不同于使用注解的方法，因为它允许我们将配置和源代码彼此隔离。

为了展示如何使用“web.xml”文件定义初始化 servlet 参数，让我们首先从UserServlet类中删除initParam和@WebInitParam注解：

```java
@WebServlet(name = "UserServlet", urlPatterns = {"/userServlet"}) 
public class UserServlet extends HttpServlet { ... }

```

接下来，让我们在“web.xml”文件中定义 servlet 初始化参数：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app  
  xmlns="http://xmlns.jcp.org/xml/ns/javaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd" version="3.1">
    <servlet>
        <display-name>UserServlet</display-name>
        <servlet-name>UserServlet</servlet-name>
        <init-param>
            <param-name>name</param-name>
            <param-value>Not provided</param-value>
        </init-param>
        <init-param>
            <param-name>email</param-name>
            <param-value>Not provided</param-value>
        </init-param>
    </servlet>
</web-app>

```

如上所示，使用“web.xml”文件定义 servlet 初始化参数归结为使用<init-param>、 <param-name>和<param-value>标签。

此外，只要我们坚持上述标准结构，就可以根据需要定义任意数量的 servlet 参数。

当我们将应用程序重新部署到服务器并重新运行时，它的行为应该与使用注解的版本相同。

## 3.初始化上下文参数

有时我们需要定义一些必须在整个 Web 应用程序中全局共享和访问的不可变数据。

由于数据的全局性质，我们应该使用应用程序范围的上下文初始化参数来存储数据，而不是求助于 servlet 对应物。

尽管无法使用注解来定义上下文初始化参数，但我们可以在“web.xml”文件中执行此操作。

假设我们要为我们的应用程序运行的国家和省份提供一些默认的全局值。

我们可以使用几个上下文参数来完成此操作。

让我们相应地重构“web.xml”文件：

```xml
<web-app 
  xmlns="http://xmlns.jcp.org/xml/ns/javaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
  http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd" version="3.1">
    <context-param>
        <param-name>province</param-name>
        <param-value>Mendoza</param-value>
    </context-param>
    <context-param>
        <param-name>country</param-name>
        <param-value>Argentina</param-value>
    </context-param>
    <!-- Servlet initialization parameters -->
</web-app>
```

这一次，我们使用了<context-param>、<param-name>和<param-value>标签来定义省和国家上下文参数。

当然，我们需要重构UserServlet类，使其能够获取这些参数并将它们传递给结果页面。

以下是 servlet 的相关部分：

```java
@WebServlet(name = "UserServlet", urlPatterns = {"/userServlet"})
public class UserServlet extends HttpServlet {
    // ...
    
    protected void processRequest(
      HttpServletRequest request, 
      HttpServletResponse response)
      throws ServletException, IOException {
 
        request.setAttribute("name", getRequestParameter(request, "name"));
        request.setAttribute("email", getRequestParameter(request, "email"));
        request.setAttribute("province", getContextParameter("province"));
        request.setAttribute("country", getContextParameter("country"));
    }

    protected String getContextParameter(String name) {-
        return getServletContext().getInitParameter(name);
    }
}

```

请注意getContextParameter()方法的实现，它首先通过getServletContext()获取 servlet 上下文，然后使用getInitParameter()方法获取上下文参数。

接下来，我们需要重构“result.jsp”文件，以便它可以显示上下文参数以及特定于 servlet 的参数：

```html
<p><strong>Name:</strong> ${name}</p>
<p><strong>Email:</strong> ${email}</p>
<p><strong>Province:</strong> ${province}</p>
<p><strong>Country:</strong> ${country}</p>

```

最后，我们可以重新部署应用程序并再次执行它。

如果用户用姓名和电子邮件填写 HTML 表单，那么它将显示此数据以及上下文参数：

```plaintext
User Information 
Name: the user's name 
Email: the user's email
Province: Mendoza
Country: Argentina

```

否则，它将输出 servlet 和上下文初始化参数：

```plaintext
User Information 
Name: Not provided 
Email: Not provided
Province: Mendoza 
Country: Argentina

```

虽然这个例子是人为设计的，但它展示了如何使用上下文初始化参数来存储不可变的全局数据。

由于数据绑定到应用程序上下文，而不是特定的 servlet，我们可以使用getServletContext()和getInitParameter()方法从一个或多个 servlet 访问它们。

## 4. 总结

在本文中，我们了解了上下文和 servlet 初始化参数的关键概念，以及如何使用注解和“web.xml”文件定义和访问它们。

很长一段时间以来，Java 一直强烈倾向于摆脱 XML 配置文件并尽可能迁移到注解。

[CDI](https://docs.oracle.com/javaee/6/tutorial/doc/giwhl.html)、[Spring](https://spring.io/)、[Hibernate](http://hibernate.org/)等等，都是这方面的明显例子。

尽管如此，使用“web.xml”文件定义上下文和 servlet 初始化参数并没有本质上的错误。

尽管[Servlet API](https://search.maven.org/classic/#search|ga|1|servlet-api)已经朝着这种趋势快速发展，我们仍然需要使用部署描述符来定义上下文初始化参数。
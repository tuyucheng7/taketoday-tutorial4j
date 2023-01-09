## 1. 概述

在本教程中，我们将学习如何检查用户的登录并确保用户已使用有效凭据填写登录表单并启动会话。但是，我们将在不使用[Spring Security](https://www.baeldung.com/spring-security-login)且仅使用 JSP 和[servlet](https://www.baeldung.com/register-servlet)的情况下执行此操作。因此，我们需要一个可以支持它的 servlet 容器，比如 Tomcat 9。

到最后，我们将很好地理解引擎盖下的工作原理。

## 2.持久化策略

首先，我们需要用户。为了简单起见，我们将使用预加载的地图。让我们和我们的用户一起定义它：

```java
public class User {
    static HashMap<String, User> DB = new HashMap<>();
    static {
        DB.put("user", new User("user", "pass"));
        // ...
    }

    private String name;
    private String password;

    // getters and setters
}
```

## 3.过滤请求

我们将首先创建一个[过滤器](https://www.baeldung.com/intercepting-filter-pattern-in-java)来检查无会话请求，阻止对我们的 servlet 的直接访问：

```java
@WebFilter("/")
public class UserCheckFilter implements Filter {
    
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        // ...
        request.setAttribute("origin", request.getRequestURI());

        if (!request.getRequestURI().contains("login") && request.getSession(false) == null) {
            forward(request, response, "/login.jsp");
            return;
        }

        chain.doFilter(request, response);
    }
}
```

在这里，通过在@WebFilter上定义“ / ”作为我们的 URL 模式，所有请求都将首先通过我们的过滤器。然后，如果还没有会话，我们将请求重定向到我们的登录页面，存储来源供以后使用。最后，我们提早返回，防止我们的 servlet 在没有适当会话的情况下进行处理。

## 4. 使用 JSP 创建登录表单

要构建我们的登录表单，我们需要从[JSTL](https://www.baeldung.com/jstl)导入核心 Taglib 。此外，让我们在页面指令中将会话 属性设置为“ false ” 。因此，不会自动创建新会话，我们可以完全控制：

```xml
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<form action="login" method="POST">
    ...
</form>

```

然后，在我们的表单中，我们将有一个隐藏的输入来保存原点：

```xml
<input type="hidden" name="origin" value="${origin}">
```

接下来，我们将包含一个条件元素来输出错误：

```xml
<c:if test="${not empty error}">
     error: ${error} 
</c:if>
```

最后，让我们添加一些输入标签，以便用户可以输入并提交凭据：

```xml
<input type="text" name="name">
<input type="password" name="password"> 
<input type="submit">
```

## 5. 设置我们的登录 Servlet

在我们的 servlet 中，如果请求是GET，我们会将请求转发到我们的登录表单。最重要的是，我们验证登录是否为POST：

```java
@WebServlet("/login")
public class UserCheckLoginServlet extends HttpServlet {
    // ...
}

```

因此，在我们的doGet()方法中，我们将重定向到我们的登录 JSP，向前传递原点：

```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String referer = (String) request.getAttribute("origin");
    request.setAttribute("origin", referer);
    forward(request, response, "/login.jsp");
}
```

在我们的doPost()中，我们验证凭据并创建一个会话，将User对象转发并重定向到origin：

```java
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String key = request.getParameter("name");
    String pass = request.getParameter("password");

    User user = User.DB.get(key);
    if (!user.getPassword().equals(pass)) {
        request.setAttribute("error", "invalid login");
        forward(request, response, "/login.jsp");
        return;
    }
        
    HttpSession session = request.getSession();
    session.setAttribute("user", user);

    response.sendRedirect(request.getParameter("origin"));
}
```

如果凭据无效，我们会在错误变量中设置一条消息。否则，我们用我们的用户对象更新会话。

## 6. 检查登录信息

最后，让我们创建我们的主页。它只显示会话信息并有一个注销链接：

```xml
<body>
    current session info: ${user.name}

    <a href="logout">logout</a>
</body>

```

我们的主页 servlet 所做的就是将用户转发到主页：

```java
@WebServlet("/home")
public class UserCheckServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);

        forward(request, response, "/home.jsp");
    }
}
```

这就是它的样子：

[![登录成功](https://www.baeldung.com/wp-content/uploads/2022/02/login-success.png)](https://www.baeldung.com/wp-content/uploads/2022/02/login-success.png)

## 7. 注销

要注销，我们只需使当前会话无效并重定向 home。之后，我们的UserCheckFilter将检测到无会话请求并将我们重定向回登录页面，重新启动该过程：

```java
@WebServlet("/logout")
public class UserCheckLogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();

        response.sendRedirect("./");
    }
}
```

## 八. 总结

在本文中，我们创建了一个完整的登录周期。我们看到我们现在如何使用单个过滤器完全控制对我们的 servlet 的访问。简而言之，使用这种方法，我们始终可以确保在我们需要的地方有一个有效的会话。同样，我们可以扩展该机制以实现更精细的访问控制。
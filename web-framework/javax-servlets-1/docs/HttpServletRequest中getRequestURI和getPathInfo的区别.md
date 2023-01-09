## 1. 概述

在本快速教程中，我们将讨论HttpServletRequest类中getRequestURI()和getPathInfo()之间的区别。

## 2. getRequestURI()和getPathInfo()的区别

函数getRequestURI() 返回完整的请求 URI。这包括部署文件夹和 servlet 映射字符串。它还将返回所有额外的路径信息。

函数getPathInfo() 只返回传递给 servlet 的路径。如果没有传递额外的路径信息，此函数将返回null。

换句话说，如果我们将应用程序部署在 Web 服务器的根目录中，并且请求映射到“/”的 servlet，则getRequestURI()和getPathInfo()将返回相同的字符串。否则，我们会得到不同的值。

## 3. 示例请求

为了更好地理解HttpServletRequest方法，假设我们有一个可以通过此 URL 访问的[servlet ：](https://www.baeldung.com/intro-to-servlets)

```plaintext
http://localhost:8080/deploy-folder/servlet-mapping
```

该请求将命中部署在“deploy-folder”中的 Web 应用程序中的“servlet-mapping”servlet。因此，如果我们为此请求调用getRequestURI()和getPathInfo() ，它们将返回不同的字符串。

让我们创建一个简单的doGet() servlet 方法：

```java
public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter writer = response.getWriter();
    if ("getPathInfo".equals(request.getParameter("function")) {
        writer.println(request.getPathInfo());
    } else if ("getRequestURI".equals(request.getParameter("function")) {
        writer.println(request.getRequestURI());
    }
    writer.flush();
}
```

首先，让我们看一下通过[curl](https://www.baeldung.com/curl-rest)命令获取的getRequestURI请求的 servlet 的输出：

```bash
curl http://localhost:8080/deploy-folder/servlet-mapping/request-path?function=getRequestURI
/deploy-folder/servlet-mapping/request-path

```

同样，让我们看一下getPathInfo的 servlet 的输出：

```bash
curl http://localhost:8080/deploy-folder/servlet-mapping/request-path?function=getPathInfo
/request-path
```

## 4. 总结

在本文中，我们解释了HttpServletRequest中getRequestURI()和getPathInfo()之间的区别。我们还使用示例 servlet 和请求对其进行了演示。
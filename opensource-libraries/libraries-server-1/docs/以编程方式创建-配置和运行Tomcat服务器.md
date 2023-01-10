## 1. 概述

在这篇快速文章中，我们将以编程方式创建、配置和运行[Tomcat 服务器](https://tomcat.apache.org/index.html)。

## 2.设置

在我们开始之前，我们需要通过将以下依赖项添加到我们的pom.xml来设置我们的 Maven 项目：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.tomcat</groupId>
        <artifactId>tomcat-catalina</artifactId>
        <version>${tomcat.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>${apache.httpclient}</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>

```

这是[Maven Central 的链接，](https://search.maven.org/classic/#search|ga|1|(g%3A"org.apache.tomcat" AND a%3A"tomcat-catalina") OR (g%3A"org.apache.httpcomponents" AND a%3A"httpclient") OR (g%3A"junit" AND a%3A"junit"))其中包含项目中使用的最新版本的依赖项。

## 3.初始化和配置Tomcat

先说一下Tomcat服务器的初始化和配置所需要的步骤。

### 3.1. 创建 Tomcat

我们可以通过简单地执行以下操作来创建实例：

```java
Tomcat tomcat = new Tomcat();

```

现在我们有了服务器，让我们配置它。

### 3.2. 配置 Tomcat

我们将专注于如何启动和运行服务器，添加一个 servlet 和一个过滤器。

首先，我们需要配置一个端口、主机名和一个appBase(通常是 Web 应用程序)。为了我们的目的，我们将使用当前目录：

```java
tomcat.setPort(8080);
tomcat.setHostname("localhost");
String appBase = ".";
tomcat.getHost().setAppBase(appBase);

```

接下来，我们需要设置一个docBase(此 Web 应用程序的上下文根目录)：

```java
File docBase = new File(System.getProperty("java.io.tmpdir"));
Context context = tomcat.addContext("", docBase.getAbsolutePath());

```

在这一点上，我们有一个几乎可以正常运行的 Tomcat。

接下来，我们将添加一个 servlet 和一个过滤器并启动服务器以查看它是否正常工作。

### 3.3. 将 Servlet 添加到 Tomcat 上下文

接下来，我们将向HttpServletResponse 添加一个简单的文本。这是当我们访问此 servlet 的 URL 映射时将要显示的文本。

让我们首先定义我们的 servlet：

```java
public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(
      HttpServletRequest req, 
      HttpServletResponse resp) throws IOException {
 
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("test");
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}

```

现在我们将这个 servlet 添加到 Tomcat 服务器：

```java
Class servletClass = MyServlet.class;
Tomcat.addServlet(
  context, servletClass.getSimpleName(), servletClass.getName());
context.addServletMappingDecoded(
  "/my-servlet/", servletClass.getSimpleName());

```

### 3.4. 向 Tomcat 上下文添加过滤器

接下来，我们定义一个过滤器并将其添加到 Tomcat 中：

```java
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // ...
    }

    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) 
      throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("myHeader", "myHeaderValue");
        chain.doFilter(request, httpResponse);
    }

    @Override
    public void destroy() {
        // ...
    }
}

```

将过滤器添加到上下文需要更多的工作：

```java
Class filterClass = MyFilter.class;
FilterDef myFilterDef = new FilterDef();
myFilterDef.setFilterClass(filterClass.getName());
myFilterDef.setFilterName(filterClass.getSimpleName());
context.addFilterDef(myFilterDef);

FilterMap myFilterMap = new FilterMap();
myFilterMap.setFilterName(filterClass.getSimpleName());
myFilterMap.addURLPattern("/my-servlet/");
context.addFilterMap(myFilterMap);

```

此时，我们应该有一个 servlet 和一个过滤器添加到 Tomcat。

剩下要做的就是启动它并获得“测试”页面并检查日志以查看过滤器是否有效。

## 4.启动Tomcat

这是一个非常简单的操作，之后，我们应该看到 Tomcat 正在运行：

```java
tomcat.start();
tomcat.getServer().await();

```

启动后，我们可以访问 http://localhost:8080/my-servlet 并查看测试页面：

[![我的 servlet](https://www.baeldung.com/wp-content/uploads/2018/01/my-servlet.png)](https://www.baeldung.com/wp-content/uploads/2018/01/my-servlet.png)

如果我们查看日志，我们会看到类似这样的内容：

[![tomcat日志](https://www.baeldung.com/wp-content/uploads/2018/01/tomcat-logs.jpg)](https://www.baeldung.com/wp-content/uploads/2018/01/tomcat-logs.jpg)

这些日志显示 Tomcat 开始侦听端口 8080，并且我们的过滤器工作正常。

## 5.总结

在本教程中，我们完成了 Tomcat 服务器的基本编程设置。

我们研究了如何创建、配置和运行服务器，还研究了如何以编程方式将 Servlet 和过滤器添加到 Tomcat 上下文。
## 一、概述

在本教程中，我们将深入研究前端控制器 模式，它是Martin Fowler的书“企业应用程序架构模式”中定义的企业模式的一部分。

前端控制器被定义为“处理网站所有请求的控制器”。它站在网络应用程序的前面，并将请求委托给后续资源。它还为安全、国际化和向特定用户呈现特定视图等常见行为提供接口。

这使应用程序能够在运行时更改其行为。此外，它通过防止代码重复来帮助阅读和维护应用程序。

>   前端控制器通过单个处理程序对象引导请求，从而整合所有请求处理。

## 2. 它是如何工作的？

前端控制器模式主要分为两部分。单个调度控制器和命令层次结构。以下 UML 描述了通用前端控制器实现的类关系：

[![前置控制器](https://www.baeldung.com/wp-content/uploads/2016/09/front-controller.png)](https://www.baeldung.com/wp-content/uploads/2016/09/front-controller.png)

这个单一的控制器将请求分派给命令，以触发与请求相关的行为。

为了演示其实现，我们将在FrontControllerServlet中实现控制器，并将命令实现为从抽象FrontCommand继承的类。

## 3.设置

### 3.1. Maven 依赖项

首先，我们将设置一个包含[javax.servlet-api](https://search.maven.org/classic/#search|gav|1|g%3A"javax.servlet" AND a%3A"javax.servlet-api")的新Maven WAR项目：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.0-b01</version>
    <scope>provided</scope>
</dependency>

```

以及[jetty-maven-plugin](https://search.maven.org/classic/#search|gav|1|g%3A"org.eclipse.jetty" AND a%3A"jetty-maven-plugin")：

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>9.4.0.M1</version>
    <configuration>
        <webApp>
            <contextPath>/front-controller</contextPath>
        </webApp>
    </configuration>
</plugin>
```

### 3.2. 模型

接下来，我们将定义一个Model类和一个模型Repository。我们将使用以下Book类作为我们的模型：

```java
public class Book {
    private String author;
    private String title;
    private Double price;

    // standard constructors, getters and setters
}
```

这里就是repository，具体实现可以自己查找源码或者自己提供：

```java
public interface Bookshelf {
    default void init() {
        add(new Book("Wilson, Robert Anton & Shea, Robert", 
          "Illuminati", 9.99));
        add(new Book("Fowler, Martin", 
          "Patterns of Enterprise Application Architecture", 27.88));
    }

    Bookshelf getInstance();

    <E extends Book> boolean add(E book);

    Book findByTitle(String title);
}
```

### 3.3. 前端控制器Servlet

Servlet 本身的实现相当简单。我们从请求中提取命令名称，动态创建命令类的新实例并执行它。

这使我们能够在不更改Front Controller代码库的情况下添加新命令。

另一种选择是使用静态、条件逻辑来实现 Servlet。这具有编译时错误检查的优点：

```java
public class FrontControllerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, 
      HttpServletResponse response) {
        FrontCommand command = getCommand(request);
        command.init(getServletContext(), request, response);
        command.process();
    }

    private FrontCommand getCommand(HttpServletRequest request) {
        try {
            Class type = Class.forName(String.format(
              "com.baeldung.enterprise.patterns.front." 
              + "controller.commands.%sCommand",
              request.getParameter("command")));
            return (FrontCommand) type
              .asSubclass(FrontCommand.class)
              .newInstance();
        } catch (Exception e) {
            return new UnknownCommand();
        }
    }
}
```

### 3.4. 前线指挥部

让我们实现一个名为FrontCommand的抽象类，它包含所有命令的通用行为。

此类可以访问ServletContext及其请求和响应对象。此外，它将处理视图分辨率：

```java
public abstract class FrontCommand {
    protected ServletContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public void init(
      ServletContext servletContext,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
        this.context = servletContext;
        this.request = servletRequest;
        this.response = servletResponse;
    }

    public abstract void process() throws ServletException, IOException;

    protected void forward(String target) throws ServletException, IOException {
        target = String.format("/WEB-INF/jsp/%s.jsp", target);
        RequestDispatcher dispatcher = context.getRequestDispatcher(target);
        dispatcher.forward(request, response);
    }
}
```

这个抽象的FrontCommand的具体实现是SearchCommand。这将包括找到一本书或丢失一本书的情况的条件逻辑：

```java
public class SearchCommand extends FrontCommand {
    @Override
    public void process() throws ServletException, IOException {
        Book book = new BookshelfImpl().getInstance()
          .findByTitle(request.getParameter("title"));
        if (book != null) {
            request.setAttribute("book", book);
            forward("book-found");
        } else {
            forward("book-notfound");
        }
    }
}
```

如果应用程序正在运行，我们可以通过将浏览器指向[http://localhost:8080/front-controller/?command=Search&title=patterns 来](http://localhost:8080/front-controller/?command=Search&title=patterns)访问此命令。

SearchCommand解析为两个视图，可以使用以下请求测试第二个视图http://localhost:8080/front-controller/?command=Search&title=any-title。

为了结束我们的场景，我们将实现第二个命令，它在所有情况下都作为后备触发，Servlet 不知道命令请求：

```java
public class UnknownCommand extends FrontCommand {
    @Override
    public void process() throws ServletException, IOException {
        forward("unknown");
    }
}
```

此视图可通过http://localhost:8080/front-controller/?command=Order&title=any-title或完全省略URL参数来访问。

## 4.部署

因为我们决定创建一个WAR文件项目，所以我们需要一个 Web 部署描述符。有了这个web.xml，我们就可以在任何 Servlet 容器中运行我们的 Web 应用程序：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
  http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
  version="3.1">
    <servlet>
        <servlet-name>front-controller</servlet-name>
        <servlet-class>
            com.baeldung.enterprise.patterns.front.controller.FrontControllerServlet
        </servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>front-controller</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

作为最后一步，我们将运行“mvn install jetty:run”并在浏览器中检查我们的视图。

## 5.总结

正如我们目前所见，我们现在应该熟悉前端控制器模式及其作为 Servlet 和命令层次结构的实现。
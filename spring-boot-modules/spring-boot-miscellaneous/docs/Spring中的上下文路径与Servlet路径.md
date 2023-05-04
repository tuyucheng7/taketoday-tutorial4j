## 1. 概述

DispatcherServlet在Spring应用程序中起着重要作用，并为应用程序提供单一入口点。而上下文路径定义了最终用户将访问应用程序的URL。

在本教程中，我们将了解上下文路径和 servlet 路径之间的区别。

## 2.上下文路径

简单地说，上下文路径是访问 Web 应用程序所用的名称。它是应用程序的根。默认情况下，Spring Boot提供根上下文路径(“/”)上的内容。

因此，任何具有默认配置的 Boot 应用程序都可以通过以下方式访问：

```bash
http://localhost:8080/
```

但是，在某些情况下，我们可能希望更改应用程序的上下文。[有多种配置上下文路径的方法](https://www.baeldung.com/spring-boot-context-path)，application.properties就是其中之一。该文件位于src/main/resources文件夹下。

让我们使用application.properties文件配置它：

```bash
server.servlet.context-path=/demo
```

因此，应用程序主页将是：

```bash
http://localhost:8080/demo
```

当我们将此应用程序部署到外部服务器时，此修改有助于我们避免可访问性问题。

## 3.Servlet路径

[servlet 路径表示主DispatcherServlet](https://www.baeldung.com/spring-dispatcherservlet)的路径。DispatcherServlet是一个实际的[Servlet](https://www.baeldung.com/intro-to-servlets)，它继承自HttpSerlvet基类。默认值类似于上下文路径，即(“/”)：

```bash
spring.mvc.servlet.path=/
```

在较早版本的 Boot 中，该属性位于ServerProperties类中，称为server.servlet-path =/。

从 2.1.x 开始，此属性被移至WebMvcProperties类并重命名为spring.mvc.servlet.path =/。

让我们修改servlet路径：

```bash
spring.mvc.servlet.path=/baeldung
```

因为一个 servlet 属于一个 servlet 上下文，改变上下文路径也会影响 servlet 路径。所以，修改后，应用的servlet路径会变成http://localhost:8080/demo/baeldung。

换句话说，如果一个样式表被用作http://localhost:8080/demo/style.css，现在将用作http://localhost:8080/demo/baeldung/style.css。

通常，我们不会自己配置 DispatcherServlet。但是，如果我们真的需要这样做，我们必须提供自定义DispatcherServlet的路径。

## 4。总结

在这篇简短的文章中，我们研究了上下文路径和 servlet 路径的语义。我们还看到了这些术语代表什么以及它们如何在Spring应用程序中协同工作。
## 1. 概述

在本教程中，我们将从概念上理解什么是Servlet和Servlet容器以及它们是如何工作的。

我们还将在请求、响应、会话对象、共享变量和多线程的上下文中看到它们。

## 2. 什么是Servlet及其容器

Servlet是用于Web开发的JEE框架的一个组件。它们基本上是在容器边界内运行的Java程序。总的来说，他们负责接受请求、处理请求并发回响应。[Java Servlet简介](https://www.baeldung.com/intro-to-servlets)提供了对该主题的良好基本理解。

要使用它们，需要先[注册](https://www.baeldung.com/register-servlet)Servlet，这样JEE或基于Spring的容器可以在启动时获取它们。一开始，容器通过调用它的init()方法来实例化一个Servlet。

一旦初始化完成，Servlet就准备好接受传入的请求。随后，容器将这些请求引导到Servlet的service()方法中进行处理。之后，它进一步根据HTTP请求类型将请求委托给适当的方法，例如doGet()或doPost()。

使用destroy()，容器将Servlet拆掉，它不能再接受传入的请求。我们将这个init-service-destroy循环称为Servlet的生命周期。

现在让我们从容器的角度来看这个，例如[Apache Tomcat](https://www.baeldung.com/tomcat)或[Jetty](https://www.baeldung.com/deploy-to-jetty)。在启动时，它创建一个[ServletContext](https://www.baeldung.com/context-servlet-initialization-param)对象。ServletContext的工作是充当服务器或容器的内存，并记住与Web应用程序关联的所有Servlet、过滤器和监听器，如其web.xml或等效注解中所述。在我们停止或终止容器之前，ServletContext会一直存在。

但是，Servlet的load-on-startup参数在这里起着重要的作用。如果此参数的值大于零，则服务器仅在启动时对其进行初始化。如果未指定此参数，则在第一次请求命中时调用Servlet的init()。

## 3. 请求、响应和会话

在上一节中，我们讨论了发送请求和接收响应，这基本上是任何客户端-服务器应用程序的基石。现在，让我们结合Servlet详细了解它们。

在这种情况下，请求将由[HttpServletRequest](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/http/HttpServletRequest.html)表示，响应由[HttpServletResponse](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/http/HttpServletResponse.html)表示。

每当浏览器或curl命令等客户端发送请求时，容器都会创建一个新的HttpServletRequest和HttpServletResponse对象。然后它将这些新对象传递给Servlet的服务方法。基于HttpServletRequest的方法属性，此方法确定应调用哪个doXXX方法。

除了关于方法的信息，请求对象还携带其他信息，例如头部、参数和正文。同样，HttpServletResponse对象也带有标头、参数和正文，我们可以在Servlet的doXXX方法中设置它们。

这些对象是短暂的。当客户端收到响应时，服务器将请求和响应对象标记为垃圾回收。

那么我们将如何维护后续客户端请求或连接之间的状态？[HttpSession](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/http/HttpSession.html)就是这个谜语的答案。

这基本上将对象绑定到用户会话，以便可以跨多个请求保留与特定用户有关的信息。这通常是使用cookie的概念来实现的，使用[JSESSIONID](https://www.baeldung.com/java-servlet-cookies-session#httpsession-object)作为给定会话的唯一标识符。我们可以在web.xml中指定会话的超时时间：

```xml
<session-config>
    <session-timeout>10</session-timeout>
</session-config>
```

这意味着如果我们的会话空闲了10分钟，服务器将丢弃它。任何后续请求都会创建一个新会话。

## 4. Servlets如何共享数据

根据所需的范围，Servlet可以通过多种方式共享数据。

正如我们在前面几节中看到的，不同的对象有不同的生命周期，HttpServletRequest和HttpServletResponse对象只存在于一个Servlet调用之间。只要HttpSession处于活动状态并且没有超时，它就会一直存在。

ServletContext的生命周期最长。它与Web应用程序一起诞生，只有在应用程序本身关闭时才会被销毁。由于Servlet、过滤器和监听器实例与上下文相关联，因此只要Web应用程序启动并运行，它们也会存在。

因此，如果我们的要求是在所有Servlet之间共享数据，比方说，如果我们想计算站点的访问者数量，那么我们应该将变量放在ServletContext中。如果我们需要在会话中共享数据，那么我们会将其保存在会话范围内。在这种情况下，用户名就是一个例子。

最后，请求范围与单个请求的数据有关，例如请求负载。

## 5. 处理多线程

多个HttpServletRequest对象彼此共享Servlet，这样每个请求都使用自己的Servlet实例线程进行操作。

就线程安全而言，这实际上意味着我们不应该将请求或会话范围内的数据分配为Servlet的实例变量。

例如，让我们考虑这个片段：

```java
public class ExampleThree extends HttpServlet {

    private String instanceMessage;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
        String message = request.getParameter("message");
        instanceMessage = request.getParameter("message");
        request.setAttribute("text", message);
        request.setAttribute("unsafeText", instanceMessage);
        request.getRequestDispatcher("/jsp/ExampleThree.jsp").forward(request, response);
    }
}
```

在这种情况下，会话中的所有请求共享instanceMessage，而消息对于给定的请求对象是唯一的。因此，在并发请求的情况下，instanceMessage中的数据可能不一致。

## 6. 总结

在本教程中，我们了解了有关Servlet的一些概念、它们的容器以及它们围绕的一些基本对象。我们还看到了Servlet如何共享数据以及多线程如何影响它们。
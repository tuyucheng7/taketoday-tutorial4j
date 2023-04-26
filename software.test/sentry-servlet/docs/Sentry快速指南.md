## 1. 简介

在本教程中，我们将展示如何将[Sentry](https://sentry.io/)与基于Java的服务器端应用程序一起使用。

## 2. 什么是Sentry？

**Sentry是一个错误跟踪平台，可帮助开发人员监控实时应用程序**。它可以与Java应用程序一起使用，以自动跟踪和报告任何错误或异常。这些报告捕获有关其发生的上下文的全面详细信息，从而使重现、查找原因以及最重要的是修复它变得更加容易。

该平台有两种基本类型：

-   开源，我们需要托管、保护和支持运行Sentry所需的所有基础设施
-   SaaS，所有这些琐事都在这里处理。

对于小型项目和评估目的(例如本教程)，开始使用Sentry的最佳方式是使用SaaS模型。有一个功能有限的永久免费层(例如，仅24小时警报保留)，这足以让我们熟悉该平台，以及完整产品的试用版。

## 3. Sentry集成概述

Sentry支持多种语言和框架。无论我们选择哪一种，所需的步骤都是相同的：

-   将需要的依赖添加到目标项目中
-   将Sentry集成到应用程序中，以便它可以捕获错误以及
-   生成API密钥

除了这些必需的步骤之外，还有一些我们可能想要采用的可选步骤

-   添加额外的标签和属性以丰富发送到服务器的事件
-   为我们的代码中检测到的某些相关情况发送事件
-   过滤事件，防止它们被发送到服务器

集成启动并运行后，我们将能够在Sentry的仪表板中看到错误。这是典型项目的外观：

<img src="../assets/img_7.png">

我们还可以配置警报和相关操作。例如，我们可以定义一个规则来为新的错误发送电子邮件，从而生成格式良好的消息：

<img src="../assets/img_8.png">

## 4. 将Sentry集成到Java Web应用程序中

在我们的教程中，我们会将Sentry添加到基于Servlet的标准应用程序中。请注意，还有一个特定于Spring Boot的集成，但我们不会在这里介绍它。

### 4.1 Maven依赖项

为标准Maven War应用程序添加Sentry支持只需要单个依赖项：

```xml
<dependency>
    <groupId>io.sentry</groupId>
    <artifactId>sentry-servlet</artifactId>
    <version>6.11.0</version>
</dependency>
```

此依赖项的最新版本可在[Maven Central](https://central.sonatype.com/artifact/io.sentry/sentry-servlet/6.17.0)上获得。

### 4.2 测试Servlet

测试应用程序有一个Servlet，它根据op查询参数的值处理对/faulty的GET请求：

-   无值：返回200状态代码和带有“OK”的纯文本响应
-   fault：返回包含应用程序定义的错误消息的500响应
-   exception：抛出一个非受检异常，它也会生成500响应，但带有服务器定义的错误消息

Servlet代码非常简单：

```java
@WebServlet(urlPatterns = "/fault", loadOnStartup = 1)
public class FaultyServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String op = req.getParameter("op");
        if("fault".equals(op)) {
            resp.sendError(500, "Something bad happened!");
        }
        else if ("exception".equals(op)) {
            throw new IllegalArgumentException("Internal error");
        }
        else {
            resp.setStatus(200);
            resp.setContentType("text/plain");
            resp.getWriter().println("OK");
        }
    }
}
```

**注意这段代码完全不知道Sentry**。我们这样做是为了模拟将Sentry添加到现有代码库的场景。

### 4.3 库初始化

Sentry的Servlet支持库包括一个ServletContainerInitializer，它会被任何最近的容器自动选取。**但是，此初始化器不包含捕获事件的逻辑**。事实上，它所做的只是添加一个RequestListener，该RequestListener使用从当前请求中提取的信息丰富捕获的事件。

**此外，有点奇怪的是，这个初始化器并不初始化SDK本身**。如[文档](https://docs.sentry.io/platforms/java/#configure)中所述，应用程序必须调用Sentry.init()方法变体之一才能开始发送事件。

SDK初始化所需的主要信息是DSN，它是Sentry生成的项目特定标识符，同时充当事件目标和API密钥。这是一个典型的DSN的样子：

```plaintext
https://fb81e60b8eac4286810c07baea898c95@o4505078833020928.ingest.sentry.io/4505078976217088
```

我们将在一分钟内看到如何获取项目的DSN，所以让我们暂时把它放在一边。初始化SDK的一种直接方法是为此创建一个ServletContextListener：

```java
@WebListener
public class SentryContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Sentry.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Sentry.close();
    }
}
```

init()方法在没有任何参数的情况下调用时，使用默认值和从这些来源之一提取的值配置SDK：

-   Java系统属性
-   系统环境变量
-   位于当前工作目录中的sentry.properties文件
-   位于类路径根目录中的sentry.properties资源

此外，用于最后两个源的文件名也可以通过sentry.properties.file系统属性或SENTRY_PROPERTIES_FILE环境变量指定。

完整的配置选项可[在线获得](https://docs.sentry.io/platforms/java/configuration/#options)。

### 4.4 捕获事件

现在Sentry的设置已经完成，我们需要在我们的应用程序中添加逻辑来捕获我们感兴趣的事件。通常，这意味着报告失败的请求，分为两类：

-   任何返回5xx状态的请求
-   未处理的异常

对于基于Servlet的应用程序，捕获这些失败请求的自然方法是添加具有所需逻辑的请求过滤器：

```java
@WebFilter(urlPatterns = "/*" )
public class SentryFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
            int rc = ((HttpServletResponse)response).getStatus();
            if (rc/100 == 5) {
                Sentry.captureMessage("Application error: code=" + rc, SentryLevel.ERROR);
            }
        }
        catch(Throwable t) {
            Sentry.captureException(t);
            throw t;
        }
    }
}
```

**在这种情况下，过滤器将只拦截同步请求**，这对我们的测试Servlet来说很好。这段代码的关键点是我们需要处理两个可能的返回路径。第一个是“快乐路径”，doChain()方法正常返回。如果响应中的状态代码是500到599之间的任何值，我们将假设应用程序逻辑即将返回错误。在这种情况下，我们创建一条包含错误代码的消息，并使用captureMessage()将其发送到Sentry。

第二个路径发生在上游代码抛出异常时。我们使用catch语句捕获它并使用captureException()将其报告给Sentry。这里的一个重要细节是重新抛出这个异常，这样我们就不会干扰任何现有的错误处理逻辑。

### 4.5 生成DSN

**下一个任务是生成项目的DSN，以便我们的应用程序可以将事件发送到Sentry**。首先，我们需要登录Sentry的控制台并在左侧菜单中选择Projects：

<img src="../assets/img.png">

接下来，我们可以使用现有项目或创建一个新项目。在本教程中，我们将通过点击“Create Project”按钮来创建一个新项目：

<img src="../assets/img_1.png">

现在，我们必须输入三个信息：

-   Platform：Java
-   Alert frequency：在每个新问题时提醒我
-   Project name：sentry-servlet

点击“Create Project”按钮后，我们将被重定向到一个充满有用信息的页面，包括DSN。或者，我们可以跳过此信息并返回到项目页面。**新创建的现在应该在这个区域可用**。

要访问其DSN，我们将单击该项目，然后通过单击右上角的齿轮状小图标转到项目设置页面：

<img src="../assets/img_2.png">

最后，我们将在SDK Instrumentation/Client Keys(DSN)下找到DSN值：

<img src="../assets/img_3.png">

## 5. 测试Sentry的SDK集成

有了DSN，我们现在可以测试我们添加到应用程序中的集成代码是否正常工作。仅出于测试目的，我们将在项目的resources文件夹中创建一个sentry.properties文件，并在其中添加具有相应值的单个dsn属性：

```properties
# Sentry configuration file
dsn=https://fb81e60b8eac4286810c07baea898c95@o4505078833020928.ingest.sentry.io/4505078976217088
```

示例Maven项目具有使用Cargo和嵌入式Tomcat 9服务器直接从Maven运行它所需的配置：

```shell
$ mvn package cargo:run
... many messages omitted
[INFO] [beddedLocalContainer] Tomcat 9.x Embedded started on port [8080]
[INFO] Press Ctrl-C to stop the container...
```

我们现在可以使用浏览器或命令行实用程序(如curl)来测试每个场景。首先，让我们尝试一个“happy path”请求：

```shell
$ curl http://localhost:8080/sentry-servlet/fault
... request headers omitted
< HTTP/1.1 200
... other response headers omitted
<
OK
* Connection #0 to host localhost left intact
```

正如预期的那样，我们有一个200状态代码和“OK”作为响应主体。其次，让我们尝试500响应：

```shell
$ curl -v "http://localhost:8080/sentry-servlet/fault?op=fault"
... requests headers omitted
< HTTP/1.1 500
... other response headers and body omitted
```

现在，我们可以转到Sentry上的项目页面以确认它已捕获它：

<img src="../assets/img_4.png">

单击问题，我们可以看到所有捕获的信息，以及我们传递给captureMessage()的消息：

<img src="../assets/img_5.png">

滚动浏览问题详情页面，我们可以找到很多与请求相关的有趣信息：

-   完整的堆栈跟踪
-   用户代理详细信息
-   应用程序版本
-   JDK版本
-   完整请求的URL，包括使用[curl](https://www.baeldung.com/curl-rest)重现它的示例

最后，我们来测试未处理的异常场景：

```shell
$ curl -v "http://localhost:8080/sentry-servlet/fault?op=exception"
... request headers omitted
< HTTP/1.1 500
... response headers and body omitted
```

和以前一样，我们得到一个500响应，但是，在这种情况下，正文包含一个堆栈跟踪，因此我们知道它来自其他路径。捕获的Sentry事件看起来也有点不同：

<img src="../assets/img_6.png">

我们可以看到堆栈跟踪与应用程序代码中抛出异常的位置相匹配。

## 6. 总结

在这个快速教程中，我们展示了如何将Sentry的SDK集成到基于Servlet的应用程序中，并使用其SaaS版本获取详细的错误报告。尽管只使用基本的集成功能，但我们得到的报告类型可以改善开发人员的错误分析体验。
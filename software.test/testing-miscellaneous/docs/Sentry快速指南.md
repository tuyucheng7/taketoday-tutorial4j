## 一、简介

在本教程中，我们将展示如何将[Sentry](https://sentry.io/)与基于 Java 的服务器端应用程序一起使用。

## 2.什么是哨兵？

**Sentry 是一个错误跟踪平台，可帮助开发人员监控实时应用程序**。它可以与 Java 应用程序一起使用，以自动跟踪和报告任何错误或异常。这些报告捕获有关其发生的上下文的全面详细信息，从而使重现、查找原因以及最重要的是修复它变得更加容易。

该平台有两种基本类型：

-   开源，我们需要托管、保护和支持运行 Sentry 所需的所有基础设施
-   SaaS，所有这些琐事都在这里处理。

对于小型项目和评估目的，例如本教程，开始使用 Sentry 的最佳方式是使用 SaaS 模型。有一个功能有限的永久免费层（例如，仅 24 小时警报保留），这足以让我们熟悉该平台，以及完整产品的试用版。

## 3.哨兵集成概述

Sentry 支持多种语言和框架。无论我们选择哪一种，所需的步骤都是相同的：

-   将需要的依赖添加到目标项目中
-   将 Sentry 集成到应用程序中，以便它可以捕获错误以及
-   生成 API 密钥

除了这些必需的步骤之外，还有一些我们可能想要采用的可选步骤

-   添加额外的标签和属性以丰富发送到服务器的事件
-   为我们的代码中检测到的某些相关情况发送事件
-   过滤事件，防止它们被发送到服务器

集成启动并运行后，我们将能够在 Sentry 的仪表板中看到错误。这是典型项目的外观：

[![项目](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_project-1-1.png)](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_project-1-1.png)

我们还可以配置警报和相关操作。例如，我们可以定义一个规则来为新的错误发送电子邮件，这会生成一个格式良好的消息：

[![警报](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_alert-1.png)](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_alert-1.png)

## 4. 将哨兵集成到 Java Web 应用程序中

在我们的教程中，我们会将 Sentry 添加到基于 servlet 的标准应用程序中。请注意，还有一个特定于 SpringBoot 的集成，但我们不会在这里介绍。

让我们开始吧！

### 4.1. Maven 依赖项

添加对标准 Maven war 应用程序的哨兵支持需要一个单一的依赖：

```xml
<dependency>
    <groupId>io.sentry</groupId>
    <artifactId>sentry-servlet</artifactId>
    <version>6.11.0</version>
</dependency>
复制
```

此依赖项的最新版本可在[Maven Central](https://search.maven.org/search?q=a:sentry-servlet AND g:io.sentry)上获得。

### 4.2. 测试小服务程序

Out 测试应用程序有一个 servlet，它根据 *op 查询参数的值处理对**/faulty 的*GET 请求：

-   无值：返回 200 状态代码和带有“OK”的纯文本响应
-   *fault*：返回带有应用程序定义的错误消息的 500 响应
-   *exception*：抛出一个未经检查的异常，它也会产生一个 500 响应，但带有服务器定义的错误消息

servlet 代码非常简单：

```java
@WebServlet(urlPatterns = "/fault", loadOnStartup = 1)
public class FaultyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
        
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
复制
```

**注意这段代码完全不知道哨兵**。我们这样做是为了模拟将 Sentry 添加到现有代码库的场景。

### 4.3. 库初始化

Sentry 的 servlet 支持库包括一个*ServletContainerInitializer*，它会被任何最近的容器自动选取。**但是，此初始化器不包含捕获事件的逻辑**。事实上，它所做的只是添加一个*RequestListener*，该 RequestListener 使用从当前请求中提取的信息丰富捕获的事件。

**此外，有点奇怪的是，这个初始化程序并不初始化 SDK 本身。**如[文档中](https://docs.sentry.io/platforms/java/#configure)所述，应用程序必须调用*Sentry.init()*方法变体之一才能开始发送事件。

SDK 初始化所需的主要信息是 DSN *，*它是 Sentry 生成的项目特定标识符，同时充当事件目标和 API 密钥。这是一个典型的 DSN 的样子：

```plaintext
https://ad295ac6c17f4a9b89aad23f51a806a3@o75061.ingest.sentry.io/161906复制
```

我们将在一分钟内看到如何获取项目的 DSN，所以让我们暂时把它放在一边。初始化 SDK 的一种直接方法是为此创建一个*ServletContextListener ：*

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
复制
```

init *()*方法在调用时不带任何参数，使用默认值和从这些来源之一提取的值配置 SDK：

-   Java 系统属性
-   系统环境变量
-   位于当前工作目录中的*sentry.properties文件*
-   *sentry.properties*资源位于类路径的根目录

*此外，用于最后两个源的文件名也可以通过sentry.properties.file*系统属性或*SENTRY_PROPERTIES_FILE*环境变量指定。

完整的配置选项[可在线获得](https://docs.sentry.io/platforms/java/configuration/#options)。

### 4.4. 捕获事件

现在哨兵的设置已经完成，我们需要在我们的应用程序中添加逻辑来捕获我们感兴趣的事件。通常，这意味着报告失败的请求，分为两类：

-   任何返回请求 5xx 状态
-   未处理的异常

对于基于 servlet 的应用程序，捕获那些失败请求的自然方法是添加具有所需逻辑的请求过滤器：

```java
@WebFilter(urlPatterns = "/*" )
public class SentryFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
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
复制
```

**在这种情况下，过滤器将只拦截同步请求**，这对我们的测试 servlet 来说很好。这段代码的关键点是我们需要处理两个可能的返回路径。第一个是“快乐之路”，*doChain()*方法正常返回。如果响应中的状态代码是 500 到 599 之间的任何值，我们将假设应用程序逻辑即将返回错误。在这种情况下，我们创建一条包含错误代码的消息，并使用*captureMessage()*将其发送到 Sentry。

第二条路径发生在上游代码抛出异常时。我们使用 catch 语句捕获它并使用*captureException()*将其报告给 Sentry 。这里的一个重要细节是重新抛出这个异常，这样我们就不会干扰任何现有的错误处理逻辑。

### 4.5. 生成 DSN

**下一个任务是生成项目的 DSN，以便我们的应用程序可以将事件发送到 Sentry**。首先，我们需要登录 Sentry 的控制台并在左侧菜单中选择 Projects：

[![项目菜单](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_project_menu-1-150x150.png)](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_project_menu-1.png)

接下来，我们可以使用现有项目或创建一个新项目。在本教程中，我们将通过按“创建项目”按钮来创建一个新项目：

[![创建项目](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_create_project-1.png)](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_create_project-1.png)

现在，我们必须输入三个信息：

-   *平台*：Java
-   *警报频率*：在每个新问题上提醒我
-   *项目名称*：哨兵servlet

一旦我们按下“创建项目”按钮，我们将被重定向到一个充满有用信息的页面，包括 DSN。或者，我们可以跳过此信息并返回到项目页面。**新创建的现在应该在这个区域可用。**

要访问其 DSN，我们将单击该项目，然后通过单击右上角的小齿轮状图标转到项目设置页面：

[![项目设置](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_project_settings_1.png)](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_project_settings_1.png)

最后，我们将在 SDK Instrumentation/Client Keys (DSN) 下找到 DSN 值：

[![项目密钥](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_project_keys-1.png)](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_project_keys-1.png)

## 5.测试哨兵的SDK集成

有了 DSN，我们现在可以测试我们添加到应用程序中的集成代码是否正常工作。仅出于测试目的，我们将在项目的资源文件夹中创建一个*sentry.properties*文件，并在其中添加具有相应值的创建单个*dsn属性：*

```properties
# Sentry configuration file
dsn=https://ad295ac6c17f4a9b89aad23f51a806a3@o75061.ingest.sentry.io/161906复制
```

示例 maven 项目具有使用 cargo 和嵌入式 Tomcat 9 服务器直接从 Maven 运行它所需的配置：

```shell
$ mvn package cargo:run
... many messages omitted
[INFO] [beddedLocalContainer] Tomcat 9.x Embedded started on port [8080]
[INFO] Press Ctrl-C to stop the container...
复制
```

我们现在可以使用浏览器或命令行实用程序（如 curl）来测试每个场景。首先，让我们尝试一个“happy path”请求：

```shell
$ curl http://localhost:8080/sentry-servlet/fault
... request headers omitted
< HTTP/1.1 200
... other response headers omitted
<
OK
* Connection #0 to host localhost left intact
复制
```

正如预期的那样，我们有一个 200 状态代码和“OK”作为响应主体。其次，让我们尝试 500 响应：

```shell
$ curl -v "http://localhost:8080/sentry-servlet/fault?op=fault"
... requests headers omitted
< HTTP/1.1 500
... other response headers and body omitted
复制
```

我们现在可以转到 Sentry 上的项目页面以确认它已捕获它：

[![项目问题](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_project_issues.png)](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_project_issues.png)

单击问题，我们可以看到所有捕获的信息，以及我们传递给*captureMessage()*的消息：

[![问题详情](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_issue_detail.png)](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_issue_detail.png)

滚动浏览问题详情页面，我们可以找到很多与请求相关的有趣信息：

-   完整的堆栈跟踪
-   用户代理详细信息
-   应用版本
-   JDK版本
-   [完整请求的 URL，包括使用curl](https://www.baeldung.com/curl-rest)重现它的示例

最后，我们来测试未处理的异常场景：

```shell
$ curl -v "http://localhost:8080/sentry-servlet/fault?op=exception"
... request headers omitted
< HTTP/1.1 500
... response headers and body omitted复制
```

和以前一样，我们得到一个 500 响应，但是，在这种情况下，正文包含一个堆栈跟踪，所以我们知道它来自其他路径。捕获的哨兵事件看起来也有点不同：

[![问题未解决的异常](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_issue_undlandled_exception.png)](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_issue_undlandled_exception.png)

我们可以看到堆栈跟踪与应用程序代码中抛出异常的位置相匹配。

## 六，结论

在本快速教程中，我们展示了如何将 Sentry 的 SDK 集成到基于 servlet 的应用程序中，并使用其 SaaS 版本获取详细的错误报告。尽管只使用基本的集成功能，但我们得到的报告类型可以改善开发人员的错误分析体验。
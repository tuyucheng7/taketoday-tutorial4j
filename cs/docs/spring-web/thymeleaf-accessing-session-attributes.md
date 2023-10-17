## 1. 概述

在这篇短文中，我们将学习如何使用Thymeleaf库访问服务器端的 HTTP 会话。考虑到这一目的，我们将构建一个网页，其中包含一个用于发送名称分析请求的表单和一个用于显示结果的部分，最后是一个包含会话期间启动的所有请求的面板。

为了简化起见，该示例将使用 Spring +Thymeleaf，因此我们将使用Thymeleaf Spring Standard dialect。

## 2.Thymeleaf中的 Web 会话属性

会话信息位于 servlet 上下文内部，我们可以在模板级别或Spring Boot控制器内部访问该信息。现在，我们将检查两种访问会话信息的方法。

### 2.1. 访问Thymeleaf模板中的会话属性

在Thymeleaf中，我们有两个始终可用的[基础对象： ](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html#base-objects)ctx和locale，它们以“#”为前缀。#ctx基础对象提供对包含 HTTP 会话信息的 servlet 上下文的访问。因此，在模板中，我们可以使用以下表达式访问会话：

```xml
#ctx.session复制
```

如果我们想以更短的方式访问会话，我们可以使用变量session，因此前面的命令相当于：

```xml
session复制
```

现在，让我们检查一下我们可以对模板中的会话实例做什么和不能做什么。首先，我们可以获取会话中存在多少个属性：

```xml
${#ctx.session.size()}复制
```

另外，我们可以检查会话是否为空：

```xml
${#ctx.session.isEmpty()}复制
```

我们无法使用模板中的containsKey方法检查属性是否在会话中注册：

```xml
${#ctx.session.containsKey('lastAnalysis')}复制
```

此方法将始终返回 true，因此我们应该检查 session 属性是否为 null：

```xml
${#ctx.session.lastAnalysis}==null复制
```

最后，我们可以访问会话属性：

```xml
${#ctx.session.foo}复制
```

### 2.2. 访问Spring Boot控制器中的会话属性

在控制器内部，Thymeleaf的IWebSession接口定义了我们访问会话信息的方法：

```java
public interface IWebSession {
    public boolean exists();
    public boolean containsAttribute(String name);
    public int getAttributeCount();
    public Set<String> getAllAttributeNames();
    public Map<String,Object> getAttributeMap();
    public Object getAttributeValue(String name);
    public void setAttributeValue(String name,Object value);
    public void removeAttribute(String name);
}复制
```

在我们的示例中，我们将了解如何获取IWebSession接口的实例，并且我们将使用它来删除、获取和设置它的属性，因此我们不会使用该接口中的整个方法，但它应该足以展示如何使用它。

从最后开始，IServletWebExchange将提供IWebSession实例。我们使用HttpServletRequest 和使用webApp属性在NameAnalysisController控制器请求中接收到的HttpServletResponse构建IServletWebExchange的实例。

让我们看一下getIWebSession方法：

```java
private IWebSession getIWebSession(HttpServletRequest request, HttpServletResponse response) {
    IServletWebExchange exchange = webApp.buildExchange(request, response);
    return exchange == null ? null : exchange.getSession();
}
复制
```

现在，让我们看看webApp属性的类型 以及它是如何实例化的：

```java
private JakartaServletWebApplication webApp;

@Autowired
public NameAnalysisController(NameAnalysisService nameAnalysisService, SessionNameRequestFactory sessionNameRequestFactory, ServletContext servletContext) {
    super();
    ...
    this.webApp = JakartaServletWebApplication.buildApplication(servletContext);
}
复制
```

在这里，我们可以看到webApp属性是 使用注入的ServletContext实例构建的 JakartaServletWebApplication实例。至此，我们已准备好访问会话信息的一切。

## 3. 项目设置

让我们回顾一下我们的项目设置。这是一个具有两个依赖项的 Maven 项目。第一个[spring-boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.5)将使用Spring Boot导入 Web 项目所需的所有内容：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>复制
```

第二个[spring-boot-starter-thymeleaf](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf/3.1.1)将导入所有内容以支持将Thymeleaf与Spring Boot一起使用：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>${spring.boot.starter.thymeleaf}</version>
</dependency>复制
```

### 3.1.Thymeleaf引擎配置

spring -boot-starter-thymeleaf依赖项将为我们配置所有内容，但在我们的示例中，让我们对SpringResourceTemplateResolver进行一些调整以设置模板模式、模板前缀和模板后缀：

```java
@Autowired
public SpringWebConfig(SpringResourceTemplateResolver templateResolver) {
    super();

    templateResolver.setPrefix("/WEB-INF/templates/");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);
}复制
```

通过这些更改，解析器将通过添加前缀/WEB-INF/templates/和后缀.html 来转换请求。因此，对URL的以下请求：

```bash
http://localhost:8080/name-analysis.html复制
```

转化为如下模板路径：

```bash
WEB-INF/templates/name-analysis.html复制
```

## 4. 运行示例

要检查一切是否正在运行并正在进行，让我们从项目根目录的命令行执行以下 Maven 命令：

```powershell
mvn spring-boot:run复制
```

该命令将启动 Tomcat 服务器并嵌入该应用程序。服务器侦听端口 8080 并在根上下文中发布示例的应用程序。因此，访问基页的 URL 为：

```plaintext
http://localhost:8080复制
```

该请求将显示：[![img](https://www.baeldung.com/wp-content/uploads/2023/07/name-analysis-base-1.png)](https://www.baeldung.com/wp-content/uploads/2023/07/name-analysis-base-1.png)

在这里，我们可以看到示例的三个不同部分。我们将从分析名称面板开始。它无权访问任何会话信息。它使用公开的nameRequest模型属性。

我们继续使用“名称分析”面板，该面板显示使用会话中的lastRequest属性进行名称分析请求的结果。最后，最后一个面板，即“请求历史记录”面板，还将访问存储在会话的请求属性中的信息。

## 5. 结论

在本文中，我们了解了如何配置 Maven 项目以使用[Spring + Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)。最重要的是，我们重点关注如何从Thymeleaf模板和Spring Boot服务器端访问 HTTP 会话信息。要深入挖掘并了解Thymeleaf如何从头开始工作，请阅读[Spring 中使用Thymeleaf简介](https://www.baeldung.com/thymeleaf-in-spring-mvc)。
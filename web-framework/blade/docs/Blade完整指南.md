## 1. 概述

[Blade](https://lets-blade.com/)是一个微型Java8+ MVC 框架，从头开始构建，并牢记一些明确的目标：独立、高效、优雅、直观和超快。

许多不同的框架激发了它的设计灵感：Node 的[Express](https://expressjs.com/)、Python 的[Flask](http://flask.pocoo.org/)和 Golang 的[Macaron](https://github.com/go-macaron/macaron) / [Martini](https://github.com/go-martini/martini)。

Blade 也是一个雄心勃勃的更大项目[Let's Blade](https://github.com/lets-blade)的一部分。它包括其他小型库的异构集合，从验证码生成到 JSON 转换，从模板化到简单的数据库连接。

但是，在本教程中，我们将只关注 MVC。

## 2. 开始

首先，让我们创建一个空的 Maven 项目，并在pom.xml中添加[最新的 Blade MVC 依赖](https://search.maven.org/search?q=g:com.bladejava AND a:blade-mvc)：

```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-mvc</artifactId>
    <version>2.0.14.RELEASE</version>
</dependency>

```

### 2.1. 捆绑刀片应用程序

由于我们的应用程序将创建为 JAR，因此它不会有/lib文件夹，就像在 WAR 中一样。因此，这导致我们遇到如何向我们的应用程序提供blade-mvc JAR 以及我们可能需要的任何其他 JAR 的问题。

如何使用 Maven教程[创建可执行 JAR](https://www.baeldung.com/executable-jar-with-maven)中解释了执行此操作的不同方法，每种方法各有利弊。

为简单起见，我们将使用Maven Assembly Plugin技术，它分解 pom.xml 中导入的任何 JAR ，然后将所有类捆绑在一个 uber-JAR 中。

### 2.2. 运行刀片应用程序

Blade 基于[Netty](https://www.baeldung.com/netty)，一个令人惊叹的异步事件驱动网络应用程序框架。因此，要运行我们基于 Blade 的应用程序，我们不需要任何外部应用程序服务器或 Servlet 容器；JRE 就足够了：

```bash
java -jar target/sample-blade-app.jar

```

之后，可以通过http://localhost:9000 URL 访问该应用程序。

## 3. 了解架构

Blade 的架构非常简单：

[![建筑学](https://www.baeldung.com/wp-content/uploads/2019/01/architecture.png)](https://www.baeldung.com/wp-content/uploads/2019/01/architecture.png)

它始终遵循相同的生命周期：

1.  Netty 收到一个请求
2.  执行中间件(可选)
3.  WebHooks 被执行(可选)
4.  执行路由
5.  响应被发送到客户端
6.  清理

我们将在下一节中探讨上述功能。

## 4.路由

简而言之，MVC 中的路由是用于在 URL 和控制器之间创建绑定的机制。

Blade 提供两种类型的路由：一种是基本路由，另一种是注解路由。

### 4.1. 基本路线

基本路由适用于非常小的软件，例如微服务或最小的 Web 应用程序：

```java
Blade.of()
  .get("/basic-routes-example", ctx -> ctx.text("GET called"))
  .post("/basic-routes-example", ctx -> ctx.text("POST called"))
  .put("/basic-routes-example", ctx -> ctx.text("PUT called"))
  .delete("/basic-routes-example", ctx -> ctx.text("DELETE called"))
  .start(App.class, args);

```

用于注册路由的方法名称对应于将用于转发请求的 HTTP 动词。就如此容易。

在这种情况下，我们将返回一个文本，但我们也可以呈现页面，正如我们将在本教程后面看到的那样。

### 4.2. 带注解的路线

当然，对于更实际的用例，我们可以使用注解定义我们需要的所有路由。我们应该为此使用单独的类。

首先，我们需要通过@Path注解创建一个Controller，Blade在启动时会扫描它。

然后我们需要使用与我们要拦截的 HTTP 方法相关的路由注解：

```java
@Path
public class RouteExampleController {    
    
    @GetRoute("/routes-example") 
    public String get(){ 
        return "get.html"; 
    }
    
    @PostRoute("/routes-example") 
    public String post(){ 
        return "post.html"; 
    }
    
    @PutRoute("/routes-example") 
    public String put(){ 
        return "put.html"; 
    }
    
    @DeleteRoute("/routes-example") 
    public String delete(){ 
        return "delete.html"; 
    }
}

```

我们还可以使用简单的@Route注解并将 HTTP 方法指定为参数：

```java
@Route(value="/another-route-example", method=HttpMethod.GET) 
public String anotherGet(){ 
    return "get.html" ; 
}

```

另一方面，如果我们不放置任何方法参数，路由将拦截对该 URL 的每个 HTTP 调用，无论动词是什么。

### 4.3. 参数注入

有几种方法可以将参数传递给我们的路由。让我们[通过文档中的](https://lets-blade.com/docs/route.html#参数注入)一些示例来探索它们。

-   表单参数：

```java
@GetRoute("/home")
public void formParam(@Param String name){
    System.out.println("name: " + name);
}

```

-   宁静参数：

```java
@GetRoute("/users/:uid")
public void restfulParam(@PathParam Integer uid){
    System.out.println("uid: " + uid);
}

```

-   文件上传参数：

```java
@PostRoute("/upload")
public void fileParam(@MultipartParam FileItem fileItem){
    byte[] file = fileItem.getData();
}

```

-   标头参数：

```java
@GetRoute("/header")
public void headerParam(@HeaderParam String referer){
    System.out.println("Referer: " + referer);
}

```

-   饼干参数：

```java
@GetRoute("/cookie")
public void cookieParam(@CookieParam String myCookie){
    System.out.println("myCookie: " + myCookie);
}

```

-   车身参数：

```java
@PostRoute("/bodyParam")
public void bodyParam(@BodyParam User user){
    System.out.println("user: " + user.toString());
}

```

-   Value 对象参数，通过将其属性发送到路由来调用：

```java
@PostRoute("/voParam")
public void voParam(@Param User user){
    System.out.println("user: " + user.toString());
}

<form method="post">
    <input type="text" name="age"/>
    <input type="text" name="name"/>
</form>

```

## 5.静态资源

如果需要，Blade 还可以提供静态资源，只需将它们放在/resources/static文件夹中即可。

例如， src/main/resources/static/app.css将在 http://localhost:9000/static/app.css 可用。

### 5.1. 自定义路径

我们可以通过以编程方式添加一个或多个静态路径来调整此行为：

```java
blade.addStatics("/custom-static");

```

通过配置，通过编辑文件src/main/resources/application.properties可以获得相同的结果：

```plaintext
mvc.statics=/custom-static

```

### 5.2. 启用资源列表

我们可以允许列出静态文件夹的内容，出于安全原因默认关闭该功能：

```java
blade.showFileList(true);

```

或者在配置中：

```plaintext
mvc.statics.show-list=true

```

我们现在可以打开 http://localhost:9000/custom-static/来显示文件夹的内容。

### 5.3. 使用 WebJar

[如WebJars 简介](https://www.baeldung.com/maven-webjars)教程中所示，打包为 JAR 的静态资源也是一个可行的选择。

Blade 在/webjars/路径下自动公开它们。

例如，让我们在pom.xml中导入[Bootstrap](https://search.maven.org/search?q=g:org.webjars AND a:bootstrap)：

```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>bootstrap</artifactId>
    <version>4.2.1</version>
</dependency>

```

因此，它将在http://localhost:9000/webjars/bootstrap/4.2.1/css/bootstrap.css下可用

## 6.HTTP 请求

由于Blade 不是基于 Servlet Specification ，因此它的接口[Request](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/mvc/http/Request.java)和它的类[HttpRequest 等](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/mvc/http/HttpRequest.java)对象与我们习惯的对象略有不同。

### 6.1. 表单参数

在读取表单参数时，Blade 在查询方法的结果中大量使用了Java的Optional(以下所有方法都返回一个 Optional 对象)：

-   查询(字符串名称)
-   queryInt(字符串名称)
-   queryLong(字符串名称)
-   queryDouble(字符串名称)

它们还具有后备值：

-   字符串查询(字符串名称，字符串默认值)
-   int queryInt(字符串名称，int defaultValue)
-   long queryLong(字符串名称，长默认值)
-   双查询双(字符串名称，双默认值)

我们可以通过自动映射属性读取表单参数：

```java
@PostRoute("/save")
public void formParams(@Param String username){
    // ...
}

```

或者从Request对象：

```java
@PostRoute("/save")
public void formParams(Request request){
    String username = request.query("username", "Baeldung");
}

```

### 6.2. JSON数据

现在让我们看一下如何将 JSON 对象映射到 POJO：

```bash
curl -X POST http://localhost:9000/users -H 'Content-Type: application/json'  
  -d '{"name":"Baeldung","site":"baeldung.com"}'

```

POJO(为了可读性用[Lombok](https://www.baeldung.com/intro-to-project-lombok)注解)：

```java
public class User {
    @Getter @Setter private String name;
    @Getter @Setter private String site;
}

```

同样，该值可作为注入属性使用：

```java
@PostRoute("/users")
public void bodyParams(@BodyParam User user){
    // ...
}

```

从请求中：

```java
@PostRoute("/users")
public void bodyParams(Request request) {
    String bodyString = request.bodyToString();
}

```

### 6.3. RESTful 参数

像localhost:9000/user/42这样漂亮的 URL 中的 RESTFul 参数也是一等公民：

```java
@GetRoute("/user/:id")
public void user(@PathParam Integer id){
    // ...
}

```

像往常一样，我们可以在需要时依赖Request对象：

```java
@GetRoute("/user")
public void user(Request request){
    Integer id = request.pathInt("id");
}

```

显然，同样的方法也适用于Long和String类型。

### 6.4. 数据绑定

Blade 支持 JSON 和 Form 绑定参数，并自动将它们附加到模型对象：

```java
@PostRoute("/users")
public void bodyParams(User user){}

```

### 6.5. 请求和会话属性

用于在Request和 Session中读取和写入对象的 API 非常清晰。

具有两个参数(表示键和值)的方法是我们可以用来将值存储在不同上下文中的更改器：

```java
Session session = request.session();
request.attribute("request-val", "Some Request value");
session.attribute("session-val", 1337);

```

另一方面，仅接受 key 参数的相同方法是访问器：

```java
String requestVal = request.attribute("request-val");
String sessionVal = session.attribute("session-val"); //It's an Integer

```

一个有趣的特性是[它们的通用返回类型  T](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/mvc/http/Request.java#L500)，这使我们无需强制转换结果。

### 6.6. 标头

相反，请求标头只能从请求中读取：

```java
String header1 = request.header("a-header");
String header2 = request.header("a-safe-header", "with a default value");
Map<String, String> allHeaders = request.headers();

```

### 6.7. 公用事业

以下实用方法也是开箱即用的，它们非常明显，不需要进一步解释：

-   布尔 isIE()
-   布尔 isAjax()
-   字符串内容类型()
-   字符串 userAgent()

### 6.8. 阅读饼干

让我们看看Request对象如何帮助我们处理 Cookie，特别是在读取 Optional<Cookie>时：

```java
Optional<Cookie> cookieRaw(String name);

```

如果 Cookie 不存在，我们还可以通过指定要应用的默认值来将其作为 字符串获取：

```java
String cookie(String name, String defaultValue);

```

最后，这是我们一次读取所有 Cookie 的方式(键是 Cookie 的名称，值是 Cookie 的值)：

```java
Map<String, String> cookies = request.cookies();

```

## 7.HTTP响应

类似于Request所做的事情，我们可以通过简单地将Response对象声明为路由方法的参数来获取对 Response 对象的引用：

```java
@GetRoute("/")
public void home(Response response) {}

```

### 7.1. 简单输出

我们可以通过其中一种方便的输出方法轻松地向调用方发送一个简单的输出，连同 200 HTTP 代码和适当的 Content-Type。

首先，我们可以发送一个纯文本：

```java
response.text("Hello World!");
```

其次，我们可以生成一个 HTML：

```java
response.html("<h1>Hello World!</h1>");
```

第三，我们同样可以生成一个 XML：

```java
response.xml("<Msg>Hello World!</Msg>");
```

最后，我们可以使用String输出 JSON ：

```java
response.json("{"The Answer":42}");

```

甚至从 POJO，利用自动 JSON 转换：

```java
User user = new User("Baeldung", "baeldung.com"); 
response.json(user);

```

### 7.2. 文件输出

从服务器下载文件再简单不过了：

```java
response.download("the-file.txt", "/path/to/the/file.txt");

```

第一个参数设置将要下载的文件的名称，而第二个参数(一个File对象，这里用String构造)表示服务器上实际文件的路径。

### 7.3. 模板渲染

Blade 还可以通过模板引擎渲染页面：

```java
response.render("admin/users.html");

```

模板的默认目录是src/main/resources/templates/，因此前一行将查找文件 src/main/resources/templates/admin/users.html。

稍后我们将在模板部分了解更多相关信息。

### 7.4. 重定向

重定向意味着向浏览器发送一个 302 HTTP 代码，以及一个 URL 以跟随第二个 GET。

我们可以重定向到另一个路由，或者重定向到一个外部 URL：

```java
response.redirect("/target-route");

```

### 7.5. 编写 Cookie

在这一点上，我们应该已经习惯了 Blade 的简单性。因此，让我们看看如何在一行代码中编写一个未过期的 Cookie：

```java
response.cookie("cookie-name", "Some value here");

```

事实上，删除 Cookie 同样简单：

```java
response.removeCookie("cookie-name");

```

### 7.6. 其他操作

最后，Response对象为我们提供了其他几种方法来执行写入 Headers、设置 Content-Type、设置 Status 代码等操作。

让我们快速浏览一下其中的一些：

-   响应状态(int status)
-   地图标题()
-   响应 notFound()
-   地图饼干()
-   响应内容类型(字符串内容类型)
-   void body(@NonNull byte[] 数据)
-   响应头(字符串名称，字符串值)

## 8.网络挂钩

WebHook 是一个拦截器，我们可以通过它在路由方法执行前后运行代码。

我们可以通过简单地实现WebHook功能接口并覆盖before()方法来创建 WebHook：

```java
@FunctionalInterface
public interface WebHook {

    boolean before(RouteContext ctx);

    default boolean after(RouteContext ctx) {
        return true;
    }
}

```

正如我们所见，after()是默认方法，因此我们只会在需要时覆盖它。

### 8.1. 拦截每一个请求

@Bean注解告诉框架使用 IoC 容器扫描类。

带有它注解的 WebHook 因此将在全球范围内工作，拦截对每个 URL 的请求：

```java
@Bean
public class BaeldungHook implements WebHook {

    @Override
    public boolean before(RouteContext ctx) {
        System.out.println("[BaeldungHook] called before Route method");
        return true;
    }
}

```

### 8.2. 缩小到一个 URL

我们还可以拦截特定的 URL，仅在这些路由方法周围执行代码：

```java
Blade.of()
  .before("/user/", ctx -> System.out.println("Before: " + ctx.uri()));
  .start(App.class, args);

```

### 8.3. 中间件

中间件是具有优先级的 WebHook，它们在任何标准 WebHook 之前执行：

```java
public class BaeldungMiddleware implements WebHook {

    @Override
    public boolean before(RouteContext context) {
        System.out.println("[BaeldungMiddleware] called before Route method and other WebHooks");
        return true;
    }
}

```

它们只需要在没有@Bean注解的情况下定义，然后通过use()以声明方式注册：

```java
Blade.of()
  .use(new BaeldungMiddleware())
  .start(App.class, args);

```

此外，Blade 自带以下与安全相关的内置中间件，其名称应该是不言自明的：

-   [基本授权中间件](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/security/web/auth/BasicAuthMiddleware.java)
-   [Cors中间件](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/security/web/cors/CorsMiddleware.java)
-   [Xss中间件](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/security/web/xss/XssMiddleware.java)
-   [Csrf中间件](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/security/web/csrf/CsrfMiddleware.java)

## 九、配置

在 Blade 中，配置完全是可选的，因为按照惯例一切都是开箱即用的。但是，我们可以在src/main/resources/application.properties文件中自定义默认设置并引入新属性。

### 9.1. 读取配置

我们可以用不同的方式读取配置，指定或不指定默认值以防设置不可用。

-   启动期间：

```java
Blade.of()
  .on(EventType.SERVER_STARTED, e -> {
      Optional<String> version = WebContext.blade().env("app.version");
  })
  .start(App.class, args);

```

-   在路线内：

```java
@GetRoute("/some-route")
public void someRoute(){
    String authors = WebContext.blade().env("app.authors","Unknown authors");
}

```

-   在自定义加载器中，通过实现BladeLoader接口，覆盖load()方法，并使用@Bean注解类：

```java
@Bean
public class LoadConfig implements BladeLoader {

    @Override
    public void load(Blade blade) {
        Optional<String> version = WebContext.blade().env("app.version");
        String authors = WebContext.blade().env("app.authors","Unknown authors");
    }
}

```

### 9.2. 配置属性

已配置但准备自定义的多个设置按类型分组并[在该地址](https://lets-blade.com/docs/configuration.html#配置清单)以三列表(名称、描述、默认值)列出。我们也可以参考翻译的页面，注意翻译错误地将设置名称大写。真正的设置是完全小写的。

按前缀对配置设置进行分组使它们可以同时读取到地图中，这在它们很多时很有用：

```java
Environment environment = blade.environment();
Map<String, Object> map = environment.getPrefix("app");
String version = map.get("version").toString();
String authors = map.get("authors","Unknown authors").toString();

```

### 9.3. 处理多个环境

将我们的应用程序部署到不同的环境时，我们可能需要指定不同的设置，例如与数据库连接相关的设置。Blade无需手动替换application.properties文件，而是为我们提供了一种为不同环境配置应用程序的方法。我们可以简单地保留所有开发设置的application.properties，然后在同一文件夹中创建其他文件，如application-prod.properties，只包含不同的设置。

在启动过程中，我们可以指定我们想要使用的环境，框架将使用 application-prod.properties 中最具体的设置和默认application.properties文件中的所有其他设置来合并文件：

```bash
java -jar target/sample-blade-app.jar --app.env=prod

```

## 10. 模板

Blade 中的模板是一个模块化的方面。虽然它集成了一个非常基本的模板引擎，但对于视图的任何专业用途，我们都应该依赖外部模板引擎。然后，我们可以从GitHub 上的[blade-template-engines存储库中](https://github.com/lets-blade/blade-template-engines)选择一个引擎，这些引擎是FreeMarker、Jetbrick、Pebble和Velocity，甚至可以创建一个包装器来导入我们喜欢的另一个模板。

Blade 的作者推荐[了 Jetbrick](https://search.maven.org/search?q=g:com.github.subchen AND a:jetbrick-template)，这是另一个聪明的中国项目。

### 10.1. 使用默认引擎

默认模板通过${}符号解析来自不同上下文的变量：

```xml
<h1>Hello, ${name}!</h1>

```

### 10.2. 插入外部引擎

切换到不同的模板引擎是一件轻而易举的事！我们只需导入引擎(的 Blade 包装器)的依赖项：

```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-template-jetbrick</artifactId>
    <version>0.1.3</version>
</dependency>

```

此时，编写一个简单的配置来指示框架使用该库就足够了：

```java
@Bean
public class TemplateConfig implements BladeLoader {

    @Override
    public void load(Blade blade) {
        blade.templateEngine(new JetbrickTemplateEngine());
    }
}

```

因此，现在src/main/resources/templates/下的每个文件都将使用新引擎进行解析，其语法超出了本教程的范围。

### 10.3. 包装新引擎

包装一个新的模板引擎需要创建一个类，它必须实现TemplateEngine接口并覆盖render()方法：

```java
void render (ModelAndView modelAndView, Writer writer) throws TemplateException;

```

为此，我们可以查看[实际 Jetbrick 包装器的代码](https://github.com/lets-blade/blade-template-engines/blob/master/blade-template-jetbrick/src/main/java/com/blade/mvc/view/template/JetbrickTemplateEngine.java)以了解其含义。

## 11. 记录

Blade 使用slf4j-api作为日志接口。

它还包括一个已配置的日志记录实现，称为[blade-log](https://github.com/lets-blade/blade-log)。因此，我们不需要导入任何东西；它按原样工作，只需定义一个Logger：

```java
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExample.class);

```

### 11.1. 自定义集成记录器

如果我们想要修改默认配置，我们需要调整以下参数作为系统属性：

-   日志记录级别(可以是“trace”、“debug”、“info”、“warn”或“error”)：

```plaintext
# Root Logger
com.blade.logger.rootLevel=info

# Package Custom Logging Level
com.blade.logger.somepackage=debug

# Class Custom Logging Level
com.blade.logger.com.baeldung.sample.SomeClass=trace

```

-   显示信息：

```plaintext
# Date and Time
com.blade.logger.showDate=false

# Date and Time Pattern
com.blade.logger.datePattern=yyyy-MM-dd HH:mm:ss:SSS Z

# Thread Name
com.blade.logger.showThread=true

# Logger Instance Name
com.blade.logger.showLogName=true

# Only the Last Part of FQCN
com.blade.logger.shortName=true

```

-   记录器：

```plaintext
# Path 
com.blade.logger.dir=./logs

# Name (it defaults to the current app.name)
com.blade.logger.name=sample

```

### 11.2. 不包括集成记录器

虽然已经配置了一个集成记录器对于启动我们的小项目非常方便，但我们可能很容易在其他库导入他们自己的日志记录实现的情况下结束。而且，在那种情况下，我们可以删除集成的，以避免冲突：

```xml
<dependency>
    <groupId>com.bladejava</groupId>
    <artifactId>blade-mvc</artifactId>
    <version>${blade.version}</version>
    <exclusions>
        <exclusion>
            <groupId>com.bladejava</groupId>
            <artifactId>blade-log</artifactId>
        </exclusion>
    </exclusions>
</dependency>

```

## 12.定制

### 12.1. 自定义异常处理

异常处理程序也默认内置在框架中。它将异常打印到控制台，如果app.devMode为true，则堆栈跟踪也会在网页上可见。

但是，我们可以通过定义扩展DefaultExceptionHandler类的@Bean以特定方式处理异常：

```java
@Bean
public class GlobalExceptionHandler extends DefaultExceptionHandler {

    @Override
    public void handle(Exception e) {
        if (e instanceof BaeldungException) {
            BaeldungException baeldungException = (BaeldungException) e;
            String msg = baeldungException.getMessage();
            WebContext.response().json(RestResponse.fail(msg));
        } else {
            super.handle(e);
        }
    }
}

```

### 12.2. 自定义错误页面

同样，错误404 – Not Found和500 – Internal Server Error是通过瘦默认页面处理的。

我们可以强制框架使用我们自己的页面，方法是在application.properties文件中使用以下设置声明它们：

```plaintext
mvc.view.404=my-404.html
mvc.view.500=my-500.html

```

当然，那些 HTML 页面必须放在src/main/resources/templates文件夹下。

在 500 个中，我们还可以通过它们的特殊变量检索异常消息和堆栈跟踪：

```html
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>500 Internal Server Error</title>
    </head>
    <body>
        <h1> Custom Error 500 Page </h1>
        <p> The following error occurred： "<strong>${message}</strong>"</p>
        <pre> ${stackTrace} </pre>
    </body>
</html>
```

## 13.计划任务

该框架的另一个有趣的特性是可以安排方法的执行。

这可以通过使用@Schedule注解注解@Bean类的方法来实现：

```java
@Bean
public class ScheduleExample {

    @Schedule(name = "baeldungTask", cron = "0 /1    ?")
    public void runScheduledTask() {
        System.out.println("This is a scheduled Task running once per minute.");
    }
}

```

实际上，它使用经典的 cron 表达式来指定DateTime坐标。我们可以在[A Guide to Cron Expressions](https://www.baeldung.com/cron-expressions)中阅读更多关于这些的信息。

稍后，我们可能会利用[TaskManager](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/task/TaskManager.java)类的静态方法来对计划任务执行操作。

-   获取所有定时任务：

```java
List<Task> allScheduledTasks = TaskManager.getTasks();

```

-   按名称获取任务：

```java
Task myTask = TaskManager.getTask("baeldungTask");

```

-   按名称停止任务：

```java
boolean closed = TaskManager.stopTask("baeldungTask");

```

## 14. 事件

正如在 9.1 节中已经看到的，可以在运行一些自定义代码之前监听指定的事件。

Blade 开箱即用地提供了以下事件：

```java
public enum EventType {
    SERVER_STARTING,
    SERVER_STARTED,
    SERVER_STOPPING,
    SERVER_STOPPED,
    SESSION_CREATED,
    SESSION_DESTROY,
    SOURCE_CHANGED,
    ENVIRONMENT_CHANGED
}

```

虽然前六个很容易猜到，但后两个需要一些提示：ENVIRONMENT_CHANGED允许我们在服务器启动时配置文件更改时执行操作。相反， SOURCE_CHANGED尚未实现，仅供将来使用。

让我们看看如何在创建会话时将值放入会话中：

```java
Blade.of()
  .on(EventType.SESSION_CREATED, e -> {
      Session session = (Session) e.attribute("session");
      session.attribute("name", "Baeldung");
  })
  .start(App.class, args);

```

## 15.会话实现

谈到会话，它的默认实现将会话值存储在内存中。

因此，我们可能希望切换到不同的实现来提供缓存、持久性或其他功能。让我们以 Redis 为例。我们首先需要通过实现[Session](https://github.com/lets-blade/blade/blob/master/src/main/java/com/blade/mvc/http/Session.java)接口来创建我们的RedisSession包装器，如[HttpSession](https://lets-blade.com/docs/session-ext.html)[的文档](https://lets-blade.com/docs/session-ext.html)所示。

然后，只需让框架知道我们要使用它即可。我们可以用与自定义模板引擎相同的方式来做到这一点，唯一的区别是我们调用了sessionType()方法：

```java
@Bean
public class SessionConfig implements BladeLoader {
 
    @Override
    public void load(Blade blade) {
        blade.sessionType(new RedisSession());
    }
}

```

## 16.命令行参数

当从命令行运行 Blade 时，我们可以指定三个设置来改变它的行为。

首先，我们可以更改 IP 地址，默认为本地0.0.0.0环回：

```bash
java -jar target/sample-blade-app.jar --server.address=192.168.1.100

```

其次，我们还可以更改端口，默认情况下是9000：

```bash
java -jar target/sample-blade-app.jar --server.port=8080

```

最后，如第 9.3 节所示，我们可以更改环境，让不同的application-XXX.properties文件在默认文件application.properties上被读取：

```bash
java -jar target/sample-blade-app.jar --app.env=prod

```

## 17.在IDE中运行

任何现代JavaIDE 都可以运行 Blade 项目，甚至不需要 Maven 插件。在 IDE 中运行 Blade 在运行[Blade Demos](https://github.com/lets-blade/blade-demos)时特别有用，这些示例专门用于展示框架的功能。它们都继承了父 pom，因此让 IDE 完成工作更容易，而不是手动调整它们以作为独立应用程序运行。

### 17.1. 蚀

在 Eclipse 中，右键单击项目并启动Run asJavaApplication，选择我们的App类，然后按OK就足够了。

然而，Eclipse 的控制台不会正确显示 ANSI 颜色，而是输出它们的代码：

[![Eclipse 工作区示例应用程序 - Eclipse IDE](https://www.baeldung.com/wp-content/uploads/2019/01/Screenshot-from-2019-01-08-23-43-10.png)](https://www.baeldung.com/wp-content/uploads/2019/01/Screenshot-from-2019-01-08-23-43-10.png)

幸运的是，[在控制台](https://marketplace.eclipse.org/content/ansi-escape-console)扩展中安装 ANSI Escape 可以永久解决这个问题：

[![Eclipse 工作区 - Eclipse IDE](https://www.baeldung.com/wp-content/uploads/2019/01/Screenshot-from-2019-01-08-23-44-25.png)](https://www.baeldung.com/wp-content/uploads/2019/01/Screenshot-from-2019-01-08-23-44-25.png)

### 17.2. 我明白这个想法

IntelliJ IDEA 开箱即用地支持 ANSI 颜色。因此，只需创建项目、右键单击App文件并启动Run 'App.main()'(相当于按Ctrl+Shift+F10)：

[![示例刀片应用程序](https://www.baeldung.com/wp-content/uploads/2019/01/Screenshot-from-2019-01-12-00-44-01.png)](https://www.baeldung.com/wp-content/uploads/2019/01/Screenshot-from-2019-01-12-00-44-01.png)

### 17.3. 视觉工作室代码

[也可以通过预先安装Java Extension Pack](https://code.visualstudio.com/docs/java/extensions)来使用 VSCode，这是一种流行的非以Java为中心的 IDE 。

然后按Ctrl+F5将运行该项目：

[![pom.xml 示例应用程序](https://www.baeldung.com/wp-content/uploads/2019/01/Screenshot-from-2019-01-12-00-59-43.png)](https://www.baeldung.com/wp-content/uploads/2019/01/Screenshot-from-2019-01-12-00-59-43.png)

## 18.总结

我们已经了解了如何使用 Blade 创建一个小型 MVC 应用程序。

[整个文档](https://github.com/lets-blade/blade)仅提供中文版本。尽管主要在中国广泛传播，但由于[其起源于中国](https://lets-blade.com/)，[作者](https://github.com/hellokaton)最近翻译了 API 并将该项目的核心功能用英文记录在[GitHub](https://github.com/lets-blade/blade)上。
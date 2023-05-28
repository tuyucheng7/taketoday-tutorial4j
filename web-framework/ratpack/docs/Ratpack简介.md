## 1. 概述

[Ratpack](https://ratpack.io/)是一组基于JVM 的库，专为现代高性能实时应用程序构建。它建立在嵌入式Netty事件驱动网络引擎之上，完全符合反应式设计模式。

在本文中，我们将学习如何使用 Ratpack 并使用它构建一个小型应用程序。

## 2. 为什么是老鼠包？

Ratpack的主要优点：

-   它非常轻巧，快速且可扩展
-   与 DropWizard 等其他框架相比，它消耗的内存更少；[一个有趣的基准比较结果可以在这里](https://phillbarber.blogspot.in/2016/01/choosing-between-ratpack-and-dropwizard.html)找到
-   因为它建立在Netty之上，Ratpack 本质上是完全事件驱动和非阻塞的
-   它支持Guice依赖管理
-   很像Spring Boot， Ratpack 有自己的测试库来快速设置测试用例

## 3.创建应用程序

要了解 Ratpack 的工作原理，让我们先用它创建一个小应用程序。

### 3.1. Maven 依赖项

首先，让我们将以下依赖项添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-core</artifactId>
    <version>1.4.5</version>
</dependency>
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-test</artifactId>
    <version>1.4.5</version>
</dependency>
```

[你可以在Maven ](https://search.maven.org/classic/#search|gav|1|g%3A"io.ratpack" AND a%3A"ratpack-core")[Central](https://search.maven.org/classic/#search|gav|1|g%3A"io.ratpack" AND a%3A"ratpack-core")查看最新版本。

请注意，虽然我们使用 Maven 作为构建系统，但根据[Ratpack 的建议](https://ratpack.io/manual/current/quick-start.html#using_the_gradle_plugins)，最好使用Gradle作为构建工具，因为 Ratpack 具有通过[Ratpack 的 Gradle 插件](https://plugins.gradle.org/search?term=ratpack)提供的一流 Gradle 支持。

我们可以使用以下构建 Gradle 脚本：

```plaintext
buildscript {
    repositories {
      jcenter()
    }
    dependencies {
      classpath "io.ratpack:ratpack-gradle:1.4.5"
    }
}
 
apply plugin: "io.ratpack.ratpack-java"
repositories {
    jcenter()
}
dependencies {
    testCompile 'junit:junit:4.11'
    runtime "org.slf4j:slf4j-simple:1.7.21"
}
test {
    testLogging {
      events 'started', 'passed'
    }
}

```

### 3.2. 构建应用程序

配置构建管理后，我们需要创建一个类来启动嵌入式Netty服务器并构建一个简单的上下文来处理默认请求：

```java
public class Application {
	
    public static void main(String[] args) throws Exception {
        RatpackServer.start(server -> server.handlers(chain -> chain
          .get(ctx -> ctx.render("Welcome to Baeldung ratpack!!!"))));
    }
}
```

如我们所见，通过使用RatpackServer我们现在可以启动服务器(默认端口 5050)。handlers ()方法接受一个接收[Chain](https://ratpack.io/manual/current/api/ratpack/handling/Chain.html)对象的函数，该对象映射所有相应的传入请求。此“Handler Chain API”用于构建响应处理策略。

如果我们运行此代码片段并在浏览器中访问 http://localhost:5050，“欢迎来到 Baeldung ratpack！！！” 应该显示。

同样，我们可以映射一个 HTTP POST 请求。

### 3.3. 处理 URL 路径参数

在下一个示例中，我们需要在应用程序中捕获一些 URL 路径参数。在 Ratpack 中，我们使用[PathTokens](https://ratpack.io/manual/current/api/ratpack/path/PathTokens.html)来捕获它们：

```java
RatpackServer.start(server -> server
  .handlers(chain -> chain
  .get(":name", ctx -> ctx.render("Hello " 
  + ctx.getPathTokens().get("name") + " !!!"))));
```

在这里，我们映射名称URL 参数。每当像http://localhost:5050/John这样的请求出现时，响应将是“Hello John !!!”。

### 3.4. 使用/不使用过滤器的请求/响应标头修改

有时，我们需要根据需要修改内联 HTTP 响应标头。Ratpack 有[MutableHeaders](https://ratpack.io/manual/current/api/ratpack/http/MutableHeaders.html)来自定义传出响应。

例如，我们需要更改响应中的以下标头：Access-Control-Allow-Origin、Accept-Language和Accept-Charset：

```java
RatpackServer.start(server -> server.handlers(chain -> chain.all(ctx -> {
    MutableHeaders headers = ctx.getResponse().getHeaders();
    headers.set("Access-Control-Allow-Origin", "");
    headers.set("Accept-Language", "en-us");
    headers.set("Accept-Charset", "UTF-8");
    ctx.next();
}).get(":name", ctx -> ctx
    .render("Hello " + ctx.getPathTokens().get("name") + "!!!"))));
```

通过使用MutableHeaders，我们设置了三个标头并将它们推送到Chain中。

同样，我们也可以检查传入的请求标头：

```java
ctx.getRequest().getHeaders().get("//TODO")
```

同样可以通过创建过滤器来实现。Ratpack 有一个[Handler](https://ratpack.io/manual/current/api/ratpack/handling/Handler.html) interface ，可以实现它来创建一个过滤器。它只有一个方法handle()，将当前Context作为参数：

```java
public class RequestValidatorFilter implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        MutableHeaders headers = ctx.getResponse().getHeaders();
        headers.set("Access-Control-Allow-Origin", "");
        ctx.next();
    }
}
```

我们可以通过以下方式使用此过滤器：

```java
RatpackServer.start(
    server -> server.handlers(chain -> chain
      .all(new RequestValidatorFilter())
      .get(ctx -> ctx.render("Welcome to baeldung ratpack!!!"))));
}
```

### 3.5. JSON 解析器

Ratpack 内部使用faster-jackson进行 JSON 解析。我们可以使用[Jackson](https://ratpack.io/manual/current/api/ratpack/jackson/Jackson.html)模块将任何对象解析为 JSON。

让我们创建一个用于解析的简单 POJO 类：

```java
public class Employee {

    private Long id;
    private String title;
    private String name;

    // getters and setters 

}
```

在这里，我们创建了一个名为Employee的简单 POJO 类，它具有三个参数：id、title和name。现在我们将使用此Employee对象转换为 JSON 并在命中特定 URL 时返回相同的内容：

```java
List<Employee> employees = new ArrayList<Employee>();
employees.add(new Employee(1L, "Mr", "John Doe"));
employees.add(new Employee(2L, "Mr", "White Snow"));

RatpackServer.start(
    server -> server.handlers(chain -> chain
      .get("data/employees",
      ctx -> ctx.render(Jackson.json(employees)))));
```

如我们所见，我们手动将两个Employee对象添加到列表中，并使用Jackson模块将它们解析为 JSON。只要点击/data/employees URL，就会返回 JSON 对象。

这里要注意的是，我们根本没有使用ObjectMapper，因为 Ratpack 的 Jackson 模块会在运行中完成需要的工作。

### 3.6. 内存数据库

Ratpack 对内存数据库有一流的支持。它使用[HikariCP](https://github.com/brettwooldridge/HikariCP)进行 JDBC 连接池。为了使用它，我们需要在pom.xml中添加 Ratpack 的[HikariCP 模块依赖](https://search.maven.org/classic/#search|ga|1|a%3A"ratpack-hikari")：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-hikari</artifactId>
    <version>1.4.5</version>
</dependency>
```

如果我们使用Gradle，同样需要在 Gradle 构建文件中添加：

```plaintext
compile ratpack.dependency('hikari')
```

现在，我们需要创建一个包含表 DDL 语句的 SQL 文件，以便在服务器启动并运行时立即创建表。我们将在src/main/resources目录中创建DDL.sql文件并向其中添加一些 DDL 语句。

由于我们使用的是 H2 数据库，因此我们也必须为其添加依赖项。

现在，通过使用 HikariModule，我们可以在运行时初始化数据库：

```java
RatpackServer.start(
    server -> server.registry(Guice.registry(bindings -> 
      bindings.module(HikariModule.class, config -> {
          config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
          config.addDataSourceProperty("URL",
          "jdbc:h2:mem:baeldung;INIT=RUNSCRIPT FROM 'classpath:/DDL.sql'");
      }))).handlers(...));
```

## 4.测试

如前所述，Ratpack 对 jUnit 测试用例有一流的支持。通过使用[MainClassApplicationUnderTest](https://ratpack.io/manual/current/api/ratpack/test/MainClassApplicationUnderTest.html)，我们可以轻松创建测试用例并测试端点：

```java
@RunWith(JUnit4.class)
public class ApplicationTest {

    MainClassApplicationUnderTest appUnderTest
      = new MainClassApplicationUnderTest(Application.class);

    @Test
    public void givenDefaultUrl_getStaticText() {
        assertEquals("Welcome to baeldung ratpack!!!", 
          appUnderTest.getHttpClient().getText("/"));
    }

    @Test
    public void givenDynamicUrl_getDynamicText() {
        assertEquals("Hello dummybot!!!", 
          appUnderTest.getHttpClient().getText("/dummybot"));
    }

    @Test
    public void givenUrl_getListOfEmployee() 
      throws JsonProcessingException {
 
        List<Employee> employees = new ArrayList<Employee>();
        ObjectMapper mapper = new ObjectMapper();
        employees.add(new Employee(1L, "Mr", "John Doe"));
        employees.add(new Employee(2L, "Mr", "White Snow"));

        assertEquals(mapper.writeValueAsString(employees), 
          appUnderTest.getHttpClient().getText("/data/employees"));
    }
 
    @After
    public void shutdown() {
        appUnderTest.close();
    }

}
```

请注意，我们需要通过调用close()方法手动终止正在运行的MainClassApplicationUnderTest实例，因为它可能会不必要地阻塞 JVM 资源。这就是为什么我们使用@After注解在测试用例执行后强制终止实例。

## 5.总结

在这篇文章中，我们看到了使用 Ratpack 的简单性。
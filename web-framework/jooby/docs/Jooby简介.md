## 1. 概述

[Jooby](http://jooby.org/)是一个可扩展且快速的微型网络框架，构建在最常用的NIO 网络服务器之上。它非常简单和模块化，显然是为现代网络架构设计的。它还支持[Javascript](https://www.jooby.org/docs/)和Kotlin。

默认情况下，Jooby对Netty、Jetty 和 Undertow提供了强大的支持。

在本文中，我们将了解Jooby项目的总体结构以及如何使用Jooby构建一个简单的 Web 应用程序。

## 2. 应用架构

一个简单的Jooby应用程序结构如下所示：

```plaintext
├── public
|   └── welcome.html
├── conf
|   ├── application.conf
|   └── logback.xml
└── src
|   ├── main
|   |   └── java
|   |       └── com
|   |           └── baeldung
|   |               └── jooby
|   |                   └── App.java
|   └── test
|       └── java
|           └── com
|               └── baeldung
|                   └── jooby
|                       └── AppTest.java
├── pom.xml
```

这里要注意的是，public目录下可以放css/js/html等静态文件， conf目录下可以放logback.xml、application.conf等应用需要的配置文件。

## 3.Maven依赖

我们可以通过将以下依赖项添加到我们的pom.xml 来创建一个简单的Jooby应用程序：

```xml
<dependency>
    <groupId>org.jooby</groupId>
    <artifactId>jooby-netty</artifactId>
    <version>1.1.3</version>
</dependency>
```

如果我们想选择Jetty或Undertow，我们可以使用以下依赖项：

```xml
<dependency>
    <groupId>org.jooby</groupId>
    <artifactId>jooby-jetty</artifactId>
    <version>1.1.3</version>
</dependency>
<dependency>
    <groupId>org.jooby</groupId>
    <artifactId>jooby-undertow</artifactId>
    <version>1.1.3</version>
</dependency>
```

[你可以在Central Maven Repository中查看](https://search.maven.org/classic/#search|ga|1|jooby)Jooby项目的最新版本。

Jooby也有专门的 Maven 原型。我们可以使用它来创建一个示例项目，其中包含所有必要的预构建依赖项。

我们可以使用下面的脚本来生成示例工程：

```plaintext
mvn archetype:generate -B -DgroupId=com.baeldung.jooby -DartifactId=jooby 
-Dversion=1.0 -DarchetypeArtifactId=jooby-archetype 
-DarchetypeGroupId=org.jooby -DarchetypeVersion=1.1.3
```

## 4. 构建应用程序

### 4.1. 启动服务器

要启动嵌入式服务器，我们需要使用以下代码片段：

```java
public class App extends Jooby {
    public static void main(String[] args) {
        run(App::new, args);
    }
}
```

一旦启动，服务器将在默认端口 8080上运行。

我们还可以使用自定义端口和自定义HTTPS端口配置后端服务器：

```java
{
    port( 8081 );
    securePort( 8443 );
}
```

### 4.2. 实施路由器

在Jooby中创建基于路径的路由器非常容易。例如，我们可以通过以下方式为路径“ /login ”创建一个路由器：

```java
{
    get( "/login", () -> "Hello from Baeldung");
}
```

以类似的方式，如果我们想处理其他HTTP方法，如 POST、PUT 等，我们可以使用下面的代码片段：

```java
{
    post( "/save", req -> {
        Mutant token = req.param( "token" );
        return token.intValue();
    });
}
```

在这里，我们从请求中获取请求参数名称令牌。默认情况下，所有请求参数都被类型转换为Jooby的[Mutant](https://javadoc.io/static/org.jooby/jooby/1.2.3/org/jooby/Mutant.html)数据类型。基于预期，我们可以将其转换为任何支持的原始数据类型。

我们可以通过以下方式检查任何 url 参数：

```java
{
    get( "/user/{id}", req -> "Hello user : " + req.param("id").value() );
    get( "/user/:id", req -> "Hello user: " + req.param("id").value() );
}
```

我们可以使用以上任何一种。也可以找到以固定内容开头的参数。例如，我们可以通过以下方式找到以“ uid:”开头的 URL 参数：

```java
{
    get( "/uid:{id}", req -> "Hello User with id : uid" + 
        req.param("id").value());
}
```

### 4.3. 实现 MVC 模式控制器

对于企业应用程序，Jooby带有一个 MVC API，就像任何其他 MVC 框架(如 Spring MVC)一样。

例如，我们可以处理名为“ /hello ”的路径：

```java
@Path("/hello")
public class GetController {
    @GET
    public String hello() {
        return "Hello Baeldung";
    }
}
```

非常类似地，我们可以创建一个处理程序来处理带有[@POST](https://javadoc.io/static/org.jooby/jooby/1.2.3/org/jooby/mvc/POST.html)、[@PUT 、](https://javadoc.io/static/org.jooby/jooby/1.2.3/org/jooby/mvc/PUT.html) [@DELETE](https://javadoc.io/static/org.jooby/jooby/1.2.3/org/jooby/mvc/DELETE.html)等注解的其他 HTTP 方法。

### 4.4. 处理静态内容

要提供任何静态内容，如 HTML、Javascript、CSS、图像等，我们需要将这些文件放在公共目录中。

放置后，我们可以从路由器将任何 url 映射到这些资源：

```java
{
    assets( "/employee" , "form.html" );
}
```

### 4.5. 办理表格

默认情况下，Jooby 的[Request接口可以处理任何表单对象，而无需使用任何手动类型转换。](https://javadoc.io/static/org.jooby/jooby/1.2.3/org/jooby/Request.html)

假设我们需要通过表单提交员工详细信息。第一步，我们需要创建一个用于保存数据的Employee bean 对象：

```java
public class Employee {
    String id;
    String name;
    String email;

    // standard constructors, getters and setters
}
```

现在，我们需要创建一个页面来创建表单：

```java
<form enctype="application/x-www-form-urlencoded" action="/submitForm" 
    method="post">
    <input name="id" />
    <input name="name" />
    <input name="email" />
    <input type="submit" value="Submit"/>
</form>
```

接下来，我们将创建一个 post 处理程序来处理此表单并获取提交的数据：

```java
post( "/submitForm", req -> {
    Employee employee = req.params(Employee.class);
    // ...
    return "empoyee data saved successfullly";
});
```

这里需要注意的是，我们必须要声明表单enctype为application/x-www-form-urlencoded来支持动态表单绑定。

通过[Request.file(String filename)](https://javadoc.io/static/org.jooby/jooby/1.2.3/org/jooby/Request.html#file-java.lang.String-)我们可以检索上传的文件：

```java
post( "/upload", req -> {
    Upload upload = req.file("file");
    // ...
    upload.close();
});
```

### 4.6. 实现过滤器

开箱即用，Jooby提供了定义全局过滤器和基于路径的过滤器的灵活性。

在Jooby中实现过滤器有点棘手，因为我们需要配置 URL 路径两次，一次用于过滤器，另一次用于处理程序。

例如，如果我们必须为名为“ /filter”的 URL 路径实现过滤器，则需要在该路径中显式实现过滤器：

```java
get( "/filter", ( req, resp, chain ) -> {
    // ...
    chain.next( req, resp );
});
```

语法与Servlet过滤器非常相似。可以通过调用[Response.send(Result result)](https://javadoc.io/static/org.jooby/jooby/1.2.3/org/jooby/Response.html#send-org.jooby.Result-)方法来限制请求并在过滤器本身中发回响应。

实现过滤器后，我们需要实现请求处理程序：

```java
get("/filter", (req, resp) -> {
    resp.send("filter response");
});
```

### 4.7. 会议

Jooby带有两种类型的会话实现；内存中和基于 cookie 的。

实现内存中会话管理非常简单。我们可以选择Jooby提供的任何高吞吐量会话存储，例如EhCache、Guava、HazleCast、Cassandra、Couchbase、Redis、MongoDB和Memcached。

例如，要实现一个基于 Redis 的会话存储，我们需要添加如下 Maven 依赖：

```xml
<dependency>
    <groupId>org.jooby</groupId>
    <artifactId>jooby-jedis</artifactId>
    <version>1.1.3</version>
</dependency>
```

现在我们可以使用下面的代码片段来启用会话管理：

```java
{
    use(new Redis());
    session(RedisSessionStore.class);

    get( "/session" , req -> {
        Session session = req.session();
        session.set("token", "value");
        return session.get("token").value();
    });
}
```

这里需要注意的是，我们可以在application.conf中将Redis url 配置为'db'属性。

要启用基于 cookie 的会话管理，我们需要声明cookieSession()。如果选择基于 cookie 的方法，我们必须需要在application.conf文件中声明application.secret属性。由于每个 cookie 都将使用此密钥进行签名，因此始终建议使用长随机字符串片段作为密钥。


在内存和基于 cookie 的方法中，我们必须在application.conf文件中声明必要的配置参数，否则应用程序将在启动时抛出[IllegalStateException 。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/IllegalStateException.html)

## 5. 测试

测试 MVC 路由确实很容易，因为路由绑定到某个类的策略。这使得对所有路由运行单元测试变得容易。

例如，我们可以为默认 URL 快速创建一个测试用例：

```java
public class AppTest {
 
    @ClassRule
    public static JoobyRule app = new JoobyRule(new App());

    @Test
    public void given_defaultUrl_expect_fixedString() {
 
        get("/").then().assertThat().body(equalTo("Hello World!"))
          .statusCode(200).contentType("text/html;charset=UTF-8");
    }
}
```

这里要注意的是，使用@ClassRule注解只会为所有测试用例创建一个服务器实例。如果我们需要为每个测试用例构建单独的服务器实例，我们必须使用不带静态修饰符的@Rule注解。

我们也可以使用Jooby 的[MockRouter](https://github.com/jooby-project/jooby/blob/2.x/modules/jooby-test/src/main/java/io/jooby/MockRouter.java)以同样的方式测试路径：

```java
@Test
public void given_defaultUrl_with_mockrouter_expect_fixedString() 
  throws Throwable {
 
    String result = new MockRouter(new App()).get("/");
 
    assertEquals("Hello World!", result);
}
```

## 六. 总结

在本教程中，我们探讨了Jooby项目及其基本功能。
## 1. 概述

[Ratpack](https://www.baeldung.com/ratpack)是一组轻量级Java库，用于构建具有反应性、异步和非阻塞特性的可伸缩 HTTP 应用程序。

此外，Ratpack 还提供与[Google Guice、](https://www.baeldung.com/ratpack-google-guice) [Spring Boot](https://www.baeldung.com/ratpack-spring-boot)、[RxJava](https://www.baeldung.com/ratpack-rxjava)和[Hystrix](https://www.baeldung.com/ratpack-hystrix)等技术和框架的集成。

在本教程中，我们将探讨如何将 Ratpack 与 Groovy 结合使用。

## 2. 为什么选择 Groovy？

[Groovy](https://www.baeldung.com/groovy-language)是一种在 JVM 中运行的强大的动态语言。

因此，Groovy 使脚本和领域特定语言变得非常容易。使用 Ratpack，这提供了快速的 Web 应用程序开发。

Ratpack 通过ratpack-groovy 和 ratpack -groovy-test库提供与 Groovy 的轻松集成。

## 3. 使用 Groovy 脚本的 Ratpack 应用程序

Ratpack [Groovy API](https://github.com/ratpack/ratpack/tree/master/ratpack-groovy)是用Java构建的，因此它们可以轻松地与Java和 Groovy 应用程序集成。它们在ratpack.groovy 包中可用。

实际上，结合 Groovy 的脚本能力和 Grape 依赖管理，我们可以在几行代码中快速创建一个 Ratpack 驱动的 Web 应用程序：

```groovy
@Grab('io.ratpack:ratpack-groovy:1.6.1')
import static ratpack.groovy.Groovy.ratpack

ratpack {
    handlers {
        get {
            render 'Hello World from Ratpack with Groovy!!'
        }    
    }
}
```

这是我们的第一个处理程序，处理 GET 请求。我们所要做的就是添加一些基本的 DSL 来启用 Ratpack 服务器。

现在让我们将其作为 Groovy 脚本运行以启动应用程序。默认情况下，该应用程序将在[http://localhost:5050](http://localhost:5050/)上可用：

```bash
$ curl -s localhost:5050
Hello World from Ratpack with Groovy!!
```

我们还可以使用ServerConfig配置端口：

```groovy
ratpack {
    serverConfig {
        port(5056)
    }
}
```

Ratpack 还提供热重载功能，这意味着我们可以更改Ratpack.groovy，然后在应用程序为我们的下一个 HTTP 请求提供服务时查看更改。

## 4. Ratpack-Groovy依赖管理

有几种方法可以启用ratpack-groovy支持。

### 4.1. 葡萄

我们可以使用 Groovy 的嵌入式依赖管理器 Grape。

就像在我们的Ratpack.groovy脚本中添加注解一样简单：

```java
@Grab('io.ratpack:ratpack-groovy:1.6.1')
import static ratpack.groovy.Groovy.ratpack
```

### 4.2. Maven 依赖

[为了在 Maven 中构建，我们只需要添加对ratpack-groovy](https://mvnrepository.com/artifact/io.ratpack/ratpack-groovy)[库的](https://mvnrepository.com/artifact/io.ratpack/ratpack-groovy)依赖：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-groovy</artifactId>
    <version>${ratpack.version}</version>
</dependency>

```

### 4.3. 摇篮

我们可以通过在build.gradle中为 Groovy 添加 Ratpack 的 Gradle 插件来启用ratpack-groovy集成：

```shell
plugins { 
  id 'io.ratpack.ratpack-groovy' version '1.6.1' 
}
```

## 5. Groovy 中的 Ratpack 处理程序

处理程序提供了一种处理 Web 请求和响应的方法。可以在此闭包中访问请求和响应对象。

我们可以使用 GET 和 POST 等 HTTP 方法处理 Web 请求：

```groovy
handlers { 
    get("greet/:name") { ctx ->
        render "Hello " + ctx.getPathTokens().get("name") + " !!!"
    }
}      

```

我们可以通过[http://localhost:5050/greet/](http://localhost:5050/greet/Norman)来测试这个网络请求：

```bash
$ curl -s localhost:5050/greet/Norman
Hello Norman!!!
```

在处理程序的代码中，ctx是上下文注册表对象，它授予对路径变量、请求和响应对象的访问权限。

处理程序还支持通过 Jackson 处理 JSON。

让我们返回从 Groovy 映射转换而来的 JSON：

```groovy
get("data") {
    render Jackson.json([title: "Mr", name: "Norman", country: "USA"])
}

$ curl -s localhost:5050/data
{"title":"Mr","name":"Norman","country":"USA"}
```

在这里，Jackson.json用于进行转换。

## 6. Ratpack 在 Groovy 中的承诺

正如我们所知，Ratpack 在应用程序中启用了异步和非阻塞特性。这是通过[Ratpack Promises](https://www.baeldung.com/ratpack-http-client)实现的。

Promises 类似于 JavaScript 中使用的那些，有点像Java[Future](https://www.baeldung.com/java-future)。我们可以将Promise视为将来可用的值的表示：

```groovy
post("user") {
    Promise<User> user = parse(Jackson.fromJson(User)) 
    user.then { u -> render u.name } 
}
```

这里的最后一个动作是then动作，它决定了如何处理最终值。在这种情况下，我们将其作为对 POST 的响应返回。

让我们更详细地了解这段代码。在这里，Jackson.fromJson使用ObjectMapper User解析请求主体的 JSON 。然后，内置的Context。parse方法将其绑定到Promise对象。

承诺异步运行。当最终执行then操作时，返回响应：

```bash
curl -X POST -H 'Content-type: application/json' --data 
'{"id":3,"title":"Mrs","name":"Jiney Weiber","country":"UK"}' 
http://localhost:5050/employee

Jiney Weiber
```

我们应该注意到 Promise 库非常丰富，允许我们使用map和flatMap等函数链接操作。

## 7. 与数据库集成

当我们的处理程序必须等待服务时，拥有异步处理程序是最有好处的。让我们通过将 Ratpack 应用程序与 H2 数据库集成来演示这一点。

我们可以使用 Ratpack 的HikariModule类，它是[HikariCP](https://www.baeldung.com/hikaricp) JDBC 连接池的扩展，也可以使用 Groovy [S ql](http://docs.groovy-lang.org/latest/html/api/groovy/sql/Sql.html)进行数据库集成。

### 7.1. Hikari模块

要添加 HikariCP 支持，让我们首先在我们的pom.xml中添加以下[Hikari](https://search.maven.org/search?q=g:io.ratpack AND a:ratpack-hikari&core=gav)和[H2](https://search.maven.org/search?q=g:com.h2database AND a:h2&core=gav) maven 依赖项：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-hikari</artifactId>
    <version>${ratpack.version}</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>${h2.version}</version>
</dependency>
```

或者，我们可以将以下依赖项添加到我们的build.gradle中：

```shell
dependencies {
  compile ratpack.dependency('hikari')
  compile "com.h2database:h2:$h2.version"
}
```

现在，我们将在连接池的绑定闭包下声明HikariModule ：

```groovy
import ratpack.hikari.HikariModule

ratpack {
    bindings {
        module(HikariModule) { config ->
            config.dataSourceClassName = 'org.h2.jdbcx.JdbcDataSource'
            config.addDataSourceProperty('URL', 
              "jdbc:h2:mem:devDB;INIT=RUNSCRIPT FROM 'classpath:/User.sql'")
        }
    }
}

```

最后，我们都准备好使用它来使用Java的Connection和PreparedStatement进行简单的数据库操作：

```groovy
get('fetchUserName/:id') { Context ctx ->
    Connection connection = ctx.get(DataSource.class).getConnection()
    PreparedStatement queryStatement = 
      connection.prepareStatement("SELECT NAME FROM USER WHERE ID=?")
    queryStatement.setInt(1, Integer.parseInt(ctx.getPathTokens().get("id")))
    ResultSet resultSet = queryStatement.executeQuery()
    resultSet.next()
    render resultSet.getString(1)  
}

```

让我们检查处理程序是否按预期工作：

```bash
$ curl -s localhost:5050/fetchUserName/1
Norman Potter
```

### 7.2. Groovy SQL类 

我们可以通过rows和executeInsert等方法使用 Groovy Sql进行快速数据库操作：

```groovy
get('fetchUsers') {
    def db = [url:'jdbc:h2:mem:devDB']
    def sql = Sql.newInstance(db.url, db.user, db.password)
    def users = sql.rows("SELECT  FROM USER");
    render(Jackson.json(users))
}

$ curl -s localhost:5050/fetchUsers
[{"ID":1,"TITLE":"Mr","NAME":"Norman Potter","COUNTRY":"USA"},
{"ID":2,"TITLE":"Miss","NAME":"Ketty Smith","COUNTRY":"FRANCE"}]
```

让我们用Sql编写一个 HTTP POST 示例：

```groovy
post('addUser') {
    parse(Jackson.fromJson(User))
        .then { u ->
            def db = [url:'jdbc:h2:mem:devDB']
            Sql sql = Sql.newInstance(db.url, db.user, db.password)
            sql.executeInsert("INSERT INTO USER VALUES (?,?,?,?)", 
              [u.id, u.title, u.name, u.country])
            render "User $u.name inserted"
        }
}
$ curl -X POST -H 'Content-type: application/json' --data 
'{"id":3,"title":"Mrs","name":"Jiney Weiber","country":"UK"}' 
http://localhost:5050/addUser

User Jiney Weiber inserted
```

## 8. 单元测试

### 8.1. 设置测试

如前所述，Ratpack 还提供[了](https://search.maven.org/search?q=g:io.ratpack AND a:ratpack-groovy-test&core=gav)用于测试ratpack-groovy应用程序的[ratpack ](https://search.maven.org/search?q=g:io.ratpack AND a:ratpack-groovy-test&core=gav)[-groovy-test](https://search.maven.org/search?q=g:io.ratpack AND a:ratpack-groovy-test&core=gav)[库。](https://search.maven.org/search?q=g:io.ratpack AND a:ratpack-groovy-test&core=gav)

要使用它，我们可以将它作为 Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-groovy-test</artifactId>
    <version>1.6.1</version>
</dependency>
```

或者，我们可以在build.gradle中添加 Gradle 依赖项：

```shell
testCompile ratpack.dependency('groovy-test')
```

然后我们需要创建一个 Groovy 主类RatpackGroovyApp.groovy让我们测试Ratpack.groovy脚本。

```groovy
public class RatpackGroovyApp {
    public static void main(String[] args) {
        File file = new File("src/main/groovy/com/baeldung/Ratpack.groovy");
        def shell = new GroovyShell()  
        shell.evaluate(file)
    }
}
```

当将 Groovy 测试作为 JUnit 测试运行时，该类将使用GroovyShell调用Ratpack.groovy脚本。反过来，它将启动 Ratpack 服务器进行测试。

现在，让我们编写我们的 Groovy 测试类RatpackGroovySpec.groovy以及通过 RatpackGroovyApp 启动 Ratpack 服务器的代码：

```groovy
class RatpackGroovySpec {
    ServerBackedApplicationUnderTest ratpackGroovyApp = 
      new MainClassApplicationUnderTest(RatpackGroovyApp.class)
    @Delegate TestHttpClient client = 
      TestHttpClient.testHttpClient(ratpackGroovyApp)
}
```

Ratpack 提供MainClassApplicationUnderTest来模拟启动服务器的应用程序类。

### 8.2. 编写我们的测试

让我们编写我们的测试，从一个非常基本的测试开始，以检查应用程序是否可以启动：

```groovy
@Test
void "test if app is started"() {
    when:
    get("")

    then:
    assert response.statusCode == 200
    assert response.body.text == 
      "Hello World from Ratpack with Groovy!!"
}
```

现在让我们编写另一个测试来验证fetchUsers get 处理程序的响应：

```groovy
@Test
void "test fetchUsers"() {
    when:
    get("fetchUsers")
        
    then:
    assert response.statusCode == 200
    assert response.body.text == 
      '[{"ID":1,"TITLE":"Mr","NAME":"Norman Potter","COUNTRY":"USA"},{"ID":2,"TITLE":"Miss","NAME":"Ketty Smith","COUNTRY":"FRANCE"}]'
}
```

Ratpack 测试框架负责为我们启动和停止服务器。

## 9.总结

在本文中，我们了解了几种使用 Groovy 为 Ratpack 编写 HTTP 处理程序的方法。我们还探讨了 Promises 和数据库集成。

我们已经了解了 Groovy 闭包、DSL 和 Groovy 的Sql如何 使我们的代码简洁、高效和可读。同时，Groovy 的测试支持使单元和集成测试变得简单明了。

通过这些技术，我们可以使用 Groovy 的动态语言特性和富有表现力的 API，通过 Ratpack 快速开发高性能、可扩展的 HTTP 应用程序。
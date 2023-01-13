## 一、简介

Kovert 是一个自以为是的 REST API 框架，因此非常容易上手。它利用了[Vert.x](https://www.baeldung.com/vertx)的强大功能，但使得一致地开发应用程序变得更加容易。

我们可以从头开始编写 Kovert API，或者在现有的 Vert.x 应用程序中使用 Kovert 控制器。该库旨在与我们希望使用的方式一起使用。

## 2.Maven依赖

Kovert 是一个标准的 Kotlin 库，在[Maven Central](https://search.maven.org/search?q=g:uy.kohesive.kovert AND a:kovert-vertx)上可用：

```xml
<dependency>
    <groupId>uy.kohesive.kovert</groupId>
    <artifactId>kovert-vertx</artifactId>
    <version>1.5.0</version>
</dependency>
```

## 3. 启动 Kovert 服务器

Kovert 大量使用[Kodein](https://www.baeldung.com/kotlin-kodein-dependency-injection) 来连接我们的应用程序。这包括加载 Kovert 和 Vert.x 的配置以及使一切正常工作所需的所有模块。

我们可以用相对较少的代码启动一个简单的 Kovert 服务器。

让我们看一下 Kovert 服务器的示例配置文件：

```javascript
{
   kovert: {
       vertx: {
           clustered: false
       }
       server: {
           listeners: [
               {
                    host: "0.0.0.0"
                    port: "8000"
               }
           ]
       }
   }
}
```

然后我们可以启动一个构建和运行 Kovert 服务器的 Kodein 实例：

```groovy
fun main(args: Array<String>) {
    NoopServer.start()
}

class NoopServer {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(NoopServer::class.java)
    }

    fun start() {
        Kodein.global.addImport(Kodein.Module {
            val config =ClassResourceConfig("/kovert.conf", NoopServer::class.java)
            importConfig(loadConfig(config, ReferenceConfig())) {
                import("kovert.vertx", KodeinKovertVertx.configModule)
                import("kovert.server", KovertVerticleModule.configModule)
            }

            import(KodeinVertx.moduleWithLoggingToSlf4j)
            import(KodeinKovertVertx.module)
            import(KovertVerticleModule.module)
        })

        val initControllers = fun Router.() { }

        KovertVertx.start() bind { vertx ->
            KovertVerticle.deploy(vertx, routerInit = initControllers)
        } success { deploymentId ->
            LOG.warn("Deployment complete.")
        } fail { error ->
            LOG.error("Deployment failed!", error)
        }
    }
}
```

这将加载我们的配置，然后启动一个按配置运行的 Web 服务器。

此时，网络服务器没有控制器。

## 4. 简单控制器

Kovert 最强大的方面之一是我们编写控制器的方式。我们无需指示系统如何将 HTTP 请求分配给代码。相反，这是由基于方法名称的约定驱动的。

我们将我们的控制器编写为简单的类，并使用以特定模式命名的方法。我们的方法也需要写成RoutingContext类的扩展方法：

```groovy
class SimpleController {
    fun RoutingContext.getStringById(id: String) = id
}
```

这定义了绑定到GET /string/:id的单个控制器方法。提供一个值作为路径参数，此控制器按原样返回它。

然后我们可以将它们插入到 initControllers 闭包中的 Kovert 路由器中 ，然后它们都可以从正在运行的服务器获得：

```groovy
val initControllers = fun Router.() {
    bindController(SimpleController(), "api")
}
```

这会将我们的控制器中的方法安装在 /api下——因此我们的 getStringById()方法实际上可以在 /api/string/:id上使用。可以在同一路径下安装的控制器数量没有限制，只要生成的 URL 没有冲突即可。

### 4.1. 方法命名约定

Kovert 应用程序详细记录了如何使用方法名称生成 URL 的规则。

简而言之：

-   第一个词用作 HTTP 方法名称——get、post、put、delete 等。这些的别名也是可能的，因此可以使用“remove”代替“delete”。
-   单词“By”和“In”用于指示下一个单词是路径参数。例如， ById变为 /:id。
-   “With”一词用于表示下一个词既是路径参数又是路径的一部分。例如， WithId变为 /id/:id。

所有其他词都用作路径段，分成单独的词，每个词都是不同的路径。如果我们需要改变这一点，我们可以使用下划线来分隔单词，而不是让 Kovert 自动计算出来。

例如：

```groovy
fun getSomethingSimple()       // GET /something/simple
fun get_something_elseSimple() // GET /something/elseSimple
```

请注意，使用下划线分隔路径段时，所有段都应以小写字母开头。这包括特殊词“By”、“In”和“With”。

如果不是，那么 Kovert 会将它们视为路径段。

例如：

```kotlin
fun getTruncatedStringById()    // GET /truncated/string/:id
fun get_TruncatedString_By_Id() // GET /TruncatedString/By/Id
fun get_truncatedString_by_id() // GET /truncatedString/:id
fun get_truncatedString_by_Id() // GET /truncatedString/:Id
```

### 4.2. JSON 响应

默认情况下，Kovert 将为从我们的控制器返回的任何 beans 返回 JSON 响应：

```groovy
data class Person(
    val id: String,
    val name: String,
    val job: String
)

class JsonController {
    fun RoutingContext.getPersonById(id: String) = Person(
        id = id,
        name = "Tony Stark",
        job = "Iron Man"
    )
}
```

这定义了一个控制器来处理 /person/:id。如果我们随后请求 /person/abc，我们将得到一个 JSON 响应：

```javascript
{
    "id": "abc",
    "name": "Tony Stark",
    "job": "Iron Man"
}
```

Kovert 使用 Jackson 来转换我们的响应，因此我们可以在需要时使用所有支持的注解来管理它：

```groovy
data class Person(
    @JsonProperty("_id")
    val id: String,
    val name: String,
    val job: String
)
```

这现在将返回以下内容：

```javascript
{
    "_id": "abc",
    "name": "Tony Stark",
    "job": "Iron Man"
}
```

### 4.3. 错误响应

有时我们需要向客户端返回一个错误，表明我们无法继续。HTTP 有很多不同的错误，我们可以出于不同的原因返回这些错误，而Kovert 有一个简单的机制来处理这些。

要触发此机制，我们只需要从我们的控制器方法中抛出一个适当的异常。Kovert 为每个受支持的 HTTP 状态代码定义一个异常，并在抛出这些代码时自动执行正确的操作：

```groovy
fun RoutingContext.getForbidden() {
    throw HttpErrorForbidden() // Returns an HTTP 403
}
```

有时我们还需要更多地控制发生的事情，因此 Kovert 定义了两个我们可以使用的额外异常 ——HttpErrorCode和 HttpErrorCodeWithBody。

与更通用的不同，这些也会导致异常输出到服务器日志，并允许我们以编程方式确定状态代码——包括标准不支持的状态代码——和响应主体：

```groovy
fun RoutingContext.getError() {
    throw HttpErrorCode("Something went wrong", 590)
}
fun RoutingContext.getErrorbody() {
    throw HttpErrorCodeWithBody("Something went wrong", 591, "Body here")
}
```

与往常一样，我们可以在正文中使用任何丰富的对象，这将自动转换为 JSON。

## 5.高级控制器绑定

虽然大多数时候我们可以使用已经涵盖的简单控制器支持，但有时我们需要更多支持才能使我们的应用程序完全按照我们的要求进行操作。Kovert 为我们提供了灵活处理很多事情的能力，使我们能够构建我们想要的应用程序。

### 5.1. 查询字符串参数

有时我们还需要将不属于请求路径一部分的其他参数传递给我们的控制器。通过简单地向我们的方法添加额外的参数，我们将获得作为查询字符串参数传递的任何值：

```groovy
fun RoutingContext.get_truncatedString_by_id(id: String, length: Int = 1) = 
    id.subSequence(0, length)
```

我们还可以为这些参数指定默认值，以便可以选择性地在 URL 上提供它们。

例如，以上将做：

-   /truncatedString/abc => “a”
-   /truncatedString/abc?length=2 => “ab”

### 5.2. JSON 请求体

通常我们也希望能够将结构化数据发送到我们的服务器。如果请求主体是 JSON并且控制器具有适当的丰富类型参数，Kovert 将自动为我们处理此问题。

例如，我们可以发送一个新的 Person到我们的服务器：

```groovy
fun RoutingContext.putPersonById(id: String, person: Person) = person
```

这将为 PUT /person/:id创建一个新的处理程序， 它接受符合 Person bean 的 JSON 请求主体。然后，它会自动提供给我们，以便我们根据需要使用。

### 5.3. 自定义动词别名

有时，我们可能希望能够自定义 Kovert 将我们的方法名称与请求 URL 匹配的方式。特别是，我们可能对可用的默认 HTTP 动词别名集不满意。

Kovert 为我们提供了一种使用KovertConfig.addVerbAlias调用轻松管理它的方法 。这允许我们为任何 HTTP 方法注册我们喜欢的任何词，如果我们愿意，包括替换现有的：

```groovy
KovertConfig.addVerbAlias("submit", HttpVerb.POST)
```

这将允许我们编写一个名为 submitPerson()的方法名称， 它会自动映射到POST /person。

### 5.4. 注解方法

有时我们可能需要更进一步自定义我们的控制器方法。在这种情况下，Kovert 提供了注解，我们可以在我们的方法上使用这些注解来完全控制映射。

在各个方法级别，我们可以使用 @Verb和 @Location注解指定匹配的确切 HTTP 动词和 URL。例如，以下将响应“GET /ping/:id”：

```groovy
@Verb(HttpVerb.GET)
@Location("/ping/:id")
fun RoutingContext.ping(id: String) = id
```

或者，我们可以使用@VerbAlias和 @VerbAliases方法覆盖单个类中所有方法的动词别名，而不是整个应用程序中的 方法。例如，以下将响应“GET /string/:id”：

```kotlin
@VerbAlias("show", HttpVerb.GET)
class AnnotatedController {
    fun RoutingContext.showStringById(id: String) = id
}
```

## 6.异步响应

到目前为止，我们所有的控制器方法都是同步的。

这对于简单的情况很好，但是因为 Vert.x 运行单个 I/O 线程，如果我们的任何控制器方法需要等待执行某些操作，这可能会导致问题——例如，如果我们正在调用数据库，那么我们不想阻止整个应用程序的其余部分。

Kovert 旨在与[Kovenant](https://www.baeldung.com/kotlin-kovenant)库一起工作以支持异步处理。

我们需要做的就是返回一个 Promise<Result, Exception> ——其中 Result是处理程序的返回类型——然后我们得到异步处理：

```groovy
fun RoutingContext.getPersonById(id: String): Promise<Person, Exception> {
    task {
        return personService.getById(id) ?: throw HttpErrorNotFound()
    }
}
```

这将启动一个后台线程，我们可以在其中调用 personService来加载我们想要的 Person 详细信息。如果我们找到一个，我们会按原样返回它，Kovert 会为我们将它转换为 JSON。

如果我们没有找到，我们将抛出一个 HttpErrorNotFound导致返回一个 HTTP 404。

## 7.路由上下文

到目前为止，我们已经使用默认的 RoutingContext作为基础编写了所有控制器。这可行，但不是唯一的选择。

我们也可以使用我们自己的任何自定义类，只要它具有采用 RoutingContext的单参数构造函数即可。 这个类就是控制器方法的上下文——即这个的值 ——并且可以为调用做任何必要的事情：

```groovy
class SecuredContext(private val routingContext: RoutingContext) {
    val authenticated = routingContext.request().getHeader("Authorization") == "Secure"
}

class SecuredController {
    fun SecuredContext.getSecured() = this.authenticated
}
```

 这提供了一个上下文，允许我们通过检查“授权”标头是否具有值“安全”来确定调用是否安全。这可以让我们抽象出更多的 HTTP 细节，以便我们的控制器类只处理简单的方法调用，而不用关心它们的实现。

在这种情况下，我们确定请求安全的方式是使用 HTTP 标头。我们可以轻松地使用查询字符串参数或会话值，而控制器代码并不关心。

每个控制器方法都单独声明它要使用哪个路由上下文类。它们可以完全相同，也可以各不相同，Kovert 会做正确的事情。

## 八、总结

在本文中，我们介绍了 Kovert 在 Kotlin 中编写简单的 REST API。

使用 Kovert 可以实现的远不止这里显示的那样。希望这能让我们开始简单的 REST API 之旅。
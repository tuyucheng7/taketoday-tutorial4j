## 1. 概述

在本教程中，我们将了解[Fuel HTTP 库](https://github.com/kittinunf/Fuel)，用作者的话来说，它是 Kotlin/Android 最简单的 HTTP 网络库。此外，该库还可以在 Java 中使用。

图书馆的主要特点包括：

-   支持异步和阻塞请求的基本 HTTP 动词(GET、POST、DELETE 等)
-   下载和上传文件的能力(multipart/form-data)
-   管理全局配置的可能性
-   内置对象序列化模块(Jackson、Gson、Mhosi、Forge)
-   支持 Kotlin 的协程模块和 RxJava 2.x
-   轻松设置路由器设计模式

## 2.依赖关系

该库由不同的模块组成，因此我们可以轻松地包含我们需要的功能。其中一些包括：

-   用于 RxJava 和 Kotlin 协程支持的模块
-   Android 和 Android LiveData Architecture 组件支持模块
-   我们可以从中选择要使用的对象序列化模块的四个模块——Gson、Jackson、Moshi 或 Forge。

在本教程中，我们将重点关注核心模块、Coroutines 模块、RxJava 和 Gson 序列化模块：

```xml
<dependency>
    <groupId>com.github.kittinunf.fuel</groupId>
    <artifactId>fuel</artifactId>
    <version>${fuel.version}</version>
</dependency>
<dependency>
    <groupId>com.github.kittinunf.fuel</groupId>
    <artifactId>fuel-gson</artifactId>
    <version>${fuel.version}</version>
</dependency>
<dependency>
    <groupId>com.github.kittinunf.fuel</groupId>
    <artifactId>fuel-rxjava</artifactId>
    <version>${fuel.version}</version>
</dependency>
<dependency>
    <groupId>com.github.kittinunf.fuel</groupId>
    <artifactId>fuel-coroutines</artifactId>
    <version>${fuel.version}</version>
</dependency>
```

你可以在[JFrog Bintray](https://bintray.com/kittinunf/maven/Fuel-Android)上找到最新版本。

## 3.提出要求

为了发出请求，Fuel 提供了一个 字符串扩展。此外，作为替代方案，我们可以使用 Fuel类，该类对每个 HTTP 谓词都有一个方法。

Fuel 支持除 PATCH 之外的所有 HTTP 动词。原因是 Fuel 的HttpClient是java.net.HttpUrlConnection的包装器 ，不支持 PATCH。

为了解决这个问题，HttpClient 将 PATCH 请求转换为 POST 请求并添加 X-HTTP-Method-Override: PATCH标头，因此我们需要确保我们的 API 配置为默认接受此标头。

为了解释 Fuel 的功能，我们将使用[httpbin.org](https://httpbin.org/#/)，一个简单的 HTTP 请求和响应服务，以及[JsonPlaceholder——](https://jsonplaceholder.typicode.com/) 一个用于测试和原型制作的假在线 API。

### 3.1. 获取请求

让我们开始在异步模式下创建简单的 HTTP GET请求：

```java
"http://httpbin.org/get".httpGet().response {
  request, response, result ->
    //response handling
}
```

在String上使用 httpGet()给了我们一个 Triple<Request, Response, Result>。

[Result](https://github.com/kittinunf/Result)是一个函数式数据结构，包含操作的结果(成功或失败) 。我们将在稍后阶段重新访问结果数据结构。

我们也可以在阻塞模式下发出请求：

```java
val (request, response, result) = "http://httpbin.org/get"
  .httpGet().response()
```

请注意，返回的参数与异步版本相同，但在这种情况下，执行请求的线程被阻塞。

此外，还可以使用编码的 URL 参数：

```java
val (request, response, result) = 
  "https://jsonplaceholder.typicode.com/posts"
  .httpGet(listOf("userId" to "1")).response() 
  // resolve to https://jsonplaceholder.typicode.com/posts?userId=1

```

httpGet () 方法(以及其他类似的方法)可以接收一个List<Pair>来编码 URL 参数。

### 3.2. POST请求

我们可以使用与 GET 相同的方式发出 POST 请求，使用httpPost() 或使用 Fuel类的post()方法：

```java
"http://httpbin.org/post".httpPost().response{
  request, response, result ->
    //response handling
}
val (request, response, result) = Fuel.post("http://httpbin.org/post")
  .response()

```

如果我们有一个主体，我们可以通过 body()方法将其以 JSON 字符串格式放入：

```java
val bodyJson = """
  { "title" : "foo",
    "body" : "bar",
    "id" : "1"
  }
"""
val (request, response, result) = Fuel.post("https://jsonplaceholder.typicode.com/posts")
  .body(bodyJson)
  .response()
```

### 3.3. 其他动词

与 GET 和 POST 相同，其余每个动词都有一个方法：

```java
Fuel.put("http://httpbin.org/put")
Fuel.delete("http://httpbin.org/delete")
Fuel.head("http://httpbin.org/get")
Fuel.patch("http://httpbin.org/patch")
```

请记住，Fuel.patch()将执行带有X-HTTP-Method-Override: PATCH标头的 POST 请求。

## 4.配置

该库提供了一个单例对象——FuelManager.instance——来管理全局配置。

让我们配置一个基本路径、一些标头和公共参数。另外，让我们配置一些拦截器。

### 4.1. 基本路径

使用 basePath 变量，我们可以为所有请求设置一个公共路径。

```java
FuelManager.instance.basePath = "http://httpbin.org"
val (request, response, result) = "/get".httpGet().response()
// will perform GET http://httpbin.org/get
```

### 4.2. 标头

此外，我们可以使用baseHeaders映射管理常见的 HTTP 标头：

```java
FuelManager.instance.baseHeaders = mapOf("OS" to "Debian")
```

另一种方法是，如果我们想设置一个本地标头，我们可以在请求中使用header()方法：

```java
val (request, response, result) = "/get"
  .httpGet()
  .header(mapOf("OS" to "Debian"))
  .response()
```

### 4.3. 参数

最后，我们还可以使用 baseParams列表设置常用参数：

```java
FuelManager.instance.baseParams = listOf("foo" to "bar")
```

### 4.4. 其他选项

我们可以通过 FuelManager 管理更多选项：

-   默认情况下为 null的密钥库 
-   将由用户提供或从 密钥库派生的socketFactory (如果它不为 空)
-   默认设置为使用 HttpsURLConnection类提供的hostnameVerifier
-   请求拦截器和 响应拦截器
-   请求的超时和 超时读取

### 4.5. 请求/响应拦截器

关于拦截器，我们可以添加提供的请求/响应拦截器，如 cUrlLoggingRequestInterceptors()，或者我们可以定义我们的拦截器：

```java
FuelManager.instance.addRequestInterceptor(cUrlLoggingRequestInterceptor())

FuelManager.instance.addRequestInterceptor(tokenInterceptor())
fun tokenInterceptor() = {
    next: (Request) -> Request ->
    { req: Request ->
        req.header(mapOf("Authorization" to "Bearer AbCdEf123456"))
        next(req)
    }
}
```

## 5.响应处理

之前，我们引入了一个函数式数据结构 ——Result——代表操作结果(成功或失败)。

使用 Result很容易，它是一个数据类，可以包含 ByteArray、String、JSON 或通用 T对象中的响应：

```java
fun response(handler: (Request, Response, Result<ByteArray, FuelError>) -> Unit)
fun responseString(handler: (Request, Response, Result<String, FuelError>) -> Unit)
fun responseJson(handler: (Request, Response, Result<Json, FuelError>) -> Unit)
fun <T> responseObject(deserializer: ResponseDeserializable<T>, 
  handler: (Request, Response, Result<T, FuelError>) -> Unit)

```

让我们得到一个字符串形式的响应来说明这一点：

```java
val (request, response, result) = Fuel.post("http://httpbin.org/post")
  .responseString()
val (payload, error) = result // payload is a String
```

请注意，JSON 格式的响应需要 Android 依赖项。

```xml
<dependency>
    <groupId>com.github.kittinunf.fuel</groupId>
    <artifactId>fuel-android</artifactId>
    <version>${fuel.version}</version>
</dependency>
```

## 6. JSON序列化/反序列化

Fuel 通过四种方法为响应反序列化提供内置支持，根据我们的需要和我们选择的 JSON 解析库，我们需要实现这些方法：

```java
public fun deserialize(bytes: ByteArray): T?
public fun deserialize(inputStream: InputStream): T?
public fun deserialize(reader: Reader): T?
public fun deserialize(content: String): T?
```

通过包含 Gson 模块，我们可以反序列化和序列化对象：

```java
data class Post(var userId:Int,
                var id:Int,
                var title:String,
                var body:String){

    class Deserializer : ResponseDeserializable<Array<Post>> {
        override fun deserialize(content: String): Array<Post> 
          = Gson().fromJson(content, Array<Post>::class.java)
    }
}
```

我们可以使用自定义反序列化器反序列化对象：

```java
"https://jsonplaceholder.typicode.com/posts"
  .httpGet().responseObject(Post.Deserializer()){ 
    _,_, result ->
      val postsArray = result.component1()
  }
```

或者通过使用内部 Gson 反序列化器的 responseObject<T>：

```java
"https://jsonplaceholder.typicode.com/posts/1"
  .httpGet().responseObject<Post> { _, _, result ->
    val post = result.component1()
  }
```

另一方面，我们可以使用 Gson().toJson()进行序列化：

```java
val post = Post(1, 1, "Lorem", "Lorem Ipse dolor sit amet")

val (request, response, result) 
  = Fuel.post("https://jsonplaceholder.typicode.com/posts")
    .header("Content-Type" to "application/json")
    .body(Gson().toJson(post).toString())
```

设置 Content-Type很重要，否则服务器可能会在另一个 JSON 对象中接收该对象。

最终，以类似的方式，我们可以通过使用 Jackson、Moshi 或 Forge 依赖项来完成。

## 7.下载和上传文件

Fuel 库包含下载和上传文件的所有必要功能。

### 7.1. 下载

使用download()方法，我们可以轻松下载文件并将其保存到 destination() lambda返回的文件中：

```java
Fuel.download("http://httpbin.org/bytes/32768")
  .destination { response, url -> 
    File.createTempFile("temp", ".tmp")
  }
```

我们还可以下载带有进度处理程序的文件：

```java
Fuel.download("http://httpbin.org/bytes/327680")
  .progress { readBytes, totalBytes ->
    val progress = readBytes.toFloat() / totalBytes.toFloat() 
    //...
  }
```

### 7.2. 上传

同理，我们可以使用 upload()方法上传文件，用source()方法指明要上传的文件 ：

```java
Fuel.upload("/upload").source { request, url ->
  File.createTempFile("temp", ".tmp") 
}
```

请注意， upload()默认使用 POST 动词。如果我们想使用另一个 HTTP 动词，我们可以指定它：

```java
Fuel.upload("/upload", Method.PUT).source { request, url ->
  File.createTempFile("temp", ".tmp") 
}
```

此外，我们可以使用接受文件列表的sources()方法上传多个 文件：

```java
Fuel.upload("/post").sources { request, url ->
  listOf(
    File.createTempFile("temp1", ".tmp"),
    File.createTempFile("temp2", ".tmp")
  )
}
```

最后，我们可以从InputStream上传一个数据块 ：

```java
Fuel.upload("/post").blob { request, url ->
  Blob("filename.png", someObject.length, { someObject.getInputStream() })
}
```

## 8. RxJava 和协程支持

Fuel 提供对 RxJava 和 Coroutines 的支持，这两种编写异步、非阻塞代码的方式。

[RxJava](https://www.baeldung.com/rx-java)是[Reactive Extensions](http://reactivex.io/)的 Java VM 实现，它是一个用于编写异步和基于事件的程序的库。

它扩展了[观察者模式](https://www.baeldung.com/java-observer-pattern) 以支持数据/事件序列，并添加了允许以声明方式将序列组合在一起的运算符，而无需担心同步、线程安全和并发数据结构。

[Kotlin 的 Coroutines](https://www.baeldung.com/kotlin-coroutines)就像轻量级的线程，因此它们可以并行运行，相互等待并进行通信……最大的区别是协程非常便宜；我们可以创建数千个，并且在内存方面付出的代价很少。

### 8.1. RxJava

为了支持 RxJava 2.x，Fuel 提供了六个扩展：

```java
fun Request.rx_response(): Single<Pair<Response, Result<ByteArray, FuelError>>>
fun Request.rx_responseString(charset: Charset): Single<Pair<Response, Result<String, FuelError>>>
fun <T : Any> Request.rx_responseObject(deserializable: Deserializable<T>): 
  Single<Pair<Response, Result<T, FuelError>>>
fun Request.rx_data(): Single<Result<ByteArray, FuelError>>
fun Request.rx_string(charset: Charset): Single<Result<String, FuelError>>
fun <T : Any> Request.rx_object(deserializable: Deserializable<T>): Single<Result<T, FuelError>>
```

请注意，为了支持所有不同的响应类型，每个方法都返回不同的 Single<Result<>>。

我们可以通过Request调用更相关的方法来轻松使用“Rx”方法：

```java
 "https://jsonplaceholder.typicode.com/posts?id=1"
  .httpGet().rx_object(Post.Deserializer()).subscribe{
    res, throwable ->
      val post = res.component1()
  }
```

### 8.2. 协程

通过协程模块，Fuel 提供扩展函数来将响应包装在协程中并处理其结果。

要使用协程，可以使用类似的 API，例如 responseString()变成 了 awaitStringResponse()：

```java
runBlocking {
    Fuel.get("http://httpbin.org/get").awaitStringResponse()
}
```

它还提供了 使用 awaitObject()、awaitObjectResult() 或 awaitObjectResponse()处理String或 ByteArray ( awaitByteArrayResponse())以外的对象的有用方法：

```java
runBlocking {
    Fuel.get("https://jsonplaceholder.typicode.com/posts?id=1")
      .awaitObjectResult(Post.Deserializer())
}
```

请记住，Kotlin 的协程是实验性的，这意味着它可能会在即将发布的版本中进行更改。

## 9. API路由

最后但同样重要的是，为了处理网络路由，Fuel 通过实现路由器设计模式来提供支持。

通过路由器模式，我们可以使用FuelRouting接口集中管理 API，该接口提供了一组方法，用于根据调用的端点设置适当的 HTTP 谓词、路径、参数和标头。

该接口定义了五个属性，可以通过它们配置我们的路由器：

```java
sealed class PostRoutingAPI : FuelRouting {
    class posts(val userId: String, override val body: String?): PostRoutingAPI() 
    class comments(val postId: String, override val body: String?): PostRoutingAPI()
    override val basePath = "https://jsonplaceholder.typicode.com"

    override val method: Method
        get() {
            return when(this) {
                is PostRoutingAPI.posts -> Method.GET
                is PostRoutingAPI.comments -> Method.GET
            }
        }

    override val path: String
        get() {
            return when(this) {
                is PostRoutingAPI.posts -> "/posts"
                is PostRoutingAPI.comments -> "/comments"
            }
        }

    override val params: List<Pair<String, Any?>>?
        get() {
            return when(this) {
                is PostRoutingAPI.posts -> listOf("userId" to this.userId)
                is PostRoutingAPI.comments -> listOf("postId" to this.postId)
            }
        }

    override val headers: Map<String, String>?
        get() {
            return null
        }
}
```

为了选择使用哪个 HTTP 动词，我们有 method属性，同样，我们可以覆盖 path属性，以便选择合适的路径。

更重要的是使用 params属性，我们有机会设置请求的参数，如果我们需要设置 HTTP 标头，我们可以覆盖相关属性。

因此，我们使用它的方式与整个教程中使用 request()方法的方式相同：

```java
Fuel.request(PostRoutingAPI.posts("1",null))
  .responseObject(Post.Deserializer()) {
      request, response, result ->
        //response handling
  }
Fuel.request(PostRoutingAPI.comments("1",null))
  .responseString { request, response, result -> 
      //response handling 
  }
```

## 10.总结

在本文中，我们展示了 Kotlin 的 Fuel HTTP 库及其适用于任何用例的更有用的功能。

该库在不断发展，因此，请查看他们的[GitHub](https://github.com/Baeldung/kotlin-tutorials/tree/master/kotlin-libraries-2) 存储库以跟踪新功能。
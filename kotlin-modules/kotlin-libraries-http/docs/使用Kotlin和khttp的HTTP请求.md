## 一、简介

HTTP 协议和基于它构建的 API 在当今的编程中至关重要。

在 JVM 上，我们有几个可用的选项，从较低级别的库到非常高级的库，从已建立的项目到新项目。但是，它们中的大多数主要针对 Java 程序。

在本文中，我们将了解[khttp](https://khttp.readthedocs.io/en/latest/)，这是一个惯用的 Kotlin 库，用于使用基于 HTTP 的资源和 API。

## 2.依赖关系

为了在我们的项目中使用这个库，首先，我们必须将它添加到我们的依赖项中：

```xml
<dependency>
    <groupId>khttp</groupId>
    <artifactId>khttp</artifactId>
    <version>0.1.0</version>
</dependency>
```

由于 Maven Central 还没有，我们还必须启用 JCenter 存储库：

```xml
<repository>
    <id>central</id>
    <url>http://jcenter.bintray.com</url>
</repository>
```

版本 0.1.0 是撰写本文时的最新版本。当然，我们可以[检查 JCenter 是否有更新的](https://bintray.com/jkcclemens/maven/khttp)。

## 3. 基本用法

HTTP 协议的基础很简单，尽管细节可能相当复杂。因此，khttp 也有一个简单的界面。

对于每个 HTTP 方法，我们都可以在khttp包中找到包级函数，例如get、post等。

这些函数都采用相同的参数集并返回一个Response对象；我们将在以下部分中看到这些的详细信息。

在本文中，我们将使用完全限定形式，例如khttp.put。当然，在我们的项目中，我们可以导入并可能重命名这些方法：

```java
import khttp.delete as httpDelete
```

注意：为了清晰起见，我们在整个代码示例中添加了类型声明，因为如果没有 IDE，它们可能很难理解。

## 4. 一个简单的请求

每个 HTTP 请求至少有两个必需的组件：方法和 URL。在 khttp 中，方法由我们调用的函数决定，正如我们在上一节中看到的那样。

URL 是该方法唯一必需的参数；所以，我们可以很容易地执行一个简单的请求：

```java
khttp.get("http://httpbin.org/get")
```

在以下部分中，我们将考虑所有成功完成的请求。

### 4.1. 添加参数

除了基本 URL 之外，我们通常还必须提供查询参数，尤其是对于 GET 请求。

khttp 的方法接受一个params参数，该参数是要包含在查询字符串中的键值对映射：

```java
khttp.get(
  url = "http://httpbin.org/get",
  params = mapOf("key1" to "value1", "keyn" to "valuen"))
```

请注意，我们已经使用mapOf函数动态构建了一个Map；生成的请求 URL 将是：

```html
http://httpbin.org/get?key1=value1&keyn=valuen
```

## 5.请求体

我们经常需要执行的另一个常见操作是发送数据，通常作为 POST 或 PUT 请求的有效负载。

为此，该库提供了几个选项，我们将在以下部分中对其进行检查。

### 5.1. 发送 JSON 负载

我们可以使用json参数发送 JSON 对象或数组。它可以有几种不同的类型：

-   org.json 库提供的JSONObject或JSONArray
-   一个Map，它被转换成一个 JSON 对象
-   转换为 JSON 数组的Collection、Iterable或数组

我们可以轻松地将之前的 GET 示例转换为 POST 示例，后者将发送一个简单的 JSON 对象：

```java
khttp.post(
  url = "http://httpbin.org/post",
  json = mapOf("key1" to "value1", "keyn" to "valuen"))
```

请注意，从集合到 JSON 对象的转换是浅层的。例如，Map 的列表不会转换为 JSON 对象的 JSON 数组，而是转换为字符串数组。

对于深度转换，我们需要一个更复杂的 JSON 映射库，例如 Jackson。该库的转换工具仅适用于简单的情况。

### 5.2. 发送表单数据(URL 编码)

要发送表单数据(URL 编码，如在 HTML 表单中)，我们将data参数与Map一起使用：

```java
khttp.post(
  url = "http://httpbin.org/post",
  data = mapOf("key1" to "value1", "keyn" to "valuen"))
```

### 5.3. 上传文件(多部分形式)

我们可以发送一个或多个编码为多部分表单数据请求的文件。

在这种情况下，我们使用files参数：

```java
khttp.post(
  url = "http://httpbin.org/post",
  files = listOf(
    FileLike("file1", "content1"),
    FileLike("file2", File("kitty.jpg"))))
```

我们可以看到 khttp 使用了FileLike抽象，它是一个具有名称和内容的对象。内容可以是字符串、字节数组、文件或路径。

### 5.4. 发送原始内容

如果以上选项都不合适，我们可以使用InputStream将原始数据作为 HTTP 请求的主体发送：

```java
khttp.post(url = "http://httpbin.org/post", data = someInputStream)
```

在这种情况下，我们很可能还需要手动设置一些标头，我们将在后面的部分中介绍。

## 6. 处理响应

到目前为止，我们已经看到了向服务器发送数据的各种方式。但是许多 HTTP 操作很有用，因为它们也返回数据。

khttp 基于阻塞 I/O，因此与 HTTP 方法对应的所有函数都返回一个包含从服务器接收到的响应的Response对象。

该对象具有我们可以访问的各种属性，具体取决于内容的类型。

### 6.1. JSON 响应

如果我们知道响应是一个 JSON 对象或数组，我们可以使用jsonObject和jsonArray属性：

```java
val response : Response = khttp.get("http://httpbin.org/get")
val obj : JSONObject = response.jsonObject
print(obj["someProperty"])
```

### 6.2. 文本或二进制响应

如果我们想将响应读取为String，我们可以使用text属性：

```java
val message : String = response.text
```

或者，如果我们想将其作为二进制数据读取(例如文件下载)，我们使用content属性：

```java
val imageData : ByteArray = response.content
```

最后，我们还可以访问底层的InputStream：

```java
val inputStream : InputStream = response.raw
```

## 7.高级用法

我们还来看看一些通常有用的更高级的使用模式，我们在前面的部分中还没有讨论过。

### 7.1. 处理标头和 Cookie

所有 khttp 函数都带有一个headers参数，它是一个头名称和值的映射。

```java
val response = khttp.get(
  url = "http://httpbin.org/get",
  headers = mapOf("header1" to "1", "header2" to "2"))
```

同样对于 cookie：

```java
val response = khttp.get(
  url = "http://httpbin.org/get",
  cookies = mapOf("cookie1" to "1", "cookie2" to "2"))
```

我们还可以在响应中访问服务器发送的标头和 cookie：

```java
val contentType : String = response.headers["Content-Type"]
val sessionID : String = response.cookies["JSESSIONID"]
```

### 7.2. 处理错误

HTTP 中可能出现两种类型的错误：错误响应，例如 404 – Not Found，这是协议的一部分；和低级错误，例如“连接被拒绝”。

第一种不会导致 khttp 抛出异常；相反，我们应该检查Response statusCode属性：

```java
val response = khttp.get(url = "http://httpbin.org/nothing/to/see/here")
if(response.statusCode == 200) {
    process(response)
} else {
    handleError(response)
}
```

相反，较低级别的错误会导致从底层 Java I/O 子系统中抛出异常，例如ConnectException。

### 7.3. 流式响应

有时服务器会响应大量内容，和/或需要很长时间才能响应。在这些情况下，我们可能希望分块处理响应，而不是等待它完成并占用内存。

如果我们想指示库给我们一个流式响应，那么我们必须将true作为流参数传递：

```java
val response = khttp.get(url = "http://httpbin.org", stream = true)
```

然后，我们可以分块处理它：

```java
response.contentIterator(chunkSize = 1024).forEach { arr : ByteArray -> handleChunk(arr) }
```

### 7.4. 非标准方法

在不太可能的情况下，我们需要使用 khttp 本身不提供的 HTTP 方法(或动词)——比如，对于 HTTP 协议的某些扩展，如 WebDAV——我们仍然可以覆盖。

事实上，khttp 包中所有对应 HTTP 方法的函数都是使用我们也可以使用的通用请求函数实现的：

```java
khttp.request(
  method = "COPY",
  url = "http://httpbin.org/get",
  headers = mapOf("Destination" to "/copy-of-get"))
```

### 7.5. 其他特性

我们还没有触及 khttp 的所有特性。例如，我们还没有讨论超时、重定向和历史记录或异步操作。

[官方文档](https://khttp.readthedocs.io/en/latest/)是有关库及其所有功能的最终信息来源。

## 八、总结

在本教程中，我们了解了如何使用惯用库 khttp 在 Kotlin 中发出 HTTP 请求。
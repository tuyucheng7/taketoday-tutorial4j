## 1. 概述

在本文中，我们将了解[用于Java的 Google HTTP 客户端库](https://developers.google.com/api-client-library/java/google-http-java-client/)，这是一个快速、抽象良好的库，用于通过 HTTP 连接协议访问任何资源。

客户端的主要特点是：

-   一个 HTTP 抽象层，可让解耦任何低级库
-   快速、高效、灵活的 HTTP 响应和请求内容的 JSON 和 XML 解析模型
-   易于使用 HTTP 资源映射的注解和抽象

该库还可用于Java5 及更高版本，使其成为遗留(SE 和 EE)项目的重要选择。

在本文中，我们将开发一个简单的应用程序，它将连接到 GitHub API 并检索用户，同时涵盖该库的一些最有趣的功能。

## 2.Maven依赖

要使用该库，我们需要google-http-client依赖项：

```xml
<dependency>
    <groupId>com.google.http-client</groupId>
    <artifactId>google-http-client</artifactId>
    <version>1.23.0</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|google-http-client)找到。

## 3.提出一个简单的请求

让我们首先向 GitHub 页面发出一个简单的 GET 请求，以展示 Google Http 客户端如何开箱即用：

```java
HttpRequestFactory requestFactory
  = new NetHttpTransport().createRequestFactory();
HttpRequest request = requestFactory.buildGetRequest(
  new GenericUrl("https://github.com"));
String rawResponse = request.execute().parseAsString()
```

为了提出最简单的请求，我们至少需要：

-   HttpRequestFactory这用于构建我们的请求
-   HttpTransport低级 HTTP 传输层的抽象
-   GenericUrl包装 Url 的类
-   HttpRequest处理请求的实际执行

在接下来的部分中，我们将通过一个返回 JSON 格式的实际 API 来完成所有这些和一个更复杂的示例。

## 4. 可插拔 HTTP 传输

该库有一个抽象良好的HttpTransport类，允许我们在它之上构建并更改为底层低级 HTTP 传输库的选择：

```java
public class GitHubExample {
    static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
}
```

在此示例中，我们使用的是NetHttpTransport，它基于在所有JavaSDK 中都可以找到的HttpURLConnection 。这是一个很好的开始选择，因为它众所周知且可靠。

当然，可能存在我们需要一些高级定制的情况，因此需要更复杂的低级库。

对于这种情况，有ApacheHttpTransport：

```java
public class GitHubExample {
    static HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
}
```

ApacheHttpTransport基于流行的[Apache HttpClient](https://hc.apache.org/httpcomponents-client-ga/index.html)，它包含多种配置连接的选项。

此外，该库提供了构建低级实现的选项，使其非常灵活。

## 5.JSON解析

Google Http Client 包含另一个用于 JSON 解析的抽象。这样做的一大优点是低级解析库的选择是可以互换的。

有三个内置选项，它们都扩展了 JsonFactory，并且还包括实现我们自己的选项的可能性。

### 5.1. 可互换的解析库

在我们的示例中，我们将使用 Jackson2 实现，它需要[google-http-client-jackson2](https://search.maven.org/classic/#search|ga|1|a%3A"google-http-client-jackson2")依赖项：

```java
<dependency>
    <groupId>com.google.http-client</groupId>
    <artifactId>google-http-client-jackson2</artifactId>
    <version>1.23.0</version>
</dependency>
```

在此之后，我们现在可以包含JsonFactory：

```java
public class GitHubExample {

    static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    staticJsonFactory JSON_FACTORY = new JacksonFactory();
}
```

JacksonFactory是用于解析/序列化操作的最快和最受欢迎的库。

这是以库大小为代价的(在某些情况下这可能是一个问题)。为此，Google 还提供了GsonFactory，它是 Google GSON 库的实现，是一个轻量级的 JSON 解析库。

也有可能编写我们的低级解析器实现。

### 5.2. @键注解

我们可以使用@Key注解来指示需要从 JSON 解析或序列化为 JSON 的字段：

```java
public class User {
 
    @Key
    private String login;
    @Key
    private long id;
    @Key("email")
    private String email;

    // standard getters and setters
}
```

在这里，我们正在做一个用户抽象，我们从 GitHub API 中批量接收它(我们将在本文后面进行实际解析)。

请注意，没有@Key注解的字段被视为内部字段，不会从 JSON 解析或序列化为 JSON。此外，字段的可见性并不重要，getter 或 setter 方法的存在也不重要。

我们可以指定@Key注解的值，将其映射到正确的 JSON 键。

### 5.3. 泛型Json

仅解析我们声明并标记为@Key的字段。

为了保留其他内容，我们可以声明我们的类来扩展GenericJson：

```java
public class User extends GenericJson {
    //...
}
```

GenericJson实现了Map接口，这意味着我们可以使用 get 和 put 方法来设置/获取请求/响应中的 JSON 内容。

## 6. 打电话

要使用 Google Http 客户端连接到端点，我们需要一个HttpRequestFactory，它将使用我们之前的抽象HttpTransport和JsonFactory 进行配置：

```java
public class GitHubExample {

    static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static JsonFactory JSON_FACTORY = new JacksonFactory();

    private static void run() throws Exception {
        HttpRequestFactory requestFactory 
          = HTTP_TRANSPORT.createRequestFactory(
            (HttpRequest request) -> {
              request.setParser(new JsonObjectParser(JSON_FACTORY));
          });
    }
}
```

接下来我们需要的是要连接的 URL。该库将其作为一个扩展GenericUrl的类来处理，在该类上声明的任何字段都被视为查询参数：

```java
public class GitHubUrl extends GenericUrl {

    public GitHubUrl(String encodedUrl) {
        super(encodedUrl);
    }

    @Key
    public int per_page;
 
}
```

在我们的GitHubUrl 中，我们声明了per_page属性以指示我们希望在一次 GitHub API 调用中有多少用户。

让我们继续使用GitHubUrl构建我们的调用：

```java
private static void run() throws Exception {
    HttpRequestFactory requestFactory
      = HTTP_TRANSPORT.createRequestFactory(
        (HttpRequest request) -> {
          request.setParser(new JsonObjectParser(JSON_FACTORY));
        });
    GitHubUrl url = new GitHubUrl("https://api.github.com/users");
    url.per_page = 10;
    HttpRequest request = requestFactory.buildGetRequest(url);
    Type type = new TypeToken<List<User>>() {}.getType();
    List<User> users = (List<User>)request
      .execute()
      .parseAs(type);
}
```

请注意我们如何指定 API 调用需要多少用户，然后我们使用HttpRequestFactory构建请求。

在此之后，由于 GitHub API 的响应包含用户列表，我们需要提供一个复杂的Type，即List<User>。

然后，在最后一行，我们进行调用并将响应解析为我们的User类列表。

## 7.自定义标题

我们在发出 API 请求时通常做的一件事是包含某种自定义标头，甚至是修改后的标头：

```java
HttpHeaders headers = request.getHeaders();
headers.setUserAgent("Baeldung Client");
headers.set("Time-Zone", "Europe/Amsterdam");
```

我们通过在创建请求之后但在执行它并添加必要的值之前获取HttpHeaders来做到这一点。

请注意，Google Http 客户端包含一些标头作为特殊方法。例如，User-Agent标头，如果我们尝试仅将其包含在 set 方法中，则会抛出错误。

## 8.指数退避

Google Http 客户端的另一个重要特性是可以根据某些状态代码和阈值重试请求。

我们可以在创建请求对象后立即包含指数退避设置：

```java
ExponentialBackOff backoff = new ExponentialBackOff.Builder()
  .setInitialIntervalMillis(500)
  .setMaxElapsedTimeMillis(900000)
  .setMaxIntervalMillis(6000)
  .setMultiplier(1.5)
  .setRandomizationFactor(0.5)
  .build();
request.setUnsuccessfulResponseHandler(
  new HttpBackOffUnsuccessfulResponseHandler(backoff));
```

指数退避在HttpRequest中默认关闭，因此我们必须在HttpRequest中包含一个HttpUnsuccessfulResponseHandler实例以激活它。

## 9. 记录

Google Http 客户端使用java.util.logging.Logger来记录 HTTP 请求和响应的详细信息，包括 URL、标头和内容。

通常，使用logging.properties文件管理日志记录：

```plaintext
handlers = java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.level = ALL
com.google.api.client.http.level = ALL
```

在我们的示例中，我们使用ConsoleHandler，但也可以选择FileHandler。

属性文件配置 JDK 日志记录工具的操作。此配置文件可以指定为系统属性：

```plaintext
-Djava.util.logging.config.file=logging.properties
```

因此，在设置文件和系统属性后，库将生成如下日志：

```plaintext
-------------- REQUEST  --------------
GET https://api.github.com/users?page=1&per_page=10
Accept-Encoding: gzip
User-Agent: Google-HTTP-Java-Client/1.23.0 (gzip)

Nov 12, 2017 6:43:15 PM com.google.api.client.http.HttpRequest execute
curl -v --compressed -H 'Accept-Encoding: gzip' -H 'User-Agent: Google-HTTP-Java-Client/1.23.0 (gzip)' -- 'https://api.github.com/users?page=1&per_page=10'
Nov 12, 2017 6:43:16 PM com.google.api.client.http.HttpResponse 
-------------- RESPONSE --------------
HTTP/1.1 200 OK
Status: 200 OK
Transfer-Encoding: chunked
Server: GitHub.com
Access-Control-Allow-Origin: 
...
Link: <https://api.github.com/users?page=1&per_page=10&since=19>; rel="next", <https://api.github.com/users{?since}>; rel="first"
X-GitHub-Request-Id: 8D6A:1B54F:3377D97:3E37B36:5A08DC93
Content-Type: application/json; charset=utf-8
...
```

## 10.总结

在本教程中，我们展示了适用于Java的 Google HTTP 客户端库及其更有用的功能。他们的[Github](https://github.com/google/google-http-java-client)包含更多关于它的信息以及库的源代码。
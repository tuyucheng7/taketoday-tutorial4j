## 1. 简介

在本教程中，我们将了解[ScribeJava](https://github.com/scribejava/scribejava)库。

ScribeJava 是一个简单的JavaOAuth 客户端，可帮助管理 OAuth 流程。

该库的主要特点是它支持所有主要的 1.0 和 2.0 OAuth API 开箱即用。此外，如果我们必须使用不受支持的 API，该库提供了几个类来实现我们的 OAuth API。

另一个重要的特点是可以选择使用哪个客户端。事实上，ScribeJava 支持多种 HTTP 客户端：

-   [异步 Http 客户端](https://www.baeldung.com/async-http-client)
-   [好的HTTP](https://www.baeldung.com/guide-to-okhttp)
-   [Apache HttpComponents HttpClient](https://hc.apache.org/httpcomponents-client-5.1.x/)

此外，该库是线程安全的并且与 Java7 兼容，因此我们可以在遗留环境中使用它。

## 2.依赖关系

ScribeJava 分为核心和 APIs 模块，后者包括一组外部 API(Google、GitHub、Twitter 等)和核心工件：

```xml
<dependency>
    <groupId>com.github.scribejava</groupId>
    <artifactId>scribejava-apis</artifactId>
    <version>latest-version</version>
</dependency>
```

如果我们只需要核心类而不需要任何外部 API，我们必须只拉取核心模块：

```xml
<dependency>
    <groupId>com.github.scribejava</groupId>
    <artifactId>scribejava-core</artifactId>
    <version>latest-version</version>
</dependency>
```

最新版本可以在[Maven 存储库](https://search.maven.org/search?q=scribejava-core)中找到。

## 3.OAuth服务

该库的主要部分是抽象类 OAuthService，它包含正确管理 OAuth 的“握手”所需的所有参数。

根据协议的版本，我们将 分别为[OAuth 1.0](https://tools.ietf.org/html/rfc5849)和[OAuth 2.0使用](https://tools.ietf.org/html/rfc6749)Oauth10Service或 Oauth20Service具体类。

为了构建 OAuthService实现，该库提供了一个 ServiceBuilder：

```java
OAuthService service = new ServiceBuilder("api_key")
  .apiSecret("api_secret")
  .scope("scope")
  .callback("callback")
  .build(GoogleApi20.instance());
```

我们应该设置 授权服务器提供的api_key和 api_secret令牌。

此外，我们可以设置请求 的范围和 授权服务器在授权流程结束时应将用户重定向到的回调。

请注意，根据协议的版本，并非所有参数都是强制性的。

最后，我们必须构建 OAuthService ，调用 build()方法并将我们要使用的 API 的实例传递给它。我们可以在 ScribeJava [GitHub 上找到](https://github.com/scribejava/scribejava)支持的 API 的完整列表。

### 3.1. 客户端

此外，该库允许我们选择使用哪个 HTTP 客户端：

```java
ServiceBuilder builder = new ServiceBuilder("api_key")
  .httpClient(new OkHttpHttpClient());
```

当然，对于前面的示例，我们已经包含了所需的依赖项：

```xml
<dependency>
    <groupId>com.github.scribejava</groupId>
    <artifactId>scribejava-httpclient-okhttp</artifactId>
    <version>latest-version</version>
</dependency>
```

最新版本可以在[Maven 存储库](https://mvnrepository.com/artifact/com.github.scribejava)中找到。

### 3.2. 调试模式

此外，我们还可以使用调试模式来帮助我们排除故障：

```java
ServiceBuilder builder = new ServiceBuilder("api_key")
  .debug();
```

我们只需要调用 debug()方法。Debug会向 System.out输出一些相关信息。

此外，如果我们想使用不同的输出，还有另一种方法接受 OutputStream将调试信息发送到：

```java
FileOutputStream debugFile = new FileOutputStream("debug");

ServiceBuilder builder = new ServiceBuilder("api_key")
  .debug()
  .debugStream(debugFile);
```

## 4. OAuth 1.0 流程

现在让我们关注如何处理 OAuth1 流程。

在此示例中，我们将使用 Twitter API 获取 访问令牌，并使用它来发出请求。

首先，我们必须构建 Oauth10Service，正如我们之前看到的那样，使用构建器：

```java
OAuth10aService service = new ServiceBuilder("api_key")
  .apiSecret("api_secret")
  .build(TwitterApi.instance());
```

一旦我们有了 OAuth10Service， 我们就可以得到一个requestToken并用它来获取授权 URL：

```java
OAuth1RequestToken requestToken = service.getRequestToken();
String authUrl = service.getAuthorizationUrl(requestToken);
```

此时需要将用户重定向到 authUrl，获取页面提供的oauthVerifier 。

因此，我们使用oauthVerifier 来获取accessToken：

```java
OAuth1AccessToken accessToken = service.getAccessToken(requestToken,oauthVerifier);
```

最后，我们可以使用 OAuthRequest对象创建一个请求，并使用 signRequest()方法向其中添加令牌：

```java
OAuthRequest request = new OAuthRequest(Verb.GET, 
    "https://api.twitter.com/1.1/account/verify_credentials.json");
service.signRequest(accessToken, request);

Response response = service.execute(request);
```

作为执行该 请求的结果，我们得到一个Response 对象。

## 5. OAuth 2.0 流程

OAuth 2.0 流程与 OAuth 1.0 没有太大区别。为了解释这些变化，我们将 使用 Google API获取访问令牌。

以同样的方式，我们在 OAuth 1.0 流程中做了，我们必须构建 OAuthService 并获取 authUrl ，但这次我们将使用 OAuth20Service实例：

```java
OAuth20Service service = new ServiceBuilder("api_key")
  .apiSecret("api_secret")
  .scope("https://www.googleapis.com/auth/userinfo.email")
  .callback("http://localhost:8080/auth")
  .build(GoogleApi20.instance());

String authUrl = service.getAuthorizationUrl();
```

请注意，在这种情况下，我们需要提供 请求 的范围和我们将在授权流程结束时联系到的回调。

同样，我们必须将用户重定向到 authUrl 并 在回调的 url 中获取代码参数：

```java
OAuth2AccessToken accessToken = service.getAccessToken(code);

OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
service.signRequest(accessToken, request);

Response response = service.execute(request);
```

最后，为了发出请求，我们使用getAccessToken()方法获取accessToken 。

## 6.自定义API

我们可能不得不使用 ScribeJava 不支持的 API。在这种情况下，该库允许我们实现自己的 API。

我们唯一需要做的就是提供 DefaultApi10 或 DefaultApi20 类的实现。

让我们假设我们有一个 OAuth 2.0 授权服务器和密码授予。在这种情况下，我们可以实现 DefaultApi20 以便我们可以获取访问令牌：

```java
public class MyApi extends DefaultApi20 {

    public MyApi() {}

    private static class InstanceHolder {
        private static final MyApi INSTANCE = new MyApi();
    }

    public static MyApi instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "http://localhost:8080/oauth/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return null;
    }
}
```

因此，我们可以像以前一样以类似的方式获得访问令牌：

```java
OAuth20Service service = new ServiceBuilder("baeldung_api_key")
  .apiSecret("baeldung_api_secret")
  .scope("read write")
  .build(MyApi.instance());

OAuth2AccessToken token = service.getAccessTokenPasswordGrant(username, password);

OAuthRequest request = new OAuthRequest(Verb.GET, "http://localhost:8080/me");
service.signRequest(token, request);
Response response = service.execute(request);
```

## 七. 总结

在本文中，我们了解了 ScribeJava 提供的现成的最有用的类。

我们学习了如何使用外部 API 处理 OAuth 1.0 和 OAuth 2.0 流程。我们还学习了如何配置库以使用我们自己的 API。
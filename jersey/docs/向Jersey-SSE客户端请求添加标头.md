## 1. 概述

在本教程中，我们将看到一种使用 Jersey 客户端 API在[服务器发送事件(SSE) 客户端请求中发送标头的简单方法。](https://www.baeldung.com/spring-server-sent-events)

我们还将介绍使用默认的 Jersey 传输连接器发送基本键/值标头、身份验证标头和受限标头的正确方法。

## 2.开门见山

在尝试使用 SSE 发送标头时，我们可能都遇到过这种情况：

我们使用SseEventSource来接收 SSE，但要构建SseEventSource，我们需要一个WebTarget实例，它不会为我们提供添加标头的方法。Client实例也无济于事。听起来有点熟？

请记住，标头与 SSE 无关，而是与客户端请求本身相关， 因此我们真的应该查看那里。

让我们看看我们可以用ClientRequestFilter做什么。

## 3.依赖关系

要开始我们的旅程，我们需要[在](https://search.maven.org/search?q=g:org.glassfish.jersey.core AND a:jersey-client&core=gav)Maven pom.xml文件中添加[jersey-client](https://search.maven.org/search?q=g:org.glassfish.jersey.core AND a:jersey-client&core=gav)[依赖](https://search.maven.org/search?q=g:org.glassfish.jersey.core AND a:jersey-client&core=gav)项以及[Jersey 的 SSE 依赖项：](https://search.maven.org/search?q=g:org.glassfish.jersey.media AND a:jersey-media-sse&core=gav)

```xml
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>2.29</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-sse</artifactId>
    <version>2.29</version>
</dependency>
```

请注意，从 2.29 开始，Jersey 支持 JAX-RS 2.1，因此看起来我们将能够使用其中的功能。

## 4.客户端请求过滤器

首先，我们将实现将标头添加到每个客户端请求的过滤器：

```java
public class AddHeaderOnRequestFilter implements ClientRequestFilter {

    public static final String FILTER_HEADER_VALUE = "filter-header-value";
    public static final String FILTER_HEADER_KEY = "x-filter-header";

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(FILTER_HEADER_KEY, FILTER_HEADER_VALUE);
    }
}
```

之后，我们将注册并使用它。

对于我们的示例，我们将使用https://sse.example.org作为我们希望客户端使用事件的假想端点。实际上，我们会将其更改为我们希望客户端使用的真实[SSE 事件服务器端点。](https://www.baeldung.com/java-ee-jax-rs-sse)

```java
Client client = ClientBuilder.newBuilder()
  .register(AddHeaderOnRequestFilter.class)
  .build();

WebTarget webTarget = client.target("https://sse.example.org/");

SseEventSource sseEventSource = SseEventSource.target(webTarget).build();
sseEventSource.register((event) -> { / Consume event here / });
sseEventSource.open();
// do something here until ready to close
sseEventSource.close();
```

现在，如果我们需要将更复杂的标头(如身份验证标头)发送到我们的 SSE 端点怎么办？

让我们一起进入下一节，了解有关 Jersey Client API 中标头的更多信息。

## 5. Jersey 客户端 API 中的标头

重要的是要知道默认的 Jersey 传输连接器实现使用JDK中的HttpURLConnection类。此类限制某些标头的使用。为了避免这种限制，我们可以设置系统属性：

```java
System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
```

我们可以在[Jersey 文档](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/client.html#d0e5144)中找到受限标头列表。

### 5.1. 简单的通用标题

定义标头最直接的方法是调用WebTarget#request以获取提供标头 方法的Invocation.Builder 。

```java
public Response simpleHeader(String headerKey, String headerValue) {
    Client client = ClientBuilder.newClient();
    WebTarget webTarget = client.target("https://sse.example.org/");
    Invocation.Builder invocationBuilder = webTarget.request();
    invocationBuilder.header(headerKey, headerValue);
    return invocationBuilder.get();
}
```

而且，实际上，我们可以很好地压缩它以增加可读性：

```java
public Response simpleHeaderFluently(String headerKey, String headerValue) {
    Client client = ClientBuilder.newClient();

    return client.target("https://sse.example.org/")
      .request()
      .header(headerKey, headerValue)
      .get();
}
```

从这里开始，我们将仅使用流畅的形式作为示例，因为它更容易理解。

### 5.2. 基本认证

实际上，Jersey Client API提供了HttpAuthenticationFeature类，允许我们轻松发送身份验证标头：

```java
public Response basicAuthenticationAtClientLevel(String username, String password) {
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, password);
    Client client = ClientBuilder.newBuilder().register(feature).build();

    return client.target("https://sse.example.org/")
      .request()
      .get();
}
```

由于我们在构建客户端时注册了功能，因此它将应用于每个请求。API 处理 Basic 规范要求的用户名和密码的编码。

请注意，顺便说一句，我们暗示 HTTPS 作为我们的连接模式。 虽然这始终是一项有价值的安全措施，但在使用基本身份验证时它是基础，否则密码将以明文形式公开。Jersey 还支持[更复杂的安全配置](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/user-guide.html#d0e5313)。

现在，我们也可以在请求时指定信用：

```java
public Response basicAuthenticationAtRequestLevel(String username, String password) {
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().build();
    Client client = ClientBuilder.newBuilder().register(feature).build();

    return client.target("https://sse.example.org/")
      .request()
      .property(HTTP_AUTHENTICATION_BASIC_USERNAME, username)
      .property(HTTP_AUTHENTICATION_BASIC_PASSWORD, password)
      .get();
}
```

### 5.3. 摘要认证

Jersey 的HttpAuthenticationFeature也支持 Digest 认证：

```java
public Response digestAuthenticationAtClientLevel(String username, String password) {
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.digest(username, password);
    Client client = ClientBuilder.newBuilder().register(feature).build();

    return client.target("https://sse.example.org/")
      .request()
      .get();
}
```

同样，我们可以在请求时覆盖：

```java
public Response digestAuthenticationAtRequestLevel(String username, String password) {
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.digest();
    Client client = ClientBuilder.newBuilder().register(feature).build();

    return client.target("http://sse.example.org/")
      .request()
      .property(HTTP_AUTHENTICATION_DIGEST_USERNAME, username)
      .property(HTTP_AUTHENTICATION_DIGEST_PASSWORD, password)
      .get();
}
```

### 5.4. 使用 OAuth 2.0 的不记名令牌身份验证

OAuth 2.0 支持 Bearer 令牌作为另一种身份验证机制的概念。

我们需要[Jersey 的oauth2-client依赖](https://search.maven.org/search?q=g:org.glassfish.jersey.security AND a:oauth2-client&core=gav)项来为我们提供类似于HttpAuthenticationFeature的OAuth2ClientSupportFeature：

```xml
<dependency>
    <groupId>org.glassfish.jersey.security</groupId>
    <artifactId>oauth2-client</artifactId>
    <version>2.29</version>
</dependency>
```

要添加不记名令牌，我们将遵循与以前类似的模式：

```java
public Response bearerAuthenticationWithOAuth2AtClientLevel(String token) {
    Feature feature = OAuth2ClientSupport.feature(token);
    Client client = ClientBuilder.newBuilder().register(feature).build();

    return client.target("https://sse.examples.org/")
      .request()
      .get();
}
```

或者，我们可以在请求级别覆盖，这在令牌因轮换而更改时特别方便：

```java
public Response bearerAuthenticationWithOAuth2AtRequestLevel(String token, String otherToken) {
    Feature feature = OAuth2ClientSupport.feature(token);
    Client client = ClientBuilder.newBuilder().register(feature).build();

    return client.target("https://sse.example.org/")
      .request()
      .property(OAuth2ClientSupport.OAUTH2_PROPERTY_ACCESS_TOKEN, otherToken)
      .get();
}
```

### 5.5. 使用 OAuth 1.0 的不记名令牌身份验证

第四，如果我们需要与使用 OAuth 1.0 的遗留代码集成，我们将需要[Jersey 的oauth1-client依赖](https://search.maven.org/search?q=g:org.glassfish.jersey.security AND a:oauth1-client&core=gav)项：

```xml
<dependency>
    <groupId>org.glassfish.jersey.security</groupId>
    <artifactId>oauth1-client</artifactId>
    <version>2.29</version>
</dependency>
```

与 OAuth 2.0 类似，我们有可以使用的OAuth1ClientSupport：

```java
public Response bearerAuthenticationWithOAuth1AtClientLevel(String token, String consumerKey) {
    ConsumerCredentials consumerCredential = 
      new ConsumerCredentials(consumerKey, "my-consumer-secret");
    AccessToken accessToken = new AccessToken(token, "my-access-token-secret");

    Feature feature = OAuth1ClientSupport
      .builder(consumerCredential)
      .feature()
      .accessToken(accessToken)
      .build();

    Client client = ClientBuilder.newBuilder().register(feature).build();

    return client.target("https://sse.example.org/")
      .request()
      .get();
}
```

随着 OAuth1ClientSupport.OAUTH_PROPERTY_ACCESS_TOKEN属性再次启用请求级别。

## 六. 总结

总而言之，在本文中，我们介绍了如何使用过滤器向 Jersey 中的 SSE 客户端请求添加标头。我们还特别介绍了如何使用身份验证标头。
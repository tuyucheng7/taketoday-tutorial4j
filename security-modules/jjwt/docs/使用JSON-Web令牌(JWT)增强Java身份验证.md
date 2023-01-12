准备好在你的Java应用程序中构建安全身份验证了吗？不确定使用令牌(特别是 JSON 网络令牌)的好处，或者它们应该如何部署？我很高兴在本教程中为你回答这些问题以及更多问题！

在我们深入了解 JSON Web 令牌 ( [JWT](https://en.wikipedia.org/wiki/JSON_Web_Token) ) 和[JJWT 库](https://github.com/jwtk/jjwt)(由 Stormpath 的 CTO Les Hazlewood 创建并由贡献者[社区](https://github.com/jwtk/jjwt/graphs/contributors)维护)之前，让我们先了解一些基础知识。

## 1.认证与令牌认证

应用程序用来确认用户身份的协议集是身份验证。应用程序传统上通过会话 cookie 来持久化身份。此范例依赖于会话 ID 的服务器端存储，这迫使开发人员创建唯一且特定于服务器的会话存储，或者实现为完全独立的会话存储层。

开发令牌身份验证是为了解决服务器端会话 ID 没有也不能解决的问题。就像传统身份验证一样，用户提供可验证的凭据，但现在会获得一组令牌而不是会话 ID。初始凭据可以是标准的用户名/密码对、API 密钥，甚至是来自其他服务的令牌。(Stormpath 的 API 密钥身份验证功能就是一个例子。)

### 1.1. 为什么是代币？

非常简单，使用令牌代替会话 ID 可以降低服务器负载、简化权限管理并提供更好的工具来支持分布式或基于云的基础架构。在 JWT 的情况下，这主要是通过这些类型的令牌的无状态性质来实现的(更多内容见下文)。

令牌提供了广泛的应用程序，包括：跨站点请求伪造 ( [CSRF](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html) ) 保护方案、[OAuth 2.0](https://tools.ietf.org/html/rfc6749)交互、会话 ID 和(在 cookie 中)作为身份验证表示。在大多数情况下，标准不指定令牌的特定格式。这是HTML 表单中典型的[Spring Security CSRF 令牌](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/csrf/CsrfToken.html)的示例：

```html
<input name="_csrf" type="hidden" 
  value="f3f42ea9-3104-4d13-84c0-7bcb68202f16"/>
```

如果你尝试在没有正确的 CSRF 令牌的情况下发布该表单，你会收到错误响应，这就是令牌的实用性。上面的例子是一个“哑巴”令牌。这意味着没有从令牌本身收集到的内在含义。这也是 JWT 发挥重要作用的地方。

## 延伸阅读：

## [将 JWT 与 Spring Security OAuth 结合使用](https://www.baeldung.com/spring-security-oauth-jwt)

在 Spring Security 5 中使用 JWT 令牌的指南。

[阅读更多](https://www.baeldung.com/spring-security-oauth-jwt)→

## [Spring REST API + OAuth2 + Angular](https://www.baeldung.com/rest-api-spring-oauth2-angular)

了解如何使用 Spring Security 5 为 Spring REST API 设置 OAuth2，以及如何从 Angular 客户端使用它。

[阅读更多](https://www.baeldung.com/rest-api-spring-oauth2-angular)→

## [用于 Spring REST API 的 OAuth2——处理 Angular 中的刷新令牌](https://www.baeldung.com/spring-security-oauth2-refresh-token-angular)

查看如何使用 Spring Security 5 OAuth 堆栈和利用 Zuul 代理来刷新令牌。

[阅读更多](https://www.baeldung.com/spring-security-oauth2-refresh-token-angular)→

## 2. JWT 中有什么？

JWT(发音为“jots”)是 URL 安全的、编码的、加密签名的(有时是加密的)字符串，可以在各种应用程序中用作令牌。以下是将 JWT 用作 CSRF 令牌的示例：

```html
<input name="_csrf" type="hidden" 
  value="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlNjc4ZjIzMzQ3ZTM0MTBkYjdlNjg3Njc4MjNiMmQ3MCIsImlhdCI6MTQ2NjYzMzMxNywibmJmIjoxNDY2NjMzMzE3LCJleHAiOjE0NjY2MzY5MTd9.rgx_o8VQGuDa2AqCHSgVOD5G68Ld_YYM7N7THmvLIKc"/>
```

在这种情况下，你可以看到令牌比我们之前的示例中的要长得多。就像我们之前看到的那样，如果提交的表单没有令牌，你会收到错误响应。

那么，为什么是智威汤逊？

上述令牌经过加密签名，因此可以验证，提供未被篡改的证据。此外，JWT 编码有各种附加信息。

让我们看一下 JWT 的剖析，以更好地理解我们如何从中榨取所有这些好处。你可能已经注意到，三个不同的部分由句点 ( `.`) 分隔：

| 标头     | eyJhbGciOiJIUzI1NiJ9                                         |
| -------- | ------------------------------------------------------------ |
| 有效载荷 | eyJqdGkiOiJlNjc4ZjIzMzQ3ZTM0MTBkYjdlNjg3Njc4MjNiMmQ3MCIsImlhdC I6MTQ2NjYzMzMxNywibmJmIjoxNDY2NjMzMzE3LCJleHAiOjE0NjY2MzY5MTd9 |
| 签名     | rgx_o8VQGuDa2AqCHSgVOD5G68Ld_YYM7N7THmvLIKc                  |

每个部分都是[base64](https://en.wikipedia.org/wiki/Base64) URL 编码的。这确保它可以在 URL 中安全地使用(稍后会详细介绍)。让我们分别仔细看看每个部分。

### 2.1. 标题

如果你使用 base64 来解码 header，你将得到以下 JSON 字符串：

```javascript
{"alg":"HS256"}
```

这表明 JWT 是使用[SHA-256与](https://en.wikipedia.org/wiki/SHA-2)[HMAC](https://en.wikipedia.org/wiki/Hash-based_message_authentication_code)签署的。

### 2.2. 有效载荷

如果你解码有效负载，你将获得以下 JSON 字符串(为清楚起见格式化)：

```javascript
{
  "jti": "e678f23347e3410db7e68767823b2d70",
  "iat": 1466633317,
  "nbf": 1466633317,
  "exp": 1466636917
}
```

如你所见，在有效负载中，有许多带有值的键。这些键称为“声明”，[JWT 规范](https://tools.ietf.org/html/rfc7519#section-4.1)将其中七个指定为“已注册”声明。他们是：

| 伊斯 | 发行人     |
| ---- | ---------- |
| 子   | 主题       |
| 音频 | 观众       |
| exp  | 过期       |
| nbf  | 之前没有   |
| 我在 | 发出于     |
| jti  | 智威汤逊ID |

在构建 JWT 时，你可以输入任何你想要的自定义声明。上面的列表仅表示在使用的密钥和预期类型中保留的声明。我们的 CSRF 有一个 JWT ID、一个“发布时间”、一个“不早于”时间和一个过期时间。过期时间恰好是发出时间后的一分钟。

### 2.3. 签名

最后，签名部分是通过将标头和有效负载放在一起(中间有 .)并将其传递给指定算法(在本例中使用 SHA-256 的 HMAC)以及已知秘密来创建的。请注意，秘密始终是一个字节数组，并且其长度应该对所使用的算法有意义。下面，我使用一个随机的 base64 编码字符串(为了可读性)，它被转换成一个字节数组。

在伪代码中看起来像这样：

```javascript
computeHMACSHA256(
    header + "." + payload, 
    base64DecodeToByteArray("4pE8z3PBoHjnV1AhvGk+e8h2p+ShZpOnpr8cwHmMh1w=")
)
```

只要你知道这个秘密，你就可以自己生成签名，并将你的结果与 JWT 的签名部分进行比较，以验证它没有被篡改。从技术上讲，经过加密签名的 JWT 称为[JWS](https://tools.ietf.org/html/rfc7515)。JWTs 也可以被加密，然后被称为[JWE](https://tools.ietf.org/html/rfc7516)。(在实际操作中，术语 JWT 用于描述 JWE 和 JWS。)

这让我们回到使用 JWT 作为我们的 CSRF 令牌的好处。我们可以验证签名，我们可以使用 JWT 中编码的信息来确认其有效性。因此，不仅 JWT 的字符串表示需要与服务器端存储的内容相匹配，我们还可以通过检查 exp 声明来确保它没有过期。这使服务器免于维护额外的状态。

好吧，我们已经在这里讨论了很多内容。让我们深入研究一些代码！

## 3. 设置 JJWT 教程

JJWT ( https://github.com/jwtk/jjwt ) 是一个提供端到端 JSON Web Token 创建和验证的Java库。永远免费和开源(Apache 许可，版本 2.0)，它设计有一个以构建器为中心的界面，隐藏了它的大部分复杂性。

使用 JJWT 的主要操作涉及构建和解析 JWT。接下来我们将查看这些操作，然后进入 JJWT 的一些扩展功能，最后，我们将看到 JWT 在 Spring Security、Spring Boot 应用程序中作为 CSRF 令牌运行。

[可以在此处](https://github.com/eugenp/tutorials/tree/master/security-modules/jjwt)找到以下部分中演示的代码。注意：该项目从一开始就使用 Spring Boot，因为它很容易与它公开的 API 进行交互。

Spring Boot 的一大优点是[构建和启动应用程序](https://www.baeldung.com/spring-boot-run-maven-vs-executable-jar)非常容易。要运行 JJWT Fun 应用程序，只需执行以下操作：

```bash
mvn clean spring-boot:run
```

此示例应用程序中公开了十个端点(我使用 httpie 与应用程序交互。可以[在此处](https://github.com/jkbrzt/httpie)找到。)

```bash
http localhost:8080
Available commands (assumes httpie - https://github.com/jkbrzt/httpie):

  http http://localhost:8080/
	This usage message

  http http://localhost:8080/static-builder
	build JWT from hardcoded claims

  http POST http://localhost:8080/dynamic-builder-general claim-1=value-1 ... [claim-n=value-n]
	build JWT from passed in claims (using general claims map)

  http POST http://localhost:8080/dynamic-builder-specific claim-1=value-1 ... [claim-n=value-n]
	build JWT from passed in claims (using specific claims methods)

  http POST http://localhost:8080/dynamic-builder-compress claim-1=value-1 ... [claim-n=value-n]
	build DEFLATE compressed JWT from passed in claims

  http http://localhost:8080/parser?jwt=<jwt>
	Parse passed in JWT

  http http://localhost:8080/parser-enforce?jwt=<jwt>
	Parse passed in JWT enforcing the 'iss' registered claim and the 'hasMotorcycle' custom claim

  http http://localhost:8080/get-secrets
	Show the signing keys currently in use.

  http http://localhost:8080/refresh-secrets
	Generate new signing keys and show them.

  http POST http://localhost:8080/set-secrets 
    HS256=base64-encoded-value HS384=base64-encoded-value HS512=base64-encoded-value
	Explicitly set secrets to use in the application.
```

在接下来的部分中，我们将检查每个端点和处理程序中包含的 JJWT 代码。

## 4. 使用 JJWT 构建 JWT

由于 JJWT 的[流畅接口](https://en.wikipedia.org/wiki/Fluent_interface)，JWT 的创建基本上是一个三步过程：

1.  令牌内部声明的定义，如发行人、主题、到期日和 ID。
2.  JWT 的加密签名(使其成为 JWS)。
3.  根据[JWT 紧凑序列化](https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)规则，将 JWT 压缩为 URL 安全字符串。

最终的 JWT 将是一个由三部分组成的 base64 编码字符串，使用指定的签名算法签名，并使用提供的密钥。在此之后，令牌已准备好与另一方共享。

下面是 JJWT 的一个例子：

```java
String jws = Jwts.builder()
  .setIssuer("Stormpath")
  .setSubject("msilverman")
  .claim("name", "Micah Silverman")
  .claim("scope", "admins")
  // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
  .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
  // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
  .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
  .signWith(
    SignatureAlgorithm.HS256,
    TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")
  )
  .compact();
```

这与代码项目的StaticJWTController.fixedBuilder方法中的代码非常相似。

在这一点上，有必要谈谈一些与 JWT 和签名相关的反模式。如果你以前看过 JWT 示例，你可能遇到过以下签名反模式场景之一：

1.  ```java
    .signWith(
        SignatureAlgorithm.HS256,
       "secret".getBytes("UTF-8")    
    )
    ```

2.  ```java
    .signWith(
        SignatureAlgorithm.HS256,
        "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=".getBytes("UTF-8")
    )
    ```

3.  ```java
    .signWith(
        SignatureAlgorithm.HS512,
        TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")
    )
    ```

任何HS类型签名算法都采用字节数组。人类阅读很方便取一个字符串并将其转换为字节数组。

上面的反模式 1 证明了这一点。这是有问题的，因为秘密因太短而被削弱，而且它不是原始形式的字节数组。因此，为了保持可读性，我们可以对字节数组进行 base64 编码。

但是，上面的反模式 2 采用 base64 编码的字符串并将其直接转换为字节数组。应该做的是将 base64 字符串解码回原始字节数组。

上面的数字 3 证明了这一点。那么，为什么这也是一种反模式呢？在这种情况下，这是一个微妙的原因。请注意，签名算法是 HS512。字节数组不是HS512可以支持的最大长度，使其成为比该算法可能的秘密更弱的秘密。

示例代码包括一个名为SecretService的类，该类确保将适当强度的秘密用于给定算法。在应用程序启动时，会为每个 HS 算法创建一组新的秘密。有端点可以刷新机密以及显式设置机密。

如果你的项目按上述方式运行，请执行以下操作，以便下面的 JWT 示例与你的项目的响应相匹配。

```bash
http POST localhost:8080/set-secrets 
  HS256="Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=" 
  HS384="VW96zL+tYlrJLNCQ0j6QPTp+d1q75n/Wa8LVvpWyG8pPZOP6AA5X7XOIlI90sDwx" 
  HS512="cd+Pr1js+w2qfT2BoCD+tPcYp9LbjpmhSMEJqUob1mcxZ7+Wmik4AYdjX+DlDjmE4yporzQ9tm7v3z/j+QbdYg=="
```

现在，你可以点击/static-builder端点：

```bash
http http://localhost:8080/static-builder
```

这会生成一个如下所示的 JWT：

```bash
eyJhbGciOiJIUzI1NiJ9.
eyJpc3MiOiJTdG9ybXBhdGgiLCJzdWIiOiJtc2lsdmVybWFuIiwibmFtZSI6Ik1pY2FoIFNpbHZlcm1hbiIsInNjb3BlIjoiYWRtaW5zIiwiaWF0IjoxNDY2Nzk2ODIyLCJleHAiOjQ2MjI0NzA0MjJ9.
kP0i_RvTAmI8mgpIkDFhRX3XthSdP-eqqFKGcU92ZIQ
```

现在，点击：

```bash
http http://localhost:8080/parser?jwt=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJzdWIiOiJtc2lsdmVybWFuIiwibmFtZSI6Ik1pY2FoIFNpbHZlcm1hbiIsInNjb3BlIjoiYWRtaW5zIiwiaWF0IjoxNDY2Nzk2ODIyLCJleHAiOjQ2MjI0NzA0MjJ9.kP0i_RvTAmI8mgpIkDFhRX3XthSdP-eqqFKGcU92ZIQ
```

响应包含我们在创建 JWT 时包含的所有声明。

```javascript
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
...
{
    "jws": {
        "body": {
            "exp": 4622470422,
            "iat": 1466796822,
            "iss": "Stormpath",
            "name": "Micah Silverman",
            "scope": "admins",
            "sub": "msilverman"
        },
        "header": {
            "alg": "HS256"
        },
        "signature": "kP0i_RvTAmI8mgpIkDFhRX3XthSdP-eqqFKGcU92ZIQ"
    },
    "status": "SUCCESS"
}
```

这就是解析操作，我们将在下一节中介绍。

现在，让我们点击一个将声明作为参数并将为我们构建自定义 JWT 的端点。

```bash
http -v POST localhost:8080/dynamic-builder-general iss=Stormpath sub=msilverman hasMotorcycle:=true
```

注意： hasMotorcycle声明与其他声明之间存在细微差别。httpie 默认假定 JSON 参数是字符串。要使用 httpie 提交原始 JSON，你可以使用:=形式而不是=。否则，它会提交“hasMotorcycle”：“true”，这不是我们想要的。

这是输出：

```javascript
POST /dynamic-builder-general HTTP/1.1
Accept: application/json
...
{
    "hasMotorcycle": true,
    "iss": "Stormpath",
    "sub": "msilverman"
}

HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
...
{
    "jwt": 
      "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJzdWIiOiJtc2lsdmVybWFuIiwiaGFzTW90b3JjeWNsZSI6dHJ1ZX0.OnyDs-zoL3-rw1GaSl_KzZzHK9GoiNocu-YwZ_nQNZU",
    "status": "SUCCESS"
}

```

让我们看一下支持此端点的代码：

```java
@RequestMapping(value = "/dynamic-builder-general", method = POST)
public JwtResponse dynamicBuilderGeneric(@RequestBody Map<String, Object> claims) 
  throws UnsupportedEncodingException {
    String jws =  Jwts.builder()
        .setClaims(claims)
        .signWith(
            SignatureAlgorithm.HS256,
            secretService.getHS256SecretBytes()
        )
        .compact();
    return new JwtResponse(jws);
}
```

第 2 行确保传入的 JSON 自动转换为JavaMap<String, Object>，这对于 JJWT 来说非常方便，因为第 5 行的方法只需获取该 Map 并立即设置所有声明。

与此代码一样简洁，我们需要更具体的内容来确保传递的声明有效。当你已经知道地图中表示的声明有效时，使用.setClaims(Map<String, Object> claims)方法会很方便。这就是Java的类型安全进入 JJWT 库的地方。

对于 JWT 规范中定义的每个 Registered Claims，在 JJWT 中都有一个相应的Java方法采用规范正确的类型。

让我们点击示例中的另一个端点，看看会发生什么：

```bash
http -v POST localhost:8080/dynamic-builder-specific iss=Stormpath sub:=5 hasMotorcycle:=true
```

请注意，我们为“sub”声明传入了一个整数 5。这是输出：

```javascript
POST /dynamic-builder-specific HTTP/1.1
Accept: application/json
...
{
    "hasMotorcycle": true,
    "iss": "Stormpath",
    "sub": 5
}

HTTP/1.1 400 Bad Request
Connection: close
Content-Type: application/json;charset=UTF-8
...
{
    "exceptionType": "java.lang.ClassCastException",
    "message": "java.lang.Integer cannot be cast to java.lang.String",
    "status": "ERROR"
}
```

现在，我们收到错误响应，因为代码正在强制执行已注册声明的类型。在这种情况下，sub必须是一个字符串。这是支持此端点的代码：

```java
@RequestMapping(value = "/dynamic-builder-specific", method = POST)
public JwtResponse dynamicBuilderSpecific(@RequestBody Map<String, Object> claims) 
  throws UnsupportedEncodingException {
    JwtBuilder builder = Jwts.builder();
    
    claims.forEach((key, value) -> {
        switch (key) {
            case "iss":
                builder.setIssuer((String) value);
                break;
            case "sub":
                builder.setSubject((String) value);
                break;
            case "aud":
                builder.setAudience((String) value);
                break;
            case "exp":
                builder.setExpiration(Date.from(
                    Instant.ofEpochSecond(Long.parseLong(value.toString()))
                ));
                break;
            case "nbf":
                builder.setNotBefore(Date.from(
                    Instant.ofEpochSecond(Long.parseLong(value.toString()))
                ));
                break;
            case "iat":
                builder.setIssuedAt(Date.from(
                    Instant.ofEpochSecond(Long.parseLong(value.toString()))
                ));
                break;
            case "jti":
                builder.setId((String) value);
                break;
            default:
                builder.claim(key, value);
        }
    });
	
    builder.signWith(SignatureAlgorithm.HS256, secretService.getHS256SecretBytes());

    return new JwtResponse(builder.compact());
}
```

就像以前一样，该方法接受声明的Map<String, Object>作为其参数。但是，这一次，我们为每个强制类型的已注册声明调用特定方法。

对此的一项改进是使错误消息更加具体。现在，我们只知道我们的声明之一不是正确的类型。我们不知道哪个声明是错误的或应该是什么。这里有一个方法可以给我们一个更具体的错误信息。它还处理当前代码中的错误。

```java
private void ensureType(String registeredClaim, Object value, Class expectedType) {
    boolean isCorrectType =
        expectedType.isInstance(value) ||
        expectedType == Long.class && value instanceof Integer;

    if (!isCorrectType) {
        String msg = "Expected type: " + expectedType.getCanonicalName() + 
		    " for registered claim: '" + registeredClaim + "', but got value: " + 
			value + " of type: " + value.getClass().getCanonicalName();
        throw new JwtException(msg);
    }
}
```

第 3 行检查传入的值是否属于预期类型。如果不是，则抛出带有特定错误的JwtException 。让我们通过进行与之前相同的调用来实际查看一下：

```bash
http -v POST localhost:8080/dynamic-builder-specific iss=Stormpath sub:=5 hasMotorcycle:=true
POST /dynamic-builder-specific HTTP/1.1
Accept: application/json
...
User-Agent: HTTPie/0.9.3

{
    "hasMotorcycle": true,
    "iss": "Stormpath",
    "sub": 5
}

HTTP/1.1 400 Bad Request
Connection: close
Content-Type: application/json;charset=UTF-8
...
{
    "exceptionType": "io.jsonwebtoken.JwtException",
    "message": 
      "Expected type: java.lang.String for registered claim: 'sub', but got value: 5 of type: java.lang.Integer",
    "status": "ERROR"
}
```

现在，我们有一条非常具体的错误消息告诉我们sub声明是错误的。

让我们回到我们代码中的那个错误。该问题与 JJWT 库无关。问题是Spring Boot中内置的 JSON 到Java对象映射器对于我们自己来说太聪明了。

如果有一个方法接受Java对象，JSON 映射器会自动将传入的小于或等于 2,147,483,647 的数字转换为JavaInteger。同样，它会自动将传入的大于 2,147,483,647 的数字转换为JavaLong。对于 JWT 的iat、nbf和exp声明，我们希望我们的 ensureType 测试能够通过映射的对象是 Integer 还是 Long。这就是为什么我们有额外的子句来确定传入的值是否是正确的类型：

```java
 boolean isCorrectType =
     expectedType.isInstance(value) ||
     expectedType == Long.class && value instanceof Integer;
```

如果我们期待一个 Long，但值是 Integer 的一个实例，我们仍然说它是正确的类型。了解此验证发生的情况后，我们现在可以将其集成到我们的dynamicBuilderSpecific方法中：

```java
@RequestMapping(value = "/dynamic-builder-specific", method = POST)
public JwtResponse dynamicBuilderSpecific(@RequestBody Map<String, Object> claims) 
  throws UnsupportedEncodingException {
    JwtBuilder builder = Jwts.builder();

    claims.forEach((key, value) -> {
        switch (key) {
            case "iss":
                ensureType(key, value, String.class);
                builder.setIssuer((String) value);
                break;
            case "sub":
                ensureType(key, value, String.class);
                builder.setSubject((String) value);
                break;
            case "aud":
                ensureType(key, value, String.class);
                builder.setAudience((String) value);
                break;
            case "exp":
                ensureType(key, value, Long.class);
                builder.setExpiration(Date.from(
				    Instant.ofEpochSecond(Long.parseLong(value.toString()))
				));
                break;
            case "nbf":
                ensureType(key, value, Long.class);
                builder.setNotBefore(Date.from(
					Instant.ofEpochSecond(Long.parseLong(value.toString()))
				));
                break;
            case "iat":
                ensureType(key, value, Long.class);
                builder.setIssuedAt(Date.from(
					Instant.ofEpochSecond(Long.parseLong(value.toString()))
				));
                break;
            case "jti":
                ensureType(key, value, String.class);
                builder.setId((String) value);
                break;
            default:
                builder.claim(key, value);
        }
    });

    builder.signWith(SignatureAlgorithm.HS256, secretService.getHS256SecretBytes());

    return new JwtResponse(builder.compact());
}
```

注意：在本节的所有示例代码中，JWT 使用 SHA-256 算法通过 HMAC 进行签名。这是为了使示例保持简单。JJWT 库支持 12 种不同的签名算法，你可以在自己的代码中加以利用。

## 5. 使用 JJWT 解析 JWT

我们之前看到我们的代码示例有一个用于解析 JWT 的端点。命中此端点：

```bash
http http://localhost:8080/parser?jwt=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJzdWIiOiJtc2lsdmVybWFuIiwibmFtZSI6Ik1pY2FoIFNpbHZlcm1hbiIsInNjb3BlIjoiYWRtaW5zIiwiaWF0IjoxNDY2Nzk2ODIyLCJleHAiOjQ2MjI0NzA0MjJ9.kP0i_RvTAmI8mgpIkDFhRX3XthSdP-eqqFKGcU92ZIQ
```

产生这个响应：

```javascript
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
...
{
    "claims": {
        "body": {
            "exp": 4622470422,
            "iat": 1466796822,
            "iss": "Stormpath",
            "name": "Micah Silverman",
            "scope": "admins",
            "sub": "msilverman"
        },
        "header": {
            "alg": "HS256"
        },
        "signature": "kP0i_RvTAmI8mgpIkDFhRX3XthSdP-eqqFKGcU92ZIQ"
    },
    "status": "SUCCESS"
}
```

StaticJWTController类的解析器方法如下所示：

```java
@RequestMapping(value = "/parser", method = GET)
public JwtResponse parser(@RequestParam String jwt) throws UnsupportedEncodingException {
    Jws<Claims> jws = Jwts.parser()
        .setSigningKeyResolver(secretService.getSigningKeyResolver())
        .parseClaimsJws(jwt);

    return new JwtResponse(jws);
}
```

第 4 行表示我们希望传入的字符串是已签名的 JWT(JWS)。而且，我们在解析 JWT 时使用的是与签名相同的秘密。第 5 行解析来自 JWT 的声明。在内部，它正在验证签名，如果签名无效，它将抛出异常。

请注意，在这种情况下，我们传递的是SigningKeyResolver而不是密钥本身。这是 JJWT 最强大的方面之一。JWT 的标头指示用于对其进行签名的算法。但是，我们需要在信任 JWT 之前对其进行验证。这似乎是一个陷阱 22。让我们看一下SecretService.getSigningKeyResolver方法：

```java
private SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
    @Override
    public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
        return TextCodec.BASE64.decode(secrets.get(header.getAlgorithm()));
    }
};
```

使用对JwsHeader的访问，我可以检查算法并返回用于签署 JWT 的秘密的正确字节数组。现在，JJWT 将使用此字节数组作为密钥来验证 JWT 是否未被篡改。

如果我删除传入的 JWT 的最后一个字符(这是签名的一部分)，这就是响应：

```javascript
HTTP/1.1 400 Bad Request
Connection: close
Content-Type: application/json;charset=UTF-8
Date: Mon, 27 Jun 2016 13:19:08 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "exceptionType": "io.jsonwebtoken.SignatureException",
    "message": 
      "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.",
    "status": "ERROR"
}
```

## 6. JWT 实践：Spring Security CSRF 令牌

虽然这篇文章的重点不是 Spring Security，但我们将在这里深入研究一下，以展示 JJWT 库的一些实际用法。

[Cross Site Request Forgery](https://en.wikipedia.org/wiki/Cross-site_request_forgery)是一种安全漏洞，恶意网站会诱使你向你已建立信任的网站提交请求。对此的常见补救措施之一是实施[同步器令牌模式](https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/csrf.html)。这种方法将一个令牌插入到 Web 表单中，应用程序服务器根据其存储库检查传入的令牌以确认它是正确的。如果令牌丢失或无效，服务器将响应错误。

Spring Security 内置了同步器令牌模式。更好的是，如果你使用的是[Spring Boot 和 Thymeleaf 模板](https://spring.io/guides/gs/serving-web-content/)，同步器令牌会自动为你插入。

默认情况下，Spring Security 使用的令牌是“哑”令牌。它只是一系列字母和数字。这种方法很好并且有效。在本节中，我们通过使用 JWT 作为令牌来增强基本功能。除了验证提交的令牌是否是预期的之外，我们还验证 JWT 以进一步证明令牌未被篡改并确保其未过期。

首先，我们将使用Java配置来配置 Spring Security。默认情况下，所有路径都需要身份验证，所有 POST 端点都需要 CSRF 令牌。我们将放宽一点，以便我们迄今为止构建的内容仍然有效。

```java
@Configuration
public class WebSecurityConfig {

    private String[] ignoreCsrfAntMatchers = {
        "/dynamic-builder-compress",
        "/dynamic-builder-general",
        "/dynamic-builder-specific",
        "/set-secrets"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
                .ignoringAntMatchers(ignoreCsrfAntMatchers)
            .and().authorizeRequests()
                .antMatchers("/")
                .permitAll();
        return http.build();
    }
}
```

我们在这里做两件事。首先，我们说在发布到我们的 REST API 端点时不需要CSRF 令牌(第 15 行)。其次，我们说应该允许所有路径进行未经身份验证的访问(第 17-18 行)。

让我们确认 Spring Security 是否按照我们预期的方式工作。启动应用程序并在浏览器中点击此网址：

```bash
http://localhost:8080/jwt-csrf-form
```

这是此视图的 Thymeleaf 模板：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
        <!--// <th:block th:include="fragments/head :: head"/> //-->
    </head>
    <body>
        <div class="container-fluid">
            <div class="row">
                <div class="box col-md-6 col-md-offset-3">
                    <p/>
                    <form method="post" th:action="@{/jwt-csrf-form}">
                        <input type="submit" class="btn btn-primary" value="Click Me!"/>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
```

这是一个非常基本的表单，提交时将 POST 到相同的端点。请注意，表单中没有明确引用 CSRF 令牌。如果查看源代码，你将看到类似以下内容：

```html
<input type="hidden" name="_csrf" value="5f375db2-4f40-4e72-9907-a290507cb25e" />
```

这是你需要知道 Spring Security 正在运行并且 Thymeleaf 模板正在自动插入 CSRF 令牌的所有确认。

为了使该值成为 JWT，我们将启用自定义CsrfTokenRepository。以下是我们的 Spring Security 配置更改的方式：

```java
@Configuration
public class WebSecurityConfig {

    @Autowired
    CsrfTokenRepository jwtCsrfTokenRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
                .csrfTokenRepository(jwtCsrfTokenRepository)
                .ignoringAntMatchers(ignoreCsrfAntMatchers)
            .and().authorizeRequests()
                .antMatchers("/")
                .permitAll();
        return http.build();
    }
}
```

为了连接这个，我们需要一个配置来公开一个返回自定义令牌存储库的 bean。这是配置：

```java
@Configuration
public class CSRFConfig {

    @Autowired
    SecretService secretService;

    @Bean
    @ConditionalOnMissingBean
    public CsrfTokenRepository jwtCsrfTokenRepository() {
        return new JWTCsrfTokenRepository(secretService.getHS256SecretBytes());
    }
}
```

而且，这是我们的自定义存储库(重要的部分)：

```java
public class JWTCsrfTokenRepository implements CsrfTokenRepository {

    private static final Logger log = LoggerFactory.getLogger(JWTCsrfTokenRepository.class);
    private byte[] secret;

    public JWTCsrfTokenRepository(byte[] secret) {
        this.secret = secret;
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String id = UUID.randomUUID().toString().replace("-", "");

        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + (100030)); // 30 seconds

        String token;
        try {
            token = Jwts.builder()
                .setId(id)
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        } catch (UnsupportedEncodingException e) {
            log.error("Unable to create CSRf JWT: {}", e.getMessage(), e);
            token = id;
        }

        return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", token);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        ...
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        ...
    }
}
```

generateToken方法创建一个 JWT，该 JWT 在创建后 30 秒后过期。有了这个管道，我们可以再次启动应用程序并查看/jwt-csrf-form的源代码。

现在，隐藏字段看起来像这样：

```html
<input type="hidden" name="_csrf" 
  value="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxZjIyMDdiNTVjOWM0MjU0YjZlMjY4MjQwYjIwNzZkMSIsImlhdCI6MTQ2NzA3MDQwMCwibmJmIjoxNDY3MDcwNDAwLCJleHAiOjE0NjcwNzA0MzB9.2kYLO0iMWUheAncXAzm0UdQC1xUC5I6RI_ShJ_74e5o" />
```

万岁！现在我们的 CSRF 令牌是 JWT。那不是太难。

然而，这只是谜题的一半。默认情况下，Spring Security 仅保存 CSRF 令牌并确认以 Web 表单提交的令牌与保存的令牌匹配。我们想要扩展功能以验证 JWT 并确保它没有过期。为此，我们将添加一个过滤器。这是我们的 Spring Security 配置现在的样子：

```java
@Configuration
public class WebSecurityConfig {

    ...

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .addFilterAfter(new JwtCsrfValidatorFilter(), CsrfFilter.class)
            .csrf()
                .csrfTokenRepository(jwtCsrfTokenRepository)
                .ignoringAntMatchers(ignoreCsrfAntMatchers)
            .and().authorizeRequests()
                .antMatchers("/")
                .permitAll();
        return http.build();
    }

    ...
}
```

在第 9 行，我们添加了一个过滤器，并将其放在默认CsrfFilter之后的过滤器链中。因此，当我们的过滤器被命中时，JWT 令牌(作为一个整体)已经被确认为 Spring Security 保存的正确值。

这是JwtCsrfValidatorFilter(它是私有的，因为它是我们 Spring Security 配置的内部类)：

```java
private class JwtCsrfValidatorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
      HttpServletRequest request, 
      HttpServletResponse response, 
      FilterChain filterChain) throws ServletException, IOException {
        // NOTE: A real implementation should have a nonce cache so the token cannot be reused
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

        if (
            // only care if it's a POST
            "POST".equals(request.getMethod()) &&
            // ignore if the request path is in our list
            Arrays.binarySearch(ignoreCsrfAntMatchers, request.getServletPath()) < 0 &&
            // make sure we have a token
            token != null
        ) {
            // CsrfFilter already made sure the token matched. 
            // Here, we'll make sure it's not expired
            try {
                Jwts.parser()
                    .setSigningKey(secret.getBytes("UTF-8"))
                    .parseClaimsJws(token.getToken());
            } catch (JwtException e) {
                // most likely an ExpiredJwtException, but this will handle any
                request.setAttribute("exception", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                RequestDispatcher dispatcher = request.getRequestDispatcher("expired-jwt");
                dispatcher.forward(request, response);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

看一下第 23 行。我们像以前一样解析 JWT。在这种情况下，如果抛出异常，请求将转发到expired-jwt模板。如果 JWT 验证通过，则处理将照常继续。

这将关闭使用 JWT 令牌存储库和验证器覆盖默认 Spring Security CSRF 令牌行为的循环。

如果启动应用程序，浏览至/jwt-csrf-form，等待 30 多秒并单击按钮，你将看到如下内容：

[![jwt_expired](https://www.baeldung.com/wp-content/uploads/2016/08/jwt_expired.png)](https://www.baeldung.com/wp-content/uploads/2016/08/jwt_expired.png)

## 7. JJWT 扩展功能

我们将结束我们的 JJWT 之旅，谈谈一些超出规范的功能。

### 7.1. 执行索赔

作为解析过程的一部分，JJWT 允许你指定所需的声明和这些声明应具有的值。如果你的 JWT 中有某些信息必须存在才能让你认为它们有效，这将非常方便。它避免了很多分支逻辑来手动验证声明。这是为示例项目的/parser-enforce端点提供服务的方法。

```java
@RequestMapping(value = "/parser-enforce", method = GET)
public JwtResponse parserEnforce(@RequestParam String jwt) 
  throws UnsupportedEncodingException {
    Jws<Claims> jws = Jwts.parser()
        .requireIssuer("Stormpath")
        .require("hasMotorcycle", true)
        .setSigningKeyResolver(secretService.getSigningKeyResolver())
        .parseClaimsJws(jwt);

    return new JwtResponse(jws);
}
```

第 5 行和第 6 行向你展示了已注册声明和自定义声明的语法。在此示例中，如果 iss 声明不存在或不具有以下值，则 JWT 将被视为无效：Stormpath。如果自定义 hasMotorcycle 声明不存在或不具有值，它也将无效：true。

让我们首先创建一个遵循快乐路径的 JWT：

```bash
http -v POST localhost:8080/dynamic-builder-specific 
  iss=Stormpath hasMotorcycle:=true sub=msilverman
POST /dynamic-builder-specific HTTP/1.1
Accept: application/json
...
{
    "hasMotorcycle": true,
    "iss": "Stormpath",
    "sub": "msilverman"
}

HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
...
{
    "jwt": 
      "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJoYXNNb3RvcmN5Y2xlIjp0cnVlLCJzdWIiOiJtc2lsdmVybWFuIn0.qrH-U6TLSVlHkZdYuqPRDtgKNr1RilFYQJtJbcgwhR0",
    "status": "SUCCESS"
}
```

现在，让我们验证 JWT：

```bash
http -v localhost:8080/parser-enforce?jwt=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJoYXNNb3RvcmN5Y2xlIjp0cnVlLCJzdWIiOiJtc2lsdmVybWFuIn0.qrH-U6TLSVlHkZdYuqPRDtgKNr1RilFYQJtJbcgwhR0
GET /parser-enforce?jwt=http 
  -v localhost:8080/parser-enforce?jwt=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJoYXNNb3RvcmN5Y2xlIjp0cnVlLCJzdWIiOiJtc2lsdmVybWFuIn0.qrH-U6TLSVlHkZdYuqPRDtgKNr1RilFYQJtJbcgwhR0 HTTP/1.1
Accept: /
...
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
...
{
    "jws": {
        "body": {
            "hasMotorcycle": true,
            "iss": "Stormpath",
            "sub": "msilverman"
        },
        "header": {
            "alg": "HS256"
        },
        "signature": "qrH-U6TLSVlHkZdYuqPRDtgKNr1RilFYQJtJbcgwhR0"
    },
    "status": "SUCCESS"
}
```

到目前为止，一切都很好。现在，这一次，让我们把 hasMotorcycle 排除在外：

```bash
http -v POST localhost:8080/dynamic-builder-specific iss=Stormpath sub=msilverman
```

这一次，如果我们尝试验证 JWT：

```bash
http -v localhost:8080/parser-enforce?jwt=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJzdWIiOiJtc2lsdmVybWFuIn0.YMONlFM1tNgttUYukDRsi9gKIocxdGAOLaJBymaQAWc
```

我们得到：

```javascript
GET /parser-enforce?jwt=http -v localhost:8080/parser-enforce?jwt=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJzdWIiOiJtc2lsdmVybWFuIn0.YMONlFM1tNgttUYukDRsi9gKIocxdGAOLaJBymaQAWc HTTP/1.1
Accept: /
...
HTTP/1.1 400 Bad Request
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: close
Content-Type: application/json;charset=UTF-8
...
{
    "exceptionType": "io.jsonwebtoken.MissingClaimException",
    "message": 
      "Expected hasMotorcycle claim to be: true, but was not present in the JWT claims.",
    "status": "ERROR"
}
```

这表明我们的 hasMotorcycle 声明是预期的，但丢失了。

让我们再举一个例子：

```bash
http -v POST localhost:8080/dynamic-builder-specific iss=Stormpath hasMotorcycle:=false sub=msilverman
```

这一次，要求的声明出现了，但它的值是错误的。让我们看看输出：

```bash
http -v localhost:8080/parser-enforce?jwt=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJoYXNNb3RvcmN5Y2xlIjpmYWxzZSwic3ViIjoibXNpbHZlcm1hbiJ9.8LBq2f0eINB34AzhVEgsln_KDo-IyeM8kc-dTzSCr0c
GET /parser-enforce?jwt=http 
  -v localhost:8080/parser-enforce?jwt=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJoYXNNb3RvcmN5Y2xlIjpmYWxzZSwic3ViIjoibXNpbHZlcm1hbiJ9.8LBq2f0eINB34AzhVEgsln_KDo-IyeM8kc-dTzSCr0c HTTP/1.1
Accept: /
...
HTTP/1.1 400 Bad Request
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: close
Content-Type: application/json;charset=UTF-8
...
{
    "exceptionType": "io.jsonwebtoken.IncorrectClaimException",
    "message": "Expected hasMotorcycle claim to be: true, but was: false.",
    "status": "ERROR"
}
```

这表明我们的 hasMotorcycle 声明存在，但具有未预期的值。

MissingClaimException和IncorrectClaimException是你在 JWT 中执行声明时的好帮手，也是只有 JJWT 库才具备的功能。

### 7.2. JWT 压缩

如果你对 JWT 有很多声明，它可能会变得很大——大到在某些浏览器中它可能不适合 GET url。

让我们做一个大的 JWT：

```bash
http -v POST localhost:8080/dynamic-builder-specific 
  iss=Stormpath hasMotorcycle:=true sub=msilverman the=quick brown=fox jumped=over lazy=dog 
  somewhere=over rainbow=way up=high and=the dreams=you dreamed=of
```

这是生成的 JWT：

```bash
eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTdG9ybXBhdGgiLCJoYXNNb3RvcmN5Y2xlIjp0cnVlLCJzdWIiOiJtc2lsdmVybWFuIiwidGhlIjoicXVpY2siLCJicm93biI6ImZveCIsImp1bXBlZCI6Im92ZXIiLCJsYXp5IjoiZG9nIiwic29tZXdoZXJlIjoib3ZlciIsInJhaW5ib3ciOiJ3YXkiLCJ1cCI6ImhpZ2giLCJhbmQiOiJ0aGUiLCJkcmVhbXMiOiJ5b3UiLCJkcmVhbWVkIjoib2YifQ.AHNJxSTiDw_bWNXcuh-LtPLvSjJqwDvOOUcmkk7CyZA
```

那个傻逼好大！现在，让我们使用相同的声明来访问一个略有不同的端点：

```bash
http -v POST localhost:8080/dynamic-builder-compress 
  iss=Stormpath hasMotorcycle:=true sub=msilverman the=quick brown=fox jumped=over lazy=dog 
  somewhere=over rainbow=way up=high and=the dreams=you dreamed=of
```

这一次，我们得到：

```bash
eyJhbGciOiJIUzI1NiIsImNhbGciOiJERUYifQ.eNpEzkESwjAIBdC7sO4JegdXnoC2tIk2oZLEGB3v7s84jjse_AFe5FOikc5ZLRycHQ3kOJ0Untu8C43ZigyUyoRYSH6_iwWOyGWHKd2Kn6_QZFojvOoDupRwyAIq4vDOzwYtugFJg1QnJv-5sY-TVjQqN7gcKJ3f-j8c-6J-baDFhEN_uGn58XtnpfcHAAD__w.3_wc-2skFBbInk0YAQ96yGWwr8r1xVdbHn-uGPTFuFE
```

短了 62 个字符！下面是用于生成 JWT 的方法的代码：

```java
@RequestMapping(value = "/dynamic-builder-compress", method = POST)
public JwtResponse dynamicBuildercompress(@RequestBody Map<String, Object> claims) 
  throws UnsupportedEncodingException {
    String jws =  Jwts.builder()
        .setClaims(claims)
        .compressWith(CompressionCodecs.DEFLATE)
        .signWith(
            SignatureAlgorithm.HS256,
            secretService.getHS256SecretBytes()
        )
        .compact();
    return new JwtResponse(jws);
}
```

请注意，在第 6 行，我们指定了要使用的压缩算法。这里的所有都是它的。

解析压缩的 JWT 怎么样？JJWT 库自动检测压缩并使用相同的算法解压缩：

```javascript
GET /parser?jwt=eyJhbGciOiJIUzI1NiIsImNhbGciOiJERUYifQ.eNpEzkESwjAIBdC7sO4JegdXnoC2tIk2oZLEGB3v7s84jjse_AFe5FOikc5ZLRycHQ3kOJ0Untu8C43ZigyUyoRYSH6_iwWOyGWHKd2Kn6_QZFojvOoDupRwyAIq4vDOzwYtugFJg1QnJv-5sY-TVjQqN7gcKJ3f-j8c-6J-baDFhEN_uGn58XtnpfcHAAD__w.3_wc-2skFBbInk0YAQ96yGWwr8r1xVdbHn-uGPTFuFE HTTP/1.1
Accept: /
...
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
...
{
    "claims": {
        "body": {
            "and": "the",
            "brown": "fox",
            "dreamed": "of",
            "dreams": "you",
            "hasMotorcycle": true,
            "iss": "Stormpath",
            "jumped": "over",
            "lazy": "dog",
            "rainbow": "way",
            "somewhere": "over",
            "sub": "msilverman",
            "the": "quick",
            "up": "high"
        },
        "header": {
            "alg": "HS256",
            "calg": "DEF"
        },
        "signature": "3_wc-2skFBbInk0YAQ96yGWwr8r1xVdbHn-uGPTFuFE"
    },
    "status": "SUCCESS"
}
```

注意标头中的calg声明。这被自动编码到 JWT 中，并向解析器提供有关使用何种算法进行解压缩的提示。

注意：JWE 规范确实支持压缩。在即将发布的 JJWT 库版本中，我们将支持 JWE 和压缩的 JWE。我们将继续支持其他类型的 JWT 中的压缩，即使未指定。

## 8.Java开发人员的代币工具

虽然本文的核心重点不是Spring Boot或 Spring Security，但使用这两种技术可以轻松演示本文中讨论的所有功能。你应该能够启动服务器并开始使用我们讨论过的各种端点。只需点击：

```bash
http http://localhost:8080
```

[Stormpath](https://stormpath.com/)也很高兴为Java社区带来许多开源开发工具。这些包括：

### 8.1. JJWT(我们一直在谈论什么)

[JJWT](https://github.com/jwtk/jjwt)是一个易于使用的[工具，供开发人员使用Java创建和验证 JWT](https://stormpath.com/blog/jjwt-how-it-works-why)。与 Stormpath 支持的许多库一样，JJWT 是完全免费和开源的(Apache License，Version 2.0)，所以每个人都可以看到它做了什么以及它是如何做的。不要犹豫，报告任何问题，提出改进建议，甚至提交一些代码！

### 8.2. jsonwebtoken.io 和 java.jsonwebtoken.io

[jsonwebtoken.io](http://jsonwebtoken.io/)是我们创建的开发人员工具，可以轻松解码 JWT。只需将现有的 JWT 粘贴到适当的字段中，即可对其标头、有效负载和签名进行解码。[jsonwebtoken.io 由nJWT](https://github.com/jwtk/njwt)提供支持，nJWT 是面向 Node.js 开发人员的最干净的免费和开源(Apache 许可证，版本 2.0)JWT 库。你还可以在该网站上查看为各种语言生成的代码。该网站本身是开源的，可以[在此处](https://github.com/stormpath/jsonwebtoken.io)找到。

[java.jsonwebtoken.io](http://java.jsonwebtoken.io/)专门用于 JJWT 库。你可以在右上角的框中更改标头和有效负载，在左上角的框中查看 JJWT 生成的 JWT，并在下方的框中查看构建器和解析器Java代码的示例。[该网站本身是开源的，可以在此处](https://github.com/stormpath/JJWTsite)找到。

### 8.3. JWT 检查员

作为新手，[JWT Inspector](https://www.jwtinspector.io/)是一个开源的 Chrome 扩展，它允许开发人员直接在浏览器中检查和调试 JWT。JWT Inspector 将在你的网站上发现 JWT(在 cookie、本地/会话存储和标头中)，并使它们可以通过你的导航栏和 DevTools 面板轻松访问。

## 9. JWT 这个下来！

JWT 为普通令牌添加了一些智能。以加密方式签名和验证、建立到期时间并将其他信息编码到 JWT 中的能力为真正的无状态会话管理奠定了基础。这对扩展应用程序的能力有很大影响。

在 Stormpath，我们将 JWT 用于 OAuth2 令牌、CSRF 令牌和微服务之间的断言，以及其他用途。

一旦你开始使用 JWT，你可能永远不会回到过去的愚蠢令牌。
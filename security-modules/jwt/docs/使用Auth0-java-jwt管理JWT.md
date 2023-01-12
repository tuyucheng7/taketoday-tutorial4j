## 1. 概述

[JWT](https://www.rfc-editor.org/rfc/rfc7519)(JSON Web Token)是一种标准，它定义了一种紧凑且安全的方式来传输数据以及双方之间的签名。JWT 中的有效负载是一个声明某些声明的 JSON 对象。此有效负载可以很容易地被验证者验证和信任，因为它是经过数字签名的。JWT 可以使用密钥或公钥/私钥对进行签名。

在本教程中，我们将学习如何使用[Auth0 JWT Java 库](https://github.com/auth0/java-jwt)[创建和解码 JWT](https://www.baeldung.com/java-jwt-token-decode)。

## 2. JWT 的结构

JWT 基本上由三部分组成：

-   标头
-   有效载荷
-   签名

这些部分中的每一个都代表一个[Base64 编码的](https://www.baeldung.com/java-base64-encode-and-decode)字符串，以点 ('.') 作为分隔符分隔。

### 2.1. JWT 标头

JWT Header 通常由两部分组成：令牌类型，即“JWT”，以及用于对 JWT 进行签名的签名算法。

Auth0 Java JWT 库提供了各种算法实现来签署 JWT，如 HMAC、RSA 和 ECDSA。

让我们看一下示例 JWT 标头：

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

然后将上面的标头对象进行 Base64 编码以形成 JWT 的第一部分。

### 2.2. 智威汤逊负载

JWT 负载包含一组声明。声明基本上是关于一个实体的声明以及一些额外的数据。

索赔分为三种类型：

-   已注册 – 这些是一组有用的预定义声明，建议但不是强制性的。这些声明名称只有三个字符长，以保持 JWT 紧凑。一些已注册的声明包括iss(发行者)、exp(到期时间)和sub(主题)等。
-   公共——这些可以由使用 JWT 的人随意定义。
-   私人 - 我们可以使用这些声明来创建自定义声明。

让我们看一下 JWT Payload 示例：

```json
{
  "sub": "Baeldung Details",
  "nbf": 1669463994,
  "iss": "Baeldung",
  "exp": 1669463998,
  "userId": "1234",
  "iat": 1669463993,
  "jti": "b44bd6c6-f128-4415-8458-6d8b4bc98e4a"
}
```

在这里，我们可以看到 Payload 包含一个私有声明userId，表示登录用户的 ID。此外，我们还可以找到一些其他有用的受限声明，这些声明定义了有关 JWT 的其他详细信息。

JWT Payload 然后进行 Base64 编码以形成 JWT 的第二部分。

### 2.3. JWT 签名

最后，当我们使用带有密钥的签名算法对编码的标头和编码的有效负载进行签名时，会生成 JWT 签名。然后可以使用签名来验证 JWT 中的数据是否有效。

请务必注意，任何有权访问 JWT 的人都可以轻松解码和查看其内容。签名令牌可以验证其中包含的声明的完整性。如果令牌是使用公钥/私钥对签名的，则签名还证明只有持有私钥的一方才是签名者。

最后，结合所有三个部分，我们得到我们的 JWT：

```json
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJCYWVsZHVuZyBEZXRhaWxzIiwibmJmIjoxNjY5NDYzOTk0LCJpc3MiOiJCYWVsZHVuZyIsImV4cCI6MTY2OTQ2Mzk5OCwidXNlcklkIjoiMTIzNCIsImlhdCI6MTY2OTQ2Mzk5MywianRpIjoiYjQ0YmQ2YzYtZjEyOC00NDE1LTg0NTgtNmQ4YjRiYzk4ZTRhIn0.14jm1FVPXFDJCUBARDTQkUErMmUTqdt5uMTGW6hDuV0
```

接下来，让我们看看如何使用 Auth0 Java JWT 库创建和管理 JWT。

## 3.使用Auth0

Auth0 提供了一个易于使用的 Java 库来创建和管理 JWT。

### 3.1. 依赖关系

首先，我们将 Auth0 Java JWT 库的[Maven 依赖](https://search.maven.org/search?q=g:com.auth0 AND a:java-jwt)项添加到项目的pom.xml文件中：

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.2.1</version>
</dependency>
```

### 3.2. 配置算法和验证器

我们首先创建一个Algorithm类的实例。在本教程中，我们将使用 HMAC256 算法对我们的 JWT 进行签名：

```java
Algorithm algorithm = Algorithm.HMAC256("baeldung");
```

在这里，我们使用密钥初始化算法的实例。我们稍后将在令牌的创建和验证期间使用它。

此外，让我们初始化一个JWTVerifier实例，我们将使用它来验证创建的令牌：

```java
JWTVerifier verifier = JWT.require(algorithm)
  .withIssuer("Baeldung")
  .build();
```

为了初始化验证器，我们使用JWT.require(Algorithm)方法。此方法返回一个Verification实例，然后我们可以使用它来构建JWTVerifier实例。

我们现在准备创建我们的 JWT。

### 3.3. 创建智威汤逊

要创建 JWT，我们使用JWT.create()方法。该方法返回JWTCreator.Builder类的一个实例。我们将使用此Builder类通过使用Algorithm实例签署声明来构建 JWT 令牌：

```java
String jwtToken = JWT.create()
  .withIssuer("Baeldung")
  .withSubject("Baeldung Details")
  .withClaim("userId", "1234")
  .withIssuedAt(new Date())
  .withExpiresAt(new Date(System.currentTimeMillis() + 5000L))
  .withJWTId(UUID.randomUUID()
    .toString())
  .withNotBefore(new Date(System.currentTimeMillis() + 1000L))
  .sign(algorithm);
```

上面的代码片段返回一个 JWT：

```json
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJCYWVsZHVuZyBEZXRhaWxzIiwibmJmIjoxNjY5NDYzOTk0LCJpc3MiOiJCYWVsZHVuZyIsImV4cCI6MTY2OTQ2Mzk5OCwidXNlcklkIjoiMTIzNCIsImlhdCI6MTY2OTQ2Mzk5MywianRpIjoiYjQ0YmQ2YzYtZjEyOC00NDE1LTg0NTgtNmQ4YjRiYzk4ZTRhIn0.14jm1FVPXFDJCUBARDTQkUErMmUTqdt5uMTGW6hDuV0
```

让我们讨论上面使用的一些JWTCreator.Builder类方法来设置一些声明：

-   withIssuer() – 标识创建令牌并对其签名的一方
-   withSubject() – 标识 JWT 的主题
-   withIssuedAt() – 标识创建 JWT 的时间；我们可以用它来确定 JWT 的年龄
-   withExpiresAt() – 标识 JWT 的到期时间
-   withJWTId() – JWT 的唯一标识符
-   withNotBefore() – 标识不应接受 JWT 进行处理的时间
-   withClaim() – 用于设置任何自定义声明

### 3.4. 验证 JWT

此外，为了验证 JWT，我们使用之前初始化的 JWTVerifier 中的JWTVerifier.verify ( String)方法。如果 JWT 有效，该方法将解析 JWT 并返回DecodedJWT的实例。

DecodedJWT实例提供了各种方便的方法，我们可以使用这些方法来获取 JWT 中包含的声明。如果 JWT 无效，该方法将抛出JWTVerificationException。

让我们解码之前创建的 JWT：

```java
try {
    DecodedJWT decodedJWT = verifier.verify(jwtToken);
} catch (JWTVerificationException e) {
    System.out.println(e.getMessage());
}
```

一旦我们获得了DecodedJWT实例的实例，我们就可以使用它的各种 getter 方法来获取声明。

例如，要获取自定义声明，我们使用DecodedJWT.getClaim(String)方法。此方法返回Claim的一个实例：

```java
Claim claim = decodedJWT.getClaim("userId");
```

在这里，我们正在获取我们之前在创建 JWT 时设置的自定义声明userId 。我们现在可以通过调用Claim.asString()或任何其他基于声明数据类型的可用方法来获取我们的声明值：

```java
String userId = claim.asString();
```

上面的代码片段返回我们自定义声明的字符串“ 1234” 。

除了 Auth0 Java JWT 库之外，Auth0 还提供了一个直观的基于 Web 的[JWT Debugger](https://jwt.io/)来帮助我们解码和验证 JWT。

## 4。总结

在本文中，我们了解了 JWT 的结构以及如何将其用于身份验证。

然后，我们使用 Auth0 Java JWT 库使用令牌的签名、算法和密钥创建令牌并验证其完整性。
## 1. 概述

[JWT(JSON Web Token)](https://www.rfc-editor.org/rfc/rfc7519)基本上是一个 JSON 对象，用于安全地通过网络传输信息。此信息可以被验证和信任，因为它是经过数字签名的。

[在本教程中，我们将首先了解验证 JWT](https://www.baeldung.com/java-auth0-jwt)和[解码 JWT](https://www.baeldung.com/java-jwt-token-decode)之间的区别。然后，我们将学习如何检查 JWT 的到期时间而不在 Java 中抛出任何异常。

## 2. 验证和解码 JWT 的区别

在我们开始研究如何检查 JWT 的到期时间之前，让我们先了解一些基础知识。

正如我们所知，紧凑形式的 JWT 是一个[Base64 编码的](https://www.baeldung.com/java-base64-encode-and-decode)字符串，包含三个部分：标头、有效负载和签名。任何访问 JWT 的人都可以轻松解码并查看其内容。因此，要信任令牌，我们必须验证 JWT 中包含的签名。

有多种 Java JWT 库可用于创建和管理 JWT。我们将为我们的代码示例使用[Auth0 JWT Java 库](https://github.com/auth0/java-jwt)。它是一个易于使用的库，用于创建和管理 JWT。

### 2.1. 依赖关系

首先，我们将 Auth0 Java JWT 库的[Maven 依赖](https://search.maven.org/search?q=g:com.auth0 AND a:java-jwt)项添加到项目的pom.xml文件中：

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.2.1</version>
</dependency>
```

接下来，让我们了解解码和验证 JWT 之间的区别。

### 2.2. 解码 JWT

我们可以通过简单的 Base64 解码其各个部分来解码 JWT。解码 JWT 会返回解码后的有效负载，而无需验证 JWT 签名是否有效。此操作不建议用于任何不受信任的消息，仅用于查看 JWT 内容。

要解码 JWT，我们使用JWT.decode(String)方法。 此方法解析 JWT 并返回DecodedJWT的实例。

DecodedJWT实例提供了各种方便的方法，我们可以使用它们来获取 JWT 中包含的数据。如果 JWT 不是有效的 Base64 编码字符串，该方法将抛出JWTDecodeException。

让我们看一下解码 JWT 的代码：

```java
try {
    DecodedJWT decodedJWT = JWT.decode(jwtString);
    return decodedJWT;
} catch (JWTDecodeException e) {
    // ...
}
```

一旦我们获得了DecodedJWT实例的实例，我们就可以使用它的各种 getter 方法来获取解码数据。

例如，要获取令牌过期时间，我们使用DecodedJWT.getExpiresAt()方法。此方法返回包含令牌到期时间的java.util.Date实例：

```java
Date expiresAt = decodedJWT.getExpiresAt();
```

接下来我们看一下JWT的验证操作。

### 2.3. 验证 JWT

验证 JWT 可确保包含的签名有效。可选地，它还会检查到期时间、无效前时间、发行者、受众或任何其他声明(如果 JWT 包含任何声明)。

为了验证 JWT，我们使用JWTVerifier.verify(String)方法。如果签名有效，验证操作还会返回一个DecodedJWT实例。只有当签名和所有声明都有效时，它才会返回解码后的 JWT。如果签名无效或任何声明验证失败，它会抛出JWTVerificationException。

让我们检查代码以验证 JWT：

```java
try {
    DecodedJWT decodedJWT = verifier.verify(jwtString);
} catch (JWTVerificationException e) {
    // ...
}
```

从上面的代码片段可以清楚地看出，如果 JWT 无效，则verify()方法会抛出异常。由于该方法在验证后还会对token进行解码，因此提供了一种更加安全可靠的token解码方式。另一方面，decode() 方法只是对提供的 JWT 令牌进行解码。因此，为了验证令牌的到期时间而不抛出任何异常，我们使用JWT.decode()方法。

## 3. 检查 JWT 过期时间

为了简单地读取JWT中包含的数据，我们可以解码JWT并解析数据。让我们看一下检查 JWT 是否已过期的 Java 代码：

```java
boolean isJWTExpired(DecodedJWT decodedJWT) {
    Date expiresAt = decodedJWT.getExpiresAt();
    return expiresAt.before(new Date());
}
```

如前所述，我们使用DecodedJWT.getExpiresAt()方法来获取 JWT 的到期时间。然后我们将过期时间与当前时间进行匹配，以检查令牌是否已过期。

JWT.decode ()方法和JWTVerifier.verify()方法都返回DecodedJWT的一个实例。唯一的区别是verify()方法还会检查签名的有效性，如果无效则返回异常。因此，我们必须只对可信消息使用decode()方法。对于任何不受信任的消息，我们应该始终使用verify()方法，以确保这些 JWT 中的有效签名和任何其他声明。

## 4。总结

在本文中，我们首先了解了 JWT 解码和 JWT 验证操作之间的区别。

然后我们查看了如何使用解码操作来检查 JWT 的到期时间而不抛出任何异常。
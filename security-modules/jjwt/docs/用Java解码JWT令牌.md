## 1. 概述

[JSON Web 令牌](https://tools.ietf.org/html/rfc7519)(JWT)通常用于 REST API 安全性。尽管令牌可以被[Spring Security OAuth](https://www.baeldung.com/spring-security-5-oauth2-login)等框架解析，但我们可能希望在自己的代码中处理令牌。

在本教程中，我们将[解码并验证 JWT 的完整性](https://jwt.io/)。

## 2. JWT 的结构

[首先，让我们了解JWT](https://datatracker.ietf.org/doc/html/rfc7519#section-3)的结构：

-   标头
-   有效负载(通常称为主体)
-   签名

签名是可选的。有效的 JWT 可以只包含标头和负载部分。但是，我们使用签名部分来验证 header 和 payload 的内容以 进行[安全授权](https://www.baeldung.com/java-json-web-tokens-jjwt)。

部分表示为由句点 ('.') 分隔符分隔的 base64url 编码字符串。按照设计，任何人都可以解码 JWT 并读取标头和有效负载部分的内容。但是我们需要访问用于创建签名的密钥以验证令牌的完整性。

最常见的是，JWT 包含用户的“声明”。这些表示有关用户的数据，API 可使用这些数据授予权限或跟踪提供令牌的用户。解码令牌允许应用程序使用数据，验证允许应用程序相信 JWT 是由可信源生成的。

让我们看看如何在Java中解码和验证令牌。

## 3. 解码 JWT

我们可以使用内置的Java函数解码令牌。

首先，让我们将令牌分成几个部分：

```java
String[] chunks = token.split(".");
```

我们应该注意到传递给 String.split的正则表达式使用了转义的“.”。要避免“.”的字符 意思是“任何字符”。

我们的块数组现在应该有两个或三个元素对应于 JWT 的部分。

接下来，让我们使用 base64url 解码器解码标头和有效负载部分：

```java
Base64.Decoder decoder = Base64.getUrlDecoder();

String header = new String(decoder.decode(chunks[0]));
String payload = new String(decoder.decode(chunks[1]));
```

让我们用 JWT 运行这段代码(我们可以[在线解码](https://jwt.io/#encoded-jwt)以比较结果)：

```java
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkJhZWxkdW5nIFVzZXIiLCJpYXQiOjE1MTYyMzkwMjJ9.qH7Zj_m3kY69kxhaQXTa-ivIpytKXXjZc1ZSmapZnGE
```

输出将为我们提供任何有效负载的解码标头：

```json
{"alg":"HS256","typ":"JWT"}{"sub":"1234567890","name":"Baeldung User","iat":1516239022}
```

如果在 JWT 中只定义了标头和有效负载部分，我们就完成了并成功解码了信息。

## 4.验证JWT

接下来，我们可以使用签名部分验证标头和有效负载的完整性，以确保它们没有被更改。

### 4.1. 依赖关系

为了验证，我们可以将[jjwt](https://search.maven.org/artifact/io.jsonwebtoken/jjwt)添加到我们的pom.xml中：

```java
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.7.0</version>
</dependency>
```

我们应该注意，我们需要这个库的版本从0.7.0开始。

### 4.2. 配置签名算法和密钥规范

要开始验证有效负载和标头，我们需要最初用于对令牌进行签名的签名算法和密钥：

```java
SignatureAlgorithm sa = HS256;
SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), sa.getJcaName());
```

在此示例中，我们已将签名算法硬编码为HS256。但是，我们可以解码标头的 JSON 并读取alg字段以获取此值。

我们还应注意，变量secretKey 是密钥的字符串表示形式。我们可以通过其配置或通过发布 JWT 的服务公开的 REST API 将其提供给我们的应用程序。

### 4.3. 执行验证

现在我们有了签名算法和密钥，我们就可以开始进行验证了。

让我们将 header 和 payload 重新组合成一个未签名的 JWT，用 '.' 连接它们。分隔符：

```java
String tokenWithoutSignature = chunks[0] + "." + chunks[1];
String signature = chunks[2];
```

现在我们有了未签名的令牌和提供的签名。我们可以使用库来验证它：

```java
DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(sa, secretKeySpec);

if (!validator.isValid(tokenWithoutSignature, signature)) {
    throw new Exception("Could not verify JWT token integrity!");
}
```

让我们分解一下。

首先，我们使用选定的算法和秘密创建一个验证器。然后我们向它提供未签名的令牌数据和提供的签名。

然后验证器生成一个新的签名并将其与提供的签名进行比较。如果它们相等，我们就验证了标头和负载的完整性。

## 5.总结

在本文中，我们了解了 JWT 的结构以及如何将其解码为 JSON。

然后我们使用一个库来验证令牌的完整性，使用它的签名、算法和密钥。
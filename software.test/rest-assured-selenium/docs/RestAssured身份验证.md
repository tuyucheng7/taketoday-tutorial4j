## 一、概述

在本教程中，我们将分析如何使用[REST Assured](https://www.baeldung.com/rest-assured-tutorial)进行身份验证，以正确测试和验证安全 API。

**该工具支持多种身份验证方案**：

-   基本认证
-   摘要式身份验证
-   表单认证
-   OAuth 1 和 OAuth 2

我们将看到每个示例。

## 2. 使用基本认证

[基本身份验证方案](https://tools.ietf.org/html/rfc7617)要求消费者发送以*Base64*编码的用户 ID 和密码。

REST Assured 提供了一种简单的方法来配置请求所需的凭据：

```java
given().auth()
  .basic("user1", "user1Pass")
  .when()
  .get("http://localhost:8080/spring-security-rest-basic-auth/api/foos/1")
  .then()
  .assertThat()
  .statusCode(HttpStatus.OK.value());复制
```

### 2.1。抢先认证

正如我们在[之前一篇关于 Spring Security 身份验证的文章中](https://www.baeldung.com/spring-security-basic-authentication#usage)所见，服务器可能会使用[质询-响应机制](https://tools.ietf.org/html/rfc2617#section-1.2)来明确指示消费者何时需要进行身份验证才能访问资源。

**默认情况下，REST Assured 会等待服务器质询，然后再发送凭据。**

这在某些情况下可能会很麻烦，例如，服务器被配置为检索登录表单而不是质询响应。

出于这个原因，库提供了我们可以使用的*抢先* 指令：

```java
given().auth()
  .preemptive()
  .basic("user1", "user1Pass")
  .when()
  // ...复制
```

有了这个，REST Assured 将发送凭据而无需等待*未经授权*的响应。

我们几乎从来没有对测试服务器的挑战能力感兴趣。**因此，我们通常可以添加此命令以避免复杂性和发出额外请求的开销。**

## 3. 使用摘要认证

尽管这也被认为是一种[“弱”的身份验证方法](https://tools.ietf.org/html/rfc2617#section-4.4)，但使用[摘要式身份验证](https://tools.ietf.org/html/rfc7616)代表了优于基本协议的优势。

这是因为该方案避免以明文形式发送密码。

**尽管存在这种差异，但使用 REST Assured 实现这种形式的身份验证与我们在上一节中遵循的非常相似：**

```java
given().auth()
  .digest("user1", "user1Pass")
  .when()
  // ...复制
```

请注意，目前，该库仅支持此方案的质询身份验证，**因此我们不能像之前那样使用 *preemptive()*。**

## 4. 使用表单认证

许多服务为用户提供了一个 HTML 表单，通过在字段中填写他们的凭据来进行身份验证。

当用户提交表单时，浏览器会执行一个带有信息的 POST 请求。

*通常，表单使用其action*属性指示它将调用的端点，并且每个*输入*字段对应于请求中发送的表单参数。

如果登录表单足够简单并且遵循这些规则，那么我们可以依靠 REST Assured 为我们找出这些值：

```java
given().auth()
  .form("user1", "user1Pass")
  .when()
  // ...复制
```

无论如何，这不是最佳方法，因为 REST Assured 需要执行附加请求并解析 HTML 响应以查找字段。

我们还必须记住，该过程仍然可能失败，例如，如果网页很复杂，或者服务配置了一个未包含在*操作*属性中的上下文路径。

**因此，更好的解决方案是自己提供配置，明确指出三个必填字段：**

```java
given().auth()
  .form(
    "user1",
    "user1Pass",
    new FormAuthConfig("/perform_login", "username", "password"))
  // ...
复制
```

除了这些基本配置之外，REST Assured 还提供以下功能：

-   检测或指示网页中的 CSRF 令牌字段
-   在请求中使用额外的表单字段
-   认证过程的日志信息

## 5. OAuth 支持

OAuth 在技术上是一个*授权*框架，它没有定义任何验证用户的机制。

尽管如此，它仍可用作构建身份验证和身份协议的基础，就像[OpenID Connect](https://www.baeldung.com/spring-security-openid-connect)的情况一样。

### 5.1。OAuth 2.0

**REST Assured 允许配置 OAuth 2.0 访问令牌以请求安全资源：**

```java
given().auth()
  .oauth2(accessToken)
  .when()
  .// ...复制
```

该库在获取访问令牌方面没有提供任何帮助，因此我们必须自己弄清楚如何做到这一点。

对于客户端凭据和密码流，这是一项简单的任务，因为只需提供相应的凭据即可获得令牌。

另一方面，自动化授权代码流程可能并不容易，我们可能还需要其他工具的帮助。

要正确理解此流程以及[获取访问令牌所需的内容，我们可以查看有关该主题的这篇精彩文章。](https://www.baeldung.com/spring-security-oauth-authorization-code-flow)

### 5.2. OAuth 1.0a

在 OAuth 1.0a 的情况下，**REST Assured 提供了一种方法，该方法接收消费者密钥、秘密、访问令牌和令牌秘密**以访问受保护的资源：

```java
given().accept(ContentType.JSON)
  .auth()
  .oauth(consumerKey, consumerSecret, accessToken, tokenSecret)
  // ...复制
```

该协议需要用户输入，因此获取最后两个字段并非易事。

请注意，如果我们使用的是 2.5.0 之前版本的 OAuth 2.0 功能，或者我们正在使用 OAuth 1.0a 功能，我们需要在我们的项目中添加*scribejava-apis依赖项。*

## 六，结论

在本教程中，我们了解了如何使用 REST Assured 进行身份验证以访问受保护的 API。

该库简化了我们实施的几乎任何方案的身份验证过程。
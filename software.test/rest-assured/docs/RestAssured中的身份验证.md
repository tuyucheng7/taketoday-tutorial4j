## 1. 概述

在本教程中，我们将分析如何使用[REST Assured](RestAssured指南.md)进行身份验证，以正确测试和验证受保护的API。

**该工具支持多种身份验证方案**：

-   基本身份验证
-   摘要式身份验证
-   表单身份验证
-   OAuth 1和OAuth 2

## 2. 使用基本身份验证

[基本身份验证方案](https://tools.ietf.org/html/rfc7617)要求消费者发送用户ID和以Base64编码的密码。

REST Assured提供了一种简单的方法来配置请求所需的凭据：

```java
given().auth()
    .basic("user1", "user1Pass")
    .when()
    .get("http://localhost:8080/spring-security-web-rest-basic-auth/api/foos/1")
    .then()
    .assertThat()
    .statusCode(HttpStatus.OK.value());
```

### 2.1 抢占式身份验证

正如我们在之前[关于Spring Security身份验证](https://www.baeldung.com/spring-security-basic-authentication#usage)的帖子中看到的那样，服务器可能会使用[质询-响应机制](https://tools.ietf.org/html/rfc2617#section-1.2)来明确指示消费者何时需要进行身份验证才能访问资源。

**默认情况下，REST Assured在发送凭据之前等待服务器质询**。

这在某些情况下可能会很麻烦。例如，服务器配置为检索登录表单而不是质询响应。

出于这个原因，该库提供了我们可以使用的preemptive()方法：

```java
given().auth()
    .preemptive()
    .basic("user1", "user1Pass")
    .when()
    // ...
```

完成此操作后，REST Assured将发送凭据而无需等待Unauthorized响应。

我们几乎从不对测试服务器的挑战能力感兴趣。**因此，我们通常可以添加这个命令来避免复杂性和额外请求的开销**。

## 3. 使用摘要认证

尽管这也被认为是一种[“弱”身份验证方法](https://tools.ietf.org/html/rfc2617#section-4.4)，但使用[摘要式身份验证](https://tools.ietf.org/html/rfc7616)代表了优于基本协议的优势。

这是因为该方案避免以明文形式发送密码。

**尽管存在这种差异，但使用REST Assured实现这种形式的身份验证与我们在上一节中遵循的非常相似**：

```java
given().auth()
    .digest("user1", "user1Pass")
    .when()
    // ...
```

请注意，目前该库仅支持此方案的质询身份验证，**因此我们不能像之前那样使用preemptive()**。

## 4. 使用表单身份验证

许多服务提供HTML表单，供用户通过在字段中填写凭据来进行身份验证。

当用户提交表单时，浏览器使用信息执行POST请求。

通常，表单指示它将使用其action属性调用的端点，并且每个输入字段对应于请求中发送的表单参数。

如果登录表单足够简单并遵循这些规则，那么我们可以依靠REST Assured为我们找出这些值：

```java
given().auth()
    .form("user1", "user1Pass")
    .when()
    // ...
```

无论如何，这不是最佳方法，因为REST Assured需要执行额外的请求并解析HTML响应以查找字段。

我们还必须记住，该过程仍然可能失败，例如，如果网页很复杂，或者如果服务配置了未包含在action属性中的上下文路径。

**因此，更好的方案是自己提供配置，显式指明必填的三个字段**：

```java
given().auth()
    .form("user1", "user1Pass", new FormAuthConfig("/perform_login", "username", "password"))
    // ...
```

除了这些基本配置之外，REST Assured还附带了以下功能：

-   检测或指示网页中的CSRF token字段
-   在请求中使用额外的表单域
-   记录有关身份验证过程的信息

## 5. OAuth支持

OAuth在技术上是一个授权框架，它没有定义任何用于对用户进行身份验证的机制。

尽管如此，它仍然可以用作构建身份验证和身份协议的基础，例如[OpenID Connect](https://www.baeldung.com/spring-security-openid-connect)。

### 5.1 OAuth 2.0

**REST Assured允许配置OAuth 2.0访问令牌以请求受保护的资源**：

```java
given().auth()
    .oauth2(accessToken)
    .when()
    .// ...
```

该库在获取访问令牌没有提供任何帮助，因此我们必须自己弄清楚如何执行此操作。

对于客户端凭据和密码流程，这是一项简单的任务，因为令牌是通过提供相应的凭据来获取的。

另一方面，自动化授权代码流程可能并不那么容易，我们可能还需要其他工具的帮助。

要正确理解此流程以及获取访问令牌所需的条件，我们可以查看有关该主题的[这篇](https://www.baeldung.com/spring-security-oauth-authorization-code-flow)文章。

### 5.2 OAuth 1.0a

对于OAuth 1.0a，**REST Assured提供了一种接收Consumer Key、Secret、Access Token和Token Secret以访问受保护资源的方法**：

```java
given().accept(ContentType.JSON)
    .auth()
    .oauth(consumerKey, consumerSecret, accessToken, tokenSecret)
    // ...
```

该协议需要用户输入，因此获取最后两个字段并非易事。

请注意，如果我们在2.5.0之前的版本中使用OAuth 2.0功能，或者如果我们正在使用OAuth 1.0a功能，则需要在我们的项目中添加scribejava-apis依赖项。

## 6. 总结

在本教程中，我们学习了如何使用REST Assured进行身份验证以访问受保护的API。

该库简化了我们实施的几乎所有方案的身份验证过程。
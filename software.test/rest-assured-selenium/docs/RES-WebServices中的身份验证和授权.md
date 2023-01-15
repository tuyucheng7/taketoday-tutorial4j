### ***什么是身份验证？以及授权如何在 REST Web 服务中工作？***

身份验证是证明您是您想要成为的人的过程。

*例如，在登录您的电子邮件帐户时，您通过提供**用户名**和**密码来证明****您就是您**。如果您**有用户名**和**密码**，那么您就是您自称的人。这就是身份验证的含义。*

在 REST API 身份验证的上下文中，使用 HTTP 请求进行。

***注意：不仅仅是 REST API，任何通过 HTTP 协议工作的应用程序的身份验证都使用 HTTP 请求进行。***

### ***基本认证流程***

以电子邮件登录为例，我们知道为了验证我们自己，我们必须提供用户名和密码。在使用用户名和密码的非常基本的身份验证流程中，我们也将在 REST API 调用中执行相同的操作。但是我们如何在 REST 请求中发送用户名和密码呢？

REST 请求可以有一个称为***Authorization Header 的特殊标头，*** 该标头可以包含某种形式的凭据（*用户名和密码*）。一旦收到带有授权标头的请求，服务器就可以验证凭据并允许您访问私有资源。

***注意：我希望从之前的教程中你能够理解资源的含义。如果没有，请阅读本教程：*** [***休息架构元素。***](https://toolsqa.com/rest-assured/rest-architectural-elements/) ***私有资源不是每个人都可以访问的。您需要对自己进行身份验证才能访问私有资源。例如电子邮件收件箱，您必须登录才能查看电子邮件。***

让我们看一个例子，我们创建了一个需要有效用户名和密码才能访问资源的 API。

***端点：http://restapi.demoqa.com/authentication/CheckForAuthentication***

在下面的代码中，我们将尝试点击 URL 并查看我们得到的响应是什么。

```java
@Test
public void AuthenticationBasics()
{
	RestAssured.baseURI = "https://restapi.demoqa.com/authentication/CheckForAuthentication";
	RequestSpecification request = RestAssured.given();

	Response response = request.get();
	System.out.println("Status code: " + response.getStatusCode());
	System.out.println("Status message " + response.body().asString());
}
```

在上面的代码中，我们只是向端点发出***HTTP GET***请求。在这段代码中，我们没有添加任何***Authorization***标头。所以预期的行为是我们会得到授权错误。如果您运行此测试，您将获得以下输出。

```java
Status code: 401
Status message: 
{
    "StatusID": "FAULT_USER_INVALID_USER_PASSWORD",
    "Status": "Invalid or expired Authentication key provided"
}
```

输出清楚地表明我们有***“提供的身份验证密钥无效或过期”*** 错误。这意味着要么没有身份验证信息，要么提供的信息无效。最终，服务器拒绝我们的请求并返回错误响应。

***注意：**特别注意返回的**状态码**。在 的情况下**，身份验证**失败服务器应响应状态码 401 **Unauthorized。***

尝试使用浏览器访问该 URL。您应该得到一个用户名和密码提示。下图显示了当您从浏览器中点击此 URL 时应该得到什么。

![REST WebServices 中的身份验证和授权](https://toolsqa.com/gallery/Rest%20Assured/1.Authentication%20and%20Authorization%20in%20REST%20WebServices.png)

在本教程中，我们不会讨论如何在 Request 标头中传递 Authentication 信息。这里我们只关注 ***Authentication***和***Authorization***的定义。在下一组教程中，我们将看到不同的***身份验证模型，***它们将解决上述问题。

### ***什么是授权？以及授权如何在 REST Web 服务中工作？***

授权是授予某人访问权限的过程。如果您已获得授权，则您可以访问该资源。现在授权您需要提供凭据，正如我们之前讨论的那样，该过程称为身份验证。因此，授权和身份验证是密切相关的术语，并且经常互换使用。

在结束本教程之前，让我们在上面提到的 URL 中查看私有资源的内容。为此，请输入以下凭据

-   ***用户名：ToolsQA***
-   ***密码：TestPassword***

服务器将能够进行**身份验证**，然后**授权**您访问私有资源内容。下图显示了认证成功后的内容。

![私有资源](https://toolsqa.com/gallery/Rest%20Assured/2.PrivateResource.png)

有了对身份验证和授权的基本了解，请阅读接下来的教程，我们将在其中讨论***REST API中******身份验证***模型的特定类型。
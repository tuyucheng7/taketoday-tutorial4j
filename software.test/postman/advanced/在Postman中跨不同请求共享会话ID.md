## 如何在 Postman 中跨不同请求共享会话 ID？

所以基本上本教程旨在让你了解我们如何将一个 cookie 值传递给另一个请求。仅当网站相同时，Cookie 值才会传递到另一个请求。正如我们在什么是 cookies 教程中讨论的那样，服务器识别它自己的 cookies 而不是其他 cookies。这使得 cookie 高度安全。需要将 Cookie 值传递给另一个请求，以匹配你的偏好和随你看到的每个页面保存的数据。由于从服务器的角度来看，每个页面都是一个独特的请求，因此每个网页都需要 cookie 才能向你显示最佳结果。在本教程中，我们会将Session ID Cookie 传递给下一个请求。

会话 ID 类似于令牌过期。当令牌过期时，授权失败。同样，当会话 ID 过期时，你不再向服务器进行身份验证，需要重新登录。应该注意的是，这在网站的服务器上不起作用。这纯粹是为了我们在开发/测试时理解和使用它们。

### 会话 ID Cookie 工作流程

在使用 cookie 对用户进行身份验证时，工作流程分为四个简单的步骤

1.  用户通过向网站提供其凭据进行登录。
2.  在我们登录后，凭据将通过数据库中的简单查询进行检查。如果发现凭据有效，则会为用户创建会话。为该会话提供了一个称为会话 ID 的唯一 ID，通过该 ID 可以在会话处于活动状态时对用户进行身份验证。然后将此会话 ID 发送到客户端并保存在浏览器中。
3.  对于来自客户端的每个后续请求，浏览器都会发送会话 ID 以及对用户进行身份验证的请求。此会话 ID 被检查为数据库中的那个。如果发现它们相等，则用户将得到他请求的网页的响应。
4.  用户退出网站后，会话 ID 在客户端和服务器端都被销毁。

### 如何与另一个请求共享 Session ID Cookie？

为了将会话 ID 传递给另一个请求，我们必须遵循两个步骤，即

-   从我们从第一个请求获得的响应中保存 cookie。
-   将此 cookie 作为标头添加到我们发送的下一个请求中。

1.  去www.amazon.com
2.  查看你是否在响应中将会话 ID作为 cookie

![在 Postman 中跨不同请求共享会话 ID](https://www.toolsqa.com/gallery/Postman/1.Share%20Session%20ID%20across%20Different%20Requests%C2%A0in%20Postman.png)

1.  我们将尝试将此会话 ID 保存为 Postman 中的变量。为此，请在“测试”选项卡中使用以下代码。

var a = pm.cookies.get('session-id'); pm.globals.set("会话 ID", a);

![saving_session_id](https://www.toolsqa.com/gallery/Postman/2.saving_session_id.png)

这将获取会话 ID cookie 并将其作为全局变量发送，其键是会话 ID，值是 cookie 的值。

1.  现在按发送并查看变量。

![环境变量](https://www.toolsqa.com/gallery/Postman/3.environment_variables.png)

注意：你可以参考[环境和变量](https://toolsqa.com/postman/environment-variables-in-postman/)教程来了解有关变量的更多信息。

1.  现在我们已经在 Postman 中保存了会话 ID。我们现在需要做的就是将此会话 ID 与下一个请求一起作为 cookie 发送。这是通过添加标头 cookie 及其值来完成的。有关标头和如何在请求中使用标头的详细信息，请参阅课程。
2.  输入地址 https://www.amazon.in/your-account

考虑作为一个新的请求。此请求打开我的帐户详细信息。我们需要告诉服务器这是同一个会话。因此，我们将 cookie 作为标头发送。

![亚马逊账户地址](https://www.toolsqa.com/gallery/Postman/4.amazon_account_address.png)

1.  转到标题选项卡。

![Header_Tab](https://www.toolsqa.com/gallery/Postman/5.Header_Tab.png)

1.  输入 Key 作为 Cookie 和 Value 作为变量session ID。我希望你事先知道如何提及变量。

![Cookie_Header](https://www.toolsqa.com/gallery/Postman/6.Cookie_Header.png)

你还可以将鼠标悬停在变量上以查看它的值，该值与上一个请求的值相同。

这样，你可以将 cookie 连同标头一起发送到 Postman 中的下一个请求。这个例子纯粹是为了理解它是如何工作的。由于亚马逊或任何其他公司的安全性，这不能与成功响应一起显示。当你开发自己的 API 时，这将对你的测试有很大帮助。
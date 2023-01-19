## Postman 的 OAuth 2.0 授权？

在本教程中，我们将使用 Postman 查看OAuth 2.0的工作流程。为此，我们将使用[imgur](https://imgur.com/)网站 API，这是一个在线图像共享社区。你还可以使用使用OAuth 2流程的任何其他公司的API 。

但在此之前，让我们看看当我们未经授权访问安全端点时会发生什么。

1.转到 Postman 并点击端点： https ://api.imgur.com/3/account/me/images![Imgur_API_Hit](https://www.toolsqa.com/gallery/Postman/1.1Imgur_API_Hit.png)

注意：这是从你在 imgur 中的帐户获取图像的端点。

2.会出现鉴权错误，返回401 Unauthorized状态。这意味着此端点需要身份验证才能访问资源。

![Imgur_API_Hit_Response](https://www.toolsqa.com/gallery/Postman/2.Imgur_API_Hit_Response.png)

让我们看看如何生成访问令牌。

### 如何在 Postman 中使用 OAuth 2 生成访问令牌？

[请记住，在上一篇关于OAuth 2.0 授权](https://toolsqa.com/postman/oauth-2-0-authorization/)流程的教程中，我们讨论过可以通过授权服务器生成访问令牌。但是要访问授权服务器，你的应用程序必须注册。我们也将在这里遵循相同的流程。

1.  在本节中，我们将在 Postman 中使用OAuth生成令牌。如需注册，请先注册imgur网站，然后转到以下 URL 注册你的应用程序。确保你已使用用户名和密码登录到imgur 。

https://api.imgur.com/oauth2/addclient

![注册_应用_Imgur](https://www.toolsqa.com/gallery/Postman/3.Register_Application_Imgur.png)

1.  用适当的输入填充方框，如下所示

![注册_Application_Imgur_2](https://www.toolsqa.com/gallery/Postman/4.Register_Application_Imgur_2.png)

注意：回调 URL 用于了解你将从中调用 imgur api 的应用程序的注册地址。这在应用程序和服务器中应该相同。这是你将调用的应用程序的注册服务器地址。

1.  点击提交，你将收到一个Client Id和Client Secret。

![授权_客户端_ID_Imgur](https://www.toolsqa.com/gallery/Postman/5.Authorization_Client_ID_Imgur.png)

注意：请记住这些是机密值，不应共享。要了解更多信息，请参阅 OAuth 2.0 教程l。

1.  转到你的 Postman 应用程序并打开授权选项卡。

![Authorization_Tab_Postman](https://www.toolsqa.com/gallery/Postman/6.Authorization_Tab_Postman.png)

1.  从下拉列表中选择Oauth 2.0授权。

![Oauth2_Select_Postman](https://www.toolsqa.com/gallery/Postman/7.Authorization_Tab_Postman.png)

1.  从同一面板中选择获取新访问令牌。

![获取访问令牌](https://www.toolsqa.com/gallery/Postman/8.Get_Access_Token.png)

1.  将打开一个包含不同值的新面板。如图所示填写值。

![Postman 的 OAuth 2.0 授权](https://www.toolsqa.com/gallery/Postman/9.OAuth%202.0%20Authorization%20with%20Postman.png)

注意：Client Id 和 Client secret 与你在注册应用程序时获得的相同。

有关 URL 的信息可以在[Imgur Documentation](https://api.imgur.com/)中获得。如果你通过任何其他网站练习，你将始终在该网站的文档部分下获得此信息。通常这个页面很难从主页上找到，最好直接到任何搜索引擎搜索。例如Facebook API 文档或 Twitter API 文档。每一步都将写在文档中。

1.  按Request Token，将打开一个新窗口，询问你的凭据。

![Postman_Connect_Imgur](https://www.toolsqa.com/gallery/Postman/10.Postman_Connect_Imgur.png)

1.  填写你的凭据并登录到imgur网站。按允许后，你将在以下面板中收到访问令牌。

![Token_Generated_Imgur](https://www.toolsqa.com/gallery/Postman/11.Token_Generated_Imgur.png)

注意：Postman 中的访问令牌将在上面面板中给出的秒数后过期。这完全取决于你访问的网站服务器，如 imgur here。上面的面板显示此令牌将在 315360000 秒后过期。到目前为止，你可以多次使用端点。你需要在指定时间过去后请求一个新令牌，即令牌已过期。

1.  在上面的屏幕中点击 Use Token ，然后从下拉面板中选择Postman Token 。

![选择_Postman_Token](https://www.toolsqa.com/gallery/Postman/12.Select_Postman_Token.png)

1.  一旦你点击你的代币名称，该代币就会出现。

![Token_entered_Postman](https://www.toolsqa.com/gallery/Postman/13.Token_entered_Postman.png)

1.  按预览请求自动更新标题

![Preview_Request_Token_Update](https://www.toolsqa.com/gallery/Postman/14.Preview_Request_Token_Update.png)

1.  你还可以访问Header选项卡以查看输入的令牌值。

![Header_Token_Updated](https://www.toolsqa.com/gallery/Postman/15.Header_Token_Updated.png)

1.  现在按发送到我们在步骤 1 中输入的相同API并查看响应。

![Response_After_Authorization_Imgur](https://www.toolsqa.com/gallery/Postman/16.Response_After_Authorization_Imgur.png)

我们得到了正确的状态，发现我们的数据为零。这意味着我们能够通过邮递员的第三方应用程序进入服务器并访问我们的帐户信息。因此我们被授权使用OAuth 2.0。

你也可以练习使用其他网站。使用 OAuth 2 是一项非常重要的功能，并且由于它受到安全保护，因此非常重要。我们现在将进入下一个教程，我们将在其中学习会话变量。
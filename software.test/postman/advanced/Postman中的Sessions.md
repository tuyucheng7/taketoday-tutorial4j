Postman 中的会话是一项新引入的功能(在 6.2 版之后)，在此之前非常需要它。它解决了与团队其他成员共享你在项目中使用的敏感信息的问题，这些成员可能在你身边，也可能不在你身边。它提供了一个附加层来保存集合、环境和变量。下图显示了 Postman 会话持有两个变量值，分别称为Initial Value 和 Current Value。

![邮递员会议](https://www.toolsqa.com/gallery/Postman/1.Postman_Sessions.jpg)

不要担心这里的不同术语。请注意，这里为每个变量使用了两种类型的值。这在 Postman 的这个版本之前是不可用的。关于 Postman 会话的一切都局限于这两个值。我们将在后面的部分详细介绍这一点。虽然这是一项新引入的功能，但使用它需要一些先决条件

### 先决条件

-   环境和变量知识(参考[环境和变量](https://toolsqa.com/postman/environment-variables-in-postman/))
-   Postman中的收款知识(参考[Postman中的收款](https://toolsqa.com/postman/collections-in-postman/))
-   Postman 中的工作区知识(参考 Postman 中的工作[区](https://toolsqa.com/postman/collections-in-postman/))

Postman 中的会话被描述为一个瞬态层，它通过允许你使用在你的个人范围内保持本地的某些变量来保存与你当前工作实例相关的变量值([从官方邮递员网站](https://www.getpostman.com/docs/v6/postman/environments_and_globals/sessions)引用)。这是非常技术性的，我很确定现在很难解码这个功能的真正价值。因此，我们将在本教程接下来的部分中介绍所有这些内容。在本教程中，我们将了解

-   邮递员中的会话是什么
-   会议的优势
-   如何在邮递员中使用会话

## Postman 中的会话是什么？

如上所述，会话保存你的变量值，以便你可以在本地使用它们。邮递员中的会话意味着你可以根据你更改某些变量的值并相应地在你的系统上工作。当你与团队分享你的工作时，无需分享这些价值观。假设你与团队中的一群开发人员一起工作。你们都在需要访问令牌的服务器上工作，这是使用客户端 ID 获取的，秘密如 Oauth 2.0 教程中所述。正如那里提到的，这些价值观对你来说是机密的，不应该共享，即使是在你的团队成员之间也是如此。请牢记这一点，在 Postman 中引入了会话。使用会话，你可以在本地变量中使用自己的值，这些值不会同步到邮递员服务器. 当你共享集合时，这些值也不会被共享。简而言之，你的会话变量在各个方面都将保持本地和个人化。我们将在下一节中使用会话变量的解释中使用此处描述的相同示例，以使其更加清晰。

### 会话变量的优点

-   值的更改： 你可以根据自己的需要更改 Postman 会话中的值，包括在团队中工作时的敏感数据，因为 Postman 不会同步这些值。因此，这些值对你来说仍然是局部的。
-   Postman 中的会话允许你分别更改初始值和当前值。
-   会话允许你在你的工作空间中与许多人一起工作，并且仍然使用你自己的 ID、令牌、密码等。

## 邮递员会议

在继续使用Postman中的session变量之前，我们必须先看看6.2版本前后变量面板界面的区别。

以前界面看起来像这样

![Eye_Icon_All_Variables](https://www.toolsqa.com/gallery/Postman/2.Eye_Icon_All_Variables.png)

更新并包含session后，这个界面变成了这样

![New_Environment_Eye_Interface](https://www.toolsqa.com/gallery/Postman/3.New_Environment_Eye_Interface.png)

现在，你可以在该界面中看到两个新列

-   初始值：初始值是当你分享你的收藏时其他人将看到的值。这不应包含可能导致访问你的帐户的敏感信息，如令牌、ID 等。
-   当前值：此值对你而言是个人的和本地的。与其他人共享收藏集后，其他人将看不到它。

下图显示了相同的内容。

![Environment_Panel_With_Values_Sessions](https://www.toolsqa.com/gallery/Postman/4.Environment_Panel_With_Values_Sessions.png)

这里的初始值被命名为Your Key，这样当我们与其他人共享时，他们就会知道他们必须在当前值中插入自己的密钥。这样我们的敏感信息将保留在我们系统的本地。此值未与 Postman 云同步，因此无需担心。

现在，我们可以进一步了解有关会话的更多信息。

### 如何在 Postman 中使用会话？

我们将继续使用与[在 Postman 中生成 OAuth 2.0 令牌](https://toolsqa.com/postman/oauth-2-0-authorization-with-postman/)时相同的过程。在该教程中，我们生成了一个访问令牌，用于与 API 一起使用以访问我们的帐户信息。

1.  如图所示，使用可变URL (包含端点 URL)创建新环境Imgur

![URL_Variable_Sessions](https://www.toolsqa.com/gallery/Postman/5.URL_Variable_Sessions.png)

请参阅[环境和变量](https://toolsqa.com/postman/environment-variables-in-postman/)以了解有关在 Postman 中创建环境和变量的信息。

1.  在此环境中添加另一个名为token的变量，它将包含你获取的令牌值。

![令牌_变量_会话](https://www.toolsqa.com/gallery/Postman/6.Token_Variable_Sessions.png)

注意：以上示例是 [OAuth 2.0 Authorization with Postman](https://toolsqa.com/postman/oauth-2-0-authorization-with-postman/)的延续，请参考相同的教程获取Authorization token的实际值。

只需输入初始值，你就可以看到你的当前值会自动更新。

1.  返回 Postman 并在Header部分看到授权是硬编码的，因为我们自动添加了它。

![授权硬编码](https://www.toolsqa.com/gallery/Postman/7.Authorization_Hard_Coded.png)

注意：以上示例是OAuth 2.0 Authorization with Postman的延续，请参考相同的教程获取Authorization token的实际值。

1.  现在，如果我们尝试添加另一个授权标头，我们会收到 Postman 的提示。

![Duplicate_Header_Prompt](https://www.toolsqa.com/gallery/Postman/8.Duplicate_Header_Prompt.png)

此提示基本上意味着你不能使用相同的标头 Authorization 两次，因为它将被覆盖。

1.  转到授权选项卡并在授权类型选项卡中选择无授权。

![No_Auth_Select](https://www.toolsqa.com/gallery/Postman/9.No_Auth_Select.png)

1.  返回 Header 选项卡，现在输入值为Bearer {{token}}的授权密钥。

![Authorization_Bearer_Token](https://www.toolsqa.com/gallery/Postman/10.Authorization_Bearer_Token.png)

1.  将鼠标悬停在{{token}}上并查看提到的值

![令牌值](https://www.toolsqa.com/gallery/Postman/11.token_values.png)

1.  这些都是当前值和初始值。但代币价值是敏感的。任何人都可以使用令牌值访问你的帐户。因此，如上所述，我们必须更改初始值，使其不与其他人共享。为此，你需要按下环境面板中的编辑按钮，然后将打开第 2 步中给出的面板。改变给定的初始值。

![Initial_Value_Token_Change](https://www.toolsqa.com/gallery/Postman/12.Initial_Value_Token_Change.png)

这将提醒每个其他成员在该字段中输入他们自己的值。

现在你可以按发送到 API 并查看结果与你在 OAuth 2.0 教程中获得的结果相同。所以基本上结束这个，会话变量用于个人使用我们自己的变量值。它帮助我们将敏感信息保密到我们的系统。无论你是在团队中分享收藏，还是在登录邮递员云后分享。我希望你明白了。关于这一点，我们到此结束本教程。
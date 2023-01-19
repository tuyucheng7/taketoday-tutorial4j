### 什么是授权？

授权的意思可以看做一个问题，我们是否有资格访问Server上的[安全资源？](https://toolsqa.com/postman/postman-navigation/)如果答案是肯定的，那么在技术术语中我们可以说我们被授权访问该资源。如果答案是否定的，我们可以说我们没有被授权访问该资源。例如，假设你已将你和你姐姐的指纹添加到你的手机中。你和你妹妹可以打开同一部手机，也就是说只有你和你妹妹有权限打开手机看数据。同样，虽然可能有很多API在公司或项目中。没有必要每个人都可以访问所有 API。只有授权人员才能访问受保护的 API。

### 授权与身份验证

授权和身份验证是两个密切相关的术语。这两个术语起初也可能令人困惑。在本节中，我们将消除对这两个术语的混淆。

身份验证是向系统出示你的凭据并且系统验证你的凭据的过程。这些凭据告诉系统你是谁。这使系统能够确保和确认用户的身份。这里的系统可以是任何东西，它可以是电脑、电话、银行或任何实体办公场所。

而授权是允许或拒绝某人访问某物的过程，一旦完成身份验证。因此，通俗地说，身份验证告诉你你是谁，而授权则告诉你可以做什么。

当某人使用key/password访问服务器时，服务器会检查该人是否在目录中可用并且是否也与相同的key/password相关联。如果是，你就可以开始了(身份验证)。如果你有权访问该资源，那么你将被授予对该资源的访问权限 (Authorized)。

![身份验证与授权](https://www.toolsqa.com/gallery/Postman/1.Authentication%20vs%20Authorization.png)

我们将看到以下简短示例，告诉你服务器如何拒绝未经授权的人。

## 使用 Postman 授权

### 检查授权

对于本章，我们将使用端点https://postman-echo.com/basic-auth

1.  创建GET请求并将端点输入为https://postman-echo.com/basic-auth

![邮递员中的基本身份验证](https://www.toolsqa.com/gallery/Postman/2.Basic%20Authentication%20in%20Postman.png)

按发送并查看响应

![Basic_Auth_Response_Unauthorized](https://www.toolsqa.com/gallery/Postman/3.Basic_Auth_Response_Unauthorized.png)

注意：状态代码为401，对应于未经授权的访问，响应消息为Unauthorized。

来自服务器的状态代码和响应表明我们无权访问我们尝试访问的API (请参阅[响应教程](https://toolsqa.com/postman/response-in-postman/) 以了解更多信息)。在本教程的后面，我们将尝试使用我们在上一节中讨论的凭据访问相同的API 。

### 需要授权

在上一节中，我们讨论了资源所有者不允许公司中的每个人访问资源。这是因为它可能导致可能的安全漏洞。如果我允许实习生访问我的数据库 API，那么他可能会无意中更改数据，并且该数据可能会永远丢失，这可能会成为公司的成本。同样的原因可能有很多。也许有人为了钱而更改数据，或者有人可以将数据泄露给另一家公司。授权在决定访问权限和加强安全性方面起着非常重要的作用。让我们看看我们可以使用的不同类型的身份验证。

### 基本访问认证/HTTP 基本认证

基本访问身份验证是可用的最简单和基本的授权类型。它只需要一个用户名和密码来检查任何人的授权(这就是为什么我们说基本访问身份验证)。用户名和密码作为授权标头中的标头值发送。在使用基本身份验证时，我们在输入用户名和密码之前添加 Basic 一词。这些用户名和密码值应该用 Base64 编码，否则服务器将无法识别。我们将按照以下步骤检查我们是否可以访问我们上面使用的相同 API

## 使用凭据检查授权

1.在GET请求中输入端点https://postman-echo.com/basic-auth 。

1.  转到标题

![选择_标题](https://www.toolsqa.com/gallery/Postman/4.Selecting_Headers.png)

1.  在 Header 中输入以下键值对

授权：基本邮递员：密码

![授权_基本_纯文本](https://www.toolsqa.com/gallery/Postman/5.Authorization_Basic_Plain_Text.png)

注意：我们使用用户名作为邮递员，密码作为密码

1.  按发送并查看响应框和状态代码。

![Bad_Request_Basic_Auth](https://www.toolsqa.com/gallery/Postman/6.Bad_Request_Basic_Auth.png)

它仍然显示400，Bad Request。(这部分我们已经  在状态代码及其含义下的[响应章节](https://toolsqa.com/postman/response-in-postman/)中介绍过)。你能猜出为什么吗？ 如果你还记得我们在上一节中学到的知识，基本的访问认证需要将用户名和密码用 Base64 编码，但这里我们只是以明文形式发送用户名和密码。结果，服务器返回了 400，Bad Request 状态代码。在我们继续之前，了解什么是Base64编码将是有益的。

### 什么是Base64编码？

编码用于身份验证，因为我们不希望我们的数据直接通过网络传输。这有很多原因。网络扫描仪可以读取你的请求并检索未经编码发送的用户名和密码。此外，直接传输的位和字节可以被调制解调器或网络链中的其他设备视为内置命令位。例如，如果有一个0101101010的内置命令，这意味着重置调制解调器，那么在传输时我们可能希望获得 001101010 0101101010 11020 的数据序列。调制解调器可能会将其解释为重置命令并自行重置。为了避免此类问题，对数据进行编码是有益的。

我们特别使用base64，因为它将数据传输为文本形式，并以更简单的形式(如 HTML 表单数据)发送。我们特别使用Base64是因为我们可以在我们使用的任何编码语言中依赖相同的 64 个字符。虽然我们也可以使用更高基础的编码方法，但它们很难转换和传输，从而浪费了不必要的时间。

回到最初在Authorization标头中发送Base64编码字符串的问题。我们面前有两种创建Base64编码字符串的方法：

-   通过第三方网站
-   通过邮递员

我们将一一看到这两个选项。现在，按照从第三方网站解码访问 API 的步骤进行操作。

## 通过第三方网站编码认证

1.  转到https://www.base64encode.org/

![Base64_网站](https://www.toolsqa.com/gallery/Postman/7.Base64_Website.png)

注意：有数以千计的网站可用于同一目的。你可以使用任何人，只要确保你编码为与我们相同的值即可。此外，我们使用Microsoft Edge作为浏览器，但应该没有任何区别。

1.  在框中粘贴以下值

邮递员：密码

![编码盒](https://www.toolsqa.com/gallery/Postman/8.Encode_Box.png)

3.按编码。

![新闻编码](https://www.toolsqa.com/gallery/Postman/9.Press_Encde.png)

1.  复制编码文本。

![编码_响应](https://www.toolsqa.com/gallery/Postman/10.Encode_Response.png)

注意：不要在任何两个文本或符号之间使用空格。邮递员：密码将编码为不同的值，而邮递员：密码将编码为不同的值。不用说，两者都会被认为是错误的。仅使用邮递员：密码。

1.  转到邮递员应用程序，而不是邮递员：密码，粘贴编码值

![编码_基本_Base64](https://www.toolsqa.com/gallery/Postman/11.Encoded_Basic_Base64.png)

1.  按发送并查看响应框的值和状态代码。

![Basic_Auth_Response_Authorized](https://www.toolsqa.com/gallery/Postman/12.Basic_Auth_Response_Authorized.png)

200 OK, authenticated表示我们提供了正确的凭据，现在我们有权访问数据。

## 通过 Postman 通过编码进行身份验证

我们将尝试使用 Postman 进行编码，而不是去第三方网站。

1.  擦除我们之前输入的键值对，使其现在没有值。

![标头_空](https://www.toolsqa.com/gallery/Postman/13.Header_Empty.png)

1.  转到授权选项卡

![授权标签](https://www.toolsqa.com/gallery/Postman/14.Authorization_Tab.png)

3.在类型下拉列表中选择基本身份验证

![选择_基本_授权](https://www.toolsqa.com/gallery/Postman/15.Select_Basic_Auth.png)

4.输入用户名作为邮递员和密码作为密码

![Basic_Auth_Username_Password](https://www.toolsqa.com/gallery/Postman/16.Basic_Auth_Username_Password.png)

5.按预览请求

![预览请求](https://www.toolsqa.com/gallery/Postman/17.Preview_Request.png)

1.  去Header看到 Postman 已经帮你转换了用户名和密码。

![编码_基本_Base64](https://www.toolsqa.com/gallery/Postman/18.Encoded_Basic_Base64.png)

7.按下发送，瞧！我们通过了身份验证。

![Basic_Auth_Response_Authorized](https://www.toolsqa.com/gallery/Postman/19.Basic_Auth_Response_Authorized.png)

在这里，我们结束了我们的教程。我希望你一定已经学到了很多关于Postman 中基本授权的知识。这并不难，但只要再次阅读本教程就可以很好地掌握Authorization也无妨。授权和身份验证将是你将学习的一些最重要的主题，因此请将此处教授的所有概念内化。我们现在将继续下一个教程。

Postman 中还有其他类型的授权，如果你想了解更多信息，请参阅以下视频：

Postman 中的 Bearer Token 授权

<iframe class="embed-responsive-item youtube-player" type="text/html" width="640" height="390" src="https://www.youtube.com/embed/PPi9teNKRHY?enablejsapi=1&amp;origin=https%3A%2F%2Fwww.toolsqa.com" frameborder="0" webkitallowfullscreen="" mozallowfullscreen="" allowfullscreen="" data-gtm-yt-inspected-31117772_24="true" id="756841179" title="Postman 教程 #16 - Postman 中的 Bearer Token 身份验证" style="box-sizing: border-box; position: absolute; inset: 0px; width: 730.344px; height: 410.812px;"></iframe>
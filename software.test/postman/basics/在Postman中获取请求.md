由于我们现在已经了解了 Postman 并[了解了如何在 postman 中创建和保存新请求](https://toolsqa.com/postman/create-new-request-in-postman/)，现在是时候开始处理Postman 中的第一个 GET 请求了。当我们从客户端机器(用户)向服务器机器请求时，我们遵循架构和 HTTP 协议。我建议你阅读以下教程以更好地理解HTTP 协议、请求和响应。这些可以在这里查看：

-   [客户端服务器架构和 HTTP 协议](https://toolsqa.com/client-server/client-server-architecture-and-http-protocol/)
-   [HTTP 请求](https://toolsqa.com/client-server/http-request/)
-   [HTTP 响应](https://toolsqa.com/client-server/http-response/)

假设你现在熟悉 HTTP 协议和体系结构，我们现在将讨论一种特定类型的请求，即 GET请求。GET请求用于从服务器获取信息，对服务器没有任何副作用。副作用意味着当你发出这种类型的请求时，服务器上没有更新/删除/添加数据，你只是向服务器请求，服务器响应请求。

GET请求的所有信息都在URL中，并且由于 URL 始终可见，因此建议你在发送一些敏感信息(例如密码)时不要使用此类请求。例如，当你在 google.com 的搜索框中输入任何内容后按搜索时，你实际上是在进行 GET 请求，因为没有敏感信息，你只是请求包含搜索结果的页面，你会注意到相同的搜索字符串网址。

你还可以查看 Postman 教程的录音，我们的专家在其中深入解释了这些概念。


在此图像中，如你所见，有一个下拉按钮，根据 API 需要，它具有不同类型的请求类型。到目前为止，不必担心所有这些不同的[HTTP 请求](https://toolsqa.com/client-server/http-request/)，因为我们将在本 Postman 教程系列中介绍其中的每一个。但现在，只关注 GET 请求。

1.  从请求类型列表中选择GET 。

![Postman 中的 GET 请求](https://www.toolsqa.com/gallery/Postman/1.GET%20Request%20in%20Postman.png)

1.  如上图所示，在地址栏中 输入www.google.com ，然后按发送。 现在，查看状态代码。

![Status_Code_200](https://www.toolsqa.com/gallery/Postman/2.Status_Code_200.png)

不同的状态码有不同的含义，无论是 GEt 请求还是任何其他类型的请求都没有关系。在这种情况下，我们有状态代码200 OK，这意味着 EndPoint 是正确的并且它返回了所需的结果。稍后我们将显示更多状态代码。

下面框中的彩色文本是服务器的响应。如果你仔细观察响应框内的内容，你会看到页面代码已发送给我们。上面的标签显示Body。正文表示你已选择查看显示在框内的响应正文。在正文中，你将看到三个选项。

![Pretty_Raw_Preview](https://www.toolsqa.com/gallery/Postman/3.Pretty_Raw_Preview.png)

-   漂亮：在此代码中，不同的关键字将以彩色的方式显示，并且会以不同的颜色显示，并且为了便于阅读，某些格式会缩进。
-   原始：与没有颜色和单行的漂亮部分相同。
-   预览：显示已发送页面的预览。如果未正确加载，请不要担心谷歌涂鸦。自己尝试任何其他网站。

响应是一个比本章需要解释的更详细的主题。我们将在下一章中完整解释响应。
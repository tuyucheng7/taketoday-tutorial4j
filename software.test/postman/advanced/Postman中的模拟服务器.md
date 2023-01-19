在[API with Postman 教程](https://toolsqa.com/postman/api-testing-with-postman/)中，我们了解了[客户端和服务器架构](https://toolsqa.com/client-server/client-server-architecture-and-http-protocol/)，这是API 测试的灵魂。在这种类型的架构中，我们需要一个服务器来获取响应。这是一个组织的工作在线服务器。那为什么要模拟服务器？

## 邮递员中的模拟服务器

好吧，在一般字典术语中嘲笑意味着模仿或制作某物的复制品。所以很容易解码我们将在这里复制服务器。我们以与部署后相同的方式模仿服务器，并以与当时相同的方式响应。我们该怎么做？它简单明了。我们需要相同的响应，因此我们需要使用相同的参数模拟与原始 API 相同的 API。虽然它是一个副本，但我们甚至可以在没有实时服务器的情况下继续我们的工作和调试。使用模拟服务器还有很多其他原因和工具。但我们很幸运，Postman 已经有了一个内置功能，无需任何外部安装即可部署模拟服务器。在接下来的部分中，我们将讨论：

-   什么是模拟服务器？
-   为什么要使用模拟服务器？
-   如何在 Postman 中创建模拟服务器？

### 什么是模拟服务器？

模拟服务器是不是真实服务器的服务器。它只是一个模拟真实服务器工作的假服务器，以便我们可以测试我们的 API 并检查响应或错误. 该服务器的设置方式使我们可以针对我们希望看到的特定请求获得特定响应。模拟服务器的行为类似于真实服务器，并且使用假 API，当然是为了测试和开发目的。我们需要模拟服务器的原因有很多。除了上面给出的案例，它也是当今测试世界所需要的。这种要求在敏捷方法中是最新的并且比瀑布方法更好。在这种方法中，测试和开发并行进行。为此，测试人员需要具有与开发人员相同的要求才能同时工作。为此你需要一个模拟服务器。除此之外，下一节列出了一些原因。

### 为什么我们需要模拟服务器？

出于多种原因，我们需要模拟服务器。需要模拟服务器

-   在开发时测试你自己的 API，然后再将该 API 部署到你组织的服务器上。
-   更快地获得反馈和错误。
-   用于在每个人都可用之前检查 API 中的依赖项。
-   供 QA 工程师使用它来测试/隔离外部依赖项和集成测试。
-   由前端开发人员在实际端点可用之前使用它。这是在设计 UI 时完成的，因此你无需等待实际端点开发完成的时间。它节省了很多时间。
-   让工程师开发他们想法的原型，并将其展示给投资者以获得资金。

![前端模拟](https://www.toolsqa.com/gallery/Postman/1.Front-End_Mock.png)

上图解释了上面提到的相同点。前端开发人员需要开发 UI，他必须知道他将获得的响应。同样，他不能等到 API 出现在服务器上，所以他使用模拟服务器来实现同样的目的并节省时间。

我想现在你一定知道模拟服务器对于测试人员来说是一项非常重要的功能。它在软件的开发和测试阶段都非常有帮助。继续我们现在将继续创建我们的第一个模拟服务器。

## 如何在 Postman 中创建模拟服务器？

在本节中，我们将在 Postman 中创建我们的第一个模拟服务器，但在此之前，你必须了解有关模拟服务器的一些知识

-   模拟服务器已经集成在邮递员应用程序中，外部不需要。
-   模拟服务器还启用了 CORS(跨源资源共享)。这意味着你在使用模拟服务器时不会遇到任何跨源错误。
-   模拟服务器是免费使用的，即它在 Postman 的免费层中可用。

好的，现在我们将按照以下步骤创建我们的第一个模拟服务器。

1.单击“页眉”部分左上角的“新建”按钮

![NEW_邮递员](https://www.toolsqa.com/gallery/Postman/2.NEW_Postman.jpg)

1.  在面板中选择模拟服务器

![邮递员中的模拟服务器](https://www.toolsqa.com/gallery/Postman/3.Mock%20Server%20in%20Postman.jpg)

1.  将打开一个新面板，使我们能够创建请求。

![模拟服务器请求](https://www.toolsqa.com/gallery/Postman/4.Mock_Server_Request.jpg)

此模拟服务器面板中有不同的列，代表：

-   第一列 Method 用于请求类型方法，如 GET、Post 等。
-   第二个请求路径将为你的 API 创建 url
-   响应代码将定义你希望在响应中获得的代码([阅读有关响应代码](https://toolsqa.com/postman/response-in-postman/)的更多信息)
-   Response Body 将包含你要显示的响应主体([阅读有关 Response Body 的更多信息](https://toolsqa.com/postman/response-in-postman/))

1.  如镜像所显示，填满列。

![Filling_Request_Mock_Server](https://www.toolsqa.com/gallery/Postman/5.Filling_Request_Mock_Server.jpg)

1.  点击下一步

![Mock_Server_Next](https://www.toolsqa.com/gallery/Postman/6.Mock_Server_Next.jpg)

1.  根据你的选择命名你的模拟服务器

![命名_Mock_Server](https://www.toolsqa.com/gallery/Postman/7.Naming_Mock_Server.jpg)

注意：如果你不想让每个人都可以访问你的信息，你也可以将服务器设为私有，但它需要 Postman API 密钥才能访问服务器。首先，我们将公开我们的服务器以降低复杂性。

1.  现在下一个屏幕将向你显示 URL，你可以通过该 URL 访问服务器。这是你的模拟服务器已成功创建的确认屏幕。

![Mock_Server_Created](https://www.toolsqa.com/gallery/Postman/8.Mock_Server_Created.jpg)

8.单击关闭并关闭面板。

1.  关闭面板后，你将看到已使用你输入的 API 创建了一个同名的新集合。

![Collection_Created_Mock](https://www.toolsqa.com/gallery/Postman/9.Collection_Created_Mock.jpg)

1.  你还可以注意到也创建了一个新环境([请参阅 Postman 教程中的环境](https://toolsqa.com/postman/environment-variables-in-postman/))

![Environment_Created_Mock](https://www.toolsqa.com/gallery/Postman/10.Environment_Created_Mock.jpg)

1.  选择集合中的第一个请求并将鼠标悬停在请求中写入的{{url}}上

![Hover_Mouse_URL_Mock](https://www.toolsqa.com/gallery/Postman/11.Hover_Mouse_URL_Mock.png)

你能猜出为什么这是一个未解决的变量吗？如果可以，那就太好了。是的，没有选择环境。这就是自动创建环境的原因。将环境更改为创建的环境并再次悬停。

![Hover_Mouse_URL_Created](https://www.toolsqa.com/gallery/Postman/12.Hover_Mouse_URL_Created.png)

现在可以显示 URL 值。按发送按钮并查看响应

![Response_Mock_Server.png](https://www.toolsqa.com/gallery/Postman/13.Response_Mock_Server.png)

我们得到的响应与我们在开始时设置模拟服务器时创建的响应相同。还要检查响应代码。

### 如何在 Mock Server 中获取不同格式的响应？

也很容易获得其他格式的 Mock Server 响应。由于我们在上一节中收到了文本响应，现在我们将了解如何以最常见的格式(即JSON )获取响应。

按照上一节中的步骤 1 到 3 进行操作。

在服务器创建面板中，不要编写纯文本，而是以JSON格式编写响应正文。

![Response_Body_JSON](https://www.toolsqa.com/gallery/Postman/14.Response_Body_JSON.png)

上面的代码是一个书店的数据，不同的书有不同的价值。代码写在下面。

{ "books": [ { "isbn": "9781449325862", "title": "Git Pocket Guide", "subTitle": "A Working Introduction", "author": "Richard E. Silverman", "published": "2013-08-02T00:00:00", "publisher": "O'Reilly Media", "pages": 234, "description": "这本袖珍指南是 Git 的完美工作伴侣，分布式版本控制系统。它为新用户提供了一个紧凑、可读的 Git 介绍，并为那些有 Git 经验的人提供了常用命令和过程的参考。", "website": "https://chimera.labs.oreilly.com/books/1230000000561/index.html" }, { "isbn": "9781449331818", "title": "学习 JavaScript 设计模式", "subTitle": "A JavaScript和 jQuery 开发人员指南", "author": "Addy Osmani", "published": "2012-07-01T00:00:00", "publisher": "O'Reilly Media", "pages": 254, "description ": "通过学习 JavaScript 设计模式，你将学习如何通过将经典和现代设计模式应用于语言来编写美观、结构化和可维护的 JavaScript。如果你想保持代码高效、更易于管理和更新-最新的最佳实践，这本书适合 你。","website":"https://www.addyosmani.com/resources/essentialjsdesignpatterns/book/" }

] }

使用你选择的名称创建服务器(我使用JSON RETURN作为名称)，然后在选择正确的 API 和环境后按发送。

你现在将收到JSON格式的响应。你可能会直接获取 HTML，但会从下拉列表中将格式更改为JSON以美化响应，如图所示。

![JSON_Response_MOCK](https://www.toolsqa.com/gallery/Postman/15.JSON_Response_MOCK.png)

注意：不要忘记在发送请求之前选择合适的环境。

至此，我们已经在 Postman 中创建并理解了一个模拟服务器。你可以使用不同的格式来练习。我希望你喜欢这个教程。我们将继续我们的下一个教程，直到那时，继续探索模拟服务器并创建你自己的请求。
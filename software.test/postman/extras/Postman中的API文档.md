一般而言，文件是提供或作为官方证据或信息的书面材料。当我们谈论文档时，我们通常指的是学习或收集有关某些事物的信息。这取决于文档的主题。例如，实验室中某个标本的文档将为医生提供有关该标本的信息。在 IT 世界中，不同软件、工具和 API 的文档可帮助你以正确的方式从可信来源(官方)了解这些东西。这会将我们带到Postman 中的 API 文档。为了索引我们的教程，我们将学习：

-   什么是 API 文档？
-   需要 API 文档。
-   Postman 中的 API 文档。
-   如何在 Postman 中生成 API 文档？

## Postman 中的 API 文档是什么？

API文档是一种结构良好的书面材料，提供第三方用户高效使用API 。这包括关于如何使用你的 API 的分步过程和说明。该文档使第三方用户/开发人员能够非常快速地了解你的 API 的使用情况。下面给出的是 Paypal API 的文档

![Paypal_API_Doc_Sample](https://www.toolsqa.com/gallery/Postman/1.Paypal_API_Doc_Sample.png)

编写 API 文档需要对 API 及其用途、使用的编程语言和响应有很好的理解。好的 API 文档应该能够通过详细的说明以简单易行的方式传达复杂的过程。

### 为什么 API 文档是必需的并且很重要？

在整个课程中，我们学习了如何使用 API 并对其执行测试。你一定记得，我们使用自己的 API 向你演示了 Postman 的一些功能。由于 API 是由允许其使用它们的每个公司构建的，因此他们需要一个结构良好的官方文档来指导开发人员如何使用它。这非常重要，因为当没有人能够使用它时，开发伟大的东西有什么意义呢？API 开发公司不是其 API 的唯一用户。第三方开发人员占据了很大一部分用户。由于 API 充当使用产品的中间人，因此文档充当使用 API 的平台。没有良好的 API 文档，产品将无法使用，进而给组织带来损失。这适用于小型企业，崭露头角的初创公司和像你这样的个人。任何愿意分享他的 API 的人都必须有文档供人们指导。因此，API 文档与 API 一样重要，在我们的例子中，这是由 Postman 完成的。我们将在下一节中看到如何。

你可以访问我在此处提到的 API 文档页面，这些页面在开发人员中非常受欢迎：

[贝宝 API 文档](https://developer.paypal.com/docs/api/payments/v1/)

[推特 API 文档](https://developer.twitter.com/en/docs/api-reference-index.html)

[脸书 API 文档](https://developers.facebook.com/docs/)

[特雷洛 API 文档](https://developers.trello.com/reference/)

### 使用 Postman 的 API 文档

Postman 帮助我们在几次点击的帮助下创建 API 文档。这是一种很好的方式，而不是从头开始编写自己的文档并自行发布。在我们开始实际发布文档之前，你必须注意一些事项。

在贵公司不知情的情况下发布文档是不可取的。登录 Postman 应用程序后，你所有的本地收藏和请求都会同步到 Postman 云。这也将同步你的文档，因为你无法在不登录的情况下在 Postman 中发布 API 文档。换句话说，你的文档将公开并且任何人都可以访问。要将你的 Postman 设为私有，你需要成为 Postman Enterprise 或 Postman Pro 会员。如果是，你可以在你的团队或组织内私下共享你的文档。

你还应该注意，每月只允许对文档进行 1000 次查看。Postman 中的所有计划都是一样的。完成 1000 次查看后，你的文档将在 30 天后才能访问。从这里开始，让我们在 Postman 中发布文档。

## 如何在 Postman 中生成 API 文档？

1.  打开你的 Postman 应用程序(确保你已注销)。
2.  [从这里](https://developers.trello.com/reference/)导入集合。(参考[如何在 Postman 中导入收藏](https://www.toolsqa.com/postman/collections-in-postman/))。
3.  导入收藏后，你将在侧边栏中看到它。

![导入_集合](https://www.toolsqa.com/gallery/Postman/2.Imported_Collection.jpg)

1.  现在，当你将鼠标悬停在集合名称上方时，选择出现在集合名称旁边的小箭头。

![选项_集合_2](https://www.toolsqa.com/gallery/Postman/3.Options_Collection_2.png)

1.  这将打开一个新面板。
2.  选择描述上的编辑图标以编辑描述。

![编辑_描述_收藏](https://www.toolsqa.com/gallery/Postman/4.Edit_Description_Collection.png)

1.  在文本框中写下你的选择说明，然后按保存。

![描述_集合](https://www.toolsqa.com/gallery/Postman/5.Description_Collection.png)

1.  按顶部共享和运行旁边的菜单按钮以打开多个选项。按选项中的发布文档。

![发布_文档](https://www.toolsqa.com/gallery/Postman/6.Publish_Docs.png)

1.  你将收到登录提示。如果你已经登录，你将跳转到第 12 点。

![登录_发布_文档](https://www.toolsqa.com/gallery/Postman/7.Sign_In_Publishing_Docs.png)

1.  登录邮递员。登录后，你会看到一个新按钮将出现在共享和运行选项旁边。

![查看_In_Web_2](https://www.toolsqa.com/gallery/Postman/8.View_In_Web_2.png)

1.  此选项可用于在发布后临时查看文档。回到发布文档选项。按第 8 点中讨论的发布文档选项。
2.  此选项将在你的浏览器中打开一个网页以选择你的环境名称。

![选择_Env_Publish_Docs](https://www.toolsqa.com/gallery/Postman/9.Choose_Env_Publish_Docs.png)

注意：你可以参考 Postman 教程中的环境和变量来了解更多关于环境的信息。

1.  为采集选择合适的环境。

![Env_More_Styling_Options_Publish_Docs](https://www.toolsqa.com/gallery/Postman/10.Env_More_Styling_Options_Publish_Docs.png)

1.  你还可以单击“显示自定义样式选项”来更改文档的配色方案。
2.  单击发布集合以发布集合。

![发布_收藏](https://www.toolsqa.com/gallery/Postman/11.Publish_Collection.png)

注意：让所有用户在 Postman 应用程序中发现此收藏集用于让你的收藏集可供 Postman 家族使用。为此，你的 API 应该不是你所特有的，但任何学习者或专家都可以使用。它应该有一个明确的工作流程，因为它将被 Postman 社区公开看到。

16. 你将看到你的收藏已发布的网页。

![收藏_已出版](https://www.toolsqa.com/gallery/Postman/12.Collection_Published.png)

你可以从此屏幕编辑或取消发布你的收藏。你将获得一个已发布的文档公共 URL。

1.  访问 URL 以查看你发布的 API 文档。

![Postman 中的 API 文档](https://www.toolsqa.com/gallery/Postman/13.API%20Documentation%20in%20Postman.png)

### 自动生成什么？

由于你自己编写的 API 文档可以有任意数量的字段，因此当我们使用 Postman 创建文档时，只会创建一些重要的字段。因此，你的文档将包括：

-   集合和请求名称。![sample_request_name_docs_postman](https://www.toolsqa.com/gallery/Postman/14.sample_request_name_docs_postman.png)
-   与请求、文件夹和集合相关的描述。这还将包含方法类型、标题和主体代码(如果有的话)。

![描述_文档_邮递员](https://www.toolsqa.com/gallery/Postman/15.Description_Docs_Postman.png)

-   以一些最流行的编程语言生成的代码片段。

![Code_Generated_Docs_Postman](https://www.toolsqa.com/gallery/Postman/16.Code_Generated_Docs_Postman.png)

-   直接在 Postman 中运行集合的链接以及用于更改环境的下拉菜单。

![Run_Postman_Docs_Postman](https://www.toolsqa.com/gallery/Postman/17.Run_Postman_Docs_Postman.png)

Postman 使用有序的请求和文件夹来按部分组织文档以反映你的集合结构。[你可以使用带有嵌入式图形的Markdown](https://learning.getpostman.com/docs/v6/postman/api_documentation/how_to_document_using_markdown)样式来自定义描述 ，以补充你的文档。Postman 支持 GitHub 风格的 Markdown，因此你可以包含表格。包含块元素时，请确保前后留空行以避免任何渲染问题。

你现在可以将此文档分享给任何你想要使用你的 API 的人。你可以在这里和那里找到更多选项，以一种或其他方式编辑文档。让自己参与其中一段时间，以提取有关文档的每个细节，这肯定会在将来对你有所帮助。正如我上面所讨论的，只要 API 的文档清晰易懂，它就是有用的。
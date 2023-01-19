### 请求中的参数是什么？

请求参数是用于向服务器发送附加数据的 URL 的一部分。让我们分析一个简单的 URL：

https://www.bing.com/search?q=ToolsQA

在此 URL 请求参数由 URL 的“ q=ToolsQA ”部分表示。请求参数以问号 ( ? ) 开头。请求参数遵循“Key=Value”数据格式。在我们的示例中， “q”是键，“ToolsQA”是值。服务器从 URL 中读取 Request 参数，并根据 Request Parameter 发送 Response。在我们的示例中，Bing 将返回 ToolsQA 的搜索结果。如果将值从ToolsQA更改为DemoQA，你将获得DemoQA而不是 ToolsQA 的结果。这意味着服务器读取请求参数并基于该参数进行响应。

简而言之，假设我设计了一个页面，可以向你显示特定班级的学生名单。现在，列表将取决于你选择的类，它将作为参数传递到 URL 中，而我设计的页面对于每个类都是相同的。有多少课程，我就不必设计多少页面。通过这种方式，我们提高了两个级别的效率和使用率。

GET Request中可以传递参数，如果你不清楚如何使用 Postman 进行 GET 请求，请查看之前的文章[如何进行 GET 请求](https://toolsqa.com/postman/get-request-in-postman/)。由于现在你知道如何发出 GET 请求，我们将继续在 GET 请求中发送参数。


在谈论参数并清楚地理解它们之前，我们会将 URL 发送到我们的浏览器。

1.  转到浏览器并在地址栏中输入www.google.com

![Postman 中的请求参数](https://www.toolsqa.com/gallery/Postman/1.Request%20Parameters%20in%20Postman.png)

1.  你将看到来自 Google 的响应页面。 在搜索栏中输入ToolsQA ，然后按Google 搜索。

![Search_ToolsQA_browser](https://www.toolsqa.com/gallery/Postman/2.Search_ToolsQA_browser.png)

现在你需要了解，显示结果的页面将保持不变，只是结果会因搜索而异。你刚才搜索了 ToolsQA，它作为 URL 中的一个参数来告诉服务器我们需要 ToolsQA 的具体结果。服务器根据搜索参数进行响应。

一个URL由很多参数组成，比如source id和encoding format等。看下面的URL，你会看到在URL中添加了&q=ToolsQA来告诉服务器。

![工具QA_Seach](https://www.toolsqa.com/gallery/Postman/3.ToolsQA_Seach.png)

注意：这里的“q”是代表查询的键，ToolsQA是查询的键或搜索词的值。

现在，我们将尝试通过 Postman 实现相同的结果。

## Postman 中的请求参数

 1.只需在 Postman中使用 URL www.google.com/search准备一个[GET 请求](https://toolsqa.com/postman/get-request-in-postman/)，然后单击Params。

![参数](https://www.toolsqa.com/gallery/Postman/4.params.png)

1.  如图所示，在Key-Value对下写下以下内容。q在这里再次代表查询，而 ToolsQA 是搜索词。现在按发送。

![Q_Params](https://www.toolsqa.com/gallery/Postman/5.Q_Params.png)

1.  查看预览，你会看到我们收到的不是 google 主页，而是针对特定搜索查询的响应，即ToolsQA。你可以编写任何内容并接收其响应，而不是 ToolsQA。这表明我们已经传递了一些关于我们希望看到的结果的信息(参数)。

![搜索工具QA](https://www.toolsqa.com/gallery/Postman/6.Search_ToolsQA.png)

注意：如上所述，你可以看到不同的搜索查询会给出不同的结果，但页面设计保持不变，只是内容不同。

### 多个参数

你还可以在单个查询中使用多个参数。正如我们在上面讨论的那样，在将搜索查询作为ToolsQA发送时，URL 中发送了很多参数。ToolsQA 是针对要显示的结果，另外一个参数比如encoding format也是用来告诉服务端结果可以用什么格式编码发送给客户端。在上面的示例中，使用的默认编码格式是 UTF-8。

查看上图并关注发送到服务器的 URL

![多参数浏览器](https://www.toolsqa.com/gallery/Postman/7.multiple_params_browser.png)

在上面的 URL 中，无论你在哪里看到&，它都必须跟在一个参数之后，例如&ie=UTF-8意味着ie是一个值为UTF-8的关键参数。你可以在邮递员中写入上面看到的每个参数，并发送带有多个参数的请求。

![多参数](https://www.toolsqa.com/gallery/Postman/8.Multiple_Params.png)

这些参数不供我们用户详细研究。即使你更改参数，反映的更改也不会在页面上看到，你仍然会得到与以前相同的响应，因为所有这些参数都用于服务器中的内部活动，例如记录提交。

### 从 URL 中分离参数

如果你想知道如何将给定的完整 URL 与其参数分开以在 Postman 中使用，那么 Postman 已经为你整理好了。你无需担心 URL 中的参数。你只需粘贴 URL，Postman 就会自行填充参数。

例如，将此 URL 复制并粘贴到你的邮递员中，如下所示https://www.google.co.in/search?q=toolsqa&oq=toolsqa&aqs=chrome..69i57j69i60l5.2885j0j4&sourceid=chrome&ie=UTF-8

现在点击Params，你可以看到一切都自己整理好了，参数如上图(或多或少)。

![多参数](https://www.toolsqa.com/gallery/Postman/9.Multiple_Params.png)

### 将参数复制到另一个 Postman Request

Params的另一个有趣的特性是 Postman 消除了每次查询时记住和输入相同参数的麻烦，而是让你输入一次而忘记再次输入相同的参数。例如，假设你必须运行与我们刚刚运行的查询相同的查询，但参数要少一些。为了实现同样的目标，

1.  单击批量编辑，你将看到所有参数的列表

![批量编辑](https://www.toolsqa.com/gallery/Postman/10.Bulk_Edit.png)

1.  复制一切

![批量编辑面板](https://www.toolsqa.com/gallery/Postman/11.Bulk_Edit_Panel.png)

1.  打开一个新标签并输入你的网址， 在本例中为www.google.com/search

![writing_url](https://www.toolsqa.com/gallery/Postman/12.writing_url.png)

1.  点击Params，然后点击Bulk Edit

![bulk_edit_again](https://www.toolsqa.com/gallery/Postman/13.bulk_edit_again.png)

1.  将你复制的所有内容粘贴到编辑器中，然后单击“ Key-Value edit

![Key_Value_Edit](https://www.toolsqa.com/gallery/Postman/14.Key_Value_Edit.png)

在这里你会看到每个参数都已根据新请求自动调整。

![参数_自动](https://www.toolsqa.com/gallery/Postman/15.Param_Automatic.png)

这使得 Postman 在使用参数选项时非常高效，并且让我们脱离了它的复杂性。参数是URL中非常重要的一部分，建议读者观察URL中的不同参数，以便更好地学习和理解，而这都是关于Postman内部的参数使用。接下来，我们将看到 Postman 中的响应。

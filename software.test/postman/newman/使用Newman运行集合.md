到目前为止，我们已经了解[了 Postman 中的 Newman 是什么，](https://toolsqa.com/postman/what-is-newman-in-postman/)并将其安装到我们的系统中。你现在一定已经了解我们使用我们系统的外壳来与 Newman 一起工作并利用它的特性和功能。在Newman 教程的介绍中提到，我们提到 Newman 允许我们以与 Postman 中的 Collection Runner 相同的方式运行集合。进一步采用相同的定义，我们将在本教程中运行从 Postman 到 Newman 的收集运行程序，并涵盖以下主题

-   如何通过共享链接与 Newman 一起运行一个集合
-   如何通过将集合导出为 JSON 来使用 Newman 运行集合

要开始使用 Newman 运行一个集合，首先你需要在你的 Postman 中有一个集合。我们将使用我们在[Collection Runner](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)教程中使用的相同集合，其中包含以下 API 请求。[你可以通过以下链接](https://toolsqa.com/wp-content/uploads/2018/07/Newman-Collection.postman_collection.zip)下载并导入到你的邮递员中。要使用它，请确保你首先需要解压缩文件夹并在邮递员中上传 .txt 文件。你也可以参考教程按照步骤[在邮递员中导入集合](https://toolsqa.com/postman/collections-in-postman/)

现在，我们已经将所有请求添加到我们的Newman Collection 集合中，并且在 Postman 中一切都运行良好。是时候尝试执行 Newman 系列中的所有内容了。

要通过 Newman 运行一个系列，我们有两种方法可以继续。

-   通过分享链接
-   通过 Json 文件

我们将使用这两种方法运行集合。

## 通过共享链接使用 Newman 运行集合

1.单击集合名称旁边的箭头。

![Newman_Collection_Arrow 系列](https://www.toolsqa.com/gallery/Postman/1.Newman_Collection_Arrow.png)

1.  单击共享。

![分享_纽曼_合集](https://www.toolsqa.com/gallery/Postman/2.Share_Newman_Collection.png)

1.  点击获取链接

![获取链接](https://www.toolsqa.com/gallery/Postman/3.Get_Link.png)

1.  复制此链接

![复制链接_Newman](https://www.toolsqa.com/gallery/Postman/4.Copy_Link_Newman.png)

5.打开你的shell(windows命令提示符和mac终端)

注意：从现在开始，我们将使用术语 shell，它是终端的技术术语。

1.  输入以下内容：

纽曼跑`<link>`

![Comman_Prompt_Newman_Run](https://www.toolsqa.com/gallery/Postman/5.Comman_Prompt_Newman_Run.png)

注意：请输入你自己的链接，该链接与我们使用的上述链接不同。

1.  按回车键。

如果你看到以下屏幕，则你的收藏已成功执行

![使用 Newman 运行集合](https://www.toolsqa.com/gallery/Postman/6.Running%20Collection%20Using%20Newman.png)

执行集合后，你将看到我们在 Postman 中的集合运行程序中看到的测试详细信息。我们对每个请求都进行了一次测试，因此我们会相应地看到结果。从上图也可以看出，细节与collection runner类似。我们可以看到每个请求的响应状态(时间、大小和状态代码)以及我们执行的测试脚本。

由于在天气 API 请求中我们断言状态代码为 200，事实证明这是正确的，但在客户注册 API 中并非如此。在客户注册 API 中，我们断言响应时间小于 200 毫秒，结果是 535 毫秒，因此是错误的。也可以通过customer register api下的红线看到。

现在，让我们尝试更改客户 API 中的相同断言并查看结果。

### 如何更新链接

1.转到客户注册API。

2.在测试中将时间更改为1500ms。

![客户_API_测试](https://www.toolsqa.com/gallery/Postman/7.Customer_API_Test.png)

3.保存请求并再次运行相同的命令并按回车键。

![Command_Prompt_Newman_Executed_2](https://www.toolsqa.com/gallery/Postman/8.Command_Prompt_Newman_Executed_2.png)

等待！这与我们预期的结果不同。这次响应时间是 750 毫秒，小于 1500 毫秒，但我们仍然使用相同的断言行得到相同的错误，即响应时间小于 200 毫秒。

发生这种情况是因为链接未更新。如果你想再次运行它，那么你需要按照我们上面所做的相同步骤来获取链接。

这一次，点击更新链接。

![更新链接](https://www.toolsqa.com/gallery/Postman/9.Update_Link.png)

复制新链接并再次在命令提示符 ( windows ) 中运行相同的命令。

![更新链接执行](https://www.toolsqa.com/gallery/Postman/10.Update_Link_Execution.png)

瞧！我们现在已经从我们的测试中得到了更新的和预期的结果。因此，请务必记住在进行更改后更新链接。但这有一个问题。在团队中工作时，这是行不通的。该链接只是邮递员的快照。一旦你在 Postman 中更新了一些东西，它不会自动更新，直到你更新链接。在团队中工作时，变化总是在发生，通过链接运行会使你的 API 更容易出错。因此，还有另一种通过 Newman 测试 API 的方法。

## 通过 JSON 文件使用 Newman 运行集合

1.现在，为了获得错误，我们将客户注册 API 中的响应时间更改为 100 毫秒。 保存此 API。

![Customer_Register_API_Time_100ms](https://www.toolsqa.com/gallery/Postman/11.Customer_Register_API_Time_100ms.png)

2.单击集合名称旁边的导出链接([在 Postman 中的集合中](https://toolsqa.com/postman/collections-in-postman/)学习)，然后 在以下面板中单击导出。

![出口面板](https://www.toolsqa.com/gallery/Postman/12.Export_Panel.png)

注意：始终使用 collection v2.1，如[Postman 中的 Collections](https://toolsqa.com/postman/collections-in-postman/)章节中所推荐的那样。

1.  将 json 文件保存在你的系统中并记住目录。

![导出_收集_保存](https://www.toolsqa.com/gallery/Postman/13.Export_Collection_Save.png)

1.  保存 json 文件后，访问系统的 shell并将当前目录更改为保存此 json 文件的目录。

例如：如果你将 json 文件保存在 C:\harish 中，则将目录更改为 C:\harish

1.  更改目录后，运行以下命令

纽曼跑`<name of the file>`

![Running_Json_Collection](https://www.toolsqa.com/gallery/Postman/14.Running_Json_Collection.png)

注意：请记住将文件名放在引号中，否则 shell 会将其视为目录名。

1.  按回车，你会看到你收藏纽曼收藏的预期结果

![纽曼_Run_Through_Export](https://www.toolsqa.com/gallery/Postman/15.Newman_Run_Through_Export.png)

至此，我们已经成功地通过纽曼执行了集合。如上节所述，错误是可以自我理解的。与 Newman 一起工作并从 shell 而不是 Postman 本身运行我们的第一个集合非常有趣。

也许你想知道我们在上一个教程中讨论的 CI 部分。没有什么是持续整合的。你只是自己编写了一些东西并在 Newman 中对其进行了测试。是的。你说的对。这不是我们使用 Newman 的方式，同时还进入 CI 将其集成到主要构建过程中。通过这种方法，我们只能检查 API，测试它们，如果发现任何错误，我们可以将其报告给我们团队的问题线程。虽然总是导出并再次运行它很忙，但可以使用主构建测试 Postman 集合，这是我们通过 Newman 进行测试的第三种方法。这就是同时使用 CI 的方法。我希望我们清楚这些概念，我们将进入下一个教程。
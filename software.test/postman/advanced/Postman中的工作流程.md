到目前为止，我们已经了解了如何在一个请求上[运行测试](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)，以及如何在[Collection runner](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)中同时运行多个请求和多个测试。collection runner 是 Postman 中一个非常重要的特性，如果没有它，一个人将很难执行多个请求并同时对它们执行不同的测试。但是，如果你注意到收集运行程序如何运行请求有一个模式。如果没有，我们将在本教程中进行，并考虑对其进行详细修改。在本章中，我们将重点关注：

-   collection runner 的执行顺序
-   什么是工作流程和默认工作流程。
-   如何改变工作流程
-   工作流中的无限循环
-   setNextRequest 在 Postman 中的放置

### Collection Runner 的执行顺序

正如我们上面所讨论的，collection runner 以特定的顺序运行请求。如果你之前注意到，它是顺序运行。我们需要顺序运行来测试端到端的 api 流，以检查一切是否正确。端到端测试是为了检查应用程序从开始到结束的流程，就像它设计的那样。大多数用户旅程都不是简单的一两个 API 调用。通常，用户必须完成一组操作，进而进行一组 API 调用，才能完成用户旅程。以电子商务网站为例，你必须执行许多步骤才能购买商品。通用流程可以概括为：

-   登录网站
-   搜索产品
-   将产品添加到购物车
-   签出
-   添加地址等个人详细信息
-   支付

这就是收集运行器发挥作用的地方，它是一个非常有用的工具。我们可以在收集运行器中定义步骤序列(API 调用)。Collection runner 将按顺序运行调用，从而完成业务需求中定义的用户旅程。

## Postman 中的默认工作流程

以下简短示例将突出显示 Postman 运行请求的方式

1.创建一个名为No Workflow的新集合([How to create Collections in Postman](https://toolsqa.com/postman/collections-in-postman/))

2.在其中输入三个请求，我们在之前的教程中一直使用的请求。将它们命名为Google Api、[Weather Api](https://toolsqa.com/postman/get-request-in-postman/)和[Customer Api](https://toolsqa.com/postman/post-request-in-postman/) 。

![Postman 中的工作流程](https://www.toolsqa.com/gallery/Postman/1.Workflows%20in%20Postman.png)

注意：记住我们是如何使用[Environment 和 Variables](https://toolsqa.com/postman/environment-variables-in-postman/)的，我们在这里使用url作为全局变量，其值为 http://restapi.demoqa.com。.

3.运行运行器(Collection Runner)并查看输出。

![No_Workflow_Runner](https://www.toolsqa.com/gallery/Postman/2.No_Workflow_Runner.png)

注意：我们对客户 api 使用了与[POST 章节](https://toolsqa.com/postman/post-request-in-postman/)中相同的测试。你可以使用它或保留它，但这对你的练习有好处。

它从第一个请求开始，然后继续到第二个，然后是第三个。它是顺序运行的。但是，如果一段时间后不需要第二次请求怎么办？假设你有一个包含 30 个有意义的请求的集合。几天后，我们发现该请求没有。16 需要在请求号之后运行。19 否则我们不会得到正确的结果。现在，这会产生一个问题。你不能上下移动请求，因为其他请求可能会在此过程中移动。上下移动请求成为一个耗时的过程。

### 什么是工作流程？

工作流是为了完成特定任务而进行的一系列事情。工作流是一组明确定义的步骤，你必须采取这些步骤才能完成任务。在邮递员工作流中，请求流是按定义的顺序进行的。我们讨论的上述问题有一个预定义的模式，即按顺序运行请求。

## 如何更改 Postman 中的工作流程。

在邮递员中，你可以很容易地根据需要安排请求的运行。当你创建多个请求时，默认工作流程是顺序的，即所有请求将在你创建它们时运行(考虑到你没有移动任何请求)。可以在 Postman 中轻松更改工作流程。为此，请执行以下步骤。

1.新建一个Collection，命名为With Workflow

2.在其中输入我们上面提到的所有三个请求，并按照我们提到的顺序输入。

![With_Workflow](https://www.toolsqa.com/gallery/Postman/3.With_Workflow.png)

注意：不要再次执行所有工作，而是尝试复制集合No Workflow并将其重命名为With Workflow([集合章节](https://toolsqa.com/postman/collections-in-postman/))

3.转到Google Api请求并在测试选项卡中输入以下内容

postman.setNextRequest("客户API")

![设置下一个请求](https://www.toolsqa.com/gallery/Postman/4.SetNextRequest.png)

我相信这是一个自我描述的陈述。你是在告诉 Postman设置下一个请求。在大括号中，你需要始终写下请求的名称。

注意：请求的名称应该与你在命名请求时所写的完全相同。它们区分大小写。

4.点击Runner并运行请求

![With_Workflow_Runner](https://www.toolsqa.com/gallery/Postman/5.With_Workflow_Runner.png)

注意现在请求的运行顺序。这改变了集合的工作流程，因此现在我们可以按照我们想要的顺序运行集合。观察到这次有一个请求没有运行，即 weather api。发生这种情况是因为在 Google Api 中我们提到了下一个请求，但是在客户 api 之后要运行什么？我们没有提到它，所以 Postman 尝试在没有工作流的情况下按顺序运行它，但由于客户 api 是我们的最后一个请求，它就停在那里。

工作流在 Postman 中具有重要而强大的功能。在使用 Postman 的工程师中，这是一个非常受欢迎的功能。在 Postman 中使用它时，当需要更改请求时，你会发现它非常方便。但是，我们在测试选项卡中设置下一个请求时需要小心。有时，你可能会陷入死循环。为了展示这一点，让我们创建一个新的工作流，如下一节所述

## 工作流中的无限循环

为了了解有时我们会因为一个简单的错误而陷入无限循环，我们将在谷歌上进行三个搜索请求，即ToolsQA、Postman和Calculator，它们将分别搜索 ToolsQA 、 Postman 、 Calculator 。

1.新建一个名为Infinite Workflow的Collection ，在我们刚刚创建的collection中保存以下三个api请求

-   https://www.google.co.in/search?q=toolsqa并将其命名为ToolsQA
-   https://www.google.co.in/search?q=postman并将其命名为Postman
-   https://www.google.co.in/search?q=calculator并将其命名为Calculator

![无限工作流集合](https://www.toolsqa.com/gallery/Postman/6.Infinite_Workflow_Collection.png)

2.转到ToolsQA请求并将下一个请求设置为计算器

![工具QA_Script](https://www.toolsqa.com/gallery/Postman/7.ToolsQA_Script.png)

3.转到计算器并将下一个请求设置为邮递员

![计算器脚本](https://www.toolsqa.com/gallery/Postman/8.Calculator_Script.png)

1.  转到Postman并将下一个请求设置为ToolsQA

![邮差脚本](https://www.toolsqa.com/gallery/Postman/9.Postman_Script.png)

1.  在集合运行器中运行集合无限工作流并查看运行器([如何在集合运行器中运行集合](https://toolsqa.com/postman/test-and-collection-runner-in-postman/))

![无限循环赛跑者](https://www.toolsqa.com/gallery/Postman/10.Infinite_Loop_Runner.png)

它现在在无限循环中运行。继续观察每个请求的测试选项卡，看看我们将循环设置为无限运行的位置。

尽管如果你被困在这里，那么你的工作流程肯定有问题。看 runner 是没有意义的，因为它会永远运行并最终挂起。最好停止运行程序以节省内存和其他资源(例如服务器负载)。只需单击停止按钮即可停止跑步者

![停止跑步者](https://www.toolsqa.com/gallery/Postman/11.Stop_Runner.png)

### setNextRequest 的放置

你可能想知道如果有很多测试代码怎么办？你应该将 setNextRequest 放在哪里才能成功运行它？ 在 tests 中编写 setNextRequest 时，放置它并不重要。你可以把它写在任何你喜欢的地方，它仍然会运行。这只是一种对邮递员说一旦你完成了所有测试和其他事情的方法，当你离开这个请求时你必须运行这个请求。你把它放在哪里并不重要，因为它就像一个命令。

因此，让我们在这里结束本教程。我们希望到目前为止，你一定已经了解了很多关于 Postman 的知识以及它的神奇之处。我们现在将继续下一个教程并记住，始终保持练习。
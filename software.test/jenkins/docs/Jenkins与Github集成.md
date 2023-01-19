Jenkins 是DevOps时代重要的[CI/CD](https://www.atlassian.com/continuous-delivery/principles/continuous-integration-vs-delivery-vs-deployment)工具之一。正如我们已经知道的，我们可以将Jenkins与版本控制工具、构建工具、持续集成、配置管理工具等集成。因此，现在让我们开始Jenkins与最流行的版本控制工具之一(即[Git](https://www.toolsqa.com/git/git-tutorial/) )集成的旅程。在本文中，我们将通过涵盖以下主题中的详细信息来了解Jenkins GitHub 集成的详细信息：

-   什么是 Git 以及为什么需要它？
-   如何将 Jenkins 与 GitHub 集成？
    -   为什么需要 GitHub 与 Jenkins 集成？
    -   Jenkins 如何与 GitHub 集成？
    -   如何在 Jenkins 中自动触发构建？

## 什么是 Git 以及为什么需要它？

众所周知，[Git](https://www.toolsqa.com/git/what-is-git/)是一个开源的[分布式版本控制系统](https://www.toolsqa.com/git/distributed-version-control-systems/)，用于在项目开发过程中跟踪和管理源代码。与SVN 和 CVS等其他[版本控制系统](https://www.toolsqa.com/git/distributed-version-control-systems/)相比，Git 以不同方式存储文件中的更改。这是你应该尽早内化的Git重要概念之一。版本控制系统存储两个版本之间的差异。例如，考虑 文件 A 那被改变了三遍。文件的第一个版本将按原样存储，从某种意义上说，完整的文件将被存储。随着新版本的推出，只会保存与先前版本的差异。

另一方面， Git存储更改文件的快照。例如，如果你对文件 A进行了更改，则会存储更改文件的完整快照。 如果一个文件在两个版本之间没有变化，Git 将保留对原始文件的引用，而不是在新版本中再次复制它。

Git有一个存储在服务器上的远程存储库和一个存储在每个开发人员计算机中的本地存储库。这意味着代码不仅存储在中央服务器上，而且代码的完整副本也存在于所有开发人员的计算机中。因为每个节点都有一个本地副本，所以几乎所有对Git的操作都是本地的。这意味着你不必一直连接到远程存储库来完成你的工作。

自从Git的开发和发布以来，它在开发人员中获得了巨大的普及，并且开源包含了许多特性。今天，数量惊人的项目使用Git进行版本控制，包括商业项目和个人项目。考虑到流行度和使用率，Jenkins还提供了一种与 Git 上的代码片段挂钩的机制。让我们看看我们可以执行哪些步骤来成功集成 Jenkins GitHub？

## 如何将 Jenkins 与 GitHub 集成？

在上一节中，我们讨论了 Git 的必要性。所以现在是时候了解Jenkins GitHub 集成的必要性了。让我们在以下小节的帮助下了解详细信息：

### 为什么需要 GitHub 与 Jenkins 集成？

在真实的 IT 世界中，每个人都在他的本地机器上工作，但最终，他们需要将他们的完整代码推送到某个中央存储库，例如 GitHub 存储库，从那里他们可以运行完整的项目。因此，GitHub是一个中央存储库，我们的完整代码远程驻留在其中。请访问 ToolsQA 网站以获取有关[GitHub](https://www.toolsqa.com/git/github/)的更多信息。

现在，我们将GitHub存储库集成到 Jenkins，因为GitHub与Jenkins的集成自动化了部署过程、测试过程并提高了最终产品质量，因为它为开发人员和测试人员节省了大量时间。每次GitHub存储库中发生新的提交时，Jenkins都会自动触发构建并向我们提供构建结果。因此，要自动化整个部署过程以及测试过程，将 GitHub 与 Jenkins 集成非常重要。现在让我们在下一节中了解Jenkins GitHub 集成是如何工作的？

### Jenkins 如何与 GitHub 集成？

在上一节中，我们讨论了GitHub与Jenkins集成的必要性。现在，在本节中，让我们从如何将Jenkins与GitHub集成的实际实现开始我们的旅程。作为其中的一部分，我们需要安装Jenkins Git 插件。

#### 如何在 Jenkins 中安装 Git 插件？

让我们从在 Jenkins 中安装Git 插件开始。为此，请按照以下步骤操作：

第 1 步：启动Jenkins并重定向到Jenkins 仪表板。现在点击下面突出显示的“管理Jenkins”链接：

注意：要查看有关如何安装和运行 Jenkins 的详细信息，请参阅我们的文章[安装 Jenkins](https://www.toolsqa.com/jenkins/install-jenkins/)

![Jenkins 仪表板 - 管理 Jenkins](https://www.toolsqa.com/gallery/Jenkins/1.Jenkins%20Dashboard%20-%20Manage%20Jenkins.jpg)

第 2 步：一旦我们单击“管理 Jenkins ”链接，我们将被重定向到“管理 Jenkins”页面。现在单击下面突出显示的“管理插件”链接：

![管理 Jenkins - 管理插件](https://www.toolsqa.com/gallery/Jenkins/2.Manage%20Jenkins%20-%20Manage%20Plugins.jpg)

第 3 步：一旦我们单击“管理插件”链接，我们将被重定向到“插件管理器”页面。

![插件管理器页面](https://www.toolsqa.com/gallery/Jenkins/3.Plugin%20Manager%20Page.jpg)

-   如果尚未安装 git 插件，则单击“可用”选项卡。
-   在 Filter 框中键入“ Git Plugin ”，并在其出现后选中Git Plugin复选框。
-   选择“不重启安装”或“立即下载并在重启后安装”按钮。如果我们选择“ Install without restart ”选项，那么它将在不重启 Jenkins 的情况下安装插件，如果我们选择“立即下载并在重启后安装”，那么 Git 插件将在重启 Jenkins 后安装。

第 4 步：现在安装Git 插件后，我们还需要验证它。如需验证，请按照以下步骤操作：

![Jenkins github 与 Git 插件的集成](https://www.toolsqa.com/gallery/Jenkins/4.Jenkins%20github%20integration%20with%20Git%20plugin.jpg)

-   单击“已安装”选项卡。
-   在过滤器框中键入“ Git 插件”。

现在执行上述步骤后，如果 Git 插件安装正确，它将出现在“已安装”选项卡下，如上图中红色矩形突出显示的那样。因此，现在Jenkins Git 插件已与 Jenkins 正确集成。在下一节中，我们将看到如何在 Jenkins 中拉取 GitHub 存储库？

#### 如何在 Jenkins 中拉取 GitHub 存储库？

在上一小节中，我们已经安装了Jenkins Git 插件。因此，是时候在 Jenkins 中对 GitHub 存储库进行配置，以便 Jenkins 自动从GitHub存储库中提取代码，运行它并给出所需的结果。我们已经将代码推送到[GitHub 存储库](https://github.com/toolsqa17061989/DemoJava.git)。请按照以下步骤在 Jenkins 中配置 GitHub 存储库：

注意：请访问 ToolsQA 网站上的[Git 教程](https://www.toolsqa.com/git/git-tutorial/)，以便在我们的机器上安装 Git 以及将代码推送到存储库。

第 1 步：转到Jenkins 仪表板并单击“新建项目”链接以创建一个以红色矩形突出显示的新作业。

![点击新项目](https://www.toolsqa.com/gallery/Jenkins/5.Click%20on%20new%20item.jpg)

第 2 步：现在为选择的项目进一步执行以下步骤：

![项目选择](https://www.toolsqa.com/gallery/Jenkins/6.Selection%20of%20Project.jpg)

-   输入项目名称。
-   选择我们选择的“自由式项目”的项目类型。
-   单击确定按钮。

第 3 步：一旦我们单击“确定”按钮，我们将被重定向到配置页面。在这里我们可以放置我们项目的描述。

![项目描述](https://www.toolsqa.com/gallery/Jenkins/7.Description%20of%20Project.jpg)

第 4 步：现在只需向下滚动并转到“源代码管理”部分。现在，选择“ Git ”选项。

注意： 这里显示 Git 选项是因为我们在 Jenkins 中安装了 Git 插件，但是如果 Git 选项没有出现在这里，那么我们需要再次检查是否正确安装了 Git 插件。

![Jenkins github 集成源代码管理部分使用 Git 插件](https://www.toolsqa.com/gallery/Jenkins/8.Jenkins%20github%20integration%20%20Source%20Code%20Management%20section%20using%20Git%20Plugin.jpg)

一旦我们选择Git 选项，我们就会看到上面的对话框，我们将在其中提供存储库相关信息。

第 5 步：现在输入存储库 URL，如下图所示：

![9.Jenkins github集成：在Jenkins Build中配置GitHub仓库](https://www.toolsqa.com/gallery/Jenkins/9.Jenkins%20github%20integration%20Configure%20GitHub%20repository%20in%20Jenkins%20Build.png)

第 6 步：现在，转到构建触发器部分并选择选项“ GitHub hook trigger for GITScm polling ”。

![Jenkins github 集成：在 GitHub 轮询上触发构建](https://www.toolsqa.com/gallery/Jenkins/10.Jenkins%20github%20integration%20Trigger%20Build%20on%20GitHub%20Polling.png)

第 7 步：转到构建部分。现在单击“添加构建步骤”下拉列表后单击“执行 Windows 批处理命令”。我们正在使用执行 Windows 批处理命令选项，因为我们在步骤 8 中提到的命令的帮助下运行一个简单的 Java 程序。

![选择 Windows 批处理命令选项](https://www.toolsqa.com/gallery/Jenkins/11.Select%20windows%20batch%20command%20option.jpg)

第 8 步：现在我们将编写批处理命令来执行此Java程序。

```java
cd <locate the package for source code>
javac Basic/Hello_ToolsQA.java
java Basic/Hello_ToolsQA
```

让我们理解上面代码的每一行：

-   在第一行，我们需要转到项目的 src 目录。
-   在第二行中，我们需要借助 javac 命令编译我们的类文件。请注意，我们需要提供包名/类名等路径。
-   第三行运行我们的类文件并给出输出。在这里，路径也像package name/class name。

现在点击保存按钮。

![Windows批处理命令运行java程序](https://www.toolsqa.com/gallery/Jenkins/12.Windows%20batch%20command%20to%20run%20the%20java%20Program.jpg)

只要我们单击“保存”按钮，我们的作业就会正确设置以执行。

### 如何在 Jenkins 中自动触发构建？

在上一节中，我们看到我们已经设置了一个作业。现在是时候了解我们如何在 Jenkins 中自动触发构建了。在这里，webhooks的概念出现了。因此，在本节中，我们将了解如何在 GitHub 中设置一个webhook并自动触发我们在 Jenkins 中的工作。请按照以下步骤实现此部分：

第 1 步：转到GitHub存储库并单击红色矩形突出显示的设置链接。

![点击设置按钮](https://www.toolsqa.com/gallery/Jenkins/13.click%20on%20settings%20button.png)

当我们单击“设置”链接时，我们将重定向到“设置”页面。

第 2 步：单击列出的Webhooks选项，如下突出显示：

![Jenkins github 集成：点击 webhooks](https://www.toolsqa.com/gallery/Jenkins/14.Jenkins%20github%20integration%20Click%20on%20webhooks.png)

第 3 步：一旦我们单击Webhooks，我们将重定向到Webhooks页面。现在，单击下图中突出显示的“添加 webhook ”按钮。

![Jenkins github 集成：在 GitHub 中添加 webhook](https://www.toolsqa.com/gallery/Jenkins/15.Jenkins%20github%20integration%20Add%20webhook%20in%20GitHub.png)

单击添加 webhook按钮后，我们需要提供有关 webhook 的信息。

第四步：我们需要填写的第一个信息是“ Payload URL ”。在这里，我们需要提供我们正在使用的Jenkins URL。关于这方面的重要事情是我们不能使用Jenkins 的本地主机 URL，因为它不在互联网上。因此，我们需要提供一个在互联网上的URL。为了实现它，我们需要根据我们的操作系统在我们的系统[中安装ngrok 。](https://ngrok.com/download)

![下载 ngrok：本地 Jenkins 与 GitHub 集成所需](https://www.toolsqa.com/gallery/Jenkins/16.download%20ngrok%20Needed%20for%20local%20Jenkins%20integration%20with%20GitHub.png)

第 5 步：现在保存下载的 zip 文件，将其存储在一个目录中，然后解压缩。一旦我们解压缩它，我们就会看到ngrok.exe文件。单击ngrok.exe启动应用程序。

![ngrok 终端窗口](https://www.toolsqa.com/gallery/Jenkins/17.ngrok%20terminal%20window.png)

第 6 步：在终端窗口中键入以下命令，然后按ENTER 键。

```
ngrok.exe http 8080
```

![Jenkins github 集成：本地 Jenkins 需要 ngrok 连接](https://www.toolsqa.com/gallery/Jenkins/18.Jenkins%20github%20integration%20ngrok%20connected%20needed%20for%20local%20Jenkins.png)

在这里，请参阅上图中突出显示的 URL。此外，我们将在后续步骤中将此部分用作有关端口 8080的Payload URL的一部分。

第 7 步：现在执行以下步骤在 GitHub 中设置 webhook。

-   将有效负载 URL 放入文本框中。请注意不要忘记在最后附加文本github-webhook/。
-   单击“仅推送事件”选项。
-   请确保选中“活动”复选框。
-   单击“添加 webhook ”按钮。

![Jenkins github 集成：Webhook 配置](https://www.toolsqa.com/gallery/Jenkins/19.Jenkins%20github%20integration%20Webhook%20configuration.png)

单击添加 webhook按钮后，我们将看到一条成功消息。

![配置成功消息](https://www.toolsqa.com/gallery/Jenkins/20.successful%20configuration%20message.png)

第 8 步：现在开始编写代码，进行一些更改，然后再次提交我们的代码。一旦我们提交更改，webhook 就会向Jenkins发送通知，并触发自动构建。

![构建自动触发](https://www.toolsqa.com/gallery/Jenkins/21.Build%20automatically%20triggered.png)

第 9 步：成功运行构建后，我们可以在控制台输出中看到结果。

![控制台输出](https://www.toolsqa.com/gallery/Jenkins/22.console%20output.png)

现在单击红色矩形中突出显示的立即构建链接。

![单击立即构建链接](https://www.toolsqa.com/gallery/Jenkins/23.Click%20on%20Build%20now%20link.jpg)

第十步：运行成功后，我们可以看到下图中红色矩形框内突出显示的Build History部分下的蓝色标志之后，单击“ Console Output ”以查看实际输出。

![构建成功和控制台输出](https://www.toolsqa.com/gallery/Jenkins/24.Build%20success%20and%20console%20output.jpg)

如果构建成功，我们可以在控制台输出中看到一条成功消息。

![构建成功](https://www.toolsqa.com/gallery/Jenkins/25.successful%20build.png)

因此，通过使用Git 插件提取代码并使用WebHooks触发作业，我们成功地以两种方式集成了 Jenkins 和 GitHub 。

## 要点：

-   [Git](https://www.toolsqa.com/git/git-tutorial/)是一个开源的分布式版本控制系统，用于在项目开发过程中跟踪和管理源代码。
-   GitHub 与 Jenkins 的集成自动化了部署过程、测试过程并提高了最终产品质量，因为它为开发人员和测试人员节省了大量时间。
-   我们需要 Git 插件才能在 Jenkins 中启用 Git 功能。
-   此外，我们将代码推送到 GitHub 存储库，以便 Jenkins 可以从存储库中提取代码并执行它。
-   我们可以借助 GitHub 中的 Webhooks 在 Jenkins 中自动触发构建。
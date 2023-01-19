在前面的几个教程中，我们学习了如何 在 Jenkins 上[运行命令](https://toolsqa.com/postman/configure-jenkins-job-to-run-batch-command/)、[运行集合](https://toolsqa.com/postman/run-postman-collection-on-jenkins/)和[发布报告。](https://toolsqa.com/postman/generate-newman-reports-on-jenkins/)在[Postman with Newman 和 Jenkins](https://toolsqa.com/postman/postman-with-newman-jenkins/)教程中，我们向你介绍了 Jenkins，并特别指出 Jenkins 的主要功能来自于它的插件。我们还告诉我们，许多开发人员在 Jenkins 中使用 freestyle 项目的原因是因为许多插件是为 freestyle 项目开发的，使其比其他项目更强大和灵活。所以，是时候了解插件并尝试通过 Jenkins 复习一下了。在Publish Jenkins HTML Reports for Newman 教程中，我们将遇到

-   插件
-   安装插件
-   使用 HTML 报告插件生成报告

## 什么是插件？

插件作为一个通用术语，是指为软件添加特定功能的软件组件。例如，你可能对系统中的以下屏幕非常熟悉。

![adobe-flash-插件](https://www.toolsqa.com/gallery/Postman/1.adobe-flash-plugin.png)

当网站有视频但由于缺少插件(更常见的是Adobe Flash Player)而无法播放时，就会发生这种情况。这个adobe flash player正在增加浏览器的功能，方便我们观看视频。同样，Jenkins 中有许多可用的插件。成千上万的人。这些插件将 Jenkins 的功能提升到一个更高的水平，这使得 Jenkins 在其领域如此著名和强大。它们有不同的用途，你可以根据你的用途下载插件。如果你认为它通过将功能作为插件分发而不是将它们组合在一个软件中来减小软件的大小，那么你是对的。我们现在将讨论这些。

### 为什么在软件中使用插件？

如上一节所述，插件用于增加软件的功能并使其更加灵活、强大和方便使用。但除此之外还有其他优点：

-   第三方开发人员：如果你是第三方开发人员并且不直接参与软件的生产，你可以创建一个插件来根据你的需要增加软件的功能。这也将帮助其他开发人员。
-   新增功能：插件开发完成后，你可以轻松享受插件带来的功能。这非常简单，你无需一次又一次地更新软件。
-   减小尺寸：插件减小了软件的尺寸。正如上面关于 Jenkins 的讨论，一个软件可以提供数以千计的功能。如果我们将所有这些功能组合到一个单独的软件包中，它将导致更大的尺寸，并且没有测试人员会使用所有这些功能。因此，使用插件以便测试人员/开发人员可以根据他的用途安装插件，从而减小了大小。

所以我猜你现在已经非常了解插件了。现在，我们将通过在 Jenkins 中使用插件来让你更加熟悉它们。

## 在 Jenkins 中安装插件

在本教程中，我们将在 Jenkins 中发布 HTML 报告。它的过程与我们之前发布的 JUNIT 报告类似。如果你还记得在 Jenkins 中生成报告时在构建后操作下拉列表中，我们在构建后命令中选择了发布 Junit 操作。但是，如果我们找到 HTML 报告选项，它不存在。因此，我们需要安装一个插件。

先决条件

-   詹金斯应该启动并运行。参考教程[如何安装和启动 Jenkins](https://toolsqa.com/jenkins/install-jenkins/)
-   Jenkins 作业配置为运行 Postman Collection。参考教程[如何在 Jenkins 中运行 Postman Collection](https://toolsqa.com/postman/run-postman-collection-on-jenkins/)
-   了解在 Jenkins 中发布报告。参考教程[How to generate reports in Jenkins](https://toolsqa.com/postman/generate-newman-reports-on-jenkins/)

### 如何从 Jenkins 中安装插件

要在 Jenkins 中安装插件，请按照简单的步骤操作

1.点击Jenkins Dashboard中的Manage Jenkins

![管理詹金斯](https://www.toolsqa.com/gallery/Postman/2.Manage_Jenkins.png)

1.  点击管理插件

![管理插件_詹金斯](https://www.toolsqa.com/gallery/Postman/3.Manage_Jenkins.png)

这将打开此面板

![插件_仪表板_Jekins](https://www.toolsqa.com/gallery/Postman/4.Plugin_Dashboard_Jekins.png)

1.  如图所示，此面板有 4 个选项卡。一个用于可用更新，另一个用于可用插件(所有插件)，已安装等。单击“可用”选项卡以打开所有可用插件。在搜索栏中搜索HTML Publisher。这将动态显示结果。

![为 Newman 发布 Jenkins HTML 报告](https://www.toolsqa.com/gallery/Postman/5.Publish%20Jenkins%20HTML%20Reports%20for%20Newman.png)

1.  选中HTML Publisher并单击Install without restart

![安装插件](https://www.toolsqa.com/gallery/Postman/6.Install_Plugin.png)

1.  你现在将看到插件已开始安装。

![安装_插件_屏幕](https://www.toolsqa.com/gallery/Postman/7.INstalling_Plugin_Screen.png)

1.  安装插件后，它将显示为Success而不是 Pending。

### 如何从系统中安装插件

还有另一种安装插件的方法。由于插件是第三方的，你可以自己创建一个，因此你需要将其上传到 Jenkins。我们可以通过以下步骤来做到这一点。

1.  转到管理 Jenkins-> 管理插件。
2.  单击高级选项卡

![Advanced_Tab_Plugins](https://www.toolsqa.com/gallery/Postman/8.Advanced_Tab_Plugins.png)

1.  转到上传插件

![上传插件](https://www.toolsqa.com/gallery/Postman/9.Upload_Plugin.png)

1.  Jenkins 接受hpi扩展中的插件。你有时可能会有.zip文件。你可以将它们重命名为 .hpi 文件并点击上传

![Upload_Plugin_With_File](https://www.toolsqa.com/gallery/Postman/10.Upload_Plugin_With_File.png)

插件将被上传。

## 如何为 Newman 发布 Jenkins HTML 报告？

现在我们已经安装了插件，我们将尝试使用该插件。我们已经详细解释了 Jenkins 中的命令和生成报告。你可以回顾那些教程来回忆基本的事情。在本教程中，我们将直接从构建后命令开始。

就像 JUNIT 报告命令一样，HTML 命令也类似。

-   生成 HTML 报告的命令： -reporters html
-   导出 HTML 报告的命令： --reporter-html-export :"newman/myHTMLreport.html"

在继续编写 JUNIT 命令的命令后，你的构建命令框应如下所示

![HTML_Commands_Batch](https://www.toolsqa.com/gallery/Postman/11.HTML_Commands_Batch.png)

1.单击Post-Build Actions下拉列表中的Publish HTML reports 。

![选择_发布_报告_HTML](https://www.toolsqa.com/gallery/Postman/12.Select_Publish_Report_HTML.png)

1.  单击发布 HTMl 报告后，将立即添加一个新面板。点击添加

![添加_报告_HTML](https://www.toolsqa.com/gallery/Postman/13.Add_Report_HTML.png)

1.  在你选择的目录和报告名称中写入newman。

![Adding_Report_Paramters_HTML](https://www.toolsqa.com/gallery/Postman/14.Adding_Report_Paramters_HTML.png)

1.  单击保存并在仪表板中构建它。
2.  构建完成后，立即刷新页面。
3.  现在在仪表板上可以看到一个新元素

![HTML_Report_Dashboard](https://www.toolsqa.com/gallery/Postman/15.HTML_Report_Dashboard.png)

该报告将以 HTML 格式显示给你。因此，通过结束本教程，我们就完成了与 Newman 和 Jenkins 一起运行 Postman 的旅程。你可以自行探索 Jenkins 以发现它提供的一些强大功能。我们现在将继续下一节。
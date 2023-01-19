[与之前的如何在 Jenkins 上运行集合](https://toolsqa.com/postman/run-postman-collection-on-jenkins/)的教程一样，我们使用 Newman 命令在Jenkins 上 运行Postman 集合。但是看Console Output日志和分析结果有点不方便。出于这个原因，Jenkins 有很多不同的报告插件可以免费使用。使用这些插件，测试结果以图形报告的形式生成。在下一节中，我们将简要了解如何 在 Jenkins 上生成 Newman 报告，我们将涵盖以下主题：

-   生成 CLI & JUNIT Newman 报告
-   指定保存纽曼报告的位置
-   查看纽曼报告
-   发布报告
-   生产 Jenkins 构建测试趋势

### 詹金斯报告

Jenkins 中的报告是一种结构化和图形化的方式，我们可以在其中查看执行结果或测试输出。当我们必须与我们的团队成员或其他利益相关者交流结果时，报告也更容易发挥作用。如上所述，有许多插件可用。简单方便，值得推荐。Jenkins 报告不是图形化的，但可以生成详细的书面报告。该书面报告可以采用不同的格式，并且可以通过与构建命令一起指定来管理这些格式。在 [Newman](https://github.com/postmanlabs/newman#using-reporters-with-newman)的官方文档中，很少指定报告生成命令。

## 如何在 Jenkins 上生成 Newman 报告？

知道我们需要指定报表的输出路径非常重要，否则报表将自动保存在某个地方。Newman中有不同类型的报告器，但我们将 在本教程中使用CLI和JUNIT报告器。

先决条件

-   詹金斯应该启动并运行。参考教程 [How to Install & Start Jenkins?](https://toolsqa.com/jenkins/install-jenkins/)
-   Jenkins Job 配置为运行 Postman Collections。请参考教程 [如何在 Jenkins 上运行 Postman Collections？](https://toolsqa.com/postman/run-postman-collection-on-jenkins/)

我希望你在 Jenkins 作业/项目中并且在你的系统中可以看到以下屏幕。

![项目_工作区_詹金斯](https://www.toolsqa.com/gallery/Postman/1.Project_Workspace_Jenkins.png)

### 如何在 Jenkins 上生成 CLI & JUNIT Newman 报告？

导航到Configure --> Build部分，并在使用 Newman 运行收集的命令的延续中编写以下命令。

-   使用 Newman 运行集合的命令：newman run`<Collection share link>`
-   生成 CLI 和 JUNIT 报告的命令：- -reporters cli,junit

![记者_CLI_JUNIT_Jenkins](https://www.toolsqa.com/gallery/Postman/2.Reporters_CLI_JUNIT_Jenkins.png)

注意：请务必注意“--reporters”后只有一个空格。cli,junit 应该没有任何空格。还值得注意的是，当你按下回车键并编写下一个命令时，Jenkins 不会读取第三行，在某些系统中甚至不会读取第二行。所以你最好连续写，或者如果你想一行接一行地写，那么总是`\`在每一行的结尾后面指定“”，我将在下一步中向你展示。

### 如何指定 Jenkins Newman 报告的保存路径？

要提及保存报告的目录，请在上述命令的延续中键入以下内容：

--reporter-junit-export "newman/myreport.xml"

![在 Jenkins 上生成 Newman 报告](https://www.toolsqa.com/gallery/Postman/3.Generate%20Newman%20Reports%20on%20Jenkins.png)

注意：如果指定的文件夹不可用，那么它将在 Jenkins 目录的工作区内自动创建。下面就看这个。

### 如何查看 Jenkins 生成的 XML 结果/输出？

1.  要生成执行输出，我们需要 使用上述配置构建项目。构建完成后，转到工作区文件夹。

![工作区_Jenkins](https://www.toolsqa.com/gallery/Postman/4.Workspace_Jenkins.png)

注意：通过单击Build Now从左侧导航栏完成构建，它位于 Jenkins Job/Project 的根文件夹中。

1.  在工作区中，你将看到已按你指定的名称创建了一个新文件夹。

![Newman_Folder_Created_Jenkins](https://www.toolsqa.com/gallery/Postman/5.Newman_Folder_Created_Jenkins.png)

3.单击文件夹。你将看到报告已创建。

![Report_Name_Jenkins](https://www.toolsqa.com/gallery/Postman/6.Report_Name_Jenkins.png)

你可以打开该文件并查看以 XML 格式编写的测试结果。

![XML_report_Jenkins](https://www.toolsqa.com/gallery/Postman/7.XML_report_Jenkins.png)

现在，此文件包含报告的 xml 代码。该报告采用 XML 格式，是 Jenkins 用来绘制报告图形表示的原始代码。这也称为发布报告。

每当我们想要发布报告时，我们都需要明确地告诉 Jenkins。由于报告只能在构建后发布，因此发布报告成为Post-Build Actions的一部分。通过在 Post-Build Actions 中编写命令，我们告诉 Jenkins 在构建部分完成后它必须做的工作。因此，在下一节中，我们将尝试实现相同的目标。

### 如何发布关于 Jenkins 的报告？

1.转到配置部分。

![配置_Jenkins](https://www.toolsqa.com/gallery/Postman/8.Configure_Jenkins.png)

1.  转到构建后操作。所以这是构建完成后 Jenkins 将要查看的内容。

![Post_Build_Actions_Jenkins](https://www.toolsqa.com/gallery/Postman/9.Post_Build_Actions_Jenkins.png)

1.  选择发布 JUnit 测试结果报告。

![Publish_Junit_Report_jenkins](https://www.toolsqa.com/gallery/Postman/10.Publish_Junit_Report_jenkins.png)

1.  将打开一个新框。在测试报告 XML 中输入文件的路径。保存更改并单击 立即构建以再次构建项目。

![姓名_报告_詹金斯](https://www.toolsqa.com/gallery/Postman/11.Name_Report_Jenkins.png)

注意：正如你在上面看到的，这将使用我们在上一节中创建的相同 XML 输出来发布报告。

1.  现在，在构建历史部分中单击日期和时间以打开构建结果，或者你也可以单击最左侧显示的球来打开。

![Build_Now_Jenkins_Pass](https://www.toolsqa.com/gallery/Postman/12.Build_Now_Jenkins_Pass.png)

1.  单击测试结果。

![Test_Click_Jenkins](https://www.toolsqa.com/gallery/Postman/13.Test_Click_Jenkins.png)

给你。你现在可以看到简单、美观且易于理解的测试结果。正如你在下面看到的，这份报告比 XML 代码更容易理解。你可以看到测试的数量和失败的数量。如果你的测试失败，它将以红色显示。

![测试报告图形化詹金斯](https://www.toolsqa.com/gallery/Postman/14.Test_Report_Graphical_Jenkins.png)

如果你想查看测试，请单击root。

![Root_Tests_Jenkins](https://www.toolsqa.com/gallery/Postman/15.Root_Tests_Jenkins.png)

只要你单击根目录，就会出现所有单独的测试。这些测试是你的 API 测试，我们保存在集合中，我们正在 Jenkins 上使用 Newman 命令运行相同的集合。报告中还提到了持续时间，它告诉了单个测试的执行时间。

![APIs_root_Jenkins](https://www.toolsqa.com/gallery/Postman/16.APIs_root_Jenkins.png)

单击任何 API 以查看测试名称及其状态以及持续时间。

![所有_测试_詹金斯](https://www.toolsqa.com/gallery/Postman/17.All_Tests_Jenkins.png)

这样我们就可以轻松生成测试报告并查看详细信息。 此外，在尝试在 Jenkins 上进行练习时，请始终记住使用失败的测试，否则你在仪表板上看不到太多内容。如果你应用所有正确的断言，你可能无法知道你的断言是否运行。

通过上述方法我们得到了单次测试的报告。但是，如果我们一次又一次地运行测试，那么Jenkins 也会生成测试趋势图。测试趋势显示项目构建的完整行为。

### 如何在 Jenkins 上创建测试趋势？

1.  更改你的测试结果(如果你之前失败，请尝试编写现在通过的断言)。换句话说，改变你的测试趋势。
2.  多次构建项目以设定总体趋势，因为有时 Jenkins 不会显示一次构建的报告。
3.  刷新页面。
4.  你将在仪表板上看到作为测试结果趋势的通用报告。

![测试结果_趋势_詹金斯](https://www.toolsqa.com/gallery/Postman/18.Test_Result_Trend_Jenkins.png)

通过这种方式，你已经创建了一个普遍的趋势，并且可以分析项目构建的过去和现在的行为。这就是本教程中关于生成报告的全部内容。在下一个教程中，我将向你介绍一些有关插件的知识。你将使用这些插件向 Jenkins 添加附加功能。我们将通过安装附加插件以 HTML 格式发布报告。在那之前，继续练习。
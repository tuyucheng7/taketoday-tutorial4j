在自动化时代，开发测试脚本只是整个测试自动化周期的一部分。我们可以开发测试脚本来验证功能是否正常工作。尽管如此，执行结果仍然很重要，因为测试脚本只有技术人员才能理解。如果我们谈论高层管理人员或利益相关者或非技术人员，他们不关心测试脚本的技术方面；他们只关心最后的执行结果。所以，我们需要把我们所有的执行结果汇总成一个报表，这样任何人只要看到那个报表就可以分析执行结果的最终状态。这就是测试报告发挥作用的地方，而Jenkins 报告功能是 Jenkins 最著名的功能之一。

随后，在本文中，我们将尝试了解 Jenkins 支持的几种报告格式。然后，我们将通过涵盖以下主题中的详细信息来了解如何配置和使用这些Jenkins 报告：

-   Jenkins 中的报告是什么？
    -   如何在 Jenkins 中发布 JUnit 报告？
-   什么是 Jenkins HTML 报告？
    -   如何在 Jenkins 中设置 HTML 报告插件？
    -   如何在 Jenkins 中发布 HTML 报告？

## Jenkins 中的报告是什么？

报告基本上是生成测试执行结果的结构化和图形化方式。正如我们在介绍部分所讨论的那样，我们生成报告，以便高层管理人员、利益相关者以及技术知识较少的其他团队成员也可以轻松了解我们脚本的成功率，并基于此，他们可以分析脚本的质量我们的测试脚本和产品。

在 Jenkins 看来，可以使用不同格式的报告。我们希望以哪种格式发布结果取决于我们。这些报告可以是图形、表格或其他详细格式。Jenkins的报告功能主要依赖于第三方插件。因此，无论报告的预期格式如何，我们都需要安装和配置兼容的插件。让我们快速了解如何在 Jenkins 中集成和查看 JUnit 报告：

### 如何在 Jenkins 中发布 JUnit 报告？

在本节中，我们将看到如何在 Jenkins 中发布 JUnit 测试结果报告。请按照以下步骤实现此部分：

第 1 步：在进行进一步操作之前，请确保Junit 插件 已正确安装在 Jenkins 机器中。默认情况下，它会自动安装在Jenkins 中，但是如果没有安装我们可以去插件管理器中安装它。安装后，它将显示在已安装选项卡下，如下图所示：

![安装了 JUnit Jenkins 报告插件](https://www.toolsqa.com/gallery/Jenkins/1.JUnit%20Jenkins%20reporting%20plugin%20installed.jpg)

第二步：发布Junit报表，我们以freestyle项目为例。我将项目创建为“Test_Junit_Report”，如下图所示：

![创建自由式项目](https://www.toolsqa.com/gallery/Jenkins/2.Creation%20of%20freestyle%20project.jpg)

第 3 步：现在，转到配置部分，将项目描述和 GitHub 项目 URL 放入 GitHub 中。请找到 GitHub 存储库 URL [ https://github.com/toolsqa17061989/SetupBuildJob/。

![GitHub 项目地址](https://www.toolsqa.com/gallery/Jenkins/3.GitHub%20Project%20URL.jpg)

第 4 步：转到“源代码管理” 部分并输入下图中提到的存储库 URL。

![提到的存储库 URL](https://www.toolsqa.com/gallery/Jenkins/4.Repository%20URL%20mentioned.jpg)

第 5 步：现在，转到构建 部分并执行以下步骤：

-   单击“添加构建步骤” 下拉菜单。
-   选择选项“调用顶级 Maven 目标”。
-   在“目标”文本框中，写入“clean compile test”之类的命令。
-   单击保存按钮。

![在 Jenkins 中构建部分](https://www.toolsqa.com/gallery/Jenkins/5.Build%20section%20in%20Jenkins.jpg)

第六步：现在运行构建，查看构建结果是否成功。运行成功后，进入job，点击“Workspace” 链接。

![单击工作区目录](https://www.toolsqa.com/gallery/Jenkins/6.Clicking%20on%20workspace%20directory.jpg)

只要我们点击上面提到的链接，我们就会进入这个目录。现在点击“目标”链接。

![单击目标链接以获取报告路径](https://www.toolsqa.com/gallery/Jenkins/7.clicking%20on%20target%20link%20to%20get%20report%20path.jpg)

只要我们点击上面提到的链接，我们就会进入这个目录。现在点击“surefire-reports” 链接。

![单击 surefire 报告以获取 Jenkins 中的报告路径](https://www.toolsqa.com/gallery/Jenkins/8.clicking%20on%20surefire%20reports%20to%20get%20report%20paths%20in%20Jenkins.jpg)

第 7 步：单击上一步中提到的链接后，我们将进入该目录。现在，我们需要复制下图中突出显示的路径：

![在 Jenkins 中复制报告目录路径](https://www.toolsqa.com/gallery/Jenkins/9.Copying%20reports%20directory%20path%20in%20Jenkins.jpg)

第 8 步：转到作业配置并转到“构建后操作” 部分。现在执行以下步骤：

-   单击“添加构建后操作” 下拉菜单。
-   选择选项“Publish JUnit test result report” 选项。
-   在 Test report XMLs 文本框中，复制 .xml 后跟的相同路径，因此完整路径为 target/surefire-reports/ .xml。
-   单击保存按钮。

![用于发布 junit 报告的 Jenkins 报告设置](https://www.toolsqa.com/gallery/Jenkins/10.Jenkins%20reporting%20setting%20for%20publishing%20junit%20report.jpg)

第 9 步：现在触发构建。完成后，转到相应的工作，我们可以看到名为“Latest Test Results”的新项目， 这是我们的JUnit 测试结果报告。

![Jenkins reporting JUnit 测试结果报告](https://www.toolsqa.com/gallery/Jenkins/11.Jenkins%20reporting%20JUnit%20Test%20result%20report.jpg)

第十步：我们可以点击上面突出显示的链接，查看测试结果。

![Jenkins reporting Published JUnit 测试结果报告](https://www.toolsqa.com/gallery/Jenkins/12.Jenkins%20reporting%20Published%20JUnit%20test%20result%20report.jpg)

那么，通过这种方式，我们就可以发布JUnit测试结果报告了。在下一节中，让我们看看Jenkins 支持的另一种报告类型，即HTML。

## 什么是 Jenkins HTML 报告？

在上一节中，我们看到了如何发布Jenkins JUnit 报告。因此，在本节中，我们将讨论另一种格式，即HTML Report。HTML 报告是表示测试结果的更好方式，并且可以通过启用 HTML 报告插件轻松地与Jenkins 报告功能集成。顾名思义，这份报告是HTML格式的，让我们在分析结果时有更好的外观和感觉。让我们在下一小节中看看 Jenkins 中需要什么插件来发布HTML报告。

### 如何在 Jenkins 中设置 HTML 报告插件？

为了在 Jenkins 中获得 HTML 报告选项，我们需要在 Jenkins 中安装“HTML Publisher” 插件。请按照以下步骤在 Jenkins 中安装上述插件：

第 1 步：转到“管理插件”部分并执行以下步骤：

-   单击“可用”选项卡。
-    在搜索框中键入文本“HTML” 。
-   选中 HTML Publisher 插件的复选框。
-   单击“立即下载并在重启后安装” 按钮。建议重新启动 Jenkins 以有效安装此插件。

![Jenkins 中的 HTML 插件安装](https://www.toolsqa.com/gallery/Jenkins/13.HTML%20plugin%20installation%20in%20Jenkins.jpg)

第二步：只要我们点击“立即下载并重启后安装”，就会打开一个新窗口，我们可以在其中看到这个插件的安装进度。

![在 Jenkins 中安装 HTML 插件](https://www.toolsqa.com/gallery/Jenkins/14.Installation%20of%20HTML%20Plugin%20In%20Jenkins.jpg)

第三步：安装成功后，我们可以在“已安装”栏目下看到这个插件

![在 Jenkins 中成功安装 HTML 插件](https://www.toolsqa.com/gallery/Jenkins/15.Successful%20installation%20of%20HTML%20plugin%20in%20Jenkins.jpg)

因此，通过这种方式，我们可以在 Jenkins 中安装 HTML 发布者插件。在下一小节中，让我们看看如何在 Jenkins 中发布 HTML 报告。

### 如何在 Jenkins 中发布 HTML 报告？

在上一小节中，我们看到了如何在 Jenkins 中安装 HTML 发布者插件。因此，在本小节中，让我们看看如何在 Jenkins 中发布 HTML 报告。这次我们以maven项目为例。所以，请按照以下步骤来实现它：

注意：要了解更多关于 maven 的信息，请访问文章[“Jenkins Maven Configuration”](https://www.toolsqa.com/jenkins/jenkins-maven/)。

第 1 步：创建一个 Maven 作业，就像我们创建的“Setup build Job”一样。现在转到配置部分，然后直接转到“源代码管理” 部分并执行以下步骤：

-   选择选项“Git”。
-   将存储库 URL 设为 [https://github.com/toolsqa17061989/SetupBuildJob.git](https://github.com/toolsqa17061989/SetupBuildJob.git)因为我们已经有一个 GitHub 存储库。

![Jenkins 中 Freestyle 作业的源代码管理部分](https://www.toolsqa.com/gallery/Jenkins/16.Source%20code%20management%20section%20for%20a%20Freestyle%20job%20in%20Jenkins.jpg)

第 2 步：转到构建部分，将“pom.xml” 文本放入 Root POM 文本框中，并将命令“clean install” 放入目标和选项文本框中。

![在 Jenkins 中为 Maven 项目构建部分](https://www.toolsqa.com/gallery/Jenkins/17.build%20section%20in%20Jenkins%20for%20Maven%20project.jpg)

第 3 步：转到“Post-build Actions” 部分，单击“Add post-build action” 下拉菜单，在这里我们可以看到“Publish HTML reports”选项。

![Jenkins 报告选择发布 HTML 报告选项](https://www.toolsqa.com/gallery/Jenkins/18.Jenkins%20reporting%20Selection%20of%20Publish%20HTML%20reports%20option.jpg)

现在点击上面提到的“Publish HTML reports”选项。

第 4 步：一旦我们点击上述选项，我们就可以看到发布 HTML 报告部分。现在，单击“添加” 按钮。

![Jenkins 报告 点击添加按钮](https://www.toolsqa.com/gallery/Jenkins/19.Jenkins%20reporting%20Click%20on%20add%20button.jpg)

第 5 步：一旦我们点击添加按钮，就会显示发布 HTML 报告部分，我们需要在其中提供一些信息。

![Jenkins 报告发布 HTML 报告部分](https://www.toolsqa.com/gallery/Jenkins/20.Jenkins%20reporting%20Publish%20HTML%20report%20section.jpg)

第 6 步：我们需要填写的第一件事是HTML 目录路径。要提取此路径，请转到Jenkins 工作区，移至项目，然后转到测试输出文件夹。这是我们将用来提供HTML目录路径的路径。

![HTML目录路径](https://www.toolsqa.com/gallery/Jenkins/21.HTML%20directory%20path.jpg)

第 7 步：现在，我们需要在 HTML 发布报告部分下填写信息。请执行以下步骤：

-   把上一步提取出来的HTML目录路径放上去。
-   保持 Index 页面文本框的值不变，即 index.html。
-   根据我们的默认标题设置为 HTML 报告，随心所欲地放置报告标题。
-   单击保存按钮。

![Jenkins 报告填充发布 HTML 报告部分](https://www.toolsqa.com/gallery/Jenkins/22.Jenkins%20reporting%20Filling%20Publish%20HTML%20reports%20section.jpg)

第 8 步：现在触发作业并在运行后转到作业，在这里我们可以看到下图中突出显示的“HTML 报告” 链接。

![Jenkins 报告 HTML 报告在 Jenkins 中发布](https://www.toolsqa.com/gallery/Jenkins/23.Jenkins%20reporting%20HTML%20Report%20published%20in%20Jenkins.jpg)

第 9 步：当我们单击此链接时，将在同一浏览器中打开一个新选项卡，并在该选项卡中显示我们的HTML 报告。

![HTML 报告格式不正确](https://www.toolsqa.com/gallery/Jenkins/24.HTML%20Report%20Not%20well%20formatted.jpg)

我们可以单击报告中显示的各种链接以查看其他结果。所以，通过这种方式，我们可以在 Jenkins 中发布我们的 HTML 报告。上面报告的问题在于它没有很好地显示，因为没有为这个文件加载 CSS 属性。 请按照进一步的步骤来解决它。

第 10 步：转到 Jenkins 仪表板并单击“管理 Jenkins”。

![仪表板管理插件](https://www.toolsqa.com/gallery/Jenkins/25.Dashboard%20Manage%20Plugins.jpg)

第 11 步：单击名为“管理节点和云”的链接。

![单击管理节点和云](https://www.toolsqa.com/gallery/Jenkins/26.Clicking%20on%20Manage%20nodes%20and%20clouds.jpg)

第 12 步：单击右上角显示的齿轮图标。

![点击齿轮图标](https://www.toolsqa.com/gallery/Jenkins/27.Clicking%20on%20gear%20icon.jpg)

第 13 步：单击下图中突出显示的“脚本控制台” 链接。

![单击脚本控制台](https://www.toolsqa.com/gallery/Jenkins/28.Clicking%20on%20script%20console.jpg)

第十四步：现在，我们需要在脚本控制台的groovy脚本下面写一行，然后点击运行。

```java
System.setProperty("hudson.model.DirectoryBrowserSupport.CSP","")
```

![启用跨站点脚本的 Groovy 脚本](https://www.toolsqa.com/gallery/Jenkins/29.Groovy%20script%20to%20enable%20cross%20site%20scripting.jpg)

只要我们点击运行按钮，我们就会看到“结果” 文本，如果我们能看到这个文本，那么就意味着我们的脚本运行良好。

第 15 步：现在再次进入相应的作业，刷新页面或再次触发构建，这一次，我们可以看到 well-versed 报告。

![Jenkins 报告格式良好的 HTML 报告](https://www.toolsqa.com/gallery/Jenkins/30.Jenkins%20reporting%20Well%20formatted%20HTML%20report.jpg)

因此，通过这种方式，我们可以使用 Jenkins 报告功能发布HTML 报告。

## 关键要点

-   Jenkins 的报表功能依赖于第三方插件，我们可以根据需要集成多个报表插件。
-   报告基本上是生成测试执行结果的结构化和图形化方式。
-   要发布 JUnit 测试结果报告，需要在 Jenkins 上安装 JUnit Plugin。
-   在Post Build action下，我们可以看到JUnit测试结果报告的选项，给出相关信息后，我们就可以触发构建并看到报告了。
-   要发布 HTML 报告，需要在 Jenkins 上安装 HTML Publisher Plugin。
-   在Post Build action下，我们可以看到HTML发布报告的选项，给出相关信息后，我们就可以触发构建并看到报告了。
-   如果报告显示不正确，我们需要运行groovy脚本，然后我们就可以看到一个格式正确的报告。
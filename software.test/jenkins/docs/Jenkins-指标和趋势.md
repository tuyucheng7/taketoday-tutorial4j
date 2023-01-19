使用Jenkins运行作业是项目开发和维护期间必不可少的活动之一，但这里出现了一个问题：这是唯一重要的活动。高层管理人员希望在一段时间内查看构建活动的情况又如何呢？在这里，Jenkins 指标和趋势出现了。Jenkins中有一些可用的插件，我们可以使用它们轻松查看构建历史随时间的变化。我们致力于通过本文了解一些插件的详细信息，这些插件可以帮助收集各种工作和活动的 Jenkins 指标和趋势。让我们从了解以下几点开始旅程：

-   Jenkins 的指标和趋势是什么？
-   Jenkins中如何安装metric相关插件？
    -   还有，如何在 Jenkins 中查看指标？
-   如何在Jenkins中安装趋势相关的插件？
    -   如何在Jenkins中查看趋势？

## Jenkins 的指标和趋势是什么？

我们在介绍部分指出，指标和趋势有助于呈现我们在一段时间内执行的构建指标。我们还可以在图形格式的帮助下查看这些指标和趋势，以便在单个报告中，我们可以轻松地分析一段时间内构建的成功率和失败率。例如，如果我们使用“Build History Metrics”插件，那么这个插件会显示 Jenkins 中所有构建的以下指标：

-   MTTF(度量失败时间)
-   MTTR(平均恢复时间)
-   构建时间的标准差

在同样的情况下，如果我们在Jenkins中安装“build-metrics” 插件，那么我们可以看到图形格式的全局构建统计信息。那么，在后面的章节中，我们将讨论这些插件的安装和使用。

## Jenkins中如何安装metric相关插件？

在上一节中，我们了解了 Jenkins 中指标和趋势的概念。现在，是时候看看这些指标和趋势的实际实施了。下面开始安装metric相关的插件。首先，我们将在Jenkins中安装[“Build History Metrics”](https://plugins.jenkins.io/build-history-metrics-plugin/)插件。请按照以下步骤安装此插件：

第 1 步：首先，转到 Jenkins 仪表板并单击下图中突出显示的“管理 Jenkins” 链接：

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/1.Click%20on%20manage%20Jenkins.jpg)

第 2 步：我们将重定向到管理 Jenkins 页面。在这里单击“管理插件”。

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/2.Click%20on%20manage%20jenkins.jpg)

第三步：点击后，我们将重定向到插件管理器页面。请执行以下步骤来安装构建历史指标插件：

-   单击“可用”选项卡。
-   在搜索框中放置/输入文本“Build History Metrics”。
-   选中插件的复选框。
-   单击“无需重新启动即可安装”按钮。

![安装指标插件](https://www.toolsqa.com/gallery/Jenkins/3.Installing%20metric%20plugin.jpg)

第四步：完成以上步骤后，我们会跳转至插件安装进度页面，安装成功后即可看到成功提示。

![插件安装成功提示](https://www.toolsqa.com/gallery/Jenkins/4.Plugin%20installation%20successful%20message.jpg)

第 5 步：为了验证插件是否成功安装，我们可以转到“已安装” 选项卡，我们可以看到构建历史指标插件的存在。

![插件安装验证](https://www.toolsqa.com/gallery/Jenkins/5.Verification%20of%20installation%20of%20plugin.jpg)

那么，这样一来，我们就可以在Jenkins中安装metric相关的插件了。在下一小节中，让我们看看如何在Jenkins 中查看任何作业的指标。

### 如何在 Jenkins 中查看指标？

好的，我们已经在 Jenkins 中安装了构建历史指标插件。因此，在本小节中，我们将了解如何在 Jenkins 中查看指标。请按照以下步骤查看不同条件下的指标：

第 1 步：转到Jenkins仪表板并单击任何作业。在这里，我们可以在下面突出显示的表格中看到MTTR、MTTF 和标准偏差等指标。

注意：如果安装下面的插件后，没有显示表格形式，那么重启Jenkins。

![表格形式的 Jenkins 指标](https://www.toolsqa.com/gallery/Jenkins/6.Jenkins%20metrics%20in%20tabular%20form.jpg)

在上图中，我们可以看到没有记录失败的时间，因为相应作业的所有构建都已通过。在下一步中，让我们也看看失败的时间。

第 2 步：运行构建显示为失败状态的任何作业，然后我们可以看到MTTR和 MTTF 的时间，如下图所示。

![失败指标](https://www.toolsqa.com/gallery/Jenkins/7.Metric%20for%20failure.jpg)

因此，通过这种方式，我们可以以表格格式查看任何作业的不同类型的指标。在下一节中，我们将讨论Jenkins 中的趋势插件。

## 如何在Jenkins中安装趋势相关的插件？

在上一节中，我们了解了如何在 Jenkins 中查看任何作业的指标。在本节中，让我们谈谈相同的趋势。为此，我们将在Jenkins中安装[“build-metrics”](https://plugins.jenkins.io/build-metrics/) 插件。借助此插件，我们可以查看全局统计信息。请按照以下步骤安装此插件：

第 1 步：首先，转到Jenkins仪表板并单击下图中突出显示的“管理 Jenkins” 链接：

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/8.Click%20on%20manage%20Jenkins.jpg)

第 2 步：我们将重定向到管理 Jenkins页面。在这里单击“管理插件”。

![点击管理插件](https://www.toolsqa.com/gallery/Jenkins/9.Click%20on%20manage%20Plugins.jpg)

第三步：点击后，我们将重定向到插件管理器页面。请执行以下步骤来安装build-metrics插件：

-   单击“可用”选项卡。
-   在搜索框中输入文本“build metrics”。
-   选中插件的复选框。
-   单击“无需重新启动即可安装”按钮。

![安装构建指标插件](https://www.toolsqa.com/gallery/Jenkins/10.Installation%20of%20build%20metrics%20plugin.jpg)

第四步：完成以上步骤后，我们会跳转至插件安装进度页面，安装成功后即可看到成功提示。

![趋势插件安装进度页面](https://www.toolsqa.com/gallery/Jenkins/11.Trends%20Plugin%20installation%20progress%20page.jpg)

第五步：为了验证插件是否安装成功，我们可以进入“installed” 选项卡，我们可以看到build metrics插件的存在。

![Jenkins Metrics and Trends 构建指标插件](https://www.toolsqa.com/gallery/Jenkins/12.Jenkins%20Metrics%20and%20Trends%20Build%20Metrics%20Plugin.jpg)

那么，这样一来，我们就可以在Jenkins中安装trends相关的插件了。在下一节中，让我们看看如何在 Jenkins 中查看趋势。

### 如何在Jenkins中查看趋势？

好的，我们已经在 Jenkins 中安装了构建指标插件。因此，在本小节中，我们将了解如何在 Jenkins 中查看全局构建统计信息。请按照以下步骤查看：

第 1 步：首先，转到Jenkins 仪表板并单击下图中突出显示的“管理 Jenkins” 链接：

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/13.Click%20on%20manage%20Jenkins.jpg)

第 2 步：如果我们成功安装构建指标插件，我们将在“未分类” 部分下看到“Global Build Stats” 链接单击下图中突出显示的此链接。

![Jenkins 指标和趋势 点击全局构建统计链接](https://www.toolsqa.com/gallery/Jenkins/14.Jenkins%20Metrics%20and%20Trends%20Click%20on%20global%20build%20stats%20link.jpg)

第 3 步：单击上述链接后，我们将重定向到全局构建统计页面。现在单击“Initialize stats” 按钮开始构建统计信息的初始化。

![Jenkins Metrics and Trends 点击initialize stats](https://www.toolsqa.com/gallery/Jenkins/15.Jenkins%20Metrics%20and%20Trends%20Click%20on%20initialize%20stats.jpg)

单击此按钮后，我们将看到一条消息，如“数据已成功初始化！”。

![Jenkins 指标和趋势收到成功消息](https://www.toolsqa.com/gallery/Jenkins/16.Jenkins%20Metrics%20and%20Trends%20Successful%20message%20received.jpg)

第 4 步：现在，下一步是我们需要创建一个图表来显示统计数据。我们需要点击下图中突出显示的“创建新图表” 链接来实现它。

![点击创建新图表](https://www.toolsqa.com/gallery/Jenkins/17.click%20on%20create%20new%20chart.jpg)

第 5 步：一旦我们点击上述链接，我们将看到“添加新图表” 弹出窗口，我们需要在其中填写以下内容：

-   给一个标题，就像我们给“全球结果”一样。
-   我们需要像给定 700 和 500 一样给图表宽度和高度。
-   给出一个图表时间尺度，就像我们给出的“每日”一样。我们可以提供其他选项，例如每小时。
-   我们需要根据我们的要求给出图表时间长度，如“30”天。
-   在具有 Y 轴类型的构建状态中提供诸如“计数”之类的选项。
-   现在，单击“创建新图表”按钮。

![Jenkins 指标和趋势创建新图表](https://www.toolsqa.com/gallery/Jenkins/18.Jenkins%20Metrics%20and%20Trends%20Creating%20new%20chart.jpg)

第 6 步：一旦我们点击创建新图表按钮，我们就会看到图表出现在构建统计页面上。这里蓝色部分表示成功，红色部分表示失败。

![Jenkins 指标和趋势图已创建](https://www.toolsqa.com/gallery/Jenkins/19.Jenkins%20Metrics%20and%20Trends%20Chart%20created.jpg)

第 7 步：我们可以单击蓝色或红色部分以显示有关构建的更多统计信息。

![Jenkins Metrics and Trends 成功构建统计](https://www.toolsqa.com/gallery/Jenkins/20.Jenkins%20Metrics%20and%20Trends%20success%20build%20stats.jpg)

![构建的 Jenkins 指标和趋势失败统计](https://www.toolsqa.com/gallery/Jenkins/21.Jenkins%20Metric%20and%20Trends%20Failure%20stats%20of%20builds.jpg)

第 8 步：同样，如果我们希望看到 Y 轴类型为“百分比”， 则选择 Build status with Y-axis type as a percentage。

![Jenkins 指标和趋势 Y 轴百分比](https://www.toolsqa.com/gallery/Jenkins/22.Jenkins%20Metrics%20and%20Trends%20Y%20axis%20as%20percentage.jpg)

只要我们点击创建新图表按钮，就会显示新图表，我们可以在其中看到 Y 轴上的百分比。

![Jenkins 指标和趋势 Y 轴百分比](https://www.toolsqa.com/gallery/Jenkins/23.Jenkins%20Metrics%20and%20Trends%20Y%20axis%20as%20percentage.jpg)

因此，通过这种方式，我们可以借助趋势插件在 Jenkins 中查看全局统计信息。

因此，使用这些插件，我们可以可视化Jenkins 指标和趋势并分析 Jenkins 中的工作/活动。

## 关键要点

-   指标和趋势可用于显示一段时间内执行的构建指标。此外，我们还可以借助图形格式了解这些指标和趋势。
-   此外，我们可以在“Build History Metrics”插件的帮助下查看不同类型的指标。
-   此外，对于图形格式表示，我们可以安装“build-metrics”插件。
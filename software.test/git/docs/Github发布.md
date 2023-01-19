[在关于GitHub 标签](https://www.toolsqa.com/git/github-tags/)的教程中，我提到标签是一种在存储库中保存点的方法。它可以是新版本、任何其他特定的重要事件或任何其他内容。它可以是任何东西。但是[Git 标签](https://www.toolsqa.com/git/git-tags/)在 GitHub 中带来了发布的概念。看起来它们是相关的，因为它们经常互换使用，并且可以通过 GitHub 中的切换按钮使用。那么，Releases 中有哪些新内容？我们将在本教程中探索它：

-   GitHub 中的发布是什么？
    -   在 GitHub 中创建一个版本。
    -   如何在 GitHub 中删除一个 Release？

## GitHub 中的发布是什么？

GitHub 中的发布是 GitHub 的一站式解决方案，以二进制文件形式提供软件包及其每个软件版本的发行说明。二进制文件是在特定点之前以代码形式为用户提供软件版本的好方法。因此，如果你需要XYZ软件版本 2.5的二进制文件，该文件目前是3.1 版，你可以通过GitHub快速获取。除了代码，软件发行说明也在那里。反过来，其中包括添加新功能或其他改进的详细信息。所以如果你想了解软件而不实际安装软件，你可以阅读这些笔记。此外，发布功能还可以帮助世界各地的人们了解软件是如何随着时间的推移而发展的，以及如何使用他们的二进制文件。

### 创建 GitHub 版本

GitHub 赋予开发人员对发布的完全控制权。你可以通过两种方式在 GitHub 中创建发布：

-   首先，通过已经创建的标签。
-   其次，通过创建一个新的/新鲜的版本。

让我们一一了解它们。

#### 如何通过标签在 GitHub 中创建发布？

[在GitHub](https://www.toolsqa.com/git/github-tags/)中的标签教程中，我们讨论了从 GitHub 中的标签创建发布的选项。要查看，请打开 GitHub 中的标签列表(请参阅 GitHub 中的标签)。此外，创建发布的选项将在标签名称的右侧可用。

![创建发布选项在标签名称的右侧可用](https://www.toolsqa.com/gallery/Git/1.Create%20release%20option%20is%20available%20to%20the%20right%20of%20the%20tag%20name.webp)

按突出显示的“创建版本”按钮转到下一个屏幕。此后，你会注意到这里有两个变化。

![点击Create Release 创建Github Release](https://www.toolsqa.com/gallery/Git/2.Click%20on%20Create%20Release%20to%20create%20Github%20Release.webp)

-   首先，选项卡已从“标签”转移到“发布”，表示我们现在正在处理一个发布。
-   其次，带有“ Existing tag ”消息的标签名称表明我们正在从现有标签创建一个版本。

在此之下，我们将获得一些选项来填充我们的发布。我们可以快速填充它们。

![填写创建 Github 版本的选项](https://www.toolsqa.com/gallery/Git/3.Fill%20Option%20to%20create%20Github%20release.webp)

需要注意的是 description 字段支持 markdown 格式。如果你使用过Jupyter notebooks ，你会很熟悉。但是，你也可以了解[markdown 格式](https://en.wikipedia.org/wiki/Markdown)。简而言之，它是关于通过我们在编写文本时应用的特定符号来设计结果输出的样式。例如，# 表示 H1 或一级标题。同样，##表示二级标题等。相应地填写给定的字段。

将出现复选框，询问你是否是预发布版本。在此期间，我将保持原样，以表示此版本的发布已经发生。

![发布发布](https://www.toolsqa.com/gallery/Git/4.Publish%20Release.webp)

按Publish Release从现有标签发布版本。

只要你在 GitHub 上发布版本，我们就可以在发布选项卡下看到它，之前只显示标签名称。

![在release标签下可以看到发布的Github release](https://www.toolsqa.com/gallery/Git/5.Published%20Github%20release%20can%20be%20seen%20under%20the%20release%20tab.png)

通过这种方式，我们已经从现有标签创建了一个版本。因此，在下一节中，我们将尝试创建一个新版本。

#### 如何在 GitHub 中创建新版本？

如上所述，发布可以通过现有标签或创建新发布在 GitHub 中发布。要创建新版本，请按照下列步骤操作：

1.  首先，转到发布选项卡。
2.  其次，选择右侧可用的Draft a new release按钮。

![草拟新版本](https://www.toolsqa.com/gallery/Git/6.draft%20new%20release.webp)

1.  第三，写一个目前不存在的标签名(这里是v2.0.1)。

![发布信息github](https://www.toolsqa.com/gallery/Git/7.release%20information%20github.png)

1.  最后，与上一节类似，填写详细信息并按下新闻稿按钮发布新闻稿。此外，返回到Releases选项卡以查看发布列表中可用的发布。

### 如何删除 GitHub 发布？

在 GitHub 中删除发布是一项非常简单的任务。只需按照以下步骤删除版本。在本节中，为了证明一点，我们将继续使用我们从现有标签创建的版本，即上一节中的v2.0。

1. 转到你的 GitHub 帐户中的发布。

2. 发布列表将出现在屏幕上。从列表中选择名为Release v2.0的版本。

![选择github release删除](https://www.toolsqa.com/gallery/Git/8.select%20github%20release%20to%20delete.webp)

1.  选择版本后，将打开版本的详细信息。在右侧，将出现“删除”按钮。

![删除按钮](https://www.toolsqa.com/gallery/Git/9.delete%20button.webp)

1.  GitHub 将要求你确认操作。

![确认发布删除](https://www.toolsqa.com/gallery/Git/10.confirm%20release%20delete.webp)

1.  按删除此版本以完全删除版本。

我们现在回到本节开始的地方。还记得吗，我们说过我们试图通过从现有标签中获取版本来证明这一点？需要注意的重要一点是，如果我们要删除从当前标签创建的版本，这并不意味着我们删除了底层标签。在上一节的上下文中，如果我们删除我们从中创建的版本，则不会删除标签 v2.0。

它让我们结束了“标签和发布”的概念。有关更多信息，你可以浏览[Git](https://www.toolsqa.com/git/git-tags/)中的[标签和 GitHub](https://www.toolsqa.com/git/github-tags/)教程中的标签。不断练习，不断释放！！

## GitHub 发布的常见问题

发布是否需要生成？

发布是让你的用户了解软件最新版本的绝佳方式。发布还有助于避免存储大型二进制文件。它们不是必需的，但却是一种很好的做法。

GitHub 发布是基于 Git 标签的吗？

是的，GitHub 发布的基础是 Git 标签。换句话说，标签是定义发布版本所必需的。
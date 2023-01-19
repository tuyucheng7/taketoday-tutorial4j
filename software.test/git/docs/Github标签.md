在本课程的[Git 部分，关于](https://www.toolsqa.com/git/git-tutorial/)[Git](https://www.toolsqa.com/git/git-tags/)中的标签的教程强调了标签在 Git 世界中的重要性。当我们需要显示发布和特定于标签的提交时，它们是至关重要的。它们在 Git 中的频繁使用使它们非常受欢迎。但是，本教程不讨论 Git。标签不仅仅是像我们在 Git 中那样标记提交。当我们在另一个平台上查看标签时，标签会连接并揭示更多信息。本教程是关于 GitHub 的标签，我们在本课程中一直使用的远程存储库与 Git 一起使用。概述看起来像：

-   什么是 Github 标签？
    -   标签作为远程存储库的时间线。
    -   GitHub 上的标签揭示了什么？
-   如何在 GitHub 上编辑和删除标签？

## 什么是 GitHub 标签？

GitHub 中的标签与提交、分支、包等一起占据了主标题栏的位置。

![GitHub 中的标记图标](https://www.toolsqa.com/gallery/Git/1.tag%20icon%20in%20GitHub.png)

当你单击显示三个版本的突出显示选项卡时，标签页将打开。

![github标签标签](https://www.toolsqa.com/gallery/Git/2.github%20tag%20tab.png)

一旦我向你展示了存储库仪表板中的更改，我们将在几分钟后回到这里。创建标签后，你是否注意到分支下拉列表中出现了一个新选项卡？不？让我给你看。

![添加了分支下拉标签](https://www.toolsqa.com/gallery/Git/3.branch%20dropdown%20tag%20added.png)

是的，标签与分支一起出现在分支下拉列表中。

### 标签作为存储库的时间线

GitHub Tags 可以帮助我们在 GitHub 的不同“重要”时间看到仓库。单击标签(正如我在上面的屏幕截图中所做的那样)，将以相反的时间顺序显示所有标签的列表，即最新创建的标签将位于顶部。

从列表中选择任何一个标签。

![在下拉列表中选择标签](https://www.toolsqa.com/gallery/Git/4.selecting%20a%20tag%20in%20dropdown.png)

存储库现在将刷新。现在你看到的存储库的图像是标签的创建点。例如，现在查看最新的提交。

![最新提交的 github 标签](https://www.toolsqa.com/gallery/Git/5.latest%20commit%20github%20tags.png)

将此与我未选择标签时的情况进行比较。

![最新提交原始](https://www.toolsqa.com/gallery/Git/6.latest%20commit%20original.png)

它们不一样。因此，标签将存储库带回其创建时间。当你只想查看在发布期间创建标签时存储库的外观时，它会让你的工作更轻松。在你检查和浏览该时间快照的任何时候，你都可以更改任何其他标签或任何其他分支。

让我们回到我们在本教程开始时简要提到的Release 和 Tags选项卡。目前，Release 和 Tags 都会向你展示相同的故事，而且它们看起来可能很相似。

![发布和 github 标签](https://www.toolsqa.com/gallery/Git/7.release%20and%20github%20tags.png)

当然，尽管它们之间存在细微差别，但我们现在将保留发布选项卡。我们将在有关在 GitHub 中发布的教程中讨论这一点。

现在让我们关注标签部分。

### GitHub 中的标签揭示了什么？

一个标签将显示五个基本信息。

![Github 标签显示五个基本信息](https://www.toolsqa.com/gallery/Git/8.Github%20tag%20displays%20five%20essential%20pieces%20of%20information.png)

1.  标签名称：首先是标签的名称。
2.  创建时间：这是自标签创建以来经过的时间。在我们的例子中，它是两天。
3.  哈希码：这是标签被“标记”到的提交的哈希码。简而言之，这是此标记可以代表存储库的最后一次提交。
4.  Zipped Source Code：这是代码的 zip 文件，直到该标记提交为止。
5.  Tarball Source Code：这是仅提交该标记的代码的 tarball。
6.  创建发布：此菜单栏将帮助你在 GitHub 中创建发布。

除了标签名称外，还会出现三个点。

![github 标签提交信息](https://www.toolsqa.com/gallery/Git/9.github%20tag%20commit%20message.png)

这些点代表该标记提交的提交消息。单击圆点以打开提交消息。

![标记提交消息打开](https://www.toolsqa.com/gallery/Git/10.tags%20commit%20message%20open.png)

我们现在可以通过单击标签名称来浏览标签。

对于本节，我将继续使用v1.1标记。单击标签，标签屏幕将打开。

![标签屏幕](https://www.toolsqa.com/gallery/Git/11.tag%20screen.png)

我们已经在上一节中讨论了此页面上显示的所有详细信息。除了它们之外，此页面还抛出一个新行“自此标记以来 1 commit to dev ”。此行告诉我们自标记创建以来此分支上完成了多少次提交。

请注意，标签仅在“dev”分支上创建。所以，如果我们转到 master 分支，标签将不会在 Git 或 GitHub 上可见。这就是这里处理“ dev ”分支引用的原因。

单击此行可查看此标记之后发生的提交历史记录。

![自标记后提交](https://www.toolsqa.com/gallery/Git/12.commit%20since%20tag.png)

在这里你可以看到标签创建后的提交历史。如上图突出显示的那样，可以看到一个提交，它是在标签创建很久之后完成的。

### 如何编辑和删除标签？

继续使用标签，你还可以根据你在此屏幕中的选择编辑或删除标签。

![编辑标签](https://www.toolsqa.com/gallery/Git/13.edit%20del%20tag.png)

单击“编辑”选项卡将我们带到存储库的“发布”屏幕。在这里，你可以通过编辑标签的描述和名称来发布版本。它在 GitHub教程的 Releases 中有解释。

单击删除将永久删除标签。由于这是一个不可逆的决定，你在确认之前会得到提示。

![删除标签确认](https://www.toolsqa.com/gallery/Git/14.delete%20tag%20confirm.png)

因此，GitHub 中的标签更好地讲述了它们自己和存储库的故事。通过标签，我们可以获得存储库的适当时间线以及到那时为止的源代码，这非常方便。标签还附加到 GitHub 中称为 Releases 的另一个概念。发布有助于在每个发布中向团队提供发布说明，即在我们创建的每个标签中。我们可以像任何其他标签一样更新和删除它。在下一个教程中，我们将创建几个发行说明并尝试通过[GitHub](https://github.com/)更新它们。

## GitHub 标签上的常见问题

可以直接在GitHub中添加Tags吗？

是的，我们可以直接在 GitHub 上添加标签。要与本地存储库同步，你需要使用 Git 拉取更改。有关拉取更改的更多详细信息，请参阅 Git Pull 教程。

GitHub 上的标签和发布是一样的吗？

不，标签不一定定义软件的版本，而版本可以。因此，它们彼此不同。但是，如果你的 GitHub 存储库中没有发布，标签和发布对应于相同的数据，即它们看起来很相似。

我可以直接从 GitHub 中的标签创建 Release 吗？

是的，可以直接从存储库中可用的标签创建发布。除此之外，你还可以在 GitHub 中创建新版本。

GitHub 中删除的标签可以恢复吗？

不，没有选项可以恢复 Git 或 GitHub 中已删除的标签。
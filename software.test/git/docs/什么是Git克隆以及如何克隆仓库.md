简单英语中的“克隆”意味着自然或人工产生相同的个体。如果你熟悉该术语，那么本教程中的内容就不足为奇了。在介绍什么是 Git 中的克隆或 Git Clone之前，我希望读者熟悉[GitHub 中的 Forking](https://www.toolsqa.com/git/git-fork/)过程。

有时，非技术人员或尚未使用 Git 的人员认为这两个术语(Git Clone 和 Git Fork)相似。实际上，它们是，但有一些差异。在学习 Git 中克隆的概念之前，最好先用 fork 洗脑。

另外，由于本课程已经涵盖了 Git 和 GitHub 的基础知识，因此从现在开始，我们将使用它们来对我们的代码/文件执行操作和过程。在更大的圈子上，本教程将使你熟悉：

-   什么是克隆？
-   克隆的目的
-   在 Git 中克隆的重要性
-   克隆存储库和 Git Clone 命令

## Git 中的 Git 克隆或克隆是什么？

克隆是为本地计算机创建 Git 远程存储库的相同副本的过程。

现在，你可能想知道，这就是我们在分叉存储库时所做的！！

![Git 克隆](https://www.toolsqa.com/gallery/Git/1.Git%20Clone.png)

当我们克隆一个仓库时，所有的文件都被下载到本地机器上，但远程 git 仓库保持不变。进行更改并将它们提交到本地存储库(克隆存储库)不会影响你以任何方式克隆的远程存储库。用户可以随时将在本地计算机上所做的这些更改与远程存储库同步。

### 为什么要克隆存储库？

前面章节提到，克隆会将完整的源代码下载到本地系统，让我们首先了解一下为什么需要克隆的真正原因：

-   为组织项目做出贡献： 多人在同一代码库上工作的组织需要一个集中式系统。克隆帮助我们实现这一动机。通过克隆，人们可以编辑项目代码来解决一些问题或提供一些修改，即额外或扩展的功能。这无疑有助于在更短的时间内通过更好的协作生产出更好的软件。
-   Make use of Open Source Repositories：英语中的一句名言“ Do not reinvent the wheel ”很适合理解这一点。同样，如果有人想使用其他人已经开发的某些功能，那么为什么要从头开始编写代码并浪费时间和资源呢？例如，有无限的开源存储库可用，可以直接适合项目。

由于克隆是 Git 和 GitHub 之旅的重要组成部分，因此最好详细了解克隆的工作原理。这是一个非常简单直接的过程，下一节将专门介绍。

### Git 中的克隆是如何工作的？

很多人都想建立一个共享的仓库，让一组开发人员可以在 GitHub/GitLab/BitBucket 等平台上发布他们的代码。在线上传协作的仓库称为上游仓库或中央仓库。

中央存储库表示所有贡献者的所有更改仅推送到此存储库。因此，这是其自身最新的存储库实例。有时这通常称为原始存储库。现在，下面给出的图像非常清楚克隆的概念。

![Git Clone - 克隆一个仓库](https://www.toolsqa.com/gallery/Git/2.Git%20Clone%20-%20Cloning%20a%20Repo.png)

对于上图，克隆过程按以下步骤进行：

-   克隆存储库：用户从 GitHub 上的上游存储库开始。由于用户导航到存储库是因为他/她对这个概念感兴趣并且他们喜欢做出贡献。当他们将存储库克隆到本地计算机时，该过程从克隆开始。现在他们在系统上拥有项目文件的精确副本以进行更改。
-   进行所需的更改：克隆后，贡献者向存储库提供他们的贡献。以编辑源文件的形式做出贡献，导致错误修复或添加功能或可能优化代码。但最重要的是，一切都发生在他们的本地系统上。
-   推送更改：更改完成后，现在可以将修改推送到上游存储库。

注意：仓库的所有者可以允许/禁止直接更改中央仓库，设置各种通知(推送到中央仓库的任何更改的通知)等等，但所有这些设置将在本系列 Git 教程的后面介绍。

## Git 克隆

Git 中的克隆可以在自有存储库或任何其他人的存储库上完成。在下一节中，我们将克隆我的 GitHub 帐户上可用的 ToolsQA 存储库。你可以创建一个新的[GitHub 存储库](https://www.toolsqa.com/git/create-github-repository/)并尝试克隆它。

### 如何克隆存储库或使用 Git 克隆命令？

从 GitHub 克隆存储库是一个简单的过程。但是，在克隆之前，请确保你的 GitHub 帐户上有一个存储库。在本节中，我将使用ToolsQA 存储库([参考](https://github.com/harishrajora/ToolsQA))。

1.  要克隆存储库，请转到要克隆的存储库页面。这可以通过仪表板上的侧栏来完成。

![存储库_部分](https://www.toolsqa.com/gallery/Git/3.repository_Section_0.png)

1.  按克隆或下载按钮。

![克隆或下载按钮](https://www.toolsqa.com/gallery/Git/4.clone_or_download_button.png)

1.  复制按下按钮后出现的代码。

![复制克隆代码](https://www.toolsqa.com/gallery/Git/5.copy_clone_code.png)

注意：位于使用 HTTPS 克隆旁边的使用 SSH功能将在 SSH 教程中进行详细说明，但截至目前，我们正在使用 https(“使用 HTTPS 克隆”)而不是 ssh 克隆存储库。

1.  完成后，在你的系统上打开Git bash 。

![navigate_to_repo](https://www.toolsqa.com/gallery/Git/6.navigate_to_repo.png)

注意：请记住更改要将存储库克隆到的目录。我在上图中标记了我的目录 Git Repo ToolsQA，我将从 GitHub 克隆存储库。

1.  检查已在此目录下创建的目录(或存储库)。(使用 ls 命令)

![列出所有目录](https://www.toolsqa.com/gallery/Git/7.list_all_the_directories_0.png)

如图所示，我在Git Repo ToolsQA 目录下只有一个存储库。

1.  按以下命令克隆存储库：

```
git clone <URL>
```

此处的 URL 代表我们在第三步中复制的相同 URL。

![Git 克隆命令](https://www.toolsqa.com/gallery/Git/8.Git%20Clone%20Command_0.png)

注意： URL 是 GitHub Cloud 上存储库的链接。你可以在浏览器的地址栏中输入此内容，然后检查存储库页面是否打开。

1.  当你按回车键时，将出现以下消息。

![git_clone_successful](https://www.toolsqa.com/gallery/Git/9.git_clone_successful_0.png)

将存储库克隆到你的系统需要几秒钟。

注意：请注意，克隆取决于互联网连接，时间将取决于你的连接带宽。如果 Git 由于弱连接而无法克隆，则会显示致命错误并要求用户重试，直到不再出现上述消息为止。

1.  通过使用列出所有文件和文件夹的ls命令再次列出目录来确认克隆。

![list_all_the_directories_after_clone](https://www.toolsqa.com/gallery/Git/10.list_all_the_directories_after_clone_0.png)

由于我们案例中的集中存储库称为 ToolsQA，因此已将其复制到我的本地计算机。

通过手动导航到本地驱动器来签入它。

![Cloned_Repo_In_Local_Machine](https://www.toolsqa.com/gallery/Git/11.Cloned_Repo_In_Local_Machine_0.png)

可以清楚地看到，我们已经成功地从 GitHub 克隆了存储库。现在我们可以像处理普通代码文件一样处理本地系统上的文件。克隆非常有用，正如你可能已经猜到的那样，并且使用 Git 或 GitHub 进行软件开发的人们经常使用克隆。进行更改后，用户将更改推送到云 ( GitHub ) 上的原始存储库，并提供给观看它或为其做出贡献的每个人。现在是时候清除 Git Clone 和 Git Fork 之间的模糊了。
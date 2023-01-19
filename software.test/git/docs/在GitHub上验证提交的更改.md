在之前的教程中，我们讨论了如何在本地仓库中进行更改并将它们推送到远程仓库。这篇文章将帮助我们了解如何在远程存储库中验证 Github 上提交的更改。如果你还没有完成[git push](https://www.toolsqa.com/git/git-push/)教程，强烈建议先阅读一下。继续，因为我们已经将更改推送到 Github(远程存储库)，现在让我们看看如何直接在 Github 中验证这些更改。总而言之，本教程将涵盖以下主题：

-   在 GitHub 存储库上验证提交的更改

## 在 GitHub 上验证提交的更改

要检查 GitHub 上的更改，用户需要导航到他们在 git 推送教程中执行更改的 GitHub 存储库。为此，首先，登录[GitHub 账号](https://github.com/login)。

通过可用于快速导航的侧面板导航到存储库页面。

![侧面板-github-home](https://www.toolsqa.com/gallery/Git/1.side-panel-github-home.png)

你可以在 GitHub 存储库页面中注意到两件事，如下图所示：

![验证提交的更改 - GitHub 提交页面](https://www.toolsqa.com/gallery/Git/2.Verify%20Committed%20Changes%20-%20GitHub%20Commits%20Page.png)

1.  Time Of Commit (Latest Commit 285f559 2 days ago)：最新的commit显示为“ 2 days ago ”，表示该commit是用户在两天前推送的。此外，它还显示了提交的哈希值 ( 285f559 )。如果你已经阅读了[Git 日志](https://www.toolsqa.com/git/git-log/)教程，你会熟悉它并且会注意到这是实际哈希值的较短版本。通过在推送更改时匹配你在Git Bash中获得的哈希值来验证这一点
2.  Commit Message (Clone Web Page Modified)：这是用户提交更改的提交消息。你将在此页面上收到你的消息。要了解有关提交消息以及如何编写它们的更多信息，[编写好的提交消息](https://www.toolsqa.com/git/writing-good-commit-messages/)一文会有所帮助。

尽管足以确认更改与 GitHub 帐户的同步；要获得更多信息，用户还可以浏览同一存储库页面上的提交选项卡。![验证提交的更改 - 提交选项卡 github repo home](https://www.toolsqa.com/gallery/Git/3.Verify%20Committed%20Changes%20-%20Commit%20tab%20github%20repo%20home.png)

提交选项卡将显示在此存储库上发生的提交的完整历史记录。

![提交时间表](https://www.toolsqa.com/gallery/Git/4.timeline-of-commits.png)

每个提交块都将包含大量信息和链接，可以访问这些信息和链接以获得有关存储库和该特定提交的更多信息。让我们探索一个区块(最顶层)来了解它。

![提交块](https://www.toolsqa.com/gallery/Git/5.commit-block.png)以下是上面屏幕截图中标记的关键步骤：

1. 提交消息：块的标题将包含用户在 Git 中提交更改时使用的提交消息。

2. Copy Hash Value to Clipboard : 这个图标代表复制一些东西到剪贴板的动作。在 GitHub 中，按下此图标会将提交的哈希码复制到剪贴板。

3.提交的哈希码：这个块是关于提交的哈希码。它是实际哈希码的压缩版本。当你按下复制到剪贴板图标时，相同的代码会以原始形式和扩展形式复制。

按哈希码 将用户导航到提交下更改的文件：

![验证提交的更改](https://www.toolsqa.com/gallery/Git/6.Verify%20Committed%20Changes.png)

此页面将显示在提交期间执行的更改，即更改之前和之后。上图中粉红色的线表示被删除的线。人们可以通过在线旁边的( - ) 符号来识别它。同样，添加的行有一个 ( + ) 号。

用户还可以查看该网页的 URL。它类似于通用形式的github.com/repository_name/hash_code_value 。这就是哈希码如此重要的原因。如果用户有哈希码，则可以直接导航到 GitHub 上的任何提交。

4. 检查历史：此按钮将帮助用户检查存储库的历史，直到执行此提交的那个时间点。因此，如果我们在任何先前的提交块上单击此按钮，则不会显示此提交，因为它发生在该提交之后。用户只能在那个时间点之前看到存储库的状态。

接下来，选择历史记录按钮：

![验证提交的更改 - Toolsqa 存储库](https://www.toolsqa.com/gallery/Git/7.Verify%20Committed%20Changes%20-%20Toolsqa%20Repository.png)当该特定提交发生时，它将把用户带到存储库主页，并显示相同的提交消息和哈希码等值。由此，我们确信我们所做的更改反映在 GitHub 帐户中。

总而言之，我们清楚用户如何验证推送到 GitHub 帐户远程存储库的所有更改的所有不同方式。我们现在将继续学习下一个关于 Git 的教程。
在整个课程中，我们详细讨论了[连接本地存储库与远程存储库](https://www.toolsqa.com/git/local-repository-remote-repository/)以及[将存储库克隆](https://www.toolsqa.com/git/git-clone/)到本地机器。这篇文章的内容很明显。这是该系列的下一个里程碑，也是有关将用户在本地系统上所做的更改反映到 GitHub 帐户/远程存储库的过程的信息。是的，我们将学习 Git Push。

在本文中，我们将介绍：-

-   什么是 Git 推送命令？
-   Git如何将本地仓库的修改推送到远程仓库？
-   Git 中可用的选项 Git 中的推送命令
    -   Git 推送中的修剪选项
    -   Git Push 中的 Dry Ryn
    -   Git 推送中的原子性
    -   全部在 Git 推送中

在开始本教程之前，我们将要求用户学习以下内容。

在 Git 中推送更改的先决条件：

-   如何将 Git 本地存储库与远程存储库连接(参考[链接](https://www.toolsqa.com/git/local-repository-remote-repository/))。
-   如何将存储库分叉到 GitHub 帐户(参考[链接](https://www.toolsqa.com/git/git-fork/))。
-   将存储库克隆到本地计算机的过程(参考[链接](https://www.toolsqa.com/git/git-clone/))。

## 什么是 Git 推送命令？

执行 git push 命令时，会将用户在本地计算机上所做的更改推送到远程存储库。一旦用户克隆了远程存储库并在其本地设备中进行了必要的更改，就需要将这些更改推送到远程存储库。原因是，它们可以被其他用户共享和使用。Git push 命令就可以做到。这些更改代表对存储库执行的承诺，而不是未提交的更改(如果有)。

除此之外，如果 GitHub 云不反映用户对本地系统所做的更改，则对贡献者和查看者来说毫无价值。想象一个用户正在修改一些软件(第三方存储库)，并且合并完成的更改并不容易。例如，如果你在一个团队中从事单个项目，所有团队成员都可以将他们的代码更改推送到 Github 远程存储库。此外，其他成员可以从该远程存储库中分叉和拉取更改。因此，多个用户与所有团队成员共享他们的代码更改变得毫不费力。

为了能够推送到你的远程存储库，你必须确定你对本地存储库的所有更改都已提交。

将 Git 推送视为 Git 中同步过程的一部分。同步发生在源和接收者可能不同的本地和远程存储库之间。还有很多其他部分需要同步，git push是其中之一，因为它会上传在本地存储库上完成的更改，以保持远程存储库是最新的。它没有什么复杂的，概念也很简单，就像它的语法一样。

![Git 推送过程](https://www.toolsqa.com/gallery/Git/1.Git%20Push%20Process.webp)

上图概括了这个概念。

1.  用户克隆存储库作为在存储库中进行某些更改的第一步。
2.  此后，他们继续对本地系统进行更改并将这些更改添加到临时区域。
3.  在完成所有更改后，用户将所有更改提交到本地存储库。
4.  在他们满意之后，他们将这些更改推送到远程服务器。最后，它同步本地和远程存储库。

### Git中Git Push命令的语法：

通过键入以下命令执行 Git push 命令：

```
git push <remote_repo> <branch_name>
```

-   remote_repo：这是我们将更改推送到的远程存储库的名称(或别名)。
-   branch_name：这是用户推送到远程存储库的分支。

我们会在 GitHub教程的 Branches 中讲到分支。但在此之前，请想象 Git 中的分支类似于树中的分支。每个分支代表正在开发的新功能或修改。另外，主分支就像树干一样是稳定的代码，也叫主分支。这反过来又有助于分支的不稳定代码远离稳定的主代码。

在下一节中，我们将看到如何将更改推送到远程云存储库。

## 如何在 Git 中将更改从本地存储库推送到远程存储库

要将某些更改推送到远程存储库，存储库必须首先包含本地系统上的一些提交。因此，在本节中，我们将首先对存储库进行一些更改。其次，我们将提交这些更改，最后，我们将它们反映在远程存储库中。

在将更改创建到存储库之前，请确保你已执行以下操作：

-   你已将存储库分叉到 GitHub 帐户。
-   你已将相同的存储库克隆到本地计算机。

注意：在本教程中，我们将使用在之前的教程中已经分叉和克隆的[ToolsQA 存储库。](https://github.com/harishrajora805/ToolsQA)用户可以自由使用任何公共存储库。但是，建议使用与本教程相同的存储库。

[首先，作为一个好的做法，请通过git status](https://www.toolsqa.com/git/git-status-command-in-git/)命令检查你是否有一个干净的存储库(没有待提交的更改)。

![Git状态命令](https://www.toolsqa.com/gallery/Git/2.Git%20status%20command.webp)

执行git status命令后出现如下几行：

在分支 master上：表示我们当前在 master 分支上。由于还没有其他分支，我们默认在 master 分支上。

你的分支是最新的 origin/master： Origin 是我们在将本地存储库与远程存储库连接时提供的远程存储库的名称。请参阅此[链接](https://www.toolsqa.com/git/local-repository-remote-repository/)以了解更多信息。

1.  使用ls命令列出存储库中的所有文件。

![ls命令列出文件](https://www.toolsqa.com/gallery/Git/3.ls%20command%20to%20List%20files.webp)

由于只有一个文件(README.md 只是说明)，让我们对其内容进行一些更改。

1.  使用你喜欢的编辑器打开文件并对文件进行任何更改。
2.  我们已将文件更改为以下代码。

![更改网页html代码](https://www.toolsqa.com/gallery/Git/4.changed%20web%20page%20html%20code.webp)

注意：这是 vi 编辑器的屏幕截图。你可以使用你选择的任何编辑器。

1.  添加对暂存区所做的更改并提交这些更改。

![Git Add - 提交克隆 web_page](https://www.toolsqa.com/gallery/Git/5.Git%20Add%20-%20committing%20clone%20web_page.webp)

注意： GitHub 和 Git 将仅通过提交识别任何更改。如果用户还没有提交更改并尝试将更改推送到 GitHub，它会显示“ Everything is up-to-date ”消息。

1.  键入以下命令将这些更改推送到你的 GitHub 存储库中，然后按 Enter 键。

```
git push origin master
```

![推送命令](https://www.toolsqa.com/gallery/Git/6.git%20push%20command.webp)

注意：请参阅上面的语法部分以了解此命令的语法。

1.  作为安全的一部分，GitHub 会提示用户提供凭据。提供你的凭据并点击“登录”按钮。

![github 提示提供凭据](https://www.toolsqa.com/gallery/Git/7.github%20prompt%20to%20Provide%20credentials.webp)

1.  一旦用户获得批准并合并更改，用户将在 Git Bash 中收到以下消息。

![git push 命令成功消息](https://www.toolsqa.com/gallery/Git/8.git%20push%20command%20success%20message.webp)

注意：最后两行如下：

https://github.com/harishrajora805/ToolsQA.git： 反映更改的存储库 URL。

1b4522a..285f559：这描述了两个分支的哈希值。因此，GitHub 上反映的最终提交的哈希值为 285f559。

master -> master：行 master-> master 显示了合并发生到目标分支的源分支。在上面的场景中，两者都是 master 分支。

突出显示的行上方的行写为Writing Objects: 100%是必不可少的。在 Git 中，只看这一行就可以判断 push 命令是否执行成功。如果显示为 100%，则所有更改已成功推送到云端。

除了我们上面讨论的简单、直接的命令，就像 Git 中的任何其他命令一样，我们可以在执行命令时使用选项来完成特定任务。例如，如果你想推送所有分支，你会使用所有选项等等。让我们看看 Git 中的一些选项。

## Git 中可用的选项 Git 中的推送命令

正如上一节所述，Git push 命令中有许多可用选项，可以帮助我们仅执行一次即可完成某些特定任务。在本节中，我们将带你了解 git push 命令中最基本和最常用的选项。

### Git 推送中的修剪选项

如果本地存储库中不存在任何同名分支，则 git push 命令中的 --prune 选项将从远程存储库中删除分支 XYZ。

用法：`git push --prune remote XYZ`

### Git 推送中的试运行选项

此选项将执行并显示 git push 命令的执行，但不会将任何更新发送到远程存储库。

用法：`git push --dry-run <remote> <local_branch>`

### Git Push 中的原子选项

Git Push 中的原子选项提供对远程存储库的原子操作，即每次引用更新或什么都不更新。

```
git push --atomic <remote_repo> <working_branch>
```

### Git 推送中的所有选项

所有选项都会将所有分支及其提交的更改推送到远程存储库。

用法：`git push --all <remote>`

至此，成功的推送操作完成。现在不要放松；工作没有在这里完成。仅仅将更改推送到 GitHub 是不够的。应该对帐户中的这些更改进行验证。除此之外，还会进行分析，说明这些更改是如何反映出来的。让我们在下一个教程中分析差异并查看它们是否更新。
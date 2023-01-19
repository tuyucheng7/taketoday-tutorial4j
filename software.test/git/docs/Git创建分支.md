在上一个教程中，我们讨论[了 Git 中什么是分支](https://www.toolsqa.com/git/branch-in-git/)。它为我们提供了分支概念的总体概述。分支可以直接通过远程[GitHub 存储库](https://www.toolsqa.com/git/difference-between-git-and-github/)创建，也可以通过我们本地系统上的 Git 创建。虽然在现实场景中，直接通过GitHub创建分支并不是创建分支的首选。开发人员的首要选择是通过本地计算机上的控制台/终端在 Git 中创建分支，并将这些更改推送到 GitHub 上的远程存储库。在本教程中，我们将了解：

-   在 Git 中获取分支
    -   查看当地分支机构
    -   查看远程分支
-   如何在 Git 中创建本地分支？
    -   如何将本地分支推送到远程仓库？
-   S巫术分支
-   在单个 git 命令中创建和切换分支

## 在 Git 中查看分支

在我们继续在本地系统上创建分支之前，我们需要学习如何在本地和远程存储库上查看现有分支。

### Git如何查看本地分支？

查看分支有助于我们检查远程和本地存储库之间的同步。我们可以通过一个简单的命令查看所有的本地分支：

```
git branch
```

![Git Create Branch - 在Git中查看本地分支的命令](https://www.toolsqa.com/gallery/Git/1.Git%20Create%20Branch%20-%20Command%20to%20view%20local%20branches%20in%20Git.png)

将[存储库克隆](https://www.toolsqa.com/git/git-clone/)到本地计算机后，所有分支都将通过此命令可见。

### Git如何查看远程分支？

执行不带任何标志的 git branch只会显示那些在本地可用的分支。由于我们有 master 分支，因此只有 master 分支显示在输出中。要检查所有分支(包括远程分支)，请键入以下命令：

```
git branch -a
```

![Git Create Branch - 在git中查看远程分支的命令](https://www.toolsqa.com/gallery/Git/2.Git%20Create%20Branch%20-%20Command%20to%20view%20remote%20branches%20in%20git.png)

执行命令列出所有分支，如下图：

![在 git 中查看远程分支的命令 - Outlut](https://www.toolsqa.com/gallery/Git/3.Command%20to%20view%20remote%20branches%20in%20git%20-%20Outlut.png)

在高亮的红色中，可以看到多了一个分支，就是“ dev ”分支。当我们在下一节中创建更多分支时，我们将在此处反映它们。我们会不时检查以向用户显示所有可用的分支机构。

## 如何在 Git 中创建本地分支？

在 Git 中创建分支是一个简单的分步过程。但是，在开始之前，请通过运行[git status](https://www.toolsqa.com/git/git-status-command-in-git/)命令确保你有一个干净的工作目录(没有要提交的内容)。

打开Git Bash并导航到本地工作存储库。键入以下命令以查看所有分支。

```
git branch
```

![Git 创建分支](https://www.toolsqa.com/gallery/Git/4.Git%20Create%20Branch.png)

本地分支与上一节中所示的相同，即“ master ”。

让我们现在在我们的本地工作存储库中创建一个新分支。键入以下命令以创建一个名为“ prod ”(production 的缩写)的新分支。

```
git branch <branch_name>
```

![Git Create Branch - 创建一个新的本地分支的命令](https://www.toolsqa.com/gallery/Git/5.Git%20Create%20Branch%20-%20Command%20to%20Create%20a%20new%20Local%20Branch.png)

这是在 Git 中创建分支的初始且更简单的方法。我们将在本教程后面看到另一种方法。

`git branch`现在再次输入命令检查本地系统上的分支：

![git_branch_list](https://www.toolsqa.com/gallery/Git/6.git_branch_list.png)

注意：我们在本地工作目录中的分支创建现已完成。注意“ dev ”分支`\`前面的“ ”。星号表示我们当前所在的当前分支。在上一张图片中查看“主人”拥有星星的地方。

正如我们可能假设的那样，新分支还必须反映在我们的 GitHub 帐户上，并将新分支添加到分支列表中。让我们检查一下。

我导航到我的帐户并在“分行”下拉列表中检查分行数量。

![查看远程分支](https://www.toolsqa.com/gallery/Git/7.View%20branch%20on%20Remote.png)

该死！我仍然有以前创建的分支，我在本地创建的新分支 ( prod ) 尚未在此处同步。

由此，我假设创建一个新分支是对存储库所做的更改，就像任何其他更改一样，因此我需要通过 git push 命令将我们的更改推送到远程存储库。所以，让我们在这里尝试同样的事情，看看它是否有效。

### 如何将本地分支推送到远程仓库？

现在我们已经在本地机器上创建了分支，我们需要在远程存储库上反映相同的内容。使用以下命令更新 GitHub 仓库上的本地分支。

```
git push -u origin prod
```

![Git Create Branch - 在 Git 上推送本地分支的命令](https://www.toolsqa.com/gallery/Git/8.Git%20Create%20Branch%20-%20Command%20to%20Push%20local%20branch%20on%20Git.png)

注意：-u 标志为 prod 分支添加跟踪引用，以便你即将进行的推送不需要任何额外参数，并且本地分支链接到远程分支。

按回车键并执行命令。

![Git 创建分支 - 在 Git 上推送本地分支的命令 - 输出](https://www.toolsqa.com/gallery/Git/9.Git%20Create%20Branch%20-%20Command%20to%20Push%20local%20branch%20on%20Git%20-%20Output.png)

此消息显示我们的分支已全部设置并跟踪。让我们在我们的 GitHub 帐户上再次确认一下。

![prod_new_branch](https://www.toolsqa.com/gallery/Git/10.prod_new_branch.png)

Branch 下拉列表显示新分支已成功添加并同步到 GitHub 远程存储库中。

## 如何在 Git 中切换分支？

由于我们正在处理多个分支，因此了解如何在这些分支之间切换至关重要。此外，我们应该知道如何分别处理它们中的每一个。切换分支是一个非常频繁的操作，所以它对我们来说也是非常重要的。

在接下来的步骤中，我们将尝试将分支从 master 切换到 prod。

检查你当前所在的分支，它在目录名称旁边可见。

![current_branch_master](https://www.toolsqa.com/gallery/Git/11.current_branch_master.png)

通过执行命令切换到“ prod ”：

```
git checkout prod
```

![Git Create Branch - 在 Git 中 Checkout Switch barnch 的命令](https://www.toolsqa.com/gallery/Git/12.Git%20Create%20Branch%20-%20Command%20to%20Checkout%20Switch%20barnch%20in%20Git.png)

给你。我们已经成功地将分支从 master 切换到 prod。现在，无论你做什么操作，它们都会登录到 prod 分支，因为现在你已经与 master 分离了。更常见的是，在使用 Git 工作时，我们会创建一个分支并从那个时间点开始处理它。它通常需要两个命令：

-   `git branch`创建分支的命令
-   `git checkout`命令切换到分支

执行这两个分支都需要一些时间，幸运的是，Git 有一个解决方案。让我们看看如何。

## 如何使用单个 Git 命令创建和切换分支？

Git 为我们提供了创建分支并通过执行单个命令切换到该分支的选项。由于我们经常需要创建一个分支并同时切换到它，所以这个命令对实现相同的目标有很大帮助。执行此命令并检查输出：

```
git checkout -b <name_of_branch>
```

![git_checkout](https://www.toolsqa.com/gallery/Git/13.git_checkout.png)

注意：这里的标志“ b ”告诉 Git 创建一个新分支，分支名称在标志后面。如果没有指定新分支的标志，Git 会抛出“ prod 不匹配任何文件”错误。

执行这条命令，你会自动切换到新创建的分支。

![在 Git 中创建和切换分支的单个命令](https://www.toolsqa.com/gallery/Git/14.Single%20Command%20to%20Create%20and%20Switch%20Branch%20in%20Git.png)

注意：Git Branch 命令不会像 git checkout 命令那样自动切换到新创建的分支。

一旦我们完成这些操作，我们就有了一个新创建的分支来工作。可见，知道了创建，就应该知道如何[删除Git分支](https://www.toolsqa.com/git/git-delete-branch/)。在下一篇教程中，我们会讲到分支的删除以及为什么要删除分支。
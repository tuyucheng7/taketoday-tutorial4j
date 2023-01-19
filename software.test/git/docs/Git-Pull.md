在上一个教程中，我们熟悉了[Git fetch 和 Git merge 命令](https://www.toolsqa.com/git/git-fetch/)。两者在 Git 中都非常流行，使用非常频繁。Git fetch 和 Git merge一起用于合并更改并接受它们。问题是，如果用户一天使用 Git fetch 十次并且所有更改都必须合并，那么 git merge 也会被使用十次。有什么东西可以结合这两个过程吗？是的，肯定有。Git fetch 和 Git merge 命令非常常用，以至于 Git 有一个特殊的命令，将这两个命令组合成一个命令，称为Git Pull命令。本教程是关于：

-   什么是 Git 拉取？
    -   如何使用 Git 拉取？
    -   什么时候使用 Git Pull？
    -   Git Pull 中有哪些不同的选项可用？

## 什么是 Git 拉取？

Git pull是一种通过单个命令执行 git-fetch 和 git-merge 组合操作的神奇方法。“ Pull ”，这是不言自明的，描述了用户正试图从存储库中获取一些东西。在某种程度上，“ fetch ”不是正确的词，因为我们已经讨论过 git fetch 命令。如果你认为 Git Pull 所做的就是抓取，那么为什么我们对Git Fetch不满意？

这就是我一开始所说的魔法。Git Pull 将在不通知用户的情况下执行 Git Fetch，并在不询问用户的情况下自动合并这些更改。难以消化？让我们再看一点。

![git pull 命令新建](https://www.toolsqa.com/gallery/Git/1.git%20pull%20command%20new.png)

执行 git pull 命令将合并更改，而不会通知用户或显示正在合并的更改。

用户只是被告知命令的结果，操作是成功还是失败，包括任何警告等。这听起来可能有风险，但在行业中，git pull 得到了非常普遍的使用。“有风险”，git pull 甚至会合并那些不需要的更改或你不想合并的更改。还记得 Git Fetch 曾经为你带来存储库上发生的更改吗？好吧，Git Pull 假定存储库中发生的任何更改都需要合并。

该命令本身带来的副作用或预防措施是“合并冲突”。Git pull有时会在 Git 中发出“合并冲突”的警告。什么时候发生，如何解决？我们将在后面的部分简要介绍。

### 如何使用 Git Pull 命令？

我们可以通过在 Git Bash 中键入以下命令来使用 Git pull 命令。

```
git pull
```

![git pull 命令的使用方法](https://www.toolsqa.com/gallery/Git/2.how%20to%20use%20git%20pull%20command.png)

请注意我在上图中标记的 2 个部分。

第一部分与git fetch命令具有相同的输出(参考 [Git Fetch 命令](https://www.toolsqa.com/git/git-fetch-and-git-pull/))，而第二部分具有与git merge命令相同的输出。它证明 git pull 是git fetch 和 git merge命令的混合体，应该谨慎使用。

### 什么时候使用 Git Pull？

用户可能想知道，他们什么时候应该使用 Git fetch，什么时候应该使用 Git pull 命令。Git fetch 通常被认为是更安全的 Git Pull 版本，如果用户是[Git](https://www.toolsqa.com/git/git-fetch-and-git-pull/)的新手，应该使用它。如果新用户有足够的信心，他们可以只在干净的工作目录(没有提交的更改)上使用 git pull 命令。

对于有经验的用户，使用 git fetch 通常是在其他人也在同一个分支上与你一起工作的情况下。该场景可能看起来像你的朋友会要求你查看他们在分支上所做的一些更改，如果你愿意则合并。现在由于你对更改的合并不够确定，你将首先获取这些更改，审查它们，然后合并。当一个人在分支上单独工作时，我们使用 Git pull。由于没有必要再次检查你的更改，你可以直接将它们拉到你的存储库中。

使用 Git pull 命令与使用 Git merge 命令没有什么不同。请记住，git pull 是 git fetch 和 git merge 的捷径。这意味着我们不需要执行 git fetch 和 git merge，更改将直接合并。

### Git Pull 中的不同选项

与 Git 中的任何其他命令一样，pull 命令也拥有一些快速选项，有助于自然而有效地使用该命令。

#### 无提交选项

no-commit 选项将提取更改，但合并不会是显式提交，即不会列出合并提交。

相同的命令是：

```
git pull --no-commit
```

![git pull - 没有提交选项](https://www.toolsqa.com/gallery/Git/3.git%20pull%20-%20no%20commit%20option.png)

#### 变基选项

rebase 选项在将一个分支合并到另一个分支后创建线性提交历史记录。除此之外，Git rebase 选项有助于透明的工作流程。而且，即使它涉及多个分支，它看起来像一个具有线性工作流程的单个分支。

![变基前的 git 分支](https://www.toolsqa.com/gallery/Git/4.git%20branches%20before%20rebase.png)Rebase 之前的 Git 分支[/caption]

![变基后的git分支](https://www.toolsqa.com/gallery/Git/5.git%20branches%20after%20rebase.png)

Git Branches After Rebase[/caption]
如果你一次查看所有提交，使用 Git 合并会创建一个混乱的图表。为了实现清晰的工作流程，我们继续使用 Git rebase，并且项目的开发线看起来很干净。

该命令可以执行为：

```
git pull --rebase
```

![git pull rebase 选项](https://www.toolsqa.com/gallery/Git/6.git%20pull%20rebase%20option.png)

使用Git rebase 命令的缺点是，随着提交历史变得线性，它会使开发人员和测试人员难以识别存储库中所做的小提交和更改。由于这个原因，开源项目通常不使用“ rebase ”命令，但一如既往，这是个人选择。

它结束了教程。到现在为止，我们已经介绍了 Git 中的所有主要命令及其用法。我希望你经常练习它们。在下一节中，我们将讨论 Git 中的分支并将它们形象化以理解为什么它们是 Git 最重要的方面。

## Git Pull 的常见问题

为什么我们一般把git pull命令写成git pull origin master？

' git pull origin master ' 将仅获取和更新远程存储库中名为 master 和 origin 的特定分支。通常，Git 中的默认分支是 master 分支，并且它不断更新。用户可以使用任何分支名称从远程拉取该分支。

git pull 是否获取所有分支？

是的，如果使用的命令只是“ git pull ”，Git 将获取对跟踪远程分支的本地分支的所有更新引用。

我可以撤消 git pull 吗？

是的，我们可以通过命令恢复 Git Pull 所做的更改`git reset --hard`。Git reset hard 将分支重置为用户刚刚获取的数据，而 hard 选项更改工作树中的文件以匹配分支中的文件。
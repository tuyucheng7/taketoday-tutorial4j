在上一个教程中，我们了解了[Git 推送](https://www.toolsqa.com/git/git-push/)。Git push 命令将用户在本地仓库所做的修改推送到远程仓库。将更改推送到远程存储库使整个团队或与项目相关的组织能够看到这些更改并相应地工作。但是，这是用户进行更改后的故事。让我们切换到另一面，看看看到变化的用户的故事。当用户对项目进行一些更改(推送)时，将这些更改合并到他们的本地系统以与稳定代码并行的过程称为拉取。这是本教程的基础：

-   Git Fetch 命令的重要性。
-   Git Merge 的重要性。
-   Git Pull 以及如何在 Git 中使用它。

与此同时，它还将包含在 Git Bash 中使用命令的演示。

## Git中的Git Fetch是什么？

Git fetch 命令帮助用户将提交、引用和文件从远程存储库下载到本地存储库。这些提交是由组织中与项目相关的团队成员和人员完成的，或者可能是用户自己直接在远程存储库上完成的。换句话说，执行git fetch命令将帮助你查看存储库中所有队友的更新。你可能会想，如果你不想保留更改怎么办？好吧，Git fetch 根本不会损害你的工作存储库。Git fetch 是一种很好的方式，可以让你站在一个可以看到更改并决定是要保留还是丢弃它们的地方。保留更改在 Git 中称为合并这是一个显式操作。所以在你按下 git merge 之前，你不会添加任何东西。下一节专门介绍 Git 中的合并。

![Git 获取和 Git 拉取](https://www.toolsqa.com/gallery/Git/1.Git%20Fetch%20and%20Git%20Pull.webp)

## Git 中的 Git Merge 是什么？

Git merge 命令是你决定合并使用 Git fetch 命令看到的更改的肯定结论。让我理顺一下。一旦用户喜欢自己在远程存储库上所做的更改或他们的队友所做的更改，他们就可以将这些更改合并到存储库(远程和本地)。这些可能来自多个分支的更改被合并到一个分支中。尽管合并更改的用户有责任确保他们的头指针指向他们想要合并更改的同一分支。不遵守这一点将在存储库中造成不必要的不一致。让我们对 git fetch 和 merge 命令进行一些实践。

## 在 Git 中使用 Git Fetch 和 Git Merge

按照以下步骤在 Git 中执行提取和合并操作：

1.  打开你的 GitHub 帐户并导航到[ToolsQA](https://github.com/harishrajora805/ToolsQA)存储库。
2.  打开Readme.md文件。

![自述文件](https://www.toolsqa.com/gallery/Git/2.readme_md.webp)

1.  按编辑图标编辑文件。

![编辑自述文件](https://www.toolsqa.com/gallery/Git/3.edit%20readme.png)

1.  以有意义的方式编辑文件描述。

![自述文件编辑新](https://www.toolsqa.com/gallery/Git/4.readme%20edit%20new.png)

1.  向下滚动并使用有意义的描述提交更改，然后按commit changes。

![提交自述文件](https://www.toolsqa.com/gallery/Git/5.commit%20readme%20file.png)

1.  更改将反映在README.md文件中。

![readme_change_reflected](https://www.toolsqa.com/gallery/Git/6.readme_change_reflected.webp)

现在我们对远程存储库进行了一些更改，我们必须在存储库的本地工作副本中获取这些更改。

1.  打开Git bash并导航到你的工作目录。
2.  检查一个干净的工作存储库(没有提交的更改)。
3.  在 Git Bash 中执行以下命令：

```
git fetch
```

![Git 获取命令](https://www.toolsqa.com/gallery/Git/7.Git%20Fetch%20Command.webp)

最后两行如下：

-   https://<repo_url>：存储库的 URL。
-   e7b37f6..47b2bf6：第一个哈希是最后一次合并提交到本地存储库的哈希，而 47b2bf6 是新的提交/更改哈希代码。

因此很明显，我们在远程仓库上有一个提交尚未合并到本地仓库中。让我们开始吧。

1.  执行以下命令将更改合并到本地工作存储库中。

```
git merge
```

![git 合并命令](https://www.toolsqa.com/gallery/Git/8.git%20merge%20command.webp)

注意：如果你已经在自己的仓库上执行了上述功能，你必须推送更改并使用`git push`命令告诉远程服务器你已拉取更改。由于这是一个分叉的存储库，我们不需要在这里这样做。

Git 作为一个[版本控制系统](https://www.toolsqa.com/git/version-control-system/)，确保即使在合并操作完成后，这些独立分支的提交历史也保持独立。Git merge 在获取更改后执行，即 Git fetch 已经执行。但是，有一种方法可以绕过这个两步过程并将其转换为一个步骤。这在 Git 中称为拉取，并使用`git pull`命令执行。

## Git 中的 Git Pull 命令是什么？

git pull 命令是 git fetch 和 git merge 的组合命令。执行 git pull 命令将合并更改，而不会通知或显示正在合并的更改。这听起来很冒险。git pull 甚至会合并那些不需要的更改或你不想合并的更改，这在某种程度上是有风险的。具有讽刺意味的是，一般来说，每个人都使用术语“拉取我的更改”，而它更像是“获取并合并我的更改”。Git pull 不是合并更改的推荐方法，因为它会在 Git 中引发“合并冲突”。

当两个用户试图修改同一文件的同一行时，就会出现合并冲突。例如，一个用户试图删除该行，而另一个用户修改了它。

![Git 获取和 Git 拉取](https://www.toolsqa.com/gallery/Git/9.Git%20Fetch%20and%20Git%20Pull.webp)

Git fetch 是 git pull 的更安全版本，应该改用它。尽管如果用户有足够的信心，建议他们只在干净的工作目录(没有提交的更改)上使用git pull命令。

使用Git pull命令与使用 Git merge 命令没有什么不同。请记住，git pull 是 git fetch 和 git merge 的组合命令。这意味着我们不需要执行 git fetch 和 git merge，更改将直接合并。

![git 拉命令](https://www.toolsqa.com/gallery/Git/10.git%20pull%20command.webp)

请注意我在上图中标记的 2 个部分。

第一部分与上一节中的git fetch命令具有相同的输出，而第二部分具有与git merge命令相同的输出。这基本上证明了git pull是git fetch和git merge命令的混合体，应该谨慎使用。

这使我们结束了本教程。到目前为止，我们已经介绍了 Git 中的所有主要命令及其用法。Git 中的一切都由这些命令构成。我希望你经常练习它们。在下一节中，我们将讨论 Git 中的分支并将它们形象化以理解为什么它们是 Git 最重要的方面。
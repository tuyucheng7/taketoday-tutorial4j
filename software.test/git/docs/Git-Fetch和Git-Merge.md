在上一个教程中，我们了解了[Git push 命令](https://www.toolsqa.com/git/git-push/)。Git push 命令将用户在其本地存储库中所做的更改推送到远程存储库。此外，将更改推送到远程存储库使整个团队能够协作和共享他们的工作。但是，这是故事的一方面，用户将他们的本地更改推送到远程存储库。另一方面，所有其他团队成员需要将这些更改从远程存储库同步到他们的本地存储库。因此，为此，团队成员需要获取更改并将它们合并到他们的本地存储库中。同样，在本教程中，我们将介绍Git Fetch和Git Merge，这将简要说明用户如何将更改从远程存储库同步到他们的本地存储库：

-   Git 获取
    -   什么是 Git 获取命令？
    -   如何使用 Git Fetch 命令？
    -   Git Fetch 命令中的选项
-   Git 合并
    -   什么是 Git 合并命令？
    -   如何使用 Git 合并命令？
    -   Git 中的快进合并
    -   Git 合并中的选项

## 什么是 Git 获取命令？

Git fetch 命令帮助用户将提交、引用和文件从远程存储库下载到本地存储库。换句话说，执行此命令将帮助你查看远程存储库上的所有更新。你可能会想，如果你不想保留更改怎么办？好吧，这个命令根本不会损害你的工作存储库。

![仅获取 git](https://www.toolsqa.com/gallery/Git/1.git%20fetch%20only.png)

Git fetch 是一种很好的方式，可以让你站在一个可以看到更改并决定是要保留还是丢弃它们的地方。

### 如何使用 Git fetch 命令？

在使用该命令之前，让我们对远程存储库进行一些更改，以便我们可以通过本地存储库获取它们。

在你的 GitHub 帐户中执行以下步骤：

1.  首先，打开你的 GitHub 帐户并导航到[ToolsQA](https://github.com/harishrajora805/ToolsQA)存储库。
2.  其次，打开Readme.md文件。

![自述文件](https://www.toolsqa.com/gallery/Git/2.readme%20md.png)

1.  第三，按编辑图标编辑文件。

![编辑自述文件](https://www.toolsqa.com/gallery/Git/3.edit%20readme.png)

1.  第四，以有意义的方式编辑文件描述。

![自述文件编辑新](https://www.toolsqa.com/gallery/Git/4.readme%20edit%20new.png)

1.  之后，向下滚动并使用有意义的描述提交更改，然后按commit changes。

![提交自述文件](https://www.toolsqa.com/gallery/Git/5.commit%20readme%20file.png)

1.  更改将反映在README.md文件中。

![自述文件更改反映](https://www.toolsqa.com/gallery/Git/6.readme%20change%20reflected.png)

现在我们对远程存储库进行了一些更改，我们必须在存储库的本地工作副本中获取这些更改。

1.  打开Git bash并导航到你的工作目录。
2.  检查一个干净的工作存储库(没有提交的更改)。
3.  最后在Git Bash中执行命令：`git fetch`

![git 获取输出](https://www.toolsqa.com/gallery/Git/7.git%20fetch%20output.png)

最后两行如下：

https://<repo_url>：存储库的 URL。

e7b37f6..47b2bf6：第一个哈希是最后一次合并提交到本地存储库的哈希，而 47b2bf6 是来自远程存储库的新提交/更改哈希代码。

用户还可以通过 git log 命令检查提交和最近的活动。可以参考Git中什么是[Git Log命令](https://www.toolsqa.com/git/git-log/)和[Git Diff命令](https://www.toolsqa.com/git/git-diff/)。

如果你的屏幕显示类似的输出，如上图所示，则你已成功从远程存储库中获取更改。

### Git Fetch 命令中的选项

与 Git 中的任何其他命令一样，Git fetch 命令也包含一些用于快速高效地使用该命令的选项。让我们在下面讨论它们：

#### 所有选项

“所有选项”获取所有远程引用、文件等。

```
git fetch --all
```

![git 获取所有](https://www.toolsqa.com/gallery/Git/8.git%20fetch%20all.png)

#### 试运行选项

空运行选项将向用户显示命令将如何执行而不进行任何更改。

```
git fetch --dry-run
```

![git fetch 试运行](https://www.toolsqa.com/gallery/Git/9.git%20fetch%20dry%20run.png)

## 什么是 Git 合并命令？

Git merge 命令是你决定合并使用 Git fetch 命令看到的更改的肯定结论。让我理顺一下。一旦用户准备好接受来自远程存储库的更改，他们就可以将这些更改合并到本地存储库。顾名思义，你确认“合并”这些更改。

下图可以帮助直观地描述 Git Merge 的含义。

![git 获取](https://www.toolsqa.com/gallery/Git/10.git%20fetch.png)

通过 fetch 命令提取在远程完成的更改，然后如果用户批准，它们将与本地存储库合并。

### 如何使用 Git 合并命令？

要合并我们在上一节中获取的更改，请执行以下命令：

```
git merge
```

![混帐合并](https://www.toolsqa.com/gallery/Git/11.git%20merge.png)

如果你看到相同的输出，则你已成功将更改合并到本地存储库中。上图中，第三行写的是“ Fast-forward ”。这是因为这是 git 完成的快进合并。让我们看看它是什么。

### Git 中的快进合并

Git 中的快进合并意味着分支有一条线性路径从你要合并到的分支转移。线性路径意味着自从功能分支转移到它正在合并的点后，没有提交到 master 分支。

![线性路径](https://www.toolsqa.com/gallery/Git/12.linear_path.png)

上图显示分支被转移，进行了 3 次提交，而在那个时候，master 分支中没有提交(绿点)。三次提交后，我会将特性分支合并到主分支中，这将导致快进提交。

### Git 合并中的选项

Git Merge 中的 --no-ff 选项

--no-ff 选项阻止 git 以快进方式合并更改。

```
git merge --no-ff
```

![1 没有快进](https://www.toolsqa.com/gallery/Git/13.%20no%20fast%20forward.png)

--Git 合并中的 no-commit 选项

no-commit 选项不会自动提交 git merge 并将请求用户干预完成。

```
git merge --no-commit
```

![1 没有提交 git 合并](https://www.toolsqa.com/gallery/Git/14.1%20no%20commit%20git%20merge.png)

git merge 中还有更多选项，你可以在本地存储库中探索和试用。Git 作为一个版本控制系统，确保即使在合并操作完成后，这些独立分支的提交历史也保持独立。Git 合并发生在更改获取之后，即 Git 获取的执行已经发生。但是，有一种方法可以绕过这个两步过程并将其转换为一个步骤。它被称为拉入 Git，Git pull命令执行它。

在接下来的教程中，我们将学习如何使用 Git pull 命令从远程拉取更改，并将在将两个分支合并为一个分支时使用这些概念。

## 关于 Git Fetch 和 Git Merge 的常见问题

git fetch 和 git fetch - 都一样吗？

Git Fetch 和 Git Fetch - 在操作上都是相似的。当用户使用`git fetch <branch_name>`从特定分支获取更改时，就会出现差异。

git fetch 和 git pull 有什么区别？

git fetch 命令和git pull 命令在操作上都是不同的。Git fetch 获取更改，而 git pull 在获取后合并它们。所以在某种程度上，git fetch 是 git pull 的一部分，因为它首先获取更改，然后执行 git merge。

我可以撤消 git merge 所做的更改吗？

是的，Git 合并更改可以通过以下操作逆转。

-   `git reflog`通过命令确定要返回的提交。
-   通过 执行硬重置以及提交哈希`git reset --hard <hash>`。

如何合并历史不相关的分支？

更常见的是，在通过 git rebase 命令合并时，会弹出一个错误，提示“拒绝合并不相关的历史记录” 。它以前被允许作为默认选项，但从 Git 2.9 开始就被删除了。现在，用户必须明确地命令 Git 合并不相关历史的分支。因此，与 rebase 一起使用的选项是--allow-unrelated-histories。
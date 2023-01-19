到目前为止，在本节中，我们已经了解了很多关于分支的知识。我们学习了[分支的基础知识](https://www.toolsqa.com/git/branch-in-git/)，[如何](https://www.toolsqa.com/git/git-create-branch/)在我们的本地存储库上创建它们，并尝试了一些对它们的操作，例如[删除分支](https://www.toolsqa.com/git/git-delete-branch/)，检出它们等等。但是你还记得我们为什么首先创建分支吗？好吧，如果你认为我们创建分支是为了在不同时间处理不同的功能，那你是对的。一旦他们的工作完成，我们不会让分支悬空。我们将 Git中的分支与主分支(或任何其他分支) 来描述功能添加成功并准备好发布。这是我们在删除分支之前执行的部分。本教程围绕 Git 中的相同操作展开。但是，在继续之前，你应该了解一些事项。

先决条件：

-   [Git 分支](https://www.toolsqa.com/git/branch-in-git/)
-   [Git 合并](https://www.toolsqa.com/git/git-fetch/)

完成这些先决条件后，你可以继续探索本教程中的以下主题：

-   为什么我们在 Git 中合并分支？
-   Git 如何合并分支？

## 为什么我们在 Git 中合并分支？

我们创建分支以在不干扰主要稳定分支(也称为主分支)的情况下单独处理功能。但是，分支的命运就是要合并。我们创建它们，以便它们可以在完成对它们的工作后合并。将一个分支合并到 master 分支中表示你在该分支中开发的功能已经完成并经过测试，现在可以成为软件的一部分了。

一个小例子可以清楚地说明这一点。假设你目前正在照常开发你的软件，突然，出现了一个新需求。非常紧急，应尽快执行。最简单的步骤是从 production 或 master 或任何其他分支创建一个新分支。处理此分支中的功能。测试这个分支。最后，将其与母版合并。

在 Git 中，单个存储库通常包含多个分支。两个分支的简单合并如下图所示：

![在 Git 中合并分支](https://www.toolsqa.com/gallery/Git/1.Merge%20Branch%20in%20Git.webp)

如上图所示，顶级分支在三次提交后合并到 master 分支。

### 在 Git 中合并分支的语法

可以通过键入命令来执行合并操作

```
git merge <branch_name>
```

那么，当你键入此命令时，Git 在幕后做了什么？当命令执行时，git 重放从这个分支被转移的时间，即从图像中标记为“ 1 ”的时间到分支被合并到 master 的时间，即标记为“ 2 ”的时间的所有提交. 自从分支从主分支分离后发生的这些提交将记录到单个分支，即我们案例中的顶级分支。

## 如何将Git中的分支合并到另一个分支？

在本节中，我们将逐步将一个名为“ dev ”的分支合并到另一个名为“ master ”的分支中。在合并之前，我们需要将一些更改记录到分支中。为此，让我们切换到我们将通过以下命令合并的分支：

```
git checkout <branch_name>
```

![命令切换到我们要合并的分支](https://www.toolsqa.com/gallery/Git/2.command%20to%20switch%20to%20the%20branch%20that%20we%20want%20to%20merge.webp)

现在使用记事本打开一个文件并对该文件进行一些更改。(如果没有则创建一个文件)。

![使用记事本打开文件以对其进行一些更改的命令](https://www.toolsqa.com/gallery/Git/3.command%20to%20open%20a%20file%20using%20notepad%20to%20make%20some%20changes%20to%20it.webp)

关闭文件并提交这些更改(将文件添加到暂存区之后)。

![提交更改的命令](https://www.toolsqa.com/gallery/Git/4.command%20to%20commit%20changes.webp)

使用 oneline 标志执行 Git 日志操作以检查提交(参考 [Git 日志](https://www.toolsqa.com/git/git-log/))。

![git 日志联机合并](https://www.toolsqa.com/gallery/Git/5.git%20log%20oneline%20merge.webp)

现在切换到我们将合并更改的分支(本例中为 master)。

![命令切换到我们要合并更改的分支](https://www.toolsqa.com/gallery/Git/6.command%20to%20switch%20to%20the%20branch%20to%20which%20we%20want%20to%20merge%20the%20changes.webp)

执行以下命令将分支dev合并到分支master。

```
git merge <branch_name>
```

![合并分支的命令](https://www.toolsqa.com/gallery/Git/7.command%20to%20merge%20branches.webp)

输出将显示合并成功以及文件名，即toolsqa.txt。

重新检查日志以查看合并。

![合并分支日志](https://www.toolsqa.com/gallery/Git/8.log%20of%20merge%20branches.webp)

突出显示的行以 ( HEAD -> master, dev ) 开头。Head 是一个指针，告诉我们 HEAD 当前在哪里。在这种情况下，它同时处于两个分支。这两个分支的名字描述的是第二个分支合并到第一个分支，即dev分支合并到master分支，两者在同一个位置。术语“相同位置”意味着合并后不再发生任何提交。如果有，头指针将移动到该提交。

完成后，我们现在可以成功删除分支(参考 [Git 中](https://www.toolsqa.com/git/git-delete-branch/)的删除分支)。

![分支已删除](https://www.toolsqa.com/gallery/Git/9.branch%20deleted.webp)

分支可以合并到用户想要的任何分支。在这个例子中，为了演示，我们使用了 master 分支。值得注意的是，当在 Git 中创建新分支时，它会继承该分支的提交。请看下图：

![合并分支示例](https://www.toolsqa.com/gallery/Git/10.merge%20branches%20example.webp)

顶级分支的创建发生在主分支(绿色圆圈)中的三个提交完成后。底部分支也是如此，它的创建是在执行四次提交之后发生的。现在让我们看看同一张图片，除了最上面的分支外，每个分支都被移除了。

![主分支](https://www.toolsqa.com/gallery/Git/11.Master%20Branch.webp)

如图所示，当我删除所有其他分支时，这看起来像一个线性分支。好吧，就是这样。如果单独来看，从创建分支的角度来看，它继承了所有的提交。此外，它描述的好像从一开始就是项目的一部分。这是有益的，因为我们正在创建的功能可能需要在其开发过程中从以前的提交和标记中获取帮助。你在此分支中执行的提交不会以任何方式妨碍任何其他分支。

我希望你的手现在已经习惯了树枝，并且对它们进行操作对你来说很舒服。在下一个教程中，我们将讨论 Git 中的标签。保持练习！！

## 关于 Git Merge 的常见问题

我可以撤消 git merge 所做的更改吗？

是的，通过 git merge 命令所做的更改可以通过`git reflog`和`git hard reset`命令撤消。

Git 中的压缩是什么？

Git 中的压缩将所有提交压缩为单个提交。多次压缩提交，或者说 n 次提交 n 次会将其压缩为单个提交。下图描述了 git squash：

![git 南瓜](https://www.toolsqa.com/gallery/Git/12.git%20squash.webp)

两个用户修改同一行然后执行git merge会发生什么？

这种情况在 Git 中就是 merge-conflict。在 Git 中，当两个用户试图修改同一行时，Git 会搞不清哪些更改要保留，哪些要拒绝。这是一个合并冲突。

我希望在阅读本文后，你将能够轻松地在 Git 中合并分支。
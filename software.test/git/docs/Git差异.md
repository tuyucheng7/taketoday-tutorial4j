在上一篇关于[Git Log 中有用命令的](https://www.toolsqa.com/git/git-log/)教程中，我们介绍了git log中一些非常流行和重要的命令。这些在 Git 中使用非常频繁。在这些命令的列表中，我还简要提到了git diff 命令。本教程是关于git diff命令的。

先决条件

-   Git 知识 - [什么是 Git 以及为什么要使用它](https://www.toolsqa.com/git/what-is-git/)
-   Git中commit的知识- [Git Commit](https://www.toolsqa.com/git/first-commit-in-git/)

## 什么是 Git 差异命令？

git 中使用 diff 命令来跟踪对文件所做的更改之间的差异。由于 Git 是一个[版本控制系统](https://www.toolsqa.com/git/distributed-version-control-systems/)，因此跟踪更改对它来说非常重要。Diff命令接受两个输入并反映它们之间的差异。这些输入不一定只是文件。它可以是分支、工作树、提交等等。我们将使用`git diff`命令，因为我们将深入学习课程，因为在一个地方定义所有内容是不可行的。检查两个文件的差异是git diff中最常用的操作，我们将以相同的意图继续。

要首先使用 diff 命令，我们必须对文件进行一些更改。我们将对现有文件harish.txt进行更改，并在其中写入“ This is Harish ”。你可以使用[Vi 编辑器](https://www.toolsqa.com/git/set-up-notepad-for-git-bash/)。之后，输入以下命令：

```
git diff
```

![Git 差异](https://www.toolsqa.com/gallery/Git/1.Git%20Diff.png)

注意： Diff 命令将在所有存在的文件中产生更改。仅对于某些特定文件的更改，在命令名称后键入文件名称。

现在让我们看看`git diff`命令如何响应我们刚刚执行的操作。

现在让我们分解 git diff 给出的响应并一一理解所有内容。

1.  第一行显示了在 git diff 中被认为是输入的文件名。你可以看到它们已被标记为a和b以及已作为输入的两个不同文件状态。
2.  这条线没有用。这显示了与命令相关的元数据及其在文件上的执行。[通过我们在Dot Git 文件夹](https://www.toolsqa.com/git/dot-git-folder/)中的讨论，你必须知道，这是 Git 内部使用所需的对象哈希值。
3.  这一行定义了一个符号，称为图例，告诉你用什么来描述第一个文件，用什么来描述第二个文件。可以看到，第一个文件前面用的是-，第二个文件前面用的是+。因此，每当 diff 向你显示与第一个文件相关的更改时，它们将被标记为 - 而第二个文件中的更改将被标记为符号 +。
4.  第四行显示符号@@ 和它前面的符号。它们被称为块。git diff 中的块定义了更改的摘要。在我们下面的图片中可以看到以下块@@ -1,2 +1 @@
5.  这意味着第一行和第二行在第一个文件中被更改，而第一行在第二个文件中被更改。请记住第三点中使用的 - 和 + 符号分别作为第一个和第二个文件的符号。

### Git 差异命令

现在让我们考虑 diff 命令的三种情况，看看它是如何响应的。

-   在文件中添加数据时的 Git Diff
-   在文件中删除数据时的 Git Diff
-   带有颜色词选项的 Git 差异

#### 将数据添加到文件时，Git Diff 的行为如何？

在本节中，我们将看到git diff命令在将数据添加到文件时的行为方式。但在此之前，我们必须通过提交来清除我们在上述部分中看到的更改。要提交，首先，我们需要将文件添加到暂存区。

键入以下命令以将更改添加到临时区域： `git add`。

![Git 全部添加](https://www.toolsqa.com/gallery/Git/2.Git%20Add%20All.png)

一旦一切都完成了。使用以下命令提交更改：`git commit`

这将打开记事本以输入提交消息。输入适当的提交消息并关闭编辑器。在我们完成提交更改后，通过键入以下命令检查git diff是否显示任何内容： `git diff`

![Git 差异检查](https://www.toolsqa.com/gallery/Git/3.Git%20Diff%20Check.png)

如我们所见，没有任何变化，因此我们可以继续将内容添加到我们的文件harish.txt 中。

以前的内容是这是 Harish。我在句子末尾添加了Rajora使完整的句子成为This is Harish Rajora。添加单词后，让我们执行diff命令，看看会发生什么。

![添加内容后的 Git 差异](https://www.toolsqa.com/gallery/Git/4.Git%20Difference%20After%20Adding%20Content.png)

可以理解的是，第一行在文件的两个版本中都发生了变化，并反映了变化。

#### 当文件中的数据被删除时，Git Diff 如何表现？

完成上述部分后，提交更改，这样就没有要显示的更改了。使用与上面相同的 diff 命令来确认它。之后，删除我们在上面添加到原始句子This is Harish的姓氏Rajora 。

执行`git diff`命令查看变化

![删除内容后的 Git 差异](https://www.toolsqa.com/gallery/Git/5.Git%20Difference%20After%20Deleting%20Content.png)

此屏幕截图中没有任何内容需要解释。如有疑问，请尝试修改前一节中提到的要点。

#### 如何在一行中查看差异变化？

很明显，我们在上面的绿色屏幕截图中看到了最新的变化。但是红色的句子并不需要那么多。因为有时候，我们只对更改后的文本感兴趣，而不是它以前的样子。这并不意味着我们不想看到已经发生的变化或差异。我们将通过在diff 命令中使用选项颜色词来清除它。

要使用该选项，请键入命令： `git diff --color-words`

![Git 差异颜色词选项](https://www.toolsqa.com/gallery/Git/6.Git%20Difference%20Color%20Words%20Option.png)

现在只能在一行中看到更改。红色的字表示它已从原始文件中删除。

Git Diff不仅限于此，它还用于许多其他目的，你可以[在此处](https://www.atlassian.com/git/tutorials/saving-changes/git-diff)了解这些目的。你一定也明白这是一个非常重要的命令，将非常常用。当我们转向分支和其他此类概念时，我们将探索此命令的其他部分。到那时继续练习，我们现在将继续下一个教程。
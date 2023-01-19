由于在最后几章中我们学习了如何[添加和跟踪暂存](https://www.toolsqa.com/git/add-and-track-changes-to-staging/)更改、[在 git 中提交更改](https://www.toolsqa.com/git/first-commit-in-git/)以及查看[git 日志](https://www.toolsqa.com/git/git-log/)。完成上述所有操作后，我们对在 Git 中工作有了一个非常熟悉的想法。上面说的命令是 Git 中最重要的东西。

在本教程中，我们将增强git log命令以查看Git 历史记录，并向你展示一些非常有用和最常用的Git 日志命令。请注意，这些命令仅特定于 Git 日志。完成 Git 中的重要概念后，我们将为你提供 Git 备忘单。在本教程中，我们将介绍以下内容：

-   Git 显示命令
-   查看提交日志大小
-   查看日期范围的提交历史记录
-   跳过顶部提交
-   查看作者提交历史
-   以相反的顺序查看提交历史
-   查看提交统计

## 什么是 Git 显示命令？

`Git Show`命令`git log`在输出方面类似。[Git show 还以与我们在git log](https://www.toolsqa.com/git/git-log/)教程中学习的格式相同的格式向你展示输出。一个细微的差别是git show命令向你展示了两件事：

-   HEAD 指向的提交
-   HEAD 指向的文件版本之间的差异

HEAD 指向的提交是你正在处理的最后一个分支的最后一次提交。在这种情况下，我创建了一个名为“ lakshay.txt ”的新文件([触摸命令](https://www.toolsqa.com/git/common-directory-commands-on-git-bash/))并在其中写了一行并提交了更改。之后我尝试输入命令：

```
git show
```

![Git 显示命令](https://www.toolsqa.com/gallery/Git/1.Git%20Show%20Command.png)

`git show`图像中已标记的命令的第一部分与命令类似，`git log`但指向 HEAD。该命令给我们的输出还有第二部分：

![Git 显示命令差异](https://www.toolsqa.com/gallery/Git/2.Git%20Show%20Command%20Diff.png)

这部分称为差异。虽然在这里定义它是不正确的，因为它将在[Git Diff](https://www.toolsqa.com/git/git-diff/)教程中详细讨论。但是既然在这里遇到了，我就简单说一下吧。Git 中的差异告诉你 HEAD 指向的文件之间的差异(默认情况下)。如你所见，Git 在第一行标记了 a/lakshay.txt和 b/lakshay.txt  。它用于表示文件lakshay.txt的两个特定状态。当我们在Diff教程中详细了解它时就会清楚。

如果要`git show`用于特定提交而不是一般命令，请使用该提交的哈希值，如下所示。

![带哈希的 Git 显示命令](https://www.toolsqa.com/gallery/Git/3.Git%20Show%20Command%20With%20Hash.png)

注意：我使用了使用--oneline选项时出现的压缩哈希值。请参阅[Git 日志](https://www.toolsqa.com/git/git-log/)教程以了解更多信息。

## 如何查看特定时间间隔的 Git Commit History？

它是git log中一个不言自明的选项。当我们输入命令时：

```
git log --since=<date>
```

![Git 日志自](https://www.toolsqa.com/gallery/Git/4.Git%20Log%20Since.png)

自该日期以来发生的所有提交都作为输出。它将排除在该日期发生的提交。无需进一步解释。它在git log中用作过滤器。

## 如何跳过 git 日志历史记录中的一些提交？

Skip 选项在 Git Log 中用于跳过 Git Log 中的一些提交。让我们首先看看`git log`是如何显示的，以便你清楚区别。键入`git log --oneline`以查看提交列表。

![Git 日志联机](https://www.toolsqa.com/gallery/Git/5.Git%20Log%20Oneline.png)

现在让我们尝试通过键入以下命令来跳过 4 次提交：

```
git log --skip 4 --oneline
```

![Git 日志跳过](https://www.toolsqa.com/gallery/Git/6.Git%20Log%20Skip.png)

看上面两张图。skip 命令消除了四个最高提交。没有书面说明为什么 skip 命令首先出现在画面中，但它的使用已经超过平均水平，使其成为重要的命令之一。

## 如何查看特定作者的提交？

Git 日志中的作者选项用于过滤掉由特定作者完成的所有提交。毋庸置疑，这是一个非常重要的命令，可以查看属于你团队中某个人的提交，或者可能是你所有的提交。

要执行该命令，请键入以下内容：

```
git log --author=<name of author>
```

![Git 日志作者 Harish](https://www.toolsqa.com/gallery/Git/7.Git%20Log%20Author%20Harish.png)

这些都是 Harish 所做的所有提交。如果我检查 Lakshay 在这个存储库上完成了多少次提交，那么我将只更改命令中的作者姓名。

![Git 日志作者 Lakshay](https://www.toolsqa.com/gallery/Git/8.Git%20Log%20Author%20Lakshay.png)

正如预期的那样，作者姓名 Lakshay 没有任何提交。

## 如何按时间顺序(反向)查看 Git 提交历史记录？

在关于 Git Log 的教程中，我讨论了执行 git log 命令时提交的显示顺序。它们以相反的时间顺序显示，即最先提交的将首先显示。这是有道理的，因为随着我们在存储库中进一步研究项目，我们会更加专注于最新的更改。很明显，我们一次又一次地不会查看较旧的提交。但是，如果你想知道如何按时间顺序获取提交，那么 Git 中的这个选项可以帮你。要按时间顺序查看提交，请键入命令：

```
git log --reverse
```

![Git 日志反转](https://www.toolsqa.com/gallery/Git/9.Git%20Log%20Reverse.png)

注意：最后显示的是 Head 指针。另外请注意，我一直在使用 --oneline 选项以简短明了的方式提供所有内容。请参阅[Git 日志](https://www.toolsqa.com/git/git-log/)教程以获取更多信息。

## 如何查看提交的统计信息？

git log 中的 stat 命令用于查看文件的摘要。看到输出后，我们将看到命令是什么。为此，键入：

```
git log --stat <commit id>
```

![Git 日志统计](https://www.toolsqa.com/gallery/Git/10.Git%20Log%20Stat.png)

作为练习，想想为什么我在 stat 之后使用字母数字词。

如你所见，在提交消息之后，它显示了文件名Harish.txt以及符号 1+。在此符号中，数字表示操作总数，符号(在本例中为+)表示执行的操作。所以这里执行了 1 个操作，即插入，如该行下方所述。

## 如何查看git历史记录中的日志大小？

Git 中的日志大小是 Git 日志中的一个选项，以数字形式告诉你日志大小。键入命令后，它会输出一个带有日志大小的附加行，`<number>`如下图所示：

```
git log --log-size
```

![Git 日志 LogSize 命令](https://www.toolsqa.com/gallery/Git/11.Git%20Log%20LogSize%20Command.png)

注意：我只向你展示了最后一次提交。你将获得一系列按时间倒序排列的提交。

如你所见，控制台上多打印了一行，称为日志大小和一个数字。所以，这个数字是以字节为单位的提交消息长度。各种通过`git log`命令读取提交消息的工具都需要它。通过读取这个数字，他们可以提前分配准确的空间来保存提交消息。还记得我们讨论过[编写好的提交消息](https://www.toolsqa.com/git/writing-good-commit-messages/)以保持消息简短吗？好吧，它在这里节省了内存。

因此，这些是Git Log中一些最有用的命令。记住git log和git commit一样有用，因为你会经常看到你的提交历史，这是 Git 的主要焦点，你可以看到提交历史。我希望你以不同的方式尽可能多地练习这些命令。我们将继续下一个教程。
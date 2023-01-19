在上一篇关于[在 Git 中提交更改](https://www.toolsqa.com/git/first-commit-in-git/)的教程中，我们了解到，首先，我们需要将文件带到暂存区，然后从那里提交。同时处理同一个存储库中的不同文件会使你忘记所做的更改。你可能会忘记你修改了某些内容，或者你可能添加了一个现在没有用的新文件。你需要一些可以帮助你跟踪它的东西。在 Git 中，这项工作是通过名为git status的命令完成的。在本教程中，我们将重点关注 git status 命令。

## Git 状态命令

Git 状态命令在 Git 中用于了解工作树的状态。它显示你的工作目录的状态，并帮助你查看所有未被 Git 跟踪、暂存或未暂存的文件。简而言之，Git 将向你显示当前树和 HEAD 指针的任何差异(请参阅 [Git 术语](https://www.toolsqa.com/git/first-commit-in-git/))。除了这个 Git 状态，还将向你显示存储库中的更改文件或新文件。我们将通过示例探索其中的每一个。我们将涵盖以下使用 Git Status 的案例

-   在新创建的文件上
-   在修改后的文件上
-   在已删除的文件上

### 工作树为 Clean 时的 Git 状态

在我们进行任何新更改之前，让我们看看我们正在使用的 Git 存储库的状态(第一个项目)。参考[Git 存储库](https://www.toolsqa.com/git/create-git-repository/)

1.打开Git Bash

1.  导航到存储库的目录(第一个项目)。
2.  键入以下命令 `git status`并按回车键执行命令。

![Git 中的 Git 状态命令](https://www.toolsqa.com/gallery/Git/1.Git%20Status%20Command%20in%20Git.png)

清晰可见，没有要提交的内容，工作树是干净的，即没有未跟踪的文件。

### 创建新文件时的 Git 状态

现在让我们做一些改变，看看会发生什么。

1.使用命令创建文件ABC.txt ：

```
touch ABC.txt
```

![触摸_ABC](https://www.toolsqa.com/gallery/Git/2.touch_ABC.png)

注意： touch 命令 是创建新的空文件的最简单方法。 

1.  按回车键创建文件。
2.  创建文件后，再次执行`git status`命令。

![git_status_untracked_file](https://www.toolsqa.com/gallery/Git/3.git_status_untracked_file.png)

消息非常清楚，没有要提交的内容，但存在未跟踪的文件。[Git 提交](https://www.toolsqa.com/git/first-commit-in-git/)教程中讨论的未跟踪文件是尚未添加到暂存区的文件。Git 中有两种类型的未跟踪文件。一个是我们上面看到的普通项目文件，另一个是存储库中的二进制文件，如.obj或 .exe。

上图中的 Git 也提供了使用git add跟踪文件的建议。这些建议将根据状态命令的情况提供。正如我们将在本教程中看到的那样，你将在不同的情况下获得不同的建议。

1.  将文件添加到暂存区。

![git_add_ABTXtxt](https://www.toolsqa.com/gallery/Git/4.git_add_ABCtxt.png)

添加文件后，再次查看`git status`命令，这次它说了什么。

![git_status_new_file](https://www.toolsqa.com/gallery/Git/5.git_status_new_file.png)

新文件：ABC.txt：这告诉你存在一个新文件(及其名称)并且也已添加到暂存中。

当你必须在盲目提交之前提交时，这会非常方便。查看git status可以帮助避免我们根本不想提交的更改。

\5. 提交这个文件。([Git 提交](https://www.toolsqa.com/git/first-commit-in-git/) 教程)

![git_commit_new_file](https://www.toolsqa.com/gallery/Git/6.git_commit_new_file.png)

提交文件成功后，你可以再次查看git状态，它会告诉你没有什么可以提交的。

现在我们将看到修改现有文件时会发生什么。

### 修改现有文件时的 Git 状态

在本节中，我们将看到在最近修改的文件上执行时的git status响应。

1.在文件ABC.txt中输入一句话

![echo_ABC_file](https://www.toolsqa.com/gallery/Git/7.echo_ABC_file.png)

注意： echo 命令用于在文本文件中添加文本。

1.  按回车键将这句话写入你的文件ABC.txt中。完成后，再次执行git status命令以查看它现在显示的内容。

![git_status_modified](https://www.toolsqa.com/gallery/Git/8.git_status_modified.png)

modified: ABC.txt：这表示文件ABC.txt 已被修改。它是红色的，因为我们还没有把它添加到暂存区，这就是底线说没有添加到提交的更改的原因。

1.  通过命令将文件添加到暂存区`git add`，然后`git status`再次键入以查看状态。

![git_status_modified_staged](https://www.toolsqa.com/gallery/Git/9.git_status_modified_staged.png)

修改：ABC.txt：此消息由红色变为绿色。此外，关于无须提交的信息也消失了。这意味着我们的文件在暂存区内，它正在被跟踪并且有更改要提交。提交文件以提交我们刚刚所做的更改。

### 删除文件时的 Git 状态

现在我们已经看到了带有新创建文件和修改文件的git status命令，让我们看看它如何响应已删除的文件。

1.  要删除文件 ( ABC.txt )，请键入以下命令并按enter键删除文件。

```
rm <file_name>
```

![rm_command](https://www.toolsqa.com/gallery/Git/10.rm_command.png)

注意： RM 代表删除。

1.  删除文件后，再次检查`git status`命令。

![git_status_deleted_file](https://www.toolsqa.com/gallery/Git/11.git_status_deleted_file.png)

deleted: ABC.txt : 消息说名为 ABC.txt 的文件已删除。

你可以像我们在上述部分中所做的那样提交更改。在执行任何提交之前练习git status命令非常重要。因为你可能不希望发生任何不需要的提交。此外，它还会为你提供有关发生的最新更改的信息。谈到提交更改，我们将继续下一个教程，在那里我们将学习如何使用良好的提交消息提交更改。
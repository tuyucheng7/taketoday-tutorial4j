在 Git 中来到这一步，我们遇到了如何通过 git bash[添加 git 暂存区](https://www.toolsqa.com/git/add-and-track-changes-to-staging/)中的文件，如何在这些文件中[写入文件中的内容，](https://www.toolsqa.com/git/first-commit-in-git/)[如何提交文件。](https://www.toolsqa.com/git/first-commit-in-git/)

这些操作对于基础知识来说已经足够了，但是，随着我们的进步，我们不需要的文件在存储库中越来越多。此外，到目前为止，还没有重命名文件的选项。在 Git 中创建文件后，其名称在整个项目中保持不变。围绕同一问题，我们将学习 Git 中的一些操作，其中包括 Git 中的Alter Files。以点的形式压缩它，我们将学习：

-   从 Git 存储库中删除文件。
-   重命名 Git 存储库中的文件。

## 如何在 Git 中更改文件？

读者应该清楚 Bash 和 Git Bash 是一个独立的实体。在本教程中，我们将使用 Bash 和 Git Bash 的相同命令来查看 Git 对两者的反应。了解和掌握 Git 非常重要。

### 如何从 Git 存储库中删除文件

文件不断地从 git 存储库中添加和删除。清理空间是一个很好的做法。可能有很多原因需要你清除内存，并且需要删除文件，你应该知道如何去做。在本节中，我们将探讨可以删除文件的两种情况：

-   从 Git Bash 中删除文件。
-   从 Git 存储库外部删除文件。

这两个都是 Git 中的正常操作，我们将看到 Git 如何对它们中的每一个做出反应。

#### 如何在 Git 存储库中使用 Git 删除文件

作为一种好的做法，在 Git 中开始任何新的操作之前，我们必须检查我们的存储库的状态。它应该是一个干净的存储库(暂存区中没有待提交的更改)。

首先，通过键入检查存储库的状态`git status`

![Git 状态命令](https://www.toolsqa.com/gallery/Git/1.Git%20Status%20Command.png)

是的，我们的树很干净，可以继续了。现在，要删除一个文件，我们必须有一个文件。通过touch命令新建文件toolsqa.txt，如下图：

```
touch toolsqa.txt
```

![创建 ToolsQA 文件](https://www.toolsqa.com/gallery/Git/2.Create%20ToolsQA%20File.png)

注意： touch 命令 是创建新的空文件的最简单方法。 

好了，我们的文件准备好了。将其添加到暂存区并提交更改。

![添加和提交](https://www.toolsqa.com/gallery/Git/3.Adding%20and%20Committing.png)

上面显示的三个步骤是：

1.  将更改添加到暂存区。
2.  检查 Git 存储库的状态。
3.  提交 Git 存储库中的更改。

好的，现在我们已经将文件添加到 Git 存储库中。现在让我们通过键入以下命令删除文件并按回车键：

```
git rm <file_name>
```

![在 Git 中更改文件 - 删除文件](https://www.toolsqa.com/gallery/Git/4.Alter%20Files%20in%20Git%20-%20Removing%20A%20File.jpg)

按回车键后，你的文件将被删除并`rm <file_name>`显示消息。

![在 Git 中更改文件 - 文件删除成功](https://www.toolsqa.com/gallery/Git/5.Alter%20Files%20in%20Git%20-%20File%20Removed%20Success.jpg)

现在，让我们检查一下git status和它给出的消息。

![在 Git 中更改文件 - Git Del From Git](https://www.toolsqa.com/gallery/Git/6.Alter%20Files%20in%20Git%20-%20Git%20Del%20From%20Git.png)

提交这些更改。现在我们将查看相同的场景，但是在没有 Git 帮助的情况下删除文件。

#### 如何从 Git 存储库中删除外部文件

从 Git 存储库外部删除文件意味着你没有借助 Git 的帮助来清理空间。你要么直接为此目的使用 Bash，要么使用某些 IDE。IDE 是一种通过 GUI 轻松重命名或删除文件的常用方法。正如我们所知，Git Bash 中缺少它。当我们查看已删除文件的 Git 状态行为时，我们在[Git 状态](https://www.toolsqa.com/git/git-status-command-in-git/)教程中使用了相同的命令。

请重新访问该帖子以了解场景以及 Git 在不借助 Git Bash 的情况下删除文件后的反应。

你应该注意到，当我们从 Git 外部删除文件时(不使用 git 命令)，该文件并未暂存。在这里，没有将文件添加到暂存区，文件已添加并准备好提交。这是一个重要的点，在面试时应该记下来。

### 重命名 Git 存储库中的文件

重命名文件也是对文件最常用的操作之一。由于许多因素，可以进行重命名。作为开发人员，你可能想要重命名你的开发人员同事创建的文件。也许你可以更改名称，即使你由于某种原因创建了文件。不管是什么原因，你都会经常遇到重命名。作为Git的重要组成部分，我们需要学习如何重命名Git仓库中的文件。正如我们在删除中了解到的，重命名也将通过 Git 和 Git 外部完成。

-   通过 Git 重命名文件。
-   在 Git 存储库外部重命名文件。

#### 如何通过 Git 重命名文件

通过 Git 重命名文件意味着你将访问 Git 命令进行操作。使用 Git bash 和一个简单的 bash 有时是相似的，只是在命令之前特别提到 Git。例如，rm toolsqa.txt 是一个 bash 命令，但可以通过 Git 执行与 git rm toolsqa.txt 相同的命令。

由于我们在 Git 存储库中没有任何文件，请按照上述步骤创建一个同名的新文件，即 toolsqa.txt。完成后，你必须检查 git 状态。确认它是一个干净的目录，就像我们上面所做的那样。完成所有操作后，键入以下命令并按回车键：

```
git mv <original_file_name> <new_file_name>
```

![通过 Git 重命名文件](https://www.toolsqa.com/gallery/Git/7.Renaming%20File%20through%20Git.jpg)

通过git status检查存储库的状态。

![重命名成功](https://www.toolsqa.com/gallery/Git/8.Renaming%20Successful.jpg)

如你所见，Git 发现文件已重命名。请记住这一步，因为当我们在 git 之外重命名文件时，它将发挥重要作用。提交这些更改。

#### 如何在 Git 之外重命名文件

在本节中，我们将了解在不通知 Git 的情况下重命名文件时 Git 的反应。因此，正如我上面提到的，使用不带“ git ”的相同命令。

在 Git Bash 中键入以下命令并按回车键：

```
mv <original_file_name> <new_file_name>
```

![在 Git 外重命名文件](https://www.toolsqa.com/gallery/Git/9.Renaming%20File%20Outside%20Git.jpg)

现在，理想情况下，如果我们通过 Git 重命名文件，Git 应该知道该文件已重命名。但是，让我们看看 Git 在遇到 Git 之外的文件重命名时的反应。

![在 Git 外重命名文件](https://www.toolsqa.com/gallery/Git/10.Renaming%20Files%20outside%20Git.jpg)

在这里注意 Git 反应非常重要。Git 删除了一个名为renaming.txt的文件并添加了一个新文件toolsqa.txt。他们两个目前都不在集结区。这与我们在上面看到的不同。在那里，Git 提到该文件已重命名。现在我们需要将这些更改添加到暂存区。

![重命名 Outisde Success](https://www.toolsqa.com/gallery/Git/11.Renaming%20Outisde%20Success.jpg)

现在[Git](https://git-scm.com/)已经识别出重命名并因此向你显示相同的内容。在 Git 存储库中提交这些更改。当你提交更改时，你会注意到一些事情。

![重命名文件提交](https://www.toolsqa.com/gallery/Git/12.Renamed%20Files%20Commit.jpg)

高亮的那行说，rename renaming.txt -> toolsqa.txt (100%)，按100%，Git表示之前的文件和新的文件完全一样，没有区别。它被称为置信水平。如果你在新文件中进行了任何更改，然后提交了更改，则它会显示不到 100%。

因此，在介绍了这些基础知识之后，我们完成了 Git 中的 Alter Files，我们将继续学习下一个关于 Git Ignore 的教程。
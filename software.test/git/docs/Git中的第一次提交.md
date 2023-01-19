[在 Git 中提交是我们在Git 简介 中](https://www.toolsqa.com/git/what-is-git/)讨论的三个阶段中的最后一个阶段。在提交之前，我们有一个暂存区，我们可以在其中添加更改。因此，在本教程中，我们将创建一个文件并尝试向其提交一些更改。为此，我们需要事先了解一些事情。

先决条件

-   如何创建存储库(参考教程)
-   通过 Git Bash 导航到另一个目录([参考教程](https://www.toolsqa.com/git/common-directory-commands-on-git-bash/))

在本教程中，我们将重点关注

-   如何在 Git 中创建文件
-   如何在暂存区添加文件
-   在 Git 中提交更改
-   在没有提交消息的情况下提交 Git 中的更改

## 如何使用 Git Bash 创建文件

要通过 Git Bash 创建文件，你必须首先创建一个存储库并导航到该目录作为当前工作目录。我希望我们正在Git 存储库教程中创建的第一个项目目录中工作。在你进入正确的存储库之后。键入以下命令

`touch <filename with extension>`![触摸命令](https://www.toolsqa.com/gallery/Git/1.touch_command.png)

按enter键创建名为harish.txt的文件。

可以查看目录可以看出文件已经创建成功(或者执行ls命令验证，[参考](https://www.toolsqa.com/git/common-directory-commands-on-git-bash/))。要在文件中写入内容，请在 Git Bash 中键入以下命令

```
echo "Your Message" > filename
```

![回声命令](https://www.toolsqa.com/gallery/Git/2.echo_command.png)

按回车键 ，消息将写入文件中。

现在我们有了文件并且通过写消息对它进行了更改，我们将尝试提交更改。在介绍性章节中，我们了解到我们需要将文件添加到暂存区，并且只有在这里我们才能提交对文件所做的更改。所以在这里，在提交之前，我们必须将文件添加到暂存区。

## 如何将文件添加到暂存区

要提交更改，首先，我们必须在暂存区内。让我们看看当我们直接提交并且没有事先将文件添加到暂存区时会发生什么。键入以下命令

`git commit`![git_untracked_commit](https://www.toolsqa.com/gallery/Git/3.git_untracked_commit.png)

此命令用于提交更改。但是看到 Git 说没有添加要提交的内容，但存在未跟踪的文件。这是因为我们在暂存区什么都没有。如果暂存区中有文件，我们只能提交更改。请注意上图中的以下行

未跟踪的文件：

哈里什.txt

这意味着我们有一些文件发生了变化，但还没有添加到暂存区。如果一个文件在暂存区并且它的所有更改都被定期跟踪，则称该文件被跟踪。因此，为了成功提交，我们需要将文件添加到暂存区。为此，请键入以下命令

`git add <filename>`![git_add_command](https://www.toolsqa.com/gallery/Git/4%20git_add_command.png)

现在我们的文件在暂存区。我们现在可以继续提交更改。

## 如何在 Git 中提交文件？

尽管提交是一个巨大的过程，并且不能在同一个教程中讨论所有这些过程。我们将在接下来的教程中慢慢进步。提交更改是 Git 中的一个简单命令。只需键入以下命令。

`git commit -m "This is my first commit"`![git_commit](https://www.toolsqa.com/gallery/Git/5.git_commit.png)

如你所见，更改已通过提交消息“ This is my first commit ”提交。

1 file changed 是我们刚刚添加到暂存区的文件。现在重要的是讨论我们上面写的 Git 中的消息部分。

你还可以通过在文本编辑器中编写提交消息来提交，我们在[为 git bash 设置记事本++时](https://www.toolsqa.com/git/set-up-notepad-for-git-bash/)学到了这一点。要编写提交消息，只需编写不带-m标志的 git commit

![git_commit_notepad](https://www.toolsqa.com/gallery/Git/6.git_commit_notepad.png)

按Enter。你的文本编辑器将打开。

![Notepad_for_commit_message](https://www.toolsqa.com/gallery/Git/7.Notepad_for_commit_message.png)

你可以在此处输入你的提交消息。

![提交记事本消息](https://www.toolsqa.com/gallery/Git/8.commit_notepad_message.png)

按Ctrl + S保存消息并关闭编辑器。关闭编辑器后，更改将随提交消息一起提交。

![git_commit_with_notepad](https://www.toolsqa.com/gallery/Git/9.git_commit_with_notepad.png)

## 在没有消息的情况下提交 Git

在上一节中，我们看到我们提交了消息“ This is my first commit ”并且更改已成功提交。此外，我们还研究了如何在记事本中使用提交消息进行提交。但是，在那些部分中，我们尝试在实际有一些更改要提交时提交。如果没有怎么办？对于第一次使用正确的提交命令和消息提交 Git 中的所有更改。完成后，键入以下命令

`git commit`![git_commit_without_message_without_changes](https://www.toolsqa.com/gallery/Git/10.git_commit_without_message_without_changes.png)

因此，如果你没有任何内容可提交，你将被告知在没有打开文本编辑器的情况下没有任何内容可提交。因此，如果你的文本编辑器在命令后打开，那么肯定有一些东西需要提交。

## 为什么我们需要提交信息？

Git 不建议在没有任何消息的情况下提交。Git 提交消息对于回顾和查看在特定提交期间所做的更改是必需的。如果每个人都只是提交而没有任何消息，那么没有人会知道开发人员做了什么更改。此外，一旦你看到历史记录，你将无法追踪这些更改。所以，Git 不推荐这样做。

在第一个场景中发生的事情是有一些东西要提交，而在 Git 中不允许仅使用“ git commit ”提交它。

但是在第二种情况下，我们实际上并没有提交任何东西，而且 git 知道因为你正在尝试提交并且你没有任何东西可以提交，所以它只会显示没有任何东西可以提交的消息。所以实际上你在第二种情况下没有提交任何东西，因此git 提交成功执行。当你在暂存区域中没有任何内容但进行了更改时也是如此，这些更改被称为上面讨论的未跟踪文件。

现在，Git 不建议在没有任何提交消息的情况下提交并不意味着我们不能在没有消息的情况下提交。允许但不推荐。要在没有任何提交消息的情况下在 Git 中提交，请按照这些简单的步骤进行操作，并对上一个命令稍作更改。

1.  打开你的Git Bash
2.  对我们上面创建的文件 ( harish.txt )进行一些更改

![回声无消息](https://www.toolsqa.com/gallery/Git/11.Echo_Without_Message.png)

3.将文件添加到暂存区

![git_add_without_message](https://www.toolsqa.com/gallery/Git/12.git_add_without_message.webp)

1.  键入以下命令

`git commit -a --allow-empty-message -m ' '`![git_commit_without_commit_message_success](https://www.toolsqa.com/gallery/Git/13.git_commit_without_commit_message_success.png)

1.  按回车就完成了。

![git_commit_without_message_success](https://www.toolsqa.com/gallery/Git/14.git_commit_without_message_success.png)

这样我们就可以在没有任何提交消息的情况下提交 Git 中的更改。但不推荐。我们将继续下一个教程，继续练习更改并在 Git 中提交。
到目前为止，在本课程中，我们已经在我们的系统上[安装了 Git](https://toolsqa.com/git/install-git-on-windows/)，并且我们已经知道，我们将在本完整课程中使用Git Bash 。我们已经理解了Git 中使用的概念和术语，现在是时候开始使用Git Bash并做一些实际的事情了。

在本教程中，我们将重点关注

-   什么是 Git 帮助？
-   如何列出所有的 Git 命令
-   如何获得特定命令的帮助？
-   如何从 Git Bash 打开 Git 官方文档帮助？

## 什么是 Git 帮助？

与任何其他软件的帮助选项一样，Git 帮助也是 Git 中的一个选项，可为像你这样的新手提供帮助。Git 没有选项字段或命令。只需键入命令，即可从Git Bash访问Git 帮助。`git help`

![git_help_command](https://www.toolsqa.com/gallery/Git/1.git_help_command.png)

按Enter 键执行帮助命令。

![git_帮助](https://www.toolsqa.com/gallery/Git/2.git_help.png)

此命令将显示有关 Git 的所有信息。这些是让任何初学者入门的简要信息。上面显示的这些命令是 Git 中最常用的命令。这些只是一些简短的介绍，我们将在下一节中看到所有可用的命令。

### 如何列出所有的 Git 命令？

如果我必须说的话，上一节中使用的命令git help是更大的命令。根据我们的说法，这可用于获得更具体的结果和更具体的命令。

如果我们需要查看 Git 中所有可用的命令，我们需要使用以下命令。

`git help -a` 或 `git help - -a`

![git_help_--a_command](https://www.toolsqa.com/gallery/Git/3.git_help_--a_command.png)

注意：一个是单'-'，另一个是双但'- -'之间没有空格

按回车键执行命令

![git_help_--a](https://www.toolsqa.com/gallery/Git/4.git_help_--a.png)

这将向你显示 Git 中所有可用的命令。如果在命令后滚动到末尾，你将看到三行信息。无论你执行什么命令，这都是不变的。

![命令_信息_滚动](https://www.toolsqa.com/gallery/Git/5.Command_Info_Scroll.png)

注意：相同的命令ie`git help -a`类似于`git help --all`命令。你也可以试试看。结果将是相似的。

还值得注意的是，如果你输入了错误的命令，git bash 会建议你正确的命令。例如，让我们尝试`git help -all`在 git bash 中执行命令，看看会发生什么。

![git_wrong_command_execution](https://www.toolsqa.com/gallery/Git/6.git_wrong_command_execution.png)

它告诉我们，也许我们希望命令带有两个破折号。现在让我们进入下一节并进行更多探索。

### 如何访问特定命令的帮助？

很容易获得有关某个特定命令的帮助。在上一节中，我们列出了 git 中所有可用的命令，在某些情况下，你要么不知道特定命令但想了解它，要么知道命令名称并想了解语法。不管是哪种情况，以下步骤都会告诉你有关特定命令的信息。

1.  列出所有使用的命令`git help -a`
2.  从选项中选择任何命令，这里我选择fetch。
3.  使用你选择的命令键入以下命令

`git help <command name>`

![git_help_fetcha](https://www.toolsqa.com/gallery/Git/7.git_help_fetcha.png)

1.  按回车键，“获取”手册将在你的浏览器中打开。

![git_fetch_manual_page](https://www.toolsqa.com/gallery/Git/8.git_fetch_manual_page.png)

此页面将包含有关你键入的命令的所有信息。到目前为止的信息足以让任何初学者开始尝试刷一些命令。在下一节中，我们将向你展示如何在 git bash 中获取官方文档帮助。

### 如何从 Git Bash 访问 Git 指南？

在上一节中，我们展示了当你滚动到(你执行的命令的)结果的末尾时，会出现一个选项。

![命令_信息_滚动](https://www.toolsqa.com/gallery/Git/9.Command_Info_Scroll.png)

这个选项是`git help -g`。如上所示，此命令将向你展示一些概念指南。这些指南将帮助你开始使用 git 的旅程。让我们探索这个命令。

输入指令`git help -g`

![git_help_-g](https://www.toolsqa.com/gallery/Git/10.git_help_-g.png)

按回车键执行命令

![git_help_g_result](https://www.toolsqa.com/gallery/Git/11.git_help_g_result.png)

这将向你展示可用的常用 Git 指南。你还可以在指南名称前面看到摘要。例如，名为“attributes”的指南在 git per path 中定义了属性。指南“日常”将有常用的命令等。

要打开任何特定指南，请在 git bash 中键入以下内容

`git help <guide_name>`

![git_help_attributes](https://www.toolsqa.com/gallery/Git/12.git_help_attributes.png)

按回车键执行命令。这将在你的浏览器中打开关于属性的手册，就像上面打开的关于 fetch 一样。

![git_attribute_manual_page](https://www.toolsqa.com/gallery/Git/13.git_attribute_manual_page.png)

至此，本教程到此结束。我希望你现在有时间花时间在 git bash 上。通过点击和尝试不断练习所有这些命令。我们现在将进入下一个教程。
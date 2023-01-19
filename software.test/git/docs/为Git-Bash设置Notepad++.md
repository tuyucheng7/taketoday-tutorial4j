在之前的教程中，我们已经[将默认凭据设置为 Git 配置](https://www.toolsqa.com/git/set-up-default-credentials-for-git-config/)。现在我们准备在本教程中设置更多内容。如前所述，这些只是一次性设置。我们执行的命令会更改全局配置文件，并且设置会永久保存在配置文件中。所以我们只需要设置这些东西一次(以防你不会进一步改变它)。在本教程中，我们将学习

-   为什么我们需要文本编辑器
-   Git Bash 自带的文本编辑器
-   在 Windows 中为 Git Bash 设置 Notepad++

## 为什么我们需要文本编辑器？

这个问题的答案非常相似。你必须以前在终端上做过任何事情。你一定知道终端(命令提示符、Git Bash 等)只接受预定义的命令并执行它们。每个终端都有自己的协议，并且会相应地运行。例如，按 enter 自动表示你正在执行书面命令。这样就很难在命令提示符或 Git Bash等终端中编写特定消息。出于同样的目的，我们需要一个文本编辑器来解决我们的问题以及更多功能。为此，你有很多选择，包括一些预装的编辑器，建议在 Windows 机器上使用Notepad++ 。

## Git Bash 的默认文本编辑器

为了方便开发人员， Git Bash中已经包含了两个编辑器。这些选项也出现在我们同意并[安装](https://www.toolsqa.com/git/install-git-on-windows/)这些编辑器的安装过程中。

你应该注意，选择你的编辑器是你个人的选择，你应该使用你感到舒服并且已经使用了一段时间的编辑器。在本课程中，我们将使用Notepad++作为 Git 的文本编辑器。回来，让我们看看两个已经安装好的 Git 编辑器。

-   为什么
-   纳米

### 为什么

Vim 文本编辑器是Unix系统默认的文本编辑器，预装了Git Bash。要打开 Vim 文本编辑器，只需打开Git Bash并在其中输入“vim”。按回车键打开编辑器

![Vi_Editor](https://www.toolsqa.com/gallery/Git/1.Vi_Editor.png)

Vim 在开发者社区中被认为是一个坚硬但功能强大的文本编辑器。Vim非常古老，所有花哨的 GUI、IDE 的引入导致人们相信 Vim 很难学。对于初学者，不推荐使用Vim，并且经常被在该领域工作多年的资深程序员和开发人员使用。vim 的难度可以通过打开 vim 编辑器然后尝试关闭它而不看屏幕上的说明来演示。是的，像关闭编辑器这样简单的事情对初学者来说很难，但如果你已经掌握了 Git 或其他编辑器， Vim绝对是一个选择。Vim 也被认为是具有基本命令的非常稳定的文本编辑器。

### 纳米

GNU Nano是另一个预装了Git Bash 的 Unix 系统文本编辑器。如果你在 Windows 的命令提示符下工作，那么你可能无法将Nano作为文本编辑器打开。Nano可以在Git Bash 中通过输入“nano”作为命令打开。

![纳米编辑器](https://www.toolsqa.com/gallery/Git/2.nano_editor.png)

对于初学者来说， Nano比Vim更容易学习和尝试，因为除了命令行感觉和命令外，它还有相当比例的 GUI 部分。所以基本上如果你是一个Vim编辑器爱好者并且目前没有 Vim，你也可以使用 Nano。但文本编辑器的选择最终归结为个人选择或项目需求。所以如果你还没有，你甚至可以研究它来选择一个。

## 如何在 Windows 中为 Git Bash 设置 Notepad++

在本教程中，我们将设置Notepad++作为 Git 的文本编辑器。Notepad++是一个非常简单易用的文本编辑器，社区中有很多开发人员在使用它。与其他文本编辑器相比，它具有许多巨大的优势，例如对插件的巨大支持。Notepad++有大量可用的插件，可以使你的任务变得简单。Notepad++支持多种编程语言编写，启动速度非常快。它可以非常快速地打开(可以说是许多现代编辑器中最快的)当涉及到在大中型项目中很常见的大代码文件时。如果你只想在大代码之间插入一行，这也会有所帮助。但正如我上面所说，文本编辑器的选择完全取决于程序员。

### 将 Notepad++ 设置为 Git 文本编辑器的步骤

先决条件

Notepad++ 应该安装在你的系统上。你可以从[这里](https://notepad-plus-plus.org/download/v7.6.3.html)下载相同的。记住你安装记事本的目录 ( notepad++.exe )。

![notepad_exe_directory](https://www.toolsqa.com/gallery/Git/3.notepad_exe_directory.png)

注意：这是安装 Notepad++ 的默认目录。如果你在安装时选择了另一个目录，你可以记住你的目录。

有了这个，让我们开始按照以下步骤将Notepad++设置为我们的编辑器：

1.  在你的系统上打开Git Bash并在Git Bash中键入以下命令

`git config --global core.editor <directory address>`

![在 Windows 中为 Git Bash 设置 Notepad++](https://www.toolsqa.com/gallery/Git/4.Set%20Up%20Notepad++%20for%20Git%20Bash%20in%20Windows.png)

注意： Notepad++.exe 我们放在目录名的末尾。这是因为它是我们在 Git 中需要编辑器时希望打开的 exe 文件。该文件对应软件的Notepad++启动。

1.  按Enter 键，如果没有出现错误，则你已成功将Notepad++设置为 Git Bash 中的文本编辑器。

你也可以通过输入命令来检查它 `git config --global core.editor`

![confirm_text_editor_change](https://www.toolsqa.com/gallery/Git/5.confirm_text_editor_change.png)

那好吧。完成本教程后，我们都准备好创建我们的第一个 git 存储库并利用我们到目前为止所做的所有事情。
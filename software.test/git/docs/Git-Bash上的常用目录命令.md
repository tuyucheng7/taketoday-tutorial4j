计算中的存储库被称为存储和管理数据的中心位置。因此，Git 存储库意味着将存储和管理所有项目文件的中心位置。Git 存储库是系统中的一个文件夹，所有项目文件都位于其中。它允许你保存代码的版本，以便你可以在需要时访问它们。在你的系统中，Git 存储库与许多其他文件夹一样是一个简单的文件夹。

从现在开始我们非常接近开始 Git 活动，但我们必须了解Git Bash 上的一些 Common Directory 命令 以使过程简单。这些包括

-   使用 Git Bash 更改目录
-   使用 Git Bash 创建目录
-   查看Git Bash中的所有目录

## 如何通过 Git Bash 更改目录？

更改目录很重要，因为在使用 Git Bash 时，你总是在不同的目录之间来回切换。目录是文件夹的技术术语。你可以通过两种方式更改目录：

-   直接通过 Git Bash 使用命令
-   通过在所需文件夹中打开 Git Bash

### 通过 Git Bash 中的命令浏览到所需的目录

`cd `你可以在命令的帮助下更改 Git Bash 中的目录。出于相同的目的，`cd`command 通常在系统的 shell ( cmd ) 中使用。在这里，我将目录通过`cd`命令更改为ToolsQA

1.  打开你的 Git Bash。
2.  输入以下命令 `cd <path of the directory>`并按enter键。看到目录已经改变了。

![Git Bash 上的常用目录命令](https://www.toolsqa.com/gallery/Git/1%20Common%20Directory%20commands%20on%20Git%20Bash.png)

注意： ToolsQA是我系统E 盘中的一个文件夹。

### 直接在文件夹中打开Git Bash

通过在同一文件夹中打开目录来更改目录非常简单。为此，以浏览系统的正常方式转到要更改目录的目录。

在此之后，右键单击目录中的任意位置 => Open Git Bash here。

![打开_git_bash_here](https://www.toolsqa.com/gallery/Git/2%20open_git_bash_here.png)

在我们学习了如何更改工作目录之后，我们可以在我们需要工作的任何项目文件夹中创建一个存储库。为了创建存储库，我们将首先创建一个文件夹，我们将在其中工作

## 如何使用 Git Bash 创建新目录？

使用 Git Bash 创建目录只是一个简单的命令，它也用于 Linux 系统。虽然你可以使用创建新文件夹的传统方法来创建目录，但尽可能多地使用 Git Bash 来完成工作。让我们看看如何使用 Git Bash 创建目录。

1.  打开Git 狂欢。
2.  导航到要在其中创建文件夹的目录。
3.  键入以下命令 `mkdir <folder name>` 并按回车键创建目录。

![mkdir_command](https://www.toolsqa.com/gallery/Git/3%20mkdir_command.png)

注意：请记住，当你没有在引号中提及多于一个单词的目录名称时，它将创建两个目录，即 First 和 Project。我们将在下一节中看到这一点，然后删除这些文件夹并创建一个名为 First Project 的目录。

## 如何查看Git Bash中的所有目录？

现在我们已经创建了文件夹，我们还必须知道如何查看工作目录中的所有目录/文件夹。

1.  导航到要查看目录的目录 ( ToolsQA )
2.  键入以下命令 `ls` 并按Enter 键。你将看到所有目录。

![ls_command](https://www.toolsqa.com/gallery/Git/4%20ls_command.png)

注意：值得注意的是，`ls`不会显示隐藏的文件夹。你需要使用`ls -a`相同的。

正如我们在上一节中讨论的那样，创建了两个目录。我们只需要一个名为First Project的项目。所以对于这个使用` mkdir "First Project"`命令。

`rmdir`你可以使用带有目录名称的命令来删除目录。

![rmdir_command](https://www.toolsqa.com/gallery/Git/5%20rmdir_command.jpg)

注意：`mk`代表Make，`rm`代表Remove 。

现在我们都准备好在我们的项目目录中初始化 Git。如上所述，我们需要了解用于创建存储库的Git Init命令。`Git Init`我们将在下一个教程中简要介绍该命令。
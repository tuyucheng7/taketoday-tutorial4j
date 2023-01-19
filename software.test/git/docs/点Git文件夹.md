在上一个教程中我们学习了如何[创建 Git 存储库](https://www.toolsqa.com/git/create-git-repository/)，我们谈到了在执行命令时在存储库中创建的.git文件夹。`git init`如果你看不到该文件夹，可能是因为你的隐藏文件夹不可见(在这种情况下使用`ls -a`)。在开始本教程之前，我想提一个小提示，本教程属于 Git 的高级功能。你可能在 Git 之旅中根本不会使用它，也不需要它。这只是为了你的学习目的。没有必要学习它，或者跳过它也没有坏处。回来，让我们通过Git Bash看看它。

打开 Git Bash，导航到我们在上一教程中创建的存储库，键入：

`ls -a`

![ls_a_command](https://www.toolsqa.com/gallery/Git/1ls_a_command.png)

你可以看到我们在First Project存储库中有两个文件夹。其中一个是.git文件夹，另一个是我们在上一教程中克隆的文件夹。一个 。需要git文件夹来记录每个提交历史记录以及远程存储库、版本控制、提交等所需的所有其他信息。这些东西保存在具有不同含义的不同文件夹中。创建文件夹后，打开它并查看文件夹的内容。它们可能看起来像这样：

-   挂钩
-   信息
-   对象
-   配置
-   描述
-   头

稍后，在讨论了这些文件夹中的每一个之后，我们还将了解当我们在 git 中删除 .git 文件夹时会发生什么。

## Dot Git 文件夹 (.git) 中的 Hooks 文件夹

这个文件夹里面有几个脚本文件。这些脚本文件称为 Git Hook 脚本。Git 钩子是在事件之前或之后执行的脚本。这些事件可以是任何 Git 事件，包括常见的 Git 事件，如提交、推送或接收。这些脚本提高了开发人员的工作效率。

预提交脚本是在执行提交事件之前执行的脚本。这可能包括检查拼写错误等。类似地，提交完成后执行提交后脚本。你可以进入 hooks 文件夹以查看其中的不同脚本以及我们刚刚谈到的 pre-commit 脚本。你可以更改这些脚本或根据你更新它们。

![Git_Hooks](https://www.toolsqa.com/gallery/Git/2%20Git_Hooks.jpeg)

注意：在 Git 上的日常操作中不需要此编辑脚本的功能。但是，一旦你逐步使用该软件并拥有多个存储库，你可能需要它的帮助。你可能需要为相同的.

在上图中，可以通过流程图看出commit前执行pre-commit，commit后执行post-commit，等等。

## Dot Git 文件夹 (.git) 中的信息文件夹

信息文件夹包含其中的排除文件。顾名思义，exclude file 用于排除代码中一些你不希望 Git 读取或执行的特定模式。请记住，此文件是你本地的和个人的，不会在克隆你的项目的开发人员之间共享。如果项目中的所有开发人员都应该忽略某些内容，那么它就会出现在.gitignore下，我们将在后面的教程中讨论。

## Dot Git 文件夹 (.git) 中的对象文件夹

Objects文件夹是.git 目录中一个非常重要的文件夹。在 Git 中，所有内容都作为哈希值保存在对象文件夹中。我所说的一切是指你创建的每次提交、每棵树或每个文件都保存在该目录中。对于每个对象，都有一个与其相关联的散列值，Git 通过该散列值知道什么是什么。相应地创建文件夹。例如，你可以看到，一旦你初始化 Git 存储库，对象文件夹就为空。现在，假设我创建了一个名为harish.txt的新文件。

![touch_harish](https://www.toolsqa.com/gallery/Git/3%20touch_harish.png)

现在，查看对象文件夹。它现在有 2 个文件夹info和pack但它们是空的。

![对象_文件夹_摘要](https://www.toolsqa.com/gallery/Git/4%20Objects_Folder_Summary.png)

让我们在文件中写入一些内容并提交并查看更改。

### 对象文件夹如何保存哈希值？

1.  键入此命令：

`echo "this is my first file" > harish.txt`

![回声哈里什](https://www.toolsqa.com/gallery/Git/5%20Echo_Harish.png)

注意： 这样我们只能通过 Git Bash 向文件写入任何内容。你还可以使用我们将在后面的教程中学习的文本编辑器。

1.  使用此命令将你的文件添加到暂存区， `git add harish.txt`然后按enter键。

![git_add_harish.txt](https://www.toolsqa.com/gallery/Git/6%20git_add_harish.png)

1.  通过此命令提交更改： `git commit -m "Committing my Changes"`

![git_commit_harish](https://www.toolsqa.com/gallery/Git/7%20git_commit_harish.png)

注意：暂时不要关注添加到暂存区等任何事情。我们将在后面的教程中讨论所有这些并清除概念。

现在查看对象文件夹。现在已经创建了新文件夹。

![对象文件夹](https://www.toolsqa.com/gallery/Git/8%20objects_folder.png)

这些包含你刚刚执行的事件的哈希值。Git 通过将其转换为哈希值来跟踪和识别所有内容。文件夹根据里面保存的哈希值命名。通俗地说，散列是一种将数据转换为散列值(字母和数字的随机组合)的流行方法(数据结构)) 只能由可信来源解码。由于其性质，如今散列法最常用于安全措施中。但在 Git 中，它主要不是用作安全功能。Git 中的散列用于能够在不发生冲突的情况下信任和创建稳定的数据，这意味着可以保存两个相同的文件，因为它们将具有不同的散列值。哈希作为一种媒介，让你的数据以一种可以在未来转换并用于任何其他技术的形式出现。由于哈希表现平庸，我们可以随时创建将输入作为哈希值而不是源代码文件的技术。这样，由于 Git 的这一特性，你今天保存的代码也可以在多年后查看。转到文件夹并查看已创建的哈希值。

![哈希值对象文件夹](https://www.toolsqa.com/gallery/Git/9%20hash_value_objects_folder.png)

上图是对象文件夹中的 4b 文件夹。

## 点 Git 文件夹 (.git) 中的配置文件夹

配置文件包含你的配置，在[设置 Git 中的凭据](https://www.toolsqa.com/git/set-up-default-credentials-for-git-config/)时已经详细讨论过。你为项目设置的所有配置都永久保存在此文件中，如用户名、电子邮件等。你可以在设置后修改它，但不需要一次又一次地进行。编辑配置设置后，它们将永久保存。你可以通过键入以下命令来查看它们： `vi ~/.gitconfig`

![git_config_directory_value](https://www.toolsqa.com/gallery/Git/10%20git_config_directory_value.png)

### Dot Git 文件夹中的描述文件

描述包含有关存储库的数据，只能在GitWeb上看到。该文件不供程序员使用。它只是为了查看 GitWeb 上的存储库。[你可以在此处](https://git-scm.com/book/en/v2/Git-on-the-Server-GitWeb)了解 GitWeb 。

## Dot Git 文件夹 (.git) 中的 HEAD 文件

头文件包含对我们当前正在处理的分支的引用。它是对分支的符号引用，而不是正常引用。不同之处在于普通引用包含我们在上面的Objects文件夹中看到的哈希值它直接指代一些东西，就像编程中的指针一样。 符号引用是对另一个普通引用的引用. 这意味着符号引用将引用普通引用，并且据了解，普通引用将引用实际值。它是一种两步参考。所以在这种情况下，Head 指的是其他地方，它包含我们正在处理的分支的哈希值。请注意，头部总是指出最后签出的修订版。如果你最近签出过一个分支，那么它将指向该分支。如果该修订版是提交，则头部将指向提交。

![head_git_文件夹](https://www.toolsqa.com/gallery/Git/11%20head_git_folder.png)

这里的PH指的是前面的head。C2 或提交 2 是最后签出的提交，因此即使在那之后有另一个提交(C3)，当前头(CH)仍然指向提交 2。

要查看头文件的内容，你只需键入命令： `cat .git/HEAD`

命令后按回车键。

![git_cat_HEAD_value](https://www.toolsqa.com/gallery/Git/12%20git_cat_HEAD_value.png)

所以到现在为止，很清楚 .git 文件夹对我们有多重要，并且是我们在存储库中工作所必需的。但是如果我手动删除这个文件夹会发生什么？我们会看到的。

## 删除点 Git 文件夹 (.git)

通过了解上一节中 .git 目录中的子目录，很明显我们在项目/存储库中所做的一切都保存在 .git 目录中。删除此文件夹将删除我们的整个历史记录。如果你还没有克隆它，所有版本和历史记录都会从 Git 中删除。尽管你的源文件将保持原样。要删除 .git 文件夹，只需转到Git Bash并通过键入以下命令删除该文件夹： `rm -rf .git`

![删除_dot_git_folder](https://www.toolsqa.com/gallery/Git/13%20removing_dot_git_folder.png)

按enter后，列出所有目录以查看 .git 是否被删除。

![list_directories_without_dot_git_folder](https://www.toolsqa.com/gallery/Git/14%20list_directories_without_dot_git_folder.png)

正如你所注意到的，.git 不在列表中。这样，我们就删除了 .git 文件夹和保存在其中的全部工作。通过查看文件夹的大小，你可以很容易地猜到所有工作都保存在.git中。它有时会超过 10GB 的空间。

让我们看看自从我们删除了.git文件夹后，Git 现在如何执行与存储库相关的命令。

尝试提交一个文件(不管你有没有提交的文件)：

`git commit`

![committing_without_dot_git_folder](https://www.toolsqa.com/gallery/Git/15%20committing_without_dot_git_folder.png)

Git 抛出一个错误，指出你正在其中执行命令的目录不是一个存储库。

这意味着存储库不可用。你还可以看到它在括号中提到了“或任何父目录”。这是因为当 Git 在当前目录中找不到 .git 文件夹时，它会在父目录中搜索相同的文件夹。由于我们所有的文件都特定于这个特定的存储库，因此它不会在父目录中找到任何与之相关的内容。

所以，如果你还没有克隆你的项目，最好不要删除 .git 文件夹。虽然，你可以获取保存在父目录中的一些东西。我们现在将继续下一个教程。
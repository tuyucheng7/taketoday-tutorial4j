别名在英语中的意思是某人(或某物)的原始身份与他们(或那个)所知道的不同。例如，说唱歌手艺术家的别名是 Eminem，但他的真名是 Marshall。从技术上讲，如果我们谈论 python，可以原样导入 matplotlib.pyplot 等模块。但是，到处使用这个大牌并不方便。所以理想情况下，我们通过输入命令 import matplotlib.pyplot as py 创建一个别名，以指示无论我们在哪里使用 py，它都意味着 matplotlib.pyplot。在这里， py 成为别名。或多或少类似，我们有“ Git Alias ”，它可以压缩大命令(理想情况下) 或任何重要的命令。本教程是关于同一主题的一个小分享，将跟进索引如下：

-   什么是 Git 别名？
-   如何在 Git 中创建别名？

## 什么是 Git 别名？

别名是与 Git 中的快捷方式相关联的术语。它将较长的命令序列压缩为较短的序列。将别名应用于经常使用的较长命令总是更好，因为它有助于提高效率。更长的命令是指更长的命令模式，其中包括不同的选项和标志，但命令本身没有必要更长。此外，用户可以为他们想要的任何命令创建一个别名，并考虑他们是否会非常频繁地使用该命令。值得注意的是，Git 中不存在“ alias ”这样的命令。换句话说，别名只是用于从较长的命令序列中创建较短命令的术语。那么，既然没有别名的概念，我们在哪里做这一切呢？所有这些都存储在哪里？

别名出现在 Git 配置文件中。要了解有关 git 配置文件以及如何在 Git Bash 中查看和编辑它的更多信息，你可以访问[Git-Config](https://www.toolsqa.com/git/set-up-default-credentials-for-git-config/)教程。因此，一旦你创建了别名，就可以将其保存为存储库的本地副本或全局副本。用户不需要只使用别名；用户也可以自由使用原始命令。没有限制。让我们看看如何创建一个别名以及一些使用非常频繁的标志。

### 如何在 Git 中创建别名？

1.  在工作目录中打开Git Bash 。键入以下代码并执行命令：

git config --global alias.hist "log --pretty=format:'%h %ad | %s%d [%an]' --graph --date=short"

![git hist 完整命令](https://www.toolsqa.com/gallery/Git/1.git%20hist%20full%20command.png)

上面的命令可以分为三个部分。

-   git config --global = Config --Global 告诉 git 在配置文件中包含别名，或者我们正在尝试编辑配置文件。global 关键字全局存储别名。
-   alias.hist = alias 是告诉 git 后面的单词将作为别名的关键字。“ hist ”是我们将在这里使用的词。
-   " log --pretty=format: '%h %ad | %s%d [%an]' --graph --date=short " =这是我们将为其创建别名的实际命令。因此，在上面的屏幕截图中，我们试图为“ log --pretty=format: '%h %ad | %s%d [%an]' --graph --date=short ”创建一个更短的命令。

在接下来的步骤中，我们将尝试在 Git bash 中使用我们新创建的别名。

1.  现在键入git hist 以查看别名是否有效：

![输入 Git Hist 以检查 Git Alias 是否有效](https://www.toolsqa.com/gallery/Git/2.Type%20Git%20Hist%20to%20check%20whether%20Git%20Alias%20works.webp)

1.  按回车键执行命令。

![git hist 完整命令](https://www.toolsqa.com/gallery/Git/3.git%20hist%20full%20command.png)

这样，我们就可以为命令创建别名并随时使用它们。你现在可以练习创建自己的。

## 常见问题

### Git 别名存储在哪里？

它们在 Git 的配置文件中。用户可以选择将别名保存为局部或全局变量。本地别名将仅特定于存储库。

### 有没有最常见的标准 Git 别名？

不，没有这样的别名，因为 Git 中没有别名这样的术语。许多平台使用 hist 作为 git log 命令的通用别名，用于查看图形并对其进行一些美化。但是，没有这样的标准命令。
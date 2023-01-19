正如我们在之前的一个教程中了解到的那样，[GitHub 存储库](https://www.toolsqa.com/git/github/)是一个云端存储库。这意味着，本地存储库上可用的任何数据都可以上传到GitHub 上的远程存储库。我们在 GitHub 上创建了一个帐户，现在是时候将本地数据推送到 GitHub 上的远程位置了。

为了阐明这篇文章的主题，这篇文章将引导你完成：

-   什么是 Git 远程命令？
-   如何连接本地仓库和远程仓库？
-   如何获取远程存储库详细信息？

## 如何将现有的 Git 本地存储库链接到远程存储库？

总是有几种方法可以将本地存储库链接到远程存储库。

-   从远程：当存储库在 GitHub 上可用时，这会在 Git Fork 命令的帮助下发生。用户在本地对同一个存储库进行 git fork 或 git clone。(注意：我们将在下一章介绍)
-   来自本地：当存储库首次在本地创建时，这会在 Git Remote 命令的帮助下发生。然后用户将它连接到远程存储库。

下面我们将介绍第二种方法，其中用户已经拥有一个本地存储库和一个空的远程存储库，但是这些存储库没有相互链接。

由于我们在上一个教程中[创建了一个 GitHub 存储库](https://www.toolsqa.com/git/create-github-repository/)，在本教程中我们将尝试将其与本地存储库连接起来。这样数据就可以从本地推送到远程。

### Git 中的 Git 远程命令

git remote 命令用于建立远程连接，例如将 Git 本地存储库与 GitHub 远程存储库连接。

现在， git remote看起来像是本地和远程存储库之间的实时数据交换(你在本地所做的一切)，但事实并非如此。Git remote 只是本地和 GitHub 存储库之间的连接。由于 GitHub 存储库包含无法记住每个存储库的扭曲 URL，因此我们为这些链接提供名称以供记忆。我们同样使用 git remote。通过 git remote，我们为仓库提供了一个名字，通过它我们可以引用 GitHub 仓库。

换句话说，git remote可以被视为对 GitHub 存储库的引用，它不提供对你在本地所做的任何实时访问，即未经你的许可，你在本地所做的任何事情都不会反映在你的 GitHub 存储库中。

Git remote 可用于连接到你自己的存储库(正如我们将在下一节中所做的那样)或连接到其他人的存储库。现在，让我们看看如何将现有的本地 Git 存储库链接到远程 GitHub 存储库。

回到我们在上面留下的同一个 GitHub 页面，请注意我们有一个名为 ... or push an existing repository from the command line 的部分。

![本地存储库远程存储库 - 推送现有存储库](https://www.toolsqa.com/gallery/Git/1.Local%20Repository%20Remote%20repository%20-%20Push%20an%20existing%20repository.png)

### 将本地存储库与 GitHub 远程存储库连接

该图像部分中的第一个命令将用于将存储库链接到 GitHub 存储库。

1.  打开Git Bash并导航到需要链接的存储库。([了解如何导航到存储库](https://www.toolsqa.com/git/common-directory-commands-on-git-bash/))

![First_Project_Repo](https://www.toolsqa.com/gallery/Git/2.First_Project_Repo.png)

上图显示 Git Bash 已经在First Project仓库中打开。

1.  使用命令检查存储库是否干净并且没有任何突出的内容`git status`。

![Clean_Reporitory_Check](https://www.toolsqa.com/gallery/Git/3.Clean_Reporitory_Check.png)

3、执行`git remote`以下命令：

![Git 远程命令](https://www.toolsqa.com/gallery/Git/4.Git%20Remote%20command.png)

由于没有链接存储库，因此没有从 Git 收到任何输出。

1.  现在，借助上面给出的 URL，我们将链接存储库。要链接存储库，请执行以下命令并按 enter：

```
git remote add origin https://github.com/harishrajora805/myFirstRepo.git
```

![git_remote_add_origin](https://www.toolsqa.com/gallery/Git/5.git_remote_add_origin.png)

注意：以上命令遵循通用语法`git remote add <name> <repository url>`

-   添加：向存储库添加新的 URL。
-   名称：提供一个名称，你将使用该名称代替存储库的 URL。
-   URL：存储库的 URL。

完成后，本地存储库将链接到 GitHub 存储库。不过，有几件事应该牢记在心。

注意：我在上图中使用的 URL 是我的存储库的 URL。请使用你自己的 URL 进行链接。此外，名称 origin 是开发人员推荐或默认使用的名称，用于 GitHub 上的第一个或主要存储库。它不受限制，你可以使用自己的名字。

### 如何检查本地存储库是否与远程存储库连接？

要检查我们是否链接了我们的存储库，请`git remote`再次执行命令

![Local Repository Remote repository - Git Remote命令检查仓库是否与Origin连接](https://www.toolsqa.com/gallery/Git/6.Local%20Repository%20Remote%20repository%20-%20Git%20Remote%20command%20to%20check%20if%20repository%20is%20connected%20with%20Origin.png)

如图所示，原始存储库(GitHub myFirstRepo 存储库的别名)可用。继续并使用`git remote -v`命令查看相同的结果以及所示的 URL。

![Local Repository Remote repository - Git Remote命令显示链接远程仓库](https://www.toolsqa.com/gallery/Git/7.Local%20Repository%20Remote%20repository%20-%20Git%20Remote%20command%20to%20show%20link%20remote%20repository.png)

当我们将一些更改推送到 GitHub 存储库时，你收到的输出的含义将在本教程中讨论。在那之前，继续练习。
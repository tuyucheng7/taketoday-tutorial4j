在本教程中，我们将学习如何创建 Git 存储库。但是，在你继续学习本教程之前，你已经阅读了之前关于GIT的一组教程非常重要：

-   [Git 基础知识](https://toolsqa.com/git/version-control-system/)
-   [在 Windows 上安装 Git](https://toolsqa.com/git/install-git-on-windows/)
-   [什么是 GIT 和 GitHub](https://www.toolsqa.com/git/difference-between-git-and-github/)

## 什么是 Git 存储库？

Git的目的 是管理一个项目或一组文件，因为它们会随时间发生变化。 Git 将此信息存储在称为 存储库的数据结构中。简而言之 ，Git 存储库是所有项目文件及其历史的集合。它是项目的虚拟存储，你可以在其中保存项目的所有资源/文件以及一个名为.git 的特殊文件夹。Git程序使用 git 存储库中的 .git文件夹 来存储有关存储库的信息，例如Logs、Position of Head等。它允许你保存代码的版本，可以访问、跟踪和管理这些版本。

在本文中，我们对这些术语知之甚少，因此我们将尽量减少对.git文件夹的讨论。目前，请记住每个GIT 存储库都有一个隐藏的 .git文件夹，以使GIT 程序能够跟踪存储库中的更改。

### 创建 Git 存储库的不同方法

我们可以使用下面列出的三种方法之一创建 Git 存储库：

1.  创建一个裸仓库
2.  在现有项目目录中初始化存储库
3.  从 Github 克隆一个远程仓库

我们将使用Git 命令行工具创建存储库，并学习本系列教程中的所有 Git 操作。使用 Git 命令行工具将帮助你熟悉GIT 命令。[使用 Git 命令行工具比Git 客户端](https://www.toolsqa.com/git/git-clients/)或Git GUI更灵活，因为它们只提供GIT 功能的一个子集。因此，要像专业人士一样充分利用 Git 的全部功能，Git 命令行工具是使用 GIT 的推荐方式。

那么，让我们开始吧，看看我们如何通过上述三种方式创建一个 Git 存储库。首先是在系统上找到Git 命令行工具。我希望你已经完成了此处教程中描述的 Git 安装过程：

-   [在 Windows 上安装 Git](https://toolsqa.com/git/install-git-on-windows/)

#### 创建裸 Git 存储库

为新项目创建 Bare Git 存储库分为三个步骤：

1.  创建新项目/文件夹
2.  浏览到新项目
3.  为项目初始化 Git 存储库

安装 Git 后，只需在系统搜索栏中搜索git cmd  。你将获得如下图所示列出的命令行工具。

![搜索 Git 命令](https://www.toolsqa.com/gallery/Git/1.Search%20for%20Git%20CMD.jpg)

注意：这是 Windows 8 系统搜索栏，根据你拥有的操作系统，你会有不同的搜索栏。

打开Git命令行工具，你会看到一个命令行窗口打开，如下图所示。

![导航到用于存储所有本地 git 存储库的文件夹](https://www.toolsqa.com/gallery/Git/2.Navigate%20to%20folder%20for%20storing%20all%20local%20git%20repositories.jpg)

第 1 步：创建一个新项目/文件夹- 现在我们已经打开了命令行工具，让我们创建一个项目文件夹。创建一个名字好听的项目文件夹非常重要。如果你的项目有一个好的且相关的名称，那么当你将来回过头来时，你将更容易识别并与该项目相关联。

注意：通常人们会创建一个带有公司名称的根项目文件夹，例如toolsqa、amazon、flipkart等，并且在这个根文件夹中，他们保留同一公司的所有不同项目。

在 Windows 和 Mac 系统上创建文件夹的命令是`mkdir <folderName>`. 其中文件夹名称是项目名称。让我们将第一个项目 命名为LocalGit。使用此名称，命令变为：`mkdir LocalGit`

![创建一个目录来存储所有本地 git 存储库](https://www.toolsqa.com/gallery/Git/3.Create%20a%20directory%20to%20store%20all%20local%20git%20repositories.jpg)

`cd LocalGit`在 Windows 和 Mac 系统上使用命令导航到此文件夹。

![导航到为存储 git 存储库而创建的目录](https://www.toolsqa.com/gallery/Git/4.Navigating%20to%20directory%20created%20for%20storing%20git%20repositories.jpg)

现在你位于存储库文件夹中，我们将在其中使用上述三种方法创建 Git 存储库。

裸 Git 存储库意味着一个空目录，只有一个隐藏的.git文件夹。让我们将该项目命名为BareGitRepo。在继续执行以下步骤之前，请确保你位于LocalGit文件夹中。输入命令`mkdir BareGitRepo`。

![创建一个目录来存放裸仓库](https://www.toolsqa.com/gallery/Git/5.Create%20a%20directory%20to%20store%20bare%20repository.jpg)

注意：现在你已经准备好根文件夹 (LocalGit)，并且在该项目 (BareGitRepository) 中

第 2 步：浏览到新项目- 使用命令导航到在上一步中创建的项目`cd BareGitRepo`。

![导航到创建的目录以初始化裸仓库](https://www.toolsqa.com/gallery/Git/6.Navigate%20to%20directory%20created%20to%20initialize%20bare%20repo.jpg)

第 3 步：为项目初始化 Bare Git 存储库- 输入`git init`此命令用于创建 Git 存储库的命令。因此，执行此命令会在其中创建一个隐藏的.git文件夹。或者换句话说，一个空的Git Repository 被初始化了。你会注意到一条消息，指出已创建一个空的Git存储库。

![创建 Git 存储库](https://www.toolsqa.com/gallery/Git/7.Create%20Git%20Repository.jpg)

注意： git init 是标准的GIT 命令，它 使用用于跟踪项目工件版本的.git 文件夹初始化目录。

让我们通过执行命令来查看新初始化的 Git 仓库的内容`dir /ah`。

![查看裸仓库的内容](https://www.toolsqa.com/gallery/Git/8.View%20content%20of%20bare%20repository.jpg)

执行dir后，你将看到Bare Git 存储库中预期的空项目文件夹。你现在可以将项目文件添加到此存储库，GIT 将跟踪它们。

但是，如果你想在命令行中查看报告的 .git 文件夹，请使用命令`dir /a:hd`。这将显示目录中的隐藏文件夹，你会注意到那里有.git文件夹。

#### 为现有项目创建 Git 存储库

我们还想使用 Git 跟踪现有项目。在这种情况下，我们将在现有项目目录中初始化一个 Git 存储库。为现有项目创建 git 存储库没有火箭科学，它与为新项目创建 git 存储库相同，唯一的区别是不需要步骤 1：

1.  创建新项目/文件夹
2.  浏览到现有项目
3.  为项目初始化 Git 存储库

第 2 步：浏览到现有项目- 导航到包含你的项目工件的目录。在这种情况下，让我们考虑项目的名称是SampleProject在位置C:\Users\admin1\LocalGit\SampleProject，移动到系统上的类似位置。可以使用命令查看目录的内容`dir`。

![现有项目内容](https://www.toolsqa.com/gallery/Git/9.Content%20of%20existing%20project.jpg)

注意：我在 SampleProject 中已经有几个文件用于演示目的，所以它不是一个空文件夹。

第 3 步：为项目初始化 Git 存储库- 使用创建裸存储库时使用的相同命令初始化此项目中的 git 存储库，即`git init`.

![现有项目目录中的 Git init](https://www.toolsqa.com/gallery/Git/10.Git%20init%20in%20existing%20project%20directory.jpg)

查看目录内容，发现其中已经创建了一个.git文件夹。

![Git Init命令后查看项目目录](https://www.toolsqa.com/gallery/Git/11.View%20Project%20Directory%20after%20Git%20Init%20command.jpg)

Git 初始化后，项目由GIT跟踪。

#### 从 GitHub 克隆一个远程仓库

当你想为托管在 GitHub 或类似在线 Git 服务提供商上的现有项目做出贡献时，你必须从远程服务器(如 Github、GitLab 等)克隆他们的存储库。例如，Apache POI 的远程存储库(Java 库读取和写入 Excel 文件)托管在 https://github.com/apache/poi

在 Git 上下文中，单词克隆的定义是创建远程存储库的本地副本。在这种情况下，远程存储库托管在https://github.com/apache/poi上，我们将在本地系统上克隆它。

以下是克隆(下载并跟踪更改)此存储库的步骤。

第 1 步： 获取并复制GitHub 上Apache POI 存储库的 URL，如下图所示。即 https://github.com/apache/poi.git

![远程 Apache POI 存储库](https://www.toolsqa.com/gallery/Git/12.Remote%20Apache%20POI%20Repository.jpg)

第 2 步：在 Git CMD 中，导航到用于在本地存储所有 Git 存储库的文件夹。在这个例子中就是C:\Users\admin1\LocalGit 。

第 3 步：创建一个名为 RemoteCloneRepo 的目录，使用命令在本地存储 Apache POI 存储库的源代码 使用命令`mkdir RemoteCloneRepo `在这个新创建的目录中导航`cd RemoteCloneRepo`

![创建一个目录来克隆远程仓库](https://www.toolsqa.com/gallery/Git/13.Create%20a%20directory%20to%20clone%20remote%20repo.jpg)

第 4 步：要克隆存储库，请输入命令`git clone https://github.com/apache/poi.git`

![执行git克隆命令](https://www.toolsqa.com/gallery/Git/14.Execute%20git%20clone%20command.jpg)

注意： git clone`<repoURL>` 是 用于克隆现有远程存储库的标准GIT命令。

第 5 步：克隆存储库取决于存储库的大小。通常，大型存储库需要一段时间。你必须等到所有文件都签出。

现在你可以对存储库进行更改。Git 将跟踪所有更改。

![执行git克隆命令](https://www.toolsqa.com/gallery/Git/15.Execute%20git%20clone%20command.jpg)

在本教程中，我们了解了创建Git存储库的三种方法。在下一个教程中，我们将了解Git存储库中文件的典型生命周期。我们还将了解不同的Git命令，这些命令将帮助我们将 Git 存储库移动到典型Git生命周期的不同阶段。
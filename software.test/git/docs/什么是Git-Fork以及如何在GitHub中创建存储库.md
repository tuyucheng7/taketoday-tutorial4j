通过这些教程，我们[在 GitHub 上获得了一个帐户，](https://www.toolsqa.com/git/how-to-create-github-account/)并将[本地存储库与 GitHub 存储库链接起来](https://www.toolsqa.com/git/create-github-repository/)。这样做让我们可以自由使用令人惊叹的 GitHub 功能。当我们开始将 Git 与 GitHub 结合使用时，我们将阐明如何将其他人的存储库复制到我们的帐户中(使用 Git Fork 在 Git 中进行 Forking)。此操作的原因简单明了，即为了我们自己的目的进行更改或使用存储库。

换句话说，在本教程中，我们将看到一个希望为公共存储库(或私有许可存储库)做出贡献的人的视角。这个术语或过程称为git forking，这篇文章就是关于它的。

听起来很令人兴奋？综上所述，在本教程中，我们将讨论以下主题：

-   Git Fork 及其重要性
-   分叉 GitHub 存储库
-   删除分叉存储库

## Git 中的 Git Fork 和 Fork 是什么？

复刻是存储库的副本。 分叉存储库允许在不影响原始项目的情况下自由试验更改。

使困惑？让我再尝试一次。GitHub 中的分叉是将完整存储库的副本从另一个帐户创建到用户的 GitHub 帐户的过程。当用户 fork 一个仓库时，仓库中的所有文件都会自动复制到用户在 GitHub 上的帐户中，感觉就像是用户自己的仓库。此过程类似于将文件夹从一个驱动器复制到计算机上的另一个驱动器。然后用户可以出于他们的目的自由使用此存储库或尝试更改代码。通过git fork，用户可以对属于他人的代码进行自己的修改。

需要注意的是，此过程对原始存储库(也称为上游存储库)代码没有任何影响。

![Git Fork - 分叉回购](https://www.toolsqa.com/gallery/Git/1.Git%20Fork%20-%20Fork%20a%20Repo.png)

Git 通过 GitHub 进行分叉是一个与 GitHub 隔离的过程。这意味着无论何时发生 git 分叉，存储库和代码仍然限制在用户的 GitHub 帐户中。对用户的本地计算机或 Git 参与该过程没有影响。

### 为什么要在 GitHub 上创建一个仓库？

分叉 GitHub 存储库为个人提供了上游存储库的副本到他们的帐户。但是为什么我们需要 fork 一个由其他人开发的存储库呢？这个问题的答案在于 GitHub 本身的概念。GitHub 的开发旨在为全球所有开发人员提供一个平台，以便他们可以为彼此的项目做出贡献，并制作出更好、更可靠的软件。很明显，没有人愿意在未经他们同意的情况下对原始存储库进行数百次更改。因此，存储库副本或分叉存储库的概念出现了。

在 GitHub 上分叉存储库有两个主要目的：

-   改进某人的代码/软件：改进某人的代码并不一定意味着修复错误和缩短执行时间。改进可以是向现有软件/存储库添加新功能。例如，我导航到存储库并喜欢该软件的概念。现在我想到了一些可能对同一软件有用的东西。我可以分叉存储库，在我的机器上开发功能并将更改发送给存储库的所有者。
-   在项目中重用代码：用户还可以使用 git fork 来 fork 另一个用户的存储库以在自己的项目中使用。这里的想法是“为什么要重新发明轮子”。Git 的流行也是因为人们将他们的代码、项目、模块、软件等添加到 GitHub 作为公共存储库。现在其他人可以在他们的项目中使用该开源代码，这有助于他们节省精力和时间。

注意：未经许可，允许对公共存储库进行分叉。但是，如果存储库是私有的，则只有在获得存储库所有者的许可后，他或她才能进行分叉。

### 分叉(Git Fork)是如何工作的？

Git Fork 是 GitHub 中的一个简单过程，不需要使用任何 git 命令。Git Fork 的过程遵循以下步骤：

1.  Fork a Repository：用户将存储库的副本创建到他们自己的 GitHub 帐户，下一节将介绍相同的步骤。
2.  代码改动：这涉及到git克隆，这是Git教程系列的下一章。但在高层次上，用户进行更改并将其推送回他们自己的分叉存储库。
3.  将更改发送到原始存储库：此过程在 Git 中称为Pull Request。在此步骤中，用户将更改作为请求发送给存储库的所有者。现在由所有者决定是接受更改还是拒绝。拉取请求是对上游存储库所有者接受用户更改的请求。

![1_拉请求](https://www.toolsqa.com/gallery/Git/2.1_Pull-Request.png)

好吧，我希望你已经清楚在 GitHub 上分叉存储库的概念。现在是我们看看如何做到这一点的时候了。

## Git 叉子

在本节中，我们将看到如何分叉 GitHub 存储库。用户可以分叉任何公共存储库并将其添加到自己的帐户中。但是，为简单起见，我们将分叉一个我在另一个帐户上创建的小型存储库。

### 如何在 GitHub 上创建一个仓库？

要 fork 我在 GitHub 上的一个存储库进行练习，请[登录](https://github.com/login)你的帐户。

现在，我的帐户名(我们将从中分叉存储库)是harishrajora，而存储库名称是ToolsQA。你可以使用以下任一参数或同时使用这两个参数来搜索存储库。

如果你使用名称作为参数，请在搜索栏中键入名称。输入名称后按All GitHub可以在 GitHub 中到处搜索。

![Git 分叉过程 - search_by_name_github](https://www.toolsqa.com/gallery/Git/3.Git%20Forking%20Process%20-%20search_by_name_github.png)

将出现一条消息，告知没有此名称的存储库。

![Git 分叉过程 - 找到要分叉的 GIT 存储库](https://www.toolsqa.com/gallery/Git/4.Git%20Forking%20Process%20-%20Find%20a%20GIT%20repo%20to%20fork.png)

这是因为如果你查看页面的左栏，默认情况下会在该栏中选择存储库。由于没有名为harishrajora的存储库，它显示了该消息。我们知道harishrajora是一个用户名，所以，在列表中选择Users 。

![Git 分叉过程 - 选择用户搜索 GitHub](https://www.toolsqa.com/gallery/Git/5.Git%20Forking%20Process%20-%20Select%20Users%20Search%20GitHub.png)

用户前面的数字表示有两个用户可以使用该名称。仅选择名称为harishrajora的第一个。

![Git 分叉 - list_of_users](https://www.toolsqa.com/gallery/Git/6.Git%20Forking%20-%20list_of_users.png)

如果你使用名称+存储库作为搜索参数，请在搜索栏中键入以下内容并按All GitHub。

![search_by_name_repo](https://www.toolsqa.com/gallery/Git/7.search_by_name_repo.png)

继续我们首先执行的按用户名搜索，一旦你选择了一个用户，他的个人资料就会打开。该配置文件将显示流行的存储库。在存储库中选择ToolsQA。

![工具QA_select_repo](https://www.toolsqa.com/gallery/Git/8.ToolsQA_select_repo.png)

按ToolsQA将打开存储库页面。你也可以跳过上述所有步骤，直接通过此[链接](https://github.com/harishrajora/ToolsQA)访问存储库页面。但是，最好知道如何导航。

按Fork按钮启动 git 分叉过程。

![Github 上的 Git 分叉按钮](https://www.toolsqa.com/gallery/Git/9.Git%20Forking%20button%20on%20Github.png)

存储库ToolsQA将立即分叉到你的帐户。这可以在分叉存储库后通过你的用户名看到。

![分叉回购](https://www.toolsqa.com/gallery/Git/10.forked_repo.png)

现在你已经使用 git fork 复制了存储库，你可以根据自己的需要修改和改进代码。在某些情况下，你可能想要删除分叉的存储库。下一节将在这方面帮助你。

### 如何删除分叉的存储库？

用户可以出于任何原因删除分叉的存储库。也许需求已经结束，或者用户希望在更改完成后在存储库部分有一个更小的混乱。删除分叉存储库也有助于节省内存，因为 GitHub 创建了一个指向分叉存储库的原始存储库指针(以及需要额外内存的更改)。这些只是几个原因，删除分叉存储库的原因可能有很多。

幸运的是，这是一个非常简单的过程。请记住，在更改已合并到原始存储库中之前不要删除存储库，或者用户对删除存储库过于确定，因为删除后，你将丢失所有更改。

好吧，让我们删除上面分叉的同一个存储库。为此，请转到我们在上一节中分叉的存储库页面 ( ToolsQA )。你可以从位于仪表板左栏中的存储库列表导航到存储库页面。

![存储库_部分](https://www.toolsqa.com/gallery/Git/11.repository_Section.png)

1.在存储库页面上，转到位于顶行的设置。

![设置_存储库](https://www.toolsqa.com/gallery/Git/12.Settings_Repository.png)

1.  在设置页面上，向下滚动到页面底部，将出现一个名为“危险区域”的部分。

![删除_this_repository](https://www.toolsqa.com/gallery/Git/13.delete_this_repository.png)

1.  如上图所示，在此部分中按删除此存储库。
2.  屏幕上会弹出一条免责声明，要求你重写存储库名称。如图所示写入存储库名称，然后按我了解后果，删除此存储库按钮。

![免责声明_del_repo](https://www.toolsqa.com/gallery/Git/14.disclaimer_del_repo.png)

1.  这将成功删除存储库。

![Git 分叉过程 - repository_deleted](https://www.toolsqa.com/gallery/Git/15.Git%20Forking%20Process%20-%20repository_deleted.png)

GitHub 的核心是 Git 存储库。它的目的是在全世界分享它们。在 GitHub 中分叉存储库是 GitHub 上最常见的操作之一。如果你不将原始存储库分叉到你的帐户上，你将如何做出贡献？分叉存储库并删除它是一个简单的过程，我希望你能理解它。

现在看来 git fork 和 git clone 是一回事，其实不然。你可能会听到人们有时会对这两件事感到困惑。但是在 GitHub 上工作的开发人员不会感到困惑，因为它们有自己的重要性和差异。我们将在下一个关于克隆的教程中了解这一点，以及它与分叉的区别。
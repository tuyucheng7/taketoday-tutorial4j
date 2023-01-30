## 1. 概述

版本控制系统跟踪对文件所做的更改。因此，它们提供了恢复项目文件及其不同版本的便利。此外，它们通常允许多个用户在处理同一文件时进行合作。如今，它已成为每个软件开发过程的必备工具，无论是商业软件还是个人软件。

在本教程中，我们将分析和比较两个最流行的版本控制系统，即 GIT 和 SVN。

## 2. 混帐

首先，让我们介绍一下常见版本控制系统的功能：

-   存储项目的文件并控制对它们的访问
-   保存文件更改的历史记录
-   提供创建和检索项目的不同版本的可能性
-   表示更改文档
-   允许团队成员同时处理相同的文件并同步更改
-   [与CI/CD 管道](https://www.baeldung.com/spring-boot-ci-cd)集成

GIT 是广泛使用的版本控制系统之一。据估计，[2019 年所有存储库中有 72% 使用了 git](https://www.openhub.net/repositories/compare)。存储库是指项目文件的存储容器。GIT 是一个分布式版本控制系统。它最初由 Linus Torvalds 创建，用于支持 Linux 内核的开发过程。

GIT 在 GNU GPL 下获得许可。因此，它可以免费用于个人和商业目的。此外，还有许多免费的基于 GIT 的存储库托管。它导致了这个版本控制系统如此受欢迎。

### 2.1. 特征

首先，GIT 支持[分支](https://www.baeldung.com/egit#branches)。分支是项目的一个单独实例。它们允许开发人员同时在同一个代码库上工作而不会互相干扰。GIT 提供了一些算法来合并(同步)不同的分支。此外，它还允许按需添加自定义算法。

其次，GIT 允许离线工作。每个团队成员在自己的计算机上都有一个本地存储库。因此，他们可以在没有网络连接的情况下保存更改。离线时，更改可以被存储、分支或提交。然后，在连接到 Internet 网络时，可以将它们推送到服务器上的存储库。

第三，GIT 确保高效处理大型项目。[Mozilla 所做的性能测试](https://web.archive.org/web/20100529094107/http://weblogs.mozillazine.org/jst/archives/2006/11/vcs_performance.html)证明 GIT 比许多竞争对手快得多。此外，Linus Torvald 将 GIT 描述为一种非常快速且可扩展的解决方案。

此外，GIT 与许多重要的协议和系统兼容，例如[HTTP、HTTPS](https://www.baeldung.com/spring-channel-security-https)、[FTP](https://www.baeldung.com/java-file-sftp)、[SSH](https://www.baeldung.com/cs/ssh-intro)。

总而言之，GIT 是一种分布式版本控制系统，它提供了处理项目(尤其是大型项目)的高效技术。此外，它支持分布式工作流程并保证舒适的协作方式。

### 2.2. 基本命令

一般来说，GIT 是一个基于命令行的工具。这意味着可以通过在控制台中执行特定命令来访问每个功能。尽管有很多图形化工具可用于 GIT，但基本命令的知识至关重要。在本节中，我们将定义最重要的：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9b97cc70b9f105c4dd88a9f965bebb07_l3.svg)

### 2.2. 创建存储库

为了体验使用 GIT，让我们看看如何创建一个新的存储库。以下说明适用于任何基于 GIT 的托管，例如[Github](https://github.com/)、[GitLab](https://about.gitlab.com/)或[BitBucket](https://bitbucket.org/)。[其中最受欢迎的是 Github](https://thenewstack.io/i-dont-git-it-tracking-the-source-collaboration-market/)。它是一个强大的版本控制系统和协作工具。它功能强大，并且大部分功能都可以免费使用。所以，这可能是一条路要走。

首先，我们需要在项目目录中使用以下命令将本地项目初始化为 GIT 存储库：

```bash
git init
```

该命令在给定位置创建一个 .git子目录。它包含 GIT 处理存储库所需的元数据。其次，我们需要添加所有项目文件以进行 GIT 跟踪。为了实现这一点，我们可以使用上一节中提到的命令：

```bash
git add .
```

之后，暂存所有文件。因此，我们需要将它们提交到本地存储库：

```bash
git commit -m "Initial commit"
```

然后，需要连接到远程存储库。它只需要完成一次：

```bash
git remote add origin [repository-url]
```

最后，让我们将所有内容发送到服务器，发送到默认的master分支：

```bash
git push -u origin master
```

## 3.SVN

第二个著名的版本控制系统是 SVN。正如我们在前面提到的[统计数据](https://www.openhub.net/repositories/compare)中看到的那样，它被 23% 的存储库使用。因此，它不如 GIT 受欢迎。尽管如此，它仍然是市场上使用第二多的版本控制系统。它是作为称为[CVS](https://en.wikipedia.org/wiki/Concurrent_Versions_System)的旧系统的继承者创建的。SVN 旨在消除其祖先的错误和限制。不过，它的目标是尽可能与[CVS](https://www.baeldung.com/spring-app-setup-with-csv-files)兼容。

SVN 在 [Apache License](https://en.wikipedia.org/wiki/Apache_License)下获得许可。因此，它可以免费用于个人和商业目的。虽然，SVN 的免费托管和工具不如 GIT 多。

在接下来的部分中，我们将定义 SVN 的核心概念。

### 3.1. 特征

首先，SVN需要一个中心化的服务器。与 GIT 不同，它既可以充当客户端又可以充当服务器。因此，SVN被称为集中式版本控制系统。 

其次，SVN 不提供用于分支的高级工具。分支只是所谓的目录的廉价副本。简而言之，副本不是文件的文字副本。相反，它只是指向特定修订的指针。这种技术的优点是分支是轻量级的。他们需要很少的额外空间来创建。

此外，SVN 旨在有效地处理二进制文件。这意味着它可以用于版本控制文档，例如，使用 Microsoft Office 创建的文档。因此，甚至有专用于此目的的工具，例如[MSOfficeSVN](https://sourceforge.net/projects/msofficesvn/)。

由于 SVN 的集中特性，所有内容都存储在服务器上的单个存储库中。因此，可以轻松管理访问控制属性。此外，备份很简单，因为只需要处理一个位置。

最后，SVN 被认为易于学习。当然，与 GIT 相比，学习曲线更低。SVN 主要使用简单直接的机制，至少从用户的角度来看是这样。然而，它是双刃剑，因为在某些情况下，版本控制可能需要一些高级功能。

### 3.2. 基本命令

让我们快速进入 SVN 的一些基本命令：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-29bc7e1bdca1b2376cbff202641119b3_l3.svg)

### 3.3. 创建存储库

创建存储库取决于一些因素，例如服务器的操作系统、客户端的操作系统或使用的协议。以下是从本地项目创建存储库的说明，假设服务器上已经设置了一个空存储库。

首先，我们需要通过下载一个空的存储库目录来连接到远程服务器：

```bash
svn co [repository-url]

```

然后，我们需要将特定的项目文件到下载的目录中：

```bash
cp [local-project-directory] [local-repository-directory]
```

之后，需要添加所有的文件以进行 SVN 跟踪：

```bash
svn add [local-repository-directory]
```

最后，我们可以将所有文件发送到服务器：

```bash
svn commit
```

## 4. GIT 与 SVN

我们已经了解 GIT 和 SVN 版本控制系统的基础知识。让我们比较一下它们的核心功能之间的差异：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6ae51e8866d2957d0f69aa0804f91f0f_l3.svg)

那么，选择哪一个呢？基本上，正如我们在比较中看到的那样，GIT 和 SVN 各有利弊。首先，我们应该始终考虑所要求的要求和偏好，然后选择最适合他们的。这两个版本控制系统都是一个强大的工具，可以很好地支持开发过程。尽管 GIT 因其高级分支和协作功能以及各种免费工具和托管可用性而最受欢迎。

## 5.总结

在本文中，我们描述了两个最流行的版本控制系统，即 GIT 和 SVN。首先，我们定义了GIT的核心概念。我们解释了它的基本命令，并提供了一个示例来描述如何创建一个简单的存储库。其次，我们用同样的方式来描述SVN。最后，我们比较了两种版本控制系统的关键特性。
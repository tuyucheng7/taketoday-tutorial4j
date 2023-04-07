## 1. 概述

使用[Git](https://www.baeldung.com/git-guide)是任何开发人员日常工作的重要组成部分。但是，一开始，它可能会让人不知所措，并且错误消息可能并不明显。人们在开始使用 Git 时遇到的最常见问题之一是 refspec 错误：

```shell
error: src refspec master does not match any 
error: failed to push some refs to 'https://github.com/profile/repository.git'复制
```

在本教程中，我们将了解此问题的原因以及如何解决和缓解此问题。

## 二、问题描述

我们中的许多人至少在控制台中看到过一次refspec错误消息。推送到远程存储库时会发生此错误。让我们尝试理解这一行的确切含义：

```shell
error: src refspec master does not match any复制
```

简单的说，这个错误信息告诉我们没有要推送的分支，这是导致这个错误的主要原因。

## 3. 完成步骤

当我们克隆未初始化的存储库并尝试推送本地存储库时，可能会出现refspec 错误 。这就是 Git 服务解释设置本地存储库的方式。以下是来自 GitHub 的步骤：

```shell
$ echo "# repository" >> README.md
$ git init
$ git add README.md
$ git commit -m "first commit"
$ git branch -M main
$ git remote add origin https://github.com/profile/repository.git
$ git push -u origin main复制
```

我们将在下面的段落中提到这些步骤。

## 4.推送一个不存在的分支

让我们按照 GitHub 提供给我们的说明一步步来。

### 4.1. 初始化存储库

第一行创建一个README.md 文件：

```shell
$ echo "# repository" >> README.md复制
```

以下命令将初始化本地 Git 存储库：

```shell
$ git init复制
```

此命令可能会发出以下消息：

```shell
hint: Using 'master' as the name for the initial branch. This default branch name
hint: is subject to change. To configure the initial branch name to use in all
hint: of your new repositories, which will suppress this warning, call:
hint: 
hint: 	git config --global init.defaultBranch <name>
hint: 
hint: Names commonly chosen instead of 'master' are 'main', 'trunk' and
hint: 'development'. The just-created branch can be renamed via this command:
hint: 
hint: 	git branch -m <name>复制
```

2020 年，GitHub将在新存储库中创建的分支的默认名称从“master”[更改](https://github.com/github/renaming)为“main”。[GitLab](https://about.gitlab.com/blog/2021/03/10/new-git-default-branch-name/)上也发生了同样的变化。它仍然可以在[GitHub](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-branches-in-your-repository/changing-the-default-branch)、[GitLab](https://docs.gitlab.com/ee/user/project/repository/branches/default.html#change-the-default-branch-name-for-a-project)和[Git](https://github.blog/2020-07-27-highlights-from-git-2-28/)上配置。

### 4.2. 第一次提交

随后的两个命令在我们的本地存储库中创建一个提交：

```shell
$ git add README.md 
$ git commit -m "first commit"复制
```

### 4.3. 重命名分支

有趣的事情发生在以下行中：

```shell
$ git branch -M main复制
```

这一行负责将我们当前的本地分支重命名为“main”。发生这种情况是因为提示消息中解释的原因。这一行将重命名我们的默认分支以匹配我们远程存储库上的默认分支名称。

GitHub 提供的步骤将包含平台上配置的默认名称。然而，这种剩余成为refspec错误背后最常见的原因之一。让我们看看如果我们有一个使用“master”作为默认分支的本地存储库和一个使用“main”的远程存储库会发生什么。我们将跳过此示例的重命名步骤，直接设置我们的远程存储库：

```shell
$ git remote add origin https://github.com/profile/repository.git复制
```

### 4.4. 问题

我们将开始在这条线上遇到问题：

```shell
$ git push -u origin main复制
```

让我们回顾一下这一行并查阅[文档](https://git-scm.com/docs/git-push)以了解发生了什么。这一行将更改从分支(在本例中为“main”)推送到我们在上一行中配置的远程存储库。

这意味着本地存储库应该包含“主”分支。但是，默认的本地分支名称设置为“master”， 我们没有创建新的“ main ”分支或重命名“master”分支。在这种情况下，Git 将无法找到要推送的“ main ” 分支，我们将收到以下错误消息：

```shell
error: src refspec main does not match any
error: failed to push some refs to 'origin'复制
```

现在这条消息更有意义了。如前所述，这个错误告诉我们没有“主” 分支。有几种方法可以解决此问题。第一个是将我们当前的“ master ” 分支重命名为“main” ：

```shell
$ git branch -M main复制
```

重命名操作后，我们可以重复 push 命令，这将没有问题。同时，我们可以将要推送的分支的名称从“ main ”更改为“ master ”，或者我们在本地存储库中用作默认名称的任何名称。以下命令将在远程存储库上创建一个“ master ”分支：

```shell
$ git push -u origin master复制
```

最后，如果我们想在本地存储库中使用“ master” 名称，在远程存储库中使用“main”名称，我们可以使用以下命令显式设置上游分支：

```shell
$ git push -u origin master:main复制
```

标志-u还将设置本地“ master ” 分支和远程“ main ” 分支之间的上游连接。这意味着下次我们可以在不显式识别上游分支的情况下使用命令：

```
$ git push
```

## 5.推送一个空的仓库

此问题的另一个原因是推送一个空存储库。然而，其背后的原因是相同的——试图推送一个不存在的分支。假设我们已经创建了一个新的存储库。分支机构被恰当地命名。我们添加了一个文件但没有提交它：

```shell
$ echo "# another-test-repo" >> README.md
$ git init
$ git add README.md
$ git branch -M main
$ git remote add origin https://github.com/profile/repository.git
$ git push -u origin main复制
```

虽然我们在“主要”分支上，但从技术上讲，它并不存在。对于要在 .git/refs/heads 下创建的分支，它应该至少包含一个提交。让我们确保此时我们的仓库中的文件夹.git/refs/heads 是空的：

```shell
$ ls .git/refs/heads 复制
```

此命令应向我们显示一个空文件夹。因此，与前面的示例一样，我们试图推送一个不存在的分支。一次提交即可解决问题。它将创建一个分支并可以推送更改：

```shell
$ git commit -m "first commit"
$ git push -u origin master复制
```

## 六，总结

创建和初始化新的本地存储库并不是一项具有挑战性的任务。但是，跳过步骤或盲目遵循说明可能会导致错误。这些错误有时无法明确理解，尤其是对于 Git 新用户而言。

在本文中，我们了解了如何处理refspec错误以及它们背后的原因。
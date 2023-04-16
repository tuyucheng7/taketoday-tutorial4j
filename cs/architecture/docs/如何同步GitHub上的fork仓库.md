## 1. 概述

在本快速教程中，我们将学习如何将 GitHub 存储库与其派生的原始存储库同步。

## 2.同步存储库

有两种同步存储库的方法：

### 2.1. 使用 GitHub 网页界面

第一种方法是使用 GitHub 的 Web UI。如果我们输入我们一直在处理的分支的 URL，我们会得到以下信息：

[![旧分支](https://www.baeldung.com/wp-content/uploads/2022/12/old_branch.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/old_branch.jpg)

要将分支与原始存储库同步，我们应该点击“Sync fork”：

[![同步分支](https://www.baeldung.com/wp-content/uploads/2022/12/syncing_branch.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/syncing_branch.jpg)

分支开始与原始存储库同步。之后，分支将是最新的：

[![新分行](https://www.baeldung.com/wp-content/uploads/2022/12/new_branch.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/new_branch.jpg)

我们已成功将分支与原始存储库同步。

### 2.2. 使用命令行 (CLI)

另一种方法是使用命令行。要使用命令行同步我们的分支，我们应该首先克隆它：

```shell
$ git clone https://github.com/[username]/[repository_name]复制
```

克隆我们的 fork 后，我们应该 向原始存储库添加一个远程。我们可以随心所欲地称呼它。让我们称之为上游：

```shell
$ git remote add upstream https://github.com/[original_username]/[original_repository_name]复制
```

现在，我们应该从上游远程获取所有分支 ：

```shell
$ git fetch upstream复制
```

之后，我们应该切换到我们想要更新的分支：

```shell
$ git checkout branch3
Branch 'branch3' set up to track remote branch 'branch3' from 'origin'.
Switched to a new branch 'branch3'复制
```

现在，我们应该更改分支的基础，以便我们的新提交出现在上游存储库的 master分支之上：

```shell
$ git rebase upstream/master
First, rewinding head to replay your work on top of it...
Fast-forwarded branch3 to upstream/master.复制
```

最后，我们应该将更改推送到 GitHub 上的原始存储库：

```powershell
$ git push -f origin branch3复制
```

我们使用 -f标志强制推送到我们的远程存储库。

## 三、总结

在本快速教程中，我们学习了如何使用 GitHub 的 Web UI 和命令行将分叉与其原始存储库同步。
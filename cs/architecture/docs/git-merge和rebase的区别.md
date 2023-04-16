## 1. 概述

在使用 git 作为我们的版本控制系统 (VCS) 时，我们可能会遵循任何分支策略，但最终，我们可能需要将更改从其中一个功能分支集成到主分支或主要分支。

在本教程中，我们将研究将更改从一个分支集成到另一个分支的两种不同方式。

## 2. 混帐狐狸

简单来说，git rebase将你的整个功能分支移动到主分支的顶端。它为原始功能分支中的每个提交创建全新的提交。

让我们在存储库中创建一个新的存储库和一个功能分支，以了解变基的工作原理：

```shell
git clone <your_repository_here>
git branch testBranch1
git branch testBranch2复制
```

让我们在testBranch1功能分支中创建一个新文件并提交更改：

```shell
git add .
git commit -m "<Commit_Message_Here>"
git push --set-upstream origin testBranch1
git log复制
```

执行这些命令将为我们提供以下输出：

[![git RebaseFeature Branch1 合并提交日志](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch1CommitLog.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch1CommitLog.jpg)

现在，让我们尝试将这个分支变基到主分支上：

```shell
git rebase main复制
```

这将导致以下消息：

[![Git Rebase 功能分支 1 后提交](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch1PostCommit.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch1PostCommit.jpg)

由于主分支中没有提交，我们不应该期待任何更改，如上所示。

现在，让我们将功能分支合并到主分支上：

```shell
git checkout main
git merge testBranch1
git push
git log复制
```

这些命令将输出以下内容：

[![git Merge Feature Branch1 Rebase 合并提交日志](https://www.baeldung.com/wp-content/uploads/2022/05/1_gitRebaseFeatureBranch1RebaseMergeCommitLog-1024x200.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch1RebaseMergeCommitLog.jpg)

合并到主分支时，功能分支的提交 ID 没有变化。这类似于快进合并发生的情况。

由于我们已经将testBranch1合并到主分支，因此testBranch2缺少从它被剪切的地方开始的提交。

我们来看看testBranch2是如何变基和合并的。

让我们在testBranch2 功能分支中创建一个新文件并提交更改：

```shell
git checkout testBranch2
git add .
git commit -m "<Commit_Message_Here>"
git push --set-upstream origin testBranch2
git log复制
```

在这些命令完成后，我们将看到：

[![git Rebase Feature Branch2 提交日志](https://www.baeldung.com/wp-content/uploads/2022/05/2_gitRebaseFeatureBranch2CommitLog.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/2_gitRebaseFeatureBranch2CommitLog.jpg)

现在让我们尝试在主分支上重新设置这个分支：

```shell
git rebase main复制
```

这应该给我们一个与之前案例不同的信息：

[![git Rebase 特性 Branch2 Rebase](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch2PostCommit.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch2PostCommit.jpg)

由于主分支上有一些提交，因此功能分支基于它进行了重新设置。现在让我们合并主分支上的 featureBranch2。我们应该期望featureBranch2在 rebase 之前和之后的提交 ID 是不同的：

```shell
git checkout main
git merge testBranch2
git push
git log复制
```

这些命令将输出以下内容：

[![git Rebase Feature Branch 2 Rebase 合并提交日志](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch2RebaseMergeCommitLog.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitRebaseFeatureBranch2RebaseMergeCommitLog.jpg)

提交 ID 与预期不同，如果我们看一下 git 日志图，我们将看到 repo 具有线性历史记录：

```shell
git log --graph --oneline复制
```

上面的命令显示了一个图形结构，在一行中显示提交信息：

[![git Rebase 合并分支图最终](https://www.baeldung.com/wp-content/uploads/2022/05/1_gitRebaseMergeBranchGraphFinal.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/1_gitRebaseMergeBranchGraphFinal.jpg)

## 3. 合并

git merge 会把我们正在合并的两个分支，找到公共的base commit 然后在base commit 上从两个分支播放commit sequence 来合并分支。

让我们创建一个新的存储库和几个功能分支来了解合并的工作原理：

克隆本地计算机中的存储库并创建一个新的功能分支：

```shell
git clone <your_repository_here>
git branch testBranch1
git branch testBranch2复制
```

让我们在testBranch1功能分支中创建一个新文件并提交更改：

```shell
git add .
git commit -m "<Commit_Message_Here>"
git push --set-upstream origin testBranch1
git log复制
```

执行这些命令将为我们提供以下输出：

[![Git 合并功能分支 1 提交日志](https://www.baeldung.com/wp-content/uploads/2022/05/2_gitMergeFeatureBranch1CommitLog.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/2_gitMergeFeatureBranch1CommitLog.jpg)

现在让我们使用 merge 命令将这个特性分支合并到主分支上：

```shell
git checkout main
git merge testBranch1
git push
git log复制
```

这些命令将输出以下内容：

[![git Merge Feature Branch1 合并提交日志](https://www.baeldung.com/wp-content/uploads/2022/05/gitMergeFeatureBranch1MergeCommitLog.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitMergeFeatureBranch1MergeCommitLog.jpg)

我们可以注意到最新的提交 ID 与之前的图像相同，但 HEAD 指针指向主分支。

上面是一个简单的合并，其中在我们处理功能分支时主分支没有变化。

让我们看看另一个场景，其中主要分支和特性分支都有变化，以及 git 是如何处理它们的。

让我们在testBranch2 功能分支中创建一个新文件并提交更改：

```avrasm
git checkout testBranch2
git add .
git commit -m "<Commit_Message_Here>"
git push --set-upstream origin testBranch2
git log复制
```

在这些命令完成后，我们将得到以下内容：

[![Git 合并功能分支 2 提交日志](https://www.baeldung.com/wp-content/uploads/2022/05/gitMergeFeatureBranch2CommitLog.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitMergeFeatureBranch2CommitLog.jpg)

现在让我们使用 merge 命令将这个特性分支合并到主分支上：

```r
git checkout main
git merge testBranch2
git log复制
```

然后我们可以在终端中看到它：

[![git Merge Feature Branch2 合并提交日志](https://www.baeldung.com/wp-content/uploads/2022/05/gitMergeFeatureBranch2MergeCommitLog.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitMergeFeatureBranch2MergeCommitLog.jpg)

HEAD 现在指向一个单独的合并提交，而两个功能分支都存在原始提交。最顶层的提交还有一个附加信息键“Merge”，它包含两个分支的提交 ID。

我们还可以检查分支图并验证存储库的历史记录：

```lua
git log --graph --oneline复制
```

上面的命令显示了一个图形结构，在一行中显示提交信息：

[![Git 合并分支图](https://www.baeldung.com/wp-content/uploads/2022/05/gitMergeBranchGraphFinal.jpg)](https://www.baeldung.com/wp-content/uploads/2022/05/gitMergeBranchGraphFinal.jpg)

## 4.用例

每当我们要求我们的存储库历史是线性的时，我们应该进行变基。但是我们应该小心使用变基而不是合并我们存储库之外的提交，因为其他合作者可能有他们自己的工作基于现有的提交。

将已经推送到公共仓库的提交变基将导致不同的提交 ID，这可能会使 git 认为其他开发人员的主分支和你变基的主分支已经分离。如果有多个协作者，这可能会造成合并/同步的潜在困难情况。

## 5.总结

在本文中，我们介绍了 git merge 和 git rebase 之间的基本区别，每个开发人员在使用 git VCS 时都应该了解这些区别。
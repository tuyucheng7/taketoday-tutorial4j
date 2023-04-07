## 1. 概述

在[Git](https://www.baeldung.com/git-guide)中，有时[分支](https://www.baeldung.com/git-guide#11-git-branching)没有共同的历史基础。所以，如果我们尝试合并它们，我们会得到“拒绝合并不相关的历史记录”错误。在本教程中，我们将讨论如何修复此错误以及如何在以后的项目中避免此错误。

## 2. 为什么分支可以有不相关的历史？

让我们看看当分支有不相关的历史时的场景。拥有不相关历史基础的最常见原因是启动彼此独立的分支。例如，如果我们在本地机器上启动一个新的 Git 项目，然后将它连接到远程 GitHub 分支，这些分支将具有不同的历史基础。

唯一的例外是其中一个分支没有提交。在这种情况下，它们应该可以毫无问题地合并。否则，我们将得到“拒绝合并不相关的历史记录”，如下例所示：

```bash
$ git pull origin main
...
fatal: refusing to merge unrelated histories
复制
```

如我们所见，我们无法使用[git pull](https://www.baeldung.com/git-guide#103-git-pull--update-and-apply-at-once)命令合并具有不常见历史记录的分支。

## 3. 如何修复错误

要修复上述错误，我们需要在git pull <remote> <branch>命令后使用选项–allow-unrelated-histories，其中

-   <remote>是远程存储库 URL 或其短名称来源
-   <branch>是我们要合并的分支名称

例如：

```bash
$ git pull origin main --allow-unrelated-histories复制
```

–allow -unrelated-histories选项将告诉 Git 我们允许合并没有公共历史基础的分支，然后它应该无误地完成合并。我们应该注意，对于 Git 版本 2.9 或更早版本，此选项不是必需的，我们的合并应该在没有它的情况下工作。要查看 Git 版本，我们可以使用[git –version](https://git-scm.com/search/results?search=git version)命令。


## 4. 以后如何避免错误

通常，独立于远程存储库创建本地存储库分支不是最佳实践。[更可靠的方法是使用git clone](https://www.baeldung.com/git-guide#2-git-clone---clone-an-external-repository)命令将远程存储库下载到本地机器，如下所示：

```bash
$ git clone <repo_url>复制
```

这样，我们从远程服务器复制存储库，并且远程和本地分支的提交历史记录库保持不变。

## 5.总结

在本教程中，我们讨论了在 Git 拒绝合并无关历史记录时如何合并分支。我们首先研究了何时会发生此错误。然后，我们使用–allow-unrelated-histories选项来修复它。最后，我们学会了如何在未来的项目中避免这个错误。
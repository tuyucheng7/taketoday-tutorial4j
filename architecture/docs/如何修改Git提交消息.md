## 1. 概述

在本教程中，我们将了解如何修改[Git](https://www.baeldung.com/git-guide)提交消息，无论是最近的提交还是较早的提交。

## 2.修改最近的提交消息

我们将从最简单的案例开始。让我们构建一个在其提交消息中有拼写错误的简单提交：

```shell
$ touch file1
$ git add file1
$ git commit -m "Ading file1"
[articles/BAEL-5627-how-to-modify-git-commit-message 3e9ac2dbcd] Ading file1
1 file changed, 0 insertions(+), 0 deletions(-)
create mode 100644 file1
复制
```

现在让我们确认我们最新的提交消息中是否存在拼写错误，并注意提交的哈希值：

```shell
$ git log -1
commit 3e9ac2dbcdde562e50c5064b288f5b3fa23f39da (HEAD -> articles/BAEL-5627-how-to-modify-git-commit-message)
Author: baeldung <baeldung@baeldung.com>
Date: Tue Jun 21 21:53:12 2022 +0200

 Ading file1复制
```

要修复拼写错误，我们将使用[修改](https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History)选项：

```shell
$ git commit --amend -m "Adding file1"
[articles/BAEL-5627-how-to-modify-git-commit-message 66dfa06796] Adding file1
Date: Tue Jun 21 21:53:12 2022 +0200
1 file changed, 0 insertions(+), 0 deletions(-)
create mode 100644 file1复制
```

同样，让我们显示最近的提交：

```shell
$ git log -1
commit 66dfa067969f941eef5304a6fbcd5b22d0ba6c2b (HEAD -> articles/BAEL-5627-how-to-modify-git-commit-message)
Author: baeldung <baeldung@baeldung.com>
Date: Tue Jun 21 21:53:12 2022 +0200

 Adding file1复制
```

我们现在可以确认拼写错误已在最近的消息中修复，但也注意到提交哈希已更改。从技术上讲，我们没有更改我们的提交，而是用一个新的替换了它。

## 3.改写旧的提交消息

现在让我们添加两个新的提交，以便在最近的提交中不存在拼写错误，但在较旧的提交中：

```shell
$ touch file2
$ git add file2
$ git commit -m "Ading file2"
[articles/BAEL-5627-how-to-modify-git-commit-message ffb7a68bf6] Ading file2
1 file changed, 0 insertions(+), 0 deletions(-)
create mode 100644 file2
$ touch file3
$ git add file3
$ git commit -m "Adding file3"
[articles/BAEL-5627-how-to-modify-git-commit-message 517193e1e9] Adding file3
1 file changed, 0 insertions(+), 0 deletions(-)
create mode 100644 fil3
复制
```

让我们检查一下我们刚刚添加的两个提交：

```shell
$ git log -2
commit 517193e1e99c784efd48086f955fcdbc3110d097 (HEAD -> articles/BAEL-5627-how-to-modify-git-commit-message)
Author: baeldung <baeldung@baeldung.com>
Date: Tue Jun 21 22:04:56 2022 +0200

 Adding file3

commit ffb7a68bf63c7da9bd0b261ebb9b2ca548aa1333
Author: baeldung <baeldung@baeldung.com>
Date: Tue Jun 21 22:02:59 2022 +0200

 Ading file2复制
```

Git 的amend选项只适用于最新的提交，所以这次我们不能用它来修复拼写错误。

相反，我们将使用 rebase。

### 3.1. 启动交互式 Rebase

 要修复较旧的提交消息，让我们通过运行以下命令继续所谓的[交互式变基：](https://git-scm.com/docs/git-rebase)

```shell
$ git rebase -i HEAD~2
hint: Waiting for your editor to close the file...复制
```

这里的HEAD~2意味着我们将重新访问最近的两个提交。

这将[在你的计算机上打开与 Git 相关联的文本编辑器](https://git-scm.com/book/en/v2/Customizing-Git-Git-Configuration) ，并使用可在变基过程中使用的所有命令填充编辑器，包括：

```plaintext
# Commands:
# p, pick <commit> = use commit
# r, reword <commit> = use commit, but edit the commit message
# e, edit <commit> = use commit, but stop for amending
# s, squash <commit> = use commit, but meld into previous commit复制
```

正如我们所见，我们可以在变基提交时做各种各样的事情，比如通过[squash命令](https://www.baeldung.com/ops/git-squash-commits)将两个提交合并为一个。

在这里，我们要重写提交消息，所以我们将使用reword命令。

### 3.2. 改写提交消息

编辑器中的前两行包含以下文本：

```plaintext
pick ffb7a68bf6 Ading file2
pick 517193e1e9 Adding file3复制
```

请注意，在此视图中，提交是从最早到最近列出的，这与我们使用[git log](https://git-scm.com/docs/git-log)时相反。

让我们将第一行更改为使用reword命令而不是pick命令；我们将pick留给第二次提交，因为我们希望保持该消息原样：

```plaintext
reword ffb7a68bf6 Ading file2
pick 517193e1e9 Adding file3复制
```

如果我们想在单个 rebase 中更改两条消息，我们可以简单地将两行上的命令更改为reword。

现在，我们还没有更改提交消息。所以让我们保存我们的文件并关闭文本编辑器，这让 Git 知道我们已经完成了我们的变基指令。

Git 现在将处理 rebase 命令，在需要我们交互时提示我们。因为我们告诉 Git改写第一个提交，它会用第一个提交的内容重新打开文本编辑器。第一行包含提交信息：

```plaintext
Ading file2复制
```

以下几行的注释描述了改写操作将如何工作。

让我们通过将第一行修改为“Adding file2”、保存文件并关闭编辑器来编辑提交消息。

Git 将更新我们的提交消息，然后完成其余的 rebase 指令。让我们确认 Git 的工作：

```shell
$ git log -2
commit 421d446d77d4824360b516e2f274a7c5299d6498 (HEAD -> articles/BAEL-5627-how-to-modify-git-commit-message)
Author: baeldung <baeldung@baeldung.com>
Date: Tue Jun 21 22:04:56 2022 +0200

 Adding file3

commit a6624ee55fdb9a6a2446fbe6c6fb8fe3bc4bd456
Author: baeldung <baeldung@baeldung.com>
Date: Tue Jun 21 22:02:59 2022 +0200

 Adding file2复制
```

另外，请注意这些提交的提交哈希已经更改，就像我们之前修改过的提交一样。我们所有的原始提交都已替换为新的提交。

## 4.推送你重写的提交

此时，我们的分支上有三个提交：一个修改后的和两个 rebased 的。它们都具有与我们原始提交不同的提交哈希值。

只要原始提交没有被推送到任何远程存储库，我们就可以正常[推送。](https://git-scm.com/docs/git-push)

但是，假设我们之前在替换它们之前推送了任何有缺陷的提交。在这种情况下，远程存储库将拒绝我们的新推送，因为我们的本地提交历史不再与存储库的历史兼容。

因此，如果我们[想要](https://git-scm.com/docs/git-push)推送更正的(但之前推送的)提交，我们将不得不使用[force选项](https://git-scm.com/docs/git-push)：

```shell
$ git push --force复制
```

应谨慎使用此命令，因为它会用我们的更改覆盖远程分支，如果其他人正在使用该分支，这可能会导致问题。

## 5.总结

在本文中，我们了解了如何编辑提交消息，无论是最后一条还是较早的一条。我们还看到了如何将更改后的提交推送到包含原始提交的存储库，并指出这应该小心完成。
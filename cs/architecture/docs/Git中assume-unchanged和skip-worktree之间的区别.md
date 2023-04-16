## 1. 概述

当我们想手动操作[Git](https://www.baeldung.com/git-guide)暂存区中的文件时，我们使用[git update-index 。](https://git-scm.com/docs/git-update-index)此命令支持两个经常被误用的选项： –assume-unchanged和–skip-worktree。

在本教程中，我们将了解这两个选项有何不同，并为每个选项提供一个用例。

## 2. assume-unchanged选项有什么作用？

–assume -unchanged选项告诉 Git 暂时假设跟踪的文件在工作树中没有被修改。因此，所做的更改不会反映在暂存区中：

```powershell
$ git update-index --assume-unchanged assumeunchanged.txt
```

[我们可以使用git ls-files](https://www.baeldung.com/Program Files/Git/mingw64/share/doc/git-doc/git-ls-files.html)验证文件状态：

```powershell
$ git ls-files -v
$ h assumeunchanged.txt 
```

在这里，h标签表示assume-unchanged.txt标有assumed-unchanged选项。

虽然主要用于该目的，但[assume-unchanged 选项绝不意味着忽略对跟踪文件的更改](https://github.com/git/git/commit/936d2c9301e41a84a374b98f92777e00d321a2ea)。它专为检查一组文件是否已被修改的情况而设计。如果我们想优化慢速文件系统上的资源使用会发生什么：git 忽略对目标文件的任何检查，并且不会比较其在工作目录和索引中的版本。

只要目标文件在索引中的条目发生更改，此功能就会丢失。当上游更改文件时可能会发生这种情况。要取消设置此选项，我们可以使用–no-assume-unchanged：

```vim
$ git update-index --no-assume-unchanged assumeunchanged.txt
```

## 3. skip-worktree选项有什么作用？

–skip-worktree选项忽略已跟踪文件中未提交的更改。无论对工作树进行任何修改，git 将始终使用暂存区中的文件内容和属性。当我们想要将本地更改添加到文件而不将它们推送到上游时，这很有用：

```sas
$ git update-index --skip-worktree skipworktree.txt
```

我们可以验证文件状态：

```powershell
$ git ls-files -v
$ S skipworktree.txt 
```

此处，S表示skip-worktree.txt标记有skip-worktree选项。

当文件在索引中更改时，此选项会自动取消设置，即，如果文件已在上游更改并且我们将其拉出。

–no-skip-worktree用于取消设置此选项。如果标记了错误的文件，或者如果情况发生变化并且不应再忽略以前跳过的文件，这将很有用：

```vim
$ git update-index --no-skip-worktree skipworktree.txt
```

## 4.选项之间的差异

### 4.1. 分支切换

当文件启用了–skip-worktree选项时，检出分支时没有问题。但是，–assume-unchanged会引发错误：

```vim
$ git checkout  another-branch
error: Your local changes to the following files would be overwritten by checkout:
        assumeunchanged.txt
Please commit your changes or stash them before you switch branches.
Aborting
```

我们可以取消设置选项来克服这种情况：

```vim
$ git update-index --no-assume-unchanged assumeunchanged.txt 
$ git checkout another-branch 
Switched to branch 'another-branch'
```

### 4.2. 优先级

–skip-worktree优先于 — assume-unchanged位，当两者都设置时。让我们尝试在文件上设置这两个选项：

```sas
$ git update-index --assume-unchanged --skip-work-tree worktree-assumeunchanged.txt
```

该文件的状态确认了skip-wortkree 优先级：

```powershell
$ git ls-files -v
$ S worktree-assumeunchanged.txt
```

## 5.总结

在本文中，我们讨论了 Git 的—assume-unchanged和 —skip -worktree选项的用法差异。我们还讨论了它们的优先级以及它们如何与本地和上游分支交互。
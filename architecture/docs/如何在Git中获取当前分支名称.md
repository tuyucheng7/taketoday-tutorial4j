## 1. 概述

[Git](https://www.baeldung.com/git-guide)已经成为业界流行和广泛使用的版本控制系统。通常，当我们使用 Git 存储库时，我们会使用分支。

在本教程中，我们将探讨如何获取当前正在处理的分支名称。

## 二、问题简介

首先，让我们准备一个名为myRepo的 Git 存储库：

```bash
$ git branch -a
* feature
  master
  remotes/origin/HEAD -> origin/master
  remotes/origin/master复制
```

正如 [git branch](https://www.baeldung.com/git-guide#11-git-branching)命令的输出所示，我们在myRepo中有两个本地分支，现在，当前签出的分支是feature分支，因为“ feature ”前面有一个“”字符。

有时，我们可能只想获取当前分支名称而不是整个分支列表。例如，如果我们在版本库中有很多本地分支，我们可能会得到一个很长的分支列表。或者，当我们编写一些脚本来自动化一些流程时，我们只想获取分支名称并将其传递给其他命令。

那么接下来，我们就来看看如何快速获取当前分支的“特征”。

## 3. 使用 git symbolic-ref 命令

当前分支信息由.git/HEAD文件存储或链接，具体取决于 Git 版本。例如，在这台机器上，.git/HEAD文件包含：

```bash
$ cat .git/HEAD
ref: refs/heads/feature
复制
```

因此，它指向一个符号 ref。 [git symbolic-ref](https://git-scm.com/docs/git-symbolic-ref)命令 允许我们读取和修改符号引用。我们可以使用此命令获取当前分支的简称：

```bash
$ git symbolic-ref --short HEAD
feature
复制
```

## 4. 使用git rev-parse命令

从 Git 1.7 版本开始，我们可以选择使用[git rev-parse](https://git-scm.com/docs/git-rev-parse)命令来获取当前分支名称：

```bash
$ git rev-parse --abbrev-ref HEAD
feature
复制
```

## 5. 使用 git name-rev命令

Git 的 [git name-rev](https://git-scm.com/docs/git-name-rev)命令可以找到给定转速的符号名称。因此，要获取当前分支名称，我们可以读取 rev HEAD的名称：

```bash
$ git name-rev --name-only HEAD
feature复制
```

如上面的输出所示，git name-rev命令也打印当前分支名称。我们应该注意到，我们 在命令中添加了–name-only选项以抑制输出中的rev 信息(HEAD) 。

## 6. 使用git branch命令

我们知道如果我们启动 不带任何选项的git branch命令，Git 将打印所有本地分支并将当前分支放在第一行，名称前面有一个“”字符：

```bash
$ git branch
* feature
  master
复制
```

因此，我们可以解析 git branch命令的输出来获取分支名称。让我们看一个结合git branch和 [sed](https://www.baeldung.com/linux/sed-editor)命令的例子：

```bash
$ git branch | sed -n '1{s/^* *//;p}'
feature
复制
```

实际上，从 2.22 版本开始，Git在 git branch命令中引入了–show-current选项 ，这样我们就可以直接获取当前分支名称：

```bash
$ git branch --show-current
feature
复制
```

显然，这个命令比解析git branch命令的输出要好。

## 七、总结

在这篇快速文章中，我们介绍了几种获取 Git 存储库中当前分支名称的方法。
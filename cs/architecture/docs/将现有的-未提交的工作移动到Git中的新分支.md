## 1. 概述

[Git](https://www.baeldung.com/git-guide)是当今非常流行的版本控制系统。

在本快速教程中，我们将探索如何将现有但未提交的更改移动到新分支。

## 二、问题简介

首先，让我们考虑一下向 Git 管理的项目添加新功能的典型工作流程：

-   创建一个新的功能分支，比如feature，然后切换到该分支
-   实现该功能并将其提交到我们的本地存储库
-   将功能分支推送到远程存储库并创建拉取请求
-   经过其他队友的审核后，新的更改可以合并到master或release 分支

然而，有时，我们已经开始进行更改，但忘记了创建一个新的功能分支并切换到它。因此，当我们要提交更改时，我们可能会意识到我们在错误的分支上——例如， master分支。

因此，我们需要新建一个特性分支，将未提交的工作移至新的分支。此外， 不应修改master分支。

一个示例可以快速解释这种情况。假设我们有一个名为myRepo的 Git 存储库：

```bash
$ git branch
* master

$ git status
On branch master
nothing to commit, working tree clean复制
```

正如我们从上面的输出中看到的，我们目前在master分支上。此外，工作树很干净。

接下来，让我们做一些改变：

```bash
$ git status
On branch master
Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
	modified:   Readme.md

Untracked files:
  (use "git add <file>..." to include in what will be committed)
	a-new-file.txt

no changes added to commit (use "git add" and/or "git commit -a")
复制
```

如上面的输出所示，我们添加了一个新文件a-new-file.txt并更改了Readme.md的内容。现在，我们意识到应该将工作提交到功能分支而不是主分支。

接下来，让我们看看如何将更改移动到新分支并保持 master不变。

## 3. 使用 git checkout 命令

git checkout -b <BranchName>命令将创建一个新分支并切换到它。此外，此命令将保持当前分支不变，并将所有未提交的更改带到新分支。

接下来，让我们 在 myRepo项目上测试git checkout命令：

```bash
$ git branch
* master

$ git co -b feature1
Switched to a new branch 'feature1'

$ git status
On branch feature1
Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
	modified:   Readme.md

Untracked files:
  (use "git add <file>..." to include in what will be committed)
	a-new-file.txt

no changes added to commit (use "git add" and/or "git commit -a")
复制
```

如上面的命令所示，我们已经创建了 feature1分支并将所有未提交的更改从 master移动到 feature1。接下来，让我们暂存并提交更改：

```bash
$ git add . && git commit -m'implemented feature1'
[feature1 2ffc161] implemented feature1
 2 files changed, 2 insertions(+)
 create mode 100644 a-new-file.txt

$ git log --abbrev-commit feature1
commit 2ffc161 (HEAD -> feature1)
Author: ...
Date:   ...
    implemented feature1

commit b009ddf (master)
Author: ...
Date:   ...
    init commit
复制
```

现在，让我们切换回 master分支并检查我们是否保持不变：

```bash
$ git checkout master
Switched to branch 'master'
$ git status
On branch master
nothing to commit, working tree clean
$ git log --abbrev-commit master
commit b009ddf (HEAD -> master)
Author: ...
Date:   ...
    init commit复制
```

正如我们在输出中看到的那样，master分支上没有本地更改。此外， master上也没有新的提交。

## 4.使用 git switch命令

正如我们所知，Git 的结帐命令就像一把瑞士军刀。同一个命令可以做很多不同种类的操作，比如恢复工作树文件、切换分支、创建分支、移动头部等等。checkout命令的使用非常多。

因此，Git 从 2.23 版本开始引入了[git switch](https://git-scm.com/docs/git-switch)命令，以消除checkout命令重载使用带来的一些困惑。顾名思义，git switch允许我们在分支之间切换。此外，我们可以使用 -C选项来创建一个新分支并一次性切换到它。它的工作原理与git checkout -b命令几乎相同 。

接下来，让我们对myRepo项目进行与git checkout -b相同的测试：

```bash
$ git branch
  feature1
* master

$ git status
On branch master
Changes not staged for commit:
  (use "git add/rm ...)
  (use "git restore ...)
	deleted:    Readme.md

Untracked files:
  (use "git add <file>..." to include in what will be committed)
	ReadmeNew.md

...
复制
```

正如我们在上面的输出中看到的，我们目前在 master分支上。这一次，我们删除了文件Readme.md并添加了一个新的ReadmeNew.md文件。

接下来，让我们使用 git switch命令将这些未提交的更改移动到一个名为 feature2的新分支：

```bash
$ git switch -C feature2
Switched to a new branch 'feature2'

$ git status
On branch feature2
Changes not staged for commit:
  (use "git add/rm ...)
  (use "git restore ...)
        deleted: Readme.md
Untracked files:
  (use "git add <file>..." to include in what will be committed)
        ReadmeNew.md
...
$ git add . && git commit -m 'feature2 is done'
[feature2 6cd5933] feature2 is done
1 file changed, 0 insertions(+), 0 deletions(-)
rename Readme.md => ReadmeNew.md (100%)
$ git log --abbrev-commit feature2
commit 6cd5933 (HEAD -> feature2)
Author: ...
Date:   ...
      feature2 is done
commit b009ddf (master)
Author: ...
Date:   ...
      init commit
复制
```

正如我们在上面的输出中看到的，git switch -C创建了一个新的分支feature2并将我们带到feature2。此外，所有未提交的更改都已从master移至feature2分支。然后，我们将更改提交到feature2分支。

接下来，让我们切换回 master分支并检查它是否未被修改：

```bash
$ git switch master
Switched to branch 'master'

$ git status
On branch master
nothing to commit, working tree clean
$ ls -1 Readme.md 
Readme.md

$ git log --abbrev-commit master
commit b009ddf (HEAD -> master)
Author: ...
Date:   ...
    init commit
复制
```

正如我们所见，在master分支上，我们之前对工作树文件所做的所有更改都已恢复。例如，删除的文件 Readme.md又回来了。此外， git log命令显示master上没有新的提交。

## 5.总结

在本文中，我们介绍了几种将未提交的更改移动到新 Git 分支的快速方法。这两个命令都非常简单易用：

-   git checkout -b <NEW_BRANCH>
-   git switch -C <NEW_BRANCH>
## 1. 概述

[Git](https://www.baeldung.com/git-guide)作为版本控制系统在业界得到了广泛的应用。此外，Git 分支是我们日常开发过程的一部分。

在本教程中，我们将探讨如何删除 Git 分支。

## 2. Git仓库的准备

为了更容易解决如何删除Git 分支，我们先准备一个Git 仓库作为例子。

首先，让我们从 GitHub 克隆myRepo存储库 ( https://github.com/sk1418/myRepo ) 进行测试：

```bash
$ git clone git@github.com:sk1418/myRepo.git
Cloning into 'myRepo'...
...
remote: Total 6 (delta 0), reused 3 (delta 0), pack-reused 0
Receiving objects: 100% (6/6), done复制
```

其次，我们进入本地的myRepo目录，查看分支：

```bash
$ git branch -a
* master
  remotes/origin/HEAD -> origin/master
  remotes/origin/master
复制
```

从上面的输出可以看出，目前， myRepo仓库中只有一个master 分支。此外，主分支是myRepo的默认分支。

接下来，让我们创建一些分支并展示如何在本地和远程删除分支。在本教程中，我们将专注于在命令行中删除分支。

## 3.删除本地分支

让我们首先看一下删除本地分支。

Git 的 [git branch](https://www.baeldung.com/git-guide#111-git-branch--manage-branches)命令有两个用于删除本地分支的选项：-d和-D。

接下来，让我们仔细看看它们，并通过一个例子来了解这两个选项之间的区别。

### 3.1. 使用-d选项删除本地分支

首先，让我们尝试创建一个本地分支：

```bash
$ git checkout -b feature
Switched to a new branch 'feature'复制
```

接下来，让我们使用-d选项删除功能分支：

```bash
$ git branch -d feature
error: Cannot delete branch 'feature' checked out at '/tmp/test/myRepo'复制
```

糟糕，正如我们所见，我们收到了一条错误消息。这是因为我们目前在 功能分支上：

```bash
$ git branch
* feature
  master复制
```

换句话说，我们不能删除当前签出的分支。所以，让我们切换到master分支并再次触发命令：

```bash
$ git checkout master
Switched to branch 'master'
Your branch is up to date with 'origin/master'.
$ git branch -d feature
Deleted branch feature (was 3aac499)

$ git branch -a
* master
remotes/origin/HEAD -> origin/master
remotes/origin/master复制
```

如我们所见，我们已经成功删除了本地特性分支。

### 3.2. 使用-D选项删除本地分支

首先，让我们再次创建功能分支。但是这一次，我们将进行一些更改并提交：

```bash
$ git checkout -b feature
Switched to a new branch 'feature'

# ... modify the README.md file ...
$ echo "new feature" >> README.md
$ git status
On branch feature
Changes not staged for commit:
...
	modified:   README.md

no changes added to commit (use "git add" and/or "git commit -a")

$ git ci -am'add "feature" to the readme'
[feature 4a87db9] add "feature" to the readme
 1 file changed, 1 insertion(+)复制
```

现在，如果我们仍然使用-d选项，Git 将拒绝删除功能分支：

```bash
$ git checkout master
Switched to branch 'master'
Your branch is up to date with 'origin/master'.

$ git branch -d feature
error: The branch 'feature' is not fully merged.
If you are sure you want to delete it, run 'git branch -D feature'.复制
```

这是因为要删除的分支(feature )在默认分支( master)之前：

```bash
$ git log --graph --abbrev-commit 
* commit 4a87db9 (HEAD -> feature)
| Author: ...
| Date:   ...| 
|     add "feature" to the readme
| 
* commit 3aac499 (origin/master, origin/HEAD, master)
| Author: ...
| Date:   ...| 
|     the first commit
| 
* commit e1ccb56
  Author: ...
  Date:   ...  
      Initial commit
复制
```

有两种方法可以解决这个问题。首先，我们可以将feature分支合并到master中，然后再次执行“ git branch -d feature ”。

但是，如果我们想丢弃未合并的提交，如错误信息所示，我们可以运行“ git branch -D feature ”来执行强制删除：

```bash
$ git branch -D feature
Deleted branch feature (was 4a87db9)

$ git branch -a
* master
remotes/origin/HEAD -> origin/master
remotes/origin/master.复制
```

### 3.3. git branch -d/-D不会删除远程分支

到目前为止，我们已经使用 带有-d和-D选项的git branch删除了一个本地分支。值得一提的是，无论我们使用-d还是 -D进行删除，该命令都只会删除本地分支。不会删除远程分支，即使删除的本地分支正在跟踪远程分支。

接下来，让我们通过一个例子来理解这一点。同样，让我们创建一个 功能分支，进行一些更改，然后将提交推送到远程存储库：

```bash
$ git checkout -b feature
Switched to a new branch 'feature'

# add a new file
$ echo "a wonderful new file" > wonderful.txt

$ git add . && git ci -am'add wonderful.txt'
[feature 2dd012d] add wonderful.txt
 1 file changed, 1 insertion(+)
 create mode 100644 wonderful.txt
$ git push
...
To github.com:sk1418/myRepo.git
 * [new branch]      feature -> feature复制
```

如上面的输出所示，我们在功能分支上创建了一个新文件wonderful.txt，并将提交推送到远程存储库。

因此，本地 特性分支正在跟踪远程特性分支：

```bash
$ git remote show origin | grep feature
    feature tracked
    feature pushes to feature (up to date)复制
```

由于我们还没有将feature 合并到 master中，让我们使用-D选项删除本地 feature 分支 ：

```bash
$ git checkout master
Switched to branch 'master'
Your branch is up to date with 'origin/master'.

$ git branch -D feature
Deleted branch feature (was 2dd012d).

$ git branch -a
* master
  remotes/origin/HEAD -> origin/master
  remotes/origin/feature
  remotes/origin/master复制
```

正如我们在命令git branch -a的输出中看到的那样，本地功能分支已经消失。但是/remotes/origin/feature分支没有被删除。

现在，如果我们再次检查功能分支，我们所做的更改仍然存在：

```bash
$ git checkout feature
Switched to branch 'feature'
Your branch is up to date with 'origin/feature'.
$ cat wonderful.txt 
a wonderful new file
复制
```

接下来，让我们看看如何删除远程分支。

## 4.删除远程分支

如果我们的 Git 版本在 1.7.0 之前，我们可以使用命令git push origin :<branchName>来删除远程分支。但是，这个命令看起来不像是删除操作。因此，从1.7.0版本开始，Git引入了git push origin -d <branchName>命令来删除远程分支。显然，这个命令比旧版本更容易理解和记忆。

刚才，我们删除了本地分支feature，我们看到远程feature分支还在。所以现在，让我们使用提到的命令删除远程功能分支。

在我们删除远程 功能之前，让我们先创建一个本地 功能分支来跟踪远程功能。这是因为我们要检查删除远程分支是否会影响本地分支的跟踪：

```bash
$ git checkout feature 
branch 'feature' set up to track 'origin/feature'.
Switched to a new branch 'feature'
$ git branch -a
* feature
  master
  remotes/origin/HEAD -> origin/master
  remotes/origin/feature
  remotes/origin/master复制
```

所以，现在我们有了本地和远程功能分支。此外，我们目前在本地 功能分支上。

接下来，让我们删除远程功能分支：

```bash
$ git push origin -d feature
To github.com:sk1418/myRepo.git
 - [deleted]         feature
$ git branch -a
* feature
  master
  remotes/origin/HEAD -> origin/master
  remotes/origin/master
复制
```

可以看到，我们执行 git push -d feature命令后，远程的 feature分支已经被删除了。但是，本地功能分支仍然存在。也就是说，删除远程分支不会影响本地跟踪分支。因此，如果我们现在启动 git push，本地 特性分支将再次被推送到远程。

而且，与删除本地分支不同的是，我们可以删除远程分支，而不管我们当前在哪个本地分支上工作。在上面的示例中，我们在本地功能分支上，但我们仍然可以毫无问题地删除远程功能分支。

## 5.总结

在本文中，我们探讨了如何使用命令删除 Git 的本地和远程分支。

让我们快速总结一下：

-   删除本地分支：git branch -d/-D <branchName>(-D选项是强制删除)
-   删除远程分支：git push origin -d <branchName>或git push origin :<branchName>

此外，我们了解到，删除本地或远程分支不会影响另一端的分支。
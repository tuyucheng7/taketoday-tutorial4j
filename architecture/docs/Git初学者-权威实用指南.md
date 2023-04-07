## 1. 概述

在本教程中，我们将讨论使用 Git 时最常用的命令。

我们将从安装和配置开始，然后创建我们的第一个本地存储库。接下来，我们将学习如何提交更改并将它们与远程存储库同步。

此外，我们将讨论分支并学习一些高级技术，例如修改提交和操纵提交历史记录。

## 2. 什么是 Git？

Git 是一个版本控制系统 (VCS)，允许在不覆盖以前的快照的情况下保存和跟踪文件随时间的变化。它可以帮助开发人员一起协作处理项目。

与其主要竞争对手[SVN](https://www.baeldung.com/cs/git-vs-svn)不同，Git 还实现了分布式工作流系统。这意味着每个使用 Git 的开发人员都拥有整个存储库的本地副本。Git 还允许在不与中央存储库持续连接的情况下异步工作。

## 3.Git安装

我们可以 在 Windows、Mac 和 Linux 等最常见的操作系统上[安装 Git 。](https://git-scm.com/downloads)事实上，在大多数 Mac 和 Linux 机器上，Git 都是默认安装的。

要查看我们是否已经安装了 Git，让我们打开一个终端并执行：

```bash
$ git version
git version 2.24.3 (Apple Git-128)
```

[此外，Git 带有用于提交 ( git-gui](https://git-scm.com/docs/git-gui) ) 和浏览 ( [gitk](https://git-scm.com/docs/gitk) )的内置 GUI 工具。还有很多[第三方工具](https://git-scm.com/downloads/guis) 或 IDE 插件可以增强体验。

## 4. git help – 一个方便的手册

在我们创建我们的第一个存储库之前，让我们运行[git help](https://git-scm.com/docs/git-help)命令。它显示有关 Git 本身的有用信息：

```bash
$ git help
usage: git [--version] [--help] [-C <path>] [-c <name>=<value>]
           [--exec-path[=<path>]] [--html-path] [--man-path] [--info-path]
           [-p | --paginate | -P | --no-pager] [--no-replace-objects] [--bare]
           [--git-dir=<path>] [--work-tree=<path>] [--namespace=<name>]
           <command> [<args>]
...
```

我们还可以通过多种方式查看特定命令的手册：

```bash
$ git --help init
$ git help init
$ git init --help
```

上面的所有三个变体都返回相同的输出。

使用-g选项，我们还可以访问内部指南列表来发展我们的技能：

```shell
$ git help -g
The common Git guides are:
   attributes          Defining attributes per path
   cli                 Git command-line interface and conventions
   core-tutorial       A Git core tutorial for developers
...
$ git help core-tutorial
```

要打印教程，我们需要提供它的名称作为参数。

## 5. git config – 配置Git

安装 Git 后，我们可以使用[git config](https://git-scm.com/docs/git-config)命令轻松配置它，该命令允许管理选项。

Git 支持不同级别的选项，如系统、全局、本地、工作树或文件。

虽然系统设置是系统范围的并且适用于每个用户及其在系统上的所有存储库，但全局级别是指用户特定的设置。

本地配置特定于单个存储库，当我们不向 git config 命令传递任何选项时，它是 Git 使用的默认级别。

工作 树和 文件级别是更高级的配置级别，可以应用于存储库中的单个分支或文件。

此外， Git 通过首先检查本地级别来解析选项的有效值，如果未设置选项，则继续直到系统级别。

例如，让我们配置在提交历史中使用的用户名：

```bash
$ git config --global user.name "Baeldung User"
```

我们刚刚在全球树立了自己的名字。

要覆盖单个存储库的选项，我们可以在其目录中使用–local标志。

要打印有效选项列表，我们使用：

```bash
$ git config -l
user.name=Baeldung User
```

我们可以执行git –help config命令来获取有关所有可用选项的详细信息。

## 6. 创建存储库

接下来，我们需要创建一个存储库。为此，我们有两种选择—— 可以从头开始在本地创建一个新的存储库，也可以克隆一个现有的存储库。

### 6.1. git init – 初始化一个新的仓库

如果我们决定初始化一个新的仓库，我们需要使用[git init](https://git-scm.com/docs/git-init)命令。它将当前目录转换为 Git 存储库并开始跟踪其内容：

```bash
$ mkdir simple-repo; cd simple-repo; git init
Initialized empty Git repository in /simple-repo/.git/
```

Git 还在其中创建了一个名为.git的隐藏目录 。这个目录存储了 Git 创建和使用的所有对象和[引用](https://git-scm.com/book/en/v2/Git-Internals-Git-References)，作为我们项目历史的一部分。这些文件是在提交期间创建的，并指向我们文件的特定修订版。

在那之后，在大多数情况下，我们希望将我们已经创建的存储库与远程存储库连接起来。我们使用[git remote](https://git-scm.com/docs/git-remote)命令来管理当前存储库的远程链接：

```bash
$ git remote add origin https://github.com/eugenp/tutorials.git
```

我们刚刚添加了一个名为origin的新远程并将其连接到官方 Baeldung GitHub 存储库。

### 6.2. git clone – 克隆外部存储库

有时我们想为现有的存储库做贡献。首先，我们需要在本地下载现有的存储库。

[git clone](https://git-scm.com/docs/git-clone)命令将存储库克隆到一个新目录中：

```bash
$ git clone https://github.com/eugenp/tutorials.git
Cloning into 'repo'...
```

完成后，创建的新目录包含项目的所有文件、分支和历史记录。

此外，克隆的存储库已配置并与外部源连接：

```bash
$ cd tutorials
$ git remote -v
origin	https://github.com/eugenp/tutorials.git (fetch)
origin	https://github.com/eugenp/tutorials.git (push)
```

Git 将使用这些原始链接来管理任何进一步的更改。

## 7. Git 工作流程

在我们配置了我们的本地存储库之后，我们准备好应用第一个更改。但在我们这样做之前，让我们检查一下 Git 如何跟踪这些更改。

我们的本地存储库由 Git 维护的三个不同的树组成。

第一个是工作目录，它包含文件的实际版本。

对文件进行更改后，我们可以将文件移动到充当临时区域的索引中。我们使用git add命令执行此操作。Index中的文件开始被 Git 跟踪。

最后，我们可以使用git commit命令将我们的更改应用并保存到本地存储库中。提交更改会更新存储库的 HEAD，它始终指向我们所做的最后一次提交。

这三个步骤用于维护本地更改。但正如我们所知，存储库也可能包含外部源。最后一步是同步两个存储库并发布我们的更改。

## [![工作流程](https://www.baeldung.com/wp-content/uploads/2021/11/git_workflow.png)](https://www.baeldung.com/wp-content/uploads/2021/11/git_workflow.png)8. 做出改变

现在我们知道 Git 的跟踪系统是如何工作的，我们准备将我们的第一个更改应用到我们的存储库。

### 8.1. git status – 显示当前更改

让我们创建一个简单的文件并将其添加到我们的存储库中。之后，我们执行[git status](https://git-scm.com/docs/git-status)命令并分析其输出：

```bash
$ "Hello World" >> myfile.txt
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Untracked files:
  (use "git add <file>..." to include in what will be committed)
	myfile.txt

nothing added to commit but untracked files present (use "git add" to track)
```

该命令打印我们更改的当前详细状态。第一部分显示本地和远程分支是否同步。

接下来，输出显示工作树的状态——当前修改的文件列表及其维护状态。如我们所见，myfile.txt文件当前位于工作目录区域中，并且未被 Git 跟踪。

### 8.2. git add——跟踪变化

要开始跟踪更改，我们需要 使用 [git add](https://git-scm.com/docs/git-add)命令将它们移动到索引：

```bash
$ git add myfile.txt
$ git stage *
```

我们可以通过用空格分隔它们来一次指定多个文件。我们还可以使用星号指定所有文件。

或者，我们也可以使用[git stage](https://git-scm.com/docs/git-stage)命令，它是git add命令的同义词。

现在让我们验证状态：

```bash
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
	new file:   myfile.txt复制
```

正如我们所见，Git 已经开始跟踪我们的文件。

### 8.3. git restore和gitignore – 取消跟踪更改

Git 允许从索引中删除文件。如果我们错误地将我们的更改移入其中并想暂时禁用跟踪它们，我们使用[git restore](https://git-scm.com/docs/git-restore)：

```bash
$ git restore -S myfile.txt
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Untracked files:
  (use "git add <file>..." to include in what will be committed)
	myfile.txt
```

我们刚刚将我们的文件再次移动到工作区 并将其排除在进一步提交之外，直到我们再次暂存它。-S ( –staged )标志告诉 Git 只恢复存储库的索引。

我们还可以永久排除文件并禁止跟踪它们。为此，我们需要创建一个[.gitignore](https://git-scm.com/docs/gitignore)文件。此文件包含文件名模式，并应用于当前目录及其子目录中的所有文件。任何进一步的添加操作将忽略与这些模式匹配的文件。

### 8.4. git commit – 保存更改

让我们恢复最后的更改并将我们的文件再次移动到暂存区：

```
$ git add myfile.txt
```

现在，是时候保存我们的工作了，所以我们需要提交。

commit 是一个 Git 对象，它就像是我们存储库在特定时间的快照。

要提交更改，让我们使用[git commit](https://git-scm.com/docs/git-commit)命令：

```bash
$ git commit -m "My first commit"
[master 8451901] My first commit
 1 file changed, 1 insertion(+)
 create mode 100644 myfile.txt
```

我们刚刚在本地创建了第一个提交。

git commit命令包含许多附加选项来执行更复杂的操作，我们可以使用git commit –help命令检查这些操作。

最有用的是-m标志，它指定描述当前快照中所做更改的提交消息。

最后，让我们检查一下状态：

```java
$ git status
On branch master
Your branch is ahead of 'origin/master' by 1 commit.
  (use "git push" to publish your local commits)

nothing to commit, working tree clean
```

现在，我们的工作树不包含任何其他更改，但本地存储库包含比其外部源更多的提交。因此，要发布我们的更改，我们应该将本地更改与源同步。

### 8.5. git log & git show——检查提交

一旦我们创建了提交，我们就可以检查它的细节。提交包括许多额外的元数据，如作者、时间戳等。

要打印当前分支的提交列表，我们使用[git log](https://git-scm.com/docs/git-log)命令：

```bash
$ git log
commit 845190154ed7a491a6143669c4ce88058fb93f8a (HEAD -> master)
Author: ...
Date: ...

    My first commit

commit 9a1e11ec981b41e4b4b9c245a7a96cd6707f4705 (origin/master, origin/HEAD)
...
```

该列表默认以时间倒序显示当前分支的提交历史。

每个条目都包含一般元数据，如提交的 ID(唯一的 SHA-1 校验和)、作者、日期和给定的消息。

当我们想要更深入地了解单个提交时，我们使用 [git show](https://git-scm.com/docs/git-show)命令打印其详细信息，后跟请求的提交 ID：

```bash
$ git show 845190154ed7a491a6143669c4ce88058fb93f8a
commit 845190154ed7a491a6143669c4ce88058fb93f8a (HEAD -> master)
Author: ...
Date:...

    My first commit

diff --git a/myfile.txt b/myfile.txt
new file mode 100644
index 0000000..557db03
--- /dev/null
+++ b/myfile.txt
@@ -0,0 +1 @@
+Hello World
```

这一次，输出还显示了提交与之前使用[git diff](https://git-scm.com/docs/git-diff)命令生成的快照之间的差异。

### 8.6. git stash – 搁置更改

[git stash](https://git-scm.com/docs/git-stash)命令暂时搁置我们所做的更改，恢复工作目录以匹配HEAD提交。这使我们能够快速切换上下文并开始处理其他事情。

让我们创建另一个文件并将其添加到暂存区。之后，让我们执行git stash：

```shell
$ touch myfile-2.txt; git add *
$ git stash push
Saved working directory and index state WIP on master: 8451901 My first commit
```

现在，让我们尝试列出文件：

```
$ ls myfile-2.txt
ls: myfile-2.txt: No such file or directory
```

我们可以看到现在文件不存在。这是因为所有挂起的更改都已从工作目录中删除并保存在存储中。

我们可以使用列表选项打印所有隐藏的修改：

```bash
$ git stash list
stash@{0}: WIP on master: 8451901 My first commit
```

由于我们没有提供它的描述，因此默认情况下，存储在…上列为 WIP。我们可以使用命令行上的-m标志将默认值更改为更具描述性的消息。

要检查其详细信息，我们使用show选项：

```shell
$ git stash show
 myfile-2.txt | 0
 1 file changed, 0 insertions(+), 0 deletions(-)
```

输出打印有关存储在最新存储中的更改的信息。

最后，如果我们想恢复更改，我们使用pop选项：

```bash
$ git stash pop
...
$ ls myfile-2.txt 
myfile-2.txt

```

我们刚刚从隐藏列表中删除了一个隐藏状态，并将其应用到当前状态之上。

## 9. 操纵提交历史

现在我们已经了解了如何在存储库中保存更改，让我们修改以前保存的提交。在以下部分中，我们将介绍最常见的用例。

### 9.1. git commit –amend – 添加对提交的额外更改

假设我们在提交更改时忘记包含一个文件。当然，我们可以在上一个提交之上创建另一个提交，但这可能会使更改历史变得混乱。

在这种情况下，我们可能希望 Git 重写我们的最后一次提交并包含我们使用amend选项忘记的文件。

让我们回顾一下最后一次提交：

```sql
$ git show --summary
commit 845190154ed7a491a6143669c4ce88058fb93f8a (HEAD -> master)
Author: ...
Date: ...

    My first commit

 create mode 100644 myfile.txt

```

让我们的my-file2.txt从存储中弹出，让我们使用修改选项提交它：

```sql
$ git commit --amend
[master 0ed9f03] My first commit
 2 files changed, 1 insertion(+)
 create mode 100644 myfile-2.txt
 create mode 100644 myfile.txt
```

我们可以注意到 Git 将文件添加到我们最后一次合并更改的提交中。

### 9.2. git rebase – 重新应用提交

一种更高级的修改提交的技术是通过[git ](https://git-scm.com/docs/git-rebase)[rebase](https://git-scm.com/docs/git-rebase) 命令。它在另一个基础上重新应用历史记录中的提交，允许我们即时更改它们。

让我们在我们的存储库中创建另一个提交：

```bash
$ touch myfile-3.txt
$ git add *
$ git commit -m "My second commit"
```

现在，我们应该有两个单独的提交——我的第一次提交和 我的第二次提交。

让我们开始以交互模式 rebase 两个提交：

```bash
git rebase -i HEAD~2复制
```

这将打开一个编辑器，我们可以在其中使用命令操作历史记录：

```bash
pick 82d8635 My first commit
pick 6d58108 My second commit

# Rebase 9a1e11e..82d8635 onto 9a1e11e (2 commands)
#
# Commands:
# p, pick <commit> = use commit
# r, reword <commit> = use commit, but edit the commit message
# e, edit <commit> = use commit, but stop for amending
# s, squash <commit> = use commit, but meld into previous commit
# d, drop <commit> = remove commit 
...
```

在顶部，我们有变基提交列表，后面是手册。我们这里有很多选择。我们可以通过交换行来更改顺序，或者改写提交消息，或者将它们压缩为一个，编辑，甚至 删除单个提交。指令行将从上到下应用。

### 9.3. git reset – 回滚到特定状态

有时，我们可能希望放弃当前状态并恢复到历史快照。为此，我们使用[git reset](https://git-scm.com/docs/git-reset)选项：

```bash
$ git reset 82d8635复制
```

它在指定的提交之后撤消所有提交，在本地保留更改并将它们移动到暂存区。但是，如果我们想放弃所有工作更改，我们可以使用–hard标志。

## 10.同步存储库

到目前为止，在本地存储库上工作之后，终于可以发布我们的更改了。

在上传它们之前，我们应该始终将我们的本地副本与远程同步，以避免在发布过程中发生冲突。

### 10.1. git fetch – 更新引用

当我们实施我们的更改时，其他人可能已将更改发布到同一分支。所以我们应该检查它们并将它们与我们的本地存储库同步。

[git fetch](https://git-scm.com/docs/git-fetch)命令可以帮助我们做到这一点：

```bash
$ git fetch复制
```

这将从原始存储库下载对象和引用。

我们应该注意到这个动作永远不会修改当前的工作树。这是因为我们只能检查我们存储库的更新提交历史记录。如果我们发现任何未决的变化，我们必须更进一步。

### 10.2. git merge – 应用传入的更改

在发布代码之前，我们必须合并同一分支上的任何传入更改。如果我们不这样做，发布过程可能会失败。

让我们更新我们的分支：

```bash
$ git merge origin/master复制
```

[git merge](https://git-scm.com/docs/git-merge)命令 是一个非常强大的工具。它从给定的引用中下载所有新的更改，并将它们与当前的工作树结合起来，选择正确的合并策略。许多更改将自动应用，即使对相同文件存在修改也是如此。

但有时，没有简单的方法来合并更改。在那种情况下，我们有一个合并冲突，我们必须在继续之前手动解决它。我们需要编辑失败的文件，准备最终版本，并提交更改。

### 10.3. git pull – 立即更新和应用

[git pull](https://git-scm.com/docs/git-pull)命令 无非是将git fetch和 git merge合二为一：

```
$ git pull origin/master
```

它检查给定分支的最新更改并将它们与当前分支合并，其方式与git fetch和git merge相同。这是更新当前分支的最常见方式。

此外，拉取更改可能还需要额外的手动操作来解决合并冲突。

### 10.4. git push——发布本地提交

一旦我们同步了我们的本地存储库并修复了未决的合并冲突，我们终于准备好发布我们的提交了。我们需要选择远程目标和本地分支。

让我们执行[git push](https://git-scm.com/docs/git-push)命令：

```
$ git push origin master
```

这会使用所有本地提交更新远程存储库的master分支。

最后，我们查看历史：

```bash
$ git log
commit 6d5810884c3ce63ca08084959e3a21405a1187df (HEAD -> master, origin/master, origin/HEAD)
Author: ...
Date: ...
    My second commit
```

我们完成了！我们刚刚将更改发送到远程存储库。

## 11.Git 分支

现在，我们来谈谈分支。以前，我们有意跳过任何分支操作。所有更改都在master分支上完成，这是每个 Git 存储库的默认分支。

分支用于开发相互隔离的特性。我们使用其他分支进行开发，完成后合并回master分支。

### 11.1. git branch – 管理分支

git [branch](https://git-scm.com/docs/git-branch)帮助我们管理分支。要创建一个新的，我们只需指定它的名称：

```bash
$ git branch new-branch复制
```

在我们将其推送到远程存储库之前，其他人无法使用本地分支。

我们现在可以通过列出所有分支来查看新创建的分支：

```bash
$ git branch --list --all
* master
  new-branch
  remotes/origin/HEAD -> origin/master
  remotes/origin/master
```

如果我们要删除本地分支，我们执行：

```
$ git branch -d new-branch
```

### 11.2. git checkout – 更改当前分支

如果我们想切换当前分支，我们使用[git checkout](https://git-scm.com/docs/git-checkout)或[git switch](https://git-scm.com/docs/git-switch)函数：

```bash
$ git switch new-branch
Switched to branch 'new-branch'
$ git checkout master
Switched to branch 'master'
```

我们刚刚从master更改为新分支，然后使用这两个命令再次回到master 。

尽管两者的工作方式相似，但git switch命令仅允许切换分支。相比之下，git checkout是一个更复杂的命令，使我们能够额外管理工作树文件、重置分支或将文件恢复到特定版本。

## 12.总结

在本文中，我们涵盖了所有 Git 基础知识，并讨论了每个开发人员在使用 Git 时应该了解的大多数常见操作。通过实例，我们学习了如何使用这个版本控制系统。

我们从安装和配置 Git 开始，然后创建了第一个存储库。之后，我们做了一些修改，学习了如何修改提交历史。最后，我们通过同步两个存储库发布了更改，并学习了如何使用 Git 分支。
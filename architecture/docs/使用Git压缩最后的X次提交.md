## 1. 概述

当我们谈论[Git](https://git-scm.com/)工作流时，我们经常会听到“squash”这个词。

在本教程中，我们将简要介绍什么是 Git 压缩。然后我们将讨论何时需要压缩提交。最后，我们将仔细研究如何压缩提交。

## 2. 什么是 Git Squashing？

当我们在 Git 中说“squash”时，它的意思是将多个连续的提交合并为一个。

让我们看一个例子：

```bash
          ┌───┐      ┌───┐     ┌───┐      ┌───┐
    ...   │ A │◄─────┤ B │◄────┤ C │◄─────┤ D │
          └───┘      └───┘     └───┘      └───┘

 After Squashing commits B, C, and D:

          ┌───┐      ┌───┐
    ...   │ A │◄─────┤ E │
          └───┘      └───┘

          ( The commit E includes the changes in B, C, and D.)
复制
```

在此示例中，我们将提交 B、C 和 D 压缩为 E。

接下来，我们将讨论何时应该压缩提交。

## 3. 什么时候压缩提交？

简而言之，我们使用压缩来保持分支图的清洁。

让我们想象一下我们如何实现一个新功能。通常，我们会多次提交才能获得满意的结果，例如一些修复和测试。

但是，当我们实现该功能时，那些中间提交看起来是多余的。因此，我们可能希望将我们的提交压缩为一个。

我们想要压缩提交的另一个常见场景是合并分支。

很可能，当我们开始研究新功能时，我们将启动一个功能分支。假设我们在功能分支中完成了 20 次提交。

因此，当我们将 feature 分支合并到 master 分支时，我们想要进行压缩以将 20 个提交合并为一个。这样，我们就可以保持 master 分支的清洁。

## 4. 如何压缩提交？

如今，一些现代 IDE，例如[IntelliJ](https://www.jetbrains.com/idea/)和[Eclipse](https://www.eclipse.org/)，已经集成了对常见 Git 操作的支持。这允许我们压缩来自 GUI 的提交。

例如，在 IntelliJ 中，我们可以选择要压缩的提交，然后在右键单击上下文菜单中选择“压缩提交”：

[![理解](https://www.baeldung.com/wp-content/uploads/2021/08/intellij_git.png)](https://www.baeldung.com/wp-content/uploads/2021/08/intellij_git.png)

但是，在本教程中，我们将专注于使用 Git 命令进行压缩。

我们应该注意到，squash 不是 Git 命令，即使它是一个常见的 Git 操作。也就是说，“ git squash ... ”是一个无效的 Git 命令。

我们将讨论压缩提交的两种不同方法：

-   交互式变基：[git rebase](https://git-scm.com/docs/git-rebase) -i …
-   使用–squash选项合并：[git merge](https://git-scm.com/docs/git-merge) –squash

接下来，让我们看看它们的实际效果。

## 5. Interactive Rebase 压缩

在我们开始之前，让我们创建一个 Git[别名](https://git-scm.com/book/en/v2/Git-Basics-Git-Aliases) slog(代表短日志)以在紧凑视图中显示 Git 提交日志：

```bash
git config --global alias.slog = log --graph --all --topo-order --pretty='format:%h %ai %s%d (%an)'
复制
```

我们准备了一个 Git 存储库作为示例：

```bash
$ git slog
* ac7dd5f 2021-08-23 23:29:15 +0200 Commit D (HEAD -> master) (Kai Yuan)
* 5de0b6f 2021-08-23 23:29:08 +0200 Commit C (Kai Yuan)
* 54a204d 2021-08-23 23:29:02 +0200 Commit B (Kai Yuan)
* c407062 2021-08-23 23:28:56 +0200 Commit A (Kai Yuan)
* 29976c5 2021-08-23 23:28:33 +0200 BugFix #1 (Kai Yuan)
* 34fbfeb 2021-08-23 23:28:19 +0200 Feature1 implemented (Kai Yuan)
* cbd350d 2021-08-23 23:26:19 +0200 Init commit (Kai Yuan)复制
```

Git 的交互式 rebase 将在默认编辑器中列出所有相关的提交。在这种情况下，这些是我们想要压缩的提交。

然后我们可以根据需要控制每个提交和提交消息，并将更改保存在编辑器中。

接下来，让我们压缩最后四次提交。

值得一提的是，当我们说“最后X次提交”时，我们指的是来自HEAD的最后X次提交。

因此，在这种情况下，这些是最后四次提交：

```bash
* ac7dd5f ... Commit D (HEAD -> master)
* 5de0b6f ... Commit C 
* 54a204d ... Commit B 
* c407062 ... Commit A复制
```

此外，如果我们压缩了已经推送的提交，并且我们想发布压缩后的结果，我们必须进行强制推送。

值得一提的是，强制推送到公共存储库可能是一项危险的操作，因为它可能会覆盖其他人的提交。

此外，当我们真的要强制推送时，我们应该确保只强制推送所需的分支。

例如，我们可以将push.default属性设置为current ，这样只有当前分支才会被推送/强制推送到远程存储库。

或者，我们可以通过在要推送的refspec前面添加“+”来强制推送到一个分支。例如，git push origin +feature 将强制推送到功能分支。

### 5.1. 压缩最后的X次提交

下面是使用交互式变基压缩最后X 次提交的语法：

```bash
git rebase -i HEAD~[X]复制
```

所以，这是我们应该运行的：

```bash
git rebase -i HEAD~4复制
```

执行命令后，Git 将启动系统默认编辑器(本例中为 Vim 编辑器)，其中包含我们要压缩的提交和交互式 rebase 帮助信息：

[![狐狸](https://www.baeldung.com/wp-content/uploads/2021/08/rebase.png)](https://www.baeldung.com/wp-content/uploads/2021/08/rebase.png)

正如我们在上面的屏幕截图中看到的那样，我们想要压缩的所有四个提交都在编辑器中使用pick命令列出 。

在随后的注释行中有关于如何控制每个提交和提交消息的详细指南。

例如，我们可以将提交的pick命令改为s或squash 来压缩它们：

[![狐狸 1](https://www.baeldung.com/wp-content/uploads/2021/08/rebase-1.png)](https://www.baeldung.com/wp-content/uploads/2021/08/rebase-1.png)

如果我们保存更改并退出编辑器，Git 将按照我们的说明进行变基：

```bash
$ git rebase -i HEAD~4
[detached HEAD f9a9cd5] Commit A
 Date: Mon Aug 23 23:28:56 2021 +0200
 1 file changed, 1 insertion(+), 1 deletion(-)
Successfully rebased and updated refs/heads/master.复制
```

现在，如果我们再次检查 Git 提交日志，将会看到以下内容：

```bash
$ git slog
* f9a9cd5 2021-08-23 23:28:56 +0200 Commit A (HEAD -> master) (Kai Yuan)
* 29976c5 2021-08-23 23:28:33 +0200 BugFix #1 (Kai Yuan)
* 34fbfeb 2021-08-23 23:28:19 +0200 Feature1 implemented (Kai Yuan)
* cbd350d 2021-08-23 23:26:19 +0200 Init commit (Kai Yuan)复制
```

如 slog输出所示，我们已将最后四次提交压缩为一个新提交f9a9cd5 。

现在，如果我们查看提交的完整日志，我们可以看到所有压缩提交的消息都合并在一起：

```bash
$ git log -1
commit f9a9cd50a0d11b6312ba4e6308698bea46e10cf1 (HEAD -> master)
Author: Kai Yuan
Date:   2021-08-23 23:28:56 +0200

    Commit A
    
    Commit B
    
    Commit C
    
    Commit D
复制
```

### 5.2. 当X比较大时

我们已经了解到，命令git rebase -i HEAD~X 可以非常直接地压缩最后的X次提交。

但是，当我们的分支中有很多提交时，计算更大的X数可能会很痛苦。此外，它容易出错。

当X不好统计的时候，我们可以找到我们想要 rebase 的 commit hash “onto” 并运行命令git rebase -i hash_onto。

让我们看看它是如何工作的：

```bash
$ git slog
e7cb693 2021-08-24 15:00:56 +0200 Commit F (HEAD -> master) (Kai Yuan)
2c1aa63 2021-08-24 15:00:45 +0200 Commit E (Kai Yuan)
ac7dd5f 2021-08-23 23:29:15 +0200 Commit D (Kai Yuan)
5de0b6f 2021-08-23 23:29:08 +0200 Commit C (Kai Yuan)
54a204d 2021-08-23 23:29:02 +0200 Commit B (Kai Yuan)
c407062 2021-08-23 23:28:56 +0200 Commit A (Kai Yuan)
29976c5 2021-08-23 23:28:33 +0200 BugFix #1 (Kai Yuan)
34fbfeb 2021-08-23 23:28:19 +0200 Feature1 implemented (Kai Yuan)
cbd350d 2021-08-23 23:26:19 +0200 Init commit (Kai Yuan)复制
```

正如 git slog所示，在这个分支中，我们有一些提交。

现在假设我们想压缩所有提交并使用消息重新定位到提交29976c5 ： BugFix #1。

因此，我们不必计算需要压缩多少次提交。相反，我们可以只执行命令 git rebase -i 29976c5。

我们了解到，我们需要 在编辑器中将pick命令更改为squash ，Git 将按照我们的预期进行压缩：

```bash
$ git rebase -i 29976c5
[detached HEAD aabf37e] Commit A
 Date: Mon Aug 23 23:28:56 2021 +0200
 1 file changed, 1 insertion(+), 1 deletion(-)
Successfully rebased and updated refs/heads/master.

$ git slog
* aabf37e 2021-08-23 23:28:56 +0200 Commit A (HEAD -> master) (Kai Yuan)
* 29976c5 2021-08-23 23:28:33 +0200 BugFix #1 (Kai Yuan)
* 34fbfeb 2021-08-23 23:28:19 +0200 Feature1 implemented (Kai Yuan)
* cbd350d 2021-08-23 23:26:19 +0200 Init commit (Kai Yuan)复制
```

## 6. 通过与–squash选项合并来压缩

我们已经了解了如何使用 Git 交互式变基来压缩提交。这可以有效地清理分支中的提交图。

但是，有时我们在处理功能分支时会在功能分支中进行多次提交。在我们开发完功能后，我们通常希望将功能分支合并到主分支，比如“master”。

我们希望保持主分支图干净，例如，一项功能，一项提交。但是我们不关心我们的功能分支中有多少次提交。

在这种情况下，我们可以使用 commit git merge –squash命令来实现。

让我们通过一个例子来理解它：

```bash
$ git slog
* 0ff435a 2021-08-24 15:28:07 +0200 finally, it works. phew! (HEAD -> feature) (Kai Yuan)
* cb5fc72 2021-08-24 15:27:47 +0200 fix a typo (Kai Yuan)
* 251f01c 2021-08-24 15:27:38 +0200 fix a bug (Kai Yuan)
* e8e53d7 2021-08-24 15:27:13 +0200 implement Feature2 (Kai Yuan)
| * 204b03f 2021-08-24 15:30:29 +0200 Urgent HotFix2 (master) (Kai Yuan)
| * 8a58dd4 2021-08-24 15:30:15 +0200 Urgent HotFix1 (Kai Yuan)
|/  
* 172d2ed 2021-08-23 23:28:56 +0200 BugFix #2 (Kai Yuan)
* 29976c5 2021-08-23 23:28:33 +0200 BugFix #1 (Kai Yuan)
* 34fbfeb 2021-08-23 23:28:19 +0200 Feature1 implemented (Kai Yuan)
* cbd350d 2021-08-23 23:26:19 +0200 Init commit (Kai Yuan)复制
```

如上面的输出所示，在这个 Git 存储库中，我们在功能 分支中实现了“Feature2”。

在我们的功能分支中，我们进行了四次提交。

现在我们想通过一次提交将结果合并回master分支，以保持master分支的清洁：

```bash
$ git checkout master
Switched to branch 'master'

$ git merge --squash feature
Squash commit -- not updating HEAD
Automatic merge went well; stopped before committing as requested复制
```

与常规合并不同，当我们执行带有–squash选项的git merge命令时，Git 不会自动创建合并提交。

相反，它将源分支(本场景中的功能分支)的所有更改转换为工作副本中的本地更改：

```bash
$ git status
On branch master
Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
	modified:   readme.md复制
```

在此示例中，“Feature2”的所有更改都是关于readme.md文件的。

我们需要提交更改以完成合并：

```bash
$ git commit -am'Squashed and merged the Feature2 branch'
[master 565b254] Squashed and merged the Feature2 branch
 1 file changed, 4 insertions(+)
复制
```

现在让我们检查分支图：

```bash
$ git slog
* 565b254 2021-08-24 15:53:05 +0200 Squashed and merged the Feature2 branch (HEAD -> master) (Kai Yuan)
* 204b03f 2021-08-24 15:30:29 +0200 Urgent HotFix2 (Kai Yuan)
* 8a58dd4 2021-08-24 15:30:15 +0200 Urgent HotFix1 (Kai Yuan)
| * 0ff435a 2021-08-24 15:28:07 +0200 finally, it works. phew! (feature) (Kai Yuan)
| * cb5fc72 2021-08-24 15:27:47 +0200 fix a typo (Kai Yuan)
| * 251f01c 2021-08-24 15:27:38 +0200 fix a bug (Kai Yuan)
| * e8e53d7 2021-08-24 15:27:13 +0200 implement Feature2 (Kai Yuan)
|/  
* 172d2ed 2021-08-23 23:28:56 +0200 BugFix #2 (Kai Yuan)
* 29976c5 2021-08-23 23:28:33 +0200 BugFix #1 (Kai Yuan)
* 34fbfeb 2021-08-23 23:28:19 +0200 Feature1 implemented (Kai Yuan)
* cbd350d 2021-08-23 23:26:19 +0200 Init commit (Kai Yuan)复制
```

我们可以看到我们已经将feature分支中的所有更改合并到master分支中，并且我们在master分支中有一个提交565b254。

另一方面，在功能分支中，我们仍然有四个提交。

## 七、总结

在本文中，我们讨论了 Git 压缩是什么以及何时应该考虑使用它。

我们还学习了如何在 Git 中压缩提交。
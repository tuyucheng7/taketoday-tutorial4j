## 1. 概述

在本文中，我们将探讨修改[Git](https://www.baeldung.com/git-guide)提交的不同方法。

## 2.使用修正

我们可以通过简单地使用amend选项修改最新的 Git 提交。它取代了最近的提交。我们可以修改提交消息并更新提交中包含的文件。Git 将修改后的提交视为新提交。

让我们用一个例子来尝试修改选项。为简单起见，让我们更新一个文件并提交消息“Commit 1”。现在，让我们尝试使用amend选项更新提交：

```bash
git commit --amend复制
```

执行上述命令会打开一个编辑器以包含更改。让我们更新提交消息并保存更改。关闭编辑器后，我们可以看到更新后的提交：

```bash
[master c0bc5d3] Amended Commit 1
 Date: Wed Jun 29 22:41:08 2022 +0530
 1 file changed, 1 insertion(+), 1 deletion(-)
复制
```

我们还可以在修改提交时包含阶段性更改。让我们创建额外的更改并使用amend选项将它们包含在最新的提交中，再次更改提交消息：

```bash
[master 0a1d571] Amended Commit 1 - Added new file
 Date: Wed Jun 29 22:41:08 2022 +0530
 2 files changed, 2 insertions(+), 1 deletion(-)
 create mode 100644 README2
复制
```

如果我们只想添加分阶段更改而不更新提交消息，我们可以使用no-edit选项：

```bash
git commit --amend --no-edit复制
```

因此，我们可以看到amend选项是向最近的提交添加更改的便捷方式。现在，让我们探索更新 Git 历史中较旧提交的不同方法。

## 3.使用[变基](https://www.baeldung.com/git-merge-vs-rebase#git-rebase)

我们可以使用rebase命令将一系列提交移动到新的基础。Git 在内部为每个旧提交创建一个新提交并移动到指定的新基。

将-i选项与rebase命令一起使用可启动交互式会话。在此会话期间，如果需要，我们可以使用以下命令修改每个提交：

-   选择(p) ->包括特定的提交
-   squash (s) -> 将提交与之前的提交合并
-   drop (d) -> 删除特定的提交
-   改写(r) -> 包含提交并更新提交消息
-   编辑(e) -> 包含提交，并带有一个选项来更新包含的文件

让我们尝试使用上述命令更新示例中的提交历史记录。在此步骤中，git log显示以下提交：

```bash
commit 5742fcbe1cb14a9c4f1425eea9032ffb4c6191e5 (HEAD -> master)
Author: #####
Date:   Fri Jul 1 08:11:52 2022 +0530
    commit 5
commit e9ed266b84dd29095577ddd8f6dc7fcf5cf9db0d
Author: #####
Date:   Fri Jul 1 08:11:37 2022 +0530
    commit 4
commit 080e3ecc041b7be1757af67bf03db982135b9093
Author: #####
Date:   Fri Jul 1 08:11:18 2022 +0530
    commit 3
commit d5923e0ced1caff5874d8d41f39d197b5e1e2468
Author: #####
Date:   Fri Jul 1 08:10:58 2022 +0530
    commit 2
commit 1376dc1182a798b16dc85239ec7382e8340d5267
Author: #####
Date:   Wed Jun 29 22:41:08 2022 +0530
    Amended Commit 1 - Added new file
复制
```

假设我们想要更改提交“ Amended Commit 1 – Added new file ”之后提交的修改。

让我们将上面的提交设置为新的基础：

```bash
git rebase -i 1376dc1182a798b16dc85239ec7382e8340d5267复制
```

这将打开一个编辑器，我们可以在其中根据需要进行更改：

```bash
pick d5923e0 commit 2
pick 080e3ec commit 3
pick e9ed266 commit 4
pick 5742fcb commit 5
# Rebase #####..### onto #### (4 commands)
#
# Commands:
# p, pick <commit> = use commit
# r, reword <commit> = use commit, but edit the commit message
# e, edit <commit> = use commit, but stop for amending
# s, squash <commit> = use commit, but meld into previous commit
# f, fixup <commit> = like "squash", but discard this commit's log message
# x, exec <command> = run command (the rest of the line) using shell
# b, break = stop here (continue rebase later with 'git rebase --continue')
# d, drop <commit> = remove commit
# l, label <label> = label current HEAD with a name
# t, reset <label> = reset HEAD to a label
# m, merge [-C <commit> | -c <commit>] <label> [# <oneline>]
# .       create a merge commit using the original merge commit's
# .       message (or the oneline, if no original merge commit was
# .       specified). Use -c <commit> to reword the commit message.
#
# These lines can be re-ordered; they are executed from top to bottom.
#
# If you remove a line here THAT COMMIT WILL BE LOST.
#
# However, if you remove everything, the rebase will be aborted.
#
# Note that empty commits are commented out复制
```

现在，让我们尝试一些命令来更新 Git 历史记录：

```bash
reword d5923e0 commit 2-updated
edit 080e3ec commit 3
squash e9ed266 commit 4
drop 5742fcb commit 5复制
```

在第一个命令之后，我们将看到输出：

```bash
[detached HEAD 178e8eb] commit 2-alter
 Date: Fri Jul 1 08:10:58 2022 +0530
 1 file changed, 1 insertion(+)
Stopped at ######...  commit 3
You can amend the commit now, with

  git commit --amend 

Once you are satisfied with your changes, run

  git rebase --continue复制
```

现在，按照 Git 输出中的指定，我们执行git commit –amend命令并进行一些更改：

```bash
commit 3          
# Please enter the commit message for your changes. Lines starting
# with '#' will be ignored, and an empty message aborts the commit.
#
# Date:      Fri Jul 1 08:11:18 2022 +0530
#
# interactive rebase in progress; onto 1376dc1
# Last commands done (2 commands done):
#    reword d5923e0 commit 2-updated
#    edit 080e3ec commit 3
# Next commands to do (2 remaining commands):
#    squash e9ed266 commit 4
#    drop 5742fcb commit 5
# You are currently splitting a commit while rebasing branch 'master' on '1376dc1'.
#
# Changes to be committed:
#       modified:   README
#       modified:   README2复制
```

关闭编辑器后，我们得到以下输出：

```bash
[detached HEAD 9433120] commit 3 - updated
 Date: Fri Jul 1 08:11:18 2022 +0530
 2 files changed, 3 insertions(+), 1 deletion(-)
复制
```

我们可以在这里看到，我们不仅更新了提交消息，而且还包含一个文件作为提交的一部分。

接下来，我们需要执行git rebase –continue命令以移动到下一次更新。

我们的下一步涉及将提交4与提交 3合并，并打开以下编辑器：

```bash
# This is a combination of 2 commits.
# This is the 1st commit message:
commit 3 - updated
# This is the commit message #2:
commit 4
# Please enter the commit message for your changes. Lines starting
# with '#' will be ignored, and an empty message aborts the commit.
# Date:      Fri Jul 1 08:11:18 2022 +0530
# interactive rebase in progress; onto 1376dc1
# Last commands done (3 commands done):
#    edit 080e3ec commit 3
#    squash e9ed266 commit 4
# Next command to do (1 remaining command):
#    drop 5742fcb commit 5
# You are currently rebasing branch 'master' on '1376dc1'.
#
# Changes to be committed:
#       modified:   README
#       modified:   README2
复制
```

对上述编辑器添加修改后，我们得到以下输出：

```bash
[detached HEAD 917c583] commit 3 - squashed with commit 4
Date: Fri Jul 1 08:11:18 2022 +0530
2 files changed, 3 insertions(+), 1 deletion(-)
Successfully rebased and updated refs/heads/master.
复制
```

最后，我们要求删除提交5，这不需要我们做任何进一步的修改。

### 4. 分析日志

执行上述步骤后，我们可以看到git log的输出为：

```bash
commit 917c583d5bb02803ee43cf87a2143f201c97bbe8 (HEAD -> master)
Author: #######
Date:   Fri Jul 1 08:11:18 2022 +0530
commit 3 - squashed with commit 4
commit 4
commit 178e8ebec178c166d1c9def2d680f41933eba29b
Author: #######
Date:   Fri Jul 1 08:10:58 2022 +0530
commit 2-alter
commit 1376dc1182a798b16dc85239ec7382e8340d5267
Author: #######
Date:   Wed Jun 29 22:41:08 2022 +0530
Amended Commit 1 - Added new file
复制
```

在这里，我们可以做一些观察：

-   提交 5已删除
-   提交 4与提交 3合并
-   提交 2消息已更新

在最后的日志中，我们可以看到提交的 ID 现在已更改。这是因为 Git 已经替换了以前的提交并创建了带有修改的新提交。

我们可以使用reflog命令查看当前HEAD相关的引用日志。它包括与 Git 提交相关的所有更新的历史记录。

让我们使用reflog命令来观察我们示例的 git 历史记录：

```bash
917c583 (HEAD -> master) HEAD@{0}: rebase -i (finish): returning to refs/heads/master
917c583 (HEAD -> master) HEAD@{1}: rebase -i (squash): commit 3 - squashed with commit 4
9433120 HEAD@{2}: commit (amend): commit 3 - updated
f4e8340 HEAD@{3}: commit (amend): commit 3 - updated
fd048e1 HEAD@{4}: commit (amend): commit 3 - updated
39b2f1b HEAD@{5}: commit (amend): commit 3 - updated
f79cbfb HEAD@{6}: rebase -i (edit): commit 3
178e8eb HEAD@{7}: rebase -i (reword): commit 2-alter
d5923e0 HEAD@{8}: rebase -i: fast-forward
1376dc1 HEAD@{9}: rebase -i (start): checkout 1376dc1182a798b16dc85239ec7382e8340d5267
5742fcb HEAD@{10}: commit: commit 5
e9ed266 HEAD@{11}: commit: commit 4
080e3ec HEAD@{12}: commit: commit 3
d5923e0 HEAD@{13}: commit: commit 2
1376dc1 HEAD@{14}: commit (amend): Amended Commit 1 - Added new file复制
```

## 5.总结

在本文中，我们探讨了改变 Git 历史记录的不同方法。但是，我们在使用这些选项时应谨慎行事，因为它也可能导致内容丢失。
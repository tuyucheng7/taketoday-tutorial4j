## 1. 概述

[git rebase](https://www.baeldung.com/git-guide#2-git-rebase---reapply-commits)是编写干净的代码提交历史记录的推荐最佳实践，尤其是对于多开发人员代码存储库。手动完成此操作后，我们可能稍后会意识到我们想要返回到原始状态。

在本教程中，我们将探讨一些撤消 git rebase操作的技术。

## 2.设置

让我们创建一个测试台来模拟具有多个分支的多开发人员代码存储库。我们可以假设开发分支是项目的唯一真实来源，每个开发人员都使用它来使用特定于功能的分支来处理特定功能：

![回购设置](https://www.baeldung.com/wp-content/uploads/2022/08/repo-setup.png)现在，假设我们已经为项目准备好上述版本控制，让我们检查一下feature2分支：

```bash
$ git branch --show-current
feature2复制
```

最后，让我们看看feature1和feature2分支的代码提交历史：

```sql
$ git log feature1
commit e5e9afbbd82e136fc20957d47d05e72a38d8d10d
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:27:22 2022 +0530

    Add feature-1

commit 033306a06895a4034b681afa912683a81dd17fed
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:27:22 2022 +0530

    Add .gitignore file

$ git log feature2
commit 9cec4652f34f346e293b19a52b258d9d9a49092e
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:27:22 2022 +0530

    Add feature-2

commit 033306a06895a4034b681afa912683a81dd17fed
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:27:22 2022 +0530

    Add .gitignore file复制
```

在后续部分中，我们将重复使用此基本场景来执行git rebase，然后一次应用一种方法来撤消 rebase 操作。

## 3.使用ORIG_HEAD

让我们首先使用干净的场景检查feature2分支的当前提交：

```bash
$ git log HEAD
commit 728ceb3219cc5010eae5840c992072cac7a5da00 (HEAD -> feature2)
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:46:56 2022 +0530

    Add feature-2

commit 6ed8a4d2a961fdfc4d5e4c7c00b221ed6f283bf4 (development)
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:46:56 2022 +0530

    Add .gitignore file复制
```

现在，让我们在feature1分支之上重新设置feature2 分支：

```bash
$ git rebase feature1
复制
```

做完rebase操作后，我们看一下HEAD引用：

```bash
$ git log HEAD
commit 9d38b792d0c9a8d0cd8e517fcb2ca5260989cc4a
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:46:56 2022 +0530

    Add feature-2

commit 1641870338662a016d5c8a17ef5cada0309f107e
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:46:56 2022 +0530

    Add feature-1

commit 6ed8a4d2a961fdfc4d5e4c7c00b221ed6f283bf4
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:46:56 2022 +0530

    Add .gitignore file
复制
```

此外，我们可以验证ORIG_HEAD仍然指向728ceb3219cc5010eae5840c992072cac7a5da00提交：

```bash
$ git log ORIG_HEAD
commit 728ceb3219cc5010eae5840c992072cac7a5da00
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:46:56 2022 +0530

    Add feature-2

commit 6ed8a4d2a961fdfc4d5e4c7c00b221ed6f283bf4
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:46:56 2022 +0530

    Add .gitignore file
复制
```

最后，我们使用ORIG_HEAD引用进行重置：

```bash
$ git reset --hard ORIG_HEAD
$ git log HEAD -1
commit 728ceb3219cc5010eae5840c992072cac7a5da00
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 16:46:56 2022 +0530

    Add feature-2
复制
```

就是这样！在ORIG_HEAD的帮助下，我们已经成功地恢复了 rebase 操作。

## 4. 使用git reflog

同样，让我们从一个新的场景设置开始：

```bash
$ git log HEAD
commit 07b98ef156732ba41e2cbeef7939b5bcc9c364bb
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 17:53:35 2022 +0530

    Add feature-2

commit d6c52eb601e3ba11d65e7cb6e99ec6ac6018e272
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 17:53:35 2022 +0530

    Add .gitignore file
复制
```

现在，让我们做一个变基并检查提交历史：

```bash
$ git rebase feature1
$ git log HEAD
commit b6ea25bf83ade2caca5ed92f6c5e5e6a3cb2ca7b
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 17:53:35 2022 +0530

    Add feature-2

commit d2cabe48747699758e2b14e76fb2ebebfc49acb1
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 17:53:35 2022 +0530

    Add feature-1

commit d6c52eb601e3ba11d65e7cb6e99ec6ac6018e272
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 17:53:35 2022 +0530

    Add .gitignore file
复制
```

接下来，让我们使用[git reflog](https://git-scm.com/docs/git-reflog)命令来细粒度地检查事件记录：

```bash
$ git reflog
b6ea25b HEAD@{0}: rebase (continue) (finish): returning to refs/heads/feature2
b6ea25b HEAD@{1}: rebase (continue): Add feature-2
d2cabe4 HEAD@{2}: rebase (start): checkout feature1
07b98ef HEAD@{3}: commit: Add feature-2
d6c52eb HEAD@{4}: checkout: moving from feature1 to feature2
d2cabe4 HEAD@{5}: commit: Add feature-1
d6c52eb HEAD@{6}: checkout: moving from development to feature1
d6c52eb HEAD@{7}: Branch: renamed refs/heads/master to refs/heads/development
d6c52eb HEAD@{9}: commit (initial): Add .gitignore file
复制
```

我们可以注意到 git 在内部以粒度级别维护引用，其中HEAD在 rebase 操作之前的位置由HEAD@ {3}引用表示。

因此，作为最后一步，让我们通过执行git reset来恢复之前的状态：

```bash
$ git reset --hard HEAD@{3}
$ git log HEAD
commit 07b98ef156732ba41e2cbeef7939b5bcc9c364bb
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 17:53:35 2022 +0530

    Add feature-2

commit d6c52eb601e3ba11d65e7cb6e99ec6ac6018e272
Author: Tapan Avasthi <tavasthi@Tapans-MacBook-Air.local>
Date:   Sun Jul 31 17:53:35 2022 +0530

    Add .gitignore file
复制
```

伟大的！我们也成功地学会了这种方法。但是，这种方法使用了 git 的一些低级细节，只有高级 git 用户应该使用它。

## 5.总结

在本文中，我们使用了 git 存储库的测试场景，并了解了两种流行的撤消git rebase操作的技术。
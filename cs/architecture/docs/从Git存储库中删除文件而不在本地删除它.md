## 1. 概述

Git 已成为一种广泛使用的分布式版本控制系统。在本教程中，让我们探讨如何从 Git 存储库中删除文件或目录，但保留其本地副本。

## 二、问题简介

像往常一样，让我们通过一个例子来理解这个问题。假设我们正在处理 Git 存储库 myRepo：

```bash
$ ls -l
total 12
drwxr-xr-x 2 kent kent 60 May 12 23:00 logs/
-rw-r--r-- 1 kent kent 26 May 11 13:22 README.md
-rw-r--r-- 1 kent kent 21 May 11 13:22 some-file.txt
-rw-r--r-- 1 kent kent 16 May 12 22:40 user-list.txt复制
```

我们已将存储库克隆到本地，如ls输出所示，存储库中有三个文件和一个 日志目录。

现在，假设我们想从 Git 存储库中删除文件user-list.txt和日志目录。但是，我们不想从我们的本地工作副本中删除它们。

一个常见的场景是我们已经提交了一些文件或目录，然后意识到我们应该忽略一些文件。因此，我们将从存储库中删除相关文件，保留本地副本，并将相应的模式添加到.gitignore文件中，以便 Git 不再跟踪这些文件。

我们知道git rm user-list.txt 命令将从存储库中删除文件。但是，它也会删除本地文件。

当然，我们可以将文件和目录移动到另一个目录，提交提交，然后将它们复制回本地工作目录。它解决了这个问题。但是，这种方法效率低下，尤其是当文件或目录很大时。

接下来，让我们看看如何更高效地解决这个问题。

## 3. 使用 git rm –cached命令

我们已经提到git rm FILE默认会从索引和本地工作树中删除文件。

但是， git rm命令提供了–cached选项，允许我们只从存储库的索引中删除文件并保持本地文件不变。

接下来，让我们尝试使用 user-list.txt文件：

```bash
$ git rm --cached user-list.txt
rm 'user-list.txt'
复制
```

如上面的输出所示， user-list.txt文件已被删除。那么现在，让我们执行git status命令来验证一下：

```bash
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
	deleted:    user-list.txt

Untracked files:
  (use "git add <file>..." to include in what will be committed)
	user-list.txt
复制
```

正如我们所见， user-list.txt被“删除”了。此外，由于其本地副本仍然存在，因此已将其标记为“未跟踪”。

我们可以类似地删除日志目录。然而，由于它是一个目录，我们需要另外将-r(递归) 选项传递给 git rm命令：

```bash
$ git rm --cached -r logs
rm 'logs/server.log'复制
```

现在，让我们提交更改：

```bash
$ git commit -m 'remove user-list.txt and logs'
[master ee8cfe8] remove user-list.txt and logs
 2 files changed, 4 deletions(-)
 delete mode 100644 logs/server.log
 delete mode 100644 user-list.txt
复制
```

[然后，让我们使用git ls-files](https://git-scm.com/docs/git-ls-files) 命令检查当前暂存的文件 ：

```bash
$ git ls-files -c
.gitignore
README.md
some-file.txt
复制
```

如输出所示，目标文件和目录不再存在。此外，还会保留本地副本。因此，我们已经解决了这个问题。

如果愿意，我们可以将它们添加到.gitignore文件中以防止 Git 再次跟踪它们。

## 4. 删除.gitignore中定义的所有文件

有时，我们想检查 Git 的索引并删除.gitignore中定义的所有文件。假设我们已经完成了.gitignore定义。然后，一个简单的方法将是一个三步过程：

-   首先，从索引中删除所有文件：git rm -r –cached
-   然后，再次暂存所有文件。.gitignore中定义的文件将被自动忽略：git add
-   提交我们的更改：git commit -m “a proper commit message”

或者，我们可以仅查找并删除当前已跟踪但应忽略的文件。git ls-files命令可以帮助我们找到文件。

让我们恢复之前的提交并再次删除 user-list.txt文件和日志目录。这次，让我们先将它们添加到 .gitignore文件中：

```bash
$ cat .gitignore
user-list.txt
logs/
复制
```

接下来，让我们找出要从 Git 索引中删除的文件：

```bash
$ git ls-files -i -c -X .gitignore
logs/server.log
user-list.txt
复制
```

如我们所见，上面的命令列出了我们要删除的暂存文件。

现在，让我们结合 git rm –cached 和 git ls-files命令一次性删除它们：

```bash
$ git rm --cached $(git ls-files -i -c -X .gitignore)
rm 'logs/server.log'
rm 'user-list.txt'复制
```

值得一提的是，该命令会删除logs目录下的所有文件，所以最后，它会从 index.html 中删除空的 logs 目录。所以，在这个例子中，我们在logs目录下只有一个文件。

现在，如果我们检查暂存文件，删除的文件已经消失：

```bash
$ git ls-files -c
.gitignore
README.md
some-file.txt
复制
```

当然，user-list.txt和logs/仍然在我们的本地工作树中：

```bash
$ ls -l
total 12
drwxr-xr-x 2 kent kent 60 May 13 00:45 logs/
-rw-r--r-- 1 kent kent 26 May 11 13:22 README.md
-rw-r--r-- 1 kent kent 21 May 11 13:22 some-file.txt
-rw-r--r-- 1 kent kent 16 May 13 00:45 user-list.txt复制
```

## 5. 删除的文件仍在 Git 历史记录中

我们已经使用git rm –cached命令解决了我们的问题。但是，我们应该记住，我们只是从 Git 的跟踪索引中删除了该文件。我们仍然可以在 Git 的提交历史中看到该文件及其内容。例如，我们仍然可以通过检查之前的提交来查看user-list.txt的内容：

```bash
$ git show 668fa2f user-list.txt
commit 668fa2f...
Author: ...
Date:   ...

    add user-list.txt and some-file.txt

diff --git a/user-list.txt b/user-list.txt
new file mode 100644
index 0000000..3da7fab
--- /dev/null
+++ b/user-list.txt
@@ -0,0 +1,3 @@
+kent
+eric
+kevin
复制
```

知道这一点很重要，因为有时我们会忘记将一些敏感文件添加到.gitingore文件中，例如凭据。但是我们已经提交它们并将更改推送到远程存储库。意识到这一点后，我们可能希望从 Git 历史记录中彻底擦除敏感文件。

如果是这种情况，我们需要[从 Git 的提交历史中删除这些文件](https://www.baeldung.com/git-remove-file-commit-history)。

## 六，总结

在本文中，我们通过示例展示了如何从 Git 存储库中删除文件或目录，但保留其本地副本。

此外，我们还提供了一种快速删除 .gitignore文件中定义的所有文件的方法。最后，我们应该记住，被git rm –cached删除的文件仍然存在于 Git 的提交历史中。
## 1. 概述

在本教程中，我们将学习如何使用各种工具从 git 存储库的提交历史记录中删除大文件。

## 2. 使用git 过滤器分支

这是最常用的方法，它帮助我们重写提交分支的历史。

例如，假设我们错误地将一个 blob 文件放入项目文件夹中，删除后，我们仍然会在 git 历史记录中注意到该文件：

```bash
$ git log --graph --full-history --all --pretty=format:"%h%x09%d%x20%s"
* 9e87646        (HEAD -> master) blob file removed
* 2583677        blob file
* 34ea256        my first commit复制
```

我们可以通过使用以下命令重写树及其内容来从 git 历史记录中删除 blob 文件：

```bash
$ git filter-branch --tree-filter 'rm -f blob.txt' HEAD复制
```

在这里，rm选项从树中删除文件。此外，如果该文件不存在于我们项目中的其他已提交目录中，则-f 选项可防止命令失败。如果没有-f 选项，当我们的项目中有多个目录时，该命令可能会失败。

这是我们运行命令后的 git 日志：

```bash
* 8f39d86        (HEAD -> master) blob file removed
* e99a81d        blob file
| * 9e87646      (refs/original/refs/heads/master) blob file removed
| * 2583677      blob file
|/  
* 34ea256        my first commit
复制
```

我们可以将 HEAD 替换为提交历史的 SHA1 密钥，以尽量减少重写。

我们的 git 日志仍然包含对已删除文件的引用。我们可以通过更新我们的 repo 来删除引用：

```bash
$ git update-ref -d refs/original/refs/heads/master
复制
```

-d选项在验证它仍然包含旧值后删除命名的 ref。

我们需要记录我们的引用在存储库中发生了变化：

```bash
$ git reflog expire --expire=now --all复制
```

expire子命令删除旧的引用日志条目。

最后，我们需要清理和优化我们的 repo：

```bash
$ git gc --prune=now复制
```

–prune =now选项会修剪松散的对象，而不管它们的年龄。

运行命令后，这是我们的 git 日志：

```bash
* 6f49d86        (HEAD -> master) my first commit
复制
```

我们可以看到引用已被删除。

或者，我们可以运行：

```bash
$ git filter-branch --index filter 'git rm --cached --ignore-unmatched blob.txt' HEAD复制
```

这与树过滤器完全一样 ，但速度更快，因为它只重写索引，即工作目录。如果文件从我们项目中的其他已提交目录中丢失，则子命令–ignore-unmatched可防止命令失败。

我们应该注意，这种使用两个不同命令的方法在删除大文件时可能会很慢。

## 3. 使用git-filter-repo

另一种方法是使用git-filter-repo命令。它是第三方插件，使用起来更简单，比其他方法更快。[而且是git官方文档](https://git-scm.com/docs/git-filter-branch)中推荐的方案。

### 3.1. 安装

它至少需要 python3 >= 3.5 和 git >= 2.22.0；某些功能需要 git 2.24.0 或更高版本。

我们将在我们的 Linux 机器上安装git-filter-repo 。Windows安装指南，我们可以参考[文档](https://github.com/newren/git-filter-repo#how-do-i-install-it)。 

首先，我们将使用以下命令安装python-pip和git-filter-repo ：

```bash
$ sudo apt install python3-pip
$ pip install --user git-filter-repo复制
```

或者，我们可以使用以下命令安装git-filter-repo：

```bash
# Add to bashrc.
export PATH="${HOME}/bin:${PATH}"

mkdir -p ~/bin
wget -O ~/bin/git-filter-repo https://raw.githubusercontent.com/newren/git-filter-repo/7b3e714b94a6e5b9f478cb981c7f560ef3f36506/git-filter-repo
chmod +x ~/bin/git-filter-repo复制
```

### 3.2. 删除文件

让我们运行命令来检查我们的 git 日志：

```bash
$ git log --graph --full-history --all --pretty=format:"%h%x09%d%x20%s"
* ee36517        (HEAD -> master) blob.txt removed
* a480073        project folder复制
```

接下来我们要分析我们的 repo：

```bash
$ git filter-repo --analyze
Processed 5 blob sizes
Processed 2 commits
Writing reports to .git/filter-repo/analysis...done.复制
```

这会生成一个我们的回购状态报告目录。该报告可在 .git/filter-repo/analysis 中找到 。此信息可能有助于确定在后续运行中要过滤的内容。它还可以帮助我们确定我们之前的过滤命令是否真的做了我们想要它做的事情。

然后，让我们使用选项–path-match 运行此命令，这有助于指定要包含在过滤历史记录中的文件：

```bash
$ git filter-repo --force --invert-paths --path-match blob.txt复制
```

这是我们的新 git 日志：

```bash
* 8940776        (HEAD -> master) project folder
复制
```

执行后，它将更改修改后的提交的提交哈希。 

## 4. 使用 BRG Repo-Cleaner

另一个不错的选择是[BRG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/)，它是用 Java 编写的第三方插件。

它比git filter-branch方法更快。此外，它还适用于删除大文件、密码、凭据和其他私人数据。 

假设我们要删除大于 200MB 的 blob 文件。这个附加组件可以很容易地做到这一点：

```lua
$ java -jar bfg.jar --strip-blob-bigger-than 200M my-repo.git复制
```

然后，让我们运行这个命令来清理死数据：

```bash
$ git gc --prune=now --aggressive复制
```

## 5. 使用git-rebase

我们需要来自 git 日志的 SHA1 密钥来使用这种方法：

```bash
$ git log --graph --full-history --all --pretty=format:"%h%x09%d%x20%s"
* 535f7ea        (HEAD -> master) blob file removed
* 8bffdfa        blob file
* 5bac30b        index.html复制
```

我们的目标是从我们的提交历史中删除 blob 文件。因此，我们将使用我们要删除的条目之前的条目历史记录中的 SHA1 密钥。

使用此命令，我们进入交互式变基：

```bash
$ git rebase -i 5bac30b复制
```

这将打开我们的nano编辑器，显示：

```bash
pick 535f7ea blob file removed
pick 8bffdfa blob file 

# Rebase 5bac30b..535f7ea onto 535f7ea (2 command)
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
# .  create a merge commit using the original merge commit's
# .  message (or the oneline, if no original merge commit was
# .  specified). Use -c <commit> to reword the commit message.
复制
```

现在，我们将通过删除文本“ pick 535f7ea blob file removed ”来修改它。这有助于我们更改提交历史并删除我们之前删除的历史。

然后我们保存文件并退出编辑器，这会在终端显示以下消息：

```bash
interactive rebase in progress; onto 535f7ea
Last command done (1 command done):
pick 535f7ea blob file removed
No commands remaining.
You are currently rebasing branch 'master' on '535f7ea'.
(all conflicts fixed: run "git rebase --continue")
复制
```

最后我们继续rebase操作：

```bash
$ git rebase --continue
Successfully rebased and updated refs/heads/master.复制
```

然后我们可以验证我们的提交历史：

```bash
$ git log --graph --full-history --all --pretty=format:"%h%x09%d%x20%s"
* 5bac30b        (HEAD -> master) index.html复制
```

我们应该注意，这种方法不如git-filter-repo快。

## 六，总结

在本文中，我们了解了从 git 存储库的提交历史记录中删除大文件的不同方法。我们还看到，根据 git 文档，推荐使用git filter-repo ，因为与其他方法相比，它速度快且缺点更少。
## 1. 概述

git stash和git stash pop等命令用于搁置(隐藏)和恢复我们工作目录中的更改。在本教程中，我们将学习如何在[Git](https://www.baeldung.com/git-guide)中恢复丢失的存储。

## 2. 隐藏工作目录中的更改

对于我们的示例，假设我们已经分叉并克隆了一个[Git 存储库](https://github.com/eugenp/tutorials)。现在，让我们对README.md文件进行一些更改，只需在末尾添加一个新行并检查我们工作目录的状态：

```bash
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   README.md

no changes added to commit (use "git add" and/or "git commit -a")复制
```

从这里，我们可以使用git stash 命令暂时搁置我们的更改。

```bash
$ git stash
Saved working directory and index state WIP on master: a8088442db Updated pom.xml复制
```

现在，如果再次执行git status ，我们将看到我们的工作目录是干净的。

```bash
$ git status
On branch master
Your branch is up to date with 'origin/master'.

nothing to commit, working tree clean复制
```

## 3. 恢复隐藏的更改并找到哈希

让我们看看我们如何恢复隐藏的更改并找到与隐藏提交关联的哈希。

### 3.1. 将隐藏的更改恢复到工作目录中

我们可以像这样将隐藏的更改带回我们的工作目录：

```bash
$ git stash pop
On branch master
Your branch is up to date with 'origin/master'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   README.md

no changes added to commit (use "git add" and/or "git commit -a")
Dropped refs/stash@{0} (59861637f7b599d87cb7a1ff003f1b0212e8908e)复制
```

正如我们在最后一行中看到的那样，git stash pop不仅恢复了隐藏的更改，而且还删除了对其关联提交的引用。

### 3.2. 在终端打开时定位哈希

如果我们的终端还开着，我们可以很容易地找到执行git stash pop后生成的哈希。在我们的示例中，最后一行显示的哈希值是59861637f7b599d87cb7a1ff003f1b0212e8908e。

### 3.3. 终端关闭后恢复哈希

即使我们关闭了终端，我们仍然可以通过以下方式找到我们的哈希值：

```bash
$ git fsck --no-reflog
Checking object directories: 100% (256/256), done.
Checking objects: 100% (302901/302901), done.
dangling commit 59861637f7b599d87cb7a1ff003f1b0212e8908e复制
```

我们现在可以看到已删除存储的提交哈希。

## 4. 找回掉落的藏品

一旦我们应用了它，我们通常不需要存储条目。但是，可能存在我们希望在删除后返回到隐藏条目的情况。例如，如果使用git reset –hard HEAD将从我们的工作目录中丢弃所有未提交的更改。在这种情况下，我们可能希望召回一些早期隐藏的更改，即使它们已被删除。

### 4.1. 使用哈希恢复存储

将散列用于悬空提交，我们仍然可以恢复这些更改：

```bash
$ git stash apply 59861637f7b599d87cb7a1ff003f1b0212e8908e
On branch master
Your branch is up to date with 'origin/master'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   README.md

no changes added to commit (use "git add" and/or "git commit -a")复制
```

我们可以看到我们的工作目录已恢复，其中包含之前隐藏的更改。

### 4.2. 查找所有哈希提交

如果我们没有现成的哈希值，我们可以找到它：

```bash
git fsck --no-reflog | awk '/dangling commit/ {print $3}'复制
```

在这里，我们将–no-reflog 选项与awk结合起来，只为我们过滤掉哈希。

## 5.总结

在本文中，我们了解了git stash 的工作原理以及它如何在我们使用它时删除一个条目。然后我们看到了当我们知道它的散列时我们如何仍然可以使用一个被丢弃的条目，以及如何找到一个隐藏提交的散列。
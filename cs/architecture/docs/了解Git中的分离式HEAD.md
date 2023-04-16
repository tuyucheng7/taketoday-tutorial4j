## 1. 概述

在使用 git 时遇到神秘状态的情况并不少见。然而，有一天它最有可能看到“detached HEAD”。

在本教程中，我们将讨论什么是分离式 HEAD 以及它是如何工作的。我们将介绍如何在 Git 中导航进出分离的 HEAD。

## 2. Git 中的 HEAD 是什么

当我们创建提交时，Git 会存储存储库中所有文件状态的记录。HEAD 是另一种重要的引用类型。HEAD 的目的是跟踪 Git 存储库中的当前点。换句话说，HEAD 回答了“我现在在哪里？”这个问题：

```bash
$git log --oneline
a795255 (HEAD -> master) create 2nd file
5282c7c appending more info
b0e1887 creating first file复制
```

例如，当我们使用 log 命令时，Git 如何知道应该从哪个提交开始显示结果？HEAD 提供了答案。当我们创建一个新提交时，它的父提交由 HEAD 当前指向的位置指示。

因为 Git 具有如此高级的版本跟踪功能，我们可以回到我们存储库中的任何时间点来查看其内容。

能够回顾过去的提交也让我们看到一个存储库或一个特定的文件或一组文件是如何随着时间的推移而演变的。 当我们签出不是分支的提交时，我们将进入“分离的 HEAD 状态”。这是指我们正在查看不是存储库中最新提交的提交。

## 3、Detached HEAD示例

大多数时候，HEAD 指向一个分支名称。当我们添加一个新的提交时，我们的分支引用被更新以指向它，但 HEAD 保持不变。当我们更改分支时，HEAD 会更新以指向我们已切换到的分支。所有这一切意味着，在上述场景中，HEAD 是“当前分支中的最后一次提交”的同义词。这是 HEAD 附加到分支的正常状态：

[![有颜料的图像](https://www.baeldung.com/wp-content/uploads/2021/09/image-with-paints.png)](https://www.baeldung.com/wp-content/uploads/2021/09/image-with-paints.png)

正如我们所看到的，HEAD 指向 master 分支，它指向最后一次提交。一切看起来都很完美。但是，在运行以下命令后，repo 位于分离的 HEAD 中：

```bash
$ git checkout 5282c7c
Note: switching to '5282c7c'.

You are in 'detached HEAD' state. You can look around, make experimental
changes and commit them, and you can discard any commits you make in this
state without impacting any branches by switching back to a branch.

If you want to create a new branch to retain commits you create, you may
do so (now or later) by using -c with the switch command. Example:

  git switch -c <new-branch-name>

Or undo this operation with:

  git switch -

HEAD is now at 5282c7c appending more info

users@ubuntu01: MINGW64 ~/git/detached-head-demo ((5282c7c...))复制
```

下面是当前 git HEAD 的图形表示。由于我们已经检出之前的提交，现在 HEAD 指向5282c7c提交，而 master 分支仍然指的是相同的：

[![带头彩的图像](https://www.baeldung.com/wp-content/uploads/2021/09/image-with-head-paints.png)](https://www.baeldung.com/wp-content/uploads/2021/09/image-with-head-paints.png)

## 4. Git 分离式 HEAD 的好处

通过检查特定的(5282c7c)提交分离 HEAD 后，它允许我们转到项目历史中的前一个点。

假设我们要检查给定的错误是否在上周二已经存在。我们可以使用 log 命令，按日期过滤，来启动相关的提交哈希。然后我们可以手动或通过运行我们的自动化测试套件检查提交并测试应用程序。

如果我们不仅可以回顾过去，还可以改变过去，会怎样？这就是分离的 HEAD 允许我们做的事情。让我们回顾一下如何使用以下命令来做到这一点：

```bash
echo "understanding git detached head scenarios" > sample-file.txt
git add .
git commit -m "Create new sample file"
echo "Another line" >> sample-file.txt
git commit -a -m "Add a new line to the file"复制
```

我们现在有两个额外的提交，它们来自我们的第二次提交。让我们运行git log –oneline并查看结果：

```bash
$ git log --oneline
7a367ef (HEAD) Add a new line to the file
d423c8c create new sample file
5282c7c appending more info
b0e1887 creating first file复制
```

在 HEAD 指向5282c7c提交之前，我们又添加了两个提交，d423c8c和7a367ef 。下面是在 HEAD 之上完成的提交的图形表示。它表明现在 HEAD 指向最新的提交7a367ef：

[![带有最终头部提交的图像](https://www.baeldung.com/wp-content/uploads/2021/09/image-with-final-head-commits.png)](https://www.baeldung.com/wp-content/uploads/2021/09/image-with-final-head-commits.png)

如果我们想保留这些更改或回到以前的更改，我们应该怎么做？我们将在下一点看到。

## 5.场景

### 5.1. 意外地

如果我们不小心到达了分离的 HEAD 状态——也就是说，我们并不是要检查提交——返回很容易。在使用以下命令之前，只需检查我们所在的分支：

```bash
git switch <branch-name>   复制
git checkout <branch-name>复制
```

### 5.2. 进行了实验性更改但需要放弃它们

在某些情况下，如果我们在分离 HEAD 后进行更改以测试某些功能或识别错误，但我们不想将这些更改合并到原始分支，那么我们可以使用与前一个场景相同的命令简单地丢弃它并继续支持我们原来的分支。

### 5.3. 进行了实验性更改但需要保留它们

如果我们想保留使用分离的 HEAD 所做的更改，我们只需创建一个新分支并切换到它。我们可以在到达分离的 HEAD 后或在创建一个或多个提交后立即创建它。结果是一样的。唯一的限制是我们应该在返回正常分支之前执行此操作。让我们在创建一个或多个提交后使用以下命令在我们的演示存储库中执行此操作：

```bash
git branch experimental
git checkout experimental复制
```

我们可以注意到git log –oneline的结果与之前完全一样，唯一的区别是上次提交时指示的分支名称：

```bash
$ git log --oneline
7a367ef (HEAD -> experimental) Add a new line to the file
d423c8c create new sample file
5282c7c appending more info
b0e1887 creating first file复制
```

## 六，总结

正如我们在本文中所见，分离的 HEAD 并不意味着我们的存储库有问题。分离的 HEAD 只是我们的存储库可能处于的一种不太常见的状态。除了不是错误之外，它实际上非常有用，允许我们运行我们可以选择保留或丢弃的实验。
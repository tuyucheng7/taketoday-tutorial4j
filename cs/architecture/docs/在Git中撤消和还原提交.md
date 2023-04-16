## 1. 概述

我们经常发现自己在使用 Git 时需要撤消或恢复提交，无论是回滚到特定时间点还是恢复特别麻烦的提交。

在本教程中，我们将通过最常用的命令来撤消和还原 Git 中的提交。我们还将演示这些命令运行方式的细微差别。

## 2. 使用git checkout查看旧的提交

首先，我们可以使用git checkout命令查看特定提交时项目的状态。我们可以使用git log命令查看 Git 存储库的历史记录。每个提交都有一个唯一的 SHA-1 标识哈希，我们可以将其与git checkout一起使用 ，以便重新访问时间轴中的任何提交。

在此示例中，我们将重新访问具有e0390cd8d75dc0f1115ca9f350ac1a27fddba67d标识哈希的提交：


```bash
git checkout e0390cd8d75dc0f1115ca9f350ac1a27fddba67d复制
```

我们的 工作目录现在将与我们指定的提交的确切状态匹配。因此，我们 可以查看项目的历史状态和编辑文件，而不用担心丢失当前项目状态。我们在这里所做的任何事情都不会保存到存储库中。我们称之为分离的 HEAD状态。

我们可以对本地修改的文件使用 git checkout将它们恢复到它们的工作副本版本。

## 3. 使用git revert恢复提交

我们可以使用git revert命令恢复 Git 中的提交。请务必记住，此命令不是传统的撤消操作。相反，它反转提交引入的更改，并生成具有反转内容的新提交。

这意味着如果我们想要应用特定提交的反转，我们应该只使用git revert。它不会通过删除所有后续提交来恢复项目的先前状态，它只是撤消单个提交。

git revert不会将 ref 指针移动到我们要还原的提交，这与其他“撤消”命令形成对比，例如git checkout和git reset。相反，这些命令将HEAD ref 指针移动到指定的提交。

让我们来看一个恢复提交的例子：

```bash
mkdir git_revert_example
cd git_revert_example/
git init .
touch test_file
echo "Test content" >> test_file 
git add test_file
git commit -m "Adding content to test file"
echo "More test content" >> test_file 
git add test_file
git commit -m "Adding more test content"
git log
git revert e0390cd8d75dc0f1115ca9f350ac1a27fddba67d
cat test_file复制
```

在此示例中，我们创建了一个 test_file，添加了一些内容并提交了它。然后我们在运行git log 以识别 我们要还原的提交的提交哈希之前向文件添加并提交了更多内容。

在这种情况下，我们要还原最近的提交。最后，我们运行git revert，并通过输出文件内容验证提交中的更改是否已还原。

## 4. 使用git reset恢复到之前的项目状态

使用 Git 将项目恢复到以前的状态是通过使用git reset 命令来实现的。此工具可撤消更复杂的更改。它具有三种与 Git 的内部状态管理系统相关的主要调用形式：–hard、–soft和–mixed。了解使用哪个调用是执行git revert最复杂的部分。

git reset在行为上与git checkout相似；但是，git reset将移动HEAD ref 指针，而git checkout对HEAD ref 指针进行操作并且不会移动它。

要了解不同的调用，我们将查看 Git 的内部状态管理系统，也称为 Git 的三棵树。

第一棵树是工作目录。此树与本地文件系统同步，代表对文件和目录中的内容所做的即时更改。

接下来，我们有临时索引树。这棵树跟踪工作目录中的更改，换句话说，使用git add选择的更改 将存储在下一次提交中。

最后一棵树是提交历史。git commit命令将更改添加到存储在提交历史记录中的永久快照。

### 4.1. -难的

此调用最危险和最常用的选项是提交历史记录，因为 ref 指针更新到指定的提交。此后，暂存索引和工作索引会重置以匹配指定提交的索引。任何先前对暂存索引和工作目录进行的未决更改都会重置，以匹配提交树的状态。我们将在staging index和working index中丢失任何未决或未提交的工作。

除了上面的示例，让我们向文件提交更多内容，并将一个全新的文件提交到存储库：

```bash
echo "Text to be committed" >> test_file
git add test_file
touch new_test_file
git add new_test_file
git commit -m "More text added to test_file, added new_test_file"复制复制
```

假设我们然后决定恢复到存储库中的第一个提交。我们将通过运行命令来实现这一点：

```bash
git reset --hard 9d6bedfd771f73373348f8337cf60915372d7954复制
```

Git 会告诉我们 HEAD现在 位于指定的提交哈希中。查看test_file的内容告诉我们，我们最新的文本添加不存在，我们的new_test_file不再存在。这种数据丢失是不可逆的，因此我们了解–hard如何与 Git 的三棵树一起工作是至关重要的。

### 4.2. -柔软的

当使用–soft进行调用时，ref 指针会更新，并且重置会在此处停止。因此，暂存索引和 工作目录保持相同状态。

在我们之前的示例中，如果我们使用–soft 参数，我们提交给暂存索引的更改将不会被删除。我们仍然能够在staging index中提交我们的更改。

### 4.3. –混合

如果未传递任何参数，则默认操作模式为–mixed，它提供了–soft和–hard调用之间的中间地带。暂存索引重置为指定提交和引用指针更新的状态。暂存索引中任何未完成的更改都移至工作目录。

在我们上面的示例中使用–mixed意味着我们对文件的本地更改不会被删除。然而，与–soft不同的是，更改从暂存索引中撤消并等待进一步的操作。

## 5.总结

简单总结一下这两种方法就是git revert是安全的，而git reset 是危险的。正如我们在示例中看到的那样，使用git reset有可能会丢失我们的工作。使用git revert，我们可以安全地撤消公共提交，而git reset是为撤消工作目录和暂存索引中的本地更改而量身定制的。 

git reset将移动HEAD ref 指针，而git revert将简单地恢复提交并通过对HEAD的新提交应用撤消。同样重要的是要注意， 当任何后续快照已被推送到共享存储库时，我们不应该使用git reset。我们必须假设其他开发人员依赖于已发布的提交。
## 1. 概述

在本教程中，我们将探索查找两个[git](https://www.baeldung.com/git-guide#what-is-git)分支之间差异的方法。我们将探索[git diff](https://git-scm.com/docs/git-diff)命令并将其用于分支比较。

## 2. 在单个命令中比较分支

git diff是一个有用的命令，它允许我们比较不同类型的 git 对象，例如文件、提交、分支等等。当我们需要比较两个分支之间的差异时，这使得git diff成为一个不错的选择。

为了比较分支，我们在git diff命令后指定两个分支的名称：

```bash
$ git diff branch1 branch2 
diff --git a/file1.txt b/file1.txt
index 3b18e51..c28f4fa 100644
--- a/file1.txt
+++ b/file1.txt
@@ -1 +1 @@
-hello world
+hello from branch2复制
```

让我们检查一下输出。第一行diff ‐‐git a/file1.txt b/file1.txt显示了分支中哪些文件不同。在我们的示例中，它是文件file1.txt，它表示为branch1的a/file1.txt ，而b/file1.txt是branch2的同一文件。

下一行显示两个分支中file1.txt的索引缓存编号。然后，有几行显示添加内容的文件名，以及删除内容的文件名。

最后，最重要的行在输出的末尾，我们可以在其中看到确切的修改行。我们可以看到来自branch1 的文件file1.txt中的“hello world”行被替换为branch2中的“hello from branch2”行。

## 3. 只显示文件名

尽管我们在上面的示例中发现了两个分支之间的差异，但显示每一行的更改可能很不方便。通常，我们只想查看更改文件的名称。

为了只显示两个分支之间不同的文件名，我们 在git diff命令中使用-name-only选项：

```bash
$ git diff branch1 branch2 --name-only
file1.txt复制
```

现在，输出仅显示两个分支中不同的文件名。在我们的例子中，它只是一个文件file1.txt。

## 4. 比较分支的另一种方法

在上面的示例中，我们使用单个git diff命令来查找分支之间的差异。但是，我们必须在此命令中指定两个分支的名称。有时，如果我们从其中一个分支中进行比较，感觉会更直观。

要签出分支，我们使用[git checkout](https://git-scm.com/docs/git-checkout)命令。之后，我们只需要比较 另一个分支并查看相对于我们当前分支的所有更改。

例如，让我们从branch2中比较branch1和branch2：

```bash
$ git checkout branch2 
$ git diff branch1复制
```

我们现在应该能够看到与之前相同的输出。

## 5. 找出与共同祖先的差异

到目前为止，我们正在比较这两个分支的最新状态。然而，有时我们需要将一个分支与另一个分支的共同祖先进行比较。

换句话说，我们可能需要找到分支中与另一个分支相比的所有差异，因为它们是分支出来的。为了便于说明，让我们看下面的树：

```bash
---A---B---C---D  <== branch1
        \
         E---F    <== branch2复制
```

我们想找出branch2(当前提交位置F)和branch1在其提交位置B的区别。可以看到，在位置B，两个分支被分叉出来，然后各自独立发展。在这种情况下，位置 B 是两个分支的共同祖先。

要找到branch2和branch1共同祖先之间的差异，我们需要在分支名称之间使用三个点：

```bash
git diff branch1...branch2复制
```

输出将采用与之前类似的格式。但是，现在比较发生在branch2和branch1的共同祖先之间。

## 6. 找出提交哈希之间的差异

不仅可以显示分支之间的差异，还可以显示提交之间的差异。

此语法类似于分支比较。但是，我们需要指定要比较的提交哈希，而不是分支：

```bash
git diff b94a88bac17318fb3c3cc881d657c04de9fd7901 73ea8956375c10fe41c669ba8c6f6f9e01490452复制
```

输出将采用与上述示例类似的格式。

## 七、总结

在本文中，我们学习了如何使用git diff命令查找两个 git 分支之间的差异。

我们首先查看了使用单个命令比较分支。然后，我们了解了如何使用git checkout命令比较其中一个分支中的分支。

最后，我们了解了如何将分支与共同祖先进行比较，以及如何找出提交之间的差异。
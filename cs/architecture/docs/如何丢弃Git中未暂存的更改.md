## 1. 概述

Git工作目录可以包含不同类型的文件，包括暂存文件、未暂存[文件](https://www.baeldung.com/git-guide)和未跟踪文件。

在本教程中，我们将了解如何丢弃工作目录中不在索引中的更改。

## 2. 分析工作目录的状态

对于我们的示例，假设我们已经分叉并克隆了一个[Git 存储库](https://github.com/eugenp/tutorials)，然后对我们的工作目录进行了一些更改。

让我们检查一下工作目录的状态：

```bash
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   README.md
        modified:   gradle/maven-to-gradle/src/main/java/com/sample/javacode/DisplayTime.java

Untracked files:
  (use "git add <file>..." to include in what will be committed)
        gradle/maven-to-gradle/src/main/java/com/sample/javacode/TimeZones.java

no changes added to commit (use "git add" and/or "git commit -a")
复制
```

在这里，我们可以看到一些已修改和未暂存的文件，以及我们添加的新文件。

现在，让我们使用git add 暂存现有的 Java 文件并再次检查状态：

```bash
$ git add gradle/maven-to-gradle/src/main/java/com/sample/javacode/DisplayTime.java
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
        modified:   gradle/maven-to-gradle/src/main/java/com/sample/javacode/DisplayTime.java

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   README.md

Untracked files:
  (use "git add <file>..." to include in what will be committed)
        gradle/maven-to-gradle/src/main/java/com/sample/javacode/TimeZones.java复制
```

在这里，我们可以看到我们的工作目录中有三类文件：

-   暂存文件 – DisplayTime.java
-   未暂存文件 – README.md
-   未跟踪的文件 – TimeZones.java

## 3.删除未跟踪的文件

未跟踪文件是存储库中的新文件，尚未添加到版本控制中。我们可以使用clean命令删除它们：

```plaintext
$ git clean -df复制
```

-df选项确保强制删除，并且还包括未跟踪的目录以供删除。运行此命令将输出删除了哪些文件：

```bash
Removing gradle/maven-to-gradle/src/main/java/com/sample/javacode/TimeZones.java复制
```

现在，让我们再次检查状态：

```bash
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
        modified:   gradle/maven-to-gradle/src/main/java/com/sample/javacode/DisplayTime.java

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
        modified:   README.md复制
```

我们可以看到clean命令如何从我们的工作目录中删除未跟踪的文件。

## 4.删除未暂存提交的更改

现在我们已经删除了未跟踪的文件，我们剩下来处理工作目录中的暂存和未暂存文件。我们可以使用带有“–”选项的checkout命令来删除所有未暂存提交的更改：

```plaintext
$ git checkout -- .复制
```

运行命令后，让我们再次检查状态：

```bash
$ git status
On branch master
Your branch is up to date with 'origin/master'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
        modified:   gradle/maven-to-gradle/src/main/java/com/sample/javacode/DisplayTime.java复制
```

我们可以看到我们的工作目录现在只包含暂存的更改。

## 5.总结

在本教程中，我们了解了我们的工作目录如何在当前不受 Git 版本控制的文件中包含已暂存、未暂存和未跟踪的文件。

我们还看到了git clean -df和git checkout — 是如何工作的。可以从我们的工作目录中删除所有未暂存的更改。
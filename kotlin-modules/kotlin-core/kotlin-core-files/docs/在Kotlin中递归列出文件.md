## 1. 概述

使用某种形式的输入(例如文件)对任何开发人员来说都是生计。[Java 已经提供了几种与文件交互的方式](https://www.baeldung.com/java-list-directory-files)，但是 Kotlin 扩展了这个功能来帮助我们更简单地遍历文件树。

在本快速教程中，我们将学习如何使用标准 Kotlin 方法以递归方式列出目录树中的所有文件。

## 2.递归列出文件

Kotlin 添加了三个可用于导航文件树的扩展方法：walk()、walkTopDown()和walkBottomUp()。

稍后我们将仔细研究它们，但首先，了解它们调用的类很重要：FileTreeWalk。

### 2.1. 文件树遍历

无论我们选择这三种方法中的哪一种，它们都使用下面的FileTreeWalk来完成这项工作。

该类本身有两个重要特征。首先，它的构造函数是私有的，所以我们使用这个类的唯一方法就是通过上述方法。

其次，更重要的是，它扩展了一个Sequence<File>。这意味着我们可以使用 Kotlin 提供的所有迭代方法(如map和 forEach)。

FileTreeWalk实现了两种遍历方法，允许我们遍历目录中的所有文件：自上而下和自下而上。它们都是深度优先搜索，它们之间的主要区别在于是在文件之前还是之后访问目录。

一旦我们查看下一节中的示例，就会很容易理解这种差异意味着什么。

### 2.2. 步行法_

Kotlin 暴露的主要文件树遍历方法是walk：

```kotlin
public fun File.walk(direction: FileWalkDirection = FileWalkDirection.TOP_DOWN): FileTreeWalk 
  = FileTreeWalk(this, direction)
```

它是 Java 的File类的扩展方法，默认情况下，它执行自上而下的迭代。

假设我们有以下文件结构：

```powershell
src/test/resources
├── one-in
│   ├── empty-folder
│   ├── one-in-file.md
│   └── two-in
│       ├── two-in-1.md
│       └── two-in-2.md
└── root-file.md
```

当我们使用walk查看这个结构时：

```kotlin
File("src/test/resources").walk().forEach {
    println(it)
}
```

我们将看到以下输出：

```powershell
src/test/resources
src/test/resources/root-file.md
src/test/resources/one-in
src/test/resources/one-in/one-in-file.md
src/test/resources/one-in/empty-folder
src/test/resources/one-in/two-in
src/test/resources/one-in/two-in/two-in-2.md
src/test/resources/one-in/two-in/two-in-1.md
```

现在，让我们将行走方向更改为自下而上：

```kotlin
File("src/test/resources").walk(FileWalkDirection.BOTTOM_UP).forEach {
    println(it)
}
```

执行后，我们会看到不同的输出：

```powershell
src/test/resources/root-file.md
src/test/resources/one-in/one-in-file.md
src/test/resources/one-in/empty-folder
src/test/resources/one-in/two-in/two-in-2.md
src/test/resources/one-in/two-in/two-in-1.md
src/test/resources/one-in/two-in
src/test/resources/one-in
src/test/resources
```

我们可以看到自上而下的遍历导致先访问目录然后访问文件，而自下而上的遍历导致先访问文件然后访问目录。

将枚举作为步行参数传递并不是最漂亮的景象。值得庆幸的是，我们不必在每次使用walk时都手动提供FileWalkDirection。像往常一样，Kotlin 提供了一些方便的速记。

### 2.3. 速记

Kotlin 为walk提供了两个方便(且更易读)的简写形式—— walkTopDown和walkBottomUp：

```kotlin
File("src/test/resources").walkTopDown().forEach {
    println(it)
}

File("src/test/resources").walkBottomUp().forEach {
    println(it)
}
```

它们完全符合我们的预期——第一次运行采用默认的自上而下模式，第二次运行采用自下而上模式。

## 3.总结

在本文中，我们了解了Kotlin 如何帮助我们轻松导航文件树。
## 1. 简介

JGit 是 Git 版本控制系统的轻量级、纯Java库实现——包括存储库访问例程、网络协议和核心版本控制算法。

JGit 是用Java编写的 Git 的一个功能比较全的实现，在Java社区中被广泛使用。JGit 项目在 Eclipse 保护伞下，可以在[JGit](https://eclipse.org/jgit/)找到它的主页。

在本教程中，我们将解释如何使用它。

## 2. 开始

有多种方法可以将的项目与 JGit 连接起来并开始编写代码。可能最简单的方法是使用 Maven——通过将以下代码片段添加到我们的pom.xml文件中的<dependencies>标记来完成集成：

```xml
<dependency>
    <groupId>org.eclipse.jgit</groupId>
    <artifactId>org.eclipse.jgit</artifactId>
    <version>4.6.0.201612231935-r</version>
</dependency>
```

请访问[Maven Central 存储库](https://search.maven.org/classic/#search|ga|1|a%3A"org.eclipse.jgit")以获取最新版本的 JGit。完成此步骤后，Maven 将自动获取并使用我们需要的 JGit 库。

如果更喜欢 OSGi 包，还有一个 p2 存储库。请访问[Eclipse JGit](https://www.eclipse.org/jgit/download/)以获取有关如何集成此库的必要信息。

## 3.创建存储库

JGit 有两个基本级别的 API：plumbing和porcelain。这些术语来自 Git 本身。JGit 分为相同的区域：

-   瓷器API——常见用户级操作的前端(类似于 Git 命令行工具)
-   管道API——直接与低级存储库对象交互

大多数 JGit 会话的起点是Repository类。我们要做的第一件事是创建一个新的Repository实例。

init命令将让我们创建一个空的存储库：

```java
Git git = Git.init().setDirectory("/path/to/repo").call();
```

这将在给定的位置创建一个带有工作目录的存储库setDirectory()。

可以使用cloneRepository命令克隆现有存储库：

```java
Git git = Git.cloneRepository()
  .setURI("https://github.com/eclipse/jgit.git")
  .setDirectory("/path/to/repo")
  .call();
```

上面的代码会将 JGit 存储库克隆到名为path/to/repo的本地目录中。

## 4.Git 对象

所有对象都由 Git 对象模型中的 SHA-1 id 表示。在 JGit 中，这由AnyObjectId和ObjectId类表示。

Git 对象模型中有四种类型的对象：

-   blob——用于存储文件数据
-   树——一个目录；它引用其他树和斑点
-   commit——指向一棵树
-   tag – 将提交标记为特殊；通常用于标记特定版本

要从存储库中解析对象，只需像以下函数一样传递正确的修订：

```java
ObjectId head = repository.resolve("HEAD");
```

### 4.1. 参考

Ref是一个包含单个对象标识符的变量。对象标识符可以是任何有效的 Git 对象(blob、tree、commit、tag)。

例如，要查询对 head 的引用，可以简单地调用：

```java
Ref HEAD = repository.getRef("refs/heads/master");
```

### 4.2. 复步

RevWalk遍历提交图并按顺序生成匹配的提交：

```java
RevWalk walk = new RevWalk(repository);
```

### 4.3. 修订提交

RevCommit表示 Git 对象模型中的提交。要解析提交，请使用RevWalk实例：

```java
RevWalk walk = new RevWalk(repository);
RevCommit commit = walk.parseCommit(objectIdOfCommit);
```

### 4.4. 版本标签

RevTag表示 Git 对象模型中的标签。可以使用RevWalk实例来解析标签：

```java
RevWalk walk = new RevWalk(repository);
RevTag tag = walk.parseTag(objectIdOfTag);
```

### 4.5. 牧师树

RevTree表示 Git 对象模型中的树。RevWalk实例也用于解析树：

```java
RevWalk walk = new RevWalk(repository);
RevTree tree = walk.parseTree(objectIdOfTree);
```

## 5.瓷API

虽然 JGit 包含许多与 Git 存储库一起工作的低级代码，但它还包含一个更高级别的 API，它模仿org.eclipse.jgit.api包中的一些 Git瓷器命令。

### 5.1. 添加命令( git-add )

AddCommand允许通过以下方式将文件添加到索引：

-   添加文件模式()

以下是如何使用porcelain API 将一组文件添加到索引的快速示例：

```java
Git git = new Git(db);
AddCommand add = git.add();
add.addFilepattern("someDirectory").call();
```

### 5.2. 提交命令( git-commit )

CommitCommand允许执行提交并具有以下可用选项：

-   设置作者()
-   setCommitter ()
-   设置所有()

以下是如何使用porcelain API 进行提交的简单示例：

```java
Git git = new Git(db);
CommitCommand commit = git.commit();
commit.setMessage("initial commit").call();
```

### 5.3. 标签命令( git-tag )

TagCommand支持多种标记选项：

-   设置名称()
-   设置消息()
-   设置标记器()
-   setObjectId ()
-   setForceUpdate ()
-   设置签名()

下面是使用porcelain API标记提交的快速示例：

```java
Git git = new Git(db);
RevCommit commit = git.commit().setMessage("initial commit").call();
RevTag tag = git.tag().setName("tag").call();
```

### 5.4. 日志命令( git-log )

LogCommand允许轻松遍历提交图。

-   添加(AnyObjectId 开始)
-   addRange(AnyObjectId 自，AnyObjectId 直到)

以下是如何获取一些日志消息的快速示例：

```java
Git git = new Git(db);
Iterable<RevCommit> log = git.log().call();
```

## 6.蚂蚁任务

JGit 也有一些常见的 Ant 任务包含在org.eclipse.jgit.ant包中。

要使用这些任务：

```xml
<taskdef resource="org/eclipse/jgit/ant/ant-tasks.properties">
    <classpath>
        <pathelement location="path/to/org.eclipse.jgit.ant-VERSION.jar"/>
        <pathelement location="path/to/org.eclipse.jgit-VERSION.jar"/>
        <pathelement location="path/to/jsch-0.1.44-1.jar"/>
    </classpath>
</taskdef>
```

这将提供git-clone、git-init和git-checkout任务。

### 6.1. git 克隆

```xml
<git-clone uri="http://egit.eclipse.org/jgit.git" />
```

需要以下属性：

-   uri：要从中克隆的 URI

以下属性是可选的：

-   dest：要克隆到的目标(默认使用基于URI的最后一个路径组件的人类可读目录名称)
-   bare : true / false / yes / no指示克隆的存储库是否应该是裸的(默认为false)
-   branch：克隆存储库时要检查的初始分支(默认为HEAD)

### 6.2. 温暖的

```xml
<git-init />
```

运行git-init任务不需要任何属性。

以下属性是可选的：

-   dest：初始化 git 仓库的路径(默认为$GIT_DIR或当前目录)
-   bare：true / false / yes / no指示存储库是否应该是 bare (默认为false)

### 6.3. git-checkout

```xml
<git-checkout src="path/to/repo" branch="origin/newbranch" />
```

需要以下属性：

-   src : git 仓库的路径
-   branch : 结帐的初始分支

以下属性是可选的：

-   createbranch : true / false / yes / no表示分支不存在时是否应该创建(默认为false)
-   force : true / false / yes / no : 如果true / yes并且具有给定名称的分支已经存在，则现有分支的起点将设置为新的起点；如果为false，则现有分支不会更改(默认为false)

## 七. 总结

高级 JGit API 并不难理解。如果你知道使用什么 git 命令，你可以很容易地猜出在 JGit 中使用哪些类和方法。

[此处](https://github.com/eugenp/tutorials/tree/master/jgit)提供了一组随时可用的 JGit 代码片段。

如果仍有困难或疑问，请在这里发表评论或向[JGit 社区](https://www.eclipse.org/jgit/support/)寻求帮助。
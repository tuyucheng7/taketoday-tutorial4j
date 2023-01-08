## 1. 概述

在本教程中，我们将学习如何在Java中从两个绝对路径构造一个相对路径。我们将关注两个内置的JavaAPI——新的 I/O (NIO2) Path API 和 URI 类。

## 2. 绝对路径与相对路径

在我们开始之前，让我们快速回顾一下。对于文本中的所有示例，我们将在用户主目录中使用相同的文件结构：

```lua
/ (root)
|-- baeldung
    -- bar
    |   |-- one.txt
    |   |-- two.txt
    -- foo
        |-- three.txt
```

绝对路径描述了一个位置，与当前工作目录无关，从根节点开始。以下是我们文件的绝对路径：

```javascript
one.txt -> /baeldung/bar/one.txt
two.txt -> /baeldung/bar/two.txt
three.txt -> /baeldung/foo/three.txt
```

即使我们更改工作目录，绝对路径也始终保持不变。

另一方面，相对路径描述了目标节点相对于源节点的位置。如果我们在baeldung目录中，让我们看看文件的相对路径：

```rust
one.txt -> ./bar/one.txt
two.txt -> ./bar/two.txt
three.txt -> ./foo/three.txt
```

现在，让我们移至bar子目录并再次检查相对路径：

```rust
one.txt -> ./one.txt
two.txt -> ./two.txt
three.txt -> ../foo/three.txt
```

正如我们所看到的，结果略有不同。我们必须记住，如果我们修改源上下文，相对值可能会改变，而绝对路径是不变的。绝对路径是相对路径的特例，其中源节点是系统的根。

## 3. NIO2 API

现在我们知道了相对路径和绝对路径是如何工作的，是时候[查看 NIO2 API 了](https://www.baeldung.com/java-nio-2-file-api)。众所周知，NIO2 API 是随着Java7 的发布而引入的，它改进了旧的 I/O API，它有很多缺陷。使用此 API，我们将尝试确定由绝对路径描述的两个文件之间的相对路径。

让我们从为我们的文件构建Path对象开始：

```java
Path pathOne = Paths.get("/baeldung/bar/one.txt");
Path pathTwo = Paths.get("/baeldung/bar/two.txt");
Path pathThree = Paths.get("/baeldung/foo/three.txt");
```

要在源和给定节点之间构建相对路径，我们可以使用Path类提供的[relativize(Path)](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#relativize-java.nio.file.Path-)方法：

```java
Path result = pathOne.relativize(pathTwo);

assertThat(result)
  .isRelative()
  .isEqualTo(Paths.get("../two.txt"));
```

如我们所见，结果肯定是相对路径。那是对的吗？尤其是在父运算符 ( ../ ) 的开头？

我们必须记住，可以从任何类型的节点开始指定相对路径，可以是目录或文件。特别是当我们使用 CLI 或浏览器时，我们使用的是目录。然后根据当前工作目录计算所有相对路径。

在我们的示例中，我们创建了指向特定文件的路径。所以我们首先需要到达文件的父目录，它的目录，然后转到第二个文件。总的来说，结果是正确的。

如果我们想让结果相对于源目录，我们可以使用[getParent()](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#getParent--)方法：

```java
Path result = pathOne.getParent().relativize(pathTwo);

assertThat(result)
  .isRelative()
  .isEqualTo(Paths.get("two.txt"));
```

我们应该注意到Path对象可以指向任何文件或目录。如果我们正在构建更复杂的逻辑，我们需要提供额外的检查。

最后，让我们检查one.txt和three.txt文件之间的相对路径：

```java
Path resultOneToThree = pathOne.relativize(pathThree);
Path resultThreeToOne = pathThree.relativize(pathOne);

assertThat(resultOneToThree)
  .isRelative()
  .isEqualTo(Paths.get("....foothree.txt"));
assertThat(result)
  .isRelative()
  .isEqualTo(Paths.get("....barone.txt"));

```

这个快速测试确认相对路径是上下文相关的。虽然绝对路径仍然相同，但当我们将源节点和目标节点交换在一起时，相对路径会有所不同。

## 4. java.net.URI API

检查完NIO2 API后，我们进入java.net.URI类。我们知道[URI](https://www.baeldung.com/cs/uniform-resource-identifiers)(统一资源标识符)是一串字符，它使我们能够识别在处理文件时也可以使用的任何资源。

让我们为我们的文件构建URI对象：

```java
URI uriOne = pathOne.toURI();
// URI uriOne = URI.create("file:///baeldung/bar/one.txt")
URI uriTwo = pathTwo.toURI();
URI uriThree = pathThree.toURI();
```

我们可以使用String构造URI对象或转换先前创建的Path。

和以前一样，URI类也提供了[relativize(URI)](https://docs.oracle.com/javase/8/docs/api/java/net/URI.html#relativize-java.net.URI-)方法。让我们用它来构造相对路径：

```java
URI result = uriOne.relativize(uriTwo);

assertThat(result)
  .asString()
  .contains("file:///baeldung/bar/two.txt");
```

结果不是我们所期望的，相对路径没有被正确构建。要回答为什么会这样，我们需要查看[该类的官方文档](https://docs.oracle.com/javase/8/docs/api/java/net/URI.html)。

如果源 URI 是目标 URI 的前缀，则此方法仅返回相对值。否则，它返回目标值。因此，我们无法在文件节点之间建立相对路径。在这种情况下，一个 URI 永远不会为另一个添加前缀。

要返回相对路径，我们可以将源URI设置为第一个文件的目录：

```java
URI uriOneParent = pathOne.getParent().toUri(); // file:///baeldung/bar/
URI result = uriOneParent.relativize(uriTwo);

assertThat(result)
  .asString()
  .contains("two.txt");
```

现在源节点是目标前缀，所以结果计算正确。由于该方法的限制，我们无法通过URI方法确定one.txt/two.txt和three.txt文件之间的相对路径。他们的目录没有共同的前缀。

## 5.总结

在本文中，我们首先了解绝对路径和相对路径之间的主要区别。

接下来，我们在两个文件之间构造了一个由绝对路径描述的相对路径。我们从检查 NIO2 API 开始，详细介绍了相关路径构建过程。

最后，我们尝试使用java.net.URI类实现相同的结果。我们发现由于其限制，我们无法使用此 API 进行所有转换。
## 1. 概述

在本快速教程中，我们将熟悉检查文件或目录是否存在的不同方法。

首先，我们将从现代 NIO API 开始，然后介绍传统的 IO 方法。

## 2.使用java.nio.file.Files

要检查文件或目录是否存在，我们可以利用[Files.exists(Path)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#exists(java.nio.file.Path,java.nio.file.LinkOption...)) 方法。从方法签名中可以清楚地看出，我们应该首先[获得 ](https://www.baeldung.com/java-nio-2-path)目标文件或目录的[路径。](https://www.baeldung.com/java-nio-2-path)然后我们可以将该 [Path](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html) 传递给Files.exists(Path) 方法：

```java
Path path = Paths.get("does-not-exist.txt");
assertFalse(Files.exists(path));
```

由于该文件不存在，因此它返回 false。还值得一提的是，如果Files.exists(Path) 方法遇到 IOException，它也会返回 false。

另一方面，当给定文件存在时，它将 按预期返回true ：

```java
Path tempFile = Files.createTempFile("baeldung", "exist-article");
assertTrue(Files.exists(tempFile));
```

这里我们创建一个临时文件，然后调用Files.exists(Path) 方法。

这甚至适用于目录：

```java
Path tempDirectory = Files.createTempDirectory("baeldung-exists");
assertTrue(Files.exists(tempDirectory));
```

如果我们特别想知道一个文件或目录是否存在，我们也可以使用 [Files.isDirectory(Path)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isDirectory(java.nio.file.Path,java.nio.file.LinkOption...)) 或[Files.isRegularFile(Path)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isRegularFile(java.nio.file.Path,java.nio.file.LinkOption...)) 方法：

```java
assertTrue(Files.isDirectory(tempDirectory));
assertFalse(Files.isDirectory(tempFile));
assertTrue(Files.isRegularFile(tempFile));
```

如果给定的 Path 不存在，还有一个 [notExists(Path)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#notExists(java.nio.file.Path,java.nio.file.LinkOption...)) 方法返回true ：

```java
assertFalse(Files.notExists(tempDirectory));
```

有时 Files.exists(Path) 返回 false 因为我们不具备所需的文件权限。在这种情况下，我们可以使用 [Files.isReadable(Path)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isReadable(java.nio.file.Path)) 方法来确保当前用户实际上可以读取该文件：

```java
assertTrue(Files.isReadable(tempFile));
assertFalse(Files.isReadable(Paths.get("/root/.bashrc")));
```

### 2.1. 符号链接

默认情况下， Files.exists(Path) 方法遵循符号链接。如果文件A 具有指向文件B的符号链接 ，则 当且仅当文件 B 已经存在时， Files.exists(A)方法才返回 true ：

```java
Path target = Files.createTempFile("baeldung", "target");
Path symbol = Paths.get("test-link-" + ThreadLocalRandom.current().nextInt());
Path symbolicLink = Files.createSymbolicLink(symbol, target);
assertTrue(Files.exists(symbolicLink));
```

现在，如果我们删除链接的目标， Files.exists(Path) 将返回 false：

```java
Files.deleteIfExists(target);
assertFalse(Files.exists(symbolicLink));
```

由于链接目标不再存在，因此点击链接不会导致任何结果，并且Files.exists(Path)将返回false。

[通过将适当的LinkOption](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/LinkOption.html) 作为第二个参数传递，甚至可以不跟随符号链接 ：

```java
assertTrue(Files.exists(symbolicLink, LinkOption.NOFOLLOW_LINKS));
```

因为链接本身存在，所以 Files.exists(Path) 方法返回 true。 此外，我们可以 使用[Files.isSymbolicLink(Path)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path))方法检查Path 是否为符号链接 ： 

```java
assertTrue(Files.isSymbolicLink(symbolicLink));
assertFalse(Files.isSymbolicLink(target));
```

## 3. 使用java.io.File

如果我们使用Java7 或更新版本的 Java，强烈建议使用现代JavaNIO API 来满足这些需求。

但是，为了确保文件或目录是否存在于Java遗留 IO 世界中，我们可以在[File实例上调用](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html)[exists()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#exists(java.nio.file.Path,java.nio.file.LinkOption...)) 方法 ： 

```java
assertFalse(new File("invalid").exists());
```

如果文件或目录确实已经存在，它将返回 true：

```java
Path tempFilePath = Files.createTempFile("baeldung", "exist-io");
Path tempDirectoryPath = Files.createTempDirectory("baeldung-exists-io");

File tempFile = new File(tempFilePath.toString());
File tempDirectory = new File(tempDirectoryPath.toString());

assertTrue(tempFile.exists());
assertTrue(tempDirectory.exists());
```

如上所示，exists () 方法不关心它是文件还是目录。因此，只要它确实存在，它就会返回 true。 

但是， 如果给定路径是现有文件，则isFile [() ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#isFile())方法返回true ：

```java
assertTrue(tempFile.isFile());
assertFalse(tempDirectory.isFile());
```

类似地， 如果给定路径是现有目录，则[isDirectory()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#isDirectory()) 方法返回 true ：

```java
assertTrue(tempDirectory.isDirectory());
assertFalse(tempFile.isDirectory());
```

最后， 如果文件可读，则[canRead()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#canRead()) 方法返回 true ：

```java
assertTrue(tempFile.canRead());
assertFalse(new File("/root/.bashrc").canRead());
```

当它返回 false时，文件不存在或当前用户不拥有文件的读取权限。

## 4。总结

在这个简短的教程中，我们了解了如何确保文件或目录在Java中存在。一路上，我们讨论了现代 NIO 和遗留 IO API。此外，我们还了解了 NIO API 如何处理符号链接。
## 1. 概述

在本文中，我们将重点关注Java平台中的新 I/O API – NIO2 – 以执行基本的文件操作。

NIO2 中的文件 API 构成了Java7 附带的Java平台的主要新功能区域之一，特别是新文件系统 API 的子集以及路径 API。

## 2.设置

设置你的项目以使用文件 API 只是进行此导入的问题：

```java
import java.nio.file.;
```

由于本文中的代码示例可能会在不同的环境中运行，因此让我们了解用户主目录的句柄，这对所有操作系统都有效：

```java
private static String HOME = System.getProperty("user.home");
```

Files类是java.nio.file包的主要入口点之一。此类提供了一组丰富的 API，用于读取、写入和操作文件和目录。Files类方法适用于Path对象的实例。

## 3. 检查文件或目录

我们可以有一个Path实例代表文件系统上的文件或目录。它指向的文件或目录是否存在，是否可访问，可以通过文件操作来确认。

为了简单起见，除非另有明确说明，否则每当我们使用术语文件时，我们都会指代文件和目录。

要检查文件是否存在，我们使用exists API：

```java
@Test
public void givenExistentPath_whenConfirmsFileExists_thenCorrect() {
    Path p = Paths.get(HOME);

    assertTrue(Files.exists(p));
}
```

要检查文件是否不存在，我们使用notExists API：

```java
@Test
public void givenNonexistentPath_whenConfirmsFileNotExists_thenCorrect() {
    Path p = Paths.get(HOME + "/inexistent_file.txt");

    assertTrue(Files.notExists(p));
}
```

我们还可以检查一个文件是像myfile.txt这样的常规文件还是只是一个目录，我们使用isRegularFile API：

```java
@Test
public void givenDirPath_whenConfirmsNotRegularFile_thenCorrect() {
    Path p = Paths.get(HOME);

    assertFalse(Files.isRegularFile(p));
}
```

还有检查文件权限的静态方法。要检查文件是否可读，我们使用isReadable API：

```java
@Test
public void givenExistentDirPath_whenConfirmsReadable_thenCorrect() {
    Path p = Paths.get(HOME);

    assertTrue(Files.isReadable(p));
}
```

要检查它是否可写，我们使用isWritable API：

```java
@Test
public void givenExistentDirPath_whenConfirmsWritable_thenCorrect() {
    Path p = Paths.get(HOME);

    assertTrue(Files.isWritable(p));
}
```

同样，检查它是否可执行：

```java
@Test
public void givenExistentDirPath_whenConfirmsExecutable_thenCorrect() {
    Path p = Paths.get(HOME);
    assertTrue(Files.isExecutable(p));
}
```

当我们有两条路径时，我们可以检查它们是否都指向底层文件系统上的同一个文件：

```java
@Test
public void givenSameFilePaths_whenConfirmsIsSame_thenCorrect() {
    Path p1 = Paths.get(HOME);
    Path p2 = Paths.get(HOME);

    assertTrue(Files.isSameFile(p1, p2));
}
```

## 4. 创建文件

文件系统 API 提供用于创建文件的单行操作。要创建常规文件，我们使用createFile API 并向其传递一个表示我们要创建的文件的Path对象。

路径中的所有名称元素都必须存在，除了文件名，否则，我们将得到一个IOException：

```java
@Test
public void givenFilePath_whenCreatesNewFile_thenCorrect() {
    String fileName = "myfile_" + UUID.randomUUID().toString() + ".txt";
    Path p = Paths.get(HOME + "/" + fileName);
    assertFalse(Files.exists(p));

    Files.createFile(p);

    assertTrue(Files.exists(p));
}
```

在上面的测试中，当我们首先检查路径时，它是不存在的，然后在createFile操作之后，发现它是存在的。

要创建目录，我们使用createDirectory API：

```java
@Test
public void givenDirPath_whenCreatesNewDir_thenCorrect() {
    String dirName = "myDir_" + UUID.randomUUID().toString();
    Path p = Paths.get(HOME + "/" + dirName);
    assertFalse(Files.exists(p));

    Files.createDirectory(p);

    assertTrue(Files.exists(p));
    assertFalse(Files.isRegularFile(p));
    assertTrue(Files.isDirectory(p));
}
```

此操作要求路径中的所有名称元素都存在，如果不存在，我们也会得到一个IOException：

```java
@Test(expected = NoSuchFileException.class)
public void givenDirPath_whenFailsToCreateRecursively_thenCorrect() {
    String dirName = "myDir_" + UUID.randomUUID().toString() + "/subdir";
    Path p = Paths.get(HOME + "/" + dirName);
    assertFalse(Files.exists(p));

    Files.createDirectory(p);
}
```

但是，如果我们希望通过一次调用创建目录层次结构，我们可以使用createDirectories方法。与前面的操作不同，当它在路径中遇到任何缺失的 name 元素时，它不会抛出 IOException ，而是递归地创建它们直到最后一个元素：

```java
@Test
public void givenDirPath_whenCreatesRecursively_thenCorrect() {
    Path dir = Paths.get(
      HOME + "/myDir_" + UUID.randomUUID().toString());
    Path subdir = dir.resolve("subdir");
    assertFalse(Files.exists(dir));
    assertFalse(Files.exists(subdir));

    Files.createDirectories(subdir);

    assertTrue(Files.exists(dir));
    assertTrue(Files.exists(subdir));
}
```

## 5. 创建临时文件

许多应用程序在运行时会在文件系统中创建一系列临时文件。因此，大多数文件系统都有专门的目录来存储此类应用程序生成的临时文件。

新的文件系统 API 为此提供了特定的操作。createTempFile API 执行此操作。它需要一个路径对象、一个文件前缀和一个文件后缀：

```java
@Test
public void givenFilePath_whenCreatesTempFile_thenCorrect() {
    String prefix = "log_";
    String suffix = ".txt";
    Path p = Paths.get(HOME + "/");

    Files.createTempFile(p, prefix, suffix);
        
    assertTrue(Files.exists(p));
}
```

这些参数足以满足需要此操作的要求。但是，如果你需要指定文件的特定属性，则还有第四个可变 arguments 参数。

上面的测试在HOME目录中创建了一个临时文件，分别预先挂起和附加提供的前缀和后缀字符串。我们最终会得到一个类似log_8821081429012075286.txt的文件名。长数字字符串是系统生成的。

但是，如果我们不提供前缀和后缀，则文件名将仅包含长数字字符串和默认的.tmp扩展名：

```java
@Test
public void givenPath_whenCreatesTempFileWithDefaults_thenCorrect() {
    Path p = Paths.get(HOME + "/");

    Files.createTempFile(p, null, null);
        
    assertTrue(Files.exists(p));
}
```

上述操作创建了一个名称类似于8600179353689423985.tmp的文件。

最后，如果我们既不提供路径、前缀也不提供后缀，那么操作将始终使用默认值。创建的文件的默认位置将是文件系统提供的临时文件目录：

```java
@Test
public void givenNoFilePath_whenCreatesTempFileInTempDir_thenCorrect() {
    Path p = Files.createTempFile(null, null);

    assertTrue(Files.exists(p));
}
```

在 Windows 上，这将默认为类似C:UsersuserAppDataLocalTemp6100927974988978748.tmp 的内容。

通过使用createTempDirectory而不是createTempFile，所有上述操作都可以适应创建目录而不是常规文件。

## 6.删除文件

要删除文件，我们使用删除API。为清楚起见，以下测试首先确保该文件不存在，然后创建它并确认它现在存在，最后删除它并确认它不再存在：

```java
@Test
public void givenPath_whenDeletes_thenCorrect() {
    Path p = Paths.get(HOME + "/fileToDelete.txt");
    assertFalse(Files.exists(p));
    Files.createFile(p);
    assertTrue(Files.exists(p));

    Files.delete(p);

    assertFalse(Files.exists(p));
}
```

但是，如果文件系统中不存在文件，则删除操作将失败并出现IOException：

```java
@Test(expected = NoSuchFileException.class)
public void givenInexistentFile_whenDeleteFails_thenCorrect() {
    Path p = Paths.get(HOME + "/inexistentFile.txt");
    assertFalse(Files.exists(p));

    Files.delete(p);
}
```

我们可以通过使用deleteIfExists来避免这种情况，如果文件不存在，它会自动失败。当多个线程正在执行此操作时，这一点很重要，我们不希望仅仅因为一个线程比当前失败的线程更早执行操作而出现失败消息：

```java
@Test
public void givenInexistentFile_whenDeleteIfExistsWorks_thenCorrect() {
    Path p = Paths.get(HOME + "/inexistentFile.txt");
    assertFalse(Files.exists(p));

    Files.deleteIfExists(p);
}
```

当处理目录而不是常规文件时，我们应该记住删除操作默认情况下不会递归进行。因此，如果目录不为空，它将因IOException而失败：

```java
@Test(expected = DirectoryNotEmptyException.class)
public void givenPath_whenFailsToDeleteNonEmptyDir_thenCorrect() {
    Path dir = Paths.get(
      HOME + "/emptyDir" + UUID.randomUUID().toString());
    Files.createDirectory(dir);
    assertTrue(Files.exists(dir));

    Path file = dir.resolve("file.txt");
    Files.createFile(file);

    Files.delete(dir);

    assertTrue(Files.exists(dir));
}
```

## 7.文件

你可以使用API 文件或目录：

```java
@Test
public void givenFilePath_whenCopiesToNewLocation_thenCorrect() {
    Path dir1 = Paths.get(
      HOME + "/firstdir_" + UUID.randomUUID().toString());
    Path dir2 = Paths.get(
      HOME + "/otherdir_" + UUID.randomUUID().toString());

    Files.createDirectory(dir1);
    Files.createDirectory(dir2);

    Path file1 = dir1.resolve("filetocopy.txt");
    Path file2 = dir2.resolve("filetocopy.txt");

    Files.createFile(file1);

    assertTrue(Files.exists(file1));
    assertFalse(Files.exists(file2));

    Files.copy(file1, file2);

    assertTrue(Files.exists(file2));
}
```

如果目标文件存在，则失败，除非指定了REPLACE_EXISTING选项：

```java
@Test(expected = FileAlreadyExistsException.class)
public void givenPath_whenCopyFailsDueToExistingFile_thenCorrect() {
    Path dir1 = Paths.get(
      HOME + "/firstdir_" + UUID.randomUUID().toString());
    Path dir2 = Paths.get(
      HOME + "/otherdir_" + UUID.randomUUID().toString());

    Files.createDirectory(dir1);
    Files.createDirectory(dir2);

    Path file1 = dir1.resolve("filetocopy.txt");
    Path file2 = dir2.resolve("filetocopy.txt");

    Files.createFile(file1);
    Files.createFile(file2);

    assertTrue(Files.exists(file1));
    assertTrue(Files.exists(file2));

    Files.copy(file1, file2);

    Files.copy(file1, file2, StandardCopyOption.REPLACE_EXISTING);
}
```

但是，在目录时，不会递归内容。这意味着如果/baeldung包含/articles.db和/authors.db文件，将/baeldung到新位置将创建一个空目录。

## 8.移动文件

你可以使用移动API 移动文件或目录。它在大多数方面类似于操作。如果操作类似于基于 GUI 的系统中的和粘贴操作，那么移动类似于剪切和粘贴操作：

```java
@Test
public void givenFilePath_whenMovesToNewLocation_thenCorrect() {
    Path dir1 = Paths.get(
      HOME + "/firstdir_" + UUID.randomUUID().toString());
    Path dir2 = Paths.get(
      HOME + "/otherdir_" + UUID.randomUUID().toString());

    Files.createDirectory(dir1);
    Files.createDirectory(dir2);

    Path file1 = dir1.resolve("filetocopy.txt");
    Path file2 = dir2.resolve("filetocopy.txt");
    Files.createFile(file1);

    assertTrue(Files.exists(file1));
    assertFalse(Files.exists(file2));

    Files.move(file1, file2);

    assertTrue(Files.exists(file2));
    assertFalse(Files.exists(file1));
}
```

如果目标文件存在，则移动操作将失败，除非像操作那样指定了REPLACE_EXISTING选项：

```java
@Test(expected = FileAlreadyExistsException.class)
public void givenFilePath_whenMoveFailsDueToExistingFile_thenCorrect() {
    Path dir1 = Paths.get(
      HOME + "/firstdir_" + UUID.randomUUID().toString());
    Path dir2 = Paths.get(
      HOME + "/otherdir_" + UUID.randomUUID().toString());

    Files.createDirectory(dir1);
    Files.createDirectory(dir2);

    Path file1 = dir1.resolve("filetocopy.txt");
    Path file2 = dir2.resolve("filetocopy.txt");

    Files.createFile(file1);
    Files.createFile(file2);

    assertTrue(Files.exists(file1));
    assertTrue(Files.exists(file2));

    Files.move(file1, file2);

    Files.move(file1, file2, StandardCopyOption.REPLACE_EXISTING);

    assertTrue(Files.exists(file2));
    assertFalse(Files.exists(file1));
}
```

## 9.总结

在本文中，我们了解了作为Java7 的一部分发布的新文件系统 API (NIO2) 中的文件 API，并了解了大部分重要的文件操作。
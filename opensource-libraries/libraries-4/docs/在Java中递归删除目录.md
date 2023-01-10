## 1. 简介

在本文中，我们将说明如何在纯Java中递归删除目录。我们还将研究使用外部库删除目录的一些替代方法。

## 2.递归删除目录

Java 有一个删除目录的选项。但是，这要求目录为空。因此，我们需要使用递归来删除特定的非空目录：

1.  获取待删除目录的所有内容
2.  删除所有不是目录的子项(退出递归)
3.  对于当前目录的每个子目录，从第1步开始(递归步骤)
4.  删除目录

让我们实现这个简单的算法：

```java
boolean deleteDirectory(File directoryToBeDeleted) {
    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
        for (File file : allContents) {
            deleteDirectory(file);
        }
    }
    return directoryToBeDeleted.delete();
}
```

可以使用一个简单的测试用例来测试此方法：

```java
@Test
public void givenDirectory_whenDeletedWithRecursion_thenIsGone() 
  throws IOException {
 
    Path pathToBeDeleted = TEMP_DIRECTORY.resolve(DIRECTORY_NAME);

    boolean result = deleteDirectory(pathToBeDeleted.toFile());

    assertTrue(result);
    assertFalse(
      "Directory still exists", 
      Files.exists(pathToBeDeleted));
}
```

我们测试类的@Before方法在pathToBeDeleted位置创建一个包含子目录和文件的目录树，@ After方法在需要时清理目录。

接下来，我们来看看如何使用两个最常用的库——Apache 的commons-io和Spring Framework 的spring-core 来实现删除。这两个库都允许我们使用一行代码来删除目录。

## 3. 使用commons -io 中的FileUtils

首先，我们需要在 Maven 项目中添加commons-io依赖：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"commons-io" AND a%3A"commons-io")找到最新版本的依赖项。

现在，我们可以使用FileUtils执行任何基于文件的操作，包括deleteDirectory()，只需一条语句：

```java
FileUtils.deleteDirectory(file);
```

## 4. 使用Spring 中的FileSystemUtils

或者，我们可以将 s pring-core依赖添加到 Maven 项目中：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>4.3.10.RELEASE</version>
</dependency>
```

可以在[此处找到最新版本的依赖项。](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework" AND a%3A"spring-core")

我们可以使用FileSystemUtils中的deleteRecursively()方法来执行删除：

```java
boolean result = FileSystemUtils.deleteRecursively(file);
```

最新版本的Java提供了更新的方法来执行以下部分中描述的此类 IO 操作。

## 5. 在Java7 中使用 NIO2

Java 7 引入了一种使用Files执行文件操作的全新方式。它允许我们遍历目录树并使用回调来执行要执行的操作。

```java
public void whenDeletedWithNIO2WalkFileTree_thenIsGone() 
  throws IOException {
 
    Path pathToBeDeleted = TEMP_DIRECTORY.resolve(DIRECTORY_NAME);

    Files.walkFileTree(pathToBeDeleted, 
      new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult postVisitDirectory(
          Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
        
        @Override
        public FileVisitResult visitFile(
          Path file, BasicFileAttributes attrs) 
          throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }
    });

    assertFalse("Directory still exists", 
      Files.exists(pathToBeDeleted));
}
```

Files.walkFileTree ()方法遍历文件树并发出事件。我们需要为这些事件指定回调。因此，在这种情况下，我们将定义SimpleFileVisitor以对生成的事件执行以下操作：

1.  访问一个文件——删除它
2.  在处理条目之前访问目录——什么也不做
3.  在处理完条目后访问目录 - 删除目录，因为现在该目录中的所有条目都已被处理(或删除)
4.  无法访问文件——重新抛出导致失败的IOException

有关处理文件操作的 NIO2 API 的更多详细信息，请参阅[Java NIO2 文件 API 简介。](https://www.baeldung.com/java-nio-2-file-api)

## 6.在Java8 中使用 NIO2

从Java8 开始，Stream API 提供了一种更好的删除目录的方法：

```java
@Test
public void whenDeletedWithFilesWalk_thenIsGone() 
  throws IOException {
    Path pathToBeDeleted = TEMP_DIRECTORY.resolve(DIRECTORY_NAME);

    Files.walk(pathToBeDeleted)
      .sorted(Comparator.reverseOrder())
      .map(Path::toFile)
      .forEach(File::delete);

    assertFalse("Directory still exists", 
      Files.exists(pathToBeDeleted));
}
```

在这里，Files.walk()返回我们按相反顺序排序的路径流。这会将表示目录内容的路径放在目录本身之前。此后它将路径映射到文件并删除每个文件。

## 七. 总结

在本快速教程中，我们探讨了删除目录的不同方法。在了解如何使用递归删除的同时，我们还了解了一些库、利用事件的 NIO2 和采用函数式编程范例的Java8 Path Stream。
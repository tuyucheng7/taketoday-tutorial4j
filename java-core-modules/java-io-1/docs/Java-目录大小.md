## 1. 概述

在本教程中，我们将学习如何使用Java6、7 和新的Java8 以及 Guava 和 Apache Common IO获取Java 中文件夹的大小。

最后 - 我们还将获得目录大小的人类可读表示。

## 2.用Java

让我们从一个计算文件夹大小的简单示例开始——使用其内容的总和：

```java
private long getFolderSize(File folder) {
    long length = 0;
    File[] files = folder.listFiles();

    int count = files.length;

    for (int i = 0; i < count; i++) {
        if (files[i].isFile()) {
            length += files[i].length();
        }
        else {
            length += getFolderSize(files[i]);
        }
    }
    return length;
}
```

我们可以测试我们的方法getFolderSize()，如下例所示：

```java
@Test
public void whenGetFolderSizeRecursive_thenCorrect() {
    long expectedSize = 12607;

    File folder = new File("src/test/resources");
    long size = getFolderSize(folder);

    assertEquals(expectedSize, size);
}
```

注意：listFiles()用于列出给定文件夹的内容。

## 3. 使用Java7

接下来 – 让我们看看如何使用Java 7 来获取文件夹大小。在下面的示例中——我们使用Files.walkFileTree()遍历文件夹中的所有文件以求出它们的大小：

```java
@Test
public void whenGetFolderSizeUsingJava7_thenCorrect() throws IOException {
    long expectedSize = 12607;

    AtomicLong size = new AtomicLong(0);
    Path folder = Paths.get("src/test/resources");

    Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
          throws IOException {
            size.addAndGet(attrs.size());
            return FileVisitResult.CONTINUE;
        }
    });

    assertEquals(expectedSize, size.longValue());
}
```

请注意我们如何在这里利用文件系统树遍历功能并利用访问者模式来帮助我们访问和计算每个文件和子文件夹的大小。

## 4. 使用Java8

现在 – 让我们看看如何使用Java 8、流操作和 lambda获取文件夹大小。在下面的示例中——我们使用Files.walk()遍历文件夹中的所有文件以求出它们的大小：

```java
@Test
public void whenGetFolderSizeUsingJava8_thenCorrect() throws IOException {
    long expectedSize = 12607;

    Path folder = Paths.get("src/test/resources");
    long size = Files.walk(folder)
      .filter(p -> p.toFile().isFile())
      .mapToLong(p -> p.toFile().length())
      .sum();

    assertEquals(expectedSize, size);
}
```

注意：mapToLong()用于通过在每个元素中应用长度函数来生成LongStream——之后我们可以求和并得到最终结果。

## 5. 使用 Apache Commons IO

接下来 – 让我们看看如何使用Apache Commons IO获取文件夹大小。在以下示例中——我们仅使用FileUtils.sizeOfDirectory()来获取文件夹大小：

```java
@Test
public void whenGetFolderSizeUsingApacheCommonsIO_thenCorrect() {
    long expectedSize = 12607;

    File folder = new File("src/test/resources");
    long size = FileUtils.sizeOfDirectory(folder);

    assertEquals(expectedSize, size);
}
```

请注意，这个直截了当的实用方法在幕后实现了一个简单的Java6 解决方案。

另外请注意，该库还提供了一个FileUtils.sizeOfDirectoryAsBigInteger()方法，可以更好地处理安全受限目录。

## 6.番石榴

现在 - 让我们看看如何使用Guava计算文件夹的大小。在下面的示例中——我们使用Files.fileTreeTraverser()遍历文件夹中的所有文件以求出它们的大小：

```java
@Test public void whenGetFolderSizeUsingGuava_thenCorrect() { 
    long expectedSize = 12607; 
    File folder = new File("src/test/resources"); 
   
    Iterable<File> files = Files.fileTraverser().breadthFirst(folder);
    long size = StreamSupport.stream(files.spliterator(), false) .filter(f -> f.isFile()) 
      .mapToLong(File::length).sum(); 
   
    assertEquals(expectedSize, size); 
}
```

## 7. 人类可读大小

最后 - 让我们看看如何获得更易读的文件夹大小表示 - 而不仅仅是字节大小：

```java
@Test
public void whenGetReadableSize_thenCorrect() {
    File folder = new File("src/test/resources");
    long size = getFolderSize(folder);

    String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
    int unitIndex = (int) (Math.log10(size) / 3);
    double unitValue = 1 << (unitIndex  10);

    String readableSize = new DecimalFormat("#,##0.#")
                                .format(size / unitValue) + " " 
                                + units[unitIndex];
    assertEquals("12.3 KB", readableSize);
}
```

注意：我们使用DecimalFormat(“#,##0,#”)将结果四舍五入到一位小数。

## 八、注意事项

以下是有关文件夹大小计算的一些注意事项：

-   如果安全管理器拒绝访问起始文件，则Files.walk()和Files.walkFileTree()都将抛出 SecurityException。
-   如果文件夹包含符号链接，可能会出现无限循环。

## 9.总结

在这个快速教程中，我们举例说明了使用不同的Java版本、Apache Commons IO和Guava来计算文件系统中目录的大小。
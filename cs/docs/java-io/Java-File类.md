## 1. 概述

在本教程中，我们将概述File类，它是java.io API的一部分 。 File 类使我们能够处理文件系统上的文件和目录。

## 2.创建文件对象

File 类 有 4 个公共构造函数。根据开发人员的需要，可以创建File类的不同类型的实例。

-   File(String pathname) – 创建一个代表给定路径名的实例
-   File(String parent, String child) – 创建一个实例，表示通过连接父路径和 子路径形成的路径 
-   File(File parent, String child) – 创建一个实例，其路径是通过连接由另一个File 实例表示的 父路径和子路径形成的
-   File(URI uri) – 创建一个代表给定统一资源标识符的实例

## 3. 使用文件类

File 类有许多方法可以让我们使用和操作文件系统上的文件。我们将在这里重点介绍其中的一些。重要的是要注意File类不能修改或访问它所代表的文件的内容。

### 3.1. 创建和删除目录和文件

File 类具有创建和删除目录和文件的实例方法。目录和文件分别使用mkdir和createNewFile方法创建。

使用 delete方法删除目录和文件。所有这些方法都返回一个布尔值，该值在操作成功时为true ，否则为false：

```java
@Test
public void givenDir_whenMkdir_thenDirIsDeleted() {
    File directory = new File("dir");
    assertTrue(directory.mkdir());
    assertTrue(directory.delete());
}

@Test
public void givenFile_whenCreateNewFile_thenFileIsDeleted() {
    File file = new File("file.txt");
    try {
        assertTrue(file.createNewFile());
    } catch (IOException e) {
        fail("Could not create " + "file.txt");
    }
    assertTrue(file.delete());
}
```

在上面的代码片段中，我们还看到了其他有用的方法。

isDirectory方法可用于测试提供的名称表示的文件是否为目录，而isFile方法可用于测试提供的名称表示的文件是否为文件。并且，我们可以使用exists方法 来测试系统中是否已经存在目录或文件。

### 3.2. 获取有关文件实例的元数据

File 类 有许多方法可以返回有关File 实例的元数据。让我们看看如何使用getName、getParentFile和 getPath方法：

```java
@Test
public void givenFile_whenCreateNewFile_thenMetadataIsCorrect() {

    String sep = File.separator;

    File parentDir = makeDir("filesDir");

    File child = new File(parentDir, "file.txt");
    try {
        child.createNewFile();
    } catch (IOException e) {
        fail("Could not create " + "file.txt");
    }

    assertEquals("file.txt", child.getName());
    assertEquals(parentDir.getName(), child.getParentFile().getName());
    assertEquals(parentDir.getPath() + sep + "file.txt", child.getPath());

    removeDir(parentDir);
}
```

在这里，我们说明了如何验证关于在目录中创建的文件的元数据。我们还展示了如何找到文件的父文件和该文件的相对路径。

### 3.3. 设置文件和目录权限

File 类 具有允许你设置文件或目录权限的方法。在这里，我们将看看setWritable和setReadable 方法：

```java
@Test
public void givenReadOnlyFile_whenCreateNewFile_thenCantModFile() {
    File parentDir = makeDir("readDir");

    File child = new File(parentDir, "file.txt");
    try {
        child.createNewFile();
    } catch (IOException e) {
        fail("Could not create " + "file.txt");
    }
    child.setWritable(false);
    boolean writable = true;
    try (FileOutputStream fos = new FileOutputStream(child)) {
        fos.write("Hello World".getBytes()); // write operation
        fos.flush();
    } catch (IOException e) {
        writable = false;
    } finally {
        removeDir(parentDir);
    }
    assertFalse(writable);
}
```

在上面的代码中，我们在明确设置阻止任何写入的权限后尝试写入文件。我们使用setWritable方法来做到这一点。在不允许写入文件时尝试写入文件会导致抛出IOException 。

接下来，我们在设置阻止任何读取的权限后尝试从文件中读取。使用setReadable方法阻止读取：

```java
@Test
public void givenWriteOnlyFile_whenCreateNewFile_thenCantReadFile() {
    File parentDir = makeDir("writeDir");

    File child = new File(parentDir, "file.txt");
    try {
        child.createNewFile();
    } catch (IOException e) {
        fail("Could not create " + "file.txt");
    }
    child.setReadable(false);
    boolean readable = true;
    try (FileInputStream fis = new FileInputStream(child)) {
        fis.read(); // read operation
    } catch (IOException e) {
        readable = false;
    } finally {
        removeDir(parentDir);
    }
    assertFalse(readable);
}
```

同样，如果尝试读取不允许读取的文件，JVM 将抛出IOException。

### 3.4. 列出目录中的文件

File 类 具有允许我们列出目录中包含的文件的方法。同样，也可以列出目录。在这里，我们将查看list和 list(FilenameFilter)方法：

```java
@Test
public void givenFilesInDir_whenCreateNewFile_thenCanListFiles() {
    File parentDir = makeDir("filtersDir");

    String[] files = {"file1.csv", "file2.txt"};
    for (String file : files) {
        try {
            new File(parentDir, file).createNewFile();
        } catch (IOException e) {
            fail("Could not create " + file);
        }
    }

    //normal listing
    assertEquals(2, parentDir.list().length);

    //filtered listing
    FilenameFilter csvFilter = (dir, ext) -> ext.endsWith(".csv");
    assertEquals(1, parentDir.list(csvFilter).length);

    removeDir(parentDir);
}
```

我们创建了一个目录并向其中添加了两个文件——一个扩展名为csv ，另一个扩展名为 txt 。当列出目录中的所有文件时，我们按预期得到了两个文件。当我们通过过滤带有csv扩展名的文件来过滤列表时，我们只返回一个文件。

### 3.5. 重命名文件和目录

File 类具有使用renameTo 方法重命名文件和目录的功能：

```java
@Test
public void givenDir_whenMkdir_thenCanRenameDir() {

    File source = makeDir("source");
    File destination = makeDir("destination");
    boolean renamed = source.renameTo(destination);

    if (renamed) {
        assertFalse(source.isDirectory());
        assertTrue(destination.isDirectory());

        removeDir(destination);
    }
}
```

在上面的例子中，我们创建了两个目录——源目录和目标目录。然后我们使用renameTo方法将源目录重命名为目标目录。同样可以用来重命名文件而不是目录。

### 3.6. 获取磁盘空间信息

File 类 还允许我们获取磁盘空间信息。让我们看一下getFreeSpace方法的演示：

```java
@Test
public void givenDataWritten_whenWrite_thenFreeSpaceReduces() {

    String home = System.getProperty("user.home");
    String sep = File.separator;
    File testDir = makeDir(home + sep + "test");
    File sample = new File(testDir, "sample.txt");

    long freeSpaceBefore = testDir.getFreeSpace();
    try {
        writeSampleDataToFile(sample);
    } catch (IOException e) {
        fail("Could not write to " + "sample.txt");
    }

    long freeSpaceAfter = testDir.getFreeSpace();
    assertTrue(freeSpaceAfter < freeSpaceBefore);

    removeDir(testDir);
}
```

在此示例中，我们在用户的主目录中创建了一个目录，然后在其中创建了一个文件。然后我们检查在用一些文本填充此文件后主目录分区上的可用空间是否发生了变化。提供有关磁盘空间信息的其他方法是getTotalSpace和 getUsableSpace。

## 4。总结

在本教程中，我们展示了File类提供的一些功能，用于处理文件系统上的文件和目录。
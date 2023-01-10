## 1. 概述

Apache Commons 项目旨在为开发人员提供一组可在日常代码中使用的公共库。

在本教程中，我们将探讨 Commons IO 模块的一些关键实用程序类及其最著名的功能。

## 2.Maven依赖

要使用该库，让我们在pom.xml中包含以下 Maven 依赖项：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"commons-io" AND a%3A"commons-io")中找到该库的最新版本。

## 3.实用类

简而言之，实用程序类提供了一组静态方法，可用于对文件执行常见任务。

### 3.1. 文件工具

该类提供了对文件的不同操作，如打开、读取、和移动。

让我们看看如何使用FileUtils读取或文件：

```java
File file = FileUtils.getFile(getClass().getClassLoader()
  .getResource("fileTest.txt")
  .getPath());
File tempDir = FileUtils.getTempDirectory();
FileUtils.copyFileToDirectory(file, tempDir);
File newTempFile = FileUtils.getFile(tempDir, file.getName());
String data = FileUtils.readFileToString(newTempFile,
  Charset.defaultCharset());
```

### 3.2. 文件名实用程序

该实用程序提供了一种与操作系统无关的方式来对文件名执行通用功能。让我们看看我们可以使用的一些不同方法：

```java
String fullPath = FilenameUtils.getFullPath(path);
String extension = FilenameUtils.getExtension(path);
String baseName = FilenameUtils.getBaseName(path);
```

### 3.3. 文件系统工具

我们可以使用FileSystemUtils检查给定卷或驱动器上的可用空间：

```java
long freeSpace = FileSystemUtils.freeSpaceKb("/");
```

## 4. 输入输出

这个包提供了几个处理输入和输出流的实现。

我们将专注于TeeInputStream和TeeOutputSteam。“ Tee ”一词(源自字母“ T ”)通常用于描述将单个输入拆分为两个不同的输出。

让我们看一个示例，演示如何将单个输入流写入两个不同的输出流：

```java
String str = "Hello World.";
ByteArrayInputStream inputStream = new ByteArrayInputStream(str.getBytes());
ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();

FilterOutputStream teeOutputStream
  = new TeeOutputStream(outputStream1, outputStream2);
new TeeInputStream(inputStream, teeOutputStream, true)
  .read(new byte[str.length()]);

assertEquals(str, String.valueOf(outputStream1));
assertEquals(str, String.valueOf(outputStream2));
```

## 5.过滤器

Commons IO 包括一个有用的文件过滤器列表。当开发人员想要从异构文件列表中缩小到特定的所需文件列表时，这些可以派上用场。

该库还支持对给定文件列表进行AND和OR逻辑运算。因此，我们可以混合搭配这些过滤器以获得所需的结果。

让我们看一个示例，该示例使用WildcardFileFilter和SuffixFileFilter来检索名称中包含“ ple ”并带有“ txt ”后缀的文件。请注意，我们使用ANDFileFilter包装上面的过滤器：

```java
@Test
public void whenGetFilewith_ANDFileFilter_thenFind_sample_txt()
  throws IOException {

    String path = getClass().getClassLoader()
      .getResource("fileTest.txt")
      .getPath();
    File dir = FileUtils.getFile(FilenameUtils.getFullPath(path));

    assertEquals("sample.txt",
      dir.list(new AndFileFilter(
        new WildcardFileFilter("ple", IOCase.INSENSITIVE),
        new SuffixFileFilter("txt")))[0]);
}
```

## 6.比较器

Comparator包提供不同类型的文件比较。我们将在这里探索两个不同的比较器。

### 6.1. 路径文件比较器

PathFileComparator类可用于按文件路径以区分大小写、不区分大小写或依赖于系统的区分大小写的方式对列表或文件数组进行排序。让我们看看如何使用此实用程序对资源目录中的文件路径进行排序：

```java
@Test
public void whenSortDirWithPathFileComparator_thenFirstFile_aaatxt() 
  throws IOException {
    
    PathFileComparator pathFileComparator = new PathFileComparator(
      IOCase.INSENSITIVE);
    String path = FilenameUtils.getFullPath(getClass()
      .getClassLoader()
      .getResource("fileTest.txt")
      .getPath());
    File dir = new File(path);
    File[] files = dir.listFiles();

    pathFileComparator.sort(files);

    assertEquals("aaa.txt", files[0].getName());
}
```

请注意，我们使用了IOCase.INSENSITIVE配置。PathFileComparator还提供了许多单例实例，它们具有不同的区分大小写和反向排序选项。

这些静态字段包括PATH_COMPARATOR、PATH_INSENSITIVE_COMPARATOR、PATH_INSENSITIVE_REVERSE、PATH_SYSTEM_COMPARATOR等等。

### 6.2. 大小文件比较器

SizeFileComparator，顾名思义，就是用来比较两个文件的大小(长度)的。如果第一个文件的大小小于第二个文件的大小，它返回一个负整数值。如果文件大小相等，则返回零；如果第一个文件的大小大于第二个文件的大小，则返回正值。

让我们编写一个单元测试来演示文件大小的比较：

```java
@Test
public void whenSizeFileComparator_thenLargerFile_large()
  throws IOException {

    SizeFileComparator sizeFileComparator = new SizeFileComparator();
    File largerFile = FileUtils.getFile(getClass().getClassLoader()
      .getResource("fileTest.txt")
      .getPath());
    File smallerFile = FileUtils.getFile(getClass().getClassLoader()
      .getResource("sample.txt")
      .getPath());

    int i = sizeFileComparator.compare(largerFile, smallerFile);

    Assert.assertTrue(i > 0);
}
```

## 7.文件监控

Commons IO 监视器包提供了跟踪文件或目录更改的功能。让我们看一个快速示例，了解如何将FileAlterationMonitor与FileAlterationObserver和FileAlterationListener一起使用来监视文件或文件夹。

当FileAlterationMonitor启动时，我们将开始接收有关正在监视的目录上的文件更改的通知：

```java
FileAlterationObserver observer = new FileAlterationObserver(folder);
FileAlterationMonitor monitor = new FileAlterationMonitor(5000);

FileAlterationListener fal = new FileAlterationListenerAdaptor() {

    @Override
    public void onFileCreate(File file) {
        // on create action
    }

    @Override
    public void onFileDelete(File file) {
        // on delete action
    }
};

observer.addListener(fal);
monitor.addObserver(observer);
monitor.start();
```

## 八. 总结

本文介绍了 Commons IO 包的一些常用组件。但是，该软件包也具有许多其他功能。有关详细信息，请参阅[API 文档。](https://commons.apache.org/proper/commons-io/javadocs/api-2.5/index.html)
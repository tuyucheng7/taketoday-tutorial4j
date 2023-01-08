## 1. 概述

在本文中，我们将介绍在Java中文件的常用方法。

首先，我们将使用标准IO和NIO.2 API，以及两个外部库：[commons-io](https://commons.apache.org/proper/commons-io/)和[guava](https://github.com/google/guava)。

## 2.IO API( JDK7之前)

首先，要使用 java.io API文件，我们需要打开一个流，遍历内容并将其写出到另一个流：

```java
@Test
public void givenIoAPI_whenCopied_thenCopyExistsWithSameContents() 
  throws IOException {
 
    File copied = new File("src/test/resources/copiedWithIo.txt");
    try (
      InputStream in = new BufferedInputStream(
        new FileInputStream(original));
      OutputStream out = new BufferedOutputStream(
        new FileOutputStream(copied))) {
 
        byte[] buffer = new byte[1024];
        int lengthRead;
        while ((lengthRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, lengthRead);
            out.flush();
        }
    }
 
    assertThat(copied).exists();
    assertThat(Files.readAllLines(original.toPath())
      .equals(Files.readAllLines(copied.toPath())));
}
```

实现这样的基本功能需要做很多工作。

对我们来说幸运的是，Java 改进了它的核心 API，我们有了使用NIO.2 API文件的更简单方法。

## 3. NIO.2 API (JDK7)

使用[NIO.2](https://www.baeldung.com/java-nio-2-file-api)可以显着提高文件性能，因为NIO.2使用较低级别的系统入口点。

让我们仔细看看 Files. copy()方法有效。

copy ()方法使我们能够指定表示选项的可选参数。默认情况下，文件和目录不会覆盖现有的，也不会文件属性。

可以使用以下选项更改此行为：

-   REPLACE_EXISTING –替换文件(如果存在)
-   COPY_ATTRIBUTES –将元数据到新文件
-   NOFOLLOW_LINKS –不应跟随符号链接

NIO.2 Files类提供了一组重载的copy()方法，用于在文件系统中文件和目录。

让我们看一个使用带有两个Path参数的copy() 的例子：

```java
@Test
public void givenNIO2_whenCopied_thenCopyExistsWithSameContents() 
  throws IOException {
 
    Path copied = Paths.get("src/test/resources/copiedWithNio.txt");
    Path originalPath = original.toPath();
    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
 
    assertThat(copied).exists();
    assertThat(Files.readAllLines(originalPath)
      .equals(Files.readAllLines(copied)));
}
```

请注意，目录副本是浅表的，这意味着目录中的文件和子目录不会被。

## 4.阿帕奇共享IO

使用Java文件的另一种常见方法是使用commons-io库。

首先，我们需要添加依赖：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

最新版本可以从[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"commons-io" AND a%3A"commons-io")下载。

然后，要文件，我们只需要使用FileUtils类中定义的copyFile()方法。该方法采用源文件和目标文件。

让我们看一下使用copyFile()方法的 JUnit 测试：

```java
@Test
public void givenCommonsIoAPI_whenCopied_thenCopyExistsWithSameContents() 
  throws IOException {
    
    File copied = new File(
      "src/test/resources/copiedWithApacheCommons.txt");
    FileUtils.copyFile(original, copied);
    
    assertThat(copied).exists();
    assertThat(Files.readAllLines(original.toPath())
      .equals(Files.readAllLines(copied.toPath())));
}
```

## 5.番石榴

最后，我们将看一下 Google 的 Guava 库。

同样，如果我们想使用 Guava ，我们需要包含依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

[最新版本可以在 Maven Central 上](https://search.maven.org/classic/#search|ga|1|a%3A"guava" AND g%3A"com.google.guava")找到。

这是 Guava 文件的方式：

```java
@Test
public void givenGuava_whenCopied_thenCopyExistsWithSameContents() 
  throws IOException {
 
    File copied = new File("src/test/resources/copiedWithGuava.txt");
    com.google.common.io.Files.copy(original, copied);
 
    assertThat(copied).exists();
    assertThat(Files.readAllLines(original.toPath())
      .equals(Files.readAllLines(copied.toPath())));
}
```

## 六，总结

在本文中，我们探索了在Java中文件的最常见方法。
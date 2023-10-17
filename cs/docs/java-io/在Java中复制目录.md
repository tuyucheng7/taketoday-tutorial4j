## 1. 概述

在这个简短的教程中，我们将看到如何在Java中一个目录，包括它的所有文件和子目录。这可以通过使用核心Java功能或第三方库来实现。

## 2. 使用java.nio API

[Java NIO](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/package-summary.html)从Java1.4 开始可用。Java 7 引入了[NIO 2](https://www.baeldung.com/java-nio-2-file-api)，它带来了很多有用的特性，比如更好地支持处理符号链接、文件属性访问。[它还为我们提供了Path](https://www.baeldung.com/java-nio-2-path)、Paths和Files等类，使文件系统操作变得更加容易。

让我们演示一下这种方法：

```java
public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) 
  throws IOException {
    Files.walk(Paths.get(sourceDirectoryLocation))
      .forEach(source -> {
          Path destination = Paths.get(destinationDirectoryLocation, source.toString()
            .substring(sourceDirectoryLocation.length()));
          try {
              Files.copy(source, destination);
          } catch (IOException e) {
              e.printStackTrace();
          }
      });
}
```

在此示例中，我们使用Files.walk()遍历以给定源目录为根的文件树，并为我们在源目录中找到的每个文件或目录调用Files.copy() 。

## 3. 使用java.io API

从文件系统管理的角度来看，Java 7 是一个转折点，因为它引入了许多方便的新功能。

但是，如果我们想与旧的Java版本保持兼容，我们可以使用递归和[java.io.File](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html)功能目录：

```java
private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
    if (!destinationDirectory.exists()) {
        destinationDirectory.mkdir();
    }
    for (String f : sourceDirectory.list()) {
        copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
    }
}
```

在这种情况下，我们将在目标目录中为源目录树中的每个目录创建一个目录。然后我们将调用copyDirectoryCompatibityMode()方法：

```java
public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
    if (source.isDirectory()) {
        copyDirectory(source, destination);
    } else {
        copyFile(source, destination);
    }
}

```

另外，让我们看看如何使用[FileInputStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/FileInputStream.html)和[FileOutputStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/FileOutputStream.html)文件：

```java
private static void copyFile(File sourceFile, File destinationFile) 
  throws IOException {
    try (InputStream in = new FileInputStream(sourceFile); 
      OutputStream out = new FileOutputStream(destinationFile)) {
        byte[] buf = new byte[1024];
        int length;
        while ((length = in.read(buf)) > 0) {
            out.write(buf, 0, length);
        }
    }
}

```

## 4. 使用 Apache Commons IO

[Apache Commons IO](https://search.maven.org/artifact/org.apache.commons/commons-io)有很多有用的特性，比如实用类、文件过滤器和文件比较器。在这里，我们将使用FileUtils，它提供简单的文件和目录操作方法，即读取、移动、。

让我们将 [commons-io](https://search.maven.org/search?q=a:commons-io g:commons-io)添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

最后，让我们使用这种方法一个目录：

```java
public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
    File sourceDirectory = new File(sourceDirectoryLocation);
    File destinationDirectory = new File(destinationDirectoryLocation);
    FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
}
```

如前面的示例所示，Apache Commons IO 使这一切变得容易得多，因为我们只需要调用FileUtils.copyDirectory() 方法。

## 5.总结

本文说明了如何在Java中目录。
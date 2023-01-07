## 1. 概述

JDK 7 引入了获取文件创建日期的功能。

在本教程中，我们将学习如何通过java.nio访问它。

## 2.文件.getAttribute

获取文件创建日期的一种方法是使用方法Files.getAttribute 和给定的Path：

```java
try {
    FileTime creationTime = (FileTime) Files.getAttribute(path, "creationTime");
} catch (IOException ex) {
    // handle exception
}
```

creationTime的类型 是FileTime，但是由于该方法返回的是Object，所以我们必须将其转换为。

FileTime将日期值保存为时间戳属性。例如，可以 使用toInstant()方法将其转换为Instant 。

如果文件系统不存储文件的创建日期，则该方法将返回null。

## 3.文件.readAttributes

另一种获取创建日期的方法是使用Files.readAttributes，对于给定的路径，它会立即返回文件的所有基本属性：

```java
try {
    BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
    FileTime fileTime = attr.creationTime();
} catch (IOException ex) {
    // handle exception
}
```

该方法返回一个BasicFileAttributes，我们可以使用它来获取文件的基本属性。creationTime()方法 返回文件的创建日期作为FileTime。

这一次，如果文件系统不存储创建文件的日期，那么该方法将返回 last modified date。如果上次修改日期也未存储，则将返回纪元 (01.01.1970)。

## 4。总结

在本教程中，我们学习了如何在Java中确定文件创建日期。具体来说，我们了解到可以使用 Files.getAttribute 和 Files.readAttributes来完成。
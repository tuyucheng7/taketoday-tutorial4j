## 1. 概述

在Java中获取当前工作目录是一项简单的任务，但不幸的是，JDK 中没有可用的直接 API 来执行此操作。

在本教程中，我们将学习如何使用java.lang 获取Java中的当前工作目录。系统、java.io.File、java.nio.file.FileSystems和java.nio.file.Paths。

## 2.系统

让我们从使用System#getProperty的标准解决方案开始，假设我们当前的工作目录名称在整个代码中都是Baeldung ：

```java
static final String CURRENT_DIR = "Baeldung";
@Test
void whenUsingSystemProperties_thenReturnCurrentDirectory() {
    String userDirectory = System.getProperty("user.dir");
    assertTrue(userDirectory.endsWith(CURRENT_DIR));
}
```

我们使用Java内置属性键user.dir从System的属性映射中获取当前工作目录。此解决方案适用于所有 JDK 版本。

## 3.档案

让我们看看另一个使用java.io.File的解决方案：

```java
@Test
void whenUsingJavaIoFile_thenReturnCurrentDirectory() {
    String userDirectory = new File("").getAbsolutePath();
    assertTrue(userDirectory.endsWith(CURRENT_DIR));
}
```

在这里，File#getAbsolutePath 在内部使用System#getProperty来获取目录名称，类似于我们的第一个解决方案。这是获取当前工作目录的非标准解决方案，并且适用于所有 JDK 版本。

## 4.文件系统

另一个有效的替代方法是使用新的 java.nio.file.FileSystems API：

```java
@Test
void whenUsingJavaNioFileSystems_thenReturnCurrentDirectory() {
    String userDirectory = FileSystems.getDefault()
        .getPath("")
        .toAbsolutePath()
        .toString();
    assertTrue(userDirectory.endsWith(CURRENT_DIR));
}
```

该解决方案使用新的[Java NIO API](https://www.baeldung.com/java-nio-2-file-api)，并且仅适用于JDK 7 或更高版本。

## 5.路径

最后，让我们看看使用java.nio.file.Paths API获取当前目录的更简单的解决方案：

```java
@Test
void whenUsingJavaNioPaths_thenReturnCurrentDirectory() {
    String userDirectory = Paths.get("")
        .toAbsolutePath()
        .toString();
    assertTrue(userDirectory.endsWith(CURRENT_DIR));
}
```

在这里，Paths#get 在内部使用FileSystem#getPath来获取路径。它使用新的[Java NIO API](https://www.baeldung.com/java-nio-2-file-api)，因此该解决方案仅适用于JDK 7 或更高版本。

## 六，总结

在本教程中，我们探索了四种在Java中获取当前工作目录的不同方法。前两个解决方案适用于所有版本的 JDK，而后两个解决方案仅适用于 JDK 7 或更高版本。

我们建议使用系统解决方案，因为它高效且直接，我们可以通过将此 API 调用包装在静态实用程序方法中并直接访问它来简化它。
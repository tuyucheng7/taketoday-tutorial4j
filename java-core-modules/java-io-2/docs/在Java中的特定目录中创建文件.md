## 1. 概述

在本快速教程中，我们将了解如何在[特定目录中](https://www.baeldung.com/java-create-directory)[创建文件](https://www.baeldung.com/java-how-to-create-a-file)。

我们将看到绝对文件路径和相对文件路径之间的区别，并且我们将使用适用于几个主要操作系统的路径。

## 2.绝对和相对文件路径

### 2.1. 绝对路径

让我们从通过引用整个路径(也称为绝对路径)在目录中创建文件开始。为了演示，我们将使用用户临时目录的绝对路径，并将我们的文件添加到其中。

我们使用Google Guava 的一部分Files.touch()作为创建空文件的简单方法：

```java
File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
File fileWithAbsolutePath = new File(tempDirectory.getAbsolutePath() + "/testFile.txt");

assertFalse(fileWithAbsolutePath.exists());

Files.touch(fileWithAbsolutePath);

assertTrue(fileWithAbsolutePath.exists());
```

### 2.2. 相对路径

我们还可以在相对于另一个目录的目录中创建一个文件。例如，让我们在用户临时 目录中创建文件：

```java
File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
File fileWithRelativePath = new File(tempDirectory, "newFile.txt");

assertFalse(fileWithRelativePath.exists());

Files.touch(fileWithRelativePath);

assertTrue(fileWithRelativePath.exists());
```

在上面的示例中，我们的新文件被添加到用户临时目录的路径中。

## 3. 使用平台无关的文件分隔符

要构造文件路径，我们需要使用/或等分隔符。但是，要使用的适当分隔符取决于你的操作系统。幸运的是，有一种更简单的方法。我们可以使用Java的File.separator代替分隔符。结果，Java 为我们选择了合适的分隔符。

让我们看一个使用此方法创建文件的示例：

```java
File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
File newFile = new File(tempDirectory.getAbsolutePath() + File.separator + "newFile.txt");

assertFalse(newFile.exists());

Files.touch(newFile);

assertTrue(newFile.exists());
```

使用File.separator，Java 知道如何基于底层文件系统构造路径。

## 4。总结

在本文中，我们探讨了绝对路径和相对路径之间的区别，以及如何创建适用于几个主要操作系统的文件路径。
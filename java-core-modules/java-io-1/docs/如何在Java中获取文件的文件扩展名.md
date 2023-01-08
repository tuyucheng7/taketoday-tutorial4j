## 1. 概述

在本快速教程中，我们将展示如何在Java中以编程方式获取文件扩展名。我们将重点关注解决该问题的三种主要方法。

在我们的实现中，最后一个“ .”之后的字符 将被退回。

因此，举个简单的例子，如果我们的文件名为jarvis.txt，那么它将返回字符串“ txt”作为文件的扩展名。

## 2.获取文件扩展名

对于每种方法，我们将学习如何实施它并跟进两种特殊情况下发生的情况：

-   当文件名没有扩展名时，例如makefile文件
-   如果文件名仅包含扩展名，例如.gitignore或.DS_Store。

### 2.1. 简单的字符串处理方法

通过这种方法，我们将使用简单的字符串处理方法来查找扩展：

```java
public Optional<String> getExtensionByStringHandling(String filename) {
    return Optional.ofNullable(filename)
      .filter(f -> f.contains("."))
      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
}

```

此方法将检查点 '.' 在给定的文件名中出现。

如果它存在，那么它将找到点“.”的最后位置。并返回之后的字符，即最后一个点“。”之后的字符 称为文件扩展名。

特别案例：

1.  无扩展名——此方法将返回一个空字符串
2.  仅扩展——此方法将返回点后的字符串，例如“gitignore”

### 2.2. 来自 Apache Commons IO 的FilenameUtils.getExtension

在第二种方法中，我们将使用 Apache Commons IO 库提供的实用程序类找到扩展：

```java
public String getExtensionByApacheCommonLib(String filename) {
    return FilenameUtils.getExtension(filename);
}
```

在这里，除了文件名，我们还可以指定文件的完整路径，例如“ C:/baeldung/com/demo.java ”。

getExtension(String)方法将检查给定的文件名是否为空。

如果filename为空或 null，getExtension(String filename)将返回给定的实例。否则，它返回文件名的扩展名。

为此，它使用indexOfExtension(String)方法，该方法又使用lastIndexof(char)来查找最后一次出现的“.”。这些方法均由FilenameUtils提供。

此方法还使用另一种方法indexOfLastSeparator(String)检查最后一个点后是否没有目录分隔符，该方法将处理 Unix 或 Windows 格式的文件。

特别案例：

1.  无扩展名——此方法将返回一个空字符串。
2.  仅扩展——此方法将返回点后的字符串，例如“gitignore”

### 2.3. 使用 Guava 库

在最后一种方法中，我们将使用 Guava 库来查找扩展。

要添加 Guava 库，我们可以将以下依赖项添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

对于最新的依赖，我们可以检查[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")。

添加库后，我们可以简单地使用它的getFileExtension方法：

```java
public String getExtensionByGuava(String filename) {
    return Files.getFileExtension(filename);
}

```

方法getFileExtension(String)将首先检查给定的文件名是否为空。

如果文件名不为空，则它将通过将给定文件名转换为抽象路径名并调用File 的getName()方法来创建一个File实例，该方法将返回此抽象路径名表示的文件名，或者如果给定的文件名是空的，则为空字符串。

基于此返回值，它获取最后一次出现的“.”的索引。通过使用String类内置方法lastIndexOf(char)。

特别案例：

1.  无扩展名——此方法将返回一个空字符串
2.  仅扩展——此方法将返回点后的字符串，例如“gitignore”

## 3.总结

在 Apache Commons和Guava之间进行选择时，虽然这两个库都有一些共同的特性，但它们也有替代品所没有的功能。

这意味着如果需要某些功能，请选择具有它的功能。否则，如果需要更多自定义场景，请选择最能满足你需要的场景，并随意将其与你自己的实现包装起来以获得所需的结果。
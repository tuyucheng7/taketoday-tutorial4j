## 1. 概述

当我们在Java中处理文件时，我们经常需要处理文件名。例如，有时我们想从给定的文件名中获取不带扩展名的名称。换句话说，我们要删除文件名的扩展名。

在本教程中，我们将讨论从文件名中删除扩展名的通用方法。

## 2. 去掉文件名后缀的场景

当我们第一次看它时，我们可能会认为从文件名中删除扩展名是一个非常简单的问题。

然而，如果我们仔细研究这个问题，它可能比我们想象的要复杂。

首先，让我们看一下文件名的类型：

-   没有任何扩展名，例如，“ baeldung”
-   对于单个扩展名，这是最常见的情况，例如，“ baeldung.txt ”
-   具有多个扩展名，例如“ baeldung.tar.gz ”
-   没有扩展名的点文件，例如“ .baeldung ”
-   具有单个扩展名的点文件，例如“ .baeldung.conf ”
-   具有多个扩展名的点文件，例如“ .baeldung.conf.bak ”

接下来，我们将列出删除扩展后上述示例的预期结果：

-   “ baeldung “：文件名没有扩展名。因此，文件名不应更改，我们应该得到“ baeldung ”
-   “ baeldung.txt ”：这是一个简单的案例。正确结果是“ baeldung ”
-   “ baeldung.tar.gz ”：该文件名包含两个扩展名。如果我们只想删除一个扩展名，结果应该是“ baeldung.tar ”。但是如果我们想从文件名中删除所有扩展名，“ baeldung ”是正确的结果
-   “ .baeldung “：由于此文件名也没有任何扩展名，因此也不应更改文件名。因此，我们期望在结果中看到“ .baeldung ”
-   “ .baeldung.conf ”：结果应该是“ .baeldung ”
-   “ .baeldung.conf.bak ”：如果我们只想删除一个扩展名，结果应该是“.baeldung.conf”。否则，如果我们删除所有扩展名，“ .baeldung ”是预期的输出

在本教程中，我们将测试 Guava 和 Apache Commons IO 提供的实用方法是否可以处理上面列出的所有情况。

此外，我们还将讨论解决从给定文件名中删除扩展名(或多个扩展名)的问题的通用方法。

## 3. 测试 Guava 库

从14.0版本开始，[Guava](https://github.com/google/guava)引入了[Files.getNameWithoutExtension()](https://guava.dev/releases/30.0-jre/api/docs/com/google/common/io/Files.html#getNameWithoutExtension(java.lang.String))方法。它允许我们轻松地从给定的文件名中删除扩展名。

要使用实用程序方法，我们需要将 Guava 库添加到我们的类路径中。例如，如果我们使用 Maven 作为构建工具，我们可以将[Guava 依赖项](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" AND a%3A"guava")添加到我们的pom.xml 文件中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>

```

首先我们看一下这个方法的实现：

```java
public static String getNameWithoutExtension(String file) {
   ...
   int dotIndex = fileName.lastIndexOf('.');
   return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
 }
```

实现非常简单。如果文件名包含点，则该方法从最后一个点剪切到文件名的末尾。否则，如果文件名不包含点，则将返回原始文件名而不做任何更改。

因此，Guava 的getNameWithoutExtension() 方法不适用于没有扩展名的点文件。 让我们写一个测试来证明：

```java
@Test
public void givenDotFileWithoutExt_whenCallGuavaMethod_thenCannotGetDesiredResult() {
    //negative assertion
    assertNotEquals(".baeldung", Files.getNameWithoutExtension(".baeldung"));
}

```

当我们处理具有多个扩展名的文件名时，此方法不提供从文件名中删除所有扩展名的选项：

```java
@Test
public void givenFileWithoutMultipleExt_whenCallGuavaMethod_thenCannotRemoveAllExtensions() {
    //negative assertion
    assertNotEquals("baeldung", Files.getNameWithoutExtension("baeldung.tar.gz"));
}

```

## 4. 测试 Apache Commons IO 库

与 Guava 库一样，流行的[Apache Commons IO库在](https://commons.apache.org/proper/commons-io/)FilenameUtils类中提供了一个[removeExtension()](https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FilenameUtils.html#removeExtension-java.lang.String-)方法来快速删除文件名的扩展名。

在查看此方法之前，让我们将[Apache Commons IO 依赖项](https://search.maven.org/classic/#search|gav|1|g%3A"commons-io" AND a%3A"commons-io")添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.8.0</version>
</dependency>

```

实现类似于 Guava 的getNameWithoutExtension()方法：

```java
public static String removeExtension(final String filename) {
    ...
    final int index = indexOfExtension(filename); //used the String.lastIndexOf() method
    if (index == NOT_FOUND) {
  	return filename;
    } else {
	return filename.substring(0, index);
    }
}

```

因此，Apache Commons IO 的方法也不适用于点文件：

```java
@Test
public void givenDotFileWithoutExt_whenCallApacheCommonsMethod_thenCannotGetDesiredResult() {
    //negative assertion
    assertNotEquals(".baeldung", FilenameUtils.removeExtension(".baeldung"));
}

```

如果文件名有多个扩展名，removeExtension()方法不能删除所有扩展名：

```java
@Test
public void givenFileWithoutMultipleExt_whenCallApacheCommonsMethod_thenCannotRemoveAllExtensions() {
    //negative assertion
    assertNotEquals("baeldung", FilenameUtils.removeExtension("baeldung.tar.gz"));
}

```

## 5. 从文件名中删除扩展名

到目前为止，我们已经在两个广泛使用的库中看到了从文件名中删除扩展名的实用方法。这两种方法都非常方便，适用于最常见的情况。

然而，另一方面，它们也有一些缺点：

-   它们不适用于点文件，例如“ .baeldung ”
-   当文件名有多个扩展名时，它们不提供仅删除最后一个扩展名或删除所有扩展名的选项

接下来，让我们构建一个涵盖所有情况的方法：

```java
public static String removeFileExtension(String filename, boolean removeAllExtensions) {
    if (filename == null || filename.isEmpty()) {
        return filename;
    }

    String extPattern = "(?<!^)[.]" + (removeAllExtensions ? "." : "[^.]$");
    return filename.replaceAll(extPattern, "");
}

```

我们添加了一个布尔参数 removeAllExtensions以提供从文件名中删除所有扩展名或仅删除最后一个扩展名的选项。

该方法的核心部分是[正则表达式](https://www.baeldung.com/regular-expressions-java)模式。因此，让我们了解此正则表达式模式的作用：

-   “(?<!^)[.]” ——我们在此 正则表达式中使用[负向后视](https://www.regular-expressions.info/lookaround.html)。它匹配一个点“ 。” 不在文件名的开头
-   “ (?<!^)[.]. ” – 如果设置了removeAllExtensions选项，这将匹配第一个匹配的点，直到文件名结束
-   “ (?<!^)[.][^.]$ ” – 此模式仅匹配最后一个扩展名

最后，让我们编写一些测试方法来验证我们的方法是否适用于所有不同的情况：

```java
@Test
public void givenFilenameNoExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
    assertEquals("baeldung", MyFilenameUtil.removeFileExtension("baeldung", true));
    assertEquals("baeldung", MyFilenameUtil.removeFileExtension("baeldung", false));
}

@Test
public void givenSingleExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
    assertEquals("baeldung", MyFilenameUtil.removeFileExtension("baeldung.txt", true));
    assertEquals("baeldung", MyFilenameUtil.removeFileExtension("baeldung.txt", false));
}

@Test
public void givenDotFile_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
    assertEquals(".baeldung", MyFilenameUtil.removeFileExtension(".baeldung", true));
    assertEquals(".baeldung", MyFilenameUtil.removeFileExtension(".baeldung", false));
}

@Test
public void givenDotFileWithExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
    assertEquals(".baeldung", MyFilenameUtil.removeFileExtension(".baeldung.conf", true));
    assertEquals(".baeldung", MyFilenameUtil.removeFileExtension(".baeldung.conf", false));
}

@Test
public void givenDoubleExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
    assertEquals("baeldung", MyFilenameUtil.removeFileExtension("baeldung.tar.gz", true));
    assertEquals("baeldung.tar", MyFilenameUtil.removeFileExtension("baeldung.tar.gz", false));
}

@Test
public void givenDotFileWithDoubleExt_whenCallFilenameUtilMethod_thenGetExpectedFilename() {
    assertEquals(".baeldung", MyFilenameUtil.removeFileExtension(".baeldung.conf.bak", true));
    assertEquals(".baeldung.conf", MyFilenameUtil.removeFileExtension(".baeldung.conf.bak", false));
}

```

## 六，总结

在本文中，我们讨论了如何删除给定文件名的扩展名。

首先，我们讨论了删除扩展的不同场景。

接下来，我们介绍了两个广泛使用的库提供的方法：Guava 和 Apache Commons IO。它们非常方便，适用于常见情况，但不适用于点文件。此外，它们不提供删除单个扩展名或所有扩展名的选项。

最后，我们构建了一个覆盖所有需求的方法。
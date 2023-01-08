## 1. 概述

在本教程中，我们将讨论使用 Java验证给定字符串是否具有适用于操作系统的有效文件名的不同方法。我们要根据受限字符或长度限制检查该值。

通过示例，我们只关注核心解决方案，不使用任何外部依赖项。我们将检查 SDK 的java.io和 NIO2 包，最后实现我们自己的解决方案。

## 2. 使用java.io.File

让我们从第一个示例开始，使用[java.io.File类](https://www.baeldung.com/java-io-file)。在这个解决方案中，我们需要用给定的字符串创建一个File实例，然后在本地磁盘上创建一个文件：

```java
public static boolean validateStringFilenameUsingIO(String filename) throws IOException {
    File file = new File(filename);
    boolean created = false;
    try {
        created = file.createNewFile();
        return created;
    } finally {
        if (created) {
            file.delete();
        }
    }
}
```

当给定的文件名不正确时，它会抛出IOException。请注意，由于内部文件创建，此方法要求给定的文件名 字符串不对应于已经存在的文件。

我们知道不同的文件系统有自己的[文件名限制](https://en.wikipedia.org/wiki/Filename)。因此，通过使用java.io.File方法，我们不需要为每个操作系统指定规则，因为Java会自动为我们处理。

但是，我们需要创建一个虚拟文件。当我们成功的时候，一定要记得在最后删除它。此外，我们必须确保我们拥有执行这些操作的适当权限。任何失败也可能导致IOException，因此最好检查错误消息：

```java
assertThatThrownBy(() -> validateStringFilenameUsingIO("baeldung?.txt"))
  .isInstanceOf(IOException.class)
  .hasMessageContaining("Invalid file path");
```

## 3. 使用 NIO2 API

正如我们所知，java.io包[有很多缺点](https://www.baeldung.com/java-path-vs-file)，因为它是在Java的第一个版本中创建的。NIO2 API作为java.io包的后继者，带来了很多改进，也大大简化了我们之前的解决方案：

```java
public static boolean validateStringFilenameUsingNIO2(String filename) {
    Paths.get(filename);
    return true;
}
```

我们的功能现已精简，因此它是执行此类测试的最快方式。我们不创建任何文件，因此我们不需要任何磁盘权限，也不需要在测试后进行清理。

无效的文件名抛出InvalidPathException，它扩展了RuntimeException。该错误消息还包含比上一条更多的详细信息：

```java
assertThatThrownBy(() -> validateStringFilenameUsingNIO2(filename))
  .isInstanceOf(InvalidPathException.class)
  .hasMessageContaining("character not allowed");
```

该解决方案有一个与文件系统限制有关的严重缺陷。Path类可能代表带有子目录的文件路径。与第一个示例不同，此方法不检查文件名字符的溢出限制。让我们对照使用Apache Commons的[randomAlphabetic()](https://commons.apache.org/proper/commons-lang/javadocs/api-3.9/org/apache/commons/lang3/RandomStringUtils.html#randomAlphabetic-int-)方法生成的 500 个字符的随机字符串来检查它：

```java
String filename = RandomStringUtils.randomAlphabetic(500);
assertThatThrownBy(() -> validateStringFilenameUsingIO(filename))
  .isInstanceOf(IOException.class)
  .hasMessageContaining("File name too long");

assertThat(validateStringFilenameUsingNIO2(filename)).isTrue();
```

要解决这个问题，我们应该像以前一样创建一个文件并检查结果。

## 4.自定义实现

最后，让我们尝试实现我们自己的自定义函数来测试文件名。我们还将尝试避免任何 I/O 功能，并仅使用核心Java方法。

这些解决方案提供了更多的控制权，并允许我们实施自己的规则。然而，我们必须考虑不同系统的许多额外限制。

### 4.1. 使用String.contains

我们可以使用[String.contains()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#contains(java.lang.CharSequence))方法来检查给定的String是否包含任何禁止的字符。首先，我们需要手动指定一些示例值：

```java
public static final Character[] INVALID_WINDOWS_SPECIFIC_CHARS = {'"', '', '<', '>', '?', '|'};
public static final Character[] INVALID_UNIX_SPECIFIC_CHARS = {'000'};
```

在我们的示例中，让我们只关注这两个操作系统。正如我们所知，Windows 文件名比 UNIX 更受限制。此外，一些空白字符可能会有问题。

在定义了受限字符集之后，我们来确定当前的操作系统：

```java
public static Character[] getInvalidCharsByOS() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win")) {
        return INVALID_WINDOWS_SPECIFIC_CHARS;
    } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
        return INVALID_UNIX_SPECIFIC_CHARS;
    } else {
        return new Character[]{};
    }
}
```

现在我们可以用它来测试给定的值：

```java
public static boolean validateStringFilenameUsingContains(String filename) {
    if (filename == null || filename.isEmpty() || filename.length() > 255) {
        return false;
    }
    return Arrays.stream(getInvalidCharsByOS())
      .noneMatch(ch -> filename.contains(ch.toString()));
}
```

如果我们定义的任何字符不在给定文件名中，则此Stream谓词返回 true。此外，我们实现了对空值和不正确长度的支持。

### 4.2. 正则表达式模式匹配

我们也可以直接在给定的String上使用[正则表达式](https://www.baeldung.com/regular-expressions-java) 。让我们实现一个只接受字母数字和点字符的模式，长度不超过 255：

```java
public static final String REGEX_PATTERN = "^[A-za-z0-9.]{1,255}$";

public static boolean validateStringFilenameUsingRegex(String filename) {
    if (filename == null) {
        return false;
    }
    return filename.matches(REGEX_PATTERN);
}

```

现在，我们可以根据先前准备的模式测试给定值。我们也可以很容易地修改模式。我们在此示例中跳过了操作系统检查功能。

## 5.总结

在本文中，我们重点关注文件名及其限制。我们引入了不同的算法来使用Java检测无效文件名。

我们从java.io包开始，它为我们解决了大部分系统限制，但执行额外的 I/O 操作并且可能需要一些权限。然后我们检查了 NIO2 API，这是最快的解决方案，具有文件名长度检查限制。

最后，我们实现了自己的方法，不使用任何 I/O API，但需要自定义实现文件系统规则。
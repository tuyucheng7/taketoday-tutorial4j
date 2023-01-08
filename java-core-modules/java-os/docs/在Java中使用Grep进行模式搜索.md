## 1. 概述

在本教程中，我们将学习如何使用Java和第三方库(如[Unix4J](https://github.com/tools4j/unix4j)和[Grep4J)](https://code.google.com/archive/p/grep4j/)在给定文件中搜索模式。

## 2. 背景

Unix 有一个名为grep的强大命令——它代表“全局正则表达式打印”。它在给定的一组文件中搜索模式或正则表达式。

可以使用零个或多个选项以及 grep 命令来丰富搜索结果，我们将在下一节中详细介绍。

如果你使用的是 Windows，则可以按照[此处的](https://code.google.com/archive/p/grep4j/wikis/WindowSupport.wiki)帖子中所述安装 bash 。

## 3. 使用 Unix4j 库

首先，让我们看看如何使用 Unix4J 库来 grep 文件中的模式。

在下面的例子中——我们将看看如何在Java中翻译 Unix grep 命令。

### 3.1. 构建配置

在pom.xml或build.gradle中添加以下依赖项：

```xml
<dependency>
    <groupId>org.unix4j</groupId>
    <artifactId>unix4j-command</artifactId>
    <version>0.4</version>
</dependency>
```

### 3.2. Grep 示例

Unix 中的示例 grep：

```bash
grep "NINETEEN" dictionary.txt

```

Java 中的等价物是：

```java
@Test 
public void whenGrepWithSimpleString_thenCorrect() {
    int expectedLineCount = 4;
    File file = new File("dictionary.txt");
    List<Line> lines = Unix4j.grep("NINETEEN", file).toLineList(); 
    
    assertEquals(expectedLineCount, lines.size());
}

```

另一个例子是我们可以在文件中使用反向文本搜索。这是相同的 Unix 版本：

```bash
grep -v "NINETEEN" dictionary.txt

```

这是上述命令的Java版本：

```java
@Test
public void whenInverseGrepWithSimpleString_thenCorrect() {
    int expectedLineCount = 178687;
    File file = new File("dictionary.txt");
    List<Line> lines 
      = Unix4j.grep(Grep.Options.v, "NINETEEN", file). toLineList();
    
    assertEquals(expectedLineCount, lines.size()); 
}

```

让我们看看，我们如何使用正则表达式来搜索文件中的模式。这是 Unix 版本，用于计算在整个文件中找到的所有正则表达式模式：

```bash
grep -c ".?NINE.?" dictionary.txt

```

这是上述命令的Java版本：

```java
@Test
public void whenGrepWithRegex_thenCorrect() {
    int expectedLineCount = 151;
    File file = new File("dictionary.txt");
    String patternCount = Unix4j.grep(Grep.Options.c, ".?NINE.?", file).
                          cut(CutOption.fields, ":", 1).toStringResult();
    
    assertEquals(expectedLineCount, patternCount); 
}
```

## 4. 使用 Grep4J

接下来——让我们看看如何使用 Grep4J 库来 grep 位于本地或远程位置某处的文件中的模式。

在下面的例子中——我们将看看如何在Java中翻译 Unix grep 命令。

### 4.1. 构建配置

在pom.xml或build.gradle中添加以下依赖项：

```xml
<dependency>
    <groupId>com.googlecode.grep4j</groupId>
    <artifactId>grep4j</artifactId>
    <version>1.8.7</version>
</dependency>
```

### 4.2. Grep 示例

Java 中的示例 grep 即等效于：

```bash
grep "NINETEEN" dictionary.txt

```

这是命令的Java版本：

```java
@Test 
public void givenLocalFile_whenGrepWithSimpleString_thenCorrect() {
    int expectedLineCount = 4;
    Profile localProfile = ProfileBuilder.newBuilder().
                           name("dictionary.txt").filePath(".").
                           onLocalhost().build();
    GrepResults results 
      = Grep4j.grep(Grep4j.constantExpression("NINETEEN"), localProfile);
    
    assertEquals(expectedLineCount, results.totalLines());
}

```

另一个例子是我们可以在文件中使用反向文本搜索。这是相同的 Unix 版本：

```bash
grep -v "NINETEEN" dictionary.txt

```

这是Java版本：

```java
@Test
public void givenRemoteFile_whenInverseGrepWithSimpleString_thenCorrect() {
    int expectedLineCount = 178687;
    Profile remoteProfile = ProfileBuilder.newBuilder().
                            name("dictionary.txt").filePath(".").
                            filePath("/tmp/dictionary.txt").
                            onRemotehost("172.168.192.1").
                            credentials("user", "pass").build();
    GrepResults results = Grep4j.grep(
      Grep4j.constantExpression("NINETEEN"), remoteProfile, Option.invertMatch());
    
    assertEquals(expectedLineCount, results.totalLines()); 
}

```

让我们看看，我们如何使用正则表达式来搜索文件中的模式。这是 Unix 版本，用于计算在整个文件中找到的所有正则表达式模式：

```bash
grep -c ".?NINE.?" dictionary.txt

```

这是Java版本：

```java
@Test
public void givenLocalFile_whenGrepWithRegex_thenCorrect() {
    int expectedLineCount = 151;
    Profile localProfile = ProfileBuilder.newBuilder().
                           name("dictionary.txt").filePath(".").
                           onLocalhost().build();
    GrepResults results = Grep4j.grep(
      Grep4j.regularExpression(".?NINE.?"), localProfile, Option.countMatches());
    
    assertEquals(expectedLineCount, results.totalLines()); 
}
```

## 5.总结

在本快速教程中，我们演示了使用Grep4j和Unix4J在给定文件中搜索模式。

[这些示例的实现可以在GitHub 项目](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-os)中找到——这是一个基于 Maven 的项目，因此应该很容易导入并按原样运行。
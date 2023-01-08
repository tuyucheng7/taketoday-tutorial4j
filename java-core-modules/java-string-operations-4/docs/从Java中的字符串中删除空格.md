## 1. 概述

当我们在Java中操作String时，我们经常需要从String中删除空格。

在本教程中，我们将探索从Java 中的字符串中删除空格的常见场景。

## 二、问题介绍

为了更容易理解问题，让我们先看一个字符串示例：

```java
String myString = "   I    am a    wonderful String     !   ";
```

上面的示例显示myString变量包含多个前导空格、尾随空格和中间的空白字符。

通常我们在Java中需要处理myString这样的字符串时，往往会面临这两个需求：

-   从给定字符串中删除所有空白字符 -> “IamawonderfulString!”
-   用一个空格替换连续的空白字符，并删除所有前导和尾随的空白字符 -> “I am a wonderful String !”

接下来，我们将为每种情况介绍两种方法：使用String类中方便的[replaceAll()](https://www.baeldung.com/string/replace-all)方法和广泛使用的[Apache Commons Lang3](https://www.baeldung.com/java-commons-lang-3)库中的StringUtils类。

为了简单起见，在本教程中，当我们谈论空白时，我们不会涵盖[Unicode 字符集中的空白字符。](https://en.wikipedia.org/wiki/Template:Whitespace_(Unicode))此外，我们将使用测试断言来验证每个解决方案。

现在，让我们看看它们的实际效果。

## 3. 去除字符串中的所有空格

### 3.1. 使用String.replaceAll()

首先，让我们使用replaceAll()方法从字符串中删除所有空格。

replaceAll()使用[正则表达式](https://www.baeldung.com/regular-expressions-java)(regex)。我们可以使用正则表达式字符类“ s ”来匹配空白字符。我们可以将输入字符串中的每个空白字符替换为空字符串来解决问题：inputString.replaceAll(“s”, “”)。

接下来，让我们创建一个测试，看看这个想法是否适用于我们的示例字符串：

```java
String result = myString.replaceAll("s", "");
assertThat(result).isEqualTo("IamawonderfulString!");
```

如果我们运行测试，它就会通过。所以， replaceAll() 方法解决了这个问题。接下来，让我们使用 Apache Commons Lang3 解决问题。

### 3.2. 使用 Apache Commons Lang3 库

Apache Commons Lang3 库附带了一个[StringUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html)实用程序，它允许我们方便地操作字符串。

要开始使用 Apache Commons Lang 3，让我们添加[Maven 依赖](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3&core=gav)项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

如果我们检查StringUtils类中的方法，就会发现有一种方法称为deleteWhitespace()。顾名思义，这就是我们正在寻找的方法。

接下来，让我们使用StringUtils.deleteWhitespace()从字符串中删除所有空格：

```xml
String result = StringUtils.deleteWhitespace(myString);
assertThat(result).isEqualTo("IamawonderfulString!");
```

如果我们执行它，测试就会通过。所以，deleteWhitespace()完成了这项工作。

## 4. 用一个空格替换连续的空白字符

### 4.1. 使用String.replaceAll()

现在，让我们看看另一种情况。我们可以分两步解决这个问题：

-   用一个空格替换连续的空格
-   修剪第一步的结果

值得一提的是，我们也可以先对输入的字符串进行裁剪，然后再替换连续的空格。因此，我们先采取哪一步并不重要。

对于第一步，我们仍然可以使用带有正则表达式的replaceAll()来匹配连续的空白字符并设置一个空格作为替换。

正则表达式 's+' 匹配一个或多个空白字符。因此，我们可以调用replaceAll(“s+”, ” “)方法来完成第一步。然后，我们可以调用[String.trim()](https://www.baeldung.com/string/trim)方法来应用修剪操作。

接下来，让我们创建一个测试来检查我们的想法是否可以解决问题。为了说清楚，我们为这两个步骤写了两个断言：

```java
String result = myString.replaceAll("s+", " ");
assertThat(result).isEqualTo(" I am a wonderful String ! ");
assertThat(result.trim()).isEqualTo("I am a wonderful String !");
```

如果我们试一试，测试就会通过。因此，该方法按预期工作。

接下来，让我们使用 Apache Commons Lang 3 库解决问题。

### 4.2. 使用 Apache Commons Lang3 库

StringUtils.normalizeSpace()方法修剪输入字符串，然后用单个空格替换空白字符序列。 因此，我们可以直接调用这个方法来解决问题：

```java
String result = StringUtils.normalizeSpace(myString);
assertThat(result).isEqualTo("I am a wonderful String !");
```

如果我们执行它，测试就会通过。正如我们所见，StringUtils.normalizeSpace()使用起来非常简单。

## 5.总结

在本文中，我们学习了如何从Java中的字符串中删除空白字符。
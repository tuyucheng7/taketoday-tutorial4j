## 1. 概述

当我们使用Java字符串时，有时我们想计算字符串中有多少个空格。

有多种方法可以获得结果。在本快速教程中，我们将通过示例了解如何完成它。

## 2. 示例输入字符串

首先，让我们准备一个输入字符串作为示例：

```java
String INPUT_STRING = "  This string has nine spaces and a Tab:'	'";
```

上面的字符串包含九个空格和一个用单引号括起来的制表符。我们的目标是仅计算给定输入字符串中的空格字符。

因此，我们的预期结果是：

```java
int EXPECTED_COUNT = 9;
```

接下来，让我们探索各种解决方案以获得正确的结果。

我们将首先使用Java标准库解决问题，然后使用一些流行的外部库来解决它。

最后，在本教程中，我们将解决单元测试方法中的所有解决方案。

## 3. 使用Java标准库

### 3.1. 经典解决方案：循环和计数

这可能是解决问题最直接的想法。

我们遍历输入字符串中的所有字符。此外，我们维护一个计数器变量并在我们看到空格字符时增加计数器。

最后，我们将获得字符串中的空格数：

```java
@Test
void givenString_whenCountSpaceByLooping_thenReturnsExpectedCount() {
    int spaceCount = 0;
    for (char c : INPUT_STRING.toCharArray()) {
        if (c == ' ') {
            spaceCount++;
        }
    }
    assertThat(spaceCount).isEqualTo(EXPECTED_COUNT);
}

```

### 3.2. 使用Java8 的 Stream API

[Stream API](https://www.baeldung.com/java-8-streams)从Java8 开始就存在了。

此外，从Java9 开始，一个新的[chars()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#chars())方法被添加到String类中，用于将char值从String转换为IntStream实例。

如果我们使用Java9 或更高版本，我们可以结合这两个功能来解决问题：

```java
@Test
void givenString_whenCountSpaceByJava8StreamFilter_thenReturnsExpectedCount() {
    long spaceCount = INPUT_STRING.chars().filter(c -> c == (int) ' ').count();
    assertThat(spaceCount).isEqualTo(EXPECTED_COUNT);
}

```

### 3.3. 使用 Regex 的Matcher.find()方法

到目前为止，我们已经看到了通过搜索给定字符串中的空格字符来计算的解决方案。我们使用character == ' ' 来检查字符是否为空格字符。

正则表达式(Regex)是搜索字符串的又一利器，Java对Regex有很好的支持。

因此，我们可以将单个空格定义为一个模式，并使用[Matcher.find()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Matcher.html#find())方法检查是否在输入字符串中找到该模式。

此外，为了获得空格的计数，我们在每次找到模式时递增一个计数器：

```java
@Test
void givenString_whenCountSpaceByRegexMatcher_thenReturnsExpectedCount() {
    Pattern pattern = Pattern.compile(" ");
    Matcher matcher = pattern.matcher(INPUT_STRING);
    int spaceCount = 0;
    while (matcher.find()) {
        spaceCount++;
    }
    assertThat(spaceCount).isEqualTo(EXPECTED_COUNT);
}

```

### 3.4. 使用String.replaceAll()方法

使用Matcher.find()方法搜索和查找空间非常简单。但是，由于我们讨论的是正则表达式，所以可以有其他快速计算空格的方法。

我们知道我们可以使用[String.replaceAll()](https://www.baeldung.com/string/replace-all)方法进行“搜索和替换”。

因此，如果我们将输入字符串中的所有非空格字符替换为空字符串，则输入中的所有空格都将是结果。

所以，如果我们想得到计数，结果字符串的长度就是答案。接下来，让我们试试这个想法：

```java
@Test
void givenString_whenCountSpaceByReplaceAll_thenReturnsExpectedCount() {
    int spaceCount = INPUT_STRING.replaceAll("[^ ]", "").length();
    assertThat(spaceCount).isEqualTo(EXPECTED_COUNT);
}

```

如上面的代码所示，我们只有一行来获取计数。

值得一提的是，在String.replaceAll()调用中，我们使用了模式“[^]”而不是“S”。 这是因为我们想要替换非空格字符而不仅仅是非空白字符。

### 3.5. 使用String.split() 方法

我们已经看到使用String.replaceAll()方法的解决方案简洁而紧凑。现在，让我们看看另一种解决问题的方法：使用[String.split()](https://www.baeldung.com/string/split)方法。

正如我们所知，我们可以将模式传递给String.split()方法并获得按模式拆分的字符串数组。

所以，我们的想法是，我们可以用一个空格分割输入字符串。然后，原始字符串中的空格数将比字符串数组 length 少一个。

现在，让我们看看这个想法是否可行：

```java
@Test
void givenString_whenCountSpaceBySplit_thenReturnsExpectedCount() {
    int spaceCount = INPUT_STRING.split(" ").length - 1;
    assertThat(spaceCount).isEqualTo(EXPECTED_COUNT);
}

```

## 4. 使用外部库

[Apache Commons Lang 3](https://www.baeldung.com/java-commons-lang-3)库广泛用于Java项目。此外，[Spring](https://www.baeldung.com/spring-tutorial)是Java爱好者中流行的框架。

这两个库都提供了一个方便的字符串实用程序类。

现在，让我们看看如何使用这些库计算输入字符串中的空格。

### 4.1. 使用 Apache Commons Lang 3 库

Apache Commons Lang 3 库提供了一个StringUtil类，其中包含许多方便的字符串相关方法。

要计算字符串中的空格，我们可以使用此类中的[countMatches()](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html#countMatches-java.lang.CharSequence-char-)方法。

在我们开始使用StringUtil类之前，我们应该检查库是否在类路径中。我们可以在pom.xml中添加[最新版本](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")的依赖：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

现在，让我们创建一个单元测试来展示如何使用此方法：

```java
@Test
void givenString_whenCountSpaceUsingApacheCommons_thenReturnsExpectedCount() {
    int spaceCount = StringUtils.countMatches(INPUT_STRING, " ");
    assertThat(spaceCount).isEqualTo(EXPECTED_COUNT);
}

```

### 4.2. 使用弹簧

今天，很多Java项目都是基于 Spring 框架的。因此，如果我们使用 Spring，则 Spring 提供的一个很好的字符串实用程序已经可以使用：[StringUtils](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/StringUtils.html)。

是的，它与 Apache Commons Lang 3 中的类同名。此外，它提供了一个[countOccurrencesOf()](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/StringUtils.html#countOccurrencesOf-java.lang.String-java.lang.String-)方法来计算字符串中某个字符的出现次数。

这正是我们要找的：

```java
@Test
void givenString_whenCountSpaceUsingSpring_thenReturnsExpectedCount() {
    int spaceCount = StringUtils.countOccurrencesOf(INPUT_STRING, " ");
    assertThat(spaceCount).isEqualTo(EXPECTED_COUNT);
}

```

## 5.总结

在本文中，我们介绍了计算输入字符串中空格字符的不同方法。
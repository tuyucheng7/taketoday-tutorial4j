## 1. 概述

Java标准库中已经提供了[String.toUpperCase()](https://www.baeldung.com/string/to-upper-case)方法，可以让我们将字符串中的字母全部转为大写。

在本教程中，我们将学习如何将给定字符串的第一个字符仅转换为大写。

## 二、问题介绍

一个例子可以很快说明这个问题。假设我们有一个输入字符串：

```java
String INPUT = "hi there, Nice to Meet You!";
```

给定此INPUT字符串，这是我们的预期结果：

```java
String EXPECTED = "Hi there, Nice to Meet You!";
```

如我们所见，我们只想将第一个字符“ h ”更改为“ H ”。但是，不应修改其余字符。

当然，如果输入字符串为空，结果也应该为空字符串：

```java
String EMPTY_INPUT = "";
String EMPTY_EXPECTED = "";
```

在本教程中，我们将解决该问题的几种解决方案。为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。

## 3. 使用substring()方法

解决问题的第一个想法是将输入字符串拆分为两个子字符串。例如，我们可以将INPUT字符串拆分为“ h ”和“ i there, Nice …”。“。换句话说，第一个子串只包含第一个字符，另一个子串包含字符串的其余字符。

然后，我们可以只对第一个子字符串应用toUpperCase()方法并连接第二个子字符串来解决问题。

Java的String类的[substring()](https://www.baeldung.com/string/substring)方法可以帮助我们得到两个子串：

-   INPUT.substring(0, 1) – 包含第一个字符的子串 1
-   INPUT.substring(1) – 包含其余字符的子串 2

那么接下来，让我们编写一个测试来查看该解决方案是否有效：

```java
String output = INPUT.substring(0, 1).toUpperCase() + INPUT.substring(1);
assertEquals(EXPECTED, output);
```

如果我们运行测试，它就会通过。但是，如果我们的输入是一个空字符串，这种方法将引发IndexOutOfBoundsException。这是因为当我们调用INPUT.substring(1)时，结束索引 ( 1 ) 大于空字符串的长度 ( 0 ) ：

```java
assertThrows(IndexOutOfBoundsException.class, () -> EMPTY_INPUT.substring(1));
```

此外，我们应该注意，如果输入字符串为null，这种方法将抛出NullPointerException。

因此，在使用 substring 方法之前，我们需要检查并确保输入字符串不为null或 empty。

## 4. 使用Matcher.replaceAll()方法

解决问题的另一种思路是使用[正则表达式](https://www.baeldung.com/regular-expressions-java)(“ ^. ”)匹配第一个字符，并将匹配到的组转换为大写。

在Java9 之前，这不是一件容易的事。这是因为[Matcher的替换方法](https://www.baeldung.com/regular-expressions-java#123-replacement-methods)，例如replaceAll()和replaceFirst()，不支持Function对象或 lambda 表达式替换器。但是，这在Java9 中发生了变化。

从Java9 开始，Matcher的替换方法支持Function对象作为替换器。也就是说，我们可以用一个函数来处理匹配到的字符序列，完成替换。当然，要解决我们的问题，只需要对匹配到的字符调用toUpperCase()方法即可：

```java
String output = Pattern.compile("^.").matcher(INPUT).replaceFirst(m -> m.group().toUpperCase());
assertEquals(EXPECTED, output);
```

如果我们试一试，测试就会通过。

如果正则表达式不匹配，则不会进行替换。因此，此解决方案也适用于空输入字符串：

```java
String emptyOutput = Pattern.compile("^.").matcher(EMPTY_INPUT).replaceFirst(m -> m.group().toUpperCase());
assertEquals(EMPTY_EXPECTED, emptyOutput);
```

值得一提的是，如果输入字符串为null，此解决方案也会抛出NullPointerException。所以，我们在使用它之前仍然需要做一个空检查。

## 5. 使用Apache Commons Lang 3中的 StringUtils

[Apache Commons Lang3](https://www.baeldung.com/java-commons-lang-3)是一个流行的库。它附带了许多方便的实用程序类，并扩展了标准Java库的功能。

它的[StringUtils](https://www.baeldung.com/java-commons-lang-3#the-stringutils-class)类提供了capitalize()方法，直接解决了我们的问题。

要使用该库，让我们首先添加[Maven 依赖](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3&core=gav)项：

```java
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

然后，像往常一样，让我们创建一个测试，看看它是如何工作的：

```java
String output = StringUtils.capitalize(INPUT);
assertEquals(EXPECTED, output);
```

如果我们执行它，测试就会通过。如我们所见，我们只是调用StringUtils.capitalize(INPUT)。然后图书馆为我们完成这项工作。

值得一提的是，StringUtils.capitalize()方法是 null 安全的，也适用于空输入字符串：

```java
String emptyOutput = StringUtils.capitalize(EMPTY_INPUT);
assertEquals(EMPTY_EXPECTED, emptyOutput);
String nullOutput = StringUtils.capitalize(null);
assertNull(nullOutput);
```

## 六，总结

在本文中，我们学习了如何将给定字符串的第一个字符转换为大写。
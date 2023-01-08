## 1. 概述

我们都知道拆分字符串是一项非常常见的任务。然而，我们经常只使用一个定界符来分割。

在本教程中，我们将详细讨论通过多个定界符拆分字符串的不同选项。

## 2.用多个定界符分割一个Java字符串

为了展示下面的每个解决方案如何执行拆分，我们将使用相同的示例字符串：

```java
String example = "Mary;Thomas:Jane-Kate";
String[] expectedArray = new String[]{"Mary", "Thomas", "Jane", "Kate"};
```

### 2.1. 正则表达式解决方案

程序员经常使用不同的[正则表达式](https://www.baeldung.com/regular-expressions-java)来定义字符串的搜索模式。在拆分字符串时，它们也是一种非常流行的解决方案。那么，让我们看看如何在Java中使用正则表达式通过多个定界符拆分字符串。

首先，我们不需要添加新的依赖项，因为java.util.regex包中提供了正则表达式。 我们只需要定义一个我们想要拆分的输入字符串和一个模式。

下一步是应用图案。一个模式可以匹配零次或多次。要按不同的分隔符拆分，我们应该只设置模式中的所有字符。

我们将编写一个简单的测试来演示这种方法：

```java
String[] names = example.split("[;:-]");
Assertions.assertEquals(4, names.length);
Assertions.assertArrayEquals(expectedArray, names);
```

我们已经定义了一个测试字符串，其名称应按模式中的字符拆分。模式本身包含一个分号、一个冒号和一个连字符。当应用于示例字符串时，我们将在数组中获得四个名称。

### 2.2. 番石榴溶液

[Guava](https://www.baeldung.com/guava-guide)还提供了一种通过多个定界符拆分字符串的解决方案。它的解决方案基于Splitter 类。此类使用分隔符序列从输入字符串中提取子字符串。我们可以用多种方式定义这个序列：

-   作为一个角色
-   一个固定的字符串
-   正则表达式
-   一个 CharMatcher实例

此外，Splitter类有两种定义分隔符的方法。因此，让我们对它们进行测试。

首先，我们将添加[Guava](https://search.maven.org/artifact/com.google.guava/guava/31.0.1-jre/bundle)依赖项：

```java
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

然后，我们将从 on方法开始：public static Splitter on(Pattern separatorPattern)

它采用用于定义拆分分隔符的模式。首先，我们将定义分隔符的组合并编译模式。之后，我们可以拆分字符串。

在我们的示例中，我们将使用正则表达式来指定分隔符：

```java
Iterable<String> names = Splitter.on(Pattern.compile("[;:-]")).split(example);
Assertions.assertEquals(4, Iterators.size(names.iterator()));
Assertions.assertIterableEquals(Arrays.asList(expectedArray), names);
```

另一种方法是 onPattern方法：public static Splitter onPattern(String separatorPattern)

此方法与上一个方法的区别在于 onPattern方法将模式作为字符串。不需要像on方法那样编译。我们将定义相同的定界符组合来测试 onPattern方法：

```java
Iterable<String> names = Splitter.onPattern("[;:-]").split(example);
Assertions.assertEquals(4, Iterators.size(names.iterator()));
Assertions.assertIterableEquals(Arrays.asList(expectedArray), names);
```

在这两个测试中，我们都成功地拆分了字符串并得到了包含四个名称的数组。

由于我们使用多个分隔符拆分输入字符串，因此我们还可以使用[CharMatcher](https://www.baeldung.com/guava-string-charmatcher)类中的anyOf方法 ： 

```java
Iterable<String> names = Splitter.on(CharMatcher.anyOf(";:-")).split(example);
Assertions.assertEquals(4, Iterators.size(names.iterator()));
Assertions.assertIterableEquals(Arrays.asList(expectedArray), names);
```

此选项仅随 Splitter类中的on方法一起提供 。结果与前两个测试相同。

### 2.3. Apache Commons 解决方案

我们将讨论的最后一个选项在 Apache Commons Lang 3 库中可用。

我们将从将[Apache Commons Lang](https://search.maven.org/artifact/org.apache.commons/commons-lang3/3.12.0/jar) 依赖项添加到我们的pom.xml文件开始：

```plaintext
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

接下来，我们将使用 StringUtils类中的split方法 ：

```java
String[] names = StringUtils.split(example, ";:-");
Assertions.assertEquals(4, names.length);
Assertions.assertArrayEquals(expectedArray, names);
```

我们只需要定义我们将用来分割字符串的所有字符。调用split方法会将 示例 字符串分成四个名称。

## 3.总结

在本文中，我们看到了用多个定界符拆分输入字符串的不同选项。首先，我们讨论了基于正则表达式和纯Java的解决方案。后来，我们展示了 Guava 中可用的不同选项。最后，我们使用基于 Apache Commons Lang 3 库的解决方案结束了示例。
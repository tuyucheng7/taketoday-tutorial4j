## 1. 概述

有很多方法可以检查[String是否包含 substring](https://www.baeldung.com/java-string-contains-substring)。在本文中，我们将在String中寻找子字符串，同时关注Java中[String.contains()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#contains(java.lang.CharSequence))的不区分大小写的变通方法。最重要的是，我们将提供如何解决此问题的示例。

## 2. 最简单的解决方案：String.toLowerCase

最简单的解决方案是使用[String.toLowerCase()](https://www.baeldung.com/java-string-convert-case)。在这种情况下，我们会将两个字符串都转换为小写，然后使用contains()方法：

```java
assertTrue(src.toLowerCase().contains(dest.toLowerCase()));
```

我们也可以使用[String.toUpperCase()](https://www.baeldung.com/java-string-convert-case)，它会提供相同的结果。

## 3. String.matches 与正则表达式

另一种选择是使用[String.matches()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#matches(java.lang.String)) 和正则表达式：

```java
assertTrue(src.matches("(?i)." + dest + "."));
```

matches()方法采用S字符串来表示正则表达式。(?i)启用不区分大小写，并且 .使用除换行符之外的所有字符。

## 4.字符串.regionMatches

我们也可以使用[String.regionMatches()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#regionMatches(boolean,int,java.lang.String,int,int))。它检查两个字符串区域是否匹配，对ignoreCase参数使用true ：

```java
public static boolean processRegionMatches(String src, String dest) {
    for (int i = src.length() - dest.length(); i >= 0; i--) 
        if (src.regionMatches(true, i, dest, 0, dest.length())) 
            return true; 
    return false;
}
assertTrue(processRegionMatches(src, dest));
```

为了提高性能，它开始匹配区域，同时考虑目标String的长度。然后，它减少了迭代器。

## 5.使用CASE_INSENSITIVE选项的模式

[java.util.regex.Pattern](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html)类为我们提供了一种使用matcher()方法匹配字符串的方法。在这种情况下，我们可以使用quote()方法来转义任何特殊字符，以及CASE_INSENSITIVE标志。让我们来看看：

```java
assertTrue(Pattern.compile(Pattern.quote(dest), Pattern.CASE_INSENSITIVE)
    .matcher(src)
    .find());
```

## 6.Apache Commons的 StringUtils.containsIgnoreCase

最后，我们将利用[Apache Commons StringUtils类](https://www.baeldung.com/string-processing-commons-lang)：

```java
assertTrue(StringUtils.containsIgnoreCase(src, dest));
```

## 七、性能比较

在这篇关于使用[contains](https://www.baeldung.com/java-string-contains-substring)[方法](https://www.baeldung.com/java-string-contains-substring)[检查子字符串的一般文章](https://www.baeldung.com/java-string-contains-substring)中，我们使用开源框架[Java Microbenchmark Harness](https://www.baeldung.com/java-microbenchmark-harness) (JMH) 以纳秒为单位比较这些方法的性能：

1.  模式 CASE_INSENSITIVE 正则表达式：399.387 ns
2.  字符串 toLowerCase：434.064 ns
3.  Apache Commons StringUtils：496.313 ns
4.  字符串区域匹配：718.842 ns
5.  字符串与正则表达式匹配：3964.346 ns

正如我们所见，获胜者是启用了CASE_INSENSITIVE标志的模式，紧随其后的是toLowerCase()。我们还注意到Java8 和Java11 之间的性能有了明显的改进。

## 八、总结

在本教程中，我们研究了几种不同的方法来检查字符串中的子字符串，同时忽略了Java中的大小写。

我们研究了使用String.toLowerCase()和toUpperCase()、String.matches()、String.regionMatches()、Apache Commons StringUtils.containsIgnoreCase()和Pattern.matcher().find()。

此外，我们评估了每个解决方案的性能，发现使用带有CASE_INSENSITIVE标志的java.util.regex.Pattern的compile()方法 性能最好。
## 1. 概述

在这个简短的教程中，我们将深入介绍如何检查字符串是否以Java中的特定模式结尾。

首先，我们将从考虑使用核心Java的解决方案开始。然后，我们将展示如何使用外部库完成同样的事情。

## 2. 使用字符串类

简而言之，[String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html) 提供了多个方便的选项来验证给定字符串是否以特定子字符串结尾。

让我们仔细看看每个选项。

### 2.1. String# endsWith方法

通常为此目的引入此方法。它提供了最直接的方法来检查String对象是否以另一个 string 结尾。

那么，让我们看看它的实际效果：

```java
public static boolean usingStringEndsWithMethod(String text, String suffix) {
    if (text == null || suffix == null) {
        return false;
    }
    return text.endsWith(suffix);
}
```

请注意endsWith不是空安全的。因此，我们首先需要确保text和suffix不为null以避免[NullPointerException](https://www.baeldung.com/java-illegalargumentexception-or-nullpointerexception#nullpointerexception)。

### 2.2. String# matches方法

matches是我们可以用来实现目标的另一种好方法。它只是检查字符串是否与给定的[正则表达式](https://www.baeldung.com/regular-expressions-java)匹配。

基本上，我们需要做的就是指定适合我们用例的正则表达式：

```java
public static boolean usingStringMatchesMethod(String text, String suffix) {
    if (text == null || suffix == null) {
        return false;
    }
    String regex = "." + suffix + "$";
    return text.matches(regex);
}
```

如我们所见，我们使用了一个正则表达式来匹配字符串text末尾 ( $ ) 的后缀。然后，我们将正则表达式传递给matches方法。

### 2.3. String# regionMatches方法

同样，我们可以使用[regionMatches](https://www.baeldung.com/string/region-matches) 方法来解决我们的中心问题。如果字符串部分与指定字符串完全匹配，则返回true ，否则返回false。

现在，让我们用一个例子来说明这一点：

```java
public static boolean usingStringRegionMatchesMethod(String text, String suffix) {
    if (text == null || suffix == null) {
        return false;
    }
    int toffset = text.length() - suffix.length();
    return text.regionMatches(toffset, suffix, 0, suffix.length());
}
```

toffset表示字符串中子区域的起始偏移量。因此，为了检查文本是否以指定的后缀结尾，toffset应等于文本的长度减去后缀的长度。

## 3. 使用模式类

或者，我们可以使用Pattern类来编译一个正则表达式来检查字符串是否以 pattern 结尾。

事不宜迟，让我们重用我们在上一节中指定的相同正则表达式：

```java
public static boolean usingPatternClass(String text, String suffix) {
    if (text == null || suffix == null) {
        return false;
    }
    Pattern pattern = Pattern.compile("." + suffix + "$");
    return pattern.matcher(text).find();
}
```

如上所示，Pattern编译前面的正则表达式，它表示字符串的结尾，并尝试将它与我们的字符串text匹配。

## 4. 使用 Apache Commons Lang

[Apache Commons Lang](https://www.baeldung.com/java-commons-lang-3)提供了一组用于字符串操作的现成实用程序类。在这些类中，我们找到了 StringUtils。

这个实用程序类带有一个有趣的方法，叫做endsWith。它以空安全方式检查字符序列是否以后缀结尾。

现在，让我们举例说明StringUtils.endsWith方法的使用：

```java
public static boolean usingApacheCommonsLang(String text, String suffix) {
    return StringUtils.endsWith(text, suffix);
}
```

## 5.总结

在本文中，我们探讨了检查字符串是否以特定模式结尾的不同方法。

首先，我们看到了使用内置Java类实现此目的的几种方法。然后，我们解释了如何使用 Apache Commons Lang 库做同样的事情。
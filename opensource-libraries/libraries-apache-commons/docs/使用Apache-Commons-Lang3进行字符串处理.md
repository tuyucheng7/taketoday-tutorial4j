## 1. 概述

[Apache Commons Lang 3](https://commons.apache.org/proper/commons-lang/)库提供对JavaAPI 核心类操作的支持。这种支持包括处理字符串、数字、日期、并发、对象反射等的方法。

除了提供对该库的一般介绍外，本教程还演示了用于操作String实例的StringUtils类的方法。

## 2.Maven依赖

为了使用 Commons Lang 3 库，只需使用以下依赖项从中央 Maven 存储库中拉取它：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

[可以在此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")找到该库的最新版本。

## 3.字符串工具

StringUtils类提供了对字符串进行null安全操作的方法。

此类的许多方法在类[java.lang.String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)中都有相应的定义，它们不是null安全的。但是，本节将转而关注几个在String类中没有等效方法的方法。

## 4. containsAny方法

containsAny方法检查给定的字符串是否包含给定字符集中的任何字符。这组字符可以以String或char可变参数的形式传递。

以下代码片段演示了如何使用此方法的两种重载形式进行结果验证：

```java
String string = "baeldung.com";
boolean contained1 = StringUtils.containsAny(string, 'a', 'b', 'c');
boolean contained2 = StringUtils.containsAny(string, 'x', 'y', 'z');
boolean contained3 = StringUtils.containsAny(string, "abc");
boolean contained4 = StringUtils.containsAny(string, "xyz");
 
assertTrue(contained1);
assertFalse(contained2);
assertTrue(contained3);
assertFalse(contained4);
```

## 5. containsIgnoreCase方法

containsIgnoreCase方法以不区分大小写的方式检查给定的字符串是否包含另一个字符串。

以下代码片段验证忽略大小写时字符串“baeldung.com”是否包含“BAELDUNG” ：

```java
String string = "baeldung.com";
boolean contained = StringUtils.containsIgnoreCase(string, "BAELDUNG");
 
assertTrue(contained);
```

## 6. countMatches方法

counterMatches方法计算字符或子字符串在给定字符串中出现的次数。

下面是该方法的演示，确认'w'出现了四次，“com”在字符串 “welcome to www.baeldung.com”中出现了两次：

```java
String string = "welcome to www.baeldung.com";
int charNum = StringUtils.countMatches(string, 'w');
int stringNum = StringUtils.countMatches(string, "com");
 
assertEquals(4, charNum);
assertEquals(2, stringNum);
```

## 7. Appending 和 Prepending 方法

appendIfMissing和appendIfMissingIgnoreCase方法将后缀附加到给定String的末尾，如果它还没有分别以区分大小写和不区分大小写的方式以任何传入的后缀结尾。

类似地，prependIfMissing和prependIfMissingIgnoreCase方法会在给定字符串的开头添加一个前缀(如果它不以任何传入的前缀开头)。

在下面的示例中，appendIfMissing和prependIfMissing方法用于向字符串“baeldung.com”添加后缀和前缀，而不重复这些词缀：

```java
String string = "baeldung.com";
String stringWithSuffix = StringUtils.appendIfMissing(string, ".com");
String stringWithPrefix = StringUtils.prependIfMissing(string, "www.");
 
assertEquals("baeldung.com", stringWithSuffix);
assertEquals("www.baeldung.com", stringWithPrefix);
```

## 八、大小写转换法

String类已经定义了将 String 的所有字符转换为大写或小写的方法。本小节仅说明以其他方式更改String大小写的方法的使用，包括swapCase、capitalize和uncapitalize。

swapCase方法交换字符串的大小写，将大写字母更改为小写字母，将小写字母更改为大写字母：

```java
String originalString = "baeldung.COM";
String swappedString = StringUtils.swapCase(originalString);
 
assertEquals("BAELDUNG.com", swappedString);
```

capitalize方法将给定String的第一个字符转换为大写，其余所有字符保持不变：

```java
String originalString = "baeldung";
String capitalizedString = StringUtils.capitalize(originalString);
 
assertEquals("Baeldung", capitalizedString);
```

uncapitalize方法将给定String的第一个字符转换为小写，其余所有字符保持不变：

```java
String originalString = "Baeldung";
String uncapitalizedString = StringUtils.uncapitalize(originalString);
 
assertEquals("baeldung", uncapitalizedString);
```

## 九、倒车法

StringUtils类定义了两种反转字符串的方法： reverse和reverseDelimited。reverse方法以相反的顺序重新排列String的所有字符，而reverseDelimited方法重新排列字符组，以指定的分隔符分隔。

以下代码片段反转字符串“baeldung”并验证结果：

```java
String originalString = "baeldung";
String reversedString = StringUtils.reverse(originalString);
 
assertEquals("gnudleab", reversedString);
```

使用reverseDelimited方法，字符会成组反转，而不是单独反转：

```java
String originalString = "www.baeldung.com";
String reversedString = StringUtils.reverseDelimited(originalString, '.');
 
assertEquals("com.baeldung.www", reversedString);
```

## 10. rotate()方法

rotate()方法将String的字符循环移动多个位置。下面的代码片段将字符串“baeldung”的所有字符向右移动四个位置并验证结果：

```java
String originalString = "baeldung";
String rotatedString = StringUtils.rotate(originalString, 4);
 
assertEquals("dungbael", rotatedString);
```

## 11.差异法

difference方法比较两个字符串，返回第二个字符串的剩余部分，从它与第一个不同的位置开始。以下代码片段比较两个字符串：“Baeldung Tutorials”和“Baeldung Courses”两个方向并验证结果：

```java
String tutorials = "Baeldung Tutorials";
String courses = "Baeldung Courses";
String diff1 = StringUtils.difference(tutorials, courses);
String diff2 = StringUtils.difference(courses, tutorials);
 
assertEquals("Courses", diff1);
assertEquals("Tutorials", diff2);
```

## 12.总结

本教程介绍了 Apache Commons Lang 3 中的字符串处理，并介绍了我们可以从StringUtils库类中使用的主要 API。
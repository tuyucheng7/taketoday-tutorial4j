## 1. 概述

[String.trim()](https://www.baeldung.com/string/trim)方法删除尾随和前导空格。但是，不支持仅执行 L-Trim 或 R-Trim。

在本教程中，我们将看到实现此目的的几种方法；最后，我们将比较它们的性能。

## 2.while循环_

最简单的解决方案是使用几个while循环遍历字符串。

对于 L-Trim，我们将从左到右读取字符串，直到遇到非空白字符：

```java
int i = 0;
while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
    i++;
}
String ltrim = s.substring(i);

```

ltrim是从第一个非空白字符开始的子字符串。

或者对于 R-Trim，我们将从右到左读取字符串，直到遇到非空白字符：

```java
int i = s.length()-1;
while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
    i--;
}
String rtrim = s.substring(0,i+1);
```

rtrim是一个从开头开始到第一个非空白字符结束的子字符串。

## 3. String.replaceAll使用正则表达式

另一种选择是使用[String.replaceAll()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#replaceAll(java.lang.String,java.lang.String))和正则表达式：

```java
String ltrim = src.replaceAll("^s+", "");
String rtrim = src.replaceAll("s+$", "");
```

(s+) 是匹配一个或多个空白字符的正则表达式。正则表达式开头和结尾的插入符号 (^) 和 ($) 匹配一行的开头和结尾。

## 4. Pattern.compile()和.matcher()

[我们也可以通过java.util.regex.Pattern](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html)重用正则表达式：

```java
private static Pattern LTRIM = Pattern.compile("^s+");
private static Pattern RTRIM = Pattern.compile("s+$");

String ltrim = LTRIM.matcher(s).replaceAll("");
String rtim = RTRIM.matcher(s).replaceAll("");
```

## 5.阿帕奇公地

此外，我们可以利用[Apache Commons StringUtils#stripStart和#stripEnd](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html#stripStart-java.lang.String-java.lang.String-)方法来删除空格。

为此，让我们首先添加[commons -lang3 依赖](https://mvnrepository.com/artifact/org.apache.commons/commons-lang3)项：

```java
<dependency> 
    <groupId>org.apache.commons</groupId> 
    <artifactId>commons-lang3</artifactId> 
    <version>3.12.0</version> 
</dependency>
```

按照文档，我们使用null来去除空格：

```java
String ltrim = StringUtils.stripStart(src, null);
String rtrim = StringUtils.stripEnd(src, null);
```

## 6.番石榴

最后，我们将利用[Guava ](https://guava.dev/releases/20.0-rc1/api/docs/com/google/common/base/CharMatcher.html#trimLeadingFrom-java.lang.CharSequence-)[CharMatcher#trimLadingFrom](https://guava.dev/releases/20.0-rc1/api/docs/com/google/common/base/CharMatcher.html#trimLeadingFrom-java.lang.CharSequence-)[和](https://guava.dev/releases/20.0-rc1/api/docs/com/google/common/base/CharMatcher.html#trimLeadingFrom-java.lang.CharSequence-)[#trimTrailingFrom](https://guava.dev/releases/20.0-rc1/api/docs/com/google/common/base/CharMatcher.html#trimLeadingFrom-java.lang.CharSequence-) 方法来获得相同的结果。

同样，让我们添加适当的 Maven 依赖项，这次是[guava](https://mvnrepository.com/artifact/com.google.guava/guava)：

```java
<dependency> 
    <groupId>com.google.guava</groupId> 
    <artifactId>guava</artifactId> 
    <version>31.0.1-jre</version> 
</dependency>
```

在 Guava 中，它与在 Apache Commons 中的做法非常相似，只是使用了更有针对性的方法：

```java
String ltrim = CharMatcher.whitespace().trimLeadingFrom(s); 
String rtrim = CharMatcher.whitespace().trimTrailingFrom(s);
```

## 七、性能比较

让我们看看这些方法的性能。像往常一样，我们将使用开源框架[Java Microbenchmark Harness](https://www.baeldung.com/java-microbenchmark-harness) (JMH) 在纳秒内比较不同的替代方案。

### 7.1. 基准设置

对于基准测试的初始配置，我们使用了五个分叉和以纳秒为单位的平均时间计算时间：

```java
@Fork(5)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
```

在设置方法中，我们正在初始化原始消息字段和结果字符串以与之进行比较：

```java
@Setup
public void setup() {
    src = "       White spaces left and right          ";
    ltrimResult = "White spaces left and right          ";
    rtrimResult = "       White spaces left and right";
}
```

所有基准测试都首先删除左边的空格，然后删除右边的空格，最后将结果与预期的字符串进行比较。

### 7.2. while循环

对于我们的第一个基准测试，让我们使用while循环方法：

```java
@Benchmark
public boolean whileCharacters() {
    String ltrim = whileLtrim(src);
    String rtrim = whileRtrim(src);
    return checkStrings(ltrim, rtrim);
}
```

### 7.3. String.replaceAll() 与正则表达式

然后，让我们试试String.replaceAll()：

```java
@Benchmark
public boolean replaceAllRegularExpression() {
    String ltrim = src.replaceAll("^s+", "");
    String rtrim = src.replaceAll("s+$", "");
    return checkStrings(ltrim, rtrim);
}
```

### 7.4. 模式.编译().匹配()

之后是Pattern.compile().matches()：

```java
@Benchmark
public boolean patternMatchesLTtrimRTrim() {
    String ltrim = patternLtrim(src);
    String rtrim = patternRtrim(src);
    return checkStrings(ltrim, rtrim);
}
```

### 7.5. 阿帕奇公地

四、Apache Commons：

```java
@Benchmark
public boolean apacheCommonsStringUtils() {
    String ltrim = StringUtils.stripStart(src, " ");
    String rtrim = StringUtils.stripEnd(src, " ");
    return checkStrings(ltrim, rtrim);
}
```

### 7.6. 番石榴

最后，让我们使用 Guava：

```java
@Benchmark
public boolean guavaCharMatcher() {
    String ltrim = CharMatcher.whitespace().trimLeadingFrom(src);
    String rtrim = CharMatcher.whitespace().trimTrailingFrom(src);
    return checkStrings(ltrim, rtrim);
}
```

### 7.7. 结果分析

我们应该得到一些类似于以下的结果：

```shell
# Run complete. Total time: 00:16:57

Benchmark                               Mode  Cnt     Score    Error  Units
LTrimRTrim.apacheCommonsStringUtils     avgt  100   108,718 ±  4,503  ns/op
LTrimRTrim.guavaCharMatcher             avgt  100   113,601 ±  5,563  ns/op
LTrimRTrim.patternMatchesLTtrimRTrim    avgt  100   850,085 ± 17,578  ns/op
LTrimRTrim.replaceAllRegularExpression  avgt  100  1046,660 ±  7,151  ns/op
LTrimRTrim.whileCharacters              avgt  100   110,379 ±  1,032  ns/op
```

看起来我们的赢家是while循环、Apache Commons 和 Guava！

## 八、总结

在本教程中，我们研究了几种不同的方法来删除String开头和结尾的空白字符。

我们使用while循环、String.replaceAll()、 Pattern.matcher().replaceAll()、 Apache Commons 和 Guava 来获得这个结果。
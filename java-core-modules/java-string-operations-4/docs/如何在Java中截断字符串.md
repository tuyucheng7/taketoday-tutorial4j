## 1. 概述

在本教程中，我们将学习在Java中将String截断为所需字符数的多种方法。

我们将从探索使用 JDK 本身执行此操作的方法开始。然后我们将看看如何使用一些流行的第三方库来做到这一点。

## 2. 使用 JDK截断字符串

Java 提供了许多方便的方法来截断String。让我们来看看。

### 2.1. 使用String的 substring() 方法

String类带有一个方便的方法，称为 [substring](https://www.baeldung.com/java-substring)。 顾名思义， substring()返回给定字符串在指定索引之间的部分。

让我们看看它的实际效果：

```java
static String usingSubstringMethod(String text, int length) {
    if (text.length() <= length) {
        return text;
    } else {
        return text.substring(0, length);
    }
}
```

在上面的示例中，如果指定的长度大于text的长度，我们将返回text本身。这是因为传递给substring()的长度大于String中的字符数会导致IndexOutOfBoundsException。

否则，我们返回从索引零开始并延伸到(但不包括)索引长度处的字符的子字符串。

让我们使用测试用例来确认这一点：

```java
static final String TEXT = "Welcome to baeldung.com";

@Test
public void givenStringAndLength_whenUsingSubstringMethod_thenTrim() {

    assertEquals(TrimStringOnLength.usingSubstringMethod(TEXT, 10), "Welcome to");
}
```

正如我们所见，开始索引是包含的，结束索引是不包含的。因此，索引长度处的字符将不会包含在返回的子字符串中。

### 2.2. 使用String的split() 方法

截断字符串的另一种方法是使用split()方法，该方法使用正则表达式将字符串拆分成多个部分。

[在这里，我们将使用称为正向后](https://www.baeldung.com/java-regex-lookahead-lookbehind#positive-lookbehind)视的正则表达式功能来匹配从String开头开始的指定数量的字符：

```java
static String usingSplitMethod(String text, int length) {

    String[] results = text.split("(?<=G.{" + length + "})");

    return results[0];
}
```

结果的第一个元素要么是我们截断 的String，要么是原始 String (如果length比text长) 。

让我们测试一下我们的方法：

```java
@Test
public void givenStringAndLength_whenUsingSplitMethod_thenTrim() {

    assertEquals(TrimStringOnLength.usingSplitMethod(TEXT, 13), "Welcome to ba");
}
```

### 2.3. 使用模式 类

类似地，我们可以使用Pattern 类来编译一个[正则表达式](https://www.baeldung.com/regular-expressions-java)，该正则表达式匹配String的开头，最多指定数量的字符。

例如，让我们使用{1,” + length + “}。此正则表达式匹配至少一个且最多长度的字符：

```java
static String usingPattern(String text, int length) {

    Optional<String> result = Pattern.compile(".{1," + length + "}")
      .matcher(text)
      .results()
      .map(MatchResult::group)
      .findFirst();

    return result.isPresent() ? result.get() : EMPTY;

}
```

正如我们在上面看到的，在将正则表达式编译成Pattern之后，我们可以使用Pattern 的 matcher()方法根据该正则表达式解释我们的String 。然后我们能够对结果进行分组并返回第一个，这是我们截断的String。

现在让我们添加一个测试用例来验证我们的代码是否按预期工作：

```java
@Test
public void givenStringAndLength_whenUsingPattern_thenTrim() {

    assertEquals(TrimStringOnLength.usingPattern(TEXT, 19), "Welcome to baeldung");
}
```

### 2.4. 使用CharSequence 的sc odePoints()方法

Java 9 提供了一个codePoints()方法来将String转换为[代码点值](https://developer.mozilla.org/en-US/docs/Glossary/Code_point)流。

让我们看看如何将此方法与[流 API](https://www.baeldung.com/java-8-streams)结合使用来截断字符串：

```java
static String usingCodePointsMethod(String text, int length) {

    return text.codePoints()
      .limit(length)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
}
```

在这里，我们使用[limit()](https://www.baeldung.com/java-stream-skip-vs-limit#limit)方法将Stream限制为给定的长度。然后我们使用StringBuilder来构建截断的字符串。

接下来，让我们验证我们的方法是否有效：

```java
@Test
public void givenStringAndLength_whenUsingCodePointsMethod_thenTrim() {

    assertEquals(TrimStringOnLength.usingCodePointsMethod(TEXT, 6), "Welcom");
}
```

## 3.Apache 公共库

[Apache Commons Lang](https://www.baeldung.com/java-commons-lang-3)库包含一个用于操作String的StringUtils类。

首先，让我们将 Apache Commons[依赖](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

### 3.1. 使用StringUtils 的 left()方法

StringUtils有一个有用的静态方法，叫做left()。StringUtils.left()以空安全方式返回String最左边指定数量的字符：

```java
static String usingLeftMethod(String text, int length) {

    return StringUtils.left(text, length);
}
```

### 3.2. 使用StringUtils的truncate()方法

或者，我们可以使用StringUtils.truncate()来实现相同的目标：

```java
public static String usingTruncateMethod(String text, int length) {

    return StringUtils.truncate(text, length);
}
```

## 4.番石榴图书馆

除了使用核心Java方法和 Apache Commons 库来截断String之外，我们还可以使用[Guava](https://www.baeldung.com/guava-guide)。让我们首先将 Guava [依赖](https://search.maven.org/search?q=g:com.google.guava AND a:guava)项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

现在我们可以使用 Guava 的Splitter类来截断我们的 String：

```java
static String usingSplitter(String text, int length) {
    
    Iterable<String> parts = Splitter.fixedLength(length)
      .split(text);

    return parts.iterator()
      .next();
}
```

我们使用Splitter.fixedLength()将我们的String拆分为给定长度的多个部分。然后，我们返回结果的第一个元素。

## 5.总结

在本文中，我们学习了在Java中将String截断为特定字符数的多种方法。

我们研究了使用 JDK 执行此操作的一些方法。然后我们使用几个第三方库截断了String 。
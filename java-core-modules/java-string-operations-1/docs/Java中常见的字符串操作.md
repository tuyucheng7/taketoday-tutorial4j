## 1. 概述

基于字符串的值和操作在日常开发中很常见，任何Java开发人员都必须能够处理它们。

在本教程中，我们将提供常见字符串操作的快速备忘单。

此外，我们将阐明equals 和“==”之间以及 StringUtils#isBlank 和#isEmpty 之间的区别。

## 2. 将 Char 转换为 String

一个char表示Java中的一个字符。但在大多数情况下，我们需要一个String。

因此，让我们从将char s 转换为String s开始：

```java
String toStringWithConcatenation(final char c) {
    return String.valueOf(c);
}
```

## 3.追加字符串

另一个经常需要的操作是附加带有其他值的字符串，例如char：

```java
String appendWithConcatenation(final String prefix, final char c) {
    return prefix + c;
}
```

我们也可以使用StringBuilder附加其他基本类型：

```java
String appendWithStringBuilder(final String prefix, final char c) {
    return new StringBuilder(prefix).append(c).toString();
}
```

## 4. 通过索引获取字符

如果我们需要从字符串中提取一个字符，API 提供了我们想要的一切：

```java
char getCharacterByIndex(final String text, final int index) {
    return text.charAt(index);
}
```

由于String使用char[]作为后备数据结构，因此索引从零开始。

## 5. 处理 ASCII 值

我们可以通过转换轻松地在char和它的数字表示 (ASCII) 之间切换：

```java
int asciiValue(final char character) {
    return (int) character;
}

char fromAsciiValue(final int value) {
    Assert.isTrue(value >= 0 && value < 65536, "value is not a valid character");
    return (char) value;
}
```

当然，因为 int是 4 个无符号字节而 char是 2 个无符号字节，我们需要检查以确保我们使用的是合法的字符值。

## 6. 删除所有空格

有时我们需要去掉一些字符，最常见的是空格。一个好方法是使用带有[正则表达式的](https://www.baeldung.com/regular-expressions-java)replaceAll方法：

```java
String removeWhiteSpace(final String text) {
    return text.replaceAll("s+", "");
}
```

## 7. 将集合连接到字符串

另一个常见的用例是当我们有某种Collection并想从中创建一个字符串时：

```java
<T> String fromCollection(final Collection<T> collection) { 
   return collection.stream().map(Objects::toString).collect(Collectors.joining(", "));
}
```

请注意Collectors.joining允许[指定前缀或后缀](https://www.baeldung.com/java-8-collectors)。

## 8. 拆分字符串

或者另一方面，我们可以使用split方法通过分隔符拆分字符串：

```java
String[] splitByRegExPipe(final String text) {
   return text.split("|");
}
```

同样，我们在这里使用正则表达式，这次是用管道分割。由于我们要使用特殊字符，[因此我们必须对其进行转义](https://www.baeldung.com/java-regexp-escape-char)。

另一种可能性是使用Pattern类：

```java
String[] splitByPatternPipe(final String text) {
    return text.split(Pattern.quote("|"));
}
```

## 9. 将所有字符作为流处理

在详细处理的情况下，我们可以将字符串转换为 [IntStream](https://www.baeldung.com/java-string-to-stream)：

```java
IntStream getStream(final String text) {
    return text.chars();
}
```

## 10.引用平等与价值平等

尽管字符串看起来像原始类型，但实际上并非如此。

因此，我们必须区分引用相等和值相等。引用相等总是意味着值相等，但通常情况并非如此。首先，我们检查“==”操作，后者使用equals方法：

```java
@Test
public void whenUsingEquals_thenWeCheckForTheSameValue() {
    assertTrue("Values are equal", new String("Test").equals("Test"));
}

@Test
public void whenUsingEqualsSign_thenWeCheckForReferenceEquality() {
    assertFalse("References are not equal", new String("Test") == "Test");
}
```

请注意，文字[驻留在字符串池中](https://www.baeldung.com/java-string-pool)。因此，编译器有时可以将它们优化为相同的引用：

```java
@Test
public void whenTheCompileCanBuildUpAString_thenWeGetTheSameReference() {
    assertTrue("Literals are concatenated by the compiler", "Test" == "Te"+"st");
}
```

## 11. 空白字符串与空字符串

isBlank 和isEmpty之间存在细微差别 。

如果字符串为空或长度为零，则该字符串为空。而如果字符串为空或仅包含空白字符，则该字符串为空：

```java
@Test
public void whenUsingIsEmpty_thenWeCheckForNullorLengthZero() {
    assertTrue("null is empty", isEmpty(null));
    assertTrue("nothing is empty", isEmpty(""));
    assertFalse("whitespace is not empty", isEmpty(" "));
    assertFalse("whitespace is not empty", isEmpty("n"));
    assertFalse("whitespace is not empty", isEmpty("t"));
    assertFalse("text is not empty", isEmpty("Anything!"));
}

@Test
public void whenUsingIsBlank_thenWeCheckForNullorOnlyContainingWhitespace() {
    assertTrue("null is blank", isBlank(null));
    assertTrue("nothing is blank", isBlank(""));
    assertTrue("whitespace is blank", isBlank("tt tnr"));
    assertFalse("test is not blank", isBlank("Anything!"));
}
```

## 12.总结

字符串是各种应用程序中的核心类型。在本教程中，我们学习了一些常见场景下的关键操作。

此外，我们还提供了更详细的参考资料。
## 1. 概述

在本教程中，我们将阐明如何在Java中每n 个字符拆分一个字符串。

首先，我们将首先探索使用内置Java方法执行此操作的可能方法。然后，我们将展示如何使用 Guava 实现相同的目标。

## 2. 使用String#split方法

String类带有一个方便的方法，称为[split](https://www.baeldung.com/string/split)。顾名思义，它根据给定的分隔符或正则表达式将字符串拆分为多个部分。

让我们看看它的实际效果：

```java
public static List<String> usingSplitMethod(String text, int n) {
    String[] results = text.split("(?<=G.{" + n + "})");

    return Arrays.asList(results);
}
```

如我们所见，我们使用了正则表达式(?<=G.{” + n + “})，其中n是字符数。这是一个[积极](https://www.baeldung.com/java-regex-lookahead-lookbehind#positive-lookbehind)的回顾断言，它匹配一个字符串，该字符串具有最后一个匹配项(G)后跟n 个字符。

现在，让我们创建一个测试用例来检查一切是否按预期工作：

```java
public class SplitStringEveryNthCharUnitTest {

    public static final String TEXT = "abcdefgh123456";

    @Test
    public void givenString_whenUsingSplit_thenSplit() {
        List<String> results = SplitStringEveryNthChar.usingSplitMethod(TEXT, 3);

        assertThat(results, contains("abc", "def", "gh1", "234", "56"));
    }
}
```

## 3. 使用String#substring方法

在每第 n 个字符处拆分String对象的另一种方法是使用substring 方法。

基本上，我们可以遍历字符串并调用substring根据指定的n 个字符将其分成多个部分：

```java
public static List<String> usingSubstringMethod(String text, int n) {
    List<String> results = new ArrayList<>();
    int length = text.length();

    for (int i = 0; i < length; i += n) {
        results.add(text.substring(i, Math.min(length, i + n)));
    }

    return results;
}
```

如上所示，substring方法允许我们获取当前索引i 和i+n 之间的字符串部分。

现在，让我们使用测试用例来确认这一点：

```java
@Test
public void givenString_whenUsingSubstring_thenSplit() {
    List<String> results = SplitStringEveryNthChar.usingSubstringMethod(TEXT, 4);

    assertThat(results, contains("abcd", "efgh", "1234", "56"));
}
```

## 4. 使用模式类

[Pattern](https://www.baeldung.com/regular-expressions-java#Package)提供了一种简洁的方式来编译正则表达式并将其与给定的字符串进行匹配。

因此，使用正确的正则表达式，我们可以使用Pattern来实现我们的目标：

```java
public static List<String> usingPattern(String text, int n) {
    return Pattern.compile(".{1," + n + "}")
        .matcher(text)
        .results()
        .map(MatchResult::group)
        .collect(Collectors.toList());
}
```

如我们所见，我们使用“.{1,n}”作为正则表达式来创建我们的Pattern对象。它匹配至少一个和最多n 个字符。

最后，让我们写一个简单的测试：

```java
@Test
public void givenString_whenUsingPattern_thenSplit() {
    List<String> results = SplitStringEveryNthChar.usingPattern(TEXT, 5);

    assertThat(results, contains("abcde", "fgh12", "3456"));
}
```

## 5.使用番石榴

现在我们知道了如何使用核心Java方法每n 个字符拆分一个字符串，让我们看看如何使用[Guava](https://www.baeldung.com/guava-guide)库做同样的事情：

```java
public static List<String> usingGuava(String text, int n) {
    Iterable<String> parts = Splitter.fixedLength(n).split(text);

    return ImmutableList.copyOf(parts);
}
```

Guava 提供了Splitter类来简化从字符串中提取子串的逻辑。fixedLength ()方法将给定的字符串拆分为指定长度的片段。

让我们用一个测试用例来验证我们的方法：

```java
@Test
public void givenString_whenUsingGuava_thenSplit() {
    List<String> results = SplitStringEveryNthChar.usingGuava(TEXT, 6);

    assertThat(results, contains("abcdef", "gh1234", "56"));
}
```

## 六，总结

总而言之，我们解释了如何使用Java方法在每第 n 个字符处拆分字符串。

之后，我们展示了如何使用 Guava 库实现相同的目标。
## 1. 从字符串中删除特殊字符

让我们从删除String中的所有特殊字符开始。

在以下示例中，我们使用retainFrom()删除所有不是数字或字母的字符：

```java
@Test
public void whenRemoveSpecialCharacters_thenRemoved(){
    String input = "Hel.lo,}12";
    CharMatcher matcher = CharMatcher.javaLetterOrDigit();
    String result = matcher.retainFrom(input);

    assertEquals("Hello12", result);
}
```

## 2. 从字符串中删除非 ASCII 字符

我们还可以使用CharMatcher从字符串中删除非 ASCII 字符，如下例所示：

```java
@Test
public void whenRemoveNonASCIIChars_thenRemoved() {
    String input = "あhello₤";

    String result = CharMatcher.ascii().retainFrom(input);
    assertEquals("hello", result);

    result = CharMatcher.inRange('0', 'z').retainFrom(input);
    assertEquals("hello", result);
}
```

## 3.删除不在字符集中的字符

现在 - 让我们看看如何删除不属于给定Charset的字符。在以下示例中——我们将删除不属于“cp437”字符集的字符：

```java
@Test
public void whenRemoveCharsNotInCharset_thenRemoved() {
    Charset charset = Charset.forName("cp437");
    CharsetEncoder encoder = charset.newEncoder();

    Predicate<Character> inRange = new Predicate<Character>() {
        @Override
        public boolean apply(Character c) {
            return encoder.canEncode(c);
        }
    };

    String result = CharMatcher.forPredicate(inRange)
                               .retainFrom("helloは");
    assertEquals("hello", result);
}
```

注意：我们使用CharsetEncoder创建了一个Predicate来检查给定的Character是否可以编码为给定的Charset。

## 4.验证字符串

接下来 – 让我们看看如何使用CharMatcher验证字符串。

我们可以使用matchesAllOf()来检查是否所有字符都匹配一个条件。我们可以使用matchesNoneOf()来检查条件是否不适用于任何String字符。

在下面的示例中，我们检查我们的String是否为小写，是否包含至少一个 ' e ' 字符并且不包含任何数字：

```java
@Test
public void whenValidateString_thenValid(){
    String input = "hello";

    boolean result = CharMatcher.javaLowerCase().matchesAllOf(input);
    assertTrue(result);

    result = CharMatcher.is('e').matchesAnyOf(input);
    assertTrue(result);

    result = CharMatcher.javaDigit().matchesNoneOf(input);
    assertTrue(result);
}
```

## 5.修剪字符串

现在 - 让我们看看如何使用CharMatcher修剪字符串。

在下面的示例中，我们使用trimLeading()、trimTrailing和trimFrom()来修剪我们的String：

```java
@Test
public void whenTrimString_thenTrimmed() {
    String input = "---hello,,,";

    String result = CharMatcher.is('-').trimLeadingFrom(input);
    assertEquals("hello,,,", result);

    result = CharMatcher.is(',').trimTrailingFrom(input);
    assertEquals("---hello", result);

    result = CharMatcher.anyOf("-,").trimFrom(input);
    assertEquals("hello", result);
}
```

## 6. 折叠字符串

接下来 – 让我们看看如何使用CharMatcher折叠字符串。

在下面的示例中，我们使用collapseFrom()将连续的空格替换为“ – ”：

```java
@Test
public void whenCollapseFromString_thenCollapsed() {
    String input = "       hel    lo      ";

    String result = CharMatcher.is(' ').collapseFrom(input, '-');
    assertEquals("-hel-lo-", result);

    result = CharMatcher.is(' ').trimAndCollapseFrom(input, '-');
    assertEquals("hel-lo", result);
}
```

## 7. 从字符串替换

我们可以使用CharMatcher来替换String中的特定字符，如下例所示：

```java
@Test
public void whenReplaceFromString_thenReplaced() {
    String input = "apple-banana.";

    String result = CharMatcher.anyOf("-.").replaceFrom(input, '!');
    assertEquals("apple!banana!", result);

    result = CharMatcher.is('-').replaceFrom(input, " and ");
    assertEquals("apple and banana.", result);
}
```

## 8.计算字符出现次数

最后 - 让我们看看如何使用CharMatcher计算字符的出现次数。

在下面的例子中，我们统计' a ':' h '之间的逗号和字符：

```java
@Test
public void whenCountCharInString_thenCorrect() {
    String input = "a, c, z, 1, 2";

    int result = CharMatcher.is(',').countIn(input);
    assertEquals(4, result);

    result = CharMatcher.inRange('a', 'h').countIn(input);
    assertEquals(2, result);
}
```

## 9.总结

在本文中，我们展示了一些更有用的 API 和将 Guava 用于字符串的实际使用示例。
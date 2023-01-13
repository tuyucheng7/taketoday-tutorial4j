## 1. 概述

在本教程中，我们介绍Hamcrest文本匹配器。

## 2. Maven配置

首先，我们需要在pom.xml中添加以下依赖项：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>java-hamcrest</artifactId>
    <version>2.0.0.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 文本相等匹配器

当然，我们可以使用标准的isEqual()匹配器检查两个字符串是否相等。此外，我们有两个特定于字符串类型的匹配器：equalToIgnoringCase()和equalToIgnoringWhiteSpace()。

下面检查两个字符串是否相等(忽略大小写)：

```java
@Test
void whenTwoStringsAreEqual_thenCorrect() {
    String first = "hello";
    String second = "Hello";

    assertThat(first, equalToIgnoringCase(second));
}
```

我们还可以检查两个字符串是否相等(忽略前导和尾随空格)：

```java
@Test
void whenTwoStringsAreEqualWithWhiteSpace_thenCorrect() {
    String first = "hello";
    String second = "   Hello   ";

    assertThat(first, equalToIgnoringWhiteSpace(second));
}
```

## 4. 空文本匹配器

我们可以通过使用blankString()和blankOrNullString()匹配器来检查字符串是否为empty，这意味着它只包含空格：

```java
@Test
void whenStringIsBlank_thenCorrect() {
    String first = "  ";
    String second = null;
    
    assertThat(first, blankString());
    assertThat(first, blankOrNullString());
    assertThat(second, blankOrNullString());
}
```

另一方面，如果我们想验证一个String是否为null，我们可以使用emptyString()匹配器：

```java
@Test
void whenStringIsEmpty_thenCorrect() {
    String first = "";
    String second = null;

    assertThat(first, emptyString());
    assertThat(first, emptyOrNullString());
    assertThat(second, emptyOrNullString());
}
```

## 5. 模式匹配器

我们还可以使用matchesPattern()方法检查给定文本是否与正则表达式匹配：

```java
@Test
void whenStringMatchPattern_thenCorrect() {
    String first = "hello";

    assertThat(first, matchesPattern("[a-z]+"));
}
```

## 6. 子字符串匹配器

我们可以使用containsString()或containsStringIgnoringCase()方法来确定一个文本是否包含另一个子文本：

```java
@Test
void whenVerifyStringContains_thenCorrect() {
    String first = "hello";

    assertThat(first, containsString("lo"));
    assertThat(first, containsStringIgnoringCase("EL"));
}
```

如果我们希望子字符串按特定顺序排列，我们可以调用stringContainsInOrder()匹配器：

```java
@Test
void whenVerifyStringContainsInOrder_thenCorrect() {
    String first = "hello";
    
    assertThat(first, stringContainsInOrder("e","l","o"));
}
```

接下来，让我们看看如何检查一个字符串是否以给定的字符串开头：

```java
@Test
void whenVerifyStringStartsWith_thenCorrect() {
    String first = "hello";

    assertThat(first, startsWith("he"));
    assertThat(first, startsWithIgnoringCase("HEL"));
}
```

最后，我们可以检查一个字符串是否以指定的字符串结尾：

```java
@Test
void whenVerifyStringEndsWith_thenCorrect() {
    String first = "hello";

    assertThat(first, endsWith("lo"));
    assertThat(first, endsWithIgnoringCase("LO"));
}
```

## 7. 总结

在这个快速教程中，我们介绍了Hamcrest文本匹配器。
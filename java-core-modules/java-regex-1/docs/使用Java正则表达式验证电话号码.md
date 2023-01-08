## 1. 概述

有时，我们需要验证文本以确保其内容符合某种格式。在本快速教程中，我们将了解如何使用[正则表达式](https://www.baeldung.com/regular-expressions-java)验证不同格式的电话号码。

## 2.验证电话号码的正则表达式

### 2.1. 十位数

让我们从一个简单的表达式开始，该表达式将检查数字是否有十位数字而没有其他内容：

```java
@Test
public void whenMatchesTenDigitsNumber_thenCorrect() {
    Pattern pattern = Pattern.compile("^d{10}$");
    Matcher matcher = pattern.matcher("2055550125");
    assertTrue(matcher.matches());
}
```

这个表达式将允许像2055550125这样的数字。

### 2.2. 带空格、点或连字符的数字

在第二个示例中，让我们看看如何在数字之间允许可选的空格、点或连字符 (-) ：

```java
@Test
public void whenMatchesTenDigitsNumberWhitespacesDotHyphen_thenCorrect() {
    Pattern pattern = Pattern.compile("^(d{3}[- .]?){2}d{4}$");
    Matcher matcher = pattern.matcher("202 555 0125");
    assertTrue(matcher.matches());
}
```

为了实现这个额外的目标(可选的空格或连字符)，我们只是添加了字符：

-   [- .]?

此模式将允许2055550125、202 555 0125、202.555.0125和202-555-0125等数字。

### 2.3. 带括号的数字

接下来，让我们添加将电话的第一部分放在括号之间的可能性：

```java
@Test
public void whenMatchesTenDigitsNumberParenthesis_thenCorrect() {
    Pattern pattern = Pattern.compile"^(((d{3}))|d{3})[- .]?d{3}[- .]?d{4}$");
    Matcher matcher = pattern.matcher("(202) 555-0125");
    assertTrue(matcher.matches());
}
```

为了允许在数字中使用可选的括号，我们在正则表达式中添加了以下字符：

-   ((d{3}))|d{3})

此表达式将允许诸如(202)5550125、(202) 555-0125或(202)-555-0125之类的数字。此外，此表达式还将允许前面示例中涵盖的电话号码。

### 2.4. 带国际前缀的号码

最后，让我们看看如何在电话号码的开头允许使用国际前缀：

```java
@Test
public void whenMatchesTenDigitsNumberPrefix_thenCorrect() {
  Pattern pattern = Pattern.compile("^(+d{1,3}( )?)?(((d{3}))|d{3})[- .]?d{3}[- .]?d{4}$");
  Matcher matcher = pattern.matcher("+111 (202) 555-0125");
  
  assertTrue(matcher.matches());
}

```

为了允许在我们的号码中使用前缀，我们在模式的开头添加了以下字符：

-   (+d{1,3}( )?)?

考虑到国际前缀通常是最多三位数的号码，此表达式将使电话号码能够包含国际前缀。

## 3. 应用多个正则表达式

正如我们所见，有效的电话号码可以采用多种不同的格式。因此，我们可能想要检查我们的String是否符合这些格式中的任何一种。

在上一节中，我们从一个简单的表达式开始，并增加了更多的复杂性，以实现涵盖多种格式的目标。但是，有时不可能只使用一种表达方式。在本节中，我们将看到如何将多个正则表达式连接成一个。

如果我们无法创建一个通用的正则表达式来验证我们想要涵盖的所有可能情况，我们可以为每种情况定义不同的表达式，然后通过用管道符号 (|) 连接它们来一起使用它们。

让我们看一个使用以下表达式的示例：

-   上一节中使用的表达式：

    -   ^(+d{1,3}( )?)?(((d{3}))|d{3})[- .]?d{ 3}[-.]?d{4}$

-   允许

    +111 123 456 789 这样的数字的正则表达式：

    -   ^(+d{1,3}( )?)?(d{3}[ ]?){2}d{3}$

-   允许

    +111 123 45 67 89 等数字的模式：

    -   ^(+d{1,3}( )?)?(d{3}[ ]?)(d{2}[ ]?){2}d{2}$

```java
@Test
public void whenMatchesPhoneNumber_thenCorrect() {
    String patterns 
      = "^(+d{1,3}( )?)?(((d{3}))|d{3})[- .]?d{3}[- .]?d{4}$" 
      + "|^(+d{1,3}( )?)?(d{3}[ ]?){2}d{3}$" 
      + "|^(+d{1,3}( )?)?(d{3}[ ]?)(d{2}[ ]?){2}d{2}$";

    String[] validPhoneNumbers 
      = {"2055550125","202 555 0125", "(202) 555-0125", "+111 (202) 555-0125", 
      "636 856 789", "+111 636 856 789", "636 85 67 89", "+111 636 85 67 89"};

    Pattern pattern = Pattern.compile(patterns);
    for(String phoneNumber : validPhoneNumbers) {
        Matcher matcher = pattern.matcher(phoneNumber);
        assertTrue(matcher.matches());
    }
}
```

正如我们在上面的示例中看到的那样，通过使用管道符号，我们可以一次使用三个表达式，从而使我们能够涵盖比仅使用一个正则表达式更多的情况。

## 4。总结

在本文中，我们了解了如何使用不同的正则表达式检查字符串是否包含有效的电话号码。我们还学习了如何同时使用多个正则表达式。
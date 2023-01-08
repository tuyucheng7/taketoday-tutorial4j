## 1. 概述

在Java中使用正则表达式时，我们通常希望在字符序列中搜索给定的Pattern。为了促进这一点，[Java Regular Expressions API](https://www.baeldung.com/regular-expressions-java)提供了Matcher类，我们可以使用它来将给定的正则表达式与文本进行匹配。

作为一般规则，我们几乎总是希望使用Matcher类的两种流行方法之一：

-   寻找()
-   火柴()

在本快速教程中，我们将使用一组简单的示例来了解这些方法之间的区别。

## 2. find()方法

简而言之，find()方法试图在给定的字符串中查找正则表达式模式的出现。如果在字符串中发现多次出现，那么第一次调用find()将跳转到第一次出现的地方。此后，对find()方法的每次后续调用都将转到下一个匹配项，一个接一个。

假设我们只想在提供的字符串“goodbye 2019 and welcome 2020”中搜索四位数。

为此，我们将使用模式“dddd”：

```java
@Test
public void whenFindFourDigitWorks_thenCorrect() {
    Pattern stringPattern = Pattern.compile("dddd");
    Matcher m = stringPattern.matcher("goodbye 2019 and welcome 2020");

    assertTrue(m.find());
    assertEquals(8, m.start());
    assertEquals("2019", m.group());
    assertEquals(12, m.end());
    
    assertTrue(m.find());
    assertEquals(25, m.start());
    assertEquals("2020", m.group());
    assertEquals(29, m.end());
    
    assertFalse(m.find());
}
```

由于我们在此示例中出现了两次 - 2019和2020 - find()方法将返回true两次，一旦到达匹配区域的末尾，它将返回false。

一旦找到任何匹配项，我们就可以使用start()、group()和end()等方法来获取有关匹配项的更多详细信息，如上所示。

start ()方法将给出匹配的开始索引，end()将返回匹配结束后字符的最后一个索引，而group()将返回匹配的实际值。

## 3. find(int)方法

我们还有 find 方法的重载版本 — find(int)。它以起始索引作为参数，并将起始索引视为在字符串中查找匹配项的起点。

让我们看看如何在与之前相同的示例中使用此方法：

```java
@Test
public void givenStartIndex_whenFindFourDigitWorks_thenCorrect() {
    Pattern stringPattern = Pattern.compile("dddd");
    Matcher m = stringPattern.matcher("goodbye 2019 and welcome 2020");

    assertTrue(m.find(20));
    assertEquals(25, m.start());
    assertEquals("2020", m.group());
    assertEquals(29, m.end());  
}
```

由于我们提供了20的起始索引，我们可以看到现在只找到一个事件 - 2020，它按预期出现在该索引之后。而且，与find()的情况一样，我们可以使用start()、group()和end()等方法来提取有关匹配项的更多详细信息。

## 4. matches()方法

另一方面，matches ()方法尝试将整个字符串与模式匹配。

对于同一个示例，matches()将返回false：

```java
@Test
public void whenMatchFourDigitWorks_thenFail() {
    Pattern stringPattern = Pattern.compile("dddd");
    Matcher m = stringPattern.matcher("goodbye 2019 and welcome 2020");
 
    assertFalse(m.matches());
}

```

这是因为它会尝试将“dddd”与整个字符串“ goodbye 2019 and welcome 2020”进行匹配——不像find()和find(int)方法，这两个方法都会查找字符串中任意位置出现的模式。

如果我们将字符串更改为四位数“2019”，则matches()将返回true：

```java
@Test
public void whenMatchFourDigitWorks_thenCorrect() {
    Pattern stringPattern = Pattern.compile("dddd");
    Matcher m = stringPattern.matcher("2019");
    
    assertTrue(m.matches());
    assertEquals(0, m.start());
    assertEquals("2019", m.group());
    assertEquals(4, m.end());
    assertTrue(m.matches());
}
```

如上所示，我们还可以使用start()、group()和end()等方法来收集有关匹配的更多详细信息。需要注意的一个有趣的一点是，多次调用find() 可能会在调用这些方法后返回不同的输出，正如我们在第一个示例中看到的那样，但matches()将始终返回相同的值。

## 5. matcher()和Pattern.matches()的区别

正如我们在上一节中看到的，matcher()方法返回一个Matcher，它将给定的输入与模式相匹配。

另一方面，Pattern.matches()是一个静态方法，它编译正则表达式并将 整个输入与其匹配。

让我们创建测试用例来突出差异：

```java
@Test
public void whenUsingMatcher_thenReturnTrue() {
    Pattern pattern = Pattern.compile(REGEX);
    Matcher matcher = pattern.matcher(STRING_INPUT);

    assertTrue(matcher.find());
}
```

简而言之，当我们使用matcher()时，我们会问这样一个问题：字符串是否包含模式？

对于Pattern.matches()，我们要问：字符串是模式吗？

让我们看看它的实际效果：

```java
@Test
public void whenUsingMatches_thenReturnFalse() {
    assertFalse(Pattern.matches(REGEX, STRING_INPUT));
}
```

由于Pattern.matches()尝试匹配整个字符串，因此它返回false。

## 六，总结

在本文中，我们通过一个实际示例了解了find()、find(int)和matches()之间的区别。我们还看到了诸如start()、group()和end()之类的各种方法如何帮助我们提取有关给定匹配的更多详细信息。
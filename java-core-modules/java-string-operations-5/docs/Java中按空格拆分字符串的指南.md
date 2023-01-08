## 一、概述

在 Java 中，String可以看作是多个子字符串的串联。此外，通常使用空格作为分隔符来构建子字符串集合并将其存储到单个字符串中。

在本教程中，我们将学习如何通过空格、制表符或换行符等空白字符拆分字符串。

## 2.字符串示例

首先，我们需要构建一些String样本，我们可以将其用作按空格拆分的输入。因此，让我们首先将一些空白字符定义为String常量 ，以便我们可以方便地重用它们：

```java
String SPACE = " ";
String TAB = "	";
String NEW_LINE = "n";
```

接下来，让我们使用它们作为分隔符来定义包含不同水果名称的String样本：

```java
String FRUITS_TAB_SEPARATED = "Apple" + TAB + "Banana" + TAB + "Mango" + TAB + "Orange";
String FRUITS_SPACE_SEPARATED = "Apple" + SPACE + "Banana" + SPACE + "Mango" + SPACE + "Orange";
String FRUITS_NEWLINE_SEPARATED = "Apple" + NEW_LINE + "Banana" + NEW_LINE + "Mango" + NEW_LINE + "Orange";
```

最后，我们还要编写verifySplit()方法，我们将重复使用该方法来验证按空白字符拆分这些字符串的预期结果：

```java
private void verifySplit(String[] fruitArray) {
    assertEquals(4, fruitArray.length);
    assertEquals("Apple", fruitArray[0]);
    assertEquals("Banana", fruitArray[1]);
    assertEquals("Mango", fruitArray[2]);
    assertEquals("Orange", fruitArray[3]);
}
```

现在我们已经构建了输入字符串，我们准备探索不同的策略来拆分这些字符串并验证拆分。

## 3. 使用分隔符正则表达式拆分

String类的split()方法是拆分字符串的事实标准。它接受一个定界符正则表达式并将拆分生成一个String数组：

```java
String[] split(String regex);
```

首先，让我们用一个空格字符拆分FRUITS_SPACE_SEPARATED 字符串：

```java
@Test
public void givenSpaceSeparatedString_whenSplitUsingSpace_shouldGetExpectedResult() {
    String fruits = FRUITS_SPACE_SEPARATED;
    String[] fruitArray = fruits.split(SPACE);
    verifySplit(fruitArray);
}
```

同样，我们可以分别使用TAB和NEW_LINE作为分隔符正则表达式来拆分FRUITS_TAB_SEPARATED和FRUITS_NEWLINE_SEPARATED 。

接下来，让我们尝试对空格、制表符和换行符使用更通用的正则表达式，并使用相同的正则表达式拆分所有字符串样本：

```java
@Test
public void givenWhiteSpaceSeparatedString_whenSplitUsingWhiteSpaceRegex_shouldGetExpectedResult() {
    String whitespaceRegex = SPACE + "|" + TAB + "|" + NEW_LINE;
    String[] allSamples = new String[] { FRUITS_SPACE_SEPARATED, FRUITS_TAB_SEPARATED, FRUITS_NEWLINE_SEPARATED };
    for (String fruits : allSamples) {
        String[] fruitArray = fruits.split(whitespaceRegex);
        verifySplit(fruitArray);
    }
}
```

到目前为止，看起来我们做对了！

最后，让我们通过使用本身代表各种空白字符的空白元字符 ( s )来简化我们的方法：

```java
@Test
public void givenNewlineSeparatedString_whenSplitUsingWhiteSpaceMetaChar_shouldGetExpectedResult() {
    String whitespaceMetaChar = "s";
    String[] allSamples = new String[] { FRUITS_SPACE_SEPARATED, FRUITS_TAB_SEPARATED, FRUITS_NEWLINE_SEPARATED };
    for (String fruits : allSamples) {
        String[] fruitArray = fruits.split(whitespaceMetaChar);
        verifySplit(fruitArray);
    }
}
```

我们应该注意，使用s元字符比为空白创建自定义正则表达式更方便和可靠。此外，如果我们的输入字符串可以有多个空白字符作为分隔符，那么我们可以使用[s+而不是s](https://www.baeldung.com/java-regex-s-splus#diff) 而无需更改其余代码。

## 4. 使用StringTokenizer拆分

用空格分割字符串是一种常见的用例，以至于许多 Java 库公开了一个接口来实现这一点，而无需明确指定分隔符。在本节中，让我们学习如何使用StringTokenizer来解决这个用例：

```java
@Test
public void givenSpaceSeparatedString_whenSplitUsingStringTokenizer_shouldGetExpectedResult() {
    String fruits = FRUITS_SPACE_SEPARATED;
    StringTokenizer tokenizer = new StringTokenizer(fruits);
    String[] fruitArray = new String[tokenizer.countTokens()];
    int index = 0;
    while (tokenizer.hasMoreTokens()) {
        fruitArray[index++] = tokenizer.nextToken();
    }
    verifySplit(fruitArray);
}
```

我们可以看到我们没有提供任何分隔符，因为StringTokenizer默认使用空白分隔符。此外，代码遵循[迭代器设计模式](https://www.baeldung.com/java-iterator)，其中hasMoreTokens()方法决定循环终止条件，而nextToken()给出下一个 split。

此外，我们应该注意到我们使用了countTokens()方法来预先确定拆分的数量。但是，如果我们想在一个序列中一次一个地使用生成的拆分，则不需要这样做。通常，当输入字符串很长并且我们希望立即进行下一次拆分而不需要等待整个拆分过程完成时，我们应该使用这种方法。

## 5. 使用 Apache Commons 拆分

org.apache.commons.lang3包的StringUtils类提供了一个用于拆分String的split()实用方法。与StringTokenizer类一样，它使用空格作为分割字符串的默认分隔符：

```java
public static String[] split(String str);
```

让我们首先在项目的pom.xml文件中添加[commons-lang3依赖项：](https://search.maven.org/search?q=g: org.apache.commons AND a:commons-lang3)

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
```

接下来，让我们通过拆分String样本来查看实际效果：

```java
@Test
public void givenWhiteSpaceSeparatedString_whenSplitUsingStringUtils_shouldGetExpectedResult() {
    String[] allSamples = new String[] { FRUITS_SPACE_SEPARATED, FRUITS_TAB_SEPARATED, FRUITS_NEWLINE_SEPARATED };
    for (String fruits : allSamples) {
        String[] fruitArray = StringUtils.split(fruits);
        verifySplit(fruitArray);
    }
}
```

使用StringUtils类的split()实用程序方法的优点之一是调用者不必显式执行 null 检查。那是因为split()方法优雅地处理了这个问题。让我们继续看看实际效果：

```java
@Test
public void givenNullString_whenSplitUsingStringUtils_shouldReturnNull() {
    String fruits = null;
    String[] fruitArray = StringUtils.split(fruits);
    assertNull(fruitArray);
}
```

正如预期的那样，该方法为空输入返回空值。

## 六，结论

在本教程中，我们学习了多种按空格拆分字符串的方法。此外，我们还注意到与某些策略相关的优势和推荐的最佳实践。
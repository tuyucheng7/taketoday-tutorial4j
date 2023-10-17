## 1. 概述

在本教程中，我们将讨论JavaRegex API，以及如何在Java编程语言中使用正则表达式。

在正则表达式的世界中，有许多不同的风格可供选择，例如 grep、Perl、Python、PHP、awk 等等。

这意味着适用于一种编程语言的正则表达式可能不适用于另一种编程语言。Java 中的正则表达式语法与 Perl 中的最相似。

## 2.设置

要在Java中使用正则表达式，我们不需要任何特殊设置。JDK 包含一个特殊的包，java.util.regex，完全专用于正则表达式操作。我们只需要将它导入到我们的代码中。

此外，java.lang.String类还具有我们在代码中常用的内置正则表达式支持。

## 3. Java正则表达式包

java.util.regex包由三个类组成：Pattern、Matcher和PatternSyntaxException ：

-   Pattern对象是一个已编译的正则表达式。Pattern类不提供公共构造函数。要创建一个模式，我们必须首先调用它的一个公共静态编译方法，然后它将返回一个Pattern对象。这些方法接受正则表达式作为第一个参数。
-   Matcher对象解释模式并对输入String执行匹配操作。它还没有定义公共构造函数。我们通过在Pattern对象上调用matcher方法来获得Matcher对象。
-   PatternSyntaxException对象是一个未经检查的异常，指示正则表达式模式中的语法错误。

我们将详细探讨这些类；但是，我们必须首先了解如何在Java中构造正则表达式。

如果我们已经熟悉来自不同环境的正则表达式，我们可能会发现某些差异，但它们是最小的。

## 4. 简单例子

让我们从最简单的正则表达式用例开始。正如我们之前提到的，当我们将正则表达式应用于字符串时，它可能会匹配零次或多次。

java.util.regex API支持的最基本的模式匹配形式是字符串文字的匹配。例如，如果正则表达式是foo并且输入字符串是foo，则匹配将成功，因为字符串是相同的：

```java
@Test
public void givenText_whenSimpleRegexMatches_thenCorrect() {
    Pattern pattern = Pattern.compile("foo");
    Matcher matcher = pattern.matcher("foo");
 
    assertTrue(matcher.find());
}
```

我们将首先通过调用其静态编译方法并向其传递我们要使用的模式来创建一个Pattern对象。

然后我们将创建一个Matcher对象，调用Pattern对象的matcher方法并将我们要检查匹配的文本传递给它。

最后，我们将调用Matcher 对象中的find方法。

find方法在输入文本中不断前进并为每个匹配项返回 true，因此我们也可以使用它来查找匹配项计数：

```java
@Test
public void givenText_whenSimpleRegexMatchesTwice_thenCorrect() {
    Pattern pattern = Pattern.compile("foo");
    Matcher matcher = pattern.matcher("foofoo");
    int matches = 0;
    while (matcher.find()) {
        matches++;
    }
 
    assertEquals(matches, 2);
}
```

由于我们将运行更多测试，因此我们可以抽象出用于在名为runTest的方法中查找匹配项数量的逻辑：

```java
public static int runTest(String regex, String text) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);
    int matches = 0;
    while (matcher.find()) {
        matches++;
    }
    return matches;
}
```

当我们得到 0 个匹配项时，测试应该失败；否则，它应该通过。

## 5.元字符

元字符会影响模式的匹配方式；在某种程度上，它们为搜索模式添加了逻辑。Java API 支持多种元字符，最直接的是点“.”，它匹配任何字符：

```java
@Test
public void givenText_whenMatchesWithDotMetach_thenCorrect() {
    int matches = runTest(".", "foo");
    
    assertTrue(matches > 0);
}
```

让我们考虑前面的示例，其中正则表达式foo与文本foo以及foofoo匹配两次。如果我们在正则表达式中使用点元字符，我们将不会在第二种情况下获得两个匹配项：

```java
@Test
public void givenRepeatedText_whenMatchesOnceWithDotMetach_thenCorrect() {
    int matches= runTest("foo.", "foofoo");
 
    assertEquals(matches, 1);
}
```

注意正则表达式中foo后面的点。匹配器匹配前面有 foo 的每个文本，因为最后一个点部分表示后面的任何字符。所以在找到第一个foo之后，其余的被视为任何字符。这就是为什么只有一场比赛。

API 支持其他几个元字符，<([{^-=$!|]})?+.>，我们将在本文中进一步探讨。

## 6. 字符类

浏览官方[Pattern](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html) 类规范，我们将发现支持的正则表达式构造的摘要。在字符类下，我们有大约 6 个结构。

### 6.1. 或类

我们将其构造为[abc]。这匹配集合中的任何元素：

```java
@Test
public void givenORSet_whenMatchesAny_thenCorrect() {
    int matches = runTest("[abc]", "b");
 
    assertEquals(matches, 1);
}
```

如果它们都出现在文本中，它将分别匹配每个元素而不考虑顺序：

```java
@Test
public void givenORSet_whenMatchesAnyAndAll_thenCorrect() {
    int matches = runTest("[abc]", "cab");
 
    assertEquals(matches, 3);
}
```

它们也可以作为String的一部分进行交替。在下面的示例中，当我们通过交替首字母与集合中的每个元素来创建不同的单词时，它们都是匹配的：

```java
@Test
public void givenORSet_whenMatchesAllCombinations_thenCorrect() {
    int matches = runTest("[bcr]at", "bat cat rat");
 
    assertEquals(matches, 3);
}
```

### 6.2. 或类

通过添加插入符号作为第一个元素来否定上述集合：

```java
@Test
public void givenNORSet_whenMatchesNon_thenCorrect() {
    int matches = runTest("[^abc]", "g");
 
    assertTrue(matches > 0);
}
```

这是另一种情况：

```java
@Test
public void givenNORSet_whenMatchesAllExceptElements_thenCorrect() {
    int matches = runTest("[^bcr]at", "sat mat eat");
 
    assertTrue(matches > 0);
}
```

### 6.3. 范围类

我们可以定义一个类，使用连字符 (-) 指定匹配文本应属于的范围。同样，我们也可以否定一个范围。

匹配大写字母：

```java
@Test
public void givenUpperCaseRange_whenMatchesUpperCase_
  thenCorrect() {
    int matches = runTest(
      "[A-Z]", "Two Uppercase alphabets 34 overall");
 
    assertEquals(matches, 2);
}
```

匹配小写字母：

```java
@Test
public void givenLowerCaseRange_whenMatchesLowerCase_
  thenCorrect() {
    int matches = runTest(
      "[a-z]", "Two Uppercase alphabets 34 overall");
 
    assertEquals(matches, 26);
}
```

匹配大小写字母：

```java
@Test
public void givenBothLowerAndUpperCaseRange_
  whenMatchesAllLetters_thenCorrect() {
    int matches = runTest(
      "[a-zA-Z]", "Two Uppercase alphabets 34 overall");
 
    assertEquals(matches, 28);
}
```

匹配给定范围的数字：

```java
@Test
public void givenNumberRange_whenMatchesAccurately_
  thenCorrect() {
    int matches = runTest(
      "[1-5]", "Two Uppercase alphabets 34 overall");
 
    assertEquals(matches, 2);
}
```

匹配另一个范围的数字：

```java
@Test
public void givenNumberRange_whenMatchesAccurately_
  thenCorrect2(){
    int matches = runTest(
      "3[0-5]", "Two Uppercase alphabets 34 overall");
  
    assertEquals(matches, 1);
}
```

6.4. 联合班

联合字符类是组合两个或多个字符类的结果：

```java
@Test
public void givenTwoSets_whenMatchesUnion_thenCorrect() {
    int matches = runTest("[1-3[7-9]]", "123456789");
 
    assertEquals(matches, 6);
}
```

上面的测试只会匹配九个整数中的六个，因为并集会跳过 4、5 和 6。

### 6.5. 路口类

与 union 类类似，此类是从两个或多个集合之间选取公共元素而产生的。要应用交集，我们使用&&：

```java
@Test
public void givenTwoSets_whenMatchesIntersection_thenCorrect() {
    int matches = runTest("[1-6&&[3-9]]", "123456789");
 
    assertEquals(matches, 4);
}
```

我们将得到四个匹配项，因为这两个集合的交集只有四个元素。

### 6.6. 减法类

我们可以使用减法来否定一个或多个字符类。例如，我们可以匹配一组奇数小数：

```java
@Test
public void givenSetWithSubtraction_whenMatchesAccurately_thenCorrect() {
    int matches = runTest("[0-9&&[^2468]]", "123456789");
 
    assertEquals(matches, 5);
}
```

只会匹配1、3、5、7、9 。

## 7.预定义字符类

Java 正则表达式 API 也接受预定义的字符类。上面的一些字符类可以用更短的形式表示，尽管这会使代码不那么直观。此正则表达式的Java版本的一个特殊方面是转义字符。

正如我们将看到的，大多数字符将以反斜杠开头，这在Java中具有特殊含义。对于要由Pattern类编译的这些，必须转义前导反斜杠，即d变为d。

匹配数字，相当于[0-9]：

```java
@Test
public void givenDigits_whenMatches_thenCorrect() {
    int matches = runTest("d", "123");
 
    assertEquals(matches, 3);
}
```

匹配非数字，相当于[^0-9]：

```java
@Test
public void givenNonDigits_whenMatches_thenCorrect() {
    int mathces = runTest("D", "a6c");
 
    assertEquals(matches, 2);
}
```

匹配空白：

```java
@Test
public void givenWhiteSpace_whenMatches_thenCorrect() {
    int matches = runTest("s", "a c");
 
    assertEquals(matches, 1);
}
```

匹配非空白：

```java
@Test
public void givenNonWhiteSpace_whenMatches_thenCorrect() {
    int matches = runTest("S", "a c");
 
    assertEquals(matches, 2);
}
```

匹配一个单词字符，相当于[a-zA-Z_0-9]：

```java
@Test
public void givenWordCharacter_whenMatches_thenCorrect() {
    int matches = runTest("w", "hi!");
 
    assertEquals(matches, 2);
}
```

匹配一个非单词字符：

```java
@Test
public void givenNonWordCharacter_whenMatches_thenCorrect() {
    int matches = runTest("W", "hi!");
 
    assertEquals(matches, 1);
}
```

## 8.量词

Java 正则表达式 API 还允许我们使用量词。这些使我们能够通过指定要匹配的出现次数来进一步调整匹配的行为。

要匹配文本零次或一次，我们使用? 量词：

```java
@Test
public void givenZeroOrOneQuantifier_whenMatches_thenCorrect() {
    int matches = runTest("a?", "hi");
 
    assertEquals(matches, 3);
}
```

或者，我们可以使用大括号语法，Java regex API 也支持这种语法：

```java
@Test
public void givenZeroOrOneQuantifier_whenMatches_thenCorrect2() {
    int matches = runTest("a{0,1}", "hi");
 
    assertEquals(matches, 3);
}
```

这个例子介绍了零长度匹配的概念。碰巧的是，如果量词的匹配阈值为零，它总是匹配文本中的所有内容，包括每个输入末尾的空字符串。这意味着即使输入为空，它也会返回一个零长度匹配项。

这解释了为什么我们在上面的例子中得到了三个匹配，尽管有一个长度为 2 的 S字符串。第三个匹配项是零长度空字符串。

为了匹配文本零次或无限次，我们使用  量词，类似于 ?：

```java
@Test
public void givenZeroOrManyQuantifier_whenMatches_thenCorrect() {
     int matches = runTest("a", "hi");
 
     assertEquals(matches, 3);
}
```

支持的替代方案：

```java
@Test
public void givenZeroOrManyQuantifier_whenMatches_thenCorrect2() {
    int matches = runTest("a{0,}", "hi");
 
    assertEquals(matches, 3);
}
```

有差异的量词是+，它的匹配阈值为1。如果所需的String根本没有出现，则没有匹配项，甚至没有零长度的String：

```java
@Test
public void givenOneOrManyQuantifier_whenMatches_thenCorrect() {
    int matches = runTest("a+", "hi");
 
    assertFalse(matches);
}
```

支持的替代方案：

```java
@Test
public void givenOneOrManyQuantifier_whenMatches_thenCorrect2() {
    int matches = runTest("a{1,}", "hi");
 
    assertFalse(matches);
}
```

与 Perl 和其他语言一样，我们可以多次使用大括号语法来匹配给定的文本：

```java
@Test
public void givenBraceQuantifier_whenMatches_thenCorrect() {
    int matches = runTest("a{3}", "aaaaaa");
 
    assertEquals(matches, 2);
}
```

在上面的示例中，我们得到了两个匹配项，因为只有当a连续出现三次时才会出现匹配项。然而，在接下来的测试中，我们不会得到匹配，因为文本只连续出现了两次：

```java
@Test
public void givenBraceQuantifier_whenFailsToMatch_thenCorrect() {
    int matches = runTest("a{3}", "aa");
 
    assertFalse(matches > 0);
}
```

当我们在大括号中使用范围时，匹配将是贪婪的，从范围的较高端开始匹配：

```java
@Test
public void givenBraceQuantifierWithRange_whenMatches_thenCorrect() {
    int matches = runTest("a{2,3}", "aaaa");
 
    assertEquals(matches, 1);
}
```

这里我们指定至少出现两次，但不超过三次，所以我们得到一个匹配，其中匹配器看到一个aaa和一个单独的a，它们无法匹配。

但是，API 允许我们指定一种懒惰或不情愿的方法，以便匹配器可以从范围的下限开始，匹配两次出现的aa和aa：

```java
@Test
public void givenBraceQuantifierWithRange_whenMatchesLazily_thenCorrect() {
    int matches = runTest("a{2,3}?", "aaaa");
 
    assertEquals(matches, 2);
}
```

## 9. 捕获组

API 还允许我们通过捕获组将多个字符视为一个单元。它会将数字附加到捕获组，并允许使用这些数字进行反向引用。

在本节中，我们将看到一些有关如何在Javaregex API 中使用捕获组的示例。

让我们使用一个仅当输入文本包含彼此相邻的两个数字时才匹配的捕获组：

```java
@Test
public void givenCapturingGroup_whenMatches_thenCorrect() {
    int matches = runTest("(dd)", "12");
 
    assertEquals(matches, 1);
}
```

附加到上述匹配项的数字是1，使用反向引用告诉匹配器我们要匹配文本匹配部分的另一个匹配项。这样，而不是为输入设置两个单独的匹配项：

```java
@Test
public void givenCapturingGroup_whenMatches_thenCorrect2() {
    int matches = runTest("(dd)", "1212");
 
    assertEquals(matches, 2);
}
```

我们可以有一个匹配，但使用反向引用传播相同的正则表达式匹配以跨越输入的整个长度：

```java
@Test
public void givenCapturingGroup_whenMatchesWithBackReference_
  thenCorrect() {
    int matches = runTest("(dd)1", "1212");
 
    assertEquals(matches, 1);
}
```

我们将不得不在没有反向引用的情况下重复正则表达式以获得相同的结果：

```java
@Test
public void givenCapturingGroup_whenMatches_thenCorrect3() {
    int matches = runTest("(dd)(dd)", "1212");
 
    assertEquals(matches, 1);
}
```

类似地，对于任何其他重复次数，反向引用可以使匹配器将输入视为单个匹配：

```java
@Test
public void givenCapturingGroup_whenMatchesWithBackReference_
  thenCorrect2() {
    int matches = runTest("(dd)111", "12121212");
 
    assertEquals(matches, 1);
}
```

但是，如果我们甚至更改最后一位数字，匹配也会失败：

```java
@Test
public void givenCapturingGroupAndWrongInput_
  whenMatchFailsWithBackReference_thenCorrect() {
    int matches = runTest("(dd)1", "1213");
 
    assertFalse(matches > 0);
}
```

重要的是不要忘记转义反斜杠，这在Java语法中至关重要。

## 10. 边界匹配器

Java regex API 也支持边界匹配。如果我们关心匹配应该出现在输入文本中的确切位置，那么这就是我们正在寻找的。对于前面的示例，我们只关心是否找到匹配项。

为了仅在文本开头所需的正则表达式为真时进行匹配，我们使用插入符^。

这个测试将通过，因为文本dog可以在开头找到：

```java
@Test
public void givenText_whenMatchesAtBeginning_thenCorrect() {
    int matches = runTest("^dog", "dogs are friendly");
 
    assertTrue(matches > 0);
}
```

以下测试将失败：

```java
@Test
public void givenTextAndWrongInput_whenMatchFailsAtBeginning_
  thenCorrect() {
    int matches = runTest("^dog", "are dogs are friendly?");
 
    assertFalse(matches > 0);
}
```

为了仅在文本末尾所需的正则表达式为真时进行匹配，我们使用美元字符$。我们将在以下情况下找到匹配项：

```java
@Test
public void givenText_whenMatchesAtEnd_thenCorrect() {
    int matches = runTest("dog$", "Man's best friend is a dog");
 
    assertTrue(matches > 0);
}
```

我们不会在这里找到匹配项：

```java
@Test
public void givenTextAndWrongInput_whenMatchFailsAtEnd_thenCorrect() {
    int matches = runTest("dog$", "is a dog man's best friend?");
 
    assertFalse(matches > 0);
}
```

如果我们只希望在单词边界找到所需文本时进行匹配，我们在正则表达式的开头和结尾使用b 正则表达式：

空格是单词边界：

```java
@Test
public void givenText_whenMatchesAtWordBoundary_thenCorrect() {
    int matches = runTest("bdogb", "a dog is friendly");
 
    assertTrue(matches > 0);
}
```

行首的空字符串也是一个词边界：

```java
@Test
public void givenText_whenMatchesAtWordBoundary_thenCorrect2() {
    int matches = runTest("bdogb", "dog is man's best friend");
 
    assertTrue(matches > 0);
}
```

这些测试通过了，因为String的开头以及一个文本和另一个文本之间的空格标记了单词边界。但是，以下测试显示相反的情况：

```java
@Test
public void givenWrongText_whenMatchFailsAtWordBoundary_thenCorrect() {
    int matches = runTest("bdogb", "snoop dogg is a rapper");
 
    assertFalse(matches > 0);
}
```

连续出现的两个单词字符不会标记单词边界，但我们可以通过更改正则表达式的末尾以查找非单词边界来使其通过：

```java
@Test
public void givenText_whenMatchesAtWordAndNonBoundary_thenCorrect() {
    int matches = runTest("bdogB", "snoop dogg is a rapper");
    assertTrue(matches > 0);
}
```

## 11. 模式类方法

以前，我们仅以基本方式创建Pattern对象。然而，这个类有另一个编译方法的变体，它接受一组标志和正则表达式参数，这会影响我们匹配模式的方式。

这些标志只是抽象的整数值。让我们在测试类中重载runTest方法，以便它可以将标志作为第三个参数：

```java
public static int runTest(String regex, String text, int flags) {
    pattern = Pattern.compile(regex, flags);
    matcher = pattern.matcher(text);
    int matches = 0;
    while (matcher.find()){
        matches++;
    }
    return matches;
}
```

在本节中，我们将了解不同的支持标志以及如何使用它们。

模式.CANON_EQ

此标志启用规范等效。指定后，当且仅当它们的完整规范分解匹配时，两个字符才会被视为匹配。

考虑带有重音符号的 Unicode 字符é。它的复合代码点是u00E9。但是，Unicode 也为其组成字符e、u0065和重音符u0301提供了单独的代码点。在这种情况下，复合字符u00E9与两个字符序列u无法区分0065在0301.

默认情况下，匹配不考虑规范等价：

```java
@Test
public void givenRegexWithoutCanonEq_whenMatchFailsOnEquivalentUnicode_thenCorrect() {
    int matches = runTest("u00E9", "u0065u0301");
 
    assertFalse(matches > 0);
}
```

但是如果我们添加标志，那么测试将通过：

```java
@Test
public void givenRegexWithCanonEq_whenMatchesOnEquivalentUnicode_thenCorrect() {
    int matches = runTest("u00E9", "u0065u0301", Pattern.CANON_EQ);
 
    assertTrue(matches > 0);
}
```

模式.CASE_INSENSITIVE

此标志启用匹配而不考虑大小写。默认情况下，匹配会考虑大小写：

```java
@Test
public void givenRegexWithDefaultMatcher_whenMatchFailsOnDifferentCases_thenCorrect() {
    int matches = runTest("dog", "This is a Dog");
 
    assertFalse(matches > 0);
}
```

所以使用这个标志，我们可以改变默认行为：

```java
@Test
public void givenRegexWithCaseInsensitiveMatcher
  _whenMatchesOnDifferentCases_thenCorrect() {
    int matches = runTest(
      "dog", "This is a Dog", Pattern.CASE_INSENSITIVE);
 
    assertTrue(matches > 0);
}
```

我们还可以使用等效的嵌入标志表达式来获得相同的结果：

```java
@Test
public void givenRegexWithEmbeddedCaseInsensitiveMatcher
  _whenMatchesOnDifferentCases_thenCorrect() {
    int matches = runTest("(?i)dog", "This is a Dog");
 
    assertTrue(matches > 0);
}
```

模式.COMMENTS

Java API 允许我们在正则表达式中使用# 来包含注解。这有助于记录复杂的正则表达式，而这些正则表达式对其他程序员来说可能不是很明显。

comments 标志使匹配器忽略正则表达式中的任何空格或注解，只考虑模式。在默认匹配模式下，以下测试将失败：

```java
@Test
public void givenRegexWithComments_whenMatchFailsWithoutFlag_thenCorrect() {
    int matches = runTest(
      "dog$  #check for word dog at end of text", "This is a dog");
 
    assertFalse(matches > 0);
}
```

这是因为匹配器将在输入文本中查找整个正则表达式，包括空格和 # 字符。但是当我们使用该标志时，它会忽略多余的空格，并且所有以 # 开头的文本都将被视为每行要忽略的注解：

```java
@Test
public void givenRegexWithComments_whenMatchesWithFlag_thenCorrect() {
    int matches = runTest(
      "dog$  #check end of text","This is a dog", Pattern.COMMENTS);
 
    assertTrue(matches > 0);
}
```

还有一个替代的嵌入式标志表达式：

```java
@Test
public void givenRegexWithComments_whenMatchesWithEmbeddedFlag_thenCorrect() {
    int matches = runTest(
      "(?x)dog$  #check end of text", "This is a dog");
 
    assertTrue(matches > 0);
}
```

图案.DOTALL

默认情况下，当我们使用点“.”时 正则表达式中的表达式，我们匹配输入字符串中的每个字符，直到遇到换行符。

使用此标志，匹配项也将包括行终止符。我们将通过以下示例更好地理解这一点。这些例子会有些不同。由于我们要针对匹配的String进行断言，因此我们将使用matcher的group方法，该方法返回上一个匹配项。

首先，让我们看看默认行为：

```java
@Test
public void givenRegexWithLineTerminator_whenMatchFails_thenCorrect() {
    Pattern pattern = Pattern.compile("(.)");
    Matcher matcher = pattern.matcher(
      "this is a text" + System.getProperty("line.separator") 
        + " continued on another line");
    matcher.find();
 
    assertEquals("this is a text", matcher.group(1));
}
```

正如我们所看到的，只有行结束符之前的输入的第一部分被匹配。

现在在dotall模式下，将匹配整个文本，包括行终止符：

```java
@Test
public void givenRegexWithLineTerminator_whenMatchesWithDotall_thenCorrect() {
    Pattern pattern = Pattern.compile("(.)", Pattern.DOTALL);
    Matcher matcher = pattern.matcher(
      "this is a text" + System.getProperty("line.separator") 
        + " continued on another line");
    matcher.find();
    assertEquals(
      "this is a text" + System.getProperty("line.separator") 
        + " continued on another line", matcher.group(1));
}
```

我们还可以使用嵌入式标志表达式来启用dotall模式：

```java
@Test
public void givenRegexWithLineTerminator_whenMatchesWithEmbeddedDotall
  _thenCorrect() {
    
    Pattern pattern = Pattern.compile("(?s)(.)");
    Matcher matcher = pattern.matcher(
      "this is a text" + System.getProperty("line.separator") 
        + " continued on another line");
    matcher.find();
 
    assertEquals(
      "this is a text" + System.getProperty("line.separator") 
        + " continued on another line", matcher.group(1));
}
```

模式.LITERAL

在这种模式下，匹配器不会为任何元字符、转义字符或正则表达式语法赋予任何特殊含义。如果没有此标志，匹配器将针对任何输入String匹配以下正则表达式：

```java
@Test
public void givenRegex_whenMatchesWithoutLiteralFlag_thenCorrect() {
    int matches = runTest("(.)", "text");
 
    assertTrue(matches > 0);
}
```

这是我们在所有示例中看到的默认行为。然而，使用这个标志，我们不会找到匹配项，因为匹配器将寻找(.)而不是解释它：

```java
@Test
public void givenRegex_whenMatchFailsWithLiteralFlag_thenCorrect() {
    int matches = runTest("(.)", "text", Pattern.LITERAL);
 
    assertFalse(matches > 0);
}
```

现在，如果我们添加所需的字符串，测试将通过：

```java
@Test
public void givenRegex_whenMatchesWithLiteralFlag_thenCorrect() {
    int matches = runTest("(.)", "text(.)", Pattern.LITERAL);
 
    assertTrue(matches > 0);
}
```

没有用于启用文字解析的嵌入式标志字符。

模式.MULTILINE

默认情况下，^和$元字符分别在整个输入String的开头和结尾绝对匹配。匹配器忽略任何行终止符：

```java
@Test
public void givenRegex_whenMatchFailsWithoutMultilineFlag_thenCorrect() {
    int matches = runTest(
      "dog$", "This is a dog" + System.getProperty("line.separator") 
      + "this is a fox");
 
    assertFalse(matches > 0);
}
```

此匹配将失败，因为匹配器在整个字符串的末尾搜索狗，但狗出现在字符串第一行的末尾。

然而，有了标志，相同的测试将通过，因为匹配器现在考虑了行终止符。所以 String dog就在该行终止之前找到了，这意味着成功：

```java
@Test
public void givenRegex_whenMatchesWithMultilineFlag_thenCorrect() {
    int matches = runTest(
      "dog$", "This is a dog" + System.getProperty("line.separator") 
      + "this is a fox", Pattern.MULTILINE);
 
    assertTrue(matches > 0);
}
```

这是嵌入式标志版本：

```java
@Test
public void givenRegex_whenMatchesWithEmbeddedMultilineFlag_
  thenCorrect() {
    int matches = runTest(
      "(?m)dog$", "This is a dog" + System.getProperty("line.separator") 
      + "this is a fox");
 
    assertTrue(matches > 0);
}
```

## 12.匹配器类方法

在本节中，我们将了解Matcher类的有用方法。为了清楚起见，我们将根据功能对它们进行分组。

### 12.1. 索引方法

索引方法提供了有用的索引值，可以准确地向我们展示在输入String中找到匹配项的位置。在下面的测试中，我们将确认输入String中dog匹配的开始和结束索引：

```java
@Test
public void givenMatch_whenGetsIndices_thenCorrect() {
    Pattern pattern = Pattern.compile("dog");
    Matcher matcher = pattern.matcher("This dog is mine");
    matcher.find();
 
    assertEquals(5, matcher.start());
    assertEquals(8, matcher.end());
}
```

### 12.2. 学习方法

研究方法遍历输入字符串并返回一个布尔值，指示是否找到模式。常用的是matches和lookingAt方法。

matches和lookingAt方法都尝试将输入序列与模式相匹配。不同之处在于matches需要匹配整个输入序列，而lookingAt则不需要。

两种方法都从输入String的开头开始：

```java
@Test
public void whenStudyMethodsWork_thenCorrect() {
    Pattern pattern = Pattern.compile("dog");
    Matcher matcher = pattern.matcher("dogs are friendly");
 
    assertTrue(matcher.lookingAt());
    assertFalse(matcher.matches());
}
```

在这种情况下，matches 方法将返回 true：

```java
@Test
public void whenMatchesStudyMethodWorks_thenCorrect() {
    Pattern pattern = Pattern.compile("dog");
    Matcher matcher = pattern.matcher("dog");
 
    assertTrue(matcher.matches());
}
```

### 12.3. 更换方法

替换方法可用于替换输入字符串中的文本。常见的是replaceFirst和replaceAll。

replaceFirst和replaceAll方法替换与给定正则表达式匹配的文本。顾名思义，replaceFirst替换第一个出现的地方，replaceAll替换所有出现的地方：

```java
@Test
public void whenReplaceFirstWorks_thenCorrect() {
    Pattern pattern = Pattern.compile("dog");
    Matcher matcher = pattern.matcher(
      "dogs are domestic animals, dogs are friendly");
    String newStr = matcher.replaceFirst("cat");
 
    assertEquals(
      "cats are domestic animals, dogs are friendly", newStr);
}
```

替换所有出现的地方：

```java
@Test
public void whenReplaceAllWorks_thenCorrect() {
    Pattern pattern = Pattern.compile("dog");
    Matcher matcher = pattern.matcher(
      "dogs are domestic animals, dogs are friendly");
    String newStr = matcher.replaceAll("cat");
 
    assertEquals("cats are domestic animals, cats are friendly", newStr);
}
```

replaceAll方法允许我们用相同的替换替换所有匹配项。如果我们想根据具体情况替换匹配项，我们需要一种[令牌替换技术](https://www.baeldung.com/java-regex-token-replacement)。

## 13.总结

在本文中，我们学习了如何在Java中使用正则表达式。我们还探索了java.util.regex包最重要的特性。
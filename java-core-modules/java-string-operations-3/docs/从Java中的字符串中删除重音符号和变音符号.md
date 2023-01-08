## 1. 概述

许多字母表包含重音符号和变音符号。为了可靠地搜索或索引数据，我们可能希望将带有变音符号的字符串转换为仅包含 ASCII 字符的字符串。Unicode 定义了一个文本规范化过程来帮助做到这一点。

在本教程中，我们将了解什么是 Unicode 文本规范化，我们如何使用它来删除变音符号，以及需要注意的陷阱。然后，我们将看到一些使用JavaNormalizer类和 Apache Commons StringUtils 的示例。

## 2. 问题一览

假设我们正在处理包含我们要删除的变音符号范围的文本：

```plaintext
āăąēîïĩíĝġńñšŝśûůŷ
```

阅读本文后，我们将知道如何摆脱变音符号并最终得到：

```plaintext
aaaeiiiiggnnsssuuy
```

## 3. Unicode 基础

在直接进入代码之前，让我们学习一些 Unicode 基础知识。

为了用变音符号或重音符号表示字符，Unicode 可以使用不同的代码点序列。原因是与旧字符集的历史兼容性。

Unicode 规范化是使用标准定义的等价形式分解字符。

### 3.1. Unicode 等价形式

为了比较代码点序列，Unicode 定义了两个术语：规范等效和兼容性。

规范等效代码点在显示时具有相同的外观和含义。例如，字母“ś”(带尖角的拉丁字母“s”)可以用一个代码点+U015B 或两个代码点+U0073(拉丁字母“s”)和+U0301(尖角符号)表示。

另一方面，兼容序列在某些上下文中可能具有不同的外观但具有相同的含义。例如，代码点 +U013F(拉丁文连字“Ŀ”)与序列 +U004C(拉丁文字母“L”)和 +U00B7(符号“·”)兼容。此外，有些字体可以在 L 内显示中间点，有些则紧随其后。

规范等价序列是兼容的，但反之并不总是正确的。

### 3.2. 字符分解

字符分解用基本字母的代码点替换复合字符，然后组合字符(根据等价形式)。例如，此程序会将字母“ā”分解为字符“a”和“-”。

### 3.3. 匹配变音符号和重音符号

一旦我们将基本字符与变音标记分开，我们就必须创建一个表达式来匹配不需要的字符。我们可以使用字符块或类别。

最流行的 Unicode 代码块是Combining Diacritical Marks。它不是很大，只包含 112 个最常见的组合字符。另一方面，我们也可以使用 Unicode 类别Mark。它由组合标记的代码点组成，并进一步分为三个子类别：

-   Nonspacing_Mark：此类别包括 1,839 个代码点
-   Enclosing_Mark：包含 13 个代码点
-   Spacing_Combining_Mark：包含 443 个点

Unicode 字符块和类别之间的主要区别是字符块包含连续的字符范围。另一方面，一个类别可以有许多字符块。例如，正是Combining Diacritical Marks的情况：属于该块的所有代码点也包含在 Nonspacing_Mark 类别中。

## 4.算法

现在我们了解了基本的 Unicode 术语，我们可以规划算法以从String中删除变音符号。

首先，我们将使用Normalizer类将基本字符与重音符号和变音符号分开。此外，我们将执行以Java枚举NFKD表示的兼容性分解。此外，我们使用兼容性分解，因为它比规范方法分解更多的连字(例如，连字“fi”)。

其次，我们将使用p{M}正则表达式删除所有匹配 Unicode Mark类别的字符。我们选择这个类别是因为它提供了最广泛的标记。

## 5. 使用核心Java

让我们从一些使用核心Java的示例开始。

### 5.1. 检查字符串是否规范化

在执行规范化之前，我们可能需要检查String是否尚未规范化：

```java
assertFalse(Normalizer.isNormalized("āăąēîïĩíĝġńñšŝśûůŷ", Normalizer.Form.NFKD));
```

### 5.2. 字符串分解

如果我们的String没有规范化，我们继续下一步。为了将 ASCII 字符与变音符号分开，我们将使用兼容性分解执行 Unicode 文本规范化：

```java
private static String normalize(String input) {
    return input == null ? null : Normalizer.normalize(input, Normalizer.Form.NFKD);
}
```

在此步骤之后，字母“â”和“ä”都将缩减为“a”，后跟相应的变音符号。

### 5.3. 删除表示变音符号和重音符号的代码点

一旦我们分解了String，我们就想删除不需要的代码点。因此，我们将使用[Unicode 正则表达式](https://www.regular-expressions.info/unicode.html) p{M}：

```java
static String removeAccents(String input) {
    return normalize(input).replaceAll("p{M}", "");
}
```

### 5.4. 测试

让我们看看我们的分解在实践中是如何工作的。首先，让我们选择具有 Unicode 定义的规范化形式的字符，并期望删除所有变音符号：

```java
@Test
void givenStringWithDecomposableUnicodeCharacters_whenRemoveAccents_thenReturnASCIIString() {
    assertEquals("aaaeiiiiggnnsssuuy", StringNormalizer.removeAccents("āăąēîïĩíĝġńñšŝśûůŷ"));
}
```

其次，我们挑几个没有分解映射的字符：

```java
@Test
void givenStringWithNondecomposableUnicodeCharacters_whenRemoveAccents_thenReturnOriginalString() {
    assertEquals("łđħœ", StringNormalizer.removeAccents("łđħœ"));
}
```

正如预期的那样，我们的方法无法分解它们。

此外，我们可以创建一个测试来验证分解字符的十六进制代码：

```java
@Test
void givenStringWithDecomposableUnicodeCharacters_whenUnicodeValueOfNormalizedString_thenReturnUnicodeValue() {
    assertEquals("u0066 u0069", StringNormalizer.unicodeValueOfNormalizedString("ﬁ"));
    assertEquals("u0061 u0304", StringNormalizer.unicodeValueOfNormalizedString("ā"));
    assertEquals("u0069 u0308", StringNormalizer.unicodeValueOfNormalizedString("ï"));
    assertEquals("u006e u0301", StringNormalizer.unicodeValueOfNormalizedString("ń"));
}
```

### 5.5. 使用Collator比较包括重音的字符串

java.text包 包含另一个有趣的类 ——Collator。它使我们能够执行区域敏感的字符串比较。一个重要的配置属性是Collator 的强度。此属性定义在比较过程中被视为显着的最小差异水平。

Java 为Collator提供了四个强度值：

-   PRIMARY : 比较省略大小写和重音
-   SECONDARY：比较省略大小写但包括重音和变音符号
-   TERTIARY：比较包括大小写和重音
-   相同：所有差异都很重要

让我们检查一些示例，首先是主要强度：

```java
Collator collator = Collator.getInstance();
collator.setDecomposition(2);
collator.setStrength(0);
assertEquals(0, collator.compare("a", "a"));
assertEquals(0, collator.compare("ä", "a"));
assertEquals(0, collator.compare("A", "a"));
assertEquals(1, collator.compare("b", "a"));
```

次要强度打开重音敏感度：

```java
collator.setStrength(1);
assertEquals(1, collator.compare("ä", "a"));
assertEquals(1, collator.compare("b", "a"));
assertEquals(0, collator.compare("A", "a"));
assertEquals(0, collator.compare("a", "a"));
```

三级强度包括案例：

```java
collator.setStrength(2);
assertEquals(1, collator.compare("A", "a"));
assertEquals(1, collator.compare("ä", "a"));
assertEquals(1, collator.compare("b", "a"));
assertEquals(0, collator.compare("a", "a"));
assertEquals(0, collator.compare(valueOf(toChars(0x0001)), valueOf(toChars(0x0002))));
```

相同的力量使所有差异变得重要。倒数第二个例子很有趣，因为我们可以检测到 Unicode 控制代码点 +U001(“标题开始”代码)和 +U002(“文本开始”代码)之间的区别：

```java
collator.setStrength(3);
assertEquals(1, collator.compare("A", "a"));
assertEquals(1, collator.compare("ä", "a"));
assertEquals(1, collator.compare("b", "a"));
assertEquals(-1, collator.compare(valueOf(toChars(0x0001)), valueOf(toChars(0x0002))));
assertEquals(0, collator.compare("a", "a")));
```

最后一个值得一提的例子表明，如果字符没有定义的分解规则，它不会被认为等于另一个具有相同基字母的字符。这是因为Collator无法执行 Unicode 分解：

```java
collator.setStrength(0);
assertEquals(1, collator.compare("ł", "l"));
assertEquals(1, collator.compare("ø", "o"));

```

## 6. 使用 Apache Commons StringUtils

现在我们已经了解了如何使用核心Java来删除重音符号，我们将检查[Apache Commons Text](https://www.baeldung.com/java-apache-commons-text)提供的内容。我们很快就会了解到，它更易于使用，但我们对分解过程的控制较少。在引擎盖下，它使用带有NFD分解形式和 p{InCombiningDiacriticalMarks} 正则表达式的Normalizer.normalize() 方法 ：

```java
static String removeAccentsWithApacheCommons(String input) {
    return StringUtils.stripAccents(input);
}
```

### 6.1. 测试

让我们在实践中看看这个方法——首先，只使用可分解的 Unicode 字符：

```java
@Test
void givenStringWithDecomposableUnicodeCharacters_whenRemoveAccentsWithApacheCommons_thenReturnASCIIString() {
    assertEquals("aaaeiiiiggnnsssuuy", StringNormalizer.removeAccentsWithApacheCommons("āăąēîïĩíĝġńñšŝśûůŷ"));
}
```

正如预期的那样，我们摆脱了所有口音。

让我们尝试一个包含连字和带笔划字母的字符串：

```java
@Test 
void givenStringWithNondecomposableUnicodeCharacters_whenRemoveAccentsWithApacheCommons_thenReturnModifiedString() {
    assertEquals("lđħœ", StringNormalizer.removeAccentsWithApacheCommons("łđħœ"));
}
```

正如我们所见，StringUtils.stripAccents ()方法手动定义了拉丁文 ł 和 Ł 字符的翻译规则。但是，不幸的是，它没有规范化其他连字。

## 7. Java中字符分解的局限性

综上所述，我们看到一些字符没有定义分解规则。更具体地说，Unicode 没有为连字和带笔画的字符定义分解规则。因此，Java 也无法规范化它们。如果我们想摆脱这些字符，我们必须手动定义转录映射。

最后，值得考虑的是我们是否需要摆脱口音和变音符号。对于某些语言，去掉变音符号的字母意义不大。在这种情况下，更好的想法是使用Collator 类并比较两个 Strings，包括区域设置信息。

## 八、总结

在本文中，我们研究了使用核心Java和流行的Java实用程序库 Apache Commons 来删除重音符号和变音符号。我们还看到了一些示例并学习了如何比较包含重音的文本，以及在处理包含重音的文本时需要注意的一些事项。
## 1. 概述

当我们想在Java中构建一个字符串时，我们通常会选择方便的[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)来完成这项工作。

假设我们有一个包含一些字符串段的StringBuilder序列，我们想从中删除最后一个字符。在这个快速教程中，我们将探索三种方法来做到这一点。

## 2. 使用StringBuilder的deleteCharAt()方法

StringBuilder类具有deleteCharAt [()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html#deleteCharAt(int))方法。它允许我们删除所需位置的字符。

deleteCharAt ()方法只有一个参数：我们要删除的字符索引。

因此，如果我们将最后一个字符的索引传递给该方法，我们就可以删除该字符。为简单起见，我们将使用单元测试断言来验证它是否按预期工作。

那么接下来，让我们创建一个测试来检查它是否有效：

```java
StringBuilder sb = new StringBuilder("Using the sb.deleteCharAt() method!");
sb.deleteCharAt(sb.length() - 1);
assertEquals("Using the sb.deleteCharAt() method", sb.toString());

```

如上面的测试所示，我们将最后一个字符的索引 ( sb.length() -1 ) 传递给deleteCharAt()方法，并期望删除结尾的感叹号 ( ! )。

如果我们运行测试，它就会通过。所以，deleteCharAt()解决了这个问题。

## 3. 使用StringBuilder的replace()方法

StringBuilder的[replace()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html#replace(int,int,java.lang.String))方法允许我们用给定的字符串替换序列的子字符串中的字符。该方法接受三个参数：

-   起始索引——起始索引，包括在内
-   end index – 结束索引，独占
-   replacement – 用于替换的字符串

假设序列中最后一个字符的索引是lastIdx。如果我们想删除最后一个字符，我们可以传递lastIdx作为开始索引，lastIdx+1作为结束索引，以及一个空字符串“”作为replace()的替换：

```java
StringBuilder sb = new StringBuilder("Using the sb.replace() method!");
int last = sb.length() - 1;
sb.replace(last, last + 1, "");
assertEquals("Using the sb.replace() method", sb.toString());

```

现在，如果我们运行上面的测试，它就会通过。所以，可以使用replace()方法来解决问题。

## 4. 使用StringBuilder的substring()方法

我们可以使用StringBuilder的[substring()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html#substring(int,int))方法从字符串的给定开始和结束索引中获取子序列。该方法需要两个参数，开始索引(包括)和结束索引(不包括)。

值得一提的是substring()方法返回一个新的String对象。换句话说，substring ()方法不修改StringBuilder对象。

我们可以将 0 作为起始索引，将最后一个字符的索引作为 结束索引传递给 substring()方法，以获取最后一个字符被截断的字符串：

```java
StringBuilder sb = new StringBuilder("Using the sb.substring() method!");
assertEquals("Using the sb.substring() method", sb.substring(0, sb.length() - 1));
//the stringBuilder object is not changed
assertEquals("Using the sb.substring() method!", sb.toString());

```

如果我们执行它，测试就会通过。

正如我们在测试中看到的那样，即使substring()返回的String没有最后一个字符 ( ! )，原始的StringBuilder也没有改变。

## 5.总结

在这篇简短的文章中，我们学习了如何从StringBuilder序列中删除最后一个字符。
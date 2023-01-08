## 1. 概述

在本快速教程中，我们将了解如何在 Java中反转字符串。

我们将开始使用纯Java解决方案进行此处理。接下来，我们将看看 Apache Commons 等第三方库提供的选项。

此外，我们将演示如何颠倒句子中单词的顺序。

## 2. 传统的 for循环

我们知道字符串[在Java中是不可变的](https://www.baeldung.com/java-immutable-object)。不可变对象是在完全创建后内部状态保持不变的对象。

因此，我们不能通过修改字符串来反转它。为此，我们需要创建另一个String 。

首先，让我们看一个使用for循环的基本示例。我们将遍历从最后一个元素到第一个元素的String输入，并将每个字符连接成一个新的String：

```java
public String reverse(String input) {

    if (input == null) {
        return input;
    }

    String output = "";

    for (int i = input.length() - 1; i >= 0; i--) {
        output = output + input.charAt(i);
    }

    return output;
}
```

正如我们所看到的，我们需要小心处理极端情况并分别对待它们。

为了更好地理解示例，我们可以构建一个单元测试：

```java
@Test
public void whenReverseIsCalled_ThenCorrectStringIsReturned() {
    String reversed = ReverseStringExamples.reverse("cat");
    String reversedNull = ReverseStringExamples.reverse(null);
    String reversedEmpty = ReverseStringExamples.reverse(StringUtils.EMPTY);

    assertEquals("tac", reversed);
    assertEquals(null, reversedNull);
    assertEquals(StringUtils.EMPTY, reversedEmpty);
}
```

## 3. 一个字符串生成器

Java 还提供了一些机制，例如StringBuilder和StringBuffer，它们可以创建[可变的字符序列](https://www.baeldung.com/java-string-builder-string-buffer)。这些对象有一个reverse()方法可以帮助我们达到预期的结果。

在这里，我们需要从String输入创建一个StringBuilder，然后调用reverse()方法：

```java
public String reverseUsingStringBuilder(String input) {
    if (input == null) {
        return null;
    }

    StringBuilder output = new StringBuilder(input).reverse();
    return output.toString();
}
```

## 4.阿帕奇公地

[Apache Commons](https://www.baeldung.com/java-commons-lang-3)是一个流行的Java库，具有许多实用程序类，包括字符串操作。

像往常一样，要开始使用 Apache Commons，我们首先需要添加[Maven 依赖](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3&core=gav)项：

```html
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

StringUtils类是我们在这里需要的，因为它提供了类似于StringBuilder的reverse()方法。

使用这个库的一个优点是它的实用方法执行null安全操作。因此，我们不必单独处理边缘情况。

让我们创建一个方法来实现我们的目的并使用StringUtils类：

```java
public String reverseUsingApacheCommons(String input) {
    return StringUtils.reverse(input);
}
```

现在，看看这三种方法，我们可以肯定地说第三种方法是反转String的最简单且最不容易出错的方法。

## 5.颠倒句子中的单词顺序

现在，假设我们有一个句子，单词之间用空格分隔，没有标点符号。我们需要颠倒这句话中单词的顺序。

我们可以分两步解决这个问题：用空格分隔符[分割句子](https://www.baeldung.com/java-split-string)，然后以相反的顺序连接单词。

首先，我们将展示一种经典方法。我们将使用 S tring.split()方法来完成问题的第一部分。接下来，我们将向后遍历生成的数组并使用StringBuilder连接单词。当然，我们还需要在这些词之间加一个空格：

```java
public String reverseTheOrderOfWords(String sentence) {
    if (sentence == null) {
        return null;
    }

    StringBuilder output = new StringBuilder();
    String[] words = sentence.split(" ");

    for (int i = words.length - 1; i >= 0; i--) {
        output.append(words[i]);
        output.append(" ");
    }

    return output.toString().trim();
}
```

其次，我们将考虑使用 Apache Commons 库。再一次，它帮助我们实现了更具可读性和更不易出错的代码。我们只需要以输入的句子和分隔符作为参数调用StringUtils.reverseDelimited()方法：

```java
public String reverseTheOrderOfWordsUsingApacheCommons(String sentence) {
    return StringUtils.reverseDelimited(sentence, ' ');
}
```

## 六，总结

在本教程中，我们首先研究了在 Java中反转String的不同方法。我们使用核心Java以及流行的第三方库(如 Apache Commons)浏览了一些示例。

接下来，我们已经了解了如何分两步颠倒句子中单词的顺序。这些步骤也有助于实现句子的其他排列。
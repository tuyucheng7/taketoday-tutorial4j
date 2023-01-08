## 1. 概述

根据[维基百科](https://en.wikipedia.org/wiki/Anagram)，字谜是通过重新排列不同单词或短语的字母而形成的单词或短语。

我们可以在字符串处理中概括这一点，说一个字符串的变位词是另一个字符串，其中每个字符的数量完全相同，顺序任意。

在本教程中，我们将研究检测整个字符串的变位词，其中每个字符的数量必须相等，包括非字母字符，例如空格和数字。例如，“！低盐！” 和“猫头鹰拉特！！” 将被视为字谜，因为它们包含完全相同的字符。

## 2.解决方案

让我们比较一些可以判断两个字符串是否为变位词的解决方案。每个解决方案都会在开始时检查两个字符串是否具有相同数量的字符。这是一种提前退出的快速方法，因为不同长度的输入不能是 anagrams。

对于每个可能的解决方案，让我们看看作为开发人员的实施复杂性。[我们还将使用大 O 表示法](https://www.baeldung.com/java-algorithm-complexity)查看 CPU 的时间复杂度。

## 3.排序检查

我们可以通过对每个字符串的字符进行排序来重新排列每个字符串的字符，这将产生两个规范化的字符数组。

如果两个字符串是变位词，则它们的规范化形式应该相同。

在Java中，我们可以先将这两个字符串转换成char[]数组。然后我们可以对这两个数组进行排序并检查是否相等：

```java
boolean isAnagramSort(String string1, String string2) {
    if (string1.length() != string2.length()) {
        return false;
    }
    char[] a1 = string1.toCharArray();
    char[] a2 = string2.toCharArray();
    Arrays.sort(a1);
    Arrays.sort(a2);
    return Arrays.equals(a1, a2);
}

```

该解决方案易于理解和实施。但是，该算法的总运行时间为O(n log n) ，因为对 n 个 字符 的数组进行排序需要O(n log n)时间。

为了让算法发挥作用，它必须将两个输入字符串为字符数组，并使用一些额外的内存。

## 4.计数检查

另一种策略是计算输入中每个字符的出现次数。如果这些直方图在输入之间相等，则字符串是变位词。

为了节省一点内存，我们只构建一个直方图。我们将增加第一个字符串中每个字符的计数，并减少第二个字符串中每个字符的计数。如果这两个字符串是变位词，那么结果将是一切都平衡为 0。

直方图需要一个固定大小的计数表，其大小由字符集大小定义。例如，如果我们只使用一个字节来存储每个字符，那么我们可以使用一个大小为 256 的计数数组来统计每个字符的出现次数：

```java
private static int CHARACTER_RANGE= 256;

public boolean isAnagramCounting(String string1, String string2) {
    if (string1.length() != string2.length()) {
        return false;
    }
    int count[] = new int[CHARACTER_RANGE];
    for (int i = 0; i < string1.length(); i++) {
        count[string1.charAt(i)]++;
        count[string2.charAt(i)]--;
    }
    for (int i = 0; i < CHARACTER_RANGE; i++) {
        if (count[i] != 0) {
            return false;
        }
    }
    return true;
}
```

该解决方案的时间复杂度为O(n) ，速度更快。但是，它需要额外的空间用于计数数组。256 个整数，对于 ASCII 来说还算不错。

但是，如果我们需要增加CHARACTER_RANGE以支持多字节字符集(如 UTF-8)，这将变得非常耗费内存。因此，只有当可能的字符数在一个很小的范围内时，它才真正实用。

从开发的角度来看，该解决方案包含更多需要维护的代码并且更少使用Java库函数。

## 5.用MultiSet检查

我们可以使用[MultiSet](https://www.baeldung.com/guava-multiset)简化计数和比较过程。MultiSet是一个支持与重复元素的顺序无关的相等性的集合。例如，多重集 {a, a, b} 和 {a, b, a} 是相等的。

要使用Multiset，我们首先需要将[Guava](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" a%3A"guava")依赖项添加到我们的项目pom.xml文件中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>

```

我们会将每个输入字符串转换为一个MultiSet字符。然后我们将检查它们是否相等：

```java
boolean isAnagramMultiset(String string1, String string2) {
    if (string1.length() != string2.length()) {
        return false;
    }
    Multiset<Character> multiset1 = HashMultiset.create();
    Multiset<Character> multiset2 = HashMultiset.create();
    for (int i = 0; i < string1.length(); i++) {
        multiset1.add(string1.charAt(i));
        multiset2.add(string2.charAt(i));
    }
    return multiset1.equals(multiset2);
}

```

该算法在O(n)时间内解决了问题，而无需声明一个大的计数数组。

它类似于以前的计数解决方案。然而，我们没有使用固定大小的表来计数，而是利用MutlitSet类来模拟一个可变大小的表，每个字符都有一个计数。

该解决方案的代码比我们的计数解决方案更多地使用了高级库功能。

## 6. 基于字母的字谜

到目前为止的例子并不严格遵守字谜的语言定义。这是因为他们将标点符号视为变位词的一部分，并且区分大小写。

让我们调整算法以启用基于字母的字谜。我们只考虑不区分大小写字母的重新排列，而不考虑其他字符，如空格和标点符号。例如，“一个小数点”和“我是一个点”。将是彼此的字谜。

为了解决这个问题，我们可以先对输入的两个字符串进行预处理，过滤掉不需要的字符，将字母转换为小写字母。然后我们可以使用上述解决方案之一(比如MultiSet 解决方案)来检查处理过的字符串上的字谜：

```java
String preprocess(String source) {
    return source.replaceAll("[^a-zA-Z]", "").toLowerCase();
}

boolean isLetterBasedAnagramMultiset(String string1, String string2) {
    return isAnagramMultiset(preprocess(string1), preprocess(string2));
}
```

这种方法可以是解决所有变体问题的通用方法。例如，如果我们还想包含数字，我们只需要调整预处理过滤器即可。

## 七、总结

在本文中，我们研究了三种算法，用于检查给定字符串是否是另一个字符的变位词。对于每个解决方案，我们都讨论了速度、可读性和所需内存大小之间的权衡。

我们还研究了如何调整算法以检查更传统语言意义上的字谜。我们通过将输入预处理为小写字母来实现这一点。
---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-string
copyright: java-string
excerpt: Java String
---

## 1. 概述

在本教程中，我们将学习使用简单的Java程序检查给定字符串是否有效的pangram。pangram是包含至少一次给定字母表的所有字母的任何字符串[。](https://en.wikipedia.org/wiki/Pangram)

## 2. Pangrams

Pangram不仅适用于英语，也适用于任何其他具有固定字符集的语言。

例如，一个众所周知的英文双拼词是“Aquickbrownfoxjumpsoverthelazydog”。同样，这些也有[其他](http://clagnut.com/blog/2380/)语言版本。

## 3. 使用for循环

首先，让我们尝试一个[for循环](https://www.tuyucheng.com/java-loops)。我们将为字母表中的每个字符填充一个带有标记的布尔数组。

当标记数组中的所有值都设置为true时，代码返回true：

```java
public static boolean isPangram(String str) {
    if (str == null) {
        return false;
    }
    Boolean[] alphabetMarker = new Boolean[ALPHABET_COUNT];
    Arrays.fill(alphabetMarker, false);
    int alphabetIndex = 0;
    str = str.toUpperCase();
    for (int i = 0; i < str.length(); i++) {
        if ('A' <= str.charAt(i) && str.charAt(i) <= 'Z') {
            alphabetIndex = str.charAt(i) - 'A';
            alphabetMarker[alphabetIndex] = true;
        }
    }
    for (boolean index : alphabetMarker) {
        if (!index) {
            return false;
        }
    }
    return true;
}
```

让我们测试一下我们的实现：

```java
@Test
public void givenValidString_isPanagram_shouldReturnSuccess() {
    String input = "Two driven jocks help fax my big quiz";
    assertTrue(Pangram.isPangram(input));  
}
```

## 4. 使用Java流

另一种方法涉及使用[Java Stream API](https://www.tuyucheng.com/java-8-streams-introduction)。我们可以从给定的输入文本中创建一个过滤字符流，并使用该流创建一个字母表映射。

如果Map的大小等于字母表的大小，则代码返回成功。对于英语，预期大小为26：

```java
public static boolean isPangramWithStreams(String str) {
    if (str == null) {
        return false;
    }
    String strUpper = str.toUpperCase();

    Stream<Character> filteredCharStream = strUpper.chars()
      .filter(item -> ((item >= 'A' && item <= 'Z')))
      .mapToObj(c -> (char) c);

    Map<Character, Boolean> alphabetMap = 
      filteredCharStream.collect(Collectors.toMap(item -> item, k -> Boolean.TRUE, (p1, p2) -> p1));

    return alphabetMap.size() == ALPHABET_COUNT;
}
```

当然，让我们测试一下：

```java
@Test
public void givenValidString_isPangramWithStreams_shouldReturnSuccess() {
    String input = "The quick brown fox jumps over the lazy dog";
    assertTrue(Pangram.isPangramWithStreams(input));
}
```

## 5. 修改完美的Pangrams

完美的pangram与普通的pangram有点不同。一个完美的pangram由字母表中的每个字母恰好一次组成，而不是pangram至少一次。

当Map大小等于字母表大小并且字母表中每个字符的频率恰好为一个时，代码返回true：

```java
public static boolean isPerfectPangram(String str) {
    if (str == null) {
        return false;
    }
    String strUpper = str.toUpperCase();

    Stream<Character> filteredCharStream = strUpper.chars()
        .filter(item -> ((item >= 'A' && item <= 'Z')))
        .mapToObj(c -> (char) c);
    Map<Character, Long> alphabetFrequencyMap = 
      filteredCharStream.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    return alphabetFrequencyMap.size() == ALPHABET_COUNT && 
      alphabetFrequencyMap.values().stream().allMatch(item -> item == 1);
}
```

让我们测试一下：

```java
@Test
public void givenPerfectPangramString_isPerfectPangram_shouldReturnSuccess() {
    String input = "abcdefghijklmNoPqrStuVwxyz";
    assertTrue(Pangram.isPerfectPangram(input));
}
```

一个完美的pangram应该让每个字符恰好出现一次。所以，我们之前的pangram应该会失败：

```java
String input = "Two driven jocks help fax my big quiz";
assertFalse(Pangram.isPerfectPangram(input));
```

在上面的代码中，给定的字符串输入有几个重复项，比如它有两个o。因此输出为false。

## 5. 总结

在本文中，我们介绍了各种解决方法来确定给定字符串是否为有效的pangram。

我们还讨论了另一种类型的pangram，称为perfectpangram以及如何以编程方式识别它。
与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/java-core-modules/java-string-algorithms-1)上获得。

## 1. 概述

在本文中，我们将了解如何使用 Java检查给定的字符串是否为回文。

回文是一个单词、短语、数字或其他字符序列，它们向后读与向前读相同，例如“madam”或“racecar”。

## 2.解决方案

在接下来的部分中，我们将研究检查给定字符串是否为回文的各种方法。

### 2.1. 一个简单的方法

我们可以同时开始向前和向后迭代给定的字符串，一次一个字符。如果匹配，则循环继续；否则，循环退出：

```java
public boolean isPalindrome(String text) {
    String clean = text.replaceAll("s+", "").toLowerCase();
    int length = clean.length();
    int forward = 0;
    int backward = length - 1;
    while (backward > forward) {
        char forwardChar = clean.charAt(forward++);
        char backwardChar = clean.charAt(backward--);
        if (forwardChar != backwardChar)
            return false;
    }
    return true;
}
```

### 2.2. 反转字符串

有几个不同的实现适合这个用例：我们可以在检查回文时使用StringBuilder和StringBuffer类的 API 方法，或者我们可以在没有这些类的情况下反转String 。

我们先看看没有 helper API 的代码实现：

```java
public boolean isPalindromeReverseTheString(String text) {
    StringBuilder reverse = new StringBuilder();
    String clean = text.replaceAll("s+", "").toLowerCase();
    char[] plain = clean.toCharArray();
    for (int i = plain.length - 1; i >= 0; i--) {
        reverse.append(plain[i]);
    }
    return (reverse.toString()).equals(clean);
}
```

在上面的代码片段中，我们简单地从最后一个字符开始迭代给定的字符串，并将每个字符附加到下一个字符，一直到第一个字符，从而反转给定的字符串。

最后，我们测试给定字符串和反转字符串之间的相等性。

使用 API 方法可以实现相同的行为。

让我们看一个快速演示：

```java
public boolean isPalindromeUsingStringBuilder(String text) {
    String clean = text.replaceAll("s+", "").toLowerCase();
    StringBuilder plain = new StringBuilder(clean);
    StringBuilder reverse = plain.reverse();
    return (reverse.toString()).equals(clean);
}

public boolean isPalindromeUsingStringBuffer(String text) {
    String clean = text.replaceAll("s+", "").toLowerCase();
    StringBuffer plain = new StringBuffer(clean);
    StringBuffer reverse = plain.reverse();
    return (reverse.toString()).equals(clean);
}
```

在代码片段中，我们从StringBuilder和StringBuffer API调用reverse()方法来反转给定的String并测试是否相等。

### 2.3. 使用流API

我们也可以使用IntStream来提供解决方案：

```java
public boolean isPalindromeUsingIntStream(String text) {
    String temp  = text.replaceAll("s+", "").toLowerCase();
    return IntStream.range(0, temp.length() / 2)
      .noneMatch(i -> temp.charAt(i) != temp.charAt(temp.length() - i - 1));
}
```

在上面的代码片段中，我们验证了字符串两端的字符对都不满足谓词条件。

### 2.4. 使用递归

递归是解决此类问题的一种非常流行的方法。在演示的示例中，我们递归地迭代给定的String并测试它是否是回文：

```java
public boolean isPalindromeRecursive(String text){
    String clean = text.replaceAll("s+", "").toLowerCase();
    return recursivePalindrome(clean,0,clean.length()-1);
}

private boolean recursivePalindrome(String text, int forward, int backward) {
    if (forward == backward) {
        return true;
    }
    if ((text.charAt(forward)) != (text.charAt(backward))) {
        return false;
    }
    if (forward < backward + 1) {
        return recursivePalindrome(text, forward + 1, backward - 1);
    }

    return true;
}
```

## 3.总结

在本快速教程中，我们了解了如何确定给定字符串是否为回文。
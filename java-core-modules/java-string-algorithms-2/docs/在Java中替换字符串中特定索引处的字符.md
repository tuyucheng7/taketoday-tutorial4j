## 1. 概述

在本快速教程中，我们将演示如何在 Java中替换String中特定索引处的字符。

我们将展示四种简单方法的实现，这些方法采用原始字符串、一个字符和我们需要替换它的索引。

## 2. 使用字符数组

让我们从一个简单的方法开始，使用一个 字符数组。

这里的想法是将String转换为char[] ，然后在给定的索引处分配新的char。最后，我们从该数组构造所需的字符串。

```java
public String replaceCharUsingCharArray(String str, char ch, int index) {
    char[] chars = str.toCharArray();
    chars[index] = ch;
    return String.valueOf(chars);
}
```

这是一种低级设计方法，为我们提供了很大的灵活性。

## 3.使用 substring方法

一种更高级别的方法是使用String类的substring()方法。

它将通过将索引之前的原始字符串的子字符串与索引之后的原始字符串的新字符和子字符串连接起来来创建一个新的字符串：

```java
public String replaceChar(String str, char ch, int index) {
    return str.substring(0, index) + ch + str.substring(index+1);
}

```

## 4.使用字符串生成器

我们可以通过使用[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)获得相同的效果。我们可以使用setCharAt()方法替换特定索引处的字符 ：

```java
public String replaceChar(String str, char ch, int index) {
    StringBuilder myString = new StringBuilder(str);
    myString.setCharAt(index, ch);
    return myString.toString();
}
```

## 5.总结

 在本文中，我们重点介绍了使用 Java替换String中特定索引处字符的几种方法。

字符串实例是不可变的，因此我们需要创建一个新字符串或使用 StringBuilder 来为我们提供一些可变性。
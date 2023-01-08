## 1. 概述

[String](https://www.baeldung.com/java-string)是普通类型， char是Java[中的原始类型。](https://www.baeldung.com/java-primitives)

在本教程中，我们将探讨如何在Java中将String对象转换为char。

## 二、问题介绍

我们知道一个字符只能包含一个字符。但是，一个String对象可以包含多个字符。

因此，我们的教程将涵盖两种情况：

-   源是单字符字符串。
-   源是一个多字符字符串。

对于情况 1，我们可以很容易地将单个字符作为char获取。例如，假设这是我们的输入：

```java
String STRING_b = "b";
```

转换后，我们期望有一个字符' b '。

对于情况 2，如果源String是一个多字符字符串，而我们仍然希望得到单个字符作为结果，我们必须分析需求以选择所需的字符，例如第一个、最后一个或 n-第字符。

在本教程中，我们将解决更通用的解决方案。我们会将源字符串转换为一个包含字符串中每个字符的字符数组。这样，我们可以根据需要选择任何元素。

我们将使用STRING_Baeldung作为输入示例：

```java
String STRING_Baeldung = "Baeldung";
```

那么接下来，让我们看看他们的行动。

## 3. 单字符串

Java 的String类提供[charAt()](https://www.baeldung.com/string/char-at)以从输入字符串中获取第 n 个字符(从 0 开始)作为char。因此，我们可以直接调用方法getChar(0)将单个字符String转换为char：

```java
assertEquals('b', STRING_b.charAt(0));
```

但是，我们应该注意，如果输入是空字符串，则 charAt()方法调用会抛出StringIndexOutOfBoundsException：

```java
assertThrows(StringIndexOutOfBoundsException.class, () -> "".charAt(0));
```

因此，在调用charAt()方法之前，我们应该检查输入字符串是否为 null 或为空。

## 4. 多字符串

我们已经学会了使用charAt(0)将单个字符String转换为char。如果输入是多字符String，并且我们确切地知道要将哪个字符转换为char，我们仍然可以使用charAt()方法。例如，我们可以从输入字符串“ Baeldung ”中获取第四个字符(' l ') ：

```java
assertEquals('l', STRING_Baeldung.charAt(3));
```

此外，我们可以使用String.toCharArray() 来获取包含所有字符的char[]数组：

```java
assertArrayEquals(new char[] { 'B', 'a', 'e', 'l', 'd', 'u', 'n', 'g' }, STRING_Baeldung.toCharArray());
```

值得一提的是，toCharArray()方法也适用于空字符串输入。它返回一个空的char数组作为结果：

```java
assertArrayEquals(new char[] {}, "".toCharArray());
```

除了toCharArray()之外， String.getChars( [)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#getChars(int,int,char[],int))还可以将给定String中的连续字符提取到char数组中。该方法接收四个参数：

-   srcBegin – 要获取的字符串中第一个字符的索引，包括在内
-   srcEnd – 要的字符串中最后一个字符的索引，独占
-   dst – 目标数组，这是我们的结果
-   dstBegin – 目标数组中的起始偏移量。我们将通过一个例子来讨论这个问题。

首先，让我们从字符串“ Baeldung ”中提取“ aeld ”并将其填充到预定义的字符数组中：

```java
char[] aeld = new char[4];
STRING_Baeldung.getChars(1, 5, aeld, 0);
assertArrayEquals(new char[] { 'a', 'e', 'l', 'd' }, aeld);
```

如上面的测试所示，要调用getChars()，我们应该首先有一个char数组来保存结果。

在示例中，我们 在调用getChars()时为dstBegin传递0。这是因为我们希望转换后的结果从数组aeld中的第一个元素开始。

当然，有时候，我们希望结果覆盖数组的中间部分。然后我们可以将dstBegin设置为所需的值。

接下来，让我们看另一个将“ aeld ”转换为 chars 并从第二个 (index=1) 元素开始覆盖目标数组的示例：

```java
char[] anotherArray = new char[] { '#', '#', '#', '#', '#', '#' };
STRING_Baeldung.getChars(1, 5, anotherArray, 1);
assertArrayEquals(new char[] { '#', 'a', 'e', 'l', 'd', '#' }, anotherArray);
```

因此，正如我们所见，我们将dstBegin=1传递给该方法并获得预期结果。

## 5.总结

在本文中，我们学习了如何在Java中将String转换为char。
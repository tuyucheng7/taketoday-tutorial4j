## 1. 概述

[String类](https://www.baeldung.com/java-string)是Java中使用最广泛的类之一，这促使语言设计者对其进行特殊对待[。](https://www.baeldung.com/java-string)这种特殊的行为使其成为Java面试中最热门的话题之一。

在本教程中，我们将介绍有关String的一些最常见的面试问题。

## 2.弦乐基础

本节包含有关String内部结构和内存的问题。

### Q1。什么是Java中的字符串？

在Java中， String在内部由字节值数组 (或JDK 9 之前的char值)表示。

在Java8 及之前的版本中，字符串由不可变的 Unicode 字符数组组成。然而，大多数字符只需要 8 位(1字节)来表示它们而不是 16 位(字符大小)。

为了改善内存消耗和性能，Java 9 引入了[紧凑的字符串](https://www.baeldung.com/java-9-compact-string)。这意味着如果String仅包含 1 个字节的字符，它将使用Latin-1编码表示。如果一个字符串至少包含 1 个多字节字符，它将使用 UTF-16 编码表示为每个字符 2 个字节。

在 C 和 C++ 中，String也是一个字符数组，但在Java中，它是一个具有自己 API 的独立对象。

### Q2。我们如何在Java中创建字符串对象？

[java.lang.String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html) 定义了[13 种不同的方法来创建String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#constructor.summary)。不过一般来说有两种：

-   通过 

    字符串

    文字：

    ```java
    String s = "abc";
    ```

-   通过

    新

    关键字：

    ```java
    String s = new String("abc");
    ```

Java 中的所有字符串文字都是 String类的实例。

### Q3. 字符串是 原始类型还是派生类型？

String是派生类型，因为它具有状态和行为。例如，它有像substring()、indexOf()和 equals() 这样的方法， 而这些方法是原语所没有的。

但是，由于我们都经常使用它，它有一些特殊的特性，使它感觉像一个原始的：

-   虽然字符串不像原语那样存储在调用堆栈中，但它们 存储在称为[字符串池的特殊内存区域中](https://www.baeldung.com/java-string-pool)
-   像原语一样，我们可以在字符串上使用+ 运算符
-   同样，像原语一样，我们可以创建一个不带 new 关键字的String 实例

### Q4. 字符串不可变有什么好处？

根据James Gosling[的采访，字符串是不可变的以提高性能和安全性。](https://www.artima.com/intv/gosling313.html)

实际上，我们看到了[拥有不可变字符串的几个好处](https://www.baeldung.com/java-string-immutable)：

-   字符串池只有在字符串一旦创建就永远不会更改的情况下才有可能，因为它们应该被重用
-   代码可以安全地将一个字符串传递给另一个方法，知道它不能被那个方法改变。
-   不可变地自动使此类成为线程安全的
-   由于这个类是线程安全的， 所以不需要同步公共数据，从而提高了性能
-   由于保证它们不会更改，因此可以轻松缓存它们的哈希码

### Q5. 字符串如何存储在内存中？

根据 JVM 规范，String literals 存储在 [运行时常量池中，该常量池是从 JVM 的](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.5.5)[方法区](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.5.4)分配的。

尽管方法区在逻辑上是堆内存的一部分，但规范并未规定位置、内存大小或垃圾回收策略。它可以是特定于实现的。

类或接口的运行时常量池是在 JVM 创建类或接口时构建的。

### Q6. 在Java中，实习字符串是否符合垃圾收集的条件？

是的，如果程序没有引用，字符串池中的所有String都可以进行垃圾回收。

### Q7. 什么是字符串常量池？

[字符串池](https://www.baeldung.com/java-string-pool)，也称为 String常量池或 String intern池，是JVM存放String实例的特殊内存区域。

它通过减少分配字符串的频率和数量来优化应用程序性能：

-   JVM在池中只存储一个特定字符串的副本
-   创建新的String时，JVM 在池中搜索具有相同值的String
-   如果找到，JVM 返回对该字符串的引用而不分配任何额外的内存
-   如果未找到，则 JVM 将其添加到池中(实习)并返回其引用

### Q8. 字符串是线程安全的吗？如何？

字符串确实是完全线程安全的，因为它们是不可变的。任何不可变的类都自动符合线程安全的条件，因为它的不可变性保证了它的实例不会在多个线程中被更改。

例如，如果一个线程更改了一个字符串的值，则会创建一个新的 字符串 而不是修改现有的字符串。

### Q9. 为哪些字符串操作提供语言环境很重要？

Locale类允许我们区分不同的文化区域并适当地格式化我们的内容。

当涉及到 String 类时，我们在以格式呈现字符串 或小写或大写字符串时需要它。

事实上，如果我们忘记这样做，我们可能会遇到可移植性、安全性和可用性方面的问题。

### Q10。字符串的底层字符编码是什么？

[根据Java8 及以下版本的String的 Javadocs](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html) ，字符串在内部以 UTF-16 格式存储。

char数据类型和java.lang.Character对象[也基于原始的 Unicode 规范](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#unicode)，该规范将字符定义为固定宽度的 16 位实体。

从 JDK 9 开始， 仅包含 1 个字节字符的字符串使用 Latin-1编码，而 至少包含 1 个多字节字符的字符串使用 UTF-16 编码。

## 3. 字符串API

在本节中，我们将讨论与 String API 相关的一些问题。

### Q11. 我们如何在Java中比较两个字符串？str1 == str2和str1.equals(str2)有什么区别？

我们可以通过两种不同的方式[比较字符串](https://www.baeldung.com/java-compare-strings)：使用等于运算符 ( == ) 和使用equals()方法。

两者彼此完全不同：

-   运算符 ( str1 == str2 ) 检查引用相等性
-   该方法 ( str1.equals(str2) ) 检查词法相等

但是，如果两个字符串在词法上相等，那么 str1.intern() == str2.intern() 也是真的。

通常，为了比较两个字符串的内容，我们应该始终使用 String.equals。

### Q12. 我们如何在Java中拆分字符串？

String类本身为我们提供[了String# split 方法](https://www.baeldung.com/string/split)，它接受正则表达式分隔符。它返回给我们一个String[]数组：

```java
String[] parts = "john,peter,mary".split(",");
assertEquals(new String[] { "john", "peter", "mary" }, parts);
```

关于split的一件棘手的事情 是，当拆分一个空字符串时，我们可能会得到一个非空数组：

```java
assertEquals(new String[] { "" }, "".split(","));
```

当然，split 只是 [拆分JavaString](https://www.baeldung.com/java-split-string)的众多方法之一。

### Q13. 什么是 Stringjoiner？

[StringJoiner](https://www.baeldung.com/java-string-joiner) 是Java8 中引入的一个类，用于将单独的字符串连接成一个，例如获取颜色列表并将它们作为逗号分隔的字符串返回。我们可以提供定界符以及前缀和后缀：

```java
StringJoiner joiner = new StringJoiner(",", "[", "]");
joiner.add("Red")
  .add("Green")
  .add("Blue");

assertEquals("[Red,Green,Blue]", joiner.toString());
```

### Q14. String、Stringbuffer 和 Stringbuilder 的区别？

字符串是不可变的。这意味着如果我们尝试更改或改变它的值，那么Java会创建一个全新的String。 

例如，如果我们在创建字符串str1 之后添加它：

```java
String str1 = "abc";
str1 = str1 + "def";
```

然后 JVM 不修改str1 ，而是创建一个全新的String。

但是，对于大多数简单的情况，编译器在内部使用StringBuilder并优化了上述代码。

但是，对于像循环这样更复杂的代码，它将创建一个全新的String，从而降低性能。这是StringBuilder和StringBuffer有用的地方。

[Java 中](https://www.baeldung.com/java-string-builder-string-buffer)的StringBuilder[和](https://www.baeldung.com/java-string-builder-string-buffer)[StringBuffer都会](https://www.baeldung.com/java-string-builder-string-buffer)创建包含可变字符序列的对象。StringBuffer 是同步的，因此是线程安全的，而StringBuilder不是。 

由于 StringBuffer 中的额外同步通常是不必要的，因此我们通常可以通过选择 StringBuilder 来提高性能。

### Q15. 为什么将密码存储在 Char[] 数组而不是字符串中更安全？

由于字符串是不可变的，因此它们不允许修改。这种行为使我们无法覆盖、修改或清零其内容，从而使字符串 不适合存储敏感信息。

我们必须依靠垃圾收集器来删除字符串的内容。此外，在Java6 及以下版本中，字符串存储在 PermGen 中，这意味着一旦创建了一个字符串 ，它就永远不会被垃圾回收。

通过使用char[]数组，我们可以完全控制该信息。 我们甚至可以在不依赖垃圾收集器的情况下对其进行修改或彻底清除。

 在String上使用char[] 并不能完全保护信息；这只是一种额外的措施，可以减少恶意用户访问敏感信息的机会。

### Q16. String 的 intern() 方法有什么作用？

[intern()](https://www.baeldung.com/string/intern)方法 在堆中 创建 String对象的精确副本，并将其存储在JVM 维护的String 常量池中。

Java 自动实习所有使用字符串文字创建的字符串，但是如果我们使用 new 运算符创建一个字符串，例如String str = new String(“abc”)，那么Java会将它添加到堆中，就像任何其他对象一样。

我们可以调用 intern()方法告诉 JVM 将它添加到字符串池中(如果它不存在)，并返回该驻留字符串的引用：

```java
String s1 = "Baeldung";
String s2 = new String("Baeldung");
String s3 = new String("Baeldung").intern();

assertThat(s1 == s2).isFalse();
assertThat(s1 == s3).isTrue();
```

### Q17. 我们如何在Java中将字符串转换为整数以及将整数转换为字符串？

[将String](https://www.baeldung.com/java-convert-string-to-int-or-integer)[转换为 Integer](https://www.baeldung.com/java-convert-string-to-int-or-integer)最直接的方法 是使用Integer# parseInt：

```java
int num = Integer.parseInt("22");
```

相反，我们可以使用Integer# toString：

```java
String s = Integer.toString(num);
```

### Q18. 什么是 String.format() 以及我们如何使用它？

[String#format](https://www.baeldung.com/string/format) 使用指定的格式字符串和参数返回格式化字符串。

```java
String title = "Baeldung"; 
String formatted = String.format("Title is %s", title);
assertEquals("Title is Baeldung", formatted);
```

我们还需要记住指定用户的 区域设置， 除非我们可以简单地接受操作系统默认设置：

```java
Locale usersLocale = Locale.ITALY;
assertEquals("1.024",
  String.format(usersLocale, "There are %,d shirts to choose from. Good luck.", 1024))
```

### Q19. 我们如何将字符串转换为大写和小写？

String隐式提供[String#toUpperCase](https://www.baeldung.com/string/to-upper-case) 以将大小写更改为大写。

但是， Javadocs提醒我们需要指定用户的Locale以确保正确性：

```java
String s = "Welcome to Baeldung!";
assertEquals("WELCOME TO BAELDUNG!", s.toUpperCase(Locale.US));
```

同样，要转换为小写，我们有[String#toLowerCase](https://www.baeldung.com/string/to-lower-case)：

```java
String s = "Welcome to Baeldung!";
assertEquals("welcome to baeldung!", s.toLowerCase(Locale.UK));
```

### Q20。我们如何从字符串中获取字符数组？

String提供 toCharArray，它返回 JDK9 之前的内部char数组的副本(并在 JDK9+ 中将String转换为新的char数组)：

```java
char[] hello = "hello".toCharArray();
assertArrayEquals(new String[] { 'h', 'e', 'l', 'l', 'o' }, hello);
```

### Q21. 我们如何将Java字符串转换为字节数组？

默认情况下，方法[String#getBytes()](https://www.baeldung.com/string/get-bytes)使用平台的默认字符集将 String 编码为字节数组。

虽然 API 不要求我们指定字符集，[但为了确保安全性和可移植性，我们应该](https://www.baeldung.com/java-char-encoding)：

```java
byte[] byteArray2 = "efgh".getBytes(StandardCharsets.US_ASCII);
byte[] byteArray3 = "ijkl".getBytes("UTF-8");
```

## 4.基于字符串的算法

在本节中，我们将讨论一些与String相关的编程问题。

### Q22. 我们如何检查两个字符串是否是Java中的 Anagrams？

字谜是通过重新排列另一个给定单词的字母而形成的单词，例如“car”和“arc”。

首先，我们首先检查两个字符串的长度是否相等。

然后我们[将它们转换为char[]数组，对它们进行排序，然后检查是否相等](https://www.baeldung.com/java-sort-string-alphabetically)。

### Q23. 我们如何计算给定字符在字符串中出现的次数？

Java 8 真正简化了如下聚合任务：

```java
long count = "hello".chars().filter(ch -> (char)ch == 'l').count();
assertEquals(2, count);
```

而且，还有其他几种[计算 l's 的](https://www.baeldung.com/java-count-chars)好方法，包括循环、递归、正则表达式和外部库。

### Q24. 我们如何在Java中反转字符串？

有很多方法可以做到这一点，最直接的方法是使用StringBuilder(或StringBuffer )的反向方法：

```java
String reversed = new StringBuilder("baeldung").reverse().toString();
assertEquals("gnudleab", reversed);
```

### Q25. 我们如何检查一个字符串是否是回文？

[回文](https://www.baeldung.com/java-palindrome-substrings)是任何前后读音相同的字符序列，例如“madam”、“radar”或“level”。

要[检查一个字符串是否为回文](https://www.baeldung.com/java-palindrome)，我们可以开始在一个循环中向前和向后迭代给定的字符串，一次一个字符。循环在第一次不匹配时退出。

## 5.总结

在本文中，我们介绍了一些最常见的String面试问题。
## 1. 概述

在本教程中，我们将学习如何在Java中生成随机字符串，首先使用标准Java库，然后使用Java8 变体，最后使用[Apache Commons Lang 库](https://commons.apache.org/proper/commons-lang/)。

本文是Baeldung 上[“Java – 回归基础”系列的一部分。](https://www.baeldung.com/java-tutorial)

## 2. 使用纯Java生成随机无界字符串

让我们从简单开始，生成一个限制为 7 个字符的随机字符串：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
    byte[] array = new byte[7]; // length is bounded by 7
    new Random().nextBytes(array);
    String generatedString = new String(array, Charset.forName("UTF-8"));

    System.out.println(generatedString);
}
```

请记住，新字符串不会是任何远程字母数字。

## 延伸阅读：

## [Java 中高效的词频计算器](https://www.baeldung.com/java-word-frequency)

探索在Java中计算单词的各种方法并查看它们的执行情况。

[阅读更多](https://www.baeldung.com/java-word-frequency)→

## [Java – 随机长整型、浮点型、整数和双精度](https://www.baeldung.com/java-generate-random-long-float-integer-double)

了解如何在Java中生成随机数 - 既可以是无限的，也可以是在给定的时间间隔内。

[阅读更多](https://www.baeldung.com/java-generate-random-long-float-integer-double)→

## [Java字符串池指南](https://www.baeldung.com/java-string-pool)

了解 JVM 如何优化分配给Java字符串池中字符串存储的内存量。

[阅读更多](https://www.baeldung.com/java-string-pool)→

## 3. 使用纯Java生成随机有界字符串

接下来让我们看看如何创建一个更受约束的随机字符串；我们将使用小写字母和设定长度生成一个随机字符串：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomStringBounded_thenCorrect() {
 
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(targetStringLength);
    for (int i = 0; i < targetStringLength; i++) {
        int randomLimitedInt = leftLimit + (int) 
          (random.nextFloat()  (rightLimit - leftLimit + 1));
        buffer.append((char) randomLimitedInt);
    }
    String generatedString = buffer.toString();

    System.out.println(generatedString);
}
```

## 4. 使用Java8 生成随机字母字符串

现在让我们使用JDK 8 中添加的Random.ints来生成字母字符串：

```java
@Test
public void givenUsingJava8_whenGeneratingRandomAlphabeticString_thenCorrect() {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();

    String generatedString = random.ints(leftLimit, rightLimit + 1)
      .limit(targetStringLength)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();

    System.out.println(generatedString);
}
```

## 5. 使用Java8 生成随机字母数字字符串

然后我们可以扩大我们的字符集以获得字母数字字符串：

```java
@Test
public void givenUsingJava8_whenGeneratingRandomAlphanumericString_thenCorrect() {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();

    String generatedString = random.ints(leftLimit, rightLimit + 1)
      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
      .limit(targetStringLength)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();

    System.out.println(generatedString);
}
```

我们使用上面的过滤方法来忽略 65 到 90 之间的 Unicode 字符，以避免超出范围的字符。

## 6. 使用 Apache Commons Lang 生成有界随机字符串

Apache 的 Commons Lang 库对随机字符串生成有很大帮助。让我们看一下仅使用字母生成有界字符串：

```java
@Test
public void givenUsingApache_whenGeneratingRandomStringBounded_thenCorrect() {
 
    int length = 10;
    boolean useLetters = true;
    boolean useNumbers = false;
    String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

    System.out.println(generatedString);
}
```

因此，与Java示例中的所有低级代码不同，这个代码是用一个简单的单行代码完成的。

## 7. 使用 Apache Commons Lang 生成字母字符串

这是另一个非常简单的例子，这次是一个只有字母字符的有界字符串，但没有将布尔标志传递给 API：

```java
@Test
public void givenUsingApache_whenGeneratingRandomAlphabeticString_thenCorrect() {
    String generatedString = RandomStringUtils.randomAlphabetic(10);

    System.out.println(generatedString);
}
```

## 8. 使用 Apache Commons Lang 生成字母数字字符串

最后，我们有相同的随机有界字符串，但这次是数字：

```java
@Test
public void givenUsingApache_whenGeneratingRandomAlphanumericString_thenCorrect() {
    String generatedString = RandomStringUtils.randomAlphanumeric(10);

    System.out.println(generatedString);
}
```

我们已经有了它，使用纯 Java、Java 8 变体或 Apache Commons Library创建有界和无界字符串。

## 9.总结

通过不同的实现方法，我们能够使用纯 Java、Java 8 变体或 Apache Commons Library 生成绑定和未绑定的字符串。

在这些Java示例中，我们使用了java.util.Random，但值得一提的是它不是加密安全的。考虑对安全敏感的应用程序使用[java.security.SecureRandom 。](https://www.baeldung.com/java-secure-random)
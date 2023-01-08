## 1. 概述

在本教程中，我们将熟悉用于生成包含N 个重复字符的字符串的不同选项。[当我们需要添加填充空白、生成ASCII 艺术](https://www.baeldung.com/ascii-art-in-java)等时，这会派上用场。

这个问题在 JDK11 中很容易解决，但如果我们使用的是更早的版本，那么还有许多其他解决方案可用。我们将从最常见的方法开始，并添加一些库中的其他方法。

## 2.例子

让我们定义我们将在所有解决方案中使用的常量来验证生成的字符串：

```java
private static final String EXPECTED_STRING = "aaaaaaa";
private static final int N = 7;
```

因此，EXPECTED_STRING常量表示我们需要在解决方案中生成的字符串。N常量用于定义字符重复的次数。

现在，让我们检查生成 N 个重复字符a的字符串的选项。

## 3. JDK11的String.repeat函数

Java 有一个 repeat函数来构建源字符串的副本：

```java
String newString = "a".repeat(N);
assertEquals(EXPECTED_STRING, newString);
```

这允许我们重复单个字符或多字符字符串：

```java
String newString = "-->".repeat(5);
assertEquals("-->-->-->-->-->", newString);
```

这背后的算法使用循环来非常有效地填充字符数组。

如果我们没有 JDK11，那么我们将不得不自己创建一种算法，或者使用来自第三方库的算法。其中最好的是不太可能比 JDK11 本机解决方案更快或更容易使用。

## 4. 构建字符串的常用方法

### 4.1. 带有for循环的StringBuilder

让我们从StringBuilder类开始。我们将循环遍历 N 次附加重复字符的for循环：

```java
StringBuilder builder = new StringBuilder(N);
for (int i = 0; i < N; i++) {
    builder.append("a");
}
String newString = builder.toString();
assertEquals(EXPECTED_STRING, newString);
```

通过这种方法，我们得到了想要的字符串。这可能是最容易理解的方法，但在运行时不一定是最快的。

### 4.2. 带有for循环的char数组

我们可以用我们想要的字符填充一个固定大小的char数组并将其转换为字符串：

```java
char[] charArray = new char[N];
for (int i = 0; i < N; i++) {
    charArray[i] = 'a';
}
String newString = new String(charArray);
assertEquals(EXPECTED_STRING, newString);
```

这应该更快，因为它不需要动态大小的结构来存储我们构建的字符串，并且Java可以有效地将char数组转换为 String。

### 4.3. 数组填充方法

我们可以使用库函数来填充我们的数组，而不是使用循环：

```java
char charToAppend = 'a';
char[] charArray = new char[N];
Arrays.fill(charArray, charToAppend);
String newString = new String(charArray);
assertEquals(EXPECTED_STRING, newString);
```

这更短，并且在运行时与以前的解决方案一样高效。

## 5. 使用repeat方法生成字符串

### 5.1. Apache重复 方法

此解决方案需要为[Apache Commons 库](https://search.maven.org/artifact/org.apache.commons/commons-lang3/3.12.0/jar)添加一个新的依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

添加此依赖项后，我们可以使用StringUtils类中的repeat方法 。它以一个要重复的字符和该字符应重复的次数作为参数：

```java
char charToAppend = 'a';
String newString = StringUtils.repeat(charToAppend, N);
assertEquals(EXPECTED_STRING, newString);
```

### 5.2. 番石榴重复法

与以前的方法一样，这个方法需要[Guava](https://search.maven.org/artifact/com.google.guava/guava/31.0.1-jre/bundle)库的新依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

除了它来自不同的库之外，这个解决方案与 Apache Commons 的解决方案相同：

```java
String charToAppend = "a";
String newString = Strings.repeat(charToAppend, N);
assertEquals(EXPECTED_STRING, newString);
```

## 6. 使用nCopies方法生成字符串

如果我们将目标字符串视为重复子字符串的集合，那么我们可以使用List实用程序来构建列表，然后将结果列表转换为我们的最终String。为此，我们可以使用java.util包中Collections类的nCopies方法：

```java
public static <T> List<T> nCopies(int n, T o);
```

虽然构建子字符串列表不如我们使用固定字符数组的解决方案有效，但重复字符模式而不是仅重复单个字符可能会有所帮助。

### 6.1. 字符串 连接 和nCopies方法

让我们使用nCopies方法创建一个单字符串列表，并使用String.join将其转换为我们的结果：

```json
String charToAppend = "a";
String newString = String.join("", Collections.nCopies(N, charToAppend));
assertEquals(EXPECTED_STRING, newString);
```

String.join 方法需要一个分隔符，为此我们使用空字符串。

### 6.2. Guava Joiner和nCopies方法

Guava 提供了另一种字符串连接器，我们也可以使用它：

```java
String charToAppend = "a";
String newString = Joiner.on("").join(Collections.nCopies(N, charToAppend));
assertEquals(EXPECTED_STRING, newString);
```

## 7.使用Stream generate方法生成字符串

创建子字符串列表的缺点是我们在构造最终字符串之前创建了一个可能很大的临时列表对象。

但是，从Java8 开始，我们可以使用[Stream](https://www.baeldung.com/java-8-streams-introduction)[ API中的](https://www.baeldung.com/java-8-streams-introduction)generate方法。结合limit方法(用于定义长度)和collect方法，我们可以生成一个包含 N 个重复字符的字符串：

```java
String charToAppend = "a";
String newString = generate(() -> charToAppend)
  .limit(length)
  .collect(Collectors.joining());
assertEquals(exampleString, newString);
```

## 8.使用 Apache 的RandomStringUtils生成字符串

Apache Commons库中的RandomStringUtils类可以使用随机方法生成包含 N 个重复字符的字符串。我们必须定义一个字符和重复次数：

```java
String charToAppend = "a";
String newString = RandomStringUtils.random(N, charToAppend);
assertEquals(EXPECTED_STRING, newString);
```

## 9.总结

在本文中，我们看到了生成包含 N 个重复字符的字符串的各种解决方案。其中最简单的是 String.repeat，从 JDK 11 开始可用。

对于早期版本的 Java，还有许多其他可能的可用选项。最佳选择将取决于我们在运行时效率、编码简便性和库可用性方面的要求。
## 1. 概述

Java 中的字符串在内部由包含String字符的char[]表示。而且，每个字符由 2 个字节组成，因为Java 内部使用 UTF-16。

例如，如果String包含英语单词，则每个char的前 8 位都将为 0 ，因为 ASCII 字符可以使用单个字节表示。

许多字符需要 16 位来表示它们，但统计上大多数只需要 8 位——LATIN-1 字符表示。因此，存在改善内存消耗和性能的范围。

同样重要的是，String通常通常占据 JVM 堆空间的很大一部分。而且，由于 JVM 存储它们的方式，在大多数情况下，String实例可能占用它实际需要的双倍空间。

在本文中，我们将讨论 JDK6 中引入的 Compressed String 选项和最近在 JDK9 中引入的新的 Compact String。这两个都是为了优化 JMV 上字符串的内存消耗而设计的。

## 2. 压缩字符串——Java 6

JDK 6 update 21 Performance Release，引入了一个新的 VM 选项：

```java
-XX:+UseCompressedStrings
```

启用此选项后，字符串将存储为byte[]，而不是char[]——因此可以节省大量内存。但是，这个选项最终在 JDK 7 中被删除，主要是因为它有一些意想不到的性能后果。

## 3. 压缩字符串——Java 9

Java 9带回了紧凑字符串的概念。

这意味着每当我们创建一个String时，如果String的所有字符都可以使用一个字节来表示——LATIN-1 表示，那么将在内部使用一个字节数组，这样一个字节对应一个字符。

在其他情况下，如果任何字符需要超过 8 位来表示它，则所有字符都使用两个字节来存储 - UTF-16 表示。

所以基本上，只要有可能，它只会为每个字符使用一个字节。

现在，问题是——所有字符串操作将如何工作？它将如何区分 LATIN-1 和 UTF-16 表示法？

那么，为了解决这个问题，对String的内部实现进行了另一项更改。我们有一个 final field coder，它保存了这些信息。

### 3.1.Java9 中的字符串实现

到目前为止，字符串被存储为char[]：

```java
private final char[] value;
```

从现在开始，它将是一个字节[]：

```java
private final byte[] value;
```

变量编码器：

```java
private final byte coder;
```

编码器可以在哪里：

```java
static final byte LATIN1 = 0;
static final byte UTF16 = 1;
```

大多数String操作现在检查编码器并分派给特定的实现：

```java
public int indexOf(int ch, int fromIndex) {
    return isLatin1() 
      ? StringLatin1.indexOf(value, ch, fromIndex) 
      : StringUTF16.indexOf(value, ch, fromIndex);
}  

private boolean isLatin1() {
    return COMPACT_STRINGS && coder == LATIN1;
}

```

有了 JVM 需要准备好并可用的所有信息，默认情况下启用CompactString VM 选项。要禁用它，我们可以使用：

```java
+XX:-CompactStrings
```

### 3.2. 编码器如何工作

在Java9 String类实现中，长度计算如下：

```java
public int length() {
    return value.length >> coder;
}
```

如果字符串仅包含 LATIN-1，则编码器的值将为 0，因此字符串的长度将与字节数组的长度相同。

在其他情况下，如果字符串采用 UTF-16 表示，编码器的值将为 1，因此长度将为实际字节数组大小的一半。

请注意，对 Compact String所做的所有更改都在String类的内部实现中，并且对于使用String的开发人员来说是完全透明的。

## 4. 紧凑字符串与压缩字符串

在 JDK 6 压缩字符串的情况下，面临的一个主要问题是String构造函数只接受char[]作为参数。除此之外，许多String操作依赖于char[]表示而不是字节数组。因此，必须进行大量拆包，这影响了性能。

而在 Compact String 的情况下，维护额外的字段“coder”也会增加开销。为了降低编码器的成本和将byte s解包为char s(在 UTF-16 表示的情况下)，一些方法被[内在](https://en.wikipedia.org/wiki/Intrinsic_function)化，并且 JIT 编译器生成的 ASM 代码也得到了改进。

这种变化导致了一些反直觉的结果。LATIN-1 indexOf(String)调用内部方法，而indexOf(char)不调用。在 UTF-16 的情况下，这两种方法都调用内部方法。此问题仅影响 LATIN-1字符串，将在未来版本中修复。

因此，就性能而言，Compact Strings优于 Compressed Strings。

为了找出使用压缩字符串节省了多少内存，分析了各种Java应用程序堆转储。而且，虽然结果在很大程度上取决于特定的应用程序，但总体改进几乎总是相当可观的。

### 4.1. 性能差异

让我们看一个非常简单的示例，说明启用和禁用压缩字符串之间的性能差异：

```java
long startTime = System.currentTimeMillis();
 
List strings = IntStream.rangeClosed(1, 10_000_000)
  .mapToObj(Integer::toString) 
  .collect(toList());
 
long totalTime = System.currentTimeMillis() - startTime;
System.out.println(
  "Generated " + strings.size() + " strings in " + totalTime + " ms.");

startTime = System.currentTimeMillis();
 
String appended = (String) strings.stream()
  .limit(100_000)
  .reduce("", (l, r) -> l.toString() + r.toString());
 
totalTime = System.currentTimeMillis() - startTime;
System.out.println("Created string of length " + appended.length() 
  + " in " + totalTime + " ms.");
```

在这里，我们创建了 1000 万个String，然后以一种简单的方式附加它们。当我们运行这段代码时(默认启用压缩字符串)，我们得到输出：

```java
Generated 10000000 strings in 854 ms.
Created string of length 488895 in 5130 ms.
```

同样，如果我们通过使用-XX:-CompactStrings选项禁用压缩字符串来运行它，输出为：

```java
Generated 10000000 strings in 936 ms.
Created string of length 488895 in 9727 ms.
```

显然，这是一个表面级别的测试，它不能具有高度的代表性——它只是新选项在这个特定场景中可以做些什么来提高性能的一个快照。

## 5.总结

在本教程中，我们看到了优化 JVM 性能和内存消耗的尝试——通过以内存高效的方式存储String。
## 1. 概述

在这篇简短的文章中，我们将了解 布尔 值在不同情况下在 JVM 中的占用空间是多少。

首先，我们将检查 JVM 以查看对象大小。然后，我们将了解这些尺寸背后的基本原理。

## 2.设置

为了检查 JVM 中对象的内存布局，我们将广泛使用Java对象布局 ( [JOL](https://openjdk.java.net/projects/code-tools/jol/) )。因此，我们需要添加[jol-core](https://search.maven.org/artifact/org.openjdk.jol/jol-core) 依赖：

```xml
<dependency>
    <groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.10</version>
</dependency>
```

## 3. 对象大小

如果我们要求 JOL 根据对象大小打印 VM 详细信息：

```java
System.out.println(VM.current().details());
```

[启用压缩引用](https://www.baeldung.com/jvm-compressed-oops)后 (默认行为)，我们将看到输出：

```bash
# Running 64-bit HotSpot VM.
# Using compressed oop with 3-bit shift.
# Using compressed klass with 3-bit shift.
# Objects are 8 bytes aligned.
# Field sizes by type: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
# Array element sizes: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
```

在前几行中，我们可以看到有关 VM 的一些一般信息。之后，我们了解对象大小：

-   Java引用占用4字节，boolean / byte是1字节，char / short是2字节，int / float是4字节，最后long / double是8字节
-   即使我们将它们用作数组元素，这些类型也会消耗相同数量的内存

因此，在存在压缩引用的情况下，每个布尔 值占用 1 个字节。同样，boolean[] 中的 每个布尔 值占用 1 个字节。 但是，对齐填充和对象标头会增加 boolean 和 boolean[] 占用的空间，我们将在后面看到。

### 3.1. 没有压缩参考

即使我们通过-XX:-UseCompressedOops禁用压缩引用，布尔大小也不会改变：

```bash
# Field sizes by type: 8, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
# Array element sizes: 8, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
```

另一方面，Java 引用占用了两倍的内存。

因此，尽管我们一开始可能会想到，布尔值 会消耗 1 个字节而不是 1 位。

### 3.2. 撕字

在大多数体系结构中，无法以原子方式访问单个位。即使我们想这样做，我们也可能会在更新另一个位的同时写入相邻位。

JVM 的设计目标之一就是防止这种现象，称为[word tearing](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html#jls-17.6)。也就是说，在 JVM 中，每个字段和数组元素都应该是不同的；对一个字段或元素的更新不得与任何其他字段或元素的读取或更新交互。

回顾一下，可寻址性问题和单词撕裂是布尔值不仅仅是一位的主要原因。

## 4. 普通对象指针(OOPs)

现在我们知道boolean是 1 个字节，让我们考虑这个简单的类：

```java
class BooleanWrapper {
    private boolean value;
}
```

如果我们使用 JOL 检查此类的内存布局：

```java
System.out.println(ClassLayout.parseClass(BooleanWrapper.class).toPrintable());
```

然后 JOL 将打印内存布局：

```bash
 OFFSET  SIZE      TYPE DESCRIPTION                               VALUE
      0    12           (object header)                           N/A
     12     1   boolean BooleanWrapper.value                      N/A
     13     3           (loss due to the next object alignment)
Instance size: 16 bytes
Space losses: 0 bytes internal + 3 bytes external = 3 bytes total
```

BooleanWrapper 布局 包括：

-   头部12字节，包括2个[mark](http://hg.openjdk.java.net/jdk8/jdk8/hotspot/file/87ee5ee27509/src/share/vm/oops/markOop.hpp) word和1个[klass](http://hg.openjdk.java.net/jdk8/jdk8/hotspot/file/87ee5ee27509/src/share/vm/oops/klass.hpp) word。HotSpot JVM 使用标记字来存储 GC 元数据、身份哈希码和锁定信息。此外，它使用klass词来存储类元数据，例如运行时类型检查
-   1 个字节用于实际 布尔 值
-   用于对齐目的的 3 个字节的填充

默认情况下，对象引用应按 8 字节对齐。因此，JVM 将 3 个字节添加到 13 个字节的标头和 布尔值 ，使其成为 16 个字节。

因此， 布尔 字段可能会因其字段对齐而消耗更多内存。

### 4.1. 自定义对齐

如果我们通过 -XX:ObjectAlignmentInBytes=32 将对齐值更改为 32， 则相同的类布局将更改为：

```bash
OFFSET  SIZE      TYPE DESCRIPTION                               VALUE
      0    12           (object header)                           N/A
     12     1   boolean BooleanWrapper.value                      N/A
     13    19           (loss due to the next object alignment)
Instance size: 32 bytes
Space losses: 0 bytes internal + 19 bytes external = 19 bytes total
```

如上所示，JVM 添加了 19 个字节的填充，使对象大小成为 32 的倍数。

## 5. 数组 OOP

让我们看看 JVM 如何在内存中布局一个 布尔 数组：

```java
boolean[] value = new boolean[3];
System.out.println(ClassLayout.parseInstance(value).toPrintable());
```

这将打印实例布局如下：

```bash
OFFSET  SIZE      TYPE DESCRIPTION                              
      0     4           (object header)  # mark word
      4     4           (object header)  # mark word
      8     4           (object header)  # klass word
     12     4           (object header)  # array length
     16     3   boolean [Z.<elements>    # [Z means boolean array                        
     19     5           (loss due to the next object alignment)
```

除了两个标记字和一个类字外，[数组指针](http://hg.openjdk.java.net/jdk8/jdk8/hotspot/file/87ee5ee27509/src/share/vm/oops/arrayOop.hpp)还包含额外的 4 个字节来存储它们的长度。 

由于我们的数组具有三个元素，因此数组元素的大小为 3 个字节。然而，这 3 个字节将由 5 个字段对齐字节填充以确保正确对齐。

虽然数组中的每个 布尔 元素只有 1 个字节，但整个数组消耗的内存要多得多。换句话说，我们应该在计算数组大小时考虑标头和填充开销。

## 六，总结

在本快速教程中，我们看到 布尔 字段占用 1 个字节。此外，我们了解到我们应该考虑对象大小的标头和填充开销。

如需更详细的讨论，强烈建议查看 JVM源代码的[oops 部分。](http://hg.openjdk.java.net/jdk8/jdk8/hotspot/file/87ee5ee27509/src/share/vm/oops/)此外，[Aleksey Shipilëv](https://shipilev.net/)在这方面有一篇更[深入的文章](https://shipilev.net/jvm/objects-inside-out/)。
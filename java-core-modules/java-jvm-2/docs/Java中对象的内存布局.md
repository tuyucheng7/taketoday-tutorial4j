## 1. 概述

在本教程中，我们将了解 JVM 如何在堆中布置对象和数组。

首先，我们将从一些理论开始。然后，我们将探讨不同情况下的不同对象和数组内存布局。

通常，运行时数据区的内存布局不是 JVM 规范的一部分，[由实现者自行决定](https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-2.html)。因此，每个 JVM 实现可能有不同的策略来在内存中布局对象和数组。在本教程中，我们关注一种特定的 JVM 实现：HotSpot JVM。

我们也可以互换使用 JVM 和 HotSpot JVM 术语。

## 2. 普通对象指针(OOPs)

HotSpot JVM 使用一种称为普通对象指针 ( [OOPS](https://github.com/openjdk/jdk15/tree/master/src/hotspot/share/oops) ) 的数据结构来表示指向对象的指针。 JVM 中的所有指针(包括对象和数组)都基于一种称为 [oopDesc](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/oop.hpp#L52)的特殊数据结构。 每个 oopDesc 使用以下信息描述指针：

-   一[标字](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/oop.hpp#L56)
-   一个，可能是压缩的， [类词](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/oop.hpp#L57)

标记[字](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/markWord.hpp#L33)描述对象头。HotSpot JVM 使用这个词来存储身份哈希码、偏向锁定模式、锁定信息和 GC 元数据。 

此外，标记字状态仅包含一个 [uintptr_t](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/markWord.hpp#L96)， 因此，其大小在 32 位和 64 位体系结构中分别在 4 和 8 字节之间变化。 此外，有偏见的和正常的对象的标记词是不同的。但是，我们只会考虑普通对象，因为Java15 将[弃用偏向锁](https://openjdk.java.net/jeps/374)。

此外，[klass 词](https://github.com/openjdk/jdk15/blob/bf1e6903a2499d0c2ab2f8703a1dc29046e8375d/src/hotspot/share/oops/klass.hpp#L54)封装了语言级别的类信息，例如类名、它的修饰符、超类信息等。

对于Java中的普通对象，表示为[instanceOop](https://github.com/openjdk/jdk15/blob/master/src/hotspot/share/oops/instanceOop.hpp)，对象头由 mark 和 klass words 加上可能的对齐填充组成。在对象头之后，可能有零个或多个对实例字段的引用。因此，在 64 位体系结构中至少有 16 个字节，因为 8 个字节的标记、4 个字节的 klass 和另外 4 个字节用于填充。

对于表示为 [arrayOop 的](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/arrayOop.hpp#L35)数组， 对象标头除了 mark、klass 和 paddings 之外还包含一个 4 字节的数组长度。 同样，由于 mark 的 8 个字节、klass 的 4 个字节和数组长度的另外 4 个字节，这将至少为 16 个字节。

现在我们已经对理论有了足够的了解，让我们看看内存布局在实践中是如何工作的。

## 3. 设置 JOL

为了检查 JVM 中对象的内存布局，我们将广泛使用Java对象布局 ( [JOL](https://openjdk.java.net/projects/code-tools/jol/) )。因此，我们需要添加[jol-core](https://search.maven.org/artifact/org.openjdk.jol/jol-core) 依赖：

```xml
<dependency>
    <groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.10</version>
</dependency>
```

## 4.内存布局示例

让我们从查看一般 VM 详细信息开始：

```java
System.out.println(VM.current().details());
```

这将打印：

```plaintext
# Running 64-bit HotSpot VM.
# Objects are 8 bytes aligned.
# Field sizes by type: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
# Array element sizes: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
```

这意味着引用占用 4 个字节， boolean s 和 byte s 占用 1 个字节， short s 和 char s 占用 2 个字节，int s 和 float s 占用 4 个字节，最后，long s 和 double s 占用 8 个字节。有趣的是，如果我们将它们用作数组元素，它们会消耗相同数量的内存。

此外，如果我们通过 -XX:-UseCompressedOops 禁用[压缩引用](https://www.baeldung.com/jvm-compressed-oops)， 则只有引用大小更改为 8 字节：

```plaintext
# Field sizes by type: 8, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
# Array element sizes: 8, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
```

### 4.1. 基本的

让我们考虑一个SimpleInt类：

```java
public class SimpleInt {
    private int state;
}
```

如果我们打印它的类布局：

```java
System.out.println(ClassLayout.parseClass(SimpleInt.class).toPrintable());
```

我们会看到类似的东西：

```plaintext
SimpleInt object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0    12        (object header)                           N/A
     12     4    int SimpleInt.state                           N/A
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
```

如上图，object header为12个字节，包括8个字节的mark和4个字节的klass。在那之后，我们有 4 个字节用于int state。总的来说，这个类的任何对象都会消耗 16 个字节。

此外，对象标头和状态没有值，因为我们正在解析类布局，而不是实例布局。

### 4.2. 身份哈希码

hashCode [()](https://www.baeldung.com/java-hashcode) 是所有Java对象的通用方法之一。当我们没有 为类 声明hashCode() 方法时，Java 将使用它的身份哈希码。

对象的身份哈希码在其生命周期内不会更改。因此，HotSpot JVM 在计算出该值后将其存储在标记字中。

让我们看看对象实例的内存布局：

```java
SimpleInt instance = new SimpleInt();
System.out.println(ClassLayout.parseInstance(instance).toPrintable());
```

HotSpot JVM 延迟计算身份哈希码：

```plaintext
SimpleInt object internals:
 OFFSET  SIZE   TYPE DESCRIPTION               VALUE
      0     4        (object header)           01 00 00 00 (00000001 00000000 00000000 00000000) (1) # mark
      4     4        (object header)           00 00 00 00 (00000000 00000000 00000000 00000000) (0) # mark
      8     4        (object header)           9b 1b 01 f8 (10011011 00011011 00000001 11111000) (-134145125) # klass
     12     4    int SimpleInt.state           0
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
```

如上所示，标记词目前似乎还没有存储任何重要内容。

但是，如果我们 在对象实例上调用[System.identityHashCode()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#identityHashCode(java.lang.Object)) 甚至 Object.hashCode() ，这将会改变：

```java
System.out.println("The identity hash code is " + System.identityHashCode(instance));
System.out.println(ClassLayout.parseInstance(instance).toPrintable());
```

现在，我们可以将身份哈希码识别为标记词的一部分：

```plaintext
The identity hash code is 1702146597
SimpleInt object internals:
 OFFSET  SIZE   TYPE DESCRIPTION               VALUE
      0     4        (object header)           01 25 b2 74 (00000001 00100101 10110010 01110100) (1957831937)
      4     4        (object header)           65 00 00 00 (01100101 00000000 00000000 00000000) (101)
      8     4        (object header)           9b 1b 01 f8 (10011011 00011011 00000001 11111000) (-134145125)
     12     4    int SimpleInt.state           0
```

HotSpot JVM 将身份哈希码存储为标记字中的“25 b2 74 65”。最高有效字节是 65，因为 JVM 以小端格式存储该值。因此，要恢复十进制的哈希码值(1702146597)，我们必须以相反的顺序读取“25 b2 74 65”字节序列：

```plaintext
65 74 b2 25 = 01100101 01110100 10110010 00100101 = 1702146597
```

### 4.3. 结盟

默认情况下，JVM 会向对象添加足够的填充以使其大小成为 8 的倍数。

例如，考虑SimpleLong类：

```java
public class SimpleLong {
    private long state;
}
```

如果我们解析类布局：

```java
System.out.println(ClassLayout.parseClass(SimpleLong.class).toPrintable());
```

然后 JOL 将打印内存布局：

```plaintext
SimpleLong object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0    12        (object header)                           N/A
     12     4        (alignment/padding gap)                  
     16     8   long SimpleLong.state                          N/A
Instance size: 24 bytes
Space losses: 4 bytes internal + 0 bytes external = 4 bytes total
```

如上图，object header和 long state一共消耗了20个字节。为了使这个大小成为 8 字节的倍数，JVM 添加了 4 字节的填充。

我们还可以通过-XX:ObjectAlignmentInBytes 调整标志更改默认对齐大小 。例如，对于同一个类，带有-XX:ObjectAlignmentInBytes=16 的 内存布局 将是：

```plaintext
SimpleLong object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0    12        (object header)                           N/A
     12     4        (alignment/padding gap)                  
     16     8   long SimpleLong.state                          N/A
     24     8        (loss due to the next object alignment)
Instance size: 32 bytes
Space losses: 4 bytes internal + 8 bytes external = 12 bytes total
```

对象头和 long 变量总共仍然占用 20 个字节。因此，我们应该再添加 12 个字节以使其成为 16 的倍数。

如上所示，它添加了 4 个内部填充字节以在偏移量 16 处开始 long 变量(启用更多对齐访问)。然后它将剩余的 8 个字节添加到 long 变量之后。

### 4.4. 现场包装

当一个类有多个字段时，JVM 可能会以最小化填充浪费的方式分配这些字段。例如，考虑FieldsArrangement类：

```java
public class FieldsArrangement {
    private boolean first;
    private char second;
    private double third;
    private int fourth;
    private boolean fifth;
}
```

字段声明顺序和它们在内存布局中的顺序不同：

```plaintext
OFFSET  SIZE      TYPE DESCRIPTION                               VALUE
      0    12           (object header)                           N/A
     12     4       int FieldsArrangement.fourth                  N/A
     16     8    double FieldsArrangement.third                   N/A
     24     2      char FieldsArrangement.second                  N/A
     26     1   boolean FieldsArrangement.first                   N/A
     27     1   boolean FieldsArrangement.fifth                   N/A
     28     4           (loss due to the next object alignment)
```

这背后的主要动机是尽量减少填充浪费。

### 4.5. 锁定

JVM 还在标记字中维护锁信息。让我们看看实际效果：

```java
public class Lock {}
```

如果我们创建这个类的一个实例，它 的内存布局将是：

```plaintext
Lock object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 
      4     4        (object header)                           00 00 00 00
      8     4        (object header)                           85 23 02 f8
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
```

但是，如果我们在这个实例上同步：

```java
synchronized (lock) {
    System.out.println(ClassLayout.parseInstance(lock).toPrintable());
}
```

内存布局更改为：

```plaintext
Lock object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           f0 78 12 03
      4     4        (object header)                           00 70 00 00
      8     4        (object header)                           85 23 02 f8
     12     4        (loss due to the next object alignment)
```

如上所示，当我们持有监视器锁时，标记字的位模式会发生变化。

### 4.6. 年龄和任期

为了将对象提升到老年代(当然是在分代[GC](https://www.baeldung.com/jvm-garbage-collectors)中)，JVM 需要跟踪每个对象的存活数。如前所述，JVM 也在标记字内部维护此信息。

为了模拟次要 GC，我们将通过将对象分配给volatile 变量来创建大量垃圾 。这样我们就可以防止JIT 编译器可能[消除死代码：](https://www.baeldung.com/java-microbenchmark-harness#dead-code-elimination)

```java
volatile Object consumer;
Object instance = new Object();
long lastAddr = VM.current().addressOf(instance);
ClassLayout layout = ClassLayout.parseInstance(instance);

for (int i = 0; i < 10_000; i++) {
    long currentAddr = VM.current().addressOf(instance);
    if (currentAddr != lastAddr) {
        System.out.println(layout.toPrintable());
    }

    for (int j = 0; j < 10_000; j++) {
        consumer = new Object();
    }

    lastAddr = currentAddr;
}
```

每次活动对象的地址发生变化时，这可能是因为较小的 GC 和幸存者空间之间的移动。对于每次更改，我们还打印新的对象布局以查看老化的对象。

以下是标记字的前 4 个字节如何随时间变化：

```plaintext
09 00 00 00 (00001001 00000000 00000000 00000000)
              ^^^^
11 00 00 00 (00010001 00000000 00000000 00000000)
              ^^^^
19 00 00 00 (00011001 00000000 00000000 00000000)
              ^^^^
21 00 00 00 (00100001 00000000 00000000 00000000)
              ^^^^
29 00 00 00 (00101001 00000000 00000000 00000000)
              ^^^^
31 00 00 00 (00110001 00000000 00000000 00000000)
              ^^^^
31 00 00 00 (00110001 00000000 00000000 00000000)
              ^^^^
```

### 4.7. 虚假分享和@Contended

jdk.internal.vm.annotation.Contended注解(或Java8 上的sun.misc.Contended )提示 JVM 隔离注解字段以避免[错误共享](https://alidg.me/blog/2020/4/24/thread-local-random#false-sharing)。

简而言之， Contended 注解在每个注解字段周围添加了一些填充，以将每个字段隔离在其自己的缓存行上。因此，这将影响内存布局。

为了更好地理解这一点，让我们考虑一个例子：

```java
public class Isolated {

    @Contended
    private int v1;

    @Contended
    private long v2;
}
```

如果我们检查这个类的内存布局，我们会看到类似的东西：

```plaintext
Isolated object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0    12        (object header)                           N/A
     12   128        (alignment/padding gap)                  
    140     4    int Isolated.i                                N/A
    144   128        (alignment/padding gap)                  
    272     8   long Isolated.l                                N/A
Instance size: 280 bytes
Space losses: 256 bytes internal + 0 bytes external = 256 bytes total
```

如上所示，JVM 在每个带注解的字段周围添加了 128 字节的填充。大多数现代机器中的缓存行大小约为 64/128 字节，因此需要 128 字节填充。当然，我们可以 使用-XX:ContendedPaddingWidth 调整标志来控制Contended padding 大小 。

请注意 Contended 注解是 JDK 内部的，因此我们应该避免使用它。

此外，我们应该使用-XX:-RestrictContended 调整标志运行我们的代码；否则，注解不会生效。基本上，默认情况下，此注解仅供内部使用，禁用RestrictContended 将为公共 API 解锁此功能。

### 4.8. 数组

正如我们之前提到的，数组长度也是数组 oop 的一部分。例如，对于包含 3 个元素的布尔数组：

```java
boolean[] booleans = new boolean[3];
System.out.println(ClassLayout.parseInstance(booleans).toPrintable());
```

内存布局如下所示：

```plaintext
[Z object internals:
 OFFSET  SIZE      TYPE DESCRIPTION                               VALUE
      0     4           (object header)                           01 00 00 00 # mark
      4     4           (object header)                           00 00 00 00 # mark
      8     4           (object header)                           05 00 00 f8 # klass
     12     4           (object header)                           03 00 00 00 # array length
     16     3   boolean [Z.<elements>                             N/A
     19     5           (loss due to the next object alignment)
Instance size: 24 bytes
Space losses: 0 bytes internal + 5 bytes external = 5 bytes total
```

在这里，我们有 16 个字节的对象头，其中包含 8 个字节的标记字、4 个字节的类字和 4 个字节的长度。在对象头之后，我们有 3 个字节用于包含3 个元素的布尔 数组。

### 4.9. 压缩引用

到目前为止，我们的示例是在启用了压缩引用的 64 位架构中执行的。

使用 8 字节对齐，我们最多可以使用[32 GB 的堆](https://www.baeldung.com/jvm-compressed-oops#2beyond-32-gb)压缩引用。如果我们超出这个限制甚至手动禁用压缩引用，那么 klass 字将消耗 8 个字节而不是 4 个字节。

让我们看看使用-XX:-UseCompressedOops 调整标志禁用压缩 oops 时同一数组示例的内存布局 ：

```plaintext
[Z object internals:
 OFFSET  SIZE      TYPE DESCRIPTION                               VALUE
      0     4           (object header)                           01 00 00 00 # mark
      4     4           (object header)                           00 00 00 00 # mark
      8     4           (object header)                           28 60 d2 11 # klass
     12     4           (object header)                           01 00 00 00 # klass
     16     4           (object header)                           03 00 00 00 # length
     20     4           (alignment/padding gap)                  
     24     3   boolean [Z.<elements>                             N/A
     27     5           (loss due to the next object alignment)
```

正如承诺的那样，现在 klass 字还有 4 个字节。

## 5.总结

在本教程中，我们了解了 JVM 如何在堆中布置对象和数组。

要进行更详细的探索，强烈建议查看 [JVM](http://hg.openjdk.java.net/jdk8/jdk8/hotspot/file/87ee5ee27509/src/share/vm/oops/)源代码的 oops 部分。此外，[Aleksey Shipilëv](https://shipilev.net/)在这方面有一篇更[深入的文章](https://shipilev.net/jvm/objects-inside-out/)。

此外，更多[的 JOL 示例](https://hg.openjdk.java.net/code-tools/jol/file/tip/jol-samples/src/main/java/org/openjdk/jol/samples/) 作为项目源代码的一部分提供。
## 1. 概述

在本快速教程中，我们将了解 HotSpot JVM 存储数组长度的方式和位置。

通常，运行时数据区的内存布局不是 JVM 规范的一部分，[由实现者自行决定](https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-2.html)。因此，每个 JVM 实现可能有不同的策略来在内存中布局对象和数组。

在本教程中，我们关注一种特定的 JVM 实现：HotSpot JVM。我们也可以互换使用 JVM 和 HotSpot JVM 术语。

## 2. 依赖

为了检查 JVM 中数组的内存布局，我们将使用Java对象布局 ( [JOL](https://openjdk.java.net/projects/code-tools/jol/) ) 工具。因此，我们需要添加 [jol-core](https://search.maven.org/artifact/org.openjdk.jol/jol-core) 依赖：

```xml
<dependency> 
    <groupId>org.openjdk.jol</groupId> 
    <artifactId>jol-core</artifactId>    
    <version>0.10</version> 
</dependency>
```

## 3.数组长度

HotSpot JVM 使用一种称为普通对象指针 ( [OOP](https://github.com/openjdk/jdk15/tree/master/src/hotspot/share/oops) ) 的数据结构来表示指向对象的指针。更具体地说，HotSpot JVM 用称为[arrayOop](https://github.com/openjdk/jdk15/blob/e208d9aa1f185c11734a07db399bab0be77ef15f/src/hotspot/share/oops/arrayOop.hpp#L35)的特殊 OOP 表示数组。每个arrayOop都包含一个包含以下详细信息的对象标头：

-   一个标志字用于存储身份哈希码或GC信息
-   一类词存储通用类元数据
-   4个字节代表数组长度

因此，JVM 将数组长度存储在对象头中。

让我们通过检查数组的内存布局来验证这一点：

```java
int[] ints = new int[42];
System.out.println(ClassLayout.parseInstance(ints).toPrintable());
```

如上所示，我们正在从现有数组实例解析内存布局。以下是 JVM 布局int[]的方式：

```plaintext
[I object internals:
 OFFSET  SIZE   TYPE DESCRIPTION               VALUE
      0     4        (object header)           01 00 00 00 (00000001 00000000 00000000 00000000) (1) # mark
      4     4        (object header)           00 00 00 00 (00000000 00000000 00000000 00000000) (0) # mark
      8     4        (object header)           6d 01 00 f8 (01101101 00000001 00000000 11111000) (-134217363) #klass
     12     4        (object header)           2a 00 00 00 (00101010 00000000 00000000 00000000) (42) # array length
     16   168    int [I.<elements>             N/A
Instance size: 184 bytes
```

如前所述，JVM 将数组长度存储在对象头内部的 mark 和 klass 字之后。此外，数组长度将以 4 个字节存储，因此它不能大于 32 位整数的最大值。

在对象头之后，JVM 存储实际的数组元素。因为我们有一个包含 42 个整数的数组，所以数组的总大小为 168 字节——42 乘以 4。

## 4。总结

在这个简短的教程中，我们了解了 JVM 如何存储数组长度。
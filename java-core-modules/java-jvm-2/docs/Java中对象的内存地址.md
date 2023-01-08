## 1. 概述

在本快速教程中，我们将了解如何在Java中查找对象的内存地址。

在进一步讨论之前，值得一提的是，运行时数据区域的内存布局不是 JVM 规范的一部分，由[实现者自行决定](https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-2.html)。因此，每个 JVM 实现可能有不同的策略来在内存中布局对象和数组。反过来，这将影响内存地址。

在本教程中，我们关注一种特定的 JVM 实现：HotSpot JVM。我们还可以在整个教程中互换使用 JVM 和 HotSpot JVM 术语。

## 2. 依赖

要在 JVM 中查找对象的内存地址，我们将使用Java对象布局 ( [JOL](https://openjdk.java.net/projects/code-tools/jol/) ) 工具。因此，我们需要添加[jol-core](https://search.maven.org/artifact/org.openjdk.jol/jol-core) 依赖：

```xml
<dependency> 
    <groupId>org.openjdk.jol</groupId> 
    <artifactId>jol-core</artifactId>    
    <version>0.10</version> 
</dependency>
```

## 3.内存地址

要在 JVM 中查找特定对象的内存地址，我们可以使用 [addressOf()](https://www.javadoc.io/doc/org.openjdk.jol/jol-core/latest/org/openjdk/jol/vm/VirtualMachine.html#sizeOf-java.lang.Object-) 方法：

```java
String answer = "42";

System.out.println("The memory address is " + VM.current().addressOf(answer));
```

这将打印：

```plaintext
The memory address is 31864981224
```

HotSpot JVM 中有不同的[压缩引用模式](https://shipilev.net/jvm/anatomy-quarks/23-compressed-references/#_compressed_references)。由于这些模式，此值可能不完全准确。因此，我们不应该根据这个地址去执行一些本地内存操作，因为它可能会导致奇怪的内存损坏。

此外，大多数 JVM 实现中的内存地址都会随着 GC 不时移动对象而发生变化。

## 4.身份哈希码

有一种常见的误解，认为 JVM 中对象的内存地址被表示为其默认toString 实现的一部分，例如java.lang.Object@60addb54。也就是说，许多人认为 “60addb54” 是该特定对象的内存地址。

让我们检查一下这个假设：

```java
Object obj = new Object();

System.out.println("Memory address: " + VM.current().addressOf(obj));
System.out.println("toString: " + obj);
System.out.println("hashCode: " + obj.hashCode());
System.out.println("hashCode: " + System.identityHashCode(obj));
```

这将打印以下内容：

```plaintext
Memory address: 31879960584
toString: java.lang.Object@60addb54
hashCode: 1622006612
hashCode: 1622006612
```

非常有趣的是， “60addb54” 是哈希码的十六进制版本，即 1622006612。hashCode [()](https://www.baeldung.com/java-hashcode)方法是所有Java对象的常用方法之一。当我们没有 为类 声明hashCode() 方法时，Java 将使用它的身份哈希码。

如上图，身份哈希码( toString中@后面 的那部分)和内存地址是不同的。

## 5.总结

在这个简短的教程中，我们了解了如何在Java中查找对象的内存地址。
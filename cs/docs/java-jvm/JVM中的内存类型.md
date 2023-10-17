## 1. 概述

在这个简短的教程中，我们将快速概述Java虚拟机([JVM](https://www.baeldung.com/jvm-vs-jre-vs-jdk#jvm))中的内存类型。

JVM为不同的目的使用不同类型的内存，每一种都有自己的特点和行为。了解JVM中不同类型的内存对于设计高效稳定的应用程序非常重要。

## 2. 堆内存

当JVM启动时，它会创建堆内存。**这种内存类型代表了JVM的一个重要组成部分，因为它存储了应用程序创建的所有对象**。

应用程序运行时，内存大小可能会增加或减少。但是，我们可以使用-Xms参数[指定堆内存的初始大小](https://www.baeldung.com/jvm-parameters)：

```shell
java -Xms4096M ClassName
```

此外，我们可以使用-Xmx参数定义最大堆大小：

```shell
java -Xms4096M -Xmx6144M ClassName
```

如果应用程序的堆使用量达到最大大小但仍需要更多内存，则会生成OutOfMemoryError: Java heap space错误。

## 3. 栈内存

在这种内存类型中，JVM存储局部变量和方法信息。

此外，Java使用[栈内存](https://www.baeldung.com/java-stack-heap)来执行线程。在应用程序中，每个线程都有自己的堆栈，用于存储有关其当前使用的方法和变量的信息。

但是，它不是由[垃圾回收](https://www.baeldung.com/jvm-garbage-collectors)管理的，而是由JVM本身管理的。

**栈内存有固定的大小，由JVM在运行时决定**。如果堆栈内存不足，JVM会抛出StackOverflowError错误。

为避免这种潜在问题，必须将应用程序设计为有效使用堆栈内存。

## 4. 本机内存

在Java堆之外分配并由JVM使用的内存称为[本机内存](https://www.baeldung.com/native-memory-tracking-in-jvm)。它也被称为堆外内存。

由于本机内存中的数据在JVM之外，因此我们需要进行序列化来读写数据。性能取决于缓冲区、序列化过程和磁盘空间。

此外，由于它位于JVM之外，垃圾回收器不会释放它。

**在本机内存中，JVM存储线程堆栈、内部数据结构和内存映射文件**。

JVM和本机库使用本机内存来执行无法完全在Java中完成的操作，例如与操作系统交互或访问硬件资源。

## 5. 直接内存

直接缓冲区内存是在Java堆之外分配的。它表示JVM进程使用的操作系统本机内存。

**Java [NIO](https://www.baeldung.com/java-io-vs-nio)使用这种内存类型以更有效的方式将数据写入网络或磁盘**。

因为垃圾回收器不会释放直接缓冲区，因此它们对应用程序内存占用的影响可能并不明显。因此，直接缓冲区应主要分配给I/O操作中使用的大型缓冲区。

要在Java中使用直接缓冲区，我们调用[ByteBuffer](https://www.baeldung.com/java-bytebuffer)上的allocateDirect()方法：

```java
ByteBuffer directBuf = ByteBuffer.allocateDirect(1024);
```

将文件加载到内存中时，Java使用直接内存分配一系列DirectByteBuffer。这样，它减少了复制相同字节的次数。缓冲区有一个类负责在不再需要文件时释放内存。

我们可以使用–XX:MaxDirectMemorySize参数限制直接缓冲区内存大小：

```shell
-XX:MaxDirectMemorySize=1024M
```

如果本机内存将所有专用空间用于直接字节缓冲区，则会发生OutOfMemoryError: Direct buffer memory错误。

## 6. 总结

在这篇简短的文章中，我们了解了JVM中的不同内存类型。为了保证我们应用程序的性能和稳定性，了解JVM中的内存类型是很有帮助的。
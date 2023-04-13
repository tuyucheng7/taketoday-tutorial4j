## 1. 概述

在本教程中，我们将**了解[Spring Boot](https://www.baeldung.com/spring-boot)应用程序使用的默认内存设置**。 

一般情况下，Spring没有任何内存相关的配置，它运行时使用底层Java进程的配置。以下是我们可以配置Java应用程序内存的方法。

## 2. 内存设置

Java进程或JVM的内存分为[堆、栈](https://www.baeldung.com/java-stack-heap)、[元空间](https://matthung0807.blogspot.com/2019/03/about-g1-garbage-collector-permanent.html)、[JIT](https://www.baeldung.com/graal-java-jit-compiler)代码缓存和共享库。

### 2.1 堆

堆是内存中对象所在的部分，直到被垃圾收集器收集为止。

**最小堆的默认值为8Mb或8Mb到1Gb范围内物理内存的1/64**。

对于大于192MB的物理内存，**最大堆的默认值为物理内存的1/4，否则为物理内存的1/2**。

在堆内部，我们有nursery大小限制，当超过该限制时，会导致新一代垃圾回收运行。**它的默认值是特定于平台的**。

我们也有保留区域限制。它是总堆大小的百分比，当达到该百分比时，会导致足够长寿命的对象从年轻代提升到老年代。**它的默认值为25%**。

从Java 8开始，我们还将元空间作为存储所有类元数据的堆的一部分。默认情况下，**它的最小值是平台相关的，最大值不受限制**。

要覆盖最小堆、最大堆和元空间大小的默认值，请参阅这篇[关于配置堆大小](https://www.baeldung.com/spring-boot-heap-size)的帖子。

我们可以使用-Xns参数覆盖nursery大小限制。由于nursery是堆的一部分，因此它的值不应大于-Xmx值：

```shell
java -Xns:10m MyApplication
```

我们还可以使用–XXkeepAreaRatio参数覆盖保留区域限制的默认值。例如，我们可以将其设置为10%：

```shell
java -XXkeepAreaRatio:10 MyApplication
```

最后，这是我们在Linux上检查堆大小的方法：

```shell
java -XX:+PrintFlagsFinal -version | grep HeapSize
```

在Windows上检查堆大小的相同命令是：

```shell
java -XX:+PrintFlagsFinal -version | findstr HeapSize
```

### 2.2 栈

它是提供给每个线程执行的内存量。**它的默认值是特定于平台的**。

因此，我们可以使用-Xss参数定义线程堆栈大小。例如，我们可以将其分配为512kB：

```shell
java -Xss:512k MyApplication
```

然后我们可以在Linux上检查线程堆栈大小：

```shell
java -XX:+PrintFlagsFinal -version | grep ThreadStackSize
```

或者在Windows机器上执行相同的操作：

```shell
java -XX:+PrintFlagsFinal -version | findstr ThreadStackSize
```

## 3. 总结

在本文中，我们了解了可用于Java应用程序的各种堆和堆栈内存配置选项的默认值。

因此，在启动我们的Spring Boot应用程序时，我们可以根据我们的要求定义这些参数。

有关进一步的调整选项，我们确实参考了[官方指南](https://docs.oracle.com/cd/E13150_01/jrockit_jvm/jrockit/geninfo/diagnos/memman.html#:~:text=Since%20all%20Java%20threads%20are,the%20young%20collection%20pause%20times.&text=This%20starts%20up%20the%20JVM%20with%20a%20fixed%20nursery%20size,see%20the%20documentation%20on%20%2DXns%20.)。另外，有关所有配置参数的列表，请参阅此[文档](https://docs.oracle.com/cd/E13150_01/jrockit_jvm/jrockit/jrdocs/refman/optionX.html#wp999527)。
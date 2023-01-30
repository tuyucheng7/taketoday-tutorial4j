## 1. 概述

Java 虚拟机[(JVM)](https://www.baeldung.com/jvm-vs-jre-vs-jdk#jvm)是一种抽象计算机器，它使计算机能够运行 Java 程序。JVM 负责执行编译后的 Java 代码中包含的指令。为此，它需要一定量的内存来存储它需要操作的数据和指令。该内存分为多个区域。

在本教程中，我们将讨论不同类型的运行时数据区及其用途。每个 JVM 实现都必须遵循此处说明[的规范](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.5)。

## 2.共享数据区

JVM 有几个共享数据区，在 JVM 中运行的所有线程之间共享。因此，各种线程可以同时访问这些区域中的任何一个。

### 2.1. 堆

[Heap](https://www.baeldung.com/java-stack-heap#heap-space-in-java)是运行时的数据区，所有的Java对象都存放在这里。因此，每当我们创建一个新的类实例或数组时，JVM 都会在堆中找到一些可用的内存并将其分配给对象。堆的创建发生在 JVM 启动时，它的销毁发生在退出时。

根据规范，自动管理工具必须处理对象的存储：这个工具被称为[垃圾收集器](https://www.baeldung.com/java-destructor#garbage-collection)。

JVM 规范中没有关于堆大小的限制。内存处理也留给了 JVM 实现。然而，如果垃圾收集器无法回收足够的可用空间来创建新对象，则 JVM 会抛出OutOfMemory错误。

### 2.2. 方法区

方法区是JVM中的一个共享数据区，用于存放类和接口的定义。它在 JVM 启动时创建，只有在 JVM 退出时才被销毁。

具体来说，[类加载器](https://www.baeldung.com/java-classloaders)加载类的字节码并将其传递给 JVM。然后 JVM 创建该类的内部表示，用于在运行时创建对象和调用方法。这种内部表示收集有关类和接口的字段、方法和构造函数的信息。

此外，让我们指出方法区是一个逻辑概念。因此，它可能是具体 JVM 实现中堆的一部分。

再一次，JVM 规范没有定义方法区的大小，也没有定义 JVM 处理内存块的方式。

如果方法区中的可用空间不足以加载新类，JVM 会抛出[OutOfMemory](https://www.baeldung.com/java-permgen-space-error)错误。

### 2.3. 运行时常量池

[运行时常量池](https://www.baeldung.com/jvm-constant-pool)是方法区中的一个区域，它包含对类和接口名称、字段名称和方法名称的符号引用。

JVM 利用在方法区中为类或接口创建表示的同时为此类创建运行时常量池。

创建运行时常量池时，如果 JVM 需要的内存多于方法区中可用的内存，则会抛出 OutOfMemory错误。

## 3. 每线程数据区

除了共享运行时数据区之外，JVM 还使用每线程数据区来存储每个线程的特定数据。JVM 确实支持同时执行多个线程。

### 3.1. 电脑注册

每个 JVM 线程都有其[PC(程序计数器)寄存器](https://www.baeldung.com/cs/os-program-counter-vs-instruction-register)。每个线程在任何给定时间执行单个方法的代码。PC 的行为取决于方法的性质：

-   对于非本地方法，PC 寄存器存储当前正在执行的指令的地址
-   对于本地方法，PC 寄存器具有未定义的值

最后，让我们注意 PC 寄存器生命周期与其底层线程之一相同。

### 3.2. JVM堆栈

同样，每个 JVM 线程都有其私有的[Stack](https://www.baeldung.com/java-stack-heap#stack-memory-in-java)。JVM Stack是一种存储方法调用信息的数据结构。每个方法调用都会触发在堆栈上创建一个新帧来存储方法的局部变量和返回地址。这些帧可以存储在堆中。

多亏了 JVM 堆栈，JVM 可以跟踪程序的执行并按需记录[堆栈跟踪。](https://www.baeldung.com/java-get-current-stack-trace)

再一次，JVM 规范让 JVM 实现决定它们要如何处理 JVM 堆栈的大小和内存分配。

JVM 堆栈上的内存分配错误会[引发 StackOverflow](https://www.baeldung.com/java-stack-overflow-error)错误。但是，如果 JVM 实现允许动态扩展其 JVM 堆栈的大小，并且如果在扩展期间发生内存错误，则 JVM 必须抛出OutOfMemory错误。

### 3.3. 本机方法堆栈

本[机方法](https://www.baeldung.com/java-native#native-methods)是用 Java 以外的另一种编程语言编写的方法。这些方法未编译为字节码，因此需要不同的内存区域。

本机方法堆栈与 JVM 堆栈非常相似，但仅专用于本机方法。

本机方法栈的目的是跟踪本机方法的执行。

JVM 实现可以自行决定本地方法栈的大小以及它如何处理内存块。

至于 JVM Stack，Native Method Stack 上的内存分配错误会导致StackOverflow错误。另一方面，增加本机方法堆栈大小的失败尝试会导致OutOfMemory错误。

最后，让我们注意规范强调 JVM 实现可以决定不支持本地方法调用：这样的实现不需要实现本地方法栈。

## 4。总结

在本教程中，我们详细介绍了不同类型的运行时数据区域及其用途。这些区域对于 JVM 的正常运行至关重要。了解它们有助于优化 Java 应用程序的性能。
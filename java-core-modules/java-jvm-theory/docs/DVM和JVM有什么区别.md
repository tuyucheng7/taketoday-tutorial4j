## 1. 概述

在本文中，我们将探讨[Java 虚拟机 (JVM)](https://docs.oracle.com/javase/specs/index.html)和[Dalvik 虚拟机 (DVM)](https://source.android.com/devices/tech/dalvik)之间的差异。我们将首先快速浏览一下它们中的每一个，然后进行比较。

请注意，从 Android 5.0 开始，Dalvik 虚拟机已被 Android Runtime (ART) 取代。

## 2. 什么是运行时？

运行时系统提供了一个环境，可以将用 Java 等高级语言编写的代码翻译成中央处理器 (CPU) 可以理解的机器代码。

我们可以区分这些类型的翻译器：

-   汇编程序：他们直接将汇编代码翻译成机器代码，所以速度很快
-   编译器：他们将代码翻译成汇编代码，然后使用汇编器将生成的代码翻译成二进制代码。使用这种技术很慢，但执行速度很快。此外，生成的机器代码依赖于平台
-   解释器：他们在执行代码时翻译代码。由于翻译发生在运行时，执行速度可能会很慢

## 3.Java虚拟机

[JVM 是运行 Java](https://www.baeldung.com/jvm-languages)桌面、服务器和 Web 应用程序的虚拟机。关于 Java 的另一个重要的事情是它是在考虑可移植性的情况下开发的。因此，JVM 也被设计为支持多主机架构并随处运行。但是，它对于嵌入式设备来说太重了。

Java有一个活跃的社区，未来会继续被广泛使用。此外，HotSpot 是 JVM 参考实现。此外，开源社区还维护了五个以上的其他实现。

通过基于节奏的新版本，Java 和 JVM 每六个月接收一次新更新。例如，我们可以为下一个版本列出一些建议，例如[Foreign-Memory Access](https://openjdk.java.net/jeps/383)和[Packaging Tool](https://openjdk.java.net/jeps/343)。

## 4. Dalvik 虚拟机

DVM 是运行 Android 应用程序的虚拟机。DVM 执行 Dalvik 字节码，它是从用 Java 语言编写的程序编译而来的。请注意，DVM 不是 JVM。

DVM 的关键设计原则之一是它应该在低内存移动设备上运行，并且与任何 JVM 相比加载速度更快。此外，此 VM 在同一设备上运行多个实例时效率更高。

2014 年，Google 发布了适用于 Android 5 的[Android Runtime (ART)](https://source.android.com/devices/tech/dalvik#features)，它取代了 Dalvik 以提高应用程序性能和电池使用率。最后一个版本是 Android 4.4 上的 1.6.0。

## 5. JVM 和 DVM 的区别

### 5.1. 建筑学

[JVM 是一个基于堆栈的 VM](https://www.baeldung.com/jvm-vs-jre-vs-jdk#jvm)，其中所有算术和逻辑操作都是通过 push 和 pop 操作数执行的，结果存储在堆栈中。栈也是存放方法的数据结构。

相比之下，DVM 是一个基于寄存器的 VM。这些位于 CPU 中的寄存器执行所有算术和逻辑运算。寄存器是存放操作数的数据结构。

### 5.2. 汇编

Java 代码在 JVM 内部编译为称为[Java 字节码](https://www.baeldung.com/java-class-view-bytecode)(.class 文件)的中间格式。然后，JVM解析生成的 Java 字节码并将其翻译成机器码。

在 Android 设备上，DVM 像 JVM 一样将 Java 代码编译成称为 Java 字节码(.class 文件)的中间格式。然后，借助名为Dalvik eXchange 或 dx 的工具，它将 Java 字节码转换为 Dalvik 字节码。最后，DVM 将 Dalvik 字节码翻译成二进制机器码。

两个虚拟机都使用[即时](https://www.baeldung.com/graal-java-jit-compiler)[( ](https://www.baeldung.com/graal-java-jit-compiler)[JIT ](https://www.baeldung.com/graal-java-jit-compiler)[) 编译器](https://www.baeldung.com/graal-java-jit-compiler)。JIT 编译器是一种在运行时执行编译的编译器。

### 5.3. 表现

如前所述，JVM 是基于堆栈的 VM，而 DVM 是基于寄存器的 VM。基于堆栈的 VM 字节码非常紧凑，因为操作数的位置隐式位于操作数堆栈上。基于寄存器的 VM 字节码要求所有隐式操作数都是指令的一部分。这表明基于寄存器的代码大小通常比基于堆栈的字节码大得多。

另一方面，基于寄存器的 VM 可以使用比相应的基于堆栈的 VM 更少的 VM 指令来表达计算。调度 VM 指令的成本很高，因此减少执行的 VM 指令可能会显着提高基于寄存器的 VM 的速度。

当然，这种区别仅在以解释模式运行 VM 时才有意义。

### 5.4. 执行

虽然可以为每个正在运行的应用程序设置一个 JVM 实例，但通常我们只会配置一个具有共享进程和内存空间的 JVM 实例来运行我们已部署的所有应用程序。

然而，Android 被设计为运行多个 DVM 实例。因此，为了运行应用程序或服务，Android 操作系统在共享内存空间中创建一个具有独立进程的新 DVM 实例，并部署代码来运行应用程序。

## 六，总结

在本教程中，我们介绍了 JVM 和 DVM 之间的主要区别。两种 VM 都运行用 Java 编写的应用程序，但它们使用不同的技术和过程来编译和运行代码。
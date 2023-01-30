## 1. 概述

在本快速教程中，我们将了解如何在 HotSpot JVM 中配置线程堆栈大小。

## 2. 默认筹码量

每个 JVM 线程都有一个私有的本机堆栈，用于存储[调用堆栈](https://www.baeldung.com/cs/call-stack)信息、局部变量和部分结果。因此，堆栈在方法调用中起着至关重要的作用。这是[JVM 规范](https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-2.html#jvms-2.5.2)的一部分，因此，每个 JVM 实现都在使用堆栈。

但是，其他实现细节(例如堆栈大小)是特定于实现的。从现在开始，我们将重点关注 HotSpot JVM，并将交替使用术语 JVM 和 HotSpot JVM。

无论如何，JVM 将在创建拥有线程的同时创建堆栈。

如果我们不指定堆栈的大小，JVM 将创建一个具有默认大小的堆栈。通常，此默认大小取决于操作系统和计算机体系结构。例如，这些是 Java 14 的一些默认大小：

-   [Linux/x86(64 位)](https://github.com/openjdk/jdk14u/blob/7a3bf58b8ad2c327229a94ae98f58ec96fa39332/src/hotspot/os_cpu/linux_x86/globals_linux_x86.hpp#L34)：1 MB
-   [macOS(64 位)](https://github.com/openjdk/jdk14u/blob/7a3bf58b8ad2c327229a94ae98f58ec96fa39332/src/hotspot/os_cpu/bsd_x86/globals_bsd_x86.hpp#L35)：1 MB
-   [Oracle Solaris(64 位)](https://github.com/openjdk/jdk14u/blob/7a3bf58b8ad2c327229a94ae98f58ec96fa39332/src/hotspot/os_cpu/solaris_x86/globals_solaris_x86.hpp#L34)：1 MB
-   在 Windows 上，JVM 使用[系统范围的堆栈大小](https://github.com/openjdk/jdk14u/blob/7a3bf58b8ad2c327229a94ae98f58ec96fa39332/src/hotspot/os_cpu/windows_x86/globals_windows_x86.hpp#L37)

基本上，在大多数现代操作系统和体系结构中，我们可以预期每个堆栈大约有 1 MB。

## 3.自定义筹码量

要更改堆栈大小，我们可以使用 -Xss 调整 标志。例如， -Xss1048576 将堆栈大小设置为 1 MB：

```bash
java -Xss1048576 // omitted
```

如果我们不想以字节为单位计算大小，我们可以使用一些方便的快捷方式来指定不同的单位——字母k 或K 表示 KB，m 或M表示 MB，g 或G表示 GB。例如，让我们看看将堆栈大小设置为 1 MB 的几种不同方法：

```plaintext
-Xss1m 
-Xss1024k
```

与 -Xss类似，我们也可以使用 -XX:ThreadStackSize 调整标志来配置堆栈大小。但是， -XX:ThreadStackSize的语法 有点不同。我们应该用等号分隔大小和标志名称：

```bash
java -XX:ThreadStackSize=1024 // omitted
```

HotSpot JVM[不允许我们使用小于最小值的大小](https://github.com/openjdk/jdk14u/blob/03db2e14dde027eb5dae1435bc9b7f95b1fb48df/src/hotspot/os/posix/os_posix.cpp#L1397)：

```bash
$ java -Xss1K -version
The Java thread stack size specified is too small. Specify at least 144k
Error: Could not create the Java Virtual Machine.
Error: A fatal exception has occurred. Program will exit.
```

此外，它[不允许我们使用超过最大值](https://github.com/openjdk/jdk14u/blob/7a3bf58b8ad2c327229a94ae98f58ec96fa39332/src/hotspot/share/runtime/arguments.cpp#L2413)(通常为 1 GB)的大小：

```bash
$ java -Xss2g -version
Invalid thread stack size: -Xss2g
The specified size exceeds the maximum representable size.
Error: Could not create the Java Virtual Machine.
Error: A fatal exception has occurred. Program will exit.
```

## 4。总结

在这个快速教程中，我们了解了如何在 HotSpot JVM 中配置线程堆栈大小。
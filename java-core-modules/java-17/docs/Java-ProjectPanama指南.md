## 一、概述

在本教程中，我们将浏览[Project Panama](https://openjdk.java.net/projects/panama/)组件。我们将首先探索[Foreign Function 和 Memory API](https://openjdk.org/jeps/424)。然后，我们将了解[JExtract](https://jdk.java.net/jextract/)工具如何促进其使用。

## 2. 什么是巴拿马项目？

[巴拿马项目](https://openjdk.java.net/projects/panama/)旨在简化 Java 与外部（非 Java）API 之间的交互，即用 C、C++ 等编写的本机代码。

到目前为止，使用[Java 本机接口 (JNI)](https://www.baeldung.com/jni)是从 Java 调用外部函数的解决方案。但是[JNI 有一些缺点](https://www.baeldung.com/jni#disadvantages) ，巴拿马项目通过以下方式解决了这些缺点：

-   无需在 Java 中编写中间本机代码包装器
-   用更面向未来的 Memory API替换[ByteBuffer API](https://www.baeldung.com/java-bytebuffer)
-   引入一种与平台无关、安全且内存高效的方法来从 Java 调用本机代码

为实现其目标，Panama 提供了一组 API 和工具：

-   [Foreign-Function and Memory API](https://openjdk.org/jeps/424)：用于分配和访问堆外内存以及直接从 Java 代码调用外部函数
-   [Vector API](https://openjdk.org/jeps/426)：使高级开发人员能够用 Java 表达复杂的数据并行算法
-   [JExtract](https://jdk.java.net/jextract/)：一种从一组本机标头中机械地派生 Java 绑定的工具

## 3.先决条件

要使用外部函数和内存 API，让我们下载[Project Panama Early-Access Build](https://jdk.java.net/panama/)。在撰写本文时，我们使用的是*Build 19-panama+1-13 (2022/1/18)*。接下来，我们[根据使用的系统](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)[设置*JAVA_HOME*](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)。

由于 Foreign Function 和 Memory API 是[预览 API](https://openjdk.java.net/jeps/12)，我们必须在启用预览功能的情况下编译和运行我们的代码，即通过将*–enable-preview*标志添加到*java*和*javac。*

## 4. 外部函数和内存API

外部函数和内存 API 帮助 Java 程序与 Java 运行时之外的代码和数据进行互操作。

它通过有效地调用外部函数（即 JVM 外部的代码）和安全地访问外部内存（即不受 JVM 管理的内存）来实现这一点。

它结合了两个早期的孵化 API：[外部内存访问 API](https://openjdk.java.net/jeps/393)和[外部链接器 API](https://openjdk.java.net/jeps/389)。

API 提供了一组类和接口来执行这些操作：

-   *使用MemorySegment*、 *MemoryAddress*和 *SegmentAllocator*分配外部内存
-   *通过MemorySession*控制外来内存的分配和释放
-   *使用MemoryLayout*操作结构化外部内存
-   [通过VarHandles](https://www.baeldung.com/java-variable-handles)访问结构化外部内存
-   借助*Linker*、 *FunctionDescriptor*和 *SymbolLookup调用外部函数*

### 4.1. 外部内存分配

首先，让我们探讨一下内存分配。在这里，主要的抽象是*MemorySegment*。它模拟位于堆外或堆上的连续内存区域。*MemoryAddress*是段内的偏移量。简单的说，一个内存段是由内存地址组成的，一个内存段可以包含其他的内存段。

此外，内存段绑定到它们封装的*MemorySession*并在不再需要时释放。MemorySession管理段的生命周期并确保它们在被多个线程访问时被正确释放*。*

让我们在内存段中的连续偏移处创建四个*字节*，然后将浮点值设置为*6*：

```java
try (MemorySession memorySession = MemorySession.openConfined()) {
    int byteSize = 4;
    int index = 0;
    float value = 6;
    MemorySegment segment = MemorySegment.allocateNative(byteSize, memorySession);
    segment.setAtIndex(JAVA_FLOAT, index, value);
    float result = segment.getAtIndex(JAVA_FLOAT, index);
    System.out.println("Float value is:" + result);
 }复制
```

在我们上面的代码中，*受限* 内存会话限制对创建会话的线程的访问，而 *共享*内存会话允许从任何线程进行访问。

此外，*JAVA_FLOAT ValueLayout*指定取消引用操作的属性：类型映射的正确性和要取消引用的字节数*。*

SegmentAllocator抽象定义了有用的操作来分配和*初始化*内存段。当我们的代码管理大量堆外段时，它会非常有用：

```java
String[] greetingStrings = {"hello", "world", "panama", "baeldung"};
SegmentAllocator allocator = SegmentAllocator.implicitAllocator();
MemorySegment offHeapSegment = allocator.allocateArray(ValueLayout.ADDRESS, greetingStrings.length);
for (int i = 0; i < greetingStrings.length; i++) {
    // Allocate a string off-heap, then store a pointer to it
    MemorySegment cString = allocator.allocateUtf8String(greetingStrings[i]);
    offHeapSegment.setAtIndex(ValueLayout.ADDRESS, i, cString);
}复制
```

### 4.2. 外部记忆操作

接下来，我们深入研究内存布局的内存操作。MemoryLayout*描述*段的内容。它对操作本机代码的高级数据结构很有用，例如*struct* s、指针和指向*struct* s 的指针。

让我们使用*GroupLayout*在堆外分配一个表示具有*x*和*y坐标的点的*[C*结构*](https://www.w3schools.com/c/c_structs.php)：

```java
GroupLayout pointLayout = structLayout(
    JAVA_DOUBLE.withName("x"),
    JAVA_DOUBLE.withName("y")
);

VarHandle xvarHandle = pointLayout.varHandle(PathElement.groupElement("x"));
VarHandle yvarHandle = pointLayout.varHandle(PathElement.groupElement("y"));

try (MemorySession memorySession = MemorySession.openConfined()) {
    MemorySegment pointSegment = memorySession.allocate(pointLayout);
    xvarHandle.set(pointSegment, 3d);
    yvarHandle.set(pointSegment, 4d);
    System.out.println(pointSegment.toString());
}复制
```

值得注意的是，不需要计算偏移量，因为不同的[VarHandle](https://www.baeldung.com/java-variable-handles)[*用于*](https://www.baeldung.com/java-variable-handles)初始化每个点坐标。

我们还可以使用 *SequenceLayout 构造数据数组。*以下是获取五点列表的方法*：*

```java
SequenceLayout ptsLayout = sequenceLayout(5, pointLayout);复制
```

### 4.3. 来自 Java 的本机函数调用

Foreign Function API 允许 Java 开发人员在不依赖第三方包装器的情况下使用任何本机库。它严重依赖[方法句柄](https://www.baeldung.com/java-method-handles) 并提供三个主要类：*Linker*、 *FunctionDescriptor*和 *SymbolLookup*。

让我们考虑通过调用 C *printf()函数来打印“* *Hello world* ”消息：

```c
#include <stdio.h>
int main() {
    printf("Hello World from Project Panama Baeldung Article");
    return 0;
}复制
```

首先，我们在标准库的类加载器中查找函数：

```java
Linker nativeLinker = Linker.nativeLinker();
SymbolLookup stdlibLookup = nativeLinker.defaultLookup();
SymbolLookup loaderLookup = SymbolLookup.loaderLookup();复制
```

链接*器* 是两个二进制接口之间的桥梁：JVM 和 C/C++ 本机代码，也称为 [C ABI](https://en.wikipedia.org/wiki/Application_binary_interface)。

下面，我们需要描述函数原型：

```java
FunctionDescriptor printfDescriptor = FunctionDescriptor.of(JAVA_INT, ADDRESS);复制
```

值布局*JAVA_INT*和*ADDRESS*分别对应 C *printf()*函数的返回类型和输入：

```c
int printf(const char * __restrict, ...)复制
```

接下来，我们得到[方法句柄](https://www.baeldung.com/java-method-handles)：

```java
String symbolName = "printf";
String greeting = "Hello World from Project Panama Baeldung Article";
MethodHandle methodHandle = loaderLookup.lookup(symbolName)
  .or(() -> stdlibLookup.lookup(symbolName))
  .map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, printfDescriptor))
  .orElse(null);复制复制
```

链接器接口支持向下调用（从 Java 代码调用本机代码）和向上调用（从本机代码调用回 Java 代码）。最后，我们调用本机函数：

```java
try (MemorySession memorySession = MemorySession.openConfined()) {
     MemorySegment greetingSegment = memorySession.allocateUtf8String(greeting);
     methodHandle.invoke(greetingSegment);
}复制
```

## 5.J提取

使用 JExtract，无需直接使用大部分外部函数和内存 API 抽象。让我们重新打印上面的“ *Hello World”*示例。

首先，我们需要从标准库头文件生成 Java 类：

```
jextract --source --output src/main -t foreign.c -I c:\mingw\include c:\mingw\include\stdio.h
```

*stdio*的路径必须更新为目标操作系统中的路径。接下来，我们可以简单地从 Java 中*导入*本机*printf()函数：*

```java
import static foreign.c.stdio_h.printf;

public class Greetings {

    public static void main(String[] args) {
        String greeting = "Hello World from Project Panama Baeldung Article, using JExtract!";
        try (MemorySession memorySession = MemorySession.openConfined()) {
            MemorySegment greetingSegment = memorySession.allocateUtf8String(greeting);
            printf(greetingSegment);
        }
    }
}复制
```

运行代码将问候语打印到控制台：

```apache
java --enable-native-access=ALL-UNNAMED --enable-preview --source 19 .\src\main\java\com\baeldung\java\panama\jextract\Greetings.java复制
```

## 六，结论

在本文中，我们了解了 Project Panama 的主要功能。

首先，我们探索了使用外部函数和内存 API 进行本机内存管理。*然后我们使用MethodHandles*调用外部函数。最后，我们使用 JExtract 工具来隐藏 Foreign Function 和 Memory API 的复杂性。

Project Panama 还有很多值得探索的地方，特别是从本机代码调用 Java、调用第三方库和[Vector API](https://openjdk.org/jeps/426)。
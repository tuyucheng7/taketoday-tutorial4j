## 1. 概述

在本教程中，我们将了解如何使用JavaNative Access 库(简称 JNA)访问本地库，而无需编写任何[JNI(Java Native Interface)](https://www.baeldung.com/jni)代码。

## 2. 为什么选择 JNA？

多年来，Java 和其他基于 JVM 的语言在很大程度上实现了其“一次编写，到处运行”的座右铭。然而，有时我们需要使用原生代码来实现一些功能：

-   重用以 C/C++ 或任何其他能够创建本机代码的语言编写的遗留代码
-   访问标准Java运行时中不可用的系统特定功能
-   针对给定应用程序的特定部分优化速度和/或内存使用。

最初，这种要求意味着我们不得不求助于 JNI –JavaNative Interface。虽然有效，但这种方法有其缺点，并且由于以下几个问题通常被避免：

-   要求开发人员编写 C/C++“胶水代码”以桥接Java和本机代码
-   需要适用于每个目标系统的完整编译和链接工具链
-   将值编组和解组进出 JVM 是一项乏味且容易出错的任务
-   混合使用Java和本机库时的法律和支持问题

JNA 开始解决与使用 JNI 相关的大部分复杂性。特别是，无需创建任何 JNI 代码即可使用位于动态库中的本机代码，这使得整个过程更加容易。

当然，有一些权衡：

-   我们不能直接使用静态库
-   与手工编写的 JNI 代码相比速度较慢

不过，对于大多数应用程序而言，JNA 的简单性优势远远超过这些劣势。因此，可以公平地说，除非我们有非常具体的要求，否则今天的 JNA 可能是从Java或任何其他基于 JVM 的语言访问本机代码的最佳选择。

## 3. JNA 项目设置

要使用 JNA，我们要做的第一件事是将其依赖项添加到我们项目的pom.xml中：

```xml
<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna-platform</artifactId>
    <version>5.6.0</version>
</dependency>

```

最新版本的 [jna-platform](https://search.maven.org/search?q=g:net.java.dev.jna a:jna-platform)可以从 Maven Central 下载。

## 4.使用JNA

使用 JNA 是一个两步过程：

-   首先，我们创建一个扩展JNA的[Library](https://java-native-access.github.io/jna/5.6.0/javadoc/com/sun/jna/Library.html)接口的Java接口，用于描述调用目标本地代码时使用的方法和类型
-   接下来，我们将这个接口传递给 JNA，它返回我们用来调用本地方法的这个接口的具体实现

### 4.1. 从 C 标准库调用方法

对于我们的第一个示例，让我们使用 JNA 从标准 C 库中调用 [cosh](https://man7.org/linux/man-pages/man3/cosh.3.html)函数，该库在大多数系统中都可用。此方法采用双参数并计算其[双曲余弦](https://en.wikipedia.org/wiki/Hyperbolic_functions)。AC程序只要包含<math.h>头文件就可以使用这个函数：

```cpp
#include <math.h>
#include <stdio.h>
int main(int argc, char argv) {
    double v = cosh(0.0);
    printf("Result: %fn", v);
}
```

让我们创建调用此方法所需的Java接口：

```java
public interface CMath extends Library { 
    double cosh(double value);
}

```

接下来，我们使用 JNA 的 [Native](https://java-native-access.github.io/jna/5.6.0/javadoc/com/sun/jna/Native.html)类创建此接口的具体实现，以便调用我们的 API：

```java
CMath lib = Native.load(Platform.isWindows()?"msvcrt":"c", CMath.class);
double result = lib.cosh(0);

```

这里真正有趣的部分是对 load() 方法的调用。它有两个参数：动态库名称和描述我们将使用的方法的Java接口。它返回这个接口的具体实现，允许我们调用它的任何方法。

现在，动态库名称通常是系统相关的，C 标准库也不例外：在大多数基于 Linux 的系统中是libc.so ，但 在 Windows 中是msvcrt.dll 。这就是我们使用 JNA 中包含的Platform助手类来检查我们在哪个平台上运行并选择正确的库名称的原因。

请注意，我们不必添加.so或.dll 扩展名，因为它们是隐含的。此外，对于基于 Linux 的系统，我们不需要指定共享库的标准前缀“lib”。

由于从Java的角度来看动态库的行为类似于[单例](https://www.baeldung.com/java-singleton)，因此通常的做法是将INSTANCE字段声明为接口声明的一部分：

```java
public interface CMath extends Library {
    CMath INSTANCE = Native.load(Platform.isWindows() ? "msvcrt" : "c", CMath.class);
    double cosh(double value);
}

```

### 4.2. 基本类型映射

在我们的初始示例中，被调用方法仅使用基本类型作为其参数和返回值。JNA 自动处理这些情况，通常在从 C 类型映射时使用它们的自然Java对应物：

-   字符 => 字节
-   短 => 短
-   wchar_t => 字符
-   整数 => 整数
-   长 => com.sun.jna.NativeLong
-   长长 => 长
-   浮动 => 浮动
-   双 => 双
-   字符  => 字符串

一个可能看起来很奇怪的映射是用于本机long类型的映射。这是因为，在 C/C++ 中， long类型可能表示 32 位或 64 位值，这取决于我们是在 32 位还是 64 位系统上运行。

为了解决这个问题，JNA 提供了NativeLong类型，它根据系统的体系结构使用适当的类型。

### 4.3. 结构和联合

另一个常见的场景是处理需要指向某些 结构或联合类型的指针的本机代码 API 。在创建访问它的Java接口时，相应的参数或返回值必须分别是扩展Structure 或 Union的Java类型。

例如，给定这个 C 结构：

```cpp
struct foo_t {
    int field1;
    int field2;
    char field3;
};
```

它的Java对等类是：

```java
@FieldOrder({"field1","field2","field3"})
public class FooType extends Structure {
    int field1;
    int field2;
    String field3;
};
```

JNA 需要@FieldOrder注解，以便它可以在将数据用作目标方法的参数之前将数据正确地序列化到内存缓冲区中。

或者，我们可以覆盖getFieldOrder()方法以获得相同的效果。当针对单一架构/平台时，前一种方法通常就足够了。我们可以使用后者来处理跨平台的对齐问题，这有时需要添加一些额外的填充字段。

工会的工作方式类似，除了几点：

-   无需使用@FieldOrder注解或实现getFieldOrder()
-   我们必须 在调用本机方法之前调用setType()

让我们看看如何用一个简单的例子来做到这一点：

```java
public class MyUnion extends Union {
    public String foo;
    public double bar;
};

```

现在，让我们将MyUnion与一个假设的库一起使用：

```java
MyUnion u = new MyUnion();
u.foo = "test";
u.setType(String.class);
lib.some_method(u);

```

如果 foo和bar where 的类型相同，我们必须改用字段的名称：

```java
u.foo = "test";
u.setType("foo");
lib.some_method(u);
```

### 4.4. 使用指针

JNA 提供了一个 [Pointer](https://java-native-access.github.io/jna/5.6.0/javadoc/com/sun/jna/Pointer.html)抽象，有助于处理用无类型指针声明的 API——通常是void 。此类提供允许对底层本机内存缓冲区进行读写访问的方法，这具有明显的风险。

在开始使用这个类之前，我们必须确保清楚地了解每次谁“拥有”所引用的内存。如果不这样做，可能会产生难以调试的与内存泄漏和/或无效访问相关的错误。

假设我们知道我们在做什么(一如既往)，让我们看看如何将众所周知的malloc()和free()函数与 JNA 一起使用，用于分配和释放内存缓冲区。首先，让我们再次创建我们的包装器接口：

```java
public interface StdC extends Library {
    StdC INSTANCE = // ... instance creation omitted
    Pointer malloc(long n);
    void free(Pointer p);
}

```

现在，让我们用它来分配缓冲区并使用它：

```java
StdC lib = StdC.INSTANCE;
Pointer p = lib.malloc(1024);
p.setMemory(0l, 1024l, (byte) 0);
lib.free(p);

```

setMemory ()方法只是用一个常量字节值(在本例中为零)填充底层缓冲区。请注意， Pointer实例不知道它指向什么，更不用说它的大小了。这意味着我们可以很容易地使用它的方法破坏我们的堆。

稍后我们将看到如何使用 JNA 的崩溃保护功能来减少此类错误。

### 4.5. 处理错误

标准 C 库的旧版本使用全局errno变量来存储特定调用失败的原因。例如，这是一个典型的 open()调用如何在 C 中使用这个全局变量：

```cpp
int fd = open("some path", O_RDONLY);
if (fd < 0) {
    printf("Open failed: errno=%dn", errno);
    exit(1);
}
```

当然，在现代的多线程程序中，这段代码是行不通的，对吧？好吧，多亏了 C 的预处理器，开发人员仍然可以编写这样的代码，而且它会工作得很好。事实证明，如今， errno是一个扩展为函数调用的宏：

```cpp
// ... excerpt from bits/errno.h on Linux
#define errno (__errno_location ())

// ... excerpt from <errno.h> from Visual Studio
#define errno (_errno())
```

现在，这种方法在编译源代码时工作正常，但在使用 JNA 时就没有这样的事情了。我们可以在包装器接口中声明扩展函数并显式调用它，但 JNA 提供了更好的替代方法：[LastErrorException](https://java-native-access.github.io/jna/5.6.0/javadoc/com/sun/jna/LastErrorException.html)。

在带有throws LastErrorException的包装器接口中声明的任何方法都将在本机调用后自动包含错误检查。如果报告错误，JNA 将抛出LastErrorException，其中包括原始错误代码。

让我们向之前使用的StdC包装器接口添加几个方法 来展示此功能：

```java
public interface StdC extends Library {
    // ... other methods omitted
    int open(String path, int flags) throws LastErrorException;
    int close(int fd) throws LastErrorException;
}

```

现在，我们可以 在 try/catch 子句中使用open() ：

```java
StdC lib = StdC.INSTANCE;
int fd = 0;
try {
    fd = lib.open("/some/path",0);
    // ... use fd
}
catch (LastErrorException err) {
    // ... error handling
}
finally {
    if (fd > 0) {
       lib.close(fd);
    }
}

```

在 catch块中，我们可以使用LastErrorException.getErrorCode()获取原始errno值并将其用作错误处理逻辑的一部分。

### 4.6. 处理访问冲突

如前所述，JNA 不会保护我们不滥用给定的 API，尤其是在处理来回传递的本地代码的内存缓冲区时。在正常情况下，此类错误会导致访问冲突并终止 JVM。

JNA 在某种程度上支持一种允许Java代码处理访问冲突错误的方法。有两种激活方式：

-   将 jna.protected系统属性设置为 true
-   调用 Native.setProtected(true)

一旦我们激活了这种保护模式，JNA 将捕获通常会导致崩溃的访问冲突错误并抛出java.lang.Error异常。我们可以使用 用无效地址初始化的指针并尝试向其写入一些数据来验证这是否有效：

```java
Native.setProtected(true);
Pointer p = new Pointer(0l);
try {
    p.setMemory(0, 1001024, (byte) 0);
}
catch (Error err) {
    // ... error handling omitted
}

```

但是，正如文档所述，此功能只能用于调试/开发目的。

## 5.总结

在本文中，我们展示了与 JNI 相比如何使用 JNA 轻松访问本机代码。
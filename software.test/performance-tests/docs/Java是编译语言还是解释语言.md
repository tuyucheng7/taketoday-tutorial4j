## 1. 概述

编程语言根据其抽象级别进行分类。我们区分高级语言(Java、Python、JavaScript、C++、Go)、低级语言(汇编程序)，最后是机器代码。

**每个高级语言代码，如Java，都需要翻译成机器本机代码才能执行**。这个翻译过程可以是编译，也可以是解释。但是，还有第三种选择。寻求利用这两种方法的组合。

在本教程中，我们将探讨如何在多个平台上编译和执行Java代码。我们将了解一些Java和JVM设计细节。这些将帮助我们确定Java是编译型、解释型还是两者的混合。

## 2. 编译与解释

让我们首先研究[编译型和解释型编程语言](https://www.baeldung.com/cs/compiled-vs-interpreted-languages)之间的一些基本差异。

### 2.1 编译型语言

编译语言(C++、Go)通过编译程序直接转换为机器本机代码。

它们在执行前需要一个明确的构建步骤。这就是为什么我们每次更改代码时都需要重新构建程序。

**编译语言往往比解释语言更快、更高效**。但是，他们生成的机器代码是特定于平台的。

### 2.2 解释型语言

另一方面，在解释型语言(Python、JavaScript)中，没有构建步骤。相反，解释器在执行程序时对程序的源代码进行操作。

解释型语言曾被认为比编译型语言慢得多。但是，随着即时(JIT)编译的发展，性能差距正在缩小。然而，我们应该注意，JIT编译器在程序运行时将代码从解释语言转换为机器本机代码。

此外，**我们可以在Windows、Linux或Mac等多种平台上执行解释型语言代码**。解释型代码与特定类型的CPU体系结构没有关联。

## 3. 一次编写随处运行

[Java和JVM](https://www.baeldung.com/jvm-languages)在设计时就考虑到了可移植性。因此，当今大多数流行的平台都可以运行Java代码。

这听起来像是在暗示Java是一种纯粹的解释型语言。但是，**在执行之前，需要将Java源代码编译成[字节码](https://www.baeldung.com/java-class-view-bytecode)**。字节码是JVM原生的一种特殊机器语言。JVM在运行时解释并执行此代码。

为支持Java的每个平台构建和定制的是JVM，而不是我们的程序或库。

现代JVM也有一个JIT编译器。**这意味着JVM在运行时优化我们的代码以获得与编译型语言类似的性能优势**。

## 4. Java编译器

[javac](https://www.baeldung.com/javac)命令行工具将Java源代码编译成包含平台无关字节码的Java类文件：

```shell
$ javac HelloWorld.java
```

源代码文件具有.java后缀，而包含字节码的类文件则使用.class后缀生成。

<img src="../assets/img.png">

## 5. Java虚拟机

编译后的类文件(字节码)可以由[Java虚拟机(JVM)](https://www.baeldung.com/jvm-vs-jre-vs-jdk)[执行](https://www.baeldung.com/java-single-file-source-code)：

```shell
$ java HelloWorld
Hello Java!
```

现在让我们更深入地了解一下JVM体系结构。我们的目标是确定字节码如何在运行时转换为机器本机代码。

### 5.1 架构概述

JVM由五个子系统组成：

-   类加载器
-   JVM内存
-   执行引擎
-   本机方法接口
-   本机方法库

<img src="../assets/img_1.png">

### 5.2 类加载器

JVM利用[类加载器](https://www.baeldung.com/java-classloaders)子系统**将编译后的类文件放入[JVM内存](https://www.baeldung.com/java-stack-heap)中**。

除了加载之外，类加载器还执行链接和初始化。这包括：

-   验证字节码是否存在任何安全漏洞
-   为静态变量分配内存
-   用原始引用替换符号内存引用
-   将原始值分配给静态变量
-   执行所有静态代码块

### 5.3 执行引擎

执行引擎子系统负责**读取字节码，将其转换为机器本机代码并执行**。

三个主要组件负责执行，包括解释器和编译器：

-   由于JVM与平台无关，它使用解释器来执行字节码
-   [JIT编译器](https://www.baeldung.com/ahead-of-time-compilation)通过将字节码编译为本机代码以进行重复的方法调用来提高性能
-   [垃圾收集器](https://www.baeldung.com/jvm-garbage-collectors)收集并删除所有未引用的对象

执行引擎使用[本机方法接口(JNI)](https://www.baeldung.com/jni)来调用本地库和应用程序。

### 5.4 即时编译器

解释器的主要缺点是每次调用方法时都需要解释，这可能比编译的本机代码慢。Java使用JIT编译器来克服这个问题。

JIT编译器并没有完全取代解释器。执行引擎仍然使用它。但是，JVM根据调用方法的频率使用JIT编译器。

**JIT编译器将整个方法的字节码编译为机器本机代码**，因此可以直接重用。与标准编译器一样，会生成中间代码、优化，然后生成机器本机代码。

探查器是JIT编译器的一个特殊组件，负责查找热点。JVM根据运行时收集的分析信息决定对哪些代码进行JIT编译。

<img src="../assets/img_2.png">

这样做的一个效果是，Java程序在几个执行周期后可以更快地执行其工作。一旦JVM了解了热点，它就能够创建本地代码，让事情运行得更快。

## 6. 性能比较

让我们来看看JIT编译是如何提高Java的运行时性能的。

### 6.1 斐波那契性能测试

我们将使用一个简单的递归方法来计算第n个斐波那契数：

```java
private static int fibonacci(int index) {
    if (index <= 1) {
        return index;
    }
    return fibonacci(index-1) + fibonacci(index-2);
}
```

为了衡量重复方法调用的性能优势，我们将运行fibonacci方法100次：

```java
for (int i = 0; i < 100; i++) {
    long startTime = System.nanoTime();
    int result = fibonacci(12);
    long totalTime = System.nanoTime() - startTime;
    System.out.println(totalTime);
}
```

首先，我们将正常编译和执行Java代码：

```shell
$ java Fibonacci.java
```

然后，我们将在禁用JIT编译器的情况下执行相同的代码：

```shell
$ java -Djava.compiler=NONE Fibonacci.java
```

最后，我们将在C++和JavaScript中实现并运行相同的算法以进行比较。

### 6.2 性能测试结果

让我们来看看运行斐波那契递归测试后测量的平均性能(以纳秒为单位)：

-   使用JIT编译器的Java：2726ns–最快
-   不使用JIT编译器的Java：17965ns–慢559%
-   没有O2优化的C++：9435ns-慢246%
-   具有O2优化的C++：3639ns-慢33%
-   JavaScript：22998ns–慢743%

在此示例中，**使用JIT编译器，Java的性能提高了500%以上**。但是，JIT编译器确实需要运行几次才能启动。

有趣的是，Java的性能比C++代码高33%，即使C++在启用O2优化标志的情况下编译也是如此。正如预期的那样，**当Java仍然被解释时，C++在前几次运行中表现得更好**。

Java的性能也优于使用Node运行的等效JavaScript代码，后者也使用JIT编译器。结果显示性能提高了700%以上。主要原因是**Java的JIT编译器启动速度更快**。

## 7. 需要考虑的事项

从技术上讲，可以将任何静态编程语言代码直接编译为机器代码。也可以逐步解释任何编程代码。

与许多其他现代编程语言类似，Java使用编译器和解释器的组合。目标是利用两全其美的优势，**实现高性能和平台中立的执行**。

在本文中，我们重点解释了HotSpot中的工作原理。HotSpot是Oracle默认的开源JVM实现。[GraalVM](https://www.baeldung.com/graal-java-jit-compiler)也基于HotSpot，因此适用相同的原则。

如今，**大多数流行的JVM实现都使用解释器和JIT编译器的组合**。但是，他们使用的方法可能并不同。

## 8. 总结

在本文中，我们研究了Java和JVM的内部结构。我们的目标是确定Java是编译型语言还是解释型语言。我们探索了Java编译器和JVM执行引擎的内部结构。

基于此，我们得出结论：Java使用这两种方法的组合。

我们用Java编写的源代码在构建过程中首先被编译成字节码。JVM然后解释生成的字节码以供执行。但是，JVM还在运行时使用JIT编译器来提高性能。
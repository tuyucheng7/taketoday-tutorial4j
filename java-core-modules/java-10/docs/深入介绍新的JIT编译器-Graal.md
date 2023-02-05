## 1. 概述

在本教程中，我们将更深入地了解新的Java即时(JIT)编译器Graal。

我们将了解[Graal](https://github.com/oracle/graal)项目是什么并描述它的一部分，即高性能动态JIT编译器。

## 2. 什么是JIT编译器？

让我们先解释一下JIT编译器的作用。

**当我们编译我们的Java程序(例如，使用javac命令)时，我们最终会将源代码编译成代码的二进制表示形式，即JVM字节码**。这个字节码比我们的源代码更简单、更紧凑，但我们计算机中的常规处理器无法执行它。

**为了能够运行Java程序，JVM会解释字节码**。由于解释器通常比在实际处理器上执行的本机代码慢很多，因此**JVM可以运行另一个编译器，该编译器现在会将我们的字节码编译成可以由处理器运行的机器代码**。这种所谓的即时编译器比javac编译器复杂得多，它运行复杂的优化以生成高质量的机器代码。

## 3. 更详细地了解JIT编译器

Oracle的JDK实现基于开源OpenJDK项目，这包括从Java 1.3版本开始可用的**HotSpot虚拟机**。它**包含两个传统的JIT编译器：客户端编译器(也称为C1)和服务器编译器(称为opto或C2)**。

C1被设计为运行速度更快，生成的优化代码更少，而另一方面，C2需要更多的运行时间，但生成的代码优化得更好。客户端编译器更适合桌面应用程序，因为我们不希望JIT编译有长时间的停顿。服务器编译器更适合长时间运行的服务器应用程序，这些应用程序可能会花费更多的时间进行编译。

### 3.1 分层编译

如今，Java分发包在正常程序执行期间使用这两种JIT编译器。

正如我们在上一节中提到的，由javac编译的Java程序以解释模式开始执行，JVM跟踪每个频繁调用的方法并编译它们。为此，它使用C1进行编译。但是，HotSpot仍然密切关注这些方法的未来调用。如果调用次数增加，JVM将再次重新编译这些方法，但这次使用C2。

这是HotSpot使用的默认策略，称为**分层编译**。

### 3.2 服务器编译器

现在让我们稍微关注一下C2，因为它是两者中最复杂的。C2已经经过了极大的优化，生成的代码可以与C++竞争，甚至更快。服务器编译器本身是用特定的C++方言编写的。

但是，它也存在一些问题。由于C++中可能存在分段错误，它可能会导致VM崩溃。此外，在过去几年中，编译器没有实现任何重大改进。C2中的代码已经变得难以维护，因此我们不能指望当前设计会有一些新的重大增强功能。考虑到这一点，新的JIT编译器随着GraalVM的项目的开始正在蓬勃发展。

## 4. GraalVM项目

[GraalVM](https://www.graalvm.org/)项目是由Oracle创建的一个研究项目，我们可以将Graal视为几个相互关联的项目：一个基于HotSpot的新JIT编译器和一个新的多语言虚拟机。它提供了一个全面的生态系统，支持大量语言(Java和其他基于JVM的语言；JavaScript、Ruby、Python、R、C/C++和其他基于LLVM的语言)。

当然，我们主要关注的是Java。

### 4.1 Graal–一个用Java编写的JIT编译器

**Graal是一个高性能的JIT编译器**，它接收JVM字节码并生成机器码。

用Java编写编译器有几个关键优势：首先是安全性，这意味着没有崩溃，而只是出现异常，并且没有真正的内存泄漏。此外，我们将拥有良好的IDE支持，并且我们将能够使用调试器或分析器或其他方便的工具。此外，编译器可以独立于HotSpot，它可以生成更快的JIT编译版本。

Graal编译器在创建时就考虑到了这些优势，**它使用新的JVM编译器接口JVMCI与VM通信**。为了启用新的JIT编译器，我们需要在从命令行运行Java时设置以下选项：

```shell
-XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler
```

这意味着**我们可以通过三种不同的方式运行一个简单的程序：使用常规的分层编译器、使用Java 10上的JVMCI版本的Graal、或者使用GraalVM本身**。

### 4.2 JVM编译器接口

JVMCI是自JDK 9以来OpenJDK的一部分，因此我们可以使用任何标准的OpenJDK或Oracle JDK来运行Graal。

**JVMCI实际上允许我们做的是排除标准的分层编译并插入我们全新的编译器(即Graal)，而无需更改JVM中的任何内容**。

接口非常简单，当Graal编译一个方法时，它会将该方法的字节码作为输入传递给JVMCI'.作为输出，我们得到的是编译后的机器代码，输入和输出都只是字节数组：

```java
interface JVMCICompiler {
    byte[] compileMethod(byte[] bytecode);
}
```

在实际场景中，我们通常需要更多的信息，例如局部变量的数量、堆栈大小以及从解释器中的分析收集的信息，以便我们知道代码在实践中是如何运行的。

本质上，当调用[JVMCICompiler](https://github.com/md-5/OpenJDK/blob/master/src/jdk.internal.vm.ci/share/classes/jdk.vm.ci.runtime/src/jdk/vm/ci/runtime/JVMCICompiler.java)接口的compileMethod()方法时，我们需要传递一个CompilationRequest对象。然后它将返回我们想要编译的Java方法，在该方法中，我们可以找到我们需要的所有信息。

### 4.3 Graal实践

Graal本身由VM执行，因此当它变热时，将首先对其进行解释和JIT编译。让我们看一个例子，该例子也可以在GraalVM的[官方网站](https://www.graalvm.org/latest/examples/java-performance-examples/)上找到：

```java
public class CountUppercase {
    static final int ITERATIONS = Math.max(Integer.getInteger("iterations", 1), 1);

    public static void main(String[] args) {
        String sentence = String.join(" ", args);
        for (int iter = 0; iter < ITERATIONS; iter++) {
            if (ITERATIONS != 1) System.out.println("-- iteration " + (iter + 1) + " --");
            long total = 0, start = System.currentTimeMillis(), last = start;
            for (int i = 1; i < 10_000_000; i++) {
                total += sentence
                      .chars()
                      .filter(Character::isUpperCase)
                      .count();
                if (i % 1_000_000 == 0) {
                    long now = System.currentTimeMillis();
                    System.out.printf("%d (%d ms)%n", i / 1_000_000, now - last);
                    last = now;
                }
            }
            System.out.printf("total: %d (%d ms)%n", total, System.currentTimeMillis() - start);
        }
    }
}
```

现在，我们将编译并运行它：

```shell
javac CountUppercase.java
java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler
```

生成的输出类似于：

```shell
1 (1581 ms)
2 (480 ms)
3 (364 ms)
4 (231 ms)
5 (196 ms)
6 (121 ms)
7 (116 ms)
8 (116 ms)
9 (116 ms)
total: 59999994 (3436 ms)
```

我们可以看到，**一开始需要很长的时间**。预热时间取决于各种因素，例如应用程序中的多线程代码量或VM使用的线程数。如果内核数量较少，则预热时间可能会更长。

如果我们想查看Graal编译的统计数据，我们需要在执行程序时添加以下标志：

```shell
-Dgraal.PrintCompilation=true
```

这将显示与编译方法相关的数据、所花费的时间、处理的字节码(也包括内联方法)、生成的机器代码的大小以及编译期间分配的内存量。执行的输出会占用相当多的篇幅，所以我们不会在这里展示它。

### 4.4 与顶级编译器比较

现在让我们将上述结果与使用顶级编译器编译的同一程序的执行进行比较。为此，我们需要告诉VM不要使用JVMCI编译器：

```shell
java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:-UseJVMCICompiler 
1 (510 ms)
2 (375 ms)
3 (365 ms)
4 (368 ms)
5 (348 ms)
6 (370 ms)
7 (353 ms)
8 (348 ms)
9 (369 ms)
total: 59999994 (4004 ms)
```

我们可以看到，各个时间之间的差异较小。它还导致更短的初始时间。

### 4.5 Graal背后的数据结构

正如我们之前所说，Graal基本上是将一个字节数组转换为另一个字节数组。在本节中，我们将重点介绍这个过程背后的内容。以下示例基于[Chris Seaton在JokerConf 2017上的演讲](https://chrisseaton.com/truffleruby/jokerconf17/)。

一般来说，基本编译器的工作是对我们的程序进行操作。这意味着它必须使用适当的数据结构对其进行符号化。**Graal为此目的使用了一个图，即所谓的程序依赖图**。

在一个简单的场景中，我们想要相加两个局部变量，即x + y，**我们将有一个节点用于加载每个变量，另一个节点用于将它们相加。除此之外，我们还有两条表示数据流的边**：

<img src="../assets/img.png">

**数据流边显示为蓝色**。他们指出，当加载局部变量时，结果将进入加法运算。

**现在让我们介绍另一种类型的边，即描述控制流的边**。为此，我们将通过调用方法来检索我们的变量而不是直接读取它们来扩展我们的示例。当我们这样做时，我们需要跟踪方法调用的顺序，我们用红色箭头表示这个顺序：

<img src="../assets/img_1.png">

在这里，我们可以看到节点实际上并没有改变，但我们添加了控制流边。

### 4.6 实际图

我们可以使用[IdealGraphVisualiser](https://ssw.jku.at/General/Staff/TW/igv.html)检查真实的Graal图。要运行它，我们使用mx igv命令。我们还需要通过设置-Dgraal.Dump参数来配置JVM。

让我们看一个简单的例子：

```java
int average(int a, int b) {
    return (a + b) / 2;
}
```

这有一个非常简单的数据流：

<img src="../assets/img_2.png">

在上图中，我们可以看到我们方法的清楚表示。参数P(0)和P(1)流入加法运算，该加法运算进入常数C(2)的除法运算。最后，返回结果。

现在，我们将更改前面的示例，使其适用于数字数组：

```java
int average(int[] values) {
    int sum = 0;
    for (int n = 0; n < values.length; n++) {
        sum += values[n];
    }
    return sum / values.length;
}
```

我们可以看到，添加一个循环将我们引向更复杂的图：

<img src="../assets/img_3.png">

我们可以在这里注意到的是：

-   开始循环和结束循环节点
-   表示数组读取和数组长度读取的节点
-   数据和控制流边

**这种数据结构有时被称为sea-of-nodes(节点海)或soup-of-nodes(节点汤)**。我们需要提到的是，C2编译器使用了类似的数据结构，所以它并不是专门为Graal创新的新东西。

值得注意的是，Graal通过修改上述数据结构来优化和编译我们的程序。我们可以理解为什么用Java编写Graal JIT编译器实际上是一个不错的选择：**图只不过是一组对象，其中引用将它们连接为边。该结构与面向对象的语言(在本例中为Java)完美兼容**。

### 4.7 提前编译器模式

还有一点很重要，**我们也可以在Java 10的Ahead-of-Time编译器模式下使用Graal编译器**。正如我们已经说过的，Graal编译器是从头开始编写的。它符合一个新的干净接口JVMCI，这使我们能够将它与HotSpot集成，但这并不意味着编译器与之绑定。

使用编译器的一种方法是使用Profile文件驱动的方法只编译热方法，但**我们也可以使用Graal在离线模式下对所有方法进行全面编译，而无需执行代码**。这就是所谓的“Ahead-of-Time Compilation(提前编译-[JEP 295](https://openjdk.org/jeps/295))”，但我们不会在这里深入探讨AOT编译技术。

我们以这种方式使用Graal的主要原因是加快启动时间，直到HotSpot中的常规分层编译方法能够接管为止。

## 5. 总结

在本文中，我们探讨了作为Graal项目一部分的新Java JIT编译器的功能。

我们首先描述了传统的JIT编译器，然后讨论了Graal的新功能，尤其是新的JVM Compiler接口。然后，我们说明了这两种编译器的工作原理并比较了它们的性能。

之后，我们讨论了Graal用来操作程序的数据结构，最后介绍了作为使用Graal的另一种方式的AOT编译器模式。

请记住，JVM需要使用特定的标志进行配置-在此处进行了描述。
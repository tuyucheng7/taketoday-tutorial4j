## 1. 概述

JVM在运行时[解释](https://www.baeldung.com/java-compiled-interpreted)和执行[字节码。](https://www.baeldung.com/java-class-view-bytecode)此外，它还利用即时 (JIT) 编译来提高性能。

在早期的Java版本中，我们必须在 Hotspot JVM 中可用的两种类型的 JIT 编译器之间手动进行选择。一个针对更快的应用程序启动进行了优化，而另一个则实现了更好的整体性能。Java 7 引入了分层编译以实现两全其美。

在本教程中，我们将了解客户端和服务器 JIT 编译器。我们将回顾分层编译及其五个编译级别。最后，我们将通过跟踪编译日志来了解方法编译是如何工作的。

## 2. JIT 编译器

JIT 编译器将字节码编译为经常执行的部分的本机代码。这些部分称为热点，因此得名 Hotspot JVM。因此，Java 可以以与完全编译语言类似的性能运行。让我们看一下 JVM 中可用的两种类型的 JIT 编译器。

### 2.1. C1 – 客户端编译器

客户端编译器，也称为 C1，是一种为加快启动时间而优化的 JIT 编译器。它试图尽快优化和编译代码。

过去，我们将 C1 用于短期应用程序和启动时间是重要的非功能性需求的应用程序。在Java8 之前，我们必须指定-client标志才能使用 C1 编译器。但是，如果我们使用Java8 或更高版本，此标志将无效。

### 2.2. C2——服务器编译器

服务器编译器，也称为 C2，是一种为获得更好的整体性能而优化的 JIT 编译器。与 C1 相比，C2 在更长的时间内观察和分析代码。这使得 C2 可以在编译后的代码中进行更好的优化。

过去，我们将 C2 用于长时间运行的服务器端应用程序。在Java8 之前，我们必须指定-server标志才能使用 C2 编译器。但是，此标志在Java8 或更高版本中无效。

我们应该注意到，[Graal](https://www.baeldung.com/graal-java-jit-compiler) JIT 编译器从Java10 开始也可用，作为 C2 的替代品。与 C2 不同，Graal 可以在即时和[提前](https://www.baeldung.com/ahead-of-time-compilation)编译模式下运行以生成本机代码。

## 3.分层编译

C2 编译器通常需要更多的时间和消耗更多的内存来编译相同的方法。但是，它生成的本机代码比 C1 生成的代码优化得更好。

分层编译的概念最早是在Java7 中引入的，其目标是混合使用 C1 和 C2 编译器，以实现快速启动和良好的长期性能。

### 3.1. 两全其美

在应用程序启动时，JVM 最初解释所有字节码并收集有关它的分析信息。然后，JIT 编译器利用收集到的分析信息来查找热点。

首先，JIT 编译器将频繁执行的代码段用 C1 编译，以快速达到原生代码性能。稍后，当有更多分析信息可用时，C2 就会启动。C2 使用更积极和更耗时的优化重新编译代码以提高性能：

[![1](https://www.baeldung.com/wp-content/uploads/2021/07/1.png)](https://www.baeldung.com/wp-content/uploads/2021/07/1.png)

综上所述， C1 提升性能更快，而 C2基于更多热点信息，性能提升更好。

### 3.2. 准确的分析

分层编译的另一个好处是更准确的分析信息。在分层编译之前，JVM 仅在解释期间收集分析信息。

启用分层编译后，JVM 还会收集 有关 C1 编译代码的分析信息。由于编译后的代码实现了更好的性能，它允许 JVM 收集更多的分析样本。

### 3.3. 代码缓存

[代码缓存](https://www.baeldung.com/jvm-code-cache)是 JVM 存储所有编译成本机代码的字节码的内存区域。分层编译将需要缓存的代码量增加了四倍。

从Java9 开始，JVM 将代码缓存分为三个区域：

-   非方法段——JVM 内部相关代码(大约 5 MB，可通过-XX:NonNMethodCodeHeapSize配置)
-   剖析代码段——C1 编译代码可能具有较短的生命周期(默认情况下约为 122 MB，可通过-XX:ProfiledCodeHeapSize进行配置)
-   非剖析段——C2 编译代码可能具有较长的生命周期(默认情况下类似 122 MB，可通过-XX:NonProfiledCodeHeapSize进行配置)

分段代码缓存有助于改善代码局部性并减少内存碎片。因此，它提高了整体性能。

### 3.4. 反优化

即使 C2 编译代码经过高度优化和长期存在，也可以对其进行反优化。结果，JVM 将暂时回滚到解释。

当编译器的乐观假设被证明是错误的时，就会发生去优化——例如，当配置文件信息与方法行为不匹配时：

[![2](https://www.baeldung.com/wp-content/uploads/2021/07/2.png)](https://www.baeldung.com/wp-content/uploads/2021/07/2.png)

在我们的示例中，一旦热路径发生变化，JVM 就会取消优化已编译和内联的代码。

## 4.编译级别

尽管 JVM 只使用一个解释器和两个 JIT 编译器，但有五个可能的编译级别。这背后的原因是 C1 编译器可以在三个不同的级别上运行。这三个级别之间的区别在于完成的分析量。

### 4.1. 0 级——解释代码

最初，JVM 解释所有Java代码。在这个初始阶段，与编译语言相比，性能通常不如编译语言。

然而，JIT 编译器在预热阶段后启动并在运行时编译热代码。JIT 编译器利用在此级别收集的分析信息来执行优化。

### 4.2. 级别 1 – 简单的 C1 编译代码

在此级别上，JVM 使用 C1 编译器编译代码，但不收集任何分析信息。JVM 将级别 1 用于被认为是微不足道的方法。

由于方法复杂性低，C2 编译不会使其更快。因此，JVM 得出总结，没有必要为无法进一步优化的代码收集分析信息。

### 4.3. 级别 2 – 有限的 C1 编译代码

在第 2 层，JVM 使用带有轻量级分析的 C1 编译器编译代码。当 C2 队列已满时， JVM 使用此级别。目标是尽快编译代码以提高性能。

随后，JVM 使用完整分析重新编译级别 3 上的代码。最后，一旦 C2 队列不太忙，JVM 就会在第 4 层重新编译它。

### 4.4. 级别 3 – 完整的 C1 编译代码

在第 3 层，JVM 使用具有完整分析的 C1 编译器编译代码。级别 3 是默认编译路径的一部分。因此，除了琐碎的方法或编译器队列已满时，JVM 在所有情况下都使用它。

JIT 编译中最常见的场景是解释后的代码直接从 0 级跳转到 3 级。

### 4.5. 级别 4 – C2 编译代码

在此级别上，JVM 使用 C2 编译器编译代码以获得最大的长期性能。Level 4 也是默认编译路径的一部分。JVM 使用这个级别来 编译除普通方法之外的所有方法。

鉴于第 4 级代码被认为是完全优化的，JVM 将停止收集分析信息。但是，它可能会决定取消优化代码并将其发送回级别 0。

## 5.编译参数

自Java8 以来，默认启用分层编译。强烈建议使用它，除非有充分的理由禁用它。

### 5.1. 禁用分层编译

我们可以通过设置–XX:-TieredCompilation标志来禁用分层编译。当我们设置这个标志时，JVM 将不会在编译级别之间转换。因此，我们需要选择要使用的 JIT 编译器：C1 或 C2。

除非明确指定，否则 JVM 会根据我们的 CPU 决定使用哪个 JIT 编译器。对于多核处理器或 64 位 VM，JVM 将选择 C2。为了禁用 C2 并仅使用 C1 而没有分析开销，我们可以应用-XX:TieredStopAtLevel=1参数。

要完全禁用 JIT 编译器并使用解释器运行所有内容，我们可以应用-Xint标志。但是，我们应该注意，禁用 JIT 编译器会对性能产生负面影响。

### 5.2. 设置水平阈值

编译阈值是代码编译前方法调用的次数。在分层编译的情况下，我们可以为编译级别 2-4 设置这些阈值。例如，我们可以设置一个参数-XX:Tier4CompileThreshold=10000。

为了检查特定Java版本上使用的默认阈值，我们可以使用-XX:+PrintFlagsFinal标志运行 Java：

```bash
java -XX:+PrintFlagsFinal -version | grep CompileThreshold
intx CompileThreshold = 10000
intx Tier2CompileThreshold = 0
intx Tier3CompileThreshold = 2000
intx Tier4CompileThreshold = 15000
```

我们应该注意到，当启用分层编译时， JVM 不使用通用的CompileThreshold参数。

## 6.方法编译

现在让我们看一下方法编译生命周期：

[![3](https://www.baeldung.com/wp-content/uploads/2021/07/3.png)](https://www.baeldung.com/wp-content/uploads/2021/07/3.png)

总之，JVM 最初会解释一个方法，直到它的调用达到Tier3CompileThreshold 为止。然后，它使用 C1 编译器编译该方法，同时继续收集分析信息。最后，JVM 在其调用达到Tier4CompileThreshold时使用 C2 编译器编译该方法。最终，JVM 可能决定取消优化 C2 编译代码。这意味着整个过程将重复。

### 6.1. 编译日志

默认情况下，JIT 编译日志是禁用的。要启用它们，我们可以设置-XX:+PrintCompilation标志。编译日志的格式为：

-   时间戳——应用程序启动后的毫秒数
-   编译 ID – 每个编译方法的增量 ID
-   属性——编译状态有五个可能的值：
    -   % - 发生堆栈替换
    -   s – 方法是同步的
    -   ！– 该方法包含异常处理程序
    -   b – 编译发生在阻塞模式
    -   n – 编译将包装器转换为本地方法
-   编译级别——介于 0 和 4 之间
-   方法名称
-   字节码大小
-   去优化指标——有两个可能的值：
    -   Made not entrant – 标准 C1 去优化或编译器的乐观假设被证明是错误的
    -   Made zombie——垃圾收集器从代码缓存中释放空间的清理机制

### 6.2. 一个例子

让我们用一个简单的例子来演示方法编译生命周期。首先，我们将创建一个实现 JSON 格式化程序的类：

```java
public class JsonFormatter implements Formatter {

    private static final JsonMapper mapper = new JsonMapper();

    @Override
    public <T> String format(T object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

}
```

接下来，我们将创建一个实现相同接口但实现 XML 格式化程序的类：

```java
public class XmlFormatter implements Formatter {

    private static final XmlMapper mapper = new XmlMapper();

    @Override
    public <T> String format(T object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

}
```

现在，我们将编写一个使用两种不同格式化程序实现的方法。在循环的前半部分，我们将使用 JSON 实现，然后在其余部分切换到 XML 实现：

```java
public class TieredCompilation {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1_000_000; i++) {
            Formatter formatter;
            if (i < 500_000) {
                formatter = new JsonFormatter();
            } else {
                formatter = new XmlFormatter();
            }
            formatter.format(new Article("Tiered Compilation in JVM", "Baeldung"));
        }
    }

}
```

最后，我们将设置-XX:+PrintCompilation标志，运行 main 方法，并观察编译日志。

### 6.3. 查看日志

让我们关注三个自定义类及其方法的日志输出。

前两个日志条目显示 JVM 在级别 3 上编译了main方法和format方法的 JSON 实现。因此，这两个方法都是由 C1 编译器编译的。C1 编译代码替换了最初解释的版本：

```bash
567  714       3       tieredcompilation.cn.tuyucheng.taketoday.JsonFormatter::format (8 bytes)
687  832 %     3       tieredcompilation.cn.tuyucheng.taketoday.TieredCompilation::main @ 2 (58 bytes)
几百毫秒后，JVM 在第 4 层编译了这两种方法。因此，C2 编译版本取代了之前使用 C1 编译的版本：
659  800       4       tieredcompilation.cn.tuyucheng.taketoday.JsonFormatter::format (8 bytes)
807  834 %     4       tieredcompilation.cn.tuyucheng.taketoday.TieredCompilation::main @ 2 (58 bytes)
```

几毫秒后，我们看到了第一个反优化示例。在这里，JVM 标记为过时(未进入)C1 编译版本：

```bash
812  714       3       tieredcompilation.cn.tuyucheng.taketoday.JsonFormatter::format (8 bytes)   made not entrant
838 832 % 3 tieredcompilation.cn.tuyucheng.taketoday.TieredCompilation::main @ 2 (58 bytes) made not entrant
```

一段时间后，我们会注意到另一个去优化的例子。这个日志条目很有趣，因为 JVM 标记为过时(未进入)完全优化的 C2 编译版本。这意味着当 JVM 检测到完全优化的代码不再有效时，它会回滚完全优化的代码：

```bash
1015  834 %     4       tieredcompilation.cn.tuyucheng.taketoday.TieredCompilation::main @ 2 (58 bytes)   made not entrant
1018  800       4       tieredcompilation.cn.tuyucheng.taketoday.JsonFormatter::format (8 bytes)   made not entrant

```

接下来，我们将首次看到格式方法的 XML 实现。JVM 在第 3 层编译它，连同main方法：

```bash
1160 1073       3       tieredcompilation.cn.tuyucheng.taketoday.XmlFormatter::format (8 bytes)
1202 1141 %     3       tieredcompilation.cn.tuyucheng.taketoday.TieredCompilation::main @ 2 (58 bytes)
```

几百毫秒后，JVM 在第 4 层编译了这两个方法。然而，这一次，主要方法使用的是 XML 实现：

```bash
1341 1171       4       tieredcompilation.cn.tuyucheng.taketoday.XmlFormatter::format (8 bytes)
1505 1213 %     4       tieredcompilation.cn.tuyucheng.taketoday.TieredCompilation::main @ 2 (58 bytes
```

和以前一样，几毫秒后，JVM 将 C1 编译版本标记为过时(未进入)：

```bash
1492 1073       3       tieredcompilation.cn.tuyucheng.taketoday.XmlFormatter::format (8 bytes)   made not entrant
1508 1141 %     3       tieredcompilation.cn.tuyucheng.taketoday.TieredCompilation::main @ 2 (58 bytes)   made not entrant
```

JVM 继续使用第 4 级编译方法，直到我们的程序结束。

## 七、总结

在本文中，我们探讨了 JVM 中的分层编译概念。我们回顾了两种类型的 JIT 编译器，以及分层编译如何使用它们来获得最佳结果。我们看到了五个级别的编译，并学习了如何使用 JVM 参数来控制它们。

在示例中，我们通过观察编译日志探索了完整的方法编译生命周期。
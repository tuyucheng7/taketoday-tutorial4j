## 1. 概述

可以使用各种调整标志来调整 HotSpot JVM。由于有数百个这样的标志，跟踪它们和它们的默认值可能有点令人生畏。

在本教程中，我们将介绍几种发现此类调整标志并学习如何使用它们的方法。

## 二、Java选项概述

java 命令支持多种标志， 分为以下几类：

-   保证所有 JVM 实现都支持的标准选项。通常，这些选项用于日常操作，例如 –classpath、-cp、–version 等
-   并非所有 JVM 实现都支持的额外选项，通常会发生变化。这些选项以 -X开头

请注意，我们不应该随便使用这些额外的选项。此外，其中一些附加选项更高级并以 -XX开头。 

在整篇文章中，我们将关注更高级的 -XX 标志。

## 3. JVM 调整标志

要列出全局 JVM 调整标志，我们可以启用 PrintFlagsFinal 标志，如下所示：

```bash
>> java -XX:+PrintFlagsFinal -version
[Global flags]
    uintx CodeCacheExpansionSize                   = 65536                                  {pd product} {default}
     bool CompactStrings                           = true                                   {pd product} {default}
     bool DoEscapeAnalysis                         = true                                   {C2 product} {default}
   double G1ConcMarkStepDurationMillis             = 10.000000                                 {product} {default}
   size_t G1HeapRegionSize                         = 1048576                                   {product} {ergonomic}
    uintx MaxHeapFreeRatio                         = 70                                     {manageable} {default}

// truncated
openjdk version "14" 2020-03-17
OpenJDK Runtime Environment (build 14+36-1461)
OpenJDK 64-Bit Server VM (build 14+36-1461, mixed mode, sharing)
```

如上所示，某些标志具有此特定 JVM 版本的默认值。

某些标志的默认值在不同平台上可能不同，这显示在最后一列中。例如， 产品意味着标志的默认设置在所有平台上都是统一的；pd 产品意味着标志的默认设置是平台相关的。 可 管理 的值可以在运行时动态更改。

### 3.1. 诊断标志

但是， PrintFlagsFinal 标志并未显示所有可能的调整标志。例如，要同时查看诊断调整标志，我们应该添加 UnlockDiagnosticVMOptions 标志：

```bash
>> java -XX:+PrintFlagsFinal -version | wc -l
557

>> java -XX:+PrintFlagsFinal -XX:+UnlockDiagnosticVMOptions -version | wc -l
728
```

显然，当我们包括诊断选项时，还有几百个标志。例如，打印本机内存跟踪统计信息仅作为诊断标志的一部分提供：

```bash
bool PrintNMTStatistics                       = false                                  {diagnostic} {default}
```

### 3.2. 实验标志

要同时查看实验选项，我们应该添加 UnlockExperimentalVMOptions 标志：

```bash
>> java -XX:+UnlockDiagnosticVMOptions -XX:+UnlockExperimentalVMOptions -XX:+PrintFlagsFinal -version | wc -l
809
```

### 3.3. JVMCI 标志

从 Java 9 开始，JVM 编译器接口或[JVMCI](https://openjdk.java.net/jeps/243)使我们能够使用用 Java 编写的编译器，例如 Graal，作为动态编译器。

要查看与 JVMCI 相关的选项，我们应该添加更多标志，甚至启用 JVMCI：

```bash
>> java -XX:+UnlockDiagnosticVMOptions -XX:+UnlockExperimentalVMOptions 
>> -XX:+JVMCIPrintProperties -XX:+EnableJVMCI -XX:+PrintFlagsFinal  -version | wc -l
1516
```

然而，大多数时候，使用全局、诊断和实验选项应该就足够了，并且会帮助我们找到我们想要的标志。

### 3.4. 把它们放在一起

这些选项组合可以帮助我们找到调整标志，尤其是当我们不记得确切名称时。例如，在 Java 中查找与软引用相关的调优标志：

```bash
>> alias jflags="java -XX:+UnlockDiagnosticVMOptions -XX:+UnlockExperimentalVMOptions -XX:+PrintFlagsFinal  -version"
>> jflags | grep Soft
size_t SoftMaxHeapSize                          = 4294967296                             {manageable} {ergonomic}
intx SoftRefLRUPolicyMSPerMB                    = 1000                                   {product} {default}
```

从结果中，我们可以很容易地猜到SoftRefLRUPolicyMSPerMB就是我们要找的标志。

## 4.不同类型的标志

在上一节中，我们忽略了一个重要主题：标志类型。让我们再看一下 java -XX:+PrintFlagsFinal -version 的输出：

```bash
[Global flags]
    uintx CodeCacheExpansionSize                   = 65536                                  {pd product} {default}
     bool CompactStrings                           = true                                   {pd product} {default}
     bool DoEscapeAnalysis                         = true                                   {C2 product} {default}
   double G1ConcMarkStepDurationMillis             = 10.000000                                 {product} {default}
   size_t G1HeapRegionSize                         = 1048576                                   {product} {ergonomic}
    uintx MaxHeapFreeRatio                         = 70                                     {manageable} {default}
// truncated
```

如上所示，每个标志都有特定的类型。

布尔选项用于启用或禁用功能。此类选项不需要值。要启用它们，我们只需要在选项名称前加上一个加号：

```bash
-XX:+PrintFlagsFinal
```

相反，要禁用它们，我们必须在它们的名称前添加一个减号：

```bash
-XX:-RestrictContended
```

其他标志类型需要参数值。可以用空格、冒号、等号将值与选项名称分开，或者参数可以直接跟在选项名称后面(每个选项的确切语法不同)：

```bash
-XX:ObjectAlignmentInBytes=16 -Xms5g -Xlog:gc
```

## 5.文档和源代码

找到正确的标志名称是一回事。找出那个特定的标志在引擎盖下做什么是另一回事。

找出这些细节的一种方法是查看文档。例如，[JDK 工具规范部分中](https://docs.oracle.com/en/java/javase/14/docs/specs/man/java.html)java 命令的文档 是一个很好的起点。

有时，再多的文档也比不上源代码。因此，如果我们有特定标志的名称，那么我们可以探索 JVM 源代码以找出发生了什么。

例如，我们可以从[GitHub](https://github.com/openjdk/jdk14u)甚至他们的[Mercurial 存储库](http://hg.openjdk.java.net/jdk8)中查看 HotSpot JVM 的源代码，然后：

```bash
>> git clone git@github.com:openjdk/jdk14u.git openjdk
>> cd openjdk/src/hotspot
>> grep -FR 'PrintFlagsFinal' .
./share/runtime/globals.hpp:  product(bool, PrintFlagsFinal, false,                                   
./share/runtime/init.cpp:  if (PrintFlagsFinal || PrintFlagsRanges) {
```

我们在这里寻找包含 PrintFlagsFinal 字符串的所有文件。找到负责的文件后，我们可以环顾四周，看看该特定标志是如何工作的。

## 六，总结

在本文中，我们了解了如何找到几乎所有可用的 JVM 调优标志，还学习了一些技巧以更有效地使用它们。
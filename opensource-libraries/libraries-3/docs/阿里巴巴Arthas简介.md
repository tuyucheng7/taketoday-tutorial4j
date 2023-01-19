## 1. 简介

[Alibaba Arthas](https://github.com/alibaba/arthas)是一个诊断工具，它使我们能够监控、分析和排除我们的Java应用程序的故障。使用 Arthas 的主要好处之一是我们不需要更改我们的代码，甚至不需要重新启动我们想要监控的Java服务。

在本教程中，我们将首先安装 Arthas，然后再通过一个简单的案例研究来演示 Arthas 的一些关键功能。

最后，由于 Arthas 是用Java编写的，它是跨平台的，可以在 Linux、macOS 和 Windows 上愉快地运行。

## 2. 下载并开始

首先，让我们直接通过[下载链接](https://alibaba.github.io/arthas/arthas-boot.jar)或使用curl下载 Arthas 库：

```bash
curl -O https://alibaba.github.io/arthas/arthas-boot.jar

```

现在，让我们通过使用-h(帮助)选项运行 Arthas 来测试它是否正常工作：

```bash
java -jar arthas-boot.jar -h
```

如果成功，我们应该看到显示的所有命令的帮助指南：

[![baeldung arthas 帮助命令](https://www.baeldung.com/wp-content/uploads/2020/03/baeldung-arthas-help-command-900.png)](https://www.baeldung.com/wp-content/uploads/2020/03/baeldung-arthas-help-command-900.png)

## 3. 案例研究

在本教程中，我们将使用一个非常简单的应用程序，该应用程序基于使用递归的[斐波那契数列的相当低效的实现：](https://www.baeldung.com/java-fibonacci)

```java
public class FibonacciGenerator {

    public static void main(String[] args) {
        System.out.println("Press a key to continue");
        System.in.read();
        for (int i = 0; i < 100; i++) {
            long result = fibonacci(i);
            System.out.println(format("fib(%d): %d", i, result));
        }
    }

    public static long fibonacci(int n) {
        if (n == 0 || n == 1) {
            return 1L;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }
}

```

此示例中最有趣的部分是遵循斐波那契数学定义的斐波那契方法。

在main方法中，我们使用了一个for循环，其中的数字相对较大，因此我们的计算机将忙于处理更长的计算。当然，这正是我们想要展示 Arthas 的地方。

## 4.启动阿尔萨斯

现在让我们试试阿尔萨斯吧！我们需要做的第一件事是运行我们的小型 Fibonacci 应用程序。为此，我们可以使用我们最喜欢的 IDE 或直接在终端中运行它。它会要求按一个键才能开始。将流程附加到 Arthas 后，我们将按任意键。

现在，让我们运行 Arthas 可执行文件：

```bash
java -jar arthas-boot.jar
```

Arthas 提示一个菜单来选择我们要附加到哪个进程：

```plaintext
[INFO] arthas-boot version: 3.1.7
[INFO] Found existing java process, please choose one and hit RETURN.
 [1]: 25500 com.baeldung.arthas.FibonacciGenerator
...
```

让我们选择名称为com.baeldung.arthas.FibonacciGenerator的那个。只需在列表中输入数字，在此示例中为“1”，然后按 Enter 键。

Arthas 现在将附加到这个进程并开始：

```plaintext
INFO] Try to attach process 25500
[INFO] Attach process 25500 success.
...                     

```

一旦我们启动了 Arthas，我们就会看到一个提示符，我们可以在其中发出不同的命令。

我们可以使用帮助命令来获取有关可用选项的更多信息。此外，为了方便使用Arthas，我们还可以使用tab键来自动补全它的命令。

将 Arthas 附加到我们的流程后，现在我们可以按一个键，程序开始打印斐波那契数列。

## 5.仪表板

Arthas 启动后，我们就可以使用仪表板了。在这种情况下，我们通过键入仪表板命令来继续。现在我们看到一个包含多个窗格的详细屏幕以及有关我们的Java进程的大量信息：

[![baeldung 阿尔萨斯仪表板](https://www.baeldung.com/wp-content/uploads/2020/03/baeldung-arthas-dashboar-1200.png)](https://www.baeldung.com/wp-content/uploads/2020/03/baeldung-arthas-dashboar-1200.png)

让我们更详细地看一下其中的一些：

1.  顶部专用于当前运行的线程
2.  重要的列之一是每个线程的 CPU 消耗
3.  第 3 节显示每个线程的 CPU 时间
4.  另一个有趣的窗格用于内存分析。列出了不同的内存区域及其统计信息。在右侧，我们有关于[垃圾收集器的信息](https://www.baeldung.com/jvm-garbage-collectors)
5.  最后，在第 5 节中我们有关于主机平台和 JVM 的信息

我们可以通过按q退出仪表板。

我们应该记住，即使我们退出，Arthas 也会依附于我们的进程。因此，为了正确地取消它与我们进程的链接，我们需要运行停止命令。 

## 6. 分析堆栈跟踪

在仪表板中，我们看到我们的主进程占用了几乎 100% 的 CPU。这个进程的ID为 1，我们可以在第一列中看到它。

现在我们已经退出仪表板，我们可以通过运行线程命令来更详细地分析该过程：

```bash
thread 1
```

作为参数传递的数字是线程 ID。Arthas 打印出一个堆栈跟踪，不出所料，其中充斥着对斐波那契方法的调用。

如果堆栈跟踪很长且难以阅读，thread 命令允许我们使用管道：

```bash
thread 1 | grep 'main('
```

这只会打印与 grep 命令匹配的行：

```plaintext
[arthas@25500]$ thread 1 | grep 'main('
    at com.baeldung.arthas.FibonacciGenerator.main(FibonacciGenerator.java:10)
```

## 7.反编译Java类

让我们想象一个场景，我们正在分析一个我们知之甚少或一无所知的Java应用程序，我们突然发现堆栈中散布着以下类型的重复调用：

```bash
[arthas@59816]$ thread 1
"main" Id=1 RUNNABLE
  at app//com.baeldung.arthas.FibonacciGenerator.fibonacci(FibonacciGenerator.java:18)
  at app//com.baeldung.arthas.FibonacciGenerator.fibonacci(FibonacciGenerator.java:18)
  ...

```

由于我们正在运行 Arthas，我们可以反编译一个类来查看它的内容。为此，我们可以使用[jad](https://alibaba.github.io/arthas/en/jad)命令，将限定的类名作为参数传递：

```bash
jad com.baeldung.arthas.FibonacciGenerator

ClassLoader:
+-jdk.internal.loader.ClassLoaders$AppClassLoader@799f7e29
  +-jdk.internal.loader.ClassLoaders$PlatformClassLoader@60f1dd34

Location:
/home/amoreno/work/baeldung/tutorials/libraries-3/target/
/
  Decompiled with CFR.
 /
package com.baeldung.arthas;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class FibonacciGenerator {
    public static void main(String[] arrstring) throws IOException {
```

输出是反编译的Java类和一些有用的元数据，例如类的位置。这是一个非常有用和强大的功能。

## 八、搜索类及搜索方法

在搜索 JVM 中加载的类时，搜索类命令会派上用场。我们可以通过键入sc并传递一个带有或不带通配符的模式作为参数来使用它：

```bash
[arthas@70099]$ sc Fibonacci
com.baeldung.arthas.FibonacciGenerator
Affect(row-cnt:1) cost in 5 ms.

```

一旦我们有了类的限定名称，我们就可以使用两个额外的标志来查找更多信息：

-   -d显示类的详细信息
-   -f显示类的字段

但是类的字段必须结合详情查询：

```bash
[arthas@70099]$ sc -df com.baeldung.arthas.FibonacciGenerator
  class-info        com.baeldung.arthas.FibonacciGenerator
  ...

```

同样，我们可以使用命令sm(搜索方法)来查找类中已加载的方法。在这种情况下，对于我们的类com.baeldung.arthas.FibonacciGenerator，我们可以运行：

```bash
[arthas@70099]$ sm com.baeldung.arthas.FibonacciGenerator
com.baeldung.arthas.FibonacciGenerator <init>()V
com.baeldung.arthas.FibonacciGenerator main([Ljava/lang/String;)V
com.baeldung.arthas.FibonacciGenerator fibonacci(I)J
Affect(row-cnt:3) cost in 4 ms.

```

我们也可以使用标志-d来检索方法的详细信息。最后，我们可以将方法名称传递给可选参数以缩小返回方法的数量：

```bash
sm -d com.baeldung.arthas.FibonacciGenerator fibonacci
 declaring-class  com.baeldung.arthas.FibonacciGenerator
 method-name      fibonacci
 modifier         public,static
 annotation
 parameters       int
 return           long
 exceptions
 classLoaderHash  799f7e29
```

## 9. 监控方法调用

我们可以用 Arthas 做的另一件很酷的事情是监控一个方法。这在我们的应用程序中调试性能问题时非常方便。为此，我们可以使用[monitor](https://alibaba.github.io/arthas/en/monitor)命令。

monitor命令需要一个标志-c <seconds>和两个参数——限定的类名和方法名。

对于我们的案例研究，现在让我们调用monitor：

```bash
monitor -c 10 com.baeldung.arthas.FibonacciGenerator fibonacci
```

正如预期的那样，Arthas 将每 10 秒打印一次关于斐波那契方法的指标：

```plaintext
Affect(class-cnt:1 , method-cnt:1) cost in 47 ms.
 timestamp            class                                          method     total   success  fail  avg-rt(ms)  fail-rate                                                                       
-----------------------------------------------------------------------------------------------------------------------------                                                                      
 2020-03-07 11:43:26  com.baeldung.arthas.FibonacciGenerator  fibonacci  528957  528957   0     0.07        0.00%
...                                                                           


```

我们也有那些以失败告终的调用的指标——这些对调试很有用。

## 10. 监控方法参数

如果我们需要调试方法的参数，我们可以使用watch命令。但是，语法有点复杂：

```bash
watch com.baeldung.arthas.FibonacciGenerator fibonacci '{params[0], returnObj}' 'params[0]>10' -n 10

```

让我们详细看看每个参数：

-   第一个参数是类名
-   第二个是方法名
-   第三个参数是一个[OGNL 表达式](https://commons.apache.org/proper/commons-ognl/language-guide.html)，定义了我们想要观察的内容——在本例中，它是第一个(也是唯一的)方法参数，以及返回值
-   第四个也是最后一个可选参数是一个布尔表达式，用于过滤我们要监视的调用

对于此示例，我们只想在参数大于 10 时对其进行监控。最后，我们添加一个标志以将结果数限制为 10：

```bash
watch com.baeldung.arthas.FibonacciGenerator fibonacci '{params[0], returnObj}' 'params[0]>10' -n 10
Press Q or Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 19 ms.
ts=2020-02-17 21:48:08; [cost=30.165211ms] result=@ArrayList[
    @Integer[11],
    @Long[144],
]
ts=2020-02-17 21:48:08; [cost=50.405506ms] result=@ArrayList[
    @Integer[12],
    @Long[233],
]
...

```

在这里，我们可以看到调用示例及其 CPU 时间和输入/返回值。

## 11. 探查器

对于那些对应用程序性能感兴趣的人来说，可以通过[profiler](https://alibaba.github.io/arthas/en/profiler.html)命令获得非常直观的功能。分析器将评估我们的进程正在使用的 CPU 的性能。

让我们通过启动profiler start来运行分析器。这是一个非阻塞任务，这意味着我们可以在分析器工作时继续使用 Arthas。

在任何时候，我们都可以通过运行profiler getSamples询问 profiler 有多少样本。

现在让我们使用profiler stop 停止分析器。此时，保存了一张[FlameGraph](http://www.brendangregg.com/flamegraphs.html)图片。在这个精确的例子中，我们有一个斐波那契线主导图形的图表：

[![baeldung 火焰图 arthas](https://www.baeldung.com/wp-content/uploads/2020/03/baeldung-flame-graph-arthas.png)](https://www.baeldung.com/wp-content/uploads/2020/03/baeldung-flame-graph-arthas.png)

请注意，当我们想要检测我们的 CPU 时间花在哪里时，此图表特别有用。

## 12.总结

在本教程中，我们探索了 Arthas 的一些最强大和有用的功能。

正如我们所看到的，Arthas 有很多命令可以帮助我们诊断各种问题。当我们无法访问正在审查的应用程序的代码，或者如果我们想对服务器上运行的有问题的应用程序进行快速诊断时，它也特别有用。